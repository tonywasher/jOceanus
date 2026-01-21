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
package net.sourceforge.joceanus.moneywise.data.validate;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import net.sourceforge.joceanus.metis.data.MetisDataDifference;
import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseAssetDirection;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseDeposit.MoneyWiseDepositList;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio.MoneyWisePortfolioList;
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
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.validate.PrometheusValidateInfoSet;

import java.util.Currency;
import java.util.Iterator;
import java.util.Objects;

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
     *
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
     *
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
     *
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
                return myAccount instanceof MoneyWiseSecurityHolding myHolding
                        && myHolding.getSecurity().isSecurityClass(MoneyWiseSecurityClass.LIFEBOND)
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a Withheld amount is required.
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param pCategory the category
     * @return the status
     */
    private MetisFieldRequired isQualifyingYearsClassRequired(final MoneyWiseTransCategoryClass pCategory) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseTransAsset myAccount = myTrans.getAccount();

        return pCategory == MoneyWiseTransCategoryClass.TRANSFER
                && myAccount instanceof MoneyWiseSecurityHolding myHolding
                && myHolding.getSecurity().isSecurityClass(MoneyWiseSecurityClass.LIFEBOND)
                ? MetisFieldRequired.MUSTEXIST
                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if an XchangeRate infoSet class is required.
     *
     * @param pCategory the category
     * @return the status
     */
    private MetisFieldRequired isXchangeRateClassRequired(final MoneyWiseTransCategoryClass pCategory) {
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseDataSet myData = myTrans.getDataSet();
        final MoneyWiseTransAsset myAccount = myTrans.getAccount();

        if (newValidation) {
            return MetisFieldRequired.CANEXIST;
        }
        return pCategory.isDividend()
                && !myAccount.getAssetCurrency().equals(myData.getReportingCurrency())
                ? MetisFieldRequired.MUSTEXIST
                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if a price infoSet class is required.
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
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
     *
     * @param pDir   the direction
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
     *
     * @param pDir   the direction
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
                getInfoSet().setValue(pClass, getDefaultAccountUnits());
                break;
            case PARTNERDELTAUNITS:
                getInfoSet().setValue(pClass, getDefaultPartnerUnits());
                break;
            case DILUTION:
                getInfoSet().setValue(pClass, OceanusRatio.ONE);
                break;
            case QUALIFYYEARS:
                getInfoSet().setValue(pClass, 1);
                break;
            case TAXCREDIT:
                getInfoSet().setValue(pClass, getDefaultTaxCredit());
                break;
            case PARTNERAMOUNT:
                getInfoSet().setValue(pClass, getDefaultPartnerAmount());
                break;
            case RETURNEDCASHACCOUNT:
                getInfoSet().setValue(pClass, getDefaultReturnedCashAccount());
                break;
            case RETURNEDCASH:
                getInfoSet().setValue(pClass, getDefaultReturnedCash());
                break;
            default:
                break;
        }
    }

    /**
     * Obtain default accountUnits.
     *
     * @return the default deltaUnits
     */
    private OceanusUnits getDefaultAccountUnits() {
        /* Determine whether the units must be +ve or -ve */
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseAssetDirection myDir = myTrans.getDirection();
        final MoneyWiseTransCategoryClass myCategoryClass = myTrans.getCategoryClass();
        final MetisFieldRequired isRequired = isAccountUnitsPositive(myDir, myCategoryClass);
        return isRequired.notAllowed()
                ? OceanusUnits.getWholeUnits(-1)
                : OceanusUnits.getWholeUnits(1);
    }

    /**
     * Obtain default partnerUnits.
     *
     * @return the default deltaUnits
     */
    private OceanusUnits getDefaultPartnerUnits() {
        /* Determine whether the units must be +ve or -ve */
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseAssetDirection myDir = myTrans.getDirection();
        final MoneyWiseTransCategoryClass myCategoryClass = myTrans.getCategoryClass();
        final MetisFieldRequired isRequired = isPartnerUnitsPositive(myDir, myCategoryClass);
        return isRequired.notAllowed()
                ? OceanusUnits.getWholeUnits(-1)
                : OceanusUnits.getWholeUnits(1);
    }

    /**
     * Obtain default taxCredit.
     *
     * @return the default taxCredit
     */
    private OceanusMoney getDefaultTaxCredit() {
        /* Access the account */
        final MoneyWiseTransAsset myAsset = getOwner().getAccount();
        final MoneyWiseCurrency myCurrency = myAsset.getAssetCurrency();

        /* Return zero cash in the appropriate currency */
        return new OceanusMoney(myCurrency.getCurrency());
    }

    /**
     * Obtain default partnerAmount.
     *
     * @return the default partnerAmount
     */
    private OceanusMoney getDefaultPartnerAmount() {
        /* Access the partner */
        final MoneyWiseTransAsset myAsset = getOwner().getPartner();
        final MoneyWiseCurrency myCurrency = myAsset.getAssetCurrency();

        /* Return zero cash in the appropriate currency */
        return new OceanusMoney(myCurrency.getCurrency());
    }

    /**
     * Obtain default account for ReturnedCashAccount.
     *
     * @return the default returnedCashAccount
     */
    private MoneyWiseTransAsset getDefaultReturnedCashAccount() {
        /* loop through the deposits */
        final MoneyWiseDepositList myDeposits
                = getEditSet().getDataList(MoneyWiseBasicDataType.DEPOSIT, MoneyWiseDepositList.class);
        final Iterator<MoneyWiseDeposit> myDepIterator = myDeposits.iterator();
        while (myDepIterator.hasNext()) {
            final MoneyWiseDeposit myDeposit = myDepIterator.next();

            /* Use if not deleted or closed */
            if (!myDeposit.isDeleted() && Boolean.FALSE.equals(myDeposit.isClosed())) {
                return myDeposit;
            }
        }

        /* loop through the portfolios */
        final MoneyWisePortfolioList myPortfolios
                = getEditSet().getDataList(MoneyWiseBasicDataType.PORTFOLIO, MoneyWisePortfolioList.class);
        final Iterator<MoneyWisePortfolio> myPortIterator = myPortfolios.iterator();
        while (myPortIterator.hasNext()) {
            final MoneyWisePortfolio myPortfolio = myPortIterator.next();

            /* Use if not deleted or closed */
            if (!myPortfolio.isDeleted() && Boolean.FALSE.equals(myPortfolio.isClosed())) {
                return myPortfolio;
            }
        }

        /* Return no account */
        return null;
    }

    /**
     * Obtain default returnedCash.
     *
     * @return the default returnedCash
     */
    private OceanusMoney getDefaultReturnedCash() {
        /* Access the returned cash account */
        final MoneyWiseTransAsset myAsset = getOwner().getReturnedCashAccount();
        final MoneyWiseCurrency myCurrency = Objects.requireNonNull(myAsset).getAssetCurrency();

        /* Return zero cash in the appropriate currency */
        return new OceanusMoney(myCurrency.getCurrency());
    }

    @Override
    protected void autoCorrect(final PrometheusDataInfoClass pClass) throws OceanusException {
        /* Switch on class */
        final MoneyWiseTransInfoClass myClass = (MoneyWiseTransInfoClass) pClass;
        switch (myClass) {
            case ACCOUNTDELTAUNITS:
                autoCorrectAccountDeltaUnits();
                break;
            case PARTNERDELTAUNITS:
                autoCorrectPartnerDeltaUnits();
                break;
            case TAXCREDIT:
            case EMPLOYERNATINS:
            case EMPLOYEENATINS:
            case DEEMEDBENEFIT:
            case WITHHELD:
                autoCorrectTaxCredit(myClass);
                break;
            case PARTNERAMOUNT:
                autoCorrectPartnerAmount();
                break;
            case RETURNEDCASH:
                autoCorrectReturnedCash();
                break;
            case PRICE:
                autoCorrectPrice();
                break;
            default:
                break;
        }
    }

    /**
     * AutoCorrect accountDeltaUnits.
     *
     * @throws OceanusException on error
     */
    private void autoCorrectAccountDeltaUnits() throws OceanusException {
        /* Determine whether the units must be +ve or -ve */
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseAssetDirection myDir = myTrans.getDirection();
        final MoneyWiseTransCategoryClass myCategoryClass = myTrans.getCategoryClass();
        final MetisFieldRequired isRequired = isAccountUnitsPositive(myDir, myCategoryClass);
        OceanusUnits myUnits = Objects.requireNonNull(myTrans.getAccountDeltaUnits());

        /* If the units are negative and must be positive or are positive and must be negative */
        if ((isRequired.mustExist() && !myUnits.isPositive())
                || (isRequired.notAllowed() && myUnits.isPositive())) {
            /* Reverse the sign */
            myUnits = new OceanusUnits(myUnits);
            myUnits.negate();
            getInfoSet().setValue(MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS, myUnits);
        }
    }


    /**
     * AutoCorrect partnerDeltaUnits.
     *
     * @throws OceanusException on error
     */
    private void autoCorrectPartnerDeltaUnits() throws OceanusException {
        /* Determine whether the units must be +ve or -ve */
        final MoneyWiseTransaction myTrans = getOwner();
        final MoneyWiseAssetDirection myDir = myTrans.getDirection();
        final MoneyWiseTransCategoryClass myCategoryClass = myTrans.getCategoryClass();
        final MetisFieldRequired isRequired = isPartnerUnitsPositive(myDir, myCategoryClass);
        OceanusUnits myUnits = Objects.requireNonNull(myTrans.getPartnerDeltaUnits());

        /* If the units are negative and must be positive or are positive and must be negative */
        if ((isRequired.mustExist() && !myUnits.isPositive())
                || (isRequired.notAllowed() && myUnits.isPositive())) {
            /* Reverse the sign */
            myUnits = new OceanusUnits(myUnits);
            myUnits.negate();
            getInfoSet().setValue(MoneyWiseTransInfoClass.PARTNERDELTAUNITS, myUnits);
        }
    }

    /**
     * AutoCorrect taxCredit.
     *
     * @param pClass the InfoClass
     * @throws OceanusException on error
     */
    private void autoCorrectTaxCredit(final MoneyWiseTransInfoClass pClass) throws OceanusException {
        /* Obtain the existing value */
        OceanusMoney myValue = getInfoSet().getValue(pClass, OceanusMoney.class);
        final MoneyWiseCurrency myAssetCurrency = getOwner().getAccount().getAssetCurrency();
        final Currency myCurrency = myAssetCurrency.getCurrency();

        /* If the value is not the correct currency */
        if (!myValue.getCurrency().equals(myCurrency)) {
            myValue = myValue.changeCurrency(myCurrency);
            getInfoSet().setValue(pClass, myValue);
        }
    }

    /**
     * AutoCorrect partnerAmount.
     *
     * @throws OceanusException on error
     */
    private void autoCorrectPartnerAmount() throws OceanusException {
        /* Obtain the existing value */
        final MoneyWiseTransaction myTrans = getOwner();
        OceanusMoney myValue = Objects.requireNonNull(myTrans.getPartnerAmount());
        final MoneyWiseCurrency myAssetCurrency = myTrans.getPartner().getAssetCurrency();
        final Currency myCurrency = myAssetCurrency.getCurrency();

        /* If the value is not the correct currency */
        if (!myCurrency.equals(myValue.getCurrency())) {
            myValue = myValue.changeCurrency(myCurrency);
            getInfoSet().setValue(MoneyWiseTransInfoClass.PARTNERAMOUNT, myValue);
        }
    }

    /**
     * AutoCorrect returnedCash.
     *
     * @throws OceanusException on error
     */
    private void autoCorrectReturnedCash() throws OceanusException {
        /* Obtain the existing value */
        final MoneyWiseTransaction myTrans = getOwner();
        OceanusMoney myValue = Objects.requireNonNull(myTrans.getReturnedCash());
        final MoneyWiseTransAsset myAsset = Objects.requireNonNull(myTrans.getReturnedCashAccount());
        final MoneyWiseCurrency myAssetCurrency = myAsset.getAssetCurrency();
        final Currency myCurrency = myAssetCurrency.getCurrency();

        /* If the value is not the correct currency */
        if (!myCurrency.equals(myValue.getCurrency())) {
            myValue = myValue.changeCurrency(myCurrency);
            getInfoSet().setValue(MoneyWiseTransInfoClass.RETURNEDCASH, myValue);
        }
    }

    /**
     * AutoCorrect price.
     *
     * @throws OceanusException on error
     */
    private void autoCorrectPrice() throws OceanusException {
        /* Obtain the existing value */
        final MoneyWiseTransaction myTrans = getOwner();
        OceanusPrice myPrice = Objects.requireNonNull(myTrans.getPrice());
        final MoneyWiseCurrency myAssetCurrency = getOwner().getAccount().getAssetCurrency();
        final Currency myCurrency = myAssetCurrency.getCurrency();

        /* If the value is not the correct currency */
        if (!myPrice.getCurrency().equals(myCurrency)) {
            myPrice = myPrice.changeCurrency(myCurrency);
            getInfoSet().setValue(MoneyWiseTransInfoClass.PRICE, myPrice);
        }
    }
}
