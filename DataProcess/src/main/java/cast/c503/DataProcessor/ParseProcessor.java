package cast.c503.DataProcessor;

import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DeformatDataPackage;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.*;
import java.util.zip.*;

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
    ByteBuffer CCDoutputByteBuffer ;
    ByteBuffer HSICCDoutputByteBuffer ;
    ByteBuffer IRSoutputByteBuffer;
    ByteBuffer HSIIRSoutputByteBuffer;
    ByteBuffer ungzBuffer;
    Map<String,ByteBuffer> unpackPayloads = new HashMap<String,ByteBuffer>();
    int[] unpackPayloadsNumcodeArray = new int[4];
    int currentNumCode = 0;

    public ParseProcessor(){
        super();
         CCDoutputByteBuffer = ByteBuffer.allocate(1024*1024);
         HSICCDoutputByteBuffer = ByteBuffer.allocate(1024*1024);
         IRSoutputByteBuffer = ByteBuffer.allocate(1024*1024);
         HSIIRSoutputByteBuffer = ByteBuffer.allocate(1024*1024);
         ungzBuffer = ByteBuffer.allocate(1024*1024);

        unpackPayloads.put("CCD",CCDoutputByteBuffer);
        unpackPayloads.put("HSICCD",HSICCDoutputByteBuffer);
        unpackPayloads.put("IRS",IRSoutputByteBuffer);
        unpackPayloads.put("HSIIRS",HSIIRSoutputByteBuffer);

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
            Map<Integer,ByteBuffer> payloadsbufferMap = dataPackageBaseInput.getPayloadsBufferMap();

            for(Iterator iterator = payloadsbufferMap.keySet().iterator(); iterator.hasNext(); ) {

                currentNumCode = (Integer)iterator.next();

                CCDoutputByteBuffer.clear();
                HSICCDoutputByteBuffer.clear();
                IRSoutputByteBuffer.clear();
                HSIIRSoutputByteBuffer.clear();

                //根据NumCode获取哈希表中当前等待处理的数据缓冲区
                ByteBuffer payloadBuffer= payloadsbufferMap.get(currentNumCode);

                //进行解zip包处理，解出来的gz包放在unpackPayloads中，将帧ID解出后放入数组中
                unzipThePack(payloadBuffer,unpackPayloads, unpackPayloadsNumcodeArray);

                //构建输出DataPackage,每包输出四个载荷包，因此构建四个。
                DataPackageBase dataPackageBaseOutput_CCD =new DataPackageBase("CCD");
                DataPackageBase dataPackageBaseOutput_IRS =new DataPackageBase("IRS");
                DataPackageBase dataPackageBaseOutput_HSIIRS =new DataPackageBase("HSIIRS");
                DataPackageBase dataPackageBaseOutput_HSICCD =new DataPackageBase("HSICCD");


                //对unpackPayLoads中每个gz包进行 解压缩处理，解压缩后结果放在ungzBuffer中。
                //根据ungzbuffer构建dataPackage
                ByteBuffer payloadbb = null;
                try {
                    ungzBuffer.clear();
                    ungzipTheData(unpackPayloads.get("CCD"),ungzBuffer,"CCD");
                    ungzBuffer.flip();
                    payloadbb = ByteBuffer.allocate(ungzBuffer.limit());
                    payloadbb.put(ungzBuffer);
                    dataPackageBaseOutput_CCD.addPayloadBuffer(unpackPayloadsNumcodeArray[0],payloadbb);

                    ungzBuffer.clear();
                    ungzipTheData(unpackPayloads.get("HSICCD"),ungzBuffer,"HSICCD");
                    ungzBuffer.flip();
                    payloadbb = ByteBuffer.allocate(ungzBuffer.limit());
                    payloadbb.put(ungzBuffer);
                    dataPackageBaseOutput_HSICCD.addPayloadBuffer(unpackPayloadsNumcodeArray[1],payloadbb);


                    ungzBuffer.clear();
                    ungzipTheData(unpackPayloads.get("IRS"),ungzBuffer,"IRS");
                    ungzBuffer.flip();
                    payloadbb = ByteBuffer.allocate(ungzBuffer.limit());
                    payloadbb.put(ungzBuffer);
                    dataPackageBaseOutput_IRS.addPayloadBuffer(unpackPayloadsNumcodeArray[2],payloadbb);

                    ungzBuffer.clear();
                    ungzipTheData(unpackPayloads.get("HSIIRS"),ungzBuffer,"HSIIRS");
                    ungzBuffer.flip();
                    payloadbb = ByteBuffer.allocate(ungzBuffer.limit());
                    payloadbb.put(ungzBuffer);
                    dataPackageBaseOutput_HSIIRS.addPayloadBuffer(unpackPayloadsNumcodeArray[3],payloadbb);
                } catch (IOException e) {
                    e.printStackTrace();
                }





//                ImageTager it = new ImageTager();
//
//                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
//
//
//
//                int numcode = NumCode;
//
//
//                //采用文件输出，设置文件路径
//                workingDir = "/dev/shm";
//                File outputfile_CCD = new File(workingDir+"/unpack_CCD_"+numcode+".jpg");
//                File outputfile_IRS = new File(workingDir+"/unpack_IRS_"+numcode+".jpg");
//                File outputfile_HSIIRS = new File(workingDir+"/unpack_HSIIRS_"+numcode+".jpg");
//                File outputfile_HSICCD = new File(workingDir+"/unpack_HSICCD_"+numcode+".jpg");
//
//                //内存输出，初始化内存stream
//                OutputStream outputStream_CCD = new ByteArrayBuffer(1024*1024);
//                OutputStream outputStream_IRS = new ByteArrayBuffer(1024*1024);
//                OutputStream outputStream_HSIIRS = new ByteArrayBuffer(1024*1024);
//                OutputStream outputStream_HSICCD = new ByteArrayBuffer(1024*1024);
//
//
//                //进行图像处理，在图片上打印标签
//                it.DoImageTag(outputStream_CCD,"解格式文件：CCD1","red",j+1);
//                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
//                it.DoImageTag(outputStream_IRS,"解格式文件：IRS","blue",j+1);
//                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
//                it.DoImageTag(outputStream_HSIIRS,"解格式文件：HSIIRS","orange",j+1);
//                it.setInputImageStream(new ByteArrayInputStream(payloadbytes));
//                it.DoImageTag(outputStream_HSICCD,"解格式文件：HSICCD","green",j+1);
//

                OutputDataPackages.add(dataPackageBaseOutput_CCD);
                OutputDataPackages.add(dataPackageBaseOutput_IRS);
                OutputDataPackages.add(dataPackageBaseOutput_HSIIRS);
                OutputDataPackages.add(dataPackageBaseOutput_HSICCD);


//                System.out.println("Doing DataProcessor Modual");
//                System.out.println("Package No:"+dataPackageBaseInput.getPackageNumCode());
//                System.out.println("Payload No:"+currentNumCode);
            }
        }
        return true;
    }

    public void ungzipTheData(ByteBuffer inputGZipByteBuffer, ByteBuffer outputunGZipBytebuffer, String dataname) throws IOException {

        InputStream is = new java.io.ByteArrayInputStream(inputGZipByteBuffer.array());
        InputStream gzis = new GZIPInputStream(is);
        ReadableByteChannel rbc = Channels.newChannel(gzis);
        outputunGZipBytebuffer.clear();
        rbc.read(outputunGZipBytebuffer);

        if(debugMode) {

            WritableByteChannel wbc = null;
            try {
                wbc = new FileOutputStream(new File("/dev/shm" + File.separator + dataname + currentNumCode + ".jp2")).getChannel();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            outputunGZipBytebuffer.flip();
            wbc.write(outputunGZipBytebuffer);
        }

    }

    public void unzipThePack(ByteBuffer InputZIPByteBuffer, Map<String, ByteBuffer> OutputUNZIPByteBuffer, int[] unpackPayloadsNumcode){
        java.io.ByteArrayInputStream bi = new java.io.ByteArrayInputStream(InputZIPByteBuffer.array());
        ZipInputStream zis = new ZipInputStream(bi);
        ReadableByteChannel rbc = Channels.newChannel(zis);
        ZipEntry zipentry = null;


        try {
            int i=0;
            while ((zipentry = zis.getNextEntry()) != null){
                String entryname = zipentry.getName();
                ByteBuffer bb= OutputUNZIPByteBuffer.get(entryname.split("_")[0]);
                rbc.read(bb);
                unpackPayloadsNumcode[i] =Integer.parseInt(entryname.split("[.]")[0].split("_")[1]);
                i++;
                //将数据输出到文件
                if(debugMode) {
                    bb.flip();
                    WritableByteChannel wbc = null;
                    try {
                        wbc = new FileOutputStream(new File("/dev/shm" + File.separator + entryname)).getChannel();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    wbc.write(bb);
                    wbc.close();
                }
                }
            rbc.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }


    void ZipFiles(String zipname,java.util.List<String> filenamelist) throws IOException {




    }

    void gzipFile(String filename) throws IOException {


    }

    @Override
    public boolean CleanDataProcess(){
        super.CleanDataProcess();
        return true;
    }



}
