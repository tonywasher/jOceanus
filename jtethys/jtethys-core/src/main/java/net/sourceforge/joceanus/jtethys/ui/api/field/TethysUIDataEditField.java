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

import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIComponent;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIDateButton;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIIconButton;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIListButton;
import net.sourceforge.joceanus.jtethys.ui.api.control.TethysUIControl.TethysUIScrollButton;
import net.sourceforge.joceanus.jtethys.ui.api.menu.TethysUIScrollMenu;

/**
 * Generic interface for displaying and editing a data field.
 * @param <T> the data type
 */
public interface TethysUIDataEditField<T>
        extends TethysEventProvider<TethysUIEvent>, TethysUIComponent {
    /**
     * Set Editable state.
     * @param pEditable true/false.
     */
    void setEditable(boolean pEditable);

    /**
     * Is the field editable?
     * @return true/false.
     */
    boolean isEditable();

    /**
     * Set the value.
     * @param pValue the value
     */
    void setValue(T pValue);

    /**
     * Obtain the value.
     * @return the value.
     */
    T getValue();

    /**
     * Show the command button.
     * @param pShow true/false
     */
    void showCmdButton(boolean pShow);

    /**
     * Set the command menu configurator.
     * @param pConfigurator the configurator.
     */
    void setCmdMenuConfigurator(Consumer<TethysUIScrollMenu<String>> pConfigurator);

    /**
     * Set the attribute state.
     * @param pAttr the attribute
     * @param pState the state
     */
    void setTheAttributeState(TethysUIFieldAttribute pAttr,
                              boolean pState);

    /**
     * Set the attribute.
     * @param pAttr the attribute
     */
    void setTheAttribute(TethysUIFieldAttribute pAttr);

    /**
     * Clear the attribute.
     * @param pAttr the attribute
     */
    void clearTheAttribute(TethysUIFieldAttribute pAttr);

    /**
     * Is the attribute set?
     * @param pAttr the attribute
     * @return true/false
     */
    boolean isAttributeSet(TethysUIFieldAttribute pAttr);

    /**
     * Adjust data field.
     */
    void adjustField();

    /**
     * Set the Preferred Width.
     * @param pWidth the width
     */
    void setPreferredWidth(Integer pWidth);

    /**
     * Set the Preferred Height.
     * @param pHeight the height
     */
    void setPreferredHeight(Integer pHeight);

    /**
     * ValidatedField.
     * @param <T> the item class
     */
    interface TethysUIValidatedField<T> {
        /**
         * Set the validator.
         * <p>
         * This should validate the value and return null for OK, and an error text for failure
         * @param pValidator the validator
         */
        void setValidator(Function<T, String> pValidator);

        /**
         * Set the reporter.
         * <p>
         * This should report the validation error
         * @param pReporter the reporter
         */
        void setReporter(Consumer<String> pReporter);
    }

    /**
     * ValidatedTextFieldControl.
     * @param <T> the item class
     */
    interface TethysUIValidatedEditField<T>
            extends TethysUIDataEditField<T>, TethysUIValidatedField<T> {
    }

    /**
     * RawDecimalTextField.
     */
    interface TethysUIRawDecimalField {
        /**
         * Set the Number of decimals supplier.
         * @param pSupplier the supplier
         */
        void setNumDecimals(IntSupplier pSupplier);
    }

    /**
     * RawDecimalTextFieldControl.
     */
    interface TethysUIRawDecimalEditField
            extends TethysUIValidatedEditField<TethysDecimal>, TethysUIRawDecimalField {
    }

    /**
     * CurrencyField.
     */
    interface TethysUICurrencyField {
        /**
         * Set the Deemed Currency supplier.
         * @param pSupplier the supplier
         */
        void setDeemedCurrency(Supplier<Currency> pSupplier);
    }

    /**
     * CurrencyTextFieldControl.
     * @param <T> the data type
     */
    interface TethysUICurrencyEditField<T extends TethysMoney>
            extends TethysUIValidatedEditField<T>, TethysUICurrencyField {
    }

    /**
     * StringTextFieldControl.
     */
    interface TethysUIStringEditField
            extends TethysUIDataEditField<String>, TethysUIValidatedEditField<String> {
    }

    /**
     * CharArrayTextFieldControl.
     */
    interface TethysUICharArrayEditField
            extends TethysUIDataEditField<char[]>, TethysUIValidatedEditField<char[]> {
    }

    /**
     * ShortTextFieldControl.
     */
    interface TethysUIShortEditField
            extends TethysUIDataEditField<Short>, TethysUIValidatedEditField<Short> {
    }

    /**
     * IntegerTextFieldControl.
     */
    interface TethysUIIntegerEditField
            extends TethysUIDataEditField<Integer>, TethysUIValidatedEditField<Integer> {
    }

    /**
     * LongTextFieldControl.
     */
    interface TethysUILongEditField
            extends TethysUIDataEditField<Long>, TethysUIValidatedEditField<Long> {
    }

    /**
     * MoneyTextFieldControl.
     */
    interface TethysUIMoneyEditField
            extends TethysUIDataEditField<TethysMoney>, TethysUICurrencyEditField<TethysMoney> {
    }

    /**
     * PriceTextFieldControl.
     */
    interface TethysUIPriceEditField
            extends TethysUIDataEditField<TethysPrice>, TethysUICurrencyEditField<TethysPrice> {
    }

    /**
     * UnitsTextFieldControl.
     */
    interface TethysUIUnitsEditField
            extends TethysUIDataEditField<TethysUnits>, TethysUIValidatedEditField<TethysUnits> {
    }

    /**
     * RateTextFieldControl.
     */
    interface TethysUIRateEditField
            extends TethysUIDataEditField<TethysRate>, TethysUIValidatedEditField<TethysRate> {
    }

    /**
     * DilutionTextFieldControl.
     */
    interface TethysUIDilutionEditField
            extends TethysUIDataEditField<TethysDilution>, TethysUIValidatedEditField<TethysDilution> {
    }

    /**
     * DilutedPriceTextFieldControl.
     */
    interface TethysUIDilutedPriceEditField
            extends TethysUIDataEditField<TethysDilutedPrice>, TethysUICurrencyEditField<TethysDilutedPrice> {
    }

    /**
     * RatioTextFieldControl.
     */
    interface TethysUIRatioEditField
            extends TethysUIDataEditField<TethysRatio>, TethysUIValidatedEditField<TethysRatio> {
    }

    /**
     * IconButtonFieldControl.
     * @param <T> the data type
     */
    interface TethysUIIconButtonField<T>
            extends TethysUIDataEditField<T>, TethysUIIconButton<T> {
    }

    /**
     * DateButton Field.
     */
    interface TethysUIDateButtonField
            extends TethysUIDataEditField<TethysDate>, TethysUIDateButton {
    }

    /**
     * ColorButton Field.
     */
    interface TethysUIColorButtonField
            extends TethysUIDataEditField<String> {
    }

    /**
     * Scroll Button Field.
     * @param <T> the value type
     */
    interface TethysUIScrollButtonField<T>
            extends TethysUIDataEditField<T>, TethysUIScrollButton<T> {
    }

    /**
     * List Button Field.
     * @param <T> the value type
     */
    interface TethysUIListButtonField<T extends Comparable<T>>
            extends TethysUIDataEditField<List<T>>, TethysUIListButton<T> {
    }
}
