package com.confinement.diconfinement;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private String searchedWord;
    private Menu searchResultsMenu;
    private boolean needsSave;
    private Menu menu;
    private Integer position;
    private List<String> definitions;

    public List<String> getDefinitions() {
        return definitions;
    }

    public void setDefinitions(List<String> definitions) {
        this.definitions = definitions;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    private String getSearchedWord() {
        return this.searchedWord;
    }

    private void setSearchedWord(String word) {
        this.searchedWord = word;
    }

    private void setPosition(Integer position) {
        this.position = position;
    }

    private Integer getPosition() {
        return this.position;
    }

    private boolean getNeedsSave() {
        return this.needsSave;
    }
    private void setNeedsSave(boolean needsSave) {
        this.needsSave=needsSave;
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        setNeedsSave(FileUtils.needsSave(getApplicationContext(), getSearchedWord()));
        setMenu(menu);

        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        if (searchManager != null) {
            SearchableInfo searchinfo = searchManager.getSearchableInfo(getComponentName());
            searchView.setSearchableInfo(
                    searchManager.getSearchableInfo(getComponentName()));
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

            @Override
            public boolean onQueryTextSubmit(String query) {
                DisplayUtils.hideSearchBar(searchView);
                return false;
            }

            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onQueryTextChange(String newText) {
                AutoCompletion.getInstance().autocomplete(newText, getMenu(), (ContextWrapper) getApplicationContext());
                return true;
            }

        });
        searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                startActivity(AutoCompletion.getInstance().createIntent(searchView));
                return true;
            }
        });
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onPrepareOptionsMenu(Menu menu){
        setSearchResultsMenu(menu);
        DisplayUtils.setIconAlpha(FileUtils.needsSave(getApplicationContext(), getSearchedWord()), getResources().getDrawable(R.drawable.ic_addword));
        return super.onPrepareOptionsMenu(menu);
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
                FileUtils.handleSaveClick(getSearchedWord(), getDefinitions(), getApplicationContext(), getResources().getDrawable(R.drawable.ic_addword));
                if (getNeedsSave()) {
                    setNeedsSave(false);
                } else {
                    setNeedsSave(true);
                }
                break;
            }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    ArrayList<String> handleIntent(Intent intent) {
        ArrayList<String> list = new ArrayList<>();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String userQuery = intent.getStringExtra(SearchManager.QUERY);
            if (DefinitionsFinder.hasDefinitions(getResources(),userQuery, list)) return list;
        }
        list.add(Globals.userQueryNotInDict);
        return list;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchable_activity);
        final ListView listView = findViewById(R.id.view_list);
        String searchedWord = null;

        TextView wordsSavedTitle = findViewById(R.id.fragment_title);
        wordsSavedTitle.setVisibility(View.GONE);
        setSearchedWord("");
        if (getIntent() != null){
            searchedWord = getIntent().getStringExtra(SearchManager.QUERY);
            setTitle(searchedWord);
            setSearchedWord(searchedWord);
        }
        //Check if word is not already stored in shared preferences. If not search in dictionnnary.
        ArrayList<String> definition = SharedPrefUtils.getSharedPrefDefinition(getApplicationContext(), FileUtils.normalizeString(searchedWord));
        if (definition == null) {
            definition = handleIntent(getIntent());
        }

        setPosition(getIntent().getIntExtra("position", 0));
        List<SpannableString> definitionsSpannable = DisplayUtils.createSpannableFromString(definition);
        setDefinitions(definition);
        listView.setAdapter(new WordDayAdapter(getApplicationContext(), definitionsSpannable));

        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setNavigationIcon(R.drawable.ic_back);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(false);
            }
        }
    }

}

