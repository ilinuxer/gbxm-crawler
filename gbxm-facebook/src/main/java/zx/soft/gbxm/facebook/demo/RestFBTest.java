package zx.soft.gbxm.facebook.demo;

import java.util.List;

import zx.soft.utils.json.JsonUtils;

import com.restfb.Connection;
import com.restfb.DefaultFacebookClient;
import com.restfb.FacebookClient;
import com.restfb.Parameter;
import com.restfb.Version;
import com.restfb.types.Post;
import com.restfb.types.User;

public class RestFBTest {

	public static void main(String[] args) {
		FacebookClient facebookClient = new DefaultFacebookClient(
				"CAAEyboPYV4UBAIQy4MwMiWmHTM0NWYTEGoTZAavB1hxoxIJoBkLlPK6ZCZCTlC6Bq8gibj5Atf0pAcoW9GnPbHhxZCmpxhhS1llTht5kPUZCqnDqVpdcbX0T7J4jigNs8bn2oJpuwV3J4qZBjNQHNVqtdBTqZAIEUoOLgYliJ8cVfWiYaTXSPwsf4JdZArsj9TRqA7mlNRZCdFcGHjoQZCX89X",
				Version.VERSION_2_2);
		User page = facebookClient.fetchObject("me", User.class);
		System.out.println(page.getId());
		Connection<Post> myFeed = facebookClient.fetchConnection("420361564693683/feed", Post.class,
				Parameter.with("type", "status"), Parameter.with("until", "yesterday"), Parameter.with("limit", 100));//
		System.out.println(myFeed.getData().size());
		for (List<Post> myFeedConnectionPage : myFeed) {
			for (Post post : myFeedConnectionPage) {
				System.out.println(JsonUtils.toJsonWithoutPretty(post));
			}
		}
	}
}
