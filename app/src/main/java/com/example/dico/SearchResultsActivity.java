package com.example.dico;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class SearchResultsActivity extends AppCompatActivity {

    private final String wordSaved = "Mot sauvegardé dans votre liste";
    private final String wordUnsaved = "Mot retiré de votre liste";
    private final String regexpPattern = "^.*(<span class=\"ExempleDefinition\">).*(</span>).*$";
    private final String userQueryNotInDict = "Ce mot n'appartient pas au dictionnaire.";
    private String searchedWord;
    private Menu searchResultsMenu;
    private boolean needsSave;

    public static CharSequence trimTrailingWhitespace(CharSequence source) {
        if(source == null)
            return "";
        int i = source.length();

        while(--i >= 0 && Character.isWhitespace(source.charAt(i))) {
        }
        return source.subSequence(0, i+1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_activity);
       // TextView textView = findViewById(R.id.textviewsearch);
        ListView listView = findViewById(R.id.view_list);
        ArrayList<SpannableString> definition = handleIntent(getIntent());
        ArrayAdapter adapter = new ArrayAdapter<SpannableString>(this,
                R.layout.textview, definition);
        setSearchedWord("");
        if (getIntent() != null) {
            setTitle(getIntent().getStringExtra(SearchManager.QUERY));
            setSearchedWord(getIntent().getStringExtra(SearchManager.QUERY));
        }
        //textView.setText(definition);
        listView.setAdapter(adapter);
        Toolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    private String getSearchedWord() {
        return this.searchedWord;
    }

    private void setSearchedWord(String word) {
        this.searchedWord = word;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        setNeedsSave(FileUtils.needsSave(getApplicationContext(), getSearchedWord()));

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        SearchableInfo searchinfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.setQuery("",false);
                searchView.setIconified(true);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        final MenuItem saveMenuItem = menu.findItem(R.id.action_save);
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        setSearchResultsMenu(menu);
        String wordToSave = getSearchedWord();
        boolean needsSave = FileUtils.needsSave(getApplicationContext(), wordToSave);
        setIconAlpha(menu, needsSave);
        return super.onPrepareOptionsMenu(menu);
    }

    private void setIconAlpha(Menu menu, boolean needsSave) {
        MenuItem item = menu.findItem(R.id.action_save);
        Drawable resIcon = getResources().getDrawable(R.drawable.ic_save);
        if (needsSave) {
            resIcon.setAlpha(255);
        } else {
            resIcon.setAlpha(50);
        }
    }

    private Menu getSearchResultsMenu() {
        return this.searchResultsMenu;
    }

    private void setSearchResultsMenu(Menu menu) {
        this.searchResultsMenu = menu;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch (item.getItemId())  {
            case android.R.id.home :
                startActivity(new Intent(SearchResultsActivity.this, MainActivity.class));
                finish(); // close this activity and return to preview activity (if there is any)
                break;
            case R.id.action_save :
                File filesDir = getApplicationContext().getFilesDir();
                if (getNeedsSave()) {
                    String wordToSave = getSearchedWord();
                    if (filesDir != null && getSearchedWord() != null) {
                        addWordToSavedList(filesDir, wordToSave);
                    }
                } else {
                    removeWordFromSavedList(filesDir);
                }
                break;
            }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void removeWordFromSavedList(File filesDir) {
        String wordToRemove = getSearchedWord();
        FileUtils.removeFromFile(filesDir, wordToRemove);
        displayToast(wordUnsaved);
        setNeedsSave(true);
        setIconAlpha(getSearchResultsMenu(), true);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void addWordToSavedList(File filesDir, String wordToSave) {
        FileUtils.writeToFile(filesDir, wordToSave);
        displayToast(wordSaved);
        setNeedsSave(false);
        setIconAlpha(getSearchResultsMenu(), false);
    }

    private void displayToast(String stringToDisplay) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, stringToDisplay, duration);
        toast.show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private ArrayList<SpannableString> handleIntent(Intent intent) {

        ArrayList<SpannableString> list = new ArrayList<>();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            if (hasDefinitions(intent, list)) return list;
        }
        list.add(new SpannableString(userQueryNotInDict));
        return list;
    }

    private boolean hasDefinitions(Intent intent, ArrayList<SpannableString> list) {
        String userQuery = intent.getStringExtra(SearchManager.QUERY);
        userQuery = userQuery.toLowerCase();
        int file = FileUtils.filetoSearch(userQuery);

        InputStream is = getResources().openRawResource(file);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = null;
        Document dictionnaryXml =null;
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
        if (definitionsAdded != null) return definitionsAdded;
        return false;
    }

    private Boolean addDefinitionsToList(ArrayList<SpannableString> list, String userQuery, NodeList definitionsList, int definitionsNumber) {
        for (int i = 0; i<definitionsNumber; i++)
        {
            if(definitionsList.item(i).getNodeType() == Node.ELEMENT_NODE)
            {
                final Element definition = (Element) definitionsList.item(i);

                final Element nom = (Element) definition.getElementsByTagName("mot").item(0);
                String wordOfDictionnary = "";
                if (nom != null){
                    wordOfDictionnary = nom.getTextContent();
                } else {
                    return false;
                }
                if (wordOfDictionnary.equalsIgnoreCase(userQuery)){
                    String def = definition.getElementsByTagName("def").item(0).getTextContent();
                    String[] stringArray = def.split("\n");
                    Pattern p = Pattern.compile(regexpPattern);
                    for (int cpt=0; cpt<stringArray.length; cpt++) {
                        Matcher m = p.matcher(stringArray[cpt]);
                        removeUnwantedCharacters(stringArray, cpt, m);
                        list.add(new SpannableString(trimTrailingWhitespace(Html.fromHtml(stringArray[cpt]))));
                    }
                    return true;
                }
            }
        }
        return null;
    }

    private void removeUnwantedCharacters(String[] stringArray, int cpt, Matcher m) {
        if (m.matches()){
            stringArray[cpt] = stringArray[cpt].replace(m.group(1), "<br><i>");
            stringArray[cpt] = stringArray[cpt].replace(m.group(2), "</i>");
        }
        stringArray[cpt] = stringArray[cpt].replace(";:", "");
        stringArray[cpt] = stringArray[cpt].replace("<li>", "");
        stringArray[cpt] = stringArray[cpt].replace("</li>", "");
    }

    private boolean getNeedsSave() {
        return this.needsSave;
    }

    private void setNeedsSave(boolean needsSave) {
        this.needsSave=needsSave;
    }
}

