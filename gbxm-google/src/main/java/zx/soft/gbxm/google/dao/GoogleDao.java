package zx.soft.gbxm.google.dao;

import java.sql.Timestamp;
import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import zx.soft.gbxm.google.domain.GooglePlusStatus;
import zx.soft.gbxm.google.domain.GoogleToken;

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
