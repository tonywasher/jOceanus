/*******************************************************************************
 * MoneyWise: Finance Application
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
package net.sourceforge.joceanus.moneywise.atlas.ui.controls;

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter;
import net.sourceforge.joceanus.moneywise.atlas.views.MoneyWiseXAnalysisFilter.MoneyWiseXAnalysisAllFilter;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

/**
 * All transactions Selection.
 */
public class MoneyWiseXAllSelect
        implements MoneyWiseXAnalysisFilterSelection {
    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The filter.
     */
    private final MoneyWiseXAnalysisAllFilter theFilter;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseXAllSelect(final TethysUIFactory<?> pFactory) {
        /* Create the filter */
        thePanel = pFactory.paneFactory().newHBoxPane();
        theFilter = new MoneyWiseXAnalysisAllFilter();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public MoneyWiseXAnalysisAllFilter getFilter() {
        return theFilter;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    /**
     * Create SavePoint.
     */
    protected void createSavePoint() {
        /* Nothing to do */
    }

    /**
     * Restore SavePoint.
     */
    protected void restoreSavePoint() {
        /* Nothing to do */
    }

    /**
     * Set analysis.
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseXAnalysis pAnalysis) {
        theFilter.setDateRange(pAnalysis.getDateRange());
    }

    @Override
    public void setFilter(final MoneyWiseXAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseXAnalysisAllFilter myFilter) {
            /* Set the dateRange */
            theFilter.setDateRange(myFilter.getDateRange());
        }
    }
}
