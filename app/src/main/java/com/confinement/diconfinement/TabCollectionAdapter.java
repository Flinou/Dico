package com.confinement.diconfinement;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class TabCollectionAdapter extends FragmentStateAdapter {

    public TabCollectionAdapter(FragmentManager supportFragmentManager, Lifecycle lifecycle) {
        super(supportFragmentManager, lifecycle);
    }

    @NonNull
        @Override
        public Fragment createFragment(int position) {
            switch (position){
                case 0:
                    return new WordsSavedFragment();
                case 1:
                    return new GameWordsFragment();
                case 2:
                     return new WordDayFragment();
            }

            return null;

        }

        @Override
        public int getItemCount() {
            return 3;
        }
    }
