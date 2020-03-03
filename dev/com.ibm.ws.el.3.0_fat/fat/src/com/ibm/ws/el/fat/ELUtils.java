/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.el.fat;

import java.util.Set;
import java.util.logging.Logger;

import componenttest.topology.impl.LibertyServer;

/**
 * A utility class for EL tests.
 */
public class ELUtils {

    protected static final Class<?> c = ELUtils.class;
    private static final Logger LOG = Logger.getLogger(c.getName());

    /**
     * Determine if an application is already installed on a server.
     *
     * @param libertyServer The server to check for the specified application.
     * @param appName       The name of the application
     * @return True if the application was already installed, false otherwise
     * @throws Exception
     */
    public static boolean isAppInstalled(LibertyServer libertyServer, String appName) throws Exception {
        boolean appInstalled = false;
        if (libertyServer.isStarted()) {
            Set<String> installedApp = libertyServer.getInstalledAppNames(appName);
            if (!installedApp.isEmpty()) {
                appInstalled = true;
            }
        }
        LOG.info("The following app: " + appName + " is installed: " + appInstalled);
        return appInstalled;
    }
}
