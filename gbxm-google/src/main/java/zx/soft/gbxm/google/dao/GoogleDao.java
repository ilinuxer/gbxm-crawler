package zx.soft.gbxm.google.dao;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import zx.soft.gbxm.google.domain.GooglePlusStatus;
import zx.soft.gbxm.google.domain.GoogleToken;
import zx.soft.gbxm.google.domain.UserInfo;
import zx.soft.model.user.CurrentUserInfo;

public interface GoogleDao {

	@Select("SELECT max(id) FROM ${tableName}")
	public int getTableCount(@Param("tableName") String tableName);

	@Update("UPDATE `googleUserInfos` SET `lastUpdateTime`=#{lastUpdateTime} WHERE `userId`=#{userId}")
	public void updateUserInfo(@Param("userId") String userId, @Param("lastUpdateTime") Timestamp lastUpdateTime);

	@Select("SELECT `id` FROM ${tableName} WHERE `userId`=#{userId}")
	public long getNameByUserId(@Param("tableName") String tableName, @Param("userId") String userId);

	@Select("SELECT `lastUpdateTime` FROM ${tableName} WHERE `userId`=#{userId}")
	public Timestamp getLastUpdateTimeByUserId(@Param("tableName") String tableName, @Param("userId") String userId);

	@Select("SELECT `userId` from ${tableName}")
	public List<String> getAllUserId(@Param("tableName") String tableName);

	@Select("SELECT  `app_name`,`client_id`,`client_secret` FROM ${tableName} WHERE id=#{id}")
	public GoogleToken getGoogleTokenById(@Param("tableName") String tableName, @Param("id") int id);

	/**
	 * 获取所有google＋应用信息
	 */
	@Select("SELECT `app_name`,`client_id`,`client_secret` FROM `gplusApps`")
	public List<GoogleToken> getGoogleTokens();

	/**
	 * 获取分页内的监控用户信息
	 */
	@Select("SELECT `userId`,`userName`,`lastUpdateTime` FROM `googleUserInfos` LIMIT #{0},#{1}")
	public List<UserInfo> getUsers(int start,int size);

	/**
	 * 获取跟踪推文信息列表
	 */
	@Select("SELECT `status_id` FROM `status_monitor` WHERE `status_sns` = \"gp\"")
	public List<String> getGpFocusStatuses();

	/**
	 * 获取总的监控用户数量
	 */
	@Select("SELECT COUNT(1) FROM `googleUserInfos`")
	public int getUserCount();


	/**
	 * 获取新增用户信息列表
	 */
	@Select("SELECT `user_id` AS userId,`user_name` AS userName,`sns` FROM `current_user_info` WHERE `sns` = \"gp\"")
	public List<CurrentUserInfo> getGpCurrentUser();

	/**
	 * 根据用户id删除新增列表中的用户信息
	 */
	@Select("DELETE FROM `current_user_info` WHERE `user_id` = #{0} AND `sns` = \"gp\"")
	public void delGpCurrentUserById(String userId);

	//插入google+状态信息到数据库
	@Insert("INSERT `status_info_googleplus`(`id`,`title`,`published`,`updated`,`url`,"
			+ "`actor_id`,`actor_display_name`,`object_id`,`object_actor_id`,`object_actor_display_name`,"
			+ "`object_original_content`,`object_url`,`object_replies_totalitems`,`object_plusoners_totalitems`,"
			+ "`object_resharers_totalitems`,`object_attachments_content`,`annotation`,`latitude`,`longitude`,`place_name`,`lasttime`) "
			+ "VALUES (#{id},#{title},#{published},#{updated},#{url},#{actor_id},#{actor_display_name},"
			+ "#{object_id},#{object_actor_id},#{object_actor_display_name},#{object_original_content},"
			+ "#{object_url},#{object_replies_totalitems},#{object_plusoners_totalitems},#{object_resharers_totalitems},"
			+ "#{object_attachments_content},#{annotation},#{latitude},#{longitude},#{place_name},now())")
	public void insertGooglePlusStatus(GooglePlusStatus googlePlusStatus);
}
