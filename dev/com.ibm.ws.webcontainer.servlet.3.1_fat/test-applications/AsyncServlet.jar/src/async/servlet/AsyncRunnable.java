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
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Scott Nicklous
 *
 */
public class AsyncRunnable implements Runnable {

    private int millis;
    private AsyncContext context;

    /**
     * @return the millis
     */
    public int getMillis() {
        return millis;
    }

    /**
     * @param millis
     *            the millis to set
     */
    public void setMillis(int millis) {
        this.millis = millis;
    }

    /**
     * @return the context
     */
    public AsyncContext getContext() {
        return context;
    }

    /**
     * @param context
     *            the context to set
     */
    public void setContext(AsyncContext context) {
        this.context = context;
    }

    @Override
    public void run() {

        ServletResponse resp = context.getResponse();
        PrintWriter writer = null;
        try {
            writer = resp.getWriter();
        } catch (IOException e1) {
        }

        writer.println("<br>*** AsyncRunnable running ***");

        async.utils.AsyncUtils.printInfo("*** AsyncRunnable before sleep ***", ((HttpServletRequest) context.getRequest()), (HttpServletResponse) resp);

        writer.println("<br>*** AsyncRunnable sleeping for " + millis * 2 + " milliseconds</p>");
        // Sleep to make sure the caller finishes.
        try {
            Thread.sleep(millis * 2);
        } catch (Exception e) {
        }

        writer.println("<br>*** AsyncRunnable back from sleep ***");

        async.utils.AsyncUtils.printInfo("*** AsyncRunnable after sleep ***", ((HttpServletRequest) context.getRequest()), (HttpServletResponse) resp);

        writer.println("<br>*** AsyncRunnable dispatching back to caller ***");

        writer.flush();

        context.dispatch("/AsyncStart?queryString=completeAsync");

    }

}
