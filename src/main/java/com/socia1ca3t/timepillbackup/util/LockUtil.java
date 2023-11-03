package com.socia1ca3t.timepillbackup.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockUtil {

    private static volatile LockUtil instance;
    private final Map<String, Lock> locks = new ConcurrentHashMap<>();

    private LockUtil() {
    }

    public static LockUtil getInstance() {

        if (instance == null) {

            synchronized (LockUtil.class) {
                if (instance == null) {
                    instance = new LockUtil();
                }
            }
        }
        return instance;
    }

    public boolean tryLock(String key) {

        Lock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());

        return lock.tryLock();
    }

    public void unlock(String key) {

        Lock lock = locks.get(key);
        if (lock != null) {
            lock.unlock();
        }
    }
}
