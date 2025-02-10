/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.lethe.views;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfo.MoneyWiseTransInfoList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction.MoneyWiseTransactionList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoType.MoneyWiseTransInfoTypeList;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.analyse.MoneyWiseAnalysisTransAnalyser;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysis;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisDataResource;
import net.sourceforge.joceanus.moneywise.lethe.data.analysis.data.MoneyWiseAnalysisManager;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseView;
import net.sourceforge.joceanus.moneywise.views.MoneyWiseViewResource;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDateRange;
import net.sourceforge.joceanus.oceanus.event.OceanusEventManager;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar;
import net.sourceforge.joceanus.oceanus.event.OceanusEventRegistrar.TethysEventProvider;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogManager;
import net.sourceforge.joceanus.oceanus.logger.OceanusLogger;
import net.sourceforge.joceanus.oceanus.profile.OceanusProfile;
import net.sourceforge.joceanus.prometheus.views.PrometheusDataEvent;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditEntry;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;

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
        FIELD_DEFS.declareLocalField(MoneyWiseViewResource.ANALYSISVIEW_UPDATESET, MoneyWiseAnalysisView::getEditSet);
        FIELD_DEFS.declareLocalField(MoneyWiseAnalysisDataResource.ANALYSIS_NAME, MoneyWiseAnalysisView::getAnalysis);
    }

    /**
     * Logger.
     */
    private static final OceanusLogger LOGGER = OceanusLogManager.getLogger(MoneyWiseAnalysisView.class);

    /**
     * The Event Manager.
     */
    private final OceanusEventManager<PrometheusDataEvent> theEventManager;

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
    private OceanusDateRange theRange;

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
        theEventManager = new OceanusEventManager<>();
    }

    @Override
    public MetisFieldSet<MoneyWiseAnalysisView> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return FIELD_DEFS.getName();
    }

    @Override
    public OceanusEventRegistrar<PrometheusDataEvent> getEventRegistrar() {
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
    public OceanusDateRange getRange() {
        return theRange;
    }

    /**
     * Refresh data.
     */
    public void refreshData() {
        /* Protect against exceptions */
        try {
            /* Access the new analysis manager */
            theManager = theView.getAnalysisManager();
            theEditSet.setDataSet(theView.getData());

            /* If we have a range */
            if (theRange != null) {
                /* Obtain the required analysis and reset to it */
                theAnalysis = theManager.getRangedAnalysis(theRange);

                /* Create the new transaction list */
                final MoneyWiseDataSet myData = theView.getData();
                theTransactions = new MoneyWiseTransactionView(myData.getTransactions());

                /* else no range */
            } else {
                /* Set nulls */
                theAnalysis = null;
                theTransactions = null;
            }

            /* register the lists */
            registerLists();

            /* Notify listeners */
            theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);

        } catch (OceanusException e) {
            LOGGER.error("Failed to refreshData", e);
        }
    }

    /**
     * Set the selected date range.
     * @param pRange the date range
     */
    public void setRange(final OceanusDateRange pRange) {
        /* If we have changed the range */
        if (!MetisDataDifference.isEqual(theRange, pRange)) {
            /* Obtain the required analysis and reset to it */
            theRange = pRange;
            theAnalysis = theManager == null ? null : theManager.getRangedAnalysis(theRange);

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
         * @throws OceanusException on error
         */
        private MoneyWiseTransactionView(final MoneyWiseTransactionList pSource) throws OceanusException {
            /* Initialise as edit list */
            super(pSource);
            setStyle(PrometheusListStyle.EDIT);
            setEditSet(theEditSet);
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

                /* Build the new linked transaction and add it to the list */
                final MoneyWiseTransaction myTrans = new MoneyWiseTransaction(this, myCurr);
                add(myTrans);
                myTrans.resolveEditSetLinks();

                /* Adjust the map */
                myTrans.adjustMapForItem();
            }
        }

        @Override
        public void postProcessOnUpdate() {
            /* Pass call on */
            super.postProcessOnUpdate();

            /* Obtain the active profile */
            OceanusProfile myTask = theView.getActiveTask();
            myTask = myTask.startTask("updateAnalysis");

            /* Protect against exceptions */
            try {
                /* Sort the transaction list */
                myTask.startTask("sortTransactions");
                reSort();

                /* Initialise the analysis */
                myTask.startTask("UpdateMaps");
                theView.getData().updateMaps();

                /* Analyse the data */
                myTask.startTask("analyseData");
                final MoneyWiseAnalysisTransAnalyser myAnalyser = new MoneyWiseAnalysisTransAnalyser(myTask, theEditSet, theView.getPreferenceManager());
                final MoneyWiseAnalysis myAnalysis = myAnalyser.getAnalysis();
                theManager = new MoneyWiseAnalysisManager(myAnalysis);
                theAnalysis = theManager.getRangedAnalysis(theRange);

                /* Notify listeners */
                myTask.startTask("Notify");
                theEventManager.fireEvent(PrometheusDataEvent.ADJUSTVISIBILITY);

                /* Catch exceptions */
            } catch (OceanusException e) {
                LOGGER.error("Failed to analyse changes", e);
            }
        }
    }
}
