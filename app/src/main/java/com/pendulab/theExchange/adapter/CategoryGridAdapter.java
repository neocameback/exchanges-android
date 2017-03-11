package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Category;
import com.pendulab.theExchange.widget.ScaleImageView;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

public class CategoryGridAdapter extends BaseAdapter {
  private LayoutInflater inflater;
  private List<Category> arrCategory;
  private Activity act;
  private ICategoryListener listener;

  public CategoryGridAdapter(Activity context, List<Category> arrDancers, ICategoryListener listener) {
    this.arrCategory = arrDancers;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.act = context;
    this.listener = listener;
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
      v = inflater.inflate(R.layout.row_category, null);
      holder = new ViewHolder();
      holder.image = (ScaleImageView) v.findViewById(R.id.ivCategory);
      holder.tvName = (TextView) v.findViewById(R.id.tvName);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
      if (holder.itemId.equals(category.getId())) {
        return v;
      }
    }

    if (category != null) {

      holder.itemId = category.getId();

//            holder.prbItem.setVisibility(View.VISIBLE);


      Glide.with(act)
          .load(arrCategory.get(position).getImage())
//                        .error(R.drawable.image_not_available)
          .centerCrop()
          .into(holder.image);

      holder.tvName.setText(category.getName());

    }
    return v;
  }

  class ViewHolder {
    private ScaleImageView image;
    private ImageView ivMenu;
    private TextView tvName, tvAge, tvLocation;
    private LinearLayout llTransparent;
    private ProgressBar prbItem;
    private String itemId;
    private boolean isMeasured;
  }

  public interface ICategoryListener {
    void onClickMenu(View v, int position);

    void onClickView(int position);
  }
}
