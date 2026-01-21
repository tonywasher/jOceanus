/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
package net.sourceforge.joceanus.moneywise.lethe.data.analysis.data;

import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;

import java.util.Currency;

/**
 * Transaction Analysis Helper.
 */
public class MoneyWiseAnalysisTransactionHelper {
    /**
     * Currency Cursor.
     */
    private final MoneyWiseAnalysisExchangeRateCursor theRateCursor;

    /**
     * Security Cursor.
     */
    private final MoneyWiseAnalysisSecurityPriceCursor thePriceCursor;

    /**
     * Reporting currency.
     */
    private final MoneyWiseCurrency theCurrency;

    /**
     * The current transaction.
     */
    private MoneyWiseTransaction theCurrent;

    /**
     * The current date.
     */
    private OceanusDate theDate;

    /**
     * The account detail.
     */
    private TransactionDetail theAccountDetail;

    /**
     * The debit XchangeRate.
     */
    private OceanusRatio theDebitXchangeRate;

    /**
     * Constructor.
     *
     * @param pData the dataSet
     */
    public MoneyWiseAnalysisTransactionHelper(final MoneyWiseDataSet pData) {
        /* Create the cursors */
        theRateCursor = new MoneyWiseAnalysisExchangeRateCursor(pData);
        thePriceCursor = new MoneyWiseAnalysisSecurityPriceCursor(pData);

        /* Note the reporting currency */
        theCurrency = pData.getReportingCurrency();
    }

    /**
     * Obtain transaction.
     *
     * @return the transaction
     */
    public MoneyWiseTransaction getTransaction() {
        return theCurrent;
    }

    /**
     * Set the transaction.
     *
     * @param pTrans the transaction.
     */
    public void setTransaction(final MoneyWiseTransaction pTrans) {
        /* Record date */
        theCurrent = pTrans;
        theDate = theCurrent.getDate();

        /* Reset details */
        theAccountDetail = new TransactionDetail();
        theDebitXchangeRate = theAccountDetail.getDebitExchangeRate();
    }

    /**
     * Set the security (for PortfolioXfer).
     *
     * @param pSecurity the security.
     */
    public void setSecurity(final MoneyWiseSecurity pSecurity) {
        final MoneyWiseCurrency myCurr = pSecurity.getAssetCurrency();
        final boolean isForeign = !MetisDataDifference.isEqual(myCurr, theCurrency);
        theDebitXchangeRate = isForeign ? theRateCursor.getExchangeRate(myCurr, theDate) : null;
    }

    /**
     * Obtain date.
     *
     * @return the date
     */
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Obtain account.
     *
     * @return the account
     */
    public MoneyWiseTransAsset getAccount() {
        return theAccountDetail.getAccount();
    }

    /**
     * Obtain partner.
     *
     * @return the partner
     */
    public MoneyWiseTransAsset getPartner() {
        return theAccountDetail.getPartner();
    }

    /**
     * Obtain direction.
     *
     * @return the direction
     */
    public MoneyWiseAssetDirection getDirection() {
        return theAccountDetail.getDirection();
    }

    /**
     * Obtain debit asset.
     *
     * @return the debit asset
     */
    public MoneyWiseTransAsset getDebitAsset() {
        return theAccountDetail.getDebitAsset();
    }

    /**
     * Obtain credit asset.
     *
     * @return the credit asset
     */
    public MoneyWiseTransAsset getCreditAsset() {
        return theAccountDetail.getCreditAsset();
    }

    /**
     * Obtain the category.
     *
     * @return the category
     */
    public MoneyWiseTransCategory getCategory() {
        return theAccountDetail.getCategory();
    }

    /**
     * Is this a particular category class?
     *
     * @param pClass the category class
     * @return true/false
     */
    public boolean isCategoryClass(final MoneyWiseTransCategoryClass pClass) {
        return theAccountDetail.isCategoryClass(pClass);
    }

