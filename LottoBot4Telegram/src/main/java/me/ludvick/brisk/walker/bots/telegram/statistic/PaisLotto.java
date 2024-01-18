package me.ludvick.brisk.walker.bots.telegram.statistic;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
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
            Pattern pattern = Pattern.compile("[0-9]+,[0-9]{2}/[0-9]{2}/[0-9]{4},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2},[0-9]{1,2}+");
            Matcher matcher;

            for (String line : fileLines) {
                matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String[] tmp = matcher.group().split(",");

                    logger.debug("Matcher group: {}; Date changes: {}", matcher.group(), tmp[1].replace("/", "-"));
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date tmpDate = inputDateFormat.parse(tmp[1]);

                    lottoGameList.add(new LottoGame(
                            UUID.randomUUID().toString(),
                            Integer.parseInt(tmp[0]),
                            java.sql.Date.valueOf(outputDateFormat.format(tmpDate)),
                            Integer.parseInt(tmp[8]),
                            new Integer[]{
                                    Integer.parseInt(tmp[2]),
                                    Integer.parseInt(tmp[3]),
                                    Integer.parseInt(tmp[4]),
                                    Integer.parseInt(tmp[5]),
                                    Integer.parseInt(tmp[6]),
                                    Integer.parseInt(tmp[7])}
                    ));
                }
            }

        } catch (IOException | ParseException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        //lottoGameList.forEach(System.out::println);
        logger.debug("LottoGameList size: {}", lottoGameList.size());
        return lottoGameList;
    }

    @Override
    public void downloadFileFromURL() {
        logger.info("Try to download history file from site...");
        try {
            FileUtils.copyURLToFile(url, outputFile);
            logger.info("File downloaded!");
        } catch (IOException e) {
            logger.error(e.getMessage());
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
