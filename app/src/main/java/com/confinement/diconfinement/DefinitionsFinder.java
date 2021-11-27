package com.confinement.diconfinement;

import android.content.res.Resources;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.InputStream;
import android.icu.text.Collator;
import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DefinitionsFinder {

    @RequiresApi(api = Build.VERSION_CODES.N)
    static List<String> retrieveDefInXml(String userQuery, NodeList definitionsList, int startIndex, int stopIndex) {
        ArrayList<String> defToDisplay = new ArrayList<>();
        Integer indexDichotomic = Math.round((startIndex + stopIndex) / 2);
        if (definitionsList.item(indexDichotomic).getNodeType() == Node.ELEMENT_NODE) {
            final Element definition = (Element) definitionsList.item(indexDichotomic);
            String wordOfDictionnary = definition.getAttribute(FileUtils.wordAttribute);
            final Collator instance = Collator.getInstance(Locale.FRENCH);
            instance.setStrength(Collator.SECONDARY);
            instance.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
            if (startIndex == stopIndex && !wordOfDictionnary.equalsIgnoreCase(userQuery) || startIndex > stopIndex || stopIndex < startIndex) {
                return null;
            } else if (wordOfDictionnary != null && instance.compare(userQuery, wordOfDictionnary) > 0) {
                return retrieveDefInXml( userQuery, definitionsList, indexDichotomic + 1, stopIndex);
            } else if (wordOfDictionnary != null && instance.compare(userQuery, wordOfDictionnary) < 0) {
                return retrieveDefInXml( userQuery, definitionsList, startIndex, indexDichotomic - 1);
            } else if (wordOfDictionnary != null && wordOfDictionnary.equalsIgnoreCase(userQuery)) {
                return writeDefinition(defToDisplay, definition);
            }
        }
        return defToDisplay;
    }

    static List<String> writeDefinition(ArrayList<String> defToDisplay, Element definition) {
        NodeList typeList = definition.getElementsByTagName(Globals.typeXml);
        for (int i = 0; i<typeList.getLength(); i++) {
            definition = (Element) typeList.item(i);
            if (definition.getElementsByTagName(Globals.defXml).item(0) != null) {
                String def = definition.getElementsByTagName(Globals.defXml).item(0).getTextContent();
                String nature = definition.getElementsByTagName(Globals.natureXml).item(0).getTextContent();
                String[] stringArray = def.split("\n");
                defToDisplay.add(nature);
                for (int cpt = 0; cpt < stringArray.length; cpt++) {
                    defToDisplay.add(stringArray[cpt]);
                }
                if (definition.getElementsByTagName(Globals.synXml) != null && definition.getElementsByTagName(Globals.synXml).item(0) != null) {
                    String synonyme = definition.getElementsByTagName(Globals.synXml).item(0).getTextContent();
                    defToDisplay.add("<b>Synonymes :</b>");
                    defToDisplay.add(synonyme);
                }
            }
        }
        return defToDisplay;
    }

    static List<String> getDefinitions(Resources resources, String userQuery) {
        if (userQuery == null || userQuery.isEmpty()) {
            return null;
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
                return null;
            }
            final Element dictionnaryRacine = dictionnaryXml.getDocumentElement();
            final NodeList definitionsList = dictionnaryRacine.getElementsByTagName(Globals.definitionXml);
            List<String> definitionsRetrieved =  definitionsRetrieved = retrieveDefInXml(userQuery, definitionsList, 0, definitionsList.getLength() - 1);
            return definitionsRetrieved;
        }
        return null;
    }
}
