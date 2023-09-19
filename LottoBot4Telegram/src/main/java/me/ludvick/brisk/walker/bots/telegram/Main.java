package me.ludvick.brisk.walker.bots.telegram;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.init.DbLottoInteraction;

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

        DbLottoInteraction dbInteraction = new DbLottoInteraction();
        dbInteraction.initConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password"),
                properties.getProperty("db.tables.lotto.history"));


        LottoGame game1 = new LottoGame(
                999,
                java.sql.Date.valueOf("2023-09-16"),
                9,
                new Integer[]{7, 8, 9, 0, 1, 2}
        );

//        List<LottoGame> lottoGames = new ArrayList<>();
//        dbInteraction.saveGame(game1);
//        lottoGames = dbInteraction.findAll();
//        System.out.println(dbInteraction.findGameById(777));
//        System.out.println(dbInteraction.findGameByDate("2023-09-16"));
//        dbInteraction.deleteGameById(777);
//        List<LottoGame> lottoGames = dbInteraction.findAll();
//
//        System.out.println("Lotto Games: ");
//        for (LottoGame game : lottoGames) {
//            System.out.println(game);
//        }

        dbInteraction.closeConnection();

        /* //Download file with statistic

        PaisLotto paisLotto = new PaisLotto();
        paisLotto.downloadFileFromURL(paisLottoURL, outputFile);

         */

//        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
//        Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();

//        SessionFactory factory = meta.getSessionFactoryBuilder().build();
//        Session session = factory.openSession();
//        Transaction t = session.beginTransaction();

//        OfficialResultsHistory history = new OfficialResultsHistory();
//        history.setId(3619);
//        history.setLottoDate(java.sql.Date.valueOf("2023-09-05"));
//        history.setStrongNumber(3);
//        Integer[] arr = new Integer[]{1, 2, 3, 4, 5};
//        history.setLottoNumbers(arr);

//        u1.setId(101);
//        u1.setUserName("Admin");
//        u1.setRegistrationDate(new Date().getTime());
//        u1.setRegion("IS");
//        u1.setRequestFlag(false);

//        session.save(history);
//        t.commit();
//        System.out.println("successfully saved");
//        factory.close();
//        session.close();

    }
}