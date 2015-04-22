package zx.soft.gbxm.twitter.dao;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
		Token token = null;
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
		List<Token> result = new LinkedList<>();
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
	 * @param name
	 */
	public void insertTwitterUser(TwitterUser twitterUser) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			twitterDao.insertTwitterUser(twitterUser);
		}
	}

	/**
	 * 更新用户信息到数据库user_info_twitter
	 * @param userInfoes
	 */
	public void updateTwitterUser(TwitterUser twitterUser) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession()) {
			TwitterDao twitterDao = sqlSession.getMapper(TwitterDao.class);
			twitterDao.updateTwitterUser(twitterUser);
		}
	}

	/**
	 * 更新Token sinceId到数据库twitterTokens
	 * @param userInfoes
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

}
