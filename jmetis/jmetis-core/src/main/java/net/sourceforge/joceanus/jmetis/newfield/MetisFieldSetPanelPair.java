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

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFormatter;
import net.sourceforge.joceanus.jmetis.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSetItem;
import net.sourceforge.joceanus.jmetis.newfield.MetisFieldSetPanel.MetisFieldSetPanelItem;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyItem;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * FieldSet Panel Pair.
 * @param <N> the node type
 * @param <C> the colour type
 * @param <F> the font type
 * @param <I> the icon type
 */
public abstract class MetisFieldSetPanelPair<N, C, F, I>
        implements TethysEventProvider<TethysUIEvent>, TethysNode<N> {
    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The fieldSet attributes.
     */
    private final MetisFieldAttributeSet<C, F> theAttributes;

    /**
     * The formatter.
     */
    private final MetisDataFormatter theFormatter;

    /**
     * The field map.
     */
    private final Map<MetisField, MetisFieldSetPanelItem<?, N, C, F, I>> theFieldMap;

    /**
     * The main panel.
     */
    private MetisFieldSetPanel<N, C, F, I> theMainPanel;

    /**
     * The tab manager.
     */
    private TethysTabPaneManager<N> theTabManager;

    /**
     * The list of subPanels.
     */
    private final List<SubPanelRegistration> theSubPanelList;

    /**
     * The editable state.
     */
    private boolean isEditable;

    /**
     * Constructor.
     * @param pAttributes the attribute set
     * @param pFormatter the data formatter
     */
    protected MetisFieldSetPanelPair(final MetisFieldAttributeSet<C, F> pAttributes,
                                     final MetisDataFormatter pFormatter) {
        /* Store parameters */
        theAttributes = pAttributes;
        theFormatter = pFormatter;

        /* Allocate fields */
        theFieldMap = new HashMap<>();
        theEventManager = new TethysEventManager<>();
        theSubPanelList = new ArrayList<>();
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Add a subPanel.
     * @param pName the name of the subPanel
     * @return the subPanel
     */
    public abstract MetisFieldSetPanel<N, C, F, I> addSubPanel(final String pName);

    /**
     * Obtain the attributes.
     * @return the attributes
     */
    public MetisFieldAttributeSet<C, F> getAttributeSet() {
        return theAttributes;
    }

    /**
     * Obtain the formatter.
     * @return the formatter
     */
    public MetisDataFormatter getFormatter() {
        return theFormatter;
    }

    /**
     * Obtain the fieldSet map.
     * @return the fieldSet map
     */
    protected Map<MetisField, MetisFieldSetPanelItem<?, N, C, F, I>> getFieldMap() {
        return theFieldMap;
    }

    /**
     * Obtain the main panel.
     * @return the main panel
     */
    public MetisFieldSetPanel<N, C, F, I> getMainPanel() {
        return theMainPanel;
    }

    /**
     * Declare the main panel.
     * @param pPanel the main panel
     */
    protected void declareMainPanel(final MetisFieldSetPanel<N, C, F, I> pPanel) {
        theMainPanel = pPanel;
        addListeners(pPanel);
    }

    /**
     * Obtain the main panel.
     * @return the main panel
     */
    protected TethysTabPaneManager<N> getTabManager() {
        return theTabManager;
    }

    /**
     * Declare the tab manager.
     * @param pManager the tab manager
     */
    protected void declareTabManager(final TethysTabPaneManager<N> pManager) {
        theTabManager = pManager;
    }

    /**
     * Declare a sub panel.
     * @param pTabItem the tab item
     * @param pPanel the sub panel
     */
    protected void declareSubPanel(final TethysTabItem<N> pTabItem,
                                   final MetisFieldSetPanel<N, C, F, I> pPanel) {
        theSubPanelList.add(new SubPanelRegistration(pTabItem, pPanel));
        addListeners(pPanel);
    }

    /**
     * Set the item.
     * @param pItem the item to set
     */
    public void setItem(final MetisFieldSetItem pItem) {
        /* Declare to the main panel */
        theMainPanel.setItem(pItem);

        /* Loop through the subPanels */
        Iterator<SubPanelRegistration> myIterator = theSubPanelList.iterator();
        while (myIterator.hasNext()) {
            SubPanelRegistration myReg = myIterator.next();
            myReg.thePanel.setItem(pItem);
        }
    }

    /**
     * obtain the item.
     * @return the item
     */
    public MetisFieldSetItem getItem() {
        return theMainPanel.getItem();
    }

    /**
     * is the panel editable.
     * @return true/false
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Refresh item values.
     * @return has visible fields true/false
     */
    public boolean refreshItem() {
        /* Declare to the main panel */
        boolean hasVisible = theMainPanel.refreshItem();

        /* Loop through the subPanels */
        Iterator<SubPanelRegistration> myIterator = theSubPanelList.iterator();
        while (myIterator.hasNext()) {
            SubPanelRegistration myReg = myIterator.next();

            /* refresh subPanel and adjust visibility */
            boolean hasSubVisible = myReg.thePanel.refreshItem();
            myReg.theTabItem.setVisible(hasSubVisible);

            /* Combine states */
            hasVisible |= hasSubVisible;
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

            /* Declare to the main panel */
            theMainPanel.setEditable(pEditable);

            /* Loop through the subPanels */
            Iterator<SubPanelRegistration> myIterator = theSubPanelList.iterator();
            while (myIterator.hasNext()) {
                SubPanelRegistration myReg = myIterator.next();
                myReg.thePanel.setEditable(pEditable);
            }
        }
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theMainPanel.setEnabled(pEnabled);
        theTabManager.setEnabled(pEnabled);
    }

    /**
     * Set readOnly field.
     * @param pField the field
     * @param pReadOnly the readOnly state
     */
    public void setReadOnlyField(final MetisField pField,
                                 final boolean pReadOnly) {
        /* Look up the field */
        MetisFieldSetPanelItem<?, N, C, F, I> myChild = theFieldMap.get(pField);
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
        MetisFieldSetPanelItem<?, N, C, F, I> myChild = theFieldMap.get(pField);
        if ((myChild != null)
            && myChild instanceof TethysCurrencyItem) {
            /* Set the currency */
            ((TethysCurrencyItem) myChild).setDeemedCurrency(pCurrency);
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
        MetisFieldSetPanelItem<?, N, C, F, I> myChild = theFieldMap.get(pField);
        if (myChild != null) {
            /* Pass the call on */
            myChild.showCmdButton(pShow);
        }
    }

    /**
     * Adjust label width.
     */
    public void adjustLabelWidth() {
        /* Adjust the main panel */
        theMainPanel.adjustLabelWidth();

        /* Loop through the subPanels */
        Iterator<SubPanelRegistration> myIterator = theSubPanelList.iterator();
        while (myIterator.hasNext()) {
            SubPanelRegistration myReg = myIterator.next();
            myReg.thePanel.adjustLabelWidth();
        }
    }

    /**
     * Add listeners.
     * @param pPanel the panel
     */
    private void addListeners(final MetisFieldSetPanel<N, C, F, I> pPanel) {
        TethysEventRegistrar<TethysUIEvent> myRegistrar = pPanel.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, theEventManager::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, theEventManager::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.PREPARECMDDIALOG, theEventManager::cascadeEvent);
        myRegistrar.addEventListener(TethysUIEvent.NEWCOMMAND, theEventManager::cascadeEvent);
    }

    /**
     * SubPanel registration.
     */
    private final class SubPanelRegistration {
        /**
         * The TabItem.
         */
        private final TethysTabItem<N> theTabItem;

        /**
         * The panel.
         */
        private final MetisFieldSetPanel<N, C, F, I> thePanel;

        /**
         * Constructor.
         * @param pTabItem the tab item
         * @param pPanel the sub panel
         */
        private SubPanelRegistration(final TethysTabItem<N> pTabItem,
                                     final MetisFieldSetPanel<N, C, F, I> pPanel) {
            theTabItem = pTabItem;
            thePanel = pPanel;
        }
    }
}
