package me.ludvick.brisk.walker.bots.telegram.db.init;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.service.DBBehavior;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DbLottoInteraction implements DBBehavior<LottoGame> {
    Connection connection;
    PreparedStatement prepareSave;
    PreparedStatement prepareSelectAll;
    PreparedStatement prepareSelectById;
    PreparedStatement prepareSelectBetweenDate;
    PreparedStatement prepareDeleteById;


    public DbLottoInteraction() {}

    public DbLottoInteraction(Connection connection) {
        this.connection = connection;
    }

    public void initConnection(String url, String username, String password, String tableName) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            prepareSave = connection.prepareStatement("INSERT INTO " + tableName + " (lotto_id, lotto_date, lotto_strong_number, lotto_regular_numbers) values (?, ?, ?, ?);");
            prepareSelectAll = connection.prepareStatement("SELECT * FROM " + tableName + ";");
            prepareSelectById = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_id = ?;");
            prepareSelectBetweenDate = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_date <= ? AND lotto_date >= ?;");
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
            prepareSelectBetweenDate.close();
            prepareDeleteById.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(LottoGame lottoGame) {
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
    public LottoGame findById(int id) {
        LottoGame lottoGame = null;

        try {
            prepareSelectById.setInt(1, id);
            System.out.println(prepareSelectById.toString());
            ResultSet rs = prepareSelectById.executeQuery();

            while (rs.next()) {
                lottoGame = new LottoGame(
                        rs.getInt(1),
                        rs.getDate(2),
                        rs.getInt(3),
                        (Integer[]) rs.getArray(4).getArray());
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGame;
    }

    @Override
    public List<LottoGame> findBetweenDate(String gameStartDate, String gameEndDate) {
        List<LottoGame> lottoGames = new ArrayList<>();

        try {
            prepareSelectBetweenDate.setDate(1, java.sql.Date.valueOf(gameStartDate));
            prepareSelectBetweenDate.setDate(2, java.sql.Date.valueOf(gameEndDate));
            System.out.println(prepareSelectBetweenDate.toString());
            ResultSet rs = prepareSelectBetweenDate.executeQuery();

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
