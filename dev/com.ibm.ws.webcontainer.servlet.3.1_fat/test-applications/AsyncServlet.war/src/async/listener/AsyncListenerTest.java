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
package async.listener;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class AsyncListenerTest implements AsyncListener {
    private static final Logger LOG = Logger.getLogger(AsyncListenerTest.class.getName());

    @Override
    public void onComplete(AsyncEvent event) throws IOException {

        AsyncContext ac = event.getAsyncContext();
        ServletResponse resp = event.getSuppliedResponse();
        ServletRequest req = event.getSuppliedRequest();

        String test = (String) req.getAttribute("ListenerTest");
        if (test == null) {
            test = "None";
        }
        PrintWriter pw = resp.getWriter();

        if (test.equals("GRR")) {
            try {
                @SuppressWarnings("unused")
                ServletRequest getReq = ac.getRequest();
                LOG.info("*** In AyncListener, onComplete : getRequest worked ***");
                pw.println("*** In AyncListener, onComplete : getRequest worked ***");
            } catch (IllegalStateException ise) {
                LOG.info("*** In AyncListener, onComplete : getRequest threw an IllegalStateException ***");
                pw.println("*** In AyncListener, onComplete : getRequest threw an IllegalStateException ***");
            }

            try {
                @SuppressWarnings("unused")
                ServletResponse getResp = ac.getResponse();
                LOG.info("*** In AyncListener, onComplete : getResponse worked ***");
                pw.println("*** In AyncListener, onComplete : getResponse worked ***");
            } catch (IllegalStateException ise) {
                LOG.info("*** In AyncListener, onComplete : getResponse threw an IllegalStateException ***");
                pw.println("*** In AyncListener, onComplete : getResponse threw an IllegalStateException ***");
            }
        } else if (test.equals("isAsync")) {
            boolean isStarted = req.isAsyncStarted();
            LOG.info("*** In AyncListener, onComplete : isAsyncStarted = " + isStarted + " ***");
            pw.println("*** In AyncListener, onComplete : isAsyncStarted = " + isStarted + " ***");
        } else {
            LOG.info("*** In AyncListener, onComplete : invalid test value :  " + test + " ***");
            pw.println("*** In AyncListener, onComplete : invalid test value :  " + test + " ***");

        }

    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {

    }

    @Override
    public void onError(AsyncEvent event) throws IOException {

    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {

    }

}
