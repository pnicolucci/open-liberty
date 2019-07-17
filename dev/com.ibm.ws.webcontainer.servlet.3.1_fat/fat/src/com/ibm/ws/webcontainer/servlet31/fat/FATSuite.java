/*******************************************************************************
 * Copyright (c) 2012, 2014 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.webcontainer.servlet31.fat;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.fat.util.FatLogHandler;
import com.ibm.ws.webcontainer.servlet31.fat.tests.HttpSessionAttListenerHttpUnit;
import com.ibm.ws.webcontainer.servlet31.fat.tests.WCDummyTest;
import com.ibm.ws.webcontainer.servlet31.fat.tests.WCServerHttpUnit;
import com.ibm.ws.webcontainer.servlet31.fat.tests.WCServerTest;

/**
 * Servlet 3.1 Tests
 *
 * Make sure to add any new test classes to the @SuiteClasses
 * annotation.
 *
 * Make sure to distinguish full mode tests using
 * <code>@Mode(TestMode.FULL)</code>. Tests default to
 * use lite mode (<code>@Mode(TestMode.LITE)</code>).
 *
 * By default only lite mode tests are run. To also run
 * full mode tests a property must be specified using
 * -Dfat.test.mode=FULL.
 */
@RunWith(Suite.class)
@SuiteClasses({
                WCDummyTest.class,
                WCServerTest.class,
                //AsyncReadListenerHttpUnit.class,
                //AsyncWriteListenerHttpUnit.class,
                //UpgradeWriteListenerHttpUnit.class,
                //UpgradeReadListenerHttpUnit.class,
                //UpgradeReadWriteTimeoutHttpUnit.class,
                //VHServerHttpUnit.class,
                WCServerHttpUnit.class,
                //JSPServerHttpUnit.class,
                //DefaultErrorPageTest.class,
                HttpSessionAttListenerHttpUnit.class//,
                //CDITests.class,
                //CDIUpgradeHandlerTest.class,
                //CDIServletInterceptorTest.class,
                //CDIBeanInterceptorServletTest.class,
                //CDIListenersTest.class,
                //CDINoInjectionTest.class,
                //CDIServletFilterListenerDynamicTest.class,
                //CDIServletFilterListenerTest.class,
                //AsyncServletTest.class,
                //FormLoginReadListenerTest.class,
                //NBMultiReadTest.class
})

public class FATSuite {

    /**
     * @see {@link FatLogHandler#generateHelpFile()}
     */
    @BeforeClass
    public static void generateHelpFile() {
        FatLogHandler.generateHelpFile();
    }
}
