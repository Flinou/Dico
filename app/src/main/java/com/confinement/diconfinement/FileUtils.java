package com.confinement.diconfinement;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import java.text.Collator;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class FileUtils  {

    //Those words are the first of each words files. Used to know in which words file user query must be seeked.
    static final String wordAttribute = "val";
    private static LinkedHashMap<String, Integer> wordDicoHashMap = new LinkedHashMap<String, Integer>();


    static Integer filetoSearch(String query){
        final Collator instance = Collator.getInstance();
        instance.setStrength(Collator.FULL_DECOMPOSITION);
        List<String> wordDicoKeys = new ArrayList<String>(wordDicoHashMap.keySet());
        //Reverse wordDicoKeys because it's simpler to compare user query browsing dictionary from the end to the beginning
        Collections.reverse(wordDicoKeys);
        if (query != null) {
            for (String firstDef : wordDicoKeys) {
                if (instance.compare(query, firstDef) > 0) {
                    return wordDicoHashMap.get(firstDef);
                }
            }
        }
        return null;
    }

    static void writeToFile(File filePath, String wordToAdd) {
        File file = new File(filePath, Globals.savedWordsFileName);
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append(wordToAdd).append('\n');
            writer.close();
        } catch (IOException e) {
            System.out.println("writeToFile : An error occurred adding word to dictionnary file.");
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static boolean needsSave(Context context, String wordToAdd) {
        FileInputStream fis;
        try {
            fis = context.openFileInput(Globals.savedWordsFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
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
            e.printStackTrace();
        }
        return true;
    }

    static TreeSet<String> populateDicoWords(InputStream is) {
        TreeSet<String> wordsListSet = new TreeSet<>();
        final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            while (reader.ready()) {
                String currentLine = reader.readLine();
                wordsListSet.add(currentLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wordsListSet;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static ArrayList<String> retrieveSuggestions(TreeSet<String> dictioSet, String wordToComplete) {
        ArrayList<String> suggestions = new ArrayList<>();
        if (wordToComplete != null && wordToComplete.length() >= Globals.suggestionsMaxLength) {
            dictioSet.subSet(wordToComplete, wordToComplete + Character.MAX_VALUE);
            int size = 0;
            for (String suggestion : dictioSet.subSet(wordToComplete, wordToComplete + Character.MAX_VALUE)) {
                if (size == MainActivity.suggestionNumbers) {
                    break;
                }
                suggestions.add(suggestion);
                size++;
            }
        }
        return suggestions;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static void removeFromFile(File filePath, String wordToRemove) {

        File savedWordsFile = new File(filePath, Globals.savedWordsFileName);
        String tempFileName = "tempfile";
        File tempFile = new File(filePath, tempFileName);

        try (BufferedReader reader = new BufferedReader(new FileReader(savedWordsFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null && wordToRemove != null) {
                if (!line.trim().equalsIgnoreCase(wordToRemove.trim())) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!savedWordsFile.delete()) {
            System.out.println("Unable to delete file");
        }

        if (!tempFile.renameTo(savedWordsFile)){
            System.out.println("Unable to rename file");
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static ArrayList<String> retrieveSavedWords(Context context) {
        ArrayList<String> savedWordsString = new ArrayList<>();

        FileInputStream savedWordsInptStrm;
        try {
            savedWordsInptStrm = context.openFileInput(Globals.savedWordsFileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return savedWordsString;
        }
        if (savedWordsInptStrm == null) {
            return savedWordsString;
        }

        InputStreamReader savedWrdsInptStrmRdr =
                new InputStreamReader(savedWordsInptStrm, StandardCharsets.UTF_8);
        try (BufferedReader savedWordsReader = new BufferedReader(savedWrdsInptStrmRdr)) {
            readSavedWordsList(savedWordsString, savedWordsReader);
        } catch (IOException e) {
            e.printStackTrace();
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
        if (savedWordsString.size() != 0) {
            Collections.sort(savedWordsString, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    o1 = Normalizer.normalize(o1, Normalizer.Form.NFD);
                    o2 = Normalizer.normalize(o2, Normalizer.Form.NFD);
                    return o1.compareTo(o2);
                }
            });
            for (String word : savedWordsString) {
                savedWordsList.add(new SpannableString(word));
            }
        }
        return savedWordsList;
    }

    static void initFirstWordDicoHashMap(Context applicationContext) {
        final String defPackage = "com.confinement.diconfinement";
        final String dicoIdentifierPattern = "dico";
        //Way to retrieve number of dictionary files in raw folder
        int dictionNumbers= R.raw.class.getFields().length;

        //length - 1 in loop because there is dico.txt file
        for (int i=1; i<=dictionNumbers - 1; i++){
            String dicoIdentifierString = dicoIdentifierPattern + i;

            int dictionaryId = applicationContext.getResources().getIdentifier(dicoIdentifierString,"raw", defPackage);
            InputStream is = applicationContext.getResources().openRawResource(dictionaryId);
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            try {
                String line = reader.readLine();
                //We stop browsing dico at the first definition
                while(!line.contains("definition")){
                    line = reader.readLine();
                }
                //Regexp to retrieve first word of the definition. XML is like : "    <definition val="firstWord">"
                Pattern p = Pattern.compile("\\s*<.* .*\"(.*)\">");
                Matcher m = p.matcher(line);
                String fileFirstWord = null;
                if (m.matches()){
                    fileFirstWord = m.group(1);
                    wordDicoHashMap.put(fileFirstWord, dictionaryId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    static Intent createSearchIntent(SpannableString savedWord, int position) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEARCH);
        intent.putExtra(SearchManager.QUERY,savedWord.toString());
        intent.setComponent(new ComponentName(Globals.packageName, Globals.packageName + ".SearchResultsActivity"));
        intent.putExtra("position", position);
        return intent;
    }

    static String normalizeString(String stringToNormalize) {
        if (stringToNormalize != null) {
            return stringToNormalize.toLowerCase();
        }
        return null;
    }



}
