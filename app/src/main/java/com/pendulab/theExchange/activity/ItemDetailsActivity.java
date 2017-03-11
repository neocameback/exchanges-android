package com.pendulab.theExchange.activity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.github.ksoichiro.android.observablescrollview.ScrollUtils;
import com.nineoldandroids.view.ViewHelper;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.CategoryFilterAdapter;
import com.pendulab.theExchange.adapter.CommentAdapter;
import com.pendulab.theExchange.adapter.LikerAdapter;
import com.pendulab.theExchange.base.BaseLocationActivity;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.fragment.ImagesFragment;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.model.Comment;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.LocationObj;
import com.pendulab.theExchange.model.Offer;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.net.NetBase;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;
import com.pendulab.theExchange.widget.CirclePageIndicator;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.apache.http.NameValuePair;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import in.flashbulb.coloredratingbar.ColoredRatingBar;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;

import static com.pendulab.theExchange.R.string.offer;

/**
 * Created by Anh Ha Nguyen on 10/19/2015.
 */
public class ItemDetailsActivity extends BaseLocationActivity implements View.OnClickListener, ObservableScrollViewCallbacks {

  private SwipeRefreshLayout swipeRefreshLayout;
  private LinearLayout llContent, llBottom;
  private LocationObj applyLocationObj;
  private SlidingUpPanelLayout panelLayout;

  private View anchorView;
  private ViewPager pager, pagerFullScreen;
  private CirclePageIndicator indicator, indicatorFullScreen;
  private CustomPagerAdapter pagerAdapter;
  private ViewStub vsFullScreen;
  private boolean touchStarted = false;
  private boolean isFullScreen = false;

  private View mToolbarView;
  private ObservableScrollView mScrollView;
  private int mParallaxImageHeight;

  private Item currentItem;
  private int currentPosition = -1;
  private String itemID;
  private TextView tvLikeCount, tvCommentCount, tvLike, tvDescription, tvPrice, tvDistance, tvUsername, tvRateSeller, tvLocation, tvCategory, tvUsername2, tvPostComment, tvNoRating;
  private ColoredRatingBar rbRating;
  private ImageView ivLikeCount, ivCommentCount, ivLike, ivShareFb, ivShareGG, ivShareTT, ivShareEmail, ivShareMore, ivUserAvatar;

  private final int PANEL_LIKE = 1, PANEL_COMMENT = 2, PANEL_REPORT = 3;

  private ViewStub vsLike;
  private SwipeRefreshLayout srlLike;
  private ListView lvLike;
  private List<Account> arrLiker;
  private LikerAdapter likerAdapter;

  private ViewStub vsComment;
  private SwipeRefreshLayout srlComment;
  private ListView lvComment;
  private List<Comment> arrComments;
  private CommentAdapter commentAdapter;
  private EditText etComment;
  private ImageButton btnSendComment;
  private boolean isPostingComment;

  private ViewStub vsReport;
  private ListView lvReport;
  private List<Category> arrReport;
  private CategoryFilterAdapter reportAdapter;
  private TextView tvApply;

  private LinearLayout llChat, llEdit, llOffer, llViewOffer, llVerifyTransaction, llVerificationPending, llGone;

  private final int RC_EDIT_ITEM = 1003, RC_VIEW_OFER = 1004, RC_OFFER = 1005, RC_VERIFY_TRANSACTION = 1006;

  private String conversationID;

  private MaterialShowcaseSequence sequence = null;
  private final String SHOWCASE_ID_OWNER = "ITEM_DETAILS_SHOWCASE_OWNER";
  private final String SHOWCASE_ID_GUEST = "ITEM_DETAILS_SHOWCASE_GUEST";

  @Override
  public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_item_details);

    setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setHomeButtonEnabled(true);
    getSupportActionBar().setElevation(0);
    getSupportActionBar().setTitle("");

    applyLocationObj = preferences.getBrowseLocation();

    initUI();

    Bundle bundle = getIntent().getExtras();
    if (bundle != null && bundle.containsKey(GlobalValue.KEY_ITEM)) {
      itemID = bundle.getString(GlobalValue.KEY_ITEM);
      if (bundle.containsKey(GlobalValue.KEY_POSITION)) {
        currentPosition = bundle.getInt(GlobalValue.KEY_POSITION);
      }
      if (bundle.containsKey(GlobalValue.KEY_VIEW_LIKER)) {
        slideUpPanel(PANEL_LIKE);
      }
      if (bundle.containsKey(GlobalValue.KEY_VIEW_COMMENT)) {
        slideUpPanel(PANEL_COMMENT);
      }
    }

    initData();
    initControl();
  }

  private void initUI() {
    panelLayout = (SlidingUpPanelLayout) findViewById(R.id.panelLayout);
    swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.app_primary_color));
    pager = (ViewPager) findViewById(R.id.pager);

    indicator = (CirclePageIndicator) findViewById(R.id.indicator);
