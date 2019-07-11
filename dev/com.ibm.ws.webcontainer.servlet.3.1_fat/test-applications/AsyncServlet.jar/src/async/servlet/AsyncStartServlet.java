/*  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package async.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Scott
 *
 */
@WebServlet(urlPatterns = "/AsyncStart/*", asyncSupported = true)
public class AsyncStartServlet extends HttpServlet {
    private static final long serialVersionUID = 2722861779284005300L;

    private final AsyncRunnable executor = new AsyncRunnable();
    private final String name = "AsyncStartServlet";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String time = req.getParameter("time");

        int millis = -1;
        if (time != null) {
            try {
                millis = Integer.parseInt(time);
            } catch (Exception e) {
            }
        }

        resp.setContentType("text/html");
        PrintWriter writer = resp.getWriter();

        String done = (String) req.getAttribute("done");
        if (done == null) {
            req.setAttribute("done", "true");

            writer.println("<br>*** AsyncStartServlet context = " + req.getContextPath() + " ***");

            async.utils.AsyncUtils.printInfo("*** AsyncStartServlet ***", req, resp);

            writer.println("<br>*** AsyncStartServlet starting Async ***");

            AsyncContext context = req.startAsync(req, resp);

            context.setTimeout(millis * 10);

            executor.setContext(context);
            executor.setMillis(millis);
            writer.println("<br>*** AsyncStartServlet starting the runnable: " + executor.getClass().getSimpleName() + " ***");
            context.start(executor);
            writer.println("<br>*** AsyncStartServlet returning from forward (after sleeeping) ****");

        } else {

            writer.println("<br>*** AsyncStartServlet completing Async ***");
            async.utils.AsyncUtils.printInfo("*** AsyncStartServlet complete async ***", req, resp);

            req.getAsyncContext().complete();
        }

    }
}
