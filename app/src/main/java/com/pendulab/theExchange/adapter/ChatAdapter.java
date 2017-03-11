package com.pendulab.theExchange.adapter;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;
import com.pendulab.theExchange.base.BaseActivity;
import com.pendulab.theExchange.config.SharedPreferencesManager;
import com.pendulab.theExchange.model.Account;
import com.pendulab.theExchange.model.Message;
import com.pendulab.theExchange.utils.AppUtil;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Calendar;
import java.util.List;

/**
 * Created by Anh Ha Nguyen on 6/24/2015.
 */
public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

  public static final int DIRECTION_INCOMING = 0;
  public static final int DIRECTION_OUTGOING = 1;

  private Activity activity;
  private List<Message> arrMessages;
  private LayoutInflater inflater;
  private SharedPreferencesManager preference;
  private Account myAccount;
  private IChatAdapter listener;
  private Calendar calendar;

  public ChatAdapter(Activity act, List<Message> list, IChatAdapter _listener) {
    this.activity = act;
    this.arrMessages = list;
    this.listener = _listener;

    preference = SharedPreferencesManager.getInstance(act);
    myAccount = preference.getUserInfo();
    inflater = LayoutInflater.from(activity);
    calendar = Calendar.getInstance();
  }

  public void setMessages(List<Message> mConversationsList) {
    this.arrMessages = mConversationsList;
    notifyDataSetChanged();
  }

  @Override
  public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    View view;
    if (viewType == DIRECTION_OUTGOING) {
      view = inflater.inflate(R.layout.messages_bubble_right, parent, false);
    } else {
      view = inflater.inflate(R.layout.messages_bubble_left, parent, false);
    }
    if (view != null) {
      MessagesViewHolder holder = new MessagesViewHolder(view);
      return holder;
    } else {
      return null;
    }
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder viewholder, final int position) {
    final Message messageItem = arrMessages.get(position);

    Log.i(this.getClass().getSimpleName(), messageItem.getMessage());

    MessagesViewHolder holder = (MessagesViewHolder) viewholder;

    holder.llAudio.setVisibility(View.GONE);
    holder.tvMessage.setVisibility(View.VISIBLE);
    getParentView(holder.ivImageFile).setVisibility(View.GONE);
    getParentView(holder.tvPrice).setVisibility(View.GONE);
    holder.viewTransparent.setVisibility(View.GONE);
    holder.viewDark.setVisibility(View.GONE);
    holder.pbUploading.setVisibility(View.GONE);

    if (holder.tvPrice != null) {
      getParentView(holder.tvPrice).setVisibility(View.GONE);
    }

    Glide.with(activity)
        .load(messageItem.getAvatar())
        .error(R.drawable.avatar_default)
        .into(holder.ivAvatar);

    holder.ivAvatar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        listener.onClickUser(position);
      }
    });

    //if the message is make offer message
    if (messageItem.getMessage().equalsIgnoreCase("make_offer")) {
      if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
        holder.tvMessage.setText(activity.getString(R.string.you_made_an_offer) + " " + activity.getString(R.string.with) + " " + messageItem.getOfferMoney() + " " + (messageItem.getOfferMoney() > 1 ? activity.getString(R.string.credits) : activity.getString(R.string.credit)) + (!messageItem.getOfferItems().equalsIgnoreCase("") ? " " + activity.getString(R.string.and_some_items) : ""));
      } else {
        holder.tvMessage.setText(messageItem.getUsername() + " " + activity.getString(R.string.sent_you_an_offer) + " " + activity.getString(R.string.with) + " " + messageItem.getOfferMoney() + " " + (messageItem.getOfferMoney() > 1 ? activity.getString(R.string.credits) : activity.getString(R.string.credit)) + (!messageItem.getOfferItems().equalsIgnoreCase("") ? " " + activity.getString(R.string.and_some_items) : ""));
      }
    }

    //if the message is accept offer message
    else if (messageItem.getMessage().equalsIgnoreCase("accept_offer")) {
      if (messageItem.getUserSend().equalsIgnoreCase(((BaseActivity) activity).myAccount.getUserID())) {
        holder.tvMessage.setText(activity.getString(R.string.you_accepted_offer));
      } else {
        holder.tvMessage.setText(messageItem.getUsername() + " " + activity.getString(R.string.accepted_your_offer));
      }
    }

    //or just a normal text message
    else {
      holder.tvMessage.setText(messageItem.getMessage());
    }

    holder.tvDateTime.setOnClickListener(null);

    try {
      if (messageItem.getStatus() == Message.STATUS_SENT) {
        holder.tvDateTime.setText(AppUtil.getDateForMessageItem(activity, messageItem.getDate()));
        holder.tvDateTime.setTextColor(activity.getResources().getColor(R.color.app_secondary_text_color));
      } else if (messageItem.getStatus() == Message.STATUS_UPLOADING) {
        holder.viewDark.setVisibility(View.VISIBLE);
        holder.pbUploading.setVisibility(View.VISIBLE);
        holder.tvDateTime.setText("Sending...");
        holder.tvDateTime.setTextColor(activity.getResources().getColor(R.color.app_secondary_text_color));
      } else if (messageItem.getStatus() == Message.STATUS_FAILED) {
        holder.viewDark.setVisibility(View.VISIBLE);
        holder.pbUploading.setVisibility(View.GONE);
        holder.tvDateTime.setText(Html.fromHtml(activity.getString(R.string.send_again)));
        holder.tvDateTime.setTextColor(activity.getResources().getColor(R.color.red));

        holder.tvDateTime.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            listener.onClickSendAgain(position);
          }
        });

      }
    } catch (Exception e) {
      e.printStackTrace();
    }


    holder.llBubble.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        listener.onClickItem(position);
      }
    });


  }


  @Override
  public int getItemViewType(int position) {
    Message messageItem = arrMessages.get(position);
    Log.i(this.getClass().getSimpleName(), messageItem.getUserSend() + "--" + myAccount.getUserID());
    if (messageItem.getUserSend().equalsIgnoreCase(myAccount.getUserID())) {
      return DIRECTION_OUTGOING;
    } else {
      return DIRECTION_INCOMING;
    }
  }


  @Override
  public int getItemCount() {
    return arrMessages.size();
  }

  public View getParentView(View v) {
    return (View) v.getParent();
  }

  private class MessagesViewHolder extends RecyclerView.ViewHolder {
    TextView tvMessage, tvDateTime, tvAudioCurrentDuration, tvAudioTotalDuration, tvPrice, tvPurchase;
    ImageButton btnPlay;
    ImageView ivAvatar, ivImageFile;
    SeekBar audioProgressBar;
    LinearLayout llAudio, llPurchase, llBubble;
    View viewTransparent, viewDark;
    ProgressBar pbUploading;

    MessagesViewHolder(View v) {
      super(v);
      ivAvatar = (ImageView) v.findViewById(R.id.senderPicture);
      ivImageFile = (ImageView) v.findViewById(R.id.imageFile);
      tvMessage = (TextView) v.findViewById(R.id.message_text);
      tvDateTime = (TextView) v.findViewById(R.id.tvDatetime);
      tvPrice = (TextView) v.findViewById(R.id.tvPrice);
      tvPurchase = (TextView) v.findViewById(R.id.tvPurchase);
      llAudio = (LinearLayout) v.findViewById(R.id.audioLayout);
      llPurchase = (LinearLayout) v.findViewById(R.id.llPurchase);
      llBubble = (LinearLayout) v.findViewById(R.id.llBubble);
      btnPlay = (ImageButton) v.findViewById(R.id.btnPlay);
      audioProgressBar = (SeekBar) v.findViewById(R.id.songProgressBar);
      tvAudioCurrentDuration = (TextView) v.findViewById(R.id.songCurrentDurationLabel);
      tvAudioTotalDuration = (TextView) v.findViewById(R.id.songTotalDurationLabel);
      viewTransparent = v.findViewById(R.id.viewTransparent);
      viewDark = v.findViewById(R.id.viewDark);
      pbUploading = (ProgressBar) v.findViewById(R.id.pbLoading);

    }
  }

  private String getImageUrlFromLatLon(String latitude, String longitude) {
    String getMapURL = "http://maps.googleapis.com/maps/api/staticmap?zoom=20&size=560x560&markers=size:large|color:red|"
        + latitude + "," + longitude + "&sensor=false";
    return getMapURL;
  }

  public interface IChatAdapter {
    void onClickUser(int position);

    void onClickItem(int position);

    void onClickSendAgain(int position);
  }

}
