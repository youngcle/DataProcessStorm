package cast.c503;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DemodRawDataPackage;
import cast.c503.DataProcessor.ParseProcessor;
import cast.c503.TaskAgents.TaskOrderParam;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by youngcle on 15-11-30.
 * 解格式处理
 */
public class DataParseBolt implements IRichBolt {
    final String TargetPath ="/dev/shm";

    OutputCollector collector;
    ParseProcessor parser;
    private ArrayList<DataPackageBase> inputPackagelist;
    private ArrayList<DataPackageBase> outputPackagelist;
    //准备工作，初始化
    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        collector = outputCollector;

        parser = new ParseProcessor();
        //初始化，做准备工作，仅运行一次
        parser.InitializeDataProcess();
        inputPackagelist = new ArrayList<DataPackageBase>();
        outputPackagelist = new ArrayList<DataPackageBase>();
    }

    //收到数据包后开始执行
    public void execute(Tuple tuple) {
        byte[] currentDatabytes = tuple.getBinaryByField("DATAPACK");
        int currentDataID = tuple.getIntegerByField("DATAPACKID");
        String currenDataName = tuple.getStringByField("DATANAME");

        //结束标识判断
        if(currentDataID ==-1){
            collector.emit("COMPRESSED_CCD_STREAM",new Values(null,-1,"CCD_COMPRESSED"));
            collector.emit("COMPRESSED_HSICCD_STREAM",new Values(null,-1,"HSICCD_COMPRESSED"));
            collector.emit("COMPRESSED_IRS_STREAM",new Values(null,-1,"IRS_COMPRESSED"));
            collector.emit("COMPRESSED_HSIIRS_STREAM",new Values(null,-1,"HSIIRS_COMPRESSED"));
        }else {


            DataPackageBase currentDataPackage = new DataPackageBase(currenDataName);
            currentDataPackage.addPayload(currentDataID, currentDatabytes);


            //构建解包的输入、输出的包列表
            inputPackagelist.clear();
            outputPackagelist.clear();
            inputPackagelist.add(currentDataPackage);


            parser.ReInitialize();
            //设置任务订单信息
            TaskOrderParam taskOrderParam = new TaskOrderParam();
            parser.setTaskParam(taskOrderParam);
            parser.setInputDataPackages(inputPackagelist);
            long starttime = System.currentTimeMillis();
//        开始处理
            parser.DoDataProcess();

//测速
            long stoptime = System.currentTimeMillis();

            int datasize = 0;
            int packagecount = 0;
            for (DataPackageBase dpb : inputPackagelist) {
                for (Iterator iterator = dpb.getPayloadsBufferMap().keySet().iterator(); iterator.hasNext(); ) {
                    Integer NumCode = (Integer) iterator.next();
                    ByteBuffer byteBuffer = dpb.getPayloadsBufferMap().get(NumCode);
                    datasize += byteBuffer.limit();
                    packagecount++;
                }
            }
            long timeeplasped = (stoptime - starttime);
            float speed = datasize / (stoptime - starttime);
            System.out.println();
            System.out.println("Prasing is done.Processed package:" + packagecount + " data(byte):" + datasize + " takes time elasped(ms):" + timeeplasped + " speed(KB/s):" + speed);


            //获得处理完成的结果数据包
            outputPackagelist.addAll(parser.getOutputDataPackages());

            //将输出包列表中的数据全部发出
            for (DataPackageBase dpb : outputPackagelist) {
                String payloadname = dpb.getPayloadName();
                for (int dataID : dpb.getPayloadsBufferMap().keySet()) {
                    byte[] databytes = dpb.getPayloadsBufferMap().get(dataID).array();
                    String dataName = dpb.getPayloadName();
                    if (dataName.contentEquals("CCD_COMPRESSED"))
                        collector.emit("COMPRESSED_CCD_STREAM", new Values(databytes, dataID, dataName));
                    else if (dataName.contentEquals("HSICCD_COMPRESSED"))
                        collector.emit("COMPRESSED_HSICCD_STREAM", new Values(databytes, dataID, dataName));
                    else if (dataName.contentEquals("IRS_COMPRESSED"))
                        collector.emit("COMPRESSED_IRS_STREAM", new Values(databytes, dataID, dataName));
                    else if (dataName.contentEquals("HSIIRS_COMPRESSED"))
                        collector.emit("COMPRESSED_HSIIRS_STREAM", new Values(databytes, dataID, dataName));
                }

            }


//        for(Integer key:currentDataPackage.getPayloadsBufferMap().keySet()) {
//            ByteBuffer bb = (currentDataPackage.getPayloadsBufferMap().get(key));
//            int PackID = key;
//            try {
//                File Outputfile = new File(TargetPath + File.separator + "PACK_back" + PackID + ".zip");
//                FileChannel fileChannel = new FileOutputStream(Outputfile).getChannel();
//                fileChannel.write(bb);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
        }
    }

    //结束前清理
    public void cleanup() {
        parser.CleanDataProcess();

    }

    //声明输出域
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream("COMPRESSED_CCD_STREAM",new Fields("DATAPACK","DATAPACKID","DATANAME"));
        outputFieldsDeclarer.declareStream("COMPRESSED_HSICCD_STREAM",new Fields("DATAPACK","DATAPACKID","DATANAME"));
        outputFieldsDeclarer.declareStream("COMPRESSED_IRS_STREAM",new Fields("DATAPACK","DATAPACKID","DATANAME"));
        outputFieldsDeclarer.declareStream("COMPRESSED_HSIIRS_STREAM",new Fields("DATAPACK","DATAPACKID","DATANAME"));


    }

    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}
