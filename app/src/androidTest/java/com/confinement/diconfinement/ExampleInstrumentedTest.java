package com.confinement.diconfinement;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        assertEquals("com.confinement.diconfinement", appContext.getPackageName());
    }

    @Test
    public void handleIntentTest() throws IOException {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        FileUtils.initFirstWordDicoHashMap(appContext);

        int dictionaryId = appContext.getResources().getIdentifier("dico","raw", "com.confinement.diconfinement");
        InputStream is = appContext.getResources().openRawResource(dictionaryId);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String missingWords = "";
        while(reader.ready()) {
            ArrayList<String> definitionList = new ArrayList<>();
            String dicoWord = reader.readLine();
            try {
                assertTrue("Le mot" + dicoWord + "n'a pas de définition", DefinitionsFinder.getDefinitions(appContext.getResources(), dicoWord, definitionList));
            } catch (AssertionError err) {
                missingWords += dicoWord + "\n";
            }
        }
        System.out.println(missingWords);
        assertTrue(missingWords.length() > 0);
    }
}
