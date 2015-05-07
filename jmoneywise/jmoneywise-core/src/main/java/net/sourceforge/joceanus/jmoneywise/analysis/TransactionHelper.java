/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
 * $URL: http://localhost/svn/Finance/jOceanus/trunk/jmoneywise/jmoneywise-core/src/main/java/net/sourceforge/joceanus/jmoneywise/analysis/AnalysisResource.java $
 * $Revision: 602 $
 * $Author: Tony $
 * $Date: 2015-04-26 07:19:38 +0100 (Sun, 26 Apr 2015) $
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.analysis;

import java.util.Currency;

import net.sourceforge.joceanus.jmetis.data.Difference;
import net.sourceforge.joceanus.jmoneywise.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.data.Deposit;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.SecurityHolding;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.data.TransactionAsset;
import net.sourceforge.joceanus.jmoneywise.data.TransactionCategory;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

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
    private JDateDay theDate;

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
    public JDateDay getDate() {
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
    public JMoney getDebitAmount() {
        return theAccountDetail.getDebitAmount();
    }

    /**
     * Obtain local debit amount.
     * @return the debit amount.
     */
    public JMoney getLocalDebitAmount() {
        return theAccountDetail.getLocalDebitAmount();
    }

    /**
     * Obtain debit exchange rate.
     * @return the debit exchange rate.
     */
    public JRatio getDebitExchangeRate() {
        return theAccountDetail.getDebitExchangeRate();
    }

    /**
     * Obtain credit amount.
     * @return the credit amount.
     */
    public JMoney getCreditAmount() {
        return theAccountDetail.getCreditAmount();
    }

    /**
     * Obtain local credit amount.
     * @return the credit amount.
     */
    public JMoney getLocalCreditAmount() {
        return theAccountDetail.getLocalCreditAmount();
    }

    /**
     * Obtain credit exchange rate.
     * @return the credit exchange rate.
     */
    public JRatio getCreditExchangeRate() {
        return theAccountDetail.getCreditExchangeRate();
    }

    /**
     * Obtain tax credit.
     * @return the tax credit.
     */
    public JMoney getTaxCredit() {
        return theAccountDetail.getTaxCredit();
    }

    /**
     * Obtain national insurance.
     * @return the national insurance.
     */
    public JMoney getNatInsurance() {
        return theAccountDetail.getNatInsurance();
    }

    /**
     * Obtain benefit.
     * @return the benefit.
     */
    public JMoney getDeemedBenefit() {
        return theAccountDetail.getBenefit();
    }

    /**
     * Obtain donation.
     * @return the donation.
     */
    public JMoney getCharityDonation() {
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
    public JUnits getDebitUnits() {
        return theAccountDetail.getDebitUnits();
    }

    /**
     * Obtain credit units.
     * @return the credit units
     */
    public JUnits getCreditUnits() {
        return theAccountDetail.getCreditUnits();
    }

    /**
     * Obtain dilution.
     * @return the dilution
     */
    public JDilution getDilution() {
        return theAccountDetail.getDilution();
    }

    /**
     * Obtain debit price.
     * @return the debit price
     */
    public JPrice getDebitPrice() {
        return theAccountDetail.getDebitPrice();
    }

    /**
     * Obtain credit price.
     * @return the credit price
     */
    public JPrice getCreditPrice() {
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
        private final JMoney theAmount;

        /**
         * The partner amount.
         */
        private final JMoney thePartnerAmount;

        /**
         * The ThirdParty.
         */
        private final Deposit theThirdParty;

        /**
         * The tax credit.
         */
        private final JMoney theTaxCredit;

        /**
         * The natInsurance.
         */
        private final JMoney theNatIns;

        /**
         * The benefit amount.
         */
        private final JMoney theBenefit;

        /**
         * The donation amount.
         */
        private final JMoney theDonation;

        /**
         * The debit units.
         */
        private final JUnits theDebitUnits;

        /**
         * The credit units.
         */
        private final JUnits theCreditUnits;

        /**
         * The dilution.
         */
        private final JDilution theDilution;

        /**
         * The account price.
         */
        private final JPrice theAccountPrice;

        /**
         * The partner price.
         */
        private final JPrice thePartnerPrice;

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
            theForeignAccount = Difference.isEqual(myActCurrency, theCurrency)
                                                                              ? null
                                                                              : new ForeignAccountDetail(this, myActCurrency);

            /* If we have a partner amount */
            if (thePartnerAmount != null) {
                /* Determine foreign partner detail */
                myActCurrency = thePartner.getAssetCurrency();
                theForeignPartner = Difference.isEqual(myActCurrency, theCurrency)
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
        private JMoney getDebitAmount() {
            return theDirection.isFrom() || thePartnerAmount == null
                                                                    ? theAmount
                                                                    : thePartnerAmount;
        }

        /**
         * Obtain local debit amount.
         * @return the local debit amount
         */
        private JMoney getLocalDebitAmount() {
            return theDirection.isFrom() && thePartnerAmount != null
                                                                    ? getLocalPartnerAmount()
                                                                    : getLocalAmount();
        }

        /**
         * Obtain local account amount.
         * @return the local partner amount
         */
        private JMoney getLocalAmount() {
            return theForeignAccount != null
                                            ? theForeignAccount.theAmount
                                            : theAmount;
        }

        /**
         * Obtain local partner amount.
         * @return the local partner amount
         */
        private JMoney getLocalPartnerAmount() {
            return theForeignPartner != null
                                            ? theForeignPartner.theAmount
                                            : thePartnerAmount;
        }

        /**
         * Obtain debit exchange rate.
         * @return the debit exchange rate
         */
        private JRatio getDebitExchangeRate() {
            return theDirection.isFrom() || theForeignPartner == null
                                                                     ? theForeignAccount.theExchangeRate
                                                                     : theForeignPartner.theExchangeRate;
        }

        /**
         * Obtain credit amount.
         * @return the credit amount
         */
        private JMoney getCreditAmount() {
            return theDirection.isTo() && thePartnerAmount != null
                                                                  ? thePartnerAmount
                                                                  : theAmount;
        }

        /**
         * Obtain local credit amount.
         * @return the local debit amount
         */
        private JMoney getLocalCreditAmount() {
            return theDirection.isTo() && thePartnerAmount != null
                                                                  ? getLocalPartnerAmount()
                                                                  : getLocalAmount();
        }

        /**
         * Obtain credit exchange rate.
         * @return the credit exchange rate
         */
        private JRatio getCreditExchangeRate() {
            return theDirection.isTo() && theForeignPartner != null
                                                                   ? theForeignPartner.theExchangeRate
                                                                   : theForeignAccount.theExchangeRate;
        }

        /**
         * Obtain debit price.
         * @return the debit price
         */
        private JPrice getDebitPrice() {
            return theDirection.isFrom()
                                        ? thePartnerPrice
                                        : theAccountPrice;
        }

        /**
         * Obtain credit price.
         * @return the credit price
         */
        private JPrice getCreditPrice() {
            return theDirection.isTo()
                                      ? thePartnerPrice
                                      : theAccountPrice;
        }

        /**
         * Obtain taxCredit.
         * @return the tax credit
         */
        private JMoney getTaxCredit() {
            return theForeignAccount != null
                                            ? theForeignAccount.theTaxCredit
                                            : theTaxCredit;
        }

        /**
         * Obtain natInsurance.
         * @return the national insurance
         */
        private JMoney getNatInsurance() {
            return theForeignAccount != null
                                            ? theForeignAccount.theNatIns
                                            : theNatIns;
        }

        /**
         * Obtain benefit.
         * @return the benefit
         */
        private JMoney getBenefit() {
            return theForeignAccount != null
                                            ? theForeignAccount.theBenefit
                                            : theBenefit;
        }

        /**
         * Obtain donation.
         * @return the donation
         */
        private JMoney getDonation() {
            return theForeignAccount != null
                                            ? theForeignAccount.theDonation
                                            : theDonation;
        }

        /**
         * Obtain debit units.
         * @return the debit units
         */
        private JUnits getDebitUnits() {
            return theDebitUnits;
        }

        /**
         * Obtain credit units.
         * @return the credit units
         */
        private JUnits getCreditUnits() {
            return theCreditUnits;
        }

        /**
         * Obtain dilution.
         * @return the dilution
         */
        private JDilution getDilution() {
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
        private final JRatio theExchangeRate;

        /**
         * The amount.
         */
        private final JMoney theAmount;

        /**
         * The tax credit.
         */
        private final JMoney theTaxCredit;

        /**
         * The natInsurance.
         */
        private final JMoney theNatIns;

        /**
         * The benefit amount.
         */
        private final JMoney theBenefit;

        /**
         * The donation amount.
         */
        private final JMoney theDonation;

        /**
         * Constructor.
         * @param pTrans the transaction detail
         * @param pCurrency the foreign currency
         */
        private ForeignAccountDetail(final TransactionDetail pTrans,
                                     final AssetCurrency pCurrency) {
            /* Obtain the required exchange rate */
            theExchangeRate = theRateCursor.getExchangeRate(pCurrency, theDate);
            JRatio myRate = theExchangeRate.getInverseRatio();
            Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            JMoney myValue = pTrans.theAmount;
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
        private final JRatio theExchangeRate;

        /**
         * The amount.
         */
        private final JMoney theAmount;

        /**
         * Constructor.
         * @param pTrans the transaction detail
         * @param pCurrency the foreign currency
         */
        private ForeignPartnerDetail(final TransactionDetail pTrans,
                                     final AssetCurrency pCurrency) {
            /* Obtain the required exchange rate */
            theExchangeRate = theRateCursor.getExchangeRate(pCurrency, theDate);
            JRatio myRate = theExchangeRate.getInverseRatio();
            Currency myCurrency = theCurrency.getCurrency();

            /* Obtain local amount */
            JMoney myValue = pTrans.theAmount;
            theAmount = myValue.convertCurrency(myCurrency, myRate);
        }
    }
}
