/*
 * Metis: Java Data Framework
 * Copyright 2026. Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package io.github.tonywasher.joceanus.metis.report;

import io.github.tonywasher.joceanus.metis.report.MetisReportHTMLBuilder.MetisReportHTMLTable;

/**
 * Simple element class for delayed tables.
 */
public final class MetisReportDelayedTable {
    /**
     * The table id.
     */
    private final String theId;

    /**
     * The parent control.
     */
    private final MetisReportHTMLTable theParent;

    /**
     * The table source.
     */
    private final Object theSource;

    /**
     * Constructor.
     *
     * @param pId     the table id
     * @param pParent the parent table.
     * @param pSource the source
     */
    MetisReportDelayedTable(final String pId,
                            final MetisReportHTMLTable pParent,
                            final Object pSource) {
        /* Store details */
        theId = pId;
        theParent = pParent;
        theSource = pSource;
    }

    /**
     * Obtain the id.
     *
     * @return the id
     */
    public String getId() {
        return theId;
    }

    /**
     * Obtain the parent.
     *
     * @return the parent
     */
    public MetisReportHTMLTable getParent() {
        return theParent;
    }

    /**
     * Obtain the source.
     *
     * @return the source
     */
    public Object getSource() {
        return theSource;
    }
}
