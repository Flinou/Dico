package com.confinement.diconfinement;
import android.text.SpannableString;

import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class Globals extends AppCompatActivity {
    static final int gameWordsNumber = 5;
    static final int gameWordsMinSize = 4;
    static TreeSet<String> dicoWords = null;
    static HashMap<Integer, String> gameWords = null;
    static final String wordSaved = "Mot sauvegardé dans votre liste";
    static final String wordUnsaved = "Mot retiré de votre liste";
    static final String regexpPattern = "^.*(<span class=\"ExempleDefinition\">).*(</span>).*$";
    static final String userQueryNotInDict = "Ce mot n'appartient pas au dictionnaire.";
    static final String defXml = "def";
    static final String synXml = "syn";
    static final String natureXml = "nature";
    static final String columnSuggestion = "wordSuggestion";
    static final String preferenceFile = "preferenceFile";
    static final Integer suggestionsMaxLength = 3;
    static String savedWordsFileName = "savedWords";
    static String packageName = "com.confinement.diconfinement";
    static ArrayList<SpannableString> gameWordsSelection = null;
    static TreeSet<String> getDicoWords(InputStream is) {
        if (dicoWords == null) {
            FileUtils.populateDicoWords(is);
        }
        return Globals.dicoWords;
    }
    static void setGameWords(HashMap<Integer, String> gameWords){
        Globals.gameWords = gameWords;
    }
    static HashMap<Integer, String> getGameWords(InputStream is) {
        if (gameWords == null) {
            FileUtils.populateDicoWords(is);
        }
        return Globals.gameWords;
    }
    public static void setDicoWords(TreeSet<String> wordsListSet) {
        Globals.dicoWords = wordsListSet;
    }

    public static void setGameWordsSelection(ArrayList<SpannableString> gameWordsSelection) {
        Globals.gameWordsSelection = gameWordsSelection;
    }
}
