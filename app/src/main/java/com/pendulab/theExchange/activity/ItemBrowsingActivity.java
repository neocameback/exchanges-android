package com.pendulab.theExchange.activity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import com.appyvet.rangebar.RangeBar;
import com.bumptech.glide.Glide;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnAnimationType;
import com.etiennelawlor.quickreturn.library.enums.QuickReturnViewType;
import com.etiennelawlor.quickreturn.library.listeners.QuickReturnListViewOnScrollListener;
import com.melnykov.fab.FloatingActionButton;
import com.pendulab.theExchange.CircularProgressBar.CircularProgressBar;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.CategoryFilterAdapter;
import com.pendulab.theExchange.adapter.ItemAdapter;
import com.pendulab.theExchange.adapter.LocationSearchAdapter;
import com.pendulab.theExchange.adapter.SearchViewAdapter;
import com.pendulab.theExchange.base.BaseLocationActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.database.DatabaseHelper;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.LocationObj;
import com.pendulab.theExchange.model.SearchKeyword;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.ActivityKeyboardObserver;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;
import com.pendulab.theExchange.widget.HeaderGridView;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/9/2015.
 */
public class ItemBrowsingActivity extends BaseLocationActivity implements View.OnClickListener {

  private HeaderGridView gvItems;
  private List<Item> arrItems;
  private ItemAdapter itemAdapter;
  private boolean isLoadingMore;
  private boolean hasMoreData = true;
  private Toolbar toolbar;
  private final int RC_ITEM_DETAILS = 1001;

  private QuickReturnAnimationType mQuickReturnAnimationType = QuickReturnAnimationType.TRANSLATION_SIMPLE;
  protected QuickReturnListViewOnScrollListener mScrollListener;

  private SlidingUpPanelLayout panelLayout;
  private RelativeLayout rlFilter;
  private LinearLayout llFooter, llLocation, llCategory, llSort;
  private TextView tvFrom, tvLocation, tvCategory, tvSort;
  private FloatingActionButton btnAddListing;
  private ImageView ivSearch;
  private CircularProgressBar cbLoading;
  private TextView tvNoResult;

  private SearchView searchView;
  private String keyword;
  private SearchViewAdapter svAdapter;
  private Cursor cursor;

  private ViewStub vsLocation;
  private GoogleMap maps;
  private TextView tvMapLocation, tvMapRadius;
  private EditText etSearchLocation;
  private ImageView ivSearchLocation;
  private RelativeLayout rlBottom;
  private FloatingActionButton btnAcceptLocation, btnCurrentLocation;
  private List<LocationObj> arrSearchLocations;
  private ListView lvLocationSearch;
  private LocationSearchAdapter locationAdapter;
  private LocationObj applyLocationObj, tempLocationObj;
  HashMap<Float, String> rangeMap = new HashMap<>();
  private RangeBar rangeBar;
  private int circleRadius = 10, tempCircleRadius;

  private ViewStub vsCategory;
  private TextView tvSelectedCategory, tvApplyCategory;
  private ListView lvCategory;
  private CategoryFilterAdapter categoryAdapter;
  private List<Category> arrCategories;
  private String categoryId, categoryName;

  private ViewStub vsSort;
  private TextView tvSelectedSort, tvApplySort;
  private EditText etMinPrice, etMaxPrice;
  private ListView lvSort;
  private CategoryFilterAdapter sortAdapter;
  private List<Category> arrSort;
  private int sortType, minPrice, maxPrice;
  private String sortName;

  int headerHeight = 0, footerHeight = 0;

  private final int PANEL_MAP = 1, PANEL_CATEGORY = 2, PANEL_SORT = 3;

