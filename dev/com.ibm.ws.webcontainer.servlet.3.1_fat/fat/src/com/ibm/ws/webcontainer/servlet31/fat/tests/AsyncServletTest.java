/*******************************************************************************
 * Copyright (c) 2013, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.webcontainer.servlet31.fat.tests;

import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import com.ibm.ws.fat.util.LoggingTest;
import com.ibm.ws.fat.util.SharedServer;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

/**
 * Tests to execute on the wcServer that use HttpUnit.
 */
@MinimumJavaLevel(javaLevel = 7)
public class AsyncServletTest extends LoggingTest {
    private static final Logger LOG = Logger.getLogger(AsyncServletTest.class.getName());
    protected static final Map<String, String> testUrlMap = new HashMap<String, String>();

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("servlet31_asyncServlet");

    @AfterClass
    public static void testCleanup() throws Exception {
        // test cleanup
    }

    @Before
    public void before() {
        // PAN: TODO
        // SHARED_SERVER.setExpectedErrors("SRVE8015E:.*");
    }

    @Test
    @Mode(TestMode.LITE)
    public void testAsyncFowardTest() throws Exception {
        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncFowardTest ");
        LOG.info("\n /************************************************************************************/");

        testAsync("None", "*** Async after forward. ***", null, true);
    }

