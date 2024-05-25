/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2024 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.analyse;

import java.util.List;

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;

/**
 * Transaction analyser.
 */
public class MoneyWiseXAnalysisTransAnalyser {
    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The current event.
     */
    private MoneyWiseXAnalysisEvent theEvent;

    /**
     * Constructor.
     */
    MoneyWiseXAnalysisTransAnalyser(final MoneyWiseXAnalysis pAnalysis) {
        theAnalysis = pAnalysis;
    }

    /**
     * Process transaction event.
     * @param pEvent the event
     */
    public void processTransaction(final MoneyWiseXAnalysisEvent pEvent) {
        /* Ignore header transactions */
        theEvent = pEvent;
        final MoneyWiseTransaction myTrans = theEvent.getTransaction();
        if (!myTrans.isHeader()) {
            /* Touch underlying items */
            myTrans.touchUnderlyingItems();

            /* Process tags */
            final List<MoneyWiseTransTag> myTags = myTrans.getTransactionTags();
            if (myTags != null) {
                /* Process the transaction tags */
                theAnalysis.getTransactionTags().processEvent(pEvent, myTags.iterator());
            }

            /* Process the transaction */
            processTransaction(myTrans);
        }
    }

    /**
     * Process transaction.
     * @param pTrans the transaction
     */
    private void processTransaction(final MoneyWiseTransaction pTrans) {

    }
}
