package com.nuhkoca.trippo.ui.content;

public enum OutsideContentType {
    SIGHTSEEING(2450),
    EAT_AND_DRINK(2451),
    NIGHTLIFE(2452),
    HOTEL(2453);

    private int sectionId;

    OutsideContentType(int i) {
        sectionId = i;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }
}
