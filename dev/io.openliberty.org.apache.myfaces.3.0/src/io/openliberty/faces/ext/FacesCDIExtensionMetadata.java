/*******************************************************************************n * Copyright (c) 2021 IBM Corporation and others.n * All rights reserved. This program and the accompanying materialsn * are made available under the terms of the Eclipse Public License v1.0n * which accompanies this distribution, and is available atn * http://www.eclipse.org/legal/epl-v10.htmln *n * Contributors:n *     IBM Corporation - initial API and implementationn *******************************************************************************/
package io.openliberty.faces.ext;

import io.openliberty.cdi.spi.CDIExtensionMetadata;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;

/**
 *
 */
@Component(service = CDIExtensionMetadata.class,
configurationPolicy = ConfigurationPolicy.IGNORE,
immediate = true,
property = { "service.vendor=IBM" })
public class FacesCDIExtensionMetadata implements CDIExtensionMetadata{
    
    @Override
    public Set<Class<?>> getBeanClasses() {
    	System.out.println("PAN: getBeanClasses FacesCDIExtensionMetadata!!");
        Set<Class<?>> beans = new HashSet<Class<?>>();
        beans.add(jakarta.faces.flow.builder.FlowDefinition.class);
        beans.add(jakarta.faces.flow.builder.FlowBuilderParameter.class);

        return beans;
    }

    @Override
    public Set<Class<? extends Annotation>> getBeanDefiningAnnotationClasses() {
    	System.out.println("PAN: getBeanDefiningAnnotationClasses FacesCDIExtensionMetadata!!");
        Set<Class<? extends Annotation>> BDAs = new HashSet<Class<? extends Annotation>>();
        BDAs.add(jakarta.faces.flow.FlowScoped.class);
        BDAs.add(jakarta.faces.view.ViewScoped.class);

        return BDAs;
    }


}
