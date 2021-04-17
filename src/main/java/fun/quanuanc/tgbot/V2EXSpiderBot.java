package fun.quanuanc.tgbot;

import fun.quanuanc.Config;
import fun.quanuanc.dto.Database;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;


public class V2EXSpiderBot extends TelegramLongPollingBot {

    private static final Logger log = LogManager.getLogger(V2EXSpiderBot.class);

    private final Set<Long> currentChatId = new HashSet<>();

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

            boolean status = handleCommand(chatId, receiveText);
            log.debug("handleCommand status: {}", status);
        }
    }

    private boolean handleCommand(Long chatId, String receiveText) {
        log.debug("chatId: {}, receiveText: {}", chatId, receiveText);

        if (currentChatId.contains(chatId) && !receiveText.startsWith("/")) {
            Database.addUserKeyword(chatId, receiveText);
            currentChatId.remove(chatId);
            sendMessage(chatId, String.format(Commands.addKeywordResponse, receiveText));
        } else if (currentChatId.contains(chatId) && receiveText.startsWith("/")) {
            currentChatId.remove(chatId);
        }

        switch (receiveText) {
            case Commands.start:
                sendMessage(chatId, Commands.startResponse);
                return true;

            case Commands.listKeyword:
                Set<String> userKeyword = Database.getUserKeywords(chatId);
                StringBuilder keywordText = new StringBuilder();
                int index = 1;
                for (String value : userKeyword) {
                    keywordText.append(index++).append(". ").append(value);
                    keywordText.append("\n");
                }
                sendMessage(chatId, Commands.listKeywordResponse + keywordText);
                return true;

            case Commands.clearKeyword:
                if (Database.clearUserKeywords(chatId)) {
                    sendMessage(chatId, Commands.clearKeywordSuccessResponse);
                    return true;
                }
                sendMessage(chatId, Commands.clearKeywordFailResponse);
                return false;

            case Commands.addKeyword:
                currentChatId.add(chatId);
                sendMessage(chatId, Commands.addKeywordStartResponse);
                return true;

            case Commands.latestEntry:
                sendMessage(chatId, Database.getLatestEntry().toString());
                return true;

            case Commands.debug:
                sendMessage(chatId, Database.getUserKeywordsMap().toString());
                return true;

            default:
                return true;
        }
    }

    public void sendMessage(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId.toString());
        sendMessage.setText(text);
        sendMessage.setDisableWebPagePreview(true);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("sendMessage failed: {}", e.getMessage());
        }
    }

}
