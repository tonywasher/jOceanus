/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jtethys.ui.api.field;

import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICurrencyEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIRawDecimalEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIValidatedEditField;

/**
 * Field Factory.
 */
public interface TethysUIFieldFactory {
    /**
     * Obtain a new string data field.
     * @return the new field
     */
    TethysUIValidatedEditField<String> newStringField();

    /**
     * Obtain a new charArray data field.
     * @return the new field
     */
    TethysUIValidatedEditField<char[]> newCharArrayField();

    /**
     * Obtain a new short data field.
     * @return the new field
     */
    TethysUIValidatedEditField<Short> newShortField();

    /**
     * Obtain a new integer data field.
     * @return the new field
     */
    TethysUIValidatedEditField<Integer> newIntegerField();

    /**
     * Obtain a new long data field.
     * @return the new field
     */
    TethysUIValidatedEditField<Long> newLongField();

    /**
     * Obtain a new raw decimal data field.
     * @return the new field
     */
    TethysUIRawDecimalEditField newRawDecimalField();

    /**
     * Obtain a new money data field.
     * @return the new field
     */
    TethysUICurrencyEditField<TethysMoney> newMoneyField();

    /**
     * Obtain a new price data field.
     * @return the new field
     */
    TethysUICurrencyEditField<TethysPrice> newPriceField();

    /**
     * Obtain a new dilutedPrice data field.
     * @return the new field
     */
    TethysUICurrencyEditField<TethysDilutedPrice> newDilutedPriceField();

    /**
     * Obtain a new rate data field.
     * @return the new field
     */
    TethysUIValidatedEditField<TethysRate> newRateField();

    /**
     * Obtain a new units data field.
     * @return the new field
     */
    TethysUIValidatedEditField<TethysUnits> newUnitsField();

    /**
     * Obtain a new dilution data field.
     * @return the new field
     */
    TethysUIValidatedEditField<TethysDilution> newDilutionField();

    /**
     * Obtain a new ratio data field.
     * @return the new field
     */
    TethysUIValidatedEditField<TethysRatio> newRatioField();

    /**
     * Obtain a new date data field.
     * @return the new field
     */
    TethysUIDateButtonField newDateField();

    /**
     * Obtain a new scroll data field.
     * @param <T> the item type
     * @return the new field
     */
    <T> TethysUIScrollButtonField<T> newScrollField();

    /**
     * Obtain a new list data field.
     * @param <T> the item type
     * @return the new field
     */
    <T extends Comparable<T>> TethysUIListButtonField<T> newListField();

    /**
     * Obtain a new simple icon data field.
     * @param <T> the item type
     * @return the new field
     */
    <T> TethysUIIconButtonField<T> newIconField();

    /**
     * Obtain a new colour data field.
     * @return the new field
     */
    TethysUIDataEditField<String> newColorField();
}
