/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.MoneyWiseAnalysisFilter.MoneyWiseAnalysisAllFilter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.factory.TethysUIFactory;
import net.sourceforge.joceanus.jtethys.ui.api.pane.TethysUIBoxPaneManager;

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
     * @param pAnalysis the analysis.
     */
    public void setAnalysis(final MoneyWiseAnalysis pAnalysis) {
        /* Nothing to do */
    }

    @Override
    public void setFilter(final MoneyWiseAnalysisFilter<?, ?> pFilter) {
        /* Nothing to do */
    }
}
