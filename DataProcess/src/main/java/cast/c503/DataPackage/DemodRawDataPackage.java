package cast.c503.DataPackage;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by yanghl on 15-12-3.
 */

/*

0～2	Integer	0：包头：1234567890
        1：信息大小（包长）：4 x N
        2：一般不用	标准记录帧TCP-IP头
3	Integer	0：遥感数据通道A
        1：遥感数据通道B
        2：遥感数据通道C	遥感数据通道
4	Integer	4：实时遥感数据	请求码
5～7			保留位
8	Integer	8～64(以bit计)	同步字长度
9	Integer	10 ～1,048,576 (以byte计)	帧长度（包括帧头）
10	Integer	1 (以64bit字计)	时间下标的字长
11	Integer	(以64bit字计) 0～8bit：若帧同步缺失，备用状态域的数量； 1～9bit：若有帧同步，帧同步状态的数量+备用状态域的数量	TM block中
状态域长度；
 12	Integer	最大 131,082
(in 64-bit words)	TM block的大小
（in 64-bit words）
13	Integer	0 ～131072	当前TM 信息帧中
TM block的数量
14	Integer	0；或者用户定义（64bit字）	TM文件的头大小
15&16	Integer	FFFFFFFFH	强制数值
17
Integer	0：无溢出警告；其他：失去PC RAM缓冲区的访问用户数量。当操作模式变为查询时清零复位。	PC RAM缓冲
区溢出告警
18	Float	0～100:PC RAM缓冲区未被占用的百分比；越接近0越有可能丢失PC RAM缓冲区内的数据。	PC RAM缓冲区
空白页比例
19		字长64bit	第一个TM block
（见下表）
…	…
…	…
最后一个TM block
N-1	Integer	-1234567890	标准TCP-IP帧尾
*/
public class DemodRawDataPackage extends DataPackageBase {
    byte[] PackageHeadCode= new byte[4];

    public DemodRawDataPackage(){
//        this.Payloads = new LinkedList<char[]>();
    }



    public boolean SetPayloads(ArrayList<byte[]> payloads) {
        Payloads = payloads;
        return false;
    }



    @Override
    public ArrayList<byte[]> getPayloads() {
        return Payloads;
    }
}
