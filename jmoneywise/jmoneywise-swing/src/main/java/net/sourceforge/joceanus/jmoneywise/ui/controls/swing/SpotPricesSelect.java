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
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.field.swing.MetisFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseIcon;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.ui.TethysNode;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenu;
import net.sourceforge.joceanus.jtethys.ui.TethysScrollMenuContent.TethysScrollMenuItem;
import net.sourceforge.joceanus.jtethys.ui.TethysUIEvent;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingArrowIcon;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingButton;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingDateButtonManager;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingGuiFactory;
import net.sourceforge.joceanus.jtethys.ui.swing.TethysSwingScrollButtonManager;

/**
 * SpotPrice selection panel.
 * @author Tony Washer
 */
public class SpotPricesSelect
        implements TethysEventProvider<PrometheusDataEvent>, TethysNode<JComponent> {
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
     * Id.
     */
    private final Integer theId;

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The panel.
     */
    private final JPanel thePanel;

    /**
     * The data view.
     */
    private final View theView;

    /**
     * The date button.
     */
    private final TethysSwingDateButtonManager theDateButton;

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
    private final TethysSwingButton theDownloadButton;

    /**
     * The portfolio button.
     */
    private final TethysSwingScrollButtonManager<PortfolioBucket> thePortButton;

    /**
     * The portfolio menu.
     */
    private final TethysScrollMenu<PortfolioBucket, ?> thePortMenu;

    /**
     * The Portfolio list.
     */
    private PortfolioBucketList thePortfolios;

    /**
     * The current state.
     */
    private SpotPricesState theState;

    /**
     * The saved state.
     */
    private SpotPricesState theSavePoint;

    /**
     * Do we show closed accounts.
     */
    private boolean doShowClosed;

    /**
     * Are we refreshing data?
     */
    private boolean refreshingData;

    /**
     * Constructor.
     * @param pView the data view
     */
    public SpotPricesSelect(final View pView) {
        /* Store table and view details */
        theView = pView;

        /* Access GUI Factory */
        TethysSwingGuiFactory myFactory = (TethysSwingGuiFactory) pView.getUtilitySet().getGuiFactory();
        theId = myFactory.getNextId();

        /* Create Event Manager */
        theEventManager = new TethysEventManager<>();

        /* Create Labels */
        JLabel myDate = new JLabel(NLS_DATE);
        JLabel myPort = new JLabel(NLS_PORT);

        /* Create the check box */
        theShowClosed = new JCheckBox(NLS_CLOSED);
        theShowClosed.setSelected(doShowClosed);

        /* Create the DateButton */
        theDateButton = myFactory.newDateButton();

        /* Create the Download Button */
        theDownloadButton = myFactory.newButton();
        MoneyWiseIcon.configureDownloadIconButton(theDownloadButton);

        /* Create the Buttons */
        theNext = new JButton(TethysSwingArrowIcon.RIGHT);
        thePrev = new JButton(TethysSwingArrowIcon.LEFT);
        theNext.setToolTipText(NLS_NEXTTIP);
        thePrev.setToolTipText(NLS_PREVTIP);

        /* Create the portfolio button */
        thePortButton = myFactory.newScrollButton();

        /* Create initial state */
        theState = new SpotPricesState();

        /* Create the panel */
        thePanel = new JPanel();
        thePanel.setLayout(new BoxLayout(thePanel, BoxLayout.X_AXIS));
        thePanel.setBorder(BorderFactory.createTitledBorder(NLS_TITLE));

        /* Define the layout */
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(myDate);
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(thePrev);
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(theDateButton.getNode());
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(theNext);
        thePanel.add(Box.createHorizontalGlue());
        thePanel.add(myPort);
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(thePortButton.getNode());
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(theShowClosed);
        thePanel.add(Box.createHorizontalGlue());
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));
        thePanel.add(theDownloadButton.getNode());
        thePanel.add(Box.createRigidArea(new Dimension(STRUT_WIDTH, 0)));

        /* Initialise the data from the view */
        refreshData();

        /* Apply the current state */
        theState.applyState();

        /* Access the menus */
        thePortMenu = thePortButton.getMenu();

        /* Add the listeners */
        TethysEventRegistrar<TethysUIEvent> myRegistrar = thePortButton.getEventRegistrar();
        myRegistrar.addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewPortfolio());
        myRegistrar.addEventListener(TethysUIEvent.PREPAREDIALOG, e -> buildPortfolioMenu());
        theDownloadButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> theEventManager.fireEvent(PrometheusDataEvent.DOWNLOAD));
        theDateButton.getEventRegistrar().addEventListener(TethysUIEvent.NEWVALUE, e -> handleNewDate());
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
    public Integer getId() {
        return theId;
    }

    @Override
    public JComponent getNode() {
        return thePanel;
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
        theDateButton.setEarliestDate(myStart);
        theDateButton.setLatestDate(myEnd);
    }

    @Override
    public void setEnabled(final boolean bEnabled) {
        theNext.setEnabled(bEnabled && (theState.getNextDate() != null));
        thePrev.setEnabled(bEnabled && (theState.getPrevDate() != null));
        theDateButton.setEnabled(bEnabled);
        thePortButton.setEnabled(bEnabled);
        theDownloadButton.setEnabled(bEnabled);
    }

    @Override
    public void setVisible(final boolean pVisible) {
        thePanel.setVisible(pVisible);
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
        thePortMenu.removeAllItems();

        /* Record active item */
        TethysScrollMenuItem<PortfolioBucket> myActive = null;
        PortfolioBucket myCurr = theState.getPortfolio();

        /* Loop through the available portfolio values */
        Iterator<PortfolioBucket> myIterator = thePortfolios.iterator();
        while (myIterator.hasNext()) {
            PortfolioBucket myBucket = myIterator.next();

            /* Create a new MenuItem and add it to the popUp */
            TethysScrollMenuItem<PortfolioBucket> myItem = thePortMenu.addItem(myBucket);

            /* If this is the active bucket */
            if (myBucket.equals(myCurr)) {
                /* Record it */
                myActive = myItem;
            }
        }

        /* Ensure active item is visible */
        if (myActive != null) {
            myActive.scrollToItem();
        }
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
        private boolean setDate(final TethysSwingDateButtonManager pButton) {
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
            theDateButton.setSelectedDate(theDate);
            thePortButton.setValue(thePortfolio);

            /* Determine whether we are todays date */
            boolean isToday = MetisDifference.isEqual(theDate, new TethysDate());
            theDownloadButton.setVisible(isToday);
        }
    }
}
