package com.nuhkoca.trippo.ui.settings;

public enum ActivityType {
    MAIN(6477),
    MAP(6478),
    OUTSIDE(6479),
    EXPERIENCE(6480),
    ARTICLE(6481);

    private int activityId;

    ActivityType(int i) {
        activityId = i;
    }

    public int getActivityId() {
        return activityId;
    }

    public void setActivityId(int activityId) {
        this.activityId = activityId;
    }
}