package me.ludvick.brisk.walker.bots.telegram.engine;

import me.ludvick.brisk.walker.bots.telegram.constants.Condition;
import me.ludvick.brisk.walker.bots.telegram.constants.Language;
import me.ludvick.brisk.walker.bots.telegram.db.init.DBTextInteraction;


public class Text {
    private DBTextInteraction dbTextInteraction;
    private String dbURL;
    private String dbUsername;
    private String dbPassword;
    private String dbInstance;

    public Text() {
    }

    public Text(String dbURL, String dbUsername, String dbPassword, String dbInstance) {
        this.dbURL = dbURL;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbInstance = dbInstance;
    }

    public void initDBConnection() {
        dbTextInteraction = new DBTextInteraction();
        dbTextInteraction.initConnection(dbURL, dbUsername, dbPassword, dbInstance);
    }

    public void closeDBConnection() {
        dbTextInteraction.closeConnection();
    }

    public String getTextFromDB(String condition, String language) {
        initDBConnection();
        String result = dbTextInteraction.getCurrentTextByLang(condition, language);
        closeDBConnection();
        return result;
    }
}
