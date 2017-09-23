package com.gh4a.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gh4a.R;

public abstract class BasePagerDialog extends DialogFragment implements View.OnClickListener {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_pager, container, false);

        ViewPager pager = view.findViewById(R.id.dialog_pager);
        pager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return makeFragment(position);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return getString(getTabTitleResIds()[position]);
            }

            @Override
            public int getCount() {
                int[] titleResIds = getTabTitleResIds();
                return titleResIds != null ? titleResIds.length : 0;
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        Button deselectButton = view.findViewById(R.id.deselect_button);
        if (showDeselectButton()) {
            deselectButton.setVisibility(View.VISIBLE);
            deselectButton.setOnClickListener(this);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        int width = getResources().getDimensionPixelSize(R.dimen.pager_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.pager_dialog_height);
        getDialog().getWindow().setLayout(width, height);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                dismiss();
                break;
            case R.id.deselect_button:
                onDeselect();
                break;
        }
    }

    protected abstract int[] getTabTitleResIds();

    protected abstract Fragment makeFragment(int position);

    protected abstract boolean showDeselectButton();

    protected void onDeselect() {
    }
}
