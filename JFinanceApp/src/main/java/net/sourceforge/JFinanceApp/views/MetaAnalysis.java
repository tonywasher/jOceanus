/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package net.sourceforge.JFinanceApp.views;

import java.util.Calendar;
import java.util.Iterator;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDateDay.JDateDay;
import net.sourceforge.JDecimal.JMoney;
import net.sourceforge.JFinanceApp.data.Account;
import net.sourceforge.JFinanceApp.data.Account.AccountList;
import net.sourceforge.JFinanceApp.data.AccountType;
import net.sourceforge.JFinanceApp.data.FinanceData;
import net.sourceforge.JFinanceApp.data.StaticClass.TaxClass;
import net.sourceforge.JFinanceApp.data.StaticClass.TransClass;
import net.sourceforge.JFinanceApp.data.TaxRegime;
import net.sourceforge.JFinanceApp.data.TaxType;
import net.sourceforge.JFinanceApp.data.TaxYear;
import net.sourceforge.JFinanceApp.data.TransactionType;
import net.sourceforge.JFinanceApp.views.Analysis.ActDetail;
import net.sourceforge.JFinanceApp.views.Analysis.AnalysisBucket;
import net.sourceforge.JFinanceApp.views.Analysis.AnalysisState;
import net.sourceforge.JFinanceApp.views.Analysis.AssetAccount;
import net.sourceforge.JFinanceApp.views.Analysis.AssetSummary;
import net.sourceforge.JFinanceApp.views.Analysis.AssetTotal;
import net.sourceforge.JFinanceApp.views.Analysis.BucketList;
import net.sourceforge.JFinanceApp.views.Analysis.DebtAccount;
import net.sourceforge.JFinanceApp.views.Analysis.ExternalAccount;
import net.sourceforge.JFinanceApp.views.Analysis.ExternalTotal;
import net.sourceforge.JFinanceApp.views.Analysis.MarketTotal;
import net.sourceforge.JFinanceApp.views.Analysis.MoneyAccount;
import net.sourceforge.JFinanceApp.views.Analysis.TaxDetail;
import net.sourceforge.JFinanceApp.views.Analysis.TransDetail;
import net.sourceforge.JFinanceApp.views.Analysis.TransSummary;
import net.sourceforge.JFinanceApp.views.Analysis.TransTotal;
import net.sourceforge.JFinanceApp.views.Analysis.ValueAccount;
import net.sourceforge.JFinanceApp.views.ChargeableEvent.ChargeableEventList;
import net.sourceforge.JPreferenceSet.PreferenceSet;
import net.sourceforge.JPreferenceSet.PreferenceSet.PreferenceManager;
import net.sourceforge.JPreferenceSet.PreferenceSet.PreferenceSetChooser;

/**
 * Class to further analyse an analysis, primarily to calculate tax liability.
 * @author Tony Washer
 */
public class MetaAnalysis implements PreferenceSetChooser {
    /**
     * Low Age Limit.
     */
    private static final int LIMIT_AGE_LO = 65;

    /**
     * High Age Limit.
     */
    private static final int LIMIT_AGE_HI = 75;

    /**
     * Allowance Quotient.
     */
    private static final JMoney ALLOWANCE_QUOTIENT = JMoney.getWholeUnits(2);

    /**
     * Allowance Multiplier.
     */
    private static final JMoney ALLOWANCE_MULTIPLIER = JMoney.getWholeUnits(1);

    /**
     * Analysis.
     */
    private final Analysis theAnalysis;

    /**
     * Analysis Buckets.
     */
    private final BucketList theList;

    /**
     * Chargeable events.
     */
    private final ChargeableEventList theCharges;

    /**
     * The date of the analysis.
     */
    private final JDateDay theDate;

    /**
     * The TaxYear of the analysis.
     */
    private final TaxYear theYear;

    /**
     * The Assets Summary.
     */
    private AssetSummary theAssetSummary = null;

    /**
     * The external totals.
     */
    private ExternalTotal theExternalTotals = null;

    /**
     * The Transaction profit.
     */
    private TransTotal theTransProfit = null;

    /**
     * The core profit.
     */
    private TransTotal theCoreProfit = null;

    /**
     * The core income.
     */
    private TransTotal theCoreIncome = null;

    /**
     * The market account.
     */
    private ExternalAccount theMarketAccount = null;

    /**
     * The market growth.
     */
    private TransDetail theMarketGrowth = null;

    /**
     * The market shrink.
     */
    private TransDetail theMarketShrink = null;

    /**
     * The capital Gains.
     */
    private TransDetail theCapitalGains = null;

    /**
     * The capital loss.
     */
    private TransDetail theCapitalLoss = null;

    /**
     * Do we have an age allowance?
     */
    private boolean hasAgeAllowance = false;

    /**
     * Do we have Gains slices?
     */
    private boolean hasGainsSlices = false;

    /**
     * Do we have a reduced allowance?
     */
    private boolean hasReducedAllow = false;

    /**
     * Age of User.
     */
    private int theAge = 0;

    @Override
    public Class<? extends PreferenceSet> getPreferenceSetClass() {
        return TaxationPreferences.class;
    }

    /**
     * Taxation Preferences.
     */
    public static class TaxationPreferences extends PreferenceSet {
        /**
         * Registry name for BirthDate.
         */
        protected static final String NAME_BIRTHDATE = "BirthDate";

        /**
         * Display name for BirthDate.
         */
        protected static final String DISPLAY_BIRTHDATE = "Birth Date";

        /**
         * Default value for BirthDate.
         */
        private static final JDateDay DEFAULT_BIRTHDATE = new JDateDay(1970, Calendar.JANUARY, 1);

        /**
         * Constructor.
         * @throws JDataException on error
         */
        public TaxationPreferences() throws JDataException {
            super();
        }

        @Override
        protected void definePreferences() {
            /* Define the preferences */
            defineDatePreference(NAME_BIRTHDATE, DEFAULT_BIRTHDATE);
        }

        @Override
        protected String getDisplayName(final String pName) {
            /* Handle default values */
            if (pName.equals(NAME_BIRTHDATE)) {
                return DISPLAY_BIRTHDATE;
            }
            return null;
        }
    }

    /**
     * Constructor.
     * @param pAnalysis the analysis
     */
    protected MetaAnalysis(final Analysis pAnalysis) {
        /* Store the analysis */
        theAnalysis = pAnalysis;
        theDate = theAnalysis.getDate();
        theYear = theAnalysis.getTaxYear();
        theList = theAnalysis.getList();
        theCharges = theAnalysis.getCharges();
    }

    /**
     * Value the priced assets.
     */
    protected void valueAssets() {
        /* Access the state of the analysis */
        AnalysisState myState = theAnalysis.getState();

        /* Ignore request if we are not in raw state */
        if (myState != AnalysisState.RAW) {
            return;
        }

        /* Obtain access to account list */
        FinanceData myData = theAnalysis.getData();
        AccountList myAccounts = myData.getAccounts();

        /* Obtain access to key elements */
        theMarketAccount = (ExternalAccount) theList.getAccountDetail(myAccounts.getMarket());
        theMarketGrowth = theList.getTransDetail(TransClass.MARKETGROWTH);
        theMarketShrink = theList.getTransDetail(TransClass.MARKETSHRINK);
        theCapitalGains = theList.getTransDetail(TransClass.CAPITALGAIN);
        theCapitalLoss = theList.getTransDetail(TransClass.CAPITALLOSS);

        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = theList.iterator();

        /* Loop through the buckets */
        while (myIterator.hasNext()) {
            AnalysisBucket myCurr = myIterator.next();

            /* Switch on bucket Type */
            switch (myCurr.getBucketType()) {
            /* Money */
                case MONEYDETAIL:
                    /* Access the Money account */
                    MoneyAccount myMoney = (MoneyAccount) myCurr;

                    /* Record the Rate */
                    myMoney.recordRate(myData, theDate);
                    break;

                /* Assets */
                case ASSETDETAIL:
                    /* Access the Asset account */
                    AssetAccount myAsset = (AssetAccount) myCurr;

                    /* Value the asset */
                    myAsset.valueAsset(theDate);

                    /* Process the market movement */
                    processMarketMovement(myAsset);

                    /* Calculate the profit */
                    myAsset.calculateProfit();
                    break;
                default:
                    break;
            }
        }

        /* Set the state to valued */
        theAnalysis.setState(AnalysisState.VALUED);
    }

