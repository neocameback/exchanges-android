package com.pendulab.theExchange.adapter;

import com.pendulab.theExchange.R;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;
import android.widget.TextView;

/**
 * Created by Anh Ha Nguyen on 10/15/2015.
 */
public class SearchViewAdapter extends CursorAdapter implements Filterable {

  private Context context;

  public SearchViewAdapter(Context context, Cursor cursor, int flags) {
    super(context, cursor, 0);
    this.context = context;
  }


  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    return LayoutInflater.from(context).inflate(R.layout.row_text_spinner, parent, false);
  }

  @Override
  public void bindView(View view, Context context, Cursor cursor) {
    TextView tv = (TextView) view.findViewById(R.id.tv);
    tv.setPadding((int) context.getResources().getDimension(R.dimen.margin_padding_small), (int) context.getResources().getDimension(R.dimen.margin_padding_small), (int) context.getResources().getDimension(R.dimen.margin_padding_small), (int) context.getResources().getDimension(R.dimen.margin_padding_small));
    tv.setBackgroundColor(Color.WHITE);
    tv.setText(cursor.getString(1));

  }


}
