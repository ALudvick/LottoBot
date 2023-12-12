package me.ludvick.brisk.walker.bots.telegram.db.init;

import me.ludvick.brisk.walker.bots.telegram.db.service.DBBehavior;

import java.sql.*;
import java.util.List;

public class DBTextInteraction implements DBBehavior {
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
    public void save(Object o) {

    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public Object findById(int id) {
        return null;
    }

    @Override
    public Object findNewestData() {
        return null;
    }

    @Override
    public Object findOldestData() {
        return null;
    }

    @Override
    public int deleteById(int id) {
        return 0;
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
