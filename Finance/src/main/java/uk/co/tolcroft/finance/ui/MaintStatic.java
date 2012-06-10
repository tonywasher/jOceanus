/*******************************************************************************
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
package uk.co.tolcroft.finance.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.AccountType.AccountTypeList;
import uk.co.tolcroft.finance.data.EventInfoType;
import uk.co.tolcroft.finance.data.EventInfoType.EventInfoTypeList;
import uk.co.tolcroft.finance.data.Frequency;
import uk.co.tolcroft.finance.data.Frequency.FrequencyList;
import uk.co.tolcroft.finance.data.TaxRegime;
import uk.co.tolcroft.finance.data.TaxRegime.TaxRegimeList;
import uk.co.tolcroft.finance.data.TaxType;
import uk.co.tolcroft.finance.data.TaxType.TaxTypeList;
import uk.co.tolcroft.finance.data.TransactionType;
import uk.co.tolcroft.finance.data.TransactionType.TransTypeList;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.ui.StdInterfaces.StdPanel;
import uk.co.tolcroft.models.ui.StdInterfaces.stdCommand;
import uk.co.tolcroft.models.views.ViewList;

public class MaintStatic implements StdPanel, ItemListener {
    private MaintenanceTab theParent = null;
    private JPanel thePanel = null;
    private JPanel theSelect = null;
    private JComboBox theSelectBox = null;
    private MaintStaticData<?, ?> theActTypes = null;
    private MaintStaticData<?, ?> theTranTypes = null;
    private MaintStaticData<?, ?> theTaxTypes = null;
    private MaintStaticData<?, ?> theTaxRegimes = null;
    private MaintStaticData<?, ?> theFrequencys = null;
    private MaintStaticData<?, ?> theInfoTypes = null;
    private MaintStaticData<?, ?> theCurrent = null;
    private ViewList theViewSet = null;
    private JDataEntry theDataEntry = null;

    /* Get Top Window */
    protected JPanel getPanel() {
        return thePanel;
    }

    protected MainTab getTopWindow() {
        return theParent.getTopWindow();
    }

    protected View getView() {
        return theParent.getView();
    }

    protected ViewList getViewSet() {
        return theViewSet;
    }

    public MaintStatic(MaintenanceTab pParent) {
        /* Store parameters */
        theParent = pParent;

        /* Build the View set */
        theViewSet = new ViewList(getView());

        /* Create the top level debug entry for this view */
        View myView = getView();
        JDataManager myDataMgr = myView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry("Static");
        theDataEntry.addAsChildOf(pParent.getDataEntry());

        /* Build the child windows */
        theActTypes = new MaintStaticData<AccountTypeList, AccountType>(this, AccountTypeList.class);
        theTranTypes = new MaintStaticData<TransTypeList, TransactionType>(this, TransTypeList.class);
        theTaxTypes = new MaintStaticData<TaxTypeList, TaxType>(this, TaxTypeList.class);
        theTaxRegimes = new MaintStaticData<TaxRegimeList, TaxRegime>(this, TaxRegimeList.class);
        theFrequencys = new MaintStaticData<FrequencyList, Frequency>(this, FrequencyList.class);
        theInfoTypes = new MaintStaticData<EventInfoTypeList, EventInfoType>(this, EventInfoTypeList.class);

        /* Build the Static box */
        theSelectBox = new JComboBox();
        theSelectBox.addItem(AccountType.LIST_NAME);
        theSelectBox.addItem(TransactionType.LIST_NAME);
        theSelectBox.addItem(TaxType.LIST_NAME);
        theSelectBox.addItem(TaxRegime.LIST_NAME);
        theSelectBox.addItem(Frequency.LIST_NAME);
        theSelectBox.addItem(EventInfoType.LIST_NAME);

        /* Add the listener for item changes */
        theSelectBox.addItemListener(this);

        /* Create the selection panel */
        theSelect = new JPanel();
        theSelect.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection"));

        /* Create the layout for the panel */
        GroupLayout panelLayout = new GroupLayout(theSelect);
        theSelect.setLayout(panelLayout);

        /* Set the layout */
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout.createSequentialGroup().addContainerGap().addComponent(theSelectBox)
                                  .addContainerGap()));
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                  .addComponent(theSelectBox)));

        /* Create the full panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        panelLayout = new GroupLayout(thePanel);
        thePanel.setLayout(panelLayout);

        /* Set the layout */
        panelLayout.setHorizontalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(panelLayout
                                  .createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(panelLayout
                                                    .createParallelGroup(GroupLayout.Alignment.TRAILING,
                                                                         false)
                                                    .addComponent(theSelect, GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                    .addComponent(theActTypes.getPanel(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)
                                                    .addComponent(theTranTypes.getPanel(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)
                                                    .addComponent(theTaxTypes.getPanel(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)
                                                    .addComponent(theTaxRegimes.getPanel(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)
                                                    .addComponent(theFrequencys.getPanel(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)
                                                    .addComponent(theInfoTypes.getPanel(),
                                                                  GroupLayout.Alignment.LEADING,
                                                                  GroupLayout.DEFAULT_SIZE, 900,
                                                                  Short.MAX_VALUE)).addContainerGap()));
        panelLayout.setVerticalGroup(panelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING,
                          panelLayout.createSequentialGroup().addComponent(theSelect)
                                  .addComponent(theActTypes.getPanel()).addComponent(theTranTypes.getPanel())
                                  .addComponent(theTaxTypes.getPanel())
                                  .addComponent(theTaxRegimes.getPanel())
                                  .addComponent(theFrequencys.getPanel())
                                  .addComponent(theInfoTypes.getPanel()).addContainerGap()));

        /* Select correct box */
        theSelectBox.setSelectedItem(AccountType.LIST_NAME);
        setSelection(theActTypes);
    }

    /* Determine Focus */
    protected void determineFocus() {
        /* Set the required focus */
        theCurrent.getDataEntry().setFocus();
    }

    @Override
    public void notifyChanges() {
    }

    /**
     * Set Selection
     * @param pClass the class that is selected
     */
    private void setSelection(MaintStaticData<?, ?> pClass) {
        /* Record the current class and set debug focus */
        theCurrent = pClass;
        determineFocus();

        /* Enable/Disable view */
        theActTypes.getPanel().setVisible(pClass == theActTypes);
        theTranTypes.getPanel().setVisible(pClass == theTranTypes);
        theTaxTypes.getPanel().setVisible(pClass == theTaxTypes);
        theTaxRegimes.getPanel().setVisible(pClass == theTaxRegimes);
        theFrequencys.getPanel().setVisible(pClass == theFrequencys);
        theInfoTypes.getPanel().setVisible(pClass == theInfoTypes);
    }

    /* ItemStateChanged listener event */
    @Override
    public void itemStateChanged(ItemEvent evt) {
        String myName;

        /* If this event relates to the Select box */
        if (evt.getSource() == (Object) theSelectBox) {
            myName = (String) evt.getItem();
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                /* Determine the new table */
                if (myName == AccountType.LIST_NAME)
                    setSelection(theActTypes);
                else if (myName == TransactionType.LIST_NAME)
                    setSelection(theTranTypes);
                else if (myName == TaxType.LIST_NAME)
                    setSelection(theTaxTypes);
                else if (myName == TaxRegime.LIST_NAME)
                    setSelection(theTaxRegimes);
                else if (myName == Frequency.LIST_NAME)
                    setSelection(theFrequencys);
                else if (myName == EventInfoType.LIST_NAME)
                    setSelection(theInfoTypes);
            }
        }
    }

    @Override
    public void notifySelection(Object o) {
    }

    @Override
    public boolean hasUpdates() {
        /* Return to caller */
        return theViewSet.hasUpdates();
    }

    /**
     * Has this set of tables got errors
     * @return true/false
     */
    public boolean hasErrors() {
        /* Return to caller */
        return theViewSet.hasErrors();
    }

    @Override
    public void printIt() {
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public void performCommand(stdCommand pCmd) {
    }

    @Override
    public EditState getEditState() {
        /* Return to caller */
        return theViewSet.getEditState();
    }

    @Override
    public JDataManager getDataManager() {
        return theParent.getDataManager();
    }

    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    @Override
    public void lockOnError(boolean isError) {
    }

    /**
     * Refresh views/controls after a load/update of underlying data
     * @throws JDataException
     */
    public void refreshData() throws JDataException {
        /* Refresh the underlying children */
        theActTypes.refreshData();
        theTranTypes.refreshData();
        theTaxTypes.refreshData();
        theTaxRegimes.refreshData();
        theFrequencys.refreshData();
        theInfoTypes.refreshData();
    }

    /**
     * Set Visibility
     */
    protected void setVisibility() {
        /* Lock down Selection if required */
        theSelectBox.setEnabled(!hasUpdates());

        /* Pass call on to parent */
        theParent.setVisibility();
    }
}
