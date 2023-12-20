package com.confinement.diconfinement;

import static com.confinement.diconfinement.Globals.POSITION;
import static com.confinement.diconfinement.StringUtilsTest.allLower;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.text.SpannableString;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import androidx.test.core.app.ApplicationProvider;

@RunWith(RobolectricTestRunner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FileUtilsTest {

    private static String eclairAccentTest = "éclair";
    private static String reverAccentTest = "rêver";
    private static String guereAccentTest = "guère";
    private static String batimentAccentTest = "bâtiment";
    private static String maisAccentTest = "maïs";
    private static String accentedESuggestionTest = "rêverie";
    private static String wordTest = "TEST";
    private static int position = 0;
    private static String smallword = "a";
    private static String smallwordTwo = "le";
    InputStream is = null;
    TreeSet<String> dicoWords = null;
    Context context = null;
    private String savedWordsfileName = "testSavedWords";
    private String savedWordsfileNameTemp = "testSavedWordstemp";
    private String wordToRemove = "testWordToRemove";
    private String wordToAdd = "testWordToAdd";
    private File testSavedWordFile;
    private File testSavedWordFileTemp;
    private Path resDir;

    private String fileContent = "Refrain\n" +
            "doctoralement\n" +
            "multi-certification\n" +
            "attentif\n" +
            "clavicorne\n" +
            "testWordToRemove\n" +
            "hermaphrodite\n";

    @Before
    public void initResources() throws IOException {
        is = this.getClass().getClassLoader().getResourceAsStream("dico.txt");
        dicoWords = Globals.getDicoWords(is);
        context = ApplicationProvider.getApplicationContext();
        resDir = Paths.get("src","test","resources");
        testSavedWordFile = Paths.get("src","test","resources", savedWordsfileName).toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(testSavedWordFile));
        writer.write(fileContent);
        writer.close();
        testSavedWordFileTemp = Paths.get("src","test","resources", savedWordsfileNameTemp).toFile();

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
        //suggestionTest(reverAccentTest, "rêver");

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
    public void createdAccentedListTest() {
        ArrayList<String> accentedWritingsList = FileUtils.createAccentedWritings(accentedESuggestionTest);
        List<String> expectedResult =  Arrays.asList("reverie","réverie","rèverie","rêverie","revérie","revèrie","revêrie","révérie","révèrie","révêrie","rèvérie","rèvèrie","rèvêrie","rêvérie","rêvèrie","rêvêrie","reverié","reveriè","reveriê","réverié","réveriè","réveriê","rèverié","rèveriè","rèveriê","rêverié","rêveriè","rêveriê","revérié","revériè","revériê","revèrié","revèriè","revèriê","revêrié","revêriè","revêriê","révérié","révériè","révériê","révèrié","révèriè","révèriê","révêrié","révêriè","révêriê","rèvérié","rèvériè","rèvériê","rèvèrié","rèvèriè","rèvèriê","rèvêrié","rèvêriè","rèvêriê","rêvérié","rêvériè","rêvériê","rêvèrié","rêvèriè","rêvèriê","rêvêrié","rêvêriè","rêvêriê");
        assertTrue(accentedWritingsList.equals(expectedResult));

    }

    @Test
    public void testCreateSearchIntent() {
        SpannableString stringTest = new SpannableString(wordTest);
        Intent searchIntent = FileUtils.createSearchIntent(stringTest, position);
        assertTrue(searchIntent != null);
        assertTrue(searchIntent.getStringExtra(SearchManager.QUERY).equalsIgnoreCase(wordTest));
        assertEquals(searchIntent.getIntExtra(POSITION, 1), position);
    }

    @Test
    public void testNormalizeString() {
        String wordNormalized = FileUtils.normalizeString(wordTest);
        assertTrue(allLower(wordNormalized));
    }

    @Test
    public void testRemoveFromFile() {
        FileUtils.copyWithoutWord(wordToRemove, testSavedWordFile, testSavedWordFileTemp);
        try (BufferedReader copyReader = new BufferedReader(new FileReader(testSavedWordFileTemp))) {
            String line;
            while ((line = copyReader.readLine()) != null) {
                assertFalse(line.equalsIgnoreCase(wordToRemove));
            }
        } catch (Exception e) {
            assertTrue("Exception trying to read copied file", false);
        }
    }
    @Test
    public void testAddToFile() {
        FileUtils.addToFile(resDir.toFile(), wordToAdd, savedWordsfileName);
        try (BufferedReader copyReader = new BufferedReader(new FileReader(testSavedWordFile))) {
            String line;
            while ((line = copyReader.readLine()) != null) {
                if (line.equalsIgnoreCase(wordToAdd)) {
                    assertTrue("Word is correctly added", true);
                    return;
                }
            }
            assertTrue("Word has not been added in file", false);
        } catch (Exception e) {
            assertTrue("Exception trying to read copied file", false);
        }
    }
    @Test
    //name with y to force this test after testRemoveFromFile
    public void testYDeleteFromFile() {
        FileUtils.deleteOldFile(testSavedWordFile);
        assertFalse(testSavedWordFile.exists());
    }
    @Test
    //name with Z to force this test after testRemoveFromFile and testYDeleteFromFile
    public void testZRenameFromFile() {
        FileUtils.renameNewFile(testSavedWordFile, testSavedWordFileTemp);
        assertTrue(testSavedWordFile.exists());
        assertFalse(testSavedWordFileTemp.exists());
    }
}