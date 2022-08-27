/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jmetis.atlas.ui;

import java.util.Currency;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateConfig;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.ui.TethysAlignment;
import net.sourceforge.joceanus.jtethys.ui.TethysBorderPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateButton;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconButton;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListButton;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButton;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollButtonField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysValidatedEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysValidatedField;
import net.sourceforge.joceanus.jtethys.ui.TethysFieldAttribute;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysIconButtonManager.TethysIconMapSet;
import net.sourceforge.joceanus.jtethys.ui.TethysLabel;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysXUIEvent;

/**
 * FieldSet Panel Item.
 *
 * @param <T> the item type
 */
public abstract class MetisFieldSetPanelItem<T>
        implements TethysComponent {
    /**
     * The panel.
     */
    private final MetisFieldSetPanel thePanel;

    /**
     * The node.
     */
    private final TethysBorderPaneManager theNode;

    /**
     * The label.
     */
    private final TethysLabel theLabel;

    /**
     * The edit field.
     */
    private final TethysDataEditField<T> theEdit;

    /**
     * The item class.
     */
    private final Class<T> theClazz;

    /**
     * The field definition.
     */
    private final MetisDataFieldId theField;

    /**
     * Is the field visible?
     */
    private boolean isVisible;

    /**
     * Is the field readOnly?
     */
    private boolean isReadOnly;

    /**
     * Constructor.
     *
     * @param pPanel the panel
     * @param pField the field definition
     * @param pEdit  the edit field
     */
    MetisFieldSetPanelItem(final MetisFieldSetPanel pPanel,
                           final MetisDataFieldId pField,
                           final TethysDataEditField<T> pEdit) {
        /* Set fields */
        this(pPanel, pField, null, pEdit);
    }

    /**
     * Constructor.
     *
     * @param pPanel the panel
     * @param pField the field definition
     * @param pClazz the item class
     * @param pEdit  the edit field
     */
    MetisFieldSetPanelItem(final MetisFieldSetPanel pPanel,
                           final MetisDataFieldId pField,
                           final Class<T> pClazz,
                           final TethysDataEditField<T> pEdit) {
        /* Set fields */
        thePanel = pPanel;
        theField = pField;
        theClazz = pClazz;
        theEdit = pEdit;
        isVisible = true;

        /* Obtain the GuiFactory */
        final TethysGuiFactory myGuiFactory = pPanel.getGuiFactory();

        /* Create the label */
        theLabel = myGuiFactory.newLabel();
        theLabel.setText(pField.getId() + TethysLabel.STR_COLON);
        theLabel.setAlignment(TethysAlignment.EAST);

        /* Create the Node */
        theNode = myGuiFactory.newBorderPane();
        theNode.setEast(theLabel);
        theNode.setCentre(pEdit);

        /* Add listeners */
        final TethysEventRegistrar<TethysXUIEvent> myRegistrar = theEdit.getEventRegistrar();
        myRegistrar.addEventListener(TethysXUIEvent.NEWVALUE, this::cascadeEvent);
        myRegistrar.addEventListener(TethysXUIEvent.PREPARECMDDIALOG, this::cascadeEvent);
        myRegistrar.addEventListener(TethysXUIEvent.NEWCOMMAND, this::cascadeEvent);
    }

    /**
     * obtain the panel.
     *
     * @return the panel
     */
    protected MetisFieldSetPanel getPanel() {
        return thePanel;
    }

    @Override
    public Integer getId() {
        return theNode.getId();
    }

    @Override
    public TethysNode getNode() {
        return theNode.getNode();
    }

    /**
     * Obtain the border pane.
     *
     * @return the pane
     */
    TethysBorderPaneManager getBorderPane() {
        return theNode;
    }

    @Override
    public void setVisible(final boolean pVisible) {
        /* Record visibility */
        isVisible = pVisible;

        /* Set node visibility */
        thePanel.getGuiFactory().setNodeVisible(theNode, pVisible);
    }

    /**
     * Obtain edit field.
     *
     * @return the edit field
     */
    TethysDataEditField<T> getEditField() {
        return theEdit;
    }

    /**
     * obtain the field.
     *
     * @return the field
     */
    public MetisDataFieldId getField() {
        return theField;
    }

    /**
     * is the field visible?
     *
     * @return true/false
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * is the field readOnly?
     *
     * @return true/false
     */
    boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theEdit.setEnabled(pEnabled);
    }

    /**
     * Set the readOnly state of the item.
     *
     * @param pReadOnly true/false
     */
    public void setReadOnly(final boolean pReadOnly) {
        /* If we are changing state */
        if (pReadOnly != isReadOnly) {
            /* Set new state */
            isReadOnly = pReadOnly;

            /* If we need to change editing state */
            if (isReadOnly && thePanel.isEditable()) {
                setEditable(false);
            }
        }
    }

    /**
     * Show/Hide the command button.
     *
     * @param pShow true/false
     */
    public void showCmdButton(final boolean pShow) {
        theEdit.showCmdButton(pShow);
    }

    /**
     * Set the value.
     *
     * @param pValue    the value
     * @param isChanged is the value changed?
     * @return is the field visible
     */
    protected boolean setValue(final Object pValue,
                               final boolean isChanged) {
        /* Determine whether we should show the field */
        final boolean showField = pValue == null
                                  ? thePanel.isEditable() && !isReadOnly
                                  : isInstance(pValue);
        setVisible(showField);

        /* If we are showing the field */
        if (showField) {
            /* Set the value */
            theEdit.setValue(getCastValue(pValue));

            /* Set attribute */
            theEdit.setTheAttributeState(TethysFieldAttribute.CHANGED, isChanged);
        }

        /* return whether the field is visible */
        return showField;
    }

    /**
     * Obtain the cast value.
     *
     * @param pValue the value
     * @return the cast value
     */
    protected T getCastValue(final Object pValue) {
        return theClazz.cast(pValue);
    }

    /**
     * Is the object an instance of the class.
     *
     * @param pValue the value
     * @return the cast value
     */
    protected boolean isInstance(final Object pValue) {
        return theClazz.isInstance(pValue);
    }

    /**
     * Set the editable state.
     *
     * @param pEditable the editable state
     */
    protected void setEditable(final boolean pEditable) {
        theEdit.setEditable(pEditable);
    }

    /**
     * Get label width.
     *
     * @return the label width
     */
    int getLabelWidth() {
        return theLabel.getWidth();
    }

    /**
     * Set label width.
     *
     * @param pWidth the label width
     */
    void setLabelWidth(final int pWidth) {
        theLabel.setPreferredWidth(pWidth);
    }

    /**
     * Cascade the event.
     *
     * @param pEvent the event
     */
    private void cascadeEvent(final TethysEvent<TethysXUIEvent> pEvent) {
        final MetisFieldUpdate myUpdate = new MetisFieldUpdate(theField, pEvent.getDetails());
        thePanel.fireEvent(pEvent.getEventId(), myUpdate);
    }

    /**
     * ValidatedItem.
     *
     * @param <T> the item type
     */
    public static class MetisFieldSetValidatedItem<T>
            extends MetisFieldSetPanelItem<T>
            implements TethysValidatedField<T> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         * @param pClazz the item class
         * @param pEdit  the edit field
         */
        MetisFieldSetValidatedItem(final MetisFieldSetPanel pPanel,
                                   final MetisDataFieldId pField,
                                   final Class<T> pClazz,
                                   final TethysValidatedEditField<T> pEdit) {
            /* Initialise underlying class */
            super(pPanel, pField, pClazz, pEdit);
        }

        @Override
        public void setValidator(final Function<T, String> pValidator) {
            ((TethysValidatedEditField<T>) getEditField()).setValidator(pValidator);
        }

        @Override
        public void setReporter(final Consumer<String> pReporter) {
            ((TethysValidatedEditField<T>) getEditField()).setReporter(pReporter);
        }
    }

    /**
     * CurrencyItem.
     *
     * @param <T> the item type
     */
    public static class MetisFieldSetCurrencyItem<T extends TethysMoney>
            extends MetisFieldSetValidatedItem<T>
            implements TethysCurrencyField {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         * @param pClazz the item class
         * @param pEdit  the edit field
         */
        MetisFieldSetCurrencyItem(final MetisFieldSetPanel pPanel,
                                  final MetisDataFieldId pField,
                                  final Class<T> pClazz,
                                  final TethysCurrencyEditField<T> pEdit) {
            /* Initialise underlying class */
            super(pPanel, pField, pClazz, pEdit);
        }

        @Override
        public void setDeemedCurrency(final Supplier<Currency> pSupplier) {
            ((TethysCurrencyEditField<T>) getEditField()).setDeemedCurrency(pSupplier);
        }
    }

    /**
     * StringItem.
     */
    public static class MetisFieldSetStringItem
            extends MetisFieldSetValidatedItem<String> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetStringItem(final MetisFieldSetPanel pPanel,
                                final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, String.class, pPanel.getGuiFactory().newStringField());
        }
    }

    /**
     * CharArrayItem.
     */
    public static class MetisFieldSetCharArrayItem
            extends MetisFieldSetValidatedItem<char[]> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetCharArrayItem(final MetisFieldSetPanel pPanel,
                                   final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, char[].class, pPanel.getGuiFactory().newCharArrayField());
        }
    }

    /**
     * ShortItem.
     */
    public static class MetisFieldSetShortItem
            extends MetisFieldSetValidatedItem<Short> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetShortItem(final MetisFieldSetPanel pPanel,
                               final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Short.class, pPanel.getGuiFactory().newShortField());
        }
    }

    /**
     * IntegerItem.
     */
    public static class MetisFieldSetIntegerItem
            extends MetisFieldSetValidatedItem<Integer> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetIntegerItem(final MetisFieldSetPanel pPanel,
                                 final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Integer.class, pPanel.getGuiFactory().newIntegerField());
        }
    }

    /**
     * LongItem.
     */
    public static class MetisFieldSetLongItem
            extends MetisFieldSetValidatedItem<Long> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetLongItem(final MetisFieldSetPanel pPanel,
                              final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Long.class, pPanel.getGuiFactory().newLongField());
        }
    }

    /**
     * MoneyItem.
     */
    public static class MetisFieldSetMoneyItem
            extends MetisFieldSetCurrencyItem<TethysMoney> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetMoneyItem(final MetisFieldSetPanel pPanel,
                               final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysMoney.class, pPanel.getGuiFactory().newMoneyField());
        }
    }

    /**
     * PriceItem.
     */
    public static class MetisFieldSetPriceItem
            extends MetisFieldSetCurrencyItem<TethysPrice> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetPriceItem(final MetisFieldSetPanel pPanel,
                               final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysPrice.class, pPanel.getGuiFactory().newPriceField());
        }
    }

    /**
     * RateItem.
     */
    public static class MetisFieldSetRateItem
            extends MetisFieldSetValidatedItem<TethysRate> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetRateItem(final MetisFieldSetPanel pPanel,
                                        final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRate.class, pPanel.getGuiFactory().newRateField());
        }
    }

    /**
     * UnitsItem.
     */
    public static class MetisFieldSetUnitsItem
            extends MetisFieldSetValidatedItem<TethysUnits> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetUnitsItem(final MetisFieldSetPanel pPanel,
                               final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysUnits.class, pPanel.getGuiFactory().newUnitsField());
        }
    }

    /**
     * RatioItem.
     */
    public static class MetisFieldSetRatioItem
            extends MetisFieldSetValidatedItem<TethysRatio> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetRatioItem(final MetisFieldSetPanel pPanel,
                               final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRatio.class, pPanel.getGuiFactory().newRatioField());
        }
    }

    /**
     * DilutionItem.
     */
    public static class MetisFieldSetDilutionItem
            extends MetisFieldSetValidatedItem<TethysDilution> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetDilutionItem(final MetisFieldSetPanel pPanel,
                                  final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDilution.class, pPanel.getGuiFactory().newDilutionField());
        }
    }

    /**
     * DateItem.
     */
    public static class MetisFieldSetDateItem
            extends MetisFieldSetPanelItem<TethysDate>
            implements TethysDateButton {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetDateItem(final MetisFieldSetPanel pPanel,
                              final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDate.class, pPanel.getGuiFactory().newDateField());
        }

        @Override
        public void setDateConfigurator(final Consumer<TethysDateConfig> pConfigurator) {
            ((TethysDateButtonField) getEditField()).setDateConfigurator(pConfigurator);
        }
    }

    /**
     * ScrollItem.
     *
     * @param <T> the item class
     */
    public static class MetisFieldSetScrollItem<T>
            extends MetisFieldSetPanelItem<T>
            implements TethysScrollButton<T> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         * @param pClazz the item class
         */
        MetisFieldSetScrollItem(final MetisFieldSetPanel pPanel,
                                final MetisDataFieldId pField,
                                final Class<T> pClazz) {
            /* Initialise underlying class */
            super(pPanel, pField, pClazz, pPanel.getGuiFactory().newScrollField(pClazz));
        }

        @Override
        public void setMenuConfigurator(final Consumer<TethysScrollMenu<T>> pConfigurator) {
            ((TethysScrollButtonField<T>) getEditField()).setMenuConfigurator(pConfigurator);
        }
    }

    /**
     * ListItem.
     *
     * @param <T> the item class
     */
    public static class MetisFieldSetListItem<T extends Comparable<T>>
            extends MetisFieldSetPanelItem<List<T>>
            implements TethysListButton<T> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         */
        MetisFieldSetListItem(final MetisFieldSetPanel pPanel,
                              final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, pPanel.getGuiFactory().newListField());
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<T> getCastValue(final Object pValue) {
            return (List<T>) pValue;
        }

        @Override
        protected boolean isInstance(final Object pValue) {
            return List.class.isInstance(pValue);
        }

        @Override
        public void setSelectables(final Supplier<Iterator<T>> pSelectables) {
            ((TethysListButtonField<T>) getEditField()).setSelectables(pSelectables);
        }
    }

    /**
     * SimpleIconItem.
     *
     * @param <T> the item class
     */
    public static class MetisFieldSetIconItem<T>
            extends MetisFieldSetPanelItem<T>
            implements TethysIconButton<T> {
        /**
         * Constructor.
         *
         * @param pPanel the panel
         * @param pField the field
         * @param pClazz the item class
         */
        MetisFieldSetIconItem(final MetisFieldSetPanel pPanel,
                              final MetisDataFieldId pField,
                              final Class<T> pClazz) {
            /* Initialise underlying class */
            super(pPanel, pField, pClazz, pPanel.getGuiFactory().newIconField(pClazz));
        }

        @Override
        public void setIconMapSet(final Supplier<TethysIconMapSet<T>> pSupplier) {
            ((TethysIconButtonField<T>) getEditField()).setIconMapSet(pSupplier);
        }
    }
}
