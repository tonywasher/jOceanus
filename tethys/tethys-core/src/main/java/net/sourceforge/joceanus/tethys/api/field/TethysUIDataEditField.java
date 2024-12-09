/*******************************************************************************
 * Tethys: GUI Utilities
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.tethys.api.field;

import java.util.Currency;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusDecimal;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.tethys.api.base.TethysUIComponent;
import net.sourceforge.joceanus.tethys.api.base.TethysUIEvent;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIDateButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIIconButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIListButton;
import net.sourceforge.joceanus.tethys.api.control.TethysUIControl.TethysUIScrollButton;
import net.sourceforge.joceanus.tethys.api.menu.TethysUIScrollMenu;

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
     * Obtain the cast value.
     * @param pValue the value as object
     * @return the value
     */
    T getCastValue(Object pValue);

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
     * Obtain the height.
     * @return the height
     */
    Integer getHeight();

    @Override
    void setPreferredWidth(Integer pWidth);

    @Override
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
            extends TethysUIValidatedEditField<OceanusDecimal>, TethysUIRawDecimalField {
        @Override
        default OceanusDecimal getCastValue(final Object pValue) {
            return (OceanusDecimal) pValue;
        }
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
    interface TethysUICurrencyEditField<T extends OceanusMoney>
            extends TethysUIValidatedEditField<T>, TethysUICurrencyField {
    }

    /**
     * StringTextFieldControl.
     */
    interface TethysUIStringEditField
            extends TethysUIDataEditField<String>, TethysUIValidatedEditField<String> {
        @Override
        default String getCastValue(final Object pValue) {
            return (String) pValue;
        }
    }

    /**
     * StringTextAreaFieldControl.
     */
    interface TethysUIStringTextAreaField
            extends TethysUIDataEditField<String>, TethysUIValidatedEditField<String> {
        @Override
        default String getCastValue(final Object pValue) {
            return (String) pValue;
        }
    }

    /**
     * CharArrayTextFieldControl.
     */
    interface TethysUICharArrayEditField
            extends TethysUIDataEditField<char[]>, TethysUIValidatedEditField<char[]> {
        @Override
        default char[] getCastValue(final Object pValue) {
            return (char[]) pValue;
        }
    }

    /**
     * StringTextAreaFieldControl.
     */
    interface TethysUICharArrayTextAreaField
            extends TethysUIDataEditField<char[]>, TethysUIValidatedEditField<char[]> {
        @Override
        default char[] getCastValue(final Object pValue) {
            return (char[]) pValue;
        }
    }

    /**
     * ShortTextFieldControl.
     */
    interface TethysUIShortEditField
            extends TethysUIDataEditField<Short>, TethysUIValidatedEditField<Short> {
        @Override
        default Short getCastValue(final Object pValue) {
            return (Short) pValue;
        }
    }

    /**
     * IntegerTextFieldControl.
     */
    interface TethysUIIntegerEditField
            extends TethysUIDataEditField<Integer>, TethysUIValidatedEditField<Integer> {
        @Override
        default Integer getCastValue(final Object pValue) {
            return (Integer) pValue;
        }
    }

    /**
     * LongTextFieldControl.
     */
    interface TethysUILongEditField
            extends TethysUIDataEditField<Long>, TethysUIValidatedEditField<Long> {
        @Override
        default Long getCastValue(final Object pValue) {
            return (Long) pValue;
        }
    }

    /**
     * MoneyTextFieldControl.
     */
    interface TethysUIMoneyEditField
            extends TethysUIDataEditField<OceanusMoney>, TethysUICurrencyEditField<OceanusMoney> {
        @Override
        default OceanusMoney getCastValue(final Object pValue) {
            return (OceanusMoney) pValue;
        }
    }

    /**
     * PriceTextFieldControl.
     */
    interface TethysUIPriceEditField
            extends TethysUIDataEditField<OceanusPrice>, TethysUICurrencyEditField<OceanusPrice> {
        @Override
        default OceanusPrice getCastValue(final Object pValue) {
            return (OceanusPrice) pValue;
        }
    }

    /**
     * UnitsTextFieldControl.
     */
    interface TethysUIUnitsEditField
            extends TethysUIDataEditField<OceanusUnits>, TethysUIValidatedEditField<OceanusUnits> {
        @Override
        default OceanusUnits getCastValue(final Object pValue) {
            return (OceanusUnits) pValue;
        }
    }

    /**
     * RateTextFieldControl.
     */
    interface TethysUIRateEditField
            extends TethysUIDataEditField<OceanusRate>, TethysUIValidatedEditField<OceanusRate> {
        @Override
        default OceanusRate getCastValue(final Object pValue) {
            return (OceanusRate) pValue;
        }
    }

    /**
     * RatioTextFieldControl.
     */
    interface TethysUIRatioEditField
            extends TethysUIDataEditField<OceanusRatio>, TethysUIValidatedEditField<OceanusRatio> {
        @Override
        default OceanusRatio getCastValue(final Object pValue) {
            return (OceanusRatio) pValue;
        }
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
            extends TethysUIDataEditField<OceanusDate>, TethysUIDateButton {
        @Override
        default OceanusDate getCastValue(final Object pValue) {
            return (OceanusDate) pValue;
        }
    }

    /**
     * ColorButton Field.
     */
    interface TethysUIColorButtonField
            extends TethysUIDataEditField<String> {
        @Override
        default String getCastValue(final Object pValue) {
            return (String) pValue;
        }
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
    interface TethysUIListButtonField<T extends Comparable<? super T>>
            extends TethysUIDataEditField<List<T>>, TethysUIListButton<T> {
    }
}
