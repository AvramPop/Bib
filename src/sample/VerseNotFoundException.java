package sample;

public class VerseNotFoundException extends Exception {
    public VerseNotFoundException(String message) {
        super(message);
       // super("Verse " + verse.book + " " + verse.chapter + ":" + verse.number + " " + verse.book + " not found!");
    }
}
