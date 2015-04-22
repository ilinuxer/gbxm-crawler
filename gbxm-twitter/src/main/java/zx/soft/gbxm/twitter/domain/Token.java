package zx.soft.gbxm.twitter.domain;

public class Token {

	private String tokenkey;
	private String tokensecret;
	private long sinceId;

	public Token() {
		//
	}

	public Token(String tokenkey, String tokensecret, long sinceId) {
		super();
		this.tokenkey = tokenkey;
		this.tokensecret = tokensecret;
		this.sinceId = sinceId;
	}

	public String getTokenkey() {
		return tokenkey;
	}

	public void setTokenkey(String tokenkey) {
		this.tokenkey = tokenkey;
	}

	public String getTokensecret() {
		return tokensecret;
	}

	public void setTokensecret(String tokensecret) {
		this.tokensecret = tokensecret;
	}

	public long getSinceId() {
		return sinceId;
	}

	public void setSinceId(long sinceId) {
		this.sinceId = sinceId;
	}

	@Override
	public String toString() {
		return "Token [tokenkey=" + tokenkey + ", tokensecret=" + tokensecret + ", sinceId=" + sinceId + "]";
	}

}
