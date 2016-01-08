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
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
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
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.ui.swing.JScrollButton.JScrollMenuBuilder;

/**
 * SpotPrice selection panel.
 * @author Tony Washer
 */
public class SpotPricesSelect
        extends JPanel
        implements TethysEventProvider<PrometheusDataEvent> {
    /**
     * Serial Id.
     */
    private static final long serialVersionUID = -361214955549174070L;

    /**
     * Strut width.
     */
    private static final int STRUT_WIDTH = 10;

    /**
     * Text for Date Label.
     */
    private static final String NLS_DATE = MoneyWiseUIResource.SPOTEVENT_DATE.getValue();

    /**
     * Text for Portfolio Label.
     */
    private static final String NLS_PORT = MoneyWiseDataType.PORTFOLIO.getItemName() + MetisFieldElement.STR_COLON;

    /**
     * Text for Show Closed.
     */
    private static final String NLS_CLOSED = MoneyWiseUIResource.UI_PROMPT_SHOWCLOSED.getValue();

    /**
     * Text for Title.
     */
    private static final String NLS_TITLE = MoneyWiseUIResource.SPOTPRICE_TITLE.getValue();

    /**
     * Text for Next toolTip.
     */
    private static final String NLS_NEXTTIP = MoneyWiseUIResource.SPOTPRICE_NEXT.getValue();

    /**
     * Text for Previous toolTip.
     */
    private static final String NLS_PREVTIP = MoneyWiseUIResource.SPOTPRICE_PREV.getValue();

    /**
     * The Event Manager.
     */
    private final transient TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The date button.
     */
    private final TethysSwingDateButton theDateButton;

    /**
     * The showClosed checkBox.
     */
    private final JCheckBox theShowClosed;

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
     * The portfolio button.
     */
    private final JScrollButton<PortfolioBucket> thePortButton;

    /**
     * The portfolio menu builder.
     */
    private final JScrollMenuBuilder<PortfolioBucket> thePortMenuBuilder;

    /**
     * The Portfolio list.
     */
    private transient PortfolioBucketList thePortfolios = null;

    /**
     * The current state.
     */
    private transient SpotPricesState theState = null;

    /**
     * The saved state.
     */
    private transient SpotPricesState theSavePoint = null;

    /**
     * Do we show closed accounts.
     */
    private boolean doShowClosed = false;

    /**
     * Are we refreshing data?
     */
    private boolean refreshingData = false;

    /**
     * Constructor.
     * @param pView the data view
     */
    public SpotPricesSelect(final View pView) {
        /* Store table and view details */
        theView = pView;

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create Labels */
        JLabel myDate = new JLabel(NLS_DATE);
        JLabel myPort = new JLabel(NLS_PORT);

        /* Create the check box */
        theShowClosed = new JCheckBox(NLS_CLOSED);
        theShowClosed.setSelected(doShowClosed);

        /* Create the DateButton */
        theDateButton = new TethysSwingDateButton();

        /* Create the Download Button */
        theDownloadButton = MoneyWiseIcons.getDownloadButton();

        /* Create the Buttons */
        theNext = new JButton(TethysSwingArrowIcon.RIGHT);
        thePrev = new JButton(TethysSwingArrowIcon.LEFT);
        theNext.setToolTipText(NLS_NEXTTIP);
        thePrev.setToolTipText(NLS_PREVTIP);

        /* Create the portfolio button */
        thePortButton = new JScrollButton<>();

        /* Create initial state */
        theState = new SpotPricesState();

        /* Create the panel */
        setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(myDate);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePrev);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theDateButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theNext);
        add(Box.createHorizontalGlue());
        add(myPort);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(thePortButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theShowClosed);
        add(Box.createHorizontalGlue());
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        add(theDownloadButton);
        add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Initialise the data from the view */
        refreshData();

        /* Apply the current state */
        theState.applyState();

        /* Add the listeners */
        thePortMenuBuilder = thePortButton.getMenuBuilder();
        thePortMenuBuilder.getEventRegistrar().addEventListener(e -> buildPortfolioMenu());
        theDownloadButton.addActionListener(e -> theEventManager.fireEvent(PrometheusDataEvent.DOWNLOAD));
        theDateButton.addPropertyChangeListener(TethysSwingDateButton.PROPERTY_DATEDAY, e -> handleNewDate());
        thePortButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, e -> handleNewPortfolio());
        theShowClosed.addItemListener(e -> handleNewClosed());
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
     * Get the selected portfolio.
     * @return the portfolio
     */
    public final Portfolio getPortfolio() {
        PortfolioBucket myBucket = theState.getPortfolio();
        return (myBucket == null)
                                  ? null
                                  : myBucket.getPortfolio();
    }

    /**
     * Do we show closed accounts?.
     * @return the date
     */
    public boolean getShowClosed() {
        return doShowClosed;
    }

    /**
     * Refresh data.
     */
    public final void refreshData() {
        /* Access the data */
        TethysDateRange myRange = theView.getRange();

        /* Set the range for the Date Button */
        setRange(myRange);

        /* Access portfolio list */
        AnalysisManager myManager = theView.getAnalysisManager();
        Analysis myAnalysis = myManager.getAnalysis();
        thePortfolios = myAnalysis.getPortfolios();

        /* Note that we are refreshing data */
        refreshingData = true;

        /* Obtain the current portfolio */
        PortfolioBucket myPortfolio = theState.getPortfolio();

        /* If we have a selected Portfolio */
        if (myPortfolio != null) {
            /* Look for the equivalent bucket */
            myPortfolio = thePortfolios.findItemById(myPortfolio.getOrderedId());
        }

        /* If we do not have an active portfolio and the list is non-empty */
        if ((myPortfolio == null) && (!thePortfolios.isEmpty())) {
            /* Access the first portfolio */
            myPortfolio = getFirstPortfolio();
        }

        /* Set the portfolio */
        theState.setPortfolio(myPortfolio);
        theState.applyState();

        /* Note that we have finished refreshing data */
        refreshingData = false;
    }

    /**
     * Obtain first portfolio.
     * @return the first portfolio
     */
    private PortfolioBucket getFirstPortfolio() {
        /* Loop through the available account values */
        Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
        return myIterator.hasNext()
                                    ? myIterator.next()
                                    : null;
    }

    /**
     * Set the range for the date box.
     * @param pRange the Range to set
     */
    public final void setRange(final TethysDateRange pRange) {
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
        thePortButton.setEnabled(bEnabled);
        theDownloadButton.setEnabled(bEnabled);
    }

    /**
     * Create SavePoint.
     */
    public void createSavePoint() {
        /* Create the savePoint */
        theSavePoint = new SpotPricesState(theState);
    }

    /**
     * Restore SavePoint.
     */
    public void restoreSavePoint() {
        /* Restore the savePoint */
        theState = new SpotPricesState(theSavePoint);

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
     * Build the portfolio menu.
     */
    private void buildPortfolioMenu() {
        /* Reset the popUp menu */
        thePortMenuBuilder.clearMenu();

        /* Record active item */
        JMenuItem myActive = null;
        PortfolioBucket myCurr = theState.getPortfolio();

        /* Loop through the available portfolio values */
        Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
        while (myIterator.hasNext()) {
            PortfolioBucket myBucket = myIterator.next();

            /* Create a new JMenuItem and add it to the popUp */
            JMenuItem myItem = thePortMenuBuilder.addItem(myBucket);

            /* If this is the active bucket */
            if (myBucket.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        thePortMenuBuilder.showItem(myActive);
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
     * Handle new Portfolio.
     */
    private void handleNewPortfolio() {
        /* Select the new portfolio */
        if (theState.setPortfolio(thePortButton.getValue())) {
            theState.applyState();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * Handle new Closed.
     */
    private void handleNewClosed() {
        if (!refreshingData) {
            doShowClosed = theShowClosed.isSelected();
            theEventManager.fireEvent(PrometheusDataEvent.SELECTIONCHANGED);
        }
    }

    /**
     * SavePoint values.
     */
    private final class SpotPricesState {
        /**
         * Portfolio.
         */
        private PortfolioBucket thePortfolio;

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
        private SpotPricesState() {
            theDate = new TethysDate();
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private SpotPricesState(final SpotPricesState pState) {
            thePortfolio = pState.getPortfolio();
            theDate = new TethysDate(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new TethysDate(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new TethysDate(pState.getPrevDate());
            }
        }

        /**
         * Get the portfolio.
         * @return the portfolio
         */
        private PortfolioBucket getPortfolio() {
            return thePortfolio;
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
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final PortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!MetisDifference.isEqual(pPortfolio, thePortfolio)) {
                thePortfolio = pPortfolio;
                return true;
            }
            return false;
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
            thePortButton.setValue(thePortfolio);

            /* Determine whether we are todays date */
            boolean isToday = MetisDifference.isEqual(theDate, new TethysDate());
            theDownloadButton.setVisible(isToday);
        }
    }
}
