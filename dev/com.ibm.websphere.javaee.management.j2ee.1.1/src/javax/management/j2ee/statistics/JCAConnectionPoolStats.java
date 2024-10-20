/*******************************************************************************
 * Copyright (c) 2015 IBM Corporation and others.
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
package javax.management.j2ee.statistics;

/**
 * The JCAConnectionPoolStats interface specifies the statistics provided by a JCA
 * connection pool.
 */
public interface JCAConnectionPoolStats extends JCAConnectionStats {

    /*
     * Returns the number of connections closed.
     */
    public CountStatistic getCloseCount();

    /*
     * Returns the number of connections created.
     */
    public CountStatistic getCreateCount();

    /*
     * Returns the number of free connections in the pool.
     */
    public BoundedRangeStatistic getFreePoolSize();

    /*
     * Returns the size of the connection pool.
     */
    public BoundedRangeStatistic getPoolSize();

    /*
     * Returns the number of threads waiting for a connection.
     */
    public RangeStatistic getWaitingThreadCount();
}
