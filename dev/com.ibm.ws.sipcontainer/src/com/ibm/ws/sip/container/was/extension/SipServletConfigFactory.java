/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
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
package com.ibm.ws.sip.container.was.extension;

import javax.servlet.ServletConfig;

import com.ibm.wsspi.webcontainer.servlet.IServletConfig;
/**
 * The ServletConfig factory creating SipServletConfig   
 * @author Nitzan
 */
public interface SipServletConfigFactory {
	public IServletConfig createSipServletConfig(ServletConfig impl);
}
