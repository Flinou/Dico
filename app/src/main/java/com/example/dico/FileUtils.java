package com.example.dico;

import android.content.Context;
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
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class FileUtils {

    //Those words are the first of each words files. Used to know in which words file user query must be seeked.
    public static String wordOne = "a";
    public static String wordTwo = "chalcophile";
    public static String wordThree = "empeigne";
    public static String wordFour = "infanterie";
    public static String wordFive = "ottonienne";
    public static String wordSix = "runique";
    public static HashMap<String, Integer> hashFiles = new HashMap<String, Integer>() {{
        put(wordOne, R.raw.file1);
        put(wordTwo, R.raw.file2);
        put(wordThree, R.raw.file3);
        put(wordFour, R.raw.file4);
        put(wordFive, R.raw.file5);
        put(wordSix, R.raw.file6);
    }};
    private static String filename= "savedWords";
    private static String tempfilename= "tempfile";

    public static int filetoSearch(String query){
        if (query != null){
            if (query.compareTo(wordTwo) < 0 ){
                return hashFiles.get(wordOne);
            } else if (query.compareTo(wordThree) < 0 ) {
                return hashFiles.get(wordTwo);
            } else if (query.compareTo(wordFour) < 0 ) {
                return hashFiles.get(wordThree);
            } else if (query.compareTo(wordFive) < 0 ) {
                return hashFiles.get(wordFour);
            } else if (query.compareTo(wordSix) < 0 ) {
                return hashFiles.get(wordFive);
            } else {
                return hashFiles.get(wordSix);
            }
        }
        return Integer.parseInt(null);
    }

    public static void writeToFile(File filePath, String wordToAdd) {
        File file = new File(filePath, filename);
        try {
            FileWriter writer = new FileWriter(file, true);
            writer.append(wordToAdd + '\n');
            writer.close();
        } catch (IOException e) {
            System.out.println("writeToFile : An error occurred adding word to dictionnary file.");
            e.printStackTrace();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static boolean needsSave(Context context, String wordToAdd) {
        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return true;
        }
        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        StringBuilder stringBuilder = new StringBuilder();
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

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void removeFromFile(File filePath, String wordToRemove) {

        File savedWordsFile = new File(filePath, filename);
        File tempFile = new File(filePath, tempfilename);

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
    public static ArrayList<SpannableString> retrieveSavedWords(Context context) {
        ArrayList<SpannableString> savedWordslist = new ArrayList<>();
        ArrayList<String> savedWordsString = new ArrayList<>();

        FileInputStream fis = null;
        try {
            fis = context.openFileInput(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return savedWordslist;
        }
        if (fis == null) {
            return savedWordslist;
        }

        InputStreamReader inputStreamReader =
                new InputStreamReader(fis, StandardCharsets.UTF_8);
        try (BufferedReader reader = new BufferedReader(inputStreamReader)) {
            readSavedWordsList(savedWordsString, reader);
            SortAndConvertToSpannableList(savedWordslist, savedWordsString);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return savedWordslist;
    }

    private static void readSavedWordsList(ArrayList<String> savedWordsString, BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            line = line.substring(0, 1).toUpperCase() + line.substring(1);
            savedWordsString.add(line);
            line = reader.readLine();
        }
    }

    private static void SortAndConvertToSpannableList(ArrayList<SpannableString> savedWordslist, ArrayList<String> savedWordsString) {
        if (savedWordsString.size() != 0) {
            Collections.sort(savedWordsString);
            for (String word : savedWordsString) {
                savedWordslist.add(new SpannableString(word));
            }
        }
    }
}
