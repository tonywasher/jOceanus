/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui.controls;

import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter;
import net.sourceforge.joceanus.jmoneywise.views.AnalysisFilter.AllFilter;
import net.sourceforge.joceanus.jtethys.event.swing.JEventPanel;

/**
 * All transactions Selection.
 */
public class AllSelect
        extends JEventPanel
        implements AnalysisFilterSelection {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 5007037268615984742L;

    /**
     * The filter.
     */
    private final transient AllFilter theFilter;

    /**
     * Constructor.
     */
    public AllSelect() {
        /* Create the filter */
        theFilter = AnalysisFilter.FILTER_ALL;
    }

    @Override
    public AllFilter getFilter() {
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
    public void setAnalysis(final Analysis pAnalysis) {
        /* Nothing to do */
    }

    @Override
    public void setFilter(final AnalysisFilter<?, ?> pFilter) {
        /* Nothing to do */
    }
}
