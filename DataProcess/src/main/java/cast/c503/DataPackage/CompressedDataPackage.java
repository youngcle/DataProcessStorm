package cast.c503.DataPackage;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yanghl on 15-12-3.
 *
 * 压缩数据包
 *
 *
 压缩包帧头	4	FAF33400H
 压缩通道标识	1	01H：CCD1
 02H：CCD2
 03H：CCD3
 04H：CCD4
 05H：CCD5
 06H：CCD6
 07H：CCD7
 08H：CCD8
 图像帧号	2	00 00～FF FF
 帧内块号	1	01H
 压缩比标识	1	D5H：8:1压缩
 EAH：4:1压缩
 C0H：无损压缩
 分段快筛标识
 （备用）	1	高4bit表示是否新分段
 低4bit表示是否是快筛目标点
 相机辅助数据	4096	128行CCD图像的所有辅助数据，每行32字节
 压缩码流	N	128行CCD图像压缩后的压缩码流
 *
 *
 */
public class CompressedDataPackage extends DataPackageBase {





    @Override
    public boolean SetPayloads(ArrayList<byte[]> payloads) {
        return false;
    }

    @Override
    public ArrayList<byte[]> getPayloads() {
        return null;
    }
}
