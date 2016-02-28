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

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.logging.Logger;

import org.silvertunnel_ng.netlib.adapter.url.NetlibURLStreamHandlerFactory;
import org.silvertunnel_ng.netlib.api.NetLayer;

/**
 * Client implementation that uses java.net.URL.
 * 
 * @author hapke
 */
public class JavaURLClient implements Client {
    private static final Logger log = Logger.getLogger(HttpServiceImpl.class.getName());
    
    
    private NetlibURLStreamHandlerFactory factory;
    
    /**
     * Initialize JavaURLClient
     * 
     * @param lowerNetLayer    TCP/IP compatible layer;
     *                         layer for SSL/TLS/https connections
     *                         will be created inside this class
     *                         and may not be passed as argument here 
     */
    public JavaURLClient(NetLayer lowerNetLayer) {
        // prepare URL handling on top of the lowerNetLayer
        factory = new NetlibURLStreamHandlerFactory(false);
        // the following method could be called multiple times
        // to change layer used by the factory over the time:
        factory.setNetLayerForHttpHttpsFtp(lowerNetLayer);

        log.info("JavaURLClient with NetlibURLStreamHandlerFactory initialized");
    }
    
    public InputStream download(URI source) throws IOException {
        // create the suitable URL object
        URLStreamHandler handler = factory.createURLStreamHandler(source.getScheme());
        URL context = null;
        URL url = new URL(context, source.toString(), handler);
 
        // send request with GET (?? TODO GET ??) data
        log.info("download (start) from source="+source);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setDoInput(true);
        urlConnection.setUseCaches(false);
        urlConnection.setAllowUserInteraction(false);
        urlConnection.setRequestProperty("Content-type", "text/xml; charset=" + "UTF-8");
        urlConnection.connect();

        // receive the response
        InputStream responseBodyIS = urlConnection.getInputStream();
        log.info("download (end)   from source="+source);
        return responseBodyIS;
    }
}
