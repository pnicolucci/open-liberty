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
package async.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import async.listener.AsyncListenerTest;

@WebServlet(urlPatterns = "/AysncForwardTest", asyncSupported = true)
public class AsyncForwardTest extends HttpServlet {

    /**  */
    /*
     * @test_Strategy: Create a Servlet AsyncTestServlet which supports async;
     * Client send a request to AsyncTestServlet at "/AsyncTestServlet?testname=forwardTest";
     * getRequestDispatcher("/AsyncTestServlet?testname=forwardDummy").forward(request, response);
     * In forwardDummy:
     * AsyncContext ac = request.startAsync();
     * ac.dispatch();
     * verifies that it dispatches to "/AsyncTestServlet?testname=forwardTest".
     */

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(AsyncForwardTest.class.getName());
    private static final String FWD = "doForward";
    private static final String DISP = "doDispatch";
    private static final String FWD_OTHER = "doForwardToOther";

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        String variation = req.getParameter("Variation");
        if (variation == null)
            variation = "NULL";

        String test;
        test = req.getParameter("Test");
        if (test == null) {
            test = FWD;
        }
        LOG.info("AsyncForwardServlet : Entering. Test = " + test + ", Variation = " + variation);

        String dispatch = (String) (req.getAttribute("dispatch"));

