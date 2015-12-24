package cast.c503;

import cast.c503.DataProcessor.ImageTager;
import com.alibaba.simpleimage.io.*;
import com.alibaba.simpleimage.render.FixDrawTextItem;
import com.sun.corba.se.impl.oa.toa.TOA;
import com.sun.corba.se.spi.ior.Writeable;
import com.sun.deploy.util.ArrayUtil;
import com.sun.media.jai.opimage.FileStoreRIF;
import com.sun.org.apache.xml.internal.security.utils.JavaUtils;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.*;
import java.util.zip.*;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.SystemUtils;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Connection;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Protocol.Command;
import redis.clients.jedis.commands.ProtocolCommand;
import redis.clients.jedis.exceptions.JedisConnectionException;
/**
 * Created by youngcle on 15-12-6.
 * 创建流式数据处理集群仿真数据
 * 数据为图片+zip，从而在集群里模拟unzip过程
 * 设置8个类别的图片文件。文件中标称载荷文字，帧序号。
 * 模拟拼接过程
 *
 * 输入：
 * 模板图片
 * 生成图片的数量。
 * 压缩形式
 * 生成图片的位置
 *  生成文件系统文件
 *  直接压入redis中的目标地址
 *“CCD-1”
 “IRS-1”
 “HSICCD-1”
 “HSIIRS-1”
 * 输出
 *  文件
 *  redis数据记录
 *
 */




public class SimulateDataGenerator {

    int TargetCount = DEFAULT_TARGETCOUNT;
    int TargetStartNum = DEFAULT_STARTNUM;
    int TotalCount = DEFAULT_TARGETCOUNT;
    String PackageType;
    String TargetPath;

    static int DEFAULT_STARTNUM = 0;
    static int DEFAULT_TARGETCOUNT = 10;
    static String DEFAULT_PackageType = "zip";
    static String DEFAULT_TARGETPATH = "/data/simdata";
    static String BasePath = "/run/media/yanghl/77b85fda-c795-4518-b551-cd0bde3131e5/youngcle/DataProcessStormNew/DataProcess/src/main/resources";
//    static String BasePath="/home/youngcle/DataProcessStormNew/DataProcess/src/main/resources";
    static String Templatefile_CCD = BasePath+File.separator+"CCD.jp2";
    static String Templatefile_HSICCD = BasePath+File.separator+"HSICCD.jp2";
    static String Templatefile_HSIIRS = BasePath+File.separator+"HSIIRS.jp2";
    static String Templatefile_IRS = BasePath+File.separator+"IRS.jp2";
    boolean ON_DEBUG = false;



    public SimulateDataGenerator(){

    }

    public SimulateDataGenerator(int targetStartNum,int targetCount,String packageType,String targetPath){
        TotalCount = targetCount;
        PackageType = packageType;
        TargetPath = targetPath;
        TargetStartNum = targetStartNum;


    }

