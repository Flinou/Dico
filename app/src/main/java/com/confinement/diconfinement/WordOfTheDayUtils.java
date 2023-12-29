package com.confinement.diconfinement;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.BufferedReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class WordOfTheDayUtils {

    static String retrieveWordOfTheDay(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        String currentDate = getDateString();
        String lastWordDayDate = sharedPref.getString(Globals.WORD_DAYDATE, null);
        boolean needsUpdate = isWordOfTheDayOutDated(currentDate, lastWordDayDate);
        if (needsUpdate) {
            return getNextWordOfTheDay(currentDate, context, sharedPref, FileUtils.openRawFile(Globals.WORD_OF_THE_DAY_FILE_NAME, context));
        } else {
            return sharedPref.getString(Globals.WORD_OF_THE_DAY, Globals.WORD_OF_THE_DAY_DEFAULT);
        }
    }

    @NonNull
    static String getDateString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    static boolean isWordOfTheDayOutDated(String currentDate, String lastWordDayDate) {
        if (lastWordDayDate == null || !lastWordDayDate.equalsIgnoreCase(currentDate)) {
            return true;
        } else {
            return false;
        }
    }

    static String getNextWordOfTheDay(String date, Context context, SharedPreferences sharedPref, BufferedReader dayWordReader) {
        SharedPrefUtils.updateWordOfTheDayDateInSharedPref(date, context);
        //Add one to previous index
        int newWordDayIndex = sharedPref.getInt(Globals.WORD_DAY_INDEX, -1) + 1;
        SharedPrefUtils.updateWordOfTheDayIndexInSharedPref(newWordDayIndex, context);
        SharedPrefUtils.updateWordDayInShrdPref(newWordDayIndex, sharedPref, dayWordReader);
        return sharedPref.getString(Globals.WORD_OF_THE_DAY, null);
    }
}