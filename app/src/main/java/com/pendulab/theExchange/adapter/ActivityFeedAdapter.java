package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.ActivityFeed;

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
 * Created by Anh Ha Nguyen on 12/2/2015.
 */
public class ActivityFeedAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<ActivityFeed> arrActivities;
  private Activity activity;
  private IActivityAdapter listener;

  private int selectedPosition;

  public ActivityFeedAdapter(Activity context, List<ActivityFeed> arrActivities, IActivityAdapter _listener) {
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
    final ActivityFeed activityItem = arrActivities.get(position);

    if (v == null) {
      v = inflater.inflate(R.layout.row_activity_feed, null);
      holder = new ViewHolder();
      holder.tvContent = (TextView) v.findViewById(R.id.tvContent);
      holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
      holder.ivUser = (ImageView) v.findViewById(R.id.ivUser);
      holder.ivItem = (ImageView) v.findViewById(R.id.ivItem);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }

    if (activityItem != null) {

      Glide.with(activity).load(activityItem.getUserAvatar()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(holder.ivUser);

      Glide.with(activity).load(activityItem.getItemImage()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_not_available).into(holder.ivItem);

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
      holder.tvContent.setText(content);
      holder.tvDate.setText(activityItem.getDate());

      holder.ivUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickUser(activityItem);
        }
      });

      holder.ivItem.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickItem(activityItem);
        }
      });

    }

    return v;
  }

  class ViewHolder {
    private ImageView ivUser, ivItem;
    private TextView tvContent, tvDate;
  }

  public interface IActivityAdapter {
    void onClickUser(ActivityFeed message);

    void onClickItem(ActivityFeed message);
  }
}
