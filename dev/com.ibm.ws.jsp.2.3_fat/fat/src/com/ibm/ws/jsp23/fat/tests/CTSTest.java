/*******************************************************************************
 * Copyright (c) 2013, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsp23.fat.tests;

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.jsp23.fat.JSPUtils;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import componenttest.annotation.Server;
import componenttest.annotation.SkipForRepeat;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.topology.impl.LibertyServer;

/**
 * Tests to execute on the jspServer that use HttpUnit/HttpClient
 */

@SkipForRepeat("CDI-2.0")
@RunWith(FATRunner.class)
public class CTSTest {
    private static final Logger LOG = Logger.getLogger(JSPTests.class.getName());
    private static final String APP_NAME = "TestJSPVersion";

    @Server("jspServer")
    public static LibertyServer server;

    @BeforeClass
    public static void setup() throws Exception {
        ShrinkHelper.defaultDropinApp(server,
                                      APP_NAME + ".war",
                                      "com.sun.ts.tests.jsp.spec.el.jsp");

        server.startServer(CTSTest.class.getSimpleName() + ".log");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        if (server != null && server.isStarted()) {
            server.stopServer();
        }
    }

    /**
     * A sample HttpUnit test case for JSP. Just ensure that the basic application is reachable.
     *
     * @throws Exception
     */
    @Test
    public void ctsTest() throws Exception {
        WebConversation wc = new WebConversation();
        wc.setExceptionsThrownOnErrorStatus(false);

        String url = JSPUtils.createHttpUrlString(server, APP_NAME, "TestJSPVersion.jsp");
        LOG.info("url: " + url);

        WebRequest request = new GetMethodWebRequest(url);
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());

        //assertEquals("Expected " + 200 + " status code was not returned!",
        //             200, response.getResponseCode());
        //assertTrue("The response did not contain: Hello World", response.getText().contains("Hello World"));
    }
}
