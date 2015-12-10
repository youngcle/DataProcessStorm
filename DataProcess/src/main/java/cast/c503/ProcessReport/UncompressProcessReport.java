package cast.c503.ProcessReport;

/**
 * Created by yanghl on 15-12-3.
 *
 * taskID	String		由PMMS指定的当前子任务的ID号
 creationTime	String		文件生成时间，北京时，格式为：
 YYYY-MM-DDTHH:MM:SS.UUU
 （.UUU可选）
 actualSensorType	String		本次任务实际接收到的载荷类型组合，多个载荷类型之间用分号隔开。
 “SAR”雷达
 “CCD”光学
 “ELECTRON”电子
 “IRS”红外
 “HSICCD”可见光高光谱
 “HSIISR”红外高光谱
 “None”适用于平台遥测数据回放
 解密数据质量分析报告decryptDataResult（内容可重复）
 decryptDataReportNumber	Number	√	报告序号
 nodeID	Number	√	节点编号
 srcchannel	string		原始数据通道号
 通道一：“channel1”
 通道二：“channel2”
 encryptEnable	String		 “yes”加密
 “no”明传
 decryptDataFileName	String	√	数据文件全路径
 decryptDataLength	String		文件长度，64位整数，单位为BYTE
 decryptDataCreationTime 	String	√	数据产生时间，北京时，格式为：
 YYYY-MM-DDTHH:MM:SS.UUU
 （.UUU可选）
 decryptDataContain	String		帧头容错状态(3bit)
 “On”
 “Off”
 decryptSensorType	String		解密机接收到的载荷类型组合，多个载荷类型之间用分号隔开。
 “SAR”雷达
 “CCD”光学
 “ELECTRON”电子
 “IRS”红外
 “HSICCD”可见光高光谱
 “HSIISR”红外高光谱
 “None”适用于平台遥测数据回放
 playback	string		回放：“pb”
 直传：“live”
 polarize	string		极化复用:
 非极化：“ no”
 极化：“ yes”
 decryptDataEcr	Double		误码率统计（帧头误码率）
 decryptFrameRx	Number		解密数据总帧数（包括填充帧）
 decryptFrameEmpty	Number		解密数据填充帧数
 CRCErNum	double		CRC校验AOS帧存在误码帧的数量
 RSErNum	double		RS译码AOS帧存在误码帧的数量
 解密数据质量分析报告结束
 格式解析数据质量分析报告deAos（可重复）
 deAosDataReportNumber	Number	√	报告序号
 nodeID	Number	√	节点编号
 srcFrameRx	Number		格式解析输入总帧数（包括填充帧）
 decryptFrameLost	Number		格式解析输入废帧数
 废帧：虚拟信道标识错误，既非载荷帧又非空帧，但是格式解析又无法处理的帧。
 格式解析载荷信息decAosLoad（可重复）
 dataSource	String		该载荷数据类型：
 “CCD-1”～“CCD-8”光学
 “IRS-1”红外
 “HSICCD-1”～ “HSICCD-4”可见光光谱仪
 “HSIIRS-1”～ “HSIIRS-2”红外光谱仪
 “SDRTU”平台遥测数据回放
 frameNum	Number		该载荷输入AOS帧数量
 packageNum	Number		该载荷输出压缩包数量
 vIrtualChannelNum	Number	√	该载荷虚拟信道标识符
 decAosframeCountBegin	Number	√	该载荷第一行帧计数
 decAosframeCountLast	Number	√	该载荷最后一行帧计数
 decAosframeCountJumpNum	Number		该载荷跳帧次数
 格式解析跳变帧信息frameJumpERR（可重复）
 frameBeginJump	double	√	跳变帧在解密后数据文件中的存储起始位置
 frameJumpNum	double	√	跳变帧的VCDU计数
 frameLostNum	double	√	丢帧个数，即跳变帧与前一帧的VCDU之差-1
 格式解析跳变帧信息结束
 格式解析跳变压缩包信息packageJumpERR（可重复）
 packageBeginJump	double	√	跳变包在格式解析后数据文件中的存储起始位置
 packageJumpNum	double	√	跳变包的包计数
 packageLostNum	double	√	丢包个数，即跳变包与前一包的包计数之差-1
 格式解析跳变压缩包信息结束
 格式解析载荷帧信息结束
 格式解析数据质量分析报告结束
 解压缩数据质量分析报告decmpDataResult（内容可重复）
 decmpDataReportNumber	Number	√	报告序号
 nodeID	Number	√	节点编号
 解压缩载荷压缩包信息decmpLoad（可重复）
 dataResource	String		该载荷数据类型：
 “CCD-1”～“CCD-8”光学
 “IRS-1”红外
 “HSICCD-1”～ “HSICCD-4”可见光光谱仪
 “HSIIRS-1”～ “HSIIRS-2”红外光谱仪
 “SDRTU”平台遥测数据回放
 decmpStartCnt	Number	√	该载荷解压缩输出第一行中卫星辅助数据中的行计数
 decmpEndCnt	Number	√	该载荷解压缩输出最后一行中卫星辅助数据中的行计数
 decmpAllCnt	Number	√	该载荷解压缩输出的所有数据总包数
 decmpEmptyCnt	Number	√	该载荷解压缩输出的填充包总数
 decmpDataCreationTime 	String	√	数据产生时间，北京时，格式为：
 YYYY-MM-DDTHH:MM:SS.UUU
 （.UUU可选）
 解压缩数据错误信息decmpERR（重复）
 packageBeginJump	double	√	跳变包在解压缩后数据文件中的存储起始位置
 packageJumpNum	double	√	跳变包的包计数
 packageLostNum	double	√	丢包个数，即跳变包与前一包的包计数之差-1
 解压缩数据错误信息结束
 解压缩载荷压缩包信息结束
 解压缩数据质量分析报告结束
 内容结束
 *
 *
 */
public class UncompressProcessReport {
}
