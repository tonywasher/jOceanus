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
package net.sourceforge.joceanus.jmoneywise.data.analysis.data;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

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
    private TethysDate theDate;

    /**
     * The account detail.
     */
    private TransactionDetail theAccountDetail;

    /**
     * The debit XchangeRate.
     */
    private TethysRatio theDebitXchangeRate;

    /**
     * Constructor.
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
     * @return the transaction
     */
    public MoneyWiseTransaction getTransaction() {
        return theCurrent;
    }

    /**
     * Set the transaction.
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
     * @param pSecurity the security.
     */
    public void setSecurity(final MoneyWiseSecurity pSecurity) {
        final MoneyWiseCurrency myCurr = pSecurity.getAssetCurrency();
        final boolean isForeign = !MetisDataDifference.isEqual(myCurr, theCurrency);
        theDebitXchangeRate = isForeign ? theRateCursor.getExchangeRate(myCurr, theDate) : null;
    }

    /**
     * Obtain date.
     * @return the date
     */
    public TethysDate getDate() {
        return theDate;
    }

    /**
     * Obtain account.
     * @return the account
     */
    public MoneyWiseTransAsset getAccount() {
        return theAccountDetail.getAccount();
    }

    /**
     * Obtain partner.
     * @return the partner
     */
    public MoneyWiseTransAsset getPartner() {
        return theAccountDetail.getPartner();
    }

    /**
     * Obtain direction.
     * @return the direction
     */
    public MoneyWiseAssetDirection getDirection() {
        return theAccountDetail.getDirection();
    }

    /**
     * Obtain debit asset.
     * @return the debit asset
     */
    public MoneyWiseTransAsset getDebitAsset() {
        return theAccountDetail.getDebitAsset();
    }

    /**
     * Obtain credit asset.
     * @return the credit asset
     */
    public MoneyWiseTransAsset getCreditAsset() {
        return theAccountDetail.getCreditAsset();
    }

    /**
     * Obtain the category.
     * @return the category
     */
    public MoneyWiseTransCategory getCategory() {
        return theAccountDetail.getCategory();
    }

    /**
     * Is this a particular category class?
     * @param pClass the category class
     * @return true/false
     */
    public boolean isCategoryClass(final MoneyWiseTransCategoryClass pClass) {
        return theAccountDetail.isCategoryClass(pClass);
    }

    /**
     * Obtain the category class.
     * @return the category class
     */
    public MoneyWiseTransCategoryClass getCategoryClass() {
        return theAccountDetail.getCategoryClass();
    }

    /**
     * Obtain debit amount.
     * @return the debit amount.
     */
    public TethysMoney getDebitAmount() {
        return theAccountDetail.getDebitAmount();
    }

    /**
     * Obtain local amount.
     * @return the amount.
     */
    public TethysMoney getLocalAmount() {
        return theAccountDetail.getLocalAmount();
    }

    /**
     * Obtain credit amount.
     * @return the credit amount.
     */
    public TethysMoney getCreditAmount() {
        return theAccountDetail.getCreditAmount();
    }

    /**
     * Obtain local returnedCash.
     * @return the returnedCash.
     */
    public TethysMoney getLocalReturnedCash() {
        return theAccountDetail.getLocalReturnedCash();
    }

    /**
     * Obtain returnedCash.
     * @return the returned cash.
     */
    public TethysMoney getReturnedCash() {
        return theAccountDetail.getReturnedCash();
    }

    /**
     * Obtain tax credit.
     * @return the tax credit.
     */
    public TethysMoney getTaxCredit() {
        return theAccountDetail.getTaxCredit();
    }

    /**
     * Obtain employer national insurance.
     * @return the national insurance.
     */
    public TethysMoney getEmployerNatIns() {
        return theAccountDetail.getEmployerNatIns();
    }

    /**
     * Obtain employee national insurance.
     * @return the national insurance.
     */
    public TethysMoney getEmployeeNatIns() {
        return theAccountDetail.getEmployeeNatIns();
    }

    /**
     * Obtain benefit.
     * @return the benefit.
     */
    public TethysMoney getDeemedBenefit() {
        return theAccountDetail.getBenefit();
    }

    /**
     * Obtain withheld.
     * @return the withheld.
     */
    public TethysMoney getWithheld() {
        return theAccountDetail.getWithheld();
    }

    /**
     * Obtain returnedCash Account.
     * @return the returnedCash account
     */
    public MoneyWiseTransAsset getReturnedCashAccount() {
        return theAccountDetail.getReturnedCashAccount();
    }

    /**
     * Obtain debit units.
     * @return the debit units
     */
    public TethysUnits getDebitUnits() {
        TethysUnits myUnits = getDirection().isTo()
                ? getAccountDeltaUnits()
                : getPartnerDeltaUnits();
        if (myUnits != null) {
            myUnits = new TethysUnits(myUnits);
            myUnits.negate();
        }
        return myUnits;
    }

    /**
     * Obtain credit units.
     * @return the debit units
     */
    public TethysUnits getCreditUnits() {
        return getDirection().isFrom()
                ? getAccountDeltaUnits()
                : getPartnerDeltaUnits();
    }

    /**
     * Obtain account delta units.
     * @return the delta units
     */
    public TethysUnits getAccountDeltaUnits() {
        return theAccountDetail.getAccountDeltaUnits();
    }

    /**
     * Obtain partner delta units.
     * @return the delta units
     */
    public TethysUnits getPartnerDeltaUnits() {
        return theAccountDetail.getPartnerDeltaUnits();
    }

    /**
     * Obtain dilution.
     * @return the dilution
     */
    public TethysRatio getDilution() {
        return theAccountDetail.getDilution();
    }

    /**
     * Obtain debit price.
     * @return the debit price
     */
    public TethysPrice getDebitPrice() {
        return theAccountDetail.getDebitPrice();
    }

    /**
     * Obtain credit price.
     * @return the credit price
     */
    public TethysPrice getCreditPrice() {
        return theAccountDetail.getCreditPrice();
    }

    /**
     * Obtain debit exchangeRate.
     * @return the rate
     */
    public TethysRatio getDebitExchangeRate() {
        return theDebitXchangeRate;
    }

    /**
     * Obtain credit exchangeRate.
     * @return the rate
     */
    public TethysRatio getCreditExchangeRate() {
        return theAccountDetail.getCreditExchangeRate();
    }

    /**
     * Obtain returnedCash exchangeRate.
     * @return the rate
     */
    public TethysRatio getReturnedCashExchangeRate() {
        return theAccountDetail.getReturnedCashExchangeRate();
    }

    /**
     * Convert amount to reporting currency.
     * @param pCurrency the currency
     * @param pDate the date for the conversion
     * @return the reporting amount
     */
    protected TethysRatio getExchangeRate(final MoneyWiseCurrency pCurrency,
                                          final TethysDate pDate) {
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
        private final TethysMoney theAmount;

        /**
         * The ReturnedCashAccount.
         */
        private final MoneyWiseTransAsset theReturnedCashAccount;

        /**
         * The returnedCash.
         */
        private final TethysMoney theReturnedCash;

        /**
         * The tax credit.
         */
        private final TethysMoney theTaxCredit;

        /**
         * The Employer natIns.
         */
        private final TethysMoney theEmployerNatIns;

        /**
         * The Employee natIns.
         */
        private final TethysMoney theEmployeeNatIns;

        /**
         * The benefit amount.
         */
        private final TethysMoney theBenefit;

        /**
         * The withheld amount.
         */
        private final TethysMoney theWithheld;

        /**
         * The account delta units.
         */
        private final TethysUnits theAccountUnits;

        /**
         * The partner delta units.
         */
        private final TethysUnits thePartnerUnits;

        /**
         * The dilution.
         */
        private final TethysRatio theDilution;

        /**
         * The account price.
         */
        private final TethysPrice theAccountPrice;

        /**
         * The partner price.
         */
        private final TethysPrice thePartnerPrice;

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
            final TethysMoney myAmount = theCurrent.getAmount();
            final TethysMoney myPartnerAmount = theCurrent.getPartnerAmount();
            final TethysMoney myReturnedCash = theCurrent.getReturnedCash();

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
         * @return the account
         */
        private MoneyWiseTransAsset getAccount() {
            return theAccount;
        }

        /**
         * Obtain partner.
         * @return the partner
         */
        private MoneyWiseTransAsset getPartner() {
            return thePartner;
        }

        /**
         * Obtain direction.
         * @return the direction
         */
        private MoneyWiseAssetDirection getDirection() {
            return theDirection;
        }

        /**
         * Obtain debit asset.
         * @return the debit asset
         */
        private MoneyWiseTransAsset getDebitAsset() {
            return theDirection.isFrom()
                    ? thePartner
                    : theAccount;
        }

        /**
         * Obtain credit asset.
         * @return the credit asset
         */
        private MoneyWiseTransAsset getCreditAsset() {
            return theDirection.isTo()
                    ? thePartner
                    : theAccount;
        }

        /**
         * Obtain returnedCash account.
         * @return the returnedCash account
         */
        private MoneyWiseTransAsset getReturnedCashAccount() {
            return theReturnedCashAccount;
        }

        /**
         * Obtain category.
         * @return the category class
         */
        private MoneyWiseTransCategory getCategory() {
            return theCategory;
        }

        /**
         * Is this a particular category class?
         * @param pClass the category class
         * @return true/false
         */
        private boolean isCategoryClass(final MoneyWiseTransCategoryClass pClass) {
            return theCategory.isCategoryClass(pClass);
        }

        /**
         * Obtain category class?
         * @return the category class
         */
        private MoneyWiseTransCategoryClass getCategoryClass() {
            return theCategory.getCategoryTypeClass();
        }

        /**
         * Obtain debit amount.
         * @return the debit amount
         */
        private TethysMoney getDebitAmount() {
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
         * @return the local debit amount
         */
        private TethysMoney getLocalAmount() {
            return theAmount;
        }

        /**
         * Obtain credit amount.
         * @return the credit amount
         */
        private TethysMoney getCreditAmount() {
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
         * @return the local returnedCash
         */
        private TethysMoney getLocalReturnedCash() {
            return theReturnedCash;
        }

        /**
         * Obtain returnedCash.
         * @return the returnedCash
         */
        private TethysMoney getReturnedCash() {
            return theForeignReturnedCash == null
                    ? theReturnedCash
                    : theForeignReturnedCash.theBase;
        }

        /**
         * Obtain debit price.
         * @return the debit price
         */
        private TethysPrice getDebitPrice() {
            return theDirection.isFrom()
                    ? thePartnerPrice
                    : theAccountPrice;
        }

        /**
         * Obtain credit price.
         * @return the credit price
         */
        private TethysPrice getCreditPrice() {
            return theDirection.isTo()
                    ? thePartnerPrice
                    : theAccountPrice;
        }

        /**
         * Obtain debit exchangeRate.
         * @return the rate
         */
        private TethysRatio getDebitExchangeRate() {
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
         * @return the rate
         */
        private TethysRatio getCreditExchangeRate() {
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
         * @return the rate
         */
        private TethysRatio getReturnedCashExchangeRate() {
            return theForeignReturnedCash == null
                    ? null
                    : theForeignReturnedCash.theExchangeRate;
        }

        /**
         * Obtain taxCredit.
         * @return the tax credit
         */
        private TethysMoney getTaxCredit() {
            return theForeignAccount != null
                    ? theForeignAccount.theTaxCredit
                    : theTaxCredit;
        }

        /**
         * Obtain employer natInsurance.
         * @return the national insurance
         */
        private TethysMoney getEmployerNatIns() {
            return theForeignAccount != null
                    ? theForeignAccount.theEmployerNatIns
                    : theEmployerNatIns;
        }

        /**
         * Obtain employee natInsurance.
         * @return the national insurance
         */
        private TethysMoney getEmployeeNatIns() {
            return theForeignAccount != null
                    ? theForeignAccount.theEmployeeNatIns
                    : theEmployeeNatIns;
        }

        /**
         * Obtain benefit.
         * @return the benefit
         */
        private TethysMoney getBenefit() {
            return theForeignAccount != null
                    ? theForeignAccount.theBenefit
                    : theBenefit;
        }

        /**
         * Obtain donation.
         * @return the donation
         */
        private TethysMoney getWithheld() {
            return theForeignAccount != null
                    ? theForeignAccount.theWithheld
                    : theWithheld;
        }

        /**
         * Obtain account delta units.
         * @return the delta units
         */
        private TethysUnits getAccountDeltaUnits() {
            return theAccountUnits;
        }

        /**
         * Obtain partner delta units.
         * @return the partner delta units
         */
        private TethysUnits getPartnerDeltaUnits() {
            return thePartnerUnits;
        }

        /**
         * Obtain dilution.
         * @return the dilution
         */
        private TethysRatio getDilution() {
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
        private final TethysRatio theExchangeRate;

        /**
         * The base amount.
         */
        private final TethysMoney theBase;

        /**
         * The amount.
         */
        private final TethysMoney theAmount;

        /**
         * The tax credit.
         */
        private final TethysMoney theTaxCredit;

        /**
         * The employer natInsurance.
         */
        private final TethysMoney theEmployerNatIns;

        /**
         * The employee natInsurance.
         */
        private final TethysMoney theEmployeeNatIns;

        /**
         * The benefit amount.
         */
        private final TethysMoney theBenefit;

        /**
         * The withheld amount.
         */
        private final TethysMoney theWithheld;

        /**
         * Constructor.
         * @param pTrans the transaction detail
         * @param pCurrency the foreign currency
         * @param pAmount the amount
         */
        private ForeignAccountDetail(final TransactionDetail pTrans,
                                     final MoneyWiseCurrency pCurrency,
                                     final TethysMoney pAmount) {
            /* Obtain the required exchange rate */
            theBase = pAmount;
            final TethysRatio myEventRate = theCurrent.getExchangeRate();
            final TethysRatio myRate = myEventRate == null
                    ? theRateCursor.getExchangeRate(pCurrency, theDate)
                    : myEventRate;
            theExchangeRate = myRate;
            final Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            theAmount = theBase != null
                    ? theBase.convertCurrency(myCurrency, myRate)
                    : null;

            /* Obtain tax value */
            TethysMoney myValue = pTrans.theTaxCredit;
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
        private final TethysRatio theExchangeRate;

        /**
         * The base amount.
         */
        private final TethysMoney theBase;

        /**
         * The local amount.
         */
        private final TethysMoney theAmount;

        /**
         * Constructor.
         * @param pCurrency the foreign currency
         * @param pAmount the amount
         */
        private ForeignPartnerDetail(final MoneyWiseCurrency pCurrency,
                                     final TethysMoney pAmount) {
            /* Obtain the required exchange rate */
            theBase = pAmount;
            theExchangeRate = theRateCursor.getExchangeRate(pCurrency, theDate);
            final TethysRatio myRate = theExchangeRate;
            final Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            theAmount = theBase != null
                    ? theBase.convertCurrency(myCurrency, myRate)
                    : null;
        }
    }
}
