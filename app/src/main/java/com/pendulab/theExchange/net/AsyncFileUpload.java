/**
 *
 */
package com.pendulab.theExchange.net;

import com.pendulab.theExchange.utils.SmartLog;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * @author cuongvm6037
 */
public class AsyncFileUpload extends NetBase {
  protected List<NameValuePair> files;
  protected List<NameValuePair> headers;

  /**
   * Constructor
   */
  public AsyncFileUpload(Context context, AsyncHttpResponse listener,
                         List<NameValuePair> parameters, List<NameValuePair> files) {
    super(context, listener, parameters);
    this.files = files;
  }

  /**
   * Constructor
   */
  public AsyncFileUpload(Context context, AsyncHttpResponse listener,
                         List<NameValuePair> parameters, List<NameValuePair> files,
                         List<NameValuePair> headers) {
    super(context, listener, parameters);
    this.files = files;
    this.headers = headers;
  }

  /* (non-Javadoc)
   * @see com.bestfashionfriend.lib.net.NetBase#request(java.lang.String)
   */
  @Override
  protected String request(String url) {
    try {
      SmartLog.logD("url------" + url);
      HttpParams params = new BasicHttpParams();
      HttpConnectionParams.setConnectionTimeout(params, TIME_OUT);
      HttpConnectionParams.setSoTimeout(params, TIME_OUT);
      HttpClient httpclient = createHttpClient(url, params);
      MultipartEntity entity = new MultipartEntity();

      // Set parameters to form
      if (parameters != null) {
        Iterator<NameValuePair> pit = parameters.iterator();
        while (pit.hasNext()) {
          NameValuePair nv = pit.next();
          entity.addPart(nv.getName(), new StringBody(nv.getValue(), Charset.forName("UTF-8")));

        }
      }

      // Set upload files to form
      if (files != null) {
        Iterator<NameValuePair> fit = files.iterator();
        while (fit.hasNext()) {
          NameValuePair nv = fit.next();
          entity.addPart(nv.getName(), new FileBody(new File(
              nv.getValue())));
        }
      }

      HttpPost httppost = new HttpPost(url);
      httppost.setEntity(entity);

      if (headers != null) {
        Iterator<NameValuePair> it = headers.iterator();
        while (it.hasNext()) {
          NameValuePair nv = it.next();
          httppost.setHeader(nv.getName(), nv.getValue());
        }
      }
      response = httpclient.execute(httppost);

      SmartLog.logD("response - response.getStatusLine():" + response.getStatusLine());
      statusCode = OK;
    } catch (Exception e) {
      statusCode = ERROR;
      e.printStackTrace();
    }
    return null;
  }
}
