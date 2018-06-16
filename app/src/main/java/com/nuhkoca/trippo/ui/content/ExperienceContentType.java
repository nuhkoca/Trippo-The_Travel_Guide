package com.nuhkoca.trippo.ui.content;

public enum ExperienceContentType {
    PRIVATE_TOURS(3421),
    ACTIVITIES(3422),
    MULTI_DAY_TOURS(3423),
    DAY_TRIPS(3424),
    WALKING_TOURS(3425);

    private int sectionId;

    ExperienceContentType(int i) {
        sectionId = i;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }
}
