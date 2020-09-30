/*******************************************************************************
 * Copyright (c) 2017, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.fat.wc.tests;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Logger;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieOrigin;
import org.apache.hc.client5.http.cookie.CookieSpec;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.client5.http.impl.cookie.RFC6265CookieSpecFactory;
import org.apache.hc.core5.http.Header;
import org.apache.hc.core5.http.protocol.BasicHttpContext;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;

/**
 * All Servlet 4.0 tests with all applicable server features enabled.
 */
@RunWith(FATRunner.class)
@Mode(TestMode.FULL)
public class ServletCTSTest {

    private static final Logger LOG = Logger.getLogger(ServletCTSTest.class.getName());
    private static final String APP_NAME = "CTSTest";

    @Server("servlet40_sameSite")
    public static LibertyServer server;

    @BeforeClass
    public static void setUp() throws Exception {
        LOG.info("Setup : add TestServlet40 to the server if not already present.");

        ShrinkHelper.defaultDropinApp(server, APP_NAME + ".war", "cts.servlet");

        server.startServer(ServletCTSTest.class.getSimpleName() + ".log");
        LOG.info("Setup : complete, ready for Tests");
    }

    @AfterClass
    public static void testCleanup() throws Exception {
        LOG.info("testCleanUp : stop server");

        // Stop the server
        if (server != null && server.isStarted()) {
            server.stopServer();
        }
    }

    /**
     * Request a simple servlet.
     *
     * @throws Exception
     */
    @Test
    public void testServletCTS() throws Exception {
        String url = "http://" + server.getHostname() + ":" + server.getHttpDefaultPort() + "/" + APP_NAME + "/CTSServlet";

        RFC6265CookieSpecFactory factory = new RFC6265CookieSpecFactory();
        CookieSpec spec = factory.create(new BasicHttpContext());

        //HttpResponse response = null;
        String dateHeader = null;
        int index = -1;
        Date expiryDate = null;
        String body = null;
        boolean foundCookie = false;
        String CUSTOM_HEADER_DATE_FORMAT = "yyyy-MM-dd HH:mm";

        HttpGet getMethod = new HttpGet(url);

        // PAN
        try (final CloseableHttpClient client = HttpClientBuilder.create().build()) {
            try (final CloseableHttpResponse response = client.execute(getMethod)) {
                dateHeader = response.getHeader("testDate").toString();
                LOG.info("dateHeader: " + dateHeader);
                List<Cookie> cookies;
                Header[] cookiesHeaders = response.getHeaders("Set-Cookie");

                String headerValue;
                for (Header header : cookiesHeaders) {
                    headerValue = header.toString();
                    LOG.info("\n" + headerValue);
                    cookies = spec.parse(header, new CookieOrigin(".eng.com", server.getHttpDefaultPort(), "CTSServlet", false));
                    int i = 0;
                    for (Cookie cookie : cookies) {
                        if (cookie.getName().equals("name1")) {
                            LOG.info("cookie: " + cookie);
                            index = i;
                        }
                        i++;
                    }

                    if (index >= 0) {
                        expiryDate = cookies.get(index).getExpiryDate();
                        LOG.info("expiryDate: " + expiryDate);
                        //body = response.getResponseBodyAsString();
                        foundCookie = true;
                        break;
                    }
                }

                if (!foundCookie) {
                    //throw new Fault("The test cookie was not located in the response");
                    LOG.info("foundCookie was not true");
                }
            }

            // put expiry date into GMT
            SimpleDateFormat sdf = new SimpleDateFormat(CUSTOM_HEADER_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
            String resultStringDate = sdf.format(expiryDate);
            LOG.info("resultStringDate, formating expiryDate object: " + resultStringDate);

            Date resultDate = sdf.parse(resultStringDate);
            Date expectedDate = sdf.parse(dateHeader.substring(dateHeader.indexOf(": ") + 2).trim());
            if (resultDate.before(expectedDate)) {
                LOG.info("The expiry date was incorrect, expected ="
                         + expectedDate + ", result = " + resultDate);
            }
            LOG.info("expectedDate: " + expectedDate);
            LOG.info("resultDate: " + resultDate);
        }
    }
}