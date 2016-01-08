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
package net.sourceforge.joceanus.jmoneywise.ui.controls.swing;

import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.date.swing.TethysSwingDateButton;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.swing.TethysSwingArrowIcon;

/**
 * SpotRates selection panel.
 * @author Tony Washer
 */
public class SpotRatesSelect
        extends JPanel
        implements TethysEventProvider<PrometheusDataEvent> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -1576166966674913077L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Text for Currency Prompt.
     */
    private static final String NLS_CURRENCY = MoneyWiseUIResource.SPOTRATE_PROMPT_CURR.getValue();

    /**
     * Text for Date Label.
     */
    private static final String NLS_DATE = MoneyWiseUIResource.SPOTEVENT_DATE.getValue();

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = MoneyWiseUIResource.SPOTRATE_TITLE.getValue();

    /**
     * Text for Next toolTip.
     */
    private static final String NLS_NEXTTIP = MoneyWiseUIResource.SPOTRATE_NEXT.getValue();

    /**
     * Text for Previous toolTip.
     */
    private static final String NLS_PREVTIP = MoneyWiseUIResource.SPOTRATE_PREV.getValue();

    /**
     * The Event Manager.
     */
    private final transient TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The currency label.
     */
    private final JLabel theCurrLabel;

    /**
     * The date button.
     */
    private final TethysSwingDateButton theDateButton;

    /**
     * The next button.
     */
    private final JButton theNext;

    /**
     * The previous button.
     */
    private final JButton thePrev;

    /**
     * The download button.
     */
    private final JButton theDownloadButton;

    /**
     * The current state.
     */
    private transient SpotRatesState theState = null;

    /**
     * The saved state.
     */
    private transient SpotRatesState theSavePoint = null;

    /**
     * Constructor.
     * @param pView the data view
     */
    public SpotRatesSelect(final View pView) {
        /* Store table and view details */
        theView = pView;

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create Labels */
        JLabel myCurr = new JLabel(NLS_CURRENCY);
        JLabel myDate = new JLabel(NLS_DATE);

        /* Create the DateButton */
        theDateButton = new TethysSwingDateButton();

        /* Create the Download Button */
        theDownloadButton = MoneyWiseIcons.getDownloadButton();

        /* Create the Currency indication */
        theCurrLabel = new JLabel();

        /* Create the Buttons */
        theNext = new JButton(TethysSwingArrowIcon.RIGHT);
        thePrev = new JButton(TethysSwingArrowIcon.LEFT);
        theNext.setToolTipText(NLS_NEXTTIP);
        thePrev.setToolTipText(NLS_PREVTIP);

        /* Create initial state */
        theState = new SpotRatesState();

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myCurr);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theCurrLabel);
        add(Box.createHorizontalGlue());
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myDate);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePrev);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theDateButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theNext);
        add(Box.createHorizontalGlue());
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theDownloadButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Initialise the data from the view */
        refreshData();

        /* Apply the current state */
        theState.applyState();

        /* Add the listeners */
        theDateButton.addPropertyChangeListener(TethysSwingDateButton.PROPERTY_DATEDAY, e -> handleNewDate());
        theDownloadButton.addActionListener(e -> theEventManager.fireEvent(PrometheusDataEvent.DOWNLOAD));
        theNext.addActionListener(e -> {
            theState.setNext();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        });
        thePrev.addActionListener(e -> {
            theState.setPrev();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        });
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Get the selected date.
     * @return the date
     */
    public TethysDate getDate() {
        return theState.getDate();
    }

    /**
     * Refresh data.
     */
    public final void refreshData() {
        /* Access the data */
        TethysDateRange myRange = theView.getRange();

        /* Set the range for the Date Button */
        setRange(myRange);

        /* Set the currency name */
        MoneyWiseData myData = theView.getData();
        AssetCurrency myDefault = myData.getDefaultCurrency();
        theCurrLabel.setText(myDefault != null
                                               ? myDefault.getDesc() + " (" + myDefault.getName() + ")"
                                               : null);
    }

    /**
     * Set the range for the date box.
     * @param pRange the Range to set
     */
    private void setRange(final TethysDateRange pRange) {
        TethysDate myStart = (pRange == null)
                                              ? null
                                              : pRange.getStart();
        TethysDate myEnd = (pRange == null)
                                            ? null
                                            : pRange.getEnd();

        /* Set up range */
        theDateButton.setEarliestDateDay(myStart);
        theDateButton.setLatestDateDay(myEnd);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        theNext.setEnabled(bEnabled && (theState.getNextDate() != null));
        thePrev.setEnabled(bEnabled && (theState.getPrevDate() != null));
        theDateButton.setEnabled(bEnabled);
        theDownloadButton.setEnabled(bEnabled);
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new SpotRatesState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new SpotRatesState(theSavePoint);

        /* Apply the state */
        theState.applyState();
    }

    /**
     * Set Adjacent dates.
     * @param pPrev the previous Date
     * @param pNext the next Date
     */
    public void setAdjacent(final TethysDate pPrev,
                            final TethysDate pNext) {
        /* Record the dates */
        theState.setAdjacent(pPrev, pNext);
    }

    /**
     * Handle new Date.
     */
    private void handleNewDate() {
        /* Select the new date */
        if (theState.setDate(theDateButton)) {
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class SpotRatesState {
        /**
         * Selected date.
         */
        private TethysDate theDate;

        /**
         * Next date.
         */
        private TethysDate theNextDate;

        /**
         * Previous date.
         */
        private TethysDate thePrevDate;

        /**
         * Constructor.
         */
        private SpotRatesState() {
            theDate = new TethysDate();
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private SpotRatesState(final SpotRatesState pState) {
            theDate = new TethysDate(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new TethysDate(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new TethysDate(pState.getPrevDate());
            }
        }

        /**
         * Get the selected date.
         * @return the date
         */
        private TethysDate getDate() {
            return theDate;
        }

        /**
         * Get the next date.
         * @return the date
         */
        private TethysDate getNextDate() {
            return theNextDate;
        }

        /**
         * Get the previous date.
         * @return the date
         */
        private TethysDate getPrevDate() {
            return thePrevDate;
        }

        /**
         * Set new Date.
         * @param pButton the Button with the new date
         * @return true/false did a change occur
         */
        private boolean setDate(final TethysSwingDateButton pButton) {
            /* Adjust the date and build the new range */
            TethysDate myDate = new TethysDate(pButton.getSelectedDate());
            if (!MetisDifference.isEqual(myDate, theDate)) {
                theDate = myDate;
                return true;
            }
            return false;
        }

        /**
         * Set Next Date.
         */
        private void setNext() {
            /* Copy date */
            theDate = new TethysDate(theNextDate);
            applyState();
        }

        /**
         * Set Previous Date.
         */
        private void setPrev() {
            /* Copy date */
            theDate = new TethysDate(thePrevDate);
            applyState();
        }

        /**
         * Set Adjacent dates.
         * @param pPrev the previous Date
         * @param pNext the next Date
         */
        private void setAdjacent(final TethysDate pPrev,
                                 final TethysDate pNext) {
            /* Record the dates */
            thePrevDate = pPrev;
            theNextDate = pNext;

            /* Adjust values */
            setEnabled(true);
        }

        /**
         * Apply the State.
         */
        private void applyState() {
            /* Adjust the lock-down */
            setEnabled(true);
            theDateButton.setSelectedDateDay(theDate);

            /* Determine whether we are todays date */
            boolean isToday = MetisDifference.isEqual(theDate, new TethysDate());
            theDownloadButton.setVisible(isToday);
        }
    }
}
