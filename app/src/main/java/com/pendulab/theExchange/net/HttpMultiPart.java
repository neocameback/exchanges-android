package com.pendulab.theExchange.net;

import com.pendulab.theExchange.R;
import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.utils.DialogUtility;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

/**
 * Created by Anh Ha Nguyen on 8/4/2015.
 */
public class HttpMultiPart {

  private Context context;
  private MultipartEntity reqEntity;
  HttpClient httpClient;
  HttpPost postRequest;
  private String url;
  private HttpMultipartResponse listener;
  private String response;
  private boolean isShowDialog;

  public HttpMultiPart(Context _context, MultipartEntity entity, String _url, boolean _isShowingDialog, String token, HttpMultipartResponse response) {
    this.context = _context;
    this.reqEntity = entity;
    this.url = _url;
    this.listener = response;
    this.isShowDialog = _isShowingDialog;

    try {
      httpClient = new DefaultHttpClient();
      postRequest = new HttpPost(url);
      postRequest.setEntity(entity);
      if (!token.equalsIgnoreCase("")) {
        postRequest.setHeader(WebServiceConfig.KEY_HEADER, "token=" + token);
      }
//        postRequest.setHeader("Accept", "application/json");
//        postRequest.setHeader("Content-type",
//                "application/json;charset=UTF-8");

      Log.i("HttpMultiPart-url", url);
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      ByteArrayOutputStream bytes = new ByteArrayOutputStream();
      entity.writeTo(bytes);
      String content = bytes.toString();
      Log.i("HttpMultiPart-content", content);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void execute() {
    if (isNetworkAvailable()) {
      new PostServiceTask().execute();
    } else {
      listener.onFailure(NetBase.NETWORK_OFF, context.getString(R.string.message_network_is_unavailable));
    }
  }

  class PostServiceTask extends AsyncTask<Void, Void, String> {
    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      if (isShowDialog) {
        DialogUtility.showProgressDialog(context);
      }
    }

    @Override
    protected String doInBackground(Void... voids) {
      try {
        HttpResponse response2 = httpClient.execute(postRequest);
        BufferedReader reader = new BufferedReader(new InputStreamReader(
            response2.getEntity().getContent(), "UTF-8"));
        String sResponse;
        StringBuilder s = new StringBuilder();
        while ((sResponse = reader.readLine()) != null) {
          s = s.append(sResponse);
        }
        response = s.toString();
        if (response != null) {
          Log.i("HttpMultiPart", response);
          return response;
        }

      } catch (Exception e) {
        e.printStackTrace();

        return null;
      }
      return null;
    }

    @Override
    protected void onPostExecute(String response) {
      super.onPostExecute(response);
      if (isShowDialog) {
        DialogUtility.closeProgressDialog();
      }
      if (response == null || response.equalsIgnoreCase("")) {
        listener.onFailure(NetBase.ERROR, context.getString(R.string.message_server_error));
      } else {
        listener.onSuccess(NetBase.OK, response);
      }
    }
  }

  protected boolean isNetworkAvailable() {
    ConnectivityManager conMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo i = conMgr.getActiveNetworkInfo();
    if (i == null)
      return false;
    if (!i.isConnected())
      return false;
    if (!i.isAvailable())
      return false;
    return true;
  }

  public interface HttpMultipartResponse {
    void onSuccess(int statusCode, String json);

    void onFailure(int statusCode, String error);
  }

}
