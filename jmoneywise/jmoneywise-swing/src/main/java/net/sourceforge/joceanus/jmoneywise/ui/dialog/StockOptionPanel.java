/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.joceanus.jmoneywise.ui.dialog;

import java.awt.GridLayout;
import java.util.Iterator;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import net.sourceforge.joceanus.jmetis.data.DataType;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.field.JFieldSetBase.FieldUpdate;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldSet;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding.SecurityHoldingMap;
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jmoneywise.data.StockOption.StockOptionList;
import net.sourceforge.joceanus.jmoneywise.data.StockOptionInfoSet;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jprometheus.ui.swing.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.dateday.swing.JDateDayButton;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.swing.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
import net.sourceforge.joceanus.jtethys.swing.JScrollMenu;
import net.sourceforge.joceanus.jtethys.swing.SpringUtilities;

/**
 * Panel to display/edit/create a StockOption.
 */
public class StockOptionPanel
        extends MoneyWiseDataItemPanel<StockOption> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = 7816774872067208354L;

    /**
     * Vests Tab Title.
     */
    private static final String TAB_VESTS = MoneyWiseUIControlResource.OPTIONPANEL_TAB_VESTS.getValue();

    /**
     * The Field Set.
     */
    private final transient JFieldSet<StockOption> theFieldSet;

    /**
     * StockHolding Button Field.
     */
    private final JScrollButton<SecurityHolding> theHoldingButton;

    /**
     * GrantDate Button Field.
     */
    private final JDateDayButton theGrantButton;

    /**
     * ExpiryDate Button Field.
     */
    private final JDateDayButton theExpiryButton;

    /**
     * Closed Button Field.
     */
    private final transient ComplexIconButtonState<Boolean, Boolean> theClosedState;

    /**
     * StockOptionVest Table.
     */
    private final StockOptionVestTable theVests;

    /**
     * Constructor.
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public StockOptionPanel(final JFieldManager pFieldMgr,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the buttons */
        theHoldingButton = new JScrollButton<SecurityHolding>();

        /* Create date buttons */
        JDateDayFormatter myFormatter = getFormatter().getDateFormatter();
        theGrantButton = new JDateDayButton(myFormatter);
        theExpiryButton = new JDateDayButton(myFormatter);

        /* Set closed button */
        theClosedState = new ComplexIconButtonState<Boolean, Boolean>(Boolean.FALSE);

        /* Build the FieldSet */
        theFieldSet = getFieldSet();

        /* Build the main panel */
        JPanel myMainPanel = buildMainPanel();

        /* Create a tabbedPane */
        JTabbedPane myTabs = new JTabbedPane();

        /* Build the notes panel */
        JPanel myPanel = buildNotesPanel();
        myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Create the StockOptionVests table */
        theVests = new StockOptionVestTable(pFieldMgr, getUpdateSet(), pError);
        myTabs.add(TAB_VESTS, theVests.getPanel());

        /* Layout the main panel */
        myPanel = getMainPanel();
        myPanel.setLayout(new GridLayout(1, 2, PADDING_SIZE, PADDING_SIZE));
        myPanel.add(myMainPanel);
        myPanel.add(myTabs);

        /* Layout the panel */
        layoutPanel();

        /* Create the listener */
        new StockOptionListener();
    }

    /**
     * Build Main subPanel.
     * @return the panel
     */
    private JPanel buildMainPanel() {
        /* Build the closed button state */
        JIconButton<Boolean> myClosedButton = new JIconButton<Boolean>(theClosedState);
        MoneyWiseIcons.buildLockedButton(theClosedState);

        /* Create the text fields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();
        JTextField myPrice = new JTextField();

        /* restrict the fields */
        restrictField(myName, StockOption.NAMELEN);
        restrictField(myDesc, StockOption.NAMELEN);
        restrictField(theHoldingButton, StockOption.NAMELEN);
        restrictField(theGrantButton, StockOption.NAMELEN);
        restrictField(theExpiryButton, StockOption.NAMELEN);
        restrictField(myPrice, StockOption.NAMELEN);
        restrictField(myClosedButton, StockOption.NAMELEN);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(StockOption.FIELD_NAME, DataType.STRING, myName);
        theFieldSet.addFieldElement(StockOption.FIELD_DESC, DataType.STRING, myDesc);
        theFieldSet.addFieldElement(StockOption.FIELD_STOCKHOLDING, SecurityHolding.class, theHoldingButton);
        theFieldSet.addFieldElement(StockOption.FIELD_GRANTDATE, DataType.DATEDAY, theGrantButton);
        theFieldSet.addFieldElement(StockOption.FIELD_EXPIREDATE, DataType.DATEDAY, theExpiryButton);
        theFieldSet.addFieldElement(StockOption.FIELD_PRICE, DataType.PRICE, myPrice);
        theFieldSet.addFieldElement(StockOption.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(StockOption.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_STOCKHOLDING, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_GRANTDATE, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_EXPIREDATE, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_PRICE, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_CLOSED, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Notes subPanel.
     * @return the panel
     */
    private JPanel buildNotesPanel() {
        /* Allocate fields */
        JTextArea myNotes = new JTextArea();
        JScrollPane myScroll = new JScrollPane(myNotes);

        /* Build the FieldSet */
        theFieldSet.addFieldElement(StockOptionInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);

        /* Create the notes panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the notes panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(StockOptionInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    @Override
    public void refreshData() {
        /* If we have an item */
        StockOption myItem = getItem();
        if (myItem != null) {
            StockOptionList myOptions = getDataList(MoneyWiseDataType.STOCKOPTION, StockOptionList.class);
            setItem(myOptions.findItemById(myItem.getId()));
        }

        /* Refresh the vests */
        theVests.refreshData();

        /* Make sure that the item is not editable */
        setEditable(false);
    }

    @Override
    protected void adjustFields(final boolean isEditable) {
        /* Access the item */
        StockOption myOption = getItem();
        boolean bIsClosed = myOption.isClosed();
        boolean bIsActive = myOption.isActive();
        boolean bIsRelevant = myOption.isRelevant();

        /* Determine whether the closed button should be visible */
        boolean bShowClosed = bIsClosed || (bIsActive && !bIsRelevant);
        theFieldSet.setVisibility(StockOption.FIELD_CLOSED, bShowClosed);

        /* Determine the state of the closed button */
        boolean bEditClosed = bIsClosed
                                       ? false
                                       : !bIsRelevant;
        theFieldSet.setEditable(StockOption.FIELD_CLOSED, isEditable && bEditClosed);
        theClosedState.setState(bEditClosed);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myOption.getDesc() != null;
        theFieldSet.setVisibility(StockOption.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        boolean bShowNotes = isEditable || myOption.getNotes() != null;
        theFieldSet.setVisibility(StockOptionInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Portfolio/Security/Dates/Units cannot be changed if the item is active */
        theFieldSet.setEditable(StockOption.FIELD_STOCKHOLDING, isEditable && !bIsActive);
        theFieldSet.setEditable(StockOption.FIELD_GRANTDATE, isEditable && !bIsActive);
        theFieldSet.setEditable(StockOption.FIELD_EXPIREDATE, isEditable && !bIsActive);
        theFieldSet.setEditable(StockOption.FIELD_PRICE, isEditable && !bIsActive);

        /* Set currency for price */
        theFieldSet.setAssumedCurrency(StockOption.FIELD_PRICE, myOption.getStockHolding().getCurrency());
    }

    @Override
    protected void updateField(final FieldUpdate pUpdate) throws JOceanusException {
        /* Access the field */
        JDataField myField = pUpdate.getField();
        StockOption myOption = getItem();

        /* Process updates */
        if (myField.equals(StockOption.FIELD_NAME)) {
            /* Update the Name */
            myOption.setName(pUpdate.getString());
        } else if (myField.equals(StockOption.FIELD_DESC)) {
            /* Update the Description */
            myOption.setDescription(pUpdate.getString());
        } else if (myField.equals(StockOption.FIELD_STOCKHOLDING)) {
            /* Update the Holding */
            SecurityHolding myHolding = pUpdate.getValue(SecurityHolding.class);
            StockOptionList myList = myOption.getList();
            myOption.setStockHolding(myList.declareStockHolding(myHolding));
        } else if (myField.equals(StockOption.FIELD_GRANTDATE)) {
            /* Update the GrantDate */
            myOption.setGrantDate(pUpdate.getDateDay());
        } else if (myField.equals(StockOption.FIELD_EXPIREDATE)) {
            /* Update the ExpiryDate */
            myOption.setExpiryDate(pUpdate.getDateDay());
        } else if (myField.equals(StockOption.FIELD_PRICE)) {
            /* Update the Price */
            myOption.setPrice(pUpdate.getPrice());
        } else if (myField.equals(StockOption.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myOption.setClosed(pUpdate.getBoolean());
        } else {
            /* Switch on the field */
            switch (StockOptionInfoSet.getClassForField(myField)) {
                case NOTES:
                    myOption.setNotes(pUpdate.getCharArray());
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void declareGoToItems(final boolean pUpdates) {
        StockOption myItem = getItem();
        SecurityHolding myHolding = myItem.getStockHolding();
        Portfolio myPortfolio = myHolding.getPortfolio();
        Security mySecurity = myHolding.getSecurity();
        declareGoToItem(myPortfolio);
        declareGoToItem(mySecurity);
    }

    @Override
    public void setItem(final StockOption pItem) {
        /* Update the vests */
        theVests.setOption(pItem);

        /* Pass call onwards */
        super.setItem(pItem);
    }

    @Override
    public void setNewItem(final StockOption pItem) {
        /* Update the vests */
        theVests.setOption(pItem);

        /* Pass call onwards */
        super.setNewItem(pItem);
    }

    @Override
    public void setEditable(final boolean isEditable) {
        /* Update the vests */
        theVests.setEditable(isEditable);

        /* Pass call onwards */
        super.setEditable(isEditable);
    }

    /**
     * Build the security list for the item.
     * @param pMenuBuilder the menu builder
     * @param pOption the option to build for
     */
    public void buildHoldingMenu(final JScrollMenuBuilder<SecurityHolding> pMenuBuilder,
                                 final StockOption pOption) {
        /* Clear the menu */
        pMenuBuilder.clearMenu();

        /* Record active item */
        SecurityHolding myCurr = pOption.getStockHolding();
        JMenuItem myActive = null;

        /* Access Portfolios and Holdings Map */
        PortfolioList myPortfolios = getDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);
        SecurityHoldingMap myMap = pOption.getList().getSecurityHoldings();

        /* Loop through the Portfolios */
        Iterator<Portfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            Portfolio myPortfolio = myPortIterator.next();

            /* Ignore deleted or closed */
            if (myPortfolio.isDeleted() || myPortfolio.isClosed()) {
                continue;
            }

            /* Look for existing and new holdings */
            Iterator<SecurityHolding> myExistIterator = myMap.existingIterator(myPortfolio);
            Iterator<SecurityHolding> myNewIterator = myMap.newIterator(myPortfolio, SecurityTypeClass.SHARES);
            if ((myExistIterator != null) || (myNewIterator != null)) {
                /* Create a new JMenu and add it to the popUp */
                JScrollMenu myMenu = pMenuBuilder.addSubMenu(myPortfolio.getName());

                /* If there are existing elements */
                if (myExistIterator != null) {
                    /* Loop through them */
                    while (myExistIterator.hasNext()) {
                        SecurityHolding myHolding = myExistIterator.next();
                        Security mySecurity = myHolding.getSecurity();

                        /* Ignore non-Shares */
                        if (!mySecurity.isSecurityClass(SecurityTypeClass.SHARES)) {
                            continue;
                        }

                        /* Add the item to the menu */
                        JMenuItem myItem = pMenuBuilder.addItem(myMenu, myHolding, mySecurity.getName());

                        /* If this is the active holding */
                        if (myHolding.equals(myCurr)) {
                            /* Record it */
                            myActive = myItem;
                        }
                    }
                }

                /* If there are new elements */
                if (myNewIterator != null) {
                    /* Create a new subMenu */
                    JScrollMenu mySubMenu = pMenuBuilder.addSubMenu(myMenu, SecurityHolding.SECURITYHOLDING_NEW);

                    /* Loop through them */
                    while (myNewIterator.hasNext()) {
                        SecurityHolding myHolding = myNewIterator.next();
                        Security mySecurity = myHolding.getSecurity();

                        /* Add the item to the menu */
                        pMenuBuilder.addItem(mySubMenu, myHolding, mySecurity.getName());
                    }
                }
            }
        }

        /* Ensure active item is visible */
        pMenuBuilder.showItem(myActive);
    }

    @Override
    protected void refreshAfterUpdate() {
        /* Pass call onwards */
        super.refreshAfterUpdate();

        /* Refresh the vests */
        theVests.refreshAfterUpdate();
    }

    /**
     * Options Listener.
     */
    private final class StockOptionListener
            implements JOceanusChangeEventListener {
        /**
         * The Holding Menu Builder.
         */
        private final JScrollMenuBuilder<SecurityHolding> theHoldingMenuBuilder;

        /**
         * HoldingMenu Registration.
         */
        private final JOceanusChangeRegistration theHoldingMenuReg;

        /**
         * Vests panel Registration.
         */
        private final JOceanusChangeRegistration theVestsReg;

        /**
         * Constructor.
         */
        private StockOptionListener() {
            /* Access the MenuBuilders */
            theHoldingMenuBuilder = theHoldingButton.getMenuBuilder();
            theHoldingMenuReg = theHoldingMenuBuilder.getEventRegistrar().addChangeListener(this);
            theVestsReg = theVests.getEventRegistrar().addChangeListener(this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* Handle menu type */
            if (theHoldingMenuReg.isRelevant(pEvent)) {
                buildHoldingMenu(theHoldingMenuBuilder, getItem());
            } else if (theVestsReg.isRelevant(pEvent)) {
                updateActions();
                fireStateChanged();
            }
        }
    }
}
