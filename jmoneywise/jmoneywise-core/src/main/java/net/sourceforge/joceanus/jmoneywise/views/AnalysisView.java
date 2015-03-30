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

import net.sourceforge.joceanus.jmetis.data.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.JDataFields;
import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.data.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionAnalyser;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEvent;
import net.sourceforge.joceanus.jtethys.event.JOceanusEvent.JOceanusChangeEventListener;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventManager;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistrar.JOceanusEventProvider;
import net.sourceforge.joceanus.jtethys.event.JOceanusEventRegistration.JOceanusChangeRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analysis Edit View.
 */
public class AnalysisView
        implements JDataContents, JOceanusEventProvider {
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
    private final JOceanusEventManager theEventManager;

    /**
     * The View.
     */
    private final View theView;

    /**
     * The Underlying analysis.
     */
    private Analysis theBaseAnalysis;

    /**
     * The UpdateSet.
     */
    private final UpdateSet<MoneyWiseDataType> theUpdateSet;

    /**
     * The active analysis.
     */
    private Analysis theAnalysis;

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

        /* Create event manager */
        theEventManager = new JOceanusEventManager();

        /* Create listener */
        new AnalysisListener();
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
    public JOceanusEventRegistrar getEventRegistrar() {
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
     * Set the analysis.
     * @param pAnalysis the new analysis
     */
    public void setAnalysis(final Analysis pAnalysis) {
        /* Store analysis */
        theBaseAnalysis = pAnalysis;
        theAnalysis = pAnalysis;

        /* Notify listeners */
        theEventManager.fireStateChanged();
    }

    /**
     * Listener class.
     */
    private final class AnalysisListener
            implements JOceanusChangeEventListener {
        /**
         * UpdateSet Registration.
         */
        private final JOceanusChangeRegistration theUpdateSetReg;

        /**
         * Constructor.
         */
        private AnalysisListener() {
            /* Listen to correct events */
            theUpdateSetReg = theUpdateSet.getEventRegistrar().addChangeListener(this);
        }

        @Override
        public void processChangeEvent(final JOceanusChangeEvent pEvent) {
            /* If this is the UpdateSet */
            if (theUpdateSetReg.isRelevant(pEvent)) {
                /* Protect against exceptions */
                try {
                    /* Build the new analysis */
                    TransactionList myTrans = theUpdateSet.getDataList(MoneyWiseDataType.TRANSACTION, TransactionList.class);
                    TransactionAnalyser myAnalyser = new TransactionAnalyser(theView.getActiveProfile(), theBaseAnalysis, myTrans);
                    theAnalysis = myAnalyser.getAnalysis();

                    /* Notify listeners */
                    theEventManager.fireStateChanged();
                } catch (JOceanusException e) {
                    LOGGER.error("Failed to analyse changes", e);
                }
            }
        }
    }
}
