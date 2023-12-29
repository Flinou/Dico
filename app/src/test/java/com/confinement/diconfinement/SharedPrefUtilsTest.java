package com.confinement.diconfinement;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import com.google.gson.Gson;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(RobolectricTestRunner.class)
public class SharedPrefUtilsTest extends TestCase {

    Context context = null;
    List<String> wordToFindDef = null;
    List<String> wordToSaveDef = null;

    String wordTofind = "voiture";
    String wordToSave = "maison";
    SharedPreferences sharedPref = null;
    private File testWordDayFile;
    private String wordDayFileName = "testDayWord";
    BufferedReader wordDayReader = null;
    int indexWordDay = 3;
    private String wordDay = "attentif";
    private Date date = null;

    private String wordDays = "Refrain\n" +
            "doctoralement\n" +
            "multi-certification\n" +
            "attentif\n" +
            "clavicorne\n" +
            "testWordToRemove\n" +
            "hermaphrodite\n";
    @Before
    public void initResources() throws IOException {
        context = ApplicationProvider.getApplicationContext();
        wordToFindDef = new ArrayList<>(List.of("<i><b>Nom féminin</i></b>",
                "<li>Véhicule consistant en une caisse montée sur des roues.</li><br><br><li><i>Le 9 décembre 1776, sur les sept heures du soir, une <b>voiture</b> d’assez belle apparence, mais dans laquelle un observateur attentif pouvait reconnaître un carrosse de louage, traversa la cour de l’hôtel des finances.</i> </li>\"",
                "<b>Synonymes :</b>",
                "auto, automobile, bagnole, bolide, cage, caisse, char"));
        wordToSaveDef = new ArrayList<>(List.of("<i><b>Nom féminin</i></b>",
                "<li>(Architecture) Bâtiment servant de logis, d’habitation, de demeure</li>\"",
                "<b>Synonymes :</b>",
                "appartement, appart"));
        sharedPref = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPref.edit();
        String json = gson.toJson(wordToFindDef);
        editor.putString(wordTofind, json);
        editor.commit();
        testWordDayFile = Paths.get("src","test","resources", wordDayFileName).toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(testWordDayFile));
        wordDayReader = new BufferedReader(new FileReader(testWordDayFile));
        writer.write(wordDays);
        writer.close();
        date = new Date();
    }

    @Test
    public void testAddWordToSharedPref() {
        assertTrue("SharedPref is not empty", SharedPrefUtils.getSharedPrefDefinition(context, wordToSave) == null);
        SharedPrefUtils.addWordToSharedPref(wordToSave, context, wordToSaveDef);
        assertFalse("Message has not been saved in sharedpref", SharedPrefUtils.getSharedPrefDefinition(context, wordToSave) == null);
        assertTrue("Wrong definition added", SharedPrefUtils.getSharedPrefDefinition(context, wordToSave).containsAll(wordToSaveDef));
    }

    @Test
    public void testRemoveWordFromSharedPref() {
        assertFalse("SharedPref is empty", SharedPrefUtils.getSharedPrefDefinition(context, wordTofind) == null);
        SharedPrefUtils.removeWordFromSharedPref(wordTofind, context);
        assertTrue("Message has not been removed in sharedpref", SharedPrefUtils.getSharedPrefDefinition(context, wordTofind) == null);
    }

    @Test
    public void testGetSharedPrefDefinition() {
        List<String> wordToFindDefs = SharedPrefUtils.getSharedPrefDefinition(context, wordTofind);
        assertNotNull("No definition retrieved", wordToFindDefs);
        assertTrue("Wrong word retrieved", wordToFindDefs.containsAll(wordToFindDef));
    }

    @Test
    public void testPutWordOfTheDayDefinition() {
        assertTrue("SharedPref is not empty", SharedPrefUtils.getSharedPrefDefinition(context, Globals.WORD_DAY_DEF) == null);
        SharedPrefUtils.putWordOfTheDayDefinition(context, wordToSaveDef);
        assertFalse("Message has not been saved in sharedpref", SharedPrefUtils.getSharedPrefDefinition(context, Globals.WORD_DAY_DEF) == null);
        assertTrue("Wrong definition added", SharedPrefUtils.getSharedPrefDefinition(context, Globals.WORD_DAY_DEF).containsAll(wordToSaveDef));
    }

    @Test
    public void testPutWordOfTheDay() {
        assertTrue("SharedPref is not empty", sharedPref.getString(Globals.WORD_OF_THE_DAY, null) == null);
        SharedPrefUtils.putWordOfTheDay(context, wordToSave);
        assertFalse("Message has not been saved in sharedpref", sharedPref.getString(Globals.WORD_OF_THE_DAY, null) == null);
        assertTrue("Wrong definition added", sharedPref.getString(Globals.WORD_OF_THE_DAY, null).equalsIgnoreCase(wordToSave));
    }

    @Test
    public void testUpdateWordDayInShrdPref() {
        SharedPrefUtils.updateWordDayInShrdPref(indexWordDay, sharedPref, wordDayReader);
        assertTrue("Wrong update of wordDay", sharedPref.getString(Globals.WORD_OF_THE_DAY, null).equalsIgnoreCase(wordDay));
    }

    @Test
    public void testUpdateWordOfTheDayDateInSharedPref() {
        SharedPrefUtils.updateWordOfTheDayDateInSharedPref(String.valueOf(date), context);
        assertTrue("Wrong update of wordDay date", sharedPref.getString(Globals.WORD_DAYDATE, null).equalsIgnoreCase(String.valueOf(date)));
    }

    @Test
    public void testUpdateWordOfTheDayIndexInSharedPref() {
        SharedPrefUtils.updateWordOfTheDayIndexInSharedPref(indexWordDay, context);
        assertEquals("Wrong update of wordDay index", sharedPref.getInt(Globals.WORD_DAY_INDEX, 0), indexWordDay);
    }
}