package zx.soft.gbxm.twitter.driver;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.twitter.api.TwitterSpider;

/**
 * 驱动类
 *
 * @author fgq
 *
 */
public class TwitterSpiderDriver {

	private static Logger logger = LoggerFactory.getLogger(TwitterSpiderDriver.class);

	/**
	 * 主函数
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
		case "twitter":
			logger.info("twitter spider： ");
			TwitterSpider.main(leftArgs);
			break;
		default:
			return;
		}

	}

}
