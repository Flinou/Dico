package com.confinement.diconfinement;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class GameWordsFragment extends Fragment {
    ListView listView = null;
    public GameWordsFragment() {
        super(R.layout.gamewords_list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        // listView = getActivity().findViewById(R.id.gamewords_list);
        return  inflater.inflate(R.layout.gamewords_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        listView = view.findViewById(R.id.gamewords_list);
        Button button = view.findViewById(R.id.tirage_button);
        DisplayUtils.displayHelpMenu(getActivity());
        DisplayUtils.hideAddMenu(getActivity());
        if (Globals.gameWordsSelection == null) {
            listView.setAdapter(new GameWordsAdapter(getActivity(), FileUtils.generateGameWords(getActivity().getResources().openRawResource(R.raw.dico))));
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
                listView.setAdapter(new GameWordsAdapter(getActivity(), FileUtils.generateGameWords(getActivity().getResources().openRawResource(R.raw.dico))));
            }
        });
        super.onViewCreated(view, savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {

        DisplayUtils.changeFragmentTitle(getActivity(), Globals.selection, getContext().getResources());
        super.onResume();
    }

}
