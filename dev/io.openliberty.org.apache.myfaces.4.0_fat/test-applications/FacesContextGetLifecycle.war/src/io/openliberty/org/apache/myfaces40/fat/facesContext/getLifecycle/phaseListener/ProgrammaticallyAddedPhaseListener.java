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
package io.openliberty.org.apache.myfaces40.fat.facesContext.getLifecycle.phaseListener;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIInput;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.NumberConverter;
import jakarta.faces.event.PhaseEvent;
import jakarta.faces.event.PhaseId;
import jakarta.faces.event.PhaseListener;

/**
 * This PhaseListener is added programmatically by the AddPhaseListenerBean.
 */
public class ProgrammaticallyAddedPhaseListener implements PhaseListener {

    private static final long serialVersionUID = 1L;

    @Override
    public void afterPhase(PhaseEvent event) {}

    @Override
    public void beforePhase(PhaseEvent event) {
        NumberConverter numberConverter = new NumberConverter();
        Object controlFrenchBad = null;
        Object controlNonFrench = null;
        Object controlFrenchCorrect = null;

        UIComponent comp = new UIInput();
        FacesContext context = event.getFacesContext();

        NumberFormat parserFrench = NumberFormat.getNumberInstance(Locale.FRENCH);
        NumberFormat parser = NumberFormat.getNumberInstance();
        //Object control;
        try {
            controlFrenchBad = parserFrench.parse("5.5");
            controlNonFrench = parser.parse("5.5");
            controlFrenchCorrect = parserFrench.parse("5,5");

            event.getFacesContext().getExternalContext().log("controlFrenchBad: " + controlFrenchBad.toString());
            event.getFacesContext().getExternalContext().log("controlNonFrench: " + controlNonFrench.toString());
            event.getFacesContext().getExternalContext().log("controlFrenchCorrect: " + controlFrenchCorrect.toString());
        } catch (ParseException pe) {
            event.getFacesContext().getExternalContext().log(pe.getMessage() + pe.getStackTrace());
        }

        Object resultRegular = numberConverter.getAsObject(context, comp, "5.5");
        event.getFacesContext().getExternalContext().log("resultRegular: " + resultRegular.toString());

        context.getViewRoot().setLocale(Locale.FRENCH);
        Object resultFrenchBad = numberConverter.getAsObject(context, comp, "5.5");
        event.getFacesContext().getExternalContext().log("resultFrenchBad: " + resultFrenchBad.toString());

        Object resultFrenchCorrect = numberConverter.getAsObject(context, comp, "5,5");
        event.getFacesContext().getExternalContext().log("resultFrenchCorrect: " + resultFrenchCorrect.toString());

        //event.getFacesContext().getExternalContext().log("Does result equal control? : " + control.equals(result));
    }

    @Override
    public PhaseId getPhaseId() {
        return PhaseId.RENDER_RESPONSE;
    }
}
