package cast.c503.DataProcessor;

import com.alibaba.simpleimage.ImageFormat;
import com.alibaba.simpleimage.ImageRender;
import com.alibaba.simpleimage.ImageWrapper;
import com.alibaba.simpleimage.SimpleImageException;
import com.alibaba.simpleimage.font.FontLoader;
import com.alibaba.simpleimage.io.ByteArrayInputStream;
import com.alibaba.simpleimage.io.ByteArrayOutputStream;
import com.alibaba.simpleimage.render.*;
import com.sun.deploy.util.StringUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;



/**
 * Created by youngcle on 15-12-7.
 */
public class ImageTager {
    Color font_color        = Color.ORANGE;//new Color(255, 255, 255, 115);
    Color font_shadow_color = new Color(170, 170, 170, 77);
    Font  str_font              = new Font("kaiti", Font.PLAIN, 10);
    String PROCESS_STRING   = "标签数据";
    final FixDrawTextItem.Position STR_POSITION = FixDrawTextItem.Position.TOP_LEFT;
    File InputImageFile;
    InputStream InputImageStream;
    private Map<String,Color> StringColorMap = new HashMap<String, Color>();



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
        str_font = new FontLoader("kaiti","/ukai.ttc").getFont();
        StringColorMap.put("red", Color.RED);

        StringColorMap.put("green", Color.GREEN);
        StringColorMap.put("orange", Color.orange);
        StringColorMap.put("blue", Color.blue);
        StringColorMap.put("black", Color.black);
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

        param.addTextInfo(new ReleatePositionDrawTextItem("编号："+numCode,font_color,font_shadow_color,str_font,10, 0.2f,0.01f,0.1f));
        param.addTextInfo(new ReleatePositionDrawTextItem("类型："+tagtext,font_color,font_shadow_color,str_font,10, 0.2f,0.85f,0.1f));
        ImageRender dr = new DrawTextRender(InputImageStream, param);
        try {
            dr.render();
        } catch (SimpleImageException e) {
            e.printStackTrace();
        }
        ImageRender wr = null;
        try {
            wr = new WriteRender(dr, outputStream, ImageFormat.JPEG);
            wr.render();
            wr.dispose();
        } catch (SimpleImageException e) {
            e.printStackTrace();
        }

    }

}
