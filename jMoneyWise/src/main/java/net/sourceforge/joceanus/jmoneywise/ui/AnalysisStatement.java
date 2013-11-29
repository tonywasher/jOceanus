/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.ui;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;

import net.sourceforge.joceanus.jdatamanager.JDataException;
import net.sourceforge.joceanus.jdatamanager.JDataManager;
import net.sourceforge.joceanus.jdatamanager.JDataManager.JDataEntry;
import net.sourceforge.joceanus.jdatamodels.ui.ErrorPanel;
import net.sourceforge.joceanus.jdatamodels.ui.SaveButtons;
import net.sourceforge.joceanus.jdatamodels.views.DataControl;
import net.sourceforge.joceanus.jdatamodels.views.UpdateEntry;
import net.sourceforge.joceanus.jdatamodels.views.UpdateSet;
import net.sourceforge.joceanus.jfieldset.JFieldManager;
import net.sourceforge.joceanus.jmoneywise.data.Event;
import net.sourceforge.joceanus.jmoneywise.ui.controls.AnalysisSelect;
import net.sourceforge.joceanus.jmoneywise.views.View;

/**
 * Analysis Statement.
 */
public class AnalysisStatement
        extends JPanel {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -8054491530459145911L;

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The updateSet.
     */
    private final transient UpdateSet theUpdateSet;

    /**
     * The field manager.
     */
    private final transient JFieldManager theFieldMgr;

    /**
     * The update entry.
     */
    private final transient UpdateEntry<Event> theUpdateEntry;

    /**
     * The data entry.
     */
    private final transient JDataEntry theDataAnalysis;

    /**
     * Analysis Selection panel.
     */
    private final AnalysisSelect theSelect;

    /**
     * The save buttons.
     */
    private final SaveButtons theSaveButtons;

    /**
     * The error panel.
     */
    private final ErrorPanel theError;

    /**
     * The scroll pane.
     */
    private final JScrollPane theScroll;

    /**
     * The table model.
     */
    private final AnalysisTableModel theModel;

    /**
     * Constructor.
     * @param pView the data view
     */
    public AnalysisStatement(final View pView) {
        /* Record the passed details */
        theView = pView;
        theFieldMgr = theView.getFieldMgr();

        /* Build the Update set and entry */
        theUpdateSet = new UpdateSet(theView);
        theUpdateEntry = theUpdateSet.registerClass(Event.class);

        /* Create the top level debug entry for this view */
        JDataManager myDataMgr = theView.getDataMgr();
        JDataEntry mySection = theView.getDataEntry(DataControl.DATA_EDIT);
        theDataAnalysis = myDataMgr.new JDataEntry(AnalysisStatement.class.getSimpleName());
        theDataAnalysis.addAsChildOf(mySection);
        theDataAnalysis.setObject(theUpdateSet);

        /* Create the Analysis Selection */
        theSelect = new AnalysisSelect();

        /* Create the save buttons */
        theSaveButtons = new SaveButtons(theUpdateSet);

        /* Create the error panel for this view */
        theError = new ErrorPanel(myDataMgr, theDataAnalysis);

        /* Create the table model */
        theModel = new AnalysisTableModel();

        /* Create the Table and ScrollPane */
        JTable myTable = new JTable(theModel);
        theScroll = new JScrollPane(myTable);

        /* Create the layout for the panel */
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        add(theSelect);
        add(theError);
        add(theScroll);
        add(theSaveButtons);

        /* Create listener */
        AnalysisListener myListener = new AnalysisListener();
        theView.addChangeListener(myListener);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        /* Pass on to important elements */
        theSelect.setEnabled(bEnabled);
        theError.setEnabled(bEnabled);
        theScroll.setEnabled(bEnabled);
        theSaveButtons.setEnabled(bEnabled);
    }

    /**
     * Determine Focus.
     */
    protected void determineFocus() {
        /* Request the focus */
        requestFocusInWindow();

        /* Set the required focus */
        theDataAnalysis.setFocus();
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Update the selection */
        theSelect.refreshData(theView);
    }

    /**
     * Set error details.
     * @param pError the error
     */
    protected void setError(final JDataException pError) {
        theError.addError(pError);
    }

    /**
     * JTable Data Model.
     */
    private final class AnalysisTableModel
            extends AbstractTableModel {
        /**
         * The Serial Id.
         */
        private static final long serialVersionUID = -7384250393275180461L;

        @Override
        public int getColumnCount() {
            return 0;
        }

        @Override
        public int getRowCount() {
            return 0;
        }

        @Override
        public Object getValueAt(final int pRow,
                                 final int pCol) {
            return null;
        }
    }

    /**
     * Listener class.
     */
    private final class AnalysisListener
            implements ChangeListener {

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            refreshData();
        }
    }
}
