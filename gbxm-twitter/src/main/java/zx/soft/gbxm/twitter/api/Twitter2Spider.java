package zx.soft.gbxm.twitter.api;

import zx.soft.utils.threads.ApplyThreadPool;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by jimbo on 5/6/15.
 */
public class Twitter2Spider {

    public static void main(String[] args) {
        ThreadPoolExecutor pool = ApplyThreadPool.getThreadPoolExector();
        pool.execute(new ThreadOne(args));
        pool.execute(new ThreadTwo(args));
    }

    public static class ThreadOne implements Runnable{

        private String[] args;

        public ThreadOne(String[] args){
            this.args = args;
        }
        @Override
        public void run() {
            TwitterUserSpider.main(args);
        }
    }

    public static class ThreadTwo implements Runnable{
        private String[] args;
        public ThreadTwo(String[] args){
            this.args = args;
        }
        @Override
        public void run() {
            TwitterCurrentUserSpider.main(args);
        }
    }
}
