package me.ludvick.brisk.walker.bots.telegram;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.init.DbLottoInteraction;
import me.ludvick.brisk.walker.bots.telegram.statistic.PaisLotto;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        InputStream configurationFileIS = Main.class.getClassLoader()
                .getResourceAsStream("constants.properties");
        properties.load(configurationFileIS);

        URL paisLottoURL = new URL(properties.getProperty("pais.lotto.url"));
        File outputFile = new File(properties.getProperty("pais.lotto.output.file.path"));

        PaisLotto lottoFile = new PaisLotto(paisLottoURL, outputFile);
        lottoFile.downloadFileFromURL();
        List<LottoGame> lottoGameList = lottoFile.parseLottoFile();

        DbLottoInteraction dbInteraction = new DbLottoInteraction();
        dbInteraction.initConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password"),
                properties.getProperty("db.tables.lotto.history"));

        System.out.println(lottoGameList.size());

        for (LottoGame lottoGame : lottoGameList) {
            dbInteraction.save(lottoGame);
        }

        dbInteraction.closeConnection();


    }
}