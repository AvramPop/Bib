package sample;

public class VersePOJO {
    private String book;
    private int id;
    private int chapter;
    private int verse;
    private String text;

    public VersePOJO() {}

    public VersePOJO(Verse verse) {
        this.book = verse.book;
        this.chapter = verse.chapter;
        this.verse = verse.number;
        this.text = verse.text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBook() {
        return book;
    }

    public void setBook(String book) {
        this.book = book;
    }

    public int getChapter() {
        return chapter;
    }

    public void setChapter(int chapter) {
        this.chapter = chapter;
    }

    public int getVerse() {
        return verse;
    }

    public void setVerse(int verse) {
        this.verse = verse;
    }
}
