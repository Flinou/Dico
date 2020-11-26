package com.confinement.diconfinement;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.Random;

public class DiconfinementGameFragment extends Fragment {
    ListView listView = null;
    public DiconfinementGameFragment() {
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