    /**
     * Obtain the category class.
     *
     * @return the category class
     */
    public MoneyWiseTransCategoryClass getCategoryClass() {
        return theAccountDetail.getCategoryClass();
    }

    /**
     * Obtain debit amount.
     *
     * @return the debit amount.
     */
    public OceanusMoney getDebitAmount() {
        return theAccountDetail.getDebitAmount();
    }

    /**
     * Obtain local amount.
     *
     * @return the amount.
     */
    public OceanusMoney getLocalAmount() {
        return theAccountDetail.getLocalAmount();
    }

    /**
     * Obtain credit amount.
     *
     * @return the credit amount.
     */
    public OceanusMoney getCreditAmount() {
        return theAccountDetail.getCreditAmount();
    }

    /**
     * Obtain local returnedCash.
     *
     * @return the returnedCash.
     */
    public OceanusMoney getLocalReturnedCash() {
        return theAccountDetail.getLocalReturnedCash();
    }

    /**
     * Obtain returnedCash.
     *
     * @return the returned cash.
     */
    public OceanusMoney getReturnedCash() {
        return theAccountDetail.getReturnedCash();
    }

    /**
     * Obtain tax credit.
     *
     * @return the tax credit.
     */
    public OceanusMoney getTaxCredit() {
        return theAccountDetail.getTaxCredit();
    }

    /**
     * Obtain employer national insurance.
     *
     * @return the national insurance.
     */
    public OceanusMoney getEmployerNatIns() {
        return theAccountDetail.getEmployerNatIns();
    }

    /**
     * Obtain employee national insurance.
     *
     * @return the national insurance.
     */
    public OceanusMoney getEmployeeNatIns() {
        return theAccountDetail.getEmployeeNatIns();
    }

    /**
     * Obtain benefit.
     *
     * @return the benefit.
     */
    public OceanusMoney getDeemedBenefit() {
        return theAccountDetail.getBenefit();
    }

    /**
     * Obtain withheld.
     *
     * @return the withheld.
     */
    public OceanusMoney getWithheld() {
        return theAccountDetail.getWithheld();
    }

    /**
     * Obtain returnedCash Account.
     *
     * @return the returnedCash account
     */
    public MoneyWiseTransAsset getReturnedCashAccount() {
        return theAccountDetail.getReturnedCashAccount();
    }

    /**
     * Obtain debit units.
     *
     * @return the debit units
     */
    public OceanusUnits getDebitUnits() {
        OceanusUnits myUnits = getDirection().isTo()
                ? getAccountDeltaUnits()
                : getPartnerDeltaUnits();
        if (myUnits != null) {
            myUnits = new OceanusUnits(myUnits);
            myUnits.negate();
        }
        return myUnits;
    }

    /**
     * Obtain credit units.
     *
     * @return the debit units
     */
    public OceanusUnits getCreditUnits() {
        return getDirection().isFrom()
                ? getAccountDeltaUnits()
                : getPartnerDeltaUnits();
    }

    /**
     * Obtain account delta units.
     *
     * @return the delta units
     */
    public OceanusUnits getAccountDeltaUnits() {
        return theAccountDetail.getAccountDeltaUnits();
    }

    /**
     * Obtain partner delta units.
     *
     * @return the delta units
     */
    public OceanusUnits getPartnerDeltaUnits() {
        return theAccountDetail.getPartnerDeltaUnits();
    }

    /**
     * Obtain dilution.
     *
     * @return the dilution
     */
    public OceanusRatio getDilution() {
        return theAccountDetail.getDilution();
    }

    /**
     * Obtain debit price.
     *
     * @return the debit price
     */
    public OceanusPrice getDebitPrice() {
        return theAccountDetail.getDebitPrice();
    }

    /**
     * Obtain credit price.
     *
     * @return the credit price
     */
    public OceanusPrice getCreditPrice() {
        return theAccountDetail.getCreditPrice();
    }

