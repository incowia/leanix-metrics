package net.leanix.metrics.dashboard.applicationdeployment;

public class AppDeployment {
	
	private String businessCapabilityID = null;
	private String displayName = null;
	private int totalApps;
	private int globalApps = 0;
	private int localApps = 0;

	public String getBusinessCapabilityID() {
		return businessCapabilityID;
	}

	public void setBusinessCapabilityID(String businessCapabilityID) {
		this.businessCapabilityID = businessCapabilityID;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public int getTotalApps() {
		return totalApps;
	}

	public void setTotalApps(int totalApps) {
		this.totalApps = totalApps;
	}

	public int getGlobalApps() {
		return globalApps;
	}

	public void setGlobalApps(int globalApps) {
		this.globalApps = globalApps;
	}
	
	public void incrementGlobalTags() {
		globalApps++;
	}

	public int getLocalApps() {
		return localApps;
	}

	public void setLocalApps(int localApps) {
		this.localApps = localApps;
	}
	
	public void incrementLocalTags() {
		localApps++;
	}

	@Override
	public String toString() {
		return "[businessCapabilityID=" + businessCapabilityID + ",displayName=" + displayName + ",globalApps="
				+ globalApps + ",localApps=" + localApps + "]";
	}
}