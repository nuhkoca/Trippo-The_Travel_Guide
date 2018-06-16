package com.nuhkoca.trippo.helper;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppsExecutor {
    private final static Executor networkIO = Executors.newFixedThreadPool(Constants.EXECUTOR_THREAD_POOL_OFFSET);

    private final static Executor mainThread = new MainThreadExecutor();

    private final static Executor backgroundThread = Executors.newSingleThreadExecutor();

    private AppsExecutor() {}

    public static Executor networkIO() {
        return networkIO;
    }

    public static Executor mainThread() {
        return mainThread;
    }

    public static Executor backgroundThread(){
        return backgroundThread;
    }

    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
