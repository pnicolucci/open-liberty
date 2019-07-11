/*******************************************************************************
 * Copyright (c) 2014 IBM Corporation and others.
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

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;

import com.ibm.ws.fat.util.LoggingTest;
import com.ibm.ws.fat.util.SharedServer;
import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;

import componenttest.annotation.ExpectedFFDC;
import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;

/**
 * Default error page testing: Test key variations of default error page
 * configuration.
 *
 * The first key variation is the web container feature version, which
 * is either servlet-3.0 or servlet 3.1. Only ibm-web-extension.xml default
 * error pages are available for servlet-3.0. For servlet-3.1, default
 * error pages are noted from web.xml, web-fragment.xml, and ibm-web-extension.xml,
 * with precedence in that order. Two test servers are provided, one running
 * feature version 3.0 and the other running feature version 3.1, to express
 * this variation.
 *
 * The second key variation is the default error page configuration,
 * which is expressed through web.xml, web-fragment.xml, and ibm-web-ext.xml.
 * Web modules are set in the test data to express this second variation.
 *
 * A subset of the second key variation is the schema level of the XML files.
 * web.xml can vary between 2.5, 3.0, and 3.1; web-fragment.xml can vary between
 * 3.0 and 3.1; ibm-web-ext.xml can vary between 1.0 and 1.1. However, since
 * these schema levels are largely irrelevant to default error page tests,
 * only a small subset of schema level variations are tested.
 *
 * The third key variation is the error generation mechanism, which is either
 * to set an error code or to throw an exception. To express this variation,
 * the test servlet accepts a servlet parameter which is used to select one or
 * the other error generation mechanism.
 *
 * The testable result is whether a variation generates a web module which can
 * be discovered (one of the test variations generates an invalid web module
 * configuration, which cannot be discovered), and, for discoverable variations,
 * whether a default error page was used, and, if a default error page was used,
 * which default error page was used.
 *
 * Not sure about whether to include the java7 rule. Default error
 * pages are provided by Liberty for Servlet 3.1, but the feature was
 * present in Servlet 3.0.
 *
 * The issue is that the WebContainer 3.0 test variations should be
 * runnable using Java6. Only the WebContainer 3.1 test variations
 * require java7.
 */
@MinimumJavaLevel(javaLevel = 7)
public class DefaultErrorPageTest extends LoggingTest {
    private static final Logger LOG = Logger.getLogger(DefaultErrorPageTest.class.getName());

    // Ports assigned indirectly within server.xml from a properties file.
    // The properties file is rewritten by a port selector.
    // The properties are available as system properties.
    //
    // The liberty server type seems to have problems assigning the port
    // values. See, for example, com.ibm.ws.cluster.manager.fat.common.
    // ClusterManagerMBeanCommon.ClusterManagerMBeanCommon(Class<?>),
    // which was used to set the code, here.
    //
    // The problem seems to be that the tests which uses the WebContainer 3.0
    // server are mixed together with tests which use the the WebContainer 3.1
    // server.  Not sure if the tests should be split.
    //
    // Note that 'testports.properties' puts 'bvt.prop.' in front of the property
    // names, where-as the actual java system property name omits that prefix.

    public static final String BVT_HTTP_PORT_1_PROPERTY_NAME = "member_1.http";
    public static final String BVT_HTTPS_PORT_1_PROPERTY_NAME = "member_1.https";
    public static final String BVT_HTTP_PORT_2_PROPERTY_NAME = "member_2.http";
    public static final String BVT_HTTPS_PORT_2_PROPERTY_NAME = "member_2.https";

    public static int getSystemPort(String portPropertyName) {
        String portPropertyValue = System.getProperty(portPropertyName);
        return Integer.parseInt(portPropertyValue);
    }

    @BeforeClass
    public static void testSetup() throws Exception {
        LibertyServer libertyServer30 = getSharedServer(WC_SERVER_30).getLibertyServer();
        libertyServer30.setHttpDefaultPort(getSystemPort(BVT_HTTP_PORT_1_PROPERTY_NAME));
        libertyServer30.setHttpDefaultSecurePort(getSystemPort(BVT_HTTPS_PORT_1_PROPERTY_NAME));

        LibertyServer libertyServer31 = getSharedServer(WC_SERVER_31).getLibertyServer();
        libertyServer31.setHttpDefaultPort(getSystemPort(BVT_HTTP_PORT_2_PROPERTY_NAME));
        libertyServer31.setHttpDefaultSecurePort(getSystemPort(BVT_HTTPS_PORT_2_PROPERTY_NAME));

        // Make sure the test framework knows that CWWKZ0117E is expected
        // PAN: TODO
        //SHARED_SERVER_30.setExpectedErrors("CWWKZ0117E:.*");

        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("CWWKZ0117E:.*");
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        // test cleanup
    }

