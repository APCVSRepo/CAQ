package com.ford.cvs.caq.client.model;

/**
 * Created by Ivan on 2017/2/20.
 */

public class DownLoadProgressInfo {
    private String name;
    private String status="下载";
    private boolean isDownloading=false;
    private int progress;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isDownloading() {
        return isDownloading;
    }

    public void setDownloading(boolean downloading) {
        isDownloading = downloading;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }
}
