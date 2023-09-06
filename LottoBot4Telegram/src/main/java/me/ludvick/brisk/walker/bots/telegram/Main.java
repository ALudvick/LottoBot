package me.ludvick.brisk.walker.bots.telegram;

import me.ludvick.brisk.walker.bots.telegram.statistic.PaisLotto;

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

        String myURL = properties.get("pais.lotto.url").toString();
        System.out.println(myURL);

        URL paisLottoURL = new URL(myURL);
        File outputFile = new File("lotto_results/test.csv");

        PaisLotto paisLotto = new PaisLotto();
        paisLotto.downloadFileFromURL(paisLottoURL, outputFile);

    }
}