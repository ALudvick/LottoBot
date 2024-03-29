package me.ludvick.brisk.walker.bots.telegram.db.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public interface DBBehavior<T, G> {
    Logger logger = LogManager.getLogger();
    void save(T t);
    List<T> findAll();
    T findById(G id);
    T findNewestData();
    T findOldestData();
    G deleteById(G id);
}
