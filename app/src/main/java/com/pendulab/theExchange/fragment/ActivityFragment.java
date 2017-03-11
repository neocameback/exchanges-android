package com.pendulab.theExchange.fragment;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.activity.ChatActivity;
import com.pendulab.theExchange.activity.ItemDetailsActivity;
import com.pendulab.theExchange.activity.OfferListActivity;
import com.pendulab.theExchange.adapter.ActivityFeedAdapter2;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.base.BaseFragment;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.database.DatabaseHelper;
import com.pendulab.theExchange.model.ActivityFeed;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.net.AsyncHttpGet;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.DialogUtility;
import com.pendulab.theExchange.utils.ParameterFactory;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/2/2015.
 */
public class ActivityFragment extends BaseFragment {

  public static ActivityFragment instance;
//    public static boolean firstTime;

  public static ActivityFragment getInstance() {
    if (instance == null) {
      instance = new ActivityFragment();

    }
//        firstTime = true;
    return instance;
  }

  private SwipeRefreshLayout srlFeed;
  private ListView lvActivity;
  private List<Object> arrActivities;
  private ActivityFeedAdapter2 adapterFeed;
  private TextView tvNoResult;

//    private SwipeMenuListView lvInbox;
//    private SwipeRefreshLayout srlInbox;
//    private List<Message> arrMessages;
//    private InboxAdapter adapterInbox;

  private boolean touchStarted;

  //    private LinearLayout llBototm;
//    private RelativeLayout rlFeed, rlInbox;
//    public NotificationTextView tvNewMessage;
  private int mLastFirstVisibleItem;

  @Override
  public int getFragmentResource() {
    return R.layout.fragment_activity;
  }

  @Override
  public void initView(View view) {
    srlFeed = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    srlFeed.setColorSchemeResources(R.color.app_primary_color);
    lvActivity = (ListView) view.findViewById(R.id.lvActivity);
    tvNoResult = (TextView) view.findViewById(R.id.tvNoResult);

//        srlInbox = (SwipeRefreshLayout) view.findViewById(R.id.srlInbox);
//        srlInbox.setColorSchemeResources(R.color.app_primary_color);
//        srlInbox.setVisibility(View.GONE);
//        lvInbox = (SwipeMenuListView) view.findViewById(R.id.lvInbox);

//        llBototm = (LinearLayout) view.findViewById(R.id.llBottom);
//        rlFeed = (RelativeLayout) view.findViewById(R.id.rlFeed);
//        rlInbox = (RelativeLayout) view.findViewById(R.id.rlInbox);
//        tvNewMessage = (NotificationTextView) view.findViewById(R.id.tvNewMessage);
  }

  @Override
  public void initData(View view) {
    if (adapterFeed == null) {
      arrActivities = new ArrayList<>();
    }

    adapterFeed = new ActivityFeedAdapter2(self, arrActivities, new ActivityFeedAdapter2.IActivityAdapter() {
      @Override
      public void onClickUserFeed(ActivityFeed message) {
        getHomeActivity().gotoUserProfileActivity(message.getUserId(), message.getUsername());
      }

      @Override
      public void onClickItemFeed(ActivityFeed message) {
        getHomeActivity().gotoItemDetailsActivity(message.getItemId());
      }

      @Override
      public void onClickUser(Message message) {

      }

      @Override
      public void onClickItem(Message message) {

      }
    });

    lvActivity.setAdapter(adapterFeed);
  }

  @Override
  public void initControl(View view) {
    lvActivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      Bundle bundle = null;

      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if (arrActivities.get(i) instanceof ActivityFeed) {

          ActivityFeed feed = (ActivityFeed) arrActivities.get(i);

          switch (feed.getType()) {
            case ActivityFeed.ACTIVITY_TYPE_ADD_WISH_LIST:
              bundle = new Bundle();
              bundle.putString(GlobalValue.KEY_ITEM, feed.getItemId());
              bundle.putString(GlobalValue.KEY_VIEW_LIKER, "");
              gotoActivity(self, ItemDetailsActivity.class, bundle);
              break;

            case ActivityFeed.ACTIVITY_TYPE_COMMENT:
              bundle = new Bundle();
              bundle.putString(GlobalValue.KEY_ITEM, feed.getItemId());
              bundle.putString(GlobalValue.KEY_VIEW_COMMENT, "");
              gotoActivity(self, ItemDetailsActivity.class, bundle);

              break;

            case ActivityFeed.ACTIVITY_TYPE_MAKE_OFFER:
              Item item = new Item();
              item.setId(feed.getItemId());
              bundle = new Bundle();
              bundle.putParcelable(GlobalValue.KEY_ITEM, item);
              gotoActivity(self, OfferListActivity.class, bundle);
              break;

            case ActivityFeed.ACTIVITY_TYPE_ACCEPT_OFFER:
              Item item2 = new Item();
              item2.setId(feed.getItemId());
              bundle = new Bundle();
              bundle.putParcelable(GlobalValue.KEY_ITEM, item2);
              gotoActivity(self, OfferListActivity.class, bundle);
              break;
          }
        } else if (arrActivities.get(i) instanceof Message) {
          Message inbox = (Message) arrActivities.get(i);

          Bundle bundle = new Bundle();
          bundle.putString(GlobalValue.KEY_ITEM_ID, inbox.getItemID());
          bundle.putString(GlobalValue.KEY_ITEM_NAME, inbox.getItemTitle());
          bundle.putString(GlobalValue.KEY_ITEM_IMAGE, inbox.getItemImage());
          bundle.putString(GlobalValue.KEY_USERNAME, inbox.getUsername());
          bundle.putString(GlobalValue.KEY_USER_ID, inbox.getUserId());
          bundle.putString(GlobalValue.KEY_CONVERSATION_ID, inbox.getConversationID());

          gotoActivity(self, ChatActivity.class, bundle);
        }
      }
    });

    lvActivity.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView absListView, int i) {

      }

      @Override
      public void onScroll(AbsListView absListView, int firstVisible, int lastVisible, int totalItemCount) {
        AppUtil.supportSwipeRefreshLayout(lvActivity, firstVisible, lastVisible, totalItemCount, srlFeed);
      }
    });

    srlFeed.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrActivities.clear();
            adapterFeed.notifyDataSetChanged();
            getActivities();
          }
        });
      }
    });


