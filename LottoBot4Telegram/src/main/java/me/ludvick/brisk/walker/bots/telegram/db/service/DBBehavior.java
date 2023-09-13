package me.ludvick.brisk.walker.bots.telegram.db.service;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;

import java.util.List;

public interface DBBehavior {
    void saveGame(LottoGame lottoGame);
    List<LottoGame> findAll();
    LottoGame findGameById(int id);
    LottoGame findGameByDate(String gameDate);
    int deleteGameById(int id);
}
