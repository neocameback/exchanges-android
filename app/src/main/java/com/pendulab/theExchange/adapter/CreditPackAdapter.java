package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.CreditPack;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 12/15/2015.
 */
public class CreditPackAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<CreditPack> arrCreditPack;
  private Activity act;

  private int selectedPosition = -1;

  public CreditPackAdapter(Activity context, List<CreditPack> arrCreditPack) {
    this.arrCreditPack = arrCreditPack;
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
    return arrCreditPack.size();
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
    CreditPack pack = arrCreditPack.get(position);

    if (v == null) {
      v = inflater.inflate(R.layout.row_credit_pack, null);
      holder = new ViewHolder();
      holder.tvName = (TextView) v.findViewById(R.id.tvName);
      holder.tvPrice = (TextView) v.findViewById(R.id.tvPrice);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }

    if (pack != null) {

      if (position == selectedPosition) {
        v.setBackgroundColor(act.getResources().getColor(R.color.white_pressed));
      } else {
        v.setBackgroundColor(act.getResources().getColor(R.color.white));
      }

      holder.tvName.setText(pack.getName() + " " + act.getString(R.string.credits_pack));
      holder.tvPrice.setText("$" + pack.getPrice());
    }

    return v;
  }

  class ViewHolder {
    private TextView tvName, tvPrice;
  }
}
