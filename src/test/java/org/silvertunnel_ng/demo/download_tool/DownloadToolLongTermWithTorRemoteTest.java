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

import java.io.StringReader;
import java.util.concurrent.atomic.AtomicInteger;

import org.testng.annotations.Test;

/**
 * Long term test of class DownloadTool.
 * 
 * This test case is important to test Netlib internals long term reliance.
 * 
 * @author hapke
 */
public class DownloadToolLongTermWithTorRemoteTest {

	// /////////////////////////////////////////////////////
	// plain (non-Tor) test cases
	// /////////////////////////////////////////////////////
	private static final String DOWNLOAD_CONFIG = "http://httptest.silvertunnel_ng.org/httptest/bigtest.php?id=DownloadToolLongTermWithTorRemoteTest1 testcase_file01.bin 30000\n"
			+ "http://httptest.silvertunnel_ng.org/httptest/bigtest.php?id=DownloadToolLongTermWithTorRemoteTest2 testcase_file02.bin 30000\n"
			+ "http://httptest.silvertunnel_ng.org/httptest/bigtest.php?id=DownloadToolLongTermWithTorRemoteTest3 testcase_file03.bin 30000\n"
			+ "http://httptest.silvertunnel_ng.org/httptest/bigtest.php?id=DownloadToolLongTermWithTorRemoteTest4 testcase_file04.bin 30000\n"
			+ "http://httptest.silvertunnel_ng.org/httptest/bigtest.php?id=DownloadToolLongTermWithTorRemoteTest5 testcase_file05.bin 30000";
	private static final int NUM_OF_FILES2DOWNLOAD = 5;

	/**
	 * Test whether the successive download of five files works.
	 * 
	 * This test case is important to test Netlib internals long term reliance.
	 */
	@Test(timeOut = 300000)
	public void testPlainApacheHttpComponentsClientHttp() throws Exception {
		AtomicInteger downloadCounter = new AtomicInteger(0);
		String downloadDir = System.getProperty("java.io.tmpdir");
		boolean useJavaURLClient = false;

		// action
		DownloadTool.downloadMultipleURLs(downloadDir, useJavaURLClient,
				downloadCounter, new StringReader(DOWNLOAD_CONFIG));

		// check result
		assertEquals("wrong number of files downloaded", NUM_OF_FILES2DOWNLOAD,
				downloadCounter.intValue());
	}
}
