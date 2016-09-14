/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.ui;

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetCharArrayItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetDateItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetDilutionItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetIconItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetIntegerItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetListItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetLongItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetMoneyItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetPriceItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetRateItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetRatioItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetScrollItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetShortItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetStateIconItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetStringItem;
import net.sourceforge.joceanus.jmetis.ui.MetisFieldSetPanelItem.MetisFieldSetUnitsItem;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysBoxPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyField;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * FieldSet Panel.
 * @param <N> the node type
 * @param <I> the icon type
 */
public class MetisFieldSetPanel<N, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory<N, I> theGuiFactory;

    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The field map.
     */
    private final Map<MetisField, MetisFieldSetPanelItem<?, N, I>> theFieldMap;

    /**
     * The VBox.
     */
    private final TethysBoxPaneManager<N, I> theNode;

    /**
     * The current item.
     */
    private MetisFieldSetItem theItem;

    /**
     * The field elements.
     */
    private List<MetisFieldSetPanelItem<?, N, I>> theItems;

    /**
     * The editable state.
     */
    private boolean isEditable;

    /**
     * Constructor.
     * @param pFactory the GUI factory
     */
    protected MetisFieldSetPanel(final TethysGuiFactory<N, I> pFactory) {
        this(pFactory, new HashMap<>());
    }

    /**
     * Constructor.
     * @param pParent the parent pair
     */
    protected MetisFieldSetPanel(final MetisFieldSetPanelPair<N, I> pParent) {
        this(pParent.getGuiFactory(), pParent.getFieldMap());
    }

    /**
     * Constructor.
     * @param pFactory the GUI factory
     * @param pFieldMap the fieldMap
     */
    private MetisFieldSetPanel(final TethysGuiFactory<N, I> pFactory,
                               final Map<MetisField, MetisFieldSetPanelItem<?, N, I>> pFieldMap) {
        theGuiFactory = pFactory;
        theFieldMap = pFieldMap;
        theItems = new ArrayList<>();
        theEventManager = new TethysEventManager<>();
        theNode = theGuiFactory.newVBoxPane();
    }

    @Override
    public Integer getId() {
        return theNode.getId();
    }

    @Override
    public N getNode() {
        return theNode.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
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
     * @param <X> the item
     * @param <T> the field type
     * @param pField the field
     * @return the field
     */
    private <X extends MetisFieldSetPanelItem<T, N, I>, T> X registerField(final X pField) {
        theFieldMap.put(pField.getField(), pField);
        theItems.add(pField);
        theNode.addNode(pField.getBorderPane());
        return pField;
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
            Iterator<MetisFieldSetPanelItem<?, N, I>> myIterator = theItems.iterator();
            while (myIterator.hasNext()) {
                MetisFieldSetPanelItem<?, N, I> myChild = myIterator.next();

                /* Obtain the field value */
                Object myValue = theItem.getFieldValue(myChild.getField());

                /* Determine font and colour */
                MetisField myField = myChild.getField();
                boolean isChanged = theItem.getFieldState(myField).isChanged();

                /* Set the value */
                hasVisible |= myChild.setValue(myValue, isChanged);
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
            Iterator<MetisFieldSetPanelItem<?, N, I>> myIterator = theItems.iterator();
            while (myIterator.hasNext()) {
                MetisFieldSetPanelItem<?, N, I> myChild = myIterator.next();
                /* Set the editable state of the field */
                myChild.setEditable(pEditable && !myChild.isReadOnly());
            }
        }
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theNode.setEnabled(pEnabled);
    }

    /**
     * Set readOnly field.
     * @param pField the field
     * @param pReadOnly the readOnly state
     */
    public void setReadOnlyField(final MetisField pField,
                                 final boolean pReadOnly) {
        /* Look up the field */
        MetisFieldSetPanelItem<?, N, I> myChild = theFieldMap.get(pField);
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
        MetisFieldSetPanelItem<?, N, I> myChild = theFieldMap.get(pField);
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
        MetisFieldSetPanelItem<?, N, I> myChild = theFieldMap.get(pField);
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
    public MetisFieldSetStringItem<N, I> addStringField(final MetisField pField) {
        return registerField(new MetisFieldSetStringItem<>(this, pField));
    }

    /**
     * Add charArray field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetCharArrayItem<N, I> addCharArrayField(final MetisField pField) {
        return registerField(new MetisFieldSetCharArrayItem<>(this, pField));
    }

    /**
     * Add short field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetShortItem<N, I> addShortField(final MetisField pField) {
        return registerField(new MetisFieldSetShortItem<>(this, pField));
    }

    /**
     * Add integer field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetIntegerItem<N, I> addIntegerField(final MetisField pField) {
        return registerField(new MetisFieldSetIntegerItem<>(this, pField));
    }

    /**
     * Add long field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetLongItem<N, I> addLongField(final MetisField pField) {
        return registerField(new MetisFieldSetLongItem<>(this, pField));
    }

    /**
     * Add money field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetMoneyItem<N, I> addMoneyField(final MetisField pField) {
        return registerField(new MetisFieldSetMoneyItem<>(this, pField));
    }

    /**
     * Add price field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetPriceItem<N, I> addPriceField(final MetisField pField) {
        return registerField(new MetisFieldSetPriceItem<>(this, pField));
    }

    /**
     * Add rate field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetRateItem<N, I> addRateField(final MetisField pField) {
        return registerField(new MetisFieldSetRateItem<>(this, pField));
    }

    /**
     * Add units field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetUnitsItem<N, I> addUnitsField(final MetisField pField) {
        return registerField(new MetisFieldSetUnitsItem<>(this, pField));
    }

    /**
     * Add dilution field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetDilutionItem<N, I> addDilutionField(final MetisField pField) {
        return registerField(new MetisFieldSetDilutionItem<>(this, pField));
    }

    /**
     * Add ratio field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetRatioItem<N, I> addRatioField(final MetisField pField) {
        return registerField(new MetisFieldSetRatioItem<>(this, pField));
    }

    /**
     * Add dateButton field.
     * @param pField the field
     * @return the field
     */
    public MetisFieldSetDateItem<N, I> addDateButtonField(final MetisField pField) {
        return registerField(new MetisFieldSetDateItem<>(this, pField));
    }

    /**
     * Add scrollButton field.
     * @param <T> the item type
     * @param pField the field
     * @param pClass the item class
     * @return the field
     */
    public <T> MetisFieldSetScrollItem<T, N, I> addScrollButtonField(final MetisField pField,
                                                                     final Class<T> pClass) {
        return registerField(new MetisFieldSetScrollItem<>(this, pField, pClass));
    }

    /**
     * Add listButton field.
     * @param <T> the item type
     * @param pField the field
     * @return the list field
     */
    public <T> MetisFieldSetListItem<T, N, I> addListButtonField(final MetisField pField) {
        return registerField(new MetisFieldSetListItem<>(this, pField));
    }

    /**
     * Add simpleIconButton field.
     * @param <T> the item type
     * @param pField the field
     * @param pClass the item class
     * @return the icon field
     */
    public <T> MetisFieldSetIconItem<T, N, I> addIconButtonField(final MetisField pField,
                                                                 final Class<T> pClass) {
        return registerField(new MetisFieldSetIconItem<>(this, pField, pClass));
    }

    /**
     * Add stateIconButton field.
     * @param <T> the item type
     * @param <S> the state type
     * @param pField the field
     * @param pClass the item class
     * @return the icon field
     */
    public <T, S> MetisFieldSetStateIconItem<T, S, N, I> addStateIconButtonField(final MetisField pField,
                                                                                 final Class<T> pClass) {
        return registerField(new MetisFieldSetStateIconItem<>(this, pField, pClass));
    }

    /**
     * Is the panel visible?
     * @return true/false
     */
    protected boolean isVisible() {
        /* Loop through the elements */
        Iterator<MetisFieldSetPanelItem<?, N, I>> myIterator = theItems.iterator();
        while (myIterator.hasNext()) {
            MetisFieldSetPanelItem<?, N, I> myChild = myIterator.next();
            /* If any child is visible return true */
            if (myChild.isVisible()) {
                return true;
            }
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
    private int widestLabel() {
        /* Start at zero width */
        int myWidth = 0;

        /* Loop through the elements */
        Iterator<MetisFieldSetPanelItem<?, N, I>> myIterator = theItems.iterator();
        while (myIterator.hasNext()) {
            MetisFieldSetPanelItem<?, N, I> myChild = myIterator.next();
            /* Adjust largest width */
            int myCurrWidth = myChild.getLabelWidth();
            if (myCurrWidth > myWidth) {
                myWidth = myCurrWidth;
            }
        }

        /* return largest width */
        return myWidth;
    }

    /**
     * Set label width.
     * @param pWidth the width
     */
    private void setLabelWidth(final int pWidth) {
        /* Loop through the elements */
        Iterator<MetisFieldSetPanelItem<?, N, I>> myIterator = theItems.iterator();
        while (myIterator.hasNext()) {
            MetisFieldSetPanelItem<?, N, I> myChild = myIterator.next();
            /* Adjust width */
            myChild.setLabelWidth(pWidth);
        }
    }

    /**
     * Fire event.
     * @param pEventId the event id
     * @param pDetails the details
     */
    protected void fireEvent(final TethysUIEvent pEventId,
                             final Object pDetails) {
        theEventManager.fireEvent(pEventId, pDetails);
    }
}