  private final String SHOWCASE_ID = "ITEM_BROWSING_SHOWCASE";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_browsing);

    toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);
    getSupportActionBar().setElevation(0);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setTitle("");

    initUI();
    initData();
    initControl();
  }


  @Override
  public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    super.onSaveInstanceState(outState, outPersistentState);
    initParamsSearch();
    showMap();
  }

  private void initUI() {
    panelLayout = (SlidingUpPanelLayout) findViewById(R.id.panelLayout);
    gvItems = (HeaderGridView) findViewById(R.id.gvItems);
    rlFilter = (RelativeLayout) findViewById(R.id.rlFilter);
    llFooter = (LinearLayout) findViewById(R.id.llFooter);
    btnAddListing = (FloatingActionButton) findViewById(R.id.btnAddListing);
    btnAddListing.setVisibility(View.GONE);

    llLocation = (LinearLayout) findViewById(R.id.llLocation);
    llCategory = (LinearLayout) findViewById(R.id.llCategory);
    llSort = (LinearLayout) findViewById(R.id.llSort);

    tvFrom = (TextView) findViewById(R.id.tvFrom);
    tvLocation = (TextView) findViewById(R.id.tvLocation);
    tvCategory = (TextView) findViewById(R.id.tvCategory);
    tvSort = (TextView) findViewById(R.id.tvSort);
    tvNoResult = (TextView) findViewById(R.id.tvNoResult);

    LayoutInflater layoutInflater = self.getLayoutInflater();
//        footer = layoutInflater.inflate(R.layout.layout_footer, null);
    View header = layoutInflater.inflate(R.layout.layout_grid_header, null);
    gvItems.addHeaderView(header);

    Glide.with(self).load(R.drawable.ic_filter_locale).into((ImageView) findViewById(R.id.ivLocation));
    Glide.with(self).load(R.drawable.ic_locale_down).into((ImageView) findViewById(R.id.ivLocaleDown1));
    Glide.with(self).load(R.drawable.ic_locale_down).into((ImageView) findViewById(R.id.ivLocaleDown2));
    Glide.with(self).load(R.drawable.ic_locale_down).into((ImageView) findViewById(R.id.ivLocaleDown3));

    initParamsSearch();
    showMap();
  }

  private void initData() {

    rlFilter.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          // Wait until layout to call Picasso
          @Override
          public void onGlobalLayout() {
            // Ensure we call this only once
            rlFilter.getViewTreeObserver()
                .removeGlobalOnLayoutListener(this);

            headerHeight = rlFilter.getHeight();

            if (footerHeight > 0) {
              initQuickReturn();
            }
          }
        });

    llFooter.getViewTreeObserver()
        .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
          // Wait until layout to call Picasso
          @Override
          public void onGlobalLayout() {
            // Ensure we call this only once
            llFooter.getViewTreeObserver()
                .removeGlobalOnLayoutListener(this);

            footerHeight = llFooter.getHeight();
            if (headerHeight > 0) {
              initQuickReturn();
            }
          }
        });
  }

  private void initControl() {

    panelLayout.setPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
      @Override
      public void onPanelSlide(View view, float v) {

      }

      @Override
      public void onPanelCollapsed(View view) {

      }

      @Override
      public void onPanelExpanded(View view) {
        //to prevent lagging, only init rangeMap when panel is slided up completely
        initializeMap();
      }

      @Override
      public void onPanelAnchored(View view) {

      }

      @Override
      public void onPanelHidden(View view) {

      }
    });

    gvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                DialogUtility.showShortToast(self, i + "");
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValue.KEY_ITEM, arrItems.get(i).getId());
        bundle.putInt(GlobalValue.KEY_POSITION, i);
        gotoActivityForResult(self, ItemDetailsActivity.class, bundle, RC_ITEM_DETAILS);
      }
    });

    findViewById(R.id.rlBrowseOptions).setOnClickListener(this);
    llLocation.setOnClickListener(this);
    llCategory.setOnClickListener(this);
    llSort.setOnClickListener(this);
    btnAddListing.setOnClickListener(this);
  }

