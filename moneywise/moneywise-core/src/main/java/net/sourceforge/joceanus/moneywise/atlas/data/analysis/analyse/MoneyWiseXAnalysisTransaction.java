/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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

import net.sourceforge.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Transaction details.
 */
public class MoneyWiseXAnalysisTransaction {
    /**
     * The event.
     */
    private final MoneyWiseXAnalysisEvent theEvent;

    /**
     * The transaction.
     */
    private final MoneyWiseTransaction theTrans;

    /**
     * The direction.
     */
    private boolean isTo;

    /**
     * The debit amount.
     */
    private OceanusMoney theDebitAmount;

    /**
     * The credit amount.
     */
    private OceanusMoney theCreditAmount;

    /**
     * The debit account.
     */
    private MoneyWiseTransAsset theDebit;

    /**
     * The credit account.
     */
    private MoneyWiseTransAsset theCredit;

    /**
     * The child asset.
     */
    private MoneyWiseTransAsset theChild;

    /**
     * The category.
     */
    private MoneyWiseTransCategory theCategory;

    /**
     * The debitUnitsDelta.
     */
    private final OceanusUnits theDebitUnitsDelta;

    /**
     * The creditUnitsDelta.
     */
    private final OceanusUnits theCreditUnitsDelta;

    /**
     * Constructor.
     * @param pEvent the event
     */
    MoneyWiseXAnalysisTransaction(final MoneyWiseXAnalysisEvent pEvent) {
        /* Store the base */
        theEvent = pEvent;
        theTrans = theEvent.getTransaction();

        /* Access account and partner */
        isTo = MoneyWiseAssetDirection.TO.equals(theTrans.getDirection());
        final MoneyWiseTransAsset myAccount = theTrans.getAccount();
        MoneyWiseTransAsset myPartner = theTrans.getPartner();
        myPartner = myPartner == null ? myAccount : myPartner;
        theDebit = isTo ? myAccount : myPartner;
        theCredit = isTo ? myPartner : myAccount;

        /* Store the category */
        theCategory = theTrans.getCategory();

        /* Sort out amounts */
        OceanusMoney myAmount = theTrans.getAmount();
        myAmount = myAmount == null ? new OceanusMoney() : myAmount;
        OceanusMoney myPartnerAmount = theTrans.getPartnerAmount();
        myPartnerAmount = myPartnerAmount == null ? myAmount : myPartnerAmount;
        if (isTo) {
            theDebitAmount = new OceanusMoney(myAmount);
            theCreditAmount = myPartnerAmount;
        } else {
            theDebitAmount = new OceanusMoney(myPartnerAmount);
            theCreditAmount = myAmount;
        }
        theDebitAmount.negate();

        /* Access delta units */
        theDebitUnitsDelta = isTo ? theTrans.getAccountDeltaUnits() : theTrans.getPartnerDeltaUnits();
        theCreditUnitsDelta = isTo ? theTrans.getPartnerDeltaUnits() : theTrans.getAccountDeltaUnits();
    }

    /**
     * Obtain the event.
     * @return the event
     */
    MoneyWiseXAnalysisEvent getEvent() {
        return theEvent;
    }

    /**
     * Obtain the transaction.
     * @return the transaction
     */
    MoneyWiseTransaction getTransaction() {
        return theTrans;
    }

    /**
     * Is this transaction Account -> partner?
     * @return true/false
     */
    boolean isTo() {
        return isTo;
    }

    /**
     * Obtain the debit account.
     * @return the debit account
     */
    MoneyWiseTransAsset getDebitAccount() {
        return theDebit;
    }

    /**
     * Obtain the credit account.
     * @return the credit account
     */
    MoneyWiseTransAsset getCreditAccount() {
        return theCredit;
    }

    /**
     * Obtain the child account.
     * @return the child account
     */
    public MoneyWiseTransAsset getChildAccount() {
        return theChild;
    }

