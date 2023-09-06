package me.ludvick.brisk.walker.bots.telegram;

import me.ludvick.brisk.walker.bots.telegram.db.entity.OfficialResultsHistory;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class Main {
    public static void main(String[] args) throws IOException {

        Properties properties = new Properties();
        InputStream configurationFileIS = Main.class.getClassLoader()
                .getResourceAsStream("constants.properties");
        properties.load(configurationFileIS);

        URL paisLottoURL = new URL(properties.getProperty("pais.lotto.url"));
        File outputFile = new File(properties.getProperty("pais.lotto.output.file.path"));

        /* //Download file with statistic

        PaisLotto paisLotto = new PaisLotto();
        paisLotto.downloadFileFromURL(paisLottoURL, outputFile);

         */

        StandardServiceRegistry ssr = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
        Metadata meta = new MetadataSources(ssr).getMetadataBuilder().build();

        SessionFactory factory = meta.getSessionFactoryBuilder().build();
        Session session = factory.openSession();
        Transaction t = session.beginTransaction();

        OfficialResultsHistory history = new OfficialResultsHistory();
        history.setId(3619);
        history.setLottoDate(java.sql.Date.valueOf("2023-09-05"));
        history.setStrongNumber(3);
        history.setLottoNumbers(new int[]{2, 3, 5, 9, 36, 37});

//        u1.setId(101);
//        u1.setUserName("Admin");
//        u1.setRegistrationDate(new Date().getTime());
//        u1.setRegion("IS");
//        u1.setRequestFlag(false);

        session.save(history);
        t.commit();
        System.out.println("successfully saved");
        factory.close();
        session.close();

    }
}