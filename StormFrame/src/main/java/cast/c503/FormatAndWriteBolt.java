package cast.c503;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by youngcle on 15-11-30.
 */
public class FormatAndWriteBolt implements IRichBolt {

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

    }

    public void execute(Tuple tuple) {
        ArrayList<byte[]> databytes = (ArrayList<byte[]>) tuple.getValueByField("DATABYTESLIST");
        ArrayList<Integer> dataIDs = (ArrayList<Integer>)  tuple.getValueByField("DATAIDSLIST");
        //结束标识判断
        if(dataIDs.get(dataIDs.size()-1) ==-1){
         //结束处理
            System.out.println("Got a end flag! It's time to stop!!!!!!!!!!!!!");
        }else {
            String dataName = tuple.getStringByField("DATANAME");
            //将按照制定格式要求写出
            writeDataBytesToFile(databytes, dataName + "_merge.raw");
        }
    }
    private boolean writeDataBytesToFile(ArrayList<byte[]> byteslist, String filename){

        WritableByteChannel wbc = null;
        try {
            wbc = new FileOutputStream(new File("/dev/shm" + File.separator + filename), true).getChannel();
            for(byte[] bytes:byteslist){
                try{
                    ByteBuffer bb = ByteBuffer.wrap(bytes);
                    wbc.write(bb);


                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            wbc.close();
            return true;


        }catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}