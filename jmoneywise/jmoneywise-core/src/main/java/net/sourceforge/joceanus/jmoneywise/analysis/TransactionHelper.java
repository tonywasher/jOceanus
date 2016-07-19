/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.data.MetisDifference;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * Resource IDs for jMoneyWise Analysis Fields.
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
     * Obtain local debit amount.
     * @return the debit amount.
     */
    public TethysMoney getLocalDebitAmount() {
        return theAccountDetail.getLocalDebitAmount();
    }

    /**
     * Obtain debit exchange rate.
     * @return the debit exchange rate.
     */
    public TethysRatio getDebitExchangeRate() {
        return theAccountDetail.getDebitExchangeRate();
    }

    /**
     * Obtain credit amount.
     * @return the credit amount.
     */
    public TethysMoney getCreditAmount() {
        return theAccountDetail.getCreditAmount();
    }

    /**
     * Obtain local credit amount.
     * @return the credit amount.
     */
    public TethysMoney getLocalCreditAmount() {
        return theAccountDetail.getLocalCreditAmount();
    }

    /**
     * Obtain credit exchange rate.
     * @return the credit exchange rate.
     */
    public TethysRatio getCreditExchangeRate() {
        return theAccountDetail.getCreditExchangeRate();
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
         * The partner amount.
         */
        private final TethysMoney thePartnerAmount;

        /**
         * The ThirdParty.
         */
        private final Deposit theThirdParty;

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
         * The foreign account details.
         */
        private final ForeignPartnerDetail theForeignPartner;

        /**
         * Constructor.
         */
        private TransactionDetail() {
            /* Store detail */
            theAccount = theCurrent.getAccount();
            thePartner = theCurrent.getPartner();
            theDirection = theCurrent.getDirection();
            theCategory = theCurrent.getCategory();
            theAmount = theCurrent.getAmount();
            theTaxCredit = theCurrent.getTaxCredit();
            theNatIns = theCurrent.getNatInsurance();
            theBenefit = theCurrent.getDeemedBenefit();
            theDonation = theCurrent.getCharityDonation();
            thePartnerAmount = theCurrent.getPartnerAmount();
            theThirdParty = theCurrent.getThirdParty();
            theDebitUnits = theCurrent.getDebitUnits();
            theCreditUnits = theCurrent.getCreditUnits();
            theDilution = theCurrent.getDilution();

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
                                                                              : new ForeignAccountDetail(this, myActCurrency);

            /* If we have a partner amount */
            if (thePartnerAmount != null) {
                /* Determine foreign partner detail */
                myActCurrency = thePartner.getAssetCurrency();
                theForeignPartner = MetisDifference.isEqual(myActCurrency, theCurrency)
                                                                                  ? null
                                                                                  : new ForeignPartnerDetail(this, myActCurrency);
            } else {
                theForeignPartner = null;
            }
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
            return theDirection.isFrom() || thePartnerAmount == null
                                                                    ? theAmount
                                                                    : thePartnerAmount;
        }

        /**
         * Obtain local debit amount.
         * @return the local debit amount
         */
        private TethysMoney getLocalDebitAmount() {
            return theDirection.isFrom() && thePartnerAmount != null
                                                                    ? getLocalPartnerAmount()
                                                                    : getLocalAmount();
        }

        /**
         * Obtain local account amount.
         * @return the local partner amount
         */
        private TethysMoney getLocalAmount() {
            return theForeignAccount != null
                                            ? theForeignAccount.theAmount
                                            : theAmount;
        }

        /**
         * Obtain local partner amount.
         * @return the local partner amount
         */
        private TethysMoney getLocalPartnerAmount() {
            return theForeignPartner != null
                                            ? theForeignPartner.theAmount
                                            : thePartnerAmount;
        }

        /**
         * Obtain debit exchange rate.
         * @return the debit exchange rate
         */
        private TethysRatio getDebitExchangeRate() {
            return theDirection.isFrom() || theForeignPartner == null
                                                                     ? theForeignAccount.theExchangeRate
                                                                     : theForeignPartner.theExchangeRate;
        }

        /**
         * Obtain credit amount.
         * @return the credit amount
         */
        private TethysMoney getCreditAmount() {
            return theDirection.isTo() && thePartnerAmount != null
                                                                  ? thePartnerAmount
                                                                  : theAmount;
        }

        /**
         * Obtain local credit amount.
         * @return the local debit amount
         */
        private TethysMoney getLocalCreditAmount() {
            return theDirection.isTo() && thePartnerAmount != null
                                                                  ? getLocalPartnerAmount()
                                                                  : getLocalAmount();
        }

        /**
         * Obtain credit exchange rate.
         * @return the credit exchange rate
         */
        private TethysRatio getCreditExchangeRate() {
            return theDirection.isTo() && theForeignPartner != null
                                                                   ? theForeignPartner.theExchangeRate
                                                                   : theForeignAccount.theExchangeRate;
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
         */
        private ForeignAccountDetail(final TransactionDetail pTrans,
                                     final AssetCurrency pCurrency) {
            /* Obtain the required exchange rate */
            theExchangeRate = theRateCursor.getExchangeRate(pCurrency, theDate);
            TethysRatio myRate = theExchangeRate.getInverseRatio();
            Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            TethysMoney myValue = pTrans.theAmount;
            theAmount = myValue.convertCurrency(myCurrency, myRate);

            /* Obtain tax value */
            myValue = pTrans.theTaxCredit;
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
         * The amount.
         */
        private final TethysMoney theAmount;

        /**
         * Constructor.
         * @param pTrans the transaction detail
         * @param pCurrency the foreign currency
         */
        private ForeignPartnerDetail(final TransactionDetail pTrans,
                                     final AssetCurrency pCurrency) {
            /* Obtain the required exchange rate */
            theExchangeRate = theRateCursor.getExchangeRate(pCurrency, theDate);
            TethysRatio myRate = theExchangeRate.getInverseRatio();
            Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            TethysMoney myValue = pTrans.theAmount;
            theAmount = myValue.convertCurrency(myCurrency, myRate);
        }
    }
}