    /**
     * Obtain debit exchangeRate.
     *
     * @return the rate
     */
    public OceanusRatio getDebitExchangeRate() {
        return theDebitXchangeRate;
    }

    /**
     * Obtain credit exchangeRate.
     *
     * @return the rate
     */
    public OceanusRatio getCreditExchangeRate() {
        return theAccountDetail.getCreditExchangeRate();
    }

    /**
     * Obtain returnedCash exchangeRate.
     *
     * @return the rate
     */
    public OceanusRatio getReturnedCashExchangeRate() {
        return theAccountDetail.getReturnedCashExchangeRate();
    }

    /**
     * Convert amount to reporting currency.
     *
     * @param pCurrency the currency
     * @param pDate     the date for the conversion
     * @return the reporting amount
     */
    protected OceanusRatio getExchangeRate(final MoneyWiseCurrency pCurrency,
                                           final OceanusDate pDate) {
        return theRateCursor.getExchangeRate(pCurrency, pDate);
    }

    /**
     * Transaction Detail class.
     */
    private final class TransactionDetail {
        /**
         * The account.
         */
        private final MoneyWiseTransAsset theAccount;

        /**
         * The partner.
         */
        private final MoneyWiseTransAsset thePartner;

        /**
         * The direction.
         */
        private final MoneyWiseAssetDirection theDirection;

        /**
         * The category.
         */
        private final MoneyWiseTransCategory theCategory;

        /**
         * The amount.
         */
        private final OceanusMoney theAmount;

        /**
         * The ReturnedCashAccount.
         */
        private final MoneyWiseTransAsset theReturnedCashAccount;

        /**
         * The returnedCash.
         */
        private final OceanusMoney theReturnedCash;

        /**
         * The tax credit.
         */
        private final OceanusMoney theTaxCredit;

        /**
         * The Employer natIns.
         */
        private final OceanusMoney theEmployerNatIns;

        /**
         * The Employee natIns.
         */
        private final OceanusMoney theEmployeeNatIns;

        /**
         * The benefit amount.
         */
        private final OceanusMoney theBenefit;

        /**
         * The withheld amount.
         */
        private final OceanusMoney theWithheld;

        /**
         * The account delta units.
         */
        private final OceanusUnits theAccountUnits;

        /**
         * The partner delta units.
         */
        private final OceanusUnits thePartnerUnits;

        /**
         * The dilution.
         */
        private final OceanusRatio theDilution;

        /**
         * The account price.
         */
        private final OceanusPrice theAccountPrice;

        /**
         * The partner price.
         */
        private final OceanusPrice thePartnerPrice;

        /**
         * The foreign account details.
         */
        private final ForeignAccountDetail theForeignAccount;

        /**
         * The foreign partner details.
         */
        private final ForeignPartnerDetail theForeignPartner;

        /**
         * The foreign returnedCash details.
         */
        private final ForeignPartnerDetail theForeignReturnedCash;

