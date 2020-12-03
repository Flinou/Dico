package com.confinement.diconfinement;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.text.Html;
import android.text.SpannableString;

import androidx.annotation.RequiresApi;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DefinitionsFinder {

    static Boolean addDefinitionsToList(ArrayList<SpannableString> list, String userQuery, NodeList definitionsList, int definitionsNumber) {
        boolean previousDefinitionsFound = false;
        for (int i = 0; i<definitionsNumber; i++)
        {
            if(definitionsList.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                final Element definition = (Element) definitionsList.item(i);

                String wordOfDictionnary = definition.getAttribute(FileUtils.wordAttribute);

                if (wordOfDictionnary != null && wordOfDictionnary.equalsIgnoreCase(userQuery)){
                    previousDefinitionsFound = true;
                    String def = definition.getElementsByTagName(Globals.defXml).item(0).getTextContent();
                    String nature = definition.getElementsByTagName(Globals.natureXml).item(0).getTextContent();
                    String[] stringArray = def.split("\n");
                    list.add(new SpannableString(Html.fromHtml(nature)));
                    for (int cpt=0; cpt<stringArray.length; cpt++) {
                        DisplayUtils.removeUnwantedCharacters(stringArray, cpt);
                        list.add(new SpannableString(DisplayUtils.trimTrailingWhitespace(Html.fromHtml(stringArray[cpt]))));
                    }
                    if (definition.getElementsByTagName(Globals.synXml) != null && definition.getElementsByTagName(Globals.synXml).item(0) != null) {
                        String synonyme = definition.getElementsByTagName(Globals.synXml).item(0).getTextContent();
                        list.add(new SpannableString(Html.fromHtml("<b>Synonymes :</b>")));
                        list.add(new SpannableString(Html.fromHtml(synonyme)));
                    }
                } else if (!wordOfDictionnary.equalsIgnoreCase(userQuery) && previousDefinitionsFound){
                    return true;
                }
            }
        }
        return false;
    }

    static boolean hasDefinitions(Resources resources, String userQuery, ArrayList<SpannableString> list) {
        if (userQuery == null || userQuery.isEmpty()) {
            return false;
        }
        userQuery = userQuery.toLowerCase();
        Integer file = FileUtils.filetoSearch(userQuery);
        if (file != null) {
            InputStream is = resources.openRawResource(file);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;
            Document dictionnaryXml;
            try {
                db = dbf.newDocumentBuilder();
                dictionnaryXml = db.parse(is);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
            final Element dictionnaryRacine = dictionnaryXml.getDocumentElement();
            final NodeList definitionsList = dictionnaryRacine.getChildNodes();
            final int definitionsNumber = definitionsList.getLength();

            Boolean definitionsAdded = addDefinitionsToList(list, userQuery, definitionsList, definitionsNumber);
            return definitionsAdded;
        }
        return false;
    }

    static ArrayList<SpannableString> getSharedPrefDefinition(Context context, String searchedWord) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
        String serializedObject = sharedPreferences.getString(FileUtils.normalizeString(searchedWord), null);
        ArrayList<SpannableString> definition = null, defPref = null;
        if (serializedObject != null) {
            Gson gsonBis = new Gson();
            Type type = new TypeToken<List<SpannableString>>(){}.getType();
            defPref = gsonBis.fromJson(serializedObject, type);
            definition = new ArrayList<>(defPref);
        }
        return definition;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    static String getNextOrPreviousSavedWord(int index, Context context) {
        ArrayList<String> savedWordsString = FileUtils.retrieveSavedWords(context);
        if (index >= 0 && index < savedWordsString.size()) {
            ArrayList<SpannableString> savedWords = FileUtils.sortAndConvertToSpannableList(savedWordsString);
            return String.valueOf(savedWords.get(index));
        }
        return null;
    }
}
