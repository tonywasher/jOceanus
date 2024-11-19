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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.analyse;

import net.sourceforge.joceanus.moneywise.MoneyWiseLogicException;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysis;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisPayeeBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisAccountBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTaxBasisBucket.MoneyWiseXAnalysisTaxBasisBucketList;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket;
import net.sourceforge.joceanus.moneywise.atlas.data.analysis.buckets.MoneyWiseXAnalysisTransCategoryBucket.MoneyWiseXAnalysisTransCategoryBucketList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTaxClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.tethys.OceanusException;
import net.sourceforge.joceanus.tethys.decimal.TethysMoney;

/**
 * Tax Analysis.
 */
public class MoneyWiseXAnalysisTax {
    /**
     * The analysis.
     */
    private final MoneyWiseXAnalysis theAnalysis;

    /**
     * The analysis state.
     */
    private final MoneyWiseXAnalysisState theState;

    /**
     * The taxMan account.
     */
    private final MoneyWiseXAnalysisPayeeBucket theTaxManPayee;

    /**
     * The taxCredit category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theTaxCreditCat;

    /**
     * The employeeNatIns category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theEmployeeNatInsCat;

    /**
     * The employerNatIns category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theEmployerNatInsCat;

    /**
     * The deemedBenefit category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theDeemedBenefitCat;

    /**
     * The withheld category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theWithheldCat;

    /**
     * The taxRelief category.
     */
    private final MoneyWiseXAnalysisTransCategoryBucket theTaxReliefCat;

    /**
     * The expense TaxBasis.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theExpenseTax;

    /**
     * The taxFree TaxBasis.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theTaxFreeTax;

    /**
     * The virtual TaxBasis.
     */
    private final MoneyWiseXAnalysisTaxBasisBucket theVirtualTax;

    /**
     * The XferIn analyser.
     */
    private MoneyWiseXAnalysisXferIn theXferIn;

    /**
     * Currently active Transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Currently active Payee Bucket.
     */
    private MoneyWiseXAnalysisPayeeBucket thePayeeBucket;

    /**
     * Currently active Transaction Category Bucket.
     */
    private MoneyWiseXAnalysisTransCategoryBucket theCategoryBucket;

    /**
     * Currently active TaxBucket.
     */
    private MoneyWiseXAnalysisTaxBasisBucket theTaxBucket;

    /**
     * Currently active Account.
     */
    private MoneyWiseTransAsset theAccount;

    /**
     * Constructor.
     * @param pAnalyser the analyser
     */
    MoneyWiseXAnalysisTax(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        /* Store the state */
        theAnalysis = pAnalyser.getAnalysis();
        theState = pAnalyser.getState();

        /* Store the taxMan Payee Bucket */
        theTaxManPayee = theAnalysis.getPayees().getBucket(MoneyWisePayeeClass.TAXMAN);

        /* Store the various Category buckets */
        final MoneyWiseXAnalysisTransCategoryBucketList myCategories = theAnalysis.getTransCategories();
        theTaxCreditCat = myCategories.getEventInfoBucket(MoneyWiseTransInfoClass.TAXCREDIT);
        theEmployeeNatInsCat = myCategories.getEventInfoBucket(MoneyWiseTransInfoClass.EMPLOYEENATINS);
        theEmployerNatInsCat = myCategories.getEventInfoBucket(MoneyWiseTransInfoClass.EMPLOYERNATINS);
        theDeemedBenefitCat = myCategories.getEventInfoBucket(MoneyWiseTransInfoClass.DEEMEDBENEFIT);
        theWithheldCat = myCategories.getEventInfoBucket(MoneyWiseTransInfoClass.WITHHELD);
        theTaxReliefCat = myCategories.getEventSingularBucket(MoneyWiseTransCategoryClass.TAXRELIEF);

        /* Store the various taxBuckets */
        final MoneyWiseXAnalysisTaxBasisBucketList myBases = theAnalysis.getTaxBasis();
        theTaxFreeTax = myBases.getBucket(MoneyWiseTaxClass.TAXFREE);
        theExpenseTax = myBases.getBucket(MoneyWiseTaxClass.EXPENSE);
        theVirtualTax = myBases.getBucket(MoneyWiseTaxClass.VIRTUAL);
    }

    /**
     * Declare the Security analyser
     * @param pSecurity the securityAnalyser
     */
    void declareSecurityAnalyser(final MoneyWiseXAnalysisSecurity pSecurity) {
        theXferIn = pSecurity.getXferInAnalyser();
    }

