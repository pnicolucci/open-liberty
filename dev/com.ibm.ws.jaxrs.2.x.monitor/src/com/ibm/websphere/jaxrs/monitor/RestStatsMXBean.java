/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.ibm.websphere.jaxrs.monitor;

import com.ibm.websphere.monitor.jmx.Counter;
import com.ibm.websphere.monitor.jmx.StatisticsMeter;

/**
 * Management12 interface for MBeans with names of the form "WebSphere:type=RestStats,name=*"
 * where * is the name of a RESTful resource method within an application under the Liberty profile of the form <appName>.<resourceMethodName>. For example, myApp.DemoResource. One such MBean for each resource method in the system is available
 * from the Liberty profile platform MBean server when the monitor-1.0 feature is enabled. This interface can be used to request a proxy object via the {@link javax.management.JMX#newMMBeanProxy} method.
 * 
 * @ibm-api
 */
public interface RestStatsMXBean {

    /**
     * Retrieves the value of the read-only attribute Description, which is a description of the MBean itself.
     * 
     * @return description
     */
    public String getDescription();

    /**
     * Retrieves the value of the read-only attribute MethodName, the name of the resource method as specified in the deployment descriptor.
     * 
     * @return method name
     */
    public String getMethodName();

    /**
     * Retrieves the value of the read-only attribute RequestCount, the number of requests the server has received for this resource method.
     * 
     * @return request count
     */
    public long getRequestCount();

    /**
     * Retrieves the value of the read-only attribute RequestCountDetails, which provides other details on the request count.
     * 
     * @return request count details
     */
    public Counter getRequestCountDetails();

    /**
     * Retrieves the value of the read-only attribute ResponseTime, which is the average (mean) time spent responding to each request for the resource method.
     * 
     * @return response time
     */
    public double getResponseTime();

    /**
     * Retrieves the value of the read-only attribute ResponseCountDetails, which provides statistical details on the response time.
     * 
     * @return response time details
     */
    public StatisticsMeter getResponseTimeDetails();

    /**
     * Retrieves the value of the read-only attribute AppName, the name of the application of which the resource method belongs.
     * 
     * @return app name
     */
    public String getAppName();

}
