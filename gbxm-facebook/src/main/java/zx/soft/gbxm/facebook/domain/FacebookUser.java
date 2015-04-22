package zx.soft.gbxm.facebook.domain;

import java.io.Serializable;
import java.util.Date;

public class FacebookUser implements Serializable {

	private static final long serialVersionUID = 2374197354751511569L;

	private String id = "";//用户id"160144950715498"
	private String username = "";//用户名"MilitarySpokesman"
	private String name = "";//"國防部發言人"
	private String gender = "";
	private String locale = "";//Locale转换为String
	private String link = "";//用户的facebook主页链接
	private String website = "";//用户的个人网站
	private String email = "";
	private float timezone;
	private Date updatedTime;
	private boolean verified;
	private String about = "";//描述
	private String birthday = "";
	private String locaton = "";//Reference 转换为String
	private int inspirationalPeopleCount;//将follow人列表转换为数量
	private String languages = "";//将使用语言列表转换为拼接的语言字符串
	private String political = "";
	private String quotes = "";

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getLocale() {
		return locale;
	}

	public void setLocale(String locale) {
		this.locale = locale;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Float getTimezone() {
		return timezone;
	}

	public void setTimezone(Float timezone) {
		this.timezone = timezone;
	}

	public Date getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(Date updatedTime) {
		this.updatedTime = updatedTime;
	}

	public Boolean getVerified() {
		return verified;
	}

	public void setVerified(Boolean verified) {
		this.verified = verified;
	}

	public String getAbout() {
		return about;
	}

	public void setAbout(String about) {
		this.about = about;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getLocaton() {
		return locaton;
	}

	public void setLocaton(String locaton) {
		this.locaton = locaton;
	}

	public int getInspirationalPeopleCount() {
		return inspirationalPeopleCount;
	}

	public void setInspirationalPeopleCount(int inspirationalPeopleCount) {
		this.inspirationalPeopleCount = inspirationalPeopleCount;
	}

	public String getLanguages() {
		return languages;
	}

	public void setLanguages(String languages) {
		this.languages = languages;
	}

	public String getPolitical() {
		return political;
	}

	public void setPolitical(String political) {
		this.political = political;
	}

	public String getQuotes() {
		return quotes;
	}

	public void setQuotes(String quotes) {
		this.quotes = quotes;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "FacebookUser [id=" + id + ", username=" + username + ", name=" + name + ", gender=" + gender
				+ ", locale=" + locale + ", link=" + link + ", website=" + website + ", email=" + email + ", timezone="
				+ timezone + ", updatedTime=" + updatedTime + ", verified=" + verified + ", about=" + about
				+ ", birthday=" + birthday + ", locaton=" + locaton + ", inspirationalPeopleCount="
				+ inspirationalPeopleCount + ", languages=" + languages + ", political=" + political + ", quotes="
				+ quotes + "]";
	}

}
