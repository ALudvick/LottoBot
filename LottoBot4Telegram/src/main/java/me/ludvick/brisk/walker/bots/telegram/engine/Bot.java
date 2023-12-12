package me.ludvick.brisk.walker.bots.telegram.engine;

import me.ludvick.brisk.walker.bots.telegram.Main;
import me.ludvick.brisk.walker.bots.telegram.constants.Condition;
import me.ludvick.brisk.walker.bots.telegram.constants.Language;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

public class Bot extends TelegramLongPollingBot {
    private Properties properties;
    private Commands commands;
    private Text text;

    public Bot(String botToken) {
        super(botToken);
        properties = new Properties();
        try {
            InputStream configurationFileIS = Main.class.getClassLoader()
                    .getResourceAsStream("constants.properties");
            properties.load(configurationFileIS);

            commands = new Commands(
                    properties.getProperty("pais.lotto.url"),
                    properties.getProperty("pais.lotto.output.file.path"),
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password"),
                    properties.getProperty("db.tables.lotto.history"));

            text = new Text(
                    properties.getProperty("db.url"),
                    properties.getProperty("db.username"),
                    properties.getProperty("db.password"),
                    properties.getProperty("db.tables.lotto.text")
            );

            //commands.downloadNewHistoryToDB();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            long chatId = update.getMessage().getChatId();
            String messageText = update.getMessage().getText();
            String userLanguage =  update.getMessage().getFrom().getLanguageCode();


            if ("/start".equals(messageText)) {
                sendHelpMessage(chatId, getLanguageByName(userLanguage));
                sendMainMenu(chatId);
            } else if ("/menu".equals(messageText)) {
                sendMainMenu(chatId);
            } else if (isValidDateRange(messageText)) {
                sendCustomTop(chatId, messageText);
            } else {
                sendErrorMessage(chatId, getLanguageByName(userLanguage));
            }
        } else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update.getCallbackQuery());
        }
    }

    @Override
    public String getBotUsername() {
        return "MyUserInfo_tlgrm_BOT";
    }

    private static boolean isValidDateRange(String input) {
        String[] dateParts = input.split(" - ");

        if (dateParts.length != 2) {
            return false; // Неправильное количество частей
        }

        if (!isValidDate(dateParts[0]) || !isValidDate(dateParts[1])) {
            return false; // Один из диапазонов дат неверен
        }

        return true;
    }

    private static boolean isValidDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false); // Строгая проверка даты

        try {
            dateFormat.parse(date);
            return true; // Дата валидна
        } catch (ParseException e) {
            return false; // Неверный формат даты
        }
    }

    private void sendAction(long chatId, ActionType actionType) {
        try {
            execute(SendChatAction.builder()
                    .chatId(chatId)
                    .action(String.valueOf(actionType))
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private Language getLanguageByName(String userLanguage) {
        switch (userLanguage) {
            case "en": return Language.ENG;
            case "ru": return Language.RUS;
            default: return Language.ENG;
        }
    }

    public void sendMainMenu(long chatId) {
        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .text(text.getTextFromDB(Condition.MAIN_MENU.name(), Language.ENG.name()))
                    .replyMarkup(getMainMenuInlineKeyboard())
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHelpMessage(long chatId, Language language) {
        sendAction(chatId, ActionType.TYPING);

        String message = text.getTextFromDB(Condition.GREETING.name(), language.name());

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .parseMode("Markdown")
                    .text(message)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendErrorMessage(long chatId, Language language) {
        sendAction(chatId, ActionType.TYPING);

        String message = text.getTextFromDB(Condition.ERROR.name(), language.name());

        try {
            execute(SendMessage.builder()
                    .chatId(chatId)
                    .parseMode("Markdown")
                    .text(message)
                    .build());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendCustomTop(long chatId, String userDate) {
        String[] dates = userDate.split(" - ");
        if (dates.length == 2) {
            String startDate = convertDateFormat(dates[0]);
            String endDate = convertDateFormat(dates[1]);

            sendAction(chatId, ActionType.TYPING);
            List<String> messages = getTopOrLastMessage("top", 10, endDate, startDate);
            System.out.println(messages);

            for (String message : messages) {
                try {
                    execute(SendMessage.builder()
                            .chatId(chatId)
                            .parseMode("Markdown")
                            .text(message)
                            .build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private static String convertDateFormat(String inputDate) {
        SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date date = null;

        try {
            date = inputDateFormat.parse(inputDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        return outputDateFormat.format(date);
    }

    private void handleCallbackQuery(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        long chatId = callbackQuery.getMessage().getChatId();
        long messageId = callbackQuery.getMessage().getMessageId(); // Get the message ID

        System.out.println(data);
        // Handle different button callbacks
        if ("lastGame".equals(data)) {
            System.out.println(data);

            sendAction(chatId, ActionType.TYPING);

            String message = commands.getNewestGameData();

            try {
                execute(SendMessage.builder()
                        .chatId(chatId)
                        .parseMode("Markdown")
                        .text(message)
                        .build());
            } catch (TelegramApiException e) {
                throw new RuntimeException(e);
            }

        } else if ("lastMonth".equals(data)
                || "lastYear".equals(data)
                || "allTime".equals(data)) {
            //sendSubMenu(chatId);
            editMessageWithSubMenu(chatId, messageId, data);
        } else if ("back".equals(data)) {
            editMessageWithMainMenu(chatId, messageId);
        } else if (data.contains("top;10") || data.contains("last;10")) {
            String[] userCases = data.split(";");
            System.out.println(userCases[0]);

            sendAction(chatId, ActionType.TYPING);

            String startDate = commands.getNewestGameDate();
            String endDate = "";

            switch (userCases[0]) {
                case "lastMonth":
                    System.out.println("lastMonth");
                    endDate = LocalDate.parse(startDate).minusMonths(1).toString();
                    break;
                case "lastYear":
                    System.out.println("lastYear");
                    endDate = LocalDate.parse(startDate).minusYears(1).toString();
                    break;
                default:
                    System.out.println("allTime");
                    endDate = commands.getOldestGameDate();
                    break;
            }

            List<String> messages = getTopOrLastMessage(userCases[1], Integer.parseInt(userCases[2]), startDate, endDate);

            System.out.println(messages);

            for (String message : messages) {
                try {
                    execute(SendMessage.builder()
                            .chatId(chatId)
                            .parseMode("Markdown")
                            .text(message)
                            .build());
                } catch (TelegramApiException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private List<String> getTopOrLastMessage(String condition, int positions, String startDate, String endDate) {
        List<String> resultMessages = new ArrayList<>();
        System.out.println(condition);
        System.out.println(positions);
        System.out.println(startDate);
        System.out.println(endDate);


        if (condition.contains("top")) {
            String greetingStrong = "Below are statistics on TOP strong numbers: \n";
            Map<Integer, Integer> strongResultMap = commands.getStatStrongMapBetweenDates(endDate, startDate);
            resultMessages.add(greetingStrong + "`" + commands.getTopFromMap(positions, strongResultMap) + "`");

            Map<Integer, Integer> numberResultMap = commands.getStatNumbersMapBetweenDates(endDate, startDate);
            String greetingNumbers = "Below are statistics on TOP regular numbers: \n";
            resultMessages.add(greetingNumbers + "`" + commands.getTopFromMap(positions, numberResultMap) + "`");
        } else if (condition.contains("last")) {
            String greetingStrong = "Below are statistics on LAST strong numbers: \n";
            Map<Integer, Integer> strongResultMap = commands.getStatStrongMapBetweenDates(endDate, startDate);
            resultMessages.add(greetingStrong + "`" + commands.getLastFromMap(positions, strongResultMap) + "`");

            Map<Integer, Integer> numberResultMap = commands.getStatNumbersMapBetweenDates(startDate, endDate);
            String greetingNumbers = "Below are statistics on LAST regular numbers: \n";
            resultMessages.add(greetingNumbers + "`" + commands.getLastFromMap(positions, numberResultMap) + "`");
        }

        System.out.println(resultMessages);

        return resultMessages;
    }

    private void editMessageWithMainMenu(long chatId, long messageId) {
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text(text.getTextFromDB(Condition.MAIN_MENU.name(), Language.ENG.name()))
                .replyMarkup(getMainMenuInlineKeyboard()) // Replace with your updated main menu inline keyboard
                .build();

        try {
            execute(editMessageText); // Edit the original message with the updated main menu
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void editMessageWithSubMenu(long chatId, long messageId, String data) {
        EditMessageText editMessageText = EditMessageText.builder()
                .chatId(chatId)
                .messageId((int) messageId)
                .text(text.getTextFromDB(Condition.SUB_MENU.name(), Language.ENG.name()))
                .replyMarkup(getSubMenuInlineKeyboard(data)) // Replace with your updated main menu inline keyboard
                .build();

        try {
            execute(editMessageText); // Edit the original message with the updated main menu
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private InlineKeyboardMarkup getMainMenuInlineKeyboard() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton lastGameButton = new InlineKeyboardButton();
        lastGameButton.setText("Last game");
        //lastGameButton.setCallbackData(commands.getNewestGameDate());
        lastGameButton.setCallbackData("lastGame");

        InlineKeyboardButton monthGameButton = new InlineKeyboardButton();
        monthGameButton.setText("Last month");
        //monthGameButton.setCallbackData(LocalDate.parse(commands.getNewestGameDate()).minusMonths(1).toString());
        monthGameButton.setCallbackData("lastMonth");

        InlineKeyboardButton yearGameButton = new InlineKeyboardButton();
        yearGameButton.setText("Last year");
        //yearGameButton.setCallbackData(LocalDate.parse(commands.getNewestGameDate()).minusYears(1).toString());
        yearGameButton.setCallbackData("lastYear");

        InlineKeyboardButton allTimeGameButton = new InlineKeyboardButton();
        allTimeGameButton.setText("All time");
        allTimeGameButton.setCallbackData("allTime");

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

    private InlineKeyboardMarkup getSubMenuInlineKeyboard(String mainChoose) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();

        InlineKeyboardButton topButton = new InlineKeyboardButton();
        topButton.setText("Top 10");
        //lastGameButton.setCallbackData(commands.getNewestGameDate());
        topButton.setCallbackData(mainChoose + ";top;10");

        InlineKeyboardButton lastButton = new InlineKeyboardButton();
        lastButton.setText("Last 10");
        //monthGameButton.setCallbackData(LocalDate.parse(commands.getNewestGameDate()).minusMonths(1).toString());
        lastButton.setCallbackData(mainChoose + ";last;10");

        InlineKeyboardButton backButton = new InlineKeyboardButton();
        backButton.setText("<< Back");
        backButton.setCallbackData("back");

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
}

