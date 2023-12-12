package me.ludvick.brisk.walker.bots.telegram.db.service;

import java.util.List;

public interface DBBehavior<T, G> {
    void save(T t);
    List<T> findAll();
    T findById(int id);
    T findNewestData();
    T findOldestData();
    int deleteById(int id);
}
