package zx.soft.gbxm.google.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.google.domain.GooglePlusStatus;
import zx.soft.gbxm.google.domain.GoogleToken;
import zx.soft.gbxm.google.utils.MybatisConfig;
import zx.soft.model.user.CurrentUserInfo;
import zx.soft.utils.log.LogbackUtil;

public class GoogleDaoImpl {

	private static Logger logger = LoggerFactory.getLogger(GoogleDaoImpl.class);
	private static SqlSessionFactory sqlSessionFactory;

	public GoogleDaoImpl() {
		sqlSessionFactory = MybatisConfig.getSqlSessionFactory();
	}

	/**
	 * 获取所有google＋应用信息
	 */
	public List<GoogleToken> getGoogleTokens(){
		List<GoogleToken> result = new ArrayList<>();

		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			result = googleDao.getGoogleTokens();
		}
		return result;
	}
	/**
	 * 获取新增用户信息列表
	 */
	public List<CurrentUserInfo> getGpCurrentUser(){
		List<CurrentUserInfo> result;
		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			result = googleDao.getGpCurrentUser();
		}
		return result;
	}
	/**
	 * 根据用户id删除新增列表中的用户信息
	 */
	public void delGpCurrentUserById(String userId){
		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			googleDao.delGpCurrentUserById(userId);
		}

	}

	//获得表长度
	public int getTableCount(String tableName) {
		int count = 0;
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			count = googleDao.getTableCount(tableName);
		}
		return count;
	}

	//获取待查询的所有用户的id
	public List<String> getUserIdList(String tableName) {
		List<String> userIdList = new ArrayList<>();
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			userIdList = googleDao.getAllUserId(tableName);
		}
		return userIdList;
	}

	//判断指定用户id是否存在
	public boolean isExisted(String tableName, String userId) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			if (googleDao.getNameByUserId(tableName, userId) == 0) {
				return Boolean.FALSE;
			}
			return Boolean.TRUE;
		}
	}

	//根据用户Id获取上次更新时间
	public Timestamp getLastUpdateTimeByUserId(String tableName, String userId) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			return googleDao.getLastUpdateTimeByUserId(tableName, userId);
		}
	}

	//更新读取用户数据的时间记录
	public void updatedUserInfo(String userId, Timestamp lastUpdateTime) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			googleDao.updateUserInfo(userId, lastUpdateTime);
		}
	}

	public GoogleToken getGoogleTokenById(String tableName, int id) {
		GoogleToken token = null;
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			token = googleDao.getGoogleTokenById(tableName, id);
		}
		return token;
	}

	//插入获取的google+状态信息
	public void insertGooglePlusStatus(GooglePlusStatus googlePlusStatus) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			googleDao.insertGooglePlusStatus(googlePlusStatus);
		}
	}

	//插入list<GooglePlusStatus>信息
	public void insertGooglePlusListStatus(List<GooglePlusStatus> googlePlusStatuses) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			GoogleDao googleDao = sqlSession.getMapper(GoogleDao.class);
			for (GooglePlusStatus googlePlusStatus : googlePlusStatuses) {
				try {
					logger.info("insert google plus status to DB", googlePlusStatus.getId());
					googleDao.insertGooglePlusStatus(googlePlusStatus);
				} catch (Exception e) {
					if (LogbackUtil.expection2Str(e).contains("MysqlDataTruncation")) {
						try {
							googlePlusStatus.setTitle(new String(googlePlusStatus.getTitle().getBytes(), "GBK"));
							googleDao.insertGooglePlusStatus(googlePlusStatus);
						} catch (Exception e1) {
							logger.error("Exception:{}", LogbackUtil.expection2Str(e1));
						}
					} else {
						logger.error("Exception:{}", LogbackUtil.expection2Str(e));
					}
				}
			}
		}
	}
}
