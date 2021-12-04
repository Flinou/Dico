package com.confinement.diconfinement;

import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity {
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
        TabLayout tabLayout = setUpTabLayout();
        new Thread(() -> {
            runOnUiThread(() -> DisplayUtils.displayLoadingImage(toolbar, loadingLayout, tabLayout));
            Context context1 = getApplicationContext();
            FileUtils.initFirstWordDicoHashMap(context1);
            loadWordDayDefinition(context1);
            //populate dicoWords for suggestions and game
            Globals.getDicoWords(context1.getResources().openRawResource(R.raw.dico));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            runOnUiThread(() -> DisplayUtils.hideLoadingImage(toolbar, loadingLayout, tabLayout));
        }).start();
    }

    @NonNull
    private TabLayout setUpTabLayout() {
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager = findViewById(R.id.pager);
        TabCollectionAdapter tabAdapter = new TabCollectionAdapter(supportFragmentManager, getLifecycle());
        viewPager.setAdapter(tabAdapter);
        //Check if Application has been launched from word of the day notification or normally clicking the app icon
        Boolean fromNotif = getIntent().getBooleanExtra(Globals.notification, false);
        //Display word of the day Tab if launched from notif, lands to saved words tab otherwise
        if (fromNotif) {
            viewPager.setCurrentItem(2);
        } else {
            viewPager.setCurrentItem(0);
        }
        Globals.gameWordsSelection = FileUtils.generateGameWords(getResources().openRawResource(R.raw.dico));
        List<String> tabTitles = Arrays.asList(Globals.saved_words_fragment, Globals.game_words, Globals.wordOfTheDayTitle_fragment);
        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles.get(position))
        ).attach();
        return tabLayout;
    }

    /**
     * Retrieve and store in sharedPref current word of the day and definition for performances purposes
     * @param appContext
     */
    private void loadWordDayDefinition(Context appContext) {
        String oldWordOfTheDay = appContext.getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getString(Globals.wordOfTheDay, "");
        String wordOfTheDay = WordOfTheDayUtils.retrieveCurrentWordOfTheDay(appContext);
        if (!wordOfTheDay.equalsIgnoreCase(oldWordOfTheDay)) {
            SharedPrefUtils.putWordOfTheDay(appContext, wordOfTheDay);
            SharedPrefUtils.putWordOfTheDayDefinition(appContext, DefinitionsFinder.getDefinitions(getResources(), wordOfTheDay));
        }
    }

    /**
     * Set or reset alarm (for notification purpose) every time the application is upgraded or installed
     * @param context
     */
    private void setAlarmIfNeeded(Context context) {
        if (isAlarmNeeded(context)) {
            AlarmService amService = new AlarmService(context);
            amService.startAlarm();
            SharedPrefUtils.setAlarmSharedPref(context);
        }
    }

    /**
     * Check if alarm (for notification purpose) is needed. Depends on the value of the version code.
     * @param context
     * @return true if alarm needs to be set
     */
    private boolean isAlarmNeeded(Context context) {
        int versionCode = getCurrentVersionCode();
        int oldVersionCode = getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getInt(Globals.appVersion, 0);
        if (oldVersionCode == 0 || versionCode > oldVersionCode) {
            SharedPrefUtils.putNewVersionCode(context, versionCode);
            return true;
        }
        return false;

    }

    /**
     * @return current version code base on manifest file
     */
    private int getCurrentVersionCode() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = this.getPackageManager()
                    .getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo.versionCode;
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
