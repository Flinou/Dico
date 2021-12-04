package com.confinement.diconfinement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

//Class handling saved words putting them into SharedPreferences
public class SharedPrefUtils {

     static void addWordToSharedPref(String wordToSave, Context context, List<String> definitions) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(definitions);
        editor.putString(FileUtils.normalizeString(wordToSave), json);
        editor.commit();
    }

    static void removeWordFromSharedPref(String wordToRemove,  Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(FileUtils.normalizeString(wordToRemove));
        editor.commit();
    }


    static ArrayList<String> getSharedPrefDefinition(Context context, String searchedWord) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        String serializedObject = sharedPreferences.getString(searchedWord, null);
        ArrayList<String> definition = null, defPref = null;
        if (serializedObject != null) {
            Gson gsonBis = new Gson();
            Type type = new TypeToken<List<String>>(){}.getType();
            defPref = gsonBis.fromJson(serializedObject, type);
            definition = new ArrayList<>(defPref);
        }
        return definition;
    }

    static void putWordOfTheDayDefinition(Context context, List<String> definitions) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(definitions);
        editor.putString(Globals.wordOfTheDayDefinition, json);
        editor.commit();
    }

    static void putWordOfTheDay(Context context, String wordOfTheDay) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.wordOfTheDay, wordOfTheDay);
        editor.commit();
    }
    static String updateWordOfTheDayInSharedPref(int index, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        BufferedReader wordOfTheDayReader = FileUtils.openRawFile(Globals.wordOfTheDayFileName, context);
        String newWordOfTheDay = null;
        try {
            newWordOfTheDay = wordOfTheDayReader.readLine();
            int cpt = 0;
            while (newWordOfTheDay != null && cpt < index) {
                newWordOfTheDay = wordOfTheDayReader.readLine();
                cpt ++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        editor.putString(Globals.wordOfTheDayTitle, newWordOfTheDay);
        editor.commit();
        return newWordOfTheDay;
    }


    static void updateWordOfTheDayDateInSharedPref(String wordDayDate, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.wordOfTheDayDate, wordDayDate);
        editor.commit();
    }

    static void updateWordOfTheDayIndexInSharedPref(int index, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Globals.wordOfTheDayIndex, index);
        editor.commit();
    }


    public static boolean isAlarmSet(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Globals.alarm, false);
    }
    public static void setAlarmSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Globals.alarm, true);
        editor.commit();
    }

    public static void updateLastNotificationDate(Context context, String date) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.lastNotificationDate, date);
        editor.commit();
    }

    public static String getLastNotificationDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Globals.lastNotificationDate, "");
    }

    public static void putNewVersionCode(Context context, int versionCode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Globals.appVersion, versionCode);
        editor.commit();
    }

}


