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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmoneywise/src/main/java/net/sourceforge/joceanus/jmoneywise/views/AnalysisFilter.java $
 * $Revision: 571 $
 * $Author: Tony $
 * $Date: 2015-02-20 16:24:03 +0000 (Fri, 20 Feb 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.views;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataObject.JDataContents;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.analysis.Analysis;
import net.sourceforge.joceanus.jmoneywise.analysis.AnalysisResource;
import net.sourceforge.joceanus.jmoneywise.analysis.TransactionAnalyser;
import net.sourceforge.joceanus.jmoneywise.data.Transaction.TransactionList;
import net.sourceforge.joceanus.jprometheus.views.UpdateSet;
import net.sourceforge.joceanus.jtethys.JOceanusException;
import net.sourceforge.joceanus.jtethys.event.swing.JEventObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Analysis Edit View.
 */
public class AnalysisView
        extends JEventObject
        implements JDataContents {
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
        fireStateChanged();
    }

    /**
     * Listener class.
     */
    private final class AnalysisListener
            implements ChangeListener {
        /**
         * Constructor.
         */
        private AnalysisListener() {
            /* Listen to correct events */
            theUpdateSet.addChangeListener(this);
        }

        @Override
        public void stateChanged(final ChangeEvent pEvent) {
            /* Access source */
            Object o = pEvent.getSource();

            /* If the updateSet has changed */
            if (theUpdateSet.equals(o)) {
                /* Protect against exceptions */
                try {
                    /* Build the new analysis */
                    TransactionList myTrans = theUpdateSet.getDataList(MoneyWiseDataType.TRANSACTION, TransactionList.class);
                    TransactionAnalyser myAnalyser = new TransactionAnalyser(theView.getActiveProfile(), theBaseAnalysis, myTrans);
                    theAnalysis = myAnalyser.getAnalysis();

                    /* Notify listeners */
                    fireStateChanged();
                } catch (JOceanusException e) {
                    LOGGER.error("Failed to analyse changes", e);
                }
            }
        }
    }
}
