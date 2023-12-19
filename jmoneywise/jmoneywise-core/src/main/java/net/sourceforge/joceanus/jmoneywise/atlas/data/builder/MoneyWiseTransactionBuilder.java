/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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

import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataException;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Transaction Builder.
 */
public class MoneyWiseTransactionBuilder {
    /**
     * DataSet.
     */
    private final MoneyWiseDataSet theDataSet;

    /**
     * The Tags.
     */
    private final List<MoneyWiseTransTag> theTags;

    /**
     * The Date.
     */
    private TethysDate theDate;

    /**
     * The Account.
     */
    private MoneyWiseTransAsset theAccount;

    /**
     * The Partner.
     */
    private MoneyWiseTransAsset thePartner;

    /**
     * Switch directions?.
     */
    private boolean switchDirection;

    /**
     * The Transaction Category.
     */
    private MoneyWiseTransCategory theCategory;

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
    private TethysRatio theDilution;

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
    private MoneyWiseTransAsset theReturnedCashAccount;

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
    public MoneyWiseTransactionBuilder(final MoneyWiseDataSet pDataSet) {
        theDataSet = pDataSet;
        theReconciled = Boolean.FALSE;
        theTags = new ArrayList<>();
    }

    /**
     * Set Date.
     * @param pDate the date of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder date(final TethysDate pDate) {
        theDate = pDate;
        return this;
    }

    /**
     * Set the date.
     * @param pDate the Date of the rate.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder date(final String pDate) {
        return date(new TethysDate(pDate));
    }

    /**
     * Set Pair.
     * @param pFrom the from account.
     * @param pTo the to account.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder pair(final MoneyWiseTransAsset pFrom,
                                            final MoneyWiseTransAsset pTo) {
        switchDirection = pFrom instanceof MoneyWisePayee;
        theAccount = switchDirection ? pTo : pFrom;
        thePartner = switchDirection ? pFrom : pTo;
        return this;
    }

    /**
     * Set Pair.
     * @param pFrom the from account.
     * @param pTo the to account.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder pair(final String pFrom,
                                            final String pTo) {
        return pair(resolveTransactionAsset(pFrom), resolveTransactionAsset(pTo));
    }

    /**
     * Set category.
     * @param pCategory the category.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder category(final MoneyWiseTransCategory pCategory) {
        theCategory = pCategory;
        return this;
    }

    /**
     * Set category.
     * @param pCategory the category.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder category(final String pCategory) {
        return category(theDataSet.getTransCategories().findItemByName(pCategory));
    }

    /**
     * Set the amount.
     * @param pAmount the amount of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder amount(final TethysMoney pAmount) {
        theAmount = pAmount;
        return this;
    }

    /**
     * Set the amount.
     * @param pAmount the amount of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder amount(final String pAmount) {
        return amount(new TethysMoney(pAmount, theAccount.getCurrency()));
    }

    /**
     * Set the taxCredit.
     * @param pTaxCredit the taxCredit of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder taxCredit(final TethysMoney pTaxCredit) {
        theTaxCredit = pTaxCredit;
        return this;
    }

    /**
     * Set the taxCredit.
     * @param pTaxCredit the taxCredit of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder taxCredit(final String pTaxCredit) {
        return taxCredit(new TethysMoney(pTaxCredit, theDataSet.getDefaultCurrency().getCurrency()));
    }

    /**
     * Set the EmployersNI.
     * @param pNI the EmployersNI of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder employersNI(final TethysMoney pNI) {
        theErNI = pNI;
        return this;
    }

    /**
     * Set the EmployersNI.
     * @param pNI the EmployersNI of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder employersNI(final String pNI) {
        return employersNI(new TethysMoney(pNI, theDataSet.getDefaultCurrency().getCurrency()));
    }

    /**
     * Set the EmployeesNI.
     * @param pNI the EmployeesNI of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder employeesNI(final TethysMoney pNI) {
        theEeNI = pNI;
        return this;
    }

    /**
     * Set the EmployeesNI.
     * @param pNI the EmployeesNI of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder employeesNI(final String pNI) {
        return employeesNI(new TethysMoney(pNI, theDataSet.getDefaultCurrency().getCurrency()));
    }

    /**
     * Set the benefit.
     * @param pBenefit the benefit of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder benefit(final TethysMoney pBenefit) {
        theBenefit = pBenefit;
        return this;
    }

    /**
     * Set the benefit.
     * @param pBenefit the benefit of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder benefit(final String pBenefit) {
        return benefit(new TethysMoney(pBenefit, theDataSet.getDefaultCurrency().getCurrency()));
    }

    /**
     * Set the withheld.
     * @param pWithheld the withheld of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder withheld(final TethysMoney pWithheld) {
        theWithheld = pWithheld;
        return this;
    }

    /**
     * Set the withheld.
     * @param pWithheld the withheld of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder withheld(final String pWithheld) {
        return withheld(new TethysMoney(pWithheld, theDataSet.getDefaultCurrency().getCurrency()));
    }

    /**
     * Set the partner amount.
     * @param pAmount the partner amount of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder partnerAmount(final TethysMoney pAmount) {
        thePartnerAmount = pAmount;
        return this;
    }

    /**
     * Set the partner amount.
     * @param pAmount the partner amount of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder partnerAmount(final String pAmount) {
        return partnerAmount(new TethysMoney(pAmount, thePartner.getCurrency()));
    }

    /**
     * Set the account units.
     * @param pUnits the account units.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder accountUnits(final TethysUnits pUnits) {
        theAccountUnits = pUnits;
        return this;
    }

    /**
     * Set the account units.
     * @param pUnits the account units.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder accountUnits(final String pUnits) {
        return accountUnits(new TethysUnits(pUnits));
    }

    /**
     * Set the partner units.
     * @param pUnits the partner units.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder partnerUnits(final TethysUnits pUnits) {
        thePartnerUnits = pUnits;
        return this;
    }

    /**
     * Set the partner units.
     * @param pUnits the partner units.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder partnerUnits(final String pUnits) {
        return partnerUnits(new TethysUnits(pUnits));
    }

    /**
     * Set the dilution.
     * @param pDilution the dilution of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder dilution(final TethysRatio pDilution) {
        theDilution = pDilution;
        return this;
    }

    /**
     * Set the dilution.
     * @param pDilution the dilution of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder dilution(final String pDilution) {
        return dilution(new TethysRatio(pDilution));
    }

    /**
     * Set the qualifyYears.
     * @param pYears the qualifyYears of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder qualifyYears(final Integer pYears) {
        theQualifyYears = pYears;
        return this;
    }

    /**
     * Set the returnedCash.
     * @param pCash the returnedCash.
     * @param pAccount the account to which the cash was returned
     * @return the builder
     */
    public MoneyWiseTransactionBuilder returnedCash(final TethysMoney pCash,
                                                    final MoneyWiseTransAsset pAccount) {
        theReturnedCash = pCash;
        theReturnedCashAccount = pAccount;
        return this;
    }

