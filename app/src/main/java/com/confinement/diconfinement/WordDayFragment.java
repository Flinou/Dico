package com.confinement.diconfinement;

import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class WordDayFragment extends Fragment {

    ListView listView = null;
    public WordDayFragment() {super(R.layout.wordday_list);}

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onResume() {
        listView = getActivity().findViewById(R.id.wordday_list);
        DisplayUtils.changeFragmentTitle(getActivity(), Globals.wordOfTheDayTitle, getContext().getResources());

        TextView view = (TextView) getView().findViewById(R.id.textview);
        String dayWord = "abattis";
        ArrayList<String> definition = new ArrayList();
        DefinitionsFinder.hasDefinitions(getResources(), dayWord, definition);
        SpannableString dayWordSpan = new SpannableString(dayWord);
        dayWordSpan.setSpan(new RelativeSizeSpan(1f), 0,dayWordSpan.length(), 0); // set size
        SpannableString defSpan = new SpannableString("");
        List<SpannableString> definitionSpan = DisplayUtils.createSpannableFromString(definition);
        for (SpannableString definitionPart : definitionSpan) {
            defSpan = new SpannableString(TextUtils.concat(defSpan, definitionPart));
        }
        listView.setAdapter(new WordDayAdapter(getContext(), definitionSpan));
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.wordday_list,
                container, false);
        return view;
    }
}
