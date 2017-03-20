package net.leanix.metrics.dashboard.applicationdeployment;

public final class AppDeployment {

	private String factsheetId;
	private String displayName;
	private int global;
	private int local;

	public String getFactsheetId() {
		return factsheetId;
	}

	public void setFactsheetId(String factsheetId) {
		this.factsheetId = factsheetId;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getGlobal() {
		return global;
	}

	public void incrementGlobal() {
		global++;
	}

	public int getLocal() {
		return local;
	}

	public void incrementLocal() {
		local++;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append("factsheetId=").append(factsheetId).append(',');
		sb.append("displayName=").append(displayName).append(',');
		sb.append("global=").append(global).append(',');
		sb.append("local=").append(local);
		return sb.append(']').toString();
	}
}