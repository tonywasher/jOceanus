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

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * Security analysis.
 */
public class MoneyWiseXAnalysisSecurity {
    /**
     * The market analysis.
     */
    private final MoneyWiseXAnalysisMarket theMarket;

    /**
     * The tax analysis.
     */
    private final MoneyWiseXAnalysisTax theTax;

    /**
     * Constructor.
     *
     * @param pAnalyser the analyser
     */
    MoneyWiseXAnalysisSecurity(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        theMarket = pAnalyser.getMarket();
        theTax = pAnalyser.getTax();
    }

    /**
     * Process a debit security transaction.
     *
     * @param pTrans  the transaction
     * @throws OceanusException on error
     */
    void processDebitSecurity(final MoneyWiseXAnalysisTran pTrans) throws OceanusException {

    }

    /**
     * Process a credit security transaction.
     *
     * @param pTrans  the transaction
     * @throws OceanusException on error
     */
    void processCreditSecurity(final MoneyWiseXAnalysisTran pTrans) throws OceanusException {

    }

    /**
     * Process a transaction that is a portfolio transfer.
     * <p>
     * This capital event relates only to both Debit and credit accounts.
     * @param pTrans  the transaction
     */
    void processPortfolioXfer(final MoneyWiseXAnalysisTran pTrans) {
    }
}
