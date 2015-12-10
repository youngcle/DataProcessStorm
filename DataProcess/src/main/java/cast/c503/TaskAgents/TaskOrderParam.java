package cast.c503.TaskAgents;

/**
 * Created by yanghl on 15-12-3.
 *
 *
 * 1	taskID	string	由PMMS指定的当前子任务的ID号
 2	tfsAddr	string	PMMS作为服务端进行监听的IP地址和端口，任务进程通过此端口接收PMMS的控制指令并上报状态信息
 3	imsAddr	string	PMMS监听的IP地址和端口，此参数可以缺失，如果没有该参数说明信息服务系统没有启用
 4	workMode	int	任务进程工作模式
 DDMCN
 5	configDir	string	任务进程所需的配置文件所在目录
 6	workDir	string	任务进程输出的日志文件所在目录
 7	satDataDir	string	记录的卫星数据输出的目录
 8	resultFileName	string	任务进程上报的任务完成报告文件全路径和文件名
 9	startTime	string	任务的开始时间，该参数取值可以为空，如果为空，则任务将立即开始
 时间字符串格式形如：YYYY-MM-DD hh:mm:ss
 10	endTime	string	任务的结束时间，该参数取值可以为空，如果为空，则任务自行决定何时结束
 时间字符串格式形如：YYYY-MM-DD hh:mm:ss
 专用参数
 11	planID	string	跟踪接收计划ID或回放计划号
 12	satelliteID	string	卫星标识，为下列值之一
 TG-1、JB11-1、JB11-2
 13	sensorID	string	传感器组合标识
 “IRS“红外
 “HSICCD+HSIIRS“高光谱
 “ IRS+HSICCD+HSIIRS“红外+高光谱
 “ CCD+HSICCD+HSIIRS+IRS“可见光+红外+高光谱，为默认值
 “ CCD+HSICCD+HSIIRS“可见光+高光谱
 “ CCD+IRS“可见光+红外
 14	sensorName	string	本次记录的格式化数据的传感器名
 光学：“CCD-1”……“CCD-8” (1～8分别指8片CCD)
 红外：“IRS-1”、“IRS-2”（1、2指前后半视场）
 15	orbitID	string	轨道号，数字，最多6位，且唯一
 16	stationID	string	接收站标识
 “TRGS-BJ”
 “TRGS-SY”
 “TRGS-KS”
 “TRGS-KM”
 “TRGS-MDJ”
 “TRGS-JD-1”
 “TRGS-JD-2”
 “TRGS-JD-3”
 “TRGS-JD-4”
 “TRGS-JD-5”
 17	downlinkChannel	Int	下行通道
 3，channel1+channel2（适用于JB11双通道同时下传）
 6：JB11-1双通道非极化正常工作模式
 18	sensorMode	String	“LIVERECEPTION”（实时接收）；
 “PLAYBACK”（星上数据记录回放）；
 “halfplayback”（星上数据记录降速回放）；
 “semi-LIVERECEPTION”（9号准实时数据）
 “RECORD-PLAYBACK”成像对地边记边放模式；
 “INFRARED-LIVERECEPTION-PLAYBACK”单红外对地实传+回放；
 “TELEMETRY-PLAYBACK”平台数据回放模式；
 “POLARIZATION-LIVERECEPTION”极化复用对地实时传输；
 “POLARIZATION-RECORD-PLAYBACK”极化复用对地边记边放；
 “POLARIZATION- PLAYBACK”极化复用对地回放；
 “RELAY-RECORD-PLAYBACK”成像中继边记边放；
 “RELAY-PLAYBACK”数据中继回放；
 19	inputNum	int	输入数据源个数，为1
 输入数据源信息inputDataChannel（可重复）
 20	ID	string	子任务ID
 原始数据记录回放节点：BDRPN-1
 21	IP	String	输入数据的IP
 22	port	String	输入数据的端口
 输入数据源信息inputDataChannel重复结束
 23	outputNum	int	输出数据源个数，默认为6
 输出数据源信息outputDataChannel（可重复）
 24	ID	string	子任务ID
 可见光记录节点：PDRN-1、PDRN-2、PDRN-3、PDRN-4
 高光谱数据分发节点：HDDN
 红外记录节点：IDRN
 25	IP	string	输出数据的IP
 26	port	string	输出数据的端口
 输出数据源信息outputDataChannel重复结束
 27	isDataCheck	bool	是否数据质量分析：
 “TRUE”；进行数据质量分析
 “FALSE”；不进行数据质量分析
 28	targetNum	int	快筛目标点个数
 快筛目标信息targetInfo（可重复）
 29	taskID	string	成像信息的taskID
 编码规则为“年月日_分系统代号_需求流水号_任务流水号”，需求流水号为5位定长，任务流水号为3位定长，譬如任务控制分系统为
 090224_MPSS_00001_001
 30	targetID	string	目标编号，10位定长
 targetName	string	快筛目标点命名，最多80位
 31	targetTime	string	快筛目标点的起始和结束时间，以“，”分隔
 例如：
 <targetTime>2009:03:12 15:23:43,2009:03:12 15:23:48</targetTime>
 32	targetLocation	string	快筛目标点的位置信息，由中心点经纬度、左上角经纬度和右下角经纬度标识，以“，”分隔，无效值可用360代替
 例如：
 <targetLocation>111.22,43.321,360,360,360,360</targetLocation>
 快筛目标信息targetInfo重复结束
 参数结束
 *
 *
 */
public class TaskOrderParam extends TaskParamBase{


}
