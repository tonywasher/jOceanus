/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.atlas.data.analysis.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldVersionedItem;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;

/**
 * Analysis Event.
 */
public class MoneyWiseXAnalysisEvent
        extends MetisFieldVersionedItem
        implements MetisFieldItem, Comparable<Object> {
    /**
     * Local Report fields.
     */
    private static final MetisFieldSet<MoneyWiseXAnalysisEvent> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisEvent.class);

    /*
     * FieldIds.
     */
    static {
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_DATE, MoneyWiseXAnalysisEvent::getDate);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_TYPE, MoneyWiseXAnalysisEvent::getEventType);
        FIELD_DEFS.declareLocalField(MoneyWiseBasicResource.TRANSACTION_NAME, MoneyWiseXAnalysisEvent::getTransaction);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_PRICES, MoneyWiseXAnalysisEvent::getPrices);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_XCHGRATES, MoneyWiseXAnalysisEvent::getXchgRates);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_DEPRATES, MoneyWiseXAnalysisEvent::getDepRates);
        FIELD_DEFS.declareLocalField(MoneyWiseXAnalysisBaseResource.EVENT_BALANCES, MoneyWiseXAnalysisEvent::getBalances);
    }

    /**
     * Id.
     */
    private final Integer theId;

    /**
     * EventType.
     */
    private final MoneyWiseXAnalysisEventType theEventType;

    /**
     * Transaction.
     */
    private final MoneyWiseTransaction theTransaction;

    /**
     * The Date.
     */
    private final OceanusDate theDate;

    /**
     * The securityPrice List.
     */
    private final List<MoneyWiseSecurityPrice> thePrices;

    /**
     * The exchangeRate List.
     */
    private final List<MoneyWiseExchangeRate> theXchgRates;

    /**
     * The depositRate List.
     */
    private final List<MoneyWiseNewDepositRate> theDepRates;

    /**
     * The opening balance list.
     */
    private final List<MoneyWiseAssetBase> theBalances;

    /**
     * Transaction constructor.
     * @param pTrans the transaction.
     */
    public MoneyWiseXAnalysisEvent(final MoneyWiseTransaction pTrans) {
        theId = pTrans.getIndexedId();
        theEventType = MoneyWiseXAnalysisEventType.TRANSACTION;
        theTransaction = pTrans;
        theDate = pTrans.getDate();
        thePrices = null;
        theXchgRates = null;
        theDepRates = null;
        theBalances = null;
    }

    /**
     * List constructor.
     * @param pType the eventType.
     * @param pDate the date
     */
    public MoneyWiseXAnalysisEvent(final MoneyWiseXAnalysisEventType pType,
                                   final OceanusDate pDate) {
        theEventType = pType;
        theTransaction = null;
        theDate = pDate;
        theId = determineId();
        thePrices = MoneyWiseXAnalysisEventType.SECURITYPRICE == pType ? new ArrayList<>() : null;
        theXchgRates = MoneyWiseXAnalysisEventType.XCHANGERATE == pType ? new ArrayList<>() : null;
        theDepRates = MoneyWiseXAnalysisEventType.DEPOSITRATE == pType ? new ArrayList<>() : null;
        theBalances = MoneyWiseXAnalysisEventType.OPENINGBALANCE == pType ? new ArrayList<>() : null;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Determine the id of a non-transaction event.
     * @return the id
     */
    private Integer determineId() {
        final int myId = -(theDate.getId() << 2);
        switch (theEventType) {
            case SECURITYPRICE:
                return myId - 1;
            case XCHANGERATE:
                return myId;
            case DEPOSITRATE:
                return myId + 1;
            case OPENINGBALANCE:
            default:
                return myId + 2;
        }
    }

    @Override
    public Integer getIndexedId() {
        return theId;
    }

    /**
     * Obtain the event type.
     * @return the eventType
     */
    public MoneyWiseXAnalysisEventType getEventType() {
        return theEventType;
    }

    /**
     * Obtain the transaction.
     * @return the transaction
     */
    public MoneyWiseTransaction getTransaction() {
        return theTransaction;
    }

    /**
     * Obtain the priceList.
     * @return the priceList
     */
    private List<MoneyWiseSecurityPrice> getPrices() {
        return thePrices;
    }

    /**
     * Obtain the xchgRateList.
     * @return the xchgRateList
     */
    private List<MoneyWiseExchangeRate> getXchgRates() {
        return theXchgRates;
    }

    /**
     * Obtain the depositRateList.
     * @return the depositRateList
     */
    private List<MoneyWiseNewDepositRate> getDepRates() {
        return theDepRates;
    }

    /**
     * Obtain the openingBalanceList.
     * @return the openingBalanceList
     */
    private List<MoneyWiseAssetBase> getBalances() {
        return theBalances;
    }

    @Override
    public int compareTo(final Object pThat) {
        /* Handle the trivial cases */
        if (this.equals(pThat)) {
            return 0;
        }
        if (pThat == null) {
            return -1;
        }

        /* Non-DataItems are last */
        if (!(pThat instanceof MoneyWiseXAnalysisEvent)) {
            return -1;
        }

        /* Check date and then data type */
        final MoneyWiseXAnalysisEvent myThat = (MoneyWiseXAnalysisEvent) pThat;
        final int iDiff = theDate.compareTo(myThat.getDate());
        if (iDiff != 0) {
            return iDiff;
        }
        if (theEventType != myThat.getEventType()) {
            return theEventType.compareTo(myThat.getEventType());
        }

        /* Only event types with same date are transactions */
        return theTransaction == null ? 0 : theTransaction.compareTo(myThat.getTransaction());
    }

    @Override
    public boolean equals(final Object pThat) {
        /* Handle trivial cases */
        if (this == pThat) {
            return true;
        } else if (!(pThat instanceof MoneyWiseXAnalysisEvent)) {
            return false;
        }

        /* Access object correctly */
        final MoneyWiseXAnalysisEvent myThat = (MoneyWiseXAnalysisEvent) pThat;

        /* Ensure date is identical */
        if (!theDate.equals(myThat.getDate())) {
            return false;
        }

        /* Ensure eventType is identical */
        if (theEventType != myThat.getEventType()) {
            return false;
        }

        /* Check transaction */
        return Objects.equals(theTransaction, myThat.getTransaction());
    }

    @Override
    public int hashCode() {
        return Objects.hash(theDate, theEventType, theTransaction);
    }

    /**
     * declare securityPrice.
     * @param pPrice the security Price.
     */
    public void declareSecurityPrice(final MoneyWiseSecurityPrice pPrice) {
        if (thePrices != null) {
            thePrices.add(pPrice);
        }
    }

    /**
     * declare exchangeRate.
     * @param pRate the exchangeRate.
     */
    public void declareExchangeRate(final MoneyWiseExchangeRate pRate) {
        if (theXchgRates != null) {
            theXchgRates.add(pRate);
        }
    }

    /**
     * declare depositRate.
     * @param pRate the depositRate.
     */
    public void declareDepositRate(final MoneyWiseNewDepositRate pRate) {
        if (theDepRates != null) {
            theDepRates.add(pRate);
        }
    }

    /**
     * declare opening balance.
     * @param pAsset the asset.
     */
    public void declareOpeningBalance(final MoneyWiseAssetBase pAsset) {
        if (theBalances != null) {
            theBalances.add(pAsset);
        }
    }

    /**
     * obtain the securityPrice iterator.
     * @return the iterator.
     */
    public Iterator<MoneyWiseSecurityPrice> priceIterator() {
        return thePrices != null ? thePrices.iterator() : Collections.emptyIterator();
    }

    /**
     * obtain the exchangeRate iterator.
     * @return the iterator.
     */
    public Iterator<MoneyWiseExchangeRate> xchgRateIterator() {
        return theXchgRates != null ? theXchgRates.iterator() : Collections.emptyIterator();
    }

    /**
     * obtain the depositRate iterator.
     * @return the iterator.
     */
    public Iterator<MoneyWiseNewDepositRate> depRateIterator() {
        return theDepRates != null ? theDepRates.iterator() : Collections.emptyIterator();
    }

    /**
     * obtain the openingBalance iterator.
     * @return the iterator.
     */
    public Iterator<MoneyWiseAssetBase> balanceIterator() {
        return theBalances != null ? theBalances.iterator() : Collections.emptyIterator();
    }

    /**
     * Obtain the date.
     * @return the date
     */
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Is the event a header?.
     * @return true/false
     */
    public boolean isHeader() {
        return theTransaction != null && theTransaction.isHeader();
    }

    /**
     * Obtain the account.
     * @return the account
     */
    public MoneyWiseTransAsset getAccount() {
        return theTransaction == null ? null : theTransaction.getAccount();
    }

    /**
     * Obtain the category.
     * @return the category
     */
    public MoneyWiseTransCategory getCategory() {
        return theTransaction == null ? null : theTransaction.getCategory();
    }

    /**
     * Obtain the direction.
     * @return the direction
     */
    public MoneyWiseAssetDirection getDirection() {
        return theTransaction == null ? null : theTransaction.getDirection();
    }

    /**
     * Obtain the partner.
     * @return the partner
     */
    public MoneyWiseTransAsset getPartner() {
        return theTransaction == null ? null : theTransaction.getPartner();
    }

    /**
     * Obtain the amount.
     * @return the amount
     */
    public OceanusMoney getAmount() {
        return theTransaction == null ? null : theTransaction.getAmount();
    }

    /**
     * is the event reconciled?
     * @return true/false
     */
    public Boolean isReconciled() {
        return theTransaction == null ? Boolean.TRUE : theTransaction.isReconciled();
    }

    /**
     * Obtain the comment.
     * @return the comment
     */
    public String getComments() {
        return theTransaction == null ? null : theTransaction.getComments();
    }

    /**
     * Obtain the reference.
     * @return the reference
     */
    public String getReference() {
        return theTransaction == null ? null : theTransaction.getReference();
    }

    /**
     * Obtain the taxCredit.
     * @return the taxCredit
     */
    public OceanusMoney getTaxCredit() {
        return theTransaction == null ? null : theTransaction.getTaxCredit();
    }

    /**
     * Obtain the employeesNI.
     * @return the employeesNI
     */
    public OceanusMoney getEmployeeNatIns() {
        return theTransaction == null ? null : theTransaction.getEmployeeNatIns();
    }

    /**
     * Obtain the employerNI.
     * @return the employerNI
     */
    public OceanusMoney getEmployerNatIns() {
        return theTransaction == null ? null : theTransaction.getEmployerNatIns();
    }

    /**
     * Obtain the deemedBenefit.
     * @return the deemedBenefit
     */
    public OceanusMoney getDeemedBenefit() {
        return theTransaction == null ? null : theTransaction.getDeemedBenefit();
    }

    /**
     * Obtain the withheld.
     * @return the withheld
     */
    public OceanusMoney getWithheld() {
        return theTransaction == null ? null : theTransaction.getWithheld();
    }

    /**
     * Obtain the qualifyingYears.
     * @return the qualifyingYears
     */
    public Integer getQualifyingYears() {
        return theTransaction == null ? null : theTransaction.getYears();
    }

    /**
     * Obtain the partnerAmount.
     * @return the partnerAmount
     */
    public OceanusMoney getPartnerAmount() {
        return theTransaction == null ? null : theTransaction.getPartnerAmount();
    }

    /**
     * Obtain the returnedCashAccount.
     * @return the returnedCashAccount
     */
    public MoneyWiseTransAsset getReturnedCashAccount() {
        return theTransaction == null ? null : theTransaction.getReturnedCashAccount();
    }

    /**
     * Obtain the partnerAmount.
     * @return the partnerAmount
     */
    public OceanusMoney getReturnedCash() {
        return theTransaction == null ? null : theTransaction.getReturnedCash();
    }

    /**
     * Obtain the exchangeRate.
     * @return the exchangeRate
     */
    public OceanusRatio getExchangeRate() {
        return theTransaction == null ? null : theTransaction.getExchangeRate();
    }

    /**
     * Obtain the accountDeltaUnits.
     * @return the accountDeltaUnits
     */
    public OceanusUnits getAccountDeltaUnits() {
        return theTransaction == null ? null : theTransaction.getAccountDeltaUnits();
    }

    /**
     * Obtain the partnerDeltaUnits.
     * @return the partnerDeltaUnits
     */
    public OceanusUnits getPartnerDeltaUnits() {
        return theTransaction == null ? null : theTransaction.getPartnerDeltaUnits();
    }

    /**
     * Obtain the dilution.
     * @return the dilution
     */
    public OceanusRatio getDilution() {
        return theTransaction == null ? null : theTransaction.getDilution();
    }

    /**
     * Obtain the price.
     * @return the price
     */
    public OceanusPrice getPrice() {
        return theTransaction == null ? null : theTransaction.getPrice();
    }

    /**
     * Obtain the commission.
     * @return the commission
     */
    public OceanusMoney getCommission() {
        return theTransaction == null ? null : theTransaction.getCommission();
    }

    /**
     * Obtain the transactionTags.
     * @return the transactionTags
     */
    public List<MoneyWiseTransTag> getTransactionTags() {
        return theTransaction == null ? null : theTransaction.getTransactionTags();
    }

    @Override
    public int getNextVersion() {
        return 0;
    }
}