//    private void initShowcase() {
//        ShowcaseConfig config = new ShowcaseConfig();
//        config.setDelay(200); // half second between each showcase view
//
//        final MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID);
//
//        sequence.setOnItemShownListener(new MaterialShowcaseSequence.OnSequenceItemShownListener() {
//            @Override
//            public void onShow(MaterialShowcaseView itemView, int position) {
////                Toast.makeText(itemView.getContext(), "Item #" + position, Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        sequence.setOnItemDismissedListener(new MaterialShowcaseSequence.OnSequenceItemDismissedListener() {
//            @Override
//            public void onDismiss(MaterialShowcaseView itemView, int position) {
//                if (sequence.getmShowcaseQueue().size() == 0) {
//                    updateTutorial(GlobalValue.TUTORIAL_ITEM_BROWSING);
//                }
//            }
//        });
//
//        sequence.setConfig(config);
//
//        sequence.addSequenceItem(ivSearch, getString(R.string.tut_search_by_keyword), getString(R.string.tut_next));
//        sequence.addSequenceItem(llLocation, getString(R.string.tut_adjust_range), getString(R.string.tut_next));
//        sequence.addSequenceItem(llCategory, getString(R.string.tut_filter_by_category), getString(R.string.tut_next));
//        sequence.addSequenceItem(llSort, getString(R.string.tut_sort), getString(R.string.tut_got_it));
//
//        sequence.start();
//    }


  private void initParamsSearch() {
    categoryId = preferences.getStringValue(SharedPreferencesManager.BROWSE_CATEGORY_FILTER, Category.ALL_CATEGORIES + "");
    arrCategories = CommonParser.parseListCategoriesFromJson(preferences.getStringValue(SharedPreferencesManager.CATEGORIES));
    arrCategories.add(0, new Category(Category.WISH_LIST + "", getString(R.string.item_in_my_wishlist), ""));
    arrCategories.add(0, new Category(Category.ALL_CATEGORIES + "", getString(R.string.all_categories), ""));

    for (int i = 0; i < arrCategories.size(); i++) {
      if (arrCategories.get(i).getId().equalsIgnoreCase(categoryId)) {
        categoryName = arrCategories.get(i).getName();
        break;
      }
    }

    updateSearchViewHint(false);

    sortType = preferences.getIntValue(SharedPreferencesManager.BROWSE_SORT_FILTER);
    if (sortType == 0) sortType = Category.SORT_NEAREST;
    minPrice = preferences.getIntValue(SharedPreferencesManager.BROWSE_MIN_PRICE);
    maxPrice = preferences.getIntValue(SharedPreferencesManager.BROWSE_MAX_PRICE);
    arrSort = new ArrayList<>();
    arrSort.add(new Category(Category.SORT_POPULAR + "", getString(R.string.sort_popular), ""));
    arrSort.add(new Category(Category.SORT_RECENT + "", getString(R.string.sort_recent), ""));
    arrSort.add(new Category(Category.SORT_NEAREST + "", getString(R.string.sort_neareast), ""));
    arrSort.add(new Category(Category.SORT_PRICE_HIGHEST + "", getString(R.string.highest_price), ""));
    arrSort.add(new Category(Category.SORT_PRICE_LOWEST + "", getString(R.string.lowest_price), ""));

    for (int i = 0; i < arrSort.size(); i++) {
      if (arrSort.get(i).getId().equalsIgnoreCase(sortType + "")) {
        sortName = arrSort.get(i).getName();
        break;
      }
    }

    circleRadius = preferences.getIntValue(SharedPreferencesManager.BROWSE_RADIUS) == 0 ? 0 : preferences.getIntValue(SharedPreferencesManager.BROWSE_RADIUS);
    applyLocationObj = preferences.getBrowseLocation();

  }

  private void resetArrayItems() {
    arrItems.clear();
//        arrItems.add(new Item("-1"));
//        arrItems.add(new Item("-2"));
  }

  private void initQuickReturn() {
    arrItems = new ArrayList<>();
    itemAdapter = new ItemAdapter(self, arrItems, new ItemAdapter.IItemAdapter() {
      @Override
      public void onClickTransparentView(int position) {
        Bundle bundle = new Bundle();
        bundle.putString(GlobalValue.KEY_ITEM, arrItems.get(position).getId());
        bundle.putInt(GlobalValue.KEY_POSITION, position);
        gotoActivityForResult(self, ItemDetailsActivity.class, bundle, RC_ITEM_DETAILS);
      }

      @Override
      public void onClickLike(int position) {
        toggleWishList(position);
      }

      @Override
      public void onClickOptions(int position) {
        onShareLink(gvItems, arrItems.get(position));
      }
    });
    gvItems.setAdapter(itemAdapter);

    mScrollListener = new QuickReturnListViewOnScrollListener.Builder(QuickReturnViewType.BOTH)
        .header(rlFilter)
        .footer(llFooter)
        .minHeaderTranslation(-headerHeight)
        .minFooterTranslation(footerHeight)
        .build();


    mScrollListener.registerExtraOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView view, int scrollState) {

      }

      @Override
      public void onScroll(AbsListView absListView, int firstVisible, int lastVisible, int totalItemCount) {
        if (firstVisible + lastVisible == totalItemCount && totalItemCount > getResources().getInteger(R.integer.grid_column_count) && !isLoadingMore) {
//                    Log.i("ON_SCROLL", firstVisible + " - " + lastVisible + " - " + totalItemCount);
          isLoadingMore = true;
          getItems();
        }
      }
    });

    gvItems.setOnScrollListener(mScrollListener);
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
    updateSearchViewHint(false);

    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

      @Override
      public boolean onQueryTextSubmit(String query) {

        keyword = query;
        searchView.setQuery("", false);
        insertKeywordAndUpdateCursor(keyword);
        startBrowse();
        hideSoftKeyboard(searchView);
        return true;
      }

      @Override
      public boolean onQueryTextChange(String newText) {
//                cursor = DatabaseHelper.getCursorItemKeyword(preferences.getUserInfo().getUserID(), newText.trim());
//                svAdapter.changeCursor(cursor);
//                svAdapter.notifyDataSetChanged();
        return true;
      }

    });

