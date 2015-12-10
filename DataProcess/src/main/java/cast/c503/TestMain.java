package cast.c503;

import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DemodRawDataPackage;
import cast.c503.DataProcessor.DeformatProcessor;
import cast.c503.DataProcessor.ImageTager;
import cast.c503.DataProcessor.ParseProcessor;
import cast.c503.TaskAgents.TaskOrderParam;
import com.alibaba.simpleimage.io.ByteArrayInputStream;
import com.sun.org.apache.xml.internal.utils.res.StringArrayWrapper;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import org.apache.commons.lang.CharSet;
import org.apache.commons.lang.CharSetUtils;
import org.apache.commons.lang.CharUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
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




        //构建输入的包
        List<DataPackageBase> dataPackageList = new ArrayList<DataPackageBase>();
        DemodRawDataPackage demodRawDataPackage1 = new DemodRawDataPackage();
        demodRawDataPackage1.setPackageNum(1);
        String filepathstr = "/home/youngcle/DataProcessStormNew/DataProcess/src/main/resources";
        demodRawDataPackage1.addPayloadfromFile(2011,filepathstr+"/CCD.jpg");
        demodRawDataPackage1.addPayloadfromFile(2012,filepathstr+"/HSIIRS.jpg");
        demodRawDataPackage1.addPayloadfromFile(2013,filepathstr+"/IRS.jpg");
        demodRawDataPackage1.addPayloadfromFile(2014,filepathstr+"/CCD.jpg");

        DemodRawDataPackage demodRawDataPackage2 = new DemodRawDataPackage();
        demodRawDataPackage2.setPackageNum(1);
        demodRawDataPackage2.addPayloadfromFile(2015,filepathstr+"/CCD.jpg");


        //构建输入的包列表
        ArrayList<DataPackageBase> inputPackagelist = new ArrayList<DataPackageBase>();
        inputPackagelist.add(demodRawDataPackage1);
        inputPackagelist.add(demodRawDataPackage2);

        ArrayList<DataPackageBase> outputPackagelist;

        ParseProcessor parser = new ParseProcessor();


        //初始化，做准备工作，仅运行一次
        parser.InitializeDataProcess();


        //设置任务订单信息
        TaskOrderParam taskOrderParam = new TaskOrderParam();
        parser.setTaskParam(taskOrderParam);
        //设置数据包列表
        parser.setInputDataPackages(inputPackagelist);
//        开始处理
        parser.DoDataProcess();

        //获得处理完成的结果数据包
        outputPackagelist = parser.getOutputDataPackages();

        //扫尾，结束前释放资源，仅运行一次
        parser.CleanDataProcess();




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
