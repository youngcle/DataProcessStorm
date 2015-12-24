package cast.c503.DataProcessor;


import cast.c503.DataPackage.DataPackageBase;
import cast.c503.TaskAgents.TaskParamBase;
import jdk.internal.util.xml.impl.Input;
import sun.awt.image.ImageWatched;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by youngcle on 15-11-27.
 */
public abstract class DataProcessorBase {
    TaskParamBase taskParam;

    String ProcessName;
    String version;
    String workingMode;
    boolean debugMode = false;
    String workingDir;


    public ArrayList<DataPackageBase>  InputDataPackages = new ArrayList<DataPackageBase>();
    public ArrayList<DataPackageBase> OutputDataPackages = new ArrayList<DataPackageBase>();

    DataProcessorBase(){

//        System.out.println("Creating DataProcessor Modual");
    }


    public boolean  InitializeDataProcess(){
        System.out.println("Initializing DataProcessor Modual");
        return true;
    }

    public boolean DoDataProcess(){
        System.out.println("Doing DataProcessor Modual");
        return true;
    }

    public boolean CleanDataProcess(){
        System.out.println("cleaning DataProcessor Modual");
        return true;
    }



    public void setInputDataPackages(ArrayList<DataPackageBase> inputDataPackages) {
        InputDataPackages.addAll(inputDataPackages);
    }
    public void setOutputDataPackages(ArrayList<DataPackageBase> outputDataPackages) {
        OutputDataPackages.addAll(outputDataPackages);
    }


    public void addInputDataPackage(DataPackageBase inputDataPackage) {
        if(InputDataPackages==null)
            InputDataPackages = new ArrayList<DataPackageBase>();
        InputDataPackages.add(inputDataPackage);
    }

    public ArrayList<DataPackageBase> getCloneOutputDataPackages() {
        ArrayList<DataPackageBase> newoutputpackages = (ArrayList<DataPackageBase>) OutputDataPackages.clone();
        return newoutputpackages;
    }

    public ArrayList<DataPackageBase> getOutputDataPackages() {
        return OutputDataPackages;
    }


    public ArrayList<DataPackageBase> getInputDataPackages() {
        return InputDataPackages;
    }

    public void ClearOutputDataPackages(){
        OutputDataPackages.clear();
    }



    public TaskParamBase getTaskParam() {
        return taskParam;
    }

    public void setTaskParam(TaskParamBase taskParam) {
        this.taskParam = taskParam;
    }

    public void ReInitialize(){
        InputDataPackages.clear();
        OutputDataPackages.clear();
    }
}
