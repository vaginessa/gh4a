package com.gh4a.dialogs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.gh4a.R;
import com.gh4a.fragment.IssueMilestoneListFragment;

import org.eclipse.egit.github.core.Milestone;

public class MilestoneDialog extends BasePagerDialog
        implements IssueMilestoneListFragment.SelectionCallback {
    private static final String EXTRA_OWNER = "owner";
    private static final String EXTRA_REPO = "repo";
    private static final int[] TITLES = new int[] {
            R.string.open, R.string.closed
    };

    public static MilestoneDialog newInstance(String repoOwner, String repoName) {
        MilestoneDialog dialog = new MilestoneDialog();
        Bundle args = new Bundle();
        args.putString(EXTRA_OWNER, repoOwner);
        args.putString(EXTRA_REPO, repoName);
        dialog.setArguments(args);
        return dialog;
    }

    private String mRepoOwner;
    private String mRepoName;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mRepoOwner = args.getString(EXTRA_OWNER);
        mRepoName = args.getString(EXTRA_REPO);
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TITLES;
    }

    @Override
    protected Fragment makeFragment(int position) {
        return IssueMilestoneListFragment.newInstance(
                mRepoOwner,
                mRepoName,
                position == 1,
                false);
    }

    @Override
    protected boolean showDeselectButton() {
        return true;
    }

    @Override
    protected void onDeselect() {
        onMilestoneSelected(null);
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
