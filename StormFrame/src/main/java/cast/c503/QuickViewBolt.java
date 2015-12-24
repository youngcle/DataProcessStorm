package cast.c503;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Tuple;
import cast.c503.DataProcessor.ImageTager;
import com.alibaba.simpleimage.io.ByteArrayOutputStream;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by youngcle on 15-11-30.
 */
public class QuickViewBolt implements IRichBolt {

    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {

    }

    public void execute(Tuple tuple) {
        ArrayList<byte[]> databytes = (ArrayList<byte[]>) tuple.getValueByField("DATABYTESLIST");
        ArrayList<Integer> dataIDs = (ArrayList<Integer>)  tuple.getValueByField("DATAIDSLIST");
        String dataName = tuple.getStringByField("DATANAME");

        //结束
        if(dataIDs.get(dataIDs.size()-1)==-1){
            //结束处理
            System.out.println("Got a end flag! It's time to stop!!!!!!!!!!!!!");
        }else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            byte[] outputbytes = null;
            String TargetPath = "/dev/shm";

            File outputfile = new File(TargetPath + File.separator + dataName + "_" + dataIDs.get(0) + "_" + dataIDs.get(dataIDs.size() - 1) + "_qv" + ".raw");
            BufferedOutputStream bos = null;
            try {
                bos = new BufferedOutputStream(new FileOutputStream(outputfile, true));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            for (byte[] bytes : databytes) {
                try {

                    bos.write(bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                bos.flush();
                bos.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
            //生成快视图像


//            ImageTager it = new ImageTager();
//            it.setInputImageStream(baos.toInputStream());
//            it.DoImageTag(outputfile,dataName+"快视","yellow",(dataIDs.get(0)+"----"+dataIDs.get(dataIDs.size())));
    }

    public void cleanup() {

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {


    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}