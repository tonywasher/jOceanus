/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2020 Tony Washer
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

import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysComponent;
import net.sourceforge.joceanus.jtethys.ui.TethysDataEditField.TethysCurrencyEditField;
import net.sourceforge.joceanus.jtethys.ui.TethysGridPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager;
import net.sourceforge.joceanus.jtethys.ui.TethysTabPaneManager.TethysTabItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;

/**
 * FieldSet Panel Pair.
 */
public class MetisFieldSetPanelPair
        implements TethysEventProvider<TethysUIEvent>, TethysComponent {
    /**
     * The GUI Factory.
     */
    private final TethysGuiFactory theGuiFactory;

    /**
     * The event manager.
     */
    private final TethysEventManager<TethysUIEvent> theEventManager;

    /**
     * The field map.
     */
    private final Map<MetisDataFieldId, MetisFieldSetPanelItem<?>> theFieldMap;

    /**
     * The Node.
     */
    private final TethysGridPaneManager theNode;

    /**
     * The main panel.
     */
    private final MetisFieldSetPanel theMainPanel;

    /**
     * The tab manager.
     */
    private final TethysTabPaneManager theTabManager;

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
     *
     * @param pFactory the GUI factory
     */
    public MetisFieldSetPanelPair(final TethysGuiFactory pFactory) {
        /* Store parameters */
        theGuiFactory = pFactory;

        /* Allocate fields */
        theFieldMap = new HashMap<>();
        theEventManager = new TethysEventManager<>();
        theSubPanelList = new ArrayList<>();

        /* Create the panels */
        theTabManager = theGuiFactory.newTabPane();
        theMainPanel = new MetisFieldSetPanel(this);
        addListeners(theMainPanel);

        /* Create the new node */
        theNode = theGuiFactory.newGridPane();
        theNode.addCell(theMainPanel);
        theNode.allowCellGrowth(theMainPanel);
        theNode.addCell(theTabManager);
        theNode.allowCellGrowth(theTabManager);
    }

    @Override
    public Integer getId() {
        return theNode.getId();
    }

    @Override
    public TethysNode getNode() {
        return theNode.getNode();
    }

    @Override
    public void setVisible(final boolean pVisible) {
        theNode.setVisible(pVisible);
    }

    @Override
    public void setEnabled(final boolean pEnabled) {
        theNode.setEnabled(pEnabled);
    }

    @Override
    public TethysEventRegistrar<TethysUIEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the GUI Factory.
     *
     * @return the factory
     */
    protected TethysGuiFactory getGuiFactory() {
        return theGuiFactory;
    }

    /**
     * Obtain the main panel.
     *
     * @return the main panel
     */
    public MetisFieldSetPanel getMainPanel() {
        return theMainPanel;
    }

    /**
     * Add a subPanel.
     *
     * @param pName the name of the subPanel
     * @return the subPanel
     */
    public MetisFieldSetPanel addSubPanel(final String pName) {
        /* Create a new subPanel and add to tab manager */
        final MetisFieldSetPanel myPanel = new MetisFieldSetPanel(this);
        final TethysTabItem myItem = theTabManager.addTabItem(pName, myPanel);
        theSubPanelList.add(new SubPanelRegistration(myItem, myPanel));
        addListeners(myPanel);
        return myPanel;
    }

    /**
     * Obtain the fieldSet map.
     *
     * @return the fieldSet map
     */
    protected Map<MetisDataFieldId, MetisFieldSetPanelItem<?>> getFieldMap() {
        return theFieldMap;
    }

    /**
     * Set the item.
     *
     * @param pItem the item to set
     */
    public void setItem(final MetisFieldItem pItem) {
        /* Declare to the main panel */
        theMainPanel.setItem(pItem);

        /* Loop through the subPanels */
        final Iterator<SubPanelRegistration> myIterator = theSubPanelList.iterator();
        while (myIterator.hasNext()) {
            final SubPanelRegistration myReg = myIterator.next();
            myReg.setItem(pItem);
        }
    }

    /**
     * obtain the item.
     *
     * @return the item
     */
    public MetisFieldItem getItem() {
        return theMainPanel.getItem();
    }

    /**
     * is the panel editable.
     *
     * @return true/false
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * Refresh item values.
     *
     * @return has visible fields true/false
     */
    public boolean refreshItem() {
        /* Declare to the main panel */
        boolean hasVisible = theMainPanel.refreshItem();

        /* Loop through the subPanels */
        final Iterator<SubPanelRegistration> myIterator = theSubPanelList.iterator();
        while (myIterator.hasNext()) {
            final SubPanelRegistration myReg = myIterator.next();

            /* refresh subPanel and adjust visibility */
            final boolean hasSubVisible = myReg.refreshItem();
            myReg.setTabVisible(hasSubVisible);

            /* Combine states */
            hasVisible |= hasSubVisible;
        }

        /* return the state */
        return hasVisible;
    }

    /**
     * Set editable state.
     *
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
            final Iterator<SubPanelRegistration> myIterator = theSubPanelList.iterator();
            while (myIterator.hasNext()) {
                final SubPanelRegistration myReg = myIterator.next();
                myReg.setEditable(pEditable);
            }
        }
    }

    /**
     * Set readOnly field.
     *
     * @param pField    the field
     * @param pReadOnly the readOnly state
     */
    public void setReadOnlyField(final MetisDataFieldId pField,
                                 final boolean pReadOnly) {
        /* Look up the field */
        final MetisFieldSetPanelItem<?> myChild = theFieldMap.get(pField);
        if (myChild != null) {
            /* Pass the call on */
            myChild.setReadOnly(pReadOnly);
        }
    }

    /**
     * Set deemed currency.
     *
     * @param pField    the field
     * @param pCurrency the currency supplier
     */
    public void setDeemedCurrency(final MetisDataFieldId pField,
                                  final Supplier<Currency> pCurrency) {
        /* Look up the field and check that it is a currency item */
        final MetisFieldSetPanelItem<?> myChild = theFieldMap.get(pField);
        if (myChild instanceof TethysCurrencyEditField) {
            /* Set the currency */
            ((TethysCurrencyEditField<?>) myChild).setDeemedCurrency(pCurrency);
        }
    }

    /**
     * Show the command button.
     *
     * @param pField the field
     * @param pShow  true/false
     */
    public void showCmdButton(final MetisDataFieldId pField,
                              final boolean pShow) {
        /* Look up the field */
        final MetisFieldSetPanelItem<?> myChild = theFieldMap.get(pField);
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
        final Iterator<SubPanelRegistration> myIterator = theSubPanelList.iterator();
        while (myIterator.hasNext()) {
            final SubPanelRegistration myReg = myIterator.next();
            myReg.adjustLabelWidth();
        }
    }

    /**
     * Add listeners.
     *
     * @param pPanel the panel
     */
    private void addListeners(final MetisFieldSetPanel pPanel) {
        final TethysEventRegistrar<TethysUIEvent> myRegistrar = pPanel.getEventRegistrar();
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
        private final TethysTabItem theTabItem;

        /**
         * The panel.
         */
        private final MetisFieldSetPanel thePanel;

        /**
         * Constructor.
         *
         * @param pTabItem the tab item
         * @param pPanel   the sub panel
         */
        private SubPanelRegistration(final TethysTabItem pTabItem,
                                     final MetisFieldSetPanel pPanel) {
            theTabItem = pTabItem;
            thePanel = pPanel;
        }

        /**
         * Set tab visibility.
         *
         * @param pVisible the visible state
         */
        private void setTabVisible(final boolean pVisible) {
            theTabItem.setVisible(pVisible);
        }

        /**
         * Set the item.
         *
         * @param pItem the item to set
         */
        private void setItem(final MetisFieldItem pItem) {
            thePanel.setItem(pItem);
        }

        /**
         * Refresh item.
         *
         * @return has visible fields true/false
         */
        private boolean refreshItem() {
            return thePanel.refreshItem();
        }

        /**
         * Set editable state.
         *
         * @param pEditable the editable state
         */
        private void setEditable(final boolean pEditable) {
            thePanel.setEditable(pEditable);
        }

        /**
         * Adjust label width.
         */
        private void adjustLabelWidth() {
            thePanel.adjustLabelWidth();
        }
    }
}
