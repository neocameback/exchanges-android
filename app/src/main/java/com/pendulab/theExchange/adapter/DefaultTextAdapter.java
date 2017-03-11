package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 6/5/2015.
 */
public class DefaultTextAdapter extends BaseAdapter {

  private List<String> list;
  private Activity activity;
  private String firstElement;
  private LayoutInflater inflater;
  boolean isFirstTime;
  String defaultText;

  public DefaultTextAdapter(Activity activity, List<String> list, String defaultText) {
    this.activity = activity;
    this.list = list;
    this.inflater = (LayoutInflater) activity
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.isFirstTime = true;
    this.defaultText = defaultText;
    setDefaultText(defaultText);
  }

  public void setDefaultText(String defaultText) {
    if (!defaultText.equalsIgnoreCase("")) {
      this.firstElement = list.get(0);
      list.remove(0);
      list.add(0, defaultText);
    }
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public String getItem(int i) {
    return list.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  //     @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    if (!defaultText.equalsIgnoreCase("")) {
      if (isFirstTime) {
        list.remove(0);
        list.add(0, firstElement);
        isFirstTime = false;
      }
    }
    return getCustomView(position, convertView, parent, 1);
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {
    notifyDataSetChanged();
    return getCustomView(position, view, viewGroup, 0);
  }

  public View getCustomView(int position, View convertView, ViewGroup parent, int type) {

    View row = inflater.inflate(R.layout.row_text_spinner, parent, false);
    TextView label = (TextView) row.findViewById(R.id.tv);
    label.setText(list.get(position));

    if (defaultText.equalsIgnoreCase("")) {
      label.setGravity(Gravity.CENTER);
    }

    if (type == 1) {
      label.setGravity(Gravity.CENTER);
      label.setTextColor(activity.getResources().getColor(R.color.black));
      label.setPadding(0, (int) activity.getResources().getDimension(R.dimen.margin_padding_small), 0, (int) activity.getResources().getDimension(R.dimen.margin_padding_small));
    }

    return row;
  }

  class ViewHolder {
    TextView tv;
  }
}
