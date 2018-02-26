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
package net.sourceforge.joceanus.jmoneywise.lethe.data;

import java.util.Currency;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataDifference;
import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.atlas.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.AssetPair.AssetDirection;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseTax.MoneyWiseTaxCredit;
import net.sourceforge.joceanus.jmoneywise.lethe.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionCategoryClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoClass;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.TransactionInfoType.TransactionInfoTypeList;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataItem;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataList.DataListSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysDilution;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;

/**
 * TransactionInfoSet class.
 * @author Tony Washer
 */
public class TransactionInfoSet
        extends DataInfoSet<TransactionInfo, Transaction, TransactionInfoType, TransactionInfoClass, MoneyWiseDataType> {
    /**
     * Report fields.
     */
    private static final MetisFields FIELD_DEFS = new MetisFields(MoneyWiseDataResource.TRANSACTION_INFOSET.getValue(), DataInfoSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<MetisField, TransactionInfoClass> FIELDSET_MAP = MetisFields.buildFieldMap(FIELD_DEFS, TransactionInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<TransactionInfoClass, MetisField> REVERSE_FIELDMAP = MetisFields.reverseFieldMap(FIELDSET_MAP, TransactionInfoClass.class);

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList source InfoSet
     */
    protected TransactionInfoSet(final Transaction pOwner,
                                 final TransactionInfoTypeList pTypeList,
                                 final TransactionInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    @Override
    public MetisFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final MetisField pField) {
        /* Handle InfoSet fields */
        final TransactionInfoClass myClass = getClassForField(pField);
        if (myClass != null) {
            return getInfoSetValue(myClass);
        }

        /* Pass onwards */
        return super.getFieldValue(pField);
    }

    /**
     * Get an infoSet value.
     * @param pInfoClass the class of info to get
     * @return the value to set
     */
    private Object getInfoSetValue(final TransactionInfoClass pInfoClass) {
        final Object myValue;

        switch (pInfoClass) {
            case RETURNEDCASHACCOUNT:
                /* Access deposit of object */
                myValue = getDeposit(pInfoClass);
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
    public static TransactionInfoClass getClassForField(final MetisField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static MetisField getFieldForClass(final TransactionInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final TransactionInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
    }

    /**
     * Obtain the deposit for the infoClass.
     * @param pInfoClass the Info Class
     * @return the deposit
     */
    public Deposit getDeposit(final TransactionInfoClass pInfoClass) {
        /* Access existing entry */
        final TransactionInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the deposit */
        return myValue.getDeposit();
    }

    /**
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public MetisFieldRequired isFieldRequired(final MetisField pField) {
        final TransactionInfoClass myClass = getClassForField(pField);
        return myClass == null
                               ? MetisFieldRequired.NOTALLOWED
                               : isClassRequired(myClass);
    }

    @Override
    public MetisFieldRequired isClassRequired(final TransactionInfoClass pClass) {
        /* Access details about the Transaction */
        final Transaction myTransaction = getOwner();
        final MoneyWiseData myData = myTransaction.getDataSet();
        final TransactionAsset myAccount = myTransaction.getAccount();
        final TransactionAsset myPartner = myTransaction.getPartner();
        final AssetDirection myDir = myTransaction.getDirection();
        final TransactionCategory myCategory = myTransaction.getCategory();
        final MoneyWiseTaxCredit myYear = myTransaction.getTaxYear();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        final TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Switch on class */
        switch (pClass) {
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
                return myClass == TransactionCategoryClass.TAXEDINCOME
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
                return myClass == TransactionCategoryClass.TRANSFER
                       && myAccount instanceof SecurityHolding
                       && ((SecurityHolding) myAccount).getSecurity().isSecurityClass(SecurityTypeClass.LIFEBOND)
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
                return isXchangeRateClassRequired(myClass, myAccount, myData.getDefaultCurrency());

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
    public boolean isMetaData(final TransactionInfoClass pClass) {
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
    private static MetisFieldRequired isTaxCreditClassRequired(final TransactionAsset pDebit,
                                                               final TransactionCategoryClass pClass,
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
                return pDebit instanceof SecurityHolding
                       && ((SecurityHolding) pDebit).getSecurity().isSecurityClass(SecurityTypeClass.LIFEBOND)
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
    private static MetisFieldRequired isWithheldAmountRequired(final TransactionCategoryClass pClass) {
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
    private static MetisFieldRequired isAccountUnitsDeltaRequired(final TransactionAsset pAccount,
                                                                  final TransactionAsset pPartner,
                                                                  final AssetDirection pDir,
                                                                  final TransactionCategoryClass pClass) {
        /* Account must be security holding */
        if (!(pAccount instanceof SecurityHolding)) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Account cannot be autoUnits */
        final SecurityHolding myHolding = (SecurityHolding) pAccount;
        if (myHolding.getSecurity().getSecurityTypeClass().isAutoUnits()) {
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
    private static MetisFieldRequired isPartnerUnitsDeltaRequired(final TransactionAsset pPartner,
                                                                  final AssetDirection pDir,
                                                                  final TransactionCategoryClass pClass) {
        /* Partner must be security holding */
        if (!(pPartner instanceof SecurityHolding)) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Partner cannot be autoUnits */
        final SecurityHolding myHolding = (SecurityHolding) pPartner;
        if (myHolding.getSecurity().getSecurityTypeClass().isAutoUnits()) {
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
    private static MetisFieldRequired isDilutionClassRequired(final TransactionCategoryClass pClass) {
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
    private static MetisFieldRequired isReturnedCashAccountRequired(final TransactionCategoryClass pClass) {
        /* Returned Cash is possible only for StockTakeOver */
        return pClass == TransactionCategoryClass.STOCKTAKEOVER
                                                                ? MetisFieldRequired.CANEXIST
                                                                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if a ReturnedCash value is required.
     * @param pTransaction the transaction
     * @return the status
     */
    private static MetisFieldRequired isReturnedCashRequired(final Transaction pTransaction) {
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
    private static MetisFieldRequired isPartnerAmountClassRequired(final TransactionCategoryClass pCategory,
                                                                   final TransactionAsset pAccount,
                                                                   final TransactionAsset pPartner) {
        /* If the transaction requires null amount, then partner amount must also be null */
        if (pCategory.needsNullAmount()) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* If Partner currency is null or the same as Account then Partner amount is not allowed */
        final AssetCurrency myCurrency = pAccount.getAssetCurrency();
        final AssetCurrency myPartnerCurrency = pPartner.getAssetCurrency();
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
    private static MetisFieldRequired isXchangeRateClassRequired(final TransactionCategoryClass pCategory,
                                                                 final TransactionAsset pAccount,
                                                                 final AssetCurrency pCurrency) {
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
    private static MetisFieldRequired isPriceClassRequired(final TransactionCategoryClass pCategory,
                                                           final TransactionAsset pAccount,
                                                           final TransactionAsset pPartner) {
        /* Don't allow yet */
        return MetisFieldRequired.NOTALLOWED;
        /* Account or Partner must be security holding */
        // if (!(pAccount instanceof SecurityHolding)
        // && !(pPartner instanceof SecurityHolding)) {
        // return MetisFieldRequired.NOTALLOWED;
        // }
        // switch (pCategory) {
        // case STOCKSPLIT:
        // case STOCKTAKEOVER:
        // case STOCKDEMERGER:
        // case SECURITYREPLACE:
        // case OPTIONSEXERCISE:
        // case TRANSFER:
        // return MetisFieldRequired.MUSTEXIST;
        // case DIVIDEND:
        // return MetisDataDifference.isEqual(pAccount, pPartner)
        // ? MetisFieldRequired.MUSTEXIST
        // : MetisFieldRequired.NOTALLOWED;
        // default:
        // return MetisFieldRequired.NOTALLOWED;
        // }
    }

    /**
     * Determine if a Commission infoSet class is required.
     * @param pCategory the category
     * @param pAccount the account
     * @param pPartner the partner
     * @return the status
     */
    private static MetisFieldRequired isCommissionClassRequired(final TransactionCategoryClass pCategory,
                                                                final TransactionAsset pAccount,
                                                                final TransactionAsset pPartner) {
        /* Don't allow yet */
        return MetisFieldRequired.NOTALLOWED;
        /* Account or Partner must be security holding */
        // if (!(pAccount instanceof SecurityHolding)
        // && !(pPartner instanceof SecurityHolding)) {
        // return MetisFieldRequired.NOTALLOWED;
        // }
        // switch (pCategory) {
        // case TRANSFER:
        // return MetisFieldRequired.CANEXIST;
        // case DIVIDEND:
        // return MetisDataDifference.isEqual(pAccount, pPartner)
        // ? MetisFieldRequired.CANEXIST
        // : MetisFieldRequired.NOTALLOWED;
        // default:
        // return MetisFieldRequired.NOTALLOWED;
        // }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Loop through the classes */
        for (final TransactionInfoClass myClass : TransactionInfoClass.values()) {
            /* validate the class */
            validateClass(myClass);
        }
    }

    /**
     * Validate the class.
     * @param pClass the infoClass
     */
    private void validateClass(final TransactionInfoClass pClass) {
        /* Access details about the Transaction */
        final Transaction myTransaction = getOwner();
        final TransactionAsset myAccount = myTransaction.getAccount();
        final TransactionAsset myPartner = myTransaction.getPartner();
        final AssetDirection myDir = myTransaction.getDirection();
        final TransactionCategoryClass myCatClass = myTransaction.getCategoryClass();
        final Currency myCurrency = myAccount.getCurrency();

        /* Access info for class */
        final boolean isExisting = isExisting(pClass);
        final TransactionInfo myInfo = pClass.isLinkSet()
                                                          ? null
                                                          : getInfo(pClass);

        /* Determine requirements for class */
        final MetisFieldRequired myState = isClassRequired(pClass);

        /* If the field is missing */
        if (!isExisting) {
            /* Handle required field missing */
            if (myState == MetisFieldRequired.MUSTEXIST) {
                myTransaction.addError(DataItem.ERROR_MISSING, getFieldForClass(pClass));
            }
            return;
        }

        /* If field is not allowed */
        if (myState == MetisFieldRequired.NOTALLOWED) {
            myTransaction.addError(DataItem.ERROR_EXIST, getFieldForClass(pClass));
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
                    myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (myYears < 0) {
                    myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                }
                break;
            case TAXCREDIT:
                /* Check value */
                TethysMoney myAmount = myInfo.getValue(TethysMoney.class);
                if (!myAmount.isPositive()) {
                    myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                } else if (!myAmount.getCurrency().equals(myCurrency)) {
                    myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case EMPLOYEENATINS:
            case DEEMEDBENEFIT:
            case WITHHELD:
                /* Check value */
                myAmount = myInfo.getValue(TethysMoney.class);
                if (myAmount.isZero()) {
                    myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (!myAmount.isPositive()) {
                    myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                } else if (!myAmount.getCurrency().equals(myCurrency)) {
                    myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case PARTNERAMOUNT:
                /* Check value */
                myAmount = myInfo.getValue(TethysMoney.class);
                if (!myAmount.isPositive()) {
                    myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                } else if (!myAmount.getCurrency().equals(myPartner.getCurrency())) {
                    myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case RETURNEDCASHACCOUNT:
                TransactionAsset myThirdParty = myInfo.getDeposit();
                if (!myCurrency.equals(myThirdParty.getCurrency())) {
                    myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case RETURNEDCASH:
                /* Check value */
                myThirdParty = myTransaction.getReturnedCashAccount();
                myAmount = myInfo.getValue(TethysMoney.class);
                if (myAmount.isZero()) {
                    myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (!myAmount.isPositive()) {
                    myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                } else if (!myAmount.getCurrency().equals(myThirdParty.getCurrency())) {
                    myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case ACCOUNTDELTAUNITS:
            case PARTNERDELTAUNITS:
                final MetisFieldRequired isRequired = pClass == TransactionInfoClass.ACCOUNTDELTAUNITS
                                                                                                       ? isAccountUnitsPositive(myDir, myCatClass)
                                                                                                       : isPartnerUnitsPositive(myDir, myCatClass);
                final TethysUnits myUnits = myInfo.getValue(TethysUnits.class);
                if (myUnits.isZero()) {
                    myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(pClass));
                } else if (myUnits.isPositive() && isRequired.notAllowed()) {
                    myTransaction.addError(DataItem.ERROR_POSITIVE, getFieldForClass(pClass));
                } else if (!myUnits.isPositive() && isRequired.mustExist()) {
                    myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(pClass));
                }
                break;
            case DILUTION:
                /* Check range */
                final TethysDilution myDilution = myInfo.getValue(TethysDilution.class);
                if (myDilution.outOfRange()) {
                    myTransaction.addError(DataItem.ERROR_RANGE, getFieldForClass(pClass));
                }
                break;
            case REFERENCE:
            case COMMENTS:
                /* Check length */
                final String myValue = myInfo.getValue(String.class);
                if (myValue.length() > pClass.getMaximumLength()) {
                    myTransaction.addError(DataItem.ERROR_LENGTH, getFieldForClass(pClass));
                }
                break;
            case TRANSTAG:
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
    public static MetisFieldRequired isAccountUnitsPositive(final AssetDirection pDir,
                                                            final TransactionCategoryClass pClass) {
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
    public static MetisFieldRequired isPartnerUnitsPositive(final AssetDirection pDir,
                                                            final TransactionCategoryClass pClass) {
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
    protected void setDefaultValue(final DataListSet<MoneyWiseDataType> pUpdateSet,
                                   final TransactionInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch (pClass) {
            case ACCOUNTDELTAUNITS:
            case PARTNERDELTAUNITS:
                setValue(pClass, TethysUnits.getWholeUnits(1));
                break;
            case DILUTION:
                setValue(pClass, TethysDilution.MAX_DILUTION);
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
