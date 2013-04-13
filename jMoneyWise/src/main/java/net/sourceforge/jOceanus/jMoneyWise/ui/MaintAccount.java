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

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.jOceanus.jDataManager.DataType;
import net.sourceforge.jOceanus.jDataManager.EditState;
import net.sourceforge.jOceanus.jDataManager.JDataException;
import net.sourceforge.jOceanus.jDataManager.JDataException.ExceptionClass;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDataManager.JDataManager;
import net.sourceforge.jOceanus.jDataManager.JDataManager.JDataEntry;
import net.sourceforge.jOceanus.jDataModels.ui.ErrorPanel;
import net.sourceforge.jOceanus.jDataModels.ui.SaveButtons;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jDataModels.views.UpdateEntry;
import net.sourceforge.jOceanus.jDataModels.views.UpdateSet;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayButton;
import net.sourceforge.jOceanus.jEventManager.ActionDetailEvent;
import net.sourceforge.jOceanus.jEventManager.JEventPanel;
import net.sourceforge.jOceanus.jFieldSet.JFieldManager;
import net.sourceforge.jOceanus.jFieldSet.JFieldSet;
import net.sourceforge.jOceanus.jFieldSet.JFieldSet.FieldUpdate;
import net.sourceforge.jOceanus.jLayoutManager.SpringUtilities;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.data.Account.AccountList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountCategory.AccountCategoryList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfo.AccountInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.AccountInfoSet;
import net.sourceforge.jOceanus.jMoneyWise.data.FinanceData;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.AccountCategoryClass;
import net.sourceforge.jOceanus.jMoneyWise.ui.controls.AccountSelect;
import net.sourceforge.jOceanus.jMoneyWise.views.View;

/**
 * Account maintenance panel.
 * @author Tony Washer
 */
