package com.pendulab.theExchange.fragment;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.adapter.TransactionGroupAdapter;
import com.pendulab.theExchange.base.BaseFragment;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Transaction;
import com.pendulab.theExchange.model.TransactionGroup;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.CommonParser;
import com.pendulab.theExchange.utils.ParameterFactory;
import com.pendulab.theExchange.widget.AnimatedExpandableListView;

import org.apache.http.NameValuePair;

import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 12/8/2015.
 */
public class HistoryBuyFragment extends BaseFragment {

  public static HistoryBuyFragment instance;
  public static boolean firstTime;

  public static HistoryBuyFragment getInstance() {
    if (instance == null) {
      instance = new HistoryBuyFragment();
    }
    firstTime = true;

    return instance;
  }

  private SwipeRefreshLayout swipeRefreshLayout;
  private AnimatedExpandableListView lvTransaction;
  private List<TransactionGroup> arrTransactionGroup;
  private TransactionGroupAdapter adapter;

  @Override
  public int getFragmentResource() {
    return R.layout.fragment_history_sell;
  }

  @Override
  public void initView(View view) {
    setTitlePage(instance.getString(R.string.transaction_history));
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    lvTransaction = (AnimatedExpandableListView) view.findViewById(R.id.lvTransaction);
  }

  @Override
  public void initData(View view) {
    if (adapter == null) {
      arrTransactionGroup = new ArrayList<>();
    }

    adapter = new TransactionGroupAdapter(self, arrTransactionGroup, TransactionGroupAdapter.TYPE_BUY, new TransactionGroupAdapter.ITransactionGroup() {
      @Override
      public void onClickItem(int groupPosition, int childPosition) {
        getBaseActivity().gotoItemDetailsActivity(arrTransactionGroup.get(groupPosition).getArrTransaction().get(childPosition).getItemID());
      }

      @Override
      public void onClickTradeItem(int groupPos, int childPos, int itemPos) {
        getBaseActivity().gotoItemDetailsActivity(arrTransactionGroup.get(groupPos).getArrTransaction().get(childPos).getArrTradeItem().get(itemPos).getId());
      }
    });

    lvTransaction.setAdapter(adapter);
  }

  @Override
  public void initControl(View view) {
    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrTransactionGroup.clear();
            adapter.notifyDataSetChanged();
            getHistory();
          }
        });
      }
    });

    lvTransaction.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

      @Override
      public boolean onGroupClick(ExpandableListView parent, View v,
                                  int groupPosition, long id) {
        if (lvTransaction.isGroupExpanded(groupPosition)) {
          lvTransaction.collapseGroupWithAnimation(groupPosition);
          arrTransactionGroup.get(groupPosition).setIsExpand(false);
        } else {
          lvTransaction.expandGroupWithAnimation(groupPosition);
          arrTransactionGroup.get(groupPosition).setIsExpand(true);
        }
        adapter.notifyDataSetChanged();
        return true;
      }
    });
  }

  @Override
  public void onResume() {
    super.onResume();
    if (firstTime) {
      firstTime = false;
      getHistory();
    }
//        else{
//            if(arrOffers.size() == 0){
//                tvNoResult.setVisibility(View.VISIBLE);
//            }
//            else{
//                tvNoResult.setVisibility(View.GONE);
//            }
//        }
  }

  private void getHistory() {
    List<NameValuePair> params = ParameterFactory.createGetTransactionHistoryParam(1);

    AsyncHttpPost asyncGet = new AsyncHttpPost(self, getBaseActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        getBaseActivity().showRefreshLayout(swipeRefreshLayout);
      }

      @Override
      public void after(int statusCode, String response) {
        getBaseActivity().hideRefreshLayout(swipeRefreshLayout);
        List<Transaction> list = CommonParser.parseListTransactionsFromJson(self, response);
        generateTransactionGroups(list);

      }
    }, params);
    asyncGet.execute(WebServiceConfig.URL_GET_TRANSACTION_HISTORY);
  }

  private void generateTransactionGroups(List<Transaction> list) {
    arrTransactionGroup.clear();
    for (Transaction transaction : list) {
      TransactionGroup existingGroup = containsDate(transaction.getDate());
      if (arrTransactionGroup.size() != 0 && existingGroup != null) {
        existingGroup.getArrTransaction().add(transaction);
      } else {
        TransactionGroup group = new TransactionGroup();
        group.setDate(transaction.getDate());
        group.getArrTransaction().add(transaction);
        arrTransactionGroup.add(group);
      }
    }

    adapter.notifyDataSetChanged();
    for (int i = 0; i < arrTransactionGroup.size(); i++) {
      lvTransaction.expandGroup(i);
    }

  }

  private TransactionGroup containsDate(String date) {
    for (TransactionGroup group : arrTransactionGroup) {
      if (group.getDate().equalsIgnoreCase(date)) {
        return group;
      }
    }
    return null;
  }
}
