package com.confinement.diconfinement;

import static org.junit.Assert.*;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


@RunWith(RobolectricTestRunner.class)
public class WordOfTheDayUtilsTest {

    private String wordDayFileName = "testSavedWords";
    private File testWordDayFile;
    private String yesterdayDate;

    private String firstWord = "Refrain";
    private String secondWord = "doctoralement";
    private BufferedReader wordDayReader;
    private String fileContent = firstWord + "\n" +
            secondWord + "\n" +
            "multi-certification\n" +
            "attentif\n" +
            "clavicorne\n" +
            "testWordToRemove\n" +
            "hermaphrodite\n";
    private SharedPreferences sharedPref;
    private Context context;

    @Before
    public void initResources() throws IOException, ParseException {
        context = ApplicationProvider.getApplicationContext();
        testWordDayFile = Paths.get("src","test","resources", wordDayFileName).toFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(testWordDayFile));
        writer.write(fileContent);
        writer.close();
        wordDayReader = new BufferedReader(new FileReader(testWordDayFile));
        sharedPref = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        yesterdayDate = getTomorrowDate(WordOfTheDayUtils.getDateString());
    }

    private String getTomorrowDate(String dateString) throws ParseException {
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        final Date date = format.parse(dateString);
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        return format.format(calendar.getTime());
    }

    @Test
    public void isWordOfTheDayOutDated() {
        assertTrue("Word NOT declared outdated whereas it is (SharedPref empty case)", WordOfTheDayUtils.isWordOfTheDayOutDated(WordOfTheDayUtils.getDateString(), sharedPref.getString(Globals.WORD_DAYDATE, null)));
        sharedPref.edit().putString(Globals.WORD_DAYDATE, WordOfTheDayUtils.getDateString()).commit();
        assertFalse("Word declared outdated whereas it's not", WordOfTheDayUtils.isWordOfTheDayOutDated(WordOfTheDayUtils.getDateString(), sharedPref.getString(Globals.WORD_DAYDATE, null)));
        sharedPref.edit().putString(Globals.WORD_DAYDATE, yesterdayDate).commit();
        assertTrue("Word NOT declared outdated whereas it is", WordOfTheDayUtils.isWordOfTheDayOutDated(WordOfTheDayUtils.getDateString(), sharedPref.getString(Globals.WORD_DAYDATE, null)));
    }

    @Test
    public void getNextWordOfTheDay() throws FileNotFoundException {
        String wordTested = WordOfTheDayUtils.getNextWordOfTheDay(WordOfTheDayUtils.getDateString(), context, sharedPref, wordDayReader);
        assertEquals("Word retrieved when sharedpref empty is not the good one", firstWord, wordTested);
        sharedPref.edit().putString(Globals.WORD_OF_THE_DAY, secondWord).commit();
        wordDayReader = new BufferedReader(new FileReader(testWordDayFile));
        wordTested = WordOfTheDayUtils.getNextWordOfTheDay(WordOfTheDayUtils.getDateString(), context, sharedPref, wordDayReader);
        assertEquals("Word retrieved is not the good one", secondWord, wordTested);
    }
}