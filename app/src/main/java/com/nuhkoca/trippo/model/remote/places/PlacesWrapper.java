package com.nuhkoca.trippo.model.remote.places;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlacesWrapper {
    @SerializedName("next_page_token")
    private String nextPageToken;
    @SerializedName("results")
    private List<Results> results;
    @SerializedName("html_attributions")
    private String[] htmlAttributions;
    @SerializedName("status")
    private String status;

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public List<Results> getResults() {
        return results;
    }

    public void setResults(List<Results> results) {
        this.results = results;
    }

    public String[] getHtmlAttributions() {
        return htmlAttributions;
    }

    public void setHtmlAttributions(String[] htmlAttributions) {
        this.htmlAttributions = htmlAttributions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
