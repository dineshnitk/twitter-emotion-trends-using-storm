package com.dg.bolts;

import java.util.Map;

import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

@SuppressWarnings("serial")
public class TweetsFilterEmotionsBolt extends BaseRichBolt {
	private OutputCollector collector;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(Tuple input) {
		String word = input.getString(0);
		if (word.equals("love") || word.equals("hate") || word.equals("lol")
				|| word.equals("happy")) {
			collector.emit(new Values(word));
		}
		collector.ack(input);
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}
}