//        indicator.setFillColor(getResources().getColor(R.color.app_primary_color));
//        indicator.setPageColor(getResources().getColor(R.color.white));
//        indicator.setStrokeColor(getResources().getColor(R.color.item_border));

    initScrollView();

    llContent = (LinearLayout) findViewById(R.id.llContent);
    llContent.setVisibility(View.INVISIBLE);
    llBottom = (LinearLayout) findViewById(R.id.llBottom);
    llBottom.setVisibility(View.INVISIBLE);
    tvLikeCount = (TextView) findViewById(R.id.tvLikeCount);
    tvCommentCount = (TextView) findViewById(R.id.tvCommentCount);
    tvLike = (TextView) findViewById(R.id.tvLike);
    tvDescription = (TextView) findViewById(R.id.tvDescription);
    tvPrice = (TextView) findViewById(R.id.tvPrice);
    tvDistance = (TextView) findViewById(R.id.tvDistance);
    tvUsername = (TextView) findViewById(R.id.tvUsername);
    tvUsername2 = (TextView) findViewById(R.id.tvUsername2);
    tvRateSeller = (TextView) findViewById(R.id.tvRateSeller);
    tvCategory = (TextView) findViewById(R.id.tvCategory);
    tvLocation = (TextView) findViewById(R.id.tvLocation);
    tvPostComment = (TextView) findViewById(R.id.tvPostComment);
    tvNoRating = (TextView) findViewById(R.id.tvNoRating);
    rbRating = (ColoredRatingBar) findViewById(R.id.rbRating);
    rbRating.setRating(4.5f);
    rbRating.setIndicator(true);
    ivUserAvatar = (ImageView) findViewById(R.id.ivUserAvatar);

    ivLikeCount = (ImageView) findViewById(R.id.ivLikeCount);
    ivCommentCount = (ImageView) findViewById(R.id.ivCommentCount);
    ivLike = (ImageView) findViewById(R.id.ivLike);
    ivShareFb = (ImageView) findViewById(R.id.ivShareFacebook);
    ivShareGG = (ImageView) findViewById(R.id.ivShareGoogle);
    ivShareTT = (ImageView) findViewById(R.id.ivShareTwitter);
    ivShareEmail = (ImageView) findViewById(R.id.ivShareEmail);
    ivShareMore = (ImageView) findViewById(R.id.ivShareMore);

    llEdit = (LinearLayout) findViewById(R.id.llEdit);
    llOffer = (LinearLayout) findViewById(R.id.llOffer);
    llChat = (LinearLayout) findViewById(R.id.llChat);
    llViewOffer = (LinearLayout) findViewById(R.id.llViewOffer);
    llVerifyTransaction = (LinearLayout) findViewById(R.id.llVerifyTransaction);
    llGone = (LinearLayout) findViewById(R.id.llGone);
    llVerificationPending = (LinearLayout) findViewById(R.id.llVerifyPending);

    Glide.with(self).load(R.drawable.ic_credit_red).into((ImageView) findViewById(R.id.ivPrice));
    Glide.with(self).load(R.drawable.ic_distance).into((ImageView) findViewById(R.id.ivDistance));
    Glide.with(self).load(R.drawable.ic_price_red).into((ImageView) findViewById(R.id.ivCategory));
    Glide.with(self).load(R.drawable.ic_map_red).into((ImageView) findViewById(R.id.ivLocation));
    Glide.with(self).load(R.drawable.ic_user_red).into((ImageView) findViewById(R.id.ivUser));

    Glide.with(self).load(R.drawable.ic_product_share_fb).into(ivShareFb);
    Glide.with(self).load(R.drawable.ic_product_share_gplus).into(ivShareGG);
    Glide.with(self).load(R.drawable.ic_product_share_tw).into(ivShareTT);
    Glide.with(self).load(R.drawable.ic_product_share_email).into(ivShareEmail);
    Glide.with(self).load(R.drawable.ic_product_share_link).into(ivShareMore);

  }

  private void initScrollView() {
    mToolbarView = findViewById(R.id.toolbar);
    int baseColor = getResources().getColor(R.color.app_primary_color);
    mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(0.15f, baseColor));

    mScrollView = (ObservableScrollView) findViewById(R.id.scroll);
    mScrollView.setScrollViewCallbacks(this);

    mParallaxImageHeight = AppUtil.getDeviceScreenWidth(self);

    anchorView = findViewById(R.id.anchor);

    RelativeLayout.LayoutParams lpPager = (RelativeLayout.LayoutParams) pager.getLayoutParams();
    lpPager.height = mParallaxImageHeight;
    pager.setLayoutParams(lpPager);

    RelativeLayout.LayoutParams lpAnchor = (RelativeLayout.LayoutParams) anchorView.getLayoutParams();
    lpAnchor.height = mParallaxImageHeight;
    anchorView.setLayoutParams(lpAnchor);
  }

  private void initData() {
    getItemDetails();
  }

  private void initControl() {
    rbRating.setOnRatingBarChangeListener(new ColoredRatingBar.OnRatingBarChangeListener() {
      @Override
      public void onRatingChanged(ColoredRatingBar ratingBar, float rating, boolean fromUser) {

      }
    });

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        getItemDetails();
      }
    });

    pager.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent event) {
//                view.getParent().requestDisallowInterceptTouchEvent(true);
//                swipeRefreshLayout.setEnabled(false);
//                return false;
        switch (event.getAction()) {
          case MotionEvent.ACTION_MOVE:
            swipeRefreshLayout.setEnabled(false);
            view.getParent().requestDisallowInterceptTouchEvent(true);
            touchStarted = false;
            break;
          case MotionEvent.ACTION_UP:
            swipeRefreshLayout.setEnabled(true);
            view.getParent().requestDisallowInterceptTouchEvent(false);
            if (touchStarted) {
              showFullScreenImage();
            }
            break;
          case MotionEvent.ACTION_CANCEL:
            swipeRefreshLayout.setEnabled(true);
            view.getParent().requestDisallowInterceptTouchEvent(false);
            break;
          case MotionEvent.ACTION_DOWN:
            swipeRefreshLayout.setEnabled(true);
            view.getParent().requestDisallowInterceptTouchEvent(false);
            touchStarted = true;
            break;
        }
        return false;
      }
    });

    pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
