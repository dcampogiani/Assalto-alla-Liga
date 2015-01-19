package com.danielecampogiani.assaltoallaliga.support;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by danielecampogiani on 19/01/15.
 */
public class ThreadsManager {

    private static final BlockingQueue<Runnable> mDecodeWorkQueue = new LinkedBlockingQueue<>();
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static ThreadsManager sInstance;
    private static Handler mUIHandler;
    private static int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    private static ThreadPoolExecutor mThreadPool = new ThreadPoolExecutor(NUMBER_OF_CORES, NUMBER_OF_CORES, KEEP_ALIVE_TIME, KEEP_ALIVE_TIME_UNIT, mDecodeWorkQueue);

    static {
        sInstance = new ThreadsManager();
        mUIHandler = new Handler(Looper.getMainLooper());
    }

    private ThreadsManager() {
    }

    static public void execute(final Runnable background) {
        sInstance.mThreadPool.execute(background);
    }
}
