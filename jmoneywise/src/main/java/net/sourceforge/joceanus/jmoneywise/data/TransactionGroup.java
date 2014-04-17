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
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionInfoClass;

/**
 * Transaction group type.
 * @author Tony Washer
 */
public class TransactionGroup
        extends TransactionBaseGroup<Transaction> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TransactionGroup.class.getName());

    /**
     * Multiple Portfolios Error.
     */
    private static final String ERROR_PORTFOLIO = NLS_BUNDLE.getString("ErrorMultiplePortfolios");

    /**
     * Active Portfolio.
     */
    private Portfolio thePortfolio;

    /**
     * Obtain portfolio.
     * @return the portfolio if it exists
     */
    public Portfolio getPortfolio() {
        return thePortfolio;
    }

    /**
     * Constructor.
     * @param pParent the parent.
     */
    public TransactionGroup(final Transaction pParent) {
        /* Call super-constructor */
        super(pParent, Transaction.class);
    }

    @Override
    protected void analyseParent() {
        /* Analyse underlying */
        super.analyseParent();

        /* Access parent details */
        Transaction myParent = getParent();
        thePortfolio = myParent.getPortfolio();
    }

    @Override
    protected void validateChild(final Transaction pTrans) {
        /* Analyse underlying */
        super.validateChild(pTrans);

        /* If we have a portfolio */
        Portfolio myPortfolio = pTrans.getPortfolio();
        if (myPortfolio != null) {
            /* If this is the first portfolio */
            if (thePortfolio == null) {
                /* Store it */
                thePortfolio = myPortfolio;

                /* else check that it is the same portfolio */
            } else if (!thePortfolio.equals(myPortfolio)) {
                pTrans.addError(ERROR_PORTFOLIO, TransactionInfoSet.getFieldForClass(TransactionInfoClass.PORTFOLIO));
            }
        }
    }
}
