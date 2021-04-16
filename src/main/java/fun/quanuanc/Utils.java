package fun.quanuanc;

import fun.quanuanc.xml.FeedHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    public static final Logger log = LogManager.getLogger(Utils.class);

    public static Date UTCDateString2Date(String dateString) {
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        try {
            date = dateFormat.parse(dateString);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            date = new Date();
        }
        return date;
    }

    public static void xmlToFeed(String xml) {
        InputStream inputStream = new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8));
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser saxParser = factory.newSAXParser();
            FeedHandler feedHandler = new FeedHandler();
            saxParser.parse(inputStream, feedHandler);
            inputStream.close();
        } catch (ParserConfigurationException | IOException | SAXException e) {
            log.error("xmlToFeed failed: {}", e.getMessage());
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