    public void DoGenerateData(){

        int roundtorun = TotalCount/DEFAULT_TARGETCOUNT +1;
        int roundleft = TotalCount%DEFAULT_TARGETCOUNT;

        for(int k=0;k<roundtorun;k++) {
            int endroundflag =0;
            if(k==(roundtorun-1)){
                endroundflag =1;
            }else
                endroundflag = 0;

            for (int i = TargetStartNum+k * DEFAULT_TARGETCOUNT; i < TargetStartNum+((k+1-endroundflag)* DEFAULT_TARGETCOUNT+endroundflag*roundleft); i++) {
                TagFile(Templatefile_CCD, "CCD", i, "red");
                String zipname = TargetPath + File.separator + "CCD_" + i + ".jp2";
                try {
                    gzipFile(zipname);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            for (int i = TargetStartNum+k * DEFAULT_TARGETCOUNT; i < TargetStartNum+((k+1-endroundflag)* DEFAULT_TARGETCOUNT+endroundflag*roundleft); i++) {
                TagFile(Templatefile_HSIIRS, "HSIIRS", i, "green");
                String zipname = TargetPath + File.separator + "HSIIRS_" + i + ".jp2";
                try {
                    gzipFile(zipname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (int i = TargetStartNum+k * DEFAULT_TARGETCOUNT; i < TargetStartNum+((k+1-endroundflag)* DEFAULT_TARGETCOUNT+endroundflag*roundleft); i++) {
                TagFile(Templatefile_IRS, "IRS", i, "blue");
                String zipname = TargetPath + File.separator + "IRS_" + i + ".jp2";
                try {
                    gzipFile(zipname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            for (int i = TargetStartNum+k * DEFAULT_TARGETCOUNT; i < TargetStartNum+((k+1-endroundflag)* DEFAULT_TARGETCOUNT+endroundflag*roundleft); i++) {
                TagFile(Templatefile_HSICCD, "HSICCD", i, "orange");
                String zipname = TargetPath + File.separator + "HSICCD_" + i + ".jp2";
                try {
                    gzipFile(zipname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (int i = TargetStartNum+k * DEFAULT_TARGETCOUNT; i < TargetStartNum+((k+1-endroundflag)* DEFAULT_TARGETCOUNT+endroundflag*roundleft); i++) {
                java.util.List<String> fileslisttozip = new LinkedList<String>();
                String ccdname = TargetPath + File.separator + "CCD_" + i + ".jp2.gz";
                String IRSname = TargetPath + File.separator + "IRS_" + i + ".jp2.gz";
                String HSIIRSname = TargetPath + File.separator + "HSIIRS_" + i + ".jp2.gz";
                String HSICCDname = TargetPath + File.separator + "HSICCD_" + i + ".jp2.gz";
                String ZIPPackName = TargetPath + File.separator + "PACK_" + i;
                fileslisttozip.add(ccdname);
                fileslisttozip.add(IRSname);
                fileslisttozip.add(HSIIRSname);
                fileslisttozip.add(HSICCDname);
                try {
                    ZipFiles(ZIPPackName, fileslisttozip);
                } catch (IOException e) {
                    e.printStackTrace();
                }


            }
        }
    }


    File ReadTemlateFile(){

        return null;

    }

    void ZipFiles(String zipname,java.util.List<String> filenamelist) throws IOException {
        File outputfile = new File(zipname + ".zip");
        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputfile));
        ZipOutputStream zos = new ZipOutputStream(bo);
        WritableByteChannel wbc = Channels.newChannel(zos);

        for(String name:filenamelist) {

            File filetozip = new File(name);




            ZipEntry zipentry = new ZipEntry(filetozip.getName());
            ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024);
            FileChannel inputChannel = new FileInputStream(filetozip).getChannel();


            if (zos != null) {
                zos.putNextEntry(zipentry);
                inputChannel.transferTo(0, inputChannel.size(), wbc);
            }
            if(!ON_DEBUG){
                FileUtils.deleteQuietly(filetozip);
            }

        }
        zos.flush();
        zos.closeEntry();
        zos.close();



    }

    void gzipFile(String filename) throws IOException {

        File filetogzip = new File(filename);
        File outputfile= new File(filename+".gz");
        BufferedOutputStream bo = new BufferedOutputStream(new FileOutputStream(outputfile));
        OutputStream gzos = new GZIPOutputStream(bo);



        WritableByteChannel wbc = Channels.newChannel(gzos);

        FileChannel inputChannel= new FileInputStream(filetogzip).getChannel();


        if (gzos != null) {
            inputChannel.transferTo(0,inputChannel.size(),wbc);
            gzos.flush();
            gzos.close();
        }
        if(!ON_DEBUG){
            FileUtils.deleteQuietly(filetogzip);

        }
    }


    void TagFile(String tempStr,String tagText,int numCode,String colorstr){
        int nmCode = numCode;
//IRS
        File inputfile = new File(tempStr);

        File outputfile= new File(TargetPath+File.separator+tagText+"_"+nmCode+".jp2");

        ImageTager it = new ImageTager();
        it.setInputImageFile(inputfile);
        it.DoImageTag(outputfile,tagText,colorstr,numCode);


    }

    void WriteFile(){

    }


    void PushToJedis(){
        BinaryJedis jedis = new Jedis("localhost");
        long timeStart = System.currentTimeMillis();
        long timepassed = 0;
        long filesize = 0;
        long datasize = 0;
        float targetSpeed = 20000;//kb/s byte/ms
        long redisListLengthBar = 100;
        int round = 1000;
        long roundtimeStart =0;
        System.out.println("start to putting the binary data(in memory) to redis");
        System.out.println("file size(Byte):"+filesize+"   round ="+round);
        ByteBuffer buffer = ByteBuffer.allocate(4);
        long packcount = 0;
        long chkdatasize=0;
        long listlenth = 0;
        long gloabletimetosleep = 0;
//        while(true) {
            for (int i = 0; i < round; i++) {
                File inputfile = new File(TargetPath + File.separator + "PACK_" + i + ".zip");
                ByteBuffer bb = ByteBuffer.allocate((int) inputfile.length());
                try {
                    FileChannel fileChannel = new FileInputStream(inputfile).getChannel();
                    fileChannel.read(bb);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                jedis.rpush("DATA:PACK".getBytes(), bb.array());
                buffer.clear();
                jedis.rpush("DATA:PACKID".getBytes(), buffer.putInt(i).array());
                chkdatasize += bb.array().length;
                datasize += bb.array().length;
                if((i%10)== 0) {
                    long roundtimeStop = System.currentTimeMillis();
                    long roundtimePassed = roundtimeStop - roundtimeStart;
                    roundtimeStart = System.currentTimeMillis();
                    long targetroundtime = (long) (chkdatasize / targetSpeed);
                    chkdatasize = 0;
                    long timetosleep = targetroundtime - roundtimePassed;
                    if (timetosleep > 0) {
                        try {
                            Thread.currentThread().sleep(timetosleep);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }


                    timepassed = System.currentTimeMillis() - timeStart ;
                    System.out.println("data pushed(KB):"+datasize/1024);
                    System.out.println("time elasped(s):"+timepassed);
                    System.out.println("push speed(KB/S):"+(float)datasize/(float)timepassed);
                    System.out.println("data pack pushed into redis:"+packcount);
                    long redisListLenth = jedis.llen("DATA:PACKID".getBytes());
                    System.out.println("data pack list in redis is:"+redisListLenth);


                }
                packcount++;
                long redisListLenth = jedis.llen("DATA:PACKID".getBytes());
                if (redisListLengthBar > 1000) break;
                while(redisListLenth > redisListLengthBar){
//                    System.out.println("data pack in redis is not processed in time! length:"+redisListLenth);
                    try {
                        Thread.sleep(30);
                        redisListLenth = jedis.llen("DATA:PACKID".getBytes());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
//        }


        jedis.close();
    }

    void PopFromJedis(){
        BinaryJedis jedis = new Jedis("localhost");
        System.out.println();
        System.out.println("start to popping the binary data(in memory) to redis");
        long timepassed = System.currentTimeMillis();
        byte[] bytesfromjedis =null;
        int round = 300;
        long datasize = 0;
        for(int i=0;i<round;i++) {
            File Outputfile= new File(TargetPath+File.separator+"PACK_back"+i+".zip");
            bytesfromjedis = jedis.rpop(("DATA:PACK").getBytes());
            long filesize = bytesfromjedis.length;
            ByteBuffer bb = ByteBuffer.wrap(bytesfromjedis);
            try {
                FileChannel fileChannel = new FileOutputStream(Outputfile).getChannel();
                fileChannel.write(bb);

            } catch (Exception e) {
                e.printStackTrace();
            }

            ByteBuffer buffer = ByteBuffer.allocate(4);

            buffer.put(jedis.rpop("DATA:PACKID".getBytes()));
            buffer.flip();
            buffer.getInt();

            datasize +=filesize;
        }
        timepassed -= System.currentTimeMillis();
        System.out.println("data poped(KB):"+datasize/1024);
        System.out.println("time elasped(s):"+timepassed);
        System.out.println("pop speed(KB/S):"+datasize/timepassed);
        jedis.close();
    }


    void PushToRAM(){
        File inputfile= new File(TargetPath+File.separator+"PACK_0"+".zip");
        ByteBuffer bb = ByteBuffer.allocate((int) inputfile.length());
        try {
            FileChannel fileChannel = new FileInputStream(inputfile).getChannel();
            fileChannel.read(bb);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("start to putting the binary data(in memory) to RAM buffer");

        long timepassed = System.currentTimeMillis();
        long filesize = inputfile.length();
        long datasize = 0;
        int round = 2000;
        System.out.println("file size(KB):"+filesize/1024+"   round ="+round);
        ByteBuffer obb = ByteBuffer.allocate(bb.capacity());
        byte[] outbytearray = new byte[bb.capacity()];
        for(int i=0;i<round;i++) {
            System.arraycopy(bb.array(),0,outbytearray,0,bb.capacity());

        }
        datasize =filesize*round;
        timepassed -= System.currentTimeMillis();
        System.out.println("data pushed(KB):"+datasize/1024);
        System.out.println("time elasped(s):"+timepassed);
        System.out.println("push speed(KB/S):"+datasize/timepassed);
//        client.close();
    }

    public static void main(String[] args) throws Exception {
        SimulateDataGenerator simulateDataGenerator = new SimulateDataGenerator(5000,50000,"zip","/data/simdata/");
//        simulateDataGenerator.DoGenerateData();
        simulateDataGenerator.PushToJedis();
//        simulateDataGenerator.PopFromJedis();
//        simulateDataGenerator.PushToRAM();
    }
}