    // Test parameterization:

    // Setup two servers to provide the possible web container feature versions:
    // Default error page behavior changes according to the web container
    // feature version.
    //
    // 'wcServer30' provides web-container feature version 3.0.
    // 'wcServer31' provides web-container feature version 3.1.

    public static final String WC_SERVER_30 = "servlet31_wcServer30";
    public static final String WC_SERVER_31 = "servlet31_wcServer31";

    // Initialize as resources before running tests ...

    @ClassRule
    public static SharedServer SHARED_SERVER_30 = new SharedServer(WC_SERVER_30);

    @ClassRule
    public static SharedServer SHARED_SERVER_31 = new SharedServer(WC_SERVER_31);

    public static SharedServer getSharedServer(String serverName) {
        return (serverName.equals(WC_SERVER_30) ? SHARED_SERVER_30 : SHARED_SERVER_31);
    }

    // Errors are generated by two mechanisms: By setting an error
    // code as the response, or by throwing an exception.

    public static final boolean SET_ERROR_CODE = true;
    public static final boolean THROW_EXCEPTION = false;

    // The error page test data is set to

    public static final String WEB_XML = "web.xml";
    public static final String WEB_FRAG_XML = "web-fragment.xml";
    public static final String IBM_WEB_EXT_XML = "ibm-web-ext.xml";

    public enum ErrorPageSource {
        WEB(WEB_XML, new String[] { WEB_FRAG_XML, IBM_WEB_EXT_XML }),
        WEB_FRAGMENT(WEB_FRAG_XML, new String[] { WEB_XML, IBM_WEB_EXT_XML }),
        WEB_EXTENSION(IBM_WEB_EXT_XML, new String[] { WEB_XML, WEB_FRAG_XML }),
        NONE(null, new String[] { WEB_XML, WEB_FRAG_XML, IBM_WEB_EXT_XML });

        private final String includeTag;
        private final String[] excludeTags;

        private ErrorPageSource(String includeTag, String[] excludeTags) {
            this.includeTag = includeTag;
            this.excludeTags = excludeTags;
        }

        public void verify(String response) {
            if (includeTag != null) {
                assertTrue("Actual page source [ " + response + " ] does not contain source tag [ " + includeTag + " ]",
                           response.indexOf(includeTag) > -1);
            }

            for (String excludeTag : excludeTags) {
                assertTrue("Actual page source [ " + response + " ] incorrectly contains source tag [ " + excludeTag + "]",
                           response.indexOf(excludeTag) == -1);
            }
        }
    }

    public static final String EXPECTED_CONFIRM_TEXT = "Confirm Error Page Servlet";

    public static final int ERROR_CODE_404 = 404;
    public static final int ERROR_CODE_500 = 500;

    public void runErrorPageTest(String serverName, String contextRoot, boolean sendError,
                                 int expectedErrorCode, ErrorPageSource expectedSource) throws Exception {

        LOG.info("\n /******************************************************************************/");
        LOG.info("\n [WebContainer | DefaultErrorPage]: Testing a default error page");
        LOG.info("\n [WebContainer | DefaultErrorPage]: Server: " + serverName);
        LOG.info("\n [WebContainer | DefaultErrorPage]: Test context root: " + contextRoot);
        LOG.info("\n [WebContainer | DefaultErrorPage]: Send error: " + Boolean.toString(sendError));
        LOG.info("\n [WebContainer | DefaultErrorPage]: Expected error code: " + Integer.toString(expectedErrorCode));
        LOG.info("\n [WebContainer | DefaultErrorPage]: Expected error page source: " + expectedSource);
        LOG.info("\n /******************************************************************************/");

        SharedServer sharedServer = getSharedServer(serverName);

        WebConversation confirmWebConversation = new WebConversation();
        String confirmURL = getConfirmURL(contextRoot);
        String serverConfirmURL = sharedServer.getServerUrl(true, confirmURL);

        LOG.info("[WebContainer | DefaultErrorPage]: Confirm URL: " + serverConfirmURL);

        WebRequest confirmRequest = new GetMethodWebRequest(serverConfirmURL);

        WebResponse confirmResponse = confirmWebConversation.getResponse(confirmRequest); // throws IOException, SAXException

        String confirmText = confirmResponse.getText(); // throws IOException

        LOG.info("[WebContainer | DefaultErrorPage]: Confirm response: " + confirmText);
        LOG.info("/*************************************************/");

        assertTrue("Actual confirm text [ " + confirmText + " ] does not contain expected [ " + EXPECTED_CONFIRM_TEXT + " ]",
                   (confirmText.indexOf(EXPECTED_CONFIRM_TEXT) != -1));

        WebConversation errorWebConversation = new WebConversation();
        errorWebConversation.setExceptionsThrownOnErrorStatus(false);

        String testURL = getTestURL(contextRoot, sendError);
        String serverTestURL = sharedServer.getServerUrl(true, testURL);

        LOG.info("[WebContainer | DefaultErrorPage]: Test URL: " + serverTestURL);

        WebRequest testRequest = new GetMethodWebRequest(serverTestURL);

        WebResponse testResponse = errorWebConversation.getResponse(testRequest); // throws IOException, SAXException

        String actualTestText = testResponse.getText(); // throws IOException
        int actualTestCode = testResponse.getResponseCode();

        LOG.info("[WebContainer | DefaultErrorPage]: Test return code: " + actualTestCode);
        LOG.info("[WebContainer | DefaultErrorPage]: Test response: " + actualTestText);
        LOG.info("/*************************************************/");

        assertTrue("Actual status code [ " + actualTestCode + " ] does not match expected [ " + Integer.toString(expectedErrorCode) + " ]",
                   (actualTestCode == expectedErrorCode));

        expectedSource.verify(actualTestText);
    }

