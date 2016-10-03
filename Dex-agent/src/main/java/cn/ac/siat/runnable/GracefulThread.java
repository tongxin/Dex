package cn.ac.siat.runnable;

/**
 * Created by laiqingquan on 16/9/29.
 */
public abstract class GracefulThread extends Thread{
    private volatile boolean isShutdown;

    public GracefulThread() {
        super();
    }

    public GracefulThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        isShutdown = false;
        while(!isShutdown) {
            handle();
        }
    }

    public void shutdownGracfully() {
        isShutdown = true;
    }

    public abstract void handle();

}
