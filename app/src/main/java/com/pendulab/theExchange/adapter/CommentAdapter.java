package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.model.Comment;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Anh Ha Nguyen on 10/12/2015.
 */
public class CommentAdapter extends BaseAdapter {

  private LayoutInflater inflater;
  private List<Comment> arrComments;
  private Activity act;
  private ICommentAdapter listener;

  private int selectedPosition;

  public CommentAdapter(Activity context, List<Comment> arrCmt, ICommentAdapter _listener) {
    this.arrComments = arrCmt;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    this.act = context;
    this.listener = _listener;
  }

  public int getSelectedPosition() {
    return selectedPosition;
  }

  public void setSelectedPosition(int selectedPosition) {
    this.selectedPosition = selectedPosition;
  }

  @Override
  public int getCount() {
    return arrComments.size();
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
    final Comment cmt = arrComments.get(position);

    if (v == null) {
      v = inflater.inflate(R.layout.row_comment, null);
      holder = new ViewHolder();
      holder.tvComment = (TextView) v.findViewById(R.id.tvComment);
      holder.tvName = (TextView) v.findViewById(R.id.tvName);
      holder.tvDate = (TextView) v.findViewById(R.id.tvDate);
      holder.ivUser = (ImageView) v.findViewById(R.id.ivUser);
      v.setTag(holder);
    } else {
      holder = (ViewHolder) v.getTag();
    }

    if (cmt != null) {

      Glide.with(act).load(cmt.getUserImage()).placeholder(R.drawable.avatar_default).error(R.drawable.avatar_default).into(holder.ivUser);
      holder.tvName.setText(cmt.getUsername());
      holder.tvComment.setText(cmt.getText());

      holder.tvName.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickUser(cmt);
        }
      });

      holder.ivUser.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          listener.onClickUser(cmt);
        }
      });

      switch (cmt.getStatus()) {
        case Comment.STATUS_POSTED:
          holder.tvDate.setText(cmt.getDate());
          holder.tvDate.setTextColor(act.getResources().getColor(R.color.app_secondary_text_color));
          holder.tvDate.setOnClickListener(null);
          break;
        case Comment.STATUS_POSTING:
          holder.tvDate.setText(act.getString(R.string.posting));
          holder.tvDate.setTextColor(act.getResources().getColor(R.color.app_secondary_text_color));
          holder.tvDate.setOnClickListener(null);
          break;
        case Comment.STATUS_FAILED:
          holder.tvDate.setText(act.getString(R.string.posting_try_again));
          holder.tvDate.setTextColor(act.getResources().getColor(R.color.red));
          holder.tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              listener.onClickTryAgain(cmt);
            }
          });
          break;
      }
    }

    return v;
  }

  class ViewHolder {
    private ImageView ivUser;
    private TextView tvName, tvComment, tvDate;
  }

  public interface ICommentAdapter {
    void onClickUser(Comment comment);

    void onClickTryAgain(Comment comment);
  }

}
