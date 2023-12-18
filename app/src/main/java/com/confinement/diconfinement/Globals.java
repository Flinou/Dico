package com.confinement.diconfinement;
import android.text.SpannableString;

import androidx.appcompat.app.AppCompatActivity;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;


public class Globals extends AppCompatActivity {
    static final int GAME_WORDS_NUMBER = 5;
    static final int GAME_WORDS_MIN_SIZE = 4;
    static final String WORD_OF_THE_DAY = "wordOfTheDay";
    static final String WORD_OF_THE_DAY_DEFAULT = "Janotisme";
    static final String WORD_DAYDATE = "wordDaydate";
    static final String WORD_DAY_INDEX = "wordDayIndex";
    static final String WORD_DAY_DEF = "wordDayDef";
    static final String ALARM = "alarm";
    static final String LAST_NOTIFICATION_DATE = "lastNotificationDate";
    static final String CHANNEL_ID = "channel_id";
    static final String FIRST_SQL = "sorcellerie";
    static final String CHANNEL_DESCRIPTION = "Channel for new word of the day notification";
    static final CharSequence NOTIFICATION_TITLE = "Allez viens, on est bien !";
    static final CharSequence CHANNEL_NAME = "Notif channel";
    static final String GAME_WORDS = "Jeu du dico";
    static final String APP_VERSION = "appVersion";
    static final String NOTIFICATION = "notif";
    static final int SUGGESTION_NUMBERS = 3;
    static TreeSet<String> dicoWords = null;
    static HashMap<Integer, String> gameWords = null;
    static final String WORD_SAVED = "Mot sauvegardé dans votre liste";
    static final String WORD_UNSAVED = "Mot retiré de votre liste";
    static final String USER_QUERY_NOT_IN_DICT = "Ce mot n'appartient pas au dictionnaire.";
    static final String COLUMN_SUGGESTION = "wordSuggestion";
    static final String WORD_OF_THE_DAY_TITLE = "MDJ";
    static final String WORD_OF_THE_DAY_TITLE_FRAGMENT = "Mot du jour";
    static final String SAVED_WORDS_FRAGMENT = "Votre liste";
    static final String PREFERENCE_FILE = "preferenceFile";
    static final Integer SUGGESTIONS_MAX_LENGTH = 3;
    static final String GAME_NAME = "Le jeu du Diconfinement";
    static final String GAME_EXPLANATIONS = "5 mots ?\nVous en choisissez un.\nVos amis doivent ensuite en deviner le sens ou en faire la définition la plus drôle possible.\nVous choisissez l'heureux vainqueur qui prendra votre rôle au tour suivant.\nEffectivement, c'est pas fou comme jeu mais on manquait de budget." ;
    static final String SAVED_WORDS_FILE_NAME = "savedWords";
    static final String WORD_OF_THE_DAY_FILE_NAME = "dayword";
    static final String PACKAGE_NAME = "com.confinement.diconfinement";
    static List<SpannableString> gameWordsSelection = null;
    static TreeSet<String> getDicoWords(InputStream is) {
        if (dicoWords == null) {
            FileUtils.populateDicoAndGameWords(is);
        }
        return Globals.dicoWords;
    }

    static void setGameWords(HashMap<Integer, String> gameWords){
        Globals.gameWords = gameWords;
    }
    //Game words are words from dico whose size is > 4
    static HashMap<Integer, String> getGameWords(InputStream is) {
        if (gameWords == null) {
            FileUtils.populateDicoAndGameWords(is);
        }
        return Globals.gameWords;
    }
    public static void setDicoWords(TreeSet<String> wordsListSet) {
        Globals.dicoWords = wordsListSet;
    }

    public static void setGameWordsSelection(List<SpannableString> gameWordsSelection) {
        Globals.gameWordsSelection = gameWordsSelection;
    }
}
