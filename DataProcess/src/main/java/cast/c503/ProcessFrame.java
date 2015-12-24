package cast.c503;

import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DemodRawDataPackage;
import cast.c503.DataProcessor.DecompressProcessor;
import cast.c503.DataProcessor.ParseProcessor;
import cast.c503.TaskAgents.TaskOrderParam;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yanghl on 15-12-16.
 */
public class ProcessFrame implements Runnable {

    String processname;
    public  ProcessFrame(String name){
        processname = name;
    }
    public void run() {
        InitializeFrame();
        System.out.println("PopFromJedisAndDoProcess thread:"+processname+"   is up,ID:"+Thread.currentThread().getId());
        PopFromJedisAndDoProcess();
    }


    void PopFromJedisAndDoProcess(){
        BinaryJedis jedis = new Jedis("localhost");
        System.out.println();
        System.out.println("start to popping the binary data(in memory) to redis");
        long timestart = System.currentTimeMillis();
        long timepassed = 0;

        byte[] bytesfromjedis =null;
        long datasize = 0;
        String TargetPath ="/dev/shm";
        int PackID = -1;
        while(true) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            List<byte[]> currentPACKIDlist = jedis.blpop(2,"DATA:PACKID".getBytes());
            if (currentPACKIDlist == null)
                continue;
            else {
                byte[] idbytes = currentPACKIDlist.get(1);
                buffer.put(idbytes);
                buffer.flip();
                PackID = buffer.getInt();
            }

            List<byte[]> currentPACKlist = jedis.blpop(2,"DATA:PACK".getBytes());
            if (currentPACKlist == null)
                continue;
            else {
                bytesfromjedis = currentPACKlist.get(1);
            }
            if(bytesfromjedis==null) continue;


            long filesize = bytesfromjedis.length;
            ByteBuffer bb = ByteBuffer.wrap(bytesfromjedis);
            try {
                File Outputfile= new File(TargetPath+File.separator+"PACK_back"+PackID+".zip");
                FileChannel fileChannel = new FileOutputStream(Outputfile).getChannel();
                fileChannel.write(bb);
            } catch (Exception e) {
                e.printStackTrace();
            }

            ArrayList<Integer> PackIDList = new ArrayList<Integer>();
            PackIDList.add(PackID);
            ArrayList<byte[]> PackList = new ArrayList<byte[]>();
            PackList.add(bytesfromjedis);
            try {
                Depackanddecompress(PackIDList,PackList);
            } catch (Exception e) {
                e.printStackTrace();
            }


            datasize +=filesize;
            timepassed = System.currentTimeMillis() - timestart;
            System.out.println("data poped(KB):"+datasize/1024);
            System.out.println("time elasped(s):"+timepassed);
            System.out.println("pop and process speed(KB/S):"+datasize/timepassed);
        }


//        jedis.close();
    }

    ParseProcessor parser;
    DecompressProcessor CCDdecompressProcessor;
    DecompressProcessor HSICCDdecompressProcessor;
    DecompressProcessor IRSdecompressProcessor;
    DecompressProcessor HSIIRSdecompressProcessor;

    ArrayList<DataPackageBase> inputPackagelist_decom;
    ArrayList<DataPackageBase> outputPackagelist_decom;
    ArrayList<DataPackageBase> inputPackagelist;
    ArrayList<DataPackageBase> outputPackagelist;
    void InitializeFrame(){

        parser = new ParseProcessor();


        //初始化，做准备工作，仅运行一次
        parser.InitializeDataProcess();


        CCDdecompressProcessor = new DecompressProcessor();
        HSICCDdecompressProcessor = new DecompressProcessor();
        IRSdecompressProcessor = new DecompressProcessor();
        HSIIRSdecompressProcessor = new DecompressProcessor();
        inputPackagelist_decom = new ArrayList<DataPackageBase>();
        outputPackagelist_decom = new ArrayList<DataPackageBase>();
        inputPackagelist = new ArrayList<DataPackageBase>();
        outputPackagelist = new ArrayList<DataPackageBase>();

        parser.setInputDataPackages(inputPackagelist);
        parser.setInputDataPackages(outputPackagelist);
        CCDdecompressProcessor.setInputDataPackages(inputPackagelist_decom);
        CCDdecompressProcessor.setInputDataPackages(inputPackagelist_decom);
        CCDdecompressProcessor.setInputDataPackages(inputPackagelist_decom);
        CCDdecompressProcessor.setInputDataPackages(inputPackagelist_decom);
    }

    void CleanFrame(){
        //扫尾，结束前释放资源，仅运行一次
        parser.CleanDataProcess();
    }
    void Depackanddecompress(ArrayList<Integer> PackIDList,ArrayList<byte[]> PackBytesList)throws Exception{
        //构建输入的包



        //构建解包的输入、输出的包列表
        inputPackagelist.clear();
        outputPackagelist.clear();
        int count = PackBytesList.size();
        for(int i=0;i<count;i++) {
            DemodRawDataPackage demodRawDataPackage1 = new DemodRawDataPackage("RAWPACK");
            demodRawDataPackage1.setPackageNumCode(PackIDList.get(i));
            demodRawDataPackage1.addPayload(PackIDList.get(i),PackBytesList.get(i));
            inputPackagelist.add(demodRawDataPackage1);
        }


        parser.ReInitialize();
        //设置任务订单信息
        TaskOrderParam taskOrderParam = new TaskOrderParam();
        parser.setTaskParam(taskOrderParam);
        //设置数据包列表
        parser.setInputDataPackages(inputPackagelist);
        parser.setOutputDataPackages(outputPackagelist);


        long starttime = System.currentTimeMillis();
//        开始处理
        parser.DoDataProcess();

//测速
        long stoptime = System.currentTimeMillis();

        int datasize = 0;
        int packagecount = 0;
        for(DataPackageBase dpb:inputPackagelist){
            for (Iterator iterator = dpb.getPayloadsBufferMap().keySet().iterator(); iterator.hasNext(); ) {
                Integer NumCode = (Integer) iterator.next();
                ByteBuffer byteBuffer = dpb.getPayloadsBufferMap().get(NumCode);
                datasize += byteBuffer.limit();
                packagecount++;
            }
        }
        long timeeplasped = (stoptime-starttime);
        float speed = datasize/(stoptime-starttime);
        System.out.println();
        System.out.println("Prasing is done.Processed package:"+packagecount+" data(byte):"+datasize+" takes time elasped(ms):"+timeeplasped+" speed(KB/s):"+speed);


        //获得处理完成的结果数据包
        outputPackagelist.addAll(parser.getOutputDataPackages());

        //构建解压缩的输入、输出的包列表


        CCDdecompressProcessor.ReInitialize();
        IRSdecompressProcessor.ReInitialize();;
        HSIIRSdecompressProcessor.ReInitialize();
        HSICCDdecompressProcessor.ReInitialize();


        //设置任务订单信息
        CCDdecompressProcessor.setTaskParam(taskOrderParam);
        IRSdecompressProcessor.setTaskParam(taskOrderParam);
        HSIIRSdecompressProcessor.setTaskParam(taskOrderParam);
        HSICCDdecompressProcessor.setTaskParam(taskOrderParam);

        //设置输入数据包
        inputPackagelist_decom.clear();
        outputPackagelist_decom.clear();
        inputPackagelist_decom.addAll(outputPackagelist);

        for(DataPackageBase dpb:inputPackagelist_decom) {
            String payloadname = dpb.getPayloadName();
            if(payloadname=="CCD" )
                CCDdecompressProcessor.addInputDataPackage(dpb);
            else if(payloadname=="HSICCD")
                HSICCDdecompressProcessor.addInputDataPackage(dpb);
            else if(payloadname== "IRS")
                IRSdecompressProcessor.addInputDataPackage(dpb);
            else if(payloadname== "HSIIRS")
                HSIIRSdecompressProcessor.addInputDataPackage(dpb);
            else {

                //错误处理
                // ;
            }

        }

        CCDdecompressProcessor.DoDataProcess();
        HSICCDdecompressProcessor.DoDataProcess();
        IRSdecompressProcessor.DoDataProcess();
        HSIIRSdecompressProcessor.DoDataProcess();

//测速
        stoptime = System.currentTimeMillis();
        datasize = 0;
        packagecount = 0;
        for(DataPackageBase dpb:inputPackagelist_decom){
            for (Iterator iterator = dpb.getPayloadsBufferMap().keySet().iterator(); iterator.hasNext(); ) {
                Integer NumCode = (Integer) iterator.next();
                ByteBuffer byteBuffer = dpb.getPayloadsBufferMap().get(NumCode);
                datasize += byteBuffer.limit();
                packagecount++;
            }
        }
        timeeplasped = (stoptime-starttime);
        speed = datasize/(stoptime-starttime);

        System.out.println("Decompressing is done.processed package:"+packagecount+" data size(byte)"+datasize+" takes time elasped(ms):"+timeeplasped+" speed(KB/s):"+speed);
        System.out.println("______________________________________________________________________");



        //构建解压缩后的输出数据包队列
        outputPackagelist_decom.addAll(CCDdecompressProcessor.getOutputDataPackages());
        outputPackagelist_decom.addAll(HSICCDdecompressProcessor.getOutputDataPackages());
        outputPackagelist_decom.addAll(IRSdecompressProcessor.getOutputDataPackages());
        outputPackagelist_decom.addAll(HSIIRSdecompressProcessor.getOutputDataPackages());


        //输出文件
        for(DataPackageBase dpb:outputPackagelist_decom){
            if(dpb.getPayloadsBufferMap().size()!=0) {
                int codenum = (Integer) dpb.getPayloadsBufferMap().keySet().toArray()[0];
                ByteBuffer bb = (ByteBuffer) dpb.getPayloadsBufferMap().get(codenum);
                WritableByteChannel wbc = new FileOutputStream(new File("/dev/shm" + File.separator + dpb.getPayloadName()+"_"+codenum + ".raw")).getChannel();
                wbc.write(bb);
                wbc.close();
            }
        }

    }
}