//        searchView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                Log.i("SEARCH_VIEW_FOCUS", hasFocus + "");
//                updateSearchViewHint(hasFocus);
//            }
//        });

    searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
      @Override
      public void onFocusChange(View v, boolean hasFocus) {
        Log.i("SEARCH_VIEW_FOCUS", hasFocus + "");
        updateSearchViewHint(hasFocus);
      }
    });

    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    if (searchManager != null) {

      cursor = DatabaseHelper.getCursorItemKeyword(myAccount.getUserID(), "");
      svAdapter = new SearchViewAdapter(self, cursor, 0);
      svAdapter.setFilterQueryProvider(new FilterQueryProvider() {
        @Override
        public Cursor runQuery(CharSequence charSequence) {
          Cursor cursor = DatabaseHelper.getCursorItemKeyword(myAccount.getUserID(), charSequence.toString());
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

    return super.onCreateOptionsMenu(menu);
  }

  private void updateSearchViewHint(boolean focus) {
    if (!focus) {
      if (searchView != null && categoryId != null && categoryName != null) {
        if (categoryId.equalsIgnoreCase(Category.ALL_CATEGORIES + "")) {
          searchView.setQueryHint(getString(R.string.search_within) + " " + getString(R.string.app_name));
        } else {
          searchView.setQueryHint(getString(R.string.search_within) + " " + categoryName);
        }
      }
    } else {
      if (searchView != null) {
        searchView.setQueryHint("");
      }
    }
  }

  private void insertKeywordAndUpdateCursor(String keyword) {
    if (DatabaseHelper.insertKeyword(new SearchKeyword(preferences.getUserInfo().getUserID(), keyword, SearchKeyword.TYPE_SEARCH_ITEM))) {
    }
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        onBackPressed();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == RC_ITEM_DETAILS) {

        if (data.getExtras().containsKey(GlobalValue.KEY_ITEM)) {
          Item resultItem = data.getParcelableExtra(GlobalValue.KEY_ITEM);
          int currentPosition = data.getIntExtra(GlobalValue.KEY_POSITION, 0);
          arrItems.get(currentPosition).setTitle(resultItem.getTitle());
          arrItems.get(currentPosition).setLiked(resultItem.isLiked());
          arrItems.get(currentPosition).setLikeCount(resultItem.getLikeCount());
          arrItems.get(currentPosition).setImage(resultItem.getImage());
          arrItems.get(currentPosition).setPrice(resultItem.getPrice());
          itemAdapter.notifyDataSetChanged();
        } else if (data.getExtras().containsKey(GlobalValue.KEY_DELETE_ITEM)) {
          int currentPosition = data.getIntExtra(GlobalValue.KEY_POSITION, 0);
          arrItems.remove(currentPosition);
          itemAdapter.notifyDataSetChanged();
        }
      }
    }
  }

  @Override
  public void onBackPressed() {
    if (panelLayout != null && panelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
      panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    } else {
      finish();
    }
  }

  @Override
  public void onObtainLocation() {
    if (mLastLocation != null) {
      if (applyLocationObj == null) {
        applyLocationObj = new LocationObj();
        applyLocationObj.setLatitude(mLastLocation.getLatitude());
        applyLocationObj.setLongitude(mLastLocation.getLongitude());
        getLocationFromGeocode(true, mLastLocation.getLatitude(), mLastLocation.getLongitude());
      }
      if (arrItems.size() <= 0)
        startBrowse();
    }
  }

  @Override
  public void onChangeLocation() {
    if (mLastLocation != null && applyLocationObj == null) {
      getLocationFromGeocode(true, mLastLocation.getLatitude(), mLastLocation.getLongitude());
    }
  }

  @Override
  public void onLocationUnavailable() {
    if (applyLocationObj == null && circleRadius > 0) {
      showLocationAlertDialog();
    } else {
      if (arrItems.size() <= 0)
        startBrowse();
    }
  }

  private void startBrowse() {
    self.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        hasMoreData = true;
        isLoadingMore = false;
        resetArrayItems();
//                itemAdapter.notifyDataSetChanged();
        tvCategory.setText(categoryName);
        tvSort.setText(sortName);
        tvFrom.setText(circleRadius != 0 ? circleRadius + getString(R.string.km) + " " + getString(R.string.around) : "");
        tvLocation.setText(circleRadius == 0 ? getString(R.string.anywhere) : applyLocationObj.getLocationAddress().equals("") ? getString(R.string.my_location) : applyLocationObj.getLocationAddress());
        getItems();
      }
    });
  }

  private void getItems() {
    if (hasMoreData) {
      String start = arrItems.size() + "";
      List<NameValuePair> params = ParameterFactory.createGetItemsParams(keyword, categoryId, sortType, applyLocationObj != null ? applyLocationObj.getLatitude() : 0d, applyLocationObj != null ? applyLocationObj.getLongitude() : 0d, (int) circleRadius, minPrice, maxPrice, start);

      AsyncHttpPost asyncPost = new AsyncHttpPost(self, preferences.getUserInfo().getToken(), new AsyncHttpResponse() {
        @Override
        public void before() {
          showRefreshLayout();
        }

        @Override
        public void after(int statusCode, String response) {
          hideRefreshLayout();
          isLoadingMore = false;
          parseResponseStatus(statusCode, response, new NetBaseListener() {
            @Override
            public void onRequestSuccess(String json) {
              List<Item> list = CommonParser.parseListItemsFromJson(self, json);
              arrItems.addAll(list);
              itemAdapter.notifyDataSetChanged();
              if (list.size() < GlobalValue.MAX_ITEMS_PER_PAGE) hasMoreData = false;
              if (arrItems.size() <= 0) {
                tvNoResult.setVisibility(View.VISIBLE);
              } else {
                tvNoResult.setVisibility(View.GONE);
              }
              if (arrItems.size() > 0) {
//                                initShowcase();
              }
            }

            @Override
            public void onRequestError(int message) {

            }
          });
        }
      }, params);

      asyncPost.execute(WebServiceConfig.URL_GET_ITEMS);
    } else {
//            DialogUtility.showShortToast(self, getString(R.string.no_more_data));
    }
  }

  @Override
  public void onClick(View view) {

    if (view == llLocation) {
      DialogUtility.showLocationOptionsDialog(self, new DialogUtility.LocationOptionsListener() {
        @Override
        public void onClickAnywhere(Dialog dialog) {
          if (circleRadius != 0) {
            circleRadius = 0;
            preferences.putIntValue(SharedPreferencesManager.BROWSE_RADIUS, circleRadius);
            startBrowse();
          }
        }

        @Override
        public void onClickSelectLocation(Dialog dialog) {
          if (applyLocationObj == null) {
            showLocationAlertDialog();
            return;
          }
          slideUpPanel(PANEL_MAP);
        }
      });

      return;
    }

    if (view == llCategory) {
      slideUpPanel(PANEL_CATEGORY);
      return;
    }

    if (view == llSort) {
      slideUpPanel(PANEL_SORT);
      return;
    }
    if (view == ivSearch) {
      keyword = searchView.getQuery().toString().trim();
      searchView.setQuery("", false);
      insertKeywordAndUpdateCursor(keyword);
      startBrowse();
      return;
    }
    if (view == btnAddListing) {
      gotoActivity(self, AddListingActivity.class);
      return;
    }

  }

  private void slideUpPanel(int type) {
    switch (type) {
      case PANEL_MAP:
        showMap();
        break;
      case PANEL_CATEGORY:
        showCategoryFilter();
        break;
      case PANEL_SORT:
        showSortFilter();
        break;
    }

    showPanel();
  }

  private void showPanel() {
    if (panelLayout != null && panelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED) {
      panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }
  }

  private void hidePanel() {
    if (panelLayout != null && panelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
      panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }
  }


  private void showCategoryFilter() {

    if (vsSort != null) {
      vsSort.setVisibility(View.GONE);
    }
    if (vsLocation != null) {
      vsLocation.setVisibility(View.GONE);
    }

    if (vsCategory == null) {
      vsCategory = (ViewStub) findViewById(R.id.vsCategory);
      View inflateCategory = vsCategory.inflate();
      tvSelectedCategory = (TextView) inflateCategory.findViewById(R.id.tvSelectedCategory);
      tvApplyCategory = (TextView) inflateCategory.findViewById(R.id.tvApply);
      lvCategory = (ListView) inflateCategory.findViewById(R.id.lvCategory);

      categoryAdapter = new CategoryFilterAdapter(self, arrCategories);
      lvCategory.setAdapter(categoryAdapter);

      for (int i = 0; i < arrCategories.size(); i++) {
        if (arrCategories.get(i).getId().equalsIgnoreCase(categoryId)) {
          categoryAdapter.setSelectedPosition(i);
          tvSelectedCategory.setText(arrCategories.get(i).getName());
          break;
        }
      }

      categoryAdapter.notifyDataSetChanged();

      lvCategory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          categoryAdapter.setSelectedPosition(i);
          tvSelectedCategory.setText(arrCategories.get(i).getName());
          categoryAdapter.notifyDataSetChanged();
        }
      });

      tvApplyCategory.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          categoryId = arrCategories.get(categoryAdapter.getSelectedPosition()).getId();
          categoryName = arrCategories.get(categoryAdapter.getSelectedPosition()).getName();
          preferences.putStringValue(SharedPreferencesManager.BROWSE_CATEGORY_FILTER, categoryId);
          updateSearchViewHint(false);
          hidePanel();
          startBrowse();
        }
      });
    } else {
      vsCategory.setVisibility(View.VISIBLE);
      for (int i = 0; i < arrCategories.size(); i++) {
        if (arrCategories.get(i).getId().equalsIgnoreCase(categoryId)) {
          categoryAdapter.setSelectedPosition(i);
          tvSelectedCategory.setText(arrCategories.get(i).getName());
          break;
        }
      }
      categoryAdapter.notifyDataSetChanged();
    }
  }

  private void showSortFilter() {

    if (vsCategory != null) {
      vsCategory.setVisibility(View.GONE);
    }

    if (vsLocation != null) {
      vsLocation.setVisibility(View.GONE);
    }

    if (vsSort == null) {
      vsSort = (ViewStub) findViewById(R.id.vsSort);
      View inflateSort = vsSort.inflate();
      tvSelectedSort = (TextView) inflateSort.findViewById(R.id.tvSelectedSort);
      tvApplySort = (TextView) inflateSort.findViewById(R.id.tvApply);
      lvSort = (ListView) inflateSort.findViewById(R.id.lvSort);

      sortAdapter = new CategoryFilterAdapter(self, arrSort);
      lvSort.setAdapter(sortAdapter);

      for (int i = 0; i < arrSort.size(); i++) {
        if (arrSort.get(i).getId().equalsIgnoreCase(sortType + "")) {
          sortAdapter.setSelectedPosition(i);
          tvSelectedSort.setText(arrSort.get(i).getName());
          break;
        }
      }

      sortAdapter.notifyDataSetChanged();

      lvSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          sortAdapter.setSelectedPosition(i);
          tvSelectedSort.setText(arrSort.get(i).getName());
          sortAdapter.notifyDataSetChanged();
        }
      });

      etMinPrice = (EditText) inflateSort.findViewById(R.id.etMinPrice);
      etMaxPrice = (EditText) inflateSort.findViewById(R.id.etMaxPrice);

      etMinPrice.setText(minPrice > 0 ? minPrice + "" : "");
      etMaxPrice.setText(maxPrice > 0 ? maxPrice + "" : "");


      tvApplySort.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          sortType = Integer.parseInt(arrSort.get(sortAdapter.getSelectedPosition()).getId());
          sortName = arrSort.get(sortAdapter.getSelectedPosition()).getName();
          preferences.putIntValue(SharedPreferencesManager.BROWSE_SORT_FILTER, sortType);

          minPrice = etMinPrice.getText().toString().trim().length() > 0 ? Integer.parseInt(etMinPrice.getText().toString().trim()) : 0;
          preferences.putIntValue(SharedPreferencesManager.BROWSE_MIN_PRICE, minPrice);

          maxPrice = etMaxPrice.getText().toString().trim().length() > 0 ? Integer.parseInt(etMaxPrice.getText().toString().trim()) : 0;
          preferences.putIntValue(SharedPreferencesManager.BROWSE_MAX_PRICE, maxPrice);

          hidePanel();
          startBrowse();
        }
      });
    } else {
      vsSort.setVisibility(View.VISIBLE);
      for (int i = 0; i < arrSort.size(); i++) {
        if (arrSort.get(i).getId().equalsIgnoreCase(sortType + "")) {
          sortAdapter.setSelectedPosition(i);
          tvSelectedSort.setText(arrSort.get(i).getName());
          break;
        }
      }
      sortAdapter.notifyDataSetChanged();
    }
  }

  private void showMap() {
    if (vsSort != null) {
      vsSort.setVisibility(View.GONE);
    }
    if (vsCategory != null) {
      vsCategory.setVisibility(View.GONE);
    }


    if (vsLocation == null) {
      vsLocation = (ViewStub) findViewById(R.id.vsLocation);
      View inflateLocation = vsLocation.inflate();

      ivSearchLocation = (ImageView) inflateLocation.findViewById(R.id.ivSearch);
      etSearchLocation = (EditText) inflateLocation.findViewById(R.id.etSearchLocation);
      tvMapLocation = (TextView) inflateLocation.findViewById(R.id.tvMapLocation);
      tvMapRadius = (TextView) inflateLocation.findViewById(R.id.tvMapRadius);
      rlBottom = (RelativeLayout) inflateLocation.findViewById(R.id.rlBottom);
      btnAcceptLocation = (FloatingActionButton) inflateLocation.findViewById(R.id.btnAcceptLocation);
      btnCurrentLocation = (FloatingActionButton) inflateLocation.findViewById(R.id.btnCurrentLocation);
      btnAcceptLocation.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (tempLocationObj != null) {
            if (tempLocationObj != applyLocationObj || tempCircleRadius != circleRadius) {
              applyLocationObj = tempLocationObj;
              preferences.saveBrowseLocation(applyLocationObj);
              circleRadius = tempCircleRadius;
              preferences.putIntValue(SharedPreferencesManager.BROWSE_RADIUS, circleRadius);
              startBrowse();
            }
          }
          panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
      });

      btnCurrentLocation.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (mLastLocation != null)
            getLocationFromGeocode(false, mLastLocation.getLatitude(), mLastLocation.getLongitude());
        }
      });

      lvLocationSearch = (ListView) inflateLocation.findViewById(R.id.lvSearchLocation);
      arrSearchLocations = new ArrayList<>();
      locationAdapter = new LocationSearchAdapter(self, arrSearchLocations);
      lvLocationSearch.setAdapter(locationAdapter);

      lvLocationSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          hideSoftKeyboard(lvLocationSearch);
          lvLocationSearch.setVisibility(View.GONE);
          tempLocationObj = arrSearchLocations.get(i);
          showLocationOnMap(tempLocationObj);
        }
      });

      etSearchLocation.addTextChangedListener(new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
          if (etSearchLocation.getText().toString().trim().length() >= 3) {
            getLocationByAddress(etSearchLocation.getText().toString().trim());
          } else {
            arrSearchLocations.clear();
            locationAdapter.notifyDataSetChanged();
          }
        }
      });

      rangeBar = (RangeBar) inflateLocation.findViewById(R.id.rangebar);
      rangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
        @Override
        public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                          int rightPinIndex,
                                          String leftPinValue, String rightPinValue) {
          try {
            tempCircleRadius = Integer.parseInt(rightPinValue);
          } catch (Exception e) {
            e.printStackTrace();
            tempCircleRadius = 0;
          }
          showLocationOnMap(tempLocationObj);
//                    Log.i("PIN_VALUE", rightPinValue);
        }
      });

      rangeBar.setBarColor(getResources().getColor(R.color.app_primary_color));
      rangeBar.setPinColor(getResources().getColor(R.color.app_primary_color));
      rangeBar.setTickColor(getResources().getColor(R.color.app_primary_color));
      rangeBar.setSelectorColor(getResources().getColor(R.color.app_primary_color));
      rangeBar.setConnectingLineColor(getResources().getColor(R.color.app_primary_color));

      rangeMap.put(1f, "1");
      rangeMap.put(2f, "2");
      rangeMap.put(3f, "5");
      rangeMap.put(4f, "10");
      rangeMap.put(5f, "25");
      rangeMap.put(6f, "50");
      rangeMap.put(7f, "100");
      rangeMap.put(8f, "200");
      rangeMap.put(9f, "500");
      rangeMap.put(10f, "1000");

      rangeBar.setTickMap(rangeMap);
