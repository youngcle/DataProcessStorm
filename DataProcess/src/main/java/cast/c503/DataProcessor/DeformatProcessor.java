package cast.c503.DataProcessor;

import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DeformatDataPackage;
import cast.c503.DataPackage.DemodRawDataPackage;
import com.alibaba.simpleimage.ImageFormat;
import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.ImageWrapper;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.font.FontLoader;
import com.alibaba.simpleimage.font.FontManager;
import com.alibaba.simpleimage.io.*;
import com.alibaba.simpleimage.io.ByteArrayInputStream;
import com.alibaba.simpleimage.io.ByteArrayOutputStream;
import com.alibaba.simpleimage.render.*;
import org.apache.commons.lang.StringUtils;

import javax.imageio.*;
import javax.imageio.stream.MemoryCacheImageInputStream;
import javax.media.jai.PlanarImage;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.media.jai.remote.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by youngcle on 15-11-23.
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




public class DeformatProcessor extends DataProcessorBase {
    static File path              = new File("/dev/shm/");
    static File        rpath             = new File("/dev/shm");

    static final Color FONT_COLOR        = Color.ORANGE;//new Color(255, 255, 255, 115);
    static final Color FONT_SHADOW_COLOR = new Color(170, 170, 170, 77);
    static final Font  FONT              = new Font("kaiti", Font.PLAIN, 10);
    static final String PROCESS_STRING   = "解格式处理";
    static final FixDrawTextItem.Position STR_POSITION = FixDrawTextItem.Position.TOP_LEFT;


    private String satMode;
    private byte[] dataPiece;
    private File inputfile;




    public DeformatProcessor(ArrayList<DataPackageBase> dataPackages){
        super();
        InputDataPackages = dataPackages;
        OutputDataPackages = new ArrayList<DataPackageBase>();

    }

    @Override
    public boolean  InitializeDataProcess(){
        System.out.println("Initialize DataProcessor Deformat Modual");
        return true;
    }

    @Override
    public boolean DoDataProcess(){
//        for(int j=0;j<InputDataPackages.size();j++){
//            DataPackageBase dataPackageBaseInput = InputDataPackages.get(j);
//            ArrayList<char[]> payloadslist = dataPackageBaseInput.getPayloads();
//            for(int i=0;i<payloadslist.size();i++){
//                String data = new StringBuffer().append(payloadslist.get(i)).toString();
//                String[] dataset = StringUtils.split(data,":");
//                DataPackageBase dataPackageBaseOutput =new DeformatDataPackage();
//                dataPackageBaseOutput.getPayloads().add(dataset[0].toCharArray());
//                OutputDataPackages.add(dataPackageBaseOutput);
//            }
//        }
//        for (int k=0;k<OutputDataPackages.size();k++){
//            System.out.println("Doing DataProcessor Modual");
//            System.out.println(OutputDataPackages.get(k).getPayloads().toString());
//        }
        return true;
    }
    @Override
    public boolean CleanDataProcess(){
        System.out.println("cleaning DataProcessor Modual");
        return true;
    }

    protected DeformatProcessor(String SatModel, String WorkingMode) {
        satMode = SatModel;
        workingMode = WorkingMode;
    }

    protected DeformatProcessor(String SatModel, String WorkingMode, byte[] InputFilebytes) {
        satMode = SatModel;
        workingMode = WorkingMode;
        dataPiece = InputFilebytes;
    }


