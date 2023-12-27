package com.confinement.diconfinement;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

//Class handling saved words putting them into SharedPreferences
public class SharedPrefUtils {
    static Logger logger = Logger.getLogger(SharedPrefUtils.class.getName());
     static void addWordToSharedPref(String wordToSave, Context context, List<String> definitions) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(definitions);
        editor.putString(FileUtils.normalizeString(wordToSave), json);
        editor.commit();
    }

    static void removeWordFromSharedPref(String wordToRemove,  Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(FileUtils.normalizeString(wordToRemove));
        editor.commit();
    }


    static ArrayList<String> getSharedPrefDefinition(Context context, String searchedWord) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(definitions);
        editor.putString(Globals.WORD_DAY_DEF, json);
        editor.commit();
    }

    static void putWordOfTheDay(Context context, String wordOfTheDay) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.WORD_OF_THE_DAY, wordOfTheDay);
        editor.commit();
    }
    static void updateWordDayInShrdPref(int index, Context context, SharedPreferences sharedPref, BufferedReader wordOfDayReader) {
        SharedPreferences.Editor editor = sharedPref.edit();
        String newWordOfTheDay = "";
        try (wordOfDayReader){
            int cpt = 0;
            while ((newWordOfTheDay = wordOfDayReader.readLine()) != null && cpt < index) {
                cpt ++;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to open wordOfTheDay file");
        }
        editor.putString(Globals.WORD_OF_THE_DAY_TITLE, newWordOfTheDay);
        editor.commit();
     }


    static void updateWordOfTheDayDateInSharedPref(String wordDayDate, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.WORD_DAYDATE, wordDayDate);
        editor.commit();
    }

    static void updateWordOfTheDayIndexInSharedPref(int index, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Globals.WORD_DAY_INDEX, index);
        editor.commit();
    }


    public static boolean isAlarmSet(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(Globals.ALARM, false);
    }
    public static void setAlarmSharedPref(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Globals.ALARM, true);
        editor.commit();
    }

    public static void updateLastNotificationDate(Context context, String date) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Globals.LAST_NOTIFICATION_DATE, date);
        editor.commit();
    }

    public static String getLastNotificationDate(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        return sharedPreferences.getString(Globals.LAST_NOTIFICATION_DATE, "");
    }

    public static void putNewVersionCode(Context context, int versionCode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Globals.APP_VERSION, versionCode);
        editor.commit();
    }

}


