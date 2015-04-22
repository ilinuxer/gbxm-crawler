package zx.soft.gbxm.google.api;

import zx.soft.gbxm.google.common.ConstUtils;
import zx.soft.gbxm.google.dao.GoogleDaoImpl;
import zx.soft.gbxm.google.domain.GoogleToken;

public class Refresh {

	private static int i = 0;
	private static GoogleDaoImpl googleDaoImpl = new GoogleDaoImpl();

	//获取可用app数量
	public static int getGoogleAppCount() {
		return googleDaoImpl.getTableCount(ConstUtils.GOOGLE_PLUS_APPS_TABLE);
	}

	//在应用的请求次数达到限制的时候，重新获取应用token信息
	public static GoogleToken getNextGoogleToken() {
		if (i == getGoogleAppCount()) {
			i = 0;
		}
		return googleDaoImpl.getGoogleTokenById(ConstUtils.GOOGLE_PLUS_APPS_TABLE, ++i);
	}
}