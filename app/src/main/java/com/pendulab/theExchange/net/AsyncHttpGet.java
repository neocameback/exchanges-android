/*
 * Name: $RCSfile: AsyncHttpGet.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: May 12, 2011 2:38:36 PM $
 *
 * Copyright (C) 2011 COMPANY NAME, Inc. All rights reserved.
 */

package com.pendulab.theExchange.net;

import com.pendulab.theExchange.config.WebServiceConfig;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

import java.util.Iterator;
import java.util.List;

/**
 * @author cuongvm6037
 */
public class AsyncHttpGet extends NetBase {
  /**
   * Constructor
   */
  public AsyncHttpGet(Context context, AsyncHttpResponse listener,
                      List<NameValuePair> parameters) {
    super(context, listener, parameters);
  }

  public AsyncHttpGet(Context context, String token, AsyncHttpResponse listener,
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
      if (parameters != null) {
        Iterator<NameValuePair> it = parameters.iterator();
        while (it.hasNext()) {
          NameValuePair nv = it.next();
          params.setParameter(nv.getName(), nv.getValue());
        }
      }
      HttpConnectionParams.setConnectionTimeout(params, TIME_OUT);
      HttpConnectionParams.setSoTimeout(params, TIME_OUT);
      HttpClient httpclient = createHttpClient(url, params);
      HttpGet httpget = new HttpGet(url);
      if (token != null) {
        httpget.setHeader(WebServiceConfig.KEY_HEADER,
            "token=" + token);
      }
      Log.d("AsyncHttpGet", url);
      Log.d("Token", "token=" + token);
      response = httpclient.execute(httpget);
      responseString = EntityUtils.toString(response.getEntity());
      Log.d("AsyncHttpGet", responseString);
      statusCode = OK;
    } catch (Exception e) {
      statusCode = ERROR;
      e.printStackTrace();
    }
    return null;
  }
}
