package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Account;

import android.app.Activity;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/12/2015.
 */
public class LikerAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Account> arrAccount;
  private Activity act;

  private int selectedPosition;

  public LikerAdapter(Activity context, List<Account> arrAcc) {
    this.arrAccount = arrAcc;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.act = context;
  }

  public int getSelectedPosition() {
    return selectedPosition;
  }

  public void setSelectedPosition(int selectedPosition) {
    this.selectedPosition = selectedPosition;
  }

  @Override
  public int getCount() {
    return arrAccount.size();
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
    Account acc = arrAccount.get(position);

    if (v == null) {
      v = inflater.inflate(R.layout.row_liker, null);
      holder = new ViewHolder();
      holder.ivTick = (ImageView) v.findViewById(R.id.ivTick);
      holder.tvName = (TextView) v.findViewById(R.id.tvName);
      holder.ivUser = (ImageView) v.findViewById(R.id.ivUser);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }

    if (acc != null) {

      Glide.with(act).load(acc.getImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(holder.ivUser);
      holder.tvName.setText(acc.getUsername());
      Drawable dr = act.getResources().getDrawable(R.drawable.ic_action_next_item);
      dr.setColorFilter(act.getResources().getColor(R.color.app_primary_text_color), PorterDuff.Mode.MULTIPLY);
      holder.ivTick.setImageDrawable(dr);
    }

    return v;
  }

  class ViewHolder {
    private ImageView ivTick, ivUser;
    private TextView tvName;
  }

}
