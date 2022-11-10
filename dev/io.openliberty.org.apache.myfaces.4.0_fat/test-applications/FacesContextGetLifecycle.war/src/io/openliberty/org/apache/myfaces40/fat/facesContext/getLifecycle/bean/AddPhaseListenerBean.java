/*******************************************************************************
 * Copyright (c) 2022 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package io.openliberty.org.apache.myfaces40.fat.facesContext.getLifecycle.bean;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;

/**
 * This bean is used to add a PhaseListener programmatically using the new FacesContext.getLifecycle() API
 * in Faces 4.0.
 */
@Named
@ApplicationScoped
public class AddPhaseListenerBean {
    boolean phaseListenerAdded = false;

    public void addPhaseListener() {
        FacesContext facesContext = FacesContext.getCurrentInstance();

        /*
         * if (!phaseListenerAdded) {
         * ProgrammaticallyAddedPhaseListener phaseListener = new ProgrammaticallyAddedPhaseListener();
         * facesContext.getLifecycle().addPhaseListener(phaseListener);
         * phaseListenerAdded = true;
         * }
         */
    }
}
