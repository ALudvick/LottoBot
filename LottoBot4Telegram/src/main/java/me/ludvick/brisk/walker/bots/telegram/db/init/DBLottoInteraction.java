package me.ludvick.brisk.walker.bots.telegram.db.init;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.service.DBBehavior;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBLottoInteraction implements DBBehavior<LottoGame, String> {
    Connection connection;
    PreparedStatement prepareSave;
    PreparedStatement prepareSelectAll;
    PreparedStatement prepareSelectById;
    PreparedStatement prepareSelectNewestData;
    PreparedStatement prepareSelectOldestData;
    PreparedStatement prepareSelectCurrentDate;
    PreparedStatement prepareSelectBetweenDate;
    PreparedStatement prepareDeleteById;


    public DBLottoInteraction() {}

    public DBLottoInteraction(Connection connection) {
        this.connection = connection;
    }

    public void initConnection(String url, String username, String password, String tableName) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            prepareSave = connection.prepareStatement("INSERT INTO " + tableName + " (lotto_uuid, lotto_id, lotto_date, lotto_strong_number, lotto_regular_numbers) values (?, ?, ?, ?, ?);");
            prepareSelectAll = connection.prepareStatement("SELECT * FROM " + tableName + ";");
            prepareSelectById = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_id = ?;");
            prepareSelectNewestData = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_date = (SELECT MAX(lotto_date) FROM " + tableName + ");");
            prepareSelectOldestData = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_date = (SELECT MIN(lotto_date) FROM " + tableName + ");");
            prepareSelectCurrentDate = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_date = DATE(?);");
            //prepareSelectBetweenDate = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_date <= ? AND lotto_date >= ?;");
            prepareSelectBetweenDate = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_date BETWEEN ? AND ?;");
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
            prepareSelectNewestData.close();
            prepareSelectOldestData.close();
            prepareSelectCurrentDate.close();
            prepareSelectBetweenDate.close();
            prepareDeleteById.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(LottoGame lottoGame) {
        try {
            prepareSave.setString(1, lottoGame.getLotto_uuid());
            prepareSave.setInt(2, lottoGame.getLottoId());
            prepareSave.setDate(3, lottoGame.getLottoDate());
            prepareSave.setInt(4, lottoGame.getLottoStrongNumber());
            prepareSave.setArray(5, connection.createArrayOf("INTEGER", lottoGame.getLottoRegularNumbers()));

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
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getDate(3),
                        rs.getInt(4),
                        (Integer[]) rs.getArray(5).getArray()));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGames;
    }

    @Override
    public LottoGame findById(int id) {
        LottoGame lottoGame = null;

        try {
            prepareSelectById.setInt(1, id);
            System.out.println(prepareSelectById.toString());
            ResultSet rs = prepareSelectById.executeQuery();

            while (rs.next()) {
                lottoGame = new LottoGame(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getDate(3),
                        rs.getInt(4),
                        (Integer[]) rs.getArray(5).getArray());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGame;
    }

    @Override
    public LottoGame findNewestData() {
        LottoGame lottoGame = null;

        try {
            System.out.println(prepareSelectNewestData.toString());
            ResultSet rs = prepareSelectNewestData.executeQuery();

            while (rs.next()) {
                lottoGame = new LottoGame(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getDate(3),
                        rs.getInt(4),
                        (Integer[]) rs.getArray(5).getArray());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGame;
    }

    @Override
    public LottoGame findOldestData() {
        LottoGame lottoGame = null;

        try {
            System.out.println(prepareSelectOldestData.toString());
            ResultSet rs = prepareSelectOldestData.executeQuery();

            while (rs.next()) {
                lottoGame = new LottoGame(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getDate(3),
                        rs.getInt(4),
                        (Integer[]) rs.getArray(5).getArray());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGame;
    }

    public LottoGame findCurrentData(String date) {
        LottoGame lottoGame = null;

        try {
            prepareSelectCurrentDate.setString(1, date);
            System.out.println(prepareSelectCurrentDate.toString());
            ResultSet rs = prepareSelectCurrentDate.executeQuery();

            while (rs.next()) {
                lottoGame = new LottoGame(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getDate(3),
                        rs.getInt(4),
                        (Integer[]) rs.getArray(5).getArray());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGame;
    }

    public List<LottoGame> findBetweenDate(String gameStartDate, String gameEndDate) {
        List<LottoGame> lottoGames = new ArrayList<>();

        try {
            prepareSelectBetweenDate.setDate(1, java.sql.Date.valueOf(gameStartDate));
            prepareSelectBetweenDate.setDate(2, java.sql.Date.valueOf(gameEndDate));
            System.out.println(prepareSelectBetweenDate.toString());
            ResultSet rs = prepareSelectBetweenDate.executeQuery();

            while (rs.next()) {
                lottoGames.add(new LottoGame(
                        rs.getString(1),
                        rs.getInt(2),
                        rs.getDate(3),
                        rs.getInt(4),
                        (Integer[]) rs.getArray(5).getArray()));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return lottoGames;
    }

    @Override
    public int deleteById(int id) {
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
