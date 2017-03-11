package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.utils.AppUtil;

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
 * Created by Anh Ha Nguyen on 10/8/2015.
 */
public class CategoryListAdapter extends BaseAdapter {

  private List<Category> list;
  private Activity activity;
  private Category firstElement;
  private LayoutInflater inflater;
  boolean isFirstTime;
  String defaultText;

  public CategoryListAdapter(Activity activity, List<Category> list, String defaultText) {
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
//            this.firstElement = list.get(0);
//            list.remove(0);
      list.add(new Category("", defaultText, ""));
    }
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Category getItem(int i) {
    return list.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  //     @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    if (position < getCount() - 1) {

      View row = inflater.inflate(R.layout.row_text_spinner, parent, false);
      TextView label = (TextView) row.findViewById(R.id.tv);
      label.setText(list.get(position).getName());

      label.setGravity(Gravity.CENTER);
      label.setTextColor(activity.getResources().getColor(R.color.black));
      label.setPadding(0, (int) activity.getResources().getDimension(R.dimen.margin_padding_small), 0, (int) activity.getResources().getDimension(R.dimen.margin_padding_small));

      return row;
    } else {
      View row = inflater.inflate(R.layout.row_virtual, parent, false);
      return row;
    }
  }

  @Override
  public View getView(int position, View view, ViewGroup parent) {
    notifyDataSetChanged();
    View row = inflater.inflate(R.layout.row_text_spinner, parent, false);
    TextView label = (TextView) row.findViewById(R.id.tv);
    label.setText(list.get(position).getName());

    label.setTextSize(AppUtil.getDimensionInPixel(activity, R.dimen.text_size_xnormal));
    label.setGravity(Gravity.RIGHT);
    return row;
  }


  class ViewHolder {
    TextView tv;
  }

  public boolean isFirstTime() {
    return isFirstTime;
  }
}
