package me.ludvick.brisk.walker.bots.telegram.db.service;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;

import java.util.List;

public interface DBBehavior<T> {
    void save(T t);
    List<LottoGame> findAll();
    LottoGame findById(int id);
    LottoGame findByDate(String date);
    int deleteById(int id);
}
