package com.confinement.diconfinement;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.List;

public class WordDayFragment extends Fragment {
    ListView listView = null;
    TextView titleView = null;
    String wordOfTheDay = null;
    MainActivity mainAct = null;
    public void setWordOfTheDay(String wordOfTheDay) {
        this.wordOfTheDay = wordOfTheDay;
    }
    public WordDayFragment() {super(R.layout.wordday_list);}

    public void setMainAct(){
        this.mainAct = (MainActivity) getActivity();
    }

    public MainActivity getMainAct(){
        return this.mainAct;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wordday_list,
                container, false);
        listView = view.findViewById(R.id.wordday_list);
        titleView = view.findViewById(R.id.textview_wordday);
        String wordOfTheDay = getContext().getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE).getString(Globals.WORD_OF_THE_DAY, null);
        setWordOfTheDay(wordOfTheDay);
        displayWordDefinition(wordOfTheDay);
        setMainAct();
        DisplayUtils.setIconAlpha(FileUtils.needsSave(getContext(), wordOfTheDay), getMainAct().saveIcon);

        return view;
    }

    @Override
    public void onResume() {
        DisplayUtils.hideHelpMenu(getActivity());
        DisplayUtils.displayAddMenu(getActivity());
        String wordOfTheDay = getContext().getSharedPreferences(Globals.PREFERENCE_FILE, Context.MODE_PRIVATE).getString(Globals.WORD_OF_THE_DAY, null);
        displayWordDefinition(wordOfTheDay);
        DisplayUtils.setIconAlpha(FileUtils.needsSave(getContext(), wordOfTheDay), getMainAct().saveIcon);
        super.onResume();
    }

    private void displayWordDefinition(String wordOfTheDay) {
        List<SpannableString> definition = retrieveWordOfTheDayDefinition(wordOfTheDay);
        titleView.setText(wordOfTheDay);
        listView.setAdapter(new WordDayAdapter(getContext(), definition));
    }

    private List<SpannableString> retrieveWordOfTheDayDefinition(String wordOfTheDay) {
        SpannableString dayWordSpan = new SpannableString(wordOfTheDay);
        dayWordSpan.setSpan(new RelativeSizeSpan(1f), 0,dayWordSpan.length(), 0);
        SpannableString defSpan = new SpannableString("");
        List<SpannableString> definitionSpan = DisplayUtils.createSpannableFromString(SharedPrefUtils.getSharedPrefDefinition(getContext(), Globals.WORD_DAY_DEF));
        for (SpannableString definitionPart : definitionSpan) {
            defSpan = new SpannableString(TextUtils.concat(defSpan, definitionPart));
        }
        return definitionSpan;
    }
}
