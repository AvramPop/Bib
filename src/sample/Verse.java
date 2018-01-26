package sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.io.IOException;

public class Verse {
    public final String text;
    public final int number;
    public final int chapter;
    public final String book;
    public final Translation translation;

    public Verse(Translation translation, String book, int chapter, int number) {
        this.number = number;
        this.chapter = chapter;
        this.book = book;
        this.translation = translation;
        text = getVerse();
    }

    private String getVerse(){
        //  String requestURL = "https://bibles.org/v2/verses/eng-GNTD:Acts.8.34.js";

        String requestURL = getVerseRequestString();
        RESTInvoker invoker = new RESTInvoker(requestURL, Constants.bibleOrgKey, Constants.bibleOrgPassword);
        String result = invoker.makeGetRequest();
        JsonNode actualObj = getJsonNode(result);

        return Jsoup.parse( // removes HTML tags
                    actualObj
                            .get("response")
                            .get("verses")
                            .get(0)
                            .get("text")
                            .asText()
                    ).text()
                    .replaceAll("[0-9]",""); //removes leading numbers representing the verse
    }

    private JsonNode getJsonNode(String result) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = null;
        try {
            actualObj = mapper.readTree(result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return actualObj;
    }

    private String getVerseRequestString() {
        return new StringBuilder()
                    .append("https://bibles.org/v2/verses/eng-")
                    .append(translation)
                    .append(":").append(book)
                    .append(".").append(chapter)
                    .append(".").append(number)
                    .append(".js")
                    .toString();
    }
}
