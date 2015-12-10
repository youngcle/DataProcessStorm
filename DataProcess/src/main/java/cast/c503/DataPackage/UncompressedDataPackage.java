package cast.c503.DataPackage;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yanghl on 15-12-3.
 * 解压缩数据包
 *
 *
 *
 * 1~4	帧头	4	FAF33400
 5	载荷标识	1	01H：CCD1
 02H：CCD2
 03H：CCD3
 04H：CCD4
 05H：CCD5
 06H：CCD6
 07H：CCD7
 08H：CCD8
 6~7	幅计数	2	统计幅计数	计数以幅为单位，一副图像大小为128×4096
 8~9	幅内行计数	2	一副图像内行计数
 10~11	行像素数量	2	一行图像内像素个数	4096
 12	存储方式和精度	1	标识存储方式和精度	高4bit表示存储方式，低4bit表示图像精度
 13	压缩比	1	D5H：8:1压缩
 EAH：4:1压缩
 C0H：无损压缩
 14~16	备用	3
 17~48	第1行图像辅助数据	32	图像辅助数据	可见光相机一包整星辅助数据包含4行图像辅助数据（4×32字节的辅助数据）
 49~8240	第1行图像数据	8192	图像数据	相机每个像素为10bit表示，Fred数据中用16bit表示10bit数据，高6位添零
 …… ……
 第128行图像辅助数据	8192	图像数据 	相机每个像素为10bit表示，Fred数据中用16bit表示10bit数据，高6位添零
 *
 *
 *
 */
public class UncompressedDataPackage extends DataPackageBase{



    @Override
    public boolean SetPayloads(ArrayList<byte[]> payloads) {
        return false;
    }

    @Override
    public ArrayList<byte[]> getPayloads() {
        return null;
    }
}
