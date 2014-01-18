/*******************************************************************************
 * jPrometheus: Application Framework
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
package net.sourceforge.joceanus.jprometheus.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jdatamanager.Difference;
import net.sourceforge.joceanus.jdatamanager.JDataManager;
import net.sourceforge.joceanus.jdatamanager.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jprometheus.data.StaticData;
import net.sourceforge.joceanus.jprometheus.data.StaticData.StaticList;
import net.sourceforge.joceanus.jprometheus.views.DataControl;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JEnableWrapper.JEnablePanel;
import net.sourceforge.joceanus.jtethys.event.JEventPanel;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollPopupMenu;

/**
 * Top level panel for static data.
 * @author Tony Washer
 */
public class StaticDataPanel
        extends JEventPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1089967527250331711L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 5;

    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(StaticDataPanel.class.getName());

    /**
     * Text for Selection Title.
     */
    private static final String NLS_SELECT = NLS_BUNDLE.getString("SelectionTitle");

    /**
     * Text for Data.
     */
    private static final String NLS_DATA = NLS_BUNDLE.getString("StaticData");

    /**
     * The data control.
     */
    private final transient DataControl<?> theControl;

    /**
     * The card panel.
     */
    private final JEnablePanel theCardPanel;

    /**
     * The card layout.
     */
    private final CardLayout theLayout;

    /**
     * The selection button.
     */
    private final JButton theSelectButton;

    /**
     * The Panel map.
     */
    private final Map<String, StaticDataTable<?, ?>> theMap;

    /**
     * The Active panel.
     */
    private StaticDataTable<?, ?> theActive;

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
    private final transient StaticListener theListener = new StaticListener();

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
    public StaticDataPanel(final DataControl<?> pControl) {
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

        /* Create selection button and label */
        JLabel myLabel = new JLabel(NLS_DATA);
        theSelectButton = new JButton(ArrowIcon.DOWN);
        theSelectButton.setVerticalTextPosition(AbstractButton.CENTER);
        theSelectButton.setHorizontalTextPosition(AbstractButton.LEFT);

        /* Add the listener for item changes */
        theSelectButton.addActionListener(theListener);
        theError.addChangeListener(theListener);
        theSaveButtons.addActionListener(theListener);

        /* Create the selection panel */
        JPanel mySelect = new JPanel();
        mySelect.setBorder(BorderFactory.createTitledBorder(NLS_SELECT));

        /* Create the layout for the selection panel */
        mySelect.setLayout(new BoxLayout(mySelect, BoxLayout.X_AXIS));
        mySelect.add(myLabel);
        mySelect.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        mySelect.add(theSelectButton);
        mySelect.add(Box.createHorizontalGlue());

        /* Create the card panel */
        theCardPanel = new JEnablePanel();
        theLayout = new CardLayout();
        theCardPanel.setLayout(theLayout);

        /* Create the panel map */
        theMap = new LinkedHashMap<String, StaticDataTable<?, ?>>();

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
        StaticDataTable<L, T> myPanel = new StaticDataTable<L, T>(theControl, theUpdateSet, theError, pListClass, pItemClass);

        /* Add the listener for the panel */
        myPanel.addChangeListener(theListener);

        /* Add to the card panels */
        theCardPanel.add(myPanel.getPanel(), pListName);

        /* Add to the Map */
        theMap.put(pListName, myPanel);

        /* If we do not have an active panel */
        if (theActive == null) {
            /* select this panel */
            setSelection(pListName);
        }
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

        /* Show selection text */
        theSelectButton.setText(pName);
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
     * @throws JOceanusException on error
     */
    public void refreshData() throws JOceanusException {
        /* Loop through the map */
        for (StaticDataTable<?, ?> myPanel : theMap.values()) {
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
        for (StaticDataTable<?, ?> myPanel : theMap.values()) {
            /* Refresh the underlying children */
            myPanel.cancelEditing();
        }
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theSelectButton.setEnabled(bEnabled);
        theError.setEnabled(bEnabled);
        theCardPanel.setEnabled(bEnabled);
        theSaveButtons.setEnabled(bEnabled);
    }

    /**
     * Set Visibility.
     */
    protected void setVisibility() {
        /* Lock down Selection if required */
        theSelectButton.setEnabled(!hasUpdates());

        /* Update the save buttons */
        theSaveButtons.setEnabled(true);

        /* Alert listeners that there has been a change */
        fireStateChanged();
    }

    /**
     * Listener class.
     */
    private final class StaticListener
            implements ChangeListener, ActionListener {
        /**
         * Show StaticData menu.
         */
        private void showDataMenu() {
            /* Create a new popUp menu */
            JScrollPopupMenu myPopUp = new JScrollPopupMenu();

            /* Loop through the panels */
            Iterator<Map.Entry<String, StaticDataTable<?, ?>>> myIterator = theMap.entrySet().iterator();
            while (myIterator.hasNext()) {
                Map.Entry<String, StaticDataTable<?, ?>> myEntry = myIterator.next();

                /* Create a new JMenuItem and add it to the popUp */
                DataAction myAction = new DataAction(myEntry.getKey(), myEntry.getValue());
                JMenuItem myItem = new JMenuItem(myAction);
                myPopUp.addMenuItem(myItem);
            }

            /* Show the Data menu in the correct place */
            Rectangle myLoc = theSelectButton.getBounds();
            myPopUp.show(theSelectButton, 0, myLoc.height);
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
                theSelectButton.setVisible(!isError);

                /* Lock scroll-able area */
                theCardPanel.setEnabled(!isError);

                /* Lock Save Buttons */
                theSaveButtons.setEnabled(!isError);

                /* if this is one of the static data panels */
            } else if (o instanceof StaticDataTable) {
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

            /* if this is the select button reporting */
            if (theSelectButton.equals(o)) {
                /* Cancel Editing */
                cancelEditing();

                /* Show data menu */
                showDataMenu();
            }
        }
    }

    /**
     * Data action class.
     */
    private final class DataAction
            extends AbstractAction {
        /**
         * Serial Id.
         */
        private static final long serialVersionUID = 7998735079273862989L;

        /**
         * Data Name.
         */
        private final String theName;

        /**
         * Data Table.
         */
        private final StaticDataTable<?, ?> theTable;

        /**
         * Constructor.
         * @param pName the panel name
         * @param pTable the table
         */
        private DataAction(final String pName,
                           final StaticDataTable<?, ?> pTable) {
            super(pName);
            theName = pName;
            theTable = pTable;
        }

        @Override
        public void actionPerformed(final ActionEvent e) {
            /* If the panel has changed */
            if (!Difference.isEqual(theActive, theTable)) {
                /* Move correct card to front */
                setSelection(theName);
            }
        }
    }
}
