package com.ford.cvs.caq.client.data;

/**
 * Created by CLIU130 on 6/5/2016.
 */
public abstract class ServerDataReader extends Thread {
    private static final long DEFAULT_REQUEST_INTERVAL = 5 * 1000;

    protected DataReaderListener[] mListener;
    private boolean stopped;

    protected long requestInteval = DEFAULT_REQUEST_INTERVAL;

    public ServerDataReader(DataReaderListener... callback) {
        this(DEFAULT_REQUEST_INTERVAL, callback);
    }

    protected ServerDataReader(long requestInterval, DataReaderListener... callback) {
        this.mListener = callback;
        this.requestInteval = requestInterval;
    }

    public static final class Result {
        public static final int CODE_NORMAL = 1;
        public static final int CODE_FAIL = 2;

        private String msg;
        private int code;

        public Result(String message, int code) {
            this.msg = message;
            this.code = code;
        }

        public String message() {return msg;}

        public int code() {return code;}
    }

    protected abstract Result doRequestData();

    public void cancel() {
        this.stopped = true;
    }

    protected void notifyNewData(EnvData data) {
        for(DataReaderListener listener : this.mListener) {
            listener.onNewData(data);
        }
    }

    private void notifyFail(Result result) {
        for (DataReaderListener listener : this.mListener) {
            listener.onError(result.message());
        }
    }

    public void run() {
        stopped = false;
        long lastTime = 0;
        while (this.isAlive() && !stopped) {
            final long currentTime = System.currentTimeMillis();
            if (currentTime - lastTime < requestInteval) {
                try {
                    Thread.sleep(requestInteval - (currentTime - lastTime));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            lastTime = currentTime;
            Result result = doRequestData();
            if (result.code == Result.CODE_FAIL) {
               this.notifyFail(result);
            }
        }
    }
}
