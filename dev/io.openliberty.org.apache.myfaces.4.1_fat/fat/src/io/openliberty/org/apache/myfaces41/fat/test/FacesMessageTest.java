/*******************************************************************************
 * Copyright (c) 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package io.openliberty.org.apache.myfaces41.fat.test;

import java.util.logging.Logger;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;

import com.ibm.websphere.simplicity.ShrinkHelper;

import componenttest.annotation.Server;
import componenttest.custom.junit.runner.FATRunner;
import componenttest.custom.junit.runner.Mode;
import componenttest.custom.junit.runner.Mode.TestMode;
import componenttest.topology.impl.LibertyServer;

/**
 * Test the following Faces 4.1 FacesMessage methods: equals(), hashcode(), toString().
 *
 * Specification issue: https://github.com/jakartaee/faces/issues/1823
 */
@Mode(TestMode.FULL)
@RunWith(FATRunner.class)
public class FacesMessageTest {
    private static final String APP_NAME = "FacesMessage";
    protected static final Class<?> c = FacesMessageTest.class;

    private static final Logger LOG = Logger.getLogger(FacesMessageTest.class.getName());

    @Rule
    public TestName name = new TestName();

    @Server("faces41_facesMessageServer")
    public static LibertyServer server;

    @BeforeClass
    public static void setup() throws Exception {
        ShrinkHelper.defaultDropinApp(server, APP_NAME + ".war");

        // Start the server and use the class name so we can find logs easily.
        server.startServer(FacesMessageTest.class.getSimpleName() + ".log");

    }

    @AfterClass
    public static void tearDown() throws Exception {
        // Stop the server
        if (server != null && server.isStarted()) {
            server.stopServer();
        }

    }

}
