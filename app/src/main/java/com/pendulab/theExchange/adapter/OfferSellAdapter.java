package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Offer;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/20/2015.
 */
public class OfferSellAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Offer> arrOffers;
  private Activity act;
  private IOfferSellAdapter listener;

  public OfferSellAdapter(Activity context, List<Offer> arrOffers, IOfferSellAdapter listener) {
    this.arrOffers = arrOffers;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.act = context;
    this.listener = listener;
  }

  @Override
  public void notifyDataSetChanged() {
//        for (Offer offer : arrOffers) {
//            if (offer.getStatus() == Offer.STATUS_ACCEPTED) {
//                break;
//            }
//        }
    super.notifyDataSetChanged();
  }

  @Override
  public int getCount() {
    return arrOffers.size();
  }

  @Override
  public Object getItem(int position) {
    return position;
  }

  @Override
  public long getItemId(int position) {
    return position;
  }

  @Override
  public View getView(final int position, View convertView, ViewGroup parent) {

    final ViewHolder holder;
    final View v;
    final Offer offer = arrOffers.get(position);

    if (convertView == null) {

      v = inflater.inflate(R.layout.row_offer, null);
      holder = new ViewHolder();
      holder.ivUser = (ImageView) v.findViewById(R.id.ivUser);
      holder.ivDate = (ImageView) v.findViewById(R.id.ivDate);
      holder.ivDate.setVisibility(View.GONE);
      holder.ivAction = (ImageView) v.findViewById(R.id.ivAction);
      holder.ivAccept = (ImageView) v.findViewById(R.id.ivAccept);
      holder.ivReject = (ImageView) v.findViewById(R.id.ivReject);
      holder.tvOffer = (TextView) v.findViewById(R.id.tvOffer);
      holder.tvUser = (TextView) v.findViewById(R.id.tvUser);
      holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
      holder.tvStatus = (TextView) v.findViewById(R.id.tvStatus);
      holder.llItem = (LinearLayout) v.findViewById(R.id.llItem);
      holder.llItemWrapper = (LinearLayout) v.findViewById(R.id.llItemWrapper);
      holder.llActions = (LinearLayout) v.findViewById(R.id.llActions);
      holder.llItemWrapper.setVisibility(View.GONE);
      holder.ivChat = (ImageView) v.findViewById(R.id.ivChat);

      RelativeLayout.LayoutParams paramAction = (RelativeLayout.LayoutParams) holder.llActions.getLayoutParams();
      paramAction.addRule(RelativeLayout.CENTER_VERTICAL);
      holder.llActions.setLayoutParams(paramAction);

      v.setTag(holder);
    } else {
      v = convertView;
      holder = (ViewHolder) v.getTag();

    }

    if (offer != null) {


      Glide.with(act).load(offer.getItemImage()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_not_available).centerCrop().into(holder.ivUser);

      holder.tvUser.setText(offer.getItemTitle());
      holder.tvDate.setText(offer.getMoney() + " " + (offer.getMoney() > 1 ? act.getString(R.string.offers_lowcase) : act.getString(R.string.offer_lowcase)));
      holder.ivChat.setVisibility(View.GONE);

      Drawable dr = act.getResources().getDrawable(R.drawable.ic_action_next_item);
      dr.setColorFilter(act.getResources().getColor(R.color.app_primary_text_color), PorterDuff.Mode.MULTIPLY);
      holder.ivAction.setImageDrawable(dr);
      holder.ivAction.setVisibility(View.VISIBLE);


      holder.ivUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickItem(position);
        }
      });

      holder.tvUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickItem(position);
        }
      });
    }
    return v;
  }

  class ViewHolder {
    private ImageView ivUser, ivDate, ivAction, ivAccept, ivReject, ivChat;
    private TextView tvUser, tvOffer, tvDate, tvStatus;
    private LinearLayout llItem, llItemWrapper, llActions;
    private String itemId;
  }

  public interface IOfferSellAdapter {

    void onClickItem(int position);

  }

}
