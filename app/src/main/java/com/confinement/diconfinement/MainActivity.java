package com.confinement.diconfinement;

import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    protected static final String columnSuggestion = "wordSuggestion";
    protected static final Integer suggestionNumbers = 3;
    private Menu menu;

    public Menu getMenu() {
        return menu;
    }
    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Context context = getApplicationContext();
        setAlarmIfNeeded(context);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final View loadingLayout = findViewById(R.id.loadingLayout);
        final TextView fragmentTitle = findViewById(R.id.fragment_title);
        FragmentManager supportFragmentManager = getSupportFragmentManager();


        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.pager);
        TabCollectionAdapter tabAdapter = new TabCollectionAdapter(supportFragmentManager, getLifecycle());
        viewPager.setAdapter(tabAdapter);
        Globals.gameWordsSelection = FileUtils.generateGameWords(getResources().openRawResource(R.raw.dico));
        List<String> tabTitles = Arrays.asList("Votre liste", "Jeu du Dico", "Mot du jour");
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles.get(position))
        ).attach();

        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayUtils.displaySpinner(toolbar, fragmentTitle, loadingLayout, tabLayout);
                    }
                });
                Context context = getApplicationContext();
                FileUtils.initFirstWordDicoHashMap(context);
                //Necessary because of changes in sharedPreferences structure
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
                if (sharedPreferences.getInt(Globals.needsClear, 0) == 0) {
                    SharedPrefUtils.resetSharedPref(getResources(), context, FileUtils.retrieveSavedWords(context), sharedPreferences);
                }
                //populate dicoWords for suggestions and game
                Globals.getDicoWords(context.getResources().openRawResource(R.raw.dico));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DisplayUtils.hideSpinner(toolbar, fragmentTitle, loadingLayout, tabLayout);
                    }
                });
            }
        }).start();
    }

    private void setAlarmIfNeeded(Context context) {
        if (!SharedPrefUtils.isAlarmSet(context)) {
            AlarmService amService = new AlarmService(context);
            amService.startAlarm();
            SharedPrefUtils.setAlarmSharedPref(context);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        setMenu(menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

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

        final Toolbar toolbar = findViewById(R.id.toolbar);

        DisplayUtils.hideHelpMenu(this);
        DisplayUtils.hideAddMenu(this);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.help_game: {
                        new AlertDialog.Builder(MainActivity.this)
                                .setTitle(Globals.gameName)
                                .setMessage(Globals.gameExplanations)
                                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setIcon(android.R.drawable.ic_menu_help)
                                .show();
                        return true;
                    }
                    case R.id.add_word: {
                        String wordToSave = getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getString(Globals.wordOfTheDayTitle, Globals.wordOfTheDayDefault);
                        List<String> wordOfTheDayDef = SharedPrefUtils.getSharedPrefDefinition(getApplicationContext(), Globals.wordOfTheDayDefinition);
                        FileUtils.handleSaveClick(wordToSave, wordOfTheDayDef, getApplicationContext(), getResources().getDrawable(R.drawable.ic_addword));
                    }
                    default:
                        return false;
                }
            }

        });
        return true;
    }
}
