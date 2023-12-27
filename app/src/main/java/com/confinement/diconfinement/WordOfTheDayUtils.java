package com.confinement.diconfinement;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WordOfTheDayUtils {

    static String retrieveWordOfTheDay(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        String lastWordDayDate = sharedPref.getString(Globals.WORD_DAYDATE, null);
        boolean needsUpdate = isWordOfTheDayOutDated(currentDate, lastWordDayDate);
        if (needsUpdate) {
            return getNewWordOfTheDay(currentDate, context, sharedPref);
        } else {
            return sharedPref.getString(Globals.WORD_OF_THE_DAY_TITLE, Globals.WORD_OF_THE_DAY_DEFAULT);
        }
    }

    static boolean isWordOfTheDayOutDated(String currentDate, String lastWordDayDate) {
        if (lastWordDayDate == null || !lastWordDayDate.equalsIgnoreCase(currentDate)) {
            return true;
        } else {
            return false;
        }
    }

    static String getNewWordOfTheDay(String date, Context context, SharedPreferences sharedPref) {
        SharedPrefUtils.updateWordOfTheDayDateInSharedPref(date, context);
        //Add one to previous index
        int newWordDayIndex = sharedPref.getInt(Globals.WORD_DAY_INDEX, -1) + 1;
        SharedPrefUtils.updateWordOfTheDayIndexInSharedPref(newWordDayIndex, context);
        BufferedReader wordOfTheDayReader = FileUtils.openRawFile(Globals.WORD_OF_THE_DAY_FILE_NAME, context);
        SharedPrefUtils.updateWordDayInShrdPref(newWordDayIndex, context, sharedPref, wordOfTheDayReader);
        return sharedPref.getString(Globals.WORD_OF_THE_DAY_TITLE, null);
    }
}