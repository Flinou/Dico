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

import java.util.ArrayList;
import java.util.List;

public class WordDayFragment extends Fragment {

    ListView listView = null;
    ArrayList<String> wordOfTheDayDef = null;


    String wordOfTheDay = null;
    public WordDayFragment() {super(R.layout.wordday_list);}

    public ArrayList<String> getWordOfTheDayDef() {
        return wordOfTheDayDef;
    }

    public void setWordOfTheDayDef(ArrayList<String> wordOfTheDayDef) {
        this.wordOfTheDayDef = wordOfTheDayDef;
    }
    public String getWordOfTheDay() {
        return wordOfTheDay;
    }

    public void setWordOfTheDay(String wordOfTheDay) {
        this.wordOfTheDay = wordOfTheDay;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wordday_list,
                container, false);
        listView = view.findViewById(R.id.wordday_list);
        String wordOfTheDay = getContext().getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getString(Globals.wordOfTheDay, null);
        setWordOfTheDay(wordOfTheDay);
        displayWordDefinition(wordOfTheDay);
        DisplayUtils.hideHelpMenu(getActivity());
        DisplayUtils.displayAddMenu(getActivity());
        DisplayUtils.setIconAlpha(FileUtils.needsSave(getContext(), wordOfTheDay), getResources().getDrawable(R.drawable.ic_addword));
        return view;
    }

    @Override
    public void onResume() {
        DisplayUtils.changeFragmentTitle(getActivity(), getWordOfTheDay(), getContext().getResources());
        String wordOfTheDay = getContext().getSharedPreferences(Globals.preferenceFile, Context.MODE_PRIVATE).getString(Globals.wordOfTheDay, null);
        displayWordDefinition(wordOfTheDay);
        DisplayUtils.setIconAlpha(FileUtils.needsSave(getContext(), wordOfTheDay), getResources().getDrawable(R.drawable.ic_addword));
        super.onResume();
    }

    private void displayWordDefinition(String wordOfTheDay) {
        DisplayUtils.changeFragmentTitle(getActivity(), wordOfTheDay, getContext().getResources());
        List<SpannableString> definitionSpan = retrieveWordOfTheDayDefinition(wordOfTheDay);
        listView.setAdapter(new WordDayAdapter(getContext(), definitionSpan));
    }

    private List<SpannableString> retrieveWordOfTheDayDefinition(String wordOfTheDay) {
        SpannableString dayWordSpan = new SpannableString(wordOfTheDay);
        dayWordSpan.setSpan(new RelativeSizeSpan(1f), 0,dayWordSpan.length(), 0);
        SpannableString defSpan = new SpannableString("");
        List<SpannableString> definitionSpan = DisplayUtils.createSpannableFromString(SharedPrefUtils.getSharedPrefDefinition(getContext(), Globals.wordOfTheDayDefinition));
        for (SpannableString definitionPart : definitionSpan) {
            defSpan = new SpannableString(TextUtils.concat(defSpan, definitionPart));
        }
        return definitionSpan;
    }
}
