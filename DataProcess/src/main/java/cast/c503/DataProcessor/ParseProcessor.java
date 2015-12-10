package cast.c503.DataProcessor;

import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DeformatDataPackage;
import cast.c503.TaskAgents.TaskParamBase;
import com.alibaba.simpleimage.io.ByteArrayInputStream;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

/**
 * Created by youngcle on 15-12-7.
 */

/*
* 输入解密数据
* 输出
*
可见光压缩包数据
可见光高光谱压缩包数据
红外高光谱压缩包数据
红外数据
SDRTU数据
GPS报告


质量信息：数据质量分析报告
*
*
* */
//输入1个，输出多个、
//Processor输入数据可以是多个DataPackage的list。
//输出：多个DataPackage的list



public class ParseProcessor extends DataProcessorBase {
    public ParseProcessor(){
        super();

    }

    @Override
    public boolean  InitializeDataProcess(){
        super.InitializeDataProcess();
        return true;
    }

    @Override
    public boolean DoDataProcess(){
        super.DoDataProcess();
        for(int j=0;j<InputDataPackages.size();j++){
            DataPackageBase dataPackageBaseInput = InputDataPackages.get(j);
//            ArrayList<byte[]> payloadslist = dataPackageBaseInput.getPayloads();
            Map<Integer,byte[]> payloadsMap = dataPackageBaseInput.getPayloadsMap();

            for(Iterator iterator = payloadsMap.keySet().iterator(); iterator.hasNext(); ) {

                Integer NumCode = (Integer)iterator.next();

                byte[] payloadbytes = payloadsMap.get(NumCode);

                ImageTager it = new ImageTager();

                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));



                int numcode = NumCode;


                //采用文件输出，设置文件路径
                workingDir = "/dev/shm";
                File outputfile_CCD = new File(workingDir+"/unpack_CCD_"+numcode+".jpg");
                File outputfile_IRS = new File(workingDir+"/unpack_IRS_"+numcode+".jpg");
                File outputfile_HSIIRS = new File(workingDir+"/unpack_HSIIRS_"+numcode+".jpg");
                File outputfile_HSICCD = new File(workingDir+"/unpack_HSICCD_"+numcode+".jpg");

                //内存输出，初始化内存stream
                OutputStream outputStream_CCD = new ByteArrayBuffer(1024*1024);
                OutputStream outputStream_IRS = new ByteArrayBuffer(1024*1024);
                OutputStream outputStream_HSIIRS = new ByteArrayBuffer(1024*1024);
                OutputStream outputStream_HSICCD = new ByteArrayBuffer(1024*1024);


                //进行图像处理，在图片上打印标签
                it.DoImageTag(outputStream_CCD,"解格式文件：CCD1","red",j+1);
                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
                it.DoImageTag(outputStream_IRS,"解格式文件：IRS","blue",j+1);
                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
                it.DoImageTag(outputStream_HSIIRS,"解格式文件：HSIIRS","orange",j+1);
                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
                it.DoImageTag(outputStream_HSICCD,"解格式文件：HSICCD","green",j+1);


//                it.DoImageTag(outputStream_CCD,"解格式文件：CCD1","red",j+1);
//                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
//                it.DoImageTag(outputfile_IRS,"解格式文件：IRS","blue",j+1);
//                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
//                it.DoImageTag(outputfile_HSIIRS,"解格式文件：HSIIRS","orange",j+1);
//                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
//                it.DoImageTag(outputfile_HSICCD,"解格式文件：HSICCD","green",j+1);


                DataPackageBase dataPackageBaseOutput_CCD =new DeformatDataPackage();
                DataPackageBase dataPackageBaseOutput_IRS =new DeformatDataPackage();
                DataPackageBase dataPackageBaseOutput_HSIIRS =new DeformatDataPackage();
                DataPackageBase dataPackageBaseOutput_HSICCD =new DeformatDataPackage();

                dataPackageBaseOutput_CCD.addPayload(((ByteArrayBuffer)outputStream_CCD).getRawData());
                dataPackageBaseOutput_CCD.addPayload(((ByteArrayBuffer)outputStream_CCD).getRawData());
                dataPackageBaseOutput_IRS.addPayload(((ByteArrayBuffer)outputStream_CCD).getRawData());
                dataPackageBaseOutput_HSIIRS.addPayload(((ByteArrayBuffer)outputStream_CCD).getRawData());
                dataPackageBaseOutput_HSICCD.addPayload(((ByteArrayBuffer)outputStream_CCD).getRawData());


//                dataPackageBaseOutput_CCD.addPayload(((ByteArrayBuffer)outputStream_CCD).getRawData());
//                dataPackageBaseOutput_CCD.addPayloadfromFile(workingDir+"/unpack_CCD_"+numcode+".jpg");
//                dataPackageBaseOutput_IRS.addPayloadfromFile(workingDir+"/unpack_IRS_"+numcode+".jpg");
//                dataPackageBaseOutput_HSIIRS.addPayloadfromFile(workingDir+"/unpack_HSIIRS_"+numcode+".jpg");
//                dataPackageBaseOutput_HSICCD.addPayloadfromFile(workingDir+"/unpack_HSICCD_"+numcode+".jpg");
//

                OutputDataPackages.add(dataPackageBaseOutput_CCD);
                OutputDataPackages.add(dataPackageBaseOutput_IRS);
                OutputDataPackages.add(dataPackageBaseOutput_HSIIRS);
                OutputDataPackages.add(dataPackageBaseOutput_HSICCD);


                System.out.println("Doing DataProcessor Modual");
                System.out.println("Package No:"+dataPackageBaseInput.getPackageNum());
                System.out.println("Payload No:"+NumCode);
            }
        }
        return true;
    }

    @Override
    public boolean CleanDataProcess(){
        super.CleanDataProcess();
        return true;
    }



}
