package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Item;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/9/2015.
 */
public class ItemAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Item> arrItems;
  private Activity act;
  private IItemAdapter listener;

  public ItemAdapter(Activity context, List<Item> arrItems, IItemAdapter listener) {
    this.arrItems = arrItems;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.act = context;
    this.listener = listener;
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

      v = inflater.inflate(R.layout.row_item, null);
      holder = new ViewHolder();
      holder.ivItem = (ImageView) v.findViewById(R.id.ivItem);
      holder.ivCredit = (ImageView) v.findViewById(R.id.ivCredits);
      holder.ivUser = (ImageView) v.findViewById(R.id.ivUsername);
      holder.ivTime = (ImageView) v.findViewById(R.id.ivTime);
      holder.ivLike = (ImageView) v.findViewById(R.id.ivLikeCount);

      holder.tvTitle = (TextView) v.findViewById(R.id.tvTitle);
      holder.tvPrice = (TextView) v.findViewById(R.id.tvPrice);
      holder.tvUsername = (TextView) v.findViewById(R.id.tvUsername);
      holder.tvTime = (TextView) v.findViewById(R.id.tvTime);
      holder.tvLikeCount = (TextView) v.findViewById(R.id.tvLikeCount);
      holder.tvGone = (TextView) v.findViewById(R.id.tvGone);
      holder.viewClick = v.findViewById(R.id.viewClick);
      holder.rlInfo = (RelativeLayout) v.findViewById(R.id.rlInfo);

      holder.llLike = (LinearLayout) v.findViewById(R.id.llLike);
      holder.llOptions = (LinearLayout) v.findViewById(R.id.llOptions);

      v.setTag(holder);
    } else {
      v = convertView;
      holder = (ViewHolder) v.getTag();

//            if (holder.itemId.equals(item.getId())) {
//                return v;
//            }
    }

    if (item != null) {

      holder.itemId = item.getId();

      if (!holder.isMeasured) {
        v.getViewTreeObserver()
            .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
              // Wait until layout to call Picasso
              @Override
              public void onGlobalLayout() {
                // Ensure we call this only once
                v.getViewTreeObserver()
                    .removeGlobalOnLayoutListener(this);
                configureLayout(item, holder, v, position);

                holder.isMeasured = true;
              }
            });
      }

      Glide.with(act).load(item.getOwnerImage()).error(R.drawable.avatar_default).centerCrop().into(holder.ivUser);
      Glide.with(act).load(R.drawable.ic_credit).centerCrop().into(holder.ivCredit);
      Glide.with(act).load(R.drawable.ic_clock).centerCrop().into(holder.ivTime);
      Glide.with(act).load(item.isLiked() ? R.drawable.ic_heart_active : R.drawable.ic_heart_light).into(holder.ivLike);

      holder.tvTitle.setText(item.getTitle());
      holder.tvUsername.setText(item.getOwnerUsername());
      holder.tvPrice.setText(item.getPrice() + "");
      holder.tvTime.setText(item.getDate());
      holder.tvLikeCount.setText(item.getLikeCount() + "");

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
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .into(holder.ivItem);

      holder.llLike.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickLike(position);
        }
      });

      holder.llOptions.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickOptions(position);
        }
      });

      holder.viewClick.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickTransparentView(position);
        }
      });
    }
    return v;
  }

  private void configureLayout(Item item, final ViewHolder holder, final View v, final int position) {

    RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) holder.ivItem.getLayoutParams();
    param.width = v.getWidth();
    param.height = v.getWidth();
    param.setMargins(act.getResources().getDimensionPixelSize(R.dimen.border_width_very_tiny), act.getResources().getDimensionPixelSize(R.dimen.border_width_very_tiny), act.getResources().getDimensionPixelSize(R.dimen.border_width_very_tiny), 0);
    holder.ivItem.setLayoutParams(param);

    RelativeLayout.LayoutParams viewClickParam = (RelativeLayout.LayoutParams) holder.viewClick.getLayoutParams();
//            Log.i("INFO_HEIGHT", holder.rlInfo.getHeight()+"");
    viewClickParam.height = v.getWidth() + holder.rlInfo.getHeight();
    viewClickParam.width = v.getWidth();
//            Log.i("VIEW_CLICK_PARAM", v.getHeight() + "-" + v.getWidth());
    holder.viewClick.setLayoutParams(viewClickParam);
    holder.viewClick.setBackgroundResource(R.drawable.bg_item_selector);
  }

  class ViewHolder {
    private RelativeLayout rlInfo;
    private LinearLayout llLike, llOptions;
    private ImageView ivItem, ivCredit, ivUser, ivTime, ivLike;
    private TextView tvTitle, tvPrice, tvUsername, tvTime, tvLikeCount, tvGone;
    private String itemId;
    private View viewClick;
    private boolean isMeasured;

  }

  public interface IItemAdapter {
    void onClickTransparentView(int position);

    void onClickLike(int position);

    void onClickOptions(int position);

  }
}
