/*
 * Metis: Java Data Framework
 * Copyright 2012-2026. Tony Washer
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
import org.w3c.dom.Document;

/**
 * Interface provided by report builders.
 *
 * @param <D> the data type
 * @param <F> the filter type
 */
public abstract class MetisReportBase<D, F>
        implements MetisReportControl<F> {
    /**
     * Reference Manager.
     */
    private final MetisReportReferenceManager<F> theReferenceMgr;

    /**
     * Constructor.
     */
    protected MetisReportBase() {
        theReferenceMgr = new MetisReportReferenceManager<>(this);
    }

    /**
     * Obtain the reference manager.
     *
     * @return the reference manager
     */
    protected MetisReportReferenceManager<F> getReferenceMgr() {
        return theReferenceMgr;
    }

    /**
     * Record filter for id.
     *
     * @param pId     the id for the selection
     * @param pSelect the selection object
     */
    public void setFilterForId(final String pId,
                               final Object pSelect) {
        theReferenceMgr.setFilterForId(pId, pSelect);
    }

    /**
     * Record delayed table.
     *
     * @param pId     the id for the table
     * @param pParent the parent table
     * @param pSource the selection object
     */
    protected void setDelayedTable(final String pId,
                                   final MetisReportHTMLTable pParent,
                                   final Object pSource) {
        theReferenceMgr.setDelayedTable(pId, pParent, pSource);
    }

    /**
     * Create the web document.
     *
     * @param pData the source data
     * @return Web document
     */
    public abstract Document createReport(D pData);
}
