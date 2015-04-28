package zx.soft.gbxm.google.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by jimbo on 4/27/15.
 */
public class PostUrlConfig {
    private static Logger logger = LoggerFactory.getLogger(PostUrlConfig.class);

    public static Properties getProp(String fileName){
        Properties result = new Properties();
        try(InputStream inputStream = PostUrlConfig.class.getClassLoader().getResourceAsStream(fileName)){
            result.load(inputStream);
            return result;
        } catch (IOException e) {
            logger.error("wrong during reading " + fileName);
            throw new RuntimeException(e);
        }
    }
}
