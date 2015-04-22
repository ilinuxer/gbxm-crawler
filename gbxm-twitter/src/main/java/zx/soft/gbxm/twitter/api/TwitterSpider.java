package zx.soft.gbxm.twitter.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.restlet.Response;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import zx.soft.gbxm.twitter.dao.TwitterDaoImpl;
import zx.soft.gbxm.twitter.domain.PostData;
import zx.soft.gbxm.twitter.domain.RecordInfo;
import zx.soft.gbxm.twitter.domain.Token;
import zx.soft.model.user.TwitterUser;
import zx.soft.utils.checksum.CheckSumUtils;
import zx.soft.utils.config.ConfigUtil;
import zx.soft.utils.json.JsonUtils;

public class TwitterSpider {

	private static Logger logger = LoggerFactory.getLogger(TwitterSpider.class);
	private static TwitterDaoImpl twitterDaoImpl = new TwitterDaoImpl();
	private static final String URL = "http://36.7.150.150:18900/persist";
	private final ClientResource clientResource = new ClientResource(URL);
	static int i = 0;

	public int run(Follows follows) throws InterruptedException, TwitterException {

		List<TwitterStatus> twitterStatuses = new ArrayList<>();
		PostData postData = new PostData();
		List<RecordInfo> records = new ArrayList<>();
		List<Status> statuses = follows.getHomeTimeLine();
		if (statuses.size() != 0) {
			for (Status status : statuses) {
				TwitterStatus twitterStatus = new TwitterStatus();

				twitterStatus.setId(status.getId());
				twitterStatus.setUser_id(status.getUser().getId());
				twitterStatus.setScreen_name(status.getUser().getName());

				if (status.getGeoLocation() != null) {
					twitterStatus.setLatitude(status.getGeoLocation().getLatitude());
					twitterStatus.setLongitude(status.getGeoLocation().getLongitude());
				}

				twitterStatus.setCreated_at(status.getCreatedAt().toString());
				twitterStatus.setText(status.getText());
				twitterStatus.setRetweet_count(status.getRetweetCount());
				twitterStatus.setPossibly_sensitive(status.isPossiblySensitive());
				if (status.getPlace() != null) {
					twitterStatus.setLocation(status.getPlace().getFullName());
				}
				if (status.getRetweetedStatus() != null) {
					twitterStatus.setRetweeted_id(status.getRetweetedStatus().getId());
					twitterStatus.setRetweeted_user_id(status.getRetweetedStatus().getUser().getId());
					twitterStatus.setRetweeted_screen_name(status.getRetweetedStatus().getUser().getScreenName());
				}
				twitterStatuses.add(twitterStatus);

				RecordInfo record = new RecordInfo();
				String url = "https://twitter.com/" + status.getUser().getScreenName() + "/status/"
						+ twitterStatus.getId();
				record.setId(CheckSumUtils.getMD5(url).toUpperCase());//状态id,用户id进行MD5加密
				record.setMid(Long.toString(twitterStatus.getId()));//主id
				record.setUsername(twitterStatus.getUser_id() + ""); // uid
				record.setNickname(twitterStatus.getScreen_name()); //用户昵称
				record.setOriginal_id(Long.toString(twitterStatus.getRetweeted_id())); //原创记录id
				record.setOriginal_uid(Long.toString(twitterStatus.getRetweeted_user_id())); //原创用户id
				record.setOriginal_name(twitterStatus.getRetweeted_screen_name()); //原创用户昵称
				record.setUrl(url);//url
				record.setContent(twitterStatus.getText()); //该记录内容
				record.setRepost_count(twitterStatus.getRetweet_count());//转发数
				record.setTimestamp(status.getCreatedAt().getTime());//该记录发布时间
				long currentTime = System.currentTimeMillis();
				record.setLasttime(currentTime);//lasttime
				record.setUpdate_time(currentTime); //update_time
				record.setFirst_time(currentTime); //first_time
				record.setLocation(twitterStatus.getLocation());//该记录发布的地理位置信息
				records.add(record);

				TwitterUser twitterUser = new TwitterUser();
				User user = status.getUser();
				twitterUser.setId(user.getId());
				twitterUser.setName(user.getName());
				if (user.getScreenName() != null) {
					twitterUser.setScreen_name(user.getScreenName());
				}
				if (user.getProfileImageURL() != null) {
					twitterUser.setProfile_image_url(user.getProfileImageURL());
				}
				twitterUser.setCreated_at(user.getCreatedAt().toString());
				twitterUser.setLocation(user.getLocation());
				if (user.getURL() != null) {
					twitterUser.setUrl(user.getURL());
				}
				twitterUser.setFavourites_count(user.getFavouritesCount());
				twitterUser.setUtc_offset(user.getUtcOffset());
				twitterUser.setListed_count(user.getListedCount());
				twitterUser.setFollowers_count(user.getFollowersCount());
				twitterUser.setLang(user.getLang());
				if (user.getDescription() != null) {
					twitterUser.setDescription(user.getDescription());
				}
				twitterUser.setVerified(user.isVerified());
				if (user.getTimeZone() != null) {
					twitterUser.setTime_zone(user.getTimeZone());
				}
				twitterUser.setStatuses_count(user.getStatusesCount());
				twitterUser.setFriends_count(user.getFriendsCount());

				if (twitterDaoImpl.isUserExisted("user_info_twitter", twitterUser.getId())) {
					twitterDaoImpl.updateTwitterUser(twitterUser);
				} else {
					twitterDaoImpl.insertTwitterUser(twitterUser);
				}
			}
			statuses = null;
			postData.setNum(records.size());
			logger.info("post data number=" + records.size());
			postData.setRecords(records);
			Representation entity = new StringRepresentation(JsonUtils.toJsonWithoutPretty(postData));
			entity.setMediaType(MediaType.APPLICATION_JSON);
			try {
				Representation representation = clientResource.post(entity);
				logger.info("post return " + representation.toString());
				Response response = clientResource.getResponse();
				try {
					logger.info(response.getEntity().getText());
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (ResourceException e) {
				logger.error("post data to solr error ");
				logger.error(e.getMessage());
				e.printStackTrace();
			}
			//同时更新sinceId
			twitterDaoImpl.updateSinceId(twitterStatuses.get(0).getId(), i);
		}
		return twitterStatuses.size();
	}

	public static Token getTokenAndSinceId() {
		if (i == getTableCount()) {
			i = 0;
		}
		return twitterDaoImpl.getToken("twitterTokens", ++i);
	}

	/**
	 * 获得token表长度
	 * @return
	 */
	public static long getTableCount() {
		return twitterDaoImpl.getTableCount("twitterTokens");
	}

	public static void main(String[] args) throws InterruptedException, IOException {
		Twitter twitter = new TwitterFactory().getInstance();
		Properties prop = ConfigUtil.getProps("oauthconsumer.properties");
		twitter.setOAuthConsumer(prop.getProperty("consumerKey"), prop.getProperty("consumerSecret"));
		Follows follows = new Follows(twitter);
		Token instance = getTokenAndSinceId();
		follows.setAccessToken(new AccessToken(instance.getTokenkey(), instance.getTokensecret()));
		follows.setSinceId(instance.getSinceId());
		TwitterSpider twitterSpider = new TwitterSpider();
		int[] diffLength = { 0, 0 };
		long sleepTime = 0L;
		int j = 0;
		int k = 0;
		while (true) {
			diffLength[0] = diffLength[1];
			try {
				diffLength[1] = twitterSpider.run(follows);
				k = 0;
				if (diffLength[1] == 0) {
					j++;
					instance = getTokenAndSinceId();
					AccessToken changeToken = new AccessToken(instance.getTokenkey(), instance.getTokensecret());
					follows.setAccessToken(changeToken);
					follows.setSinceId(instance.getSinceId());
					diffLength[0] = 0;
					diffLength[1] = 0;
				} else {
					j = 0;
				}
				if (j == getTableCount()) {
					logger.info("已获得所有用户的状态更新，进行睡眠");
					sleepTime = (long) (1000 - diffLength[0]) * 1000;
					Thread.sleep(sleepTime);
				}
			} catch (TwitterException e) {
				logger.info("token 过期，异常发生，更换token");
				k++;
				if (k == getTableCount()) {
					logger.info("所有token均过期，休眠15min...");
					Thread.sleep(15 * 60 * 1000);
				}
				//从数据库重新获取一个授权用户token并抓取该授权用户所关注帐号的用户数据
				instance = getTokenAndSinceId();
				AccessToken changeToken = new AccessToken(instance.getTokenkey(), instance.getTokensecret());
				follows.setAccessToken(changeToken);
				follows.setSinceId(instance.getSinceId());
				diffLength[0] = 0;
				diffLength[1] = 0;
			}
		}
	}
}
