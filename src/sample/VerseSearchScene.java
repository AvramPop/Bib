package sample;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;

public class VerseSearchScene {

    private ChoiceBox<Translation> translationChoiceBox;
    private Button searchButton;
    private Button previousVerseButton;
    private Button nextVerseButton;
    private static Verse verse;
    private Label verseLabel;
    private Label referenceLabel;
    private Label copyrightLabel;
    private TextField verseReferenceTextField;
    private Translation translation;
    private GridPane buttonsGridPane;
    private GridPane verseInputGridPane;
    private static final Font ITALIC_FONT =
            Font.font(
                    "Serif",
                    FontPosture.ITALIC,
                    Font.getDefault().getSize()
            );

    private VBox rootBox;

    private static VerseSearchScene instance = null;

    private VerseSearchScene() {
        initializations();
        setupTranslationChoiceBox();
        setupVerseSearchButton();
        setupPreviousVerseButton();
        setupNextVerseButton();
        setupVerseSearchScene();
    }

    public static VBox getLayout() {
        if(instance == null) {
            instance = new VerseSearchScene();
        }
        return instance.rootBox;
    }

    public static VBox getLayout(Verse verse) {
        if(instance == null) {
            instance = new VerseSearchScene();
        }
        instance.verse = verse;
        instance.verseReferenceTextField.setText(verse.getFullReference().replaceAll(":", " "));
        instance.setVerseLabelFromVerse();
        return instance.rootBox;
    }

    private void setupVerseSearchScene() {
        verseInputGridPane.add(verseReferenceTextField, 0, 0);
        verseInputGridPane.add(translationChoiceBox, 1, 0);

        buttonsGridPane.add(previousVerseButton, 0,0);
        buttonsGridPane.add(nextVerseButton, 2, 0);
        buttonsGridPane.add(searchButton, 1, 0);

        rootBox.getChildren().addAll(verseInputGridPane,
                buttonsGridPane, verseLabel,
                referenceLabel, copyrightLabel);
    }

    private void setupNextVerseButton() {
        nextVerseButton.setOnAction(event -> {
            try {
                verse = verse.getNextVerse();
            } catch (VerseNotFoundException e) {
                e.printStackTrace();
            }
            setVerseLabelFromVerse();
            //unfocusVerseTextField();
        });
    }

    private void setupPreviousVerseButton() {
        previousVerseButton.setOnAction(event -> {
            try {
                verse = verse.getPreviousVerse();
            } catch (VerseNotFoundException e) {
                e.printStackTrace();
            }
            setVerseLabelFromVerse();
            //unfocusVerseTextField();
        });
    }

    private void setupVerseSearchButton() {
        searchButton.setOnAction(event -> {
            boolean success = false;
            try {
                createVerseFromTextField();
                success = true;
            } catch (VerseNotFoundException e) {
                verseLabel.setText("Verse not found :(");
                referenceLabel.setText("");
                copyrightLabel.setText("");
                verseLabel.setTextFill(Color.web("#ff2424"));
            }
            if(success) setVerseLabelFromVerse();
        });
    }

    private void setupTranslationChoiceBox() {
        // translationChoiceBox.getSelectionModel().select(0);
        translationChoiceBox.getSelectionModel()
                .selectedItemProperty()
                .addListener( (ObservableValue<? extends Translation> observable,
                               Translation oldTranslation,
                               Translation newTranslation)
                        -> translation = newTranslation);
    }

    private void setVerseLabelFromVerse() {
        String newlinedVerse = addNewlineAfterWords(verse.text, 6);
        verseLabel.setText(newlinedVerse);
        verseLabel.setFont(Font.font("Serif", FontWeight.NORMAL, Font.getDefault().getSize() + 12));
        verseLabel.setTextFill(Color.web("#000000"));
        referenceLabel.setText(verse.getFullReference() + " " + verse.translation);
        referenceLabel.setFont(Font.font("Serif", FontWeight.BOLD, Font.getDefault().getSize() + 3));
        String newLinedCopyright = addNewlineAfterWords(verse.getCopyright(), 10);

        copyrightLabel.setText(newLinedCopyright);
        copyrightLabel.setFont(ITALIC_FONT);
    }

    private void unfocusVerseTextField() {
        verseReferenceTextField.clear();
        rootBox.requestFocus();
    }

    private void createVerseFromTextField() throws VerseNotFoundException { // TODO refactor without reference to property
        try{
            String[] words = verseReferenceTextField.getText().split("\\s+");
            String book = words[0].toLowerCase();
            book = book.substring(0, 1).toUpperCase() + book.substring(1); // TODO formats book name in order to keep only first letter in capitals


            verse = new Verse(translation,
                    words[0],
                    Integer.parseInt(words[1]),
                    Integer.parseInt(words[2]));

            System.out.println(verse.text);
        } catch(Exception e) {
            throw new VerseNotFoundException("");
        }
    }

    private void initializations() {
        rootBox = new VBox();
        buttonsGridPane = new GridPane();
        verseInputGridPane = new GridPane();
        translationChoiceBox = new ChoiceBox<>(FXCollections.observableArrayList(
                Translation.values())
        );
        searchButton = new Button("Search!");
        nextVerseButton = new Button(">");
        previousVerseButton = new Button("<");
        verseLabel = new Label();
        referenceLabel = new Label();
        copyrightLabel = new Label();
        verseReferenceTextField = new PersistentPromptTextField("", "Verse reference");
        verseReferenceTextField.setMaxWidth(200);

        unfocusVerseTextFieldAtStart();
    }

    private void unfocusVerseTextFieldAtStart() {
        final BooleanProperty firstTime = new SimpleBooleanProperty(true);

        verseReferenceTextField.focusedProperty().addListener((observable,  oldValue,  newValue) -> {
            if(newValue && firstTime.get()){
                rootBox.requestFocus(); // Delegate the focus to container
                firstTime.setValue(false); // Variable value changed for future references
            }
        });
    }

    private String addNewlineAfterWords(String source, int numberOfWords){
        final String[] tokenedVerse = source.split(" ");
        final StringBuilder newString = new StringBuilder();

        for (int i = 0; i < tokenedVerse.length; i++) {
            if ((i > 0) && (0 == (i % numberOfWords))) {
                newString.append('\n');
            }

            newString.append(tokenedVerse[i]);

            if (i != (tokenedVerse.length - 1)) {
                newString.append(' ');
            }
        }

        return newString.toString();
    }
    
}
