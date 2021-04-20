package com.confinement.diconfinement;

import android.app.SearchManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;


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
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ImageView imageView = findViewById(R.id.logo);
        final ProgressBar progressBar = findViewById(R.id.pBar);

        //Enable navigation between fragments with bottomNavigationView
        FragmentManager supportFragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) supportFragmentManager.findFragmentById(R.id.fragment_main_activity);
        NavController navController = navHostFragment.getNavController();
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {
            }
        });
        NavigationUI.setupWithNavController(bottomNav, navController);


        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        displaySpinner(toolbar, progressBar);
                    }
                });
                Context context = getApplicationContext();
                FileUtils.initFirstWordDicoHashMap(context);
                //Necessary because of changes in sharedPreferences structure
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE);
                if (sharedPreferences.getInt(Globals.needsClear, 0) == 0) {
                    SharedPref.resetSharedPref(getResources(), context, FileUtils.retrieveSavedWords(context), sharedPreferences);
                }
                //populate dicoWords for suggestions and game
                Globals.getDicoWords(context.getResources().openRawResource(R.raw.dico));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        hideSpinner(progressBar, toolbar);
                    }
                });
            }
        }).start();
    }



    private void hideSpinner(ProgressBar progressBar, Toolbar toolbar) {
        progressBar.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
    }

    private void displaySpinner(Toolbar toolbar, ProgressBar progressBar) {
        toolbar.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
    }



    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        setMenu(menu);
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

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


}
