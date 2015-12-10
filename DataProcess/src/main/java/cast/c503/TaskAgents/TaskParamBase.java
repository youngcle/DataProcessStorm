package cast.c503.TaskAgents;

/**
 * Created by youngcle on 15-11-27.
 */
public abstract class TaskParamBase {
    String SatModel = "SAT-ABC";
    String SenserModel = "SENSOR-DEF";
    int OrbitNum = 9981;
    String WorkingMode = "Realtime";

    String TaskTime = "2015-12-07 11:11:00";
    String TaskSource = "localtest";

    String DataName = "test_data_name";
    String DataFormat = "RAW";

    String InputFormat = "RAW";
    String OutputFormat = "MOCKFRED";

    int priorty;

    public String getSatModel() {
        return SatModel;
    }

    public void setSatModel(String satModel) {
        SatModel = satModel;
    }

    public String getSenserModel() {
        return SenserModel;
    }

    public void setSenserModel(String senserModel) {
        SenserModel = senserModel;
    }

    public int getOrbitNum() {
        return OrbitNum;
    }

    public void setOrbitNum(int orbitNum) {
        OrbitNum = orbitNum;
    }

    public String getWorkingMode() {
        return WorkingMode;
    }

    public void setWorkingMode(String workingMode) {
        WorkingMode = workingMode;
    }

    public String getTaskTime() {
        return TaskTime;
    }

    public void setTaskTime(String taskTime) {
        TaskTime = taskTime;
    }

    public String getTaskSource() {
        return TaskSource;
    }

    public void setTaskSource(String taskSource) {
        TaskSource = taskSource;
    }

    public String getDataName() {
        return DataName;
    }

    public void setDataName(String dataName) {
        DataName = dataName;
    }

    public String getDataFormat() {
        return DataFormat;
    }

    public void setDataFormat(String dataFormat) {
        DataFormat = dataFormat;
    }

    public String getInputFormat() {
        return InputFormat;
    }

    public void setInputFormat(String inputFormat) {
        InputFormat = inputFormat;
    }

    public String getOutputFormat() {
        return OutputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        OutputFormat = outputFormat;
    }

    public int getPriorty() {
        return priorty;
    }

    public void setPriorty(int priorty) {
        this.priorty = priorty;
    }
}
