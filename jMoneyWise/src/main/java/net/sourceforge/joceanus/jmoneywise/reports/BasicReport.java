/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.reports;

import java.util.Map;

import net.sourceforge.joceanus.jmoneywise.reports.HTMLBuilder.HTMLTable;
import net.sourceforge.joceanus.jmoneywise.views.Analysis;
import net.sourceforge.joceanus.jsortedlist.NestedHashMap;

import org.w3c.dom.Document;

/**
 * Interface provided by report builders.
 * @param <T> the delayed table type
 * @param <F> the filter type
 */
public abstract class BasicReport<T, F> {
    /**
     * The delayed map.
     */
    private final Map<String, DelayedTable> theDelayedMap;

    /**
     * The filter element map.
     */
    private final Map<String, F> theFilterMap;

    /**
     * Constructor.
     */
    protected BasicReport() {
        /* Allocate the hashMaps */
        theDelayedMap = new NestedHashMap<String, DelayedTable>();
        theFilterMap = new NestedHashMap<String, F>();
    }

    /**
     * Create the web document.
     * @param pAnalysis the analysis
     * @return Web document
     */
    protected abstract Document createReport(final Analysis pAnalysis);

    /**
     * Process a filter.
     * @param pSource the filter source
     */
    protected void processFilter(final F pSource) {
    }

    /**
     * Process a filtered reference.
     * @param pReference the reference
     */
    protected void processFilterReference(final String pReference) {
        /* Lookup the filter */
        F mySource = theFilterMap.get(pReference);
        if (mySource != null) {
            /* Process the filter */
            processFilter(mySource);
        }
    }

    /**
     * Process a delayed table reference.
     * @param pBuilder the HTML builder
     * @param pReference the reference
     * @return has the document changed true/false
     */
    protected boolean processDelayedReference(final HTMLBuilder pBuilder,
                                              final String pReference) {
        /* Lookup the delayed table and ignore if not found */
        DelayedTable myDelay = theDelayedMap.get(pReference);
        if (myDelay == null) {
            return false;
        }

        /* Remove the element from the map */
        theDelayedMap.remove(pReference);

        /* Create the delayed table */
        HTMLTable myTable = createDelayedTable(myDelay);

        /* Embed the table correctly */
        pBuilder.embedTable(myTable, myDelay.getId());

        /* Reformat text */
        return true;
    }

    /**
     * Create the delayed table.
     * @param pTable the delayed table definition
     * @return the newly created table
     */
    protected HTMLTable createDelayedTable(final DelayedTable pTable) {
        return null;
    }

    /**
     * Clear maps.
     */
    public void clearMaps() {
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
                                  final F pSelect) {
        /* Record into filter map */
        theFilterMap.put(HTMLBuilder.REF_FILTER
                         + pId, pSelect);
    }

    /**
     * Record delayed table.
     * @param pId the id for the table
     * @param pParent the parent table
     * @param pSource the selection object
     */
    protected void setDelayedTable(final String pId,
                                   final HTMLTable pParent,
                                   final T pSource) {
        /* Create the delayed table reference */
        DelayedTable myTable = new DelayedTable(pId, pParent, pSource);

        /* Record into selection map */
        theDelayedMap.put(HTMLBuilder.REF_DELAY
                          + pId, myTable);
    }

    /**
     * Simple element class for delayed tables.
     */
    protected final class DelayedTable {
        /**
         * The table id.
         */
        private final String theId;

        /**
         * The parent control.
         */
        private final HTMLTable theParent;

        /**
         * The table source.
         */
        private final T theSource;

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
        public HTMLTable getParent() {
            return theParent;
        }

        /**
         * Obtain the source.
         * @return the source
         */
        public T getSource() {
            return theSource;
        }

        /**
         * Constructor.
         * @param pId the table id
         * @param pParent the parent table.
         * @param pSource the source
         */
        private DelayedTable(final String pId,
                             final HTMLTable pParent,
                             final T pSource) {
            /* Store details */
            theId = pId;
            theParent = pParent;
            theSource = pSource;
        }
    }
}
