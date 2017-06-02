/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2017 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.analysis;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Transaction Analysis Helper.
 */
public class TransactionHelper {
    /**
     * Currency Cursor.
     */
    private final ExchangeRateCursor theRateCursor;

    /**
     * Security Cursor.
     */
    private final SecurityPriceCursor thePriceCursor;

    /**
     * Reporting currency.
     */
    private final AssetCurrency theCurrency;

    /**
     * The current transaction.
     */
    private Transaction theCurrent;

    /**
     * The current date.
     */
    private TethysDate theDate;

    /**
     * The account detail.
     */
    private TransactionDetail theAccountDetail;

    /**
     * Constructor.
     * @param pData the dataSet
     */
    protected TransactionHelper(final MoneyWiseData pData) {
        /* Create the cursors */
        theRateCursor = new ExchangeRateCursor(pData);
        thePriceCursor = new SecurityPriceCursor(pData);

        /* Note the reporting currency */
        theCurrency = pData.getDefaultCurrency();
    }

    /**
     * Obtain transaction.
     * @return the transaction
     */
    protected Transaction getTransaction() {
        return theCurrent;
    }

    /**
     * Set the transaction.
     * @param pTrans the transaction.
     */
    protected void setTransaction(final Transaction pTrans) {
        /* Record date */
        theCurrent = pTrans;
        theDate = theCurrent.getDate();

        /* Reset details */
        theAccountDetail = new TransactionDetail();
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
    public TransactionAsset getAccount() {
        return theAccountDetail.getAccount();
    }

    /**
     * Obtain partner.
     * @return the partner
     */
    public TransactionAsset getPartner() {
        return theAccountDetail.getPartner();
    }

    /**
     * Obtain direction.
     * @return the direction
     */
    public AssetDirection getDirection() {
        return theAccountDetail.getDirection();
    }

    /**
     * Obtain debit asset.
     * @return the debit asset
     */
    public TransactionAsset getDebitAsset() {
        return theAccountDetail.getDebitAsset();
    }

    /**
     * Obtain credit asset.
     * @return the credit asset
     */
    public TransactionAsset getCreditAsset() {
        return theAccountDetail.getCreditAsset();
    }

    /**
     * Obtain the category.
     * @return the category
     */
    public TransactionCategory getCategory() {
        return theAccountDetail.getCategory();
    }

    /**
     * Is this a particular category class?
     * @param pClass the category class
     * @return true/false
     */
    public boolean isCategoryClass(final TransactionCategoryClass pClass) {
        return theAccountDetail.isCategoryClass(pClass);
    }

    /**
     * Obtain the category class.
     * @return the category class
     */
    public TransactionCategoryClass getCategoryClass() {
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
     * Obtain local thirdParty amount.
     * @return the amount.
     */
    public TethysMoney getLocalThirdPartyAmount() {
        return theAccountDetail.getLocalThirdPartyAmount();
    }

    /**
     * Obtain thirdParty amount.
     * @return the amount.
     */
    public TethysMoney getThirdPartyAmount() {
        return theAccountDetail.getThirdPartyAmount();
    }

    /**
     * Obtain tax credit.
     * @return the tax credit.
     */
    public TethysMoney getTaxCredit() {
        return theAccountDetail.getTaxCredit();
    }

    /**
     * Obtain national insurance.
     * @return the national insurance.
     */
    public TethysMoney getNatInsurance() {
        return theAccountDetail.getNatInsurance();
    }

    /**
     * Obtain benefit.
     * @return the benefit.
     */
    public TethysMoney getDeemedBenefit() {
        return theAccountDetail.getBenefit();
    }

    /**
     * Obtain donation.
     * @return the donation.
     */
    public TethysMoney getCharityDonation() {
        return theAccountDetail.getDonation();
    }

    /**
     * Obtain ThirdParty.
     * @return the ThirdParty account
     */
    public Deposit getThirdParty() {
        return theAccountDetail.getThirdParty();
    }

    /**
     * Obtain debit units.
     * @return the debit units
     */
    public TethysUnits getDebitUnits() {
        return theAccountDetail.getDebitUnits();
    }

    /**
     * Obtain credit units.
     * @return the credit units
     */
    public TethysUnits getCreditUnits() {
        return theAccountDetail.getCreditUnits();
    }

    /**
     * Obtain dilution.
     * @return the dilution
     */
    public TethysDilution getDilution() {
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
        return theAccountDetail.getDebitExchangeRate();
    }

    /**
     * Obtain credit exchangeRate.
     * @return the rate
     */
    public TethysRatio getCreditExchangeRate() {
        return theAccountDetail.getCreditExchangeRate();
    }

    /**
     * Obtain thirdParty exchangeRate.
     * @return the rate
     */
    public TethysRatio getThirdPartyExchangeRate() {
        return theAccountDetail.getThirdPartyExchangeRate();
    }

    /**
     * Convert amount to reporting currency.
     * @param pCurrency the currency
     * @param pDate the date for the conversion
     * @return the reporting amount
     */
    protected TethysRatio getExchangeRate(final AssetCurrency pCurrency,
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
        private final TransactionAsset theAccount;

        /**
         * The partner.
         */
        private final TransactionAsset thePartner;

        /**
         * The direction.
         */
        private final AssetDirection theDirection;

        /**
         * The category.
         */
        private final TransactionCategory theCategory;

        /**
         * The amount.
         */
        private final TethysMoney theAmount;

        /**
         * The ThirdParty.
         */
        private final Deposit theThirdParty;

        /**
         * The thirdParty amount.
         */
        private final TethysMoney theThirdPartyAmount;

        /**
         * The tax credit.
         */
        private final TethysMoney theTaxCredit;

        /**
         * The natInsurance.
         */
        private final TethysMoney theNatIns;

        /**
         * The benefit amount.
         */
        private final TethysMoney theBenefit;

        /**
         * The donation amount.
         */
        private final TethysMoney theDonation;

        /**
         * The debit units.
         */
        private final TethysUnits theDebitUnits;

        /**
         * The credit units.
         */
        private final TethysUnits theCreditUnits;

        /**
         * The dilution.
         */
        private final TethysDilution theDilution;

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
         * The foreign thirdParty details.
         */
        private final ForeignPartnerDetail theForeignThirdParty;

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
            theNatIns = theCurrent.getNatInsurance();
            theBenefit = theCurrent.getDeemedBenefit();
            theDonation = theCurrent.getCharityDonation();
            theThirdParty = theCurrent.getThirdParty();
            theDebitUnits = theCurrent.getDebitUnits();
            theCreditUnits = theCurrent.getCreditUnits();
            theDilution = theCurrent.getDilution();

            /* Obtain the amounts */
            TethysMoney myAmount = theCurrent.getAmount();
            TethysMoney myPartnerAmount = theCurrent.getPartnerAmount();
            TethysMoney myThirdPartyAmount = theCurrent.getThirdPartyAmount();

            /* Determine account prices */
            theAccountPrice = (theAccount instanceof SecurityHolding)
                                                                      ? thePriceCursor.getSecurityPrice(((SecurityHolding) theAccount).getSecurity(), theDate)
                                                                      : null;
            thePartnerPrice = (thePartner instanceof SecurityHolding)
                                                                      ? thePriceCursor.getSecurityPrice(((SecurityHolding) thePartner).getSecurity(), theDate)
                                                                      : null;

            /* Determine foreign account detail */
            AssetCurrency myActCurrency = theAccount.getAssetCurrency();
            theForeignAccount = MetisDifference.isEqual(myActCurrency, theCurrency)
                                                                                    ? null
                                                                                    : new ForeignAccountDetail(this, myActCurrency, myAmount);

            /* If we have a partner amount */
            myActCurrency = thePartner.getAssetCurrency();
            theForeignPartner = myActCurrency == null
                                || MetisDifference.isEqual(myActCurrency, theCurrency)
                                                                                       ? null
                                                                                       : new ForeignPartnerDetail(myActCurrency, myPartnerAmount);

            /* If we have a thirdParty account */
            if (theThirdParty != null) {
                /* Determine foreign thirdParty detail */
                myActCurrency = theThirdParty.getAssetCurrency();
                theForeignThirdParty = MetisDifference.isEqual(myActCurrency, theCurrency)
                                                                                           ? null
                                                                                           : new ForeignPartnerDetail(myActCurrency, myThirdPartyAmount);
            } else {
                theForeignThirdParty = null;
            }

            /* Determine the local amounts */
            theAmount = theForeignAccount == null
                                                  ? myAmount
                                                  : theForeignPartner == null
                                                                              ? myPartnerAmount
                                                                              : theForeignAccount.theAmount;
            theThirdPartyAmount = theForeignThirdParty == null
                                                               ? myThirdPartyAmount
                                                               : theForeignThirdParty.theAmount;
        }

        /**
         * Obtain account.
         * @return the account
         */
        private TransactionAsset getAccount() {
            return theAccount;
        }

        /**
         * Obtain partner.
         * @return the partner
         */
        private TransactionAsset getPartner() {
            return thePartner;
        }

        /**
         * Obtain direction.
         * @return the direction
         */
        private AssetDirection getDirection() {
            return theDirection;
        }

        /**
         * Obtain debit asset.
         * @return the debit asset
         */
        private TransactionAsset getDebitAsset() {
            return theDirection.isFrom()
                                         ? thePartner
                                         : theAccount;
        }

        /**
         * Obtain credit asset.
         * @return the credit asset
         */
        private TransactionAsset getCreditAsset() {
            return theDirection.isTo()
                                       ? thePartner
                                       : theAccount;
        }

        /**
         * Obtain ThirdParty.
         * @return the ThirdParty account
         */
        private Deposit getThirdParty() {
            return theThirdParty;
        }

        /**
         * Obtain category.
         * @return the category class
         */
        private TransactionCategory getCategory() {
            return theCategory;
        }

        /**
         * Is this a particular category class?
         * @param pClass the category class
         * @return true/false
         */
        private boolean isCategoryClass(final TransactionCategoryClass pClass) {
            return theCategory.isCategoryClass(pClass);
        }

        /**
         * Obtain category class?
         * @return the category class
         */
        private TransactionCategoryClass getCategoryClass() {
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
         * Obtain local thirdParty amount.
         * @return the local thirdParty amount
         */
        private TethysMoney getLocalThirdPartyAmount() {
            return theThirdPartyAmount;
        }

        /**
         * Obtain thirdParty amount.
         * @return the thirdParty amount
         */
        private TethysMoney getThirdPartyAmount() {
            return theForeignThirdParty == null
                                                ? theThirdPartyAmount
                                                : theForeignThirdParty.theBase;
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
        private TethysRatio getThirdPartyExchangeRate() {
            return theForeignThirdParty == null
                                                ? null
                                                : theForeignThirdParty.theExchangeRate;
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
         * Obtain natInsurance.
         * @return the national insurance
         */
        private TethysMoney getNatInsurance() {
            return theForeignAccount != null
                                             ? theForeignAccount.theNatIns
                                             : theNatIns;
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
        private TethysMoney getDonation() {
            return theForeignAccount != null
                                             ? theForeignAccount.theDonation
                                             : theDonation;
        }

        /**
         * Obtain debit units.
         * @return the debit units
         */
        private TethysUnits getDebitUnits() {
            return theDebitUnits;
        }

        /**
         * Obtain credit units.
         * @return the credit units
         */
        private TethysUnits getCreditUnits() {
            return theCreditUnits;
        }

        /**
         * Obtain dilution.
         * @return the dilution
         */
        private TethysDilution getDilution() {
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
         * The natInsurance.
         */
        private final TethysMoney theNatIns;

        /**
         * The benefit amount.
         */
        private final TethysMoney theBenefit;

        /**
         * The donation amount.
         */
        private final TethysMoney theDonation;

        /**
         * Constructor.
         * @param pTrans the transaction detail
         * @param pCurrency the foreign currency
         * @param pAmount the amount
         */
        private ForeignAccountDetail(final TransactionDetail pTrans,
                                     final AssetCurrency pCurrency,
                                     final TethysMoney pAmount) {
            /* Obtain the required exchange rate */
            theBase = pAmount;
            theExchangeRate = theRateCursor.getExchangeRate(pCurrency, theDate);
            TethysRatio myRate = theExchangeRate.getInverseRatio();
            Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            theAmount = theBase != null
                                        ? theBase.convertCurrency(myCurrency, myRate)
                                        : null;

            /* Obtain tax value */
            TethysMoney myValue = pTrans.theTaxCredit;
            theTaxCredit = (myValue != null)
                                             ? myValue.convertCurrency(myCurrency, myRate)
                                             : null;
            /* Obtain NatIns */
            myValue = pTrans.theNatIns;
            theNatIns = (myValue != null)
                                          ? myValue.convertCurrency(myCurrency, myRate)
                                          : null;

            /* Obtain benefit */
            myValue = pTrans.theBenefit;
            theBenefit = (myValue != null)
                                           ? myValue.convertCurrency(myCurrency, myRate)
                                           : null;

            /* Obtain donation */
            myValue = pTrans.theDonation;
            theDonation = (myValue != null)
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
        private ForeignPartnerDetail(final AssetCurrency pCurrency,
                                     final TethysMoney pAmount) {
            /* Obtain the required exchange rate */
            theBase = pAmount;
            theExchangeRate = theRateCursor.getExchangeRate(pCurrency, theDate);
            TethysRatio myRate = theExchangeRate.getInverseRatio();
            Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            theAmount = theBase != null
                                        ? theBase.convertCurrency(myCurrency, myRate)
                                        : null;
        }
    }
}
