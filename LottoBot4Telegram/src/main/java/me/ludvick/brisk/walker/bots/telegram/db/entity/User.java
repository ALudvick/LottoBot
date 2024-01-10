package me.ludvick.brisk.walker.bots.telegram.db.entity;

import me.ludvick.brisk.walker.bots.telegram.constants.Language;

import java.sql.Date;
import java.util.Objects;

public class User {
    long id;
    String nickname;
    Date registerDate;
    String language;

    public User() {
    }

    public User(long id, String nickname, Date registerDate, String language) {
        this.id = id;
        this.nickname = nickname;
        this.registerDate = registerDate;
        this.language = language;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Date getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(Date registerDate) {
        this.registerDate = registerDate;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(nickname, user.nickname) && Objects.equals(registerDate, user.registerDate) && language == user.language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nickname, registerDate, language);
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", registerDate=" + registerDate +
                ", language=" + language +
                '}';
    }
}
