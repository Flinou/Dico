package com.confinement.diconfinement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

//Class handling saved words putting them into SharedPreferences
public class SharedPref {

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

    //Method to clear and put in sharedPref word saved by user before version 3.0
    static void resetSharedPref(Resources resources, Context applicationContext, ArrayList<String> wordsList, SharedPreferences sharedPreferences) {
        sharedPreferences.edit().clear().apply();
        sharedPreferences.edit().putInt(Globals.needsClear, 1).commit();
        for (String savedWrd : wordsList) {
            if ( sharedPreferences.getString(FileUtils.normalizeString(savedWrd), null) == null ) {
                ArrayList<String> definitions = new ArrayList<>();
                DefinitionsFinder.hasDefinitions(resources, savedWrd, definitions);
                addWordToSharedPref(savedWrd, applicationContext, definitions);
            }
        }
    }

    static void addWordOfTheDayToSharedPref(String wordToSave, Context context, List<String> definitions) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json = gson.toJson(definitions);
        editor.putString(Globals.wordOfTheDayDefinition, json);
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
}


