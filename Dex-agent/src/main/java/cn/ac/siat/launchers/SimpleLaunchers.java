package cn.ac.siat.launchers;

import org.apache.spark.launcher.SparkLauncher;

import java.io.IOException;

/**
 * Created by laiqingquan on 16/9/3.
 */
public class SimpleLaunchers {
    private String appName;
    private String sparkHome;
    private String appResources;
    private String mainClass;
    private String master;
    private String deployMode;


    private SparkLauncher launcher;

    public SimpleLaunchers(String appName, String sparkHome, String appResources, String mainClass, String master, String deployMode) {
        this.appName = appName;
        this.sparkHome = sparkHome;
        this.appResources = appResources;
        this.mainClass = mainClass;
        this.master = master;
        this.deployMode = deployMode;

        launcher = new SparkLauncher();
        launcher.setAppName(appName).setSparkHome(sparkHome)
                .setAppResource(appResources).setMainClass(mainClass)
                .setMaster(master).setDeployMode(deployMode);
    }

    public String getAppName() {
        return appName;
    }

    public String getSparkHome() {
        return sparkHome;
    }

    public String getAppResources() {
        return appResources;
    }

    public String getMainClass() {
        return mainClass;
    }

    public String getMaster() {
        return master;
    }

    public String getDeployMode() {
        return deployMode;
    }

    public Process launch(String ...args) throws IOException {
        launcher.addAppArgs(args);
        return launcher.launch();
    }
}
