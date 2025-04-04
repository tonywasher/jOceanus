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
package net.sourceforge.joceanus.moneywise.data.validate;

import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransAsset;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransBase;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfo;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfoSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.oceanus.base.OceanusException;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusPrice;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRatio;
import net.sourceforge.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateInfoSet;

import java.util.Currency;
import java.util.Iterator;

/**
 * Validate TransInfoSet.
 */
public class MoneyWiseValidateTransInfoSet
        extends PrometheusValidateInfoSet<MoneyWiseTransInfo> {
    /**
     * Are we using new validation?
     */
    private final boolean newValidation;

    /**
     * Constructor.
     * @param pNewValidation true/false
     */
    MoneyWiseValidateTransInfoSet(final boolean pNewValidation) {
        newValidation = pNewValidation;
    }

    @Override
    public MoneyWiseTransaction getOwner() {
        return (MoneyWiseTransaction) super.getOwner();
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Access details about the Transaction */
        final MoneyWiseTransaction myTransaction = getOwner();
        final MoneyWiseTransCategory myCategory = myTransaction.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        final MoneyWiseTransCategoryClass myClass = myCategory.getCategoryTypeClass();
        if (myClass == null) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Switch on class */
        switch ((MoneyWiseTransInfoClass) pClass) {
            /* Reference and comments are always available */
            case REFERENCE:
            case COMMENTS:
            case TRANSTAG:
                return MetisFieldRequired.CANEXIST;

            /* NatInsurance and benefit can only occur on salary/pensionContribution */
            case EMPLOYERNATINS:
            case EMPLOYEENATINS:
                return myClass.isNatInsurance()
                        ? MetisFieldRequired.CANEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Benefit can only occur on salary */
            case DEEMEDBENEFIT:
                return myClass == MoneyWiseTransCategoryClass.TAXEDINCOME
                        ? MetisFieldRequired.CANEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Handle Withheld separately */
            case WITHHELD:
                return isWithheldAmountRequired(myClass);

            /* Handle Tax Credit */
            case TAXCREDIT:
                return isTaxCreditClassRequired(myClass);

            /* Handle AccountUnits */
            case ACCOUNTDELTAUNITS:
                return isAccountUnitsDeltaRequired(myClass);

            /* Handle PartnerUnits */
            case PARTNERDELTAUNITS:
                return isPartnerUnitsDeltaRequired(myClass);

            /* Handle Dilution separately */
            case DILUTION:
                return isDilutionClassRequired(myClass);

            /* Qualify Years is needed only for Taxable Gain */
            case QUALIFYYEARS:
                return isQualifyingYearsClassRequired(myClass);

            /* Handle ThirdParty separately */
            case RETURNEDCASHACCOUNT:
                return isReturnedCashAccountRequired(myClass);
            case RETURNEDCASH:
                return isReturnedCashRequired(myTransaction);

            case PARTNERAMOUNT:
                return isPartnerAmountClassRequired(myClass);

            case XCHANGERATE:
                return isXchangeRateClassRequired(myClass);

            case PRICE:
                return isPriceClassRequired(myClass);

            case COMMISSION:
                return isCommissionClassRequired(myClass);

            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if an infoSet class is metaData.
     * @param pClass the infoSet class
     * @return the status
     */
    public boolean isMetaData(final MoneyWiseTransInfoClass pClass) {
        /* Switch on class */
        switch (pClass) {
            /* Can always change reference/comments/tags */
            case REFERENCE:
            case COMMENTS:
            case TRANSTAG:
                return true;

            /* All others are locked */
            default:
                return false;
        }
    }

    /**
     * Determine if a TaxCredit infoSet class is required.
     * @param pClass the category class
     * @return the status
     */
    private MetisFieldRequired isTaxCreditClassRequired(final MoneyWiseTransCategoryClass pClass) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseTaxCredit myYear = myTrans.getTaxYear();
        final MoneyWiseTransAsset myAccount = myTrans.getAccount();

        /* Switch on class */
        switch (pClass) {
            case TAXEDINCOME:
                return MetisFieldRequired.MUSTEXIST;
            case LOANINTERESTCHARGED:
                return MetisFieldRequired.CANEXIST;
            case LOYALTYBONUS:
            case INTEREST:
                return myAccount.isTaxFree()
                        || myAccount.isGross()
                        || !myYear.isTaxCreditRequired()
                        ? MetisFieldRequired.NOTALLOWED
                        : MetisFieldRequired.MUSTEXIST;
            case DIVIDEND:
                return !myAccount.isTaxFree()
                        && (myYear.isTaxCreditRequired() || myAccount.isForeign())
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            case TRANSFER:
                return myAccount instanceof MoneyWiseSecurityHolding
                        && ((MoneyWiseSecurityHolding) myAccount).getSecurity().isSecurityClass(MoneyWiseSecurityClass.LIFEBOND)
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a Withheld amount is required.
     * @param pClass the category class
     * @return the status
     */
    private static MetisFieldRequired isWithheldAmountRequired(final MoneyWiseTransCategoryClass pClass) {
        /* Withheld is only available for salary and interest */
        switch (pClass) {
            case TAXEDINCOME:
            case INTEREST:
                return MetisFieldRequired.CANEXIST;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if an AccountDeltaUnits infoSet class is required.
     * @param pClass the category class
     * @return the status
     */
    private MetisFieldRequired isAccountUnitsDeltaRequired(final MoneyWiseTransCategoryClass pClass) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseTransAsset myAccount = myTrans.getAccount();
        final MoneyWiseTransAsset myPartner = myTrans.getPartner();
        final MoneyWiseAssetDirection myDir = myTrans.getDirection();

        /* Account must be security holding */
        if (!(myAccount instanceof MoneyWiseSecurityHolding)) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Account cannot be autoUnits */
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) myAccount;
        if (myHolding.getSecurity().getCategoryClass().isAutoUnits()) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Handle different transaction types */
        switch (pClass) {
            case TRANSFER:
            case STOCKDEMERGER:
                return MetisFieldRequired.CANEXIST;
            case UNITSADJUST:
            case STOCKSPLIT:
            case INHERITED:
                return MetisFieldRequired.MUSTEXIST;
            case DIVIDEND:
                return myAccount.equals(myPartner)
                        ? MetisFieldRequired.CANEXIST
                        : MetisFieldRequired.NOTALLOWED;
            case STOCKRIGHTSISSUE:
                return myDir.isFrom()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if an PartnerDeltaUnits infoSet class is required.
     * @param pClass the category class
     * @return the status
     */
    private MetisFieldRequired isPartnerUnitsDeltaRequired(final MoneyWiseTransCategoryClass pClass) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseTransAsset myPartner = myTrans.getPartner();
        final MoneyWiseAssetDirection myDir = myTrans.getDirection();

        /* Partner must be security holding */
        if (!(myPartner instanceof MoneyWiseSecurityHolding)) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Partner cannot be autoUnits */
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) myPartner;
        if (myHolding.getSecurity().getCategoryClass().isAutoUnits()) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Handle different transaction types */
        switch (pClass) {
            case TRANSFER:
                return MetisFieldRequired.CANEXIST;
            case STOCKDEMERGER:
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
                return MetisFieldRequired.MUSTEXIST;
            case STOCKRIGHTSISSUE:
                return myDir.isTo()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a Dilution infoSet class is required.
     * @param pClass the category class
     * @return the status
     */
    private static MetisFieldRequired isDilutionClassRequired(final MoneyWiseTransCategoryClass pClass) {
        /* Dilution is only required for stock split/deMerger */
        switch (pClass) {
            case STOCKSPLIT:
            case UNITSADJUST:
                return MetisFieldRequired.CANEXIST;
            case STOCKDEMERGER:
                return MetisFieldRequired.MUSTEXIST;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a ReturnedCash Account class is required.
     * @param pClass the category class
     * @return the status
     */
    private static MetisFieldRequired isReturnedCashAccountRequired(final MoneyWiseTransCategoryClass pClass) {
        /* Returned Cash is possible only for StockTakeOver */
        return pClass == MoneyWiseTransCategoryClass.STOCKTAKEOVER
                ? MetisFieldRequired.CANEXIST
                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if a ReturnedCash value is required.
     * @param pTransaction the transaction
     * @return the status
     */
    private static MetisFieldRequired isReturnedCashRequired(final MoneyWiseTransaction pTransaction) {
        /* Returned Cash Amount is possible only if ReturnedCashAccount exists */
        return pTransaction.getReturnedCashAccount() != null
                ? MetisFieldRequired.MUSTEXIST
                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if a PartnerAmount infoSet class is required.
     * @param pCategory the category
     * @return the status
     */
    private MetisFieldRequired isPartnerAmountClassRequired(final MoneyWiseTransCategoryClass pCategory) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseTransAsset myAccount = myTrans.getAccount();
        final MoneyWiseTransAsset myPartner = myTrans.getPartner();

        /* If the transaction requires null amount, then partner amount must also be null */
        if (pCategory.needsNullAmount()) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* If Partner currency is null or the same as Account then Partner amount is not allowed */
        final MoneyWiseCurrency myCurrency = myAccount.getAssetCurrency();
        final MoneyWiseCurrency myPartnerCurrency = myPartner == null ? null : myPartner.getAssetCurrency();
        if (myCurrency == null || myPartnerCurrency == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        return MetisDataDifference.isEqual(myCurrency, myPartnerCurrency)
                ? MetisFieldRequired.NOTALLOWED
                : MetisFieldRequired.MUSTEXIST;
    }

    /**
     * Determine if an QualifyingYears infoSet class is required.
     * @param pCategory the category
     * @return the status
     */
    private MetisFieldRequired isQualifyingYearsClassRequired(final MoneyWiseTransCategoryClass pCategory) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseTransAsset myAccount = myTrans.getAccount();

        return pCategory == MoneyWiseTransCategoryClass.TRANSFER
                && myAccount instanceof MoneyWiseSecurityHolding
                && ((MoneyWiseSecurityHolding) myAccount).getSecurity().isSecurityClass(MoneyWiseSecurityClass.LIFEBOND)
                ? MetisFieldRequired.MUSTEXIST
                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if an XchangeRate infoSet class is required.
     * @param pCategory the category
     * @return the status
     */
    private MetisFieldRequired isXchangeRateClassRequired(final MoneyWiseTransCategoryClass pCategory) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseDataSet myData = myTrans.getDataSet();
        final MoneyWiseTransAsset myAccount = myTrans.getAccount();

        return pCategory.isDividend()
                && !myAccount.getAssetCurrency().equals(myData.getReportingCurrency())
                ? MetisFieldRequired.MUSTEXIST
                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if a price infoSet class is required.
     * @param pCategory the category
     * @return the status
     */
    private static MetisFieldRequired isPriceClassRequired(final MoneyWiseTransCategoryClass pCategory) {
        /* Only allowed for stockSplit and UnitsAdjust */
        switch (pCategory) {
            case STOCKSPLIT:
            case UNITSADJUST:
                return MetisFieldRequired.CANEXIST;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a Commission infoSet class is required.
     * @param pCategory the category
     * @return the status
     */
    private static MetisFieldRequired isCommissionClassRequired(final MoneyWiseTransCategoryClass pCategory) {
        /* Don't allow yet */
        return MetisFieldRequired.NOTALLOWED;
        /* Account or Partner must be security holding
         if (!(pAccount instanceof SecurityHolding)
         && !(pPartner instanceof SecurityHolding)) {
         return MetisFieldRequired.NOTALLOWED;
         }
         switch (pCategory) {
         case TRANSFER:
         return MetisFieldRequired.CANEXIST;
         case DIVIDEND:
         return MetisDataDifference.isEqual(pAccount, pPartner)
         ? MetisFieldRequired.CANEXIST
         : MetisFieldRequired.NOTALLOWED;
         default:
         return MetisFieldRequired.NOTALLOWED;
         } */
    }

    @Override
    public void validateClass(final MoneyWiseTransInfo pInfo,
                              final PrometheusDataInfoClass pClass) {
        /* Switch on class */
        switch ((MoneyWiseTransInfoClass) pClass) {
            case QUALIFYYEARS:
                validateQualifyYears(pInfo);
                break;
            case TAXCREDIT:
                validateTaxCredit(pInfo);
                break;
            case EMPLOYEENATINS:
            case EMPLOYERNATINS:
            case DEEMEDBENEFIT:
            case WITHHELD:
                validateOptionalTaxCredit(pInfo);
                break;
            case PARTNERAMOUNT:
                validatePartnerAmount(pInfo);
                break;
            case RETURNEDCASHACCOUNT:
                validateReturnedCashAccount(pInfo);
                break;
            case RETURNEDCASH:
                validateReturnedCash(pInfo);
                break;
            case ACCOUNTDELTAUNITS:
            case PARTNERDELTAUNITS:
                validateDeltaUnits(pInfo);
                break;
            case REFERENCE:
            case COMMENTS:
                validateInfoLength(pInfo);
                break;
            case PRICE:
                validatePrice(pInfo);
                break;
            case TRANSTAG:
            case DILUTION:
            default:
                break;
        }
    }

    /**
     * Validate the qualifyingYears.
     * @param pInfo the info
     */
    private void validateQualifyYears(final MoneyWiseTransInfo pInfo) {
        final Integer myYears = pInfo.getValue(Integer.class);
        if (myYears == 0) {
            getOwner().addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.QUALIFYYEARS));
        } else if (myYears < 0) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.QUALIFYYEARS));
        }
    }

    /**
     * Validate the taxCredit.
     * @param pInfo the info
     */
    private void validateTaxCredit(final MoneyWiseTransInfo pInfo) {
        final OceanusMoney myAmount = pInfo.getValue(OceanusMoney.class);
        final Currency myCurrency = getOwner().getAccount().getCurrency();
        if (!myAmount.isPositive()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.TAXCREDIT));
        } else if (!myAmount.getCurrency().equals(myCurrency)) {
            getOwner().addError(MoneyWiseTransBase.ERROR_CURRENCY, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.TAXCREDIT));
        }
    }

    /**
     * Validate the optional taxCredits.
     * @param pInfo the info
     */
    private void validateOptionalTaxCredit(final MoneyWiseTransInfo pInfo) {
        final OceanusMoney myAmount = pInfo.getValue(OceanusMoney.class);
        final Currency myCurrency = getOwner().getAccount().getCurrency();
        if (myAmount.isZero()) {
            getOwner().addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseTransInfoSet.getFieldForClass(pInfo.getInfoClass()));
        } else if (!myAmount.isPositive()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseTransInfoSet.getFieldForClass(pInfo.getInfoClass()));
        } else if (!myAmount.getCurrency().equals(myCurrency)) {
            getOwner().addError(MoneyWiseTransBase.ERROR_CURRENCY, MoneyWiseTransInfoSet.getFieldForClass(pInfo.getInfoClass()));
        }
    }

    /**
     * Validate the partnerAmount.
     * @param pInfo the info
     */
    private void validatePartnerAmount(final MoneyWiseTransInfo pInfo) {
        final MoneyWiseTransAsset myPartner = getOwner().getPartner();
        final OceanusMoney myAmount = pInfo.getValue(OceanusMoney.class);
        if (!myAmount.isPositive()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.RETURNEDCASH));
        } else if (!myAmount.getCurrency().equals(myPartner.getCurrency())) {
            getOwner().addError(MoneyWiseTransBase.ERROR_CURRENCY, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.RETURNEDCASH));
        }
    }

    /**
     * Validate the returnedCashAccount.
     * @param pInfo the info
     */
    private void validateReturnedCashAccount(final MoneyWiseTransInfo pInfo) {
        final MoneyWiseTransAsset myThirdParty = pInfo.getTransAsset();
        final Currency myCurrency = getOwner().getAccount().getCurrency();
        if (!myCurrency.equals(myThirdParty.getCurrency())) {
            getOwner().addError(MoneyWiseTransBase.ERROR_CURRENCY, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.RETURNEDCASHACCOUNT));
        }
    }

    /**
     * Validate the returnedCash.
     * @param pInfo the info
     */
    private void validateReturnedCash(final MoneyWiseTransInfo pInfo) {
        final MoneyWiseTransAsset myThirdParty = getOwner().getReturnedCashAccount();
        final OceanusMoney myAmount = pInfo.getValue(OceanusMoney.class);
        if (myAmount.isZero()) {
            getOwner().addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.RETURNEDCASH));
        } else if (!myAmount.isPositive()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.RETURNEDCASH));
        } else if (!myAmount.getCurrency().equals(myThirdParty.getCurrency())) {
            getOwner().addError(MoneyWiseTransBase.ERROR_CURRENCY, MoneyWiseTransInfoSet.getFieldForClass(MoneyWiseTransInfoClass.RETURNEDCASH));
        }
    }

    /**
     * Validate the price.
     * @param pInfo the info
     */
    private void validatePrice(final MoneyWiseTransInfo pInfo) {
        final OceanusPrice myPrice = pInfo.getValue(OceanusPrice.class);
        final Currency myCurrency = getOwner().getAccount().getCurrency();
        if (myPrice.isZero()) {
            getOwner().addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseTransInfoSet.getFieldForClass(pInfo.getInfoClass()));
        } else if (!myPrice.isPositive()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseTransInfoSet.getFieldForClass(pInfo.getInfoClass()));
        } else if (!myPrice.getCurrency().equals(myCurrency)) {
            getOwner().addError(MoneyWiseTransBase.ERROR_CURRENCY, MoneyWiseTransInfoSet.getFieldForClass(pInfo.getInfoClass()));
        }
    }

    /**
     * Validate the deltaUnits.
     * @param pInfo the info
     */
    private void validateDeltaUnits(final MoneyWiseTransInfo pInfo) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseAssetDirection myDir = myTrans.getDirection();
        final MoneyWiseTransCategoryClass myCatClass = myTrans.getCategoryClass();
        final MoneyWiseTransInfoClass myInfoClass = pInfo.getInfoClass();
        final MetisFieldRequired isRequired = myInfoClass == MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS
                ? isAccountUnitsPositive(myDir, myCatClass)
                : isPartnerUnitsPositive(myDir, myCatClass);
        final OceanusUnits myUnits = pInfo.getValue(OceanusUnits.class);
        if (myUnits.isZero()) {
            getOwner().addError(PrometheusDataItem.ERROR_ZERO, MoneyWiseTransInfoSet.getFieldForClass(myInfoClass));
        } else if (myUnits.isPositive() && isRequired.notAllowed()) {
            getOwner().addError(PrometheusDataItem.ERROR_POSITIVE, MoneyWiseTransInfoSet.getFieldForClass(myInfoClass));
        } else if (!myUnits.isPositive() && isRequired.mustExist()) {
            getOwner().addError(PrometheusDataItem.ERROR_NEGATIVE, MoneyWiseTransInfoSet.getFieldForClass(myInfoClass));
        }
    }

    /**
     * Validate the info length.
     * @param pInfo the info
     */
    private void validateInfoLength(final MoneyWiseTransInfo pInfo) {
        final String myInfo = pInfo.getValue(String.class);
        final MoneyWiseTransInfoClass myClass = pInfo.getInfoClass();
        if (myInfo.length() > myClass.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, MoneyWiseTransInfoSet.getFieldForClass(myClass));
        }
    }

    /**
     * Determine if AccountDeltaUnits can/mustBe/mustNotBe positive.
     * @param pDir the direction
     * @param pClass the category class
     * @return the status
     */
    public static MetisFieldRequired isAccountUnitsPositive(final MoneyWiseAssetDirection pDir,
                                                            final MoneyWiseTransCategoryClass pClass) {
        switch (pClass) {
            case TRANSFER:
                return pDir.isFrom()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            case UNITSADJUST:
            case STOCKSPLIT:
                return MetisFieldRequired.CANEXIST;
            case INHERITED:
            case DIVIDEND:
            case STOCKRIGHTSISSUE:
                return MetisFieldRequired.MUSTEXIST;
            case STOCKDEMERGER:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if PartnerDeltaUnits can/mustBe/mustNotBe positive.
     * @param pDir the direction
     * @param pClass the category class
     * @return the status
     */
    public static MetisFieldRequired isPartnerUnitsPositive(final MoneyWiseAssetDirection pDir,
                                                            final MoneyWiseTransCategoryClass pClass) {
        switch (pClass) {
            case TRANSFER:
                return pDir.isTo()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            case STOCKDEMERGER:
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case STOCKRIGHTSISSUE:
                return MetisFieldRequired.MUSTEXIST;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    @Override
    protected void setDefault(final PrometheusDataInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch ((MoneyWiseTransInfoClass) pClass) {
            case ACCOUNTDELTAUNITS:
            case PARTNERDELTAUNITS:
                getInfoSet().setValue(pClass, OceanusUnits.getWholeUnits(1));
                break;
            case DILUTION:
                getInfoSet().setValue(pClass, OceanusRatio.ONE);
                break;
            case QUALIFYYEARS:
                getInfoSet().setValue(pClass, 1);
                break;
            case TAXCREDIT:
                getInfoSet().setValue(pClass, OceanusMoney.getWholeUnits(0));
                break;
            case RETURNEDCASHACCOUNT:
                getInfoSet().setValue(pClass, getDefaultReturnedCashAccount());
                break;
            default:
                break;
        }
    }

    /**
     * Obtain default deposit for ReturnedCashAccount.
     * @return the default returnedCashAccount
     */
    private MoneyWiseDeposit getDefaultReturnedCashAccount() {
        /* loop through the deposits */
        final MoneyWiseDepositList myDeposits
                = getEditSet().getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class);
        final Iterator<MoneyWiseDeposit> myIterator = myDeposits.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseDeposit myDeposit = myIterator.next();

            /* Use if not deleted or closed */
            if (!myDeposit.isDeleted() && !myDeposit.isClosed()) {
                return myDeposit;
            }
        }

        /* Return no deposit */
        return null;
    }
}
