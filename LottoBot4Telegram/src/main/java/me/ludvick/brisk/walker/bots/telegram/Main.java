package me.ludvick.brisk.walker.bots.telegram;

import me.ludvick.brisk.walker.bots.telegram.engine.Bot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.InputStream;
import java.util.*;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties properties = new Properties();
        InputStream configurationFileIS = Main.class.getClassLoader()
                .getResourceAsStream("constants.properties");
        properties.load(configurationFileIS);

        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            String token = properties.getProperty("telegram.bot.token");
            System.out.println(token);
            botsApi.registerBot(new Bot(token));

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}