//                pager.getParent().requestDisallowInterceptTouchEvent(true);
        swipeRefreshLayout.setEnabled(true);
      }

      @Override
      public void onPageSelected(int position) {

      }

      @Override
      public void onPageScrollStateChanged(int state) {
//                if (state == ViewPager.SCROLL_STATE_IDLE) {
//                    swipeRefreshLayout.setEnabled(true);
//                } else {
//                    swipeRefreshLayout.setEnabled(false);
//                    pager.getParent().requestDisallowInterceptTouchEvent(true);
//                }
      }
    });

    getParentView(ivLikeCount).setOnClickListener(this);
    getParentView(ivCommentCount).setOnClickListener(this);
    getParentView(ivLike).setOnClickListener(this);
    tvUsername.setOnClickListener(this);
    tvLocation.setOnClickListener(this);
    tvPostComment.setOnClickListener(this);
    ivShareFb.setOnClickListener(this);
    ivShareGG.setOnClickListener(this);
    ivShareTT.setOnClickListener(this);
    ivShareEmail.setOnClickListener(this);
    ivShareMore.setOnClickListener(this);
    tvUsername2.setOnClickListener(this);
    ivUserAvatar.setOnClickListener(this);
    tvRateSeller.setOnClickListener(this);

    llEdit.setOnClickListener(this);
    llChat.setOnClickListener(this);
    llOffer.setOnClickListener(this);
    llViewOffer.setOnClickListener(this);
    llVerifyTransaction.setOnClickListener(this);
    llVerificationPending.setOnClickListener(this);
    llGone.setOnClickListener(this);
  }

