/*******************************************************************************
 * Copyright (c) 2016 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package async.utils;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Produces output for test runs
 */
public class AsyncUtils {

    public static void printInfo(String caller, HttpServletRequest req, HttpServletResponse resp) {

        try {
            PrintWriter pw = resp.getWriter();

            pw.println("<br><br>" + caller + " : request.getDispatchType() : " + req.getDispatcherType());

            pw.println("<br><br>" + caller + " : request.getRequestURI() : " + req.getRequestURI());
            pw.println("<br>" + caller + " : request.getContextPath() : " + req.getContextPath());
            pw.println("<br>" + caller + " : request.getServletPath() : " + req.getServletPath());
            pw.println("<br>" + caller + " : request.getPathInfo() : " + req.getPathInfo());
            pw.println("<br>" + caller + " : request.getQueryString() : " + req.getQueryString());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void logInfo(String caller, HttpServletRequest req, HttpServletResponse resp) {

        System.out.println(caller + " : request.getDispatchType() : " + req.getDispatcherType());

        System.out.println(caller + " : request.getRequestURI() : " + req.getRequestURI());
        System.out.println(caller + " : request.getContextPath() : " + req.getContextPath());
        System.out.println(caller + " : request.getServletPath() : " + req.getServletPath());
        System.out.println(caller + " : request.getPathInfo() : " + req.getPathInfo());
        System.out.println(caller + " : request.getQueryString() : " + req.getQueryString());

    }

}
