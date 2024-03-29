package zx.soft.gbxm.google.common;

import com.google.api.services.plus.model.Comment;
import zx.soft.gbxm.google.domain.GooglePlusStatus;
import zx.soft.gbxm.google.domain.RecordInfo;
import zx.soft.utils.checksum.CheckSumUtils;
import zx.soft.utils.time.TimeUtils;

import com.google.api.services.plus.model.Activity;
import com.google.api.services.plus.model.Activity.PlusObject.Attachments;

public class Convert {

	/**
	 * 将activity对象转换为GooglePlusStatus(google的状态信息)
	 * @param activity
	 * @return
	 */
	public static GooglePlusStatus convertActivity2GPS(Activity activity) {
		GooglePlusStatus googlePlusStatus = new GooglePlusStatus();
		googlePlusStatus.setId(activity.getId());

		if (activity.getTitle() != null) {
			googlePlusStatus.setTitle(activity.getTitle());
		}
		googlePlusStatus.setPublished(activity.getPublished().toStringRfc3339());
		if (activity.getUpdated() != null) {
			googlePlusStatus.setUpdated(activity.getUpdated().toStringRfc3339());
		}
		googlePlusStatus.setUrl(activity.getUrl());
		googlePlusStatus.setActor_id(activity.getActor().getId());
		googlePlusStatus.setActor_display_name(activity.getActor().getDisplayName());
		if (activity.getObject() != null && activity.getObject().getId() != null
				&& activity.getObject().getUrl() != null) {
			googlePlusStatus.setObject_id(activity.getObject().getId());
			googlePlusStatus.setObject_url(activity.getObject().getUrl());
		}
		if (activity.getObject().getActor() != null) {
			googlePlusStatus.setObject_actor_id(activity.getObject().getActor().getId());
			googlePlusStatus.setObject_actor_display_name(activity.getObject().getActor().getDisplayName());
		}
		if (activity.getObject().getOriginalContent() != null || activity.getObject().getContent() != null) {
			googlePlusStatus.setObject_original_content(activity.getObject().getContent() + " "
					+ activity.getObject().getOriginalContent());
		}
		if (activity.getObject().getReplies() != null) {
			googlePlusStatus.setObject_replies_totalitems(activity.getObject().getReplies().getTotalItems().intValue());
		}
		if (activity.getObject().getPlusoners() != null) {
			googlePlusStatus.setObject_plusoners_totalitems(activity.getObject().getPlusoners().getTotalItems()
					.intValue());
		}
		if (activity.getObject().getResharers() != null) {
			googlePlusStatus.setObject_resharers_totalitems(activity.getObject().getResharers().getTotalItems()
					.intValue());
		}

		if (activity.getObject().getAttachments() != null) {
			String content = "";
			String attachmentUrl = "";
			java.util.List<Attachments> attachments = activity.getObject().getAttachments();
			for (Attachments attachment : attachments) {
				content = content + "  " + attachment.getContent();
				attachmentUrl = attachmentUrl +" , "+ attachment.getUrl();

			}
			googlePlusStatus.setObject_attachments_content(content);
			googlePlusStatus.setAttachment_url(attachmentUrl);
		}
		if (activity.getAnnotation() != null) {
			googlePlusStatus.setAnnotation(activity.getAnnotation());
		}
		if (activity.getGeocode() != null) {
			googlePlusStatus.setLatitude(Double.parseDouble(activity.getGeocode().split(" ")[0]));
			googlePlusStatus.setLongitude(Double.parseDouble(activity.getGeocode().split(" ")[1]));
		}
		if (activity.getPlaceName() != null) {
			googlePlusStatus.setPlace_name(activity.getPlaceName());
		}
		return googlePlusStatus;
	}

	/**
	 * GooglePlusStatus--> recordInfo
	 * @param googlePlusStatus
	 * @param currentTime
	 * @return
	 */
	public static RecordInfo convertGPS2Record(GooglePlusStatus googlePlusStatus, long currentTime) {
		currentTime = zx.soft.gbxm.google.timeutils.TimeUtils.exchangeTime(currentTime);
		RecordInfo record = new RecordInfo();
		record.setId(CheckSumUtils.getMD5(googlePlusStatus.getUrl()).toUpperCase());
		record.setMid(googlePlusStatus.getId());
		record.setUsername(googlePlusStatus.getActor_id());
		record.setNickname(googlePlusStatus.getActor_display_name());
		record.setOriginal_id(googlePlusStatus.getObject_id());
		record.setOriginal_uid(googlePlusStatus.getObject_actor_id());
		record.setOriginal_name(googlePlusStatus.getObject_actor_display_name());
		record.setOriginal_url(googlePlusStatus.getObject_url());
		record.setUrl(googlePlusStatus.getUrl());
		record.setTitle(googlePlusStatus.getTitle());
		record.setContent(googlePlusStatus.getTitle() + " " + googlePlusStatus.getObject_original_content() + " "
				+ googlePlusStatus.getAnnotation() + " " + googlePlusStatus.getObject_attachments_content());
//		record.setComment_count(googlePlusStatus.getObject_replies_totalitems());
//		评论数置零
		record.setPic_url(googlePlusStatus.getAttachment_url());
		record.setComment_count(0);
		record.setRepost_count(googlePlusStatus.getObject_resharers_totalitems());
		record.setAttitude_count(googlePlusStatus.getObject_plusoners_totalitems());
		record.setTimestamp(zx.soft.gbxm.google.timeutils.TimeUtils.exchangeTime(TimeUtils.transTwitterTimeLong(googlePlusStatus.getPublished())));
		record.setLasttime(currentTime);
		record.setFirst_time(currentTime);
		record.setUpdate_time(zx.soft.gbxm.google.timeutils.TimeUtils.exchangeTime(TimeUtils.transTwitterTimeLong(googlePlusStatus.getUpdated())));
		record.setLocation(googlePlusStatus.getPlace_name());
		record.setGeo(googlePlusStatus.getLatitude() + " " + googlePlusStatus.getLongitude());
		return record;
	}

	/**
	 * 将评论信息转化为接口可接受的信息
	 * @param comment
	 * @return
	 */
	public static RecordInfo convertComment2Record(Comment comment,long currentTime){
		currentTime = zx.soft.gbxm.google.timeutils.TimeUtils.exchangeTime(currentTime);

		RecordInfo record = new RecordInfo();
		record.setId(CheckSumUtils.getMD5(comment.getSelfLink()).toUpperCase());
		record.setMid(comment.getId());
		record.setNickname(comment.getActor().getDisplayName());
		record.setUsername(comment.getActor().getId());
		record.setHome_url(comment.getActor().getUrl());
		record.setOriginal_id(comment.getInReplyTo().get(0).getId());
		record.setOriginal_url(comment.getInReplyTo().get(0).getUrl());
		record.setContent(comment.getObject().getContent());
		record.setAttitude_count(Integer.parseInt(comment.getPlusoners().getTotalItems().toString()));
		record.setTimestamp(comment.getPublished().getValue());
		record.setLocation_code(comment.getPublished().getTimeZoneShift());
		record.setUpdate_time(comment.getUpdated().getValue());
		record.setLasttime(currentTime);
		return record;
	}
}