//    private void initShowcase(){
//        ShowcaseConfig config = new ShowcaseConfig();
//        config.setDelay(200); // half second between each showcase view
//
//        if(currentItem != null) {
//            if(currentItem.getOwnerId().equalsIgnoreCase(myAccount.getUserID())) {
//                sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID_OWNER);
//                sequence.addSequenceItem(llEdit, getString(R.string.tut_edit_delete_item), getString(R.string.tut_next));
//                sequence.addSequenceItem(llViewOffer, getString(R.string.tut_view_offer), getString(R.string.tut_got_it));
//            }else{
//                sequence = new MaterialShowcaseSequence(this, SHOWCASE_ID_GUEST);
//                sequence.addSequenceItem(tvUsername, getString(R.string.tut_see_user_details), getString(R.string.tut_next));
//                sequence.addSequenceItem(llChat, getString(R.string.tut_contact_seller), getString(R.string.tut_next));
//                sequence.addSequenceItem(llOffer, getString(R.string.tut_make_an_offer), getString(R.string.tut_got_it));
//            }
//
//            sequence.start();
//        }
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
//                    updateTutorial(GlobalValue.TUTORIAL_ITEM_DETAILS);
//                }
//            }
//        });
//
//        sequence.setConfig(config);
//    }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (resultCode == RESULT_OK) {
      if (requestCode == RC_EDIT_ITEM) {
        getItemDetails();
      }
      if (requestCode == RC_VIEW_OFER) {
        Item newItem = data.getParcelableExtra(GlobalValue.KEY_ITEM);
        currentItem.setStatus(newItem.getStatus());
        fillLayout();
      }
      if (requestCode == RC_VERIFY_TRANSACTION) {
        Item newItem = data.getParcelableExtra(GlobalValue.KEY_ITEM);
        currentItem.setStatus(newItem.getStatus());
        currentItem.setVerificationStatus(newItem.getVerificationStatus());
        fillLayout();
      }
      if (requestCode == RC_OFFER) {
        currentItem.setOffered(true);
        fillLayout();
      }
    }
  }

  @Override
  public void onClick(View view) {
    if (view == tvLocation) {
      openMap(currentItem.getLocation(), currentItem.getLatitude() + "", currentItem.getLongitude() + "");
      return;
    }
    if (view == getParentView(ivLike)) {
      toggleWishList(currentItem.getId());
      return;
    }

    if (view == getParentView(ivLikeCount)) {
      slideUpPanel(PANEL_LIKE);
      return;
    }

    if (view == getParentView(ivCommentCount)) {
      slideUpPanel(PANEL_COMMENT);
      return;
    }

    if (view == ivShareFb) {
      onShareFacebook(view, currentItem);
      return;
    }

    if (view == ivShareGG) {
      onShareGoogle(view, currentItem);
      return;
    }

    if (view == ivShareTT) {
      onShareTwitter(view, currentItem);
    }

    if (view == ivShareEmail) {
      onShareEmail(view, currentItem);
      return;
    }
    if (view == ivShareMore) {
      onShareLink(view, currentItem);
      return;
    }

    if (view == tvRateSeller) {
      onRateSeller();
      return;
    }
    if (view == tvPostComment) {
      slideUpPanel(PANEL_COMMENT);
      return;
    }
    if (view == tvUsername || view == tvUsername2 || view == ivUserAvatar) {
      gotoUserProfileActivity(currentItem.getOwnerId(), currentItem.getOwnerUsername());
      return;
    }
    if (view == llEdit) {
      DialogUtility.showOptionDialog(self, getString(R.string.edit_delete_options), getString(R.string.delete), getString(R.string.edit), true, new DialogUtility.DialogOptionListener() {
        @Override
        public void onPositive(Dialog dialog) {
          DialogUtility.showOptionDialog(self, getString(R.string.ask_delete_item), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
            @Override
            public void onPositive(Dialog dialog) {
              onDeleteItem();
            }

            @Override
            public void onNegative(Dialog dialog) {

            }
          });
        }

        @Override
        public void onNegative(Dialog dialog) {
          onEditItem();

        }
      });
      return;
    }
    if (view == llChat) {

      getConversation();
      return;
    }

    if (view == llOffer) {
      Bundle bundle = new Bundle();
      bundle.putParcelable(GlobalValue.KEY_ITEM, currentItem);
      gotoActivityForResult(self, OfferActivity.class, bundle, RC_OFFER);
      return;
    }

    if (view == llViewOffer) {
      if (currentItem.getOwnerId().equalsIgnoreCase(myAccount.getUserID())) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(GlobalValue.KEY_ITEM, currentItem);
        gotoActivityForResult(self, OfferListActivity.class, bundle, RC_VIEW_OFER);
      } else {
        getConversation();
      }
      return;
    }

    if (view == llVerifyTransaction) {
      DialogUtility.showOptionDialog(self, getString(R.string.request_verify_message), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
        @Override
        public void onPositive(Dialog dialog) {
          requestVerify();
        }

        @Override
        public void onNegative(Dialog dialog) {

        }
      });
      return;
    }
    if (view == llVerificationPending) {
      DialogUtility.showMessageDialog(self, getString(R.string.pending_verify_message), null);
      return;
    }
    if (view == llGone) {
      getConversation();
      return;
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_activity_item_details, menu);
    return super.onCreateOptionsMenu(menu);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if (currentItem != null) {
      if (currentItem.isReported()) {
        menu.getItem(0).getSubMenu().getItem(0).setVisible(false);
        menu.getItem(0).getSubMenu().getItem(1).setVisible(true);
      } else {
        menu.getItem(0).getSubMenu().getItem(0).setVisible(true);
        menu.getItem(0).getSubMenu().getItem(1).setVisible(false);
      }
    }
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.action_report) {
      slideUpPanel(PANEL_REPORT);
      return true;
    }
    if (item.getItemId() == R.id.action_unreport) {
      reportItem();
      return false;
    }
    return super.onOptionsItemSelected(item);
  }

  private void showPopupMenu() {
//        PopupMenu menu = new PopupMenu(this, );
  }

  @Override
  public void onBackPressed() {
    if (panelLayout != null && panelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED) {
      panelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    } else if (isFullScreen) {
      hideFullScreenLayout();
    } else {
      if (currentItem != null) {
        Intent intent = new Intent();
        intent.putExtra(GlobalValue.KEY_ITEM, currentItem);
        intent.putExtra(GlobalValue.KEY_POSITION, currentPosition);
        setResult(RESULT_OK, intent);
      }
      finish();
    }
  }

  @Override
  public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    int baseColor = getResources().getColor(R.color.app_primary_color);
    float alpha = Math.min(1, (float) scrollY / mParallaxImageHeight);
    if (alpha < 0.15f) alpha = 0.15f;
    mToolbarView.setBackgroundColor(ScrollUtils.getColorWithAlpha(alpha, baseColor));
    ViewHelper.setTranslationY(pager, scrollY / 2);
  }

  @Override
  public void onDownMotionEvent() {
  }

  @Override
  public void onUpOrCancelMotionEvent(ScrollState scrollState) {
  }


  @Override
  public void onObtainLocation() {
    Log.i(this.getClass().getSimpleName(), "On Obtain Location");
    if (currentItem == null) {
      if (applyLocationObj == null && mLastLocation != null) {
        applyLocationObj = new LocationObj();
        applyLocationObj.setLatitude(mLastLocation.getLatitude());
        applyLocationObj.setLongitude(mLastLocation.getLongitude());
      }
      getItemDetails();
    }
  }

  @Override
  public void onChangeLocation() {
  }

  @Override
  public void onLocationUnavailable() {

    if (currentItem == null) {
//            if (applyLocationObj != null) {
      getItemDetails();
//            }
//            else {
//                showLocationAlertDialog();
//            }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
  }

  private void getItemDetails() {
    List<NameValuePair> params = ParameterFactory.createGetItemDetailsParams(itemID, applyLocationObj != null ? applyLocationObj.getLatitude() : 0, applyLocationObj != null ? applyLocationObj.getLongitude() : 0);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, preferences.getUserInfo().getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        showRefreshLayout(swipeRefreshLayout);
      }

      @Override
      public void after(int statusCode, String response) {
        hideRefreshLayout(swipeRefreshLayout);

        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            currentItem = CommonParser.parseSingleItemFromJson(self, json);
            if (currentItem != null) {
              fillLayout();
            } else {
              DialogUtility.showMessageDialog(self, getString(R.string.message_server_error), null);
            }
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_GET_ITEM_DETAILS);
  }

  private void fillLayout() {
    setActionBarTitle(currentItem.getTitle());
    tvDescription.setText(currentItem.getDescription().equals("") ? getString(R.string.no_description_available) : currentItem.getDescription());
    tvPrice.setText(currentItem.getPrice() + "");
    tvUsername.setText(currentItem.getOwnerUsername());
    tvCategory.setText(currentItem.getCategoryId());
    if (currentItem.getDistance() > 0) {
      if (currentItem.getDistance() > 1) {
        tvDistance.setText((int) currentItem.getDistance() + getString(R.string.km) + " " + getString(R.string.away));
      } else {
        tvDistance.setText((int) (currentItem.getDistance() * 1000) + getString(R.string.m) + " " + getString(R.string.away));
      }
    } else {
      tvDistance.setText(getString(R.string.not_available));
    }
    tvLocation.setText(currentItem.getLocation());

    Glide.with(self).load(currentItem.getOwnerImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(ivUserAvatar);
    tvUsername2.setText(currentItem.getOwnerUsername());
    if (currentItem.getRate() == 0d) {
      rbRating.setVisibility(View.GONE);
      tvNoRating.setVisibility(View.VISIBLE);
    } else {
      rbRating.setVisibility(View.VISIBLE);
      rbRating.setRating((float) currentItem.getRate());
      tvNoRating.setVisibility(View.GONE);
    }


    Glide.with(self).load(R.drawable.ic_btn_like_white).into(ivLikeCount);
    Glide.with(self).load(R.drawable.ic_comment_white).into(ivCommentCount);
    tvLikeCount.setText(currentItem.getLikeCount() + "");
    tvCommentCount.setText(currentItem.getCommnetCount() + "");

    Glide.with(self).load(currentItem.isLiked() ? R.drawable.ic_btn_like_red : R.drawable.ic_like).into(ivLike);
    tvLike.setText(currentItem.isLiked() ? getString(R.string.remove_from_wish_list) : getString(R.string.add_to_wish_list));

    pagerAdapter = new CustomPagerAdapter(self);
    pager.setAdapter(pagerAdapter);
    indicator.setViewPager(pager);

    llBottom.setVisibility(View.VISIBLE);
    llContent.setVisibility(View.VISIBLE);
    invalidateOptionsMenu();

    llChat.setVisibility(View.GONE);
    llOffer.setVisibility(View.GONE);
    llViewOffer.setVisibility(View.GONE);
    llEdit.setVisibility(View.GONE);
    llVerifyTransaction.setVisibility(View.GONE);
    llGone.setVisibility(View.GONE);
    llVerificationPending.setVisibility(View.GONE);
    tvRateSeller.setVisibility(View.GONE);

    if (currentItem.getOwnerId().equalsIgnoreCase(myAccount.getUserID())) {
      if (currentItem.getStatus() == Item.STATUS_AVAILABLE) {
        llEdit.setVisibility(View.VISIBLE);
        llViewOffer.setVisibility(View.VISIBLE);
        Glide.with(self).load(R.drawable.ic_action_edit).into((ImageView) findViewById(R.id.ivEdit));
        Glide.with(self).load(R.drawable.ic_btn_view_offers).into((ImageView) findViewById(R.id.ivViewOffer));
      }
      if (currentItem.getStatus() == Item.STATUS_OFFER_ACCEPTED) {
        if (currentItem.getVerificationStatus() == Item.VERIFY_NONE || currentItem.getVerificationStatus() == Item.VERIFY_BUY) {
          llVerifyTransaction.setVisibility(View.VISIBLE);
          Glide.with(self).load(R.drawable.icon_verify).into((ImageView) findViewById(R.id.ivVerifyTransaction));
        } else {
          llVerificationPending.setVisibility(View.VISIBLE);
        }
      }
      if (currentItem.getStatus() == Item.STATUS_SOLD) {
        llGone.setVisibility(View.VISIBLE);
        Glide.with(self).load(R.drawable.ic_btn_chat_white).into((ImageView) findViewById(R.id.ivGone));
      }

    } else {
      Glide.with(self).load(R.drawable.ic_btn_chat_white).into((ImageView) findViewById(R.id.ivChat));
      if (currentItem.getStatus() == Item.STATUS_AVAILABLE) {
        llChat.setVisibility(View.VISIBLE);
        if (!currentItem.isOffered()) {
          llOffer.setVisibility(View.VISIBLE);
          Glide.with(self).load(R.drawable.ic_btn_offer).into((ImageView) findViewById(R.id.ivOffer));
        } else {
          llViewOffer.setVisibility(View.VISIBLE);
          Glide.with(self).load(R.drawable.ic_btn_view_offers).into((ImageView) findViewById(R.id.ivViewOffer));
        }
      }
      if (currentItem.getStatus() == Item.STATUS_OFFER_ACCEPTED) {
        llChat.setVisibility(View.VISIBLE);
        if (currentItem.getVerificationStatus() == Item.VERIFY_NONE || currentItem.getVerificationStatus() == Item.VERIFY_SELL) {
          llVerifyTransaction.setVisibility(View.VISIBLE);
          Glide.with(self).load(R.drawable.icon_verify).into((ImageView) findViewById(R.id.ivVerifyTransaction));
        } else {
          llVerificationPending.setVisibility(View.VISIBLE);
        }
      }

      if (currentItem.getStatus() == Item.STATUS_SOLD) {
        llGone.setVisibility(View.VISIBLE);
        Glide.with(self).load(R.drawable.ic_btn_chat_white).into((ImageView) findViewById(R.id.ivGone));
      }
    }

//        initShowcase();
  }

  private void showFullScreenImage() {
    if (vsFullScreen == null) {
      vsFullScreen = (ViewStub) findViewById(R.id.vsFullScreen);
      View inflateFs = vsFullScreen.inflate();
      pagerFullScreen = (ViewPager) inflateFs.findViewById(R.id.pagerFullScreen);
      indicatorFullScreen = (CirclePageIndicator) inflateFs.findViewById(R.id.indicatorFullScreen);
      RelativeLayout rlFullScreen = (RelativeLayout) inflateFs.findViewById(R.id.rlFullScreen);

      RelativeLayout.LayoutParams lpFS = (RelativeLayout.LayoutParams) rlFullScreen.getLayoutParams();
      lpFS.height = mParallaxImageHeight;
      rlFullScreen.setLayoutParams(lpFS);

      RelativeLayout.LayoutParams lpPager = (RelativeLayout.LayoutParams) pagerFullScreen.getLayoutParams();
      lpPager.height = ViewGroup.LayoutParams.MATCH_PARENT;
      pagerFullScreen.setLayoutParams(lpPager);


      pagerFullScreen.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {

        }
      });

      inflateFs.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          pager.setCurrentItem(pagerFullScreen.getCurrentItem());
          hideFullScreenLayout();
        }
      });
    }

    pagerFullScreen.setAdapter(pagerAdapter);
    indicatorFullScreen.setViewPager(pagerFullScreen);
    pagerFullScreen.setCurrentItem(pager.getCurrentItem());
    showFullScreenLayout();

  }

  private void showFullScreenLayout() {
    vsFullScreen.setVisibility(View.VISIBLE);
    isFullScreen = true;
  }

  private void hideFullScreenLayout() {
    vsFullScreen.setVisibility(View.GONE);
    isFullScreen = false;
  }

  class ImageFragmentAdapter extends FragmentPagerAdapter {

    public ImageFragmentAdapter(FragmentManager fm) {
      super(fm);
    }

    public int getItemPosition(Object object) {
      return POSITION_NONE;
    }

    @Override
    public Fragment getItem(int position) {

      return ImagesFragment.newInstance(self, currentItem.getArrImages().get(position));

    }

    @Override
    public CharSequence getPageTitle(int position) {
      return "";
    }

    @Override
    public int getCount() {
      return (currentItem.getArrImages().size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      super.destroyItem(container, position, object);
    }
  }

  class CustomPagerAdapter extends PagerAdapter {

    Context mContext;
    LayoutInflater mLayoutInflater;

    public CustomPagerAdapter(Context context) {
      mContext = context;
      mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
      return currentItem == null ? 0 : currentItem.getArrImages().size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
      return view == ((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
      return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
      View itemView = mLayoutInflater.inflate(R.layout.layout_image, container, false);

      ImageView img = (ImageView) itemView.findViewById(R.id.image);

      Glide.with(self).load(currentItem.getArrImages().get(position)).error(R.drawable.image_not_available).diskCacheStrategy(DiskCacheStrategy.ALL).into(img);

      container.addView(itemView);

      return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }
  }

  private void openMap(String label, String latitude, String longitude) {
    String uriBegin = "geo:" + latitude + "," + longitude;
    String query = latitude + "," + longitude + "(" + label + ")";
    String encodedQuery = Uri.encode(query);
    String uriString = uriBegin + "?q=" + encodedQuery + "&z=16";
    Uri uri = Uri.parse(uriString);
    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, uri);
    startActivity(intent);
  }

  private void toggleWishList(String itemID) {

    List<NameValuePair> param = ParameterFactory.createToggleWishListParams(itemID);

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
            if (currentItem.isLiked()) {
              currentItem.setLiked(false);
              currentItem.setLikeCount(currentItem.getLikeCount() - 1);
            } else {
              currentItem.setLiked(true);
              currentItem.setLikeCount(currentItem.getLikeCount() + 1);
            }
            fillLayout();
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


  private void slideUpPanel(int type) {
    switch (type) {
      case PANEL_LIKE:
        showLikeSlider();
        break;
      case PANEL_COMMENT:
        showCommentSlider();
        break;
      case PANEL_REPORT:
        showReportSlider();
        break;
    }

    showPanel();
  }

  private void showLikeSlider() {
    if (vsReport != null) {
      vsReport.setVisibility(View.GONE);
    }
    if (vsComment != null) {
      vsComment.setVisibility(View.GONE);
    }
    if (vsLike == null) {
      vsLike = (ViewStub) findViewById(R.id.vsLike);
      View inflateLike = vsLike.inflate();
      Drawable drBack = getResources().getDrawable(com.mikepenz.materialdrawer.R.drawable.abc_ab_share_pack_mtrl_alpha);
      drBack.setColorFilter(getResources().getColor(R.color.app_primary_text_color), PorterDuff.Mode.MULTIPLY);
      ((ImageView) inflateLike.findViewById(R.id.ivBack)).setImageDrawable(drBack);
      srlLike = (SwipeRefreshLayout) inflateLike.findViewById(R.id.srlLike);
      srlLike.setColorSchemeColors(R.color.app_primary_color);
      lvLike = (ListView) inflateLike.findViewById(R.id.lvLike);
      arrLiker = new ArrayList<>();
      likerAdapter = new LikerAdapter(self, arrLiker);
      lvLike.setAdapter(likerAdapter);

      srlLike.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              arrLiker.clear();
              getLikers();
            }
          });
        }
      });

      lvLike.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          gotoUserProfileActivity(arrLiker.get(i).getUserID(), arrLiker.get(i).getUsername());
        }
      });

      getLikers();
    } else {

    }
    vsLike.setVisibility(View.VISIBLE);

  }

  private void getLikers() {
    List<NameValuePair> params = ParameterFactory.createGetLikersParams(itemID);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        showRefreshLayout(srlLike);
      }

      @Override
      public void after(int statusCode, String response) {
        hideRefreshLayout(srlLike);
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Account> list = CommonParser.parseListAccountFromJson(json);
            arrLiker.clear();
            arrLiker.addAll(list);
            likerAdapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_GET_ITEM_LIKERS);
  }

  private void showCommentSlider() {
    if (vsReport != null) {
      vsReport.setVisibility(View.GONE);
    }
    if (vsLike != null) {
      vsLike.setVisibility(View.GONE);
    }
    if (vsComment == null) {
      vsComment = (ViewStub) findViewById(R.id.vsComment);
      View inflateComment = vsComment.inflate();
      Drawable drBack = getResources().getDrawable(com.mikepenz.materialdrawer.R.drawable.abc_ab_share_pack_mtrl_alpha);
      drBack.setColorFilter(getResources().getColor(R.color.app_primary_text_color), PorterDuff.Mode.MULTIPLY);

      Drawable drSend = getResources().getDrawable(android.R.drawable.ic_menu_send);
      drSend.setColorFilter(getResources().getColor(R.color.app_primary_color), PorterDuff.Mode.MULTIPLY);

      ((ImageView) inflateComment.findViewById(R.id.ivBack)).setImageDrawable(drBack);
      srlComment = (SwipeRefreshLayout) inflateComment.findViewById(R.id.srlComment);
      srlComment.setColorSchemeColors(getResources().getColor(R.color.app_primary_color));
      lvComment = (ListView) inflateComment.findViewById(R.id.lvComment);
      arrComments = new ArrayList<>();
      commentAdapter = new CommentAdapter(self, arrComments, new CommentAdapter.ICommentAdapter() {
        @Override
        public void onClickUser(Comment comment) {
          gotoUserProfileActivity(comment.getUserId(), comment.getUsername());
        }

        @Override
        public void onClickTryAgain(Comment comment) {
          comment.setStatus(Comment.STATUS_POSTING);
          commentAdapter.notifyDataSetChanged();
          postComment(comment);
        }
      });
      lvComment.setAdapter(commentAdapter);

      etComment = (EditText) inflateComment.findViewById(R.id.etComment);
      btnSendComment = (ImageButton) inflateComment.findViewById(R.id.btnSend);

      srlComment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
          self.runOnUiThread(new Runnable() {
            @Override
            public void run() {
              arrComments.clear();
              getComments();
            }
          });
        }
      });

      btnSendComment.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if (etComment.getText().toString().trim().length() > 0) {
            Comment comment = new Comment();
            comment.setItemId(currentItem.getId());
            comment.setUserImage(myAccount.getImage());
            comment.setUserId(myAccount.getUserID());
            comment.setUsername(myAccount.getUsername());
            comment.setText(etComment.getText().toString().trim());
            comment.setDate(getString(R.string.just_now));
            comment.setStatus(Comment.STATUS_POSTING);

            arrComments.add(comment);
            commentAdapter.notifyDataSetChanged();

            etComment.setText("");
            postComment(comment);
          }
        }
      });

      getComments();
    } else {

    }
    vsComment.setVisibility(View.VISIBLE);
  }

  private void getComments() {
    List<NameValuePair> params = ParameterFactory.createGetLikersParams(itemID);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        showRefreshLayout(srlComment);
      }

      @Override
      public void after(int statusCode, String response) {
        hideRefreshLayout(srlComment);
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Comment> list = CommonParser.parseListCommentsFromJson(self, json);
            arrComments.clear();
            arrComments.addAll(list);
            commentAdapter.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_GET_ITEM_COMMENTS);
  }

  private void postComment(final Comment comment) {
    if (!isPostingComment) {
      isPostingComment = true;

      List<NameValuePair> params = ParameterFactory.createPostCommentParams(comment.getItemId(), comment.getText());

      AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
        @Override
        public void before() {
        }

        @Override
        public void after(int statusCode, String response) {

          isPostingComment = false;
          switch (statusCode) {
            case NetBase.OK:
              break;
            case NetBase.NETWORK_OFF:
            case NetBase.ERROR:
            default:
              comment.setStatus(Comment.STATUS_FAILED);
              commentAdapter.notifyDataSetChanged();
              break;
          }

          parseResponseStatus(statusCode, response, new NetBaseListener() {
            @Override
            public void onRequestSuccess(String json) {
              arrComments.remove(comment);
              comment.setStatus(Comment.STATUS_POSTED);
              arrComments.add(comment);
              commentAdapter.notifyDataSetChanged();

              currentItem.setCommnetCount(currentItem.getCommnetCount() + 1);
              fillLayout();
            }

            @Override
            public void onRequestError(int message) {
              _onRequestError(message);
              comment.setStatus(Comment.STATUS_FAILED);
              commentAdapter.notifyDataSetChanged();
            }
          });

        }
      }, params);

      asyncPost.execute(WebServiceConfig.URL_POST_ITEM_COMMENTS);
    }
  }

  private void showReportSlider() {
    if (vsLike != null) {
      vsLike.setVisibility(View.GONE);
    }
    if (vsComment != null) {
      vsComment.setVisibility(View.GONE);
    }
    if (vsReport == null) {
      vsReport = (ViewStub) findViewById(R.id.vsReport);
      View inflateReport = vsReport.inflate();
      Drawable drBack = getResources().getDrawable(com.mikepenz.materialdrawer.R.drawable.abc_ab_share_pack_mtrl_alpha);
      drBack.setColorFilter(getResources().getColor(R.color.app_primary_text_color), PorterDuff.Mode.MULTIPLY);
      ((ImageView) inflateReport.findViewById(R.id.ivBack)).setImageDrawable(drBack);
      lvReport = (ListView) inflateReport.findViewById(R.id.lvReport);
      tvApply = (TextView) findViewById(R.id.tvApply);

      arrReport = new ArrayList<>();
      arrReport.add(new Category("1", getString(R.string.wrong_category), ""));
      arrReport.add(new Category("2", getString(R.string.counterfeit), ""));
      arrReport.add(new Category("3", getString(R.string.keyword_spam), ""));
      arrReport.add(new Category("4", getString(R.string.prohibited_item), ""));
      arrReport.add(new Category("5", getString(R.string.repeated_posts), ""));
      arrReport.add(new Category("6", getString(R.string.promotion_abuse), ""));
      arrReport.add(new Category("7", getString(R.string.nudity), ""));
      arrReport.add(new Category("8", getString(R.string.services), ""));
      arrReport.add(new Category("9", getString(R.string.hateful_speech), ""));

      reportAdapter = new CategoryFilterAdapter(self, arrReport);
      reportAdapter.setSelectedPosition(0);
      lvReport.setAdapter(reportAdapter);

      lvReport.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
          reportAdapter.setSelectedPosition(i);
          reportAdapter.notifyDataSetChanged();
        }
      });

      tvApply.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          reportItem();
          hidePanel();
        }
      });
    } else {

    }
    vsReport.setVisibility(View.VISIBLE);
  }

  private void reportItem() {
    String reasonId = (arrReport == null ? "" : arrReport.get(reportAdapter.getSelectedPosition()).getId());
    List<NameValuePair> params = ParameterFactory.createReportItemParams(currentItem.getId(), reasonId);

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            if (!currentItem.isReported()) {
              currentItem.setReported(true);
              DialogUtility.showMessageDialog(self, getString(R.string.message_report_successfully), null);
            } else {
              currentItem.setReported(false);
              DialogUtility.showMessageDialog(self, getString(R.string.message_unreport_successfully), null);
            }
            invalidateOptionsMenu();
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_REPORT_ITEM);
  }

  private void onRateSeller() {
    DialogUtility.showMessageDialog(self, getString(R.string.rate_seller_alert), null);
  }


  public void onPressBack(View v) {
    hidePanel();
  }

  private void onEditItem() {
    Bundle bundle = new Bundle();
    bundle.putParcelable(GlobalValue.KEY_ITEM, currentItem);
    gotoActivityForResult(self, AddListingActivity.class, bundle, RC_EDIT_ITEM);
  }

  private void onDeleteItem() {
    List<NameValuePair> params = ParameterFactory.createMakeConversationParam(currentItem.getId());

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {

            DialogUtility.showMessageDialog(self, getString(R.string.message_delete_successfully), new DialogUtility.DialogListener() {
              @Override
              public void onClose(Dialog dialog) {
                if (currentItem != null) {
                  Intent intent = new Intent();
                  intent.putExtra(GlobalValue.KEY_DELETE_ITEM, currentItem);
                  intent.putExtra(GlobalValue.KEY_POSITION, currentPosition);
                  setResult(RESULT_OK, intent);
                }
                finish();
              }
            });
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_DELETE_ITEM);

  }

  private void getConversation() {
    if (conversationID == null) {
      List<NameValuePair> params = ParameterFactory.createMakeConversationParam(currentItem.getId());

      AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
        @Override
        public void before() {
          DialogUtility.showProgressDialog(self);
        }

        @Override
        public void after(int statusCode, String response) {
          DialogUtility.closeProgressDialog();
          parseResponseStatus(statusCode, response, new NetBaseListener() {
            @Override
            public void onRequestSuccess(String json) {
              conversationID = CommonParser.getConversationIDFromJson(json);
              gotoChatActivity();
            }

            @Override
            public void onRequestError(int message) {
              _onRequestError(message);
            }
          });
        }
      }, params);

      asyncPost.execute(WebServiceConfig.URL_MAKE_CONVERSATION);
    } else {
      gotoChatActivity();
    }
  }

  private void gotoChatActivity() {
    Bundle bundle = new Bundle();
    bundle.putString(GlobalValue.KEY_ITEM_ID, currentItem.getId());
    bundle.putString(GlobalValue.KEY_ITEM_NAME, currentItem.getTitle());
    bundle.putString(GlobalValue.KEY_ITEM_IMAGE, currentItem.getImage());

      bundle.putString(GlobalValue.KEY_USERNAME, currentItem.getOwnerUsername());
      bundle.putString(GlobalValue.KEY_USER_ID,  currentItem.getOwnerId());

    bundle.putString(GlobalValue.KEY_CONVERSATION_ID, conversationID);

    gotoActivity(self, ChatActivity.class, bundle);
  }

  private void requestVerify() {
    List<NameValuePair> params = ParameterFactory.createRequestVerifyTransactionParam(currentItem.getId());
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        parseResponseStatus(statusCode, response, new NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            String[] array = CommonParser.parseVerificationCodeFromJson(json);
            String code = array[0];
            String transactionID = array[1];

            Bundle bundle = new Bundle();
            bundle.putParcelable(GlobalValue.KEY_ITEM, currentItem);
            bundle.putString(GlobalValue.KEY_CODE, code);
            bundle.putString(GlobalValue.KEY_TRANSACTION_ID, transactionID);
            gotoActivityForResult(self, VerifyTransactionActivity.class, bundle, RC_VERIFY_TRANSACTION);
          }

          @Override
          public void onRequestError(int message) {
            _onRequestError(message);
          }
        });
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_REQUEST_VERIFY_TRANSACTION);
  }
}
