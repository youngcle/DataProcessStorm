package cast.c503;

import backtype.storm.topology.TopologyBuilder;
import backtype.storm.*;


/**
 * Created by youngcle on 15-11-30.
 */


public class StormRoller {





    public static void main(String[] args) throws Exception {

        TopologyBuilder builder = new TopologyBuilder();

        builder.setSpout("spout", new RedisSpout(),1);
        builder.setBolt("deformat", new DataParseBolt(),4).shuffleGrouping("spout");
        builder.setBolt("CCDdecompress", new DecompressCCDBolt(),12).shuffleGrouping("deformat","COMPRESSED_CCD_STREAM");
        builder.setBolt("HSICCDdecompress", new DecompressHSICCDBolt(),12).shuffleGrouping("deformat","COMPRESSED_HSICCD_STREAM");
        builder.setBolt("IRSdecompress", new DecompressIRSBolt(),12).shuffleGrouping("deformat","COMPRESSED_IRS_STREAM");
        builder.setBolt("HSIIRSdecompress", new DecompressHSIIRSBolt(),12).shuffleGrouping("deformat","COMPRESSED_HSIIRS_STREAM");

        //从解压缩处理中得到不同的流，按照数据名称标示选择相对应流进行处理
        //以帧ID为主键，构建链表，数据包压入内存数据库，对主键进行排序后，输出文件
        //排序输出的文件，每3000个包输出1000个，形成文件，同时清空数据库中已经输出的数据
        builder.setBolt("CCDShardRecord", new MosaicCCDBolt(), 1).globalGrouping("CCDdecompress","RAW_CCD_STREAM");
        builder.setBolt("HSICCDShardRecord", new MosaicHSICCDBolt(), 1).globalGrouping("HSICCDdecompress","RAW_HSICCD_STREAM");
        builder.setBolt("IRSShardRecord", new MosaicIRSBolt(), 1).globalGrouping("IRSdecompress","RAW_IRS_STREAM");
        builder.setBolt("HSIIRSShardRecord", new MosaicHSIIRSBolt(), 1).globalGrouping("HSIIRSdecompress","RAW_HSIIRS_STREAM");



        TopologyBuilder recordTopBuilder = new TopologyBuilder();
        recordTopBuilder.setSpout("SortedDataSpout",new SortedDataRedisSpout(),1);

        recordTopBuilder.setBolt("CCDQuickViewMakerBolt",new QuickViewBolt(),2).shuffleGrouping("SortedDataSpout","SORTEDRAW_CCD_STREAM");
        recordTopBuilder.setBolt("HSICCDQuickViewMakerBolt",new QuickViewBolt(),2).shuffleGrouping("SortedDataSpout","SORTEDRAW_HSICCD_STREAM");
        recordTopBuilder.setBolt("IRSQuickViewMakerBolt",new QuickViewBolt(),2).shuffleGrouping("SortedDataSpout","SORTEDRAW_IRS_STREAM");
        recordTopBuilder.setBolt("HSIIRSQuickViewMakerBolt",new QuickViewBolt(),2).shuffleGrouping("SortedDataSpout","SORTEDRAW_HSIIRS_STREAM");

        recordTopBuilder.setBolt("CCDFormatAndWriterBolt",new FormatAndWriteBolt(),1).globalGrouping("SortedDataSpout","SORTEDRAW_CCD_STREAM");
        recordTopBuilder.setBolt("HSICCDFormatAndWriterBolt",new FormatAndWriteBolt(),1).globalGrouping("SortedDataSpout","SORTEDRAW_HSICCD_STREAM");
        recordTopBuilder.setBolt("IRSFormatAndWriterBolt",new FormatAndWriteBolt(),1).globalGrouping("SortedDataSpout","SORTEDRAW_IRS_STREAM");
        recordTopBuilder.setBolt("HSIIRSFormatAndWriterBolt",new FormatAndWriteBolt(),1).globalGrouping("SortedDataSpout","SORTEDRAW_HSIIRS_STREAM");



//        builder.setBolt("HSIIRSdecompress", new ShardRecordHSIIRSBolt(), 1).shuffleGrouping("deformat","HSIIRS_COMPRESSED");

//        builder.setBolt("record", new FormatAndWriteBolt(), 1).shuffleGrouping("deformat");


        Config conf = new Config();
        conf.setDebug(false);
        conf.setMaxTaskParallelism(1280);
        conf.setNumWorkers(6);
        conf.setMaxSpoutPending(10);
        conf.put("redishost","localhost");
        conf.put("redishostport","6379");




        if (args != null && args.length > 0) {
//            conf.setNumWorkers(3);

            StormSubmitter.submitTopologyWithProgressBar("simstorm", conf, builder.createTopology());
            StormSubmitter.submitTopologyWithProgressBar("SortedDataQVAndRecord", conf, recordTopBuilder.createTopology());
        }
        else {
            conf.setMaxTaskParallelism(3);


            LocalCluster cluster = new LocalCluster();
            cluster.submitTopology("simprocess", conf, builder.createTopology());
            cluster.submitTopology("SortedDataQVAndRecord", conf, recordTopBuilder.createTopology());

            Thread.sleep(1000*60*50);

            cluster.shutdown();
        }
    }
}
