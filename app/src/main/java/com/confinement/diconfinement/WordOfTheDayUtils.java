package com.confinement.diconfinement;

import android.content.Context;

public class WordOfTheDayUtils {

    static String retrieveCurrentWordOfTheDay(Context context) {
        String newDate = FileUtils.updateWordOfTheDayDate(context);
        if (newDate != null) {
            return getNewWordOfTheDay(newDate, context);
        }
        return context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getString(Globals.wordOfTheDayTitle, Globals.wordOfTheDayDefault);
    }

    static String getNewWordOfTheDay(String date, Context context) {
        SharedPrefUtils.updateWordOfTheDayDateInSharedPref(date, context);
        int newWordDayIndex = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getInt(Globals.wordOfTheDayIndex, -1) + 1;
        SharedPrefUtils.updateWordOfTheDayIndexInSharedPref(newWordDayIndex, context);
        return SharedPrefUtils.updateWordOfTheDayInSharedPref(newWordDayIndex, context);
    }
}