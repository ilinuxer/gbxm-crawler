package zx.soft.gbxm.facebook.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.facebook.api.FacebookSpider;

public class FBSpiderDriver {

	private static Logger logger = LoggerFactory.getLogger(FBSpiderDriver.class);

	public static void main(String[] args) throws InterruptedException {

		if (args.length == 0) {
			System.err.println("Usage:Driver<class-name>");
			System.exit(-1);
		}
		String[] leftArgs = new String[args.length - 1];
		System.arraycopy(args, 1, leftArgs, 0, leftArgs.length);
		switch (args[0]) {
		case "facebook":
			FacebookSpider.main(leftArgs);
			logger.info("facebook spider:");
		}

	}
}
