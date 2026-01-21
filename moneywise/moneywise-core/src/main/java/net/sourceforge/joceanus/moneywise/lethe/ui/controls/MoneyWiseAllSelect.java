/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.moneywise.lethe.ui.controls;

import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.moneywise.lethe.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisAllFilter;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;
import io.github.tonywasher.joceanus.tethys.api.factory.TethysUIFactory;
import io.github.tonywasher.joceanus.tethys.api.pane.TethysUIBoxPaneManager;

/**
 * All transactions Selection.
 */
public class MoneyWiseAllSelect
        implements MoneyWiseAnalysisFilterSelection {
    /**
     * The panel.
     */
    private final TethysUIBoxPaneManager thePanel;

    /**
     * The filter.
     */
    private final MoneyWiseAnalysisAllFilter theFilter;

    /**
     * Constructor.
     *
     * @param pFactory the GUI factory
     */
    protected MoneyWiseAllSelect(final TethysUIFactory<?> pFactory) {
        /* Create the filter */
        thePanel = pFactory.paneFactory().newHBoxPane();
        theFilter = new MoneyWiseAnalysisAllFilter();
    }

    @Override
    public TethysUIComponent getUnderlying() {
        return thePanel;
    }

    @Override
    public MoneyWiseAnalysisAllFilter getFilter() {
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
     *
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseAnalysis pAnalysis) {
        theFilter.setDateRange(pAnalysis.getDateRange());
    }

    @Override
    public void setFilter(final MoneyWiseAnalysisFilter<?, ?> pFilter) {
        /* If this is the correct filter type */
        if (pFilter instanceof MoneyWiseAnalysisAllFilter) {
            /* Access filter */
            final MoneyWiseAnalysisAllFilter myFilter = (MoneyWiseAnalysisAllFilter) pFilter;

            /* Set the dateRange */
            theFilter.setDateRange(myFilter.getDateRange());
        }
    }
}