    /**
     * Obtain the category.
     * @return the category
     */
    MoneyWiseTransCategory getCategory() {
        return theCategory;
    }

    /**
     * Obtain the categoryClass.
     * @return the categoryClass
     */
    MoneyWiseTransCategoryClass getCategoryClass() {
        return theCategory == null ? null : theCategory.getCategoryTypeClass();
    }

    /**
     * Obtain the debit unitsDelta.
     * @return the delta
     */
    OceanusUnits getDebitUnitsDelta() {
        return theDebitUnitsDelta;
    }

    /**
     * Obtain the credit unitsDelta.
     * @return the delta
     */
    OceanusUnits getCreditUnitsDelta() {
        return theCreditUnitsDelta;
    }

    /**
     * is this an income Category?
     * @return true/false
     */
    boolean isIncomeCategory() {
        final MoneyWiseTransCategoryClass myClass = getCategoryClass();
        return myClass != null && myClass.isIncome();
    }

    /**
     * is this an expense Category?
     * @return true/false
     */
    boolean isExpenseCategory() {
        final MoneyWiseTransCategoryClass myClass = getCategoryClass();
        return myClass != null && myClass.isExpense();
    }

    /**
     * is this a refund?
     * @return true/false
     */
    boolean isRefund() {
        return isIncomeCategory() ? theCredit instanceof MoneyWisePayee : theDebit instanceof MoneyWisePayee;
    }

    /**
     * Obtain the transaction value.
     * @return the value
     */
    OceanusMoney getTransactionValue() {
        return isRefund() ? theDebitAmount : theCreditAmount;
    }

    /**
     * Obtain the debit amount.
     * @return the debit amount
     */
    OceanusMoney getDebitAmount() {
        return theDebitAmount;
    }

    /**
     * Obtain the credit amount.
     * @return the credit amount
     */
    OceanusMoney getCreditAmount() {
        return theCreditAmount;
    }

    /**
     * Set the debit amount.
     * @param pAmount the amount
     */
    void setDebitAmount(final OceanusMoney pAmount) {
        theDebitAmount = pAmount;
        if (!theCredit.getAssetType().isValued()) {
            theCreditAmount = new OceanusMoney(pAmount);
            theCreditAmount.negate();
        }
    }

    /**
     * Set the credit amount.
     * @param pAmount the amount
     */
    void setCreditAmount(final OceanusMoney pAmount) {
        theCreditAmount = pAmount;
        if (!theDebit.getAssetType().isValued()) {
            theDebitAmount = new OceanusMoney(pAmount);
            theDebitAmount.negate();
        }
    }

    /**
     * Adjust parent/child.
     */
    void adjustParent() {
        /* Switch on category class */
        switch (getCategoryClass()) {
            case INTEREST:
            case LOYALTYBONUS:
            case DIVIDEND:
                /* Obtain detailed category */
                theCategory = ((MoneyWiseAssetBase) theDebit).getDetailedCategory(theCategory, theTrans.getTaxYear());

                /* The interest bearing account should be replaced by its parent */
                theChild = theDebit.equals(theCredit)
                        ? null
                        : theTrans.getAccount();
                if (isTo) {
                    theDebit = theDebit.getParent();
                } else {
                    theCredit = theCredit.getParent();
                }
                isTo = !isTo;
                break;
            case LOANINTERESTEARNED:
            case CASHBACK:
            case WRITEOFF:
            case LOANINTERESTCHARGED:
                /* True debit account is the parent of the asset */
                /* Note that debit and credit must be identical */
                if (isTo) {
                    theDebit = theDebit.getParent();
                } else {
                    theCredit = theCredit.getParent();
                }
                isTo = !isTo;
                break;
            case RENTALINCOME:
            case ROOMRENTALINCOME:
                /* True debit account is the parent of the security */
                theChild = theDebit.equals(theCredit)
                        ? null
                        : theDebit;
                theDebit = theCredit.getParent();
                break;
            default:
                break;
        }
    }
}
