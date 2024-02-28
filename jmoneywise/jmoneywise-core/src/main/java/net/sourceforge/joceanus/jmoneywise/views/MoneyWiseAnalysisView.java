/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.field.MetisFieldItem;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.data.analysis.analyse.MoneyWiseAnalysisTransAnalyser;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisDataResource;
import net.sourceforge.joceanus.jmoneywise.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransInfo;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransInfo.MoneyWiseTransInfoList;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransInfoType.MoneyWiseTransInfoTypeList;
import net.sourceforge.joceanus.jprometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditEntry;
import net.sourceforge.joceanus.jprometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.jtethys.logger.TethysLogManager;
import net.sourceforge.joceanus.jtethys.logger.TethysLogger;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Analysis Edit View.
 */
public class MoneyWiseAnalysisView
        implements MetisFieldItem, TethysEventProvider<PrometheusDataEvent> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseAnalysisView> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseAnalysisView.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.ANALYSISVIEW_BASE, MoneyWiseAnalysisView::getBaseAnalysis);
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.ANALYSISVIEW_UPDATESET, MoneyWiseAnalysisView::getEditSet);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisView::getAnalysis);
    }

    /**
     * Logger.
     */
    private static final TethysLogger LOGGER = TethysLogManager.getLogger(MoneyWiseAnalysisView.class);

    /**
     * The Event Manager.
     */
    private final TethysEventManager<PrometheusDataEvent> theEventManager;

    /**
     * The View.
     */
    private final MoneyWiseView theView;

    /**
     * The UpdateSet.
     */
    private final PrometheusEditSet theEditSet;

    /**
     * The event entry.
     */
    private final PrometheusEditEntry<MoneyWiseTransaction> theTransEntry;

    /**
     * The info entry.
     */
    private final PrometheusEditEntry<MoneyWiseTransInfo> theInfoEntry;

    /**
     * The Underlying analysis.
     */
    private MoneyWiseAnalysis theBaseAnalysis;

    /**
     * The transactions.
     */
    private MoneyWiseTransactionView theTransactions;

    /**
     * Analysis Manager.
     */
    private MoneyWiseAnalysisManager theManager;

    /**
     * The active analysis.
     */
    private MoneyWiseAnalysis theAnalysis;

    /**
     * The current range.
     */
    private TethysDateRange theRange;

    /**
     * Constructor.
     * @param pView the view
     * @param pEditSet the edit set
     */
    public MoneyWiseAnalysisView(final MoneyWiseView pView,
                                 final PrometheusEditSet pEditSet) {
        /* Store update set */
        theView = pView;
        theEditSet = pEditSet;

        /* Register data entries */
        theTransEntry = theEditSet.registerType(MoneyWiseBasicDataType.TRANSACTION);
        theInfoEntry = theEditSet.registerType(MoneyWiseBasicDataType.TRANSACTIONINFO);

        /* Create event manager */
        theEventManager = new TethysEventManager<>();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisView> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final TethysUIDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    @Override
    public TethysEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the active analysis.
     * @return the active analysis
     */
    public MoneyWiseAnalysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the base analysis.
     * @return the base analysis
     */
    private MoneyWiseAnalysis getBaseAnalysis() {
        return theBaseAnalysis;
    }

    /**
     * Obtain the editSet.
     * @return the editSet
     */
    private PrometheusEditSet getEditSet() {
        return theEditSet;
    }

    /**
     * Obtain the transaction list.
     * @return the transaction list
     */
    public MoneyWiseTransactionList getTransactions() {
        return theTransactions;
    }

    /**
     * Obtain the range.
     * @return the range
     */
    public TethysDateRange getRange() {
        return theRange;
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Access the new analysis manager */
        theManager = theView.getAnalysisManager();

        /* If we have a range */
        if (theRange != null) {
            /* Obtain the required analysis and reset to it */
            theBaseAnalysis = theManager.getRangedAnalysis(theRange);
            theAnalysis = theBaseAnalysis;

            /* Create the new transaction list */
            final MoneyWiseDataSet myData = (MoneyWiseDataSet) theView.getData();
            theTransactions = new MoneyWiseTransactionView(myData.getTransactions(), theRange);
        } else {
            /* Set nulls */
            theBaseAnalysis = null;
            theAnalysis = null;
            theTransactions = null;
        }

        /* register the lists */
        registerLists();

        /* Notify listeners */
        theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
    }

    /**
     * Set the selected date range.
     * @param pRange the date range
     */
    public void setRange(final TethysDateRange pRange) {
        /* If we have changed the range */
        if (!MetisDataDifference.isEqual(theRange, pRange)) {
            /* Obtain the required analysis and reset to it */
            theRange = pRange;
            theBaseAnalysis = theManager != null
                    ? theManager.getRangedAnalysis(theRange)
                    : null;
            theAnalysis = theBaseAnalysis;

            /* Create the new transaction list */
            final MoneyWiseDataSet myData = (MoneyWiseDataSet) theView.getData();
            theTransactions = new MoneyWiseTransactionView(myData.getTransactions(), theRange);

            /* register the lists */
            registerLists();

            /* Notify listeners */
            theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);
        }
    }

    /**
     * /** Register lists.
     */
    private void registerLists() {
        /* If we have transactions */
        if (theTransactions != null) {
            final MoneyWiseTransInfoList myInfo = theTransactions.getTransactionInfo();
            theTransEntry.setDataList(theTransactions);
            theInfoEntry.setDataList(myInfo);
        } else {
            theTransEntry.setDataList(null);
            theInfoEntry.setDataList(null);
        }
    }

    /**
     * TransactionView class.
     */
    private final class MoneyWiseTransactionView
            extends MoneyWiseTransactionList {
        /**
         * Constructor.
         * @param pSource the source transaction list
         * @param pRange the date range
         */
        private MoneyWiseTransactionView(final MoneyWiseTransactionList pSource,
                                         final TethysDateRange pRange) {
            /* Initialise as edit list */
            super(pSource);
            setStyle(PrometheusListStyle.EDIT);
            setRange(pRange);
            theEditSet.setEditEntryList(MoneyWiseBasicDataType.TRANSACTION, this);

            /* Store InfoType list */
            setTransInfoTypes(theEditSet.getDataList(MoneyWiseStaticDataType.TRANSINFOTYPE, MoneyWiseTransInfoTypeList.class));

            /* Create and store info List */
            final MoneyWiseTransInfoList myTransInfo = pSource.getTransactionInfo().getEmptyList(PrometheusListStyle.EDIT);
            theEditSet.setEditEntryList(MoneyWiseBasicDataType.TRANSACTIONINFO, myTransInfo);
            setTransactionInfo(myTransInfo);

            /* Loop through the Transactions extracting relevant elements */
            final Iterator<MoneyWiseTransaction> myIterator = pSource.iterator();
            while (myIterator.hasNext()) {
                final MoneyWiseTransaction myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Check the range */
                final int myResult = pRange.compareToDate(myCurr.getDate());

                /* Handle out of range */
                if (myResult > 0) {
                    continue;
                } else if (myResult < 0) {
                    break;
                }

                /* Build the new linked transaction and add it to the list */
                final MoneyWiseTransaction myTrans = new MoneyWiseTransaction(this, myCurr);
                add(myTrans);
                // myTrans.resolveEditSetLinks(); TODO
            }
        }

        @Override
        public void postProcessOnUpdate() {
            /* Pass call on */
            super.postProcessOnUpdate();

            /* Protect against exceptions */
            try {
                /* Build the new analysis */
                final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(theView.getActiveProfile(), theBaseAnalysis, this);
                theAnalysis = myAnalyser.getAnalysis();

                /* Notify listeners */
                theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);

                /* Catch exceptions */
            } catch (OceanusException e) {
                LOGGER.error("Failed to analyse changes", e);
            }
        }
    }
}
