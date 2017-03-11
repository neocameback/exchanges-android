package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.Item;
import com.pendulab.theExchange.model.Offer;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/17/2015.
 */
public class OfferAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Offer> arrOffers;
  private Activity act;
  private IOfferAdapter listener;
  private boolean isUserActionAllowed = true;
  private Account mAccount;

  public OfferAdapter(Activity context, List<Offer> arrOffers, IOfferAdapter listener) {
    this.arrOffers = arrOffers;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.act = context;
    this.listener = listener;
  }

  @Override
  public void notifyDataSetChanged() {
    isUserActionAllowed = true;
    for (Offer offer : arrOffers) {
      if (offer.getStatus() == Offer.STATUS_ACCEPTED) {
        isUserActionAllowed = false;
        break;
      }
    }
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
      holder.ivAction = (ImageView) v.findViewById(R.id.ivAction);
      holder.ivAccept = (ImageView) v.findViewById(R.id.ivAccept);
      holder.ivReject = (ImageView) v.findViewById(R.id.ivReject);
      holder.tvOffer = (TextView) v.findViewById(R.id.tvOffer);
      holder.tvUser = (TextView) v.findViewById(R.id.tvUser);
      holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
      holder.tvStatus = (TextView) v.findViewById(R.id.tvStatus);
      holder.llItem = (LinearLayout) v.findViewById(R.id.llItem);
      holder.llItemWrapper = (LinearLayout) v.findViewById(R.id.llItemWrapper);
      holder.ivChat = (ImageView) v.findViewById(R.id.ivChat);

      v.setTag(holder);
    } else {
      v = convertView;
      holder = (ViewHolder) v.getTag();

//            if (holder.itemId.equals(offer.getId())) {
//                return v;
//            }
    }

    if (offer != null) {

      holder.itemId = offer.getId();

      holder.tvUser.setText(offer.getUsername());

      Glide.with(act).load(offer.getAvatar()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).centerCrop().into(holder.ivUser);
      Glide.with(act).load(R.drawable.ic_clock).centerCrop().into(holder.ivDate);
      Glide.with(act).load(R.drawable.ic_action_overflow_black).into(holder.ivAction);

      holder.tvUser.setText(offer.getUsername());
      holder.tvDate.setText(offer.getDate());

      // Chat
      holder.ivChat.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          if (listener != null) listener.onClickChat(offer);
        }
      });

      //set status for offer
      holder.tvStatus.setText(offer.getStatus() == Offer.STATUS_ACCEPTED ? act.getString(R.string.accepted) : (offer.getStatus() == Offer.STATUS_REJECT ? act.getString(R.string.rejected) : act.getString(R.string.pending)));
      holder.tvStatus.setBackgroundColor(offer.getStatus() == Offer.STATUS_ACCEPTED ? act.getResources().getColor(R.color.bg_button_view_offer) : (offer.getStatus() == Offer.STATUS_REJECT ? act.getResources().getColor(R.color.bg_button_chat) : act.getResources().getColor(R.color.orange)));

      if (offer.getStatus() == Offer.STATUS_ACCEPTED) {
        if (offer.getVerify() == Item.VERIFY_NONE || (offer.getVerify() == Item.VERIFY_BUY && offer.getOwnerID().equalsIgnoreCase(((BaseActivity) act).myAccount.getUserID())) || (offer.getVerify() == Item.VERIFY_SELL && !offer.getOwnerID().equalsIgnoreCase(((BaseActivity) act).myAccount.getUserID()))) {
          holder.tvStatus.setBackgroundColor(act.getResources().getColor(R.color.red));
          holder.tvStatus.setText(act.getString(R.string.verification_required));
          v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              listener.onClickView(position);
            }
          });
        } else {
          //if both parties have verified
          if (offer.getVerify() == Item.VERIFY_BOTH) {
            holder.tvStatus.setBackgroundColor(act.getResources().getColor(R.color.bg_button_view_offer));
            holder.tvStatus.setText(act.getString(R.string.completed));
          }
          //if the other party hasn't verified
          else {
            holder.tvStatus.setBackgroundColor(act.getResources().getColor(R.color.bg_button_chat));
            holder.tvStatus.setText(act.getString(R.string.waiting_for) + " " + (offer.getOwnerID().equalsIgnoreCase(((BaseActivity) act).myAccount.getUserID()) ? act.getString(R.string.seller) : act.getString(R.string.buyer)) + " " + act.getString(R.string.to_verify));
          }
        }
      }

      holder.tvOffer.setText(act.getString(R.string.offered) + " " + offer.getMoney() + " " + (offer.getMoney() > 1 ? act.getString(R.string.credit_s) : act.getString(R.string.credit)));

      // Show actions for pending offer
      if (offer.getStatus() == Offer.STATUS_ACCEPTED || offer.getStatus() == Offer.STATUS_REJECT
          && isUserActionAllowed) {
        holder.ivAccept.setVisibility(View.GONE);
        holder.ivReject.setVisibility(View.GONE);
        holder.ivAction.setVisibility(View.VISIBLE);

      } else {
        holder.ivAccept.setVisibility(View.VISIBLE);
        holder.ivReject.setVisibility(View.VISIBLE);
        holder.ivAction.setVisibility(View.GONE);

        holder.ivAccept.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (listener != null) listener.onClickAccept(position);
          }
        });

        holder.ivReject.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (listener != null) listener.onClickReject(position);
          }
        });
      }

      //bind trade items if available
      if (offer.getArrTradeItems().size() == 0) {
        holder.llItemWrapper.setVisibility(View.GONE);
      } else {
        holder.llItemWrapper.setVisibility(View.VISIBLE);
        holder.llItem.removeAllViews();
        for (int i = 0; i < offer.getArrTradeItems().size(); i++) {
          final int j = i;
          ImageView iv = new ImageView(act);
          LinearLayout.LayoutParams ivParam = new LinearLayout.LayoutParams((int) act.getResources().getDimension(R.dimen.button_size_xnormal), (int) act.getResources().getDimension(R.dimen.button_size_xnormal));
          ivParam.setMargins(0, 0, (int) act.getResources().getDimension(R.dimen.margin_padding_small), 0);
          iv.setLayoutParams(ivParam);
          Glide.with(act).load(offer.getArrTradeItems().get(i).getImage()).into(iv);
          iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              listener.onClickTradeItem(position, j);
            }
          });

          holder.llItem.addView(iv);
        }
      }

//      if (!isUserActionAllowed) {
//        holder.ivAction.setVisibility(View.INVISIBLE);
//      } else {
//        holder.ivAction.setVisibility(View.VISIBLE);
//      }

      holder.ivUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickUser(position);
        }
      });

      holder.tvUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickUser(position);
        }
      });

      holder.ivAction.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickAction(position);
        }
      });

    }
    return v;
  }

  public OfferAdapter setAccount(Account account) {
    mAccount = account;
    return this;
  }


  class ViewHolder {
    private ImageView ivUser, ivDate, ivAction, ivAccept, ivReject, ivChat;
    private TextView tvUser, tvOffer, tvDate, tvStatus;
    private LinearLayout llItem, llItemWrapper;
    private String itemId;
  }

  public interface IOfferAdapter {
    void onClickAction(int position);

    void onClickAccept(int position);

    void onClickReject(int position);

    void onClickUser(int position);

    void onClickTradeItem(int position, int subPosition);

    void onClickView(int position);

    void onClickChat(Offer offer);
  }
}
