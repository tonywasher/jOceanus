/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.lethe.views.AnalysisFilter;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;

/**
 * Analysis Filter Selection.
 */
public interface MoneyWiseAnalysisFilterSelection
        extends TethysUIComponent {
    /**
     * Is selection available?
     * @return true/false
     */
    boolean isAvailable();

    /**
     * Obtain analysis filter.
     * @return the filter
     */
    AnalysisFilter<?, ?> getFilter();

    /**
     * Set analysis filter.
     * @param pFilter the filter
     */
    void setFilter(AnalysisFilter<?, ?> pFilter);
}