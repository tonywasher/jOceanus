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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.field.swing.JFieldElement;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket;
import net.sourceforge.joceanus.jmoneywise.analysis.PortfolioBucket.PortfolioBucketList;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.ui.MoneyWiseUIResource;
import net.sourceforge.joceanus.jmoneywise.views.View;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.dateday.swing.JDateDayButton;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;
import net.sourceforge.joceanus.jtethys.swing.ArrowIcon;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton;
import net.sourceforge.joceanus.jtethys.swing.JScrollButton.JScrollMenuBuilder;

/**
 * SpotPrice selection panel.
 * @author Tony Washer
 */
public class SpotPricesSelect
        extends JPanel
        implements JOceanusEventProvider {
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
    private static final String NLS_PORT = MoneyWiseDataType.PORTFOLIO.getItemName() + JFieldElement.STR_COLON;

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
    private final transient JOceanusEventManager theEventManager;

    /**
     * The data view.
     */
    private final transient View theView;

    /**
     * The date button.
     */
    private final JDateDayButton theDateButton;

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
        theEventManager = new JOceanusEventManager();

        /* Create Labels */
        JLabel myDate = new JLabel(NLS_DATE);
        JLabel myPort = new JLabel(NLS_PORT);

        /* Create the check box */
        theShowClosed = new JCheckBox(NLS_CLOSED);
        theShowClosed.setSelected(doShowClosed);

        /* Create the DateButton */
        theDateButton = new JDateDayButton();

        /* Create the Download Button */
        theDownloadButton = MoneyWiseIcons.getDownloadButton();

        /* Create the Buttons */
        theNext = new JButton(ArrowIcon.RIGHT);
        thePrev = new JButton(ArrowIcon.LEFT);
        theNext.setToolTipText(NLS_NEXTTIP);
        thePrev.setToolTipText(NLS_PREVTIP);

        /* Create the portfolio button */
        thePortButton = new JScrollButton<PortfolioBucket>();

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

        /* Add the listener for item changes */
        new SpotPricesListener();
    }

    @Override
    public JOceanusEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Get the selected date.
     * @return the date
     */
    public JDateDay getDate() {
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
        JDateDayRange myRange = theView.getRange();

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
    public final void setRange(final JDateDayRange pRange) {
        JDateDay myStart = (pRange == null)
                                           ? null
                                           : pRange.getStart();
        JDateDay myEnd = (pRange == null)
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
    public void setAdjacent(final JDateDay pPrev,
                            final JDateDay pNext) {
        /* Record the dates */
        theState.setAdjacent(pPrev, pNext);
    }

    /**
     * Listener class.
     */
    private final class SpotPricesListener
            implements ActionListener, JOceanusChangeEventListener, PropertyChangeListener, ItemListener {
        /**
         * The portfolio menu builder.
         */
        private final JScrollMenuBuilder<PortfolioBucket> thePortMenuBuilder;

        /**
         * PortfolioMenu Registration.
         */
        private final JOceanusChangeRegistration thePortMenuReg;

        /**
         * Constructor.
         */
        private SpotPricesListener() {
            /* Access builder */
            thePortMenuBuilder = thePortButton.getMenuBuilder();
            thePortMenuReg = thePortMenuBuilder.getEventRegistrar().addChangeListener(this);

            /* Add swing listeners */
            theDateButton.addPropertyChangeListener(JDateDayButton.PROPERTY_DATE, this);
            theShowClosed.addItemListener(this);
            theNext.addActionListener(this);
            thePrev.addActionListener(this);
            theDownloadButton.addActionListener(this);
            thePortButton.addPropertyChangeListener(JScrollButton.PROPERTY_VALUE, this);
        }

        @Override
        public void actionPerformed(final ActionEvent evt) {
            Object o = evt.getSource();

            /* If this event relates to the Next button */
            if (theNext.equals(o)) {
                /* Set next and notify changes */
                theState.setNext();
                theEventManager.fireStateChanged();

                /* If this event relates to the previous button */
            } else if (thePrev.equals(o)) {
                /* Set previous and notify changes */
                theState.setPrev();
                theEventManager.fireStateChanged();

                /* If this event relates to the download button */
            } else if (theDownloadButton.equals(o)) {
                /* fire an action event */
                theEventManager.fireActionEvent();
            }
        }

        @Override
        public void propertyChange(final PropertyChangeEvent evt) {
            Object o = evt.getSource();

            /* if event relates to the Date button */
            if (theDateButton.equals(o)
                && (theState.setDate(theDateButton))) {
                theEventManager.fireStateChanged();
            }

            /* if event relates to the Portfolio button */
            if (thePortButton.equals(o)
                && (theState.setPortfolio(thePortButton.getValue()))) {
                theState.applyState();
                theEventManager.fireStateChanged();
            }
        }

        @Override
        public void itemStateChanged(final ItemEvent evt) {
            Object o = evt.getSource();

            /* Ignore selection if refreshing data */
            if (refreshingData) {
                return;
            }

            /* If this event relates to the showClosed box */
            if (theShowClosed.equals(o)) {
                /* Note the new criteria and re-build lists */
                doShowClosed = theShowClosed.isSelected();
                theEventManager.fireStateChanged();
            }
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If this is the PortfolioMenu */
            if (thePortMenuReg.isRelevant(pEvent)) {
                buildPortfolioMenu();
            }
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
    }

    /**
     * SavePoint values.
     */
    private final class SpotPricesState {
        /**
         * Portfolio.
         */
        private PortfolioBucket thePortfolio = null;

        /**
         * Selected date.
         */
        private JDateDay theDate = null;

        /**
         * Next date.
         */
        private JDateDay theNextDate = null;

        /**
         * Previous date.
         */
        private JDateDay thePrevDate = null;

        /**
         * Constructor.
         */
        private SpotPricesState() {
            theDate = new JDateDay();
        }

        /**
         * Constructor.
         * @param pState state to copy from
         */
        private SpotPricesState(final SpotPricesState pState) {
            thePortfolio = pState.getPortfolio();
            theDate = new JDateDay(pState.getDate());
            if (pState.getNextDate() != null) {
                theNextDate = new JDateDay(pState.getNextDate());
            }
            if (pState.getPrevDate() != null) {
                thePrevDate = new JDateDay(pState.getPrevDate());
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
        private JDateDay getDate() {
            return theDate;
        }

        /**
         * Get the next date.
         * @return the date
         */
        private JDateDay getNextDate() {
            return theNextDate;
        }

        /**
         * Get the previous date.
         * @return the date
         */
        private JDateDay getPrevDate() {
            return thePrevDate;
        }

        /**
         * Set new Portfolio.
         * @param pPortfolio the Portfolio
         * @return true/false did a change occur
         */
        private boolean setPortfolio(final PortfolioBucket pPortfolio) {
            /* Adjust the selected portfolio */
            if (!Difference.isEqual(pPortfolio, thePortfolio)) {
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
        private boolean setDate(final JDateDayButton pButton) {
            /* Adjust the date and build the new range */
            JDateDay myDate = new JDateDay(pButton.getSelectedDate());
            if (!Difference.isEqual(myDate, theDate)) {
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
            theDate = new JDateDay(theNextDate);
            applyState();
        }

        /**
         * Set Previous Date.
         */
        private void setPrev() {
            /* Copy date */
            theDate = new JDateDay(thePrevDate);
            applyState();
        }

        /**
         * Set Adjacent dates.
         * @param pPrev the previous Date
         * @param pNext the next Date
         */
        private void setAdjacent(final JDateDay pPrev,
                                 final JDateDay pNext) {
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
            boolean isToday = Difference.isEqual(theDate, new JDateDay());
            theDownloadButton.setVisible(isToday);
        }
    }
}