//            Log.i("CIRCLE_RADIUS", circleRadius+"");

      if (circleRadius != 0) {
        rangeBar.setSeekPinByIndex(AppUtil.getKeyByValue(rangeMap, circleRadius + "").intValue() - 1);
      } else {
        rangeBar.setSeekPinByIndex(rangeMap.size() - 1);
      }


      ActivityKeyboardObserver.assistActivity(self, new ActivityKeyboardObserver.IToggleKeyboard() {
        @Override
        public void onShowing() {
          rlBottom.setVisibility(View.INVISIBLE);
          tvMapLocation.setVisibility(View.GONE);
          btnCurrentLocation.setVisibility(View.GONE);
          btnAcceptLocation.setVisibility(View.GONE);
          lvLocationSearch.setVisibility(View.VISIBLE);
        }

        @Override
        public void onHidden() {
          rlBottom.setVisibility(View.VISIBLE);
          tvMapLocation.setVisibility(View.VISIBLE);
          btnCurrentLocation.setVisibility(View.VISIBLE);
          btnAcceptLocation.setVisibility(View.VISIBLE);
          lvLocationSearch.setVisibility(View.GONE);
        }
      });

    } else {
      vsLocation.setVisibility(View.VISIBLE);
      tempLocationObj = applyLocationObj;
      tempCircleRadius = circleRadius;
    }
