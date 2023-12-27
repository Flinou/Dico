package com.confinement.diconfinement;

import android.app.SearchManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.SpannableString;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang3.StringUtils;

class FileUtils {

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }
    //Those words are the first of each words files. Used to know in which words file user query must be seeked.
    static Logger logger = Logger.getLogger(FileUtils.class.getName());
    private static LinkedHashMap<String, Integer> wordDicoHashMap = new LinkedHashMap<>();
    private static SecureRandom rand = new SecureRandom();



    static void addToFile(File filePath, String wordToAdd, String fileName) {
        File file = new File(filePath, fileName);
        try (FileWriter writer = new FileWriter(file, true)) {
            writer.append(wordToAdd).append('\n');
        } catch (IOException e) {
            logger.log(Level.WARNING,"writeToFile : An error occurred adding word to dictionnary file.");
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static boolean needsSave(Context context, String wordToAdd) {
        FileInputStream fis;
        try {
            fis = context.openFileInput(Globals.SAVED_WORDS_FILE_NAME);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "needsSave : Unable to find saved words file");
            return true;
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            String line = reader.readLine();
            while (line != null) {
                if (line.equalsIgnoreCase(wordToAdd)) {
                    return false;
                }
                line = reader.readLine();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "needsSave : Unable to open saved words file");
        }
        return true;
    }

    static void populateDicoAndGameWords(InputStream is) {
        TreeSet<String> wordsListSet = new TreeSet<>();
        HashMap<Integer, String> gameWords = new HashMap<>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        int index = 0;
        try {
            while (reader.ready()) {
                String currentLine = reader.readLine();
                wordsListSet.add(currentLine);
                if (currentLine.length() > Globals.GAME_WORDS_MIN_SIZE) {
                    gameWords.put(index, currentLine);
                    index++;
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "populateDicoAndGameWords : Unable create game words or dictionary TreeSet");
        }
        Globals.setGameWords(gameWords);
        Globals.setDicoWords(wordsListSet);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static ArrayList<String> retrieveSuggestions(TreeSet<String> dictioSet, String wordToComplete) {
        ArrayList<String> suggestions = new ArrayList<>();
        if (wordToComplete != null && wordToComplete.length() >= Globals.SUGGESTIONS_MAX_LENGTH) {
            int size = 0;
            ArrayList<String> accentedQueryList = createAccentedWritings(wordToComplete);
            for (String accentedQuery : accentedQueryList) {
                for (String suggestion : dictioSet.subSet(accentedQuery, accentedQuery + Character.MAX_VALUE)) {
                    suggestions.add(suggestion);
                    size++;
                    if (size == Globals.SUGGESTION_NUMBERS) {
                        return suggestions;
                    }
                }
            }
        }
        return suggestions;
    }

    /**
     * In order to retrieve suggestions, e in query are replaced by accented e like é, è and ê
     * Resulting list is compounded by all spellings with accented e
     * Ex: vert => [vert, vért, vèrt, vêrt]
     *
     * @param userQuery
     * @return
     */
    static ArrayList<String> createAccentedWritings(String userQuery) {
        ArrayList<String> accentedSpellingsPossibleList = new ArrayList<>();
        ArrayList<String> accentedSpellingsPossibleListDraft = new ArrayList<>();

        userQuery = removeAccent(userQuery);
        //Retrieve first e position in user query
        int firstE = userQuery.indexOf('e');
        accentedSpellingsPossibleList.add(userQuery);
        //Count number of e in word
        int count = StringUtils.countMatches(userQuery, "e");
        //For each e in word, we add all accented spellings in list of possible spellings starting by first e occurence
        for (int i = 1; i <= count; i++) {
            if (i > 1) {
                firstE = firstE + userQuery.substring(firstE + 1).indexOf('e') + 1;
            }
            for (String mot : accentedSpellingsPossibleList) {
                accentedSpellingsPossibleListDraft.addAll(replaceCharEAtIndex(firstE, mot));
            }
            accentedSpellingsPossibleList.addAll(accentedSpellingsPossibleListDraft);
            accentedSpellingsPossibleListDraft.clear();
        }
        return accentedSpellingsPossibleList;
    }

    private static String removeAccent(String userQuery) {
        userQuery = userQuery.replace('ê', 'e');
        userQuery = userQuery.replace('é', 'e');
        userQuery = userQuery.replace('è', 'e');
        return userQuery;

    }

    static ArrayList<String> replaceCharEAtIndex(int index, String wordToChange){
        ArrayList<String> allAccentedSpellingsPossible = new ArrayList<>();
        allAccentedSpellingsPossible.add(wordToChange.substring(0, index) + 'é' + wordToChange.substring(index + 1));
        allAccentedSpellingsPossible.add(wordToChange.substring(0, index) + 'è' + wordToChange.substring(index + 1));
        allAccentedSpellingsPossible.add(wordToChange.substring(0, index) + 'ê' + wordToChange.substring(index + 1));
        return allAccentedSpellingsPossible;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void removeFromFile(File filePath, String wordToRemove, String fileName) {

        File savedWordsFile = new File(filePath, fileName);
        String tempFileName = Globals.TEMP_FILE;
        File tempFile = new File(filePath, tempFileName);

        copyWithoutWord(wordToRemove, savedWordsFile, tempFile);
        deleteOldFile(savedWordsFile);
        renameNewFile(savedWordsFile, tempFile);

    }

    static void renameNewFile(File savedWordsFile, File tempFile) {
        if (!tempFile.renameTo(savedWordsFile)){
            logger.log(Level.WARNING, "removeFromFile - Unable to rename saved words file");
        }
    }

    static void deleteOldFile(File savedWordsFile) {
        if (!savedWordsFile.delete()) {
            logger.log(Level.WARNING, "removeFromFile - Unable to delete saved words file");
        }
    }

    static void copyWithoutWord(String wordToRemove, File savedWordsFile, File tempFile) {
        try (BufferedReader reader = new BufferedReader(new FileReader(savedWordsFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null && wordToRemove != null) {
                if (!line.trim().equalsIgnoreCase(wordToRemove.trim())) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Unable to remove word :" + wordToRemove + " from saved words file");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static ArrayList<String> retrieveSavedWords(Context context) {
        ArrayList<String> savedWordsString = new ArrayList<>();

        FileInputStream savedWordsInptStrm;
        try {
            savedWordsInptStrm = context.openFileInput(Globals.SAVED_WORDS_FILE_NAME);
        } catch (FileNotFoundException e) {
            logger.log(Level.WARNING, "retrieveSavedWords : Unable to find saved words file");
            return savedWordsString;
        }

        InputStreamReader savedWrdsInptStrmRdr =
                new InputStreamReader(savedWordsInptStrm, StandardCharsets.UTF_8);
        try (BufferedReader savedWordsReader = new BufferedReader(savedWrdsInptStrmRdr)) {
            readSavedWordsList(savedWordsString, savedWordsReader);
        } catch (IOException e) {
            logger.log(Level.WARNING, "retrieveSavedWords : Unable to read saved words file");
        }

        return savedWordsString;
    }

    private static void readSavedWordsList(ArrayList<String> savedWordsString, BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            line = line.substring(0, 1).toUpperCase() + line.substring(1);
            savedWordsString.add(line);
            line = reader.readLine();
        }
    }

    static ArrayList<SpannableString> sortAndConvertToSpannableList(ArrayList<String> savedWordsString) {
        ArrayList<SpannableString> savedWordsList = new ArrayList<>();
        if (!savedWordsString.isEmpty()) {
            Collections.sort(savedWordsString, (o1, o2) -> {
                o1 = Normalizer.normalize(o1, Normalizer.Form.NFD);
                o2 = Normalizer.normalize(o2, Normalizer.Form.NFD);
                return o1.compareTo(o2);
            });
            for (String word : savedWordsString) {
                savedWordsList.add(new SpannableString(word));
            }
        }
        return savedWordsList;
    }

    static Intent createSearchIntent(SpannableString savedWord, int position) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY,savedWord.toString());
        intent.setComponent(new ComponentName(Globals.PACKAGE_NAME, Globals.PACKAGE_NAME + ".SearchResultsActivity"));
        intent.putExtra(Globals.POSITION, position);
        return intent;
    }

    static String normalizeString(String stringToNormalize) {
        if (stringToNormalize != null) {
            return stringToNormalize.toLowerCase();
        }
        return null;
    }

    static BufferedReader openRawFile(String dayWordFileName, Context context) {
        int dayWordId = context.getResources().getIdentifier(dayWordFileName,"raw", Globals.PACKAGE_NAME);
        InputStream is = context.getResources().openRawResource(dayWordId);
        return new BufferedReader(new InputStreamReader(is));
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void removeWordFromSavedList(File filesDir, Context context, String wordToRemove) {
        FileUtils.removeFromFile(filesDir, wordToRemove, Globals.SAVED_WORDS_FILE_NAME);
        SharedPrefUtils.removeWordFromSharedPref(wordToRemove, context);
        DisplayUtils.displayToast(context, Globals.WORD_UNSAVED);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void addWordToSavedList(File filesDir, String wordToSave, Context context, List<String> definitions) {
        FileUtils.addToFile(filesDir, wordToSave, Globals.SAVED_WORDS_FILE_NAME);
        SharedPrefUtils.addWordToSharedPref(wordToSave, context, definitions);
        DisplayUtils.displayToast(context, Globals.WORD_SAVED);
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void handleSaveClick(String wordToSave, List<String> wordOfTheDayDef, Context context, Drawable icon) {
        File filesDir = context.getFilesDir();
        if (FileUtils.needsSave(context, wordToSave)) {
            if (filesDir != null && wordToSave != null) {
                FileUtils.addWordToSavedList(filesDir, wordToSave, context, wordOfTheDayDef);
                DisplayUtils.setIconAlpha(false, icon);
            }
        } else {
            FileUtils.removeWordFromSavedList(filesDir, context, wordToSave);
            DisplayUtils.setIconAlpha(true, icon);
        }
    }

    public static boolean needsNotification(Context context, String day) {
        return !SharedPrefUtils.getLastNotificationDate(context).equals(day);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static ArrayList<SpannableString> generateGameWords(InputStream dicoWords) {
        ArrayList<SpannableString> gameWords = new ArrayList<>();
        Integer size = Globals.getGameWords(dicoWords).size();
        for (int i = 0; i<Globals.GAME_WORDS_NUMBER; i++){
            int randomIndex = rand.nextInt(size);
            gameWords.add(new SpannableString(Globals.getGameWords(dicoWords).get(randomIndex)));
        }
        Globals.setGameWordsSelection(gameWords);
        return gameWords;
    }
}
