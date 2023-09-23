package me.ludvick.brisk.walker.bots.telegram.db.service;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;

import java.util.List;

public interface DBBehavior<T> {
    void save(T t);
    List<T> findAll();
    T findById(int id);
    List<T> findBetweenDate(String startDate, String endDate);
    int deleteById(int id);
}
