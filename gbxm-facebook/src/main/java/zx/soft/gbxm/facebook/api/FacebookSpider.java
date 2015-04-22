package zx.soft.gbxm.facebook.api;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.social.ApiException;
import org.springframework.social.facebook.api.PagedList;
import org.springframework.social.facebook.api.PagingParameters;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.impl.FacebookTemplate;

import zx.soft.gbxm.facebook.common.ConstUtils;
import zx.soft.gbxm.facebook.common.Convert;
import zx.soft.gbxm.facebook.common.Refresh;
import zx.soft.gbxm.facebook.common.RestletPost;
import zx.soft.gbxm.facebook.dao.FBDaoImpl;
import zx.soft.gbxm.facebook.domain.FacebookStatus;
import zx.soft.gbxm.facebook.domain.FacebookUser;
import zx.soft.gbxm.facebook.domain.PostData;
import zx.soft.gbxm.facebook.domain.RecordInfo;
import zx.soft.utils.log.LogbackUtil;

public class FacebookSpider {

	private static Logger logger = LoggerFactory.getLogger(FacebookSpider.class);
	private FBDaoImpl fbDaoImpl;

	public FacebookSpider() {
		fbDaoImpl = new FBDaoImpl();
	}

	public int run(String token, Long since) throws ApiException {
		int length = 0;
		try {
			FacebookTemplate facebook = new FacebookTemplate(token, "");
			Long until = System.currentTimeMillis() / 1000L;
			PagedList<Post> posts = facebook.feedOperations().getHomeFeed(
					new PagingParameters(ConstUtils.limit, 0, since, until));
			List<RecordInfo> records = new ArrayList<>();
			List<FacebookStatus> facebookStatuses = new ArrayList<>();
			if (posts.size() > 0) {
				for (Post post : posts) {
					FacebookStatus facebookStatus = Convert.convertPost2FacebookStatus(post);
					String userId = post.getFrom().getId();
					FacebookUser facebookUser = Convert.convertFacebookProfile2FacebookUser(facebook.userOperations()
							.getUserProfile(userId));
					if (fbDaoImpl.isExisted(userId)) {
						//更新用户信息
						fbDaoImpl.updateFacebookUser(facebookUser);
					} else {
						//插入用户信息
						fbDaoImpl.insertFacebookUser(facebookUser);
					}
					facebookStatuses.add(facebookStatus);
					RecordInfo record = Convert.convertFacebookStatus2RecordInfo(facebookStatus, until * 1000);
					records.add(record);
				}
				length = posts.size();
				PostData postData = new PostData();
				postData.setNum(length);
				postData.setRecords(records);
				RestletPost.post(postData);
				fbDaoImpl.insertFacebookStatus(facebookStatuses);
				fbDaoImpl.updateSince(until, token);
			}
		} catch (Exception e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			logger.info("token 过期，请重新授权该用户，并更新数据库中token");
		}
		return length;
	}

	public static void main(String[] args) throws InterruptedException {

		FBDaoImpl fbDaoImpl = new FBDaoImpl();
		FacebookSpider spider = new FacebookSpider();
		int i = 1;
		String nextToken = null;
		Long since = null;
		int length;
		while (true) {
			nextToken = Refresh.getNextToken(i);
			since = Refresh.getNextSince(i);
			logger.info("token id=" + i + ";next token=" + nextToken + ";since=" + since);
			try {
				length = spider.run(nextToken, since);
				logger.info("length=" + length);
				i++;
				if (i > fbDaoImpl.getTableLength("fb_token")) {
					i = 1;
					logger.info("sleep 15 min");
					Thread.sleep(15 * 60 * 1000);
				}
			} catch (ApiException e) {
				//降低请求数量error code=-3;
				if (e.getMessage().contains("reduce the amount of data")) {
					int originLimit = ConstUtils.getLimit();
					int nowLimit = originLimit - 20;
					if (nowLimit > 0) {
						ConstUtils.setLimit(nowLimit);
					} else {
						ConstUtils.setLimit(20);
					}
					length = spider.run(nextToken, since);
					logger.info("length=" + length);
					i++;
					if (i > fbDaoImpl.getTableLength("fb_token")) {
						i = 1;
						System.out.println("sleep 15 min");
						Thread.sleep(15 * 60 * 1000);
					}
				}
				//an unexpected error has occurred.Please retry your request later或者发生未知错误error code=1
				if (e.getMessage().contains("Please retry your request later")
						| e.getMessage().contains("An unknown error occurred")) {
					Thread.sleep(30_1000L);
					length = spider.run(nextToken, since);
					System.out.println("length=" + length);
					i++;
					if (i > fbDaoImpl.getTableLength("fb_token")) {
						i = 1;
					}
				}
			}
		}
	}
}
