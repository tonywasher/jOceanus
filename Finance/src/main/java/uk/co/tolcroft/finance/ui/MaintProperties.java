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

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.LayoutStyle;

import net.sourceforge.JDataManager.JDataManager;
import net.sourceforge.JDataManager.JDataManager.JDataEntry;
import uk.co.tolcroft.models.data.EditState;
import uk.co.tolcroft.models.data.PreferenceSet;
import uk.co.tolcroft.models.data.PreferenceSet.PreferenceManager;
import uk.co.tolcroft.models.ui.PreferenceSetPanel;
import uk.co.tolcroft.models.ui.StdInterfaces.StdPanel;
import uk.co.tolcroft.models.ui.StdInterfaces.stdCommand;

public class MaintProperties implements StdPanel {
    /* Properties */
    private MaintenanceTab theParent = null;
    private JPanel thePanel = null;
    private JPanel theSelection = null;
    private JPanel theButtons = null;
    private JButton theOKButton = null;
    private JButton theResetButton = null;
    private JComboBox theSelect = null;
    private JPanel theProperties = null;
    private CardLayout theLayout = null;
    private PreferenceSetPanel theActive = null;

    /* Access methods */
    public JPanel getPanel() {
        return thePanel;
    }

    /* Constructor */
    public MaintProperties(MaintenanceTab pParent) {
        /* Store parent */
        theParent = pParent;

        /* Create the buttons */
        theOKButton = new JButton("OK");
        theResetButton = new JButton("Reset");

        /* Create the buttons panel */
        theButtons = new JPanel();
        theButtons.setBorder(javax.swing.BorderFactory.createTitledBorder("Save Options"));

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(theButtons);
        theButtons.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addComponent(theOKButton)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(theResetButton).addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(theOKButton).addComponent(theResetButton));

        /* Create selection box and label */
        JLabel myLabel = new JLabel("PropertySet:");
        theSelect = new JComboBox();
        theSelect.setMaximumSize(new Dimension(300, 25));

        /* Create the selection panel */
        theSelection = new JPanel();
        theSelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(theSelection);
        theSelection.setLayout(myLayout);

        /* Set the layout */
        myLayout.setHorizontalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(myLayout.createSequentialGroup()
                                  .addContainerGap()
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addComponent(myLabel)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                                   GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE)
                                  .addComponent(theSelect)
                                  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                                                   GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                  .addContainerGap()));
        myLayout.setVerticalGroup(myLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(myLabel).addComponent(theSelect));

        /* Create the properties panel */
        theProperties = new JPanel();
        theLayout = new CardLayout();
        theProperties.setLayout(theLayout);

        /* Loop through the existing property sets */
        for (PreferenceSet mySet : PreferenceManager.getPreferenceSets()) {
            /* Register the Set */
            registerSet(mySet);
        }

        /* Add a listener for the addition of subsequent propertySets */
        PropertySetListener mySetListener = new PropertySetListener();
        PreferenceManager.addActionListener(mySetListener);

        /* Create a new Scroll Pane and add this table to it */
        JScrollPane myScroll = new JScrollPane();
        myScroll.setViewportView(theProperties);

        /* Now define the panel */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.Y_AXIS));
        thePanel.add(theSelection);
        thePanel.add(myScroll);
        thePanel.add(Box.createVerticalGlue());
        thePanel.add(theButtons);

        /* Determine the active items */
        theActive = (PreferenceSetPanel) theSelect.getSelectedItem();
        setVisibility();

        /* Add Listeners */
        PropertyListener myListener = new PropertyListener();
        theOKButton.addActionListener(myListener);
        theResetButton.addActionListener(myListener);
        theSelect.addItemListener(myListener);
    }

    /**
     * RegisterSet
     * @param pSet the set to register
     */
    private void registerSet(PreferenceSet pSet) {
        /* Create the underlying panel */
        PreferenceSetPanel myPanel = new PreferenceSetPanel(this, pSet);

        /* Add the panel */
        theProperties.add(myPanel, myPanel.toString());

        /* Add name to the ComboBox */
        theSelect.addItem(myPanel);
    }

    /* hasUpdates */
    @Override
    public boolean hasUpdates() {
        return ((theActive != null) && (theActive.hasChanges()));
    }

    /* performCommand */
    @Override
    public void performCommand(stdCommand pCmd) {
        /* Switch on command */
        switch (pCmd) {
            case OK:
                try {
                    theActive.storeChanges();
                } catch (Throwable e) {
                }
                break;
            case RESETALL:
                theActive.resetChanges();
                break;
        }

        /* Notify Status changes */
        notifyChanges();
    }

    /* Note that changes have been made */
    @Override
    public void notifyChanges() {
        /* Set the visibility */
        setVisibility();

        /* Adjust visible tabs */
        theParent.setVisibility();
    }

    /* Set Visibility */
    public void setVisibility() {
        /* Enable selection */
        theSelect.setEnabled((theActive != null) && !theActive.hasChanges());

        /* Enable the buttons */
        theOKButton.setEnabled((theActive != null) && theActive.hasChanges());
        theResetButton.setEnabled((theActive != null) && theActive.hasChanges());
    }

    /**
     * PropertyListener class
     */
    private class PropertyListener implements ActionListener, ItemListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the OK button */
            if (o == theOKButton) {
                /* Perform the command */
                performCommand(stdCommand.OK);
            }

            /* If this event relates to the reset button */
            else if (o == theResetButton) {
                /* Perform the command */
                performCommand(stdCommand.RESETALL);
            }
        }

        @Override
        public void itemStateChanged(ItemEvent evt) {
            /* If this event relates to the selected box */
            if (evt.getSource() == theSelect) {
                /* Set the Active component */
                theActive = (PreferenceSetPanel) evt.getItem();

                /* Show the requested set */
                theLayout.show(theProperties, theActive.toString());

                /* Notify changes */
                notifyChanges();
            }
        }
    }

    /**
     * PropertyListener class
     */
    private class PropertySetListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent evt) {
            /* Source is the property set that has been added */
            PreferenceSet mySet = (PreferenceSet) evt.getSource();

            /* Register the set */
            registerSet(mySet);

            /* Note that the panel should be re-displayed */
            theProperties.invalidate();
        }
    }

    @Override
    public void notifySelection(Object o) {
    }

    @Override
    public void printIt() {
    }

    @Override
    public boolean isLocked() {
        return false;
    }

    @Override
    public EditState getEditState() {
        return null;
    }

    @Override
    public JDataManager getDataManager() {
        return null;
    }

    @Override
    public JDataEntry getDataEntry() {
        return null;
    }

    @Override
    public void lockOnError(boolean isError) {
    }
}
