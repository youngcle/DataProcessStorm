package cast.c503;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.IRichBolt;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataProcessor.DecompressProcessor;
import cast.c503.TaskAgents.TaskOrderParam;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by youngcle on 15-11-30.
 * 解压缩处理
 */
public class DecompressCCDBolt implements IRichBolt {

    DecompressProcessor CCDdecompressProcessor;
    DecompressProcessor HSICCDdecompressProcessor;
    DecompressProcessor IRSdecompressProcessor;
    DecompressProcessor HSIIRSdecompressProcessor;

    ArrayList<DataPackageBase> inputPackagelist_decom;
    ArrayList<DataPackageBase> outputPackagelist_decom;
    private boolean DebugMode = false;
    OutputCollector Collector;


    public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
        CCDdecompressProcessor = new DecompressProcessor();
        CCDdecompressProcessor.InitializeDataProcess();

        HSICCDdecompressProcessor = new DecompressProcessor();
        HSICCDdecompressProcessor.InitializeDataProcess();

        IRSdecompressProcessor = new DecompressProcessor();
        IRSdecompressProcessor.InitializeDataProcess();

        HSIIRSdecompressProcessor = new DecompressProcessor();
        HSIIRSdecompressProcessor.InitializeDataProcess();

        Collector = outputCollector;
        inputPackagelist_decom = new ArrayList<DataPackageBase>();
        outputPackagelist_decom = new ArrayList<DataPackageBase>();
//        DebugMode = (Boolean) map.get("DebugMode");
    }

    public void execute(Tuple tuple) {

        long starttime = System.currentTimeMillis();


        byte[] currentDatabytes = tuple.getBinaryByField("DATAPACK");
        int currentDataID = tuple.getIntegerByField("DATAPACKID");
        String currenDataName = tuple.getStringByField("DATANAME");
        //结束标识判断
        if(currentDataID ==-1){
            Collector.emit("RAW_CCD_STREAM",new Values(null,-1,"CCD_COMPRESSED_DECOMPRESSED"));
        }else {


            DataPackageBase currentDataPackage = new DataPackageBase(currenDataName);
            currentDataPackage.addPayload(currentDataID, currentDatabytes);


            //构建解包的输入、输出的包列表
            inputPackagelist_decom.clear();
            outputPackagelist_decom.clear();
            inputPackagelist_decom.add(currentDataPackage);

            CCDdecompressProcessor.ReInitialize();
            HSICCDdecompressProcessor.ReInitialize();
            IRSdecompressProcessor.ReInitialize();
            HSIIRSdecompressProcessor.ReInitialize();

            if (currenDataName.contentEquals("CCD_COMPRESSED")) {
                //设置任务订单信息

                TaskOrderParam taskOrderParam = new TaskOrderParam();
                CCDdecompressProcessor.setTaskParam(taskOrderParam);
                CCDdecompressProcessor.setInputDataPackages(inputPackagelist_decom);
                CCDdecompressProcessor.DoDataProcess();
                //构建解压缩后的输出数据包队列
                outputPackagelist_decom.addAll(CCDdecompressProcessor.getOutputDataPackages());
            } else if (currenDataName.contentEquals("HSICCD_COMPRESSED")) {
                TaskOrderParam taskOrderParam = new TaskOrderParam();
                HSICCDdecompressProcessor.setTaskParam(taskOrderParam);
                HSICCDdecompressProcessor.setInputDataPackages(inputPackagelist_decom);
                HSICCDdecompressProcessor.DoDataProcess();
                outputPackagelist_decom.addAll(HSICCDdecompressProcessor.getOutputDataPackages());
            } else if (currenDataName.contentEquals("IRS_COMPRESSED")) {
                TaskOrderParam taskOrderParam = new TaskOrderParam();
                IRSdecompressProcessor.setTaskParam(taskOrderParam);
                IRSdecompressProcessor.setInputDataPackages(inputPackagelist_decom);
                IRSdecompressProcessor.DoDataProcess();
                outputPackagelist_decom.addAll(IRSdecompressProcessor.getOutputDataPackages());
            } else if (currenDataName.contentEquals("HSIIRS_COMPRESSED")) {
                TaskOrderParam taskOrderParam = new TaskOrderParam();
                HSIIRSdecompressProcessor.setTaskParam(taskOrderParam);
                HSIIRSdecompressProcessor.setInputDataPackages(inputPackagelist_decom);
                HSIIRSdecompressProcessor.DoDataProcess();
                outputPackagelist_decom.addAll(HSIIRSdecompressProcessor.getOutputDataPackages());
            }


            //发射结果包
            //将输出包列表中的数据全部发出
            for (DataPackageBase dpb : outputPackagelist_decom) {
                String payloadname = dpb.getPayloadName();
                for (int dataID : dpb.getPayloadsBufferMap().keySet()) {
                    byte[] databytes = dpb.getPayloadsBufferMap().get(dataID).array();
                    String dataName = dpb.getPayloadName();

                    if (dataName.contentEquals("CCD_COMPRESSED_DECOMPRESSED"))
                        Collector.emit("RAW_CCD_STREAM", new Values(databytes, dataID, dataName));
                    else if (dataName.contentEquals("HSICCD_COMPRESSED_DECOMPRESSED"))
                        Collector.emit("RAW_HSICCD_STREAM", new Values(databytes, dataID, dataName));
                    else if (dataName.contentEquals("IRS_COMPRESSED_DECOMPRESSED"))
                        Collector.emit("RAW_IRS_STREAM", new Values(databytes, dataID, dataName));
                    else if (dataName.contentEquals("HSIIRS_COMPRESSED_DECOMPRESSED"))
                        Collector.emit("RAW_HSIIRS_STREAM", new Values(databytes, dataID, dataName));
                }
            }


            //测速
            long stoptime = System.currentTimeMillis();
            int datasize = 0;
            int packagecount = 0;
            for (DataPackageBase dpb : inputPackagelist_decom) {
                for (Iterator iterator = dpb.getPayloadsBufferMap().keySet().iterator(); iterator.hasNext(); ) {
                    Integer NumCode = (Integer) iterator.next();
                    ByteBuffer byteBuffer = dpb.getPayloadsBufferMap().get(NumCode);
                    datasize += byteBuffer.limit();
                    packagecount++;
                }
            }
            long timeeplasped = (stoptime - starttime);
            if (timeeplasped == 0) timeeplasped = -1;
            float speed = datasize / timeeplasped;

            System.out.println("Decompressing is done.processed package:" + packagecount + " data size(byte)" + datasize + " takes time elasped(ms):" + timeeplasped + " speed(KB/s):" + speed);
            System.out.println("______________________________________________________________________");

            if (DebugMode) {
                //输出文件
                for (DataPackageBase dpb : outputPackagelist_decom) {
                    if (dpb.getPayloadsBufferMap().size() != 0) {
                        int codenum = (Integer) dpb.getPayloadsBufferMap().keySet().toArray()[0];
                        ByteBuffer bb = (ByteBuffer) dpb.getPayloadsBufferMap().get(codenum);
                        WritableByteChannel wbc = null;
                        try {
                            wbc = new FileOutputStream(new File("/dev/shm" + File.separator + dpb.getPayloadName() + "_" + codenum + ".raw")).getChannel();
                            wbc.write(bb);
                            wbc.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }
        }

    }

    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
        outputFieldsDeclarer.declareStream("RAW_CCD_STREAM",new Fields("DATAPACK","DATAPACKID","DATANAME"));
        outputFieldsDeclarer.declareStream("RAW_HSICCD_STREAM",new Fields("DATAPACK","DATAPACKID","DATANAME"));
        outputFieldsDeclarer.declareStream("RAW_IRS_STREAM",new Fields("DATAPACK","DATAPACKID","DATANAME"));
        outputFieldsDeclarer.declareStream("RAW_HSIIRS_STREAM",new Fields("DATAPACK","DATAPACKID","DATANAME"));
    }

    public void cleanup() {
        //扫尾，结束前释放资源，仅运行一次
        CCDdecompressProcessor.CleanDataProcess();
        HSICCDdecompressProcessor.CleanDataProcess();
        IRSdecompressProcessor.CleanDataProcess();
        HSIIRSdecompressProcessor.CleanDataProcess();


    }



    public Map<String, Object> getComponentConfiguration() {
        return null;
    }
}