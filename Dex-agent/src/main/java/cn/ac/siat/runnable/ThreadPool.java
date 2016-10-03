package cn.ac.siat.runnable;

/**
 * Created by laiqingquan on 16/9/29.
 */
public interface ThreadPool<Job extends Runnable> {
    void execute(Job job);
    void shutdown();
    void addWorkers(int num);
    void removeWorkers(int num);
    int getJobSize();
}
