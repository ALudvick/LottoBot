package me.ludvick.brisk.walker.bots.telegram.db.init;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.service.DBBehavior;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DBLottoInteraction implements DBBehavior<LottoGame, Long> {
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
        logger.info("Init DBLottoInteraction...");
        try {
            connection = DriverManager.getConnection(url, username, password);
            logger.info("Preparing statements...");
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        logger.info("Close DBLottoInteraction");
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
            logger.error(e.getMessage());
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

            logger.info("save: {}", prepareSave.toString());
            prepareSave.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<LottoGame> findAll() {
        List<LottoGame> lottoGames = new ArrayList<>();

        try {
            logger.info("findAll: {}", prepareSelectAll.toString());
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("Finding {} games", lottoGames.size());
        return lottoGames;
    }

    @Override
    public LottoGame findById(Long id) {
        LottoGame lottoGame = null;

        try {
            prepareSelectById.setLong(1, id);
            logger.info("findById: {}", prepareSelectById.toString());
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("Find game: {}", lottoGame);
        return lottoGame;
    }

    @Override
    public LottoGame findNewestData() {
        LottoGame lottoGame = null;

        try {
            logger.info("findNewestData: {}", prepareSelectNewestData.toString());
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("newest data: {}", lottoGame);
        return lottoGame;
    }

    @Override
    public LottoGame findOldestData() {
        LottoGame lottoGame = null;

        try {
            logger.info("findOldestData: {}", prepareSelectOldestData.toString());
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("oldest data: {}", lottoGame);
        return lottoGame;
    }

    public LottoGame findCurrentData(String date) {
        LottoGame lottoGame = null;

        try {
            prepareSelectCurrentDate.setString(1, date);
            logger.info("findCurrentData: {}", prepareSelectCurrentDate.toString());
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("current data: {}", lottoGame);
        return lottoGame;
    }

    public List<LottoGame> findBetweenDate(String gameStartDate, String gameEndDate) {
        List<LottoGame> lottoGames = new ArrayList<>();

        try {
            prepareSelectBetweenDate.setDate(1, java.sql.Date.valueOf(gameStartDate));
            prepareSelectBetweenDate.setDate(2, java.sql.Date.valueOf(gameEndDate));
            logger.info("findBetweenDate: {}", prepareSelectBetweenDate.toString());
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
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("data between dates: {}", lottoGames);
        return lottoGames;
    }

    @Override
    public Long deleteById(Long id) {
        try {
            prepareDeleteById.setLong(1, id);
            logger.info("deleteById: {}", prepareDeleteById.toString());
            prepareDeleteById.execute();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        logger.info("Id {} deleted", id);
        return id;
    }
}
