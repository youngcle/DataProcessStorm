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

    public void run() {

    }


    void PopFromJedisAnd(){
        BinaryJedis jedis = new Jedis("localhost");
        System.out.println();
        System.out.println("start to popping the binary data(in memory) to redis");
        long timestart = System.currentTimeMillis();
        long timepassed = 0;

        byte[] bytesfromjedis =null;
        long datasize = 0;
        String TargetPath ="/dev/shm";
        while(true) {
            ByteBuffer buffer = ByteBuffer.allocate(4);
            byte[] idbytes= jedis.blpop(10,"DATA:PACKID".getBytes()).get(1);
            if(idbytes==null) continue;

            buffer.put(idbytes);
            buffer.flip();
            int PackID = buffer.getInt();

            bytesfromjedis = jedis.blpop(10,("DATA:PACK").getBytes()).get(1);
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


    void Depackanddecompress(ArrayList<Integer> PackIDList,ArrayList<byte[]> PackBytesList)throws Exception{
        //构建输入的包
        List<DataPackageBase> dataPackageList = new ArrayList<DataPackageBase>();


        //构建输入的包列表
        ArrayList<DataPackageBase> inputPackagelist = new ArrayList<DataPackageBase>();
        int count = PackBytesList.size();
        for(int i=0;i<count;i++) {
            DemodRawDataPackage demodRawDataPackage1 = new DemodRawDataPackage("RAWPACK");
            demodRawDataPackage1.setPackageNumCode(PackIDList.get(i));
            demodRawDataPackage1.addPayload(PackIDList.get(i),PackBytesList.get(i));
            inputPackagelist.add(demodRawDataPackage1);
        }
        ArrayList<DataPackageBase> outputPackagelist;

        ParseProcessor parser = new ParseProcessor();


        //初始化，做准备工作，仅运行一次
        parser.InitializeDataProcess();


        //设置任务订单信息
        TaskOrderParam taskOrderParam = new TaskOrderParam();
        parser.setTaskParam(taskOrderParam);
        //设置数据包列表
        parser.setInputDataPackages(inputPackagelist);


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
        outputPackagelist = parser.getOutputDataPackages();

        //扫尾，结束前释放资源，仅运行一次
        parser.CleanDataProcess();


        DecompressProcessor CCDdecompressProcessor = new DecompressProcessor();
        DecompressProcessor HSICCDdecompressProcessor = new DecompressProcessor();
        DecompressProcessor IRSdecompressProcessor = new DecompressProcessor();
        DecompressProcessor HSIIRSdecompressProcessor = new DecompressProcessor();
        //设置任务订单信息
        CCDdecompressProcessor.setTaskParam(taskOrderParam);
        IRSdecompressProcessor.setTaskParam(taskOrderParam);
        HSIIRSdecompressProcessor.setTaskParam(taskOrderParam);
        HSICCDdecompressProcessor.setTaskParam(taskOrderParam);

        //设置输入数据包
        inputPackagelist = outputPackagelist;
        for(DataPackageBase dpb:inputPackagelist) {
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
        for(DataPackageBase dpb:inputPackagelist){
            for (Iterator iterator = dpb.getPayloadsBufferMap().keySet().iterator(); iterator.hasNext(); ) {
                Integer NumCode = (Integer) iterator.next();
                ByteBuffer byteBuffer = dpb.getPayloadsBufferMap().get(NumCode);
                datasize += byteBuffer.limit();
                packagecount++;
            }
        }
        timeeplasped = (stoptime-starttime);
        speed = datasize/(stoptime-starttime);
        System.out.println();
        System.out.println("Decompressing is done.processed package:"+packagecount+" data size(byte)"+datasize+" takes time elasped(ms):"+timeeplasped+" speed(KB/s):"+speed);



        outputPackagelist.clear();

        for(DataPackageBase dpb:CCDdecompressProcessor.getOutputDataPackages()) {
            DataPackageBase outdecopress_CCD = dpb;
            outputPackagelist.add(outdecopress_CCD);
        }
        for(DataPackageBase dpb:HSICCDdecompressProcessor.getOutputDataPackages()) {
            DataPackageBase outdecopress_HSICCD = dpb;
            outputPackagelist.add(outdecopress_HSICCD);
        }
        for(DataPackageBase dpb:IRSdecompressProcessor.getOutputDataPackages()) {
            DataPackageBase outdecopress_IRS = dpb;
            outputPackagelist.add(outdecopress_IRS);
        }
        for(DataPackageBase dpb:HSIIRSdecompressProcessor.getOutputDataPackages()) {
            DataPackageBase outdecopress_HSIIRS = dpb;
            outputPackagelist.add(outdecopress_HSIIRS);
        }

        for(DataPackageBase dpb:outputPackagelist){
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


