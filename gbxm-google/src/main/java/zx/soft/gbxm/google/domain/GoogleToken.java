package zx.soft.gbxm.google.domain;

public class GoogleToken {

	private String app_name = "";
	private String client_id = "";
	private String client_secret = "";

	public GoogleToken() {
		//
	}

	public GoogleToken(String app_name, String client_id, String client_secret) {
		super();
		this.app_name = app_name;
		this.client_id = client_id;
		this.client_secret = client_secret;
	}

	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getClient_id() {
		return client_id;
	}

	public void setClient_id(String client_id) {
		this.client_id = client_id;
	}

	public String getClient_secret() {
		return client_secret;
	}

	public void setClient_secret(String client_secret) {
		this.client_secret = client_secret;
	}

	@Override
	public String toString() {
		return "GoogleToken [app_name=" + app_name + ", client_id=" + client_id + ", client_secret=" + client_secret
				+ "]";
	}

}
