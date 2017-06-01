/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmetis.lethe.report;

import org.w3c.dom.Document;

import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportHTMLBuilder.MetisHTMLTable;
import net.sourceforge.joceanus.jmetis.lethe.report.MetisReportReferenceManager.DelayedTable;

/**
 * Interface provided by report builders.
 * @param <D> the data type type
 * @param <F> the filter type
 */
public abstract class MetisReportBase<D, F> {
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
     * @return the reference manager
     */
    protected MetisReportReferenceManager<F> getReferenceMgr() {
        return theReferenceMgr;
    }

    /**
     * Record filter for id.
     * @param pId the id for the selection
     * @param pSelect the selection object
     */
    public void setFilterForId(final String pId,
                               final Object pSelect) {
        theReferenceMgr.setFilterForId(pId, pSelect);
    }

    /**
     * Record delayed table.
     * @param pId the id for the table
     * @param pParent the parent table
     * @param pSource the selection object
     */
    protected void setDelayedTable(final String pId,
                                   final MetisHTMLTable pParent,
                                   final Object pSource) {
        theReferenceMgr.setDelayedTable(pId, pParent, pSource);
    }

    /**
     * Process a filter.
     * @param pSource the filter source
     * @return the Filter or null
     */
    public F processFilter(final Object pSource) {
        return null;
    }

    /**
     * Create the delayed table.
     * @param pTable the delayed table definition
     * @return the newly created table
     */
    public MetisHTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }

    /**
     * Create the web document.
     * @param pData the source data
     * @return Web document
     */
    public abstract Document createReport(D pData);
}
