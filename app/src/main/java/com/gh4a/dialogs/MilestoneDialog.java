package com.gh4a.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.gh4a.R;
import com.gh4a.fragment.IssueMilestoneListFragment;

import org.eclipse.egit.github.core.Milestone;

public class MilestoneDialog extends DialogFragment implements View.OnClickListener,
        IssueMilestoneListFragment.SelectionCallback {
    private static final String EXTRA_OWNER = "owner";
    private static final String EXTRA_REPO = "repo";

    public static MilestoneDialog newInstance(String repoOwner, String repoName) {
        MilestoneDialog dialog = new MilestoneDialog();
        Bundle args = new Bundle();
        args.putString(EXTRA_OWNER, repoOwner);
        args.putString(EXTRA_REPO, repoName);
        dialog.setArguments(args);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        Bundle args = getArguments();
        final String repoOwner = args.getString(EXTRA_OWNER);
        final String repoName = args.getString(EXTRA_REPO);

        View view = inflater.inflate(R.layout.dialog_milestone, container, false);

        ViewPager pager = view.findViewById(R.id.dialog_pager);
        pager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return IssueMilestoneListFragment.newInstance(
                        repoOwner,
                        repoName,
                        position == 1,
                        false);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return position == 1 ? "Closed" : "Open";
            }

            @Override
            public int getCount() {
                return 2;
            }
        });

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);

        Button deselectButton = view.findViewById(R.id.deselect_button);
        deselectButton.setOnClickListener(this);

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
                onMilestoneSelected(null);
                break;
        }
    }

    @Override
    public void onMilestoneSelected(@Nullable Milestone milestone) {
        FragmentActivity activity = getActivity();
        if (activity instanceof IssueMilestoneListFragment.SelectionCallback) {
            ((IssueMilestoneListFragment.SelectionCallback) activity)
                    .onMilestoneSelected(milestone);
        }
        dismiss();
    }
}
