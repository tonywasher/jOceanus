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

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * Tax Analysis.
 */
public class MoneyWiseXAnalysisTax {
    /**
     * The taxMan account.
     */
    private final MoneyWiseXAnalysisPayeeBucket theTaxMan;

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    MoneyWiseXAnalysisTax(final MoneyWiseXAnalysis pAnalysis) {
        theTaxMan = pAnalysis.getPayees().getBucket(MoneyWisePayeeClass.TAXMAN);
    }

    /**
     * Adjust for taxCredit.
     * @param pTrans the transaction
     * @param pPayee the payee bucket
     */
    void adjustForPayeeDebitTaxCredit(final MoneyWiseXAnalysisTransaction pTrans,
                                      final MoneyWiseXAnalysisPayeeBucket pPayee) {
        /* Determine whether this is income or expense */
        final boolean isIncome = pTrans.isIncome();

        /* If we have taxCredit */
        final TethysMoney myTaxCredit = pTrans.getTransaction().getTaxCredit();
        if (myTaxCredit != null && myTaxCredit.isNonZero()) {
            if (isIncome) {
                pPayee.addIncome(myTaxCredit);
                theTaxMan.addExpense(myTaxCredit);
            } else {
                pPayee.addExpense(myTaxCredit);
                theTaxMan.addIncome(myTaxCredit);
            }
        }

        /* If we have employerNI */
        final TethysMoney myEmployerNI = pTrans.getTransaction().getEmployerNatIns();
        if (myEmployerNI != null && myEmployerNI.isNonZero()) {
            if (isIncome) {
                pPayee.addIncome(myEmployerNI);
            } else {
                pPayee.addExpense(myEmployerNI);
            }
        }

        /* If we have employeeNI */
        final TethysMoney myEmployeeNI = pTrans.getTransaction().getEmployeeNatIns();
        if (myEmployeeNI != null && myEmployeeNI.isNonZero()) {
            if (isIncome) {
                pPayee.addIncome(myEmployeeNI);
            } else {
                pPayee.addExpense(myEmployeeNI);
            }
        }

        /* If we have deemedBenefit */
        final TethysMoney myBenefit = pTrans.getTransaction().getDeemedBenefit();
        if (myBenefit != null && myBenefit.isNonZero()) {
            if (isIncome) {
                pPayee.addIncome(myBenefit);
            } else {
                pPayee.addExpense(myBenefit);
            }
        }

        /* If we have withheld funds */
        final TethysMoney myWithheld = pTrans.getTransaction().getWithheld();
        if (myWithheld != null && myWithheld.isNonZero()) {
            if (isIncome) {
                pPayee.addIncome(myWithheld);
                pPayee.addExpense(myWithheld);
            } else {
                pPayee.subtractIncome(myWithheld);
                pPayee.subtractExpense(myWithheld);
            }
        }
    }
}
