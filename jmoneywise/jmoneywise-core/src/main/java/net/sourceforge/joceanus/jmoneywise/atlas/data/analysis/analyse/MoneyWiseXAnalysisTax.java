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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.jtethys.OceanusException;
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
     * Adjust Payees for taxCredit.
     * @param pTrans the transaction
     * @param pPayee the payee bucket
     */
    void adjustForPayeeDebitTaxCredit(final MoneyWiseXAnalysisTransaction pTrans,
                                      final MoneyWiseXAnalysisPayeeBucket pPayee) {
        /* Determine whether this is income or expense */
        final boolean isIncome = pTrans.isIncomeCategory();

        /* If we have taxCredit */
        final TethysMoney myTaxCredit = pTrans.getTransaction().getTaxCredit();
        if (myTaxCredit != null && myTaxCredit.isNonZero()) {
            // Add to Payee Income Bucket
            // Add to TaxMan Expense Bucket
            // Add to Category Income Bucket
            // Add to IncomeTax CatBucket
            // Add to TaxesPaid G Expense TaxBucket
            // Add to Category G+T Income TaxBucket
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
            // Add to Payee Income Bucket
            // Add to StatePensionIncome Asset Bucket
            // Add to EmployeeNI Income Bucket
            // Add to Category G Income TaxBucket
             if (isIncome) {
                pPayee.addIncome(myEmployerNI);
            } else {
                pPayee.addExpense(myEmployerNI);
            }
        }

        /* If we have employeeNI */
        final TethysMoney myEmployeeNI = pTrans.getTransaction().getEmployeeNatIns();
        if (myEmployeeNI != null && myEmployeeNI.isNonZero()) {
            // Add to Payee Income Bucket
            // Add to StatePensionIncome Asset Bucket
            // Add to EmployerNI Income Bucket
            // Add to TaxFree G Income TaxBucket
             if (isIncome) {
                pPayee.addIncome(myEmployeeNI);
            } else {
                pPayee.addExpense(myEmployeeNI);
            }
        }

        /* If we have deemedBenefit */
        final TethysMoney myBenefit = pTrans.getTransaction().getDeemedBenefit();
        if (myBenefit != null && myBenefit.isNonZero()) {
            // Add to Payee Income Bucket
            // Add to Payee Expense Bucket
            // Add to Benefit CatBucket
            // Add to Virtual Expense Bucket
            // Add to Category G Income TaxBucket
            if (isIncome) {
                pPayee.addIncome(myBenefit);
            } else {
                pPayee.addExpense(myBenefit);
            }
        }

        /* If we have withheld funds */
        final TethysMoney myWithheld = pTrans.getTransaction().getWithheld();
        if (myWithheld != null && myWithheld.isNonZero()) {
            // Add to Payee Income Bucket
            // Add to Payee Expense Bucket
            // Add to Category Income Bucket
            // Add to Virtual Expense Bucket
            // Add to Category G Income TaxBucket
            // Add to Virtual G Expense TaxBucket
            if (isIncome) {
                pPayee.addIncome(myWithheld);
                pPayee.addExpense(myWithheld);
            } else {
                pPayee.subtractIncome(myWithheld);
                pPayee.subtractExpense(myWithheld);
            }
        }
    }

    /**
     * Adjust tax basis buckets.
     * @param pTrans the transaction
     * @throws OceanusException on error
     */
    protected void adjustBasis(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException {
        /* Switch on the category type */
        switch (pTrans.getCategoryClass()) {
            case TAXEDINCOME:
            case GROSSINCOME:
                addIncome(pTrans, MoneyWiseTaxClass.SALARY);
                break;
            case OTHERINCOME:
                addIncome(pTrans, MoneyWiseTaxClass.OTHERINCOME);
                break;
            case INTEREST:
            case TAXEDINTEREST:
            case TAXEDLOYALTYBONUS:
                addIncome(pTrans, MoneyWiseTaxClass.TAXEDINTEREST);
                break;
            case GROSSINTEREST:
            case GROSSLOYALTYBONUS:
                addIncome(pTrans, MoneyWiseTaxClass.UNTAXEDINTEREST);
                break;
            case PEER2PEERINTEREST:
                addIncome(pTrans, MoneyWiseTaxClass.PEER2PEERINTEREST);
                break;
            case DIVIDEND:
            case SHAREDIVIDEND:
                addIncome(pTrans, MoneyWiseTaxClass.DIVIDEND);
                break;
            case UNITTRUSTDIVIDEND:
                addIncome(pTrans, MoneyWiseTaxClass.UNITTRUSTDIVIDEND);
                break;
            case FOREIGNDIVIDEND:
                addIncome(pTrans, MoneyWiseTaxClass.FOREIGNDIVIDEND);
                break;
            case RENTALINCOME:
                addIncome(pTrans, MoneyWiseTaxClass.RENTALINCOME);
                break;
            case ROOMRENTALINCOME:
                addIncome(pTrans, MoneyWiseTaxClass.ROOMRENTAL);
                break;
            case INCOMETAX:
                addExpense(pTrans, MoneyWiseTaxClass.TAXPAID);
                break;
            case TAXFREEINTEREST:
            case TAXFREEDIVIDEND:
            case LOANINTERESTEARNED:
            case INHERITED:
            case CASHBACK:
            case LOYALTYBONUS:
            case TAXFREELOYALTYBONUS:
            case GIFTEDINCOME:
                addIncome(pTrans, MoneyWiseTaxClass.TAXFREE);
                break;
            case PENSIONCONTRIB:
                addIncome(pTrans, MoneyWiseTaxClass.TAXFREE);
                break;
            case BADDEBTCAPITAL:
                addExpense(pTrans, MoneyWiseTaxClass.CAPITALGAINS);
                break;
            case BADDEBTINTEREST:
                addExpense(pTrans, MoneyWiseTaxClass.PEER2PEERINTEREST);
                break;
            case EXPENSE:
            case LOCALTAXES:
            case WRITEOFF:
            case LOANINTERESTCHARGED:
            case TAXRELIEF:
            case RECOVEREDEXPENSES:
                addExpense(pTrans, MoneyWiseTaxClass.EXPENSE);
                break;
            case RENTALEXPENSE:
                addExpense(pTrans, MoneyWiseTaxClass.RENTALINCOME);
                break;
            case UNITSADJUST:
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKRIGHTSISSUE:
            case PORTFOLIOXFER:
            case TRANSFER:
                break;
            default:
                throw new MoneyWiseLogicException("Unexpected Category: " + pTrans.getCategoryClass());
        }
    }

    /**
     * Add income taxBasis
     * @param pTrans the transaction
     * @param pBasis the taxBasis
     */
    private void addIncome(final MoneyWiseXAnalysisTransaction pTrans,
                           final MoneyWiseTaxClass pBasis) {

    }

    /**
     * Add expense taxBasis
     * @param pTrans the transaction
     * @param pBasis the taxBasis
     */
    private void addExpense(final MoneyWiseXAnalysisTransaction pTrans,
                            final MoneyWiseTaxClass pBasis) {

    }
}
