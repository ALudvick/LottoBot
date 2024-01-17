package me.ludvick.brisk.walker.bots.telegram.engine;

import me.ludvick.brisk.walker.bots.telegram.constants.Condition;
import me.ludvick.brisk.walker.bots.telegram.constants.Language;
import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.entity.User;
import me.ludvick.brisk.walker.bots.telegram.db.init.DBLottoInteraction;
import me.ludvick.brisk.walker.bots.telegram.db.init.DBTextInteraction;
import me.ludvick.brisk.walker.bots.telegram.db.init.DBUserInteraction;
import me.ludvick.brisk.walker.bots.telegram.statistic.PaisLotto;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Commands {
    private static final Logger logger = LogManager.getLogger(Commands.class.getName());
    private String paisLottoURL;
    private String outputFile;
    private DBLottoInteraction dbLottoInteraction;
    private DBTextInteraction dbTextInteraction;
    private DBUserInteraction dbUserInteraction;
    private String dbURL;
    private String dbUsername;
    private String dbPassword;
    private String dbLottoInstance;
    private String dbTextInstance;
    private String dbUserInstance;

    public Commands() {
    }

    public Commands(String paisLottoURL, String outputFile, String dbURL, String dbUsername, String dbPassword, String dbLottoInstance, String dbTextInstance, String dbUserInstance) {
        this.paisLottoURL = paisLottoURL;
        this.outputFile = outputFile;
        this.dbURL = dbURL;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbLottoInstance = dbLottoInstance;
        this.dbTextInstance = dbTextInstance;
        this.dbUserInstance = dbUserInstance;
    }

    public void initDBConnection() {
        dbLottoInteraction = new DBLottoInteraction();
        dbLottoInteraction.initConnection(dbURL, dbUsername, dbPassword, dbLottoInstance);
        dbTextInteraction = new DBTextInteraction();
        dbTextInteraction.initConnection(dbURL, dbUsername, dbPassword, dbTextInstance);
        dbUserInteraction = new DBUserInteraction();
        dbUserInteraction.initConnection(dbURL, dbUsername, dbPassword, dbUserInstance);
    }

    private void initDBLottoConnection() {
        dbLottoInteraction = new DBLottoInteraction();
        dbLottoInteraction.initConnection(dbURL, dbUsername, dbPassword, dbLottoInstance);
    }

    private void initDBTextConnection() {
        dbTextInteraction = new DBTextInteraction();
        dbTextInteraction.initConnection(dbURL, dbUsername, dbPassword, dbTextInstance);
    }

    private void initDBUserConnection() {
        dbUserInteraction = new DBUserInteraction();
        dbUserInteraction.initConnection(dbURL, dbUsername, dbPassword, dbUserInstance);
    }

    public void closeDBConnection() {
        dbLottoInteraction.closeConnection();
        dbTextInteraction.closeConnection();
        dbUserInteraction.closeConnection();
    }

    public void downloadNewHistoryToDB() throws MalformedURLException {
        PaisLotto lottoFile = new PaisLotto(new URL(paisLottoURL), new File(outputFile));
        lottoFile.downloadFileFromURL();
        List<LottoGame> lottoGameList = lottoFile.parseLottoFile();
        System.out.println(lottoGameList.size());

        for (LottoGame lottoGame : lottoGameList) {
            if (dbLottoInteraction.findCurrentData(lottoGame.getLottoDate().toString()) == null) {
                dbLottoInteraction.save(lottoGame);
            } else {
                logger.info("lottoId {} exist! Break the process!", lottoGame.getLottoId());
                break;
            }
        }
    }

    public String getNewestGameDate() {
        LottoGame lottoGame = dbLottoInteraction.findNewestData();
        return lottoGame.getLottoDate().toString();
    }

    public String getNewestGameData(Language language) {
        LottoGame lottoGame = dbLottoInteraction.findNewestData();
        String result = String.format(getTextFromDB(Condition.LAST_GAME_RESULT.name(), language.name()),
                new SimpleDateFormat("dd/MM/yyyy").format(lottoGame.getLottoDate()),
                lottoGame.getLottoStrongNumber(),
                Arrays.toString(lottoGame.getLottoRegularNumbers()).replace("[", "").replace("]", "")
        );
        return result;
    }

    public String getOldestGameDate() {
        LottoGame lottoGame = dbLottoInteraction.findOldestData();
        return lottoGame.getLottoDate().toString();
    }

    public Map<Integer, Integer> getStatStrongMapBetweenDates(String startDate, String endDate) {
        List<LottoGame> lottoGameList = dbLottoInteraction.findBetweenDate(startDate, endDate);
        Map<Integer, Integer> statisticMap = new HashMap<>();

        for (LottoGame lottoGame : lottoGameList) {
            logger.debug("Map: {}; Number: {}, Checker: {}", statisticMap, lottoGame.getLottoStrongNumber(), statisticMap.containsKey(lottoGame.getLottoStrongNumber()));

            if (statisticMap.containsKey(lottoGame.getLottoStrongNumber())) {
                statisticMap.put(lottoGame.getLottoStrongNumber(), statisticMap.get(lottoGame.getLottoStrongNumber()) + 1);
            } else {
                statisticMap.put(lottoGame.getLottoStrongNumber(), 1);
            }
        }
        return statisticMap;
    }

    public Map<Integer, Integer> getStatNumbersMapBetweenDates(String startDate, String endDate) {
        List<LottoGame> lottoGameList = dbLottoInteraction.findBetweenDate(startDate, endDate);
        Map<Integer, Integer> statisticMap = new HashMap<>();
        for (LottoGame lottoGame : lottoGameList) {
            for (int i : lottoGame.getLottoRegularNumbers()) {
                if (statisticMap.containsKey(i)) {
                    statisticMap.put(i, statisticMap.get(i) + 1);
                } else {
                    statisticMap.put(i, 1);
                }
            }
        }
        return statisticMap;
    }

    private int getAllNumbersSum(Map<Integer, Integer> numbersMap) {
        int resultSum = 0;

        for (Map.Entry<Integer, Integer> entry: numbersMap.entrySet()){
            resultSum += entry.getValue();
        }

        return resultSum;
    }

    public String getTopFromMap(int topPositions, Map<Integer, Integer> numbersMap, Language language) {
        logger.debug("getTopFromMap {}", numbersMap);

        Map<Integer, Integer> topNumbers = numbersMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(topPositions)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        StringBuilder resultBuilder = new StringBuilder();

        for (Map.Entry<Integer, Integer> entry : topNumbers.entrySet()) {
            Integer value = entry.getValue();
            Integer key = entry.getKey();
            Double percent = ((double) entry.getValue() / (double) getAllNumbersSum(numbersMap)) * 100f;

            logger.debug("Value: {}, Key: {}, Percent: {}", value, key, percent);

            String str = String.format(getTextFromDB(Condition.NUMBERS_RESULT.name(), language.name()),
                    key,
                    value,
                    percent);
            resultBuilder.append(str);
        }

        return resultBuilder.toString();
    }

    public String getLastFromMap(int topPositions, Map<Integer, Integer> numbersMap, Language language) {
        System.out.println(numbersMap);

        Map<Integer, Integer> topNumbers = numbersMap.entrySet().stream()
                .sorted((Map.Entry.comparingByValue()))
                .limit(topPositions)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        StringBuilder resultBuilder = new StringBuilder();

        for (Map.Entry<Integer, Integer> entry : topNumbers.entrySet()) {
            Integer value = entry.getValue();
            Integer key = entry.getKey();
            Double percent = ((double) entry.getValue() / (double) getAllNumbersSum(numbersMap)) * 100f;

            System.out.println(value + " - " + key + " (" + percent + "%)");
            String str = String.format(getTextFromDB(Condition.NUMBERS_RESULT.name(), language.name()),
                    key,
                    value,
                    percent);
            resultBuilder.append(str);
        }

        System.out.println(resultBuilder.toString());

        return resultBuilder.toString();
    }

    public String getTextFromDB(String condition, String language) {
        String result = dbTextInteraction.getCurrentTextByLang(condition, language);
        return result;
    }

    public void saveUser(User user) {
        if(dbUserInteraction.findById(user.getId()) == null) {
            dbUserInteraction.save(user);
        }
    }
}
