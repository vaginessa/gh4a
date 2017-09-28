/*
 * Copyright 2011 Azwan Adli Abdullah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.gh4a.fragment;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gh4a.Gh4Application;
import com.gh4a.R;
import com.gh4a.activities.RepositoryActivity;
import com.gh4a.adapter.RepositoryAdapter;
import com.gh4a.adapter.RootAdapter;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.service.StarService;

import java.util.HashMap;

public class StarredRepositoryListFragment extends PagedDataBaseFragment<Repository> {
    private static final String STATE_KEY_SORT_ORDER = "sort_order";
    private static final String STATE_KEY_SORT_DIRECTION = "sort_direction";

    public static StarredRepositoryListFragment newInstance(String login) {
        StarredRepositoryListFragment f = new StarredRepositoryListFragment();

        Bundle args = new Bundle();
        args.putString("user", login);
        f.setArguments(args);

        return f;
    }

    private String mLogin;
    private String mSortOrder = "created";
    private String mSortDirection = "desc";
    private RepositoryListContainerFragment.SortDrawerHelper mSortHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLogin = getArguments().getString("user");

        mSortHelper = new RepositoryListContainerFragment.SortDrawerHelper();

        if (savedInstanceState != null && savedInstanceState.containsKey(STATE_KEY_SORT_ORDER)) {
            mSortOrder = savedInstanceState.getString(STATE_KEY_SORT_ORDER);
            mSortDirection = savedInstanceState.getString(STATE_KEY_SORT_DIRECTION);
        }
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_KEY_SORT_ORDER, mSortOrder);
        outState.putString(STATE_KEY_SORT_DIRECTION, mSortDirection);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.repo_starred_list_menu, menu);
        mSortHelper.selectSortType(menu, mSortOrder, mSortDirection, true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String[] sortOrderAndDirection = mSortHelper.handleSelectionAndGetSortOrder(item);
        if (sortOrderAndDirection == null) {
            return false;
        }
        mSortOrder = sortOrderAndDirection[0];
        mSortDirection = sortOrderAndDirection[1];
        item.setChecked(true);
        recreateIteratorAndRefresh();
        return true;
    }

    @Override
    protected RootAdapter<Repository, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        return new RepositoryAdapter(getActivity());
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_starred_repos_found;
    }

    @Override
    public void onItemClick(Repository repository) {
        startActivity(RepositoryActivity.makeIntent(getActivity(), repository));
    }

    @Override
    protected PageIterator<Repository> onCreateIterator() {
        StarService starService = (StarService)
                Gh4Application.get().getService(Gh4Application.STAR_SERVICE);
        HashMap<String, String> filterData = new HashMap<>();
        filterData.put("sort", mSortOrder);
        filterData.put("direction", mSortDirection);
        return starService.pageStarred(mLogin, filterData);
    }
}