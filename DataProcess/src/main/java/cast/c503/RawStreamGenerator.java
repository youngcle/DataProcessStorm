package cast.c503;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yanghl on 15-12-4.
 */
public class RawStreamGenerator {
    private LinkedList<char[]> RawDataPackageList;
    String RawDataStrChannelOne = "satellite RAW data head:CCD1:CCD2:CCD3:CCD4:SWIR1:VNIR1:IR1:SDRTU:frameID:";
    String AOSDataStrChannelOne = "satellite:%VCDU% %FRAMEID%";

    static int STREAM_LENGTH = 1024;


    public char[] MakeRawDataPackage(int frameID) {
        char[] RawDataPackage=new char[128];
        String RawDataSeedTxt = RawDataStrChannelOne+frameID;
        int length = RawDataSeedTxt.length();
        for (int i=0;i<length;i++){
            RawDataPackage[i] =RawDataSeedTxt.toCharArray()[i];
        }
        return RawDataPackage;
    }

    public void InitRawStream(){
        RawDataPackageList = new LinkedList<char[]>();
        for(int i=0;i<STREAM_LENGTH;i++){
            getRawDataPackageList().add(MakeRawDataPackage(i));
        }
    }

    public LinkedList<char[]> getRawDataPackageList() {
        return RawDataPackageList;
    }

    public void setRawDataPackageList(LinkedList<char[]> rawDataPackageList) {
        RawDataPackageList = rawDataPackageList;
    }
}
