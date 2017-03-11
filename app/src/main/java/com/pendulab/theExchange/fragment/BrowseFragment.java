package com.pendulab.theExchange.fragment;

import com.etsy.android.grid.StaggeredGridView;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.activity.ItemBrowsingActivity;
import com.pendulab.theExchange.adapter.CategoryGridAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.base.BaseFragment;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/2/2015.
 */
public class BrowseFragment extends BaseFragment {

  public static BrowseFragment instance;

  public static BrowseFragment getInstance() {
    if (instance == null) {
      instance = new BrowseFragment();
    }

    return instance;
  }

  private SwipeRefreshLayout swipeRefreshLayout;
  private StaggeredGridView gvCategory;
  private List<Category> arrCategories;
  private CategoryGridAdapter adapter;

  @Override
  public int getFragmentResource() {
    return R.layout.fragment_browse;
  }

  @Override
  public void initView(View view) {
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    gvCategory = (StaggeredGridView) view.findViewById(R.id.gvCategory);
  }

  @Override
  public void initData(View view) {
    if (adapter == null) {
      arrCategories = new ArrayList<>();
    }
    adapter = new CategoryGridAdapter(self, arrCategories, new CategoryGridAdapter.ICategoryListener() {
      @Override
      public void onClickMenu(View v, int position) {

      }

      @Override
      public void onClickView(int position) {

      }
    });
    gvCategory.setAdapter(adapter);
  }

  @Override
  public void initControl(View view) {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrCategories.clear();
            adapter.notifyDataSetChanged();
            getCategories();
          }
        });
      }
    });

    gvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        getHomeActivity().preferences.putStringValue(SharedPreferencesManager.BROWSE_CATEGORY_FILTER, arrCategories.get(i).getId());
        gotoActivity(self, ItemBrowsingActivity.class);
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    if (arrCategories.size() == 0) {
      getCategories();
    }
  }

  private void getCategories() {
    AsyncHttpGet asyncGet = new AsyncHttpGet(self, new AsyncHttpResponse() {
      @Override
      public void before() {
        showRefreshLayout();
      }

      @Override
      public void after(int statusCode, String response) {
        hideRefreshLayout();
        getHomeActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Category> list = CommonParser.parseListCategoriesFromJson(json);
            arrCategories.addAll(list);
            adapter.notifyDataSetChanged();
            getHomeActivity().preferences.putStringValue(SharedPreferencesManager.CATEGORIES, json);
          }

          @Override
          public void onRequestError(int message) {
            getHomeActivity()._onRequestError(message);
          }
        });
      }
    }, null);
    asyncGet.execute(WebServiceConfig.URL_GET_CATEGORY);
  }

  private void showRefreshLayout() {
    swipeRefreshLayout.post(new Runnable() {
      @Override
      public void run() {
        swipeRefreshLayout.setRefreshing(true);
      }
    });
  }

  private void hideRefreshLayout() {
    swipeRefreshLayout.setRefreshing(false);
  }
}
