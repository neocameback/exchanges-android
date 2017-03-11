package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Item;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 9/6/2015.
 */
public class OfferInventoryAdapter extends BaseAdapter {

  private Activity activity;
  private List<Item> arrItems;
  private LayoutInflater inflater;
  private int selectedPosition = -1;
  private List<Item> arrSelection;

  public OfferInventoryAdapter(Activity activity, List<Item> list, List<Item> arrSelection) {
    this.activity = activity;
    this.arrItems = list;
    this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.arrSelection = arrSelection;
  }

  public List<Item> getSelections() {
    return arrSelection;
  }

  @Override
  public int getCount() {
    return arrItems.size();
  }

  @Override
  public Item getItem(int i) {
    return arrItems.get(i);
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
      holder.ivAvatar = (ImageView) view.findViewById(R.id.ivAvatar);
      holder.ivCredit = (ImageView) view.findViewById(R.id.ivCredits);
      holder.tvName = (TextView) view.findViewById(R.id.tvName);
      holder.tvPrice = (TextView) view.findViewById(R.id.tvPrice);
      holder.cbSelection = (CheckBox) view.findViewById(R.id.cbSelection);

      holder.cbSelection.setFocusable(false);
      holder.cbSelection.setFocusableInTouchMode(false);
      holder.cbSelection.setClickable(false);

      view.setTag(holder);
    } else {
      holder = (ViewHolder) view.getTag();
    }

    if (arrItems.get(position) != null) {
      Glide.with(activity)
          .load(arrItems.get(position).getImage())
          .placeholder(R.drawable.avatar_default)
          .error(R.drawable.avatar_default)
          .centerCrop()
          .crossFade()
          .into(holder.ivAvatar);

      Glide.with(activity).load(R.drawable.ic_credit).into(holder.ivCredit);

      holder.tvName.setText(arrItems.get(position).getTitle());
      holder.tvPrice.setText(arrItems.get(position).getPrice() + "");
    }

    if (arrSelection.contains(arrItems.get(position))) {
      holder.cbSelection.setChecked(true);
    } else {
      holder.cbSelection.setChecked(false);
    }

//        holder.cbSelection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
//                if (!isChecked) {
//                    if (arrSelection.contains(arrItems.get(position))) {
//                        arrSelection.remove(arrItems.get(position));
//                    }
//                } else {
//                    if (arrSelection.size() < 4) {
//                        if(!arrSelection.contains(arrItems.get(position))) {
//                            arrSelection.add(arrItems.get(position));
//                        }
//                    } else {
//                        holder.cbSelection.setChecked(false);
//                    }
//                }
//                notifyDataSetChanged();
//            }
//        });

    view.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (arrSelection.contains(arrItems.get(position))) {
          arrSelection.remove(arrItems.get(position));
          holder.cbSelection.setChecked(false);
        } else {
          if (arrSelection.size() < 4) {
            if (!arrSelection.contains(arrItems.get(position))) {
              arrSelection.add(arrItems.get(position));
            }
            holder.cbSelection.setChecked(true);
          }
        }
      }
    });

    return view;
  }

  class ViewHolder {
    private ImageView ivAvatar, ivCredit;
    private TextView tvName, tvPrice;
    private CheckBox cbSelection;
  }

//    public interface IOfferInventory {
//        void onClickView(int position);
//    }
}
