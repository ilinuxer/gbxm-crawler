package zx.soft.gbxm.facebook.common;

import java.util.List;

import org.springframework.social.facebook.api.FacebookProfile;
import org.springframework.social.facebook.api.Post;
import org.springframework.social.facebook.api.Reference;

import zx.soft.gbxm.facebook.domain.FacebookStatus;
import zx.soft.gbxm.facebook.domain.FacebookUser;
import zx.soft.gbxm.facebook.domain.RecordInfo;
import zx.soft.utils.checksum.CheckSumUtils;

public class Convert {

	public static FacebookStatus convertPost2FacebookStatus(Post post) {

		FacebookStatus facebookStatus = new FacebookStatus();
		facebookStatus.setId(post.getId());
		facebookStatus.setFrom_id(post.getFrom().getId());
		facebookStatus.setFrom_name(post.getFrom().getName());
		facebookStatus.setMessage(post.getMessage());
		facebookStatus.setLink(post.getLink());
		facebookStatus.setCreatedTime(post.getUpdatedTime());
		facebookStatus.setUpdatedTime(post.getUpdatedTime());
		facebookStatus.setSharesCount(post.getSharesCount());
		facebookStatus.setCommentsCount(post.getCommentCount());
		facebookStatus.setLikesCount(post.getLikeCount());
		return facebookStatus;
	}

	public static RecordInfo convertFacebookStatus2RecordInfo(FacebookStatus facebookStatus, long until) {

		RecordInfo record = new RecordInfo();
		String url = "www.facebook.com/" + facebookStatus.getId().split("_")[0] + "/posts/"
				+ facebookStatus.getId().split("_")[1];
		record.setId(CheckSumUtils.getMD5(url).toUpperCase());
		record.setMid(facebookStatus.getId());
		record.setUsername(facebookStatus.getFrom_id());
		record.setNickname(facebookStatus.getFrom_name());
		record.setContent(facebookStatus.getMessage());
		record.setUrl(url);
		record.setTimestamp(facebookStatus.getCreatedTime().getTime());
		record.setUpdate_time(facebookStatus.getUpdatedTime().getTime());
		record.setComment_count(facebookStatus.getCommentsCount());
		record.setRepost_count(facebookStatus.getSharesCount());
		record.setFavorite_count(facebookStatus.getLikesCount());
		record.setLasttime(until);
		return record;
	}

	public static FacebookUser convertFacebookProfile2FacebookUser(FacebookProfile facebookProfile) {

		FacebookUser facebookUser = new FacebookUser();
		facebookUser.setId(facebookProfile.getId());
		facebookUser.setUsername(facebookProfile.getUsername());
		facebookUser.setName(facebookProfile.getName());
		if (facebookProfile.getName() != null) {
			facebookUser.setGender(facebookProfile.getGender());
		}
		if (facebookProfile.getLocale() != null) {
			facebookUser.setLocale(facebookProfile.getLocale().toString());
		}
		if (facebookProfile.getLink() != null) {
			facebookUser.setLink(facebookProfile.getLink());
		}
		if (facebookProfile.getWebsite() != null) {
			facebookUser.setWebsite(facebookProfile.getWebsite());
		}
		if (facebookProfile.getEmail() != null) {
			facebookUser.setEmail(facebookProfile.getEmail());
		}
		if (facebookProfile.getTimezone() != null) {
			facebookUser.setTimezone(facebookProfile.getTimezone());
		}
		if (facebookProfile.getUpdatedTime() != null) {
			facebookUser.setUpdatedTime(facebookProfile.getUpdatedTime());
		}
		if (facebookProfile.isVerified() != null) {
			facebookUser.setVerified(facebookProfile.isVerified());
		}
		if (facebookProfile.getAbout() != null) {
			facebookUser.setAbout(facebookProfile.getAbout());
		}
		if (facebookProfile.getBirthday() != null) {
			facebookUser.setBirthday(facebookProfile.getBirthday());
		}
		if (facebookProfile.getLocation() != null && facebookProfile.getLocation().getName() != null) {
			facebookUser.setLocaton(facebookProfile.getLocation().getName());
		}
		if (facebookProfile.getInspirationalPeople() != null) {
			facebookUser.setInspirationalPeopleCount(facebookProfile.getInspirationalPeople().size());
		}
		if (facebookProfile.getLanguages() != null) {
			List<Reference> langs = facebookProfile.getLanguages();
			String combination = "";
			for (Reference lang : langs) {
				combination = combination + " " + lang.getName();
			}
			facebookUser.setLanguages(combination);
		}
		if (facebookProfile.getPolitical() != null) {
			facebookUser.setPolitical(facebookProfile.getPolitical());
		}
		if (facebookProfile.getQuotes() != null) {
			facebookUser.setQuotes(facebookProfile.getQuotes());
		}
		return facebookUser;
	}
}
