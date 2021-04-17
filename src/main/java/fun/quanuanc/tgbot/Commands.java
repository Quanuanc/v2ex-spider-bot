package fun.quanuanc.tgbot;

public class Commands {
    public static final String commandInitChar = "/";

    public static final String start = commandInitChar + "start";
    public static final String startResponse = "Hello";

    public static final String addKeyword = commandInitChar + "addkeyword";
    public static final String addKeywordStartResponse = "OK. Send me a keyword.";
    public static final String addKeywordResponse = "Add keyword [%s] successful.";

    public static final String listKeyword = commandInitChar + "listkeyword";
    public static final String listKeywordResponse = "Your keyword:\n\n";

    public static final String clearKeyword = commandInitChar + "clearkeyword";
    public static final String clearKeywordSuccessResponse = "Clear successful.";
    public static final String clearKeywordFailResponse = "You have no keyword to clear.";

    public static final String latestEntry = commandInitChar + "latestentry";

    public static final String debug = commandInitChar + "debug";
}
