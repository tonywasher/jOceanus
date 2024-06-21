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

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseAssetType;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseSecurityHolding;

/**
 * Credit XferIn Analysis.
 */
public class MoneyWiseXAnalysisXferIn {
    /**
     * The transAnalyser.
     */
    private final MoneyWiseXAnalysisTransAnalyser theTrans;

    /**
     * The transaction.
     */
    private MoneyWiseXAnalysisTransaction theTransaction;

    /**
     * Constructor.
     * @param pAnalyser the event analyser.
     */
    MoneyWiseXAnalysisXferIn(final MoneyWiseXAnalysisEventAnalyser pAnalyser) {
        theTrans = pAnalyser.getAnalyser();
    }

    /**
     * Process a transaction that is a transferIn.
     * @param pTrans  the transaction
     */
    void processTransferIn(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Record the transaction */
        theTransaction = pTrans;

        /* Access debit account and category */
        final MoneyWiseAssetBase myDebit = (MoneyWiseAssetBase) theTransaction.getDebitAccount();
        final MoneyWiseSecurityHolding myCredit = (MoneyWiseSecurityHolding) theTransaction.getCreditAccount();

        /* Process on the type of the debit account */
        if (myDebit.getAssetType() == MoneyWiseAssetType.PAYEE) {
            theTrans.processDebitPayee((MoneyWisePayee) myDebit);
        } else {
            theTrans.processDebitAsset(myDebit);
        }

        /* Adjust the credit transfer details */
        processCreditXferIn(myCredit);
    }

    /**
     * Process the credit side of a transfer in transaction.
     * @param pTrans  the transaction
     */
    void processCreditXferIn(final MoneyWiseXAnalysisTransaction pTrans) {
        /* Record the transaction */
        theTransaction = pTrans;

        /* Process the credit */
        final MoneyWiseSecurityHolding myCredit = (MoneyWiseSecurityHolding) theTransaction.getCreditAccount();
        processCreditXferIn(myCredit);
    }

    /**
     * Process the credit side of a transfer in transaction.
     * @param pHolding the credit holding
     */
    private void processCreditXferIn(final MoneyWiseSecurityHolding pHolding) {
//        /* Transfer is to the credit account and may or may not have a change to the units */
//        TethysMoney myAmount = theHelper.getCreditAmount();
//        final TethysRatio myExchangeRate = theHelper.getCreditExchangeRate();
//        final MoneyWiseSecurity mySecurity = pHolding.getSecurity();
//
//        /* Access the Asset Security Bucket */
//        final MoneyWiseAnalysisSecurityBucket myAsset = thePortfolioBuckets.getBucket(pHolding);
//        final boolean isForeign = myAsset.isForeignCurrency();
//
//        /* If this is a foreign currency asset */
//        if (isForeign) {
//            /* Adjust foreign invested amount */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.FOREIGNINVESTED, myAmount);
//
//            /* Switch to local amount */
//            myAmount = theHelper.getLocalAmount();
//        }
//
//        /* Adjust the cost and investment */
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.RESIDUALCOST, myAmount);
//        myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.INVESTED, myAmount);
//
//        /* Determine the delta units */
//        final MoneyWiseSecurityClass mySecClass = mySecurity.getCategoryClass();
//        TethysUnits myDeltaUnits = theHelper.getCreditUnits();
//        TethysUnits myUnits = myAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        if (mySecClass.isAutoUnits() && myUnits.isZero()) {
//            myDeltaUnits = TethysUnits.getWholeUnits(mySecClass.getAutoUnits());
//        }
//
//        /* If we have new units */
//        if (myDeltaUnits != null) {
//            /* Record change in units */
//            myAsset.adjustCounter(MoneyWiseAnalysisSecurityAttr.UNITS, myDeltaUnits);
//        }
//
//        /* Adjust for National Insurance */
//        myAsset.adjustForNIPayments(theHelper);
//
//        /* Get the appropriate price for the account */
//        final TethysPrice myPrice = thePriceMap.getPriceForDate(mySecurity, theHelper.getDate());
//
//        /* Determine value of this stock after the transaction */
//        myUnits = myAsset.getValues().getUnitsValue(MoneyWiseAnalysisSecurityAttr.UNITS);
//        TethysMoney myValue = myUnits.valueAtPrice(myPrice);
//
//        /* If we are foreign */
//        if (isForeign) {
//            /* Determine local value */
//            myValue = myValue.convertCurrency(theAnalysis.getCurrency().getCurrency(), myExchangeRate);
//        }
//
//        /* Register the transaction */
//        final MoneyWiseAnalysisSecurityValues myValues = myAsset.registerTransaction(theHelper);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.PRICE, myPrice);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.VALUATION, myValue);
//        myValues.setValue(MoneyWiseAnalysisSecurityAttr.CASHINVESTED, myAmount);
//        if (isForeign) {
//            myValues.setValue(MoneyWiseAnalysisSecurityAttr.EXCHANGERATE, myExchangeRate);
//        }
    }
}
