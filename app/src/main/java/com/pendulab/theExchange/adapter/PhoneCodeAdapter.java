package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.PhoneCode;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/1/2015.
 */
public class PhoneCodeAdapter extends BaseAdapter {

  private List<PhoneCode> list;
  private Activity activity;
  private String firstElement;
  private LayoutInflater inflater;
  boolean isFirstTime;

  public PhoneCodeAdapter(Activity activity, List<PhoneCode> list) {
    this.activity = activity;
    this.list = list;
    this.inflater = (LayoutInflater) activity
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.isFirstTime = true;
  }


  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public PhoneCode getItem(int i) {
    return list.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  //     @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    return getCustomView(position, convertView, parent, 1);
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {

    View row = inflater.inflate(R.layout.row_text_spinner, viewGroup, false);
    TextView label = (TextView) row.findViewById(R.id.tv);
    label.setText(list.get(position).getCode());

    label.setTextColor(activity.getResources().getColor(R.color.app_primary_text_color));

    return row;
  }

  public View getCustomView(int position, View convertView, ViewGroup parent, int type) {

    ViewHolder holder;

    if (convertView == null) {
      holder = new ViewHolder();
      convertView = inflater.inflate(R.layout.row_phone_code, parent, false);
      holder.tvCountry = (TextView) convertView.findViewById(R.id.tvCountry);
      holder.tvCode = (TextView) convertView.findViewById(R.id.tvCode);

      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    holder.tvCountry.setText(list.get(position).getCountry());
    holder.tvCode.setText(list.get(position).getCode());

    holder.tvCountry.setTextColor(activity.getResources().getColor(R.color.app_primary_text_color));
    holder.tvCode.setTextColor(activity.getResources().getColor(R.color.app_primary_text_color));

    return convertView;
  }

  class ViewHolder {
    TextView tvCountry, tvCode;
  }
}
