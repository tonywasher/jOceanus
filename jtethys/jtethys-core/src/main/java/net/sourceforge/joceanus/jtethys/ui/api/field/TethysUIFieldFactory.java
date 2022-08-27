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
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICharArrayEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIColorButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUICurrencyEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDilutedPriceEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIDilutionEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIIntegerEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIListButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUILongEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIMoneyEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIPriceEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIRateEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIRatioEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIRawDecimalEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIShortEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIStringEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIUnitsEditField;
import net.sourceforge.joceanus.jtethys.ui.api.field.TethysUIDataEditField.TethysUIValidatedEditField;

/**
 * Field Factory.
 */
public interface TethysUIFieldFactory {
    /**
     * Obtain a new string data field.
     * @return the new field
     */
    TethysUIStringEditField newStringField();

    /**
     * Obtain a new charArray data field.
     * @return the new field
     */
    TethysUICharArrayEditField newCharArrayField();

    /**
     * Obtain a new short data field.
     * @return the new field
     */
    TethysUIShortEditField newShortField();

    /**
     * Obtain a new integer data field.
     * @return the new field
     */
    TethysUIIntegerEditField newIntegerField();

    /**
     * Obtain a new long data field.
     * @return the new field
     */
    TethysUILongEditField newLongField();

    /**
     * Obtain a new raw decimal data field.
     * @return the new field
     */
    TethysUIRawDecimalEditField newRawDecimalField();

    /**
     * Obtain a new money data field.
     * @return the new field
     */
    TethysUIMoneyEditField newMoneyField();

    /**
     * Obtain a new price data field.
     * @return the new field
     */
    TethysUIPriceEditField newPriceField();

    /**
     * Obtain a new dilutedPrice data field.
     * @return the new field
     */
    TethysUIDilutedPriceEditField newDilutedPriceField();

    /**
     * Obtain a new rate data field.
     * @return the new field
     */
    TethysUIRateEditField newRateField();

    /**
     * Obtain a new units data field.
     * @return the new field
     */
    TethysUIUnitsEditField newUnitsField();

    /**
     * Obtain a new dilution data field.
     * @return the new field
     */
    TethysUIDilutionEditField newDilutionField();

    /**
     * Obtain a new ratio data field.
     * @return the new field
     */
    TethysUIRatioEditField newRatioField();

    /**
     * Obtain a new date data field.
     * @return the new field
     */
    TethysUIDateButtonField newDateField();

    /**
     * Obtain a new scroll data field.
     * @param <T> the item type
     * @param pClazz the value class
     * @return the new field
     */
    <T> TethysUIScrollButtonField<T> newScrollField(Class<T> pClazz);

    /**
     * Obtain a new list data field.
     * @param <T> the item type
     * @return the new field
     */
    <T extends Comparable<T>> TethysUIListButtonField<T> newListField();

    /**
     * Obtain a new simple icon data field.
     * @param <T> the item type
     * @param pClazz the value class
     * @return the new field
     */
    <T> TethysUIIconButtonField<T> newIconField(Class<T> pClazz);

    /**
     * Obtain a new colour data field.
     * @return the new field
     */
    TethysUIColorButtonField newColorField();
}