public class MaintAccount
        extends JEventPanel {
    /**
     * The Serial Id.
     */
    private static final long serialVersionUID = -1979836058846481969L;

    /**
     * Padding size.
     */
    private static final int PADDING_SIZE = 5;

    /**
     * Date width.
     */
    private static final int DATE_WIDTH = 90;

    /**
     * Account width.
     */
    private static final int ACT_WIDTH = 150;

    /**
     * Description width.
     */
    private static final int DESC_WIDTH = 300;

    /**
     * Column Height.
     */
    private static final int COL_HEIGHT = 20;

    /**
     * The detail panel.
     */
    private final JPanel theDetail;

    /**
     * The buttons panel.
     */
    private final JPanel theButtons;

    /**
     * The security panel.
     */
    private final JPanel theSecure;

    /**
     * The select panel.
     */
    private final AccountSelect theSelect;

    /**
     * The save panel.
     */
    private final SaveButtons theSaveButs;

    /**
     * The Field Set.
     */
    private final transient JFieldSet<Account> theFieldSet;

    /**
     * The first event.
     */
    private final JTextField theFirst;

    /**
     * The last event.
     */
    private final JTextField theLast;

    /**
     * The account status.
     */
    private final JTextField theStatus;

    /**
     * The categories comboBox.
     */
    private final JComboBox<AccountCategory> theCategoriesBox;

    /**
     * The parent comboBox.
     */
    private final JComboBox<Account> theParentBox;

    /**
     * The alias comboBox.
     */
    private final JComboBox<Account> theAliasBox;

    /**
     * The insert button.
     */
    private final JButton theInsButton;

    /**
     * The delete button.
     */
    private final JButton theDelButton;

    /**
     * The Close button.
     */
    private final JButton theClsButton;

    /**
     * The Account.
     */
    private transient Account theAccount = null;

    /**
     * The Accounts List.
     */
    private transient AccountList theAccounts = null;

    /**
     * The Account view.
     */
    private transient AccountList theActView = null;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataEntry;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * Are we showing closed accounts?
     */
    private boolean doShowClosed = false;

    /**
     * Are we editing a new account?
     */
    private boolean isNewAccount = false;

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The Update Set.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The view list.
     */
    private final transient UpdateEntry<Account> theAccountEntry;

    /**
     * The AccountInfo Update Entry.
     */
    private final transient UpdateEntry<AccountInfo> theInfoEntry;

    /**
     * Obtain the account.
     * @return the account
     */
    public Account getAccount() {
        return theAccount;
    }

    /**
     * Constructor.
     * @param pView the data view
     */
    public MaintAccount(final View pView) {
        /* Access the view */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();

        /* Build the Update set and Entry */
        theUpdateSet = new UpdateSet(theView);
        theAccountEntry = theUpdateSet.registerClass(Account.class);
        theInfoEntry = theUpdateSet.registerClass(AccountInfo.class);

        /* Create the New FieldSet */
        theFieldSet = new JFieldSet<Account>(theFieldMgr);

        /* Create the status fields */
        theFirst = new JTextField();
        theLast = new JTextField();
        theStatus = new JTextField();

        /* Create the combo boxes */
        theCategoriesBox = new JComboBox<AccountCategory>();
        theParentBox = new JComboBox<Account>();
        theAliasBox = new JComboBox<Account>();

        /* Create the buttons */
        theInsButton = new JButton("New");
        theDelButton = new JButton();
        theClsButton = new JButton();

        /* Create the listener */
        AccountListener myListener = new AccountListener();

        /* Add listeners */
        theInsButton.addActionListener(myListener);
        theDelButton.addActionListener(myListener);
        theClsButton.addActionListener(myListener);
        theFieldSet.addActionListener(myListener);

        /* Create the Account Selection panel */
        theSelect = new AccountSelect(theView, true);
        theSelect.addChangeListener(myListener);

        /* Create the Save buttons panel */
        theSaveButs = new SaveButtons(theUpdateSet);
        theSaveButs.addActionListener(myListener);

        /* Create the debug entry, attach to Views */
        JDataManager myDataMgr = theView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry(Account.class.getSimpleName());
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataEntry);
        theError.addChangeListener(myListener);

        /* Create the buttons panel */
        theButtons = new JPanel();
        theButtons.setBorder(BorderFactory.createTitledBorder("Actions"));

        /* Create the layout for the panel */
        theButtons.setLayout(new BoxLayout(theButtons, BoxLayout.X_AXIS));
        theButtons.add(Box.createRigidArea(new Dimension(PADDING_SIZE, 0)));
        theButtons.add(theInsButton);
        theButtons.add(Box.createHorizontalGlue());
        theButtons.add(theDelButton);
        theButtons.add(Box.createHorizontalGlue());
        theButtons.add(theClsButton);
        theButtons.add(Box.createRigidArea(new Dimension(PADDING_SIZE, 0)));

        /* Create the security panel */
        theSecure = buildSecurityPanel();

        /* Create the detail panel */
        theDetail = buildDetailsPanel();

        /* Create the status panel */
        JPanel myStatus = buildStatusPanel();

        /* Create the combined details/status */
        JPanel myMain = new JPanel();
        myMain.setLayout(new BoxLayout(myMain, BoxLayout.X_AXIS));
        myMain.add(theDetail);
        myMain.add(myStatus);

        /* Create layout */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theError);
        add(Box.createVerticalGlue());
        add(theSelect);
        add(Box.createVerticalGlue());
        add(myMain);
        add(Box.createVerticalGlue());
        add(theSecure);
        add(Box.createVerticalGlue());
        add(theButtons);
        add(Box.createVerticalGlue());
        add(theSaveButs);

        /* Set initial display */
        showAccount();

        /* Add listener to updateSet */
        theUpdateSet.addActionListener(myListener);
    }

    /**
     * Build Account details panel.
     * @return the panel
     */
    private JPanel buildDetailsPanel() {
        /* Create the labels */
        JLabel myNameLabel = new JLabel("Name:", SwingConstants.TRAILING);
        JLabel myDescLabel = new JLabel("Description:", SwingConstants.TRAILING);
        JLabel myTypeLabel = new JLabel("AccountType:", SwingConstants.TRAILING);
        JLabel myMatLabel = new JLabel("Maturity:", SwingConstants.TRAILING);
        JLabel myParLabel = new JLabel("Parent:", SwingConstants.TRAILING);
        JLabel myAlsLabel = new JLabel("Alias:", SwingConstants.TRAILING);

        /* Allocate the TextFields */
        JTextField myName = new JTextField();
        JTextField myDesc = new JTextField();
        JDateDayButton myMaturity = new JDateDayButton();

        /* Set sizes */
        Dimension myActDims = new Dimension(ACT_WIDTH, COL_HEIGHT);
        Dimension myDescDims = new Dimension(DESC_WIDTH, COL_HEIGHT);
        myName.setMaximumSize(myActDims);
        myDesc.setMaximumSize(myDescDims);
        theParentBox.setMaximumSize(myActDims);
        theAliasBox.setMaximumSize(myActDims);
        theCategoriesBox.setMaximumSize(myActDims);

        /*
         * Dimension fields correctly /* Add the components to the field Set
         */
        theFieldSet.addFieldElement(Account.FIELD_NAME, DataType.STRING, myNameLabel, myName);
        theFieldSet.addFieldElement(Account.FIELD_DESC, DataType.STRING, myDescLabel, myDesc);
        theFieldSet.addFieldElement(Account.FIELD_CATEGORY, AccountCategory.class, myTypeLabel, theCategoriesBox);
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_PARENT, Account.class, myParLabel, theParentBox);
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_ALIAS, Account.class, myAlsLabel, theAliasBox);
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_MATURITY, DataType.DATEDAY, myMatLabel, myMaturity);

        /* Create the limits panel */
        JPanel myPanel = new JPanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Account Details"));

        /* Layout the limits panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myNameLabel);
        myPanel.add(myName);
        myPanel.add(myDescLabel);
        myPanel.add(myDesc);
        myPanel.add(myTypeLabel);
        myPanel.add(theCategoriesBox);
        myPanel.add(myParLabel);
        myPanel.add(theParentBox);
        myPanel.add(myAlsLabel);
        myPanel.add(theAliasBox);
        myPanel.add(myMatLabel);
        myPanel.add(myMaturity);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Account status panel.
     * @return the panel
     */
    private JPanel buildStatusPanel() {
        /* Create the labels */
        JLabel myStatus = new JLabel("Status:", SwingConstants.TRAILING);
        JLabel myFirst = new JLabel("FirstEvent:", SwingConstants.TRAILING);
        JLabel myLast = new JLabel("LastEvent:", SwingConstants.TRAILING);

        /* Set sizes */
        Dimension myDims = new Dimension(DATE_WIDTH, COL_HEIGHT);
        theFirst.setMaximumSize(myDims);
        theLast.setMaximumSize(myDims);
        theStatus.setMaximumSize(myDims);

        /* Hide edit field and border */
        theFirst.setEditable(false);
        theLast.setEditable(false);
        theStatus.setEditable(false);
        theFirst.setBorder(null);
        theLast.setBorder(null);
        theStatus.setBorder(null);

        /* Create the limits panel */
        JPanel myPanel = new JPanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Account Status"));

        /* Layout the limits panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myStatus);
        myPanel.add(theStatus);
        myPanel.add(myFirst);
        myPanel.add(theFirst);
        myPanel.add(myLast);
        myPanel.add(theLast);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Build Security details panel.
     * @return the panel
     */
    private JPanel buildSecurityPanel() {
        /* Create the labels */
        JLabel myWebSiteLabel = new JLabel("WebSite:", SwingConstants.TRAILING);
        JLabel myCustNoLabel = new JLabel("CustomerNo:", SwingConstants.TRAILING);
        JLabel myUserIdLabel = new JLabel("UserId:", SwingConstants.TRAILING);
        JLabel myPasswordLabel = new JLabel("Password:", SwingConstants.TRAILING);
        JLabel myAccountLabel = new JLabel("Account:", SwingConstants.TRAILING);
        JLabel myNotesLabel = new JLabel("Notes:", SwingConstants.TRAILING);

        /* Allocate the TextFields */
        JTextField myWebSite = new JTextField();
        JTextField myCustNo = new JTextField();
        JTextField myUserId = new JTextField();
        JTextField myPassword = new JTextField();
        JTextField myAccount = new JTextField();
        JTextField myNotes = new JTextField();

        /* Set the column widths */
        myWebSite.setColumns(Account.WSITELEN);
        myCustNo.setColumns(Account.CUSTLEN);
        myUserId.setColumns(Account.UIDLEN);
        myPassword.setColumns(Account.PWDLEN);
        myAccount.setColumns(Account.ACTLEN);
        myNotes.setColumns(Account.WSITELEN);

        /* Add the components to the field Set */
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_WEBSITE, DataType.CHARARRAY, myWebSiteLabel, myWebSite);
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_CUSTNO, DataType.CHARARRAY, myCustNoLabel, myCustNo);
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_USERID, DataType.CHARARRAY, myUserIdLabel, myUserId);
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_PASSWORD, DataType.CHARARRAY, myPasswordLabel, myPassword);
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_ACCOUNT, DataType.CHARARRAY, myAccountLabel, myAccount);
        theFieldSet.addFieldElement(AccountInfoSet.FIELD_NOTES, DataType.CHARARRAY, myNotesLabel, myNotes);

        /* Create the limits panel */
        JPanel myPanel = new JPanel();
        myPanel.setBorder(BorderFactory.createTitledBorder("Security"));

        /* Layout the limits panel */
        SpringLayout mySpring = new SpringLayout();
        myPanel.setLayout(mySpring);
        myPanel.add(myWebSiteLabel);
        myPanel.add(myWebSite);
        myPanel.add(myCustNoLabel);
        myPanel.add(myCustNo);
        myPanel.add(myUserIdLabel);
        myPanel.add(myUserId);
        myPanel.add(myPasswordLabel);
        myPanel.add(myPassword);
        myPanel.add(myAccountLabel);
        myPanel.add(myAccount);
        myPanel.add(myNotesLabel);
        myPanel.add(myNotes);
        SpringUtilities.makeCompactGrid(myPanel, mySpring, myPanel.getComponentCount() >> 1, 2, PADDING_SIZE);

        /* Return the new panel */
        return myPanel;
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        theDataEntry.setFocus();
    }

    /**
     * Does this panel have updates?
     * @return true/false
     */
    public boolean hasUpdates() {
        return ((theAccount != null) && (theAccount.hasChanges()));
    }

    /**
     * Do we have errors?
     * @return true/false
     */
    public boolean hasErrors() {
        return ((theAccount != null) && (theAccount.hasErrors()));
    }

    /**
     * Obtain the edit state.
     * @return the EditState
     */
    public EditState getEditState() {
        return (theAccount == null)
                ? EditState.CLEAN
                : theAccount.getEditState();
    }

    /**
     * Notify Changes.
     */
    public void notifyChanges() {
        /* Lock down the table buttons and the selection */
        theSaveButs.setEnabled(true);
        theSelect.setEnabled(!hasUpdates());

        /* Show the account */
        showAccount();

        /* Adjust visible tabs */
        fireStateChanged();
    }

    /**
     * refreshData.
     */
    public void refreshData() {
        /* Access the data */
        FinanceData myData = theView.getData();

        /* Access categories */
        AccountCategoryList myCatList = myData.getAccountCategories();
        theAccounts = myData.getAccounts();

        /* Note that we are refreshing data */
        theFieldSet.setRefreshingData(true);

        /* Refresh the account selection */
        theSelect.refreshData();

        /* If we have categories already populated */
        if (theCategoriesBox.getItemCount() > 0) {
            /* Remove the categories */
            theCategoriesBox.removeAllItems();
        }

        /* Create an account type iterator */
        Iterator<AccountCategory> myCatIterator = myCatList.iterator();

        /* Add the AccountCategory values to the categories box */
        while (myCatIterator.hasNext()) {
            AccountCategory myCategory = myCatIterator.next();

            /* Ignore the category if it is reserved or not enabled */
            if ((myCategory.getCategoryTypeClass().isSingular())
                || (!myCategory.getCategoryType().getEnabled())) {
                continue;
            }

            /* Add the item to the list */
            theCategoriesBox.addItem(myCategory);
        }

        /* Note that we have finished refreshing data */
        theFieldSet.setRefreshingData(false);

        /* refresh the parents */
        refreshParents();

        /* Adjust the account selection */
        if (theAccount != null) {
            theSelect.setSelection(theAccount);
        }

        /* Show the account */
        setSelection(theSelect.getSelected());
    }

    /**
     * refreshParents.
     */
    private void refreshParents() {
        /* Note that we are refreshing data */
        theFieldSet.setRefreshingData(true);

        /* If we have parents already populated */
        if (theParentBox.getItemCount() > 0) {
            /* Remove them */
            theParentBox.removeAllItems();
        }

        /* Create an account iterator */
        Iterator<Account> myActIterator = theAccounts.iterator();

        /* Add the Account values to the parents box */
        while (myActIterator.hasNext()) {
            Account myAcct = myActIterator.next();

            /* Access the category */
            // AccountCategory myType = myAcct.getAccountCategory();

            /* Ignore the account if it is not an owner */
            if (!myAcct.isParent()) {
                continue;
            }

            /* Ignore the account if it is closed and we are not showing closed */
            if (myAcct.isClosed()
                && (!doShowClosed)) {
                continue;
            }

            /* Add the item to the list */
            theParentBox.addItem(myAcct);
        }

        /* Note that we have finished refreshing data */
        theFieldSet.setRefreshingData(false);
    }

    /**
     * Select an explicit account.
     * @param pAccount the account
     */
    public void selectAccount(final Account pAccount) {
        /* Adjust the account selection */
        theSelect.setSelection(pAccount);

        /* Redraw selection */
        setSelection(theSelect.getSelected());
    }

    /**
     * Set Selection.
     * @param pAccount the account
     */
    public void setSelection(final Account pAccount) {
        /* Protect against exceptions */
        try {
            /* Reset controls */
            theActView = null;
            theAccount = null;
            AccountInfoList myInfo = null;

            /* If we have a selected account */
            if (pAccount != null) {
                /* Create the view of the account */
                theActView = theAccounts.deriveEditList(pAccount);
                myInfo = theActView.getAccountInfo();

                /* Access the account */
                theAccount = theActView.findItemByName(pAccount.getName());
            }

            /* Set List */
            theAccountEntry.setDataList(theActView);
            theInfoEntry.setDataList(myInfo);

            /* Clear new account flag */
            isNewAccount = false;

            /* notify changes */
            notifyChanges();
        } catch (JDataException e) {
        }
    }

    /**
     * Show the account.
     */
    private void showAccount() {
        /* Access the category from the selection */
        AccountCategory myCategory = theSelect.getCategory();

        /* If we have an active account */
        if (theAccount != null) {
            /* Note that we are refreshing data */
            theFieldSet.setRefreshingData(true);

            /* Access details */
            boolean isClosed = theAccount.isClosed();
            boolean bActive = !theAccount.isDeleted();
            AccountCategoryClass myCatClass = theAccount.getAccountCategoryClass();

            /* Set the visibility */
            theFieldSet.setVisibility(Account.FIELD_CATEGORY, theAccount.isDeletable()
                                                              && (isNewAccount));
            theFieldSet.setVisibility(AccountInfoSet.FIELD_MATURITY, (myCatClass == AccountCategoryClass.Bond));
            theFieldSet.setVisibility(AccountInfoSet.FIELD_PARENT, myCatClass.isChild());

            /* Handle alias */
            if (myCategory.getCategoryTypeClass().canAlias()
                && (!theAccount.isAliasedTo())) {

                /* Set visible */
                theFieldSet.setVisibility(AccountInfoSet.FIELD_ALIAS, true);

                /* If we have alias already populated */
                if (theAliasBox.getItemCount() > 0) {
                    /* Remove the items */
                    theAliasBox.removeAllItems();
                }

                /* Create an account iterator */
                Iterator<Account> myActIterator = theAccounts.iterator();

                /* Add the Account values to the parents box */
                while (myActIterator.hasNext()) {
                    Account myAcct = myActIterator.next();

                    /* Access the category */
                    myCategory = myAcct.getAccountCategory();

                    /* Ignore the account if it cannot alias */
                    if (!myCategory.getCategoryTypeClass().canAlias()) {
                        continue;
                    }

                    /* Ignore the account if it is same category */
                    if (myCategory.equals(theAccount.getAccountCategory())) {
                        continue;
                    }

                    /* Ignore the account if it is an alias */
                    if (myAcct.getAlias() != null) {
                        continue;
                    }

                    /* Ignore the account if it is us */
                    if (myAcct.compareTo(theAccount) == 0) {
                        continue;
                    }

                    /* Add the item to the list */
                    theAliasBox.addItem(myAcct);
                }
            } else {
                /* Set visible */
                theFieldSet.setVisibility(AccountInfoSet.FIELD_ALIAS, false);
            }

            /* Render the FieldSet */
            theFieldSet.renderSet(theAccount);

            /* Access the formatter */
            FinanceData myData = theView.getData();
            JDataFormatter myFormatter = myData.getDataFormatter();

            /* Set the First Event */
            theFirst.setText((theAccount.getEarliest() != null)
                    ? myFormatter.formatObject(theAccount.getEarliest().getDate())
                    : "N/A");

            /* Set the Last Event */
            theLast.setText((theAccount.getLatest() != null)
                    ? myFormatter.formatObject(theAccount.getLatest().getDate())
                    : "N/A");

            /* Set the Status */
            theStatus.setText(determineStatus().toString());

            /* Set text for close button */
            theClsButton.setText((isClosed)
                    ? "ReOpen"
                    : "Close");

            /* Make sure buttons are visible */
            boolean isDeletable = theAccount.isDeletable();
            theDelButton.setVisible(isDeletable);
            theDelButton.setText("Delete");
            theClsButton.setVisible(bActive
                                    && !isDeletable);

            /* Enable buttons */
            theInsButton.setEnabled(!theAccount.hasChanges()
                                    && (!myCatClass.isSingular()));
            theClsButton.setEnabled((isClosed)
                                    || (theAccount.isCloseable()));

            /* Note that we are finished refreshing data */
            theFieldSet.setRefreshingData(false);

            /* else no account selected */
        } else {
            /* Clear details */
            theFirst.setText("");
            theLast.setText("");
            theStatus.setText("");

            /* Disable field entry */
            theClsButton.setVisible(false);

            theInsButton.setEnabled(myCategory != null);
            theDelButton.setVisible(false);
            theDelButton.setText("Recover");

            /* Hide the optional fields */
            theFieldSet.setVisibility(Account.FIELD_CATEGORY, false);
            theFieldSet.setVisibility(AccountInfoSet.FIELD_MATURITY, false);
            theFieldSet.setVisibility(AccountInfoSet.FIELD_PARENT, false);
            theFieldSet.setVisibility(AccountInfoSet.FIELD_ALIAS, false);

            /* Render the Null Field Set */
            theFieldSet.renderNullSet();
        }
    }

    /**
     * Delete New Account.
     */
    private void delNewAccount() {
        /* Set the previously selected account */
        setSelection(theSelect.getSelected());
    }

    /**
     * New Account.
     */
    private void newAccount() {
        /* Protect against exceptions */
        try {
            /* Create a account View for an empty account */
            theActView = theAccounts.deriveEditList(theSelect.getCategory());
            AccountInfoList myInfo = theActView.getAccountInfo();

            /* Access the account */
            theAccount = theActView.getAccount();

            /* Set List */
            theAccountEntry.setDataList(theActView);
            theInfoEntry.setDataList(myInfo);
            theUpdateSet.incrementVersion();

            /* Set new account flag */
            isNewAccount = true;

            /* Notify changes */
            notifyChanges();
        } catch (JDataException e) {
        }
    }

    /**
     * AccountListener class.
     */
    private final class AccountListener
            implements ActionListener, ChangeListener {
        @Override
        public void actionPerformed(final ActionEvent e) {
            Object o = e.getSource();

            /* If the event relates to the Field Set */
            if ((theFieldSet.equals(o))
                && (e instanceof ActionDetailEvent)) {
                /* Access event and obtain details */
                ActionDetailEvent evt = (ActionDetailEvent) e;
                Object dtl = evt.getDetails();
                if (dtl instanceof FieldUpdate) {
                    /* Update the field */
                    updateField((FieldUpdate) dtl);
                }

                /* If this event relates to the save buttons */
            } else if (theSaveButs.equals(o)) {
                /* If this is a new Account */
                if (isNewAccount) {
                    /* Access the command */
                    String myCmd = e.getActionCommand();

                    /* If the command is reset/last undo */
                    if ((myCmd.equals(SaveButtons.CMD_RESET))
                        || ((myCmd.equals(SaveButtons.CMD_UNDO)) && (!theAccount.hasHistory()))) {
                        /* Delete new account */
                        delNewAccount();
                        return;
                    }
                }

                /* Perform the action */
                theUpdateSet.processCommand(e.getActionCommand(), theError);

                /* Notify of any changes */
                notifyChanges();
                return;

                /* If we are performing a rewind */
            } else if (theUpdateSet.equals(o)) {
                /* Refresh the account */
                showAccount();

                /* If this event relates to the new button */
            } else if (theInsButton.equals(o)) {
                /* Create the new account */
                newAccount();
                return;

                /* If this event relates to the delete button */
            } else if (theDelButton.equals(o)) {
                /* else if this is a new account */
                if (isNewAccount) {
                    /* Delete the new account */
                    delNewAccount();

                    /* Else we should just delete/recover the account */
                } else {
                    /* Flip the deleted state */
                    theAccount.setDeleted(!theAccount.isDeleted());

                    /* Increment the update version */
                    theUpdateSet.incrementVersion();

                    /* Notify changes */
                    notifyChanges();
                }
                return;
            }

            /* Push history */
            theAccount.pushHistory();

            /* If this event relates to the close button */
            if (theClsButton.equals(o)) {
                /* Re-open or close the account as required */
                if (theAccount.isClosed()) {
                    theAccount.reOpenAccount();
                } else {
                    theAccount.closeAccount();
                }
            }

            /* Check for changes */
            if (theAccount.checkForHistory()) {
                /* Increment the updateSet */
                theUpdateSet.incrementVersion();

                /* Note that changes have occurred */
                notifyChanges();
            }
        }

        /**
         * Update field.
         * @param pUpdate the update
         */
        private void updateField(final FieldUpdate pUpdate) {
            JDataField myField = pUpdate.getField();

            /* Push history */
            theAccount.pushHistory();

            /* Protect against exceptions */
            try {
                /* If this is our Name */
                if (myField.equals(Account.FIELD_NAME)) {
                    /* Update the Value */
                    theAccount.setAccountName(pUpdate.getString());
                    /* If this is our Description */
                } else if (myField.equals(Account.FIELD_DESC)) {
                    /* Update the Value */
                    theAccount.setDescription(pUpdate.getString());
                    /* If this is our Account type */
                } else if (myField.equals(Account.FIELD_CATEGORY)) {
                    /* Update the Value */
                    theAccount.setAccountCategory(pUpdate.getValue(AccountCategory.class));

                    /* If the account is now a bond */
                    if (theAccount.isCategoryClass(AccountCategoryClass.Bond)) {
                        /* If it doesn't have a maturity */
                        if (theAccount.getMaturity() == null) {
                            /* Create a default maturity */
                            theAccount.setMaturity(new JDateDay());
                            theAccount.getMaturity().adjustYear(1);
                        }

                        /* Else set maturity to null for non-bonds */
                    } else {
                        theAccount.setMaturity(null);
                    }

                    /* Set parent to null for non-child accounts */
                    if (!theAccount.getAccountCategoryClass().isChild()) {
                        theAccount.setParent(null);
                    }

                    /* If this is our Parent */
                } else if (myField.equals(AccountInfoSet.FIELD_PARENT)) {
                    /* Update the Value */
                    theAccount.setParent(pUpdate.getValue(Account.class));
                    /* If this is our Account type */
                } else if (myField.equals(AccountInfoSet.FIELD_ALIAS)) {
                    /* Update the Value */
                    theAccount.setAlias(pUpdate.getValue(Account.class));
                    /* If this is our Maturity */
                } else if (myField.equals(AccountInfoSet.FIELD_MATURITY)) {
                    /* Update the Value */
                    theAccount.setMaturity(pUpdate.getDateDay());
                    /* If this is our WebSite */
                } else if (myField.equals(AccountInfoSet.FIELD_WEBSITE)) {
                    /* Update the Value */
                    theAccount.setWebSite(pUpdate.getCharArray());
                    /* If this is our CustomerNo. */
                } else if (myField.equals(AccountInfoSet.FIELD_CUSTNO)) {
                    /* Update the Value */
                    theAccount.setCustNo(pUpdate.getCharArray());
                    /* If this is our UserId */
                } else if (myField.equals(AccountInfoSet.FIELD_USERID)) {
                    /* Update the Value */
                    theAccount.setUserId(pUpdate.getCharArray());
                    /* If this is our Password */
                } else if (myField.equals(AccountInfoSet.FIELD_PASSWORD)) {
                    /* Update the Value */
                    theAccount.setPassword(pUpdate.getCharArray());
                    /* If this is our Account */
                } else if (myField.equals(AccountInfoSet.FIELD_ACCOUNT)) {
                    /* Update the Value */
                    theAccount.setAccount(pUpdate.getCharArray());
                    /* If this is our Notes */
                } else if (myField.equals(AccountInfoSet.FIELD_NOTES)) {
                    /* Update the Value */
                    theAccount.setNotes(pUpdate.getCharArray());
                }

                /* Handle Exceptions */
            } catch (JDataException e) {
                /* Reset values */
                theAccount.popHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to update field", e);

                /* Show the error */
                theError.setError(myError);
                return;
            }

            /* Check for changes */
            if (theAccount.checkForHistory()) {
                /* Increment the update version */
                theUpdateSet.incrementVersion();

                /* Note that changes have occurred */
                notifyChanges();
            }
        }

        @Override
        public void stateChanged(final ChangeEvent evt) {
            Object o = evt.getSource();

            /* If this is the selection box */
            if (theSelect.equals(o)) {
                /* If we have changed the show closed option */
                if (theSelect.doShowClosed() != doShowClosed) {
                    /* Record details and refresh parents */
                    doShowClosed = theSelect.doShowClosed();
                    refreshParents();
                }

                /* Set the new account */
                setSelection(theSelect.getSelected());

                /* If this is the error panel reporting */
            } else if (theError.equals(o)) {
                /* Determine whether we have an error */
                boolean isError = theError.hasError();

                /* Hide selection panel */
                theSelect.setVisible(!isError);

                /* Lock data areas */
                theDetail.setEnabled(!isError);
                theSecure.setEnabled(!isError);
                theButtons.setEnabled(!isError);

                /* Lock Save Buttons */
                theSaveButs.setEnabled(!isError);
            }
        }
    }

    /**
     * Determine status.
     * @return the status
     */
    private AccountStatus determineStatus() {
        /* Handle new account */
        if (isNewAccount) {
            return AccountStatus.New;
        }

        /* Handle unused account */
        if (theAccount.isDeletable()) {
            return AccountStatus.Unused;
        }

        /* Handle deleted account */
        if (theAccount.isDeleted()) {
            return AccountStatus.Deleted;
        }

        /* Handle closed account */
        if (theAccount.isClosed()) {
            return AccountStatus.Closed;
        }

        /* Handle standard account */
        return (theAccount.isCloseable())
                ? AccountStatus.Inactive
                : AccountStatus.Active;
    }

    /**
     * Account Status.
     */
    private enum AccountStatus {
        /**
         * New account.
         */
        New,

        /**
         * Deleted account.
         */
        Deleted,

        /**
         * Closed account.
         */
        Closed,

        /**
         * Unused account (i.e. delete-able).
         */
        Unused,

        /**
         * Inactive account (i.e. close-able).
         */
        Inactive,

        /**
         * Active account.
         */
        Active;
    }
}
