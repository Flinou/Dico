package com.confinement.diconfinement;

import android.content.Context;

public class WordOfTheDayUtils {

    static String retrieveCurrentWordOfTheDay(Context context) {
        String newDate = FileUtils.updateWordOfTheDayDate(context);
        if (newDate != null) {
            return getNewWordOfTheDay(newDate, context);
        }
        return context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE).getString(Globals.WORD_OF_THE_DAY_TITLE, Globals.WORD_OF_THE_DAY_DEFAULT);
    }

    static String getNewWordOfTheDay(String date, Context context) {
        SharedPrefUtils.updateWordOfTheDayDateInSharedPref(date, context);
        int newWordDayIndex = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE).getInt(Globals.WORD_DAY_INDEX, -1) + 1;
        SharedPrefUtils.updateWordOfTheDayIndexInSharedPref(newWordDayIndex, context);
        return SharedPrefUtils.updateWordOfTheDayInSharedPref(newWordDayIndex, context);
    }
}