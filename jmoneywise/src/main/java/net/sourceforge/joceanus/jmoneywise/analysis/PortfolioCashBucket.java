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
package net.sourceforge.joceanus.jmoneywise.analysis;

import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmoneywise.data.Portfolio;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayRange;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;

/**
 * The Portfolio Cash Bucket class.
 */
public class PortfolioCashBucket
        extends AccountBucket<Portfolio> {
    /**
     * Local Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(AnalysisResource.PORTFOLIOCASH_NAME.getValue(), AccountBucket.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pCash the cash account
     */
    protected PortfolioCashBucket(final Analysis pAnalysis,
                                  final Portfolio pPortfolio) {
        /* Call super-constructor */
        super(pAnalysis, pPortfolio);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pDate the date for the bucket
     */
    protected PortfolioCashBucket(final Analysis pAnalysis,
                                  final PortfolioCashBucket pBase,
                                  final JDateDay pDate) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pDate);
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     * @param pBase the underlying bucket
     * @param pRange the range for the bucket
     */
    protected PortfolioCashBucket(final Analysis pAnalysis,
                                  final PortfolioCashBucket pBase,
                                  final JDateDayRange pRange) {
        /* Call super-constructor */
        super(pAnalysis, pBase, pRange);
    }

    /**
     * Adjust account for credit.
     * @param pTrans the transaction causing the credit
     */
    protected void adjustForXfer(final PortfolioCashBucket pSource,
                                 final Transaction pTrans) {
        /* Access transfer amount */
        AccountValues myValues = pSource.getValues();
        JMoney myAmount = myValues.getMoneyValue(AccountAttribute.VALUATION);

        /* Adjust this valuation */
        JMoney myValuation = getNewValuation();
        myValuation.addAmount(myAmount);
        setValue(AccountAttribute.VALUATION, myValuation);

        /* Adjust source valuation */
        myValuation = pSource.getNewValuation();
        myValuation.subtractAmount(myAmount);
        pSource.setValue(AccountAttribute.VALUATION, myValuation);

        /* Register the transaction in the histories */
        pSource.registerTransaction(pTrans);
        registerTransaction(pTrans);
    }
}
