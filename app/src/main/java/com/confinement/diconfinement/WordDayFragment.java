package com.confinement.diconfinement;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class WordDayFragment extends Fragment {

    ListView listView = null;
    ArrayList<String> wordOfTheDayDef = null;
    public WordDayFragment() {super(R.layout.wordday_list);}

    public ArrayList<String> getWordOfTheDayDef() {
        return wordOfTheDayDef;
    }

    public void setWordOfTheDayDef(ArrayList<String> wordOfTheDayDef) {
        this.wordOfTheDayDef = wordOfTheDayDef;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wordday_list,
                container, false);
        String wordOfTheDay = retrieveCurrentWordOfTheDay();
        DisplayUtils.hideHelpMenu(getActivity());
        DisplayUtils.setIconAlpha(FileUtils.needsSave(getContext(), wordOfTheDay), getResources().getDrawable(R.drawable.ic_addword));
        DisplayUtils.displayAddMenu(getActivity(), wordOfTheDay);
        listView = view.findViewById(R.id.wordday_list);
        displayWordDefinition(wordOfTheDay);
        return view;
    }

    private String retrieveCurrentWordOfTheDay() {
        String newDate = FileUtils.updateWordOfTheDayDate(getContext());
        if (newDate != null) {
            return newWordOfTheDay(newDate);
        }
        return getContext().getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getString(Globals.wordOfTheDayTitle, Globals.wordOfTheDayDefault);
    }

    private String newWordOfTheDay(String date) {
        SharedPrefUtils.updateWordOfTheDayDateInSharedPref(date, getContext());
        int newWordDayIndex = getContext().getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getInt(Globals.wordOfTheDayIndex, -1) + 1;
        SharedPrefUtils.updateWordOfTheDayIndexInSharedPref(newWordDayIndex, getContext());
        return SharedPrefUtils.updateWordOfTheDayInSharedPref(newWordDayIndex, getContext());
    }

    private void displayWordDefinition(String wordOfTheDay) {
        DisplayUtils.changeFragmentTitle(getActivity(), wordOfTheDay, getContext().getResources());
        List<SpannableString> definitionSpan = retrieveWordOfTheDayDefinition(wordOfTheDay);
        listView.setAdapter(new WordDayAdapter(getContext(), definitionSpan));
    }

    private List<SpannableString> retrieveWordOfTheDayDefinition(String wordOfTheDay) {
        if (getWordOfTheDayDef() == null) {
            ArrayList<String> definition = new ArrayList<>();
            DefinitionsFinder.hasDefinitions(getResources(), wordOfTheDay, definition);
            setWordOfTheDayDef(definition);
        }
        SharedPrefUtils.addWordOfTheDayToSharedPref(getContext(), getWordOfTheDayDef());
        SpannableString dayWordSpan = new SpannableString(wordOfTheDay);
        dayWordSpan.setSpan(new RelativeSizeSpan(1f), 0,dayWordSpan.length(), 0);
        SpannableString defSpan = new SpannableString("");
        List<SpannableString> definitionSpan = DisplayUtils.createSpannableFromString(getWordOfTheDayDef());
        for (SpannableString definitionPart : definitionSpan) {
            defSpan = new SpannableString(TextUtils.concat(defSpan, definitionPart));
        }
        return definitionSpan;
    }
}
