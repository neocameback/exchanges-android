package com.pendulab.theExchange.fragment;

import com.bumptech.glide.Glide;
import com.pendulab.theExchange.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;


public final class ImagesFragment extends Fragment {
  private int imageResource = 0;
  private String imageUrl = "";
  private View view;
  private Activity self;
  private ImageView img;

  public static ImagesFragment newInstance(Activity act, int imageResource) {
    ImagesFragment fragment = new ImagesFragment();
    fragment.self = (Activity) act;
    fragment.imageResource = imageResource;
    return fragment;
  }

  public static ImagesFragment newInstance(Activity act, String imageUrl) {
    ImagesFragment fragment = new ImagesFragment();
    fragment.self = (Activity) act;
    fragment.imageUrl = imageUrl;
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    view = inflater.inflate(R.layout.layout_image, null);
    initUI();
    return view;
  }

  private void initUI() {
    img = (ImageView) view.findViewById(R.id.image);
    if (imageUrl.equalsIgnoreCase("")) {
      Glide.with(getActivity()).load(imageResource).into(img);
    } else {
      Log.i(this.getClass().getSimpleName(), imageUrl);
      Glide.with(getActivity()).load(imageUrl).into(img);
    }
  }

}
