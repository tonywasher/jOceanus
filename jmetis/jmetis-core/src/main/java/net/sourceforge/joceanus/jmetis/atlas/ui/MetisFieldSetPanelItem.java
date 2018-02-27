/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
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
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * FieldSet Panel Item.
 * @param <T> the item type
 * @param <N> the node type
 * @param <I> the icon type
 */
public abstract class MetisFieldSetPanelItem<T, N, I>
        implements TethysNode<N> {
    /**
     * The panel.
     */
    private final MetisFieldSetPanel<N, I> thePanel;

    /**
     * The node.
     */
    private final TethysBorderPaneManager<N, I> theNode;

    /**
     * The label.
     */
    private final TethysLabel<N, I> theLabel;

    /**
     * The edit field.
     */
    private final TethysDataEditField<T, N, I> theEdit;

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
     * @param pPanel the panel
     * @param pField the field definition
     * @param pEdit the edit field
     */
    protected MetisFieldSetPanelItem(final MetisFieldSetPanel<N, I> pPanel,
                                     final MetisDataFieldId pField,
                                     final TethysDataEditField<T, N, I> pEdit) {
        /* Set fields */
        this(pPanel, pField, null, pEdit);
    }

    /**
     * Constructor.
     * @param pPanel the panel
     * @param pField the field definition
     * @param pClazz the item class
     * @param pEdit the edit field
     */
    protected MetisFieldSetPanelItem(final MetisFieldSetPanel<N, I> pPanel,
                                     final MetisDataFieldId pField,
                                     final Class<T> pClazz,
                                     final TethysDataEditField<T, N, I> pEdit) {
        /* Set fields */
        thePanel = pPanel;
        theField = pField;
        theClazz = pClazz;
        theEdit = pEdit;
        isVisible = true;

        /* Obtain the GuiFactory */
        final TethysGuiFactory<N, I> myGuiFactory = pPanel.getGuiFactory();

        /* Create the label */
        theLabel = myGuiFactory.newLabel();
        theLabel.setText(pField.getId() + TethysLabel.STR_COLON);
        theLabel.setAlignment(TethysAlignment.EAST);

        /* Create the Node */
        theNode = myGuiFactory.newBorderPane();
        theNode.setEast(theLabel);
        theNode.setCentre(pEdit);

        /* Add listeners */
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = theEdit.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, this::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.PREPARECMDDIALOG, this::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.NEWCOMMAND, this::cascadeEvent);
    }

    /**
     * obtain the panel.
     * @return the panel
     */
    protected MetisFieldSetPanel<N, I> getPanel() {
        return thePanel;
    }

    @Override
    public Integer getId() {
        return theNode.getId();
    }

    @Override
    public N getNode() {
        return theNode.getNode();
    }

    /**
     * Obtain the border pane.
     * @return the pane
     */
    protected TethysBorderPaneManager<N, I> getBorderPane() {
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
     * @return the edit field
     */
    protected TethysDataEditField<T, N, I> getEditField() {
        return theEdit;
    }

    /**
     * obtain the field.
     * @return the field
     */
    public MetisDataFieldId getField() {
        return theField;
    }

    /**
     * is the field visible?
     * @return true/false
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * is the field readOnly?
     * @return true/false
     */
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theEdit.setEnabled(pEnabled);
    }

    /**
     * Set the readOnly state of the item.
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
     * @param pShow true/false
     */
    public void showCmdButton(final boolean pShow) {
        theEdit.showCmdButton(pShow);
    }

    /**
     * Set the value.
     * @param pValue the value
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
     * @param pValue the value
     * @return the cast value
     */
    protected T getCastValue(final Object pValue) {
        return theClazz.cast(pValue);
    }

    /**
     * Is the object an instance of the class.
     * @param pValue the value
     * @return the cast value
     */
    protected boolean isInstance(final Object pValue) {
        return theClazz.isInstance(pValue);
    }

    /**
     * Set the editable state.
     * @param pEditable the editable state
     */
    protected void setEditable(final boolean pEditable) {
        theEdit.setEditable(pEditable);
    }

    /**
     * Get label width.
     * @return the label width
     */
    protected int getLabelWidth() {
        return theLabel.getWidth();
    }

    /**
     * Set label width.
     * @param pWidth the label width
     */
    protected void setLabelWidth(final int pWidth) {
        theLabel.setPreferredWidth(pWidth);
    }

    /**
     * Cascade the event.
     * @param pEvent the event
     */
    private void cascadeEvent(final TethysEvent<TethysUIEvent> pEvent) {
        final MetisFieldUpdate myUpdate = new MetisFieldUpdate(theField, pEvent.getDetails());
        thePanel.fireEvent(pEvent.getEventId(), myUpdate);
    }

    /**
     * ValidatedItem.
     * @param <T> the item type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetValidatedItem<T, N, I>
            extends MetisFieldSetPanelItem<T, N, I>
            implements TethysValidatedField<T> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClazz the item class
         * @param pEdit the edit field
         */
        protected MetisFieldSetValidatedItem(final MetisFieldSetPanel<N, I> pPanel,
                                             final MetisDataFieldId pField,
                                             final Class<T> pClazz,
                                             final TethysValidatedEditField<T, N, I> pEdit) {
            /* Initialise underlying class */
            super(pPanel, pField, pClazz, pEdit);
        }

        @Override
        public void setValidator(final Function<T, String> pValidator) {
            ((TethysValidatedEditField<T, N, I>) getEditField()).setValidator(pValidator);
        }
    }

    /**
     * CurrencyItem.
     * @param <T> the item type
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetCurrencyItem<T extends TethysMoney, N, I>
            extends MetisFieldSetValidatedItem<T, N, I>
            implements TethysCurrencyField {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClazz the item class
         * @param pEdit the edit field
         */
        protected MetisFieldSetCurrencyItem(final MetisFieldSetPanel<N, I> pPanel,
                                            final MetisDataFieldId pField,
                                            final Class<T> pClazz,
                                            final TethysCurrencyEditField<T, N, I> pEdit) {
            /* Initialise underlying class */
            super(pPanel, pField, pClazz, pEdit);
        }

        @Override
        public void setDeemedCurrency(final Supplier<Currency> pSupplier) {
            ((TethysCurrencyEditField<T, N, I>) getEditField()).setDeemedCurrency(pSupplier);
        }
    }

    /**
     * StringItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetStringItem<N, I>
            extends MetisFieldSetValidatedItem<String, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetStringItem(final MetisFieldSetPanel<N, I> pPanel,
                                          final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, String.class, pPanel.getGuiFactory().newStringField());
        }
    }

    /**
     * CharArrayItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetCharArrayItem<N, I>
            extends MetisFieldSetValidatedItem<char[], N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetCharArrayItem(final MetisFieldSetPanel<N, I> pPanel,
                                             final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, char[].class, pPanel.getGuiFactory().newCharArrayField());
        }
    }

    /**
     * ShortItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetShortItem<N, I>
            extends MetisFieldSetValidatedItem<Short, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetShortItem(final MetisFieldSetPanel<N, I> pPanel,
                                         final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Short.class, pPanel.getGuiFactory().newShortField());
        }
    }

    /**
     * IntegerItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetIntegerItem<N, I>
            extends MetisFieldSetValidatedItem<Integer, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetIntegerItem(final MetisFieldSetPanel<N, I> pPanel,
                                           final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Integer.class, pPanel.getGuiFactory().newIntegerField());
        }
    }

    /**
     * LongItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetLongItem<N, I>
            extends MetisFieldSetValidatedItem<Long, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetLongItem(final MetisFieldSetPanel<N, I> pPanel,
                                        final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, Long.class, pPanel.getGuiFactory().newLongField());
        }
    }

    /**
     * MoneyItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetMoneyItem<N, I>
            extends MetisFieldSetCurrencyItem<TethysMoney, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetMoneyItem(final MetisFieldSetPanel<N, I> pPanel,
                                         final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysMoney.class, pPanel.getGuiFactory().newMoneyField());
        }
    }

    /**
     * PriceItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetPriceItem<N, I>
            extends MetisFieldSetCurrencyItem<TethysPrice, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetPriceItem(final MetisFieldSetPanel<N, I> pPanel,
                                         final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysPrice.class, pPanel.getGuiFactory().newPriceField());
        }
    }

    /**
     * RateItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetRateItem<N, I>
            extends MetisFieldSetValidatedItem<TethysRate, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetRateItem(final MetisFieldSetPanel<N, I> pPanel,
                                        final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRate.class, pPanel.getGuiFactory().newRateField());
        }
    }

    /**
     * UnitsItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetUnitsItem<N, I>
            extends MetisFieldSetValidatedItem<TethysUnits, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetUnitsItem(final MetisFieldSetPanel<N, I> pPanel,
                                         final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysUnits.class, pPanel.getGuiFactory().newUnitsField());
        }
    }

    /**
     * RatioItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetRatioItem<N, I>
            extends MetisFieldSetValidatedItem<TethysRatio, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetRatioItem(final MetisFieldSetPanel<N, I> pPanel,
                                         final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysRatio.class, pPanel.getGuiFactory().newRatioField());
        }
    }

    /**
     * DilutionItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetDilutionItem<N, I>
            extends MetisFieldSetValidatedItem<TethysDilution, N, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetDilutionItem(final MetisFieldSetPanel<N, I> pPanel,
                                            final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDilution.class, pPanel.getGuiFactory().newDilutionField());
        }
    }

    /**
     * DateItem.
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetDateItem<N, I>
            extends MetisFieldSetPanelItem<TethysDate, N, I>
            implements TethysDateButton {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetDateItem(final MetisFieldSetPanel<N, I> pPanel,
                                        final MetisDataFieldId pField) {
            /* Initialise underlying class */
            super(pPanel, pField, TethysDate.class, pPanel.getGuiFactory().newDateField());
        }

        @Override
        public void setDateConfigurator(final Consumer<TethysDateConfig> pConfigurator) {
            ((TethysDateButtonField<N, I>) getEditField()).setDateConfigurator(pConfigurator);
        }
    }

    /**
     * ScrollItem.
     * @param <T> the item class
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetScrollItem<T, N, I>
            extends MetisFieldSetPanelItem<T, N, I>
            implements TethysScrollButton<T, I> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisFieldSetScrollItem(final MetisFieldSetPanel<N, I> pPanel,
                                          final MetisDataFieldId pField,
                                          final Class<T> pClass) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, pPanel.getGuiFactory().newScrollField());
        }

        @Override
        public void setMenuConfigurator(final Consumer<TethysScrollMenu<T, I>> pConfigurator) {
            ((TethysScrollButtonField<T, N, I>) getEditField()).setMenuConfigurator(pConfigurator);
        }
    }

    /**
     * ListItem.
     * @param <T> the item class
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetListItem<T extends Comparable<T>, N, I>
            extends MetisFieldSetPanelItem<List<T>, N, I>
            implements TethysListButton<T> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         */
        protected MetisFieldSetListItem(final MetisFieldSetPanel<N, I> pPanel,
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
            ((TethysListButtonField<T, N, I>) getEditField()).setSelectables(pSelectables);
        }
    }

    /**
     * SimpleIconItem.
     * @param <T> the item class
     * @param <N> the node type
     * @param <I> the icon type
     */
    public static class MetisFieldSetIconItem<T, N, I>
            extends MetisFieldSetPanelItem<T, N, I>
            implements TethysIconButton<T> {
        /**
         * Constructor.
         * @param pPanel the panel
         * @param pField the field
         * @param pClass the item class
         */
        protected MetisFieldSetIconItem(final MetisFieldSetPanel<N, I> pPanel,
                                        final MetisDataFieldId pField,
                                        final Class<T> pClass) {
            /* Initialise underlying class */
            super(pPanel, pField, pClass, pPanel.getGuiFactory().newIconField());
        }

        @Override
        public void setIconMapSet(final Supplier<TethysIconMapSet<T>> pSupplier) {
            ((TethysIconButtonField<T, N, I>) getEditField()).setIconMapSet(pSupplier);
        }
    }
}
