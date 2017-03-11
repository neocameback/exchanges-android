/**
 *
 */
package com.pendulab.theExchange.net;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Base class to connect to server
 *
 * @author cuongvm6037
 */
public class NetBase extends AsyncTask<String, Integer, String> {
  public static final int NETWORK_OFF = 0;
  public static final int ERROR = 1;
  public static final int OK = 2;
  public static final int TIME_OUT = 30000; // 60 seconds

  protected Context context;
  protected AsyncHttpResponse listener;
  protected List<NameValuePair> parameters;
  protected String token;
  protected HttpResponse response;
  protected String responseString;
  protected int statusCode;

  /**
   * Constructor
   */
  public NetBase(Context context, AsyncHttpResponse listener,
                 List<NameValuePair> parameters) {
    this.context = context;
    this.listener = listener;
    this.parameters = parameters;
  }

  public NetBase(Context context, AsyncHttpResponse listener, String token,
                 List<NameValuePair> parameters) {
    this.context = context;
    this.listener = listener;
    this.token = token;
    this.parameters = parameters;
  }

  /**
   * Trust every server - dont check for any certificate
   */
  private static void trustAllHosts() {
    // Create a trust manager that does not validate certificate chains
    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
      public X509Certificate[] getAcceptedIssuers() {
        return new X509Certificate[]{};
      }

      public void checkClientTrusted(X509Certificate[] chain,
                                     String authType) throws CertificateException {
      }

      public void checkServerTrusted(X509Certificate[] chain,
                                     String authType) throws CertificateException {
      }
    }};

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("TLS");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Open HTTPS connection. Use this method to setup and accept all SSL
   * certificates from HTTPS protocol.
   */
  public static HttpsURLConnection openSConnection(String url)
      throws IOException {
    URL theURL = new URL(url);
    trustAllHosts();
    HttpsURLConnection https = (HttpsURLConnection) theURL.openConnection();
    https.setHostnameVerifier(new HostnameVerifier() {
      public boolean verify(String hostname, SSLSession session) {
        return true;
      }
    });
    return https;
  }

  /**
   * Open HTTP connection
   */
  public static HttpURLConnection openConnection(String url)
      throws IOException {
    URL theURL = new URL(url);
    return (HttpURLConnection) theURL.openConnection();
  }

  /**
   * Create HttpClient based on HTTP or HTTPS protocol that is parsed from url
   * parameter. With HTTPS protocol, we accept all SSL certificates.
   */
  protected HttpClient createHttpClient(String url, HttpParams params) {
    if ((url.toLowerCase().startsWith("https"))) { // HTTPS
      try {
        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        trustStore.load(null, null);

        SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
        sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http",
            PlainSocketFactory.getSocketFactory(), 80));
        registry.register(new Scheme("https", sf, 443));

        ClientConnectionManager ccm = new ThreadSafeClientConnManager(
            params, registry);

        return new DefaultHttpClient(ccm, params);
      } catch (Exception e) {
        return new DefaultHttpClient(params);
      }
    } else { // HTTP
      return new DefaultHttpClient(params);
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see android.os.AsyncTask#onPreExecute()
   */
  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    listener.before();
  }

  /*
   * (non-Javadoc)
   *
   * @see android.os.AsyncTask#doInBackground(Params[])
   */
  @Override
  protected String doInBackground(String... args) {
    if (isNetworkAvailable()) {
      return request(args[0]);
    } else {
      statusCode = NETWORK_OFF;
      return null;
    }
  }

  /*
   * (non-Javadoc)
   *
   * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
   */
  @Override
  protected void onPostExecute(String result) {
    listener.after(statusCode, responseString);
  }

  /**
   * Send request to server
   */
  protected String request(String url) {
    return null;
  }

  /**
   * Check network connection
   */
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

}
