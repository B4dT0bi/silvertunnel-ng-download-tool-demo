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

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Logger;

import org.silvertunnel_ng.netlib.api.NetFactory;
import org.silvertunnel_ng.netlib.api.NetLayer;
import org.silvertunnel_ng.netlib.api.NetLayerIDs;
import org.silvertunnel_ng.netlib.layer.tor.TorNetLayer;
import org.testng.annotations.Test;

/**
 * Test of class Tool.
 * 
 * @author hapke
 */
public class DownloadToolWithTorRemoteTest {
	private static final Logger log = Logger
			.getLogger(DownloadToolWithTorRemoteTest.class.getName());

	private static final String URI_PLAIN_HTTP_BASE_STR = "http://httptest.silvertunnel-ng.org/httptest/bigtest.php?id=";
	private static final String URI_TOR_HTTP_STR = "http://torcheck.xenobite.eu/";
	private static final String URI_HTTPS_STR = "https://www.torproject.org/";
	private static final String UTF_8 = "UTF-8";

	private static final NetLayerIDs PLAIN_LOWER_NET_LAYER_ID = NetLayerIDs.TCPIP;
	private static final NetLayerIDs TOR_LOWER_NET_LAYER_ID = NetLayerIDs.TOR;

	// /////////////////////////////////////////////////////
	// plain (non-Tor) test cases
	// /////////////////////////////////////////////////////

