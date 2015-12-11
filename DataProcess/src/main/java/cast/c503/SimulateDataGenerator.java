package cast.c503;

import cast.c503.DataProcessor.ImageTager;
import com.alibaba.simpleimage.io.*;
import com.alibaba.simpleimage.render.FixDrawTextItem;
import com.sun.corba.se.impl.oa.toa.TOA;
import com.sun.corba.se.spi.ior.Writeable;
import com.sun.media.jai.opimage.FileStoreRIF;
import org.apache.commons.io.FileUtils;

import java.awt.*;
import java.io.*;
import java.io.ByteArrayOutputStream;
import java.lang.annotation.Target;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;
import java.util.zip.*;
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
    int TotalCount = DEFAULT_TARGETCOUNT;
    String PackageType;
    String TargetPath;

    static int DEFAULT_TARGETCOUNT = 10;
    static String DEFAULT_PackageType = "zip";
    static String DEFAULT_TARGETPATH = "/data/simdata";
    static String BasePath = "/run/media/yanghl/77b85fda-c795-4518-b551-cd0bde3131e5/youngcle/DataProcessStormNew/DataProcess/src/main/resources";
    static String BasePathhome="/home/youngcle/DataProcessStormNew/DataProcess/src/main/resources";
    static String Templatefile_CCD = BasePath+File.separator+"CCD.jpg";
    static String Templatefile_HSICCD = BasePath+File.separator+"HSICCD.jpg";
    static String Templatefile_HSIIRS = BasePath+File.separator+"HSIIRS.jpg";
    static String Templatefile_IRS = BasePath+File.separator+"IRS.jpg";
    boolean ON_DEBUG = false;



    public SimulateDataGenerator(){

    }

    public SimulateDataGenerator(int targetCount,String packageType,String targetPath){
        TotalCount = targetCount;
        PackageType = packageType;
        TargetPath = targetPath;


    }

    public void DoGenerateData(){

        int roundtorun = TotalCount/DEFAULT_TARGETCOUNT;
        for(int k=0;k<roundtorun;k++) {

            for (int i = k * DEFAULT_TARGETCOUNT; i < ((k+1)* DEFAULT_TARGETCOUNT); i++) {
                TagFile(Templatefile_CCD, "CCD", i, "red");
                String zipname = TargetPath + File.separator + "CCD" + i + ".jpg";
                try {
                    gzipFile(zipname);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }


            for (int i = k * DEFAULT_TARGETCOUNT; i < ((k+1)* DEFAULT_TARGETCOUNT); i++) {
                TagFile(Templatefile_HSIIRS, "HSIIRS", i, "green");
                String zipname = TargetPath + File.separator + "HSIIRS" + i + ".jpg";
                try {
                    gzipFile(zipname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (int i = k * DEFAULT_TARGETCOUNT; i < ((k+1)* DEFAULT_TARGETCOUNT); i++) {
                TagFile(Templatefile_IRS, "IRS", i, "blue");
                String zipname = TargetPath + File.separator + "IRS" + i + ".jpg";
                try {
                    gzipFile(zipname);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            for (int i = k * DEFAULT_TARGETCOUNT; i < ((k+1)* DEFAULT_TARGETCOUNT); i++) {
                java.util.List<String> fileslisttozip = new LinkedList<String>();
                String ccdname = TargetPath + File.separator + "CCD" + i + ".jpg.gz";
                String IRSname = TargetPath + File.separator + "IRS" + i + ".jpg.gz";
                String HSIIRSname = TargetPath + File.separator + "HSIIRS" + i + ".jpg.gz";
                String ZIPPackName = DEFAULT_TARGETPATH + File.separator + "PACK" + i;
                fileslisttozip.add(ccdname);
                fileslisttozip.add(IRSname);
                fileslisttozip.add(HSIIRSname);
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

        File outputfile= new File(TargetPath+File.separator+tagText+nmCode+".jpg");

        ImageTager it = new ImageTager();
        it.setInputImageFile(inputfile);
        it.DoImageTag(outputfile,tagText,colorstr,numCode);


    }

    void WriteFile(){

    }


    void PushToJedis(){
        Jedis jedis = new Jedis("localhost");
        File inputfile= new File(DEFAULT_TARGETPATH+File.separator+"PACK0"+".zip");
        ByteBuffer bb = ByteBuffer.allocate(1024*1024);
        Pipeline pipeline = jedis.pipelined();

        try {
            FileChannel inputChannel= new FileInputStream(inputfile).getChannel();

            while(inputChannel.write(bb)!=-1){
                bb.flip();
                bb.clear();
                pipeline.lpush("test", String.valueOf(bb.array()));
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        client.close();
    }


    public static void main(String[] args) throws Exception {
        SimulateDataGenerator simulateDataGenerator = new SimulateDataGenerator(10000,"zip","/dev/shm");
        simulateDataGenerator.DoGenerateData();
    }
}
