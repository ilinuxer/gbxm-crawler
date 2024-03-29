package zx.soft.gbxm.twitter.driver;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.twitter.api.Twitter2Spider;

/**
 * 驱动类
 */
public class Twitter2SpiderDriver {

    private static Logger logger = LoggerFactory.getLogger(Twitter2SpiderDriver.class);

    /**
     * 主函数
     *
     * @throws InterruptedException
     * @throws IOException
     */
    public static void main(String[] args) throws InterruptedException, IOException {


        if (args.length == 0) {
            System.err.println("Usage: Driver <class-name>");
            System.exit(-1);
        }
        String[] leftArgs = new String[args.length - 1];
        System.arraycopy(args, 1, leftArgs, 0, leftArgs.length);

        switch (args[0]) {
            case "twitterSpider":
                logger.info("twitter spider： ");
                Twitter2Spider.main(leftArgs);
                break;
            default:
                return;
        }

    }



}
