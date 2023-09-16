package me.ludvick.brisk.walker.bots.telegram.db.init;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.service.DBBehavior;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DBInteraction implements DBBehavior {
    Connection connection;
    PreparedStatement prepareSave;
    PreparedStatement prepareSelectAll;
    PreparedStatement prepareSelectById;
    PreparedStatement prepareSelectByDate;
    PreparedStatement prepareDeleteById;


    public DBInteraction() {}

    public DBInteraction(Connection connection) {
        this.connection = connection;
    }

    public void initConnection(String url, String username, String password, String tableName) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            prepareSave = connection.prepareStatement("INSERT INTO " + tableName + " (lotto_id, lotto_date, lotto_strong_number, lotto_regular_number) values (?, ?, ?, ?);");
            prepareSelectAll = connection.prepareStatement("SELECT * FROM " + tableName + ";");
            prepareSelectById = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_id = ?;");
            prepareSelectByDate = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_date = ?;");
            prepareDeleteById = connection.prepareStatement("DELETE FROM " + tableName + " WHERE lotto_id = ?;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            connection.close();
            prepareSave.close();
            prepareSelectAll.close();
            prepareSelectById.close();
            prepareSelectByDate.close();
            prepareDeleteById.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveGame(LottoGame lottoGame) {
        try {
            prepareSave.setInt(1, lottoGame.getLottoId());
            prepareSave.setDate(2, lottoGame.getLottoDate());
            prepareSave.setInt(3, lottoGame.getLottoStrongNumber());
            prepareSave.setArray(4, connection.createArrayOf("INTEGER", lottoGame.getLottoRegularNumbers()));

            System.out.println(prepareSave.toString());
            prepareSave.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<LottoGame> findAll() {
        List<LottoGame> lottoGames = new ArrayList<>();

        try {
            System.out.println(prepareSelectAll.toString());
            ResultSet rs = prepareSelectAll.executeQuery();

            while (rs.next()) {
                lottoGames.add(new LottoGame(
                        rs.getInt(1),
                        rs.getDate(2),
                        rs.getInt(3),
                        (Integer[]) rs.getArray(4).getArray()));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGames;
    }

    @Override
    public LottoGame findGameById(int id) {
        LottoGame lottoGame = new LottoGame();

        try {
            prepareSelectById.setInt(1, id);
            System.out.println(prepareSelectById.toString());
            ResultSet rs = prepareSelectById.executeQuery();

            while (rs.next()) {
                lottoGame.setLottoId(rs.getInt(1));
                lottoGame.setLottoDate(rs.getDate(2));
                lottoGame.setLottoStrongNumber(rs.getInt(3));
                lottoGame.setLottoRegularNumbers((Integer[]) rs.getArray(4).getArray());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGame;
    }

    @Override
    public LottoGame findGameByDate(String gameDate) {
        LottoGame lottoGame = new LottoGame();

        try {
            prepareSelectByDate.setDate(1, java.sql.Date.valueOf(gameDate));
            System.out.println(prepareSelectByDate.toString());
            ResultSet rs = prepareSelectByDate.executeQuery();

            while (rs.next()) {
                lottoGame.setLottoId(rs.getInt(1));
                lottoGame.setLottoDate(rs.getDate(2));
                lottoGame.setLottoStrongNumber(rs.getInt(3));
                lottoGame.setLottoRegularNumbers((Integer[]) rs.getArray(4).getArray());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGame;
    }

    @Override
    public int deleteGameById(int id) {
        try {
            prepareDeleteById.setInt(1, id);
            System.out.println(prepareDeleteById.toString());
            prepareDeleteById.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return id;
    }
}