	@Test(timeOut = 5000)
	public void testPlainApacheHttpComponentsClientHttp() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				PLAIN_LOWER_NET_LAYER_ID);
		Client client = new ApacheHttpComponentsClient(lowerNetLayer);

		// action
		plainHttpDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	@Test(timeOut = 5000)
	public void testPlainJavaURLClientHttp() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				PLAIN_LOWER_NET_LAYER_ID);
		Client client = new JavaURLClient(lowerNetLayer);

		// action
		plainHttpDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	@Test(timeOut = 5000)
	public void testPlainNetlibHttpUtilClientHttp() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				PLAIN_LOWER_NET_LAYER_ID);
		Client client = new NetlibHttpUtilClient(lowerNetLayer);

		// action
		plainHttpDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// @Ignore("HTTPS is currently not working")
	@Test(timeOut = 5000, enabled = false)
	public void testPlainApacheHttpComponentsClientHttps() throws Exception {

		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				PLAIN_LOWER_NET_LAYER_ID);
		Client client = new ApacheHttpComponentsClient(lowerNetLayer);

		// action
		httpsDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// @Ignore("HTTPS is currently not working")
	@Test(timeOut = 5000, enabled = false)
	public void testPlainJavaURLClientHttps() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				PLAIN_LOWER_NET_LAYER_ID);
		Client client = new JavaURLClient(lowerNetLayer);

		// action
		httpsDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// @Ignore("HTTPS is currently not working")
	@Test(timeOut = 5000, enabled = false)
	public void testPlainNetlibHttpUtilClientHttps() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				PLAIN_LOWER_NET_LAYER_ID);
		Client client = new NetlibHttpUtilClient(lowerNetLayer);

		// action
		httpsDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// /////////////////////////////////////////////////////
	// plain (non-Tor) test cases
	// /////////////////////////////////////////////////////

	@Test(timeOut = 120000)
	public void testInitTor() throws Exception {
		NetLayer lowerNetLayer = (TorNetLayer) NetFactory.getInstance()
				.getNetLayerById(TOR_LOWER_NET_LAYER_ID);
		assertNotNull("Did not get a Tor lowerNetLayer", lowerNetLayer);
		lowerNetLayer.waitUntilReady();
	}

	@Test(timeOut = 20000)
	public void testTorApacheHttpComponentsClientHttp() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				TOR_LOWER_NET_LAYER_ID);
		Client client = new ApacheHttpComponentsClient(lowerNetLayer);

		// action
		torHttpDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	@Test(timeOut = 20000)
	public void testTorJavaURLClientHttp() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				TOR_LOWER_NET_LAYER_ID);
		Client client = new JavaURLClient(lowerNetLayer);

		// action
		torHttpDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	@Test(timeOut = 120000, enabled = false)
	// @Ignore("not working but not important")
	public void testTorNetlibHttpUtilClientHttp() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				TOR_LOWER_NET_LAYER_ID);
		Client client = new NetlibHttpUtilClient(lowerNetLayer);

		// action
		torHttpDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// @Ignore("HTTPS is currently not working")
	@Test(timeOut = 20000, enabled = false)
	public void testTorApacheHttpComponentsClientHttps() throws Exception {

		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				TOR_LOWER_NET_LAYER_ID);
		Client client = new ApacheHttpComponentsClient(lowerNetLayer);

		// action
		httpsDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// @Ignore("HTTPS is currently not working")
	@Test(timeOut = 20000, enabled = false)
	public void testTorJavaURLClientHttps() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				TOR_LOWER_NET_LAYER_ID);
		Client client = new JavaURLClient(lowerNetLayer);

		// action
		httpsDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// @Ignore("HTTPS is currently not working")
	@Test(timeOut = 20000, enabled = false)
	public void testTorNetlibHttpUtilClientHttps() throws Exception {
		// prepare the client
		NetLayer lowerNetLayer = NetFactory.getInstance().getNetLayerById(
				TOR_LOWER_NET_LAYER_ID);
		Client client = new NetlibHttpUtilClient(lowerNetLayer);

		// action
		httpsDownloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// /////////////////////////////////////////////////////
	// helper methods
	// /////////////////////////////////////////////////////

	private void plainHttpDownloadAFileAndCheckTheResult(
			NetLayer lowerNetLayer, Client client) throws IOException,
			URISyntaxException {
		// download a file
		String randomId = "" + ((int) (Math.random() * 1000000));
		File downloadFile = File.createTempFile("ToolLocalTest_", ".bin");
		downloadFile.deleteOnExit();
		URI url = new URI(URI_PLAIN_HTTP_BASE_STR + randomId);
		DownloadTool.downloadOneURL(client, url, downloadFile);

		// check the result
		final int MIN_FILE_SIZE = 10;
		final int MAX_FILE_SIZE = 10 * 1000;
		String containedString = "<id>" + randomId + "</id>";
		checkDownloadedFile(downloadFile, MIN_FILE_SIZE, MAX_FILE_SIZE,
				containedString);
	}

	private void torHttpDownloadAFileAndCheckTheResult(NetLayer lowerNetLayer,
			Client client) throws IOException, URISyntaxException {
		// download a file
		File downloadFile = File.createTempFile("ToolLocalTest_", ".bin");
		downloadFile.deleteOnExit();
		URI url = new URI(URI_TOR_HTTP_STR);
		DownloadTool.downloadOneURL(client, url, downloadFile);

		// check the result
		final int MIN_FILE_SIZE = 1000;
		final int MAX_FILE_SIZE = 10 * 1000 * 1000;
		String containedString = "Your IP is identified to be a Tor-";
		checkDownloadedFile(downloadFile, MIN_FILE_SIZE, MAX_FILE_SIZE,
				containedString);
	}

	private void httpsDownloadAFileAndCheckTheResult(NetLayer lowerNetLayer,
			Client client) throws IOException, URISyntaxException {
		// download a file
		File downloadFile = File.createTempFile("ToolLocalTest_", ".bin");
		downloadFile.deleteOnExit();
		URI url = new URI(URI_HTTPS_STR);
		DownloadTool.downloadOneURL(client, url, downloadFile);

		// check the result
		final int MIN_FILE_SIZE = 1000;
		final int MAX_FILE_SIZE = 10 * 1000 * 1000;
		String containedString = "Tor: anonymity online";
		checkDownloadedFile(downloadFile, MIN_FILE_SIZE, MAX_FILE_SIZE,
				containedString);
	}

	private void checkDownloadedFile(File downloadedFile, int minimumSize,
			int maximumSize, String containedString) throws IOException {
		// check file properties
		assertTrue("Downloaded file is not a regular file, but expected to be",
				downloadedFile.isFile());
		if (downloadedFile.length() < minimumSize
				|| downloadedFile.length() > maximumSize) {
			fail("Downloaded file is too small or too big with size="
					+ downloadedFile.length());
		}

		// check content of the file
		InputStream is = new FileInputStream(downloadedFile);
		byte[] buffer = new byte[(int) downloadedFile.length()];
		is.read(buffer);
		String fileContent = new String(buffer, UTF_8);
		if (!fileContent.contains(containedString)) {
			fail("Downloaded file has wrong content=" + fileContent);
		}
	}
}
