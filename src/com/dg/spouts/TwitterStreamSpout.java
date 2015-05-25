package com.dg.spouts;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import backtype.storm.utils.Utils;

@SuppressWarnings("serial")
public class TwitterStreamSpout extends BaseRichSpout {

	SpoutOutputCollector collector;
	LinkedBlockingQueue<String> queue = null;
	TwitterStream twitterStream;
	String consumerKey;
	String consumerSecret;
	String accessToken;
	String accessTokenSecret;

	public TwitterStreamSpout() {
		this.consumerKey = "evHvADs8zcoy4N1LTb4nnNH3c";
		this.consumerSecret = "EYWBULQPoqYtumhDPvZNJ5aHYYvMZesczHmmflOOnWd7czoe3P";
		this.accessToken = "46235218-2jBPNq39AQSOAmJeSzJe0hiqsDWdJT7SDjn5VZjUW";
		this.accessTokenSecret = "hyDJ3Udo9nfnxPAvmnSo26ugfQZzr8CirfNi7XLUwJvvF";
	}

	@Override
	public void open(Map conf, TopologyContext context,
			SpoutOutputCollector collector) {

		int QUEUE_CAPACITY = 1000;
		queue = new LinkedBlockingQueue<String>(QUEUE_CAPACITY);
		this.collector = collector;

		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {

				queue.offer(status.getText());
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice sdn) {
			}

			@Override
			public void onTrackLimitationNotice(int i) {
			}

			@Override
			public void onScrubGeo(long l, long l1) {
			}

			@Override
			public void onException(Exception ex) {
			}

			@Override
			public void onStallWarning(StallWarning arg0) {
			}

		};

		TwitterStream twitterStream = new TwitterStreamFactory(
				new ConfigurationBuilder().setJSONStoreEnabled(true).build())
				.getInstance();

		twitterStream.addListener(listener);
		twitterStream.setOAuthConsumer(consumerKey, consumerSecret);
		AccessToken token = new AccessToken(accessToken, accessTokenSecret);
		twitterStream.setOAuthAccessToken(token);

		// Sample of english tweets
		twitterStream.sample("en");

	}

	@Override
	public void nextTuple() {
		String ret = queue.poll();
		
		// If queue is empty, wait for 50ms
		if (ret == null) {
			Utils.sleep(50);
		}
		else { // Emit the tweet
			collector.emit(new Values(ret));
		}
	}

	@Override
	public void close() {
		twitterStream.shutdown();
	}


	@Override
	public void ack(Object id) {
	}

	@Override
	public void fail(Object id) {
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("tweet"));
	}

}