        /**
         * Constructor.
         */
        private TransactionDetail() {
            /* Store detail */
            theAccount = theCurrent.getAccount();
            thePartner = theCurrent.getPartner();
            theDirection = theCurrent.getDirection();
            theCategory = theCurrent.getCategory();
            theTaxCredit = theCurrent.getTaxCredit();
            theEmployerNatIns = theCurrent.getEmployerNatIns();
            theEmployeeNatIns = theCurrent.getEmployeeNatIns();
            theBenefit = theCurrent.getDeemedBenefit();
            theWithheld = theCurrent.getWithheld();
            theReturnedCashAccount = theCurrent.getReturnedCashAccount();
            theAccountUnits = theCurrent.getAccountDeltaUnits();
            thePartnerUnits = theCurrent.getPartnerDeltaUnits();
            theDilution = theCurrent.getDilution();

            /* Obtain the amounts */
            final OceanusMoney myAmount = theCurrent.getAmount();
            final OceanusMoney myPartnerAmount = theCurrent.getPartnerAmount();
            final OceanusMoney myReturnedCash = theCurrent.getReturnedCash();

            /* Determine account prices */
            theAccountPrice = theAccount instanceof MoneyWiseSecurityHolding
                    ? thePriceCursor.getSecurityPrice(((MoneyWiseSecurityHolding) theAccount).getSecurity(), theDate)
                    : null;
            thePartnerPrice = thePartner instanceof MoneyWiseSecurityHolding
                    ? thePriceCursor.getSecurityPrice(((MoneyWiseSecurityHolding) thePartner).getSecurity(), theDate)
                    : null;

            /* Determine foreign account detail */
            MoneyWiseCurrency myActCurrency = theAccount.getAssetCurrency();
            theForeignAccount = MetisDataDifference.isEqual(myActCurrency, theCurrency)
                    ? null
                    : new ForeignAccountDetail(this, myActCurrency, myAmount);

            /* If we have a partner amount */
            myActCurrency = thePartner.getAssetCurrency();
            theForeignPartner = myActCurrency == null
                    || MetisDataDifference.isEqual(myActCurrency, theCurrency)
                    ? null
                    : new ForeignPartnerDetail(myActCurrency, myPartnerAmount);

            /* If we have a returnedCash account */
            if (theReturnedCashAccount != null) {
                /* Determine foreign returnedCash detail */
                myActCurrency = theReturnedCashAccount.getAssetCurrency();
                theForeignReturnedCash = MetisDataDifference.isEqual(myActCurrency, theCurrency)
                        ? null
                        : new ForeignPartnerDetail(myActCurrency, myReturnedCash);
            } else {
                theForeignReturnedCash = null;
            }

            /* Determine the local amounts */
            theAmount = theForeignAccount == null
                    ? myAmount
                    : theForeignPartner == null
                    ? myPartnerAmount
                    : theForeignAccount.theAmount;
            theReturnedCash = theForeignReturnedCash == null
                    ? myReturnedCash
                    : theForeignReturnedCash.theAmount;
        }

        /**
         * Obtain account.
         *
         * @return the account
         */
        private MoneyWiseTransAsset getAccount() {
            return theAccount;
        }

        /**
         * Obtain partner.
         *
         * @return the partner
         */
        private MoneyWiseTransAsset getPartner() {
            return thePartner;
        }

        /**
         * Obtain direction.
         *
         * @return the direction
         */
        private MoneyWiseAssetDirection getDirection() {
            return theDirection;
        }

        /**
         * Obtain debit asset.
         *
         * @return the debit asset
         */
        private MoneyWiseTransAsset getDebitAsset() {
            return theDirection.isFrom()
                    ? thePartner
                    : theAccount;
        }

        /**
         * Obtain credit asset.
         *
         * @return the credit asset
         */
        private MoneyWiseTransAsset getCreditAsset() {
            return theDirection.isTo()
                    ? thePartner
                    : theAccount;
        }

        /**
         * Obtain returnedCash account.
         *
         * @return the returnedCash account
         */
        private MoneyWiseTransAsset getReturnedCashAccount() {
            return theReturnedCashAccount;
        }

        /**
         * Obtain category.
         *
         * @return the category class
         */
        private MoneyWiseTransCategory getCategory() {
            return theCategory;
        }

        /**
         * Is this a particular category class?
         *
         * @param pClass the category class
         * @return true/false
         */
        private boolean isCategoryClass(final MoneyWiseTransCategoryClass pClass) {
            return theCategory.isCategoryClass(pClass);
        }

        /**
         * Obtain category class?
         *
         * @return the category class
         */
        private MoneyWiseTransCategoryClass getCategoryClass() {
            return theCategory.getCategoryTypeClass();
        }

        /**
         * Obtain debit amount.
         *
         * @return the debit amount
         */
        private OceanusMoney getDebitAmount() {
            return theDirection.isFrom()
                    ? theForeignPartner == null
                    ? theAmount
                    : theForeignPartner.theBase
                    : theForeignAccount == null
                    ? theAmount
                    : theForeignAccount.theBase;
        }

