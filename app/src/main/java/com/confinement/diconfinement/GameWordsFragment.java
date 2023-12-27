package com.confinement.diconfinement;

import android.content.Intent;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import java.util.logging.Level;
import java.util.logging.Logger;

public class GameWordsFragment extends Fragment {
    ListView listView = null;
    static Logger logger = Logger.getLogger(FileUtils.class.getName());
    public GameWordsFragment() {
        super(R.layout.gamewords_list);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Object obj = listView.getItemAtPosition(position);
            SpannableString savedWord = (SpannableString) obj;
            if (savedWord != null) {
                Intent intent = FileUtils.createSearchIntent(savedWord, position);
                startActivity(intent);
            }
        });
        button.setOnClickListener(v -> listView.setAdapter(new GameWordsAdapter(getActivity(), FileUtils.generateGameWords(getActivity().getResources().openRawResource(R.raw.dico)))));
        super.onViewCreated(view, savedInstanceState);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        FragmentActivity activity = getActivity();
        if (listView == null && getView() != null) {
            listView = getView().findViewById(R.id.gamewords_list);;
        }
        if (Globals.gameWordsSelection == null && listView != null) {
            listView.setAdapter(new GameWordsAdapter(activity, FileUtils.generateGameWords(activity.getResources().openRawResource(R.raw.dico))));
        } else if (listView != null){
            listView.setAdapter(new GameWordsAdapter(activity, Globals.gameWordsSelection));
        }
        DisplayUtils.displayHelpMenu(activity);
        DisplayUtils.hideAddMenu(activity);
        super.onResume();
    }

}
