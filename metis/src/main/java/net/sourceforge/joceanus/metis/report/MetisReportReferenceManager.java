/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.metis.report;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.metis.report.MetisReportHTMLBuilder.MetisHTMLTable;

/**
 * Reference Manager for report builders.
 * @param <F> the filter type
 */
public class MetisReportReferenceManager<F> {
    /**
     * The report.
     */
    private final MetisReportBase<?, F> theReport;

    /**
     * The delayed map.
     */
    private final Map<String, DelayedTable> theDelayedMap;

    /**
     * The filter element map.
     */
    private final Map<String, Object> theFilterMap;

    /**
     * Constructor.
     * @param pReport the report
     */
    protected MetisReportReferenceManager(final MetisReportBase<?, F> pReport) {
        /* Store parameters */
        theReport = pReport;

        /* Allocate the hashMaps */
        theDelayedMap = new HashMap<>();
        theFilterMap = new HashMap<>();
    }

    /**
     * Process a filtered reference.
     * @param pReference the reference
     * @return the filter (or null)
     */
    protected F processFilterReference(final String pReference) {
        /* Lookup the filter */
        final Object mySource = theFilterMap.get(pReference);
        return (mySource != null)
                                  ? theReport.processFilter(mySource)
                                  : null;
    }

    /**
     * Process a delayed table reference.
     * @param pBuilder the HTML builder
     * @param pReference the reference
     * @return has the document changed true/false
     */
    protected boolean processDelayedReference(final MetisReportHTMLBuilder pBuilder,
                                              final String pReference) {
        /* Lookup the delayed table and ignore if not found */
        final DelayedTable myDelay = theDelayedMap.get(pReference);
        if (myDelay == null) {
            return false;
        }

        /* Remove the element from the map */
        theDelayedMap.remove(pReference);

        /* Create the delayed table */
        final MetisHTMLTable myTable = theReport.createDelayedTable(myDelay);

        /* Embed the table correctly */
        pBuilder.embedTable(myTable, myDelay.getId());

        /* Reformat text */
        return true;
    }

    /**
     * Clear maps.
     */
    protected void clearMaps() {
        /* Clear the maps */
        theFilterMap.clear();
        theDelayedMap.clear();
    }

    /**
     * Record filter for id.
     * @param pId the id for the selection
     * @param pSelect the selection object
     */
    protected void setFilterForId(final String pId,
                                  final Object pSelect) {
        /* Record into filter map */
        theFilterMap.put(MetisReportHTMLBuilder.REF_FILTER
                         + pId, pSelect);
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
        /* Create the delayed table reference */
        final DelayedTable myTable = new DelayedTable(pId, pParent, pSource);

        /* Record into selection map */
        theDelayedMap.put(MetisReportHTMLBuilder.REF_DELAY
                          + pId, myTable);
    }

    /**
     * Simple element class for delayed tables.
     */
    public static final class DelayedTable {
        /**
         * The table id.
         */
        private final String theId;

        /**
         * The parent control.
         */
        private final MetisHTMLTable theParent;

        /**
         * The table source.
         */
        private final Object theSource;

        /**
         * Constructor.
         * @param pId the table id
         * @param pParent the parent table.
         * @param pSource the source
         */
        private DelayedTable(final String pId,
                             final MetisHTMLTable pParent,
                             final Object pSource) {
            /* Store details */
            theId = pId;
            theParent = pParent;
            theSource = pSource;
        }

        /**
         * Obtain the id.
         * @return the id
         */
        public String getId() {
            return theId;
        }

        /**
         * Obtain the parent.
         * @return the parent
         */
        public MetisHTMLTable getParent() {
            return theParent;
        }

        /**
         * Obtain the source.
         * @return the source
         */
        public Object getSource() {
            return theSource;
        }
    }
}
