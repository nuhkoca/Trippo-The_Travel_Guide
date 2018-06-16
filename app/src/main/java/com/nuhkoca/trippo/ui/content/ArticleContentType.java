package com.nuhkoca.trippo.ui.content;

public enum ArticleContentType {
    BACKGROUND(8675),
    PRACTICALITIES(8676);

    private int sectionId;

    ArticleContentType(int i) {
        sectionId = i;
    }

    public int getSectionId() {
        return sectionId;
    }

    public void setSectionId(int sectionId) {
        this.sectionId = sectionId;
    }
}
