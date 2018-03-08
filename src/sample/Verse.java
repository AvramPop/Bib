package sample;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.ArrayList;

public class Verse {
    public final String text;
    public final int number;
    public final int chapter;
    public final String book;
    private String fullReference;
    private String nextVerseReference;
    private String previousVerseReference;
    private String copyright;
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

        setVerseInfo(actualObj);

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

    public String getFullReference() {
        return fullReference;
    }

    public String getCopyright() {
        return copyright;
    }

    private void setVerseInfo(JsonNode actualObj) {
        setFullReference(actualObj);
        setCopyright(actualObj);
        setNextVerseReference(actualObj);
        setPreviousVerseReference(actualObj);
    }

    private void setFullReference(JsonNode actualObj) {
        fullReference = actualObj.get("response").get("verses").get(0).get("reference").asText();
    }

    private void setCopyright(JsonNode actualObj) {
        copyright = Jsoup.parse(actualObj.get("response").get("verses").get(0).get("copyright").asText()).text();
    }

    private void setNextVerseReference(JsonNode actualObj){
        nextVerseReference = actualObj.get("response").get("verses").get(0).get("next").get("verse").get("name").asText();
    }

    private void setPreviousVerseReference(JsonNode actualObj){
        previousVerseReference = actualObj.get("response").get("verses").get(0).get("previous").get("verse").get("name").asText();
    }

    public Verse getNextVerse(){
        String[] res = nextVerseReference.split("[\\p{Punct}\\s]+");
        return new Verse(translation, res[0], Integer.parseInt(res[1]), Integer.parseInt(res[2]));
    }

    public Verse getPreviousVerse(){
        String[] res = previousVerseReference.split("[\\p{Punct}\\s]+");
        return new Verse(translation, res[0], Integer.parseInt(res[1]), Integer.parseInt(res[2]));
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

    public ArrayList<String> search(String query) throws IOException {
        StringBuilder stringBuilder = new StringBuilder()
                .append("https://bibles.org/v2/search.js?query=")
                .append(query)
                .append("&version=eng-")
                .append("NASB");

        String requestURL = stringBuilder.toString();
        RESTInvoker invoker = new RESTInvoker(requestURL, Constants.bibleOrgKey, Constants.bibleOrgPassword);
        String result = invoker.makeGetRequest();

        ArrayList<String> references = new ArrayList<>();

        JsonNode arrNode = new ObjectMapper().readTree(result).get("response").get("search").get("result").get("verses");
        if (arrNode.isArray()) {
            for (final JsonNode objNode : arrNode) {
                references.add(objNode.get("reference").asText());
                System.out.println(objNode.get("reference"));
            }
        }
        return references;
    }
}
