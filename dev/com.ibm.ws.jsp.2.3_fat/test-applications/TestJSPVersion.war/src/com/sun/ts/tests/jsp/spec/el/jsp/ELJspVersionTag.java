/*
 * Copyright (c) 2007, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package com.sun.ts.tests.jsp.spec.el.jsp;

import java.io.IOException;

import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class ELJspVersionTag extends SimpleTagSupport {

    private ValueExpression poundExpr;

    public void setPoundExpr(ValueExpression poundExpr) {
        this.poundExpr = poundExpr;
    }

    public void doTag() throws IOException {
        ELContext elContext = getJspContext().getELContext();
        JspWriter out = getJspContext().getOut();
        out.println("toString: " + poundExpr.toString());
        out.println("ValueReference: " + poundExpr.getValueReference(elContext));
        out.println("ExpressionString: " + poundExpr.getExpressionString());
        out.println("standard: " + poundExpr);

    }
}
