/*******************************************************************************
 * JFinanceApp: Finance Application
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

/**
 * Preference maintenance panel.
 * @author Tony Washer
 */
public class MaintProperties implements StdPanel {
    /**
     * The panel width.
     */
    private static final int PANEL_WIDTH = 300;

    /**
     * The panel height.
     */
    private static final int PANEL_HEIGHT = 25;

    /**
     * The parent.
     */
    private final MaintenanceTab theParent;

    /**
     * The parent.
     */
    private final JPanel thePanel;

    /**
     * The selection panel.
     */
    // private final JPanel theSelection;

    /**
     * The butoons panel.
     */
    // private final JPanel theButtons;

    /**
     * The ok button.
     */
    private final JButton theOKButton;

    /**
     * The reset button.
     */
    private final JButton theResetButton;

    /**
     * The selection panel.
     */
    private final JComboBox theSelect;

    /**
     * The properties panel.
     */
    private final JPanel theProperties;

    /**
     * The layout.
     */
    private final CardLayout theLayout;

    /**
     * The active set.
     */
    private PreferenceSetPanel theActive = null;

    /**
     * Obtain panel.
     * @return the panel
     */
    public JPanel getPanel() {
        return thePanel;
    }

    /**
     * Constructor.
     * @param pParent the parent
     */
    public MaintProperties(final MaintenanceTab pParent) {
        /* Store parent */
        theParent = pParent;

        /* Create the buttons */
        theOKButton = new JButton("OK");
        theResetButton = new JButton("Reset");

        /* Create the buttons panel */
        JPanel myButtons = new JPanel();
        myButtons.setBorder(javax.swing.BorderFactory.createTitledBorder("Save Options"));

        /* Create the layout for the panel */
        GroupLayout myLayout = new GroupLayout(myButtons);
        myButtons.setLayout(myLayout);

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
        theSelect.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));

        /* Create the selection panel */
        JPanel mySelection = new JPanel();
        mySelection.setBorder(javax.swing.BorderFactory.createTitledBorder("Selection"));

        /* Create the layout for the panel */
        myLayout = new GroupLayout(mySelection);
        mySelection.setLayout(myLayout);

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
        thePanel.add(mySelection);
        thePanel.add(myScroll);
        thePanel.add(Box.createVerticalGlue());
        thePanel.add(myButtons);

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
     * RegisterSet.
     * @param pSet the set to register
     */
    private void registerSet(final PreferenceSet pSet) {
        /* Create the underlying panel */
        PreferenceSetPanel myPanel = new PreferenceSetPanel(this, pSet);

        /* Add the panel */
        theProperties.add(myPanel, myPanel.toString());

        /* Add name to the ComboBox */
        theSelect.addItem(myPanel);
    }

    @Override
    public boolean hasUpdates() {
        return ((theActive != null) && (theActive.hasChanges()));
    }

    @Override
    public void performCommand(final stdCommand pCmd) {
        /* Switch on command */
        switch (pCmd) {
            case OK:
                try {
                    theActive.storeChanges();
                } catch (Exception e) {
                    e = null;
                }
                break;
            case RESETALL:
                theActive.resetChanges();
                break;
            default:
                break;
        }

        /* Notify Status changes */
        notifyChanges();
    }

    @Override
    public void notifyChanges() {
        /* Set the visibility */
        setVisibility();

        /* Adjust visible tabs */
        theParent.setVisibility();
    }

    /**
     * Set the visibility.
     */
    public void setVisibility() {
        /* Enable selection */
        theSelect.setEnabled((theActive != null) && !theActive.hasChanges());

        /* Enable the buttons */
        theOKButton.setEnabled((theActive != null) && theActive.hasChanges());
        theResetButton.setEnabled((theActive != null) && theActive.hasChanges());
    }

    /**
     * PropertyListener class.
     */
    private final class PropertyListener implements ActionListener, ItemListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the OK button */
            if (theOKButton.equals(o)) {
                /* Perform the command */
                performCommand(stdCommand.OK);

                /* If this event relates to the reset button */
            } else if (theResetButton.equals(o)) {
                /* Perform the command */
                performCommand(stdCommand.RESETALL);
            }
        }

        @Override
        public void itemStateChanged(final ItemEvent evt) {
            /* If this event relates to the selected box */
            if (theSelect.equals(evt.getSource())) {
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
     * PropertySetListener class.
     */
    private final class PropertySetListener implements ActionListener {
        @Override
        public void actionPerformed(final ActionEvent evt) {
            /* Source is the property set that has been added */
            PreferenceSet mySet = (PreferenceSet) evt.getSource();

            /* Register the set */
            registerSet(mySet);

            /* Note that the panel should be re-displayed */
            theProperties.invalidate();
        }
    }

    @Override
    public void notifySelection(final Object o) {
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
    public void lockOnError(final boolean isError) {
    }
}
