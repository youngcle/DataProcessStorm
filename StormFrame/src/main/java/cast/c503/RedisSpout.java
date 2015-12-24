package cast.c503;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.*;
import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DemodRawDataPackage;
import com.sun.image.codec.jpeg.*;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.task.ShellBolt;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import cast.c503.ProcessFrame;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import redis.clients.jedis.BinaryJedis;


/**
 * Created by youngcle on 15-11-30.
 */


public class RedisSpout implements IRichSpout {
    private boolean DebugMode = false;
    final String DataName = "RAWPACK";


    ProcessFrame processFrame;

    BinaryJedis jedis;

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }

    int nullcounter = 0;
    int msToSleep = 10000; //睡的时间

    int timeoutToStop = 30;//判断结束的超时停止标识。

    SpoutOutputCollector collector;
    //初始化REDIS连接
    public void open(Map config, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
        String redishost = (String)config.get("redishost");
        String redishostport = (String)config.get("redishostport");
        if(redishost==null) redishost = "localhost";
        if(redishostport == null ) redishostport = "6379";
        collector = spoutOutputCollector;
        jedis = new BinaryJedis(redishost);



    }

    //关闭REDIS连接
    public void close() {

    }

    //激活时
    public void activate() {

    }

    //未激活时
    public void deactivate() {

    }


    //读取REDIS中的数据
    public void nextTuple() {
//        System.out.println();
//        System.out.println("start to popping the binary data(in memory) to redis");
        long timestart = System.currentTimeMillis();
        long timepassed = 0;

        byte[] bytesfromjedis = null;
        long datasize = 0;
        String TargetPath = "/dev/shm";
        int PackID = -1;

        ByteBuffer buffer = ByteBuffer.allocate(4);
        byte[] idbytes = jedis.lpop("DATA:PACKID".getBytes());
        if (idbytes == null)
            ;
        else {
//              获取PACK ID
            buffer.put(idbytes);
            buffer.flip();
            PackID = buffer.getInt();
        }
//              获取数据包本体
        bytesfromjedis = jedis.lpop("DATA:PACK".getBytes());
        if (bytesfromjedis == null) {
            if(nullcounter*msToSleep > timeoutToStop) {
                int PackID_ENDFLAG = -1;
                collector.emit(new Values(null, PackID_ENDFLAG, DataName));
            }
            nullcounter++;
            try {
                Thread.currentThread().sleep(msToSleep);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            nullcounter=0;
            if(DebugMode) {

                long filesize = bytesfromjedis.length;
                ByteBuffer bb = ByteBuffer.wrap(bytesfromjedis);
                try {
                    File Outputfile = new File(TargetPath + File.separator + "PACK_back" + PackID + ".zip");
                    FileChannel fileChannel = new FileOutputStream(Outputfile).getChannel();
                    fileChannel.write(bb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //组装数据包
//            DataPackageBase rawDataPackage = new DataPackageBase("RAWPACK");

//            rawDataPackage.addPayload(PackID, bytesfromjedis);

            collector.emit(new Values(bytesfromjedis,PackID,DataName));
        }
    }
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("DATAPACK","DATAPACKID","DATANAME"));
    }

    //可靠传输时，成功的处理
    public void ack(Object o) {

    }


    //可靠传输时数据传输失败时的处理
    public void fail(Object o) {

    }
    //dealing with data from Redis
}
