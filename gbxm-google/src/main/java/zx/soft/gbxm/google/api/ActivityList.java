package zx.soft.gbxm.google.api;

import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.google.common.ConstUtils;
import zx.soft.gbxm.google.common.Convert;
import zx.soft.gbxm.google.domain.GooglePlusStatus;

import com.google.api.services.plus.Plus;
import com.google.api.services.plus.Plus.Activities.List;
import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.ActivityFeed;

/**
 * 获取状态信息列表类
 * @author fgq
 *
 */
public class ActivityList {

	private static Logger logger = LoggerFactory.getLogger(ActivityList.class);

	private Plus plus;

	public ActivityList(Plus plus) {
		this.plus = plus;
	}

	/**
	 * 根据用户id，获取指定截止时间lastUpdatedTime前的状态信息
	 * @param userId
	 * @param lastUpdatedTime
	 * @return
	 * @throws IOException
	 */
	public ArrayList<GooglePlusStatus> getActivitiesByUserId(String userId, long lastUpdatedTime) throws IOException {
		ArrayList<GooglePlusStatus> googlePlusStatuses = new ArrayList<>();
		List listActivities = plus.activities().list(userId, "public");
		listActivities.setMaxResults(100L);//最大可设置为100
		listActivities.setFields("nextPageToken,items(id,title,published,updated,url,actor/id,actor/displayName,"
				+ "object/id,object/actor/id,object/actor/displayName,object/originalContent,object/url,"
				+ "object/replies/totalItems,object/plusoners/totalItems,object/resharers/totalItems,"
				+ "object/attachments,annotation,geocode,placeName)");
		ActivityFeed feed = listActivities.execute();
		int currentPageNumber = 0;
		while (feed.getItems() != null && !feed.getItems().isEmpty() && ++currentPageNumber < ConstUtils.PAGE_COUNT) {
			logger.info("currentPageCount=" + currentPageNumber);
			if (feed.getItems().get(0).getPublished().getValue() <= lastUpdatedTime) {
				break;
			}
			for (Activity activity : feed.getItems()) {
				logger.info(activity.getId());
				googlePlusStatuses.add(Convert.convertActivity2GPS(activity));
			}
			String nextPageToken = feed.getNextPageToken();
			if (nextPageToken == null) {
				break;
			}
			listActivities.setPageToken(nextPageToken);
			feed = listActivities.execute();
		}
		return googlePlusStatuses;
	}

	public Plus getPlus() {
		return plus;
	}

	public void setPlus(Plus plus) {
		this.plus = plus;
	}

}