//        lvInbox.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(final int position, SwipeMenu menu, int index) {
//                switch (index) {
//                    case 0:
//                        DialogUtility.showOptionDialog(self, getString(R.string.archive_warning), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
//                            @Override
//                            public void onPositive(Dialog dialog) {
//                                moveToArchive(arrMessages.get(position));
//                            }
//
//                            @Override
//                            public void onNegative(Dialog dialog) {
//
//                            }
//                        });
//                        break;
//                    case 1:
//                        // delete
//                        DialogUtility.showOptionDialog(self, getString(R.string.delete_warning), getString(R.string.text_yes), getString(R.string.text_no), true, new DialogUtility.DialogOptionListener() {
//                            @Override
//                            public void onPositive(Dialog dialog) {
//                                deleteConversation(arrMessages.get(position));
//                            }
//
//                            @Override
//                            public void onNegative(Dialog dialog) {
//
//                            }
//                        });
//                        break;
//                }
//                // false : close the menu; true : not close the menu
//                return false;
//            }
//        });
  }

  @Override
  public void onResume() {
    super.onResume();
    if (arrActivities.size() == 0) {
      getActivities();
    } else {

    }
  }

  private void getActivities() {

    List<NameValuePair> params = ParameterFactory.createGetActivitiesParam("0");

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, getHomeActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        getHomeActivity().showRefreshLayout(srlFeed);
      }

      @Override
      public void after(int statusCode, String response) {
        getHomeActivity().hideRefreshLayout(srlFeed);
        List<ActivityFeed> list = CommonParser.parseListActivitiesFromJson(self, response);
        arrActivities.addAll(list);
//                adapterFeed.notifyDataSetChanged();
        getInbox();
      }
    }, params);
    asyncPost.execute(WebServiceConfig.URL_GET_ACTIVITIES);
  }

  private void getInbox() {
    AsyncHttpGet asyncGet = new AsyncHttpGet(self, getHomeActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        getHomeActivity().showRefreshLayout(srlFeed);
      }

      @Override
      public void after(int statusCode, String response) {
        getHomeActivity().hideRefreshLayout(srlFeed);

        getBaseActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            arrActivities = CommonParser.parseListInboxFromJson(arrActivities, json, getHomeActivity().myAccount);

            if (arrActivities.size() == 0) {
              tvNoResult.setVisibility(View.VISIBLE);
            } else {
              tvNoResult.setVisibility(View.GONE);
            }

            Collections.sort(arrActivities, new Comparator<Object>() {
              @Override
              public int compare(Object lhs, Object rhs) {
                String lhsDate = "";
                String rhsDate = "";
                try {
                  lhsDate = ((Message) lhs).getDate();
                } catch (Exception e) {
                  lhsDate = ((ActivityFeed) lhs).getDate();
                }
                try {
                  rhsDate = ((Message) rhs).getDate();
                } catch (Exception e) {
                  rhsDate = ((ActivityFeed) rhs).getDate();
                }

                Log.i("LHS_RHS", lhsDate + "****" + rhsDate);

                return rhsDate.compareTo(lhsDate);
              }
            });
//                        arrActivities.addAll(list);
            adapterFeed.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            getBaseActivity()._onRequestError(message);
          }
        });
      }
    }, null);
    asyncGet.execute(WebServiceConfig.URL_GET_LIST_INBOX);
  }

  private void moveToArchive(final Message message) {
    List<NameValuePair> params = ParameterFactory.createMoveToArchiveParam(message.getConversationID());

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, getHomeActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        getBaseActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            arrActivities.remove(message);
            adapterFeed.notifyDataSetChanged();
          }

          @Override
          public void onRequestError(int message) {
            getBaseActivity()._onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_MOVE_TO_ARCHIVE);
  }

  private void deleteConversation(final Message message) {
    List<NameValuePair> params = ParameterFactory.createMoveToArchiveParam(message.getConversationID());

    AsyncHttpPost asyncPost = new AsyncHttpPost(self, getHomeActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        DialogUtility.showProgressDialog(self);
      }

      @Override
      public void after(int statusCode, String response) {
        DialogUtility.closeProgressDialog();
        getBaseActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            arrActivities.remove(message);
            adapterFeed.notifyDataSetChanged();
            DatabaseHelper.deleteMessageByConversation(message.getConversationID());
          }

          @Override
          public void onRequestError(int message) {
            getBaseActivity()._onRequestError(message);
          }
        });
      }
    }, params);

    asyncPost.execute(WebServiceConfig.URL_DELETE_CONVERSATION);

  }
}
