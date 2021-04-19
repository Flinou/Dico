package com.confinement.diconfinement;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Random;

public class GameWordsFragment extends Fragment {
    ListView listView = null;
    public GameWordsFragment() {
        super(R.layout.gamewords_list);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        listView = getActivity().findViewById(R.id.gamewords_list);
        Button button = getActivity().findViewById(R.id.tirage_button);

        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        if (toolbar != null && toolbar.getMenu() != null && toolbar.getMenu().findItem(R.id.help) == null) {
            addGameHelpButtonInToolBar(toolbar, getContext());
        }
        DisplayUtils.changeFragmentTitle(getActivity(), Globals.selection, getContext().getResources());
        toolbar.getMenu().findItem(R.id.help).setVisible(true);
        if (Globals.gameWordsSelection == null) {
            generateGameWords(listView);
        } else {
            listView.setAdapter(new GameWordsAdapter(getActivity(), Globals.gameWordsSelection));
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = listView.getItemAtPosition(position);
                SpannableString savedWord = (SpannableString) obj;
                if (savedWord != null) {
                    Intent intent = FileUtils.createSearchIntent(savedWord, position);
                    startActivity(intent);
                }
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateGameWords(listView);
            }
        });
        super.onResume();
    }

    private void addGameHelpButtonInToolBar(Toolbar toolbar, Context applicationContext) {
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.help: {
                        new AlertDialog.Builder(applicationContext)
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
                    default:
                        return false;
                }
            }

        });

        toolbar.inflateMenu(R.menu.menu_game);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void generateGameWords(ListView listView) {
        ArrayList<SpannableString> wordsToDisplay = new ArrayList<>();
        Integer size = Globals.getGameWords(getActivity().getResources().openRawResource(R.raw.dico)).size();
        for (int i=0; i<Globals.gameWordsNumber; i++){
            Random random  = new Random();
            int randomIndex = random.nextInt(size);
            wordsToDisplay.add(new SpannableString(Globals.getGameWords(getActivity().getResources().openRawResource(R.raw.dico)).get(randomIndex)));
        }
        Globals.setGameWordsSelection(wordsToDisplay);
        listView.setAdapter(new GameWordsAdapter(getActivity(), wordsToDisplay));
    }

}