    @Test
    @Mode(TestMode.LITE)
    public void testAsyncGetReqRespInDispatcherAfterStartAsync() throws Exception {
        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncGetReqRespValid1 ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In dispatcher, after req.startAsync() : getRequest worked fine ***";
        String expect2 = "*** In dispatcher, after req.startAsync() : getResponse worked fine ***";
        testAsync("GRRinFwd", expect1, expect2, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testAsyncGetReqRespInDispatcherAfterDisp() throws Exception {

        // Make sure the test framework knows that SRVE9015E is expected
        // PAN: TODO
        //SHARED_SERVER.setExpectedErrors("SRVE9015E:.*");

        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | GetReqRespAfterDisp ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In dispatcher, after ac.dispatch() : getRequest threw an IllegalStateException ***";
        String expect2 = "*** In dispatcher, after ac.dispatch() : getResponse threw an IllegalStateException ***";
        testAsync("GRRafterDisp", expect1, expect2, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testAsyncGetReqRespInDispatchedBeforeComplete() throws Exception {
        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncGetReqRespValid2 ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In dispatched, before ac.complete() : getRequest worked fine ***";
        String expect2 = "*** In dispatched, before ac.complete() : getResponse worked fine ***";
        testAsync("GRRbeforeComp", expect1, expect2, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testAsyncGetReqRespInDispatchedAfterComplete() throws Exception {
        // Make sure the test framework knows that SRVE9015E is expected
        // PAN: TODO
        //SHARED_SERVER.setExpectedErrors("SRVE9015E:.*");

        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncGetReqRespValid2 ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In dispatched, after ac.complete() : getRequest threw an IllegalStateException ***";
        String expect2 = "*** In dispatched, after ac.complete() : getResponse threw an IllegalStateException ***";
        testAsync("GRRafterComp", expect1, expect2, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testAsyncGetReqRespInListenerAfterComplete() throws Exception {
        // Make sure the test framework knows that SRVE9015E is expected
        // PAN: TODO
        //SHARED_SERVER.setExpectedErrors("SRVE9015E:.*");

        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncGetReqRespValid2 ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In AyncListener, onComplete : getRequest threw an IllegalStateException ***";
        String expect2 = "*** In AyncListener, onComplete : getResponse threw an IllegalStateException ***";
        testAsync("GRRinLstnr", expect1, expect2, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testIsAsyncStartedInDispatcherAfterStartAsync() throws Exception {
        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncGetReqRespValid1 ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In dispatcher, after req.startAsync() : isAsyncStarted = true ***";
        testAsync("isAsyncinFwd", expect1, null, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testIsAsyncStartedInDispatcherAfterDisp() throws Exception {
        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | GetReqRespAfterDisp ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In dispatcher, after ac.dispatch() : isAsyncStarted = true ***";
        testAsync("isAsyncafterDisp", expect1, null, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testIsAsyncStartedInDispatchedBeforeComplete() throws Exception {
        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncGetReqRespValid2 ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In dispatched, before ac.complete() : isAsyncStarted = false ***";
        testAsync("isAsyncbeforeComp", expect1, null, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testIsAsyncStartedInDispatchedAfterComplete() throws Exception {
        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncGetReqRespValid2 ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In dispatched, after ac.complete() : isAsyncStarted = false ***";
        testAsync("isAsyncafterComp", expect1, null, true);

    }

    @Test
    @Mode(TestMode.LITE)
    public void testIsAsyncInListenerAfterComplete() throws Exception {
        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncGetReqRespValid2 ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** In AyncListener, onComplete : isAsyncStarted = false ***";
        testAsync("isAsyncinLstnr", expect1, null, true);

    }

    // Test designed to ensure that the a dispatch to a jsp works.
    @Test
    @Mode(TestMode.LITE)
    public void testAsyncDispatchToJSP() throws Exception {

        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncDispatchToJSP ");
        LOG.info("\n /************************************************************************************/");

        String expect1 = "*** AsyncDispatch.jsp Dispatch type: ASYNC ***";
        testAsync("doDispatch", "jsp", expect1, null, true);
    }

    // Test designed to ensure that the response is not committed on return from forward if the forwarded to
    // servlet starts aysnc and starts a runnable.
    @Test
    @Mode(TestMode.LITE)
    public void testAsyncFowardToStart() throws Exception {

        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncFowardToStart ");
        LOG.info("\n /************************************************************************************/");

        String[] expectedStrings = new String[] { "*** AsyncStartServlet context = /AsyncServlet ***",
                                                  "*** AsyncRunnable running ***",
                                                  "*** AsyncRunnable after sleep *** : request.getDispatchType() : FORWARD",
                                                  "*** AsyncRunnable after sleep *** : request.getRequestURI() : /AsyncServlet/AsyncStart/startPathInfo",
                                                  "*** AsyncRunnable after sleep *** : request.getContextPath() : /AsyncServlet",
                                                  "*** AsyncRunnable after sleep *** : request.getServletPath() : /AsyncStart",
                                                  "*** AsyncRunnable after sleep *** : request.getPathInfo() : /startPathInfo",
                                                  "*** AsyncRunnable after sleep *** : request.getQueryString() : time=1000",
                                                  "*** AsyncStartServlet complete async *** : request.getDispatchType() : ASYNC",
                                                  "*** AsyncStartServlet complete async *** : request.getRequestURI() : /AsyncServlet/AsyncStart",
                                                  "*** AsyncStartServlet complete async *** : request.getContextPath() : /AsyncServlet",
                                                  "*** AsyncStartServlet complete async *** : request.getServletPath() : /AsyncStart",
                                                  "*** AsyncStartServlet complete async *** : request.getPathInfo() : null",
                                                  "*** AsyncStartServlet complete async *** : request.getQueryString() : queryString=completeAsync",
                                                  "*** AsyncStartServlet completing Async ***" };

        testAsync("doForward", "Start", true, expectedStrings);
    }

    // Test designed to ensure that the response is not committed on return from forward if the forwarded to
    // servlet dispatches the request.
    @Test
    @Mode(TestMode.LITE)
    public void testAsyncFowardToDispatch() throws Exception {

        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncFowardToStart ");
        LOG.info("\n /************************************************************************************/");

        String[] expectedStrings = new String[] { "*** AsyncDispatchServlet starting Async and dispatch ***",
                                                  "*** AsyncDispatchServlet start async and dispatch *** : request.getDispatchType() : FORWARD",
                                                  "*** AsyncDispatchServlet start async and dispatch *** : request.getRequestURI() : /AsyncServlet/AsyncDispatch/dispatchPathInfo",
                                                  "*** AsyncDispatchServlet start async and dispatch *** : request.getContextPath() : /AsyncServlet",
                                                  "*** AsyncDispatchServlet start async and dispatch *** : request.getServletPath() : /AsyncDispatch",
                                                  "*** AsyncDispatchServlet start async and dispatch *** : request.getPathInfo() : /dispatchPathInfo",
                                                  "*** AsyncDispatchServlet start async and dispatch *** : request.getQueryString() : queryString=dispatch",
                                                  "*** AsyncDispatchedServlet running ***",
                                                  "*** AsyncDispatchedServlet *** : request.getDispatchType() : ASYNC",
                                                  "*** AsyncDispatchedServlet *** : request.getRequestURI() : /AsyncServlet/AsyncDispatched/dispatchedPathInfo",
                                                  "*** AsyncDispatchedServlet *** : request.getContextPath() : /AsyncServlet",
                                                  "*** AsyncDispatchedServlet *** : request.getServletPath() : /AsyncDispatched",
                                                  "*** AsyncDispatchedServlet *** : request.getPathInfo() : /dispatchedPathInfo",
                                                  "*** AsyncDispatchedServlet *** : request.getQueryString() : queryString=dispatched",
                                                  "*** AsyncDispatchedServlet completing async ***" };

        testAsync("doForward", "Dispatch", true, expectedStrings);
    }

    // Test designed to ensure that the response is not committed on return from forward if the forwarded to
    // servlet, which is part of another app,  starts aysnc and starts a runnable.
    @Test
    @Mode(TestMode.LITE)
    public void testAsyncFowardToOtherStart() throws Exception {

        LOG.info("\n /************************************************************************************/");
        LOG.info("\n [WebContainer | testAsyncFowardToStart ");
        LOG.info("\n /************************************************************************************/");

        String[] expectedStrings = new String[] { "*** AsyncStartServlet context = /AsyncServletOther ***",
                                                  "*** AsyncRunnable running ***",
                                                  "*** AsyncStartServlet completing Async ***" };
        testAsync("doForwardToOther", "Start", true, expectedStrings);
    }

    private void testAsync(String variation, String expectedText1, String expectedText2, boolean inResponse) throws Exception {
        testAsync("doForward", variation, expectedText1, expectedText2, inResponse);
    }

    private void testAsync(String test, String variation, String expectedText1, String expectedText2, boolean inResponse) throws Exception {
        WebConversation wc = new WebConversation();
        String contextRoot = "/AsyncServlet";
        wc.setExceptionsThrownOnErrorStatus(false);
        SHARED_SERVER.getLibertyServer().setMarkToEndOfLog();

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/AysncForwardTest?Test=" + test + "&Variation=" + variation));
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());
        assertTrue(response.getResponseCode() == 200);
        if (inResponse) {
            assertTrue("Expected text not found in response : " + expectedText1, response.getText().contains(expectedText1));
            if (expectedText2 != null) {
                assertTrue("Expected text not found in response : " + expectedText2, response.getText().contains(expectedText2));
            }
        } else {
            List<String> outList = SHARED_SERVER.getLibertyServer().findStringsInLogsAndTraceUsingMark(expectedText1);
            LOG.info("Entries found in log : " + outList.toString());
            assertTrue(!outList.isEmpty());
            if (expectedText2 != null) {
                outList = SHARED_SERVER.getLibertyServer().findStringsInLogsAndTraceUsingMark(expectedText2);
                LOG.info("Entries found in log : " + outList.toString());
                assertTrue(!outList.isEmpty());
            }
        }

    }

    private void testAsync(String test, String variation, boolean inResponse, String[] expectedTexts) throws Exception {
        WebConversation wc = new WebConversation();
        String contextRoot = "/AsyncServlet";
        wc.setExceptionsThrownOnErrorStatus(false);
        SHARED_SERVER.getLibertyServer().setMarkToEndOfLog();

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/AysncForwardTest?Test=" + test + "&Variation=" + variation));
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());
        assertTrue(response.getResponseCode() == 200);
        if (inResponse) {
            for (String expectedText : expectedTexts) {
                assertTrue("Expected text not found in response : " + expectedText, response.getText().contains(expectedText));
            }
        } else {
            for (String expectedText : expectedTexts) {
                List<String> outList = SHARED_SERVER.getLibertyServer().findStringsInLogsAndTraceUsingMark(expectedText);
                LOG.info("Entries found in log : " + outList.toString());
                assertTrue(!outList.isEmpty());
            }
        }

    }

    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
    }

}
