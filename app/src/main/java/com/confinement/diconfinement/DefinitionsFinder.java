package com.confinement.diconfinement;

import android.content.res.Resources;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.io.InputStream;
import android.icu.text.Collator;
import android.os.Build;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class DefinitionsFinder {

    private DefinitionsFinder() {
        throw new IllegalStateException("Utility class");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    static List<String> retrieveDefInXml(String userQuery, NodeList definitionsList, int startIndex, int stopIndex) {
        ArrayList<String> defToDisplay = new ArrayList<>();
        Integer indexDichotomic = Math.round((startIndex + stopIndex) / 2f);
        if (definitionsList.item(indexDichotomic).getNodeType() == Node.ELEMENT_NODE) {
            final Element definition = (Element) definitionsList.item(indexDichotomic);
            String wordOfDictionnary = definition.getAttribute(FileUtils.WORDATTRIBUTE);
            final Collator instance = Collator.getInstance(Locale.FRENCH);
            instance.setStrength(Collator.SECONDARY);
            instance.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
            if (startIndex == stopIndex && !wordOfDictionnary.equalsIgnoreCase(userQuery) || startIndex > stopIndex) {
                return new ArrayList<>();
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
        NodeList typeList = definition.getElementsByTagName(Globals.TYPE_XML);
        for (int i = 0; i<typeList.getLength(); i++) {
            definition = (Element) typeList.item(i);
            if (definition.getElementsByTagName(Globals.DEF_XML).item(0) != null) {
                String def = definition.getElementsByTagName(Globals.DEF_XML).item(0).getTextContent();
                String nature = definition.getElementsByTagName(Globals.NATURE_XML).item(0).getTextContent();
                String[] stringArray = def.split("\n");
                defToDisplay.add(nature);
                for (int cpt = 0; cpt < stringArray.length; cpt++) {
                    defToDisplay.add(stringArray[cpt]);
                }
                if (definition.getElementsByTagName(Globals.SYN_XML) != null && definition.getElementsByTagName(Globals.SYN_XML).item(0) != null) {
                    String synonyme = definition.getElementsByTagName(Globals.SYN_XML).item(0).getTextContent();
                    defToDisplay.add("<b>Synonymes :</b>");
                    defToDisplay.add(synonyme);
                }
            }
        }
        return defToDisplay;
    }

    static List<String> getDefinitions(Resources resources, String userQuery) {
        if (userQuery == null || userQuery.isEmpty()) {
            return new ArrayList<>();
        }
        userQuery = userQuery.toLowerCase();
        Integer file = FileUtils.filetoSearch(userQuery);
        if (file != null) {
            return seekDefinitions(resources, userQuery, file);
        }
        return new ArrayList<>();
    }

    @Nullable
    private static List<String> seekDefinitions(Resources resources, String userQuery, Integer file) {
        try (InputStream is = resources.openRawResource(file)) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            Document dictionnaryXml;
            dictionnaryXml = parseDicoFile(is, dbf);
            if (dictionnaryXml == null) return null;
            final Element dictionnaryRacine = dictionnaryXml.getDocumentElement();
            final NodeList definitionsList = dictionnaryRacine.getElementsByTagName(Globals.DEFINITION_XML);
            return retrieveDefInXml(userQuery, definitionsList, 0, definitionsList.getLength() - 1);
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Nullable
    private static Document parseDicoFile(InputStream is, DocumentBuilderFactory dbf) {
        Document dictionnaryXml;
        DocumentBuilder db;
        try {
            db = dbf.newDocumentBuilder();
            dictionnaryXml = db.parse(is);
        } catch (Exception e) {
            return null;
        }
        return dictionnaryXml;
    }
}
