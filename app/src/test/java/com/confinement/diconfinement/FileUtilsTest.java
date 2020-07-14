package com.confinement.diconfinement;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.TreeSet;

import static com.confinement.diconfinement.FileUtils.populateDicoWords;
import static org.junit.Assert.assertTrue;

public class FileUtilsTest {

    private static String eclairAccentTest = "éclair";
    private static String reverAccentTest = "rêver";
    private static String guereAccentTest = "guère";
    private static String batimentAccentTest = "bâtiment";
    private static String maisAccentTest = "maïs";

    private static String smallword = "a";
    private static String smallwordTwo = "le";
    InputStream is = null;
    TreeSet<String> dicoWords = null;

    @Before
    public void initResources() {
        is = this.getClass().getClassLoader().getResourceAsStream("dico.txt");
        dicoWords = populateDicoWords(is);
    }

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

        //Testing accent ê
        suggestionTest(reverAccentTest, "rêver");

        //Testing accent è
        suggestionTest(guereAccentTest, "guère");

        //Testing accent â
        suggestionTest(batimentAccentTest, "bâtiment");

        //Testing accent ï
        suggestionTest(maisAccentTest, "maïs");

        noSuggestionTest(smallword);
        noSuggestionTest(smallwordTwo);
    }

    private void noSuggestionTest(String wordToFind) throws IOException {
        ArrayList<String> suggestions = FileUtils.retrieveSuggestions(dicoWords, wordToFind);
        assertTrue(smallword + " should not have any suggestions retrieved ", suggestions.isEmpty());
        is.close();
    }

    public void suggestionTest(String searchedWord, String wordToFind) throws IOException {
        ArrayList<String> suggestions = FileUtils.retrieveSuggestions(dicoWords, searchedWord);
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