package zx.soft.gbxm.twitter.dao;

import org.apache.ibatis.annotations.*;

import zx.soft.gbxm.twitter.domain.MonitorUser;
import zx.soft.gbxm.twitter.domain.Token;
import zx.soft.model.user.CurrentUserInfo;
import zx.soft.model.user.TwitterUser;

import java.util.List;

public interface TwitterDao {

	@Select("SELECT * FROM ${tablename} WHERE id=#{id}")
	public Token getToken(@Param("tablename") String tablename, @Param("id") long id);

	/**
	 * 获取Twitter Token列表
	 */
	@Select("SELECT `tokenkey`,`tokensecret`,`sinceId` FROM `twitterTokens`")
	public List<Token> getTwitterTokens();

	@Select("SELECT max(id) FROM ${tablename}")
	public long getTableCount(@Param("tablename") String tablename);

	@Select("SELECT `name` FROM ${tablename} WHERE `id` = #{id}")
	public String getScreenNameByUserId(@Param("tablename") String tablename, @Param("id") long id);

	@Update("update twitterTokens set sinceId=#{sinceId}  where id=#{id}")
	public void updateToken(@Param("sinceId") long sinceId, @Param("id") long id);

	//插入用户信息
	@Insert("INSERT `user_info_twitter`(`id`,`name`,`screen_name`,`profile_image_url`,`created_at`,"
			+ "`location`,`url`,`favourites_count`,`utc_offset`,`listed_count`,`followers_count`,"
			+ "`lang`,`description`,`verified`,`time_zone`,`statuses_count`,`friends_count`,`lasttime`) "
			+ "VALUES (#{id},#{name},#{screen_name},#{profile_image_url},#{created_at},"
			+ "#{location},#{url},#{favourites_count},#{utc_offset},#{listed_count},#{followers_count},"
			+ "#{lang},#{description},#{verified},#{time_zone},#{statuses_count},#{friends_count},now())")
	public void insertTwitterUser(TwitterUser twitterUser);

	//更新用户信息
	@Update("UPDATE `user_info_twitter` SET  screen_name=#{twitterUser.screen_name},"
			+ "favourites_count=#{twitterUser.favourites_count},listed_count=#{twitterUser.listed_count},"
			+ "followers_count=#{twitterUser.followers_count},description=#{twitterUser.description},"
			+ "verified=#{twitterUser.verified},statuses_count=#{twitterUser.statuses_count},"
			+ "friends_count=#{twitterUser.friends_count}  WHERE id=#{twitterUser.id}")
	public void updateTwitterUser(@Param("twitterUser") TwitterUser twitterUser);

	/**
	 * 获取新增用户信息列表
	 */
	@Select("SELECT `user_id` AS userId,`user_name` AS userName,`sns` FROM `current_user_info` WHERE `sns` = \"tw\"")
	public List<CurrentUserInfo> selectTwCurrentUser();

	/**
	 * 按照id删除新增用户信息
	 */
	@Delete("DELETE FROM `current_user_info` WHERE `user_id` = #{0}")
	public void deleteTwCurrentUser(String userId);

	/**
	 * 针对每个被监控用户更新since_id
	 */
	@Update("UPDATE `user_monitor_twitter` SET `since_id` = #{1} WHERE `screen_name`= #{0}")
	public void updateTwMonitor(String screenName,long sinceId);

	/**
	 * 针对每个用户获取上次since_id
	 */
	@Select("SELECT `since_id` FROM `user_monitor_twitter` WHERE `screen_name` = #{0}")
	public long getLastSinceId(String screenName);

	/**
	 * 获取监控用户的重数量
	 */
	@Select("SELECT COUNT(1) FROM `user_monitor_twitter`")
	public int getMonitorUserCount();


	/**
	 * 获取指定区域内的监控用户信息
	 */
	@Select("SELECT `user_id` AS userId ,`screen_name` AS screenName ,`since_id` AS sinceId FROM `user_monitor_twitter`")
	public List<MonitorUser> getMoitorUsers();


}