    protected byte[] DoProcess(byte[] Filebytes) throws IOException, SimpleImageException {
        InputStream filebytesstream= new ByteArrayInputStream(Filebytes);
        DrawTextParameter param = new DrawTextParameter();
        final Font str_font = new FontLoader("kaiti","/ukai.ttc").getFont();
        Random _rand = new Random();

        for(int i=0;i<100;i++){
            float size_width = _rand.nextFloat();
            float pos_height = _rand.nextFloat();
            float pos_width = _rand.nextFloat();
            param.addTextInfo(new ReleatePositionDrawTextItem(PROCESS_STRING+satMode,Color.blue,FONT_SHADOW_COLOR,str_font,10, size_width,pos_height,pos_width));
        }


//        param.addTextInfo(new FixDrawTextItem(PROCESS_STRING+satMode,Color.MAGENTA, FONT_SHADOW_COLOR,
//                str_font , 20, FixDrawTextItem.Position.TOP_LEFT, 0.5f));
//        param.addTextInfo(new FixDrawTextItem(PROCESS_STRING+workingMode,Color.red, FONT_SHADOW_COLOR,
//                str_font , 20, FixDrawTextItem.Position.BOTTOM_LEFT, 0.5f));
        //文件输出




        ImageRender dr = new DrawTextRender(filebytesstream, param);

        BufferedImage bi = dr.render().getAsBufferedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        WriteRender wrr = new WriteRender(dr,baos,ImageFormat.BMP);
        wrr.render();



//        javax.imageio.ImageIO.write(bi,"bmp",baos);
        byte[] outbytes = baos.toByteArray().getBytes();



        try {
            ImageRender wr = new WriteRender(dr, rpath.getCanonicalPath() + File.separator
                    + "normal_new.bmp", ImageFormat.BMP);
            wr.render();
            wr.dispose();

        } catch (SimpleImageException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return outbytes;

    }

    protected byte[] DoProcessJP2(byte[] Filebytes) throws IOException, SimpleImageException {
        InputStream filebytesstream= new ByteArrayInputStream(Filebytes);
        DrawTextParameter param = new DrawTextParameter();
        final Font str_font = new FontLoader("kaiti","/ukai.ttc").getFont();
        Random _rand = new Random();

        for(int i=0;i<100;i++){
            float size_width = _rand.nextFloat();
            float pos_height = _rand.nextFloat();
            float pos_width = _rand.nextFloat();
            param.addTextInfo(new ReleatePositionDrawTextItem(PROCESS_STRING+satMode,Color.blue,FONT_SHADOW_COLOR,str_font,10, size_width,pos_height,pos_width));
        }


//        param.addTextInfo(new FixDrawTextItem(PROCESS_STRING+satMode,Color.MAGENTA, FONT_SHADOW_COLOR,
//                str_font , 20, FixDrawTextItem.Position.TOP_LEFT, 0.5f));
//        param.addTextInfo(new FixDrawTextItem(PROCESS_STRING+workingMode,Color.red, FONT_SHADOW_COLOR,
//                str_font , 20, FixDrawTextItem.Position.BOTTOM_LEFT, 0.5f));
        //文件输出
        ImageReader reader = ImageIO.getImageReadersBySuffix("jp2").next();
        System.out.println(Arrays.asList(ImageIO.getReaderMIMETypes()));
        System.out.println(Arrays.asList(ImageIO.getWriterFormatNames()));
        System.out.println(Arrays.asList(ImageIO.getReaderFormatNames()));
        reader.setInput(new MemoryCacheImageInputStream(filebytesstream));

        ImageWrapper iw = new ImageWrapper(reader.read(0));
        ImageRender dr = new DrawTextRender(iw, param);

        BufferedImage bi = dr.render().getAsBufferedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        javax.imageio.ImageIO.write(bi,"bmp",baos);
        byte[] outbytes = baos.toByteArray().getBytes();
//        ImageWriter writer = ImageIO.getImageWritersBySuffix("jp2").next();
//        J2KImageWriteParam writeParams = (J2KImageWriteParam) writer.getDefaultWriteParam();
//        writeParams.setLossless(false);
//        writeParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//        writeParams.setCompressionType("JPEG2000");
//        writeParams.setFilter(J2KImageWriteParam.FILTER_97);



//        writeParams.setCompressionQuality(0.0f);
//        writeParams.setEncodingRate(0.5f);
//        ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(baos);
//        FileImageOutputStream fios = new FileImageOutputStream(new File(getFileStr()+".jp2"));
//        writer.setOutput(fios);
//        writer.write(null, new IIOImage(bi, null, null), writeParams);

//        writer.dispose();
//        fios.close();
//        writer.dispose();
//        baos.close();






//
//        try {
//            ImageRender wr = new WriteRender(dr, rpath.getCanonicalPath() + File.separator
//                    + "normal_new.bmp", ImageFormat.BMP);
//            wr.render();
//            wr.dispose();
//        } catch (SimpleImageException e) {
//            e.printStackTrace();
//            return null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
        return outbytes;

    }



    protected boolean DoProcess() throws IOException, SimpleImageException {
        InputStream file = new FileInputStream(getFileStr());
        DrawTextParameter param = new DrawTextParameter();
        final Font str_font = new FontLoader("kaiti","/ukai.ttc").getFont();

        param.addTextInfo(new ReleatePositionDrawTextItem(PROCESS_STRING+satMode,Color.blue,FONT_SHADOW_COLOR,str_font,20, 0.5f,0.01f,0.1f));

        param.addTextInfo(new FixDrawTextItem(PROCESS_STRING+satMode,Color.MAGENTA, FONT_SHADOW_COLOR,
                str_font , 20, FixDrawTextItem.Position.TOP_LEFT, 0.5f));
        param.addTextInfo(new FixDrawTextItem(PROCESS_STRING+workingMode,Color.red, FONT_SHADOW_COLOR,
                str_font , 20, FixDrawTextItem.Position.BOTTOM_LEFT, 0.5f));
        //文件输出

        ImageRender dr = new DrawTextRender(file, param);

        try {
            write(dr);
            file.close();
        } catch (SimpleImageException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;

    }
    protected void write(ImageRender dr) throws SimpleImageException, IOException {
        ImageRender wr = new WriteRender(dr, rpath.getCanonicalPath() + File.separator
                + "normal_new.bmp");
        wr.render();
        wr.dispose();
    }

    public static String getFileStr() throws IOException {
        return path.getCanonicalPath() + File.separator + "rocks.jp2";
    }

    public static byte[] getByte(String filename) throws Exception {
        byte[] bytes = null;
        File file = new File(filename);
        if (file != null) {
            InputStream is = new FileInputStream(file);
            int length = (int) file.length();
            if (length > Integer.MAX_VALUE) // 当文件的长度超过了int的最大值
            {
                System.out.println("this file is max ");
                return null;
            }
            bytes = new byte[length];
            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }
            // 如果得到的字节长度和file实际的长度不一致就可能出错了
            if (offset < bytes.length) {
                System.out.println("file length is error");
                return null;
            }
            is.close();
        }
        return bytes;
    }

    public static void main(String[] args) {
        // write your code here

        byte[] filebytes = null;
        try {
//            com.alibaba.simpleimage.io.ByteArrayInputStream bis = new com.alibaba.simpleimage.io.ByteArrayInputStream();
            filebytes = DeformatProcessor.getByte(DeformatProcessor.getFileStr());


        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        DeformatProcessor dp = new DeformatProcessor("XX-11","normal");
        byte[] outbytes = null;
        int readinbytes = 0,writeoutbytes = 0;
        long time = System.currentTimeMillis();
        for(int i=0;i<2;i++) {

            try {
                outbytes = dp.DoProcessJP2(filebytes);
                readinbytes+=filebytes.length;
                writeoutbytes+=outbytes.length;
//            ByteArrayInputStream baisout = new ByteArrayInputStream(outbytes);
//                File file = new File(DeformatProcessor.getFileStr() + ".bmp");
//                FileOutputStream fileOutputStream = new FileOutputStream(file);
//                fileOutputStream.write(outbytes);
//                fileOutputStream.close();


//            dp.DoProcess();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        time = System.currentTimeMillis() - time;
        System.out.println("time used:"+time);
        System.out.println("read in bytes:"+readinbytes+" reading speed(KB/s):"+readinbytes/time);
        System.out.println("write out bytes:"+writeoutbytes+" writeing speed(KB/s)"+writeoutbytes/time);

    }
    public String getWorkingMode() {
        return workingMode;
    }

    public void setWorkingMode(String workingMode) {
        this.workingMode = workingMode;
    }

    private String workingMode;
    public String getSatMode() {
        return satMode;
    }

    public void setSatMode(String satMode) {
        this.satMode = satMode;
    }

}