    /**
     * Set the returnedCash.
     * @param pCash the returnedCash.
     * @param pAccount the account to which the cash was returned
     * @return the builder
     */
    public MoneyWiseTransactionBuilder returnedCash(final String pCash,
                                                    final String pAccount) {
        final MoneyWiseTransAsset myAsset = resolveTransactionAsset(pAccount);
        return returnedCash(new TethysMoney(pCash, myAsset == null ? null : myAsset.getCurrency()), myAsset);
    }

    /**
     * Set the price.
     * @param pPrice the price of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder price(final TethysPrice pPrice) {
        thePrice = pPrice;
        return this;
    }

    /**
     * Set the price.
     * @param pPrice the price of the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder price(final String pPrice) {
        return price(new TethysPrice(pPrice, theAccount.getCurrency()));
    }

    /**
     * Set a tag.
     * @param pTag the tag for the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder tag(final MoneyWiseTransTag pTag) {
        if (!theTags.contains(pTag)) {
            theTags.add(pTag);
        }
        return this;
    }

    /**
     * Set a tag.
     * @param pTag the tag for the transaction.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder tag(final String pTag) {
        return tag(theDataSet.getTransactionTags().findItemByName(pTag));
    }

    /**
     * Set reconciled.
     * @return the builder
     */
    public MoneyWiseTransactionBuilder reconciled() {
        theReconciled = Boolean.TRUE;
        return this;
    }

    /**
     * Build the transaction.
     * @return the new Transaction
     * @throws OceanusException on error
     */
    public MoneyWiseTransaction build() throws OceanusException {
        /* Create the transaction */
        final MoneyWiseTransaction myTrans = theDataSet.getTransactions().addNewItem();
        myTrans.setDate(theDate);
        myTrans.setAccount(theAccount);
        myTrans.setPartner(thePartner);
        myTrans.setDirection(MoneyWiseAssetDirection.TO);
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

        /* Check for errors */
        myTrans.validate();
        if (myTrans.hasErrors()) {
            theDataSet.getTransactions().remove(myTrans);
            throw new MoneyWiseDataException(myTrans, "Failed validation");
        }

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

    /**
     * Resolve transactionAsset.
     * @param pAsset the asset name
     * @return the asset
     */
    private MoneyWiseTransAsset resolveTransactionAsset(final String pAsset) {
        /* Look fot security holding */
        final int myIndex = pAsset.lastIndexOf(':');
        if (myIndex != -1) {
            /* Split into portfolio and security */
            final MoneyWiseSecurity mySec = theDataSet.getSecurities().findItemByName(pAsset.substring(myIndex + 1));
            final MoneyWisePortfolio myPort = theDataSet.getPortfolios().findItemByName(pAsset.substring(0, myIndex));

            /* Build security holding */
            if (mySec != null && myPort != null) {
                return theDataSet.getPortfolios().getSecurityHoldingsMap().declareHolding(myPort, mySec);
            } else {
                return null;
            }

        } else {
            /* Look for Payee */
            MoneyWiseTransAsset myAsset = theDataSet.getPayees().findItemByName(pAsset);
            if (myAsset != null) {
                return myAsset;
            }

            /* Look for Deposit */
            myAsset = theDataSet.getDeposits().findItemByName(pAsset);
            if (myAsset != null) {
                return myAsset;
            }

            /* Look for Cash */
            myAsset = theDataSet.getCash().findItemByName(pAsset);
            if (myAsset != null) {
                return myAsset;
            }

            /* Look for Loan */
            myAsset = theDataSet.getLoans().findItemByName(pAsset);
            if (myAsset != null) {
                return myAsset;
            }

            /* Look for Portfolio */
            return theDataSet.getPortfolios().findItemByName(pAsset);
        }
    }
}
