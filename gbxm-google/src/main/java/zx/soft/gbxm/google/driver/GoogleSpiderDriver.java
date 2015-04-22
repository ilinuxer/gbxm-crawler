package zx.soft.gbxm.google.driver;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.google.api.GoogleSpider;

public class GoogleSpiderDriver {

	private static Logger logger = LoggerFactory.getLogger(GoogleSpiderDriver.class);

	public static void main(String[] args) throws IOException, GeneralSecurityException, InterruptedException {
		if (args.length == 0) {
			System.out.println("Usage:Driver<class-name>");
			System.exit(-1);
		}
		String[] leftArgs = new String[args.length - 1];
		System.arraycopy(args, 1, leftArgs, 0, leftArgs.length);
		switch (args[0]) {
		case "google":
			logger.info("google spider: ");
			GoogleSpider.main(leftArgs);
			break;
		default:
			return;
		}
	}

}
