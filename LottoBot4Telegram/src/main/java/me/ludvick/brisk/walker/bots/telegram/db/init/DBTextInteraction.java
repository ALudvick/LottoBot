package me.ludvick.brisk.walker.bots.telegram.db.init;

import me.ludvick.brisk.walker.bots.telegram.db.service.DBBehavior;

import java.sql.*;
import java.util.List;

public class DBTextInteraction implements DBBehavior<String, Long> {
    Connection connection;
    PreparedStatement prepareSelectText;

    public DBTextInteraction() {
    }

    public DBTextInteraction(Connection connection) {
        this.connection = connection;
    }

    public void initConnection(String url, String username, String password, String tableName) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            prepareSelectText = connection.prepareStatement("SELECT text_local FROM " + tableName + " WHERE text_condition = ? AND text_language = ?;");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            connection.close();
            prepareSelectText.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(String str) {

    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public String findById(Long id) {
        return null;
    }

    @Override
    public String findNewestData() {
        return null;
    }

    @Override
    public String findOldestData() {
        return null;
    }

    @Override
    public Long deleteById(Long id) {
        return 0L;
    }

    public String getCurrentTextByLang(String condition, String language) {
        String result = "";

        try {
            prepareSelectText.setString(1, condition);
            prepareSelectText.setString(2, language);
            System.out.println(prepareSelectText.toString());

            ResultSet rs = prepareSelectText.executeQuery();
            while (rs.next()) {
                result = rs.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
