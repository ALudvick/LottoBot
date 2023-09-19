package me.ludvick.brisk.walker.bots.telegram.statistic;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.chrono.ChronoLocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaisLotto implements FileWorker {
    private URL url;
    private File outputFile;

    public PaisLotto() {}

    public PaisLotto(URL url, File outputFile) {
        this.url = url;
        this.outputFile = outputFile;
    }

    public List<LottoGame> parseLottoFile() {
        List<LottoGame> lottoGameList = new ArrayList<>();
        try {
            List<String> fileLines = FileUtils.readLines(outputFile, "UTF-8");
            // 3619,05/09/2023,02,03,05,09,36,37,3,0,0,
            Pattern pattern = Pattern.compile("[0-9]+,[0-9]{2}/[0-9]{2}/[0-9]{4},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2}+");
            Matcher matcher;

            for (String line : fileLines) {
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    System.out.println(matcher.group());
                    String[] tmp = matcher.group().split(",");

                    System.out.println(tmp[1].replace("/", "-"));
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date tmpDate = inputDateFormat.parse(tmp[1]);

                    lottoGameList.add(new LottoGame(
                            Integer.parseInt(tmp[0]),
                            java.sql.Date.valueOf(outputDateFormat.format(tmpDate)),
                            Integer.parseInt(tmp[2]),
                            new Integer[]{
                                    Integer.parseInt(tmp[3]),
                                    Integer.parseInt(tmp[4]),
                                    Integer.parseInt(tmp[5]),
                                    Integer.parseInt(tmp[6]),
                                    Integer.parseInt(tmp[7]),
                                    Integer.parseInt(tmp[8])}
                    ));
                }
            }

        } catch (IOException | ParseException e) {
            throw new RuntimeException(e);
        }

        lottoGameList.forEach(System.out::println);
        return lottoGameList;
    }

    @Override
    public void downloadFileFromURL() {
        try {
            FileUtils.copyURLToFile(url, outputFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteOldestFile() {
        String folder = "";
    }

    @Override
    public void deleteFileByName() {
        File fileToDelete = new File("");
    }
}
