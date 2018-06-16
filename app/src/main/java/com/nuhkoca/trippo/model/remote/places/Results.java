package com.nuhkoca.trippo.model.remote.places;

import com.google.gson.annotations.SerializedName;

public class Results {
    @SerializedName("scope")
    private String scope;
    @SerializedName("name")
    private String name;
    @SerializedName("types")
    private String[] types;
    @SerializedName("reference")
    private String reference;
    @SerializedName("geometry")
    private Geometry geometry;
    @SerializedName("vicinity")
    private String vicinity;
    @SerializedName("icon")
    private String icon;

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] getTypes() {
        return types;
    }

    public void setTypes(String[] types) {
        this.types = types;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public String getVicinity() {
        return vicinity;
    }

    public void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}