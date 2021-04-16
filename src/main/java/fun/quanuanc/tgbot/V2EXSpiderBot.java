package fun.quanuanc.tgbot;

import fun.quanuanc.Config;
import fun.quanuanc.dto.Database;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;


public class V2EXSpiderBot extends TelegramLongPollingBot {

    private static final Logger log = LogManager.getLogger(V2EXSpiderBot.class);

    private Long currentChatId = null;

    @Override
    public String getBotUsername() {
        return Config.BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return Config.BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String receiveText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            String handleText = handleCommand(chatId, receiveText);

            if (handleText != null) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId.toString());
                message.setText(handleText);
                try {
                    execute(message);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String handleCommand(Long chatId, String receiveText) {
        log.debug("chatId: {}, receiveText: {}", chatId, receiveText);

        if (chatId.equals(currentChatId) && !receiveText.startsWith("/")) {
            Database.addUserKeyword(chatId, receiveText);
            currentChatId = null;
            return "Keyword added.";
        }

        switch (receiveText) {
            case Commands.start: {
                return "Hello";
            }
            case Commands.listKeyword: {
                Set<String> userKeyword = Database.getUserKeywords(chatId);
                StringBuilder keywordText = new StringBuilder();
                int index = 1;
                for (String value : userKeyword) {
                    keywordText.append(index++).append(". ").append(value);
                    keywordText.append("\n");
                }
                return "Your Keyword: \n\n" + keywordText;
            }
            case Commands.clearKeyword: {
                if (Database.clearUserKeywords(chatId)) {
                    return "Clear successful.";
                }
                return "You have no keyword to clear.";
            }
            case Commands.addKeyword: {
                currentChatId = chatId;
                return "OK. Send me a keyword.";
            }
            case Commands.latestEntry: {
                sendMessage(chatId, Database.getLatestEntry().toString());
                return null;
            }
            default:
                return null;
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