//        initializeMap();
  }

  private void initializeMap() {
    try {
      if (maps == null) {
        maps = ((SupportMapFragment) getSupportFragmentManager()
            .findFragmentById(R.id.maps)).getMap();
        maps.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

          @Override
          public void onMapClick(final LatLng point) {
            // TODO Auto-generated method stub
            maps.clear();
            if (tempLocationObj != null) {
              maps.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude))).setTitle(
                  tempLocationObj.getLocationAddress());
            }
            getLocationFromGeocode(false, point.latitude, point.longitude);
          }
        });
        maps.setMyLocationEnabled(true);
      }


      showLocationOnMap(applyLocationObj);
      if (circleRadius != 0) {
        rangeBar.setSeekPinByIndex(AppUtil.getKeyByValue(rangeMap, circleRadius + "").intValue() - 1);
      } else {
        rangeBar.setSeekPinByIndex(rangeMap.size() - 1);
      }

      if (applyLocationObj != null) {
        tvMapLocation.setText(applyLocationObj.getLocationAddress());
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    }
  }

  private void getLocationFromGeocode(final boolean applyForItem, final double latitude, final double longitude) {

    String getUrl = ParameterFactory.createGeocodeSearchCoordination(latitude, longitude);
    AsyncHttpGet asyncHttpGet = new AsyncHttpGet(self, new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        parseNotStandardResponse(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<LocationObj> list = CommonParser.parseLocations(json);
            if (list.size() > 0) {
              tempLocationObj = CommonParser.parseLocations(json).get(0);
            } else {
              tempLocationObj = new LocationObj();
              tempLocationObj.setAddress(getString(R.string.unknown_place));
            }

            tempLocationObj.setLatitude(latitude);
            tempLocationObj.setLongitude(longitude);

            if (applyForItem && tempLocationObj != null) {
              applyLocationObj = tempLocationObj;
              preferences.saveBrowseLocation(applyLocationObj);
            }

            showLocationOnMap(tempLocationObj);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });

      }
    }, null);
    asyncHttpGet.execute(getUrl);
  }

  private void getLocationByAddress(final String address) {
    String getUrl = ParameterFactory.createAddressSearchCoordination(address);

    AsyncHttpGet asyncGet = new AsyncHttpGet(self, new AsyncHttpResponse() {
      @Override
      public void before() {

      }

      @Override
      public void after(int statusCode, String response) {
        parseNotStandardResponse(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<LocationObj> list = CommonParser.parseLocations(json);
            if (etSearchLocation.getText().toString().trim().contains(address) || address.contains(etSearchLocation.getText().toString().trim())) {
              arrSearchLocations.clear();
              arrSearchLocations.addAll(list);
              locationAdapter.notifyDataSetChanged();
            }
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });

      }
    }, null);

    asyncGet.execute(getUrl);
  }

  private void showLocationOnMap(LocationObj obj) {

    if (maps != null && obj != null) {
      maps.clear();
      LatLng latLng = new LatLng(obj.getLatitude(),
          obj.getLongitude());

      maps.addMarker(new MarkerOptions().position(latLng)).setTitle(
          obj.getLocationAddress());
      LatLngBounds bounds = AppUtil.boundsWithCenterAndLatLngDistance(latLng, (float) tempCircleRadius * 1000 * 2, (float) tempCircleRadius * 1000 * 2);
      maps.addCircle(new CircleOptions().center(latLng).radius(tempCircleRadius * 1000).strokeColor(getResources().getColor(R.color.map_circle_color)).fillColor(getResources().getColor(R.color.map_circle_color)));

      maps.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 20), 1000, new GoogleMap.CancelableCallback() {
        @Override
        public void onFinish() {
        }

        @Override
        public void onCancel() {

        }
      });


      tvMapLocation.setText(obj.getLocationAddress());
      tvMapRadius.setText(tempCircleRadius != 0 ? tempCircleRadius + " " + (tempCircleRadius == 1 ? getString(R.string.kilometer) : getString(R.string.kilometers)) + " " + getString(R.string.around) : getString(R.string.anywhere));
    }
  }

  private void toggleWishList(int position) {

    final Item item = arrItems.get(position);


    List<NameValuePair> param = ParameterFactory.createToggleWishListParams(item.getId());

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, preferences.getUserInfo().getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
//                DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
//                DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            if (item.isLiked()) {
              item.setLiked(false);
              item.setLikeCount(item.getLikeCount() - 1);
            } else {
              item.setLiked(true);
              item.setLikeCount(item.getLikeCount() + 1);
            }
            itemAdapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, param);
    asyncPost.execute(WebServiceConfig.URL_TOGGLE_WISH_LIST);
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
