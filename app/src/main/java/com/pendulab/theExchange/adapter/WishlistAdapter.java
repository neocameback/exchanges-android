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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/29/2015.
 */
public class WishlistAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Item> arrItems;
  private Activity act;
  private IWishlistAdapter listener;
  private boolean isMe;

  public WishlistAdapter(Activity context, List<Item> arrItems, boolean isMe, IWishlistAdapter listener) {
    this.arrItems = arrItems;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.act = context;
    this.listener = listener;
    this.isMe = isMe;
  }

  @Override
  public int getCount() {
    return arrItems.size();
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
    final View v;
    final Item item = arrItems.get(position);

    if (convertView == null) {

      v = inflater.inflate(R.layout.row_wishlist, null);
      holder = new ViewHolder();
      holder.ivItem = (ImageView) v.findViewById(R.id.ivItem);
      holder.ivUser = (ImageView) v.findViewById(R.id.ivUser);
      holder.ivDate = (ImageView) v.findViewById(R.id.ivDate);
      holder.ivAction = (ImageView) v.findViewById(R.id.ivAction);
      holder.tvUsername = (TextView) v.findViewById(R.id.tvUsername);
      holder.tvItem = (TextView) v.findViewById(R.id.tvItem);
      holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
      holder.tvGone = (TextView) v.findViewById(R.id.tvGone);

      v.setTag(holder);
    } else {
      v = convertView;
      holder = (ViewHolder) v.getTag();

//            if (holder.itemId.equals(item.getId())) {
//                return v;
//            }
    }

    RelativeLayout.LayoutParams lpTvDate = (RelativeLayout.LayoutParams) holder.tvDate.getLayoutParams();
    RelativeLayout.LayoutParams lpIvDate = (RelativeLayout.LayoutParams) holder.ivDate.getLayoutParams();


    if (item != null) {

      holder.itemId = item.getId();
      holder.tvItem.setText(item.getTitle());

      holder.ivAction.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickAction(position);
        }
      });


      //Real item
      if (item.getOwnerId() != null) {
        Glide.with(act).load(item.getOwnerImage()).error(R.drawable.avatar_default).centerCrop().into(holder.ivUser);
        Glide.with(act).load(R.drawable.ic_clock).centerCrop().into(holder.ivDate);
        Glide.with(act).load(R.drawable.ic_action_overflow_black).into(holder.ivAction);

        holder.ivItem.setVisibility(View.VISIBLE);
        holder.ivUser.setVisibility(View.VISIBLE);
        holder.ivDate.setVisibility(View.VISIBLE);
        holder.tvUsername.setVisibility(View.VISIBLE);
        holder.tvDate.setVisibility(View.VISIBLE);

        holder.tvUsername.setText(item.getOwnerUsername());
        holder.tvDate.setText(item.getDate());
        if (item.getStatus() == Item.STATUS_SOLD) {
          holder.tvGone.setVisibility(View.VISIBLE);
        } else {
          holder.tvGone.setVisibility(View.GONE);
        }

        Glide.with(act)
            .load(item.getImage())
            .placeholder(R.drawable.image_placeholder)
            .error(R.drawable.image_not_available)
            .centerCrop()
            .into(holder.ivItem);

        holder.tvItem.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            listener.onClickItem(position);
          }
        });

        holder.ivItem.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            listener.onClickItem(position);
          }
        });

        holder.ivUser.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            listener.onClickUser(position);
          }
        });

        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            listener.onClickUser(position);
          }
        });


        lpTvDate.removeRule(RelativeLayout.RIGHT_OF);
        lpTvDate.removeRule(RelativeLayout.BELOW);
        lpTvDate.addRule(RelativeLayout.BELOW, R.id.tvItem);
        lpTvDate.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpTvDate.setMargins(0, 0, 0, 0);
        holder.tvDate.setLayoutParams(lpTvDate);

        lpIvDate.removeRule(RelativeLayout.BELOW);
        lpIvDate.removeRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lpIvDate.addRule(RelativeLayout.BELOW, R.id.tvItem);
        lpIvDate.addRule(RelativeLayout.LEFT_OF, R.id.tvDate);
        lpIvDate.setMargins(0, 0, act.getResources().getDimensionPixelSize(R.dimen.margin_padding_tiny), 0);
        holder.ivDate.setLayoutParams(lpIvDate);

      }

      //item added manually
      else {
        holder.ivItem.setVisibility(View.INVISIBLE);
        holder.ivUser.setVisibility(View.GONE);
        holder.ivDate.setVisibility(View.GONE);
        holder.tvUsername.setVisibility(View.GONE);
        holder.tvDate.setVisibility(View.GONE);
//                Glide.with(act).load(R.drawable.ic_tag_light).into(holder.ivUser);
//                Glide.with(act).load(R.drawable.ic_tag_light).into(holder.ivDate);
//                holder.tvUsername.setText(act.getString(R.string.min_price) + ": " + (item.getImage().equalsIgnoreCase("") ? act.getString(R.string.unspecified) : item.getImage()));
//                holder.tvDate.setText(act.getString(R.string.max_price) + ": " + (item.getDate().equalsIgnoreCase("") ? act.getString(R.string.unspecified) : item.getDate()));

        holder.tvItem.setOnClickListener(null);
        holder.ivItem.setOnClickListener(null);
        holder.ivUser.setOnClickListener(null);
        holder.tvUsername.setOnClickListener(null);

        lpTvDate.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        lpTvDate.removeRule(RelativeLayout.BELOW);
        lpTvDate.addRule(RelativeLayout.BELOW, R.id.ivUser);
        lpTvDate.addRule(RelativeLayout.RIGHT_OF, R.id.ivDate);
        lpTvDate.setMargins(act.getResources().getDimensionPixelSize(R.dimen.margin_padding_tiny), act.getResources().getDimensionPixelSize(R.dimen.margin_padding_tiny), 0, 0);

        holder.tvDate.setLayoutParams(lpTvDate);

        lpIvDate.removeRule(RelativeLayout.BELOW);
        lpIvDate.removeRule(RelativeLayout.LEFT_OF);
        lpIvDate.addRule(RelativeLayout.BELOW, R.id.ivUser);
        lpIvDate.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        lpIvDate.setMargins(0, act.getResources().getDimensionPixelSize(R.dimen.margin_padding_tiny), 0, 0);
        holder.ivDate.setLayoutParams(lpIvDate);
      }

      if (isMe) {
        holder.ivAction.setVisibility(View.VISIBLE);
      } else {
        holder.ivAction.setVisibility(View.GONE);
      }
    }
    return v;
  }


  class ViewHolder {
    private ImageView ivItem, ivUser, ivDate, ivAction;
    private TextView tvItem, tvUsername, tvDate, tvGone;
    private String itemId;

  }

  public interface IWishlistAdapter {
    void onClickAction(int position);

    void onClickUser(int position);

    void onClickItem(int position);

  }
}