    // Obtain the URL which is used to confirm the availability
    // of the error servlet.

    public String getConfirmURL(String contextRoot) {
        String testURL = contextRoot + "/" + "ErrorServlet";

        testURL += "?confirmServlet=true";

        return testURL;
    }

    // Obtain the URL for a particular test case based on the context root
    // of the target web module and based on the error code parameter.

    public String getTestURL(String contextRoot, boolean setErrorCode) {
        String testURL = contextRoot + "/" + "ErrorServlet";

        if (setErrorCode) {
            testURL += "?sendError=true";
        }

        return testURL;
    }

    // Default error page cases:
    //
    // Cases with no configured default error page;
    // Cases with one configured default error page;
    // Cases with an extension default error page which is overridden by a web or web fragment error page.
    // Cases with multiple configured default error pages, all setting the same default error page.
    // Cases with multiple configured default error pages, with all setting different default error pages.

    // web31 on feature version 30 are disabled!

    // Fragment conflict cases are disabled!
    //
    // A fragment conflict causes an exception and causes deployment of the enclosing application to fail.
    //
    // FFDC will show:
    // assertNotNull("The server did not report the COLLECTIVE persistence layer was available. Aborting test setup",
    // server.waitForStringInLogUsingMark("CWWKX1015I: COLLECTIVE", FATSuite.LOG_SEARCH_TIMEOUT));
    // Stack Dump = com.ibm.wsspi.adaptable.module.UnableToAdaptException: SRVE9957E: The default error page WEB-INF/lib/ErrorPage_Frag30OneDiff.jar in the web-fragment.xml of /FragmentErrorPage1.html conflicts with the default error page WEB-INF/lib/ErrorPage_Frag30TwoDiff.jar in the web-fragment.xml of /FragmentErrorPage2.html.
    // messages.log will show:
    //
    // [9/3/14 18:41:56:647 EDT] 00000030 com.ibm.ws.app.manager.AppMessageHelper                      E
    // CWWKZ0002E: An exception occurred while starting the application ErrorPage30.
    //
    // The exception message was:
    // com.ibm.ws.container.service.metadata.MetaDataException:
    // com.ibm.wsspi.adaptable.module.UnableToAdaptException:
    // SRVE9957E: The default error page /FragmentErrorPage1.html
    // in the web-fragment.xml of WEB-INF/lib/ErrorPage_Frag30OneDiff.jar
    // conflicts with the default error page /FragmentErrorPage2.html
    // in the web-fragment.xml of WEB-INF/lib/ErrorPage_Frag30TwoDiff.jar.
    //
    // To test these cases, a new application will be needed, and possibly a new server instance.

