package me.ludvick.brisk.walker.bots.telegram;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.init.DBInteraction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        InputStream configurationFileIS = Main.class.getClassLoader()
                .getResourceAsStream("constants.properties");
        properties.load(configurationFileIS);

        URL paisLottoURL = new URL(properties.getProperty("pais.lotto.url"));
        File outputFile = new File(properties.getProperty("pais.lotto.output.file.path"));

        DBInteraction dbInteraction = new DBInteraction();
        dbInteraction.initConnection(
                properties.getProperty("db.url"),
                properties.getProperty("db.username"),
                properties.getProperty("db.password"),
                "lotto_history");


        LottoGame game1 = new LottoGame(
                777,
                java.sql.Date.valueOf("2023-09-13"),
                7,
                2,3,5,9,36,37
        );
        dbInteraction.saveGame(game1);

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