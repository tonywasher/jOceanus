/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2021 Tony Washer
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
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
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
        extends TethysEventProvider<TethysUIEvent>, TethysComponent {
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
        private final TethysEventManager<TethysUIEvent> theEventManager;

        /**
         * The Attributes.
         */
        private final Map<TethysFieldAttribute, TethysFieldAttribute> theAttributes;

        /**
         * The id.
         */
        private Integer theId;

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
        public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
            return theEventManager.getEventRegistrar();
        }

        /**
         * Fire event.
         * @param pEventId the eventId
         */
        protected void fireEvent(final TethysUIEvent pEventId) {
            theEventManager.fireEvent(pEventId);
        }

        /**
         * Fire event.
         * @param pEventId the eventId
         * @param pValue the relevant value
         */
        protected void fireEvent(final TethysUIEvent pEventId, final Object pValue) {
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
                theEventManager.fireEvent(TethysUIEvent.NEWCOMMAND, mySelected.getValue());
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
    }

    /**
     * ValidatedTextFieldControl.
     * @param <T> the item class
     */
    interface TethysValidatedEditField<T> extends TethysDataEditField<T>, TethysValidatedField<T> {
    }

    /**
     * DataEditTextField base class.
     * @param <T> the data type
     */
    class TethysDataEditTextFieldControl<T> {
        /**
         * The InvalidValue Error Text.
         */
        private static final String ERROR_BADPARSE = TethysUIResource.PARSE_BADVALUE.getValue();

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

        /**
         * Set the validator.
         * <p>
         * This should validate the value and return null for OK, and an error text for failure
         * @param pValidator the validator
         */
        public void setValidator(final Function<T, String> pValidator) {
            theValidator = pValidator;
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
                    return false;
                }

                /* set the value and fire Event */
                setValue(myValue);
                theField.fireEvent(TethysUIEvent.NEWVALUE, myValue);
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
    interface TethysRawDecimalEditField extends TethysValidatedEditField<TethysDecimal>, TethysRawDecimalField {
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
    interface TethysCurrencyEditField<T extends TethysMoney> extends TethysValidatedEditField<T>, TethysCurrencyField {
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
    interface TethysIconButtonField<T> extends TethysDataEditField<T>, TethysIconButton<T> {
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
    interface TethysDateButtonField extends TethysDataEditField<TethysDate>, TethysDateButton {
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
    interface TethysScrollButtonField<T> extends TethysDataEditField<T>, TethysScrollButton<T> {
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
    interface TethysListButtonField<T extends Comparable<T>> extends TethysDataEditField<List<T>>, TethysListButton<T> {
    }
}
