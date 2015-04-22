package zx.soft.gbxm.facebook.common;

import zx.soft.gbxm.facebook.dao.FBDaoImpl;

public class Refresh {

	public static String getNextToken(int i) {
		FBDaoImpl impl = new FBDaoImpl();
		return impl.getToken(i);
	}

	public static Long getNextSince(int i) {
		FBDaoImpl impl = new FBDaoImpl();
		return Long.valueOf(impl.getSince(i));
	}

}
