package com.confinement.diconfinement;
import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.util.TreeSet;

public class Globals extends AppCompatActivity {
    static TreeSet<String> dicoWords = null;
    static final String wordSaved = "Mot sauvegardé dans votre liste";
    static final String wordUnsaved = "Mot retiré de votre liste";
    static final String regexpPattern = "^.*(<span class=\"ExempleDefinition\">).*(</span>).*$";
    static final String userQueryNotInDict = "Ce mot n'appartient pas au dictionnaire.";
    static final String defXml = "def";
    static final String natureXml = "nature";
    static final String columnSuggestion = "wordSuggestion";

    static String packageName = "com.confinement.diconfinement";
    static TreeSet<String> getDicoWords(InputStream is) {
        if (dicoWords == null) {
            dicoWords = FileUtils.populateDicoWords(is);
        }
        return dicoWords;
    }



}
