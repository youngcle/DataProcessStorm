package cast.c503;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;

import java.util.Map;

/**
 * Created by youngcle on 15-11-30.
 */
public class LineMosaicBolt implements IRichBolt {
    OutputCollector Collector;

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        Collector = outputCollector;

    }

    public void execute(Tuple tuple) {

    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declare(new Fields("DATAPACK","DATAPACKID","DATAPACKNAME"));
    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}