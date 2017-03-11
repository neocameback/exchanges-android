package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.model.Transaction;
import com.pendulab.theExchange.model.TransactionGroup;
import com.pendulab.theExchange.widget.AnimatedExpandableListView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 12/8/2015.
 */
public class TransactionGroupAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

  private List<TransactionGroup> arrTransactionGroup;
  private Activity activity;
  private LayoutInflater inflater;
  private SharedPreferencesManager preference;
  private SimpleDateFormat sdfFull = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  private SimpleDateFormat sdfDate = new SimpleDateFormat("EEE, MMMM dd, yyyy");
  private ITransactionGroup listener;

  private int type;
  public static final int TYPE_BUY = 1, TYPE_SELL = 2;


  public TransactionGroupAdapter(Activity a, List<TransactionGroup> arr, int type, ITransactionGroup listener) {
    // TODO Auto-generated constructor stub
    this.activity = a;
    this.arrTransactionGroup = arr;
    this.type = type;
    this.listener = listener;
    inflater = (LayoutInflater) a
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    preference = SharedPreferencesManager.getInstance(activity);
  }


  @Override
  public int getRealChildrenCount(int groupPosition) {
    return arrTransactionGroup.get(groupPosition).getArrTransaction().size();
  }

  @Override
  public int getGroupCount() {
    return arrTransactionGroup.size();
  }

  @Override
  public TransactionGroup getGroup(int groupPos) {
    return arrTransactionGroup.get(groupPos);
  }

  @Override
  public Transaction getChild(int groupPos, int childPos) {
    return arrTransactionGroup.get(groupPos).getArrTransaction().get(childPos);
  }

  @Override
  public long getGroupId(int groupPos) {
    return groupPos;
  }

  @Override
  public long getChildId(int groupPos, int childPos) {
    return childPos;
  }

  @Override
  public boolean hasStableIds() {
    return true;
  }

  @Override
  public View getGroupView(int groupPos, boolean b, View convertView, ViewGroup parent) {

    final GroupHolder holder;
    final TransactionGroup item = getGroup(groupPos);

    if (convertView == null) {
      holder = new GroupHolder();
      convertView = inflater.inflate(R.layout.row_transaction_group,
          parent, false);
      holder.ivCalendar = (ImageView) convertView.findViewById(R.id.ivCalendar);
      holder.tvDate = (TextView) convertView.findViewById(R.id.tvDate);
      holder.viewSeparator = convertView.findViewById(R.id.viewSeparator);

      convertView.setTag(holder);
    } else {
      holder = (GroupHolder) convertView.getTag();
    }

    if (item != null) {
      Glide.with(activity).load(R.drawable.ic_calendar).into(holder.ivCalendar);
      holder.viewSeparator.setVisibility(item.isExpand() ? View.GONE : View.VISIBLE);
      try {
        holder.tvDate.setText(sdfDate.format(sdfFull.parse(item.getDate())));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    return convertView;
  }

  @Override
  public View getRealChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

    final ChildHolder holder;
    final Transaction item = getChild(groupPosition, childPosition);

    if (convertView == null) {
      holder = new ChildHolder();
      convertView = inflater.inflate(R.layout.row_transaction_item,
          parent, false);
      holder.ivItem = (ImageView) convertView.findViewById(R.id.ivItem);
      holder.tvItem = (TextView) convertView.findViewById(R.id.tvItem);
      holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
      holder.llItem = (LinearLayout) convertView.findViewById(R.id.llItem);
      holder.hsvItem = (HorizontalScrollView) convertView.findViewById(R.id.svItem);
      holder.viewSeparator = convertView.findViewById(R.id.viewSeparator);

      convertView.setTag(holder);
    } else {
      holder = (ChildHolder) convertView.getTag();
    }

    if (item != null) {
      Glide.with(activity).load(item.getItemImage()).placeholder(R.drawable.image_placeholder).error(R.drawable.image_not_available).into(holder.ivItem);
      try {
        holder.tvItem.setText(item.getItemTitle());
        holder.tvContent.setText((type == TYPE_BUY ? activity.getString(R.string.bought_with) : activity.getString(R.string.sold_out_with)) + " " + item.getMoney() + " " + (item.getMoney() > 1 ? activity.getString(R.string.credits) : activity.getString(R.string.credit)) + " " + (item.getArrTradeItem().size() == 0 ? "" : activity.getString(R.string.and_some_items)));
        holder.viewSeparator.setVisibility(childPosition == arrTransactionGroup.get(groupPosition).getArrTransaction().size() - 1 ? View.VISIBLE : View.GONE);

      } catch (Exception e) {
        e.printStackTrace();
      }

      holder.hsvItem.setVisibility(item.getArrTradeItem().size() > 0 ? View.VISIBLE : View.GONE);
      //remove all items before adding new ones
      holder.llItem.removeAllViews();
      for (int i = 0; i < item.getArrTradeItem().size(); i++) {
        final int j = i;
        ImageView iv = new ImageView(activity);
        LinearLayout.LayoutParams ivParam = new LinearLayout.LayoutParams((int) activity.getResources().getDimension(R.dimen.button_size_xnormal), (int) activity.getResources().getDimension(R.dimen.button_size_xnormal));
        ivParam.setMargins(0, 0, (int) activity.getResources().getDimension(R.dimen.margin_padding_small), 0);
        iv.setLayoutParams(ivParam);
        Glide.with(activity).load(item.getArrTradeItem().get(i).getImage()).into(iv);
        iv.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            listener.onClickTradeItem(groupPosition, childPosition, j);
          }
        });

        holder.llItem.addView(iv);
      }


      holder.ivItem.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickItem(groupPosition, childPosition);
        }
      });

      holder.tvItem.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickItem(groupPosition, childPosition);
        }
      });
    }


    return convertView;
  }


  @Override
  public boolean isChildSelectable(int i, int i1) {
    return true;
  }

  class GroupHolder {
    ImageView ivCalendar;
    TextView tvDate;
    View viewSeparator;
  }

  class ChildHolder {
    ImageView ivItem;
    TextView tvItem, tvContent;
    LinearLayout llItem;
    HorizontalScrollView hsvItem;
    View viewSeparator;
  }

  public interface ITransactionGroup {
    void onClickItem(int groupPosition, int childPosition);

    void onClickTradeItem(int groupPos, int childPost, int itemPos);
  }
}
