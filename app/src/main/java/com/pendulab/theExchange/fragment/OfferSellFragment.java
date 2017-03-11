package com.pendulab.theExchange.fragment;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.activity.OfferListActivity;
import com.pendulab.theExchange.adapter.OfferSellAdapter;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.base.BaseFragment;
import com.pendulab.theExchange.config.GlobalValue;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.Offer;
import com.pendulab.theExchange.net.AsyncHttpPost;
import com.pendulab.theExchange.net.AsyncHttpResponse;
import com.pendulab.theExchange.utils.AppUtil;
import com.pendulab.theExchange.utils.CommonParser;

import org.apache.http.NameValuePair;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/20/2015.
 */
public class OfferSellFragment extends BaseFragment implements View.OnClickListener {

  public static OfferSellFragment instance;
  public static boolean firstTime;

  public static OfferSellFragment getInstance() {
    if (instance == null) {
      instance = new OfferSellFragment();
    }
    firstTime = true;
    return instance;
  }

  private SwipeRefreshLayout swipeRefreshLayout;
  private ListView lvOffer;
  private List<Offer> arrOffers;
  private OfferSellAdapter adapter;
  private TextView tvNoResult;


  @Override
  public int getFragmentResource() {
    return R.layout.fragment_offer_sell;
  }

  @Override
  public void initView(View view) {
    setTitlePage(instance.getString(R.string.all_offers));
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeRefreshLayout);
    swipeRefreshLayout.setColorSchemeResources(R.color.app_primary_color);
    lvOffer = (ListView) view.findViewById(R.id.lvOffer);
    tvNoResult = (TextView) view.findViewById(R.id.tvNoResult);
  }

  @Override
  public void initData(View view) {
    if (adapter == null) {
      arrOffers = new ArrayList<>();
    }

    adapter = new OfferSellAdapter(self, arrOffers, new OfferSellAdapter.IOfferSellAdapter() {
      @Override
      public void onClickItem(int position) {
        getBaseActivity().gotoItemDetailsActivity(arrOffers.get(position).getItemID());
      }
    });

    lvOffer.setAdapter(adapter);
  }

  @Override
  public void initControl(View view) {
    lvOffer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Item item = new Item();
        item.setId(arrOffers.get(i).getItemID());

        Bundle bundle = new Bundle();
        bundle.putParcelable(GlobalValue.KEY_ITEM, item);
        gotoActivity(self, OfferListActivity.class, bundle);
      }
    });

    swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
      @Override
      public void onRefresh() {
        self.runOnUiThread(new Runnable() {
          @Override
          public void run() {
            arrOffers.clear();
            adapter.notifyDataSetChanged();
            getOfferSelling();
          }
        });
      }
    });

    lvOffer.setOnScrollListener(new AbsListView.OnScrollListener() {
      @Override
      public void onScrollStateChanged(AbsListView absListView, int i) {

      }

      @Override
      public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        AppUtil.supportSwipeRefreshLayout(lvOffer, i, i1, i2, swipeRefreshLayout);
      }
    });
  }

  @Override
  public void onClick(View view) {

  }

  @Override
  public void onResume() {
    super.onResume();
    if (firstTime) {
      arrOffers.clear();
      firstTime = false;
      getOfferSelling();
    } else {
      if (arrOffers.size() == 0) {
        tvNoResult.setVisibility(View.VISIBLE);
      } else {
        tvNoResult.setVisibility(View.GONE);
      }
    }
  }

  private void getOfferSelling() {
    AsyncHttpPost asyncPost = new AsyncHttpPost(self, getBaseActivity().myAccount.getToken(), new AsyncHttpResponse() {
      @Override
      public void before() {
        getBaseActivity().showRefreshLayout(swipeRefreshLayout);

      }

      @Override
      public void after(int statusCode, String response) {
        getBaseActivity().hideRefreshLayout(swipeRefreshLayout);
        getBaseActivity().parseResponseStatus(statusCode, response, new BaseActivity.NetBaseListener() {
          @Override
          public void onRequestSuccess(String json) {
            List<Offer> list = CommonParser.parseListOfferSellFromJson(self, json);
            arrOffers.addAll(list);
            adapter.notifyDataSetChanged();
            if (arrOffers.size() == 0) {
              tvNoResult.setVisibility(View.VISIBLE);
            } else {
              tvNoResult.setVisibility(View.GONE);
            }

          }

          @Override
          public void onRequestError(int message) {
            getBaseActivity()._onRequestError(message);
          }
        });


      }
    }, new ArrayList<NameValuePair>());
    asyncPost.execute(WebServiceConfig.URL_GET_OFFER_LIST);
  }
}
