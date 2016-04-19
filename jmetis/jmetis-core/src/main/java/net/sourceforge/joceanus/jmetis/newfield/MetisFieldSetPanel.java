/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmetis.newfield;

import java.util.Currency;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSetBase.MetisFieldUpdate;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSetItem;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.event.TethysEvent;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysDateField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysListField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysScrollField;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysStateIconField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * FieldSet Panel.
 * @param <N> the node type
 * @param <F> the font type
 * @param <C> the colour type
 * @param <I> the icon type
 */
public abstract class MetisFieldSetPanel<N, F, C, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * The Id.
     */
    private final Integer theId;

    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The fieldSet attributes.
     */
    private final MetisFieldAttributeSet<C, F> theAttributes;

    /**
     * The field map.
     */
    private final Map<MetisField, MetisFieldSetPanelItem<?, N, F, C, I>> theFieldMap;

    /**
     * The current item.
     */
    private MetisFieldSetItem theItem;

    /**
     * The first field element.
     */
    private MetisFieldSetPanelItem<?, N, F, C, I> theFirstChild;

    /**
     * The last field element.
     */
    private MetisFieldSetPanelItem<?, N, F, C, I> theLastChild;

    /**
     * The editable state.
     */
    private boolean isEditable;

    /**
     * Constructor.
     * @param pParent the parent pair
     */
    protected MetisFieldSetPanel(final MetisFieldSetPanelPair<N, F, C, I> pParent) {
        this(pParent.getGuiFactory(), pParent.getAttributeSet());
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pAttributes the attribute set
     */
    protected MetisFieldSetPanel(final TethysGuiFactory<N, I> pFactory,
                                 final MetisFieldAttributeSet<C, F> pAttributes) {
        theGuiFactory = pFactory;
        theId = theGuiFactory.getNextId();
        theAttributes = pAttributes;
        theFieldMap = new HashMap<>();
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public Integer getId() {
        return theId;
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the GUI Factory.
     * @return the factory
     */
    protected TethysGuiFactory<N, I> getGuiFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain the attributes.
     * @return the attributes
     */
    public MetisFieldAttributeSet<C, F> getAttributeSet() {
        return theAttributes;
    }

    /**
     * Set the item.
     * @param pItem the item to set
     */
    public void setItem(final MetisFieldSetItem pItem) {
        /* Store the item */
        theItem = pItem;
    }

    /**
     * obtain the item.
     * @return the item
     */
    public MetisFieldSetItem getItem() {
        return theItem;
    }

    /**
     * is the panel editable.
     * @return true/false
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Register field.
     * @param pField the field
     */
    private void registerField(final MetisFieldSetPanelItem<?, N, F, C, I> pField) {
        theFieldMap.put(pField.getField(), pField);
    }

    /**
     * Refresh item values.
     * @return has visible fields true/false
     */
    public boolean refreshItem() {
        /* Initialise state */
        boolean hasVisible = false;

        /* If the item is non-null */
        if (theItem != null) {
            /* Loop through the elements */
            MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFirstChild;
            while (myChild != null) {
                /* Obtain the field value */
                Object myValue = theItem.getFieldValue(myChild.getField());

                /* Determine font and colour */
                MetisField myField = myChild.getField();
                boolean isChanged = theItem.getFieldState(myField).isChanged();
                F myFont = theAttributes.getFontForField(myChild.isNumeric(), isChanged);
                C myColor = theAttributes.getStandardColor(isChanged);

                /* Set the value */
                hasVisible |= myChild.setValue(myValue, myFont, myColor);

                /* Move to next child */
                myChild = myChild.theNextSibling;
            }
        }

        /* return the state */
        return hasVisible;
    }

    /**
     * Set editable state.
     * @param pEditable the editable state
     */
    public void setEditable(final boolean pEditable) {
        /* If this is a change of state */
        if (isEditable != pEditable) {
            /* set the new state */
            isEditable = pEditable;

            /* Loop through the elements */
            MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFirstChild;
            while (myChild != null) {
                /* Set the editable state of the field */
                myChild.setEditable(pEditable && !myChild.isReadOnly());

                /* Move to next child */
                myChild = myChild.theNextSibling;
            }
        }
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        /* Loop through the elements */
        MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFirstChild;
        while (myChild != null) {
            /* Set the editable state of the field */
            myChild.setEnabled(pEnabled);

            /* Move to next child */
            myChild = myChild.theNextSibling;
        }
    }

    /**
     * Set readOnly field.
     * @param pField the field
     * @param pReadOnly the readOnly state
     */
    public void setReadOnlyField(final MetisField pField,
                                 final boolean pReadOnly) {
        /* Look up the field */
        MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFieldMap.get(pField);
        if (myChild != null) {
            /* Pass the call on */
            myChild.setReadOnly(pReadOnly);
        }
    }

    /**
     * Set deemed currency.
     * @param pField the field
     * @param pCurrency the currency
     */
    public void setDeemedCurrency(final MetisField pField,
                                  final Currency pCurrency) {
        /* Look up the field and check that it is a currency item */
        MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFieldMap.get(pField);
        if ((myChild != null)
            && myChild instanceof TethysCurrencyField) {
            /* Set the currency */
            ((TethysCurrencyField) myChild).setDeemedCurrency(pCurrency);
        }
    }

    /**
     * Show the command button.
     * @param pField the field
     * @param pShow true/false
     */
    public void showCmdButton(final MetisField pField,
                              final boolean pShow) {
        /* Look up the field */
        MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFieldMap.get(pField);
        if (myChild != null) {
            /* Pass the call on */
            myChild.showCmdButton(pShow);
        }
    }

    /**
     * Add string field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<String, N, F, C, I> addStringField(final MetisField pField);

    /**
     * Add short field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<Short, N, F, C, I> addShortField(final MetisField pField);

    /**
     * Add integer field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<Integer, N, F, C, I> addIntegerField(final MetisField pField);

    /**
     * Add long field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<Long, N, F, C, I> addLongField(final MetisField pField);

    /**
     * Add money field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<TethysMoney, N, F, C, I> addMoneyField(final MetisField pField);

    /**
     * Add price field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<TethysPrice, N, F, C, I> addPriceField(final MetisField pField);

    /**
     * Add rate field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<TethysRate, N, F, C, I> addRateField(final MetisField pField);

    /**
     * Add units field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<TethysUnits, N, F, C, I> addUnitsField(final MetisField pField);

    /**
     * Add dilution field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<TethysDilution, N, F, C, I> addDilutionField(final MetisField pField);

    /**
     * Add ratio field.
     * @param pField the field
     * @return the field
     */
    public abstract MetisFieldSetPanelItem<TethysRatio, N, F, C, I> addRatioField(final MetisField pField);

    /**
     * Add dateButton field.
     * @param pField the field
     * @return the field
     */
    public abstract TethysDateField<N, I> addDateButtonField(final MetisField pField);

    /**
     * Add scrollButton field.
     * @param <T> the item type
     * @param pField the field
     * @param pClass the item class
     * @return the field
     */
    public abstract <T> TethysScrollField<T, N, I> addScrollButtonField(final MetisField pField,
                                                                        final Class<T> pClass);

    /**
     * Add listButton field.
     * @param <T> the item type
     * @param pField the field
     * @return the list field
     */
    public abstract <T> TethysListField<T, N, I> addListButtonField(final MetisField pField);

    /**
     * Add simpleIconButton field.
     * @param <T> the item type
     * @param pField the field
     * @param pClass the item class
     * @return the icon field
     */
    public abstract <T> TethysIconField<T, N, I> addIconButtonField(final MetisField pField,
                                                                    final Class<T> pClass);

    /**
     * Add stateIconButton field.
     * @param <T> the item type
     * @param <S> the state type
     * @param pField the field
     * @param pClass the item class
     * @return the icon field
     */
    public abstract <T, S> TethysStateIconField<T, S, N, I> addStateIconButtonField(final MetisField pField,
                                                                                    final Class<T> pClass);

    /**
     * Is the panel visible?
     * @return true/false
     */
    protected boolean isVisible() {
        /* Loop through the elements */
        MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFirstChild;
        while (myChild != null) {
            /* If any child is visible return true */
            if (myChild.isVisible()) {
                return true;
            }

            /* Move to next child */
            myChild = myChild.theNextSibling;
        }

        /* No visible children */
        return false;
    }

    /**
     * Adjust label width.
     */
    public void adjustLabelWidth() {
        /* Adjust to widest label */
        setLabelWidth(widestLabel());
    }

    /**
     * Determine width of largest label.
     * @return the largest width
     */
    private double widestLabel() {
        /* Start at zero width */
        double myWidth = 0;

        /* Loop through the elements */
        MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFirstChild;
        while (myChild != null) {
            /* Adjust largest width */
            double myCurrWidth = myChild.getLabelWidth();
            if (myCurrWidth > myWidth) {
                myWidth = myCurrWidth;
            }

            /* Move to next child */
            myChild = myChild.theNextSibling;
        }

        /* return largest width */
        return myWidth;
    }

    /**
     * Set label width.
     * @param pWidth the width
     */
    private void setLabelWidth(final double pWidth) {
        /* Loop through the elements */
        MetisFieldSetPanelItem<?, N, F, C, I> myChild = theFirstChild;
        while (myChild != null) {
            /* Adjust width */
            myChild.setLabelWidth(pWidth);

            /* Move to next child */
            myChild = myChild.theNextSibling;
        }
    }

    /**
     * Fire event.
     * @param pEventId the event id
     * @param pDetails the details
     */
    private void fireEvent(final TethysUIEvent pEventId,
                           final Object pDetails) {
        theEventManager.fireEvent(pEventId, pDetails);
    }

    /**
     * Item class.
     * @param <T> the item type
     * @param <N> the node type
     * @param <F> the font type
     * @param <C> the colour type
     * @param <I> the icon type
     */
    protected abstract static class MetisFieldSetPanelItem<T, N, F, C, I> {
        /**
         * The panel.
         */
        private final MetisFieldSetPanel<N, F, C, I> thePanel;

        /**
         * The edit field.
         */
        private final TethysDataEditField<T, N, I> theEdit;

        /**
         * The previous field element.
         */
        private MetisFieldSetPanelItem<?, N, F, C, I> thePrevSibling;

        /**
         * The next field element.
         */
        private MetisFieldSetPanelItem<?, N, F, C, I> theNextSibling;

        /**
         * The item class.
         */
        private final Class<T> theClass;

        /**
         * The field definition.
         */
        private final MetisField theField;

        /**
         * is the field numeric?
         */
        private final boolean isNumeric;

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
         * @param pClass the item class
         * @param pNumeric is the field numeric?
         * @param pEdit the edit field
         */
        protected MetisFieldSetPanelItem(final MetisFieldSetPanel<N, F, C, I> pPanel,
                                         final MetisField pField,
                                         final Class<T> pClass,
                                         final boolean pNumeric,
                                         final TethysDataEditField<T, N, I> pEdit) {
            /* Set fields */
            thePanel = pPanel;
            theField = pField;
            theClass = pClass;
            isNumeric = pNumeric;
            theEdit = pEdit;
            isVisible = true;

            /* If there are already elements */
            MetisFieldSetPanelItem<?, N, F, C, I> myChild = thePanel.theLastChild;
            if (myChild != null) {
                /* Link to last child */
                myChild.theNextSibling = this;
                thePrevSibling = myChild;

                /* else set as first child */
            } else {
                thePanel.theFirstChild = this;
            }

            /* Add as last child of parent */
            thePanel.theLastChild = this;

            /* Add listeners */
            TethysEventRegistrar<TethysUIEvent> myRegistrar = theEdit.getEventRegistrar();
            myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, this::cascadeEvent);
            myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, this::cascadeEvent);
            myRegistrar.addEventListener(TethysUIEvent.PREPARECMDDIALOG, this::cascadeEvent);
            myRegistrar.addEventListener(TethysUIEvent.NEWCOMMAND, this::cascadeEvent);

            /* register the field */
            thePanel.registerField(this);
        }

        /**
         * obtain the panel.
         * @return the panel
         */
        protected MetisFieldSetPanel<N, F, C, I> getPanel() {
            return thePanel;
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
        public MetisField getField() {
            return theField;
        }

        /**
         * is the field numeric?
         * @return true/false
         */
        public boolean isNumeric() {
            return isNumeric;
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

        /**
         * Set the visibility of the item.
         * @param pVisible true/false
         */
        public void setVisible(final boolean pVisible) {
            /* If we are changing visibility */
            if (pVisible != isVisible) {
                /* Set new visibility */
                isVisible = pVisible;

                /* Determine position */
                int myPos = countPreviousVisibleSiblings();

                /* If we are showing the item */
                if (pVisible) {
                    attachAsChildNo(myPos);

                    /* else just detach item and children */
                } else {
                    detachFromPanel(myPos);
                }
            }
        }

        /**
         * Set the enabled state of the item.
         * @param pEnabled true/false
         */
        private void setEnabled(final boolean pEnabled) {
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
         * Attach as particular child.
         * @param pChildNo the child #
         */
        protected abstract void attachAsChildNo(final int pChildNo);

        /**
         * Detach this node from the panel.
         * @param pChildNo the child #
         */
        protected abstract void detachFromPanel(final int pChildNo);

        /**
         * Set the value.
         * @param pValue the value
         * @param pFont the font
         * @param pColor the colour
         * @return is the field visible
         */
        private boolean setValue(final Object pValue,
                                 final F pFont,
                                 final C pColor) {
            /* Determine whether we should show the field */
            boolean showField = pValue == null
                                               ? thePanel.isEditable() && !isReadOnly
                                               : isInstance(pValue);
            setVisible(showField);

            /* If we are showing the field */
            if (showField) {
                /* Set the value */
                setTheValue(getCastValue(pValue));

                /* Set the font and colour */
                // theEdit.setFont(pFont);
                // theEdit.setTextFill(pColor);
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
            return theClass.cast(pValue);
        }

        /**
         * Is the object an instance of the class.
         * @param pValue the value
         * @return the cast value
         */
        protected boolean isInstance(final Object pValue) {
            return theClass.isInstance(pValue);
        }

        /**
         * Set the value.
         * @param pValue the value
         */
        protected abstract void setTheValue(final T pValue);

        /**
         * Set the editable state.
         * @param pEditable the editable state
         */
        protected abstract void setEditable(final boolean pEditable);

        /**
         * Count previous visible items.
         * @return the count
         */
        public int countPreviousVisibleSiblings() {
            /* Determine the previous visible sibling */
            int myCount = 0;
            MetisFieldSetPanelItem<?, N, F, C, I> mySibling = thePrevSibling;
            while (mySibling != null) {
                if (mySibling.isVisible) {
                    myCount++;
                }
                mySibling = mySibling.thePrevSibling;
            }
            return myCount;
        }

        /**
         * Get label width.
         * @return the label width
         */
        protected abstract double getLabelWidth();

        /**
         * Set label width.
         * @param pWidth the label width
         */
        protected abstract void setLabelWidth(final double pWidth);

        /**
         * Cascade the event.
         * @param pEvent the event
         */
        private void cascadeEvent(final TethysEvent<TethysUIEvent> pEvent) {
            MetisFieldUpdate myUpdate = new MetisFieldUpdate(theField, pEvent.getDetails());
            thePanel.fireEvent(pEvent.getEventId(), myUpdate);
        }
    }
}