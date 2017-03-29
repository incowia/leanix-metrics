package net.leanix.metrics.dashboard.dataquality;

public final class DataQuality {

	private String factsheetId;
	private String displayName;
	private int complete;
	private int incomplete;
	private double sumOfComplete;

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

	public void addToSumOfComplete(double add) {
		sumOfComplete += add;
	}

	public int getCompleteInPercent() {
		if (complete == 0) {
			return 0;
		}
		double total = (double) (complete + incomplete);
		return (int) Math.round(((double) complete) * 100.0d / total);
	}

	public int getIncompleteInPercent() {
		return 100 - getCompleteInPercent();
	}

	public int getAvgOfComplete() {
		int total = complete + incomplete;
		if (total == 0 || sumOfComplete == 0.0d) {
			return 0;
		}
		return (int) Math.round(sumOfComplete * 100.0d / ((double) total));
	}

	public int getAvgOfIncomplete() {
		return 100 - getAvgOfComplete();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("[");
		sb.append("factsheetId=").append(factsheetId).append(',');
		sb.append("displayName=").append(displayName).append(',');
		sb.append("complete=").append(complete).append(',');
		sb.append("incomplete=").append(incomplete).append(',');
		sb.append("sumOfComplete=").append(sumOfComplete);
		return sb.append(']').toString();
	}
}
