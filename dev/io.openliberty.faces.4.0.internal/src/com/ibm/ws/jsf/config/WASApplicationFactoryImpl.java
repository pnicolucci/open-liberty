/*******************************************************************************
 * Copyright (c) 2018, 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.ws.jsf.config;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.ibm.ws.jsf.extprocessor.JSFExtensionFactory;

import jakarta.faces.application.Application;
import jakarta.faces.application.ApplicationFactory;

/**
 * WAS custom application factory that initializes CDIJSFELResolver per application
 */
public class WASApplicationFactoryImpl extends ApplicationFactory {
    private static final Logger log = Logger.getLogger(WASApplicationFactoryImpl.class.getName());

    private final ApplicationFactory _applicationFactory;
    private Application _application;

    public WASApplicationFactoryImpl(ApplicationFactory applicationFactory) {
        this._applicationFactory = applicationFactory;
        if (log.isLoggable(Level.FINE)) {
            log.fine("New WASApplicationFactory instance created");
        }
    }

    @Override
    public Application getApplication() {
        if (_application == null) {
            synchronized (this) {
                if (_application == null) {
                    _application = new WASApplicationImpl(_applicationFactory.getApplication());
                    JSFExtensionFactory.initializeCDIJSFELContextListenerAndELResolver(_application);
                }
            }
        }
        return _application;
    }

    @Override
    public void setApplication(Application application) {
        synchronized (this) {
            if (application == null) {
                throw new NullPointerException("Cannot set a null application in the ApplicationFactory");
            }
            _application = application;
        }
    }

    @Override
    public ApplicationFactory getWrapped() {
        return _applicationFactory;
    }

}
