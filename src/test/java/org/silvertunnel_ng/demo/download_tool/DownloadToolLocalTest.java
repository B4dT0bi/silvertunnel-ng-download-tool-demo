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

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.silvertunnel_ng.netlib.api.util.TcpipNetAddress;
import org.silvertunnel_ng.netlib.layer.mock.MockNetLayer;
import org.testng.annotations.Test;

/**
 * Test of class DownloadTool.
 * 
 * @author hapke
 */
public class DownloadToolLocalTest {
	private static final Logger log = Logger
			.getLogger(DownloadToolLocalTest.class.getName());

	private static final String MOCK_REMOTE_ADDRESS_STR = "invalidhostname.silvertunnel.org:80";
	private static final TcpipNetAddress MOCK_REMOTE_ADDRESS = new TcpipNetAddress(
			MOCK_REMOTE_ADDRESS_STR);
	private static final String DOWNOAD_CONTENT = "THIS IS MY\nRESULT.";
	private static final String UTF_8 = "UTF-8";
	private static URI TEST_URL;

	static {
		try {
			TEST_URL = new URI("http://" + MOCK_REMOTE_ADDRESS_STR + "/foo/bar");
		} catch (URISyntaxException e) {
			log.log(Level.SEVERE, "initialization failure", e);
		}
	}

	@Test
	public void testApacheHttpComponentsClient() throws Exception {
		// prepare the client
		MockNetLayer lowerNetLayer = createNewMockNetLayer();
		Client client = new ApacheHttpComponentsClient(lowerNetLayer);

		// action
		downloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	@Test
	public void testJavaURLClient() throws Exception {
		// prepare the client
		MockNetLayer lowerNetLayer = createNewMockNetLayer();
		Client client = new JavaURLClient(lowerNetLayer);

		// action
		downloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	@Test
	public void testNetlibHttpUtilClient() throws Exception {
		// prepare the client
		MockNetLayer lowerNetLayer = createNewMockNetLayer();
		Client client = new NetlibHttpUtilClient(lowerNetLayer);

		// action
		downloadAFileAndCheckTheResult(lowerNetLayer, client);
	}

	// /////////////////////////////////////////////////////
	// helper methods
	// /////////////////////////////////////////////////////

	private void downloadAFileAndCheckTheResult(MockNetLayer lowerNetLayer,
			Client client) throws IOException {
		// download a file
		File downloadFile = File.createTempFile("ToolLocalTest_", ".bin");
		downloadFile.deleteOnExit();
		DownloadTool.downloadOneURL(client, TEST_URL, downloadFile);

		// check the result
		checkHttpRequest(lowerNetLayer);
		checkDownloadedFile(downloadFile);
	}

	private MockNetLayer createNewMockNetLayer() {
		final byte[] RESPONSE = ("HTTP/1.1 200 OK\n"
				+ "Server: Apache/1.3.29 (Unix) ODER SO ETWAS\n"
				+ "Content-Language: de\n" + "Content-Type: text/html\n" +
				// "Content-Length: "+(DOWNOAD_CONTENT.length())+"\n"+
				"Connection: close\n" + "\n" + DOWNOAD_CONTENT).getBytes();
		final boolean ALLOW_MULTIPLE_SESSIONS = false;
		final long WAIT_AT_END_OF_RESPONSE_BEFORE_CLOSING_MS = 100;
		return new MockNetLayer(RESPONSE, ALLOW_MULTIPLE_SESSIONS,
				WAIT_AT_END_OF_RESPONSE_BEFORE_CLOSING_MS);
	}

	private void checkHttpRequest(MockNetLayer mockNetLayer) {
		assertNotNull("wrong first session is null, but not expected",
				mockNetLayer.getFirstSessionHistory());
		TcpipNetAddress providedRemoteAddress = (TcpipNetAddress) mockNetLayer
				.getFirstSessionHistory().getProvidedRemoteAddress();
		assertEquals("Wrong request remote port",
				MOCK_REMOTE_ADDRESS.getPort(), providedRemoteAddress.getPort());
		assertEquals("Wrong request remote hostname",
				MOCK_REMOTE_ADDRESS.getHostname(),
				providedRemoteAddress.getHostname());
		assertNull("DNS resulution occured but is not allowed here",
				MOCK_REMOTE_ADDRESS.getIpaddress());
	}

	private void checkDownloadedFile(File downloadedFile) throws IOException {
		// check file properties
		assertTrue("Downloaded file is not a regular file, but expected to be",
				downloadedFile.isFile());
		assertEquals("Downloaded file has wrong size",
				DOWNOAD_CONTENT.length(), downloadedFile.length());

		// check content of the file
		InputStream is = new FileInputStream(downloadedFile);
		byte[] buffer = new byte[DOWNOAD_CONTENT.length()];
		is.read(buffer);
		assertEquals("Downloaded file has content", DOWNOAD_CONTENT,
				new String(buffer, UTF_8));
	}
}
