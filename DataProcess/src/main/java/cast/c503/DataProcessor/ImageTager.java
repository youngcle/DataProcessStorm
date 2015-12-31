package cast.c503.DataProcessor;

import com.alibaba.simpleimage.ImageFormat;
import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.ImageWrapper;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.font.FontLoader;
import com.alibaba.simpleimage.io.ByteArrayInputStream;
import com.alibaba.simpleimage.io.ByteArrayOutputStream;
import com.alibaba.simpleimage.render.*;
import com.github.jaiimageio.impl.plugins.raw.RawImageReader;
import com.github.jaiimageio.impl.plugins.raw.RawImageReaderSpi;
import com.github.jaiimageio.stream.RawImageInputStream;
import com.sun.deploy.util.StringUtils;
import com.sun.javafx.iio.ImageStorage;
import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;

import javax.imageio.*;
import javax.imageio.stream.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.io.*;
import java.nio.*;
import java.nio.channels.Channels;
import java.util.*;
import java.util.List;


/**
 * Created by youngcle on 15-12-7.
 */
public class ImageTager {
    Color font_color        = Color.ORANGE;//new Color(255, 255, 255, 115);
    Color font_shadow_color = new Color(170, 170, 170, 77);
//    Font  str_font              = new Font("kaiti", Font.PLAIN, 10);
    String PROCESS_STRING   = "标签数据";
    final FixDrawTextItem.Position STR_POSITION = FixDrawTextItem.Position.TOP_LEFT;
    File InputImageFile;
    InputStream InputImageStream;
    private Map<String,Color> StringColorMap = new HashMap<String, Color>();
    static final Font str_font = new FontLoader("kaiti","/ukai.ttc").getFont();


    public File getInputImageFile() {
        return InputImageFile;
    }

