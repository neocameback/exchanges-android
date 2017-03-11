package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.model.ActivityFeed;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.utils.AppUtil;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Ha Anh on 4/30/2016.
 */
public class ActivityFeedAdapter2 extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Object> arrActivities;
  private Activity activity;
  private IActivityAdapter listener;

  private int selectedPosition;
  ActivityFeed activityItem = null;
  Message messageItem = null;


  public ActivityFeedAdapter2(Activity context, List<Object> arrActivities, IActivityAdapter _listener) {
    this.arrActivities = arrActivities;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.activity = context;
    this.listener = _listener;
  }

  @Override
  public int getCount() {
    return arrActivities.size();
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

    if (v == null) {
      v = inflater.inflate(R.layout.row_activity_feed2, null);
      holder = new ViewHolder();

      holder.rlFeed = (RelativeLayout) v.findViewById(R.id.rlFeed);
      holder.rlInbox = (RelativeLayout) v.findViewById(R.id.rlInbox);

      holder.tvContentFeed = (TextView) v.findViewById(R.id.tvContentFeed);
      holder.tvDateFeed = (TextView) v.findViewById(R.id.tvDateFeed);
      holder.ivUserFeed = (ImageView) v.findViewById(R.id.ivUserFeed);
      holder.ivItemFeed = (ImageView) v.findViewById(R.id.ivItemFeed);

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

    if (arrActivities.get(position) instanceof ActivityFeed) {
      activityItem = (ActivityFeed) (arrActivities.get(position));
      bindActivityItem(holder, activityItem);

    }

    if (arrActivities.get(position) instanceof Message) {
      messageItem = (Message) (arrActivities.get(position));
      bindInboxItem(holder, messageItem);
    }

    return v;
  }

  private void bindActivityItem(final ViewHolder holder, final ActivityFeed activityItem) {
    holder.rlFeed.setVisibility(View.VISIBLE);
    holder.rlInbox.setVisibility(View.GONE);

    if (activityItem != null) {

      Glide.with(activity).load(activityItem.getUserAvatar()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(holder.ivUserFeed);

      Glide.with(activity).load(activityItem.getItemImage()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_not_available).into(holder.ivItemFeed);

      String content = "";
      switch (activityItem.getType()) {
        case ActivityFeed.ACTIVITY_TYPE_ADD_WISH_LIST:
          content = activityItem.getUsername() + " " + activity.getString(R.string.activity_add_wishlist);
          break;
        case ActivityFeed.ACTIVITY_TYPE_COMMENT:
          content = activityItem.getUsername() + " " + activity.getString(R.string.activity_comment);
          break;
        case ActivityFeed.ACTIVITY_TYPE_MAKE_OFFER:
          content = activityItem.getUsername() + " " + activity.getString(R.string.sent_you_an_offer).toLowerCase();
          break;
        case ActivityFeed.ACTIVITY_TYPE_ACCEPT_OFFER:
          content = activityItem.getUsername() + " " + activity.getString(R.string.accepted_your_offer).toLowerCase();
          break;
      }
      holder.tvContentFeed.setText(content);
      holder.tvDateFeed.setText(activityItem.getTimeAgo());

      holder.ivUserFeed.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickUserFeed(activityItem);
        }
      });

      holder.ivItemFeed.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickItemFeed(activityItem);
        }
      });
    }
  }

  private void bindInboxItem(final ViewHolder holder, final Message messageItem) {
    holder.rlFeed.setVisibility(View.GONE);
    holder.rlInbox.setVisibility(View.VISIBLE);

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
          holder.tvLastMessage.setText(activity.getString(R.string.you_accepted_offer));
        } else {
          holder.tvLastMessage.setText(messageItem.getUsername() + " " + activity.getString(R.string.accepted_your_offer));
        }
      }

      //if the message is reject offer message
      else if (messageItem.getMessage().equalsIgnoreCase("reject_offer")) {
        if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
          holder.tvLastMessage.setText(activity.getString(R.string.you_rejected_offer));
        } else {
          holder.tvLastMessage.setText(messageItem.getUsername() + " " + activity.getString(R.string.rejected_your_offer));
        }
      }

      //or just a normal text message
      else {
        holder.tvLastMessage.setText(messageItem.getMessage());
      }

//            if (messageItem.getOfferMoney() > 0 || !messageItem.getOfferItems().equalsIgnoreCase("")) {
//                holder.tvOffer.setVisibility(View.VISIBLE);
//                if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
//                    holder.tvOffer.setText(activity.getString(R.string.you_made_an_offer));
//                } else {
//                    holder.tvOffer.setText(messageItem.getUsername() + " " + activity.getString(R.string.sent_you_an_offer));
//                }
//            } else {
//                holder.tvOffer.setVisibility(View.GONE);
//            }

      if (messageItem.getMessage().equalsIgnoreCase("make_offer")) {
        holder.tvOffer.setVisibility(View.VISIBLE);
        if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
          holder.tvOffer.setText(activity.getString(R.string.you_made_an_offer) + " " + activity.getString(R.string.with) + " " + messageItem.getOfferMoney() + " " + (messageItem.getOfferMoney() > 1 ? activity.getString(R.string.credits) : activity.getString(R.string.credit)) + (!messageItem.getOfferItems().equalsIgnoreCase("") ? " " + activity.getString(R.string.and_some_items) : ""));
        } else {
          holder.tvOffer.setText(messageItem.getUsername() + " " + activity.getString(R.string.sent_you_an_offer) + " " + activity.getString(R.string.with) + " " + messageItem.getOfferMoney() + " " + (messageItem.getOfferMoney() > 1 ? activity.getString(R.string.credits) : activity.getString(R.string.credit)) + (!messageItem.getOfferItems().equalsIgnoreCase("") ? " " + activity.getString(R.string.and_some_items) : ""));
        }
      }

      //if the message is accept offer message
      else if (messageItem.getMessage().equalsIgnoreCase("accept_offer")) {
        holder.tvOffer.setVisibility(View.VISIBLE);
        if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
          holder.tvOffer.setText(activity.getString(R.string.you_accepted_offer));
        } else {
          holder.tvOffer.setText(messageItem.getUsername() + " " + activity.getString(R.string.accepted_your_offer));
        }
      }
      //or just a normal text message
      else {
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
  }

  class ViewHolder {
    private RelativeLayout rlFeed, rlInbox;

    private ImageView ivUserFeed, ivItemFeed;
    private TextView tvContentFeed, tvDateFeed;

    private ImageView ivUser, ivItem;
    private TextView tvName, tvDate, tvItem, tvOffer, tvLastMessage, tvUnseen;
  }

  public interface IActivityAdapter {
    void onClickUserFeed(ActivityFeed message);

    void onClickItemFeed(ActivityFeed message);

    void onClickUser(Message message);

    void onClickItem(Message message);
  }
}
