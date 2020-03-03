/*******************************************************************************
 * Copyright (c) 2018, 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.el.fat.tests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;
import com.ibm.ws.el.fat.ELUtils;
import com.ibm.ws.el30.fat.servlets.EL30CollectionObjectOperationsServlet;

import componenttest.annotation.Server;
import componenttest.annotation.TestServlet;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;
import componenttest.topology.utils.FATServletClient;

/**
 * Test EL 3.0 Operations on List Collection Object
 */
@RunWith(FATRunner.class)
@Mode(TestMode.FULL)
public class EL30ListCollectionObjectOperationsTest extends FATServletClient {

    private static final String APP_NAME = "TestEL3.0";

    @Server("elServer")
    @TestServlet(servlet = EL30CollectionObjectOperationsServlet.class, contextRoot = APP_NAME)
    public static LibertyServer elServer;

    @BeforeClass
    public static void setup() throws Exception {
        if (!ELUtils.isAppInstalled(elServer, APP_NAME)) {
            ShrinkHelper.defaultDropinApp(elServer, APP_NAME + ".war", "com.ibm.ws.el30.fat.beans", "com.ibm.ws.el30.fat.servlets");
        }

        elServer.startServer(EL30ListCollectionObjectOperationsTest.class.getSimpleName() + ".log");
    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Stop the server
        if (elServer != null && elServer.isStarted()) {
            elServer.stopServer();
        }
    }
}
