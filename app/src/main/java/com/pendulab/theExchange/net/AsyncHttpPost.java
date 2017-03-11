/*
 * Name: $RCSfile: AsyncHttpPost.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: Apr 21, 2011 2:43:05 PM $
 *
 * Copyright (C) 2011 COMPANY NAME, Inc. All rights reserved.
 */

package com.pendulab.theExchange.net;

import com.pendulab.theExchange.config.WebServiceConfig;
import com.pendulab.theExchange.utils.SmartLog;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import java.util.List;

/**
 * @author cuongvm6037
 */
public class AsyncHttpPost extends NetBase {
  /**
   * Constructor
   */

  public AsyncHttpPost(Context context, AsyncHttpResponse listener,
                       List<NameValuePair> parameters) {
    super(context, listener, parameters);

  }

  public AsyncHttpPost(Context context, String token, AsyncHttpResponse listener,
                       List<NameValuePair> parameters) {
    super(context, listener, token, parameters);

  }

  /*
   * (non-Javadoc)
   *
   * @see com.bestfashionfriend.lib.net.NetBase#request(java.lang.String)
   */
  @Override
  protected String request(String url) {
    try {
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, TIME_OUT);
      HttpConnectionParams.setSoTimeout(params, TIME_OUT);
      HttpClient httpclient = createHttpClient(url, params);

      HttpPost httppost = new HttpPost(url);
      if (token != null) {
        httppost.setHeader(WebServiceConfig.KEY_HEADER,
            "token=" + token);
      }

      httppost.setEntity(new UrlEncodedFormEntity(parameters));

      // httppost.setHeader("Content-Type", "multipart/form-data");
      SmartLog.logI(getClass().getSimpleName() + " - httppost - url=" + url);
      SmartLog.logI(getClass().getSimpleName() + " - parameters=" + parameters);
      response = httpclient.execute(httppost);
      responseString = EntityUtils.toString(response.getEntity());
      Log.d("Token", "token=" + token);
      Log.i(this.getClass().getSimpleName(), responseString);
      statusCode = OK;
    } catch (Exception e) {
      statusCode = ERROR;
      e.printStackTrace();
    }
    return null;
  }
}
