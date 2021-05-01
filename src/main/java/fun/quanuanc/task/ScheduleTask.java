package fun.quanuanc.task;

import fun.quanuanc.Config;
import fun.quanuanc.Utils;
import fun.quanuanc.dto.Database;
import fun.quanuanc.tgbot.V2EXSpiderBot;
import fun.quanuanc.xml.Entry;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ScheduleTask extends TimerTask {

    public static final Logger log = LogManager.getLogger(ScheduleTask.class);

    private V2EXSpiderBot v2EXSpiderBot;

    public void setV2EXSpiderBot(V2EXSpiderBot bot) {
        v2EXSpiderBot = bot;
    }

    @Override
    public void run() {
        // 1. Get xml
        String xmlString = getXmlInputStream();

        // 2. xml to entryQueue
        if (xmlString != null) {
            Utils.xmlToFeed(xmlString);
        }

        // 3. 遍历feed，找出包含keyword的entry
        String[] keywords = Database.getAllKeywords();
        if (keywords.length > 0)
            findEntry(keywords);

        // 4. 把匹配到的entry发送给user
        sendMatchedEntryToUser();
    }

    private void sendMatchedEntryToUser() {
        Map<Long, Set<String>> userKeywordsMap = Database.getUserKeywordsMap();
        Map<String, Set<Entry>> keywordEntryMap = Database.getKeywordEntryMap();

        if (userKeywordsMap.size() > 0) {
            if (keywordEntryMap.size() > 0) {

                for (Map.Entry<Long, Set<String>> userKeywordsMapEntry : userKeywordsMap.entrySet()) {//遍历用户表

                    Long userChatId = userKeywordsMapEntry.getKey(); //用户id
                    Set<String> userKeywords = userKeywordsMapEntry.getValue();//用户keywords

                    StringBuilder textBuilder = new StringBuilder();

                    for (String userKeyword : userKeywords) {
                        Set<Entry> userKeywordEntrySet = keywordEntryMap.get(userKeyword);
                        if (userKeywordEntrySet == null) {
                            continue;
                        }
                        if (userKeywordEntrySet.size() > 1) {
                            short index = 0;
                            for (Entry entry : userKeywordEntrySet) {
                                textBuilder.append(index++).append(". ").append(entry.toString()).append("\n");
                            }
                        } else {
                            for (Entry entry : userKeywordEntrySet) {
                                textBuilder.append(entry.toString()).append("\n");
                            }
                        }
                    }
                    v2EXSpiderBot.sendMessage(userChatId, textBuilder.toString());
                }
                keywordEntryMap.clear();
            }
        }
    }

    private void findEntry(String[] keywords) {
        Map<String, Set<Entry>> keywordEntryMap = Database.getKeywordEntryMap();
        keywordEntryMap.clear();
        Queue<Entry> entryQueue = Database.getEntryQueue();
        int entryQueueSize = entryQueue.size();
        for (int i = 0; i < entryQueueSize; i++) {
            Entry entry = entryQueue.remove();
            for (String keyword : keywords) {
                Set<Entry> entrySet;
                if (keywordEntryMap.containsKey(keyword)) {
                    entrySet = keywordEntryMap.get(keyword);
                } else {
                    entrySet = new HashSet<>();
                }
                if (entry.getTitle().contains(keyword) || entry.getContent().contains(keyword)) {
                    entrySet.add(entry);
                }
                if (entrySet.size() > 0)
                    keywordEntryMap.put(keyword, entrySet);
            }
        }
    }

    private String getXmlInputStream() {
        //使用本地XML测试
//        File file = new File(Config.V2EX_URL_FILE);
//        try {
//            return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpUriRequest request = new HttpGet(Config.V2EX_URL);
            HttpResponse response = client.execute(request);
            if (response.getStatusLine().getStatusCode() == 200) {
                return EntityUtils.toString(response.getEntity());
            }
        } catch (Exception e) {
            log.error("Failed to get xml: {}", e.getMessage());
        }
        return null;
    }

}
