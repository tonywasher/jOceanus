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
package net.sourceforge.joceanus.jmoneywise.views;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisManager;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionAnalyser;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jprometheus.views.UpdateEntry;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.dateday.TethysDateRange;
import net.sourceforge.joceanus.jtethys.event.TethysEventManager;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.TethysEventRegistrar.TethysEventProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analysis Edit View.
 */
public class AnalysisView
        implements JDataContents, TethysEventProvider {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(MoneyWiseViewResource.ANALYSISVIEW_NAME.getValue());

    /**
     * Base Analysis Field Id.
     */
    private static final JDataField FIELD_BASEANALYSIS = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.ANALYSISVIEW_BASE.getValue());

    /**
     * UpdateSet Field Id.
     */
    private static final JDataField FIELD_UPDATESET = FIELD_DEFS.declareLocalField(MoneyWiseViewResource.ANALYSISVIEW_UPDATESET.getValue());

    /**
     * Analysis Field Id.
     */
    private static final JDataField FIELD_ANALYSIS = FIELD_DEFS.declareLocalField(AnalysisResource.ANALYSIS_NAME.getValue());

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisView.class);

    /**
     * The Event Manager.
     */
    private final TethysEventManager theEventManager;

    /**
     * The View.
     */
    private final View theView;

    /**
     * The UpdateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The event entry.
     */
    private final UpdateEntry<Transaction, MoneyWiseDataType> theTransEntry;

    /**
     * The info entry.
     */
    private final UpdateEntry<TransactionInfo, MoneyWiseDataType> theInfoEntry;

    /**
     * The Underlying analysis.
     */
    private Analysis theBaseAnalysis;

    /**
     * The transactions.
     */
    private TransactionView theTransactions;

    /**
     * Analysis Manager.
     */
    private AnalysisManager theManager;

    /**
     * The active analysis.
     */
    private Analysis theAnalysis;

    /**
     * The current range.
     */
    private TethysDateRange theRange;

    /**
     * Constructor.
     * @param pView the view
     * @param pUpdateSet the update set
     */
    public AnalysisView(final View pView,
                        final UpdateSet<MoneyWiseDataType> pUpdateSet) {
        /* Store update set */
        theView = pView;
        theUpdateSet = pUpdateSet;

        /* Register data entries */
        theTransEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSACTION);
        theInfoEntry = theUpdateSet.registerType(MoneyWiseDataType.TRANSACTIONINFO);

        /* Create event manager */
        theEventManager = new TethysEventManager();
    }

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_BASEANALYSIS.equals(pField)) {
            return theBaseAnalysis;
        }
        if (FIELD_UPDATESET.equals(pField)) {
            return theUpdateSet;
        }
        if (FIELD_ANALYSIS.equals(pField)) {
            return theAnalysis;
        }
        return JDataFieldValue.UNKNOWN;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    @Override
    public TethysEventRegistrar getEventRegistrar() {
        return theEventManager.getEventRegistrar();
    }

    /**
     * Obtain the active analysis.
     * @return the active analysis
     */
    public Analysis getAnalysis() {
        return theAnalysis;
    }

    /**
     * Obtain the transaction list.
     * @return the transaction list
     */
    public TransactionList getTransactions() {
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
            theBaseAnalysis = theManager.getAnalysis(theRange);
            theAnalysis = theBaseAnalysis;

            /* Create the new transaction list */
            MoneyWiseData myData = theView.getData();
            theTransactions = new TransactionView(myData.getTransactions(), theRange);
        } else {
            /* Set nulls */
            theBaseAnalysis = null;
            theAnalysis = null;
            theTransactions = null;
        }

        /* register the lists */
        registerLists();

        /* Notify listeners */
        theEventManager.fireStateChanged();
    }

    /**
     * Set the selected date range.
     * @param pRange the date range
     */
    public void setRange(final TethysDateRange pRange) {
        /* If we have changed the range */
        if (!Difference.isEqual(theRange, pRange)) {
            /* Obtain the required analysis and reset to it */
            theRange = pRange;
            theBaseAnalysis = theManager != null
                                                ? theManager.getAnalysis(theRange)
                                                : null;
            theAnalysis = theBaseAnalysis;

            /* Create the new transaction list */
            MoneyWiseData myData = theView.getData();
            theTransactions = new TransactionView(myData.getTransactions(), theRange);

            /* register the lists */
            registerLists();

            /* Notify listeners */
            theEventManager.fireStateChanged();
        }
    }

    /**
     * /** Register lists.
     */
    private void registerLists() {
        /* If we have transactions */
        if (theTransactions != null) {
            TransactionInfoList myInfo = theTransactions.getTransactionInfo();
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
    private final class TransactionView
            extends TransactionList {
        /**
         * Constructor.
         * @param pSource the source transaction list
         * @param pRange the date range
         */
        private TransactionView(final TransactionList pSource,
                                final TethysDateRange pRange) {
            /* Initialise as edit list */
            super(pSource);
            setStyle(ListStyle.EDIT);
            setRange(pRange);

            /* Store InfoType list */
            setTransInfoTypes(pSource.getTransInfoTypes());

            /* Create and store info List */
            TransactionInfoList myTransInfo = pSource.getTransactionInfo();
            setTransactionInfo(myTransInfo.getEmptyList(ListStyle.EDIT));

            /* Loop through the Transactions extracting relevant elements */
            Iterator<Transaction> myIterator = pSource.iterator();
            while (myIterator.hasNext()) {
                Transaction myCurr = myIterator.next();

                /* Ignore deleted events */
                if (myCurr.isDeleted()) {
                    continue;
                }

                /* Check the range */
                int myResult = pRange.compareTo(myCurr.getDate());

                /* Handle out of range */
                if (myResult > 0) {
                    continue;
                } else if (myResult < 0) {
                    break;
                }

                /* Build the new linked transaction and add it to the list */
                Transaction myTrans = new Transaction(this, myCurr);
                append(myTrans);
            }
        }

        @Override
        public void postProcessOnUpdate() {
            /* Pass call on */
            super.postProcessOnUpdate();

            /* Protect against exceptions */
            try {
                /* Build the new analysis */
                TransactionAnalyser myAnalyser = new TransactionAnalyser(theView.getActiveProfile(), theBaseAnalysis, this);
                theAnalysis = myAnalyser.getAnalysis();

                /* Notify listeners */
                theEventManager.fireStateChanged();

                /* Catch exceptions */
            } catch (OceanusException e) {
                LOGGER.error("Failed to analyse changes", e);
            }
        }
    }
}
