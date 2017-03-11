package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.LocationObj;
import com.pendulab.theExchange.utils.AppUtil;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/8/2015.
 */
public class LocationSearchAdapter extends BaseAdapter {
  private List<LocationObj> list;
  private Activity activity;
  private LayoutInflater inflater;

  public LocationSearchAdapter(Activity activity, List<LocationObj> list) {
    this.activity = activity;
    this.list = list;
    this.inflater = (LayoutInflater) activity
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }


  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public LocationObj getItem(int i) {
    return list.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }


  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {
    final ViewHolder holder;
    View v = view;
    LocationObj obj = list.get(position);

    if (v == null) {
      v = inflater.inflate(R.layout.row_text_spinner, null);
      holder = new ViewHolder();
      holder.tv = (TextView) v.findViewById(R.id.tv);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }
    if (obj != null) {
      holder.tv.setText(obj.getLocationAddress());
      holder.tv.setPadding(0, AppUtil.getDimensionInPixel(activity, R.dimen.margin_padding_small), 0, (AppUtil.getDimensionInPixel(activity, R.dimen.margin_padding_small)));
    }
    return v;
  }


  class ViewHolder {
    TextView tv;
  }
}
