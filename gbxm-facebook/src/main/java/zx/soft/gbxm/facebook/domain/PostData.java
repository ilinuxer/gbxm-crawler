package zx.soft.gbxm.facebook.domain;

import java.io.Serializable;
import java.util.List;

public class PostData implements Serializable {

	private static final long serialVersionUID = 4095331762954142782L;

	private int num;
	private List<RecordInfo> records;

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public List<RecordInfo> getRecords() {
		return records;
	}

	public void setRecords(List<RecordInfo> records) {
		this.records = records;
	}

	@Override
	public String toString() {
		return "PostData [num=" + num + ", records=" + records + "]";
	}

}
