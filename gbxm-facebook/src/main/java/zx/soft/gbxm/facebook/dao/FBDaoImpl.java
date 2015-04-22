package zx.soft.gbxm.facebook.dao;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.facebook.domain.FacebookStatus;
import zx.soft.gbxm.facebook.domain.FacebookUser;
import zx.soft.gbxm.facebook.utils.MybatisConfig;
import zx.soft.utils.log.LogbackUtil;

public class FBDaoImpl {

	private static Logger logger = LoggerFactory.getLogger(FBDaoImpl.class);

	private SqlSessionFactory sqlSessionFactory;

	public FBDaoImpl() {
		sqlSessionFactory = MybatisConfig.getSqlSessionFactory();
	}

	/**
	 * 获得facebook表中指定id的facebook token
	 * @param tablename
	 * @param id
	 * @return
	 */
	public String getToken(int id) {
		String token = null;
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			FBDao fbDao = sqlSession.getMapper(FBDao.class);
			token = fbDao.getToken(id);
		}
		return token;
	}

	/**
	 * 获得指定id token的since
	 * @param tablename
	 * @param id
	 * @return
	 */
	public long getSince(int id) {
		long since;
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			FBDao fbDao = sqlSession.getMapper(FBDao.class);
			since = fbDao.getSince(id);
		}
		return since;
	}

	/**
	 * 更新sinceId
	 * @param since
	 * @param token
	 */
	public void updateSince(long since, String token) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			FBDao fbDao = sqlSession.getMapper(FBDao.class);
			fbDao.updateSince(since, token);
		}
	}

	/**
	 * 获取表长度
	 * @param tablename
	 * @return
	 */
	public int getTableLength(String tablename) {
		int length = 0;
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			FBDao fbDao = sqlSession.getMapper(FBDao.class);
			length = fbDao.getTableLength(tablename);
		}
		return length;
	}

	/**
	 * 插入facebook状态信息到数据库
	 * @param facebookStatuses
	 * @throws UnsupportedEncodingException
	 */
	public void insertFacebookStatus(List<FacebookStatus> facebookStatuses) throws UnsupportedEncodingException {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			FBDao fbDao = sqlSession.getMapper(FBDao.class);
			for (FacebookStatus facebookStatus : facebookStatuses) {
				try {
					fbDao.insertFacebookStatus(facebookStatus);
					logger.info("insert " + facebookStatus.getId() + " to db succeed.");
				} catch (Exception e) {
					logger.info(e.getMessage());
					if (e.getMessage().contains("incorrect string value")) {
						facebookStatus.setMessage("此信息不能正常显示");
						fbDao.insertFacebookStatus(facebookStatus);
						logger.info("insert " + facebookStatus.getId() + " to db succeed.");
					}
					if (e.getMessage().contains("Dup")) {
						//
					}
				}
			}
		}
	}

	//插入用户信息
	public void insertFacebookUser(FacebookUser facebookUser) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			FBDao fbDao = sqlSession.getMapper(FBDao.class);
			fbDao.insertFacebookUser(facebookUser);
		} catch (Exception e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
	}

	//判断指定id的用户是否存在
	public boolean isExisted(String id) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			FBDao fbDao = sqlSession.getMapper(FBDao.class);
			if (fbDao.getNameById(id) != null) {
				return true;
			}
			return false;
		}
	}

	//更新用户信息
	public void updateFacebookUser(FacebookUser facebookUser) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			FBDao fbDao = sqlSession.getMapper(FBDao.class);
			fbDao.updateFacebookUser(facebookUser);
		} catch (Exception e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
	}
}
