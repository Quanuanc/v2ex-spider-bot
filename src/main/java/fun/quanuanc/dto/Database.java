package fun.quanuanc.dto;

import fun.quanuanc.xml.Entry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    //public static final Logger log = LogManager.getLogger(Database.class);

    private static Entry latestEntry;
    private static final Map<Long, Set<String>> userKeywordsMap = new ConcurrentHashMap<>();
    private static final Map<String, Set<Entry>> keywordEntryMap = new ConcurrentHashMap<>();
    private static final Queue<Entry> entryQueue = new LinkedList<>();

    public static Map<Long, Set<String>> getUserKeywordsMap() {
        return userKeywordsMap;
    }

    public static Entry getLatestEntry() {
        return latestEntry;
    }

    public static void setLatestEntry(Entry latestEntry) {
        Database.latestEntry = latestEntry;
    }

    public static Queue<Entry> getEntryQueue() {
        return entryQueue;
    }

    public static Map<String, Set<Entry>> getKeywordEntryMap() {
        return keywordEntryMap;
    }

    public static void addUserKeyword(Long chatId, String keyword) {
        if (userKeywordsMap.containsKey(chatId)) {
            Set<String> keywords = userKeywordsMap.get(chatId);
            keywords.add(keyword);
        } else {
            Set<String> newKeywordsSet = new HashSet<>();
            newKeywordsSet.add(keyword);
            userKeywordsMap.put(chatId, newKeywordsSet);
        }
    }

    public static String[] getAllKeywords() {
        Set<String> keywords = new HashSet<>();
        for (Set<String> userKeywords : userKeywordsMap.values()) {
            keywords.addAll(userKeywords);
        }
        String[] returnKeywords = new String[keywords.size()];
        keywords.toArray(returnKeywords);
        return returnKeywords;
    }

    public static Set<String> getUserKeywords(Long chatId) {
        if (userKeywordsMap.containsKey(chatId)) {
            return userKeywordsMap.get(chatId);
        }
        return new HashSet<>();
    }

    public static boolean clearUserKeywords(Long chatId) {
        if (userKeywordsMap.containsKey(chatId)) {
            userKeywordsMap.put(chatId, new HashSet<>());
            return true;
        }
        return false;
    }

}
