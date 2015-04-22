package zx.soft.gbxm.facebook.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisConfig {

	private static Logger logger = LoggerFactory.getLogger(MybatisConfig.class);
	private static SqlSessionFactory sqlSessionFactory;

	static {
		try (InputStream inputStream = Resources.getResourceAsStream("mybatis-config.xml");) {
			sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

		} catch (IOException e) {
			logger.info("Mybatis Config  IOException");
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	public static SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}
}
