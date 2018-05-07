package sample;

import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import javax.naming.TimeLimitExceededException;
import java.io.IOException;
import java.util.ArrayList;

public class WordSearchScene {
    private Button wordSearchButton;
    private Hyperlink hyperlinks[];
    private TextField wordSearchTextField;
    private VBox rootBox;
    private String s;
    private VerseDAO verseDAO;
    private Label errorLabel;
    private ScrollPane scrollPane;
    private static WordSearchScene instance = null;

    private WordSearchScene() {
        rootBox = new VBox();
        wordSearchTextField = new PersistentPromptTextField("", "Keyword");
        wordSearchButton = new Button("Search!");
        errorLabel = new Label();
        verseDAO = new VerseDAO();
        setupWordSearchButtonFromDB();
        // setupWordSearchButtonFromServer();
        scrollPane = new ScrollPane();
        rootBox.getChildren().addAll(wordSearchTextField, wordSearchButton, scrollPane);

    }

    public static VBox getLayout() {
        if(instance == null) {
            instance = new WordSearchScene();
        }
        return instance.rootBox;
    }

    private void setupWordSearchButtonFromDB() {
        hyperlinks = new Hyperlink[50];

        wordSearchButton.setOnAction(event -> {
            String query = wordSearchTextField.getText();
            StringBuilder sb;
            hyperlinks = new Hyperlink[5000];

            int numberOfHyperlinks = 0;
            for(VersePOJO versePojo : verseDAO.getAll()) {
                if (versePojo.getText().contains(query)) {
                    Hyperlink hyperlink = new Hyperlink();
                    sb = new StringBuilder();
                    sb.append(versePojo.getBook()).append(" ").append(versePojo.getChapter()).
                                                    append(":").append(versePojo.getVerse());
                    s = sb.toString();
                    hyperlink.setText(s);
                    //System.out.println(s);
                    hyperlink.setOnAction(event1 -> {
                        String[] words = hyperlink.getText().split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})");
                        System.out.println("String to split : " + s);
                        Verse hyperlinkChosenVerse = null;
                        try {
                            hyperlinkChosenVerse = new Verse(Translation.KJV,
                                    words[0],
                                    Integer.parseInt(words[1]),
                                    Integer.parseInt(words[3]));
                        } catch (VerseNotFoundException e) {
                            e.printStackTrace();
                        }

                        Main.getMainBox().setLeft(VerseSearchScene.getLayout(hyperlinkChosenVerse));

                    });
                    hyperlinks[numberOfHyperlinks] = hyperlink;
                    numberOfHyperlinks++;
                }
            }
            GridPane hyperlinksGridPane = new GridPane();
            for(int i = 0; i < numberOfHyperlinks; i++){
                System.out.println(hyperlinks[i]);
                hyperlinksGridPane.add(hyperlinks[i], 0, i);
            }
            if(rootBox.getChildren().get(rootBox.getChildren().size() - 1).getClass() == GridPane.class){
                rootBox.getChildren().remove(rootBox.getChildren().size() - 1);
            } //removes last set of hyperlinks (is exists) in order to add the result of the new one
            scrollPane.setContent(hyperlinksGridPane);

        });
    }


    private void setupWordSearchButtonFromServer() throws VerseNotFoundException {//

        hyperlinks = new Hyperlink[50];

        wordSearchButton.setOnAction(event -> {
            Verse v = null;
            try {
                v = new Verse(Translation.NASB, "John", 3, 16);
            } catch (VerseNotFoundException e) {
                e.printStackTrace();
            }
            try {
                ArrayList<String> arrayList = null;
                try {
                    arrayList = v.search(wordSearchTextField.getText());
                    StringBuilder sb = new StringBuilder();
                    hyperlinks = new Hyperlink[50];
                    int numberOfHyperlinks = 0;
                    for(String s : arrayList){
                        Hyperlink hyperlink = new Hyperlink();
                        hyperlink.setText(s);
                        hyperlink.setOnAction(event1 -> {
                            System.out.println(s);
                            String[] words = hyperlink.getText().split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})");


                            String book = words[0].toLowerCase();
                            book = book.substring(0, 1).toUpperCase() + book.substring(1); //formats book name in order to keep only first letter in capitals

                            Verse hyperlinkChosenVerse = null;
                            try {
                                hyperlinkChosenVerse = new Verse(Translation.KJV,
                                        words[0],
                                        Integer.parseInt(words[1]),
                                        Integer.parseInt(words[3]));
                            } catch (VerseNotFoundException e) {
                                e.printStackTrace();
                            }

                            Main.getMainBox().setLeft(VerseSearchScene.getLayout(hyperlinkChosenVerse));

                        });
                        hyperlinks[numberOfHyperlinks] = hyperlink;
                        numberOfHyperlinks++;
                    }
                    GridPane hyperlinksGridPane = new GridPane();
                    for(int i = 0; i < numberOfHyperlinks; i++){
                        System.out.println(hyperlinks[i]);
                        hyperlinksGridPane.add(hyperlinks[i], 0, i);
                    }
                    if(rootBox.getChildren().get(rootBox.getChildren().size() - 1).getClass() == GridPane.class){
                        rootBox.getChildren().remove(rootBox.getChildren().size() - 1);
                    } //removes last set of hyperlinks (is exists) in order to add the result of the new one
                    rootBox.getChildren().add(hyperlinksGridPane);
                } catch (TimeLimitExceededException e) {
                    errorLabel.setText("Request time limit exceeded!");
                    errorLabel.setTextFill(Color.web("#ff2424"));
                    rootBox.getChildren().add(errorLabel);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (VerseNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
