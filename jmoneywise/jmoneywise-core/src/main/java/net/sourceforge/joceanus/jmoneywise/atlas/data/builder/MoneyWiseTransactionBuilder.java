/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2021 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.builder;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Payee;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionTag;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Loan Builder.
 */
public class MoneyWiseTransactionBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseData theDataSet;

    /**
     * The Tags.
     */
    private final List<TransactionTag> theTags;

    /**
     * The Date.
     */
    private TethysDate theDate;

    /**
     * The Account.
     */
    private TransactionAsset theAccount;

    /**
     * The Partner.
     */
    private TransactionAsset thePartner;

    /**
     * Switch directions?.
     */
    private boolean switchDirection;

    /**
     * The Transaction Category.
     */
    private TransactionCategory theCategory;

    /**
     * The Amount.
     */
    private TethysMoney theAmount;

    /**
     * The Reconciled.
     */
    private Boolean theReconciled;

    /**
     * The TaxCredit.
     */
    private TethysMoney theTaxCredit;

    /**
     * The Employers NI.
     */
    private TethysMoney theErNI;

    /**
     * The Employees NI.
     */
    private TethysMoney theEeNI;

    /**
     * The Benefit.
     */
    private TethysMoney theBenefit;

    /**
     * The Withheld.
     */
    private TethysMoney theWithheld;

    /**
     * The Account Units.
     */
    private TethysUnits theAccountUnits;

    /**
     * The PartnerUnits.
     */
    private TethysUnits thePartnerUnits;

    /**
     * The Dilution.
     */
    private TethysDilution theDilution;

    /**
     * The QualifyYears.
     */
    private Integer theQualifyYears;

    /**
     * The ReturnedCash.
     */
    private TethysMoney theReturnedCash;

    /**
     * The ReturnedCashAccount.
     */
    private TransactionAsset theReturnedCashAccount;

    /**
     * The PartnerAmount.
     */
    private TethysMoney thePartnerAmount;

    /**
     * The Price.
     */
    private TethysPrice thePrice;

    /**
     * Constructor.
     * @param pDataSet the dataSet
     */
    MoneyWiseTransactionBuilder(final MoneyWiseData pDataSet) {
        theDataSet = pDataSet;
        theReconciled = Boolean.FALSE;
        theTags = new ArrayList<>();
    }

    /**
     * Set Date.
     * @param pDate the date of the transaction.
     */
    public void date(final TethysDate pDate) {
        theDate = pDate;
    }

    /**
     * Set Pair.
     * @param pFrom the from account.
     * @param pTo the to account.
     */
    public void pair(final TransactionAsset pFrom,
                     final TransactionAsset pTo) {
        switchDirection = pFrom instanceof Payee;
        theAccount = switchDirection ? pTo : pFrom;
        thePartner = switchDirection ? pFrom : pTo;
    }

    /**
     * Set category.
     * @param pCategory the category.
     */
    public void category(final TransactionCategory pCategory) {
        theCategory = pCategory;
    }

    /**
     * Set the amount.
     * @param pAmount the amount of the transaction.
     */
    public void setAmount(final TethysMoney pAmount) {
        theAmount = pAmount;
    }

    /**
     * Set the taxCredit.
     * @param pTaxCredit the taxCredit of the transaction.
     */
    public void setTaxCredit(final TethysMoney pTaxCredit) {
        theTaxCredit = pTaxCredit;
    }

    /**
     * Set the EmployersNI.
     * @param pNI the EmployersNI of the transaction.
     */
    public void setEmployersNI(final TethysMoney pNI) {
        theErNI = pNI;
    }

    /**
     * Set the EmployeesNI.
     * @param pNI the EmployeesNI of the transaction.
     */
    public void setEmployeesNI(final TethysMoney pNI) {
        theEeNI = pNI;
    }

    /**
     * Set the benefit.
     * @param pBenefit the benefit of the transaction.
     */
    public void setBenefit(final TethysMoney pBenefit) {
        theBenefit = pBenefit;
    }

    /**
     * Set the withheld.
     * @param pWithheld the withheld of the transaction.
     */
    public void setWithheld(final TethysMoney pWithheld) {
        theWithheld = pWithheld;
    }

    /**
     * Set the partner amount.
     * @param pAmount the partner amount of the transaction.
     */
    public void setPartnerAmount(final TethysMoney pAmount) {
        thePartnerAmount = pAmount;
    }

    /**
     * Set the debit units.
     * @param pUnits the debit units.
     */
    public void setDebitUnits(final TethysUnits pUnits) {
        final TethysUnits myUnits = new TethysUnits(pUnits);
        myUnits.negate();
        if (switchDirection) {
            thePartnerUnits = myUnits;
        } else {
            theAccountUnits = myUnits;
        }
    }


    /**
     * Set the credit units.
     * @param pUnits the credit units.
     */
    public void setCreditUnits(final TethysUnits pUnits) {
        if (switchDirection) {
            theAccountUnits = pUnits;
        } else {
            thePartnerUnits = pUnits;
        }
    }

    /**
     * Set the dilution.
     * @param pDilution the dilution of the transaction.
     */
    public void setDilution(final TethysDilution pDilution) {
        theDilution = pDilution;
    }

    /**
     * Set the qualifyYears.
     * @param pYears the qualifyYears of the transaction.
     */
    public void setQualifyYears(final Integer pYears) {
        theQualifyYears = pYears;
    }

    /**
     * Set the returnedCash.
     * @param pCash the returnedCash.
     * @param pAccount the account to which the cash was returned
     */
    public void setReturnedCash(final TethysMoney pCash,
                                final TransactionAsset pAccount) {
        theReturnedCash = pCash;
        theReturnedCashAccount = pAccount;
    }

    /**
     * Set the price.
     * @param pPrice the price of the transaction.
     */
    public void setPrice(final TethysPrice pPrice) {
        thePrice = pPrice;
    }

    /**
     * Set a tag.
     * @param pTag the tag for the transaction.
     */
    public void setTag(final TransactionTag pTag) {
        if (theTags.contains(pTag)) {
            theTags.add(pTag);
        }
    }

    /**
     * Set reconciled.
     */
    public void setReconciled() {
        theReconciled = Boolean.TRUE;
    }

    /**
     * Build the transaction.
     * @return the new Transaction
     * @throws OceanusException on error
     */
    public Transaction build() throws OceanusException {
        /* Create the transaction */
        final Transaction myTrans = theDataSet.getTransactions().addNewItem();
        myTrans.setDate(theDate);
        myTrans.setAccount(theAccount);
        myTrans.setPartner(thePartner);
        if (switchDirection) {
            myTrans.switchDirection();
        }
        myTrans.setCategory(theCategory);
        myTrans.setAmount(theAmount);
        myTrans.setReconciled(theReconciled);

        /* Add details */
        myTrans.setTaxCredit(theTaxCredit);
        myTrans.setEmployerNatIns(theErNI);
        myTrans.setEmployeeNatIns(theEeNI);
        myTrans.setBenefit(theBenefit);
        myTrans.setWithheld(theWithheld);
        myTrans.setPartnerAmount(thePartnerAmount);
        myTrans.setAccountDeltaUnits(theAccountUnits);
        myTrans.setPartnerDeltaUnits(thePartnerUnits);
        myTrans.setDilution(theDilution);
        myTrans.setYears(theQualifyYears);
        myTrans.setReturnedCash(theReturnedCash);
        myTrans.setReturnedCashAccount(theReturnedCashAccount);
        myTrans.setPrice(thePrice);
        myTrans.setTransactionTags(theTags);
        myTrans.validate();

        /* Reset values */
        theDate = null;
        theAccount = null;
        thePartner = null;
        switchDirection = false;
        theCategory = null;
        theAmount = null;
        theTaxCredit = null;
        theErNI = null;
        theEeNI = null;
        theBenefit = null;
        theWithheld = null;
        thePartnerAmount = null;
        theAccountUnits = null;
        thePartnerUnits = null;
        theDilution = null;
        theQualifyYears = null;
        theReturnedCash = null;
        theReturnedCashAccount = null;
        thePrice = null;
        theTags.clear();
        theReconciled = Boolean.FALSE;

        /* Return the transaction */
        return myTrans;
    }
}
