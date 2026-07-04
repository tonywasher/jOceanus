/*
 * MoneyWise: Finance Application
 * Copyright 2026. Tony Washer
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
 */

package io.github.tonywasher.joceanus.moneywise.quicken.builder.atlas;

import io.github.tonywasher.joceanus.moneywise.analysis.atlas.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.analysis.atlas.buckets.MoneyWiseXAnalysis;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseCash;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWisePayeeClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import io.github.tonywasher.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQIFType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccountEvents;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFClass;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventCategory;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPayee;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFRegister;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Builder class for QIF File.
 */
public class MoneyWiseXQIFBuilder
        implements MoneyWiseXQIFHelper {
    /**
     * Quicken Transfer.
     */
    private static final String XQIF_XFER = "Transfer";

    /**
     * Quicken Transfer from.
     */
    private static final String XQIF_XFERFROM = " from ";

    /**
     * Quicken Transfer to.
     */
    private static final String XQIF_XFERTO = " to ";

    /**
     * The QIF Register.
     */
    private final MoneyWiseXQIFRegister theRegister;

    /**
     * The QIF File Type.
     */
    private final MoneyWiseQIFType theFileType;

    /**
     * The QIF Portfolio Builder.
     */
    private final MoneyWiseXQIFPortfolioBuilder thePortBuilder;

    /**
     * The TaxMan payee.
     */
    private final MoneyWisePayee theTaxMan;

    /**
     * The TaxCredit category.
     */
    private final MoneyWiseTransCategory theTaxCategory;

    /**
     * The Opening category.
     */
    private final MoneyWiseTransCategory theOpeningCategory;

    /**
     * The Build Transfer engine.
     */
    private final MoneyWiseXQIFBuildXfer theBuildXfer;

    /**
     * The Build Cash engine.
     */
    private final MoneyWiseXQIFBuildCash theBuildCash;

    /**
     * The Build Income engine.
     */
    private final MoneyWiseXQIFBuildIncome theBuildIncome;

    /**
     * The Build Expense engine.
     */
    private final MoneyWiseXQIFBuildExpense theBuildExpense;

    /**
     * Constructor.
     *
     * @param pRegister the QIF Register
     * @param pData     the data
     * @param pAnalysis the analysis
     */
    protected MoneyWiseXQIFBuilder(final MoneyWiseXQIFRegister pRegister,
                                   final MoneyWiseDataSet pData,
                                   final MoneyWiseXAnalysis pAnalysis) {
        /* Store parameters */
        theRegister = pRegister;
        theFileType = pRegister.getFileType();

        /* Create portfolio builder */
        thePortBuilder = new MoneyWiseXQIFPortfolioBuilder(this, pData, pAnalysis);

        /* Store Tax account */
        final MoneyWisePayeeList myPayees = pData.getPayees();
        theTaxMan = myPayees.getSingularClass(MoneyWisePayeeClass.TAXMAN);

        /* Store categories */
        final MoneyWiseTransCategoryList myCategories = pData.getTransCategories();
        theTaxCategory = myCategories.getEventInfoCategory(MoneyWiseTransInfoClass.TAXCREDIT);
        theOpeningCategory = myCategories.getSingularClass(MoneyWiseTransCategoryClass.OPENINGBALANCE);

        /* Create subBuilders */
        theBuildXfer = new MoneyWiseXQIFBuildXfer(this);
        theBuildCash = new MoneyWiseXQIFBuildCash(this);
        theBuildIncome = new MoneyWiseXQIFBuildIncome(pData, this);
        theBuildExpense = new MoneyWiseXQIFBuildExpense(pData, this);
    }

    @Override
    public MoneyWiseXQIFRegister getRegister() {
        return theRegister;
    }

    @Override
    public MoneyWiseXQIFEventCategory getTaxCategory() {
        return theRegister.registerCategory(theTaxCategory);
    }

    @Override
    public MoneyWiseXQIFPayee getTaxMan() {
        return theRegister.registerPayee(theTaxMan);
    }

    /**
     * Process event.
     *
     * @param pTrans the transaction
     */
    protected void processEvent(final MoneyWiseXAnalysisEvent pTrans) {
        /* Access account and partner */
        final MoneyWiseTransAsset myAccount = pTrans.getAccount();
        final MoneyWiseTransAsset myPartner = pTrans.getPartner();
        final boolean bFrom = pTrans.getDirection().isFrom();

        /* If this deals with a payee */
        if (myPartner instanceof MoneyWisePayee myPayee) {
            /* If this is expense */
            if (bFrom) {
                /* Process Debit Payee */
                processDebitPayee(myPayee, myAccount, pTrans);
            } else {
                /* Process Credit Payee */
                processCreditPayee(myPayee, myAccount, pTrans);
            }

        } else if (bFrom) {
            /* else process Transfer Partner -> Account */
            processTransfer(myPartner, myAccount, pTrans);
        } else {
            /* else process Transfer Account -> Partner */
            processTransfer(myAccount, myPartner, pTrans);
        }
    }

    /**
     * Process opening balance.
     *
     * @param pAccount   the account
     * @param pStartDate the start date
     * @param pBalance   the opening balance
     */
    protected void processBalance(final MoneyWiseTransAsset pAccount,
                                  final OceanusDate pStartDate,
                                  final OceanusMoney pBalance) {
        /* Access the Account details */
        final MoneyWiseXQIFAccountEvents myAccount = theRegister.registerAccount(pAccount);

        /* Create the event */
        final MoneyWiseXQIFEvent myEvent = new MoneyWiseXQIFEvent(theRegister, pStartDate);
        myEvent.recordAmount(pBalance);

        /* If we are using self-Opening balance */
        if (theFileType.selfOpeningBalance()) {
            /* Record self reference */
            myEvent.recordAccount(myAccount.getAccount());

            /* else use an event */
        } else {
            /* Register category */
            final MoneyWiseXQIFEventCategory myCategory = theRegister.registerCategory(theOpeningCategory);
            myEvent.recordCategory(myCategory);
        }

        /* Add event to event list */
        myAccount.addEvent(myEvent);
    }

    /**
     * Process debit payee event.
     *
     * @param pPayee  the payee
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    private void processDebitPayee(final MoneyWisePayee pPayee,
                                   final MoneyWiseTransAsset pCredit,
                                   final MoneyWiseXAnalysisEvent pTrans) {
        /* If this is a cash recovery */
        if (pCredit instanceof MoneyWiseCash myCash
                && myCash.isAutoExpense()) {
            /* process as cash recovery */
            theBuildCash.processCashRecovery(pPayee, myCash, pTrans);

            /* If this is an income to a security */
        } else if (pCredit instanceof MoneyWiseSecurityHolding myHolding) {
            /* process as income to security */
            thePortBuilder.processIncomeToSecurity(pPayee, myHolding, pTrans);

            /* If this is an income to a portfolio */
        } else if (pCredit instanceof MoneyWisePortfolio myPortfolio) {
            /* process as income to portfolio */
            thePortBuilder.processIncomeToPortfolio(pPayee, myPortfolio, pTrans);

            /* else if we have additional detail */
        } else if (hasXtraDetail(pTrans)) {
            /* process as detailed income */
            theBuildIncome.processDetailedIncome(pPayee, pCredit, pTrans);

        } else {
            /* process as standard income */
            theBuildIncome.processStandardIncome(pPayee, pCredit, pTrans);
        }
    }

    /**
     * Process credit payee event.
     *
     * @param pPayee the payee
     * @param pDebit the debit account
     * @param pTrans the transaction
     */
    private void processCreditPayee(final MoneyWisePayee pPayee,
                                    final MoneyWiseTransAsset pDebit,
                                    final MoneyWiseXAnalysisEvent pTrans) {
        /* If this is a cash payment */
        if (pDebit instanceof MoneyWiseCash myCash
                && myCash.isAutoExpense()) {
            /* process as cash payment */
            theBuildCash.processCashPayment(pPayee, myCash, pTrans);

            /* If this is an expense from a security */
        } else if (pDebit instanceof MoneyWiseSecurityHolding myHolding) {
            /* process as expense from security */
            thePortBuilder.processExpenseFromSecurity(pPayee, myHolding, pTrans);

            /* If this is an expense from a portfolio */
        } else if (pDebit instanceof MoneyWisePortfolio myPortfolio) {
            /* process as expense from portfolio */
            thePortBuilder.processExpenseFromPortfolio(pPayee, myPortfolio, pTrans);

            /* else if we have additional detail */
        } else if (hasXtraDetail(pTrans)) {
            /* process as detailed income */
            theBuildExpense.processDetailedExpense(pPayee, pDebit, pTrans);

        } else {
            /* process as standard expense */
            theBuildExpense.processStandardExpense(pPayee, pDebit, pTrans);
        }
    }

    /**
     * Process transfer event.
     *
     * @param pDebit  the debit account
     * @param pCredit the credit account
     * @param pTrans  the transaction
     */
    private void processTransfer(final MoneyWiseTransAsset pDebit,
                                 final MoneyWiseTransAsset pCredit,
                                 final MoneyWiseXAnalysisEvent pTrans) {
        /* If this is a cash AutoExpense */
        if (pCredit instanceof MoneyWiseCash myCash
                && myCash.isAutoExpense()) {
            /* Process as standard expense */
            theBuildCash.processCashExpense(myCash, pDebit, pTrans);

            /* If this is a cash AutoReceipt */
        } else if (pDebit instanceof MoneyWiseCash myCash
                && myCash.isAutoExpense()) {
            /* Process as standard expense */
            theBuildCash.processCashReceipt(myCash, pCredit, pTrans);

            /* If this is a transfer from a security */
        } else if (pDebit instanceof MoneyWiseSecurityHolding myDebitHolding) {
            /* Handle transfer between securities */
            if (pCredit instanceof MoneyWiseSecurityHolding myCreditHolding) {
                /* process as transfer between securities */
                thePortBuilder.processTransferBetweenSecurities(myDebitHolding, myCreditHolding, pTrans);
            } else {
                /* process as transfer from security */
                thePortBuilder.processTransferFromSecurity(myDebitHolding, pCredit, pTrans);
            }
            /* If this is a transfer to a security */
        } else if (pCredit instanceof MoneyWiseSecurityHolding myCreditHolding) {
            /* process as transfer to security */
            thePortBuilder.processTransferToSecurity(myCreditHolding, pDebit, pTrans);

            /* If this is a transfer from a portfolio */
        } else if (pDebit instanceof MoneyWisePortfolio myDebitPortfolio) {
            /* Handle transfer between securities */
            if (pCredit instanceof MoneyWisePortfolio myCreditPortfolio) {
                /* process as transfer between portfolios */
                thePortBuilder.processTransferBetweenPortfolios(myDebitPortfolio, myCreditPortfolio, pTrans);
            } else {
                /* process as transfer from portfolio */
                thePortBuilder.processTransferFromPortfolio(myDebitPortfolio, pCredit, pTrans);
            }
            /* If this is a transfer to a portfolio */
        } else if (pCredit instanceof MoneyWisePortfolio myCreditPortfolio) {
            /* process as transfer to portfolio */
            thePortBuilder.processTransferToPortfolio(myCreditPortfolio, pDebit, pTrans);

        } else {
            /* Access details */
            final MoneyWiseTransCategoryClass myCat = Objects.requireNonNull(pTrans.getCategory().getCategoryTypeClass());

            /* Switch on category class */
            switch (myCat) {
                case CASHBACK:
                    /* Process as cashBack payment */
                    theBuildIncome.processCashBack(pDebit, pCredit, pTrans);
                    break;
                case INTEREST, LOYALTYBONUS:
                    /* Process as interest payment */
                    theBuildIncome.processInterest(pDebit, pCredit, pTrans);
                    break;
                case LOANINTERESTEARNED, RENTALINCOME, ROOMRENTALINCOME:
                    /* Process as income from parent of the credit */
                    theBuildIncome.processStandardIncome((MoneyWisePayee) pCredit.getParent(), pCredit, pTrans);
                    break;
                case WRITEOFF, LOANINTERESTCHARGED:
                    /* Process as expense to parent of the credit (recursive) */
                    theBuildExpense.processStandardExpense((MoneyWisePayee) pCredit.getParent(), pDebit, pTrans);
                    break;
                default:
                    /* Process as standard transfer */
                    theBuildXfer.processStandardTransfer(pDebit, pCredit, pTrans);
                    break;
            }
        }
    }

    @Override
    public boolean hasXtraDetail(final MoneyWiseXAnalysisEvent pTrans) {
        if (pTrans.getTaxCredit() != null) {
            return true;
        }
        if (pTrans.getEmployeeNatIns() != null) {
            return true;
        }
        if (pTrans.getDeemedBenefit() != null) {
            return true;
        }
        return pTrans.getWithheld() != null;
    }

    @Override
    public String buildXferFromPayee(final MoneyWiseTransAsset pPartner) {
        /* Determine mode */
        final boolean useSimpleTransfer = theFileType.useSimpleTransfer();

        /* Build payee description */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(XQIF_XFER);
        if (!useSimpleTransfer) {
            myBuilder.append(XQIF_XFERFROM);
            myBuilder.append(pPartner.getName());
        }

        /* Return the payee */
        return myBuilder.toString();
    }

    @Override
    public String buildXferToPayee(final MoneyWiseTransAsset pPartner) {
        /* Determine mode */
        final boolean useSimpleTransfer = theFileType.useSimpleTransfer();

        /* Build payee description */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(XQIF_XFER);
        if (!useSimpleTransfer) {
            myBuilder.append(XQIF_XFERTO);
            myBuilder.append(pPartner.getName());
        }

        /* Return the payee */
        return myBuilder.toString();
    }

    @Override
    public List<MoneyWiseXQIFClass> getTransactionClasses(final MoneyWiseXAnalysisEvent pTrans) {
        /* Create return value */
        List<MoneyWiseXQIFClass> myList = null;

        /* Obtain the tags for the transaction */
        final List<MoneyWiseTransTag> myTags = pTrans.getTransactionTags();

        /* If we have tags */
        if (myTags != null) {
            /* Allocate the list */
            myList = new ArrayList<>();

            /* Loop through the tags */
            for (MoneyWiseTransTag myTag : myTags) {
                /* Access the transaction tag */
                final MoneyWiseXQIFClass myClass = theRegister.registerClass(myTag);

                /* Add to the list */
                myList.add(myClass);
            }
        }

        /* Return the list */
        return myList;
    }
}
