package sample;

import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import javax.xml.soap.Text;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class WordSearchSceneLayout {
    private static Button wordSearchButton;
    private static Hyperlink hyperlinks[];
    private static TextField wordSearchTextField;
    private static VBox rootBox;
    private static Label searchResultsLabel;

    public static VBox sceneLayout() {
        rootBox = new VBox();
        wordSearchTextField = new PersistentPromptTextField("", "query");
        wordSearchButton = new Button("Search!");
        searchResultsLabel = new Label();
        setupWordSearchButton();
        rootBox.getChildren().addAll(wordSearchTextField, wordSearchButton, searchResultsLabel);
        return rootBox;
    }

    private static void setupWordSearchButton() {

        hyperlinks = new Hyperlink[50];

        wordSearchButton.setOnAction(event -> {
            Verse v = new Verse(Translation.NASB, "John", 3, 16);
            try {
                ArrayList<String> arrayList = v.search(wordSearchTextField.getText());
                StringBuilder sb = new StringBuilder();
                hyperlinks = new Hyperlink[50];
                int k = 0;
                for(String s : arrayList){
                    Hyperlink hyperlink = new Hyperlink();
                    hyperlink.setText(s);
                    hyperlink.setOnAction(event1 -> {
                        System.out.println(s);
                        String[] words = s.split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})");

                        Arrays.stream(words).forEach(System.out::println);

                        String book = words[0].toLowerCase();
                        book = book.substring(0, 1).toUpperCase() + book.substring(1); //formats book name in order to keep only first letter in capitals

                        Verse v2 = new Verse(Translation.KJV, //TODO check ESV and NASB error
                                words[0],
                                Integer.parseInt(words[1]),
                                Integer.parseInt(words[3]));

                        System.out.println(v2.text);

                    });
                    hyperlinks[k] = hyperlink;
                    k++;
                    sb.append(s);
                    sb.append('\n');
                }
                for(Hyperlink hy : hyperlinks){
                    rootBox.getChildren().add(hy); //TODO check exception
                }
                searchResultsLabel.setText(sb.toString());
                //  wordSearchRootBox.getChildren().addAll(hyperlinks);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
