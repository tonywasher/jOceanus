/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2025 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.tethys.api.button;

import java.util.Locale;

import net.sourceforge.joceanus.oceanus.date.OceanusDatePeriod;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.OceanusEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;

/**
 * DateRange Selector.
 */
public interface TethysUIDateRangeSelector
        extends OceanusEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Is the panel visible?
     * @return true/false
     */
    boolean isVisible();

    /**
     * Obtain selected DateRange.
     * @return the selected date range
     */
    OceanusDateRange getRange();

    /**
     * Set the overall range for the control.
     * @param pRange the range
     */
    void setOverallRange(OceanusDateRange pRange);

    /**
     * Set the locale.
     * @param pLocale the locale
     */
    void setLocale(Locale pLocale);

    /**
     * Set period.
     * @param pPeriod the new period
     */
    void setPeriod(OceanusDatePeriod pPeriod);

    /**
     * Lock period.
     * @param isLocked true/false.
     */
    void lockPeriod(boolean isLocked);

    /**
     * Copy date selection from other box.
     * @param pSource the source box
     */
    void setSelection(TethysUIDateRangeSelector pSource);

    /**
     * Create SavePoint.
     */
    void createSavePoint();

    /**
     * Restore SavePoint.
     */
    void restoreSavePoint();
}
