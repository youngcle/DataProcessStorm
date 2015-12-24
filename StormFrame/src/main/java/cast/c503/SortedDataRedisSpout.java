package cast.c503;


import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichSpout;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Tuple;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;


/**
 * Created by youngcle on 15-11-30.
 */


public class SortedDataRedisSpout implements IRichSpout {
    private boolean DebugMode = false;
    final String DataName = "RAWPACK";


    ProcessFrame processFrame;

    BinaryJedis jedis;
    int counter = 0;
    long counterInZ = 0;
    long timeStartRound = 0;
    boolean endflag = false;


    public Map<String, Object> getComponentConfiguration() {
        return null;
    }



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
        jedis.close();
    }

    //激活时
    public void activate() {

    }

    //未激活时
    public void deactivate() {

    }


    //读取REDIS中的数据
    public void nextTuple() {

        ArrayList<byte[]> databytes_CCD= new ArrayList<byte[]>();
        ArrayList<Integer> dataIDs_CCD = new ArrayList<Integer>();

        ArrayList<byte[]> databytes_HSICCD = new ArrayList<byte[]>();
        ArrayList<Integer> dataIDs_HSICCD = new ArrayList<Integer>();

        ArrayList<byte[]> databytes_IRS = new ArrayList<byte[]>();
        ArrayList<Integer> dataIDs_IRS = new ArrayList<Integer>();

        ArrayList<byte[]> databytes_HSIIRS = new ArrayList<byte[]>();
        ArrayList<Integer> dataIDs_HSIIRS = new ArrayList<Integer>();
        //定义第二个拓扑，从ＳｏｒｔｅｄＳｅｔ中取出有序数据，进行落地，

        counter++;
        //判断当前数量，当大于300时，取出前1000，生成文件
        String currenKeyName = "CCD_COMPRESSED_DECOMPRESSED";
        getDatabytesAndDataIDsFromZSET(currenKeyName,databytes_CCD,dataIDs_CCD);
        if(endflag) {
            //结束处理
            System.out.println("Got a end flag! It's time to stop!!!!!!!!!!!!!");
            databytes_CCD.clear();
            dataIDs_CCD.clear();
            dataIDs_CCD.add(-1);
            collector.emit("SORTEDRAW_CCD_STREAM", new Values(null, dataIDs_CCD, "SORTEDRAW_CCD_DATA"));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(databytes_CCD.size()!=0)
            collector.emit("SORTEDRAW_CCD_STREAM",new Values(databytes_CCD,dataIDs_CCD,"SORTEDRAW_CCD_DATA"));


        currenKeyName = "HSICCD_COMPRESSED_DECOMPRESSED";
        getDatabytesAndDataIDsFromZSET(currenKeyName,databytes_HSICCD,dataIDs_HSICCD);
        if(endflag) {
            //结束处理
            System.out.println("Got a end flag! It's time to stop!!!!!!!!!!!!!");

            databytes_CCD.clear();
            dataIDs_CCD.clear();
            dataIDs_CCD.add(-1);
            collector.emit("SORTEDRAW_HSICCD_STREAM", new Values(null, dataIDs_CCD, "SORTEDRAW_HSICCD_DATA"));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if(databytes_HSICCD.size()!=0)
            collector.emit("SORTEDRAW_HSICCD_STREAM",new Values(databytes_HSICCD,dataIDs_HSICCD,"SORTEDRAW_HSICCD_DATA"));



        currenKeyName = "IRS_COMPRESSED_DECOMPRESSED";
        getDatabytesAndDataIDsFromZSET(currenKeyName,databytes_IRS,dataIDs_IRS);
        if(endflag) {
            //结束处理
            System.out.println("Got a end flag! It's time to stop!!!!!!!!!!!!!");
            databytes_CCD.clear();
            dataIDs_CCD.clear();
            dataIDs_CCD.add(-1);
            collector.emit("SORTEDRAW_IRS_STREAM", new Values(null, dataIDs_CCD, "SORTEDRAW_IRS_DATA"));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(databytes_IRS.size()!=0)
            collector.emit("SORTEDRAW_IRS_STREAM",new Values(databytes_IRS,dataIDs_IRS,"SORTEDRAW_IRS_DATA"));


        currenKeyName = "HSIIRS_COMPRESSED_DECOMPRESSED";
        getDatabytesAndDataIDsFromZSET(currenKeyName,databytes_HSIIRS,dataIDs_HSIIRS);
        if(endflag) {
            //结束处理
            System.out.println("Got a end flag! It's time to stop!!!!!!!!!!!!!");
            databytes_CCD.clear();
            dataIDs_CCD.clear();
            dataIDs_CCD.add(-1);
            collector.emit("SORTEDRAW_HSIIRS_STREAM", new Values(null, dataIDs_CCD, "SORTEDRAW_HSIIRS_DATA"));
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else if(databytes_HSIIRS.size()!=0)
            collector.emit("SORTEDRAW_HSIIRS_STREAM",new Values(databytes_HSIIRS,dataIDs_HSIIRS,"SORTEDRAW_HSIIRS_DATA"));
        else{
            System.out.println("no data incoming,waiting!!!");
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

    //取出前面已经有序的数据和ＩＤ，放入链表，并将该数据从ＳｏｒｔｅｄＳＥＴ中的删除
    void getDatabytesAndDataIDsFromZSET(String keyNameofZSet,ArrayList<byte[]> databytes,ArrayList<Integer> dataIDs) {

        databytes.clear();
        dataIDs.clear();

        //数据结束表示，帧ID为-1
        Set<redis.clients.jedis.Tuple> tupleSet = jedis.zrangeWithScores(keyNameofZSet.getBytes(), 0, 0);
        //数据结束表示，帧ID为-1
        final int DATAEND_FLAG = -1;

        for (redis.clients.jedis.Tuple redistuple : tupleSet) {
            endflag = ((int) redistuple.getScore() == -1) ? true : false;
        }
        if(endflag) {
            jedis.zremrangeByRank(keyNameofZSet.getBytes(), 0, 0);
        }
        long countInZ = jedis.zcard(keyNameofZSet.getBytes());

        //rangeEND初设限制
        long rangeEnd = -2;
        if (endflag) {
            rangeEnd = -1;
        } else if (countInZ > 50) {
            rangeEnd = 10;
        }

        if (rangeEnd != -2) {
            Set<Tuple> sortedDataSet = jedis.zrangeWithScores(keyNameofZSet.getBytes(), 0, rangeEnd);
            for (redis.clients.jedis.Tuple redistuple : sortedDataSet) {
                databytes.add(redistuple.getBinaryElement());
                dataIDs.add((int) redistuple.getScore());
            }
            jedis.zremrangeByRank(keyNameofZSet.getBytes(), 0, rangeEnd);
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declareStream("SORTEDRAW_CCD_STREAM",new Fields("DATABYTESLIST","DATAIDSLIST","DATANAME"));
        declarer.declareStream("SORTEDRAW_HSICCD_STREAM",new Fields("DATABYTESLIST","DATAIDSLIST","DATANAME"));
        declarer.declareStream("SORTEDRAW_IRS_STREAM",new Fields("DATABYTESLIST","DATAIDSLIST","DATANAME"));
        declarer.declareStream("SORTEDRAW_HSIIRS_STREAM",new Fields("DATABYTESLIST","DATAIDSLIST","DATANAME"));

    }

    //可靠传输时，成功的处理
    public void ack(Object o) {

    }


    //可靠传输时数据传输失败时的处理
    public void fail(Object o) {

    }
    //dealing with data from Redis
}
