/*******************************************************************************
 * Copyright (c) 2012, 2024 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *******************************************************************************/
package com.ibm.ws.jsp23.fat;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.ibm.ws.fat.util.FatLogHandler;
import com.ibm.ws.jsp23.fat.tests.JSP23JSP22ServerTest;
import com.ibm.ws.jsp23.fat.tests.JSPCdiTest;
import com.ibm.ws.jsp23.fat.tests.JSPExceptionTests;
import com.ibm.ws.jsp23.fat.tests.JSPJava11Test;
import com.ibm.ws.jsp23.fat.tests.JSPJava17Test;
import com.ibm.ws.jsp23.fat.tests.JSPJava21Test;
import com.ibm.ws.jsp23.fat.tests.JSPJava8Test;
import com.ibm.ws.jsp23.fat.tests.JSPPrepareJSPThreadCountDefaultValueTests;
import com.ibm.ws.jsp23.fat.tests.JSPPrepareJSPThreadCountNonDefaultValueTests;
import com.ibm.ws.jsp23.fat.tests.JSPSkipMetaInfTests;
import com.ibm.ws.jsp23.fat.tests.JSPTests;
import com.ibm.ws.jsp23.fat.tests.JSTLTests;

import componenttest.rules.repeater.EmptyAction;
import componenttest.rules.repeater.FeatureReplacementAction;
import componenttest.rules.repeater.RepeatTests;

/**
 * JSP 2.3 Tests
 *
 * The tests for both features should be included in this test component.
 */
@RunWith(Suite.class)
@SuiteClasses({
                JSPTests.class,
                JSPExceptionTests.class,
                JSPSkipMetaInfTests.class,
                JSPJava8Test.class,
                JSPJava11Test.class,
                JSPJava17Test.class,
                JSPJava21Test.class,
                JSPCdiTest.class,
                JSP23JSP22ServerTest.class,
                JSPPrepareJSPThreadCountNonDefaultValueTests.class,
                JSPPrepareJSPThreadCountDefaultValueTests.class,
                JSTLTests.class
})
public class FATSuite {

    /**
     * Run the tests again with the cdi-2.0 feature. Tests should be skipped where appropriate
     * using @SkipForRepeat("CDI-2.0").
     */
    @ClassRule
    public static RepeatTests repeat = RepeatTests.with(new EmptyAction().fullFATOnly())
                    .andWith(new FeatureReplacementAction("cdi-1.2", "cdi-2.0")
                                    .withID("CDI-2.0")
                                    .forceAddFeatures(false)
                                    .fullFATOnly())
                    .andWith(FeatureReplacementAction.EE9_FEATURES().conditionalFullFATOnly(FeatureReplacementAction.GREATER_THAN_OR_EQUAL_JAVA_11))
                    .andWith(FeatureReplacementAction.EE10_FEATURES().conditionalFullFATOnly(FeatureReplacementAction.GREATER_THAN_OR_EQUAL_JAVA_17))
                    .andWith(FeatureReplacementAction.EE11_FEATURES());

    /**
     * @see {@link FatLogHandler#generateHelpFile()}
     */
    @BeforeClass
    public static void generateHelpFile() {
        FatLogHandler.generateHelpFile();
    }

}
