/*
 * silvertunnel.org Demo - Java example applications accessing anonymity networks
 * Copyright (c) 2009-2012 silvertunnel.org
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package org.silvertunnel_ng.demo.download_tool;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpService;
import org.apache.http.util.EntityUtils;

/**
 * 
 * @author hapke
 */
public class HttpServiceImpl {
	private static final Logger logger = Logger.getLogger(HttpServiceImpl.class
			.getName());

	private static final String USER_AGENT = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)";

	/**
	 * use the same HTTPClient for multiple requests to one site to allow cookie
	 * handling
	 */
	private HttpClient httpClient;

	private SchemeRegistry schemeRegistry;

	public HttpServiceImpl() {
		try {
			// Register the "http" protocol scheme, it is required
			// by the default operator to look up socket factories.
			schemeRegistry = new SchemeRegistry();
			SocketFactory sf = PlainSocketFactory.getSocketFactory();
			final int PORT_80 = 80;
			schemeRegistry.register(new Scheme("http", sf, PORT_80));

			initHttpClient();

		} catch (Throwable t) {
			logger.log(Level.SEVERE, "Cannot init HttpServiceImpl", t);
		}
	}

	/**
	 * @see HttpService#initHttpClient()
	 */
	public void initHttpClient() {
		try {
			// prepare parameters
			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, "UTF-8");

			// create http client
			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, schemeRegistry);
			httpClient = new DefaultHttpClient(ccm, params);

			// user agent settings
			if (USER_AGENT != null) {
				HttpProtocolParams.setUserAgent(httpClient.getParams(),
						USER_AGENT);
			}

		} catch (Throwable t) {
			logger.log(Level.WARNING, "initHttpClient()", t);
		}
	}

	/**
	 * @see HttpService#getFromURL(String)
	 */
	public String getFromURL(String url) {
		try {
			// the request URL
			// final HttpHost requestedHost = new HttpHost("google.de", 80,
			// "http");
			// HttpRequest request = new BasicHttpRequest("GET", "/",
			// HttpVersion.HTTP_1_1);

			// send request and receive response
			HttpGet httpGet = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpGet);

			// read the response
			HttpEntity responseEntity = response.getEntity();
			String result = EntityUtils.toString(responseEntity);

			/*
			 * Header[] headers = httpGet.getAllHeaders(); for (Header h:
			 * headers) { if (h.getName().toLowerCase().contains("cookie")) {
			 * logger.info("    Request header:"+h.toString()); } } headers =
			 * response.getAllHeaders(); for (Header h: headers) { if
			 * (h.getName().toLowerCase().contains("cookie")) {
			 * logger.info("    Response header:"+h.toString()); } }
			 */

			return result;
		} catch (Throwable t) {
			logger.log(Level.WARNING, "Cannot load url=" + url, t);
			return null;
		}
	}
}
