/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package cts.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This Servlet will add a number of Cookies to the HttpServletResponse depending on the value of the
 * cookieToAdd parameter. The server configuration will define a SameSite value for each of them and
 * the tests will ensure that each of the Cookies has the proper SameSite attribute added to the
 * Set-Cookie header.
 */
@WebServlet(urlPatterns = "/CTSServlet")
public class CTSServlet extends HttpServlet {

    /**  */
    private static final long serialVersionUID = 1L;

    String CUSTOM_HEADER_DATE_FORMAT = "yyyy-MM-dd HH:mm";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        PrintWriter pw = resp.getWriter();
        javax.servlet.http.Cookie testCookie = new javax.servlet.http.Cookie("name1", "value1");
        testCookie.setVersion(0);

        testCookie.setMaxAge(2);
        // Use a custom format to ensure Locale independence
        SimpleDateFormat sdf = new SimpleDateFormat(CUSTOM_HEADER_DATE_FORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date currDate = new Date();
        String dateString = sdf.format(currDate);

        resp.addCookie(testCookie);
        resp.addHeader("testDate", dateString);
    }

}
