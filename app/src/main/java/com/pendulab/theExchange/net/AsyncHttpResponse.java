/*
 * Name: $RCSfile: AsyncHttpPostListener.java,v $
 * Version: $Revision: 1.1 $
 * Date: $Date: Apr 21, 2011 2:53:19 PM $
 *
 * Copyright (C) 2011 COMPANY NAME, Inc. All rights reserved.
 */

package com.pendulab.theExchange.net;

/**
 * AsyncHttpResponse interface
 */
public interface AsyncHttpResponse {

  void before();

  void after(int statusCode, String response);

}
