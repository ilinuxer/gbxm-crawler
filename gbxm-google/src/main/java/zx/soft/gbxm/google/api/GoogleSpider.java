package zx.soft.gbxm.google.api;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.gbxm.google.common.ConstUtils;
import zx.soft.gbxm.google.common.Convert;
import zx.soft.gbxm.google.common.RestletPost;
import zx.soft.gbxm.google.dao.GoogleDaoImpl;
import zx.soft.gbxm.google.domain.GooglePlusStatus;
import zx.soft.gbxm.google.domain.GoogleToken;
import zx.soft.gbxm.google.domain.PostData;
import zx.soft.gbxm.google.domain.RecordInfo;
import zx.soft.utils.log.LogbackUtil;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.services.plus.Plus;

public class GoogleSpider {

	private static Logger logger = LoggerFactory.getLogger(GoogleSpider.class);
	public static GoogleDaoImpl googleDaoImpl = new GoogleDaoImpl();
	private static CredentialFile credentialFile;

	public GoogleSpider() {
		credentialFile = new CredentialFile();
	}

	//循环扫描所有监测用户
	public void run(Credential credential) throws IOException, InterruptedException {
		long lastUpdateTme;
		Plus plus = null;
		long currentTime;
		long onceCircleTime;
		long spendTime;
		while (true) {
			try {
				if (credential.refreshToken()) {
					plus = new Plus.Builder(credentialFile.getHttpTransport(), CredentialFile.getJsonFactory(),
							credential).setApplicationName("zxsoft crawler").build();
					logger.info("进行token更新");
				}
			} catch (IOException e) {
				logger.info("更新token失败");
				logger.error("Exception:{}", LogbackUtil.expection2Str(e));
			}
			ActivityList activityList = new ActivityList(plus);
			List<String> userIdList = googleDaoImpl.getUserIdList(ConstUtils.USER_INFO_GOOGLE_PLUS_TABLE);
			onceCircleTime = System.currentTimeMillis();
			for (String userId : userIdList) {
				ArrayList<GooglePlusStatus> googlePlusStatuses = new ArrayList<>();
				lastUpdateTme = googleDaoImpl.getLastUpdateTimeByUserId(ConstUtils.USER_INFO_GOOGLE_PLUS_TABLE, userId)
						.getTime();
				currentTime = System.currentTimeMillis();
				googlePlusStatuses = activityList.getActivitiesByUserId(userId, lastUpdateTme);
				if (googlePlusStatuses.size() > 0) {
					List<RecordInfo> records = new ArrayList<>();
					for (GooglePlusStatus googlePlusStatus : googlePlusStatuses) {
						records.add(Convert.convertGPS2Record(googlePlusStatus, currentTime));
						logger.info("add one record");
					}
					PostData data = new PostData();
					data.setNum(records.size());
					data.setRecords(records);
					//post并将数据插入数据库
					RestletPost.post(data);
					googleDaoImpl.insertGooglePlusListStatus(googlePlusStatuses);
				}
				if (googleDaoImpl.isExisted(ConstUtils.USER_INFO_GOOGLE_PLUS_TABLE, userId)) {
					googleDaoImpl.updatedUserInfo(userId, new Timestamp(currentTime));
					logger.info("更新" + userId + "检测时间");
				}
			}
			//一次扫描结束,睡眠一段时间
			spendTime = System.currentTimeMillis() - onceCircleTime;
			logger.info("一次扫描结束，休眠一天");
			Thread.sleep(24 * 3600 * 1000 - spendTime);
			logger.info("睡眠结束，重新扫描");
		}
	}

	public static void main(String[] args) throws InterruptedException {

		GoogleSpider googleSpider = new GoogleSpider();
		CredentialFile credentialFile = new CredentialFile();
		Credential credential = null;
		int j = 0;
		GoogleToken googleToken = Refresh.getNextGoogleToken();
		try {
			credential = credentialFile.loadCredential(googleToken.getClient_id(), googleToken.getClient_secret(),
					googleToken.getApp_name());
		} catch (IOException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		} catch (GeneralSecurityException e) {
			logger.error("Exception:{}", LogbackUtil.expection2Str(e));
		}
		while (true) {
			try {
				googleSpider.run(credential);
				j = 0;
			} catch (IOException e) {
				//如果是内部服务器错误，重新获取一次
				if (e.getMessage().contains("500")) {
					logger.info("500 Internal Server Error.");
					try {
						googleSpider.run(credential);
					} catch (IOException e1) {
						logger.error("again 500 Internal Server Error.");
						logger.error("Exception:{}", LogbackUtil.expection2Str(e1));
					}
				}
				//连接超时
				if (e.getMessage().contains("connect timed out")) {
					logger.error("connect timed out");
					logger.error("Exception:{}", LogbackUtil.expection2Str(e));
				}
				//应用的请求次数限制导致错误，更换应用
				if (e.getMessage().contains("Daily Limit Exceeded")) {
					++j;
					logger.info("current exceeded token  number= " + j);
					if (j == Refresh.getGoogleAppCount()) {
						logger.info("所有app均受到请求次数限制（10000/天），请添加token");
						Thread.sleep(24 * 3600_000L);
					}
					logger.info("Daily Limit Exceeded,now change app");
					googleToken = Refresh.getNextGoogleToken();
					try {
						credential = credentialFile.loadCredential(googleToken.getClient_id(),
								googleToken.getClient_secret(), googleToken.getApp_name());
					} catch (IOException e1) {
						logger.error("Exception:{}", LogbackUtil.expection2Str(e));
					} catch (GeneralSecurityException e1) {
						logger.error("Exception:{}", LogbackUtil.expection2Str(e));
					}
				}
			}
		}
	}
}
