package me.ludvick.brisk.walker.bots.telegram.db.init;

import me.ludvick.brisk.walker.bots.telegram.db.entity.User;
import me.ludvick.brisk.walker.bots.telegram.db.service.DBBehavior;

import java.sql.*;
import java.util.List;

public class DBUserInteraction implements DBBehavior<User, Long> {
    Connection connection;
    PreparedStatement prepareSave;
    PreparedStatement prepareFindById;

    public DBUserInteraction() {
    }

    public DBUserInteraction(Connection connection) {
        this.connection = connection;
    }

    public void initConnection(String url, String username, String password, String tableName) {
        try {
            connection = DriverManager.getConnection(url, username, password);
            prepareSave = connection.prepareStatement("INSERT INTO " + tableName + "(user_id, user_nickname, user_register_date, user_language) VALUES (?, ?, ?, ?)");
            prepareFindById = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE user_id = ?");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() {
        try {
            prepareSave.close();
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save(User user) {
        try {
            prepareSave.setLong(1, user.getId());
            prepareSave.setString(2, user.getNickname());
            prepareSave.setDate(3, user.getRegisterDate());
            prepareSave.setString(4, user.getLanguage());

            System.out.println(prepareSave);
            prepareSave.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List findAll() {
        return null;
    }

    @Override
    public User findById(Long id) {
        User user = null;

        try {
            prepareFindById.setLong(1, id);
            ResultSet rs = prepareFindById.executeQuery();
            while (rs.next()) {
                user = new User(
                        rs.getLong(1),
                        rs.getString(2),
                        rs.getDate(3),
                        rs.getString(4)
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return user;
    }

    @Override
    public User findNewestData() {
        return null;
    }

    @Override
    public User findOldestData() {
        return null;
    }

    @Override
    public Long deleteById(Long id) {
        return 0L;
    }
}
