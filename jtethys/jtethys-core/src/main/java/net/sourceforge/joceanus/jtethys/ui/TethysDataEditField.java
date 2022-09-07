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
package net.sourceforge.joceanus.jtethys.ui;

import java.util.Currency;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilutedPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;

/**
 * Generic class for displaying and editing a data field.
 * @param <T> the data type
 */
public interface TethysDataEditField<T>
        extends TethysEventProvider<TethysXUIEvent>, TethysComponent {
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
     * Obtain the cast value.
     * @param pValue the value as object
     * @return the value
     */
    T getCastValue(Object pValue);

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
    void setCmdMenuConfigurator(Consumer<TethysScrollMenu<String>> pConfigurator);

    /**
     * Set the attribute state.
     * @param pAttr the attribute
     * @param pState the state
     */
    void setTheAttributeState(TethysFieldAttribute pAttr,
                              boolean pState);

    /**
     * Set the attribute.
     * @param pAttr the attribute
     */
    void setTheAttribute(TethysFieldAttribute pAttr);

    /**
     * Clear the attribute.
     * @param pAttr the attribute
     */
    void clearTheAttribute(TethysFieldAttribute pAttr);

    /**
     * Is the attribute set?
     * @param pAttr the attribute
     * @return true/false
     */
    boolean isAttributeSet(TethysFieldAttribute pAttr);

    /**
     * Adjust data field.
     */
    void adjustField();

    /**
     * Obtain the height.
     * @return the height
     */
    Integer getHeight();

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
     * DataEditConverter interface.
     * @param <T> the data type
     */
    abstract class TethysBaseDataEditField<T>
            implements TethysDataEditField<T> {
        /**
         * The Event Manager.
         */
        private final TethysEventManager<TethysXUIEvent> theEventManager;

        /**
         * The Attributes.
         */
        private final Map<TethysFieldAttribute, TethysFieldAttribute> theAttributes;

        /**
         * The id.
         */
        private final Integer theId;

        /**
         * Is the field editable?
         */
        private boolean isEditable;

        /**
         * The value.
         */
        private T theValue;

        /**
         * The CommandMenu.
         */
        private TethysScrollMenu<String> theCmdMenu;

        /**
         * The CommandMenu Configurator.
         */
        private Consumer<TethysScrollMenu<String>> theCmdMenuConfigurator = c -> {
        };

        /**
         * Constructor.
         * @param pFactory the GUI factory
         */
        protected TethysBaseDataEditField(final TethysGuiFactory pFactory) {
            /* Create event manager */
            theId = pFactory.getNextId();
            theEventManager = new TethysEventManager<>();
            theAttributes = new EnumMap<>(TethysFieldAttribute.class);
        }

        @Override
        public Integer getId() {
            return theId;
        }

        @Override
        public void setEditable(final boolean pEditable) {
            isEditable = pEditable;
        }

        @Override
        public boolean isEditable() {
            return isEditable;
        }

        @Override
        public void setValue(final T pValue) {
            setTheValue(pValue);
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected void setTheValue(final T pValue) {
            theValue = pValue;
        }

        @Override
        public void setTheAttributeState(final TethysFieldAttribute pAttr,
                                         final boolean pState) {
            if (pState) {
                setTheAttribute(pAttr);
            } else {
                clearTheAttribute(pAttr);
            }
        }

        @Override
        public void setTheAttribute(final TethysFieldAttribute pAttr) {
            theAttributes.put(pAttr, pAttr);
        }

        @Override
        public void clearTheAttribute(final TethysFieldAttribute pAttr) {
            theAttributes.remove(pAttr);
        }

        @Override
        public boolean isAttributeSet(final TethysFieldAttribute pAttr) {
            return theAttributes.containsKey(pAttr);
        }

        @Override
        public T getValue() {
            return theValue;
        }

        /**
         * Obtain the command menu.
         * @return the command menu
         */
        protected TethysScrollMenu<String> getCmdMenu() {
            return theCmdMenu;
        }

        @Override
        public void setCmdMenuConfigurator(final Consumer<TethysScrollMenu<String>> pConfigurator) {
            theCmdMenuConfigurator = pConfigurator;
        }

        @Override
        public TethysEventRegistrar<TethysXUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Fire event.
         * @param pEventId the eventId
         */
        protected void fireEvent(final TethysXUIEvent pEventId) {
            theEventManager.fireEvent(pEventId);
        }

        /**
         * Fire event.
         * @param pEventId the eventId
         * @param pValue the relevant value
         */
        protected void fireEvent(final TethysXUIEvent pEventId, final Object pValue) {
            theEventManager.fireEvent(pEventId, pValue);
        }

        /**
         * handleCmdMenuRequest.
         */
        protected void handleCmdMenuRequest() {
            /* fire menuBuild actionEvent */
            theCmdMenuConfigurator.accept(theCmdMenu);

            /* If a menu is provided */
            if (!theCmdMenu.isEmpty()) {
                /* Show the menu */
                showCmdMenu();
            }
        }

        /**
         * handleCmdMenuClosed.
         */
        protected void handleCmdMenuClosed() {
            /* If we selected a value */
            final TethysScrollMenuItem<String> mySelected = theCmdMenu.getSelectedItem();
            if (mySelected != null) {
                /* fire new command Event */
                theEventManager.fireEvent(TethysXUIEvent.NEWCOMMAND, mySelected.getValue());
            }
        }

        /**
         * Show the command menu.
         */
        protected abstract void showCmdMenu();

        /**
         * Declare command menu.
         * @param pMenu the menu
         */
        protected void declareCmdMenu(final TethysScrollMenu<String> pMenu) {
            /* Store the menu */
            theCmdMenu = pMenu;
        }
    }

    /**
     * ValidatedField.
     * @param <T> the item class
     */
    interface TethysValidatedField<T> {
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
    interface TethysValidatedEditField<T>
            extends TethysDataEditField<T>, TethysValidatedField<T> {
    }

    /**
     * DataEditTextField base class.
     * @param <T> the data type
     */
    class TethysDataEditTextFieldControl<T>
            implements TethysValidatedField<T> {
        /**
         * The InvalidValue Error Text.
         */
        private static final String ERROR_BADPARSE = TethysXUIResource.PARSE_BADVALUE.getValue();

        /**
         * The Field.
         */
        private final TethysBaseDataEditField<T> theField;

        /**
         * The DataConverter.
         */
        private final TethysDataEditConverter<T> theConverter;

        /**
         * The validator.
         */
        private Function<T, String> theValidator;

        /**
         * The reporter.
         */
        private Consumer<String> theReporter;

        /**
         * The base value.
         */
        private T theValue;

        /**
         * The display text.
         */
        private String theDisplay;

        /**
         * The edit text.
         */
        private String theEdit;

        /**
         * Error text.
         */
        private String theErrorText;

        /**
         * Did we create a new value?
         */
        private boolean parsedNewValue;

        /**
         * Constructor.
         * @param pField the owing field
         * @param pConverter the data converter
         */
        public TethysDataEditTextFieldControl(final TethysBaseDataEditField<T> pField,
                                              final TethysDataEditConverter<T> pConverter) {
            theField = pField;
            theConverter = pConverter;
            theValidator = p -> null;
        }

        /**
         * Obtain the display text.
         * @return the display text.
         */
        public String getDisplayText() {
            return theDisplay;
        }

        /**
         * Obtain the edit text.
         * @return the edit text.
         */
        public String getEditText() {
            if (theEdit == null) {
                theEdit = theConverter.formatEditValue(theValue);
            }
            return theEdit;
        }

        /**
         * Obtain the converter.
         * @return the converter.
         */
        public TethysDataEditConverter<T> getConverter() {
            return theConverter;
        }

        /**
         * Did we create a new value?
         * @return true/false
         */
        public boolean parsedNewValue() {
            return parsedNewValue;
        }

        /**
         * Clear the new value indication.
         */
        public void clearNewValue() {
            parsedNewValue = false;
            theErrorText = null;
        }

        @Override
        public void setValidator(final Function<T, String> pValidator) {
            theValidator = pValidator;
        }

        @Override
        public void setReporter(final Consumer<String> pReporter) {
            theReporter = pReporter;
        }

        /**
         * Obtain the error text.
         * @return the error
         */
        public String getErrorText() {
            return theErrorText;
        }

        /**
         * Process value.
         * @param pNewValue the new value
         * @return is value valid?
         */
        public boolean processValue(final String pNewValue) {
            /* Clear flags */
            clearNewValue();

            /* NullOp if there are no changes */
            if (Objects.equals(pNewValue, theEdit)) {
                /* Return success */
                theField.fireEvent(TethysXUIEvent.EDITFOCUSLOST, null);
                return true;
            }

            /* Protect to catch parsing errors */
            try {
                /* Parse the value */
                final T myValue = pNewValue == null
                                                    ? null
                                                    : theConverter.parseEditedValue(pNewValue);

                /* Invoke the validator and reject value if necessary */
                theErrorText = theValidator.apply(myValue);
                if (theErrorText != null) {
                    if (theReporter != null) {
                        theReporter.accept(theErrorText);
                    }
                    return false;
                }

                /* set the value and fire Event */
                setValue(myValue);
                theField.fireEvent(TethysXUIEvent.NEWVALUE, myValue);
                parsedNewValue = true;
                return true;

                /* Catch parsing error */
            } catch (IllegalArgumentException e) {
                theErrorText = ERROR_BADPARSE;
                return false;
            }
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        public void setValue(final T pValue) {
            /* Obtain display text */
            theDisplay = pValue == null
                                        ? null
                                        : theConverter.formatDisplayValue(pValue);

            /* Initialise edit text */
            theValue = pValue;
            theEdit = theConverter.formatEditValue(pValue);

            /* Store the value */
            theField.setTheValue(pValue);
        }
    }

    /**
     * RawDecimalTextField.
     */
    interface TethysRawDecimalField {
        /**
         * Set the Number of decimals supplier.
         * @param pSupplier the supplier
         */
        void setNumDecimals(IntSupplier pSupplier);
    }

    /**
     * RawDecimalTextFieldControl.
     */
    interface TethysRawDecimalEditField
            extends TethysValidatedEditField<TethysDecimal>, TethysRawDecimalField {
        @Override
        default TethysDecimal getCastValue(final Object pValue) {
            return (TethysDecimal) pValue;
        }
    }

    /**
     * CurrencyField.
     */
    interface TethysCurrencyField {
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
    interface TethysCurrencyEditField<T extends TethysMoney>
            extends TethysValidatedEditField<T>, TethysCurrencyField {
    }

    /**
     * StringTextFieldControl.
     */
    interface TethysStringEditField
            extends TethysDataEditField<String>, TethysValidatedEditField<String> {
        @Override
        default String getCastValue(final Object pValue) {
            return (String) pValue;
        }
    }

    /**
     * CharArrayTextFieldControl.
     */
    interface TethysCharArrayEditField
            extends TethysDataEditField<char[]>, TethysValidatedEditField<char[]> {
        @Override
        default char[] getCastValue(final Object pValue) {
            return (char[]) pValue;
        }
    }

    /**
     * ShortTextFieldControl.
     */
    interface TethysShortEditField
            extends TethysDataEditField<Short>, TethysValidatedEditField<Short> {
        @Override
        default Short getCastValue(final Object pValue) {
            return (Short) pValue;
        }
    }

    /**
     * IntegerTextFieldControl.
     */
    interface TethysIntegerEditField
            extends TethysDataEditField<Integer>, TethysValidatedEditField<Integer> {
        @Override
        default Integer getCastValue(final Object pValue) {
            return (Integer) pValue;
        }
    }

    /**
     * LongTextFieldControl.
     */
    interface TethysLongEditField
            extends TethysDataEditField<Long>, TethysValidatedEditField<Long> {
        @Override
        default Long getCastValue(final Object pValue) {
            return (Long) pValue;
        }
    }

    /**
     * MoneyTextFieldControl.
     */
    interface TethysMoneyEditField
            extends TethysDataEditField<TethysMoney>, TethysCurrencyEditField<TethysMoney> {
        @Override
        default TethysMoney getCastValue(final Object pValue) {
            return (TethysMoney) pValue;
        }
    }

    /**
     * PriceTextFieldControl.
     */
    interface TethysPriceEditField
            extends TethysDataEditField<TethysPrice>, TethysCurrencyEditField<TethysPrice> {
        @Override
        default TethysPrice getCastValue(final Object pValue) {
            return (TethysPrice) pValue;
        }
    }

    /**
     * DilutedPriceTextFieldControl.
     */
    interface TethysDilutedPriceEditField
            extends TethysDataEditField<TethysDilutedPrice>, TethysCurrencyEditField<TethysDilutedPrice> {
        @Override
        default TethysDilutedPrice getCastValue(final Object pValue) {
            return (TethysDilutedPrice) pValue;
        }
    }

    /**
     * UnitsTextFieldControl.
     */
    interface TethysUnitsEditField
            extends TethysDataEditField<TethysUnits>, TethysValidatedEditField<TethysUnits> {
        @Override
        default TethysUnits getCastValue(final Object pValue) {
            return (TethysUnits) pValue;
        }
    }

    /**
     * DilutionTextFieldControl.
     */
    interface TethysDilutionEditField
            extends TethysDataEditField<TethysDilution>, TethysValidatedEditField<TethysDilution> {
        @Override
        default TethysDilution getCastValue(final Object pValue) {
            return (TethysDilution) pValue;
        }
    }

    /**
     * RateTextFieldControl.
     */
    interface TethysRateEditField
            extends TethysDataEditField<TethysRate>, TethysValidatedEditField<TethysRate> {
        @Override
        default TethysRate getCastValue(final Object pValue) {
            return (TethysRate) pValue;
        }
    }

    /**
     * RatioTextFieldControl.
     */
    interface TethysRatioEditField
            extends TethysDataEditField<TethysRatio>, TethysValidatedEditField<TethysRatio> {
        @Override
        default TethysRatio getCastValue(final Object pValue) {
            return (TethysRatio) pValue;
        }
    }

    /**
     * IconButton Configuration.
     * @param <T> the data type
     */
    interface TethysIconButton<T> {
        /**
         * Set the IconMapSet supplier.
         * @param pSupplier the supplier
         */
        void setIconMapSet(Supplier<TethysIconMapSet<T>> pSupplier);
    }

    /**
     * IconButtonFieldControl.
     * @param <T> the data type
     */
    interface TethysIconButtonField<T>
            extends TethysDataEditField<T>, TethysIconButton<T> {
    }

    /**
     * DateButton Configuration.
     */
    interface TethysDateButton {
        /**
         * Set the dateConfig configurator.
         * @param pConfigurator the configurator
         */
        void setDateConfigurator(Consumer<TethysDateConfig> pConfigurator);
    }

    /**
     * DateButton Field.
     */
    interface TethysDateButtonField
            extends TethysDataEditField<TethysDate>, TethysDateButton {
        @Override
        default TethysDate getCastValue(final Object pValue) {
            return (TethysDate) pValue;
        }
    }

    /**
     * Scroll Button Configuration.
     * @param <T> the value type
     */
    interface TethysScrollButton<T> {
        /**
         * Set the menu configurator.
         * @param pConfigurator the configurator
         */
        void setMenuConfigurator(Consumer<TethysScrollMenu<T>> pConfigurator);
    }

    /**
     * Scroll Button Field.
     * @param <T> the value type
     */
    interface TethysScrollButtonField<T>
            extends TethysDataEditField<T>, TethysScrollButton<T> {
    }

    /**
     * List Button Configuration.
     * @param <T> the value type
     */
    interface TethysListButton<T extends Comparable<T>> {
        /**
         * Set the selectable supplier.
         * @param pSelectables the supplier
         */
        void setSelectables(Supplier<Iterator<T>> pSelectables);
    }

    /**
     * List Button Field.
     * @param <T> the value type
     */
    interface TethysListButtonField<T extends Comparable<T>>
            extends TethysDataEditField<List<T>>, TethysListButton<T> {
    }
}
