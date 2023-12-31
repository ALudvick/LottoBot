package me.ludvick.brisk.walker.bots.telegram.engine;

import me.ludvick.brisk.walker.bots.telegram.db.entity.LottoGame;
import me.ludvick.brisk.walker.bots.telegram.db.init.DBLottoInteraction;
import me.ludvick.brisk.walker.bots.telegram.statistic.PaisLotto;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Commands {
    private String paisLottoURL;
    private String outputFile;
    private DBLottoInteraction dbLottoInteraction;
    private String dbURL;
    private String dbUsername;
    private String dbPassword;
    private String dbInstance;

    public Commands() {
    }

    public Commands(String paisLottoURL, String outputFile, String dbURL, String dbUsername, String dbPassword, String dbInstance) {
        this.paisLottoURL = paisLottoURL;
        this.outputFile = outputFile;
        this.dbURL = dbURL;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
        this.dbInstance = dbInstance;
    }

    private void initDBConnection() {
        dbLottoInteraction = new DBLottoInteraction();
        dbLottoInteraction.initConnection(dbURL, dbUsername, dbPassword, dbInstance);
    }

    private void closeDBConnection() {
        dbLottoInteraction.closeConnection();
    }

    public void downloadNewHistoryToDB() throws MalformedURLException {
        PaisLotto lottoFile = new PaisLotto(new URL(paisLottoURL), new File(outputFile));
        lottoFile.downloadFileFromURL();
        List<LottoGame> lottoGameList = lottoFile.parseLottoFile();
        System.out.println(lottoGameList.size());

        initDBConnection();
        for (LottoGame lottoGame : lottoGameList) {
            if (dbLottoInteraction.findCurrentData(lottoGame.getLottoDate().toString()) == null) {
                dbLottoInteraction.save(lottoGame);
            } else {
                System.out.println("LOTTO_ID " + lottoGame.getLottoId() + " EXIST!");
                break;
            }
        }
        closeDBConnection();
    }

    public String getNewestGameDate() {
        initDBConnection();
        LottoGame lottoGame = dbLottoInteraction.findNewestData();
        closeDBConnection();
        return lottoGame.getLottoDate().toString();
    }

    public String getNewestGameData() {
        initDBConnection();
        LottoGame lottoGame = dbLottoInteraction.findNewestData();
        String result = String.format("Date of the last lotto: `%s` \nStrong number: `%d` \nWinner numbers: `%s`",
                new SimpleDateFormat("dd/MM/yyyy").format(lottoGame.getLottoDate()),
                lottoGame.getLottoStrongNumber(),
                Arrays.toString(lottoGame.getLottoRegularNumbers()).replace("[", "").replace("]", "")
        );
        closeDBConnection();
        return result;
    }

    public String getOldestGameDate() {
        initDBConnection();
        LottoGame lottoGame = dbLottoInteraction.findOldestData();
        closeDBConnection();
        return lottoGame.getLottoDate().toString();
    }

    public Map<Integer, Integer> getStatStrongMapBetweenDates(String startDate, String endDate) {
        initDBConnection();

        List<LottoGame> lottoGameList = dbLottoInteraction.findBetweenDate(startDate, endDate);
        Map<Integer, Integer> statisticMap = new HashMap<>();

        System.out.println("TEST MAP METHOD:");
        for (LottoGame lottoGame : lottoGameList) {
            System.out.println("------>" + statisticMap);
            System.out.println("------>" + lottoGame.getLottoStrongNumber());
            System.out.println("------>" + statisticMap.containsKey(lottoGame.getLottoStrongNumber()));

            if (statisticMap.containsKey(lottoGame.getLottoStrongNumber())) {
                System.out.println("TRUE!!!");
                System.out.println(statisticMap.get(lottoGame.getLottoStrongNumber()).intValue());
                statisticMap.put(lottoGame.getLottoStrongNumber(), statisticMap.get(lottoGame.getLottoStrongNumber()) + 1);
            } else {
                statisticMap.put(lottoGame.getLottoStrongNumber(), 1);
            }
        }

        closeDBConnection();
        return statisticMap;
    }

    public Map<Integer, Integer> getStatNumbersMapBetweenDates(String startDate, String endDate) {
        initDBConnection();

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

        closeDBConnection();
        return statisticMap;
    }

    private int getAllNumbersSum(Map<Integer, Integer> numbersMap) {
        int resultSum = 0;

        for (Map.Entry<Integer, Integer> entry: numbersMap.entrySet()){
            resultSum += entry.getValue();
        }

        return resultSum;
    }

    public String getTopFromMap(int topPositions, Map<Integer, Integer> numbersMap) {
        System.out.println(numbersMap);

        Map<Integer, Integer> topNumbers = numbersMap.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .limit(topPositions)
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        StringBuilder resultBuilder = new StringBuilder();

        for (Map.Entry<Integer, Integer> entry : topNumbers.entrySet()) {
            Integer value = entry.getValue();
            Integer key = entry.getKey();
            Double percent = ((double) entry.getValue() / (double) getAllNumbersSum(numbersMap)) * 100f;

            System.out.println(value + " - " + key + " (" + percent + "%)");
            /*
            String str = String.format("%d was among the winning numbers %d times (%.2f%%)!\n",
                    key,
                    value,
                    percent);
             */
            String str = String.format("Number %d was winning %d times (%.2f%%)\n",
                    key,
                    value,
                    percent);
            resultBuilder.append(str);
        }

        System.out.println(resultBuilder.toString());

        return resultBuilder.toString();
    }

    public String getLastFromMap(int topPositions, Map<Integer, Integer> numbersMap) {
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
            String str = String.format("%d was among the winning numbers %d times (%.2f%%)!\n",
                    key,
                    value,
                    percent);
            resultBuilder.append(str);
        }

        System.out.println(resultBuilder.toString());

        return resultBuilder.toString();
    }
}
