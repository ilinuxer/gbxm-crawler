package zx.soft.gbxm.google.driver;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.google.api.Google2Spider;

public class Google2SpiderDriver {

    private static Logger logger = LoggerFactory.getLogger(Google2SpiderDriver.class);

    public static void main(String[] args) throws IOException, GeneralSecurityException, InterruptedException {
        if (args.length == 0) {
            System.out.println("Usage:Driver<class-name>");
            System.exit(-1);
        }
        String[] leftArgs = new String[args.length - 1];
        System.arraycopy(args, 1, leftArgs, 0, leftArgs.length);
        switch (args[0]) {
            case "googleSpider":
                logger.info("google spider: ");
                Google2Spider.main(leftArgs);
                break;
            default:
                return;
        }
    }

}
