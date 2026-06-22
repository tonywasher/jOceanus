/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.moneywise.ui.base;

import io.github.tonywasher.joceanus.metis.ui.MetisErrorPanel;
import io.github.tonywasher.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataItem;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusDataEvent;
import io.github.tonywasher.joceanus.tethys.api.base.TethysUIComponent;

/**
 * Base Table interface.
 *
 * @param <T> the itemType
 */
public interface MoneyWiseUIBaseTableCtl<T extends PrometheusDataItem>
        extends OceanusEventProvider<PrometheusDataEvent>, TethysUIComponent {
    /**
     * Obtain the error panel.
     *
     * @return the error panel
     */
    MetisErrorPanel getErrorPanel();

    /**
     * is Valid name?
     *
     * @param pNewName the new name
     * @param pRow     the row
     * @return error message or null
     */
    String isValidName(String pNewName,
                       T pRow);

    /**
     * is Valid description?
     *
     * @param pNewDesc the new description
     * @param pRow     the row
     * @return error message or null
     */
    String isValidDesc(String pNewDesc,
                       T pRow);
}
