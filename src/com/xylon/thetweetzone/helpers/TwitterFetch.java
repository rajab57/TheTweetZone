package com.xylon.thetweetzone.helpers;

public interface TwitterFetch {
    void fetchTwitterCallback(long sinceId, long maxId);
}