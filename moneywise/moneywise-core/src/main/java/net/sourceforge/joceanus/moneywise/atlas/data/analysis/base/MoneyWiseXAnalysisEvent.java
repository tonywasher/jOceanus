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

import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseExchangeRate;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityPrice;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransTag;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.date.OceanusDate;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Analysis Event.
 */
public class MoneyWiseXAnalysisEvent
        extends PrometheusDataItem {
    /**
     * Local Report fields.
     */
    static final MetisFieldSet<MoneyWiseXAnalysisEvent> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseXAnalysisEvent.class);

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
     * Non-transaction indicator.
     */
    private static final int NONTRANS = 0x40000000;

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
     *
     * @param pList  the owning list
     * @param pTrans the transaction.
     */
    public MoneyWiseXAnalysisEvent(final MoneyWiseXAnalysisEventList pList,
                                   final MoneyWiseTransaction pTrans) {
        super(pList, pTrans.getIndexedId());
        theEventType = MoneyWiseXAnalysisEventType.TRANSACTION;
        theTransaction = pTrans;
        theDate = pTrans.getDate();
        thePrices = null;
        theXchgRates = null;
        theDepRates = null;
        theBalances = null;
    }

    /**
     * Event constructor.
     *
     * @param pList      the owning list
     * @param pEventType the eventType.
     * @param pDate      the date
     */
    public MoneyWiseXAnalysisEvent(final MoneyWiseXAnalysisEventList pList,
                                   final MoneyWiseXAnalysisEventType pEventType,
                                   final OceanusDate pDate) {
        super(pList, determineId(pEventType, pDate));
        theEventType = pEventType;
        theTransaction = null;
        theDate = pDate;
        thePrices = MoneyWiseXAnalysisEventType.SECURITYPRICE == theEventType ? new ArrayList<>() : null;
        theXchgRates = MoneyWiseXAnalysisEventType.XCHANGERATE == theEventType ? new ArrayList<>() : null;
        theDepRates = MoneyWiseXAnalysisEventType.DEPOSITRATE == theEventType ? new ArrayList<>() : null;
        theBalances = MoneyWiseXAnalysisEventType.OPENINGBALANCE == theEventType ? new ArrayList<>() : null;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    /**
     * Determine the id of a non-transaction event.
     * @param pEventType the eventType
     * @param pDate the date
     * @return the id
     */
    private static Integer determineId(final MoneyWiseXAnalysisEventType pEventType,
                                       final OceanusDate pDate) {
        final int myId = NONTRANS + (pDate.getId() << 2);
        switch (pEventType) {
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

    /**
     * Obtain the event type.
     *
     * @return the eventType
     */
    public MoneyWiseXAnalysisEventType getEventType() {
        return theEventType;
    }

    /**
     * Obtain the transaction.
     *
     * @return the transaction
     */
    public MoneyWiseTransaction getTransaction() {
        return theTransaction;
    }

    /**
     * Obtain the priceList.
     *
     * @return the priceList
     */
    private List<MoneyWiseSecurityPrice> getPrices() {
        return thePrices;
    }

    /**
     * Obtain the xchgRateList.
     *
     * @return the xchgRateList
     */
    private List<MoneyWiseExchangeRate> getXchgRates() {
        return theXchgRates;
    }

    /**
     * Obtain the depositRateList.
     *
     * @return the depositRateList
     */
    private List<MoneyWiseNewDepositRate> getDepRates() {
        return theDepRates;
    }

    /**
     * Obtain the openingBalanceList.
     *
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
    protected int compareValues(final PrometheusDataItem pThat) {
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
     *
     * @param pPrice the security Price.
     */
    public void declareSecurityPrice(final MoneyWiseSecurityPrice pPrice) {
        if (thePrices != null) {
            thePrices.add(pPrice);
        }
    }

    /**
     * declare exchangeRate.
     *
     * @param pRate the exchangeRate.
     */
    public void declareExchangeRate(final MoneyWiseExchangeRate pRate) {
        if (theXchgRates != null) {
            theXchgRates.add(pRate);
        }
    }

    /**
     * declare depositRate.
     *
     * @param pRate the depositRate.
     */
    public void declareDepositRate(final MoneyWiseNewDepositRate pRate) {
        if (theDepRates != null) {
            theDepRates.add(pRate);
        }
    }

    /**
     * declare opening balance.
     *
     * @param pAsset the asset.
     */
    public void declareOpeningBalance(final MoneyWiseAssetBase pAsset) {
        if (theBalances != null) {
            theBalances.add(pAsset);
        }
    }

    /**
     * obtain the securityPrice iterator.
     *
     * @return the iterator.
     */
    public Iterator<MoneyWiseSecurityPrice> priceIterator() {
        return thePrices != null ? thePrices.iterator() : Collections.emptyIterator();
    }

    /**
     * obtain the exchangeRate iterator.
     *
     * @return the iterator.
     */
    public Iterator<MoneyWiseExchangeRate> xchgRateIterator() {
        return theXchgRates != null ? theXchgRates.iterator() : Collections.emptyIterator();
    }

    /**
     * obtain the depositRate iterator.
     *
     * @return the iterator.
     */
    public Iterator<MoneyWiseNewDepositRate> depRateIterator() {
        return theDepRates != null ? theDepRates.iterator() : Collections.emptyIterator();
    }

    /**
     * obtain the openingBalance iterator.
     *
     * @return the iterator.
     */
    public Iterator<MoneyWiseAssetBase> balanceIterator() {
        return theBalances != null ? theBalances.iterator() : Collections.emptyIterator();
    }

    /**
     * Obtain the date.
     *
     * @return the date
     */
    public OceanusDate getDate() {
        return theDate;
    }

    /**
     * Set the date.
     *
     * @param pDate the date
     */
    public void setDate(final OceanusDate pDate) {
        if (theTransaction != null) {
            theTransaction.setDate(pDate);
        }
    }

    /**
     * Is the event a header?.
     *
     * @return true/false
     */
    public boolean isHeader() {
        return theTransaction != null && theTransaction.isHeader();
    }

    /**
     * Obtain the account.
     *
     * @return the account
     */
    public MoneyWiseTransAsset getAccount() {
        return theTransaction == null ? null : theTransaction.getAccount();
    }

    /**
     * Set the account.
     *
     * @param pAccount the account
     */
    public void setAccount(final MoneyWiseTransAsset pAccount) {
        if (theTransaction != null) {
            theTransaction.setAccount(pAccount);
        }
    }

    /**
     * Obtain the category.
     *
     * @return the category
     */
    public MoneyWiseTransCategory getCategory() {
        return theTransaction == null ? null : theTransaction.getCategory();
    }

    /**
     * Set the category.
     *
     * @param pCategory the category
     */
    public void setCategory(final MoneyWiseTransCategory pCategory) {
        if (theTransaction != null) {
            theTransaction.setCategory(pCategory);
        }
    }

    /**
     * Obtain the direction.
     *
     * @return the direction
     */
    public MoneyWiseAssetDirection getDirection() {
        return theTransaction == null ? null : theTransaction.getDirection();
    }

    /**
     * Set the direction.
     *
     * @param pDirection the direction
     */
    public void setDirection(final MoneyWiseAssetDirection pDirection) {
        if (theTransaction != null) {
            theTransaction.setDirection(pDirection);
        }
    }

    /**
     * Obtain the partner.
     *
     * @return the partner
     */
    public MoneyWiseTransAsset getPartner() {
        return theTransaction == null ? null : theTransaction.getPartner();
    }

    /**
     * Set the partner.
     *
     * @param pPartner the partner
     */
    public void setPartner(final MoneyWiseTransAsset pPartner) {
        if (theTransaction != null) {
            theTransaction.setPartner(pPartner);
        }
    }

    /**
     * Obtain the amount.
     *
     * @return the amount
     */
    public OceanusMoney getAmount() {
        return theTransaction == null ? null : theTransaction.getAmount();
    }

    /**
     * Set the amount.
     *
     * @param pAmount the amount
     * @throws OceanusException on error
     */
    public void setAmount(final OceanusMoney pAmount) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setAmount(pAmount);
        }
    }

    /**
     * is the event reconciled?
     *
     * @return true/false
     */
    public Boolean isReconciled() {
        return theTransaction == null ? Boolean.TRUE : theTransaction.isReconciled();
    }

    /**
     * Set the reconciled.
     *
     * @param pReconciled the reconciled flag
     */
    public void setReconciled(final Boolean pReconciled) {
        if (theTransaction != null) {
            theTransaction.setReconciled(pReconciled);
        }
    }

    /**
     * Obtain the comment.
     *
     * @return the comment
     */
    public String getComments() {
        return theTransaction == null ? null : theTransaction.getComments();
    }

    /**
     * Set the comment.
     *
     * @param pComment the comment
     * @throws OceanusException on error
     */
    public void setComments(final String pComment) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setComments(pComment);
        }
    }

    /**
     * Obtain the reference.
     *
     * @return the reference
     */
    public String getReference() {
        return theTransaction == null ? null : theTransaction.getReference();
    }

    /**
     * Set the reference.
     *
     * @param pReference the reference
     * @throws OceanusException on error
     */
    public void setReference(final String pReference) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setReference(pReference);
        }
    }

    /**
     * Obtain the taxCredit.
     *
     * @return the taxCredit
     */
    public OceanusMoney getTaxCredit() {
        return theTransaction == null ? null : theTransaction.getTaxCredit();
    }

    /**
     * Set the taxCredit.
     *
     * @param pCredit the taxCredit
     * @throws OceanusException on error
     */
    public void setTaxCredit(final OceanusMoney pCredit) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setTaxCredit(pCredit);
        }
    }

    /**
     * Obtain the employeesNI.
     *
     * @return the employeesNI
     */
    public OceanusMoney getEmployeeNatIns() {
        return theTransaction == null ? null : theTransaction.getEmployeeNatIns();
    }

    /**
     * Set the employeeNI.
     *
     * @param pNatIns the employeeNI
     * @throws OceanusException on error
     */
    public void setEmployeeNatIns(final OceanusMoney pNatIns) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setEmployeeNatIns(pNatIns);
        }
    }

    /**
     * Obtain the employerNI.
     *
     * @return the employerNI
     */
    public OceanusMoney getEmployerNatIns() {
        return theTransaction == null ? null : theTransaction.getEmployerNatIns();
    }

    /**
     * Set the employerNI.
     *
     * @param pNatIns the employerNI
     * @throws OceanusException on error
     */
    public void setEmployerNatIns(final OceanusMoney pNatIns) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setEmployerNatIns(pNatIns);
        }
    }

    /**
     * Obtain the deemedBenefit.
     *
     * @return the deemedBenefit
     */
    public OceanusMoney getDeemedBenefit() {
        return theTransaction == null ? null : theTransaction.getDeemedBenefit();
    }

    /**
     * Set the deemedBenefit.
     *
     * @param pBenefit the benefit
     * @throws OceanusException on error
     */
    public void setDeemedBenefit(final OceanusMoney pBenefit) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setDeemedBenefit(pBenefit);
        }
    }

    /**
     * Obtain the withheld.
     *
     * @return the withheld
     */
    public OceanusMoney getWithheld() {
        return theTransaction == null ? null : theTransaction.getWithheld();
    }

    /**
     * Set the withheld.
     *
     * @param pWithheld the withheld
     * @throws OceanusException on error
     */
    public void setWithheld(final OceanusMoney pWithheld) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setWithheld(pWithheld);
        }
    }

    /**
     * Obtain the partnerAmount.
     *
     * @return the partnerAmount
     */
    public OceanusMoney getPartnerAmount() {
        return theTransaction == null ? null : theTransaction.getPartnerAmount();
    }

    /**
     * Set the partnerAmount.
     *
     * @param pAmount the amount
     * @throws OceanusException on error
     */
    public void setPartnerAmount(final OceanusMoney pAmount) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setEmployeeNatIns(pAmount);
        }
    }

    /**
     * Obtain the returnedCashAccount.
     *
     * @return the returnedCashAccount
     */
    public MoneyWiseTransAsset getReturnedCashAccount() {
        return theTransaction == null ? null : theTransaction.getReturnedCashAccount();
    }

    /**
     * Set the returnedCashAccount.
     *
     * @param pAccount the returnedCashAccount
     * @throws OceanusException on error
     */
    public void setReturnedCashAccount(final MoneyWiseTransAsset pAccount) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setReturnedCashAccount(pAccount);
        }
    }

    /**
     * Obtain the returnedCash.
     *
     * @return the returnedCash
     */
    public OceanusMoney getReturnedCash() {
        return theTransaction == null ? null : theTransaction.getReturnedCash();
    }

    /**
     * Set the returnedCash.
     *
     * @param pAmount the amount
     * @throws OceanusException on error
     */
    public void setReturnedCash(final OceanusMoney pAmount) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setReturnedCash(pAmount);
        }
    }

    /**
     * Obtain the accountDeltaUnits.
     *
     * @return the accountDeltaUnits
     */
    public OceanusUnits getAccountDeltaUnits() {
        return theTransaction == null ? null : theTransaction.getAccountDeltaUnits();
    }

    /**
     * Set the accountDeltaUnits.
     *
     * @param pDelta the deltaUnits
     * @throws OceanusException on error
     */
    public void setAccountDeltaUnits(final OceanusUnits pDelta) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setAccountDeltaUnits(pDelta);
        }
    }

    /**
     * Obtain the partnerDeltaUnits.
     *
     * @return the partnerDeltaUnits
     */
    public OceanusUnits getPartnerDeltaUnits() {
        return theTransaction == null ? null : theTransaction.getPartnerDeltaUnits();
    }

    /**
     * Set the partnerDeltaUnits.
     *
     * @param pDelta the deltaUnits
     * @throws OceanusException on error
     */
    public void setPartnerDeltaUnits(final OceanusUnits pDelta) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setPartnerDeltaUnits(pDelta);
        }
    }

    /**
     * Obtain the dilution.
     *
     * @return the dilution
     */
    public OceanusRatio getDilution() {
        return theTransaction == null ? null : theTransaction.getDilution();
    }

    /**
     * Set the dilution.
     *
     * @param pDilution the dilution
     * @throws OceanusException on error
     */
    public void setDilution(final OceanusRatio pDilution) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setDilution(pDilution);
        }
    }

    /**
     * Obtain the transactionTags.
     *
     * @return the transactionTags
     */
    public List<MoneyWiseTransTag> getTransactionTags() {
        return theTransaction == null ? null : theTransaction.getTransactionTags();
    }

    /**
     * Set the transactionTags.
     *
     * @param pTags the tags
     * @throws OceanusException on error
     */
    public void setTransactionTags(final List<MoneyWiseTransTag> pTags) throws OceanusException {
        if (theTransaction != null) {
            theTransaction.setTransactionTags(pTags);
        }
    }

    /**
     * Switch direction.
     */
    public void switchDirection() {
        if (theTransaction != null) {
            theTransaction.switchDirection();
        }
    }

    @Override
    public int getNextVersion() {
        return 0;
    }

    @Override
    public boolean isLocked() {
        return theTransaction == null || theTransaction.isLocked();
    }

    /**
     * Determines whether we can switch direction.
     *
     * @return true/false
     */
    public boolean canSwitchDirection() {
        return theTransaction != null && theTransaction.canSwitchDirection();
    }
}
