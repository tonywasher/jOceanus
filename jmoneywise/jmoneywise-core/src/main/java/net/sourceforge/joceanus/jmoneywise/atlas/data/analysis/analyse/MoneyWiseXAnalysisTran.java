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

import java.util.Objects;

import net.sourceforge.joceanus.jmoneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * TRansaction details.
 */
public class MoneyWiseXAnalysisTran {
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
    private final boolean isTo;

    /**
     * The debit amount.
     */
    private TethysMoney theDebitAmount;

    /**
     * The credit amount.
     */
    private TethysMoney theCreditAmount;

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
     * Constructor.
     * @param pEvent the event
     */
    MoneyWiseXAnalysisTran(final MoneyWiseXAnalysisEvent pEvent) {
        /* Store the base */
        theEvent = pEvent;
        theTrans = theEvent.getTransaction();

        /* Access account and partner */
        isTo = theTrans.getDirection() == MoneyWiseAssetDirection.TO;
        theDebit = isTo ? theTrans.getAccount() : theTrans.getPartner();
        theCredit = isTo ? theTrans.getPartner() : theTrans.getAccount();

        /* Store the category */
        theCategory = theTrans.getCategory();

        /* Sort out amounts */
        final TethysMoney myAmount = theTrans.getAmount();
        TethysMoney myPartnerAmount = theTrans.getPartnerAmount();
        myPartnerAmount = myPartnerAmount == null ? myAmount : myPartnerAmount;
        if (isTo) {
            theDebitAmount = new TethysMoney(myAmount);
            theCreditAmount = myPartnerAmount;
        } else {
            theDebitAmount = new TethysMoney(myPartnerAmount);
            theCreditAmount = myAmount;
        }
        theDebitAmount.negate();
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
     * Obtain the debit amount.
     * @return the debit amount
     */
    TethysMoney getDebitAmount() {
        return theDebitAmount;
    }

    /**
     * Obtain the credit amount.
     * @return the credit amount
     */
    TethysMoney getCreditAmount() {
        return theCreditAmount;
    }

    /**
     * Set the debit amount.
     * @param pAmount the amount
     */
    void setDebitAmount(final TethysMoney pAmount) {
        theDebitAmount = pAmount;
        if (theCredit instanceof MoneyWisePayee) {
            theCreditAmount = new TethysMoney(pAmount);
            theCreditAmount.negate();
        }
    }

    /**
     * Set the credit amount.
     * @param pAmount the amount
     */
    void setCreditAmount(final TethysMoney pAmount) {
        theCreditAmount = pAmount;
        if (theDebit instanceof MoneyWisePayee) {
            theDebitAmount = new TethysMoney(pAmount);
            theDebitAmount.negate();
        }
    }

    /**
     * Adjust parent/child.
     */
    void adjustParent() {
        /* Switch on category class */
        switch (theCategory.getCategoryTypeClass()) {
            case INTEREST:
            case LOYALTYBONUS:
                /* Obtain detailed category */
                theCategory = ((MoneyWiseAssetBase) theDebit).getDetailedCategory(theCategory, theTrans.getTaxYear());

                /* True debit account is the parent */
                theChild = theDebit.equals(theCredit)
                        ? null
                        : theDebit;
                theDebit = theDebit.getParent();
                break;
            case LOANINTERESTEARNED:
            case CASHBACK:
                /* True debit account is the parent of the asset */
                theDebit = theDebit.getParent();
                break;
            case RENTALINCOME:
            case ROOMRENTALINCOME:
                /* True debit account is the parent of the security */
                theChild = theDebit.equals(theCredit)
                        ? null
                        : theDebit;
                theDebit = theCredit.getParent();
                break;
            case WRITEOFF:
            case LOANINTERESTCHARGED:
                /* True credit account is the parent of the loan */
                theCredit = theCredit.getParent();
                break;
            default:
                break;
        }
    }
}