        /**
         * Obtain local debit amount.
         *
         * @return the local debit amount
         */
        private OceanusMoney getLocalAmount() {
            return theAmount;
        }

        /**
         * Obtain credit amount.
         *
         * @return the credit amount
         */
        private OceanusMoney getCreditAmount() {
            return theDirection.isTo()
                    ? theForeignPartner == null
                    ? theAmount
                    : theForeignPartner.theBase
                    : theForeignAccount == null
                    ? theAmount
                    : theForeignAccount.theBase;
        }

        /**
         * Obtain local returnedCash.
         *
         * @return the local returnedCash
         */
        private OceanusMoney getLocalReturnedCash() {
            return theReturnedCash;
        }

        /**
         * Obtain returnedCash.
         *
         * @return the returnedCash
         */
        private OceanusMoney getReturnedCash() {
            return theForeignReturnedCash == null
                    ? theReturnedCash
                    : theForeignReturnedCash.theBase;
        }

        /**
         * Obtain debit price.
         *
         * @return the debit price
         */
        private OceanusPrice getDebitPrice() {
            return theDirection.isFrom()
                    ? thePartnerPrice
                    : theAccountPrice;
        }

        /**
         * Obtain credit price.
         *
         * @return the credit price
         */
        private OceanusPrice getCreditPrice() {
            return theDirection.isTo()
                    ? thePartnerPrice
                    : theAccountPrice;
        }

        /**
         * Obtain debit exchangeRate.
         *
         * @return the rate
         */
        private OceanusRatio getDebitExchangeRate() {
            return theDirection.isTo()
                    ? theForeignAccount == null
                    ? null
                    : theForeignAccount.theExchangeRate
                    : theForeignPartner == null
                    ? null
                    : theForeignPartner.theExchangeRate;
        }

        /**
         * Obtain credit exchangeRate.
         *
         * @return the rate
         */
        private OceanusRatio getCreditExchangeRate() {
            return theDirection.isTo()
                    ? theForeignPartner == null
                    ? null
                    : theForeignPartner.theExchangeRate
                    : theForeignAccount == null
                    ? null
                    : theForeignAccount.theExchangeRate;
        }

        /**
         * Obtain thirdParty exchangeRate.
         *
         * @return the rate
         */
        private OceanusRatio getReturnedCashExchangeRate() {
            return theForeignReturnedCash == null
                    ? null
                    : theForeignReturnedCash.theExchangeRate;
        }

        /**
         * Obtain taxCredit.
         *
         * @return the tax credit
         */
        private OceanusMoney getTaxCredit() {
            return theForeignAccount != null
                    ? theForeignAccount.theTaxCredit
                    : theTaxCredit;
        }

        /**
         * Obtain employer natInsurance.
         *
         * @return the national insurance
         */
        private OceanusMoney getEmployerNatIns() {
            return theForeignAccount != null
                    ? theForeignAccount.theEmployerNatIns
                    : theEmployerNatIns;
        }

        /**
         * Obtain employee natInsurance.
         *
         * @return the national insurance
         */
        private OceanusMoney getEmployeeNatIns() {
            return theForeignAccount != null
                    ? theForeignAccount.theEmployeeNatIns
                    : theEmployeeNatIns;
        }

        /**
         * Obtain benefit.
         *
         * @return the benefit
         */
        private OceanusMoney getBenefit() {
            return theForeignAccount != null
                    ? theForeignAccount.theBenefit
                    : theBenefit;
        }

        /**
         * Obtain donation.
         *
         * @return the donation
         */
        private OceanusMoney getWithheld() {
            return theForeignAccount != null
                    ? theForeignAccount.theWithheld
                    : theWithheld;
        }

        /**
         * Obtain account delta units.
         *
         * @return the delta units
         */
        private OceanusUnits getAccountDeltaUnits() {
            return theAccountUnits;
        }

