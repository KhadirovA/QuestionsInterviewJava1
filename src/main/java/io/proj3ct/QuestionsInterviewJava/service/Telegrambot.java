package io.proj3ct.QuestionsInterviewJava.service;

import com.vdurmont.emoji.EmojiParser;
import io.proj3ct.QuestionsInterviewJava.config.BotConfig;
import io.proj3ct.QuestionsInterviewJava.model.User;
import io.proj3ct.QuestionsInterviewJava.model.UserRepository;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
@Slf4j
public class Telegrambot extends TelegramLongPollingBot {

    @Autowired
    private UserRepository userRepository;
    final BotConfig config;
    static final String HELP_TEXT = "Этот бот предназначет для изучения Java";
    static final String ERROR_TEXT = "Error occurred: ";
    public Telegrambot(BotConfig config) {
        this.config = config;
        List<BotCommand> listofCommands = new ArrayList<>();
        listofCommands.add(new BotCommand("/start", "Главное меню"));
        listofCommands.add(new BotCommand("/tes", "Короткий тест"));
        listofCommands.add(new BotCommand("/help", "Информация"));
        listofCommands.add(new BotCommand("/rating", "рейтинг и статистика"));
        try {
            this.execute(new SetMyCommands(listofCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error("Error setting bot's command list: " + e.getMessage());
        }
    }

    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            switch (messageText) {
                case "/start":

                    mainMenu(chatId, update.getMessage().getChat().getFirstName());
                    break;
                case "/help":


                    SendMessage message = new SendMessage();
                    message.setChatId(String.valueOf(chatId));
                    message.setText(HELP_TEXT);
                    executeMessage(message);
                    startCommandReceived(chatId, HELP_TEXT);
                    break;

//                case "/register":
//                    register(chatId);
//                    break;

                default:
                    sendMessage(chatId, "Sorry");

            }
        } else if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            if (callbackData.equals("Yes_button")) {
                String text = "you pressed YES button";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }

            } else if (callbackData.equals("book")) {





            } else if (callbackData.equals("questions")) {


            } else if (callbackData.equals("Youtube")) {


            } else if (callbackData.equals("motivation")) {


            } else {
                String text = "Sorry yyyyyyyyy";
                EditMessageText message = new EditMessageText();
                message.setChatId(String.valueOf(chatId));
                message.setText(text);
                message.setMessageId((int) messageId);

                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    log.error("Error occurred: " + e.getMessage());
                }

            }
        }

    }

    private void executeMessage(SendMessage message) {

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error(ERROR_TEXT + e.getMessage());
        }
    }


    private void mainMenu(long chatId, String fistName) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(emojgicreate("Добро пожаловать " + fistName + " :wink:"));
        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine1 = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine2 = new ArrayList<>();
        InlineKeyboardButton b1 = createButton(emojgicreate("Вопросы" + ":nerd:"), "questions");
        InlineKeyboardButton b2 = createButton(emojgicreate("Книги" + ":book:"), "book");
        InlineKeyboardButton b3 = createButton(emojgicreate("Youtube" + ":tv:"), "Youtube");
        InlineKeyboardButton b4 = createButton(emojgicreate("Мотивация" + ":sunglasses:"), "motivation");
        rowInLine1.add(b1);
        rowInLine1.add(b2);
        rowInLine2.add(b3);
        rowInLine2.add(b4);
        rowsInLine.add(rowInLine1);
        rowsInLine.add(rowInLine2);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try {
            execute(message);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }

    }

    private String emojgicreate(String stroka) {
        String answer = EmojiParser.parseToUnicode(stroka);
        return answer;
    }

    private InlineKeyboardButton createButton(String name, String keyname) {
        var menubutton = new InlineKeyboardButton();
        menubutton.setText(name);
        menubutton.setCallbackData(keyname);
        return menubutton;
    }

    private void register(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText("do you really want to regisrer");

        InlineKeyboardMarkup markupInLine = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInLine = new ArrayList<>();
        List<InlineKeyboardButton> rowInLine = new ArrayList<>();
        var yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("Yes_button");

        var noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("No_button");


        rowInLine.add(yesButton);
        rowInLine.add(noButton);

        rowsInLine.add(rowInLine);
        markupInLine.setKeyboard(rowsInLine);
        message.setReplyMarkup(markupInLine);

        try {
            execute(message);

        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }


    }


    private void registerUser(Message msg) {
        if (userRepository.findById(msg.getChatId()).isEmpty()) {
            var chatId = msg.getChatId();
            var chat = msg.getChat();
            User user = new User();
            user.setChatId(chat);
            user.setFirstName(chat.getFirstName());
            user.setLastName(chat.getLastName());
            user.getUserName(chat.getUserName());
            user.setRegisteredAt(new Timestamp(System.currentTimeMillis()));

            userRepository.save(user);
            log.info("save user:" + user);
        }


    }

    private void startCommandReceived(long chatId, String name) {
        String answer = EmojiParser.parseToUnicode(" Hi," + name + ":wink:");
        sendMessage(chatId, answer);
        log.info("Replied to user " + name);
    }

    private void sendMessage(long chatId, String textToSen) {
//        SendMessage message = new SendMessage();
//        message.setChatId(String.valueOf(chatId));
//        message.setText(textToSen);
//        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
//        List<KeyboardRow> keyboardRows = new ArrayList<>();
//        KeyboardRow row = new KeyboardRow();
//        row.add("button1");
//        row.add("button2");
//        row.add("button3");
//
//        keyboardRows.add(row);
//        KeyboardRow row1 = new KeyboardRow();
//        row1.add("button4");
//        row1.add("button5");
//        row1.add("button6");
//        keyboardRows.add(row1);
//
//        keyboardMarkup.setKeyboard(keyboardRows);
//        message.setReplyMarkup(keyboardMarkup);
//
//        try {
//            execute(message);
//
//        } catch (TelegramApiException e) {
//            throw new RuntimeException(e);
//        }
//
    }
}
