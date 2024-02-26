/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.atlas.data.basic;

import java.util.Currency;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransInfo.MoneyWiseTransInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseSecurityClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTransInfoType.MoneyWiseTransInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataInfoSet;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataList.PrometheusDataListSet;
import net.sourceforge.joceanus.jprometheus.atlas.views.PrometheusEditSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * TransactionInfoSet class.
 * @author Tony Washer
 */
public class MoneyWiseTransInfoSet
        extends PrometheusDataInfoSet<MoneyWiseTransInfo> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseTransInfoSet> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseTransInfoSet.class);

    /**
     * FieldSet map.
     */
    private static final Map<MetisDataFieldId, MoneyWiseTransInfoClass> FIELDSET_MAP = FIELD_DEFS.buildFieldMap(MoneyWiseTransInfoClass.class, MoneyWiseTransInfoSet::getFieldValue);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<MoneyWiseTransInfoClass, MetisDataFieldId> REVERSE_FIELDMAP = MetisFieldSet.reverseFieldMap(FIELDSET_MAP, MoneyWiseTransInfoClass.class);

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList source InfoSet
     */
    protected MoneyWiseTransInfoSet(final MoneyWiseTransaction pOwner,
                                    final MoneyWiseTransInfoTypeList pTypeList,
                                    final MoneyWiseTransInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MoneyWiseTransaction getOwner() {
        return (MoneyWiseTransaction) super.getOwner();
    }

    /**
     * Obtain fieldValue for infoSet.
     * @param pFieldId the fieldId
     * @return the value
     */
    public Object getFieldValue(final MetisDataFieldId pFieldId) {
        /* Handle InfoSet fields */
        final MoneyWiseTransInfoClass myClass = getClassForField(pFieldId);
        if (myClass != null) {
            return getInfoSetValue(myClass);
        }

        /* Pass onwards */
        return null;
    }

    /**
     * Get an infoSet value.
     * @param pInfoClass the class of info to get
     * @return the value to set
     */
    private Object getInfoSetValue(final MoneyWiseTransInfoClass pInfoClass) {
        final Object myValue;

        switch (pInfoClass) {
            case RETURNEDCASHACCOUNT:
                /* Access deposit of object */
                myValue = getTransAsset(pInfoClass);
                break;
            case TRANSTAG:
                /* Access InfoSetList */
                myValue = getListValue(pInfoClass);
                break;
            default:
                /* Access value of object */
                myValue = getField(pInfoClass);
                break;
        }

        /* Return the value */
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    public static MoneyWiseTransInfoClass getClassForField(final MetisDataFieldId pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static MetisDataFieldId getFieldForClass(final MoneyWiseTransInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final MoneyWiseTransInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
    }

    /**
     * Resolve editSetLinks.
     *
     * @param pEditSet the editSet
     * @throws OceanusException on error
     */
    void resolveEditSetLinks(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Loop through the items */
        final Iterator<MoneyWiseTransInfo> myIterator = iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransInfo myInfo = myIterator.next();
            myInfo.resolveEditSetLinks(pEditSet);
        }
    }

    /**
     * Obtain the deposit for the infoClass.
     * @param pInfoClass the Info Class
     * @return the deposit
     */
    public MoneyWiseTransAsset getTransAsset(final MoneyWiseTransInfoClass pInfoClass) {
        /* Access existing entry */
        final MoneyWiseTransInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the asset */
        return myValue.getTransAsset();
    }

    /**
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public MetisFieldRequired isFieldRequired(final MetisDataFieldId pField) {
        final MoneyWiseTransInfoClass myClass = getClassForField(pField);
        return myClass == null
                ? MetisFieldRequired.NOTALLOWED
                : isClassRequired(myClass);
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Access details about the Transaction */
        final MoneyWiseTransaction myTransaction = getOwner();
        final MoneyWiseDataSet myData = myTransaction.getDataSet();
        final MoneyWiseTransAsset myAccount = myTransaction.getAccount();
        final MoneyWiseTransAsset myPartner = myTransaction.getPartner();
        final MoneyWiseAssetDirection myDir = myTransaction.getDirection();
        final MoneyWiseTransCategory myCategory = myTransaction.getCategory();
        final MoneyWiseTaxCredit myYear = myTransaction.getTaxYear();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        final MoneyWiseTransCategoryClass myClass = myCategory.getCategoryTypeClass();

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
                return isTaxCreditClassRequired(myAccount, myClass, myYear);

            /* Handle AccountUnits */
            case ACCOUNTDELTAUNITS:
                return isAccountUnitsDeltaRequired(myAccount, myPartner, myDir, myClass);

            /* Handle PartnerUnits */
            case PARTNERDELTAUNITS:
                return isPartnerUnitsDeltaRequired(myPartner, myDir, myClass);

            /* Handle Dilution separately */
            case DILUTION:
                return isDilutionClassRequired(myClass);

            /* Qualify Years is needed only for Taxable Gain */
            case QUALIFYYEARS:
                return myClass == MoneyWiseTransCategoryClass.TRANSFER
                        && myAccount instanceof MoneyWiseSecurityHolding
                        && ((MoneyWiseSecurityHolding) myAccount).getSecurity().isSecurityClass(MoneyWiseSecurityClass.LIFEBOND)
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Handle ThirdParty separately */
            case RETURNEDCASHACCOUNT:
                return isReturnedCashAccountRequired(myClass);
            case RETURNEDCASH:
                return isReturnedCashRequired(myTransaction);

            case PARTNERAMOUNT:
                return isPartnerAmountClassRequired(myClass, myAccount, myPartner);

            case XCHANGERATE:
                return isXchangeRateClassRequired(myClass, myAccount, myData.getReportingCurrency());

            case PRICE:
                return isPriceClassRequired(myClass, myAccount, myPartner);

            case COMMISSION:
                return isCommissionClassRequired(myClass, myAccount, myPartner);

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
     * @param pDebit the debit account
     * @param pClass the category class
     * @param pYear the TaxYear
     * @return the status
     */
    private static MetisFieldRequired isTaxCreditClassRequired(final MoneyWiseTransAsset pDebit,
                                                               final MoneyWiseTransCategoryClass pClass,
                                                               final MoneyWiseTaxCredit pYear) {
        /* Switch on class */
        switch (pClass) {
            case TAXEDINCOME:
                return MetisFieldRequired.MUSTEXIST;
            case LOANINTERESTCHARGED:
                return MetisFieldRequired.CANEXIST;
            case LOYALTYBONUS:
                return pDebit.isTaxFree()
                        || pDebit.isGross()
                        ? MetisFieldRequired.NOTALLOWED
                        : MetisFieldRequired.MUSTEXIST;
            case INTEREST:
                return pDebit.isTaxFree()
                        || pDebit.isGross()
                        || !pYear.isTaxCreditRequired()
                        ? MetisFieldRequired.NOTALLOWED
                        : MetisFieldRequired.MUSTEXIST;
            case DIVIDEND:
                return !pDebit.isTaxFree()
                        && (pYear.isTaxCreditRequired() || pDebit.isForeign())
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            case TRANSFER:
                return pDebit instanceof MoneyWiseSecurityHolding
                        && ((MoneyWiseSecurityHolding) pDebit).getSecurity().isSecurityClass(MoneyWiseSecurityClass.LIFEBOND)
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
     * @param pAccount the account
     * @param pPartner the partner
     * @param pDir the direction
     * @param pClass the category class
     * @return the status
     */
    private static MetisFieldRequired isAccountUnitsDeltaRequired(final MoneyWiseTransAsset pAccount,
                                                                  final MoneyWiseTransAsset pPartner,
                                                                  final MoneyWiseAssetDirection pDir,
                                                                  final MoneyWiseTransCategoryClass pClass) {
        /* Account must be security holding */
        if (!(pAccount instanceof MoneyWiseSecurityHolding)) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Account cannot be autoUnits */
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pAccount;
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
                return pAccount.equals(pPartner)
                        ? MetisFieldRequired.CANEXIST
                        : MetisFieldRequired.NOTALLOWED;
            case STOCKRIGHTSISSUE:
                return pDir.isFrom()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if an PartnerDeltaUnits infoSet class is required.
     * @param pPartner the partner
     * @param pDir the direction
     * @param pClass the category class
     * @return the status
     */
    private static MetisFieldRequired isPartnerUnitsDeltaRequired(final MoneyWiseTransAsset pPartner,
                                                                  final MoneyWiseAssetDirection pDir,
                                                                  final MoneyWiseTransCategoryClass pClass) {
        /* Partner must be security holding */
        if (!(pPartner instanceof MoneyWiseSecurityHolding)) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Partner cannot be autoUnits */
        final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pPartner;
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
                return pDir.isTo()
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
     * @param pAccount the account
     * @param pPartner the partner
     * @return the status
     */
    private static MetisFieldRequired isPartnerAmountClassRequired(final MoneyWiseTransCategoryClass pCategory,
                                                                   final MoneyWiseTransAsset pAccount,
                                                                   final MoneyWiseTransAsset pPartner) {
        /* If the transaction requires null amount, then partner amount must also be null */
        if (pCategory.needsNullAmount()) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* If Partner currency is null or the same as Account then Partner amount is not allowed */
        final MoneyWiseCurrency myCurrency = pAccount.getAssetCurrency();
        final MoneyWiseCurrency myPartnerCurrency = pPartner.getAssetCurrency();
        if (myCurrency == null || myPartnerCurrency == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        return MetisDataDifference.isEqual(myCurrency, myPartnerCurrency)
                ? MetisFieldRequired.NOTALLOWED
                : MetisFieldRequired.MUSTEXIST;
    }

    /**
     * Determine if an XchangeRate infoSet class is required.
     * @param pCategory the category
     * @param pAccount the account
     * @param pCurrency the reporting currency
     * @return the status
     */
    private static MetisFieldRequired isXchangeRateClassRequired(final MoneyWiseTransCategoryClass pCategory,
                                                                 final MoneyWiseTransAsset pAccount,
                                                                 final MoneyWiseCurrency pCurrency) {
        return pCategory.isDividend()
                && !pAccount.getAssetCurrency().equals(pCurrency)
                ? MetisFieldRequired.MUSTEXIST
                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if a Commission infoSet class is required.
     * @param pCategory the category
     * @param pAccount the account
     * @param pPartner the partner
     * @return the status
     */
    private static MetisFieldRequired isPriceClassRequired(final MoneyWiseTransCategoryClass pCategory,
                                                           final MoneyWiseTransAsset pAccount,
                                                           final MoneyWiseTransAsset pPartner) {
        /* Don't allow yet */
        return MetisFieldRequired.NOTALLOWED;
        /* Account or Partner must be security holding
         if (!(pAccount instanceof SecurityHolding)
         && !(pPartner instanceof SecurityHolding)) {
         return MetisFieldRequired.NOTALLOWED;
         }
         switch (pCategory) {
         case STOCKSPLIT:
         case STOCKTAKEOVER:
         case STOCKDEMERGER:
         case SECURITYREPLACE:
         case OPTIONSEXERCISE:
         case TRANSFER:
         return MetisFieldRequired.MUSTEXIST;
         case DIVIDEND:
         return MetisDataDifference.isEqual(pAccount, pPartner)
         ? MetisFieldRequired.MUSTEXIST
         : MetisFieldRequired.NOTALLOWED;
         default:
         return MetisFieldRequired.NOTALLOWED;
         } */
    }

    /**
     * Determine if a Commission infoSet class is required.
     * @param pCategory the category
     * @param pAccount the account
     * @param pPartner the partner
     * @return the status
     */
    private static MetisFieldRequired isCommissionClassRequired(final MoneyWiseTransCategoryClass pCategory,
                                                                final MoneyWiseTransAsset pAccount,
                                                                final MoneyWiseTransAsset pPartner) {
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

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Loop through the classes */
        for (final MoneyWiseTransInfoClass myClass : MoneyWiseTransInfoClass.values()) {
            /* validate the class */
            validateClass(myClass);
        }
    }

    /**
     * Validate the class.
     * @param pClass the infoClass
     */
    private void validateClass(final MoneyWiseTransInfoClass pClass) {
        /* Access details about the Transaction */
        final MoneyWiseTransaction myTransaction = getOwner();
        final MoneyWiseTransAsset myAccount = myTransaction.getAccount();
        final MoneyWiseTransAsset myPartner = myTransaction.getPartner();
        final MoneyWiseAssetDirection myDir = myTransaction.getDirection();
        final MoneyWiseTransCategoryClass myCatClass = myTransaction.getCategoryClass();
        final Currency myCurrency = myAccount.getCurrency();

        /* Access info for class */
        final boolean isExisting = isExisting(pClass);
        final MoneyWiseTransInfo myInfo = pClass.isLinkSet()
                ? null
                : getInfo(pClass);

        /* Determine requirements for class */
        final MetisFieldRequired myState = isClassRequired(pClass);

        /* If the field is missing */
        if (!isExisting) {
            /* Handle required field missing */
            if (myState == MetisFieldRequired.MUSTEXIST) {
                myTransaction.addError(PrometheusDataItem.ERROR_MISSING, getFieldForClass(pClass));
            }
            return;
        }

        /* If field is not allowed */
        if (myState == MetisFieldRequired.NOTALLOWED) {
            myTransaction.addError(PrometheusDataItem.ERROR_EXIST, getFieldForClass(pClass));
            return;
        }
        if (myInfo == null) {
            return;
        }

        /* Switch on class */
        switch (pClass) {
            case QUALIFYYEARS:
                /* Check value */
                final Integer myYears = myInfo.getValue(Integer.class);
                if (myYears == 0) {
                    myTransaction.addError(PrometheusDataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (myYears < 0) {
                    myTransaction.addError(PrometheusDataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                }
                break;
            case TAXCREDIT:
                /* Check value */
                TethysMoney myAmount = myInfo.getValue(TethysMoney.class);
                if (!myAmount.isPositive()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                } else if (!myAmount.getCurrency().equals(myCurrency)) {
                    myTransaction.addError(MoneyWiseTransBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case EMPLOYEENATINS:
            case DEEMEDBENEFIT:
            case WITHHELD:
                /* Check value */
                myAmount = myInfo.getValue(TethysMoney.class);
                if (myAmount.isZero()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (!myAmount.isPositive()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                } else if (!myAmount.getCurrency().equals(myCurrency)) {
                    myTransaction.addError(MoneyWiseTransBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case PARTNERAMOUNT:
                /* Check value */
                myAmount = myInfo.getValue(TethysMoney.class);
                if (!myAmount.isPositive()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                } else if (!myAmount.getCurrency().equals(myPartner.getCurrency())) {
                    myTransaction.addError(MoneyWiseTransBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case RETURNEDCASHACCOUNT:
                MoneyWiseTransAsset myThirdParty = myInfo.getTransAsset();
                if (!myCurrency.equals(myThirdParty.getCurrency())) {
                    myTransaction.addError(MoneyWiseTransBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case RETURNEDCASH:
                /* Check value */
                myThirdParty = myTransaction.getReturnedCashAccount();
                myAmount = myInfo.getValue(TethysMoney.class);
                if (myAmount.isZero()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (!myAmount.isPositive()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                } else if (!myAmount.getCurrency().equals(myThirdParty.getCurrency())) {
                    myTransaction.addError(MoneyWiseTransBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case ACCOUNTDELTAUNITS:
            case PARTNERDELTAUNITS:
                final MetisFieldRequired isRequired = pClass == MoneyWiseTransInfoClass.ACCOUNTDELTAUNITS
                        ? isAccountUnitsPositive(myDir, myCatClass)
                        : isPartnerUnitsPositive(myDir, myCatClass);
                final TethysUnits myUnits = myInfo.getValue(TethysUnits.class);
                if (myUnits.isZero()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (myUnits.isPositive() && isRequired.notAllowed()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_POSITIVE, getFieldForClass(pClass));
                } else if (!myUnits.isPositive() && isRequired.mustExist()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                }
                break;
            case REFERENCE:
            case COMMENTS:
                /* Check length */
                final String myValue = myInfo.getValue(String.class);
                if (myValue.length() > pClass.getMaximumLength()) {
                    myTransaction.addError(PrometheusDataItem.ERROR_LENGTH, getFieldForClass(pClass));
                }
                break;
            case TRANSTAG:
            case DILUTION:
            default:
                break;
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
    protected void setDefaultValue(final PrometheusDataListSet pUpdateSet,
                                   final PrometheusDataInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch ((MoneyWiseTransInfoClass) pClass) {
            case ACCOUNTDELTAUNITS:
            case PARTNERDELTAUNITS:
                setValue(pClass, TethysUnits.getWholeUnits(1));
                break;
            case DILUTION:
                setValue(pClass, TethysRatio.ONE);
                break;
            case QUALIFYYEARS:
                setValue(pClass, Integer.valueOf(1));
                break;
            case TAXCREDIT:
                setValue(pClass, TethysMoney.getWholeUnits(0));
                break;
            case RETURNEDCASHACCOUNT:
                setValue(pClass, getOwner().getDefaultReturnedCashAccount());
                break;
            default:
                break;
        }
    }
}
