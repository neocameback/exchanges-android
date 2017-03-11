package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Club;

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
 * Created by Anh Ha Nguyen on 6/12/2015.
 */
public class ClubAdapter extends BaseAdapter {

  private List<Club> list;
  private Activity activity;
  private Club firstElement;
  private LayoutInflater inflater;
  boolean isFirstTime;
  private ISpinnerListener listener;

  public ClubAdapter(Activity activity, List<Club> list, String defaultText, ISpinnerListener listener) {
    this.activity = activity;
    this.list = list;
    this.inflater = (LayoutInflater) activity
        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.isFirstTime = true;
    this.listener = listener;
    setDefaultText(defaultText);
  }

  public void setDefaultText(String defaultText) {
    if (!defaultText.equalsIgnoreCase("")) {
      this.firstElement = list.get(0);
      list.remove(0);
      list.add(0, new Club("-1", defaultText));
    } else {
      isFirstTime = false;
    }
  }

  @Override
  public int getCount() {
    return list.size();
  }

  @Override
  public Club getItem(int i) {
    return list.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  //     @Override
  public View getDropDownView(int position, View convertView, ViewGroup parent) {
    if (isFirstTime) {
      list.remove(0);
      list.add(0, firstElement);
      isFirstTime = false;
      listener.onSelectFirstTime();
    }
    return getCustomView(position, convertView, parent);
  }

  @Override
  public View getView(int position, View view, ViewGroup viewGroup) {
    notifyDataSetChanged();
    return getCustomView(position, view, viewGroup);
  }

  public View getCustomView(int position, View convertView, ViewGroup parent) {

    View row = inflater.inflate(R.layout.row_text_spinner, parent, false);
    TextView label = (TextView) row.findViewById(R.id.tv);
    label.setText(list.get(position).getName());

    label.setGravity(Gravity.CENTER);
    label.setTextColor(activity.getResources().getColor(R.color.black));
    label.setPadding(0, (int) activity.getResources().getDimension(R.dimen.margin_padding_small), 0, (int) activity.getResources().getDimension(R.dimen.margin_padding_small));

    return row;
  }

  class ViewHolder {
    TextView tv;
  }

  public interface ISpinnerListener {
    void onSelectFirstTime();
  }
}
