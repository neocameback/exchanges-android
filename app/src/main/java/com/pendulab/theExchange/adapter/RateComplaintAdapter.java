package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Category;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 9/6/2015.
 */
public class RateComplaintAdapter extends BaseAdapter {

  private Activity activity;
  private List<Category> arrComplaint;
  private LayoutInflater inflater;
  private List<Category> arrSelection;

  public RateComplaintAdapter(Activity activity, List<Category> list, List<Category> arrSelection) {
    this.activity = activity;
    this.arrComplaint = list;
    this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.arrSelection = arrSelection;
  }

  public List<Category> getSelections() {
    return arrSelection;
  }

  @Override
  public int getCount() {
    return arrComplaint.size();
  }

  @Override
  public Category getItem(int i) {
    return arrComplaint.get(i);
  }

  @Override
  public long getItemId(int i) {
    return 0;
  }

  @Override
  public View getView(final int position, View view, ViewGroup viewGroup) {
    final ViewHolder holder;
    if (view == null) {
      holder = new ViewHolder();
      view = inflater.inflate(R.layout.row_offer_inventory, null);
      holder.tvName = (TextView) view.findViewById(R.id.tvName);
      holder.cbSelection = (CheckBox) view.findViewById(R.id.cbSelection);

      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    if (arrComplaint.get(position) != null) {

      holder.tvName.setText(arrComplaint.get(position).getName());
    }

    if (arrSelection.contains(arrComplaint.get(position))) {
      holder.cbSelection.setChecked(true);
    } else {
      holder.cbSelection.setChecked(false);
    }

    holder.cbSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        if (!isChecked) {
          if (arrSelection.contains(arrComplaint.get(position))) {
            arrSelection.remove(arrComplaint.get(position));
          }
        } else {
          if (arrSelection.size() < 4) {
            if (!arrSelection.contains(arrComplaint.get(position))) {
              arrSelection.add(arrComplaint.get(position));
            }
          } else {
            holder.cbSelection.setChecked(false);
          }
        }
        notifyDataSetChanged();
      }
    });

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (arrSelection.contains(arrComplaint.get(position))) {
          arrSelection.remove(arrComplaint.get(position));
          holder.cbSelection.setChecked(false);
        } else {
          if (arrSelection.size() < 4) {
            if (!arrSelection.contains(arrComplaint.get(position))) {
              arrSelection.add(arrComplaint.get(position));
            }
            holder.cbSelection.setChecked(true);
          }
        }
      }
    });

    return view;
  }

  class ViewHolder {
    private TextView tvName;
    private CheckBox cbSelection;
  }

//    public interface IOfferInventory {
//        void onClickView(int position);
//    }
}
