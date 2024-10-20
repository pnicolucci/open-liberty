/*******************************************************************************
 * Copyright (c) 2014, 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.security.oauth20.plugins.db;

import javax.naming.Context;

import org.junit.Before;
import org.junit.BeforeClass;

import com.ibm.ws.security.oauth.test.ClientRegistrationHelper;

import test.common.SharedOutputManager;

/**
 * This unit test is running with XOR enabled for the client secret
 */
public class CachedDBOidcClientProviderXORTest extends CachedDBOidcClientProviderTest {

    public CachedDBOidcClientProviderXORTest() {
        clientRegistrationHelper = new ClientRegistrationHelper(false);
    }

    @BeforeClass
    public static void setUpBeforeClass() throws Exception {

        outputMgr = SharedOutputManager.getInstance();
        outputMgr.captureStreams();

        System.setProperty(Context.INITIAL_CONTEXT_FACTORY, InitialContextFactoryMock.class.getName());
    }

    @Override
    @Before
    public void setupBefore() {
        _testName = testName.getMethodName();
        System.out.println("Entering test: " + _testName);
        clientRegistrationHelper.setHash(false);
        SAMPLE_CLIENTS = clientRegistrationHelper.getsampleOidcBaseClients(5, PROVIDER_NAME);

        CachedDBOidcClientProvider oidcBaseClientProvider = invokeConstructorAndInitialize();

        instantiateMockProvider();
        try {
            deleteAllClientsInDB(oidcBaseClientProvider);
            insertSampleClientsToDb(oidcBaseClientProvider, clientRegistrationHelper.isHash());

        } catch (Throwable t) {
            outputMgr.failWithThrowable(_testName, t);
        }
    }

    @Override
    protected CachedDBOidcClientProvider invokeConstructorAndInitialize() {
        CachedDBOidcClientProvider oidcBaseClientProvider = new CachedDBOidcClientProvider(PROVIDER_NAME, InitialContextFactoryMock.dsMock, SCHEMA_TABLE_NAME, null, null, EMPTY_STRING_ARR);
        oidcBaseClientProvider.initialize();

        return oidcBaseClientProvider;
    }

}
