/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.jOceanus.jDataManager.JDataManager;
import net.sourceforge.jOceanus.jDataManager.JDataManager.JDataEntry;
import net.sourceforge.jOceanus.jDataModels.data.StaticData;
import net.sourceforge.jOceanus.jDataModels.ui.ErrorPanel;
import net.sourceforge.jOceanus.jDataModels.ui.SaveButtons;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jDataModels.views.UpdateSet;
import net.sourceforge.jOceanus.jEventManager.JEventPanel;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountInfoType.AccountInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountType.AccountTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.Frequency;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.Frequency.FrequencyList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxRegime.TaxRegimeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxType.TaxTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TaxYearInfoType.TaxYearInfoTypeList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.TransactionType.TransTypeList;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * Top level panel for static data.
 * @author Tony Washer
 */
public class MaintStatic extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1089967527250331711L;

    /**
     * Panel width.
     */
    private static final int PANEL_WIDTH = 900;

    /**
     * Panel height.
     */
    private static final int PANEL_HEIGHT = 25;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(MaintStatic.class.getName());

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = NLS_BUNDLE.getString("SelectionTitle");

    /**
     * The card panel.
     */
    private final JPanel theCardPanel;

    /**
     * The card layout.
     */
    private final CardLayout theLayout;

    /**
     * The selection box.
     */
    private final JComboBox<String> theSelectBox;

    /**
     * The Account Types panel.
     */
    private final MaintStaticData<?, ?> theActTypes;

    /**
     * The Transaction Types panel.
     */
    private final MaintStaticData<?, ?> theTranTypes;

    /**
     * The Tax Types panel.
     */
    private final MaintStaticData<?, ?> theTaxTypes;

    /**
     * The Tax Regimes panel.
     */
    private final MaintStaticData<?, ?> theTaxRegimes;

    /**
     * The Frequencies panel.
     */
    private final MaintStaticData<?, ?> theFrequencys;

    /**
     * The TaxYear Info Types panel.
     */
    private final MaintStaticData<?, ?> theTaxInfoTypes;

    /**
     * The Account Info Types panel.
     */
    private final MaintStaticData<?, ?> theActInfoTypes;

    /**
     * The Event Info Types panel.
     */
    private final MaintStaticData<?, ?> theEvtInfoTypes;

    /**
     * The Panel map.
     */
    private final Map<String, MaintStaticData<?, ?>> theMap;

    /**
     * The Active panel.
     */
    private MaintStaticData<?, ?> theActive;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The save buttons panel.
     */
    private final SaveButtons theSaveButtons;

    /**
     * The UpdateSet.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * Obtain the updateList.
     * @return the viewSet
     */
    protected UpdateSet getUpdateSet() {
        return theUpdateSet;
    }

    /**
     * Constructor.
     * @param pView the view
     */
    public MaintStatic(final View pView) {
        /* Store parameters */
        View myView = pView;

        /* Build the Update set */
        theUpdateSet = new UpdateSet(myView);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = myView.getDataMgr();
        JDataEntry mySection = myView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.new JDataEntry(StaticData.class.getSimpleName());
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the save buttons panel */
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Build the child windows */
        theActTypes = new MaintStaticData<AccountTypeList, AccountType>(myView, theUpdateSet, theError,
                AccountTypeList.class, AccountType.class);
        theTranTypes = new MaintStaticData<TransTypeList, TransactionType>(myView, theUpdateSet, theError,
                TransTypeList.class, TransactionType.class);
        theTaxTypes = new MaintStaticData<TaxTypeList, TaxType>(myView, theUpdateSet, theError,
                TaxTypeList.class, TaxType.class);
        theTaxRegimes = new MaintStaticData<TaxRegimeList, TaxRegime>(myView, theUpdateSet, theError,
                TaxRegimeList.class, TaxRegime.class);
        theFrequencys = new MaintStaticData<FrequencyList, Frequency>(myView, theUpdateSet, theError,
                FrequencyList.class, Frequency.class);
        theTaxInfoTypes = new MaintStaticData<TaxYearInfoTypeList, TaxYearInfoType>(myView, theUpdateSet,
                theError, TaxYearInfoTypeList.class, TaxYearInfoType.class);
        theActInfoTypes = new MaintStaticData<AccountInfoTypeList, AccountInfoType>(myView, theUpdateSet,
                theError, AccountInfoTypeList.class, AccountInfoType.class);
        theEvtInfoTypes = new MaintStaticData<EventInfoTypeList, EventInfoType>(myView, theUpdateSet,
                theError, EventInfoTypeList.class, EventInfoType.class);

        /* Build the Static box */
        theSelectBox = new JComboBox<String>();
        theSelectBox.addItem(AccountType.LIST_NAME);
        theSelectBox.addItem(TransactionType.LIST_NAME);
        theSelectBox.addItem(TaxType.LIST_NAME);
        theSelectBox.addItem(TaxRegime.LIST_NAME);
        theSelectBox.addItem(Frequency.LIST_NAME);
        theSelectBox.addItem(TaxYearInfoType.LIST_NAME);
        theSelectBox.addItem(AccountInfoType.LIST_NAME);
        theSelectBox.addItem(EventInfoType.LIST_NAME);

        /* Add the listener for item changes */
        StaticListener myListener = new StaticListener();
        theSelectBox.addItemListener(myListener);
        theSelectBox.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        theError.addChangeListener(myListener);
        theSaveButtons.addActionListener(myListener);

        /* Add listener for the static data panels */
        theActTypes.addChangeListener(myListener);
        theTranTypes.addChangeListener(myListener);
        theTaxTypes.addChangeListener(myListener);
        theTaxRegimes.addChangeListener(myListener);
        theFrequencys.addChangeListener(myListener);
        theTaxInfoTypes.addChangeListener(myListener);
        theActInfoTypes.addChangeListener(myListener);
        theEvtInfoTypes.addChangeListener(myListener);

        /* Create the selection panel */
        JPanel mySelect = new JPanel();
        mySelect.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelect.setLayout(new BoxLayout(mySelect, BoxLayout.X_AXIS));
        mySelect.add(theSelectBox);

        /* Create the card panel */
        theCardPanel = new JPanel();
        theLayout = new CardLayout();
        theCardPanel.setLayout(theLayout);

        /* Add the data panels */
        theCardPanel.add(theActTypes.getPanel(), AccountType.LIST_NAME);
        theCardPanel.add(theTranTypes.getPanel(), TransactionType.LIST_NAME);
        theCardPanel.add(theTaxTypes.getPanel(), TaxType.LIST_NAME);
        theCardPanel.add(theTaxRegimes.getPanel(), TaxRegime.LIST_NAME);
        theCardPanel.add(theFrequencys.getPanel(), Frequency.LIST_NAME);
        theCardPanel.add(theTaxInfoTypes.getPanel(), TaxYearInfoType.LIST_NAME);
        theCardPanel.add(theActInfoTypes.getPanel(), AccountInfoType.LIST_NAME);
        theCardPanel.add(theEvtInfoTypes.getPanel(), EventInfoType.LIST_NAME);

        /* Build the panel map */
        theMap = new HashMap<String, MaintStaticData<?, ?>>();
        theMap.put(AccountType.LIST_NAME, theActTypes);
        theMap.put(TransactionType.LIST_NAME, theTranTypes);
        theMap.put(TaxType.LIST_NAME, theTaxTypes);
        theMap.put(TaxRegime.LIST_NAME, theTaxRegimes);
        theMap.put(Frequency.LIST_NAME, theFrequencys);
        theMap.put(TaxYearInfoType.LIST_NAME, theTaxInfoTypes);
        theMap.put(AccountInfoType.LIST_NAME, theActInfoTypes);
        theMap.put(EventInfoType.LIST_NAME, theEvtInfoTypes);

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mySelect);
        add(theError);
        add(theCardPanel);
        add(theSaveButtons);

        /* Select initial box */
        theActive = theActTypes;
        theSelectBox.setSelectedItem(AccountType.LIST_NAME);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        theActive.determineFocus(theDataEntry);
    }

    /**
     * Set Selection.
     * @param pName the name that is selected
     */
    private void setSelection(final String pName) {
        /* Select the correct static */
        theLayout.show(theCardPanel, pName);
        theActive = theMap.get(pName);
        determineFocus();
    }

    /**
     * Has this set of tables got updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        /* Return to caller */
        return theUpdateSet.hasUpdates();
    }

    /**
     * Has this set of tables got errors?
     * @return true/false
     */
    public boolean hasErrors() {
        /* Return to caller */
        return theUpdateSet.hasErrors();
    }

    /**
     * Refresh views/controls after a load/update of underlying data.
     */
    public void refreshData() {
        /* Refresh the underlying children */
        theActTypes.refreshData();
        theTranTypes.refreshData();
        theTaxTypes.refreshData();
        theTaxRegimes.refreshData();
        theFrequencys.refreshData();
        theTaxInfoTypes.refreshData();
        theActInfoTypes.refreshData();
        theEvtInfoTypes.refreshData();

        /* Touch the updateSet */
        theDataEntry.setObject(theUpdateSet);
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Refresh the underlying children */
        theActTypes.cancelEditing();
        theTranTypes.cancelEditing();
        theTaxTypes.cancelEditing();
        theTaxRegimes.cancelEditing();
        theFrequencys.cancelEditing();
        theTaxInfoTypes.cancelEditing();
        theActInfoTypes.cancelEditing();
        theEvtInfoTypes.cancelEditing();
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Lock down Selection if required */
        theSelectBox.setEnabled(!hasUpdates());

        /* Update the save buttons */
        theSaveButtons.setEnabled(true);

        /* Alert listeners that there has been a change */
        fireStateChanged();
    }

    /**
     * Listener class.
     */
    private final class StaticListener implements ItemListener, ChangeListener, ActionListener {
        @Override
        public void itemStateChanged(final ItemEvent evt) {
            /* Ignore if this is not a selection event */
            if (evt.getStateChange() != ItemEvent.SELECTED) {
                return;
            }

            /* If this event relates to the Select box */
            if (theSelectBox.equals(evt.getSource())) {
                String myName = (String) evt.getItem();

                /* Select the requested table */
                setSelection(myName);
            }
        }

        @Override
        public void stateChanged(final ChangeEvent evt) {
            /* Access reporting object */
            Object o = evt.getSource();

            /* If this is the error panel reporting */
            if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel on error */
                theSelectBox.setVisible(!isError);

                /* Lock scroll-able area */
                theCardPanel.setEnabled(!isError);

                /* Lock Save Buttons */
                theSaveButtons.setEnabled(!isError);

                /* if this is one of the static data panels */
            } else if (o instanceof MaintStaticData) {
                /* Adjust visibility */
                setVisibility();
            }
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Access reporting object and command */
            Object o = evt.getSource();
            String myCmd = evt.getActionCommand();

            /* if this is the save buttons reporting */
            if (theSaveButtons.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Process the command */
                theUpdateSet.processCommand(myCmd, theError);

                /* Adjust visibility */
                setVisibility();
            }
        }
    }
}