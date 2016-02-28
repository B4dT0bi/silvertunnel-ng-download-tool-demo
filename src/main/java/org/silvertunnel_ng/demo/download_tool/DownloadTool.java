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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.URI;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.silvertunnel_ng.netlib.api.NetFactory;
import org.silvertunnel_ng.netlib.api.NetLayer;
import org.silvertunnel_ng.netlib.api.NetLayerIDs;

/**
 * Demo application: download one or more files via HTTP through the Tor
 * anonymity network using silvertunnel.org Netlib.
 * 
 * @author hapke
 */
public class DownloadTool {
	private static final Logger log = Logger.getLogger(DownloadTool.class
			.getName());

	/**
	 * Download one or more files via HTTP through the Tor anonymity network.
	 * 
	 * The URLs that are intended to download are read from standard input in
	 * the following format (one URL per line): [URL to download] [filename on
	 * local file system; optional] [waiting time after download; optional]
	 * 
	 * HINT: Currently, only the HTTP protocol is supported, FTP or HTTPS do not
	 * work!
	 * 
	 * @param args
	 *            Global settings are provided as command line arguments:
	 *            [download dir] [download technology]: "apachehttpclient" =
	 *            Apache HTTP Client 4.0 with USER_AGENT Mozilla/5.0 "urlclient"
	 *            = java.net.URL client with empty USER_AGENT (optional; default
	 *            is "httpclient")
	 * 
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		AtomicInteger downloadCounter = new AtomicInteger(0);

		try {
			//
			// read command line arguments
			//
			if (args.length < 1) {
				log.severe("Wrong number of command line arguments");
				System.exit(1);
			}
			String downloadDir = args[0]; // System.getProperty("java.io.tmpdir")
			boolean useJavaURLClient = args.length >= 2
					&& "urlclient".equals(args[1]);

			// action
			downloadMultipleURLs(downloadDir, useJavaURLClient,
					downloadCounter, new InputStreamReader(System.in));

		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			log.info("Downloaded " + downloadCounter + " URLs");

			// do not wait for remaining threads
			System.exit(0);
		}
	}

	/**
	 * Do the work
	 * 
	 * @param downloadDir
	 * @param useJavaURLClient
	 * @param downloadCounter
	 * @param input
	 *            input stream with download parameters (one line for each
	 *            download)
	 * @throws IOException
	 */
	protected static void downloadMultipleURLs(String downloadDir,
			boolean useJavaURLClient, AtomicInteger downloadCounter,
			Reader input) throws IOException {
		//
		// initialize network
		//
		Client client;
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				NetLayerIDs.TOR);
		lowerNetLayer.waitUntilReady();
		if (useJavaURLClient) {
			client = new JavaURLClient(lowerNetLayer);
		} else {
			client = new ApacheHttpComponentsClient(lowerNetLayer);
		}

		// read URLs from standard input - and download them
		String line;
		BufferedReader in = new BufferedReader(input);
		for (int lineCounter = 1; (line = in.readLine()) != null; lineCounter++) {
			//
			// read a line: read one URL and its two optional parameters
			//
			if (line.length() == 0) {
				// ignore empty lines
				continue;
			}
			if (line.startsWith("#")) {
				// ignore comment lines
				continue;
			}
			String[] lineParts = line.split(" ");
			String url = lineParts[0];
			String localFileName;
			long waitingAfterDownloadMs = 0L;
			if (lineParts.length >= 2) {
				localFileName = lineParts[1];
			} else {
				localFileName = "downloaded_file_" + lineCounter + ".bin";
			}
			if (lineParts.length >= 3) {
				try {
					waitingAfterDownloadMs = Long.parseLong(lineParts[2]);
				} catch (NumberFormatException e) {
					log.warning("Wrong waiting time value (not a number) for url="
							+ url);
				}
			}

			//
			// download one URL
			//
			URI uri;
			try {
				uri = new URI(url);
				File file = new File(downloadDir, localFileName);
				boolean success = downloadOneURL(client, uri, file);
				if (success) {
					downloadCounter.addAndGet(1);
				}

				//
				// wait after download of the URL
				//
				try {
					Thread.sleep(waitingAfterDownloadMs);
				} catch (InterruptedException e) {
					// ignore this
				}

			} catch (Exception e1) {
				log.warning("Failure while downloading url=" + url);
			}
		}
	}

	/**
	 * 
	 * @param client
	 * @param source
	 * @param destination
	 * @return true=success
	 */
	protected static boolean downloadOneURL(Client client, URI source,
			File destination) {
		try {
			InputStream response = client.download(source);
			writeToFile(response, destination);

			// successful
			return true;

		} catch (Exception e) {
			e.printStackTrace();
			// not successful
			return false;
		}
	}

	/**
	 * @param source
	 *            read from source
	 * @param destination
	 *            write to destination
	 * @return number of bytes written to the file
	 */
	protected static long writeToFile(InputStream source, File destination)
			throws IOException {
		OutputStream out = new FileOutputStream(destination);

		// copy InputStream to OutputStream
		final int BUFFER_SIZE = 1 * 1024;
		InputStream in = source;
		byte[] buffer = new byte[BUFFER_SIZE];
		int c;
		long counter = 0;
		while ((c = in.read(buffer)) != -1) {
			out.write(buffer, 0, c);
			counter += c;
		}

		// number of bytes written to the file
		return counter;
	}
}
