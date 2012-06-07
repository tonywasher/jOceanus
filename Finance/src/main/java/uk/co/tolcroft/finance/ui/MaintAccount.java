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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.LayoutStyle;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import net.sourceforge.JDataManager.JDataObject;
import net.sourceforge.JDateDay.DateDay;
import net.sourceforge.JDateDay.DateDayButton;
import uk.co.tolcroft.finance.data.Account;
import uk.co.tolcroft.finance.data.Account.AccountList;
import uk.co.tolcroft.finance.data.AccountType;
import uk.co.tolcroft.finance.data.AccountType.AccountTypeList;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.ui.controls.AccountSelect;
import uk.co.tolcroft.finance.views.View;
import uk.co.tolcroft.models.data.DataList.DataListIterator;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.models.ui.ErrorPanel;
import uk.co.tolcroft.models.ui.ItemField;
import uk.co.tolcroft.models.ui.ItemField.FieldSet;
import uk.co.tolcroft.models.ui.SaveButtons;
import uk.co.tolcroft.models.ui.StdInterfaces.StdPanel;
import uk.co.tolcroft.models.ui.StdInterfaces.stdCommand;
import uk.co.tolcroft.models.ui.ValueField;
import uk.co.tolcroft.models.ui.ValueField.ValueClass;
import uk.co.tolcroft.models.views.ViewList;
import uk.co.tolcroft.models.views.ViewList.ListClass;

public class MaintAccount implements StdPanel {
    /* Properties */
    private MaintenanceTab theParent = null;
    private JPanel thePanel = null;
    private JPanel theDetail = null;
    private JPanel theStatus = null;
    private JPanel theButtons = null;
    private JPanel theSecure = null;
    private AccountSelect theSelect = null;
    private SaveButtons theSaveButs = null;
    private FieldSet theFieldSet = null;
    private ItemField theName = null;
    private ItemField theDesc = null;
    private ItemField theWebSite = null;
    private ItemField theCustNo = null;
    private ItemField theUserId = null;
    private ItemField thePassword = null;
    private ItemField theActDetail = null;
    private ItemField theNotes = null;
    private JLabel theFirst = null;
    private JLabel theLast = null;
    private JComboBox theTypesBox = null;
    private JComboBox theParentBox = null;
    private JComboBox theAliasBox = null;
    private DateDayButton theMatButton = null;
    private JLabel theTypLabel = null;
    private JLabel theParLabel = null;
    private JLabel theAlsLabel = null;
    private JLabel theMatLabel = null;
    private JButton theInsButton = null;
    private JButton theDelButton = null;
    private JButton theClsButton = null;
    private JButton theUndoButton = null;
    private Account theAccount = null;
    private AccountList theAccounts = null;
    private AccountList theActView = null;
    private AccountTypeList theAcTypList = null;
    private JDataEntry theDataEntry = null;
    private ErrorPanel theError = null;
    private boolean doShowClosed = false;
    private boolean refreshingData = false;
    private boolean typesPopulated = false;
    private boolean parPopulated = false;
    private boolean alsPopulated = false;
    private View theView = null;
    private ViewList theViewSet = null;
    private ListClass theViewList = null;

    /* Access methods */
    public JPanel getPanel() {
        return thePanel;
    }

    public Account getAccount() {
        return theAccount;
    }

    /* Access the debug entry */
    @Override
    public JDataEntry getDataEntry() {
        return theDataEntry;
    }

    @Override
    public JDataManager getDataManager() {
        return theParent.getDataManager();
    }