    public void setInputImageFile(File inputImageFile) {
        InputImageFile = inputImageFile;
        try {
            InputImageStream = new BufferedInputStream(new FileInputStream(InputImageFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }



    public InputStream getInputImageStream() {
        return InputImageStream;
    }

    public void setInputImageStream(InputStream inputImageStream) {
        InputImageStream = new BufferedInputStream(inputImageStream);
    }

    public ImageTager(){

        StringColorMap.put("red", Color.RED);

        StringColorMap.put("green", Color.GREEN);
        StringColorMap.put("orange", Color.orange);
        StringColorMap.put("blue", Color.blue);
        StringColorMap.put("black", Color.black);
        javax.imageio.ImageIO.setUseCache(false);
    }

    public void DoImageTag(File outputfile, String tagtext,String colorstr,int numCode){
        try {
            DoImageTag(new FileOutputStream(outputfile),tagtext,colorstr,numCode);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void DoImageTag(OutputStream outputStream, String tagtext,String colorstr,int numCode){
        PROCESS_STRING = tagtext;
        font_color = StringColorMap.get(colorstr);

        DrawTextParameter param = new DrawTextParameter();
        Random _rand = new Random();
        float size_width = 0.2f;
        float pos_height = 0.9f;
        float pos_width = 0.7f;
        //_rand.nextFloat();
        BufferedImage bi = null ;


        ImageRender dr = null;
        param.addTextInfo(new ReleatePositionDrawTextItem("编号："+numCode,font_color,font_shadow_color,str_font,10, 0.2f,0.01f,0.1f));
        param.addTextInfo(new ReleatePositionDrawTextItem("类型："+tagtext,font_color,font_shadow_color,str_font,10, 0.2f,0.7f,0.1f));
        try {
            bi =ReadIMGFORMATStream(InputImageStream,"jp2");
            dr = new DrawTextRender(new ImageWrapper(bi), param);
        } catch (Exception e) {
            dr = new DrawTextRender(InputImageStream,param);
        }


        try {
            bi = dr.render().getAsBufferedImage();
        } catch (SimpleImageException e) {
            e.printStackTrace();
        }
        MakeIMGFORMATStream(bi,outputStream,"jp2");

//        ImageRender wr = null;
//        try {
//            wr = new WriteRender(dr, outputStream, ImageFormat.JPEG);
//            wr.render();
//            wr.dispose();
//        } catch (SimpleImageException e) {
//            e.printStackTrace();
//        }

    }

    public void DoRawImageTagAndSubsample(OutputStream outputStream, String tagtext,String colorstr,String codeStr,int width,int height){
        PROCESS_STRING = tagtext;
        font_color = StringColorMap.get(colorstr);

        DrawTextParameter param = new DrawTextParameter();
        Random _rand = new Random();
        float size_width = 0.2f;
        float pos_height = 0.5f;
        float pos_width = 0.5f;
        BufferedImage bi = null ;



        ImageRender dr = null;
        param.addTextInfo(new ReleatePositionDrawTextItem("编号："+codeStr,font_color,font_shadow_color,str_font,60, 0.6f,pos_height,pos_width));
        param.addTextInfo(new ReleatePositionDrawTextItem("类型："+tagtext,font_color,font_shadow_color,str_font,60, 0.6f,pos_height+0.1f,pos_width));
        ImageTypeSpecifier its = ImageTypeSpecifier.createGrayscale(8, DataBuffer.TYPE_BYTE,false);
        try {
            bi =ReadRawIMGStreamToByteGray(InputImageStream,its,width,height);
        } catch (IOException e) {
            e.printStackTrace();
        }
        dr = new DrawTextRender(new ImageWrapper(bi), param);


        try {
            bi = dr.render().getAsBufferedImage();
        } catch (SimpleImageException e) {
            e.printStackTrace();
        }
        MakeIMGFORMATStream(bi,outputStream,"PNG");

//        ImageRender wr = null;
//        try {
//            wr = new WriteRender(dr, outputStream, ImageFormat.JPEG);
//            wr.render();
//            wr.dispose();
//        } catch (SimpleImageException e) {
//            e.printStackTrace();
//        }

    }



    public BufferedImage ReadRawIMGStreamToByteGray(InputStream inputStream,ImageTypeSpecifier its,int width,int height) throws IOException {
        ShortBuffer shortBuffer = ShortBuffer.allocate(width*height);
        ByteBuffer bb=ByteBuffer.allocate(width*2*height);
        bb.order(ByteOrder.BIG_ENDIAN);
        Channels.newChannel(inputStream).read(bb);
        bb.flip();
        byte[] outbytes = new byte[width*height];
        ByteBuffer outByteBuffer = ByteBuffer.wrap(outbytes);
        short min = 0,max = 0;

        while (bb.hasRemaining()){
            short currentByte=0;
            try{
                currentByte = bb.getShort();
            }catch (BufferUnderflowException e){
                currentByte = 0;
            }
            max = (short)Math.max(max,currentByte);
            min = (short)Math.min(min,currentByte);
        }

        bb.flip();
        while(bb.hasRemaining()) {
            byte tempbyte = 0;
            short currentByte = 0;
            try {
                currentByte = bb.getShort();

            }catch (BufferUnderflowException e){
                currentByte = 0;
            }
            tempbyte = (byte) (((float)(currentByte-min))/(float)(max-min)*256);
//            tempbyte = (currentByte % 255 == 1) ? (byte) 255 : tempbyte;
            try {
                outByteBuffer.put(tempbyte);
            }catch (BufferOverflowException e){

            }
        }
        InputStream is = new  ByteArrayInputStream(outbytes);


        ImageTypeSpecifier imageType = null;
        final ImageTypeSpecifier imageType_Gray16bit = ImageTypeSpecifier.createGrayscale(16,DataBuffer.TYPE_USHORT,false);
        long[] imageOffsets = new long[1];
        final long IMAGERAW_OFFSET = 0;
        imageOffsets[0] = IMAGERAW_OFFSET;
        Dimension dimension = new Dimension(width,height);

        Dimension[] dimensions = new Dimension[1];
        dimensions[0] = dimension;

        if(its!=null){
            imageType = its;
        } else{
            imageType = imageType_Gray16bit;
        }

        //抽样 2:1,转为8bit 灰度图


        RawImageInputStream riis = new RawImageInputStream(ImageIO.createImageInputStream(is),its,imageOffsets,dimensions);
        riis.setByteOrder(ByteOrder.BIG_ENDIAN);
        RawImageReader reader = new RawImageReader(new RawImageReaderSpi());
        reader.setInput(riis);
        ImageReadParam param  = reader.getDefaultReadParam();

//        param.setSourceSubsampling(2,1,0,0);
//        ImageTypeSpecifier.createGrayscale();
        final ImageTypeSpecifier imageType_Gray8bit = ImageTypeSpecifier.createGrayscale(8,DataBuffer.TYPE_BYTE,false);
        param.setDestinationType(imageType_Gray8bit);
        BufferedImage bi;
        bi = reader.read(0,param);
        return bi;
    }


    public BufferedImage ReadIMGFORMATStream(InputStream inputStream,String formatSuffix) throws IOException {
        ImageReader reader = ImageIO.getImageReadersBySuffix(formatSuffix).next();
        ImageInputStream imageInputStream = new MemoryCacheImageInputStream(inputStream);

        reader.setInput(imageInputStream);
        BufferedImage bi = null;

        bi = reader.read(0);
        return bi;
    }

    public void MakeIMGFORMATStream(BufferedImage bufferedImage,OutputStream outputStream,String formatSuffix){
        ImageWriter writer = ImageIO.getImageWritersBySuffix(formatSuffix).next();
        ImageOutputStream imageOutputStream = null;
        imageOutputStream = new MemoryCacheImageOutputStream(outputStream);
        writer.setOutput(imageOutputStream);
        try {
            writer.write(null, new IIOImage(bufferedImage, null, null), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            imageOutputStream.flush();
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        writer.dispose();
    }

}
