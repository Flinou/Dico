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

    static List<String> getDefinitions(String userQuery, DefinitionsDao db) {
        if (userQuery == null || userQuery.isEmpty()) {
            return new ArrayList<>();
        }
        userQuery = userQuery.toLowerCase();
        List<Definitions> definitions = db.findByWord(userQuery);
        return writeDefinitionDb(definitions);
    }

    private static List<String> writeDefinitionDb(List<Definitions> defRetrieved) {
        ArrayList<String> defToDisplay = new ArrayList<>();
        for (Definitions def : defRetrieved) {
            defToDisplay.add(def.nature);
            String[] defSplit = def.definition.split("\n");
            for (int i = 0; i < defSplit.length; i++) {
                defToDisplay.add(defSplit[i]);
            }
            if (def.synonym != null && !def.synonym.isEmpty()) {
                defToDisplay.add("<b>Synonymes :</b>");
                defToDisplay.add(def.synonym);
            }
        }
        return defToDisplay;
    }

}
