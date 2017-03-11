package com.pendulab.theExchange.activity;

import com.pendulab.theExchange.CircularProgressBar.CircularProgressBar;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.SearchUserAdapter;
import com.pendulab.theExchange.adapter.SearchViewAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.database.DatabaseHelper;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.SearchKeyword;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 12/10/2015.
 */
public class FindUserActivity extends BaseActivity implements View.OnClickListener {

  private SearchView searchView;
  private ImageView ivSearch;
  private CircularProgressBar cbLoading;
  private Cursor cursor;
  private SearchViewAdapter svAdapter;

  private String keyword;
  private boolean firstTime = true;

  private ListView lvUser;
  private SearchUserAdapter adapter;
  private List<Account> arrUsers;
  private TextView tvNoResult;


  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_find_user);

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);

    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEYWORD_SEARCH)) {
      keyword = bundle.getString(GlobalValue.KEYWORD_SEARCH);
    }

    initUI();
    initData();
    initControl();
  }

  private void initUI() {
    lvUser = (ListView) findViewById(R.id.lvUser);
    tvNoResult = (TextView) findViewById(R.id.tvNoResult);
  }

  private void initData() {
    arrUsers = new ArrayList<>();
    adapter = new SearchUserAdapter(self, arrUsers);
    lvUser.setAdapter(adapter);
  }

  private void initControl() {
    lvUser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        gotoUserProfileActivity(arrUsers.get(i).getUserID(), arrUsers.get(i).getUsername());
      }
    });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_activity_item_browsing, menu);

    searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
    final int textViewID = searchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
    final AutoCompleteTextView searchTextView = (AutoCompleteTextView) searchView.findViewById(textViewID);
    try {
      Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
      mCursorDrawableRes.setAccessible(true);
      mCursorDrawableRes.set(searchTextView, null); //This sets the cursor resource ID to 0 or @null which will make it visible on white background
    } catch (Exception e) {
      e.printStackTrace();
    }
    searchView.setQueryHint(getString(R.string.search_by_username_or_name));

    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    if (searchManager != null) {

      cursor = DatabaseHelper.getCursorUserKeyword(myAccount.getUserID(), "");
      svAdapter = new SearchViewAdapter(self, cursor, 0);
      svAdapter.setFilterQueryProvider(new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence charSequence) {
          Cursor cursor = DatabaseHelper.getCursorUserKeyword(myAccount.getUserID(), charSequence.toString());
          return cursor;
        }
      });
      searchView.setSuggestionsAdapter(svAdapter);

      searchView.setOnSuggestionListener(new SearchView.OnSuggestionListener() {

        @Override
        public boolean onSuggestionClick(int position) {
          Cursor cursor = (Cursor) svAdapter.getItem(position);
          String selectedItem = cursor.getString(1);
          searchView.setQuery(selectedItem, true);
//                    Log.i("search view", selectedItem);
          return false;
        }

        @Override
        public boolean onSuggestionSelect(int position) {
          return false;
        }
      });

    }

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
      @Override
      public boolean onQueryTextSubmit(String query) {
        keyword = query;
        insertKeywordAndUpdateCursor(keyword);
        findUser();
        hideSoftKeyboard(searchView);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
        return false;
      }
    });

    searchView.onActionViewExpanded();
    searchView.setBackgroundResource(R.drawable.bg_search_view);

    searchView.setSubmitButtonEnabled(false);
    LinearLayout linearLayoutOfSearchView = (LinearLayout) searchView.getChildAt(0);
    LayoutInflater inflater = (LayoutInflater) self.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    View searchViewButton = inflater.inflate(R.layout.layout_search_view_button, null);
    ivSearch = (ImageView) searchViewButton.findViewById(R.id.ivSearch);
    ivSearch.setOnClickListener(this);
    cbLoading = (CircularProgressBar) searchViewButton.findViewById(R.id.pbLoading);
    LinearLayout.LayoutParams searchParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
    searchParam.gravity = Gravity.CENTER_VERTICAL;
    searchParam.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.margin_padding_tiny), 0);
    searchViewButton.setLayoutParams(searchParam);
    linearLayoutOfSearchView.addView(searchViewButton);

    searchView.clearFocus();

    if (keyword != null) {
      insertKeywordAndUpdateCursor(keyword);
      searchView.setQuery(keyword, false);
    }

    return super.onCreateOptionsMenu(menu);
  }

  private void insertKeywordAndUpdateCursor(String keyword) {
    if (DatabaseHelper.insertKeyword(new SearchKeyword(preferences.getUserInfo().getUserID(), keyword, SearchKeyword.TYPE_SEARCH_USER))) {
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {

    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onClick(View view) {
    if (view == ivSearch) {
      if (searchView.getQuery().toString().length() > 0) {
        keyword = searchView.getQuery().toString().trim();
        insertKeywordAndUpdateCursor(keyword);
        findUser();
      }
      return;
    }
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (firstTime) {
      findUser();
      firstTime = false;
    }
  }

  private void findUser() {
    List<NameValuePair> params = ParameterFactory.createSearchUserParam(keyword);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        showRefreshLayout();
      }

      @Override
      public void after(int statusCode, String response) {
        hideRefreshLayout();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Account> list = CommonParser.parseListAccountFromJson(json);
            arrUsers.clear();
            arrUsers.addAll(list);
            notifyDataset();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });

      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_SEARCH_USER);
  }

  private void notifyDataset() {
    adapter.notifyDataSetChanged();
    if (arrUsers.size() == 0) {
      tvNoResult.setVisibility(View.VISIBLE);
    } else {
      tvNoResult.setVisibility(View.GONE);
    }
  }

  private void showRefreshLayout() {
    if (ivSearch != null && cbLoading != null) {
      ivSearch.setVisibility(View.GONE);
      cbLoading.setVisibility(View.VISIBLE);
    }
  }

  private void hideRefreshLayout() {
    if (ivSearch != null && cbLoading != null) {
      ivSearch.setVisibility(View.VISIBLE);
      cbLoading.setVisibility(View.GONE);
    }
  }
}
