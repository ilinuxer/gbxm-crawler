package zx.soft.gbxm.facebook.dao;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import zx.soft.gbxm.facebook.domain.FacebookStatus;
import zx.soft.gbxm.facebook.domain.FacebookUser;

public interface FBDao {

	@Select("SELECT  `token` FROM `fb_token` WHERE id=#{id}")
	public String getToken(@Param("id") int id);

	@Select("SELECT  `since` FROM `fb_token` WHERE id=#{id}")
	public long getSince(@Param("id") int id);

	@Select("SELECT max(id) FROM ${tablename}")
	public int getTableLength(@Param("tablename") String tablename);

	@Insert("INSERT `status_info_facebook` (`id`,`from_id`,`from_name`,`message`,`link`,"
			+ "`createdTime`,`updatedTime`,`sharesCount`,`commentsCount`,`likesCount`,`lasttime`) " + "VALUES "
			+ "(#{id},#{from_id},#{from_name},#{message},#{link},#{createdTime},#{updatedTime},#{sharesCount}"
			+ ",#{commentsCount},#{likesCount},now())")
	public void insertFacebookStatus(FacebookStatus facebookStatus);

	@Update("UPDATE `fb_token` SET `since` =#{since} WHERE `token`=#{token}")
	public void updateSince(@Param("since") long since, @Param("token") String token);

	@Insert("Insert `user_info_facebook`(`id`,`username`,`name`,`gender`,"
			+ "`locale`,`link`,`website`,`email`,`timezone`,`updatedTime`,`verified`"
			+ ",`about`,`birthday`,`locaton`,`inspirationalPeopleCount`,`languages`,"
			+ "`political`,`quotes`,`lasttime`) VALUES (#{id},#{username},#{name},"
			+ "#{gender},#{locale},#{link},#{website},#{email},#{timezone},#{updatedTime},"
			+ "#{verified},#{about},#{birthday},#{locaton},#{inspirationalPeopleCount},"
			+ "#{languages},#{political},#{quotes},now())")
	public void insertFacebookUser(FacebookUser facebookUser);

	@Select("Select `name` FROM  `user_info_facebook` WHERE `id`=#{id} ")
	public String getNameById(@Param("id") String id);

	@Update("UPDATE `user_info_facebook` SET  `username`=#{facebookUser.username},"
			+ "`name`=#{facebookUser.name},`gender`=#{facebookUser.gender},"
			+ "`locale`=#{facebookUser.locale},`website`=#{facebookUser.website},"
			+ "`email`=#{facebookUser.email},`timezone`=#{facebookUser.timezone},"
			+ "`updatedTime`=#{facebookUser.updatedTime},"
			+ "`verified`=#{facebookUser.verified},`about`=#{facebookUser.about},"
			+ "`birthday`=#{facebookUser.birthday},"
			+ "`locaton`=#{facebookUser.locaton},`inspirationalPeopleCount`=#{facebookUser.inspirationalPeopleCount},"
			+ "`languages`=#{facebookUser.languages},`political`=#{facebookUser.political},"
			+ "`quotes`=#{facebookUser.quotes}  ,`lasttime`=now() WHERE `id`=#{facebookUser.id}")
	public void updateFacebookUser(@Param("facebookUser") FacebookUser facebookUser);
}
