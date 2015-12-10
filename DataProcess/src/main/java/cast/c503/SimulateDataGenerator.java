package cast.c503;

import cast.c503.DataProcessor.ImageTager;
import com.alibaba.simpleimage.render.FixDrawTextItem;

import java.awt.*;
import java.io.*;
import java.lang.annotation.Target;
import java.util.zip.*;

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
    String PackageType;
    String TargetPath;

    static int DEFAULT_TARGETCOUNT = 10;
    static String DEFAULT_PackageType = "zip";
    static String DEFAULT_TARGETPATH = "/tmp/simdata";
    static String BasePath = "/run/media/yanghl/77b85fda-c795-4518-b551-cd0bde3131e5/youngcle/DataProcessStormNew/DataProcess/src/main/resources";
    static String BasePathhome="/home/youngcle/DataProcessStormNew/DataProcess/src/main/resources";
    static String Templatefile_CCD = BasePath+File.separator+"CCD.jpg";
    static String Templatefile_HSICCD = BasePath+File.separator+"HSICCD.jpg";
    static String Templatefile_HSIIRS = BasePath+File.separator+"HSIIRS.jpg";
    static String Templatefile_IRS = BasePath+File.separator+"IRS.jpg";




    public SimulateDataGenerator(){

    }

    public SimulateDataGenerator(int targetCount,String packageType,String targetPath){
        TargetCount = targetCount;
        PackageType = packageType;
        TargetPath = targetPath;


    }

    public void DoGenerateData(){
        for(int i=0;i<TargetCount;i++){
            TagFile(Templatefile_CCD,"CCD",i,"red");
            try {
                String zipname = TargetPath+File.separator+"CCD"+i+".bmp";
                ZipFile(zipname);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(int i=0;i<TargetCount;i++){
            TagFile(Templatefile_HSIIRS,"HSIIRS",i,"green");
            String zipname = TargetPath+File.separator+"HSIIRS"+i+".bmp";
            try {
                ZipFile(zipname);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for(int i=0;i<TargetCount;i++){
            TagFile(Templatefile_IRS,"IRS",i,"blue");
            String zipname = TargetPath+File.separator+"IRS"+i+".bmp";
            try {
                ZipFile(zipname);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    File ReadTemlateFile(){

        return null;

    }

    void ZipFile(String name) throws IOException {

        File filetozip = new File(name);

        File outputfile= new File(name+".zip");



        ZipEntry zipentry = new ZipEntry(name);
        InputStream inputstream = new FileInputStream(filetozip);
        ZipOutputStream zipOutputStream = null;
        try {
             zipOutputStream = new ZipOutputStream(new FileOutputStream(outputfile));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (zipOutputStream != null) {
            zipOutputStream.putNextEntry(zipentry);
            int b;
            while ((b = inputstream.read()) != -1){
                zipOutputStream.write(b);
            }
            zipOutputStream.flush();
            zipOutputStream.closeEntry();
            zipOutputStream.close();
        }




    }

    void TagFile(String tempStr,String tagText,int numCode,String colorstr){
        int nmCode = numCode;
//IRS
        File inputfile = new File(tempStr);

        File outputfile= new File(TargetPath+File.separator+tagText+nmCode+".bmp");

        ImageTager it = new ImageTager();
        it.setInputImageFile(inputfile);
        it.DoImageTag(outputfile,tagText,colorstr,numCode);

    }

    void WriteFile(){

    }

    public static void main(String[] args) throws Exception {
        SimulateDataGenerator simulateDataGenerator = new SimulateDataGenerator(5,"zip","/dev/shm");
        simulateDataGenerator.DoGenerateData();
    }
}
