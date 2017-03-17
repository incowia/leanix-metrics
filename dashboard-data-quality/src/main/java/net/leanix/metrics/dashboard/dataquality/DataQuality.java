package net.leanix.metrics.dashboard.dataquality;

public final class DataQuality {

	private String factsheetId;
	private String displayName;
	private int complete;
	private int incomplete;
	private double sumPercentIncomplete;

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

	public int getComplete() {
		return complete;
	}

	public void incrementComplete() {
		complete++;
	}

	public int getIncomplete() {
		return incomplete;
	}

	public void incrementIncomplete() {
		incomplete++;
	}

	public int getCompleteInPercent() {
		double total = complete + incomplete;
		if (complete == 0) {
			return 0;
		}
		return (int) Math.round(((double) complete) * 100.0d / total);
	}

	public int getIncompleteInPercent() {
		return 100 - getCompleteInPercent();
	}
	
	public int getAvgPercentIncomplete() {
		double total = complete + incomplete;
		return (int) Math.round(((double) sumPercentIncomplete) / total);
	}
	
	public int getCompleteAvgInPercent() {
		return 100 - getAvgPercentIncomplete();
	}

	public void setSumPercentIncomplete(double sumPercentIncomplete) {
		this.sumPercentIncomplete = this.sumPercentIncomplete+sumPercentIncomplete;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder('[');
		sb.append("factsheetId=").append(factsheetId).append(',');
		sb.append("displayName=").append(displayName).append(',');
		sb.append("complete=").append(complete).append(',');
		sb.append("incomplete=").append(incomplete);
		return sb.append(']').toString();
	}
}
