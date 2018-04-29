/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2018 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.ui.controls;

import net.sourceforge.joceanus.jmoneywise.lethe.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter.AllFilter;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;

/**
 * All transactions Selection.
 * @param <N> the node type
 * @param <I> the Icon Type
 */
public class MoneyWiseAllSelect<N, I>
        implements MoneyWiseAnalysisFilterSelection<N> {
    /**
     * The panel.
     */
    private final TethysBoxPaneManager<N, I> thePanel;

    /**
     * The filter.
     */
    private final AllFilter theFilter;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MoneyWiseAllSelect(final TethysGuiFactory<N, I> pFactory) {
        /* Create the filter */
        thePanel = pFactory.newHBoxPane();
        theFilter = new AllFilter();
    }

    @Override
    public Integer getId() {
        return thePanel.getId();
    }

    @Override
    public N getNode() {
        return thePanel.getNode();
    }

    @Override
    public AllFilter getFilter() {
        return theFilter;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        thePanel.setEnabled(pEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
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
    public void setAnalysis(final Analysis pAnalysis) {
        /* Nothing to do */
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* Nothing to do */
    }
}