        /**
         * Obtain partner delta units.
         *
         * @return the partner delta units
         */
        private OceanusUnits getPartnerDeltaUnits() {
            return thePartnerUnits;
        }

        /**
         * Obtain dilution.
         *
         * @return the dilution
         */
        private OceanusRatio getDilution() {
            return theDilution;
        }
    }

    /**
     * Foreign Account class.
     */
    private final class ForeignAccountDetail {
        /**
         * The exchange rate.
         */
        private final OceanusRatio theExchangeRate;

        /**
         * The base amount.
         */
        private final OceanusMoney theBase;

        /**
         * The amount.
         */
        private final OceanusMoney theAmount;

        /**
         * The tax credit.
         */
        private final OceanusMoney theTaxCredit;

        /**
         * The employer natInsurance.
         */
        private final OceanusMoney theEmployerNatIns;

        /**
         * The employee natInsurance.
         */
        private final OceanusMoney theEmployeeNatIns;

        /**
         * The benefit amount.
         */
        private final OceanusMoney theBenefit;

        /**
         * The withheld amount.
         */
        private final OceanusMoney theWithheld;

        /**
         * Constructor.
         *
         * @param pTrans    the transaction detail
         * @param pCurrency the foreign currency
         * @param pAmount   the amount
         */
        private ForeignAccountDetail(final TransactionDetail pTrans,
                                     final MoneyWiseCurrency pCurrency,
                                     final OceanusMoney pAmount) {
            /* Obtain the required exchange rate */
            theBase = pAmount;
            final OceanusRatio myEventRate = theCurrent.getExchangeRate();
            final OceanusRatio myRate = myEventRate == null
                    ? theRateCursor.getExchangeRate(pCurrency, theDate)
                    : myEventRate;
            theExchangeRate = myRate;
            final Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            theAmount = theBase != null
                    ? theBase.convertCurrency(myCurrency, myRate)
                    : null;

            /* Obtain tax value */
            OceanusMoney myValue = pTrans.theTaxCredit;
            theTaxCredit = myValue != null
                    ? myValue.convertCurrency(myCurrency, myRate)
                    : null;
            /* Obtain Employer NatIns */
            myValue = pTrans.theEmployerNatIns;
            theEmployerNatIns = myValue != null
                    ? myValue.convertCurrency(myCurrency, myRate)
                    : null;

            /* Obtain Employee NatIns */
            myValue = pTrans.theEmployeeNatIns;
            theEmployeeNatIns = myValue != null
                    ? myValue.convertCurrency(myCurrency, myRate)
                    : null;

            /* Obtain benefit */
            myValue = pTrans.theBenefit;
            theBenefit = myValue != null
                    ? myValue.convertCurrency(myCurrency, myRate)
                    : null;

            /* Obtain withheld */
            myValue = pTrans.theWithheld;
            theWithheld = myValue != null
                    ? myValue.convertCurrency(myCurrency, myRate)
                    : null;
        }
    }

    /**
     * Foreign Partner class.
     */
    private final class ForeignPartnerDetail {
        /**
         * The exchange rate.
         */
        private final OceanusRatio theExchangeRate;

        /**
         * The base amount.
         */
        private final OceanusMoney theBase;

        /**
         * The local amount.
         */
        private final OceanusMoney theAmount;

        /**
         * Constructor.
         *
         * @param pCurrency the foreign currency
         * @param pAmount   the amount
         */
        private ForeignPartnerDetail(final MoneyWiseCurrency pCurrency,
                                     final OceanusMoney pAmount) {
            /* Obtain the required exchange rate */
            theBase = pAmount;
            theExchangeRate = theRateCursor.getExchangeRate(pCurrency, theDate);
            final OceanusRatio myRate = theExchangeRate;
            final Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            theAmount = theBase != null
                    ? theBase.convertCurrency(myCurrency, myRate)
                    : null;
        }
    }
}
