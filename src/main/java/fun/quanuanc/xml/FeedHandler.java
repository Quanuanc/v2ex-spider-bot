package fun.quanuanc.xml;

import fun.quanuanc.Utils;
import fun.quanuanc.dto.Database;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Queue;

public class FeedHandler extends DefaultHandler {

    public static final Logger log = LogManager.getLogger(FeedHandler.class);

    private static final String TITLE = "title";
    private static final String LINK = "link";
    private static final String UPDATED = "updated";
    private static final String ENTRY = "entry";
    private static final String PUBLISHED = "published";
    private static final String CONTENT = "content";


    private StringBuilder elementValue;
    private boolean isInsideEntry = false;
    private Entry currentEntry;
    private Queue<Entry> entryQueue;
    private List<Entry> parsedEntry;

    @Override
    public void startDocument() {
        parsedEntry = new ArrayList<>();
        entryQueue = Database.getEntryQueue();
    }

    @Override
    public void endDocument() {
        parsedEntry.sort(Comparator.comparing(Entry::getPublishedTime)); //时间从早到晚
        if (Database.getLatestEntry() == null) {
            Database.setLatestEntry(parsedEntry.get(parsedEntry.size() - 1));
            entryQueue.addAll(parsedEntry);
        } else {
            for (int i = parsedEntry.size() - 1; i > -1; i--) {
                Entry entry = parsedEntry.get(i);
                if (entry.getPublishedTime().after(Database.getLatestEntry().getPublishedTime())) {
                    entryQueue.add(entry);
                    Database.setLatestEntry(entry);
                } else {
                    break;
                }
            }
        }
        log.debug("entryQueue size: {}", entryQueue.size());
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case LINK:
                if (isInsideEntry) {
                    String link = attributes.getValue("href");
                    currentEntry.setLink(link);
                }
            case TITLE:
            case UPDATED:
            case PUBLISHED:
            case CONTENT:
                elementValue = new StringBuilder();
                break;
            case ENTRY:
                isInsideEntry = true;
                currentEntry = new Entry();
                break;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case ENTRY:
                isInsideEntry = false;
                parsedEntry.add(currentEntry);
                break;
            case TITLE:
                if (isInsideEntry) {
                    currentEntry.setTitle(elementValue.toString());
                }
                break;
            case PUBLISHED:
                if (isInsideEntry) {
                    currentEntry.setPublishedTime(Utils.UTCDateString2Date(elementValue.toString()));
                }
                break;
            case UPDATED:
                if (isInsideEntry) {
                    currentEntry.setUpdatedTime(Utils.UTCDateString2Date(elementValue.toString()));
                }
                break;
            case CONTENT:
                if (isInsideEntry) {
                    currentEntry.setContent(elementValue.toString());
                }
                break;
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (elementValue == null) {
            elementValue = new StringBuilder();
        } else {
            elementValue.append(ch, start, length);
        }
    }
}
