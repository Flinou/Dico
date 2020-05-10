package com.confinement.diconfinement;

import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import static org.junit.Assert.assertTrue;

public class FileUtilsTest {

    private static String eclairAccentTest = "éclair";
    private static String eclairTest = "eclair";
    private static String reverAccentTest = "rêver";
    private static String reverTest = "rêver";
    private static String guereAccentTest = "guère";
    private static String guereTest = "guere";
    private static String batimentAccentTest = "bâtiment";
    private static String batimentTest = "batiment";
    private static String maisAccentTest = "maïs";
    private static String maisTest = "mais";

    private static String smallword = "a";
    private static String smallwordTwo = "le";


    @Test
    public void filetoSearch() {
    }

    @Test
    public void writeToFile() {
    }

    @Test
    public void needsSave() {
    }

    @Test
    public void retrieveSuggestions() throws IOException {
        //Testing accent é
        suggestionTest(eclairAccentTest, "éclair");
        suggestionTest(eclairTest, "éclair");

        //Testing accent ê
        suggestionTest(reverAccentTest, "rêver");
        suggestionTest(reverTest, "rêver");

        //Testing accent è
        suggestionTest(guereAccentTest, "guère");
        suggestionTest(guereTest, "guère");

        //Testing accent â
        suggestionTest(batimentAccentTest, "bâtiment");
        suggestionTest(batimentTest, "bâtiment");

        //Testing accent ï
        suggestionTest(maisAccentTest, "maïs");
        suggestionTest(maisTest, "maïs");

        noSuggestionTest(smallword);
        noSuggestionTest(smallwordTwo);
    }

    private void noSuggestionTest(String smallword) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("dico.txt");
        ArrayList<String> suggestions = FileUtils.retrieveSuggestions(is, smallword);
        assertTrue(smallword + " should not have any suggestions retrieved ", suggestions.isEmpty());
        is.close();
    }

    public void suggestionTest(String searchedWord, String wordToFind) throws IOException {
        InputStream is = this.getClass().getClassLoader().getResourceAsStream("dico.txt");
        ArrayList<String> suggestions = FileUtils.retrieveSuggestions(is, searchedWord);
        assertTrue(wordToFind + " has not been found in suggestions with : " + searchedWord, suggestions.contains(wordToFind));
        is.close();
    }

    @Test
    public void removeFromFile() {
    }

    @Test
    public void retrieveSavedWords() {
    }
}