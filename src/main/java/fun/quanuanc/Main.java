package fun.quanuanc;

import fun.quanuanc.task.ScheduleTask;
import fun.quanuanc.tgbot.V2EXSpiderBot;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Timer;

public class Main {

    private static final Logger log = LogManager.getLogger(Main.class);

    public static void main(String[] args) {
        //加入测试数据
//        Database.addUserKeyword(629976103L, "笔记本");
//        Database.addUserKeyword(629976103L, "显卡");

        //注册telegram bot
        V2EXSpiderBot myBot = new V2EXSpiderBot();
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(myBot);
            log.debug("bot register success");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        //获取Feed
        ScheduleTask scheduleTask = new ScheduleTask();
        scheduleTask.setV2EXSpiderBot(myBot);
        Timer scheduleTaskTimer = new Timer();
        scheduleTaskTimer.schedule(scheduleTask, 0, Config.SCHEDULE_TASK_PERIOD);

    }
}
