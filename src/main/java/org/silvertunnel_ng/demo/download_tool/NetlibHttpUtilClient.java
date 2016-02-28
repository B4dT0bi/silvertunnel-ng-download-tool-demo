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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.logging.Logger;

import org.silvertunnel_ng.netlib.api.NetLayer;
import org.silvertunnel_ng.netlib.api.util.TcpipNetAddress;
import org.silvertunnel_ng.netlib.layer.tls.TLSNetLayer;
import org.silvertunnel_ng.netlib.util.HttpUtil;

/**
 * Client implementation that uses silvertunnel.org Netlib's {@link NetLayer}
 * directly.
 * 
 * @author hapke
 */
public class NetlibHttpUtilClient implements Client {
	private static final Logger log = Logger.getLogger(HttpServiceImpl.class
			.getName());

	private NetLayer lowerNetLayer;

	/**
	 * Initialize NetlibHttpUtilClient.
	 * 
	 * @param lowerNetLayer
	 *            TCP/IP compatible layer; layer for SSL/TLS/https connections
	 *            will be created inside this class and may not be passed as
	 *            argument here
	 */
	public NetlibHttpUtilClient(NetLayer lowerNetLayer) {
		this.lowerNetLayer = lowerNetLayer;

		log.info("NetlibHttpUtilClient initialized");
	}

	public InputStream download(URI source) throws IOException {
		// prepare parameters
		String protocol = source.getScheme();
		int port = source.getPort();
		NetLayer netLayer = null;
		long timeoutInMs = 300L * 1000L;

		// protocol specific actions
		if ("http".equals(protocol)) {
			if (port == -1) {
				// set default
				port = 80;
			}
			netLayer = lowerNetLayer;
		} else if ("https".equals(protocol)) {
			if (port == -1) {
				// set default
				port = 443;
			}
			netLayer = new TLSNetLayer(lowerNetLayer);
		}

		// action
		log.info("download (start) from source=" + source);
		TcpipNetAddress httpServerNetAddress = new TcpipNetAddress(
				source.getHost(), port);
		String pathOnHttpServer = source.getRawPath();
		if (source.getQuery() != null) {
			pathOnHttpServer += "?" + source.getRawQuery();
		}
		byte[] result = HttpUtil.getInstance().get(netLayer,
				httpServerNetAddress, pathOnHttpServer, timeoutInMs);
		log.info("download (end)   from source=" + source);

		// result as stream
		return new ByteArrayInputStream(result);
	}

}
