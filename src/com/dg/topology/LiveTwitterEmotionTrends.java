package com.dg.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;
import backtype.storm.utils.Utils;

import com.dg.bolts.*;
import com.dg.spouts.TwitterStreamSpout;

public class LiveTwitterEmotionTrends {

	private static int NUM_OF_WORKERS = 2;
	private static int TOPOLOGY_RUN_TIME = 5 * 60 * 1000; // 5 minutes
	private static String TOPOLOGY_NAME = "LiveTwitterEmotionTrends";

	public static void main(String[] args) {

		Config conf = new Config();
		conf.setNumWorkers(NUM_OF_WORKERS);

		TopologyBuilder builder = new TopologyBuilder();

		String TWITTER_STREAM_SPOUT_ID = "twitter";
		
		// This spout reads the twitter stream using twitter4j and generates a stream
		IRichSpout spout = new TwitterStreamSpout();
		builder.setSpout(TWITTER_STREAM_SPOUT_ID, spout);

		String WORD_SPLITTER_BOLT_ID = "split";
		int WORD_SPLITTER_PARALLELISM_HINT = 2;
		
		// This Bolt takes the twitter stream, splits it into words and generates a twitter stream of all words
		builder.setBolt(WORD_SPLITTER_BOLT_ID, new TweetWordSpitterBolt(),
				WORD_SPLITTER_PARALLELISM_HINT).shuffleGrouping(
				TWITTER_STREAM_SPOUT_ID);

		String FILTER_EMOTIONS_BOLT_ID = "filter";
		int FILTER_EMOTIONS_PARALLELISM_HINT = 2;
		
		// This Bolt filter only the words matching words related to emotions
		builder.setBolt(FILTER_EMOTIONS_BOLT_ID,
				new TweetsFilterEmotionsBolt(),
				FILTER_EMOTIONS_PARALLELISM_HINT).shuffleGrouping(
				WORD_SPLITTER_BOLT_ID);

		String COUNT_AND_PRINT_BOLT_ID = "count_and_print";
		int COUNT_AND_PRINT_PARALLELISM_HINT = 2;
		
		// This Bolt keeps the count of the emotion words and prints it on the console
		builder.setBolt(COUNT_AND_PRINT_BOLT_ID,
				new TwitterEmotionsCountPrintBolt(),
				COUNT_AND_PRINT_PARALLELISM_HINT).fieldsGrouping(
				FILTER_EMOTIONS_BOLT_ID, new Fields("word"));

		// Create a local mode cluster to run the topology
		LocalCluster cluster = new LocalCluster();

		// Submit the topology to the local cluster
		cluster.submitTopology(TOPOLOGY_NAME, conf, builder.createTopology());

		// Run the topology for 5 minutes
		Utils.sleep(TOPOLOGY_RUN_TIME);

		// Finally shutdown the cluster
		cluster.shutdown();
	}
}