    /**
     * Adjust basis buckets.
     * @param pTrans the transaction
     */
    void adjustTaxBasis(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException {
        /* Record the transaction */
        recordTransaction(pTrans);

        /* If this is not a transfer */
        if (theTaxBucket != null) {
            /* Adjust for additional transactional elements */
            adjustForAdditionalTax();

            /* Adjust the Gross and Nett values */
            final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndNett(theAccount, pTrans.getTransactionValue());
            theState.registerBucketInterest(theTaxBucket);
            if (myAccBucket != null) {
                theState.registerBucketInterest(myAccBucket);
            }
        }

        /* Reset Payee bucket */
        thePayeeBucket = null;
    }

    /**
     * Record active payee bucket.
     * @param pPayee the payee bucket
     */
    void recordPayeeBucket(final MoneyWiseXAnalysisPayeeBucket pPayee) {
        thePayeeBucket = pPayee;
    }

    /**
     * Process an autoExpense amount.
     * @param pAmount the amount
     */
    void processAutoExpense(final TethysMoney pAmount) {
        /* Adjust the expense taxBasis by amount */
        theExpenseTax.adjustGrossAndNett(pAmount);
        theState.registerBucketInterest(theExpenseTax);
    }

    /**
     * Adjust for additional transaction elements.
     * @throws OceanusException on error
     */
    private void adjustForAdditionalTax() throws OceanusException {
        /* adjust for various additions */
        if (theTransaction.getCategory().isCategoryClass(MoneyWiseTransCategoryClass.LOANINTERESTCHARGED)) {
            adjustForTaxCredit();
        } else {
            adjustForTaxRelief();
        }
        adjustForEmployerNI();
        adjustForEmployeeNI();
        adjustForBenefit();
        adjustForWithheld();
    }

    /**
     * Adjust Buckets for taxCredit.
     * @throws OceanusException on error
     */
    private void adjustForTaxCredit() throws OceanusException {
        /* If we have taxCredit */
        TethysMoney myTaxCredit = theTransaction.getTransaction().getTaxCredit();
        if (myTaxCredit != null && myTaxCredit.isNonZero()) {
            /* Determine whether this is income or expense */
            final boolean isIncome = theTransaction.isIncomeCategory();

            /* check validity of transaction */
            checkForValidAdditional();

            /* If this is a refund, negate the taxCredit */
            if (theTransaction.isRefund()) {
                myTaxCredit = new TethysMoney(myTaxCredit);
                myTaxCredit.negate();
            }

            /* If this is an income */
            if (isIncome) {
                /* CashFlow is Income from Payee and Expense to taxMan */
                thePayeeBucket.addIncome(myTaxCredit);
                theTaxManPayee.addExpense(myTaxCredit);

                /* Income from category and Expense to taxCredit */
                theCategoryBucket.addIncome(myTaxCredit);
                theTaxCreditCat.addExpense(myTaxCredit);

                /* Register income and expense for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndTax(theAccount, myTaxCredit);
                theExpenseTax.adjustGrossAndNett(myTaxCredit);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }

                /* else this is expense */
            } else {
                /* CashFlow is Expense from Payee and Income from taxMan */
                thePayeeBucket.addExpense(myTaxCredit);
                theTaxManPayee.addIncome(myTaxCredit);

                /* Expense to category and Income from taxCredit */
                theCategoryBucket.addExpense(myTaxCredit);
                theTaxCreditCat.addIncome(myTaxCredit);

                /* Register income and expense for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndTax(theAccount, myTaxCredit);
                theExpenseTax.adjustGrossAndNett(myTaxCredit);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }
            }

            /* Register the various interests */
            theState.registerBucketInterest(theTaxManPayee);
            theState.registerBucketInterest(theTaxCreditCat);
            theState.registerBucketInterest(theExpenseTax);
        }
    }

    /**
     * Adjust Buckets for taxCredit.
     * @throws OceanusException on error
     */
    private void adjustForTaxRelief() throws OceanusException {
        /* If we have taxCredit */
        TethysMoney myTaxCredit = theTransaction.getTransaction().getTaxCredit();
        if (myTaxCredit != null && myTaxCredit.isNonZero()) {
            /* Determine whether this is income or expense */
            final boolean isIncome = theTransaction.isIncomeCategory();

            /* check validity of transaction */
            checkForValidAdditional();

            /* If this is a refund, negate the taxCredit */
            if (theTransaction.isRefund()) {
                myTaxCredit = new TethysMoney(myTaxCredit);
                myTaxCredit.negate();
            }

            /* If this is an income */
            if (isIncome) {
                /* CashFlow is Income from Payee and Expense to taxMan */
                thePayeeBucket.addIncome(myTaxCredit);
                theTaxManPayee.addExpense(myTaxCredit);

                /* Income from category and Expense to taxCredit */
                theCategoryBucket.addIncome(myTaxCredit);
                theTaxCreditCat.addExpense(myTaxCredit);

                /* Register income and expense for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndTax(theAccount, myTaxCredit);
                theExpenseTax.adjustGrossAndNett(myTaxCredit);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }

                /* else this is expense */
            } else {
                /* CashFlow is Expense from Payee and Income from taxMan */
                thePayeeBucket.addExpense(myTaxCredit);
                theTaxManPayee.addIncome(myTaxCredit);

                /* Expense to category and Income from taxCredit */
                theCategoryBucket.addExpense(myTaxCredit);
                theTaxCreditCat.addIncome(myTaxCredit);

                /* Register income and expense for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndTax(theAccount, myTaxCredit);
                theExpenseTax.adjustGrossAndNett(myTaxCredit);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }
            }

            /* Register the various interests */
            theState.registerBucketInterest(theTaxManPayee);
            theState.registerBucketInterest(theTaxCreditCat);
            theState.registerBucketInterest(theExpenseTax);
        }
    }

    /**
     * Adjust Buckets for employeeNI.
     * @throws OceanusException on error
     */
    private void adjustForEmployeeNI() throws OceanusException {
        /* If we have NatIns */
        TethysMoney myNatIns = theTransaction.getTransaction().getEmployeeNatIns();
        if (myNatIns != null && myNatIns.isNonZero()) {
            /* Determine whether this is income or expense */
            final boolean isIncome = theTransaction.isIncomeCategory();
            final boolean isPension = theTransaction.getCategory().isCategoryClass(MoneyWiseTransCategoryClass.PENSIONCONTRIB);
            final MoneyWiseXAnalysisTransCategoryBucket myCatBucket = isPension ? theCategoryBucket : theEmployeeNatInsCat;

            /* check validity of transaction */
            checkForValidAdditional();

            /* If this is a refund, negate the withheld */
            if (theTransaction.isRefund()) {
                myNatIns = new TethysMoney(myNatIns);
                myNatIns.negate();
            }

            /* If this is an income */
            if (isIncome) {
                /* CashFlow is Income from Payee */
                thePayeeBucket.addIncome(myNatIns);
                theXferIn.processStatePensionContribution(myNatIns);

                /* Income from EeNatIns */
                myCatBucket.addIncome(myNatIns);

                /* Income for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndNett(theAccount, myNatIns);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }

                /* else this is expense */
            } else {
                /* CashFlow is to Payee */
                thePayeeBucket.addExpense(myNatIns);

                /* Expense to category and Income from virtual */
                myCatBucket.addExpense(myNatIns);

                /* Register income for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndNett(theAccount, myNatIns);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }
            }
        }
    }


    /**
     * Adjust Buckets for employerNI.
     * @throws OceanusException on error
     */
    private void adjustForEmployerNI() throws OceanusException {
        /* If we have NatIns */
        TethysMoney myNatIns = theTransaction.getTransaction().getEmployerNatIns();
        if (myNatIns != null && myNatIns.isNonZero()) {
            /* Determine whether this is income or expense */
            final boolean isIncome = theTransaction.isIncomeCategory();
            final boolean isPension = theTransaction.getCategory().isCategoryClass(MoneyWiseTransCategoryClass.PENSIONCONTRIB);
            final MoneyWiseXAnalysisTransCategoryBucket myCatBucket = isPension ? theCategoryBucket : theEmployerNatInsCat;

            /* check validity of transaction */
            checkForValidAdditional();

            /* If this is a refund, negate the withheld */
            if (theTransaction.isRefund()) {
                myNatIns = new TethysMoney(myNatIns);
                myNatIns.negate();
            }

            /* If this is an income */
            if (isIncome) {
                /* CashFlow is Income from Payee */
                thePayeeBucket.addIncome(myNatIns);
                theXferIn.processStatePensionContribution(myNatIns);

                /* Income from ErNatIns */
                myCatBucket.addIncome(myNatIns);

                /* Income for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxFreeTax.adjustGrossAndNett(theAccount, myNatIns);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }

                /* else this is expense */
            } else {
                /* CashFlow is to Payee */
                thePayeeBucket.addExpense(myNatIns);

                /* Expense to category and Income from virtual */
                myCatBucket.addExpense(myNatIns);

                /* Register income for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxFreeTax.adjustGrossAndNett(theAccount, myNatIns);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }
            }

            /* Register the various interests */
            theState.registerBucketInterest(theEmployerNatInsCat);
            theState.registerBucketInterest(theTaxFreeTax);
        }
    }

    /**
     * Adjust Buckets for benefit.
     * @throws OceanusException on error
     */
    private void adjustForBenefit() throws OceanusException {
        /* If we have benefit */
        TethysMoney myBenefit = theTransaction.getTransaction().getDeemedBenefit();
        if (myBenefit != null && myBenefit.isNonZero()) {
            /* Determine whether this is income or expense */
            final boolean isIncome = theTransaction.isIncomeCategory();

            /* check validity of transaction */
            checkForValidAdditional();

            /* If this is a refund, negate the withheld */
            if (theTransaction.isRefund()) {
                myBenefit = new TethysMoney(myBenefit);
                myBenefit.negate();
            }

            /* If this is an income */
            if (isIncome) {
                /* CashFlow is Income from Payee and Expense back to Payee */
                thePayeeBucket.addIncome(myBenefit);
                thePayeeBucket.addExpense(myBenefit);

                /* Income from Benefit and Expense to withheld */
                theDeemedBenefitCat.addIncome(myBenefit);
                theWithheldCat.addExpense(myBenefit);

                /* Register income and expense for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndNett(theAccount, myBenefit);
                theVirtualTax.adjustGrossAndNett(myBenefit);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }

                /* else this is expense */
            } else {
                /* CashFlow is Expense from Payee and Income from taxMan */
                thePayeeBucket.addIncome(myBenefit);
                thePayeeBucket.addExpense(myBenefit);

                /* Expense to benefit and Income from withheld */
                theDeemedBenefitCat.addExpense(myBenefit);
                theWithheldCat.addIncome(myBenefit);

                /* Register income and expense for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndNett(theAccount, myBenefit);
                theVirtualTax.adjustGrossAndNett(myBenefit);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }
            }

            /* Register the various interests */
            theState.registerBucketInterest(theDeemedBenefitCat);
            theState.registerBucketInterest(theVirtualTax);
        }
    }

    /**
     * Adjust Buckets for withheld.
     * @throws OceanusException on error
     */
    private void adjustForWithheld() throws OceanusException {
        /* If we have withheld */
        TethysMoney myWithheld = theTransaction.getTransaction().getWithheld();
        if (myWithheld != null && myWithheld.isNonZero()) {
            /* Determine whether this is income or expense */
            final boolean isIncome = theTransaction.isIncomeCategory();

            /* check validity of transaction */
            checkForValidAdditional();

            /* If this is a refund, negate the withheld */
            if (theTransaction.isRefund()) {
                myWithheld = new TethysMoney(myWithheld);
                myWithheld.negate();
            }

            /* If this is an income */
            if (isIncome) {
                /* CashFlow is Income from Payee and Expense back to Payee */
                thePayeeBucket.addIncome(myWithheld);
                thePayeeBucket.addExpense(myWithheld);

                /* Income from category and Expense to virtual */
                theCategoryBucket.addIncome(myWithheld);
                theWithheldCat.addExpense(myWithheld);

                /* Register income and expense for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndNett(theAccount, myWithheld);
                theVirtualTax.adjustGrossAndNett(myWithheld);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }

                /* else this is expense */
            } else {
                /* CashFlow is Expense from Payee and Income from taxMan */
                thePayeeBucket.addIncome(myWithheld);
                thePayeeBucket.addExpense(myWithheld);

                /* Expense to category and Income from virtual */
                theCategoryBucket.addExpense(myWithheld);
                theWithheldCat.addIncome(myWithheld);

                /* Register income and expense for tax */
                final MoneyWiseXAnalysisTaxBasisAccountBucket myAccBucket = theTaxBucket.adjustGrossAndNett(theAccount, myWithheld);
                theVirtualTax.adjustGrossAndNett(myWithheld);
                if (myAccBucket != null) {
                    theState.registerBucketInterest(myAccBucket);
                }
            }

            /* Register the various interests */
            theState.registerBucketInterest(theWithheldCat);
            theState.registerBucketInterest(theVirtualTax);
        }
    }

    /**
     * Check validity of additional items.
     * @throws OceanusException on error
     */
    private void checkForValidAdditional() throws OceanusException {
        if (theTaxBucket == null || thePayeeBucket == null) {
            throw new MoneyWiseLogicException("Invalid additional items on transaction");
        }
    }

    /**
     * Record the account for a transaction.
     * @param pTrans the transaction
     * @throws OceanusException on error
     */
    private void recordTransaction(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException {
        /* Determine the taxBucket */
        theTaxBucket = determineTaxBasicBucket(pTrans);

        /* Record transaction and account */
        theTransaction = pTrans;
        theAccount = pTrans.getTransaction().getAccount();

        /* Record the category bucket */
        final MoneyWiseTransCategory myCategory = theTransaction.getCategory();
        theCategoryBucket = theAnalysis.getTransCategories().getBucket(myCategory);
    }

    /**
     * Obtain tax basis bucket for transaction.
     * @param pTrans the transaction
     * @return the taxBasis bucket
     * @throws OceanusException on error
     */
    private MoneyWiseXAnalysisTaxBasisBucket determineTaxBasicBucket(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException {
        final MoneyWiseTaxClass myClass = determineTaxClass(pTrans);
        return myClass == null ? null : theAnalysis.getTaxBasis().getBucket(myClass);
    }

    /**
     * Obtain tax class for transaction.
     * @param pTrans the transaction
     * @return the taxClass
     * @throws OceanusException on error
     */
    private MoneyWiseTaxClass determineTaxClass(final MoneyWiseXAnalysisTransaction pTrans) throws OceanusException {
        if (pTrans == null) {
            int i = 0;
        }
        /* Switch on the category type */
        switch (pTrans.getCategoryClass()) {
            case TAXEDINCOME:
            case GROSSINCOME:
                return MoneyWiseTaxClass.SALARY;
            case OTHERINCOME:
                return MoneyWiseTaxClass.OTHERINCOME;
            case INTEREST:
            case TAXEDINTEREST:
            case TAXEDLOYALTYBONUS:
                return MoneyWiseTaxClass.TAXEDINTEREST;
            case GROSSINTEREST:
            case GROSSLOYALTYBONUS:
                return MoneyWiseTaxClass.UNTAXEDINTEREST;
            case PEER2PEERINTEREST:
                return MoneyWiseTaxClass.PEER2PEERINTEREST;
            case DIVIDEND:
            case SHAREDIVIDEND:
                return MoneyWiseTaxClass.DIVIDEND;
            case UNITTRUSTDIVIDEND:
                return MoneyWiseTaxClass.UNITTRUSTDIVIDEND;
            case FOREIGNDIVIDEND:
                return MoneyWiseTaxClass.FOREIGNDIVIDEND;
            case RENTALINCOME:
                return MoneyWiseTaxClass.RENTALINCOME;
            case ROOMRENTALINCOME:
                return MoneyWiseTaxClass.ROOMRENTAL;
            case INCOMETAX:
                return MoneyWiseTaxClass.TAXPAID;
            case TAXFREEINTEREST:
            case TAXFREEDIVIDEND:
            case LOANINTERESTEARNED:
            case INHERITED:
            case CASHBACK:
            case LOYALTYBONUS:
            case TAXFREELOYALTYBONUS:
            case GIFTEDINCOME:
                return MoneyWiseTaxClass.TAXFREE;
            case PENSIONCONTRIB:
                return MoneyWiseTaxClass.TAXFREE;
            case BADDEBTCAPITAL:
                return MoneyWiseTaxClass.CAPITALGAINS;
            case BADDEBTINTEREST:
                return MoneyWiseTaxClass.PEER2PEERINTEREST;
            case EXPENSE:
            case LOCALTAXES:
            case WRITEOFF:
            case LOANINTERESTCHARGED:
            case TAXRELIEF:
            case RECOVEREDEXPENSES:
                return MoneyWiseTaxClass.EXPENSE;
            case RENTALEXPENSE:
                return MoneyWiseTaxClass.RENTALINCOME;
            case UNITSADJUST:
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKRIGHTSISSUE:
            case PORTFOLIOXFER:
            case TRANSFER:
                return null;
            default:
                throw new MoneyWiseLogicException("Unexpected Category: " + pTrans.getCategoryClass());
        }
    }
}
