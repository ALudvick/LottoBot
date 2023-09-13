package me.ludvick.brisk.walker.bots.telegram.db.init;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.service.DBBehavior;

import java.sql.*;
import java.util.ArrayList;
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
            prepareSave = connection.prepareStatement("INSERT INTO " + tableName + " (id, lotto_date, strong_number, lotto_num_1, lotto_num_2, lotto_num_3, lotto_num_4, lotto_num_5, lotto_num_6) values (?, ?, ?, ?, ?, ?, ?, ?, ?);");
            prepareSelectAll = connection.prepareStatement("SELECT * FROM " + tableName + ";");
            prepareSelectById = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE id = ?;");
            prepareSelectByDate = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE lotto_date = ?;");
            prepareDeleteById = connection.prepareStatement("DELETE FROM " + tableName + " WHERE id = ?;");
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
            prepareSave.setInt(1, lottoGame.getId());
            prepareSave.setDate(2, lottoGame.getLottoDate());
            prepareSave.setInt(3, lottoGame.getStrongNumber());
            prepareSave.setInt(4, lottoGame.getLottoNumber1());
            prepareSave.setInt(5, lottoGame.getLottoNumber2());
            prepareSave.setInt(6, lottoGame.getLottoNumber3());
            prepareSave.setInt(7, lottoGame.getLottoNumber4());
            prepareSave.setInt(8, lottoGame.getLottoNumber5());
            prepareSave.setInt(9, lottoGame.getLottoNumber6());

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
            ResultSet rs = prepareSelectAll.executeQuery();
            for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
                System.out.println(rs.toString());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return lottoGames;
    }

    @Override
    public LottoGame findGameById(int id) {
        return null;
    }

    @Override
    public LottoGame findGameByDate(String gameDate) {
        return null;
    }

    @Override
    public int deleteGameById(int id) {
        return 0;
    }
}
