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
                .append("GNTD");

        String requestURL = stringBuilder.toString();
        RESTInvoker invoker = new RESTInvoker(requestURL, Constants.bibleOrgKey, Constants.bibleOrgPassword);
        String result;
        result = invoker.makeGetRequest();//TODO uncomment
        //result = "{\"response\":{\"search\":{\"result\":{\"type\":\"verses\",\"summary\":{\"query\":\"love\",\"start\":1,\"total\":721,\"rpp\":\"15\",\"sort\":\"relevance\",\"versions\":[\"eng-GNTD\"],\"testaments\":[\"OT\",\"NT\"]},\"spelling\":[],\"verses\":[{\"auditid\":\"0\",\"verse\":\"5\",\"lastverse\":\"5\",\"id\":\"eng-GNTD:1Cor.13.5\",\"osis_end\":\"eng-GNTD:1Cor.13.5\",\"label\":\"1Cor.013.005,eng-GNTD\",\"reference\":\"1 Corinthians 13:5\",\"prev_osis_id\":\"1Cor.013.004,eng-GNTD\",\"next_osis_id\":\"1Cor.013.006,eng-GNTD\",\"text\":\"<sup>5</sup><em>love</em> is not ill-mannered or selfish or irritable; <em>love</em> does not keep a record of wrongs;\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:1Cor.13\",\"name\":\"1 Corinthians 13\",\"id\":\"eng-GNTD:1Cor.13\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:1Cor.13.6\",\"name\":\"1 Corinthians 13:6\",\"id\":\"eng-GNTD:1Cor.13.6\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:1Cor.13.4\",\"name\":\"1 Corinthians 13:4\",\"id\":\"eng-GNTD:1Cor.13.4\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"15\",\"lastverse\":\"15\",\"id\":\"eng-GNTD:1John.2.15\",\"osis_end\":\"eng-GNTD:1John.2.15\",\"label\":\"1John.002.015,eng-GNTD\",\"reference\":\"1 John 2:15\",\"prev_osis_id\":\"1John.002.014,eng-GNTD\",\"next_osis_id\":\"1John.002.016,eng-GNTD\",\"text\":\"<sup>15</sup>Do not <em>love</em> the world or anything that belongs to the world. If you <em>love</em> the world, you do not <em>love</em> the Father.\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:1John.2\",\"name\":\"1 John 2\",\"id\":\"eng-GNTD:1John.2\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:1John.2.16\",\"name\":\"1 John 2:16\",\"id\":\"eng-GNTD:1John.2.16\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:1John.2.14\",\"name\":\"1 John 2:14\",\"id\":\"eng-GNTD:1John.2.14\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"19\",\"lastverse\":\"19\",\"id\":\"eng-GNTD:1John.4.19\",\"osis_end\":\"eng-GNTD:1John.4.19\",\"label\":\"1John.004.019,eng-GNTD\",\"reference\":\"1 John 4:19\",\"prev_osis_id\":\"1John.004.018,eng-GNTD\",\"next_osis_id\":\"1John.004.020,eng-GNTD\",\"text\":\"<sup>19</sup>We <em>love</em> because God first <em>loved</em> us.\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:1John.4\",\"name\":\"1 John 4\",\"id\":\"eng-GNTD:1John.4\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:1John.4.20\",\"name\":\"1 John 4:20\",\"id\":\"eng-GNTD:1John.4.20\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:1John.4.18\",\"name\":\"1 John 4:18\",\"id\":\"eng-GNTD:1John.4.18\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"8\",\"lastverse\":\"8\",\"id\":\"eng-GNTD:1Pet.4.8\",\"osis_end\":\"eng-GNTD:1Pet.4.8\",\"label\":\"1Pet.004.008,eng-GNTD\",\"reference\":\"1 Peter 4:8\",\"prev_osis_id\":\"1Pet.004.007,eng-GNTD\",\"next_osis_id\":\"1Pet.004.009,eng-GNTD\",\"text\":\"<sup>8</sup>  Above everything, <em>love</em> one another earnestly, because <em>love</em> covers over many sins.\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:1Pet.4\",\"name\":\"1 Peter 4\",\"id\":\"eng-GNTD:1Pet.4\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:1Pet.4.9\",\"name\":\"1 Peter 4:9\",\"id\":\"eng-GNTD:1Pet.4.9\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:1Pet.4.7\",\"name\":\"1 Peter 4:7\",\"id\":\"eng-GNTD:1Pet.4.7\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"11\",\"lastverse\":\"11\",\"id\":\"eng-GNTD:2Cor.11.11\",\"osis_end\":\"eng-GNTD:2Cor.11.11\",\"label\":\"2Cor.011.011,eng-GNTD\",\"reference\":\"2 Corinthians 11:11\",\"prev_osis_id\":\"2Cor.011.010,eng-GNTD\",\"next_osis_id\":\"2Cor.011.012,eng-GNTD\",\"text\":\"<sup>11</sup>Do I say this because I don't <em>love</em> you? God knows I <em>love</em> you!\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:2Cor.11\",\"name\":\"2 Corinthians 11\",\"id\":\"eng-GNTD:2Cor.11\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:2Cor.11.12\",\"name\":\"2 Corinthians 11:12\",\"id\":\"eng-GNTD:2Cor.11.12\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:2Cor.11.10\",\"name\":\"2 Corinthians 11:10\",\"id\":\"eng-GNTD:2Cor.11.10\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"12\",\"lastverse\":\"12\",\"id\":\"eng-GNTD:John.15.12\",\"osis_end\":\"eng-GNTD:John.15.12\",\"label\":\"John.015.012,eng-GNTD\",\"reference\":\"John 15:12\",\"prev_osis_id\":\"John.015.011,eng-GNTD\",\"next_osis_id\":\"John.015.013,eng-GNTD\",\"text\":\"<sup>12</sup>  My commandment is this: <em>love</em> one another, just as I <em>love</em> you.\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:John.15\",\"name\":\"John 15\",\"id\":\"eng-GNTD:John.15\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:John.15.13\",\"name\":\"John 15:13\",\"id\":\"eng-GNTD:John.15.13\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:John.15.11\",\"name\":\"John 15:11\",\"id\":\"eng-GNTD:John.15.11\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"11\",\"lastverse\":\"11\",\"id\":\"eng-GNTD:Josh.23.11\",\"osis_end\":\"eng-GNTD:Josh.23.11\",\"label\":\"Josh.023.011,eng-GNTD\",\"reference\":\"Joshua 23:11\",\"prev_osis_id\":\"Josh.023.010,eng-GNTD\",\"next_osis_id\":\"Josh.023.012,eng-GNTD\",\"text\":\"<sup>11</sup>Be careful, then, to <em>love</em> the Lord your God.\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Josh.23\",\"name\":\"Joshua 23\",\"id\":\"eng-GNTD:Josh.23\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Josh.23.12\",\"name\":\"Joshua 23:12\",\"id\":\"eng-GNTD:Josh.23.12\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Josh.23.10\",\"name\":\"Joshua 23:10\",\"id\":\"eng-GNTD:Josh.23.10\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"22\",\"lastverse\":\"22\",\"id\":\"eng-GNTD:Lam.3.22\",\"osis_end\":\"eng-GNTD:Lam.3.22\",\"label\":\"Lam.003.022,eng-GNTD\",\"reference\":\"Lamentations 3:22\",\"prev_osis_id\":\"Lam.003.021,eng-GNTD\",\"next_osis_id\":\"Lam.003.023,eng-GNTD\",\"text\":\"<sup>22</sup>The Lord's unfailing <em>love</em> and mercy still continue,\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Lam.3\",\"name\":\"Lamentations 3\",\"id\":\"eng-GNTD:Lam.3\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Lam.3.23\",\"name\":\"Lamentations 3:23\",\"id\":\"eng-GNTD:Lam.3.23\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Lam.3.21\",\"name\":\"Lamentations 3:21\",\"id\":\"eng-GNTD:Lam.3.21\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"32\",\"lastverse\":\"32\",\"id\":\"eng-GNTD:Luke.6.32\",\"osis_end\":\"eng-GNTD:Luke.6.32\",\"label\":\"Luke.006.032,eng-GNTD\",\"reference\":\"Luke 6:32\",\"prev_osis_id\":\"Luke.006.031,eng-GNTD\",\"next_osis_id\":\"Luke.006.033,eng-GNTD\",\"text\":\"<sup>32</sup>“If you <em>love</em> only the people who <em>love</em> you, why should you receive a blessing? Even sinners <em>love</em> those who <em>love</em> them!\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Luke.6\",\"name\":\"Luke 6\",\"id\":\"eng-GNTD:Luke.6\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Luke.6.33\",\"name\":\"Luke 6:33\",\"id\":\"eng-GNTD:Luke.6.33\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Luke.6.31\",\"name\":\"Luke 6:31\",\"id\":\"eng-GNTD:Luke.6.31\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"19\",\"lastverse\":\"19\",\"id\":\"eng-GNTD:Matt.19.19\",\"osis_end\":\"eng-GNTD:Matt.19.19\",\"label\":\"Matt.019.019,eng-GNTD\",\"reference\":\"Matthew 19:19\",\"prev_osis_id\":\"Matt.019.018,eng-GNTD\",\"next_osis_id\":\"Matt.019.020,eng-GNTD\",\"text\":\"<sup>19</sup>  respect your father and your mother; and <em>love</em> your neighbor as you <em>love</em> yourself.”\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Matt.19\",\"name\":\"Matthew 19\",\"id\":\"eng-GNTD:Matt.19\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Matt.19.20\",\"name\":\"Matthew 19:20\",\"id\":\"eng-GNTD:Matt.19.20\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Matt.19.18\",\"name\":\"Matthew 19:18\",\"id\":\"eng-GNTD:Matt.19.18\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"17\",\"lastverse\":\"17\",\"id\":\"eng-GNTD:Prov.8.17\",\"osis_end\":\"eng-GNTD:Prov.8.17\",\"label\":\"Prov.008.017,eng-GNTD\",\"reference\":\"Proverbs 8:17\",\"prev_osis_id\":\"Prov.008.016,eng-GNTD\",\"next_osis_id\":\"Prov.008.018,eng-GNTD\",\"text\":\"<sup>17</sup>I <em>love</em> those who <em>love</em> me;\\n\\n\\nwhoever looks for me can find me.\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Prov.8\",\"name\":\"Proverbs 8\",\"id\":\"eng-GNTD:Prov.8\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Prov.8.18\",\"name\":\"Proverbs 8:18\",\"id\":\"eng-GNTD:Prov.8.18\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Prov.8.16\",\"name\":\"Proverbs 8:16\",\"id\":\"eng-GNTD:Prov.8.16\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"4\",\"lastverse\":\"4\",\"id\":\"eng-GNTD:Ps.52.4\",\"osis_end\":\"eng-GNTD:Ps.52.4\",\"label\":\"Ps.052.004,eng-GNTD\",\"reference\":\"Psalm 52:4\",\"prev_osis_id\":\"Ps.052.003,eng-GNTD\",\"next_osis_id\":\"Ps.052.005,eng-GNTD\",\"text\":\"<sup>4</sup>You <em>love</em> to hurt people with your words, you liar!\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Ps.52\",\"name\":\"Psalm 52\",\"id\":\"eng-GNTD:Ps.52\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Ps.52.5\",\"name\":\"Psalm 52:5\",\"id\":\"eng-GNTD:Ps.52.5\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Ps.52.3\",\"name\":\"Psalm 52:3\",\"id\":\"eng-GNTD:Ps.52.3\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"159\",\"lastverse\":\"159\",\"id\":\"eng-GNTD:Ps.119.159\",\"osis_end\":\"eng-GNTD:Ps.119.159\",\"label\":\"Ps.119.159,eng-GNTD\",\"reference\":\"Psalm 119:159\",\"prev_osis_id\":\"Ps.119.158,eng-GNTD\",\"next_osis_id\":\"Ps.119.160,eng-GNTD\",\"text\":\"<sup>159</sup>See how I <em>love</em> your instructions, Lord.\\n\\n\\nYour <em>love</em> never changes, so save me!\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Ps.119\",\"name\":\"Psalm 119\",\"id\":\"eng-GNTD:Ps.119\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Ps.119.160\",\"name\":\"Psalm 119:160\",\"id\":\"eng-GNTD:Ps.119.160\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Ps.119.158\",\"name\":\"Psalm 119:158\",\"id\":\"eng-GNTD:Ps.119.158\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"17\",\"lastverse\":\"17\",\"id\":\"eng-GNTD:Ps.136.17\",\"osis_end\":\"eng-GNTD:Ps.136.17\",\"label\":\"Ps.136.017,eng-GNTD\",\"reference\":\"Psalm 136:17\",\"prev_osis_id\":\"Ps.136.016,eng-GNTD\",\"next_osis_id\":\"Ps.136.018,eng-GNTD\",\"text\":\"<sup>17</sup>He killed powerful kings;\\n\\n\\nhis <em>love</em> is eternal;\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Ps.136\",\"name\":\"Psalm 136\",\"id\":\"eng-GNTD:Ps.136\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Ps.136.18\",\"name\":\"Psalm 136:18\",\"id\":\"eng-GNTD:Ps.136.18\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Ps.136.16\",\"name\":\"Psalm 136:16\",\"id\":\"eng-GNTD:Ps.136.16\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"},{\"auditid\":\"0\",\"verse\":\"18\",\"lastverse\":\"18\",\"id\":\"eng-GNTD:Ps.136.18\",\"osis_end\":\"eng-GNTD:Ps.136.18\",\"label\":\"Ps.136.018,eng-GNTD\",\"reference\":\"Psalm 136:18\",\"prev_osis_id\":\"Ps.136.017,eng-GNTD\",\"next_osis_id\":\"Ps.136.019,eng-GNTD\",\"text\":\"<sup>18</sup>he killed famous kings;\\n\\n\\nhis <em>love</em> is eternal;\",\"parent\":{\"chapter\":{\"path\":\"/chapters/eng-GNTD:Ps.136\",\"name\":\"Psalm 136\",\"id\":\"eng-GNTD:Ps.136\"}},\"next\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Ps.136.19\",\"name\":\"Psalm 136:19\",\"id\":\"eng-GNTD:Ps.136.19\"}},\"previous\":{\"verse\":{\"path\":\"/verses/eng-GNTD:Ps.136.17\",\"name\":\"Psalm 136:17\",\"id\":\"eng-GNTD:Ps.136.17\"}},\"copyright\":\"        <p>Good News Translation® (Today’s English Version, Second Edition) </p>        <p>© 1992 American Bible Society.  All rights reserved.</p>        <p>Bible text from the Good News Translation (GNT) is not to be reproduced in copies or otherwise by any means except as permitted in writing by American Bible Society, 101 North Independence Mall East, Floor 8, Philadelphia, PA 19106-2155  (www.americanbible.org).</p>        <p>LICENSEE shall reproduce the following trademark and trademark notice on the copyright page of each copy of the Licensed Products:</p>        <p>®</p>      \"}]}},\"meta\":{\"fums\":\"<script>\\nvar _BAPI=_BAPI||{};\\nif(typeof(_BAPI.t)==='undefined'){\\ndocument.write('\\\\x3Cscript src=\\\"'+document.location.protocol+'//d2ue49q0mum86x.cloudfront.net/include/fums.c.js\\\"\\\\x3E\\\\x3C/script\\\\x3E');}\\ndocument.write(\\\"\\\\x3Cscript\\\\x3E_BAPI.t('5aab71ff4a2122.60845081');\\\\x3C/script\\\\x3E\\\");\\n</script><noscript><img src=\\\"https://d3a2okcloueqyx.cloudfront.net/nf1?t=5aab71ff4a2122.60845081\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" /></noscript>\",\"fums_tid\":\"5aab71ff4a2122.60845081\",\"fums_js_include\":\"d2ue49q0mum86x.cloudfront.net/include/fums.c.js\",\"fums_js\":\"var _BAPI=_BAPI||{};if(typeof(_BAPI.t)!='undefined'){ _BAPI.t('5aab71ff4a2122.60845081'); }\",\"fums_noscript\":\"<img src=\\\"https://d3a2okcloueqyx.cloudfront.net/nf1?t=5aab71ff4a2122.60845081\\\" height=\\\"1\\\" width=\\\"1\\\" border=\\\"0\\\" alt=\\\"\\\" style=\\\"height: 0; width: 0;\\\" />\"}}}";

        ArrayList<String> references = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper);
        JsonNode arrNode = objectMapper.readTree(result);//.get("response").get("search").get("result").get("verses");
        System.out.println(arrNode);
        arrNode = arrNode.get("response").get("search").get("result").get("verses");
        System.out.println(arrNode);
        if (arrNode.isArray()) {
            for (final JsonNode objNode : arrNode) {
                references.add(objNode.get("reference").asText());
                System.out.println(objNode.get("reference"));
            }
        }
        return references;
    }
}
