package com.nuhkoca.trippo.ui.content;

public enum ContentType {
    CITY(1110),
    REGION(1111),
    NATIONAL_PARK(1112),
    ISLAND(1113);

    private int sectionId;

    ContentType(int i) {
        sectionId = i;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }
}