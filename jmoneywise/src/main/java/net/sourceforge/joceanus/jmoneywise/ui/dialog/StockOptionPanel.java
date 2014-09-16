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
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.field.JFieldManager;
import net.sourceforge.joceanus.jmetis.field.JFieldSet;
import net.sourceforge.joceanus.jmetis.field.JFieldSet.FieldUpdate;
import net.sourceforge.joceanus.jmetis.viewer.DataType;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio.PortfolioList;
import net.sourceforge.joceanus.jmoneywise.data.Security;
import net.sourceforge.joceanus.jmoneywise.data.Security.SecurityList;
import net.sourceforge.joceanus.jmoneywise.data.StockOption;
import net.sourceforge.joceanus.jmoneywise.data.StockOption.StockOptionList;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseIcons;
import net.sourceforge.joceanus.jmoneywise.ui.controls.MoneyWiseUIControlResource;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.ui.ErrorPanel;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayButton;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.swing.JIconButton;
import net.sourceforge.joceanus.jtethys.swing.JIconButton.ComplexIconButtonState;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;
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
     * Name Text Field.
     */
    private final JTextField theName;

    /**
     * Description Text Field.
     */
    private final JTextField theDesc;

    /**
     * Portfolio Button Field.
     */
    private final JScrollButton<Portfolio> thePortfolioButton;

    /**
     * Security Button Field.
     */
    private final JScrollButton<Security> theSecurityButton;

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
     * @param pView the data view
     * @param pFieldMgr the field manager
     * @param pUpdateSet the update set
     * @param pError the error panel
     */
    public StockOptionPanel(final View pView,
                            final JFieldManager pFieldMgr,
                            final UpdateSet<MoneyWiseDataType> pUpdateSet,
                            final ErrorPanel pError) {
        /* Initialise the panel */
        super(pFieldMgr, pUpdateSet, pError);

        /* Create the text fields */
        theName = new JTextField();
        theDesc = new JTextField();

        /* Create the buttons */
        thePortfolioButton = new JScrollButton<Portfolio>();
        theSecurityButton = new JScrollButton<Security>();

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
        // JPanel myPanel = buildNotesPanel();
        // myTabs.add(AccountInfoClass.NOTES.toString(), myPanel);

        /* Create the StockOptionVests table */
        theVests = new StockOptionVestTable(pFieldMgr, getUpdateSet(), pError);
        myTabs.add(TAB_VESTS, theVests.getPanel());

        /* Layout the main panel */
        JPanel myPanel = getMainPanel();
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
        MoneyWiseIcons.buildOptionButton(theClosedState);

        /* Build the units text field */
        JTextField myUnits = new JTextField();

        /* restrict the fields */
        restrictField(theName, StockOption.NAMELEN);
        restrictField(theDesc, StockOption.NAMELEN);
        restrictField(thePortfolioButton, StockOption.NAMELEN);
        restrictField(theSecurityButton, StockOption.NAMELEN);
        restrictField(theGrantButton, StockOption.NAMELEN);
        restrictField(theExpiryButton, StockOption.NAMELEN);
        restrictField(myUnits, StockOption.NAMELEN);
        restrictField(myClosedButton, StockOption.NAMELEN);

        theFieldSet.addFieldElement(StockOption.FIELD_NAME, DataType.STRING, theName);
        theFieldSet.addFieldElement(StockOption.FIELD_DESC, DataType.STRING, theDesc);
        theFieldSet.addFieldElement(StockOption.FIELD_PORTFOLIO, Portfolio.class, thePortfolioButton);
        theFieldSet.addFieldElement(StockOption.FIELD_SECURITY, Security.class, theSecurityButton);
        theFieldSet.addFieldElement(StockOption.FIELD_GRANTDATE, DataType.DATEDAY, theGrantButton);
        theFieldSet.addFieldElement(StockOption.FIELD_EXPIREDATE, DataType.DATEDAY, theExpiryButton);
        theFieldSet.addFieldElement(StockOption.FIELD_UNITS, DataType.UNITS, myUnits);
        theFieldSet.addFieldElement(StockOption.FIELD_CLOSED, Boolean.class, myClosedButton);

        /* Create the main panel */
        JEnablePanel myPanel = new JEnablePanel();

        /* Layout the panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        theFieldSet.addFieldToPanel(StockOption.FIELD_NAME, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_DESC, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_PORTFOLIO, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_SECURITY, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_GRANTDATE, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_EXPIREDATE, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_UNITS, myPanel);
        theFieldSet.addFieldToPanel(StockOption.FIELD_CLOSED, myPanel);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    // /**
    // * Build Notes subPanel.
    // * @return the panel
    // */
    // private JPanel buildNotesPanel() {
    // /* Allocate fields */
    // JTextArea myNotes = new JTextArea();
    // JScrollPane myScroll = new JScrollPane(myNotes);
    //
    // /* Adjust FieldSet */
    // theFieldSet.addFieldElement(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), DataType.CHARARRAY, myScroll);
    //
    // /* Create the notes panel */
    // JEnablePanel myPanel = new JEnablePanel();
    //
    // /* Layout the notes panel */
    // SpringLayout mySpring = new SpringLayout();
    // myPanel.setLayout(mySpring);
    // theFieldSet.addFieldToPanel(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), myPanel);
    // SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);
    //
    // /* Return the new panel */
    // return myPanel;
    // }

    @Override
    public void refreshData() {
        /* If we have an item */
        StockOption myItem = getItem();
        if (myItem != null) {
            StockOptionList myOptions = findDataList(MoneyWiseDataType.STOCKOPTION, StockOptionList.class);
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
        theClosedState.setState(bEditClosed);

        /* Determine whether the description field should be visible */
        boolean bShowDesc = isEditable || myOption.getDesc() != null;
        theFieldSet.setVisibility(StockOption.FIELD_DESC, bShowDesc);

        /* Determine whether the account details should be visible */
        // boolean bShowNotes = isEditable || mySecurity.getNotes() != null;
        // theFieldSet.setVisibility(SecurityInfoSet.getFieldForClass(AccountInfoClass.NOTES), bShowNotes);

        /* Portfolio/Security/Dates/Units cannot be changed if the item is active */
        theFieldSet.setEditable(StockOption.FIELD_PORTFOLIO, isEditable && !bIsActive);
        theFieldSet.setEditable(StockOption.FIELD_SECURITY, isEditable && !bIsActive);
        theFieldSet.setEditable(StockOption.FIELD_GRANTDATE, isEditable && !bIsActive);
        theFieldSet.setEditable(StockOption.FIELD_EXPIREDATE, isEditable && !bIsActive);
        theFieldSet.setEditable(StockOption.FIELD_UNITS, isEditable && !bIsActive);
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
        } else if (myField.equals(StockOption.FIELD_PORTFOLIO)) {
            /* Update the Portfolio */
            myOption.setPortfolio(pUpdate.getValue(Portfolio.class));
        } else if (myField.equals(StockOption.FIELD_SECURITY)) {
            /* Update the Security */
            myOption.setSecurity(pUpdate.getValue(Security.class));
        } else if (myField.equals(StockOption.FIELD_GRANTDATE)) {
            /* Update the GrantDate */
            myOption.setGrantDate(pUpdate.getDateDay());
        } else if (myField.equals(StockOption.FIELD_EXPIREDATE)) {
            /* Update the ExpiryDate */
            myOption.setExpiryDate(pUpdate.getDateDay());
        } else if (myField.equals(StockOption.FIELD_UNITS)) {
            /* Update the Units */
            myOption.setUnits(pUpdate.getUnits());
        } else if (myField.equals(StockOption.FIELD_CLOSED)) {
            /* Update the Closed indication */
            myOption.setClosed(pUpdate.getBoolean());
            // } else {
            // /* Switch on the field */
            // switch (SecurityInfoSet.getClassForField(myField)) {
            // case NOTES:
            // mySecurity.setNotes(pUpdate.getCharArray());
            // break;
            // default:
            // break;
            // }
        }
    }

    @Override
    protected void buildGoToMenu() {
        StockOption myItem = getItem();
        Portfolio myPortfolio = myItem.getPortfolio();
        Security mySecurity = myItem.getSecurity();
        buildGoToEvent(myPortfolio);
        buildGoToEvent(mySecurity);
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
            implements ChangeListener {
        /**
         * The Portfolio Menu Builder.
         */
        private final JScrollMenuBuilder<Portfolio> thePortfolioMenuBuilder;

        /**
         * The Security Menu Builder.
         */
        private final JScrollMenuBuilder<Security> theSecurityMenuBuilder;

        /**
         * Constructor.
         */
        private StockOptionListener() {
            /* Access the MenuBuilders */
            theSecurityMenuBuilder = theSecurityButton.getMenuBuilder();
            theSecurityMenuBuilder.addChangeListener(this);
            thePortfolioMenuBuilder = thePortfolioButton.getMenuBuilder();
            thePortfolioMenuBuilder.addChangeListener(this);
            theVests.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            Object o = pEvent.getSource();

            /* Handle menu type */
            if (theSecurityMenuBuilder.equals(o)) {
                buildSecurityMenu();
            } else if (thePortfolioMenuBuilder.equals(o)) {
                buildPortfolioMenu();
            } else if (theVests.equals(o)) {
                updateActions();
                fireStateChanged();
            }
        }

        /**
         * Build the security list for the item.
         */
        private void buildSecurityMenu() {
            /* Clear the menu */
            theSecurityMenuBuilder.clearMenu();

            /* Record active item */
            StockOption myOption = getItem();
            Security myCurr = myOption.getSecurity();
            JMenuItem myActive = null;

            /* Access Securities */
            SecurityList mySecurities = findDataList(MoneyWiseDataType.SECURITY, SecurityList.class);

            /* Loop through the Securities */
            Iterator<Security> myIterator = mySecurities.iterator();
            while (myIterator.hasNext()) {
                Security mySecurity = myIterator.next();

                /* Ignore deleted or closed */
                boolean bIgnore = mySecurity.isDeleted() || mySecurity.isClosed();
                if (bIgnore) {
                    continue;
                }

                /* Create a new action for the security */
                JMenuItem myItem = theSecurityMenuBuilder.addItem(mySecurity);

                /* If this is the active security */
                if (mySecurity.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            theSecurityMenuBuilder.showItem(myActive);
        }

        /**
         * Build the portfolio list for the item.
         */
        private void buildPortfolioMenu() {
            /* Clear the menu */
            thePortfolioMenuBuilder.clearMenu();

            /* Record active item */
            StockOption myOption = getItem();
            Portfolio myCurr = myOption.getPortfolio();
            JMenuItem myActive = null;

            /* Access Portfolios */
            PortfolioList myPortfolios = findDataList(MoneyWiseDataType.PORTFOLIO, PortfolioList.class);

            /* Loop through the Portfolios */
            Iterator<Portfolio> myIterator = myPortfolios.iterator();
            while (myIterator.hasNext()) {
                Portfolio myPortfolio = myIterator.next();

                /* Ignore deleted or closed */
                boolean bIgnore = myPortfolio.isDeleted() || myPortfolio.isClosed();
                if (bIgnore) {
                    continue;
                }

                /* Create a new action for the portfolio */
                JMenuItem myItem = thePortfolioMenuBuilder.addItem(myPortfolio);

                /* If this is the active portfolio */
                if (myPortfolio.equals(myCurr)) {
                    /* Record it */
                    myActive = myItem;
                }
            }

            /* Ensure active item is visible */
            thePortfolioMenuBuilder.showItem(myActive);
        }
    }
}
