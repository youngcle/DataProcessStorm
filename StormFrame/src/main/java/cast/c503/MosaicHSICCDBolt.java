package cast.c503;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import cast.c503.DataPackage.DataPackageBase;
import redis.clients.jedis.BinaryJedis;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by youngcle on 15-11-30.
 * 解压缩处理
 */
public class MosaicHSICCDBolt implements IRichBolt {


    private boolean DebugMode = true;
    OutputCollector Collector;
    BinaryJedis jedis;
    int counter=0;
    ArrayList<byte[]> databytes = new ArrayList<byte[]>();
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        String redishost = (String)map.get("redishost");
        String redishostport = (String)map.get("redishostport");
        if(redishost==null) redishost = "localhost";
        if(redishostport == null ) redishostport = "6379";
        Collector = outputCollector;
        jedis = new BinaryJedis(redishost);
    }

    public void execute(Tuple tuple) {

        long starttime = System.currentTimeMillis();


        byte[] currentDatabytes = tuple.getBinaryByField("DATAPACK");
        int currentDataID = tuple.getIntegerByField("DATAPACKID");
        String currenDataName = tuple.getStringByField("DATANAME");
        //结束标识判断
        if(currentDataID ==-1){
            jedis.zadd(currenDataName.getBytes(), (double) -1, "end".getBytes());
            System.out.println("Got a end flag! It's time to stop!!!!!!!!!!!!!");
        }else {

            DataPackageBase currentDataPackage = new DataPackageBase(currenDataName);
            currentDataPackage.addPayload(currentDataID, currentDatabytes);
            jedis.zadd(currenDataName.getBytes(), (double) currentDataID, currentDatabytes);
            counter++;
        }
    }


    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
//        outputFieldsDeclarer.declare(new Fields("DATAPACK","DATAPACKID","DATAPACKNAME"));
    }

    public void cleanup() {
        //扫尾，结束前释放资源，仅运行一次
        jedis.close();

    }



    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}