package cast.c503;

import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DemodRawDataPackage;
import cast.c503.DataProcessor.DecompressProcessor;
import cast.c503.DataProcessor.ParseProcessor;
import cast.c503.TaskAgents.TaskOrderParam;
import javafx.scene.chart.PieChart;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Jedis;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.WritableByteChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by yanghl on 15-12-3.
 *
 *
 *
 *
 *
 *
 */
public class TestMain {
    public static void main(String[] args) throws Exception {
//单通道数据处理流程模拟


        //产生原始数据，单路
//        生成AOS帧

        RawStreamGenerator rsg = new RawStreamGenerator();
        rsg.InitRawStream();



        for(int i=0;i<48;i++) {
            new Thread(new Runnable() {

                public void run() {
                    try {
                        DepackanddecompressTest(0, 20);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }




                void DepackanddecompressTest(int basenum,int count)throws Exception{
                    //构建输入的包
                    List<DataPackageBase> dataPackageList = new ArrayList<DataPackageBase>();

                    String filepathstr = "/data/simdata";
                    //构建输入的包列表
                    ArrayList<DataPackageBase> inputPackagelist = new ArrayList<DataPackageBase>();
                    for(int i=0;i<count;i++) {
                        DemodRawDataPackage demodRawDataPackage1 = new DemodRawDataPackage("RAWPACK");
                        demodRawDataPackage1.setPackageNumCode(i);
                        demodRawDataPackage1.addPayloadfromFile(i, filepathstr + "/PACK_"+i+".zip");
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

//        for(DataPackageBase dpb:outputPackagelist){
//            if(dpb.getPayloadsBufferMap().size()!=0) {
//                int codenum = (Integer) dpb.getPayloadsBufferMap().keySet().toArray()[0];
//                ByteBuffer bb = (ByteBuffer) dpb.getPayloadsBufferMap().get(codenum);
//                WritableByteChannel wbc = new FileOutputStream(new File("/dev/shm" + File.separator + dpb.getPayloadName()+"_"+(basenum+codenum) + ".raw")).getChannel();
//                wbc.write(bb);
//                wbc.close();
//            }
//        }

                }
            });

//            System.out.println("Thread "+i+" is startup!");
        }

        ProcessFrame pf = new ProcessFrame();
        pf.PopFromJedisAnd();


//        for(DataPackageBase dpb:outputPackagelist){
//            int codenum = (Integer) dpb.getPayloadsBufferMap().entrySet().toArray()[0];
//            ByteBuffer bb = (ByteBuffer)dpb.getPayloadsBufferMap().entrySet().toArray()[1];
//            WritableByteChannel wbc = new FileInputStream(new File("/dev/shm"+File.separator+"0"+".raw")).getChannel();
//            wbc.write(bb);
//            wbc.close();
//        }

//        文件包输出或落地




//        List<char[]> rawDataPackageList = rsg.getRawDataPackageList();
//        for (int i = 0; i < rawDataPackageList.size(); i++) {
////            StringBuilder sb = new StringBuilder().append(rawDataPackageList.get(i));
////            System.out.println(sb.toString());
//            DataPackageBase drdp = new DemodRawDataPackage();
//            drdp.addPayload(rawDataPackageList.get(i));
//            LinkedList<DataPackageBase> inputlist = new LinkedList<DataPackageBase>();
//            inputlist.add(drdp);
//            DeformatProcessor deformatProcessor = new DeformatProcessor(inputlist);
//            deformatProcessor.InitializeDataProcess();
//            deformatProcessor.DoDataProcess();
//            deformatProcessor.CleanDataProcess();
//        }


        //进行解格式处理，生成压缩数据包。生成模式：1：n
    //        数据拆分为8路，包可能有无码丢失，有长有短
    //        可见光相机CCD1数据
    //        可见光相机CCD2数据
    //        可见光相机CCD3数据
    //        可见光相机CCD4数据
    //        高光谱SWIR-1数据
    //        高光谱VNIR-1数据
    //        红外-1数据
    //        SDRTU数据（包括GPS）



//        需要对若干AOS包拼接，组成压缩数据。


        //合成压缩数据包处理。生成模式：n：1
//        可见光相机CCD1压缩包数据
//        合成压缩包


        //读入压缩包数据，进行解压缩处理，生成解压缩数据包。
        //压缩包数据格式完整。1：1。


        //读入解压缩后数据包，分片生成快视图像数据
        //1：1


        //读入解压缩后数据包，生成0级数据，进行必要的拼接处理
        //N：1







    }


}
