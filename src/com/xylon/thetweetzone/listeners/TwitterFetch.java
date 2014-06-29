package com.xylon.thetweetzone.listeners;

public interface TwitterFetch {
    void fetchTwitterCallback(long sinceId, long maxId);
}