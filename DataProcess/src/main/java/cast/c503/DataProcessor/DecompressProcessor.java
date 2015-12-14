package cast.c503.DataProcessor;

/**
 * Created by youngcle on 15-12-14.
 */

import com.github.jaiimageio.impl.plugins.raw.RawImageWriteParam;
import com.github.jaiimageio.jpeg2000.*;
import javax.imageio.*;

        import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageInputStream;
        import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
        import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class DecompressProcessor extends DataProcessorBase{

    static String BasePath = "/run/media/yanghl/77b85fda-c795-4518-b551-cd0bde3131e5/youngcle/DataProcessStormNew/DataProcess/src/main/resources";

    DecompressProcessor(){
        super();
        System.out.println("Creating DataProcessor Modual");
    }

    @Override
    public boolean  InitializeDataProcess(){
        System.out.println("Initializing DataProcessor Modual");
        return true;
    }
    @Override
    public boolean DoDataProcess()  {



        ImageReader reader = ImageIO.getImageReadersBySuffix("jp2").next();
        J2KImageReadParam readParams = (J2KImageReadParam) reader.getDefaultReadParam();

        ImageInputStream imageInputStream = null;
        try {
            imageInputStream = new FileImageInputStream(new File(BasePath+ "/HSICCD.jp2"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        reader.setInput(imageInputStream);
        BufferedImage bi = null;
        try {
            bi = reader.read(0);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ImageWriter writer = ImageIO.getImageWritersByFormatName("raw").next();

        ImageOutputStream imageOutputStream = null;
        try{
            imageOutputStream = new FileImageOutputStream(new File("/dev/shm/HSICCD.jp2.raw"));
        }catch (IOException e){
            e.printStackTrace();
        }

        writer.setOutput(imageOutputStream);

        try {
            writer.write(null, new IIOImage(bi, null, null), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        reader.dispose();
        writer.dispose();


        System.out.println("Doing DataProcessor Modual");
        return true;
    }

    public boolean DecodeTheJ2CStream(InputStream is){
        return true;
    }

    @Override
    public boolean CleanDataProcess(){
        System.out.println("cleaning DataProcessor Modual");
        return true;
    }

    public static void main(String[] args) throws Exception {
        new DecompressProcessor().DoDataProcess();
    }



}
