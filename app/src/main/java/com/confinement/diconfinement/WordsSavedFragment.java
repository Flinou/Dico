package com.confinement.diconfinement;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class WordsSavedFragment extends Fragment {
    Integer index, top;
    ListView listView = null;
    public WordsSavedFragment() {
        super(R.layout.savedwords_list);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.savedwords_list,
                container, false);
        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        listView = view.findViewById(R.id.savedWords_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = listView.getItemAtPosition(position);
                //Save list index and top when exiting activity
                index = listView.getFirstVisiblePosition();
                View v = listView.getChildAt(0);
                top = (v == null) ? 0 : (v.getTop() - listView.getPaddingTop());
                SpannableString savedWord = (SpannableString) obj;
                if (savedWord != null) {
                    Intent intent = FileUtils.createSearchIntent(savedWord, position);
                    startActivity(intent);
                }
            }
        });
        displaySavedWords(listView);
        //Set saved list position when returning to activity
        if (index != null && top != null){
            listView.setSelectionFromTop(index, top);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        displaySavedWords(listView);
        DisplayUtils.hideHelpMenu(getActivity());
        DisplayUtils.hideAddMenu(getActivity());
        super.onResume();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void displaySavedWords(ListView listView) {
        ArrayList<String> savedWords = FileUtils.retrieveSavedWords(getActivity());
        ArrayList<SpannableString> savedWordsSorted = FileUtils.sortAndConvertToSpannableList(savedWords);
        listView.setAdapter(new WordsSavedAdapter(getActivity(), savedWordsSorted));
    }



}
