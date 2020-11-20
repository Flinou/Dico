package com.confinement.diconfinement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.text.SpannableString;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

//Class handling saved words putting them into SharedPreferences
public class SharedPref {

     static void addWordToSharedPref(String wordToSave, Context context, List<SpannableString> definitions) {
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

    //Method to put in sharedPref word saved by user before version 3.0
    static void putSavedWordsInSharedPref(Resources resources, Context applicationContext, ArrayList<String> wordsList) {
        SharedPreferences sharedPreferences = applicationContext.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        for (String savedWrd : wordsList) {
            if ( sharedPreferences.getString(FileUtils.normalizeString(savedWrd), null) == null ) {
                ArrayList<SpannableString> definitions = new ArrayList<>();
                DefinitionsFinder.hasDefinitions(resources, savedWrd, definitions);
                addWordToSharedPref(savedWrd, applicationContext, definitions);
            }
        }
    }
}


