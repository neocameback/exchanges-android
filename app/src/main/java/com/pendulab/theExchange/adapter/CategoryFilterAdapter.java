package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Category;

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
 * Created by Anh Ha Nguyen on 10/12/2015.
 */
public class CategoryFilterAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Category> arrCategory;
  private Activity act;

  private int selectedPosition;

  public CategoryFilterAdapter(Activity context, List<Category> arrDancers) {
    this.arrCategory = arrDancers;
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
    return arrCategory.size();
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
    Category category = arrCategory.get(position);

    if (v == null) {
      v = inflater.inflate(R.layout.row_category_filter, null);
      holder = new ViewHolder();
      holder.ivTick = (ImageView) v.findViewById(R.id.ivTick);
      holder.tvName = (TextView) v.findViewById(R.id.tvName);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }

    if (category != null) {

      if (position == selectedPosition) {
        v.setBackgroundColor(act.getResources().getColor(R.color.white_pressed));
        holder.ivTick.setVisibility(View.VISIBLE);
      } else {
        v.setBackgroundColor(act.getResources().getColor(R.color.white));
        holder.ivTick.setVisibility(View.INVISIBLE);
      }

      holder.tvName.setText(category.getName());
    }

    return v;
  }

  class ViewHolder {
    private ImageView ivTick;
    private TextView tvName;
  }

}
