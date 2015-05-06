package zx.soft.gbxm.google.api;

import zx.soft.gbxm.google.status.GoogleStatus;
import zx.soft.utils.threads.ApplyThreadPool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by jimbo on 5/6/15.
 */
public class Google2Spider {

    public static void main(String[] args) {
        ThreadPoolExecutor pool = ApplyThreadPool.getThreadPoolExector();
        pool.execute(new ThreadOne(args));
        pool.execute(new ThreadTwo(args));
        pool.execute(new ThreadThree(args));
    }
    public static class ThreadOne implements Runnable{

        private String[] args;

        public ThreadOne(String[] args){
            this.args = args;
        }
        @Override
        public void run() {
            GoogleCurrentUser.main(args);
        }
    }

    public static class ThreadTwo implements Runnable{
        private String[] args;
        public ThreadTwo(String[] args){
            this.args = args;
        }
        @Override
        public void run() {
            GoogleUser.main(args);
        }
    }

    public static class ThreadThree implements Runnable{
        private String[] args;
        public ThreadThree(String[] args) {
            this.args = args;
        }

        @Override
        public void run() {
            GoogleStatus.main(args);
        }
    }
}
