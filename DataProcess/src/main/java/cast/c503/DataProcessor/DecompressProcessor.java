package cast.c503.DataProcessor;

/**
 * Created by youngcle on 15-12-14.
 */

import cast.c503.DataPackage.DataPackageBase;
import cast.c503.DataPackage.DeformatDataPackage;
import com.github.jaiimageio.impl.plugins.raw.RawImageWriteParam;
import com.github.jaiimageio.impl.plugins.raw.RawImageWriter;
import com.github.jaiimageio.impl.plugins.raw.RawImageWriterSpi;
import com.github.jaiimageio.jpeg2000.*;
import com.github.jaiimageio.jpeg2000.impl.J2KImageReader;
import com.github.jaiimageio.jpeg2000.impl.J2KImageReaderSpi;

import javax.imageio.*;

        import javax.imageio.ImageWriter;
import javax.imageio.stream.*;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.Map;

public class DecompressProcessor extends DataProcessorBase{

    static String BasePath = "/run/media/yanghl/77b85fda-c795-4518-b551-cd0bde3131e5/youngcle/DataProcessStormNew/DataProcess/src/main/resources";
    ByteBuffer RawByteBuffer = ByteBuffer.allocateDirect(1024*1024*2);//两兆缓存
    ImageReader reader;
    public  DecompressProcessor(){
        super();
        System.out.println("Creating Decompress DataProcessor Modual");
        reader = new J2KImageReader(new J2KImageReaderSpi());//ImageIO.getImageReadersBySuffix("jp2").next();
        J2KImageReadParam readParams = (J2KImageReadParam) reader.getDefaultReadParam();
//        ImageIO.scanForPlugins();
        for (String spi:ImageIO.getWriterFormatNames())
            System.out.println("format spi plugins:"+spi);

    }

    @Override
    public boolean  InitializeDataProcess(){
        System.out.println("Initializing DataProcessor Modual");

        return true;
    }
    @Override
    public boolean DoDataProcess() {



        for (int j = 0; j < InputDataPackages.size(); j++) {
            DataPackageBase dataPackageBaseInput = InputDataPackages.get(j);
            Map<Integer, ByteBuffer> payloadsbufferMap = dataPackageBaseInput.getPayloadsBufferMap();
            for (Iterator iterator = payloadsbufferMap.keySet().iterator(); iterator.hasNext(); ) {
                Integer NumCode = (Integer) iterator.next();
                ByteBuffer byteBuffer = payloadsbufferMap.get(NumCode);


                ImageInputStream imageInputStream = null;

                imageInputStream = new MemoryCacheImageInputStream(new ByteArrayInputStream(byteBuffer.array()));
//                try {
////                    imageInputStream = new FileImageInputStream(new File("/dev/shm/"+dataPackageBaseInput.getPayloadName()+2011+".jp2"));
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
                reader.setInput(imageInputStream);

                BufferedImage bi = null;
                try {
                    bi = reader.read(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                ImageWriter writer;//ImageIO.getImageWritersByFormatName("raw").next();
                writer = new RawImageWriter(new RawImageWriterSpi());

                ImageOutputStream imageOutputStream = null;
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageOutputStream = new MemoryCacheImageOutputStream(baos);

                writer.setOutput(imageOutputStream);

                try {
                    writer.write(null, new IIOImage(bi, null, null), null);
                    imageOutputStream.flush();
                    baos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                writer.dispose();
                DataPackageBase dataPackageBaseOutput_RAW =new DataPackageBase(dataPackageBaseInput.getPayloadName()+"_DECOMPRESSED");
                dataPackageBaseOutput_RAW.setPackageNumCode(NumCode);
                dataPackageBaseOutput_RAW.addPayloadBuffer(NumCode,ByteBuffer.wrap(baos.toByteArray()));
                OutputDataPackages.add(dataPackageBaseOutput_RAW);
            }
//            System.out.println("Doing DataProcessor Modual");
        }
        reader.dispose();

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
