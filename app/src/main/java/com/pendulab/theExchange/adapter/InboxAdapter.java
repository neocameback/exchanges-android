package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.utils.AppUtil;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/10/2015.
 */
public class InboxAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Message> arrInbox;
  private Activity activity;
  private IInboxAdapter listener;

  private int selectedPosition;

  public InboxAdapter(Activity context, List<Message> arrIb, IInboxAdapter _listener) {
    this.arrInbox = arrIb;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.activity = context;
    this.listener = _listener;
  }


  @Override
  public int getCount() {
    return arrInbox.size();
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
    View v = convertView;
    final Message messageItem = arrInbox.get(position);

    if (v == null) {
      v = inflater.inflate(R.layout.row_inbox, null);
      holder = new ViewHolder();
      holder.tvOffer = (TextView) v.findViewById(R.id.tvOffer);
      holder.tvItem = (TextView) v.findViewById(R.id.tvItem);
      holder.tvName = (TextView) v.findViewById(R.id.tvName);
      holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
      holder.tvLastMessage = (TextView) v.findViewById(R.id.tvLastMessage);
      holder.tvUnseen = (TextView) v.findViewById(R.id.tvUnseen);
      holder.ivUser = (ImageView) v.findViewById(R.id.ivUser);
      holder.ivItem = (ImageView) v.findViewById(R.id.ivItem);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }

    if (messageItem != null) {

      Glide.with(activity).load(messageItem.getAvatar()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(holder.ivUser);

      Glide.with(activity).load(messageItem.getItemImage()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_not_available).into(holder.ivItem);
      holder.tvName.setText(messageItem.getUsername());
      holder.tvItem.setText(messageItem.getItemTitle());
      holder.tvDate.setText(AppUtil.getDateForMessageItem(activity, messageItem.getDate()));
      holder.tvUnseen.setText(messageItem.getUnseenCount() + "");

      //if the message is make offer message
      if (messageItem.getMessage().equalsIgnoreCase("make_offer")) {
        if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
          holder.tvLastMessage.setText(activity.getString(R.string.you_made_an_offer) + " " + activity.getString(R.string.with) + " " + messageItem.getOfferMoney() + " " + (messageItem.getOfferMoney() > 1 ? activity.getString(R.string.credits) : activity.getString(R.string.credit)) + (!messageItem.getOfferItems().equalsIgnoreCase("") ? " " + activity.getString(R.string.and_some_items) : ""));
        } else {
          holder.tvLastMessage.setText(messageItem.getUsername() + " " + activity.getString(R.string.sent_you_an_offer) + " " + activity.getString(R.string.with) + " " + messageItem.getOfferMoney() + " " + (messageItem.getOfferMoney() > 1 ? activity.getString(R.string.credits) : activity.getString(R.string.credit)) + (!messageItem.getOfferItems().equalsIgnoreCase("") ? " " + activity.getString(R.string.and_some_items) : ""));
        }
      }

      //if the message is accept offer message
      else if (messageItem.getMessage().equalsIgnoreCase("accept_offer")) {
        if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
          holder.tvLastMessage.setText(activity.getString(R.string.you_made_an_offer));
        } else {
          holder.tvLastMessage.setText(messageItem.getUsername() + " " + activity.getString(R.string.accepted_your_offer));
        }
      }

      //or just a normal text message
      else {
        holder.tvLastMessage.setText(messageItem.getMessage());
      }

      if (messageItem.getOfferMoney() > 0 || !messageItem.getOfferItems().equalsIgnoreCase("")) {
        holder.tvOffer.setVisibility(View.VISIBLE);
        if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
          holder.tvOffer.setText(activity.getString(R.string.you_made_an_offer));
        } else {
          holder.tvOffer.setText(messageItem.getUsername() + " " + activity.getString(R.string.sent_you_an_offer));
        }
      } else {
        holder.tvOffer.setVisibility(View.GONE);
      }


      holder.tvName.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickUser(messageItem);
        }
      });

      holder.ivUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickUser(messageItem);
        }
      });

      holder.ivItem.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickItem(messageItem);
        }
      });


    }

    return v;
  }

  class ViewHolder {
    private ImageView ivUser, ivItem;
    private TextView tvName, tvDate, tvItem, tvOffer, tvLastMessage, tvUnseen;
  }

  public interface IInboxAdapter {
    void onClickUser(Message message);

    void onClickItem(Message message);
  }
}
