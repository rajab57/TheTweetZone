package com.xylon.thetweetzone.listeners;

public interface CustomRefreshListener {
    void onRefreshCallback(long sinceId, long maxId);
}