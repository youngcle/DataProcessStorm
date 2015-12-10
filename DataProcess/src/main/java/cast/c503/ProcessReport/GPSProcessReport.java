package cast.c503.ProcessReport;

/**
 * Created by yanghl on 15-12-3.
 *
 * messageType	String		通知类型：“GPSDATARESULT”
 messageID	Number		标识号，数字，6位定长，且唯一（与文件名中的六位整数相同）
 trPlanID	Number		数字，9位定长，且唯一。
 originatorAddress	String		发送方主机名：“DRPP”。
 recipientAddress	String		接收方主机名：“CIS”
 creationTime	String		文件生成时间，北京时，格式为：
 YYYY-MM-DDTHH:MM:SS.UUU
 （.UUU可选）
 GPS数据信息GPSDataInfo
 srcDataNumber	Number	√	报告序号
 srcChannel	string		原始数据通道号
 通道一：“channel1”
 通道二：“channel2”
 workMode	Double		工作模式：
 0：时传模式
 1：回放模式
 2：中继模式
 GPS数据报告GPSDataResult (内容可重复,每秒1次)
 GPSData	String		GPS时间：北京时，格式为：
 YYYY-MM-DDTHH:MM:SS.UUU
 （.UUU可选），具体为：
 北京时间微秒数高字长：1字长
 北京时间微秒数中字长：1字长
 北京时间微秒数低字长：1字长
 定位标记和捕获星数：1字长
 satelliteXLocation	Float		X位置坐标,4字节，二进制补码，位置坐标的单位为厘米
 satelliteYLocation	Float		Y位置坐标,4字节，二进制补码，位置坐标的单位为厘米
 satelliteZLocation	Float		Z位置坐标,4字节，二进制补码，位置坐标的单位为厘米
 satelliteXVelocity 	Float	√	X速度坐标,4字节，二进制补码，位置坐标的单位为厘米
 satelliteYVelocity 	Float	√	Y速度坐标,4字节，二进制补码，位置坐标的单位为厘米
 satelliteZVelocity 	Float	√	Z速度坐标,4字节，二进制补码，位置坐标的单位为厘米
 GPS数据报告完成情况结束
 内容结束

 *
 *
 *
 */
public class GPSProcessReport {
}