        PrintWriter pw = res.getWriter();
        if (test.equals(FWD)) {

            if (variation.equals("Start")) {

                LOG.info("AsyncForwardServlet : About to forward the request to AsyncStart.");

                async.utils.AsyncUtils.logInfo("*** AsyncForwardServlet forward to AsyncStart ***", req, res);

                RequestDispatcher disp = req.getRequestDispatcher("/AsyncStart/startPathInfo?time=1000");
                disp.forward(req, res);

                async.utils.AsyncUtils.logInfo("*** AsyncForwardServlet return from forward to AsyncStart ***", req, res);

            } else if (variation.equals("Dispatch")) {
                LOG.info("AsyncForwardServlet : About to forward the request to AsyncDispatch.");

                async.utils.AsyncUtils.logInfo("*** AsyncForwardServlet forward to AsyncDispatch ***", req, res);

                RequestDispatcher disp = req.getRequestDispatcher("/AsyncDispatch/dispatchPathInfo?queryString=dispatch");
                disp.forward(req, res);

                async.utils.AsyncUtils.logInfo("*** AsyncForwardServlet return from forward to AsyncDispatch ***", req, res);

            } else {
                LOG.info("AsyncForwardServlet : About to forward the request to AysncForwardTest.");
                RequestDispatcher disp = req.getRequestDispatcher("/AysncForwardTest?Test=" + DISP + "&Variation=" + variation);
                disp.forward(req, res);
            }

        } else if (test.equals(FWD_OTHER)) {
            LOG.info("AsyncForwardServlet getting servlet context for: /AsyncServletOther");
            ServletContext ctx1 = req.getServletContext().getContext("/AsyncServletOther");
            if (ctx1 == null) {
                pw.println("*** AsyncForwardServlet failled to get context for /AsyncServletOther  ***");
                return;
            }

            LOG.info("AsyncForwardServlet getting RequestDispatcher for: /AsyncStart?time=1000");
            RequestDispatcher rd = ctx1.getRequestDispatcher("/AsyncStart?time=1000");
            if (rd != null) {
                LOG.info("AsyncForwardServlet forwarding to /AsyncStart?time=1000");
                pw.println("*** AsyncForwardServlet forwarding request to AsyncServletOther : /AsyncStart?time=1000  ***");
                rd.forward(req, res);
            } else {
                pw.println("*** AsyncForwardServlet failed to get requestDispatcher  for /AsyncServletOther/AsyncStart?time=1000  ***");
            }
        } else if (test.equals(DISP)) {

            if (variation.equals("jsp")) {

                LOG.info("AsyncForwardServlet : dispatch to jsp");
                pw.println("*** Inbound request dipsatches to /WEB-INF/jsp/AsyncDispatch.jsp ***");
                AsyncContext actx = req.startAsync(req, res);
                actx.dispatch("/WEB-INF/jsp/AsyncDispatch.jsp");
                actx.complete();

            } else if (dispatch == null) {

                // start async
                LOG.info("AsyncForwardServlet : Forwarded");
                AsyncContext ac = req.startAsync(req, res);
                if (variation.equals("GRRinFwd") || variation.equals("allGRR")) {
                    getRequestResponse(ac, pw, "*** In dispatcher, after req.startAsync() : ");
                } else if (variation.equals("isAsyncinFwd") || variation.equals("allisAsync")) {
                    checkisAsyncStarted(req, pw, "*** In dispatcher, after req.startAsync() : ");
                }
                if (variation.equals("GRRinLstnr") || variation.equals("allGRR")) {
                    AsyncListener acl = ac.createListener(AsyncListenerTest.class);
                    ac.addListener(acl, req, res);
                    req.setAttribute("ListenerTest", "GRR");
                    LOG.info("*** Forward Request async listener registered. ***");
                } else if (variation.equals("isAsyncinLstnr") || variation.equals("allisAsync")) {
                    AsyncListener acl = ac.createListener(AsyncListenerTest.class);
                    ac.addListener(acl, req, res);
                    req.setAttribute("ListenerTest", "isAsync");
                    LOG.info("*** Forward Request async listener registered. ***");
                }
                pw.println("*** Forward Request starts sync and dispatches. ***");
                req.setAttribute("dispatch", "completed");
                ac.dispatch();
                if (variation.equals("GRRafterDisp") || variation.equals("allGRR")) {
                    getRequestResponse(ac, pw, "*** In dispatcher, after ac.dispatch() : ");
                } else if (variation.equals("isAsyncafterDisp") || variation.equals("allisAsync")) {
                    checkisAsyncStarted(req, pw, "*** In dispatcher, after ac.dispatch() : ");
                }
            } else if (dispatch.equals("jsp")) {

                LOG.info("AsyncForwardServlet : dispatch to jsp");
                pw.println("*** Inbound request dipsatches to /WEB-INF/jsp/AsyncDispatch.jsp ***");
                AsyncContext actx = req.startAsync(req, res);
                actx.dispatch("/WEB-INF/jsp/AsyncDispatch.jsp");
                actx.complete();

            } else {
                LOG.info("AsyncForwardServlet : Async processing after forward");
                pw.println("*** Async after forward. ***");
                AsyncContext ac = req.getAsyncContext();
                if (variation.equals("GRRbeforeComp") || variation.equals("allGRR")) {
                    getRequestResponse(ac, pw, "*** In dispatched, before ac.complete() : ");
                } else if (variation.equals("isAsyncbeforeComp") || variation.equals("allisAsync")) {
                    checkisAsyncStarted(req, pw, "*** In dispatched, before ac.complete() : ");
                }
                ac.complete();
                if (variation.equals("GRRafterComp") || variation.equals("allGRR")) {
                    getRequestResponse(ac, pw, "*** In dispatched, after ac.complete() : ");
                } else if (variation.equals("isAsyncafterComp") || variation.equals("allisAsync")) {
                    checkisAsyncStarted(req, pw, "*** In dispatched, after ac.complete() : ");
                }
            }
        }

    }

    private void getRequestResponse(AsyncContext ac, PrintWriter pw, String when) {
        try {
            @SuppressWarnings("unused")
            ServletRequest req = ac.getRequest();
            if (pw == null) {
                LOG.info(when + "getRequest worked fine");
            } else {
                pw.println(when + "getRequest worked fine ***");
            }
        } catch (IllegalStateException exc) {
            if (pw == null) {
                LOG.info(when + "getRequest threw an IllegalStateException");
            } else {
                pw.println(when + "getRequest threw an IllegalStateException ***");
            }
        }
        try {
            @SuppressWarnings("unused")
            ServletResponse req = ac.getResponse();
            if (pw == null) {
                LOG.info(when + "getResponse worked fine");
            } else {
                pw.println(when + "getResponse worked fine ***");
            }
        } catch (IllegalStateException exc) {
            if (pw == null) {
                LOG.info(when + "getResponse threw an IllegalStateException");
            } else {
                pw.println(when + "getResponse threw an IllegalStateException ***");
            }
        }

    }

    private void checkisAsyncStarted(ServletRequest req, PrintWriter pw, String when) {
        boolean isStarted = req.isAsyncStarted();
        if (pw == null) {
            LOG.info(when + "isAsyncStarted = " + isStarted);
        } else {
            pw.println(when + "isAsyncStarted = " + isStarted + " ***");
        }
    }
}
