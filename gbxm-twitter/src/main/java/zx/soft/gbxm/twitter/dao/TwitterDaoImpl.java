package zx.soft.gbxm.twitter.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.twitter.domain.MonitorUser;
import zx.soft.gbxm.twitter.domain.Token;
import zx.soft.gbxm.twitter.utils.MybatisConfig;
import zx.soft.model.user.CurrentUserInfo;
import zx.soft.model.user.TwitterUser;

import java.util.LinkedList;
import java.util.List;

public class TwitterDaoImpl {

	private Logger logger = LoggerFactory.getLogger(TwitterDaoImpl.class);
	private static SqlSessionFactory sqlSessionFactory;

	public TwitterDaoImpl() {
		sqlSessionFactory = MybatisConfig.getSqlSessionFactory();
	}

	//从数据库表中获取指定id的Token;
	public Token getToken(String tableName, long id) {
		Token token;
		try (SqlSession session = sqlSessionFactory.openSession()) {
			TwitterDao dao = session.getMapper(TwitterDao.class);
			token = dao.getToken(tableName, id);
			logger.info("get token id = " + token.toString());
		}
		return token;
	}

	/**
	 * 获取Twitter Token列表
	 */
	public List<Token> getTwitterTokens(){
		List<Token> result;
		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			TwitterDao dao = sqlSession.getMapper(TwitterDao.class);
			result = dao.getTwitterTokens();
		}
		return result;
	}

	//获取表的长度
	public long getTableCount(String tablename) {
		long count = 0;
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			TwitterDao dao = sqlSession.getMapper(TwitterDao.class);
			count = dao.getTableCount(tablename);
		}
		return count;
	}

	/**
	 * 判断用户存在与否
	 */
	public boolean isUserExisted(String tablename, long userId) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			TwitterDao dao = sqlSession.getMapper(TwitterDao.class);
			if (dao.getScreenNameByUserId(tablename, userId) == null) {
				return Boolean.FALSE;
			} else {
				return Boolean.TRUE;
			}
		}
	}

	/**
	 * 插入用户信息到数据库user_info_twitter
	 */
	public void insertTwitterUser(TwitterUser twitterUser) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			twitterDao.insertTwitterUser(twitterUser);
		}
	}

	/**
	 * 更新用户信息到数据库user_info_twitter
	 */
	public void updateTwitterUser(TwitterUser twitterUser) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			twitterDao.updateTwitterUser(twitterUser);
		}
	}

	/**
	 * 更新Token sinceId到数据库twitterTokens
	 */
	public void updateSinceId(long sinceId, int id) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			twitterDao.updateToken(sinceId, id);
			logger.info("update sinceId:" + sinceId + "where TokenId=" + id + "  successful");
		}
	}

	/**
	 * 获取新增用户信息列表
	 */
	public List<CurrentUserInfo> selectTwCurrentUser(){
		List<CurrentUserInfo> result = new LinkedList<>();
		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			result = twitterDao.selectTwCurrentUser();
		}
		return result;
	}

	/**
	 * 按照id删除新增用户信息
	 */
	public void deleteTwCurrentUser(String userId){

		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			twitterDao.deleteTwCurrentUser(userId);
		}
	}

	/**
	 * 针对每个被监控用户更新since_id
	 */
	public void updateTwMonitor(String screenName,long sinceId){
		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			twitterDao.updateTwMonitor(screenName, sinceId);
		}
	}
	/**
	 * 针对每个用户获取上次since_id
	 */
	public long getLastSinceId(String screenName){
		Long result;
		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			result = twitterDao.getLastSinceId(screenName);
			if(result==null){
				result = 1L;
			}
			return result;
		}
	}

	/**
	 * 获取监控用户的重数量
	 */
	public int getMonitorUserCount(){
		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			return twitterDao.getMonitorUserCount();
		}
	}

	/**
	 * 获取指定区间内监控用户
	 */
	public List<MonitorUser> getMoitorUsers(){
		List<MonitorUser> result;
		try(SqlSession sqlSession = sqlSessionFactory.openSession()){
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			result = twitterDao.getMoitorUsers();
		}
		return result;
	}
}
