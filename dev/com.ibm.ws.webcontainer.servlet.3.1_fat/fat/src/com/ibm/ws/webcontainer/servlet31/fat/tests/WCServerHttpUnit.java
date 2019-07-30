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

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
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

import componenttest.annotation.ExpectedFFDC;
import componenttest.annotation.MinimumJavaLevel;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;

/**
 * Tests to execute on the wcServer that use HttpUnit.
 */
@MinimumJavaLevel(javaLevel = 7)
public class WCServerHttpUnit extends LoggingTest {
    private static final Logger LOG = Logger.getLogger(WCServerHttpUnit.class.getName());
    protected static final Map<String, String> testUrlMap = new HashMap<String, String>();

    @ClassRule
    public static SharedServer SHARED_SERVER = new SharedServer("servlet31_wcServer");

    @AfterClass
    public static void testCleanup() throws Exception {
        // test cleanup
    }

    @Before
    public void before() {
        // PAN: TODO
        //SHARED_SERVER.setExpectedErrors("SRVE8015E:.*");
    }

    @Test
    @Mode(TestMode.LITE)
    public void testSendContentLengthLong65536L_test() throws Exception {
        String resp = sendPostRequest(65536L);
        assertTrue(resp.contains("PASS"));
    }

    // Integer.MAX_VALUE is 2^31-1, or 2,147,483,647.
    // 'sendPostRequest' does writes of the requested length,
    // making for a very, very, long test.
    @Test
    @Mode(TestMode.FULL)
    public void testSendContentLengthLongLong_test() throws Exception {
        long len = Integer.MAX_VALUE + 32769L;
        String resp = sendPostRequest(len);
        assertTrue(resp.contains("PASS"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testReceiveContentLengthLong65536L_test() throws Exception {
        assertTrue(getPostResponse(65536L));
    }

    // Integer.MAX_VALUE is 2^31-1, or 2,147,483,647.
    // 'getPostResponse' does reads to the requested length,
    // making for a very, very, long test.
    @Test
    @Mode(TestMode.FULL)
    public void testReceiveContentLengthLongLong_test() throws Exception {
        assertTrue(getPostResponse(Integer.MAX_VALUE + 32769L));
    }

    private String sendPostRequest(long postDataSize) {

        String contextRoot = "/TestServlet31";

        String URLString = SHARED_SERVER.getServerUrl(true, contextRoot + "/ContentLengthLongServlet/" + Long.toString(postDataSize));
        LOG.info("Request URL : " + URLString);

        String resp = "";
        try {

            URL url = new URL(URLString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(postDataSize);
            con.setRequestProperty("Content-Length", Long.toString(postDataSize));
            OutputStream os = con.getOutputStream();

            byte[] b = new byte[32768];
            for (int i = 0; i < 32768; i++) {
                b[i] = (byte) 0x61;
            }

            for (long i = 0; i < postDataSize; i += b.length) {
                LOG.info("Send next buffer, now sent : " + i);
                os.write(b);
                os.flush();
            }

            int code = con.getResponseCode();
            LOG.info("Response code : " + code);

            if (code == 200) {
                java.io.InputStream data = con.getInputStream();
                StringBuffer dataBuffer = new StringBuffer();
                byte[] dataBytes = new byte[1024];
                for (int n; (n = data.read(dataBytes)) != -1;) {
                    dataBuffer.append(new String(dataBytes, 0, n));
                }

                resp = dataBuffer.toString();
                LOG.info("Response data : " + resp);

            }

            con.disconnect();

        } catch (Exception e) {
            assertTrue("Exception from request: " + e.getMessage(), false);
        }

        return resp;
    }

    private boolean getPostResponse(long postDataSize) {

        boolean result = true;

        String contextRoot = "/TestServlet31";

        String URLString = SHARED_SERVER.getServerUrl(true, contextRoot + "/ContentLengthLongServlet/RESP" + Long.toString(postDataSize));
        LOG.info("Request URL : " + URLString);

        try {

            URL url = new URL(URLString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(false);

            int code = con.getResponseCode();
            LOG.info("Response code : " + code);

            if (code == 200) {

                long cL = con.getContentLengthLong();
                LOG.info("ContentLength header = " + cL);

                java.io.InputStream data = con.getInputStream();
                byte[] dataBytes = new byte[32768];
                int readLen = data.read(dataBytes);
                long total = 0L;
                while (readLen != -1) {
                    total += readLen;
                    readLen = data.read(dataBytes);
                }
                LOG.info(total + " bytes read for the reposne.");

                if ((cL != postDataSize) || (total != postDataSize)) {

                    LOG.info("Mismatch !! expected bytes = " + postDataSize + " : " + " ContentLength = " + cL + " : bytes read = " + total);
                    result = false;
                }
            } else {
                result = false;
            }

            con.disconnect();

        } catch (Exception e) {
            assertTrue("Exception from request: " + e.getMessage(), false);
        }

        return result;
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRelativeRedirect() throws Exception {
        WebConversation wc = new WebConversation();
        String contextRoot = "/TestServlet31";
        wc.setExceptionsThrownOnErrorStatus(false);

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/Redirector?Test=relative"));
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());
        assertTrue(response.getResponseCode() == 200);
        assertTrue(response.getText().contains("*** Redirected Servlet : called by relative URI. ***"));
    }

    @Test
    @Mode(TestMode.FULL)
    public void testRelativeRedirectWithPathInfo() throws Exception {
        WebConversation wc = new WebConversation();
        String contextRoot = "/TestServlet31";
        wc.setExceptionsThrownOnErrorStatus(false);

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/Redirector/path/info?Test=relativeWithPathInfo"));
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());
        assertTrue(response.getResponseCode() == 200);
        assertTrue(response.getText().contains("*** Redirected Servlet: called by relative URI with PathInfo. ***"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testAbsoluteRedirect() throws Exception {
        WebConversation wc = new WebConversation();
        String contextRoot = "/TestServlet31";
        wc.setExceptionsThrownOnErrorStatus(false);

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/Redirector?Test=absolute"));
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());
        assertTrue(response.getResponseCode() == 200);
        assertTrue(response.getText().contains("*** Redirected Servlet : called by absolute URI. ***"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testNetworkRedirect() throws Exception {
        WebConversation wc = new WebConversation();
        String contextRoot = "/TestServlet31";
        wc.setExceptionsThrownOnErrorStatus(false);

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/Redirector?Test=network"));
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());
        assertTrue(response.getResponseCode() == 200);
        assertTrue(response.getText().contains("*** Redirected Servlet : called by network URI. ***"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testSessionCookieConfig() throws Exception {
        WebConversation wc = new WebConversation();
        String contextRoot = "/TestServlet31";
        wc.setExceptionsThrownOnErrorStatus(false);

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/SessionCookieConfigTest"));
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());
        assertTrue(response.getResponseCode() == 200);
        assertTrue(response.getText().contains("SessionCookieConfigTest : setComment : IllegalStateException"));

    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestWithTwoParamters() throws Exception {
        testParamaters("?NumberOfOtherParameters=1&FirstParameter=value", "value");
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestWithSevenParamters() throws Exception {
        testParamaters("?NumberOfOtherParameters=6&FirstParameter=myValue&myValue=ParamNoEqual&ParamNoEqual&FirstAfterParamNoEqual=anotherOne&anotherOne=theLastOne&theLastOne=Pass",
                       "Pass");
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestWithNoValueNoEqualParamaters() throws Exception {
        testParamaters("?NumberOfOtherParameters=5&FirstParameter=ParamNoEqual&ParamNoEqual&FirstAfterParamNoEqual=ParamNoValue&ParamNoValue=&FirstAfterParamNoValue=finished",
                       "finished");
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestWithNoValueAsLastParamater() throws Exception {
        testParamaters("?NumberOfOtherParameters=2&FirstParameter=ParamNoValue&ParamNoValue=",
                       "EmptyString");
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestWithNoEqualAsLastParamater() throws Exception {
        testParamaters("?NumberOfOtherParameters=2&FirstParameter=ParamNoEqual&ParamNoEqual",
                       "EmptyString");
    }

    private void testParamaters(String parameters, String lastParameterValue) throws Exception {
        WebConversation wc = new WebConversation();
        String contextRoot = "/TestServlet31";
        wc.setExceptionsThrownOnErrorStatus(false);

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/RequestParameterTest" + parameters));
        WebResponse response = wc.getResponse(request);
        LOG.info("Servlet response : " + response.getText());
        assertTrue(response.getResponseCode() == 200);
        assertTrue(response.getText().contains("Last parameter value found was " + lastParameterValue));

    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest16() throws Exception {

        String initialParams = "NumberOfOtherParameters=6&FirstParameter=myValue&myValue=anotherOne&anotherOne=ParamNoEqual&ParamNoEqual&FirstAfterParamNoEqual=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        String result = testParametersPost(initialParams, null, 16, null);
        assertTrue(result.contains("Long parameter length was 16"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest16Encoded() throws Exception {

        String initialParams = "NumberOfOtherParameters=6&FirstParameter=myValue&myValue=anotherOne&anotherOne=ParamNoEqual&ParamNoEqual&FirstAfterParamNoEqual=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        String result = testParametersPost(initialParams, null, 16, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Long parameter length was 16"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest50() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // parameter value is contained in at least 3 buffers

        String initialParams = "NumberOfOtherParameters=4&FirstParameter=myValue&myValue=anotherOne&anotherOne=theLastLongOne&theLastLongOne=";

        String result = testParametersPost(initialParams, null, 50, null);
        assertTrue(result.contains("Last parameter length was 50"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest50Encoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // parameter value is contained in at least 3 buffers

        String initialParams = "NumberOfOtherParameters=4&FirstParameter=myValue&myValue=anotherOne&anotherOne=theLastLongOne&theLastLongOne=";

        String result = testParametersPost(initialParams, null, 50, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Last parameter length was 50"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest80() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // parameter value is contained in at least 3 buffers

        String initialParams = "NumberOfOtherParameters=4&FirstParameter=myValue&myValue=anotherOne&anotherOne=theLastLongOne&theLastLongOne=";

        String result = testParametersPost(initialParams, null, 80, null);
        assertTrue(result.contains("Last parameter length was 80"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest80Encoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // parameter value is contained in at least 3 buffers

        String initialParams = "NumberOfOtherParameters=4&FirstParameter=myValue&myValue=anotherOne&anotherOne=theLastLongOne&theLastLongOne=";

        String result = testParametersPost(initialParams, null, 80, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Last parameter length was 80"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest64minus() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // parameter value ends in one buffer and the first character of the next buffer is &
        String initialParams = "NumberOfOtherParameters=7&FirstParameter=myValue&myValue=anotherOne&anotherOne=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        int largeSize = initialParams.length() % 32;
        largeSize = 64 - largeSize;
        String endParams = "&FirstParameterAfterReallyReallLongParamaterValue=ParamNoEqual&ParamNoEqual&FirstAfterParamNoEqual=phew";

        String result = testParametersPost(initialParams, endParams, largeSize, null);
        assertTrue(result.contains("Long parameter length was " + largeSize));
        assertTrue(result.contains("Last parameter value found was phew"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest64minusEncoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // parameter value ends in one buffer and the first character of the next buffer is &
        String initialParams = "NumberOfOtherParameters=7&FirstParameter=myValue&myValue=anotherOne&anotherOne=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        int largeSize = initialParams.length() % 32;
        largeSize = 64 - largeSize;
        String endParams = "&FirstParameterAfterReallyReallLongParamaterValue=ParamNoEqual&ParamNoEqual&FirstAfterParamNoEqual=phew";

        String result = testParametersPost(initialParams, endParams, largeSize, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Long parameter length was " + largeSize));
        assertTrue(result.contains("Last parameter value found was phew"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest63minus() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // parameter value ends and the & is the last character of the buffer.
        String initialParams = "NumberOfOtherParameters=5&FirstParameter=myValue&myValue=anotherOne&anotherOne=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        int largeSize = initialParams.length() % 32;
        largeSize = 64 - largeSize - 1;
        String endParams = "&FirstParameterAfterReallyReallLongParamaterValue=phew";

        String result = testParametersPost(initialParams, endParams, largeSize, null);
        String expectedOut = "Long parameter length was " + largeSize;
        LOG.info("expect output : " + expectedOut);
        assertTrue(result.contains(expectedOut));
        assertTrue(result.contains("Last parameter value found was phew"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequest63minusEncoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // parameter value ends and the & is the last character of the buffer.
        String initialParams = "NumberOfOtherParameters=5&FirstParameter=myValue&myValue=anotherOne&anotherOne=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        int largeSize = initialParams.length() % 32;
        largeSize = 64 - largeSize - 1;
        String endParams = "&FirstParameterAfterReallyReallLongParamaterValue=phew";

        String result = testParametersPost(initialParams, endParams, largeSize, "application/x-www-form-urlencoded; charset=US-ASCII");
        String expectedOut = "Long parameter length was " + largeSize;
        LOG.info("expect output : " + expectedOut);
        assertTrue(result.contains(expectedOut));
        assertTrue(result.contains("Last parameter value found was phew"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestLongKey1() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // a key value spans at least 3 buffers
        String initialParams = "NumberOfOtherParameters=6&FirstParameter=myValue123&myValue123=anotherOne&anotherOne=ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue&ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        String endParams = "&FirstParameterAfterReallyReallLongParamaterValue=phew";

        String result = testParametersPost(initialParams, endParams, 140, null);
        assertTrue(result.contains("Long parameter length was 140"));
        assertTrue(result.contains("Last parameter value found was phew"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestLongKey1Encoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // a key value spans at least 3 buffers
        String initialParams = "NumberOfOtherParameters=6&FirstParameter=myValue123&myValue123=anotherOne&anotherOne=ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue&ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        String endParams = "&FirstParameterAfterReallyReallLongParamaterValue=phew";

        String result = testParametersPost(initialParams, endParams, 140, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Long parameter length was 140"));
        assertTrue(result.contains("Last parameter value found was phew"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestLongKey2() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue1234=anotherOne&anotherOne is the first character in a buffer
        String initialParams = "NumberOfOtherParameters=4&FirstParameter=myValue1234&myValue1234=anotherOne&anotherOne=ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue&ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue=ReallyReallyLongParamaterValue";

        String result = testParametersPost(initialParams, null, 0, null);
        assertTrue(result.contains("Last parameter value found was ReallyReallyLongParamaterValue"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestLongKey2Encoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue1234=anotherOne&anotherOne is the first character in a buffer
        String initialParams = "NumberOfOtherParameters=4&FirstParameter=myValue1234&myValue1234=anotherOne&anotherOne=ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue&ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue=ReallyReallyLongParamaterValue";

        String result = testParametersPost(initialParams, null, 0, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Last parameter value found was ReallyReallyLongParamaterValue"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestLongKey3() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue123=anotherOne&anotherOne is the last character in a buffer
        String initialParams = "NumberOfOtherParameters=06&FirstParameter=myValue123&myValue123=anotherOne&anotherOne=ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue&ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        String endParams = "&FirstParameterAfterReallyReallLongParamaterValue=phew";

        String result = testParametersPost(initialParams, endParams, 140, null);
        assertTrue(result.contains("Long parameter length was 140"));
        assertTrue(result.contains("Last parameter value found was phew"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestLongKey3Encoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue123=anotherOne&anotherOne is the last character in a buffer
        String initialParams = "NumberOfOtherParameters=06&FirstParameter=myValue123&myValue123=anotherOne&anotherOne=ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue&ReallyReallyExceptionallyReallyReallyWowCannotBeleiveHowLongThisLongParamaterValue=ReallyReallyLongParamaterValue&ReallyReallyLongParamaterValue=";
        String endParams = "&FirstParameterAfterReallyReallLongParamaterValue=phew";

        String result = testParametersPost(initialParams, endParams, 140, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Long parameter length was 140"));
        assertTrue(result.contains("Last parameter value found was phew"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestNoEqualNoValue() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue123=anotherOne&anotherOne is the last character in a buffer
        String initialParams = "NumberOfOtherParameters=5&FirstParameter=ParamNoEqual&ParamNoEqual&FirstAfterParamNoEqual=ParamNoValue&ParamNoValue=&FirstAfterParamNoValue=finished";

        String result = testParametersPost(initialParams, null, 0, null);
        assertTrue(result.contains("Last parameter value found was finished"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestNoEqualNoValueEncoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue123=anotherOne&anotherOne is the last character in a buffer
        String initialParams = "NumberOfOtherParameters=5&FirstParameter=ParamNoEqual&ParamNoEqual&FirstAfterParamNoEqual=ParamNoValue&ParamNoValue=&FirstAfterParamNoValue=finished";

        String result = testParametersPost(initialParams, null, 0, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Last parameter value found was finished"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestNoEqualLast() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue123=anotherOne&anotherOne is the last character in a buffer
        String initialParams = "NumberOfOtherParameters=2&FirstParameter=ParamNoEqual&ParamNoEqual";
        String result = testParametersPost(initialParams, null, 0, null);
        assertTrue(result.contains("Last parameter value found was EmptyString"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestNoEqualLastEncoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue123=anotherOne&anotherOne is the last character in a buffer
        String initialParams = "NumberOfOtherParameters=2&FirstParameter=ParamNoEqual&ParamNoEqual";
        String result = testParametersPost(initialParams, null, 0, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Last parameter value found was EmptyString"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestNoValueLast() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue123=anotherOne&anotherOne is the last character in a buffer
        String initialParams = "NumberOfOtherParameters=2&FirstParameter=ParamNoValue&ParamNoValue=";
        String result = testParametersPost(initialParams, null, 0, null);
        assertTrue(result.contains("Last parameter value found was EmptyString"));
    }

    @Test
    @Mode(TestMode.LITE)
    public void testRequestParamsInPostRequestNoValueLastEncoded() throws Exception {

        // When parameters are loaded into buffer of 32 bytes this test ensures that
        // the "=" in myValue123=anotherOne&anotherOne is the last character in a buffer
        String initialParams = "NumberOfOtherParameters=2&FirstParameter=ParamNoValue&ParamNoValue=";
        String result = testParametersPost(initialParams, null, 0, "application/x-www-form-urlencoded; charset=US-ASCII");
        assertTrue(result.contains("Last parameter value found was EmptyString"));
    }

    private String testParametersPost(String initialParams, String finalParams, int largeParamSize, String contentType) {

        String contextRoot = "/TestServlet31";

        String URLString = SHARED_SERVER.getServerUrl(true, contextRoot + "/RequestParameterTest");
        LOG.info("Request URL : " + URLString);

        byte[] initialParamsBytes = initialParams.getBytes();
        byte[] finalParamsBytes = null;
        long totalDataLength = initialParams.length();
        totalDataLength += largeParamSize;
        if (finalParams != null) {
            finalParamsBytes = finalParams.getBytes();
            totalDataLength += finalParamsBytes.length;
        }

        LOG.info("Parameter to write Content Length =  " + Long.toString(totalDataLength));

        String resp = "";
        try {

            URL url = new URL(URLString);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);
            con.setFixedLengthStreamingMode(totalDataLength);
            con.setRequestProperty("Content-Length", Long.toString(totalDataLength));
            if (contentType != null) {
                con.setRequestProperty("Content-Type", contentType);
            }

            OutputStream os = con.getOutputStream();

            LOG.info("Write the initial Params of length " + initialParamsBytes.length + " : " + initialParams);
            os.write(initialParamsBytes);
            os.flush();

            byte[] bytes = new byte[32768];
            for (int i = 0; i < 32768; i++) {
                bytes[i] = (byte) 0x61;
            }

            LOG.info("Write the large parameter value of size " + largeParamSize);

            int largeParamBytesWritten = 0;
            for (long i = 0; i < largeParamSize - bytes.length; i += bytes.length) {
                os.write(bytes);
                os.flush();
                largeParamBytesWritten += bytes.length;
            }

            int remainingLargeParamBytes = largeParamSize - largeParamBytesWritten;
            if (remainingLargeParamBytes > 0) {
                LOG.info("Finish large parameter with write of " + remainingLargeParamBytes);
                byte[] remainingBytes = new byte[remainingLargeParamBytes];
                for (int i = 0; i < remainingLargeParamBytes; i++) {
                    remainingBytes[i] = (byte) 0x61;
                }

                os.write(remainingBytes);
                os.flush();
            }

            if (finalParamsBytes != null) {
                LOG.info("Write final parmeters " + finalParams);
                os.write(finalParamsBytes);
                os.flush();
            }

            int code = con.getResponseCode();
            LOG.info("Response code : " + code);

            if (code == 200) {
                java.io.InputStream data = con.getInputStream();
                StringBuffer dataBuffer = new StringBuffer();
                byte[] dataBytes = new byte[1024];
                for (int n; (n = data.read(dataBytes)) != -1;) {
                    dataBuffer.append(new String(dataBytes, 0, n));
                }

                resp = dataBuffer.toString();
                LOG.info("Response data : " + resp);

            }

            con.disconnect();

        } catch (Exception e) {
            assertTrue("Exception from request: " + e.getMessage(), false);
        }

        return resp;
    }

    @Test
    @Mode(TestMode.LITE)
    @ExpectedFFDC("javax.servlet.ServletException")
    public void testDefaultErrorPage() throws Exception {

        // Make sure the test framework knows that SRVE0777E is expected
        // PAN: TODO
        // SHARED_SERVER.setExpectedErrors("SRVE0777E:.*", "SRVE0315E:.*");

        LOG.info("\n /******************************************************************************/");
        LOG.info("\n [WebContainer | WCServerHttpUnit | DefaultErrorPage]: Testing a default error page");
        LOG.info("\n /******************************************************************************/");

        WebConversation wc = new WebConversation();
        wc.setExceptionsThrownOnErrorStatus(false);
        String contextRoot = "/TestServlet31";
        wc.setExceptionsThrownOnErrorStatus(false);
        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/ErrorServlet?sendError=true"));
        WebResponse response = wc.getResponse(request);
        String text = response.getText();
        int code = response.getResponseCode();

        LOG.info("/*************************************************/");
        LOG.info("[WebContainer | DefaultErrorPage]: Return Code is: " + code);
        LOG.info("[WebContainer | DefaultErrorPage]: Response is: " + text);
        LOG.info("/*************************************************/");

        assertTrue("wrong status code: actual = " + code + " :: expected = 404", code == 404);
        assertTrue("wrong output: actual = " + text + " :: expected = " + "web.xml", text.indexOf("web.xml") > -1);

        request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + "/ErrorServlet"));
        response = wc.getResponse(request);
        text = response.getText();
        code = response.getResponseCode();

        LOG.info("/*************************************************/");
        LOG.info("[WebContainer | DefaultErrorPage]: Return Code is: " + code);
        LOG.info("[WebContainer | DefaultErrorPage]: Response is: " + text);
        LOG.info("/*************************************************/");

        assertTrue("wrong status code: actual = " + code + " :: expected = 500", code == 500);
        assertTrue("wrong output: actual = " + text + " :: expected = " + "web.xml", text.indexOf("web.xml") > -1);
    }

    @Test
    @Mode(TestMode.LITE)
    public void testReaderFirstValidCharset() throws Exception {

        String text = getResponse("/GetReaderFirstTest?valid_charset=true", "charset=US-ASCII");

        assertTrue("PASS1 expected in the response: ", text.indexOf("PASS1") != -1);
        assertTrue("PASS2 expected in the response: ", text.indexOf("PASS2") != -1);
        assertTrue("PASS2 expected in the response: ", text.indexOf("PASS3") != -1);
    }

    @Test
    @Mode(TestMode.LITE)
    public void testReaderFirstInValidCharset() throws Exception {

        String text = getResponse("/GetReaderFirstTest?valid_charset=false", "charset=blobby");
        assertTrue("PASS1 expected in the response: ", text.indexOf("PASS1") != -1);
        assertTrue("PASS2 expected in the response: ", text.indexOf("PASS2") != -1);
        assertTrue("PASS2 expected in the response: ", text.indexOf("PASS3") != -1);
    }

    @Test
    @Mode(TestMode.LITE)
    public void testReaderSecondValidCharset() throws Exception {

        String text = getResponse("/GetReaderSecondTest?valid_charset=true", "charset=US-ASCII");
        assertTrue("PASS1 expected in the response: ", text.indexOf("PASS1") != -1);
        assertTrue("PASS2 expected in the response: ", text.indexOf("PASS2") != -1);
    }

    @Test
    @Mode(TestMode.LITE)
    public void testReaderSecondInValidCharset() throws Exception {

        String text = getResponse("/GetReaderSecondTest?valid_charset=false", "charset=blobby");
        assertTrue("PASS1 expected in the response: ", text.indexOf("PASS1") != -1);
        assertTrue("PASS2 expected in the response: ", text.indexOf("PASS2") != -1);
    }

    private String getResponse(String uri, String contentType) throws Exception {

        WebConversation wc = new WebConversation();
        String contextRoot = "/TestServlet31";

        if (contentType != null) {
            wc.setHeaderField("Content-Type", contentType);
        }

        WebRequest request = new GetMethodWebRequest(SHARED_SERVER.getServerUrl(true, contextRoot + uri));
        WebResponse response = wc.getResponse(request);
        String text = response.getText();
        int code = response.getResponseCode();

        LOG.info("/*************************************************/");
        LOG.info("[WebContainer | testReaderFirst]: Return Code is: " + code);
        LOG.info("[WebContainer | testReaderFirst]: Response is: " + text);
        LOG.info("/*************************************************/");

        return text;

    }

    @Override
    protected SharedServer getSharedServer() {
        return SHARED_SERVER;
    }

}
