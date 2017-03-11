package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Category;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 11/23/2015.
 */
public class ReviewSuggestionAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Category> arrCategory;
  private Activity act;
  private boolean isPositive;
  private IReViewSuggestionAdapter listener;

  public ReviewSuggestionAdapter(Activity context, List<Category> arrDancers, boolean isPositive, IReViewSuggestionAdapter listener) {
    this.arrCategory = arrDancers;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.act = context;
    this.isPositive = isPositive;
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
      v = inflater.inflate(R.layout.row_review_suggestion, null);
      holder = new ViewHolder();
      holder.tvSuggestion = (TextView) v.findViewById(R.id.tvSuggestion);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }

    if (category != null) {
      holder.tvSuggestion.setText(category.getName());
    }

    if (isPositive) {
      holder.tvSuggestion.setBackgroundResource(R.drawable.btn_green_selector);
    } else {
      holder.tvSuggestion.setBackgroundResource(R.drawable.btn_grey_selector);
    }

//        holder.tvSuggestion.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                listener.onClickSuggestion(position);
//            }
//        });

    return v;
  }

  class ViewHolder {
    private TextView tvSuggestion;
  }

  public interface IReViewSuggestionAdapter {
    void onClickSuggestion(int position);
  }

}