    // Cases with no configured error pages ("ErrorPageNone"):

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageNone_Web25_On_WC30() throws Exception {

        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");

        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web25None_NoFrag_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web25None_NoFrag_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageNone_Web25_On_WC31() throws Exception {

        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");

        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web25None_NoFrag_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web25None_NoFrag_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageNone_Web30_On_WC30() throws Exception {

        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");

        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30None_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30None_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageNone_Web30_On_WC31() throws Exception {

        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");

        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30None_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30None_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageNone_Web31_On_WC31() throws Exception {

        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");

        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web31None_Frag30None_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web31None_Frag30None_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    // Cases with one configured error pages ("ErrorPageOne"):
    //
    // The configured error page is not always recognized.

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageOne_Ext_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");

        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30None_ExtOne", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB_EXTENSION);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30None_ExtOne", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB_EXTENSION);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageOne_Ext_On_WC31() throws Exception {
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30None_ExtOne", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB_EXTENSION);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30None_ExtOne", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB_EXTENSION);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageOne_Web30_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30None_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30None_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageOne_Web30_On_WC31() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30None_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30None_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageOne_Frag_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30One_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30One_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageOne_Frag_On_WC31() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30One_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB_FRAGMENT);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30One_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB_FRAGMENT);
    }

    // d148426: Care is needed when testing the combinations:
    //
    // Servlet 3.0: Web30One, ExtOne, property not enabled for default error pages in web
    //  -- don't see the web default error page, do see the ext error page
    // Servlet 3.0, Web30One, ExtOne, property enabled for default error pages in web
    //  -- do see the web default error page, do see the ext error page, use the ext error page
    //
    // Servlet 3.1, Web30One, ExtOne, (property doesn't matter, because of servlet 3.1)
    // -- do see the web default error page, do see the ext error page, use the web error page
    //
    // these tests do not handle this case, as they require a new server case:
    //
    // But, the FVT test, below, does test the combination:
    //
    // com.ibm.ws.fvt.suite.config.test.dynamic.DynamicHttpTestCode354.testPI05845Ext_test3

    // server 3.0;

    // Cases with extension overrides ("ErrorPageExtensionOverride"):
    //
    // The override will not always be recognized.
    //
    // Extension override cases are cases where a web or fragment
    // error page occurs with an extension error page.

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageExtensionOverride_Web30_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_30.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30None_ExtOne", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB_EXTENSION);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30None_ExtOne", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB_EXTENSION);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageExtensionOverride_Web30_On_WC31() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30None_ExtOne", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30None_ExtOne", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageExtensionOverride_Frag_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_30.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30One_ExtOne", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB_EXTENSION);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30One_ExtOne", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB_EXTENSION);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageExtensionOverride_Frag_On_WC31() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30One_ExtOne", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB_FRAGMENT);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30One_ExtOne", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB_FRAGMENT);
    }

    // Cases with harmless conflicts ("ErrorPageSame"):
    //
    // The error pages may not be recognized, in which case the
    // potential conflict is unrealized.
    //
    // Conflict cases are cases where both a web error page
    // and a fragment error page are configured, where two fragment
    // error pages are configured, or where a web and two fragment
    // error pages are configured.

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageSame_Web_Frag_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_30.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30OneSame_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30OneSame_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageSame_Web_Frag_On_WC31() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30OneSame_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30OneSame_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageSame_Web_FragX2_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_30.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30TwoSame_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30TwoSame_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageSame_Web_FragX2_On_WC31() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30TwoSame_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30TwoSame_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB);
    }

    // Cases with harmful conflicts ("ErrorPageSame"):
    //
    // The error pages may not be recognized, in which case the
    // potential conflict is unrealized.
    //
    // Conflict cases are cases where both a web error page
    // and a fragment error page are configured, where two fragment
    // error pages are configured, or where a web and two fragment
    // error pages are configured.

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageConflicting_Web_Frag_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_30.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30OneDiff_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30OneDiff_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageConflicting_Web_Frag_On_WC31() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30OneDiff_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30OneDiff_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB);
    }

    @Test
    @Mode(TestMode.LITE)
    // @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageConflicting_FragX2_On_WC30() throws Exception {
        // runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30TwoDiff_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        // runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30None_Frag30TwoDiff_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    // @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageConflicting_FragX2_On_WC31() throws Exception {
        // runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30TwoDiff_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        // runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30None_Frag30TwoDiff_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageConflicting_Web30_FragX2_On_WC30() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_30.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30TwoDiff_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.NONE);
        runErrorPageTest(WC_SERVER_30, "/ErrorPage_Web30One_Frag30TwoDiff_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.NONE);
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPageConflicting_Web30_FragX2_On_WC31() throws Exception {
        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        //SHARED_SERVER_31.setExpectedErrors("SRVE0777E:.*");
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30TwoDiff_ExtNone", SET_ERROR_CODE, ERROR_CODE_404, ErrorPageSource.WEB);
        runErrorPageTest(WC_SERVER_31, "/ErrorPage_Web30One_Frag30TwoDiff_ExtNone", THROW_EXCEPTION, ERROR_CODE_500, ErrorPageSource.WEB);
    }

    // PAN: TODO
    @Override
    protected SharedServer getSharedServer() {
        // TODO Auto-generated method stub
        return null;
    }
}
