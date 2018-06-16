package com.nuhkoca.trippo.api;

public class NetworkState {
    public enum Status {
        RUNNING,
        SUCCESS,
        FAILED,
        NO_ITEM
    }

    private final Status status;

    public static final NetworkState LOADED;
    public static final NetworkState LOADING;

    public NetworkState(Status status) {
        this.status = status;
    }

    static {
        LOADED = new NetworkState(Status.SUCCESS);
        LOADING = new NetworkState(Status.RUNNING);
    }

    public Status getStatus() {
        return status;
    }
}
