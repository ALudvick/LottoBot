package me.ludvick.brisk.walker.bots.telegram.engine;

import me.ludvick.brisk.walker.bots.telegram.Main;
import me.ludvick.brisk.walker.bots.telegram.constants.Condition;
import me.ludvick.brisk.walker.bots.telegram.constants.Language;
import me.ludvick.brisk.walker.bots.telegram.db.entity.User;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Bot extends TelegramLongPollingBot {
    //private static final Logger logger = LoggerFactory.getLogger(Bot.class.getName());
    private static final Logger logger = LogManager.getLogger(Bot.class.getName());
    private Properties properties;
    private Commands commands;
    private User user;

    public Bot(String botToken) {
        super(botToken);
        properties = new Properties();
        try {
            // Loading the property file with all additional information
            logger.info("Try to load properties...");
            InputStream configurationFileIS = Main.class.getClassLoader()
                    .getResourceAsStream("constants.properties");
            properties.load(configurationFileIS);

            // Creating commands object. Commands object will interact with db and make main logic
            logger.info("Commands object...");
            commands = new Commands(
                    properties.getProperty("pais.lotto.url"),
                    properties.getProperty("pais.lotto.output.file.path"),
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password"),
                    properties.getProperty("db.tables.lotto.history"),
                    properties.getProperty("db.tables.lotto.text"),
                    properties.getProperty("db.tables.lotto.users")
            );

            // init DB connections
            commands.initDBConnection();

            // Download statistic file from official web-site
            logger.info("Schedule executer thread...");
            scheduledExecute(
                    Integer.parseInt(properties.getProperty("bot.execute.download.delay")),
                    Integer.parseInt(properties.getProperty("bot.execute.download.period"))
            );

        } catch (IOException e) {
            logger.error(e.getMessage());
            //commands.closeDBConnection();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            logger.info("Bot have a message");

            user = new User(update.getMessage().getFrom().getId(),
                    update.getMessage().getFrom().getUserName(),
                    new java.sql.Date(new Date().getTime()),
                    update.getMessage().getFrom().getLanguageCode()
            );
            commands.saveUser(user);

            String userLanguage =  update.getMessage().getFrom().getLanguageCode();
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            logger.info("ChatID: {}; Language: {}; Message: {}", chatId, userLanguage, messageText);

            if ("/start".equals(messageText)) {
                sendHelpMessage(chatId, getLanguageByName(userLanguage));
                sendMainMenu(chatId, getLanguageByName(userLanguage));
            } else if ("/menu".equals(messageText)) {
                sendMainMenu(chatId, getLanguageByName(userLanguage));
            } else if (isValidDateRange(messageText)) {
                sendCustomTop(chatId, messageText, getLanguageByName(userLanguage));
            } else {
                sendErrorMessage(chatId, getLanguageByName(userLanguage));
            }
        } else if (update.hasCallbackQuery()) {
            logger.info("Bot have a callback query");
            String userLanguage = update.getCallbackQuery().getFrom().getLanguageCode();
            handleCallbackQuery(update.getCallbackQuery(), getLanguageByName(userLanguage));
        }
    }

    @Override
    public String getBotUsername() {
        return "MyUserInfo_tlgrm_BOT";
    }

    // To check user input date range
    private static boolean isValidDateRange(String input) {
        logger.info("Date range: {}", input);
        String[] dateParts = input.split(" - ");
        logger.debug(dateParts);

        if (dateParts.length != 2) {
            logger.error("Wrong parts number");
            return false; // Wrong parts number
        }

        if (!isValidDate(dateParts[0]) || !isValidDate(dateParts[1])) {
            logger.error("Wrong dates range ");
            return false; // Wrong dates range
        }

        logger.info("Dates range are valid");
        return true;
    }

    // To check date format
    private static boolean isValidDate(String date) {
        logger.info("Date: {}", date);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        try {
            dateFormat.parse(date);
            logger.info("Date is valid");
            return true;
        } catch (ParseException e) {
            logger.error("Date is not valid");
            return false;
        }
    }

    // Date format converter
    private static String convertDateFormat(String inputDate) {
        logger.info("Input date: {}", inputDate);
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;

        try {
            date = inputDateFormat.parse(inputDate);
            logger.info("Output date: {}", date);
        } catch (ParseException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }

        return outputDateFormat.format(date);
    }

    // To send bot action (like "typing" in status bar)
    private void sendAction(long chatId, ActionType actionType) {
        logger.info("Sending action");
        try {
            execute(SendChatAction.builder()
                    .chatId(chatId)
                    .action(String.valueOf(actionType))
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    // Try to get language from user default settings
    private Language getLanguageByName(String userLanguage) {
        logger.info("Try to get language from user. By default - ENG");
        switch (userLanguage) {
            case "en": return Language.ENG;
            case "ru": return Language.RUS;
            default: return Language.ENG;
        }
    }

    // Generate MainMenu
    public void sendMainMenu(long chatId, Language language) {
        logger.info("Try to send MainMenu");

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(commands.getTextFromDB(Condition.MAIN_MENU.name(), language.name()))
                    .replyMarkup(getMainMenuInlineKeyboard(language))
                    .build());
            logger.info("MainMenu has been sent");
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // This is greeting generator
    public void sendHelpMessage(long chatId, Language language) {
        logger.info("Try to send Greeting");
        sendAction(chatId, ActionType.TYPING);

        String message = commands.getTextFromDB(Condition.GREETING.name(), language.name());
        logger.info("Message: {}", message);

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .parseMode("Markdown")
                    .text(message)
                    .build());
            logger.info("Greeting has been sent");
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // If bot don't understand a command
    public void sendErrorMessage(long chatId, Language language) {
        logger.info("Try to send ErrorMessage");
        sendAction(chatId, ActionType.TYPING);

        String message = commands.getTextFromDB(Condition.ERROR.name(), language.name());
        logger.info("Message: {}", message);

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .parseMode("Markdown")
                    .text(message)
                    .build());
            logger.info("ErrorMessage has been sent");
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    // Generate customTop
    public void sendCustomTop(long chatId, String userDate, Language language) {
        logger.info("Try to send CustomTop (by user dates)");
        String[] dates = userDate.split(" - ");
        if (dates.length == 2) {
            String startDate = convertDateFormat(dates[0]);
            String endDate = convertDateFormat(dates[1]);

            sendAction(chatId, ActionType.TYPING);
            List<String> messages = getTopOrLastMessage("top", 6, endDate, startDate, language, String.valueOf(chatId));
            logger.info("Messages: {}", messages);

            for (String message : messages) {
                try {
                    execute(SendMessage.builder()
                            .chatId(chatId)
                            .parseMode("Markdown")
                            .text(message)
                            .build());
                    logger.info("Message part has been sent");
                } catch (TelegramApiException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Method to catch button pressing
    private void handleCallbackQuery(CallbackQuery callbackQuery, Language language) {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        long messageId = callbackQuery.getMessage().getMessageId();
        logger.info("Try to get positions...");
        int position = Integer.parseInt(properties.getProperty("bot.statistic.positions"));

        logger.info("ChatId: {}; MessageId: {}; Language: {}, Data: {}", chatId, messageId, language, data);

        // Handle different button callbacks
        if ("lastGame".equals(data)) {
            logger.info("LastGame condition");
            sendAction(chatId, ActionType.TYPING);
            String message = commands.getNewestGameData(language);
            logger.info("Message: {}", message);

            try {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .parseMode("Markdown")
                        .text(message)
                        .build());
                logger.info("LastGame message has been sent");
            } catch (TelegramApiException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

        } else if ("lastMonth".equals(data)
                || "lastYear".equals(data)
                || "allTime".equals(data)) {
            logger.info("Not a last game condition...");

            editMessageWithSubMenu(chatId, messageId, data, language, position);
        } else if ("back".equals(data)) {
            logger.info("Back button");
            editMessageWithMainMenu(chatId, messageId, language);
        } else if (data.contains("top;" + position) || data.contains("last;" + position)) {
            logger.info("Top or Last condition");
            String[] userCases = data.split(";");
            logger.info("User case: {}", userCases[0]);

            sendAction(chatId, ActionType.TYPING);

            String startDate = commands.getNewestGameDate();
            String endDate = "";
            logger.info("StartDate: {}", startDate);

            switch (userCases[0]) {
                case "lastMonth":
                    endDate = LocalDate.parse(startDate).minusMonths(1).toString();
                    logger.info("LastMonth; EndDate: {}", endDate);
                    break;
                case "lastYear":
                    endDate = LocalDate.parse(startDate).minusYears(1).toString();
                    logger.info("LastYear; EndDate: {}", endDate);
                    break;
                default:
                    endDate = commands.getOldestGameDate();
                    logger.info("AllTime; EndDate: {}", endDate);
                    break;
            }

            List<String> messages = getTopOrLastMessage(userCases[1], Integer.parseInt(userCases[2]), startDate, endDate, language, String.valueOf(chatId));
            logger.info("Messages list was created ");

            File userFolder = new File(String.valueOf(chatId));
            File[] files = userFolder.listFiles();
            int i = 0;

            for (String message : messages) {
                try {
//                    execute(SendMessage.builder()
//                            .chatId(chatId)
//                            .parseMode("Markdown")
//                            .text(message)
//                            .build());
                    if (files != null) {
                        execute(SendPhoto.builder()
                                .chatId(chatId)
                                .parseMode("Markdown")
                                .photo(new InputFile(files[i]))
                                .caption(message)
                                .build());
                        i++;
                    }
                    logger.info("Message: {}", message.replace("\n", " "));
                } catch (TelegramApiException e) {
                    logger.error(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }
    }

    // Generator of Top or Last messages
    private List<String> getTopOrLastMessage(String condition, int positions, String startDate, String endDate, Language language, String folderName) {
        List<String> resultMessages = new ArrayList<>();
        logger.info("Condition: {}; Positions: {}; StartDate: {}; EndDate: {}", condition, positions, startDate, endDate);

        try {
            Files.createDirectory(Paths.get(folderName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (condition.contains("top")) {
            logger.info("Condition equals \"top\"");
            String greetingStrong = commands.getTextFromDB(Condition.TOP_STRONG.name(), language.name());
            Map<Integer, Integer> strongResultMap = commands.getStatStrongMapBetweenDates(endDate, startDate);
            resultMessages.add(greetingStrong + "`" + commands.getTopFromMap(positions, strongResultMap, language) + "`");
            commands.getBarChart(strongResultMap, folderName + "/01strong.jpeg", "Strong numbers");

            Map<Integer, Integer> numberResultMap = commands.getStatNumbersMapBetweenDates(endDate, startDate);
            String greetingNumbers = commands.getTextFromDB(Condition.TOP_REGULAR.name(), language.name());
            resultMessages.add(greetingNumbers + "`" + commands.getTopFromMap(positions, numberResultMap, language) + "`");
            commands.getBarChart(numberResultMap, folderName + "/02regular.jpeg", "Regular numbers");
        } else if (condition.contains("last")) {
            String greetingStrong =  commands.getTextFromDB(Condition.LAST_STRONG.name(), language.name());
            Map<Integer, Integer> strongResultMap = commands.getStatStrongMapBetweenDates(endDate, startDate);
            resultMessages.add(greetingStrong + "`" + commands.getLastFromMap(positions, strongResultMap, language) + "`");

            Map<Integer, Integer> numberResultMap = commands.getStatNumbersMapBetweenDates(endDate, startDate);
            String greetingNumbers = commands.getTextFromDB(Condition.LAST_REGULAR.name(), language.name());
            resultMessages.add(greetingNumbers + "`" + commands.getLastFromMap(positions, numberResultMap, language) + "`");
        }

        logger.info("Result messages list created");
        return resultMessages;
    }

    // Send MainMenu
    private void editMessageWithMainMenu(long chatId, long messageId, Language language) {
        logger.info("EditMessageWithMainMenu, Language: {}", language.name());
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text(commands.getTextFromDB(Condition.MAIN_MENU.name(), language.name()))
                .replyMarkup(getMainMenuInlineKeyboard(language)) // Replace with your updated main menu inline keyboard
                .build();

        try {
            logger.info("Message has been sent");
            execute(editMessageText); // Edit the original message with the updated main menu
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    // Send SubMenu
    private void editMessageWithSubMenu(long chatId, long messageId, String data, Language language, int position) {
        logger.info("EditMessageWithSubMenu; Language: {}; Position: {}", language.name(), position);
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text(commands.getTextFromDB(Condition.SUB_MENU.name(), language.name()))
                .replyMarkup(getSubMenuInlineKeyboard(data, language, position))
                .build();

        try {
            logger.info("Message has been sent");
            execute(editMessageText); // Edit the original message with the updated main menu
        } catch (TelegramApiException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    // Making MainMenu Keyboard
    private InlineKeyboardMarkup getMainMenuInlineKeyboard(Language language) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton lastGameButton = new InlineKeyboardButton();
        lastGameButton.setText(commands.getTextFromDB(Condition.BUTTON_LAST_GAME.name(), language.name()));
        //lastGameButton.setCallbackData(commands.getNewestGameDate());
        lastGameButton.setCallbackData(Condition.BUTTON_LAST_GAME.toString());
        logger.debug("lastGameButton text: {}; lastGameButton code: {}", commands.getTextFromDB(Condition.BUTTON_LAST_GAME.name(), language.name()), Condition.BUTTON_LAST_GAME.toString());

        InlineKeyboardButton monthGameButton = new InlineKeyboardButton();
        monthGameButton.setText(commands.getTextFromDB(Condition.BUTTON_LAST_MONTH.name(), language.name()));
        //monthGameButton.setCallbackData(LocalDate.parse(commands.getNewestGameDate()).minusMonths(1).toString());
        monthGameButton.setCallbackData(Condition.BUTTON_LAST_MONTH.toString());
        logger.debug("monthGameButton text: {}; monthGameButton code: {}", commands.getTextFromDB(Condition.BUTTON_LAST_MONTH.name(), language.name()), Condition.BUTTON_LAST_MONTH.toString());

        InlineKeyboardButton yearGameButton = new InlineKeyboardButton();
        yearGameButton.setText(commands.getTextFromDB(Condition.BUTTON_LAST_YEAR.name(), language.name()));
        //yearGameButton.setCallbackData(LocalDate.parse(commands.getNewestGameDate()).minusYears(1).toString());
        yearGameButton.setCallbackData(Condition.BUTTON_LAST_YEAR.toString());
        logger.debug("yearGameButton text: {}; yearGameButton code: {}", commands.getTextFromDB(Condition.BUTTON_LAST_YEAR.name(), language.name()), Condition.BUTTON_LAST_YEAR.toString());

        InlineKeyboardButton allTimeGameButton = new InlineKeyboardButton();
        allTimeGameButton.setText(commands.getTextFromDB(Condition.BUTTON_ALL_TIME.name(), language.name()));
        allTimeGameButton.setCallbackData(Condition.BUTTON_ALL_TIME.toString());
        logger.debug("allTimeGameButton text: {}; allTimeGameButton code: {}", commands.getTextFromDB(Condition.BUTTON_ALL_TIME.name(), language.name()), Condition.BUTTON_ALL_TIME.toString());

        List<InlineKeyboardButton> firstRowButtonsList = new ArrayList<>();
        firstRowButtonsList.add(lastGameButton);
        firstRowButtonsList.add(monthGameButton);

        List<InlineKeyboardButton> secondRowButtonsList = new ArrayList<>();
        secondRowButtonsList.add(yearGameButton);
        secondRowButtonsList.add(allTimeGameButton);

        List<List<InlineKeyboardButton>> buttonsList = new ArrayList<>();
        buttonsList.add(firstRowButtonsList);
        buttonsList.add(secondRowButtonsList);

        inlineKeyboardMarkup.setKeyboard(buttonsList);
        return inlineKeyboardMarkup;
    }

    // Making SubMenu Keyboard
    private InlineKeyboardMarkup getSubMenuInlineKeyboard(String mainChoose, Language language, int position) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton topButton = new InlineKeyboardButton();
        topButton.setText(commands.getTextFromDB(Condition.BUTTON_TOP.name(), language.name()) + position);
        //lastGameButton.setCallbackData(commands.getNewestGameDate());
        topButton.setCallbackData(mainChoose + ";" + Condition.BUTTON_TOP + ";" + position);
        logger.debug("topButton text: {}; topButton code: {}", commands.getTextFromDB(Condition.BUTTON_TOP.name(), language.name()) + position, mainChoose + ";" + Condition.BUTTON_TOP + ";" + position);

        InlineKeyboardButton lastButton = new InlineKeyboardButton();
        lastButton.setText(commands.getTextFromDB(Condition.BUTTON_LAST.name(), language.name()) + position);
        //monthGameButton.setCallbackData(LocalDate.parse(commands.getNewestGameDate()).minusMonths(1).toString());
        lastButton.setCallbackData(mainChoose + ";" + Condition.BUTTON_LAST + ";" + position);
        logger.debug("lastButton text: {}; lastButton code: {}", commands.getTextFromDB(Condition.BUTTON_LAST.name(), language.name()) + position, mainChoose + ";" + Condition.BUTTON_LAST + ";" + position);

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText(commands.getTextFromDB(Condition.BUTTON_BACK.name(), language.name()));
        backButton.setCallbackData(Condition.BUTTON_BACK.toString());
        logger.debug("backButton text: {}; backButton code: {}", commands.getTextFromDB(Condition.BUTTON_BACK.name(), language.name()), Condition.BUTTON_BACK.toString());

        List<InlineKeyboardButton> firstRowButtonsList = new ArrayList<>();
        firstRowButtonsList.add(topButton);
        firstRowButtonsList.add(lastButton);

        List<InlineKeyboardButton> secondRowButtonsList = new ArrayList<>();
        secondRowButtonsList.add(backButton);

        List<List<InlineKeyboardButton>> buttonsList = new ArrayList<>();
        buttonsList.add(firstRowButtonsList);
        buttonsList.add(secondRowButtonsList);

        inlineKeyboardMarkup.setKeyboard(buttonsList);
        return inlineKeyboardMarkup;
    }

    // Schedule task
    private void scheduledExecute(int initialDelay, int period) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {

            try {
                logger.info("Downloading history of a lotto games");
                commands.downloadNewHistoryToDB();
            } catch (MalformedURLException e) {
                logger.error(e.getMessage());
                throw new RuntimeException(e);
            }

        }, initialDelay, period, TimeUnit.HOURS);
    }
}

