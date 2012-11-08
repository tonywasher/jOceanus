/*******************************************************************************
 * jDataModels: Data models
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
package net.sourceforge.jOceanus.jDataModels.ui;

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
import net.sourceforge.jOceanus.jDataModels.data.StaticData.StaticList;
import net.sourceforge.jOceanus.jDataModels.views.DataControl;
import net.sourceforge.jOceanus.jDataModels.views.UpdateSet;
import net.sourceforge.jOceanus.jEventManager.JEventPanel;

/**
 * Top level panel for static data.
 * @author Tony Washer
 */
public class MaintStatic
        extends JEventPanel {
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
     * The data control.
     */
    private final DataControl<?> theControl;

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
     * The selection listener.
     */
    private final StaticListener theListener = new StaticListener();

    /**
     * Obtain the updateList.
     * @return the viewSet
     */
    protected UpdateSet getUpdateSet() {
        return theUpdateSet;
    }

    /**
     * Constructor.
     * @param pControl the data control
     */
    public MaintStatic(final DataControl<?> pControl) {
        /* Store control */
        theControl = pControl;

        /* Build the Update set */
        theUpdateSet = new UpdateSet(pControl);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theControl.getDataMgr();
        JDataEntry mySection = theControl.getDataEntry(DataControl.DATA_MAINT);
        theDataEntry = myDataMgr.new JDataEntry(StaticData.class.getSimpleName());
        theDataEntry.addAsChildOf(mySection);
        theDataEntry.setObject(theUpdateSet);

        /* Create the error panel */
        theError = new ErrorPanel(myDataMgr, theDataEntry);

        /* Create the save buttons panel */
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the selection box */
        theSelectBox = new JComboBox<String>();

        /* Add the listener for item changes */
        theSelectBox.addItemListener(theListener);
        theSelectBox.setMaximumSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        theError.addChangeListener(theListener);
        theSaveButtons.addActionListener(theListener);

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

        /* Create the panel map */
        theMap = new HashMap<String, MaintStaticData<?, ?>>();

        /* Now define the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(mySelect);
        add(theError);
        add(theCardPanel);
        add(theSaveButtons);
    }

    /**
     * Add static panel.
     * @param pListName the name of the list
     * @param pListClass the class of the list
     * @param pItemClass the class of the items
     * @param <L> the list type
     * @param <T> the data type
     */
    public <L extends StaticList<T, ?>, T extends StaticData<T, ?>> void addStatic(final String pListName,
                                                                                   final Class<L> pListClass,
                                                                                   final Class<T> pItemClass) {
        /* Create the new panel */
        MaintStaticData<L, T> myPanel = new MaintStaticData<L, T>(theControl, theUpdateSet, theError, pListClass, pItemClass);

        /* Add the name to the selectionBox */
        theSelectBox.addItem(pListName);
        theSelectBox.setSelectedIndex(0);

        /* Add the listener for the panel */
        myPanel.addChangeListener(theListener);

        /* Add to the card panels */
        theCardPanel.add(myPanel.getPanel(), pListName);

        /* Add to the Map */
        theMap.put(pListName, myPanel);
    }

    /**
     * Determine Focus.
     */
    public void determineFocus() {
        /* Request the focus */
        if (theActive != null) {
            theActive.determineFocus(theDataEntry);
        }
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
        /* Loop through the map */
        for (MaintStaticData<?, ?> myPanel : theMap.values()) {
            /* Refresh the panel */
            myPanel.refreshData();
        }

        /* Touch the updateSet */
        theDataEntry.setObject(theUpdateSet);
    }

    /**
     * Cancel Editing of underlying tables.
     */
    private void cancelEditing() {
        /* Loop through the map */
        for (MaintStaticData<?, ?> myPanel : theMap.values()) {
            /* Refresh the underlying children */
            myPanel.cancelEditing();
        }
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
    private final class StaticListener
            implements ItemListener, ChangeListener, ActionListener {
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