    /* Constructor */
    public MaintAccount(MaintenanceTab pParent) {
        JLabel myName;
        JLabel myDesc;
        JLabel myFirst;
        JLabel myLast;
        JLabel myWebSite;
        JLabel myCustNo;
        JLabel myUserId;
        JLabel myPassword;
        JLabel myAccount;
        JLabel myNotes;
        String myDefValue;
        char[] myDefChars;

        /* Store passed data */
        theParent = pParent;

        /* Access the view */
        theView = pParent.getView();

        /* Build the View set and List */
        theViewSet = new ViewList(theView);
        theViewList = theViewSet.registerClass(Account.class);

        /* Create the labels */
        myName = new JLabel("Name:");
        myDesc = new JLabel("Description:");
        theTypLabel = new JLabel("AccountType:");
        theMatLabel = new JLabel("Maturity:");
        theParLabel = new JLabel("Parent:");
        theAlsLabel = new JLabel("Alias:");
        myFirst = new JLabel("FirstEvent:");
        myLast = new JLabel("LastEvent:");
        theFirst = new JLabel("01-Jan-2000");
        theLast = new JLabel("01-Jan-2000");
        myWebSite = new JLabel("WebSite:");
        myCustNo = new JLabel("CustomerNo:");
        myUserId = new JLabel("UserId:");
        myPassword = new JLabel("Password:");
        myAccount = new JLabel("Account:");
        myNotes = new JLabel("Notes:");

        /* Build the field set */
        theFieldSet = new FieldSet();

        /* Create the text fields */
        theName = new ItemField(ValueClass.String, Account.FIELD_NAME, theFieldSet);
        theDesc = new ItemField(ValueClass.String, Account.FIELD_DESC, theFieldSet);
        theName.setColumns(Account.NAMELEN);
        theDesc.setColumns(Account.DESCLEN);

        /* Create the password fields */
        theWebSite = new ItemField(ValueClass.CharArray, Account.FIELD_WEBSITE, theFieldSet);
        theCustNo = new ItemField(ValueClass.CharArray, Account.FIELD_CUSTNO, theFieldSet);
        theUserId = new ItemField(ValueClass.CharArray, Account.FIELD_USERID, theFieldSet);
        thePassword = new ItemField(ValueClass.CharArray, Account.FIELD_PASSWORD, theFieldSet);
        theActDetail = new ItemField(ValueClass.CharArray, Account.FIELD_ACCOUNT, theFieldSet);
        theNotes = new ItemField(ValueClass.CharArray, Account.FIELD_NOTES, theFieldSet);
        theWebSite.setColumns(Account.WSITELEN);
        theCustNo.setColumns(Account.CUSTLEN);
        theUserId.setColumns(Account.UIDLEN);
        thePassword.setColumns(Account.PWDLEN);
        theActDetail.setColumns(Account.ACTLEN);
        theNotes.setColumns(Account.WSITELEN);

        /* Create the combo boxes */
        theTypesBox = new JComboBox();
        theParentBox = new JComboBox();
        theAliasBox = new JComboBox();

        /* Add the ComboBoxes to the Field Set */
        theFieldSet.addItemField(new ItemField(theTypesBox, Account.FIELD_TYPE));
        theFieldSet.addItemField(new ItemField(theParentBox, Account.FIELD_PARENT));
        theFieldSet.addItemField(new ItemField(theAliasBox, Account.FIELD_ALIAS));

        /* Dimension the account boxes correctly */
        myDefChars = new char[Account.NAMELEN];
        Arrays.fill(myDefChars, 'X');
        myDefValue = new String(myDefChars);
        theParentBox.setPrototypeDisplayValue(myDefValue);
        theAliasBox.setPrototypeDisplayValue(myDefValue);

        /* Dimension the account type box correctly */
        myDefChars = new char[StaticData.NAMELEN];
        Arrays.fill(myDefChars, 'X');
        myDefValue = new String(myDefChars);
        theTypesBox.setPrototypeDisplayValue(myDefValue);

        /* Create the Maturity Button */
        theMatButton = new DateDayButton();
        theFieldSet.addItemField(new ItemField(theMatButton, Account.FIELD_MATURITY));

        /* Create the buttons */
        theInsButton = new JButton("New");
        theDelButton = new JButton();
        theClsButton = new JButton();
        theUndoButton = new JButton("Undo");

        /* Create the listener */
        AccountListener myListener = new AccountListener();

        /* Add listeners */
        theName.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theDesc.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theWebSite.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theCustNo.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theUserId.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        thePassword.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theActDetail.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theNotes.addPropertyChangeListener(ValueField.PROPERTY_VALUE, myListener);
        theTypesBox.addItemListener(myListener);
        theParentBox.addItemListener(myListener);
        theAliasBox.addItemListener(myListener);
        theMatButton.addPropertyChangeListener(DateDayButton.PROPERTY_DATE, myListener);
        theInsButton.addActionListener(myListener);
        theDelButton.addActionListener(myListener);
        theClsButton.addActionListener(myListener);
        theUndoButton.addActionListener(myListener);

        /* Create the Account Selection panel */
        theSelect = new AccountSelect(theView, this, true);

        /* Create the Save buttons panel */
        theSaveButs = new SaveButtons(this);

        /* Create the debug entry, attach to MaintenanceDebug entry */
        JDataManager myDataMgr = theView.getDataMgr();
        theDataEntry = myDataMgr.new JDataEntry("Account");
        theDataEntry.addAsChildOf(pParent.getDataEntry());

        /* Create the error panel for this view */
        theError = new ErrorPanel(this);

        /* Create the buttons panel */
        theButtons = new JPanel();
        theButtons.setBorder(javax.swing.BorderFactory.createTitledBorder("Actions"));

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(theButtons);
        theButtons.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(theInsButton)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(theUndoButton)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(theDelButton)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(theClsButton).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theInsButton).addComponent(theUndoButton).addComponent(theDelButton)
                .addComponent(theClsButton));

        /* Create the secure panel */
        theSecure = new JPanel();
        theSecure.setBorder(javax.swing.BorderFactory.createTitledBorder("Security Details"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theSecure);
        theSecure.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addGroup(myLayout.createSequentialGroup()
                                                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(myWebSite)
                                                                                        .addComponent(myCustNo)
                                                                                        .addComponent(myUserId)
                                                                                        .addComponent(myNotes))
                                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(theWebSite)
                                                                                        .addComponent(theNotes)
                                                                                        .addGroup(myLayout.createSequentialGroup()
                                                                                                          .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                                                            .addComponent(theCustNo,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE)
                                                                                                                            .addComponent(theUserId,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE))
                                                                                                          .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                          .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                                                                            .addComponent(myAccount,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE)
                                                                                                                            .addComponent(myPassword,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE))
                                                                                                          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                                          .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                                                            .addComponent(theActDetail,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE)
                                                                                                                            .addComponent(thePassword,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE,
                                                                                                                                          GroupLayout.PREFERRED_SIZE))))
                                                                      .addContainerGap()))));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myWebSite)
                                                    .addComponent(theWebSite, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myCustNo)
                                                    .addComponent(theCustNo, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(myAccount)
                                                    .addComponent(theActDetail, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myUserId)
                                                    .addComponent(theUserId, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(myPassword)
                                                    .addComponent(thePassword, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myNotes)
                                                    .addComponent(theNotes, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))));

        /* Create the detail panel */
        theDetail = new JPanel();
        theDetail.setBorder(javax.swing.BorderFactory.createTitledBorder("Account Details"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theDetail);
        theDetail.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addGroup(myLayout.createSequentialGroup()
                                                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(theTypLabel)
                                                                                        .addComponent(myName)
                                                                                        .addComponent(myDesc)
                                                                                        .addComponent(theParLabel))
                                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(theTypesBox,
                                                                                                      GroupLayout.PREFERRED_SIZE,
                                                                                                      GroupLayout.DEFAULT_SIZE,
                                                                                                      GroupLayout.PREFERRED_SIZE)
                                                                                        .addComponent(theDesc,
                                                                                                      GroupLayout.PREFERRED_SIZE,
                                                                                                      GroupLayout.DEFAULT_SIZE,
                                                                                                      GroupLayout.PREFERRED_SIZE)
                                                                                        .addGroup(myLayout.createSequentialGroup()
                                                                                                          .addComponent(theName,
                                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                        GroupLayout.PREFERRED_SIZE)
                                                                                                          .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                          .addComponent(theMatLabel)
                                                                                                          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                                          .addComponent(theMatButton,
                                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                        GroupLayout.PREFERRED_SIZE))
                                                                                        .addGroup(myLayout.createSequentialGroup()
                                                                                                          .addComponent(theParentBox,
                                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                        GroupLayout.PREFERRED_SIZE)
                                                                                                          .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                          .addComponent(theAlsLabel)
                                                                                                          .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                                                          .addComponent(theAliasBox,
                                                                                                                        GroupLayout.PREFERRED_SIZE,
                                                                                                                        GroupLayout.DEFAULT_SIZE,
                                                                                                                        GroupLayout.PREFERRED_SIZE)))))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(theTypLabel)
                                                    .addComponent(theTypesBox, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myName)
                                                    .addComponent(theName, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(theMatLabel)
                                                    .addComponent(theMatButton, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myDesc)
                                                    .addComponent(theDesc, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(theParLabel)
                                                    .addComponent(theParentBox, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(theAlsLabel)
                                                    .addComponent(theAliasBox, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addContainerGap()));

        /* Create the status panel */
        theStatus = new JPanel();
        theStatus.setBorder(javax.swing.BorderFactory.createTitledBorder("Account Status"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theStatus);
        theStatus.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout
                .createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addGroup(myLayout.createSequentialGroup()
                                                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(myFirst)
                                                                                        .addComponent(myLast))
                                                                      .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                                                      .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.TRAILING)
                                                                                        .addComponent(theFirst)
                                                                                        .addComponent(theLast))))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myFirst)
                                                    .addComponent(theFirst, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(myLast)
                                                    .addComponent(theLast, GroupLayout.PREFERRED_SIZE,
                                                                  GroupLayout.DEFAULT_SIZE,
                                                                  GroupLayout.PREFERRED_SIZE))
                                  .addContainerGap()));

        /* Create the panel */
        thePanel = new JPanel();

        /* Create the layout for the panel */
        myLayout = new GroupLayout(thePanel);
        thePanel.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                                    .addComponent(theError)
                                                    .addComponent(theSelect)
                                                    .addComponent(theSaveButs)
                                                    .addGroup(myLayout.createSequentialGroup()
                                                                      .addComponent(theDetail)
                                                                      .addComponent(theStatus))
                                                    .addComponent(theSecure).addComponent(theButtons))
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(theError)
                                  .addComponent(theSelect)
                                  .addContainerGap(10, 30)
                                  .addGroup(myLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                                    .addComponent(theDetail).addComponent(theStatus))
                                  .addContainerGap(10, 30).addComponent(theSecure).addContainerGap(10, 30)
                                  .addComponent(theButtons).addContainerGap(10, 30).addComponent(theSaveButs)
                                  .addContainerGap()));

        /* Set initial display */
        showAccount();
    }

    /* hasUpdates */
    @Override
    public boolean hasUpdates() {
        return ((theAccount != null) && (theAccount.hasChanges()));
    }

    /* hasErrors */
    public boolean hasErrors() {
        return ((theAccount != null) && (theAccount.hasErrors()));
    }

    /* isLocked */
    @Override
    public boolean isLocked() {
        return false;
    }

    /* getEditState */
    @Override
    public EditState getEditState() {
        if (theAccount == null)
            return EditState.CLEAN;
        return theAccount.getEditState();
    }

    /**
     * Lock on error
     * @param isError is there an error (True/False)
     */
    @Override
    public void lockOnError(boolean isError) {
        /* Hide selection panel */
        theSelect.setVisible(!isError);

        /* Lock data areas */
        theDetail.setEnabled(!isError);
        theSecure.setEnabled(!isError);

        /* Lock row/tab buttons area */
        theButtons.setEnabled(!isError);
        theSaveButs.setEnabled(!isError);
    }

    /* performCommand */
    @Override
    public void performCommand(stdCommand pCmd) {
        /* Switch on command */
        switch (pCmd) {
            case OK:
                saveData();
                break;
            case RESETALL:
                resetData();
                break;
        }
        notifyChanges();
    }

    /* Note that changes have been made */
    @Override
    public void notifyChanges() {
        /* Lock down the table buttons and the selection */
        theSaveButs.setLockDown();
        theSelect.setLockDown();

        /* Show the account */
        showAccount();

        /* Adjust visible tabs */
        theParent.setVisibility();
    }

    /**
     * Update Debug view
     */
    public void updateDebug() {
        theDataEntry.setObject(theActView);
    }

    /* resetData */
    public void resetData() {
        theAccount.clearErrors();
        theAccount.resetHistory();
        theAccount.validate();

        /* Recalculate edit state */
        theActView.findEditState();

        /* if this is a new account */
        if (theAccount.getState() == DataState.NEW) {
            /* Delete the new account */
            delNewAccount();
        }

        /* Notify changes */
        notifyChanges();
        updateDebug();
    }

    /* print */
    @Override
    public void printIt() {
    }

    /* validate */
    public void validate() {
        theAccount.clearErrors();
        theAccount.validate();
        updateDebug();
    }

    /* saveData */
    public void saveData() {
        /* Validate the data */
        validate();
        if (!hasErrors()) {
            /* Save details for the account */
            theViewSet.applyChanges();
        }
    }

    /* refreshData */
    public void refreshData() {
        FinanceData myData;
        AccountType myType;

        DataListIterator<AccountType> myTypeIterator;

        /* Access the data */
        myData = theView.getData();

        /* Access type */
        theAcTypList = myData.getAccountTypes();
        theAccounts = myData.getAccounts();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* Refresh the account selection */
        theSelect.refreshData();

        /* If we have types already populated */
        if (typesPopulated) {
            /* Remove the types */
            theTypesBox.removeAllItems();
            typesPopulated = false;
        }

        /* Create an account type iterator */
        myTypeIterator = theAcTypList.listIterator();

        /* Add the AccountType values to the types box */
        while ((myType = myTypeIterator.next()) != null) {
            /* Ignore the type if it is reserved */
            if (myType.isReserved())
                continue;

            /* Ignore the type if it is not enabled */
            if (!myType.getEnabled())
                continue;

            /* Add the item to the list */
            theTypesBox.addItem(myType.getName());
            typesPopulated = true;
        }

        /* Note that we have finished refreshing data */
        refreshingData = false;

        /* refresh the parents */
        refreshParents();

        /* Adjust the account selection */
        if (theAccount != null)
            theSelect.setSelection(theAccount);

        /* Show the account */
        setSelection(theSelect.getSelected());
    }

    /* refreshParents */
    private void refreshParents() {
        Account myAcct;
        AccountType myType;
        DataListIterator<Account> myActIterator;

        /* Note that we are refreshing data */
        refreshingData = true;

        /* If we have parents already populated */
        if (parPopulated) {
            /* Remove the types */
            theParentBox.removeAllItems();
            parPopulated = false;
        }

        /* Create an account iterator */
        myActIterator = theAccounts.listIterator();

        /* Add the Account values to the parents box */
        while ((myAcct = myActIterator.next()) != null) {
            /* Access the type */
            myType = myAcct.getActType();

            /* Ignore the account if it is not an owner */
            if (!myType.isOwner())
                continue;

            /* Ignore the account if it is closed and we are not showing closed */
            if (myAcct.isClosed() && (!doShowClosed))
                continue;

            /* Add the item to the list */
            theParentBox.addItem(myAcct.getName());
            parPopulated = true;
        }

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /* Note that there has been a selection change */
    @Override
    public void notifySelection(Object obj) {
        /* If this is a change from the account selection */
        if (obj == (Object) theSelect) {
            /* If we have changed the show closed option */
            if (theSelect.doShowClosed() != doShowClosed) {
                /* Record details and refresh parents */
                doShowClosed = theSelect.doShowClosed();
                refreshParents();
            }

            /* Set the new account */
            setSelection(theSelect.getSelected());
        }
    }

    /* Select an explicit account and period */
    public void selectAccount(Account pAccount) {
        /* Adjust the account selection */
        theSelect.setSelection(pAccount);

        /* Redraw selection */
        setSelection(theSelect.getSelected());
    }

    /* Set Selection */
    public void setSelection(Account pAccount) {
        /* Reset controls */
        theActView = null;
        theAccount = null;

        /* If we have a selected account */
        if (pAccount != null) {
            /* Create the view of the account */
            theActView = theAccounts.getEditList();

            /* Access the account */
            theAccount = theActView.searchFor(pAccount.getName());
        }

        /* Set ViewList */
        theViewList.setDataList(theActView);

        /* notify changes */
        notifyChanges();
        updateDebug();
    }

    private void showAccount() {
        AccountType myType;
        boolean isClosed;

        /* Access the type from the selection */
        myType = theSelect.getType();

        /* If we have an active account */
        if (theAccount != null) {
            /* Note that we are refreshing data */
            refreshingData = true;

            /* Access details */
            isClosed = theAccount.isClosed();
            myType = theAccount.getActType();

            /* Set the name */
            theName.setValue(theAccount.getName());
            theName.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Set the description */
            theDesc.setValue(theAccount.getDesc());
            theDesc.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Set the WebSite */
            theWebSite.setValue(theAccount.getWebSite());
            theWebSite.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Set the CustNo */
            theCustNo.setValue(theAccount.getCustNo());
            theCustNo.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Set the UserId */
            theUserId.setValue(theAccount.getUserId());
            theUserId.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Set the Password */
            thePassword.setValue(theAccount.getPassword());
            thePassword.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Set the WebSite */
            theActDetail.setValue(theAccount.getAccount());
            theActDetail.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Set the Notes */
            theNotes.setValue(theAccount.getNotes());
            theNotes.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Set the type */
            theTypesBox.setSelectedItem(myType.getName());
            theTypesBox.setVisible(theAccount.isDeletable() && (theAccount.getState() == DataState.NEW));
            theTypLabel.setVisible(theAccount.isDeletable() && (theAccount.getState() == DataState.NEW));

            /* Handle maturity */
            if (myType.isBond()) {
                theMatLabel.setVisible(true);
                theMatButton.setSelectedDateDay(theAccount.getMaturity());
                theMatButton.setVisible(true);
                theMatButton.setEnabled(!isClosed);
            } else {
                theMatButton.setVisible(false);
                theMatLabel.setVisible(false);
            }

            /* Handle parent */
            if (myType.isChild()) {
                if (theAccount.getParent() != null)
                    theParentBox.setSelectedItem(theAccount.getParent().getName());
                else
                    theParentBox.setSelectedItem(null);
                theParentBox.setVisible(true);
                theParLabel.setVisible(true);
            } else {
                theParentBox.setVisible(false);
                theParLabel.setVisible(false);
            }
            theParentBox.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Handle alias */
            if (myType.canAlias() && (!theAccount.isAliasedTo())) {
                /* If we have alias already populated */
                if (alsPopulated) {
                    /* Remove the items */
                    theAliasBox.removeAllItems();
                    alsPopulated = false;
                }

                /* Create an account iterator */
                DataListIterator<Account> myActIterator = theAccounts.listIterator();
                Account myAcct;

                /* Add the Account values to the parents box */
                while ((myAcct = myActIterator.next()) != null) {
                    /* Access the type */
                    myType = myAcct.getActType();

                    /* Ignore the account if it cannot alias */
                    if (!myType.canAlias())
                        continue;

                    /* Ignore the account if it is same type */
                    if (myType.equals(theAccount.getActType()))
                        continue;

                    /* Ignore the account if it is an alias */
                    if (myAcct.getAlias() != null)
                        continue;

                    /* Ignore the account if it is us */
                    if (myAcct.compareTo(theAccount) == 0)
                        continue;

                    /* Add the item to the list */
                    theAliasBox.addItem(myAcct.getName());
                    alsPopulated = true;
                }

                /* Set up the aliases */
                if (theAccount.getAlias() != null)
                    theAliasBox.setSelectedItem(theAccount.getAlias().getName());
                else
                    theAliasBox.setSelectedItem(null);
                theAliasBox.setVisible(true);
                theAlsLabel.setVisible(true);
            } else {
                theAliasBox.setVisible(false);
                theAlsLabel.setVisible(false);
            }
            theAliasBox.setEnabled(!isClosed && !theAccount.isDeleted());

            /* Render all fields in the set */
            theFieldSet.renderSet(theAccount);

            /* Set the First Event */
            theFirst.setText((theAccount.getEarliest() != null) ? JDataObject.formatField(theAccount
                    .getEarliest()) : "N/A");

            /* Set the Last Event */
            theLast.setText((theAccount.getLatest() != null)
                                                            ? JDataObject.formatField(theAccount.getLatest())
                                                            : "N/A");

            /* Set text for close button */
            theClsButton.setText((isClosed) ? "ReOpen" : "Close");

            /* Make sure buttons are visible */
            theDelButton.setVisible(theAccount.isDeletable());
            theDelButton.setText("Delete");
            theClsButton.setVisible(!theAccount.isDeleted());

            /* Enable buttons */
            theInsButton.setEnabled(!theAccount.hasChanges() && (!theAccount.getActType().isReserved()));
            theClsButton.setEnabled((isClosed) || (theAccount.isCloseable()));
            theUndoButton.setEnabled(theAccount.hasChanges());

            /* Note that we are finished refreshing data */
            refreshingData = false;
        }

        /* else no account selected */
        else {
            /* Clear details */
            theName.setValue(null);
            theDesc.setValue(null);
            theFirst.setText("");
            theLast.setText("");
            theWebSite.setValue(null);
            theCustNo.setValue(null);
            theUserId.setValue(null);
            thePassword.setValue(null);
            theActDetail.setValue(null);
            theNotes.setValue(null);

            /* Disable field entry */
            theName.setEnabled(false);
            theDesc.setEnabled(false);
            theWebSite.setEnabled(false);
            theCustNo.setEnabled(false);
            theUserId.setEnabled(false);
            thePassword.setEnabled(false);
            theActDetail.setEnabled(false);
            theNotes.setEnabled(false);
            theClsButton.setVisible(false);
            theUndoButton.setEnabled(false);

            theInsButton.setEnabled(myType != null);
            theDelButton.setVisible(false);
            theDelButton.setText("Recover");

            /* Hide parent and maturity */
            theParLabel.setVisible(false);
            theParentBox.setVisible(false);
            theAlsLabel.setVisible(false);
            theAliasBox.setVisible(false);
            theMatButton.setVisible(false);
            theMatLabel.setVisible(false);
            theTypLabel.setVisible(false);
            theTypesBox.setVisible(false);
        }
    }

    /* Delete New Account */
    private void delNewAccount() {
        /* Set the previously selected account */
        setSelection(theSelect.getSelected());
    }

    /* New Account */
    private void newAccount() {
        /* Create a account View for an empty account */
        theActView = theAccounts.getEditList(theSelect.getType());

        /* Access the account */
        theAccount = theActView.getAccount();

        /* Set ViewList */
        theViewList.setDataList(theActView);

        /* Notify changes */
        notifyChanges();
        updateDebug();
    }

    /* Undo changes */
    private void undoChanges() {
        /* If the account has changes */
        if (theAccount.hasHistory()) {
            /* Pop last value */
            theAccount.popHistory();

            /* Re-validate the item */
            theAccount.clearErrors();
            theAccount.validate();

            /* If the item is now clean */
            if (!theAccount.hasHistory()) {
                /* Set the new status */
                theAccount.setState(DataState.CLEAN);
            }

            /* Notify changes */
            notifyChanges();
            updateDebug();
        }

        /* else if this is a new account */
        else if (theAccount.getState() == DataState.NEW) {
            /* Delete the new account */
            delNewAccount();
        }
    }

    /**
     * AccountListener class
     */
    private class AccountListener implements ActionListener, ItemListener, PropertyChangeListener {
        @Override
        public void itemStateChanged(ItemEvent evt) {
            String myName;
            Object o = evt.getSource();

            /* Ignore selection if refreshing data */
            if (refreshingData)
                return;

            /* Push history */
            theAccount.pushHistory();

            /* Protect against exceptions */
            try {
                /* If this event relates to the period box */
                if (o == theTypesBox) {
                    if (evt.getStateChange() == ItemEvent.SELECTED) {
                        /* Store the appropriate value */
                        myName = (String) evt.getItem();
                        theAccount.setActType(theAcTypList.searchFor(myName));

                        /* If the account is now a bond */
                        if (theAccount.isBond()) {
                            /* If it doesn't have a maturity */
                            if (theAccount.getMaturity() == null) {
                                /* Create a default maturity */
                                theAccount.setMaturity(new DateDay());
                                theAccount.getMaturity().adjustYear(1);
                            }
                        }

                        /* Else set maturity to null for non-bonds */
                        else
                            theAccount.setMaturity(null);

                        /* Set parent to null for non-child accounts */
                        if (!theAccount.isChild())
                            theAccount.setParent(null);
                    }
                }

                /* If this event relates to the parent box */
                else if (o == theParentBox) {
                    if (evt.getStateChange() == ItemEvent.SELECTED) {
                        /* Store the appropriate value */
                        myName = (String) evt.getItem();
                        theAccount.setParent(theAccounts.searchFor(myName));
                    }
                }

                /* If this event relates to the alias box */
                else if (o == theAliasBox) {
                    if (evt.getStateChange() == ItemEvent.SELECTED) {
                        /* Store the appropriate value */
                        myName = (String) evt.getItem();
                        theAccount.setAlias(theAccounts.searchFor(myName));
                    }
                }
            }

            /* Handle Exceptions */
            catch (Throwable e) {
                /* Reset values */
                theAccount.popHistory();
                theAccount.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to update field", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* Check for changes */
            if (theAccount.checkForHistory()) {
                /* Note that the item has changed */
                theAccount.setState(DataState.CHANGED);

                /* validate it */
                theAccount.clearErrors();
                theAccount.validate();

                /* Note that changes have occurred */
                notifyChanges();
                updateDebug();
            }
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the new button */
            if (o == theInsButton) {
                /* Create the new account */
                newAccount();
                return;
            }

            /* If this event relates to the del button */
            else if (o == theDelButton) {
                /* else if this is a new account */
                if (theAccount.getState() == DataState.NEW) {
                    /* Delete the new account */
                    delNewAccount();
                }

                /* Else we should just delete/recover the account */
                else {
                    /* Set the appropriate state */
                    theAccount.setState(theAccount.isDeleted() ? DataState.RECOVERED : DataState.DELETED);

                    /* Notify changes */
                    notifyChanges();
                    updateDebug();
                }
                return;
            }

            /* If this event relates to the undo button */
            else if (o == theUndoButton) {
                /* Undo the changes */
                undoChanges();
                return;
            }

            /* Push history */
            theAccount.pushHistory();

            /* Protect against exceptions */
            try {
                /* If this event relates to the close button */
                if (o == theClsButton) {
                    /* Re-open or close the account as required */
                    if (theAccount.isClosed())
                        theAccount.reOpenAccount();
                    else
                        theAccount.closeAccount();
                }
            }

            /* Handle Exceptions */
            catch (Throwable e) {
                /* Reset values */
                theAccount.popHistory();
                theAccount.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to update field", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* Check for changes */
            if (theAccount.checkForHistory()) {
                /* Note that the item has changed */
                theAccount.setState(DataState.CHANGED);

                /* validate it */
                theAccount.clearErrors();
                theAccount.validate();

                /* Note that changes have occurred */
                notifyChanges();
                updateDebug();
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            Object o = evt.getSource();

            /* Push history */
            theAccount.pushHistory();

            /* Protect against exceptions */
            try {
                /* If this is the name */
                if (o == theName) {
                    /* Update the Account */
                    String myValue = (String) theName.getValue();
                    theAccount.setAccountName(myValue);
                }

                /* If this is the description */
                else if (o == theDesc) {
                    /* Update the Account */
                    String myValue = (String) theDesc.getValue();
                    theAccount.setDescription(myValue);
                }

                /* If this is our WebSite */
                else if (o == theWebSite) {
                    /* Update the Account */
                    char[] myValue = (char[]) theWebSite.getValue();
                    theAccount.setWebSite(myValue);
                }

                /* If this is our CustNo */
                else if (o == theCustNo) {
                    /* Update the Account */
                    char[] myValue = (char[]) theCustNo.getValue();
                    theAccount.setCustNo(myValue);
                }

                /* If this is our UserId */
                else if (o == theUserId) {
                    /* Update the Account */
                    char[] myValue = (char[]) theUserId.getValue();
                    theAccount.setUserId(myValue);
                }

                /* If this is our Password */
                else if (o == thePassword) {
                    /* Update the Account */
                    char[] myValue = (char[]) thePassword.getValue();
                    theAccount.setPassword(myValue);
                }

                /* If this is our Account Detail */
                else if (o == theActDetail) {
                    /* Update the Account */
                    char[] myValue = (char[]) theActDetail.getValue();
                    theAccount.setAccount(myValue);
                }

                /* If this is our Notes */
                else if (o == theNotes) {
                    /* Update the Account */
                    char[] myValue = (char[]) theNotes.getValue();
                    theAccount.setNotes(myValue);
                }

                /* If this event relates to the maturity box */
                else if (o == theMatButton) {
                    /* Access the value */
                    DateDay myDate = new DateDay(theMatButton.getSelectedDate());
                    theAccount.setMaturity(myDate);
                }
            }

            /* Handle Exceptions */
            catch (Throwable e) {
                /* Reset values */
                theAccount.popHistory();
                theAccount.pushHistory();

                /* Build the error */
                JDataException myError = new JDataException(ExceptionClass.DATA, "Failed to update field", e);

                /* Show the error */
                theError.setError(myError);
            }

            /* Check for changes */
            if (theAccount.checkForHistory()) {
                /* Note that the item has changed */
                theAccount.setState(DataState.CHANGED);

                /* validate it */
                theAccount.clearErrors();
                theAccount.validate();

                /* Note that changes have occurred */
                notifyChanges();
                updateDebug();
            }
        }
    }
}
