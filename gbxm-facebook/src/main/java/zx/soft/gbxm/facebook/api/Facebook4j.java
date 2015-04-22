package zx.soft.gbxm.facebook.api;

import java.util.Date;

import zx.soft.gbxm.facebook.dao.FBDaoImpl;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Post;
import facebook4j.Reading;
import facebook4j.ResponseList;
import facebook4j.auth.AccessToken;

public class Facebook4j {

	private Facebook facebook;

	private FBDaoImpl fbDaoImpl = new FBDaoImpl();

	public Facebook4j(String appId, String appSecret) {
		facebook = new FacebookFactory().getInstance();
		facebook.setOAuthAppId(appId, appSecret);
	}

	public void getFacebookStatuses(String accessToken, long since) {
		AccessToken token = new AccessToken(accessToken);
		facebook.setOAuthAccessToken(token);
		long currentTime = System.currentTimeMillis();
		Reading reading = new Reading().limit(150).since(new Date(since)).until(new Date(currentTime));
		ResponseList<Post> statuses = null;
		try {
			statuses = facebook.getHome(reading);
			System.out.println(statuses.size());
			if (statuses.size() > 0) {
				//	fbDaoImpl.updateSince(currentTime / 1000L, accessToken);
				//System.out.println(JsonUtils.toJsonWithoutPretty(statuses));
			}
		} catch (FacebookException e) {
			e.printStackTrace();
		}
		//System.out.println(JsonUtils.toJsonWithoutPretty(statuses));

	}

	public static void main(String[] args) {
		String token = "CAAEyboPYV4UBAIQy4MwMiWmHTM0NWYTEGoTZAavB1hxoxIJoBkLlPK6ZCZCTlC6Bq8gibj5Atf0pAcoW9GnPbHhxZCmpxhhS1llTht5kPUZCqnDqVpdcbX0T7J4jigNs8bn2oJpuwV3J4qZBjNQHNVqtdBTqZAIEUoOLgYliJ8cVfWiYaTXSPwsf4JdZArsj9TRqA7mlNRZCdFcGHjoQZCX89X";
		Facebook4j facebook = new Facebook4j("336925216495493", "4d6723811368c72b53ab33a38551dbe1");
		long threedaybefore = 1427964626114L - 3600 * 1000 * 24 * 3;
		long twodaybefore = threedaybefore + 3600 * 1000 * 24;
		long onedayfefore = twodaybefore + 3600 * 24 * 1000;
		facebook.getFacebookStatuses(token, 1427958259000L);
	}
}