    /**
     * Process market movement for asset.
     * @param pAsset the asset
     */
    private void processMarketMovement(final AssetAccount pAsset) {
        /* Create a capital event */
        CapitalEvent myEvent = pAsset.getCapitalEvents().addEvent(theDate);

        /* Add price and value */
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALPRICE, pAsset.getPrice());
        if (pAsset.getPrevValue() != null) {
            myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALVALUE, pAsset.getPrevValue());
        }
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALVALUE, pAsset.getValue());
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALINVEST, pAsset.getInvested());
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALGAINS, pAsset.getGains());
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALDIVIDEND, pAsset.getDividend());

        /*
         * Calculate basic market movement which is defined as currentValue - previousValue - amountInvested
         */
        JMoney myMarket = new JMoney(pAsset.getValue());
        if (pAsset.getBase() != null) {
            myMarket.subtractAmount(pAsset.getPrevValue());
        }
        myMarket.subtractAmount(pAsset.getInvested());

        /* Access the amount that has been gained in this period */
        JMoney myGain = pAsset.getGains();
        Account myAccount = pAsset.getAccount();

        /* If there have been gains realised in this period */
        if (myGain.isNonZero()) {
            /* If we are subject to capital gains */
            if (myAccount.isCapitalGains()) {
                /* Subtract them from the market movement */
                myMarket.subtractAmount(myGain);

                /* If the gains are positive */
                if (myGain.isPositive()) {
                    /* Add to capital Gains and market income */
                    theCapitalGains.getAmount().addAmount(myGain);
                    theMarketAccount.getIncome().addAmount(myGain);

                    /* else the gains are negative */
                } else {
                    /* Add to capital Loss and market expense */
                    theCapitalLoss.getAmount().subtractAmount(myGain);
                    theMarketAccount.getExpense().subtractAmount(myGain);
                }

                /* else if this is a LifeBond */
            } else if (myAccount.isLifeBond()) {
                /* Subtract them from the market movement */
                myMarket.subtractAmount(myGain);

                /* If the gains are positive */
                if (myGain.isPositive()) {
                    /* Add the market income */
                    theMarketAccount.getIncome().addAmount(myGain);
                }
            }
        }

        /* Determine the delta gained */
        JMoney myDeltaGained = new JMoney(myGain);
        myDeltaGained.addAmount(pAsset.getDividend());

        /* Record initial and delta gained */
        myEvent.addAttribute(CapitalEvent.CAPITAL_INITIALGAINED, pAsset.getGained());
        myEvent.addAttribute(CapitalEvent.CAPITAL_DELTAGAINED, myDeltaGained);

        /* Adjust the Gained Total */
        pAsset.getGained().addAmount(myDeltaGained);
        myEvent.addAttribute(CapitalEvent.CAPITAL_FINALGAINED, pAsset.getGained());

        /* If the market movement is positive */
        if (myMarket.isPositive()) {
            /* Add to market income and growth */
            theMarketAccount.getIncome().addAmount(myMarket);
            theMarketGrowth.getAmount().addAmount(myMarket);

            /* else the market movement is negative */
        } else {
            /* Add to market expense and shrink */
            theMarketAccount.getExpense().subtractAmount(myMarket);
            theMarketShrink.getAmount().subtractAmount(myMarket);
        }

        /* Record market details */
        myEvent.addAttribute(CapitalEvent.CAPITAL_MARKET, myMarket);
    }

    /**
     * Produce totals.
     */
    protected void produceTotals() {
        /* Access the state of the analysis */
        AnalysisState myState = theAnalysis.getState();

        /* Ignore request if we are not in valued state */
        if (myState != AnalysisState.VALUED) {
            return;
        }

        /* Create a set of total buckets */
        BucketList myTotals = new BucketList(theAnalysis);

        /* Obtain access to key totals elements */
        AssetTotal myAssetTotals = myTotals.getAssetTotal();
        MarketTotal myMarketTotals = myTotals.getMarketTotal();
        theExternalTotals = myTotals.getExternalTotal();
        theTransProfit = myTotals.getTransTotal(TaxClass.PROFITLOSS);
        theCoreProfit = myTotals.getTransTotal(TaxClass.COREPROFITLOSS);
        theCoreIncome = myTotals.getTransTotal(TaxClass.COREINCOME);

        /* Loop through the detail buckets */
        Iterator<AnalysisBucket> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            AnalysisBucket myCurr = myIterator.next();

            /* Switch on the bucket type */
            switch (myCurr.getBucketType()) {
            /* Accounts with valuations */
                case ASSETDETAIL:
                    /* Adjust Asset Summaries if the account is relevant */
                    if (myCurr.isRelevant()) {
                        adjustAssetSummary(myTotals, (ActDetail) myCurr);
                    }

                    /* Adjust Market Totals */
                    myMarketTotals.addValues((AssetAccount) myCurr);
                    break;

                /* Accounts with valuations */
                case MONEYDETAIL:
                case DEBTDETAIL:
                    /* Adjust Value Summaries */
                    if (myCurr.isRelevant()) {
                        adjustAssetSummary(myTotals, (ActDetail) myCurr);
                    }
                    break;

                /* External Accounts */
                case EXTERNALDETAIL:
                    /* Adjust External Totals */
                    adjustExternalTotals((ExternalAccount) myCurr);
                    break;

                /* Transaction Detail */
                case TRANSDETAIL:
                    /* Adjust TransactionSummary */
                    adjustTransSummary(myTotals, (TransDetail) myCurr);
                    break;

                /* Everything else */
                default:
                    /* Nothing to do */
                    break;
            }
        }

        /* Loop through the totals */
        myIterator = myTotals.iterator();
        while (myIterator.hasNext()) {
            AnalysisBucket myCurr = myIterator.next();

            /* Add it to the list */
            theList.add(myCurr);

            /* Switch on the bucket type */
            switch (myCurr.getBucketType()) {
            /* Asset Summaries */
                case ASSETSUMMARY:
                    /* Adjust Asset Totals */
                    myAssetTotals.addValues((AssetSummary) myCurr);
                    break;

                /* Transaction Summaries */
                case TRANSSUMMARY:
                    /* Adjust Transaction Totals */
                    adjustTransTotals((TransSummary) myCurr);
                    break;

                /* Asset Totals */
                case ASSETTOTAL:
                    /* Calculate profit */
                    ((AssetTotal) myCurr).calculateProfit();
                    break;

                /* External Totals */
                case EXTERNALTOTAL:
                    /* Calculate profit */
                    ((ExternalTotal) myCurr).calculateProfit();
                    break;

                /* Market Totals etc */
                case MARKETTOTAL:
                default:
                    /* Nothing to do */
                    break;
            }
        }

        /* Prune the analysis list */
        theList.prune();

        /* Set the state to totalled */
        theAnalysis.setState(AnalysisState.TOTALLED);
    }

    /**
     * Calculate tax.
     */
    protected void calculateTax() {
        TaxBands myBands;
        JMoney myIncome = new JMoney();
        JMoney myTax = new JMoney();
        TaxDetail myBucket;
        TransSummary mySrcBucket;
        AnalysisState myState;

        /* Access the state of the analysis */
        myState = theAnalysis.getState();

        /* If we are in valued state */
        if (myState == AnalysisState.VALUED) {
            /* Produce totals */
            produceTotals();

            /* Access new state */
            myState = theAnalysis.getState();
        }

        /* Ignore request if we are not in totalled state */
        if (myState != AnalysisState.TOTALLED) {
            return;
        }

        /* Ignore request if we do not have a TaxYear */
        if (theYear == null) {
            return;
        }

        /* Calculate the gross income */
        calculateGrossIncome();

        /* Calculate the allowances and tax bands */
        myBands = calculateAllowances();

        /* Calculate the salary taxation */
        myBucket = calculateSalaryTax(myBands);
        myIncome.addAmount(myBucket.getAmount());
        myTax.addAmount(myBucket.getTaxation());

        /* Calculate the rental taxation */
        myBucket = calculateRentalTax(myBands);
        myIncome.addAmount(myBucket.getAmount());
        myTax.addAmount(myBucket.getTaxation());

        /* Calculate the interest taxation */
        myBucket = calculateInterestTax(myBands);
        myIncome.addAmount(myBucket.getAmount());
        myTax.addAmount(myBucket.getTaxation());

        /* Calculate the dividends taxation */
        myBucket = calculateDividendsTax(myBands);
        myIncome.addAmount(myBucket.getAmount());
        myTax.addAmount(myBucket.getTaxation());

        /* Calculate the taxable gains taxation */
        myBucket = calculateTaxableGainsTax(myBands);
        myIncome.addAmount(myBucket.getAmount());
        myTax.addAmount(myBucket.getTaxation());

        /* Calculate the capital gains taxation */
        myBucket = calculateCapitalGainsTax(myBands);
        myIncome.addAmount(myBucket.getAmount());
        myTax.addAmount(myBucket.getTaxation());

        /* Build the TotalTaxBucket */
        myBucket = theList.getTaxDetail(TaxClass.TOTALTAXATION);
        myBucket.setAmount(myIncome);
        myBucket.setTaxation(myTax);

        /* Access the tax paid bucket */
        mySrcBucket = theList.getTransSummary(TaxClass.TAXPAID);

        /* Calculate the tax profit */
        myTax.subtractAmount(mySrcBucket.getAmount());

        /* Build the TaxProfitBucket */
        myBucket = theList.getTaxDetail(TaxClass.TAXPROFITLOSS);
        myBucket.setAmount(new JMoney());
        myBucket.setTaxation(myTax);

        /* Prune the analysis list */
        theList.prune();

        /* Set the state to taxed and record values */
        theAnalysis.setState(AnalysisState.TAXED);
        theAnalysis.setHasReducedAllow(hasReducedAllow);
        theAnalysis.setHasGainsSlices(hasGainsSlices);
        theAnalysis.setAge(theAge);
    }

    /**
     * Mark active accounts.
     */
    public void markActiveAccounts() {
        /* Access the iterator */
        Iterator<AnalysisBucket> myIterator = theList.listIterator();
        Account myAccount;

        /* Loop through the items to find the match */
        while (myIterator.hasNext()) {
            AnalysisBucket myCurr = myIterator.next();

            /* Switch on bucket type */
            switch (myCurr.getBucketType()) {
                case ASSETDETAIL:
                    /* Access the Asset */
                    AssetAccount myAsset = (AssetAccount) myCurr;

                    /* If we have non-zero units */
                    if (myAsset.getUnits().isNonZero()) {
                        /* Set the account as non-closeable */
                        myAccount = myAsset.getAccount();
                        myAccount.setNonCloseable();
                    }
                    break;

                case MONEYDETAIL:
                    /* Access the Account */
                    MoneyAccount myMoney = (MoneyAccount) myCurr;

                    /* If we have non-zero value */
                    if (myMoney.getValue().isNonZero()) {
                        /* Set the account as non-close-able */
                        myAccount = myMoney.getAccount();
                        myAccount.setNonCloseable();
                    }
                    break;

                case DEBTDETAIL:
                    /* Access the Account */
                    DebtAccount myDebt = (DebtAccount) myCurr;

                    /* If we have non-zero value */
                    if (myDebt.getValue().isNonZero()) {
                        /* Set the account as non-close-able */
                        myAccount = myDebt.getAccount();
                        myAccount.setNonCloseable();
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Adjust Asset Summary.
     * @param pTotals the totals
     * @param pBucket the bucket
     */
    private void adjustAssetSummary(final BucketList pTotals,
                                    final ActDetail pBucket) {
        ValueAccount myAccount = null;
        AccountType myType = pBucket.getAccountType();

        /* Switch on the bucket type */
        switch (pBucket.getBucketType()) {
        /* Asset/Money/Debt details */
            case ASSETDETAIL:
            case MONEYDETAIL:
            case DEBTDETAIL:
                /* Access the account */
                myAccount = (ValueAccount) pBucket;

                /* If we need to look up the Asset summary */
                if ((theAssetSummary == null) || (!theAssetSummary.getAccountType().equals(myType))) {
                    /* Access the asset summary */
                    theAssetSummary = pTotals.getAssetSummary(myType);
                }

                /* Add the value to the asset summary */
                theAssetSummary.addValues(myAccount);
                break;
            default:
                break;
        }
    }

    /**
     * Adjust Transaction Summary.
     * @param pTotals the totals
     * @param pBucket the bucket
     */
    private static void adjustTransSummary(final BucketList pTotals,
                                           final TransDetail pBucket) {
        TransactionType myType = pBucket.getTransType();
        TransSummary myBucket;

        /* Switch on the transaction type */
        switch (myType.getTranClass()) {
            case TAXEDINCOME:
                /* Adjust the Gross salary bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSSALARY);
                myBucket.addValues(pBucket);
                break;
            case INTEREST:
                /* Adjust the Gross interest bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSINTEREST);
                myBucket.addValues(pBucket);
                break;
            case DIVIDEND:
                /* Adjust the Gross dividend bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSDIVIDEND);
                myBucket.addValues(pBucket);
                break;
            case UNITTRUSTDIVIDEND:
                /* Adjust the Gross interest bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSUTDIVS);
                myBucket.addValues(pBucket);
                break;
            case TAXABLEGAIN:
                /* Adjust the Taxable Gains bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSTAXGAINS);
                myBucket.addValues(pBucket);
                break;
            case CAPITALGAIN:
                /* Adjust the Capital Gains bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSCAPGAINS);
                myBucket.addValues(pBucket);
                break;
            case CAPITALLOSS:
                /* Adjust the Capital Gains bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSCAPGAINS);
                myBucket.subtractValues(pBucket);
                break;
            case NATINSURANCE:
            case BENEFIT:
                /* Adjust the Gross salary bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSSALARY);
                myBucket.addValues(pBucket);

                /* Adjust the Virtual bucket */
                myBucket = pTotals.getTransSummary(TaxClass.VIRTUAL);
                myBucket.addValues(pBucket);
                break;
            case RENTALINCOME:
                /* Adjust the Gross rental bucket */
                myBucket = pTotals.getTransSummary(TaxClass.GROSSRENTAL);
                myBucket.addValues(pBucket);
                break;
            case TAXCREDIT:
            case TAXOWED:
                /* Adjust the Tax Paid bucket */
                myBucket = pTotals.getTransSummary(TaxClass.TAXPAID);
                myBucket.addValues(pBucket);
                break;
            case TAXREFUND:
                /* Adjust the Tax Paid bucket */
                myBucket = pTotals.getTransSummary(TaxClass.TAXPAID);
                myBucket.subtractValues(pBucket);
                break;
            case TAXFREEINCOME:
            case TAXFREEINTEREST:
            case TAXFREEDIVIDEND:
            case DEBTINTEREST:
                /* Adjust the Tax Free bucket */
                myBucket = pTotals.getTransSummary(TaxClass.TAXFREE);
                myBucket.addValues(pBucket);
                break;
            case INHERITED:
                /* Adjust the Tax Free bucket */
                myBucket = pTotals.getTransSummary(TaxClass.TAXFREE);
                myBucket.addValues(pBucket);

                /* Adjust the Non-Core bucket */
                myBucket = pTotals.getTransSummary(TaxClass.NONCORE);
                myBucket.addValues(pBucket);
                break;
            case EXPENSE:
            case MORTGAGE:
            case INSURANCE:
            case EXTRATAX:
            case WRITEOFF:
                /* Adjust the Expense bucket */
                myBucket = pTotals.getTransSummary(TaxClass.EXPENSE);
                myBucket.addValues(pBucket);
                break;
            case RECOVERED:
            case TAXRELIEF:
                /* Adjust the Expense bucket */
                myBucket = pTotals.getTransSummary(TaxClass.EXPENSE);
                myBucket.subtractValues(pBucket);
                break;
            case MARKETGROWTH:
                /* Adjust the Market bucket */
                myBucket = pTotals.getTransSummary(TaxClass.MARKET);
                myBucket.addValues(pBucket);

                /* Adjust the Non-Core bucket */
                myBucket = pTotals.getTransSummary(TaxClass.NONCORE);
                myBucket.addValues(pBucket);
                break;
            case MARKETSHRINK:
                /* Adjust the Market bucket */
                myBucket = pTotals.getTransSummary(TaxClass.MARKET);
                myBucket.subtractValues(pBucket);

                /* Adjust the Non-Core bucket */
                myBucket = pTotals.getTransSummary(TaxClass.NONCORE);
                myBucket.subtractValues(pBucket);
                break;
            case CASHTAKEOVER:
            case STOCKTAKEOVER:
            case STOCKSPLIT:
            case STOCKDEMERGER:
            case STOCKRIGHTTAKEN:
            case STOCKRIGHTWAIVED:
            case TRANSFER:
            case ENDOWMENT:
            case CASHPAYMENT:
            case CASHRECOVERY:
            default:
                break;
        }
    }

    /**
     * Adjust Transaction Total.
     * @param pBucket the bucket
     */
    private void adjustTransTotals(final TransSummary pBucket) {
        TaxType myType = pBucket.getTaxType();

        /* Switch on the tax type */
        switch (myType.getTaxClass()) {
            case GROSSSALARY:
            case GROSSINTEREST:
            case GROSSDIVIDEND:
            case GROSSUTDIVS:
            case GROSSRENTAL:
            case GROSSTAXGAINS:
            case GROSSCAPGAINS:
            case MARKET:
            case TAXFREE:
                /* Adjust the Total Profit buckets */
                theTransProfit.addValues(pBucket);
                theCoreProfit.addValues(pBucket);
                theCoreIncome.addValues(pBucket);
                break;
            case TAXPAID:
                theCoreIncome.subtractValues(pBucket);
                /* Fall through */
            case EXPENSE:
                /* Adjust the Total profits buckets */
                theTransProfit.subtractValues(pBucket);
                theCoreProfit.subtractValues(pBucket);
                break;
            case VIRTUAL:
                /* Adjust the Total profits buckets */
                theTransProfit.subtractValues(pBucket);
                theCoreProfit.subtractValues(pBucket);
                theCoreIncome.subtractValues(pBucket);
                break;
            case NONCORE:
                /* Adjust the Core profits buckets */
                theCoreProfit.subtractValues(pBucket);
                theCoreIncome.subtractValues(pBucket);
                break;
            default:
                break;
        }
    }

    /**
     * Adjust External Totals.
     * @param pBucket the bucket
     */
    private void adjustExternalTotals(final ExternalAccount pBucket) {
        /* If the expense is negative */
        JMoney myMoney = pBucket.getExpense();
        if (!myMoney.isPositive()) {
            /* Swap it to the income side */
            pBucket.getIncome().subtractAmount(myMoney);
            myMoney.setZero();
        }

        /* If the old expense is negative */
        myMoney = pBucket.getPrevExpense();
        if ((myMoney != null) && (!myMoney.isPositive())) {
            /* Swap it to the income side */
            pBucket.getPrevIncome().subtractAmount(myMoney);
            myMoney.setZero();
        }

        /* Add the values to the external totals */
        theExternalTotals.addValues(pBucket);
    }

    /**
     * Calculate the gross income for tax purposes.
     */
    private void calculateGrossIncome() {
        TaxDetail myBucket;
        TransSummary mySrcBucket;
        TransDetail myDtlBucket;
        JMoney myIncome = new JMoney();
        JMoney myChargeable;

        /* Access the salary bucket and add to income */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSSALARY);
        myIncome.addAmount(mySrcBucket.getAmount());

        /* Access the rental bucket */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSRENTAL);
        myChargeable = new JMoney(mySrcBucket.getAmount());

        /* If we have a chargeable element */
        if (myChargeable.compareTo(theYear.getRentalAllowance()) > 0) {
            /* Add the chargeable element to income */
            myChargeable.subtractAmount(theYear.getRentalAllowance());
            myIncome.addAmount(myChargeable);
        }

        /* Access the interest bucket and add to income */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSINTEREST);
        myIncome.addAmount(mySrcBucket.getAmount());

        /* Access the dividends bucket and add to income */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSDIVIDEND);
        myIncome.addAmount(mySrcBucket.getAmount());

        /* Access the unit trust dividends bucket and add to income */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSUTDIVS);
        myIncome.addAmount(mySrcBucket.getAmount());

        /* Access the taxable gains bucket and add to income */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSTAXGAINS);
        myIncome.addAmount(mySrcBucket.getAmount());

        /* Access the taxable gains bucket and subtract the tax credit */
        myDtlBucket = theList.getTransDetail(TransClass.TAXABLEGAIN);
        myIncome.subtractAmount(myDtlBucket.getTaxCredit());

        /* Access the capital gains bucket */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSCAPGAINS);
        myChargeable = new JMoney(mySrcBucket.getAmount());

        /* If we have a chargeable element */
        if (myChargeable.compareTo(theYear.getCapitalAllow()) > 0) {
            /* Add the chargeable element to income */
            myChargeable.subtractAmount(theYear.getCapitalAllow());
            myIncome.addAmount(myChargeable);
        }

        /* Access the Gross Income bucket and set the amount */
        myBucket = theList.getTaxDetail(TaxClass.GROSSINCOME);
        myBucket.setAmount(myIncome);
    }

    /**
     * Calculate the allowances and tax bands.
     * @return the taxBands
     */
    private TaxBands calculateAllowances() {
        /* Allocate the tax bands class */
        TaxBands myBands = new TaxBands();
        JMoney myAllowance;
        JMoney myAdjust;

        /* Access the taxation properties */
        TaxationPreferences myPreferences = (TaxationPreferences) PreferenceManager.getPreferenceSet(this);

        /* Determine the relevant age for this tax year */
        theAge = myPreferences.getDateValue(TaxationPreferences.NAME_BIRTHDATE).ageOn(theYear.getTaxYear());

        /* Determine the relevant allowance */
        if (theAge >= LIMIT_AGE_HI) {
            myAllowance = theYear.getHiAgeAllow();
            hasAgeAllowance = true;
        } else if (theAge >= LIMIT_AGE_LO) {
            myAllowance = theYear.getLoAgeAllow();
            hasAgeAllowance = true;
        } else {
            myAllowance = theYear.getAllowance();
        }

        /* Record the Original allowance */
        TaxDetail myParentBucket = theList.getTaxDetail(TaxClass.ORIGALLOW);
        myParentBucket.setAmount(myAllowance);

        /* Access the gross income */
        TaxDetail myBucket = theList.getTaxDetail(TaxClass.GROSSINCOME);
        JMoney myGrossIncome = myBucket.getAmount();
        myBucket.setParent(myParentBucket);

        /* If we are using age allowance and the gross income is above the Age Allowance Limit */
        if ((hasAgeAllowance) && (myGrossIncome.compareTo(theYear.getAgeAllowLimit()) > 0)) {
            /* Calculate the margin by which we exceeded the limit */
            myAdjust = new JMoney(myGrossIncome);
            myAdjust.subtractAmount(theYear.getAgeAllowLimit());

            /* Calculate the allowance reduction by dividing by £2 and then multiply up by £1 */
            myAdjust.divide(ALLOWANCE_QUOTIENT.unscaledValue());
            myAdjust.multiply(ALLOWANCE_MULTIPLIER.unscaledValue());

            /* Adjust the allowance by this value */
            myAllowance = new JMoney(myAllowance);
            myAllowance.subtractAmount(myAdjust);

            /* If we have reduced below the standard allowance */
            if (myAllowance.compareTo(theYear.getAllowance()) < 0) {
                /* Reset the allowance to the standard value */
                myAllowance = theYear.getAllowance();
                hasAgeAllowance = false;
            }

            /* Record the adjusted allowance */
            myBucket = theList.getTaxDetail(TaxClass.ADJALLOW);
            myBucket.setAmount(myBands.theAllowance);
            myBucket.setParent(myParentBucket);
            hasReducedAllow = true;
        }

        /* Set Allowance and Tax Bands */
        myBands.theAllowance = new JMoney(myAllowance);
        myBands.theLoBand = new JMoney(theYear.getLoBand());
        myBands.theBasicBand = new JMoney(theYear.getBasicBand());

        /* If we have an additional tax band */
        if (theYear.hasAdditionalTaxBand()) {
            /* Set the High tax band */
            myBands.theHiBand = new JMoney(theYear.getAddIncBound());

            /* Remove the basic band from this one */
            myBands.theHiBand.subtractAmount(myBands.theBasicBand);

            /* Record the High tax band */
            myBucket = theList.getTaxDetail(TaxClass.HITAXBAND);
            myBucket.setAmount(myBands.theHiBand);
            myBucket.setParent(myParentBucket);

            /* If the gross income is above the Additional Allowance Limit */
            if (myGrossIncome.compareTo(theYear.getAddAllowLimit()) > 0) {
                /* Calculate the margin by which we exceeded the limit */
                myAdjust = new JMoney(myGrossIncome);
                myAdjust.subtractAmount(theYear.getAddAllowLimit());

                /* Calculate the allowance reduction by dividing by £2 and then multiply up by £1 */
                myAdjust.divide(ALLOWANCE_QUOTIENT.unscaledValue());
                myAdjust.multiply(ALLOWANCE_MULTIPLIER.unscaledValue());

                /* Adjust the allowance by this value */
                myAllowance = new JMoney(myAllowance);
                myAllowance.subtractAmount(myAdjust);

                /* If we have used up the entire allowance */
                if (!myAllowance.isPositive()) {
                    /* Personal allowance is reduced to zero */
                    myBands.theAllowance = new JMoney();
                }

                /* Record the adjusted allowance */
                myBucket = theList.getTaxDetail(TaxClass.ADJALLOW);
                myBucket.setAmount(myBands.theAllowance);
                myBucket.setParent(myParentBucket);
                hasReducedAllow = true;
            }
        }

        /* Return to caller */
        return myBands;
    }

    /**
     * Calculate the tax due on salary.
     * @param pBands the remaining allowances and tax bands
     * @return the salary taxation bucket
     */
    private TaxDetail calculateSalaryTax(final TaxBands pBands) {
        TransSummary mySrcBucket;
        TaxDetail myTaxBucket;
        TaxDetail myTopBucket;
        JMoney mySalary;
        JMoney myTax = new JMoney();
        boolean isFinished = false;

        /* Access Salary */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSSALARY);
        mySalary = new JMoney(mySrcBucket.getAmount());

        /* Store the total into the TaxDueSalary Bucket */
        myTopBucket = theList.getTaxDetail(TaxClass.TAXDUESALARY);
        myTopBucket.setAmount(mySalary);

        /* Access the FreeSalaryBucket */
        myTaxBucket = theList.getTaxDetail(TaxClass.SALARYFREE);
        myTaxBucket.setParent(myTopBucket);

        /* If the salary is greater than the remaining allowance */
        if (mySalary.compareTo(pBands.theAllowance) > 0) {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(pBands.theAllowance));

            /* Adjust the salary to remove allowance */
            mySalary.subtractAmount(pBands.theAllowance);
            pBands.theAllowance.setZero();

            /* else still have allowance left after salary */
        } else {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(mySalary));

            /* Adjust the allowance to remove salary and note that we have finished */
            pBands.theAllowance.subtractAmount(mySalary);
            isFinished = true;
        }

        /* If we have salary left */
        if (!isFinished) {
            /* If we have a low salary band */
            if (theYear.hasLoSalaryBand()) {
                /* Access the LowSalaryBucket */
                myTaxBucket = theList.getTaxDetail(TaxClass.SALARYLO);
                myTaxBucket.setRate(theYear.getLoTaxRate());
                myTaxBucket.setParent(myTopBucket);

                /* If the salary is greater than the Low Tax Band */
                if (mySalary.compareTo(pBands.theLoBand) > 0) {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));

                    /* Adjust the salary to remove LoBand */
                    mySalary.subtractAmount(pBands.theLoBand);
                    pBands.theLoBand.setZero();

                    /* else we still have band left after salary */
                } else {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(mySalary));

                    /* Adjust the loBand to remove salary and note that we have finished */
                    pBands.theLoBand.subtractAmount(mySalary);
                    isFinished = true;
                }

                /* Else use up the Low Tax band */
            } else {
                /* If the salary is greater than the Low Tax Band */
                if (mySalary.compareTo(pBands.theLoBand) > 0) {
                    /* We have used up the band */
                    pBands.theLoBand.setZero();
                } else {
                    /* Adjust the band to remove salary */
                    pBands.theLoBand.subtractAmount(mySalary);
                }
            }
        }

        /* If we have salary left */
        if (!isFinished) {
            /* Access the BasicSalaryBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.SALARYBASIC);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the salary is greater than the Basic Tax Band */
            if (mySalary.compareTo(pBands.theBasicBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the salary to remove BasicBand */
                mySalary.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();

                /* else we still have band left after salary */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(mySalary));

                /* Adjust the basicBand to remove salary and note that we have finished */
                pBands.theBasicBand.subtractAmount(mySalary);
                isFinished = true;
            }
        }

        /* If we have salary left */
        if (!isFinished) {
            /* Access the HiSalaryBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.SALARYHI);
            myTaxBucket.setRate(theYear.getHiTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the salary is greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand()) && (mySalary.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the salary to remove HiBand */
                mySalary.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after salary */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(mySalary));

                /* Adjust the hiBand to remove salary and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(mySalary);
                }
                isFinished = true;
            }
        }

        /* If we have salary left */
        if (!isFinished) {
            /* Access the AdditionalSalaryBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.SALARYADD);
            myTaxBucket.setRate(theYear.getAddTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(mySalary));
        }

        /* Store the taxation value into the top bucket */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * Calculate the tax due on rental.
     * @param pBands the remaining allowances and tax bands
     * @return the rental tax bucket
     */
    private TaxDetail calculateRentalTax(final TaxBands pBands) {
        TransSummary mySrcBucket;
        TaxDetail myTaxBucket;
        TaxDetail myTopBucket;
        JMoney myRental;
        JMoney myAllowance;
        JMoney myTax = new JMoney();
        boolean isFinished = false;

        /* Access Rental */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSRENTAL);
        myRental = new JMoney(mySrcBucket.getAmount());

        /* Store the total into the TaxDueRental Bucket */
        myTopBucket = theList.getTaxDetail(TaxClass.TAXDUERENTAL);
        myTopBucket.setAmount(myRental);

        /* Access the FreeRentalBucket */
        myTaxBucket = theList.getTaxDetail(TaxClass.RENTALFREE);
        myTaxBucket.setParent(myTopBucket);

        /* Pick up the rental allowance */
        myAllowance = theYear.getRentalAllowance();

        /* If the rental is less than the rental allowance */
        if (myRental.compareTo(myAllowance) < 0) {
            /* All of the rental is free so record it and note that we have finished */
            myTax.addAmount(myTaxBucket.setAmount(myRental));
            isFinished = true;
        }

        /* If we have not finished */
        if (!isFinished) {
            /* Remove allowance from rental figure */
            myRental.subtractAmount(myAllowance);

            /* If the rental is greater than the remaining allowance */
            if (myRental.compareTo(pBands.theAllowance) > 0) {
                /* Determine the remaining allowance */
                myAllowance.addAmount(pBands.theAllowance);

                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myAllowance));

                /* Adjust the rental to remove allowance */
                myRental.subtractAmount(pBands.theAllowance);
                pBands.theAllowance.setZero();

                /* else still have allowance left after rental */
            } else {
                /* Determine the remaining allowance */
                myAllowance.addAmount(myRental);

                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myAllowance));

                /* Adjust the allowance to remove rental and note that we have finished */
                pBands.theAllowance.subtractAmount(myRental);
                isFinished = true;
            }
        }

        /* If we have salary left */
        if (!isFinished) {
            /* If we have a low salary band */
            if (theYear.hasLoSalaryBand()) {
                /* Access the LowSalaryBucket */
                myTaxBucket = theList.getTaxDetail(TaxClass.RENTALLO);
                myTaxBucket.setRate(theYear.getLoTaxRate());
                myTaxBucket.setParent(myTopBucket);

                /* If the rental is greater than the Low Tax Band */
                if (myRental.compareTo(pBands.theLoBand) > 0) {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));

                    /* Adjust the rental to remove LoBand */
                    myRental.subtractAmount(pBands.theLoBand);
                    pBands.theLoBand.setZero();

                    /* else we still have band left after salary */
                } else {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(myRental));

                    /* Adjust the loBand to remove rental and note that we have finished */
                    pBands.theLoBand.subtractAmount(myRental);
                    isFinished = true;
                }

                /* Else use up the Low Tax band */
            } else {
                /* If the rental is greater than the Low Tax Band */
                if (myRental.compareTo(pBands.theLoBand) > 0) {
                    /* We have used up the band */
                    pBands.theLoBand.setZero();
                } else {
                    /* Adjust the band to remove rental */
                    pBands.theLoBand.subtractAmount(myRental);
                }
            }
        }

        /* If we have Rental left */
        if (!isFinished) {
            /* Access the BasicRentalBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.RENTALBASIC);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the rental is greater than the Basic Tax Band */
            if (myRental.compareTo(pBands.theBasicBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the rental to remove BasicBand */
                myRental.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();

                /* else we still have band left after rental */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myRental));

                /* Adjust the basicBand to remove salary and note that we have finished */
                pBands.theBasicBand.subtractAmount(myRental);
                isFinished = true;
            }
        }

        /* If we have rental left */
        if (!isFinished) {
            /* Access the HiRentalBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.RENTALHI);
            myTaxBucket.setRate(theYear.getHiTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the rental is greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand()) && (myRental.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the rental to remove HiBand */
                myRental.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after rental */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myRental));

                /* Adjust the hiBand to remove rental and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(myRental);
                }
                isFinished = true;
            }
        }

        /* If we have rental left */
        if (!isFinished) {
            /* Access the AdditionalRentalBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.RENTALADD);
            myTaxBucket.setRate(theYear.getAddTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myRental));
        }

        /* Store the taxation total */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * Calculate the tax due on Interest.
     * @param pBands the remaining allowances and tax bands
     * @return the interest tax bucket
     */
    private TaxDetail calculateInterestTax(final TaxBands pBands) {
        TransSummary mySrcBucket;
        TaxDetail myTaxBucket;
        TaxDetail myTopBucket;
        JMoney myInterest;
        JMoney myTax = new JMoney();
        boolean isFinished = false;

        /* If we do not have a Low salary band */
        if (!theYear.hasLoSalaryBand()) {
            /* Remove LoTaxBand from BasicTaxBand */
            pBands.theBasicBand.subtractAmount(pBands.theLoBand);
        }

        /* Access Interest */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSINTEREST);
        myInterest = new JMoney(mySrcBucket.getAmount());

        /* Store the total into the TaxDueInterest Bucket */
        myTopBucket = theList.getTaxDetail(TaxClass.TAXDUEINTEREST);
        myTopBucket.setAmount(myInterest);

        /* Access the FreeInterestBucket */
        myTaxBucket = theList.getTaxDetail(TaxClass.INTERESTFREE);
        myTaxBucket.setParent(myTopBucket);

        /* If the interest is greater than the remaining allowance */
        if (myInterest.compareTo(pBands.theAllowance) > 0) {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(pBands.theAllowance));

            /* Adjust the interest to remove allowance */
            myInterest.subtractAmount(pBands.theAllowance);
            pBands.theAllowance.setZero();

            /* else still have allowance left after interest */
        } else {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myInterest));

            /* Adjust the allowance to remove interest and note that we have finished */
            pBands.theAllowance.subtractAmount(myInterest);
            isFinished = true;
        }

        /* If we have interest left */
        if (!isFinished) {
            /* Access the LowInterestBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.INTERESTLO);
            myTaxBucket.setRate(theYear.getLoTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the interest is greater than the Low Tax Band */
            if (myInterest.compareTo(pBands.theLoBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theLoBand));

                /* Adjust the interest to remove LoBand */
                myInterest.subtractAmount(pBands.theLoBand);
                pBands.theLoBand.setZero();

                /* else we still have band left after interest */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myInterest));

                /* Adjust the loBand to remove interest and note that we have finished */
                pBands.theLoBand.subtractAmount(myInterest);
                isFinished = true;
            }
        }

        /* If we have interest left */
        if (!isFinished) {
            /* Access the BasicInterestBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.INTERESTBASIC);
            myTaxBucket.setRate(theYear.getIntTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the interest is greater than the Basic Tax Band */
            if (myInterest.compareTo(pBands.theBasicBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the interest to remove BasicBand */
                myInterest.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();

                /* else we still have band left after interest */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myInterest));

                /* Adjust the basicBand to remove interest and note that we have finished */
                pBands.theBasicBand.subtractAmount(myInterest);
                isFinished = true;
            }
        }

        /* If we have interest left */
        if (!isFinished) {
            /* Access the HiInterestBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.INTERESTHI);
            myTaxBucket.setRate(theYear.getHiTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the interest is greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand()) && (myInterest.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the interest to remove HiBand */
                myInterest.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after interest */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myInterest));

                /* Adjust the hiBand to remove interest and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(myInterest);
                }
                isFinished = true;
            }
        }

        /* If we have interest left */
        if (!isFinished) {
            /* Access the AdditionalInterestBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.INTERESTADD);
            myTaxBucket.setRate(theYear.getAddTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myInterest));
        }

        /* Remaining tax credits are not reclaimable */
        /* so add any remaining allowance/LoTaxBand into BasicTaxBand */
        pBands.theBasicBand.addAmount(pBands.theAllowance);
        pBands.theBasicBand.addAmount(pBands.theLoBand);
        pBands.theAllowance.setZero();
        pBands.theLoBand.setZero();

        /* Store the taxation total */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * calculate the tax due on dividends.
     * @param pBands the remaining allowances and tax bands
     * @return the dividends tax bucket
     */
    private TaxDetail calculateDividendsTax(final TaxBands pBands) {
        TransSummary mySrcBucket;
        TaxDetail myTaxBucket;
        TaxDetail myTopBucket;
        JMoney myDividends;
        JMoney myTax = new JMoney();
        boolean isFinished = false;

        /* Access Dividends */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSDIVIDEND);
        myDividends = new JMoney(mySrcBucket.getAmount());

        /* Access Unit Trust Dividends */
        mySrcBucket = theList.getTransSummary(TaxClass.GROSSUTDIVS);
        myDividends.addAmount(mySrcBucket.getAmount());

        /* Store the total into the TaxDueDividends Bucket */
        myTopBucket = theList.getTaxDetail(TaxClass.TAXDUEDIVIDEND);
        myTopBucket.setAmount(myDividends);

        /* Access the BasicDividendBucket */
        myTaxBucket = theList.getTaxDetail(TaxClass.DIVIDENDBASIC);
        myTaxBucket.setRate(theYear.getDivTaxRate());
        myTaxBucket.setParent(myTopBucket);

        /* If the dividends are greater than the Basic Tax Band */
        if (myDividends.compareTo(pBands.theBasicBand) > 0) {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

            /* Adjust the dividends to remove BasicBand */
            myDividends.subtractAmount(pBands.theBasicBand);
            pBands.theBasicBand.setZero();

            /* else we still have band left after dividends */
        } else {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myDividends));

            /* Adjust the basicBand to remove dividends and note that we have finished */
            pBands.theBasicBand.subtractAmount(myDividends);
            isFinished = true;
        }

        /* If we have dividends left */
        if (!isFinished) {
            /* Access the HiDividendsBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.DIVIDENDHI);
            myTaxBucket.setRate(theYear.getHiDivTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the dividends are greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand()) && (myDividends.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the dividends to remove HiBand */
                myDividends.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after dividends */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myDividends));

                /* Adjust the hiBand to remove dividends and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(myDividends);
                }
                isFinished = true;
            }
        }

        /* If we have dividends left */
        if (!isFinished) {
            /* Access the AdditionalDividendsBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.DIVIDENDADD);
            myTaxBucket.setRate(theYear.getAddDivTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myDividends));
        }

        /* Store the taxation total */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * calculate the tax due on taxable gains.
     * @param pBands the remaining allowances and tax bands
     * @return the taxable gains bucket
     */
    private TaxDetail calculateTaxableGainsTax(final TaxBands pBands) {
        /* Access Gains */
        JMoney myGains = theCharges.getGainsTotal();
        JMoney myTax = new JMoney();
        boolean isFinished = false;
        TaxDetail myTaxBucket;

        /* Store the total into the TaxDueTaxGains Bucket */
        TaxDetail myTopBucket = theList.getTaxDetail(TaxClass.TAXDUETAXGAINS);
        myTopBucket.setAmount(myGains);

        /* If the gains are less than the available basic tax band */
        if (myGains.compareTo(pBands.theBasicBand) <= 0) {
            /* Access the BasicGainsBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.GAINSBASIC);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myGains));

            /* Adjust the basic band to remove taxable gains */
            pBands.theBasicBand.subtractAmount(myGains);
            isFinished = true;
        }

        /*
         * If we are not finished but either have no basic band left or are prevented from top-slicing due to
         * using age allowances
         */
        if ((!isFinished) && ((!pBands.theBasicBand.isNonZero()) || (hasAgeAllowance))) {
            /* Access the BasicGainsBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.GAINSBASIC);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the gains is greater than the Basic Tax Band */
            if (myGains.compareTo(pBands.theBasicBand) > 0) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the gains to remove BasicBand */
                myGains.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();
            }

            /* else case already handled */

            /* Access the HiGainsBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.GAINSHI);
            myTaxBucket.setRate(theYear.getHiTaxRate());
            myTaxBucket.setParent(myTopBucket);

            /* If the gains are greater than the High Tax Band */
            if ((theYear.hasAdditionalTaxBand()) && (myGains.compareTo(pBands.theHiBand) > 0)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                /* Adjust the gains to remove HiBand */
                myGains.subtractAmount(pBands.theHiBand);
                pBands.theHiBand.setZero();

                /* else we still have band left after gains */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myGains));

                /* Adjust the hiBand to remove dividends and note that we have finished */
                if (theYear.hasAdditionalTaxBand()) {
                    pBands.theHiBand.subtractAmount(myGains);
                }
                isFinished = true;
            }

            /* If we have gains left */
            if (!isFinished) {
                /* Access the AdditionalGainsBucket */
                myTaxBucket = theList.getTaxDetail(TaxClass.GAINSADD);
                myTaxBucket.setRate(theYear.getAddDivTaxRate());
                myTaxBucket.setParent(myTopBucket);

                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myGains));
                isFinished = true;
            }
        }

        /* If we are not finished then we need top-slicing relief */
        if (!isFinished) {
            /* Access the taxable slice */
            JMoney mySlice = theCharges.getSliceTotal();
            hasGainsSlices = true;

            /* Access the TaxDueSlice Bucket */
            TaxDetail mySliceBucket = theList.getTaxDetail(TaxClass.TAXDUESLICE);
            mySliceBucket.setAmount(mySlice);

            /* Access the BasicSliceBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.SLICEBASIC);
            myTaxBucket.setRate(theYear.getBasicTaxRate());
            myTaxBucket.setParent(mySliceBucket);

            /* If the slice is less than the available basic tax band */
            if (mySlice.compareTo(pBands.theBasicBand) < 0) {
                /* Set the slice details */
                myTax.addAmount(myTaxBucket.setAmount(mySlice));

                /* Distribute the Tax back to the chargeable events */
                theCharges.applyTax(myTax, theCharges.getSliceTotal());

                /* Access the BasicGainsBucket */
                myTaxBucket = theList.getTaxDetail(TaxClass.GAINSBASIC);
                myTaxBucket.setRate(theYear.getBasicTaxRate());

                /* Only basic rate tax is payable */
                myTaxBucket.setAmount(myGains);
                mySliceBucket.setTaxation(myTax);

                /* else we are using up the basic rate tax band */
            } else {
                /* Set the slice details */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Subtract the basic band from the slice */
                mySlice.subtractAmount(pBands.theBasicBand);

                /* Access the BasicGainsBucket */
                myTaxBucket = theList.getTaxDetail(TaxClass.GAINSBASIC);
                myTaxBucket.setRate(theYear.getBasicTaxRate());

                /* Basic Rate tax is payable on the remainder of the basic band */
                myTaxBucket.setAmount(pBands.theBasicBand);

                /* Remember this taxation amount to remove from HiTax bucket */
                JMoney myHiTax = new JMoney(myTaxBucket.getTaxation());
                myHiTax.negate();

                /* Access the HiSliceBucket */
                myTaxBucket = theList.getTaxDetail(TaxClass.SLICEHI);
                myTaxBucket.setRate(theYear.getHiTaxRate());
                myTaxBucket.setParent(mySliceBucket);

                /* If the slice is greater than the High Tax Band */
                if ((theYear.hasAdditionalTaxBand()) && (mySlice.compareTo(pBands.theHiBand) > 0)) {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(pBands.theHiBand));

                    /* Adjust the slice to remove HiBand */
                    mySlice.subtractAmount(pBands.theHiBand);

                    /* Access the AdditionalSliceBucket */
                    myTaxBucket = theList.getTaxDetail(TaxClass.SLICEADD);
                    myTaxBucket.setRate(theYear.getAddTaxRate());
                    myTaxBucket.setParent(mySliceBucket);

                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(mySlice));

                    /* else we still have band left after slice */
                } else {
                    /* Set the tax bucket and add the tax */
                    myTax.addAmount(myTaxBucket.setAmount(mySlice));
                }

                /* Set the total tax into the slice bucket */
                mySliceBucket.setTaxation(myTax);

                /* Distribute the Slice back to the chargeable events */
                theCharges.applyTax(myTax, theCharges.getSliceTotal());

                /* Calculate the total tax payable */
                myTax = theCharges.getTaxTotal();

                /* HiRate tax is the calculated tax minus the tax payable in the basic band */
                myHiTax.addAmount(myTax);

                /* Access the HiGainsBucket */
                myTaxBucket = theList.getTaxDetail(TaxClass.GAINSHI);
                myTaxBucket.setParent(myTopBucket);

                /* Subtract the basic band from the gains */
                myGains.subtractAmount(pBands.theBasicBand);

                /* Set the amount and tax explicitly */
                myTaxBucket.setAmount(myGains);
                myTaxBucket.setTaxation(myHiTax);
            }

            /* Re-access the gains */
            TransSummary mySrcBucket = theList.getTransSummary(TaxClass.GROSSTAXGAINS);
            myGains = new JMoney(mySrcBucket.getAmount());

            /* Subtract the gains from the tax bands */
            myGains.subtractAmount(pBands.theBasicBand);
            pBands.theBasicBand.setZero();
            if (theYear.hasAdditionalTaxBand()) {
                pBands.theHiBand.subtractAmount(myGains);
            }
        }

        /* Access the TaxDueTaxableGains Bucket */
        myTaxBucket = theList.getTaxDetail(TaxClass.TAXDUETAXGAINS);
        myTaxBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTaxBucket;
    }

    /**
     * calculate the tax due on capital gains.
     * @param pBands the remaining allowances and tax bands
     * @return the capital gains tax bucket
     */
    private TaxDetail calculateCapitalGainsTax(final TaxBands pBands) {
        /* Access Capital */
        TransSummary mySrcBucket = theList.getTransSummary(TaxClass.GROSSCAPGAINS);
        JMoney myCapital = new JMoney(mySrcBucket.getAmount());

        /* Store the total into the TaxDueCapital Bucket */
        TaxDetail myTopBucket = theList.getTaxDetail(TaxClass.TAXDUECAPGAINS);
        myTopBucket.setAmount(myCapital);

        /* Access the FreeGainsBucket */
        TaxDetail myTaxBucket = theList.getTaxDetail(TaxClass.CAPITALFREE);
        myTaxBucket.setParent(myTopBucket);

        /* Pick up the capital allowance */
        JMoney myAllowance = theYear.getCapitalAllow();
        JMoney myTax = new JMoney();
        TaxRegime myRegime = theYear.getTaxRegime();
        boolean isFinished = false;

        /* If the gains is greater than the capital allowance */
        if (myCapital.compareTo(myAllowance) > 0) {
            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myAllowance));

            /* Adjust the gains to remove allowance */
            myCapital.subtractAmount(myAllowance);

            /* else allowance is sufficient */
        } else {
            /* Set the correct value for the tax bucket and note that we have finished */
            myTax.addAmount(myTaxBucket.setAmount(myCapital));
            isFinished = true;
        }

        /* If we have gains left */
        if (!isFinished) {
            /* Access the BasicGainsBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.CAPITALBASIC);
            myTaxBucket.setRate((myRegime.hasCapitalGainsAsIncome() ? theYear.getBasicTaxRate() : theYear
                    .getCapTaxRate()));
            myTaxBucket.setParent(myTopBucket);

            /* Determine whether we need to use basic tax band */
            boolean bUseBasicBand = ((myRegime.hasCapitalGainsAsIncome()) || (theYear.getHiCapTaxRate() != null));

            /* If the gains is greater than the Basic Tax Band and we have no higher rate */
            if ((myCapital.compareTo(pBands.theBasicBand) > 0) || (!bUseBasicBand)) {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(pBands.theBasicBand));

                /* Adjust the gains to remove BasicBand */
                myCapital.subtractAmount(pBands.theBasicBand);
                pBands.theBasicBand.setZero();

                /* else we still have band left after gains */
            } else {
                /* Set the tax bucket and add the tax */
                myTax.addAmount(myTaxBucket.setAmount(myCapital));

                /* Adjust the basicBand to remove capital and note that we have finished */
                if (bUseBasicBand) {
                    pBands.theBasicBand.subtractAmount(myCapital);
                }
                isFinished = true;
            }
        }

        /* If we have gains left */
        if (!isFinished) {
            /* Access the HiGainsBucket */
            myTaxBucket = theList.getTaxDetail(TaxClass.CAPITALHI);
            myTaxBucket.setRate((myRegime.hasCapitalGainsAsIncome() ? theYear.getHiTaxRate() : theYear
                    .getHiCapTaxRate()));
            myTaxBucket.setParent(myTopBucket);

            /* Set the tax bucket and add the tax */
            myTax.addAmount(myTaxBucket.setAmount(myCapital));
        }

        /* Store the taxation total */
        myTopBucket.setTaxation(myTax);

        /* Return the tax bucket */
        return myTopBucket;
    }

    /**
     * Class to hold active allowances and tax bands.
     */
    private static class TaxBands {
        /**
         * The allowance.
         */
        private JMoney theAllowance = null;

        /**
         * The Lo Tax Band.
         */
        private JMoney theLoBand = null;

        /**
         * The Basic Tax Band.
         */
        private JMoney theBasicBand = null;

        /**
         * The High Tax Band.
         */
        private JMoney theHiBand = null;
    }
}
