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

import net.sourceforge.joceanus.jmetis.lethe.data.MetisDifference;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFieldValue;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisField;
import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisFieldRequired;
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
import net.sourceforge.joceanus.jtethys.date.TethysDate;
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
     * Bad Credit Date Error Text.
     */
    private static final String ERROR_BADDATE = MoneyWiseDataResource.TRANSACTION_ERROR_BADCREDITDATE.getValue();

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
        TransactionInfoClass myClass = getClassForField(pField);
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
        Object myValue;

        switch (pInfoClass) {
            case THIRDPARTY:
                /* Access deposit of object */
                myValue = getDeposit(pInfoClass);
                break;
            case TRANSTAG:
                /* Access InfoSetList */
                myValue = getInfoLinkSet(pInfoClass);
                break;
            default:
                /* Access value of object */
                myValue = getField(pInfoClass);
                break;
        }

        /* Return the value */
        return (myValue != null)
                                 ? myValue
                                 : MetisFieldValue.SKIP;
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
        TransactionInfo myValue = getInfo(pInfoClass);

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
        TransactionInfoClass myClass = getClassForField(pField);
        return myClass == null
                               ? MetisFieldRequired.NOTALLOWED
                               : isClassRequired(myClass);
    }

    @Override
    public MetisFieldRequired isClassRequired(final TransactionInfoClass pClass) {
        /* Access details about the Transaction */
        Transaction myTransaction = getOwner();
        TransactionAsset myAccount = myTransaction.getAccount();
        TransactionAsset myPartner = myTransaction.getPartner();
        AssetDirection myDir = myTransaction.getDirection();
        TransactionCategory myCategory = myTransaction.getCategory();
        MoneyWiseTaxCredit myYear = myTransaction.getTaxYear();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }
        TransactionCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Switch on class */
        switch (pClass) {
            /* Reference and comments are always available */
            case REFERENCE:
            case COMMENTS:
            case TRANSTAG:
                return MetisFieldRequired.CANEXIST;

            /* NatInsurance and benefit can only occur on salary */
            case NATINSURANCE:
            case DEEMEDBENEFIT:
                return (myClass == TransactionCategoryClass.TAXEDINCOME)
                                                                         ? MetisFieldRequired.CANEXIST
                                                                         : MetisFieldRequired.NOTALLOWED;

            /* Credit amount and date are only available for transfer */
            case CREDITDATE:
                return (myClass == TransactionCategoryClass.TRANSFER)
                                                                      ? MetisFieldRequired.CANEXIST
                                                                      : MetisFieldRequired.NOTALLOWED;

            /* Charity donation is only available for interest */
            case CHARITYDONATION:
                return (myClass == TransactionCategoryClass.INTEREST)
                                                                      ? MetisFieldRequired.CANEXIST
                                                                      : MetisFieldRequired.NOTALLOWED;

            /* Handle Tax Credit */
            case TAXCREDIT:
                return isTaxCreditClassRequired(myAccount, myClass, myYear);

            /* Handle debit units separately */
            case DEBITUNITS:
                return isDebitUnitsClassRequired(myDir.isFrom()
                                                                ? myPartner
                                                                : myAccount, myClass);

            /* Handle CreditUnits separately */
            case CREDITUNITS:
                return isCreditUnitsClassRequired(myDir.isFrom()
                                                                 ? myAccount
                                                                 : myPartner, myClass);

            /* Handle Dilution separately */
            case DILUTION:
                return isDilutionClassRequired(myClass);

            /* Qualify Years is needed only for Taxable Gain */
            case QUALIFYYEARS:
                return ((myClass == TransactionCategoryClass.TRANSFER)
                        && (myAccount instanceof SecurityHolding)
                        && (((SecurityHolding) myAccount).getSecurity().isSecurityClass(SecurityTypeClass.LIFEBOND)))
                                                                                                                      ? MetisFieldRequired.MUSTEXIST
                                                                                                                      : MetisFieldRequired.NOTALLOWED;

            /* Handle ThirdParty separately */
            case THIRDPARTY:
                return isThirdPartyClassRequired(myClass);
            case THIRDPARTYAMOUNT:
                return isThirdPartyAmountRequired(myTransaction);

            case PARTNERAMOUNT:
                return isPartnerAmountClassRequired(myClass, myAccount, myPartner);

            case PENSION:
            case XCHANGERATE:
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
            case BENEFITINCOME:
                return MetisFieldRequired.MUSTEXIST;
            case GRANTINCOME:
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
                return pDebit.isTaxFree()
                       || !pYear.isTaxCreditRequired()
                                                       ? MetisFieldRequired.NOTALLOWED
                                                       : MetisFieldRequired.MUSTEXIST;
            case TRANSFER:
                return (pDebit instanceof SecurityHolding)
                       && (((SecurityHolding) pDebit).getSecurity().isSecurityClass(SecurityTypeClass.LIFEBOND))
                                                                                                                 ? MetisFieldRequired.MUSTEXIST
                                                                                                                 : MetisFieldRequired.NOTALLOWED;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a DebitUnits infoSet class is required.
     * @param pDebit the debit account
     * @param pClass the category class
     * @return the status
     */
    protected static MetisFieldRequired isDebitUnitsClassRequired(final TransactionAsset pDebit,
                                                                  final TransactionCategoryClass pClass) {
        /* Debit Asset must be security holding */
        if (!(pDebit instanceof SecurityHolding)) {
            return MetisFieldRequired.NOTALLOWED;
        }
        switch (pClass) {
            case TRANSFER:
            case UNITSADJUST:
            case STOCKDEMERGER:
                return MetisFieldRequired.CANEXIST;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a CreditUnits infoSet class is required.
     * @param pCredit the credit account
     * @param pClass the category class
     * @return the status
     */
    protected static MetisFieldRequired isCreditUnitsClassRequired(final TransactionAsset pCredit,
                                                                   final TransactionCategoryClass pClass) {
        /* Credit Asset must be security holding */
        if (!(pCredit instanceof SecurityHolding)) {
            return MetisFieldRequired.NOTALLOWED;
        }
        switch (pClass) {
            case STOCKRIGHTSISSUE:
            case STOCKDEMERGER:
            case SECURITYREPLACE:
            case STOCKTAKEOVER:
            case STOCKSPLIT:
                return MetisFieldRequired.MUSTEXIST;
            case TRANSFER:
            case INHERITED:
            case UNITSADJUST:
            case DIVIDEND:
                return MetisFieldRequired.CANEXIST;
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a Dilution infoSet class is required.
     * @param pClass the category class
     * @return the status
     */
    protected static MetisFieldRequired isDilutionClassRequired(final TransactionCategoryClass pClass) {
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
     * Determine if a ThirdParty infoSet class is required.
     * @param pClass the category class
     * @return the status
     */
    protected static MetisFieldRequired isThirdPartyClassRequired(final TransactionCategoryClass pClass) {
        /* ThirdParty is possible only for StockTakeOver */
        return pClass == TransactionCategoryClass.STOCKTAKEOVER
                                                                ? MetisFieldRequired.CANEXIST
                                                                : MetisFieldRequired.NOTALLOWED;
    }

    /**
     * Determine if a ThirdParty amount is required.
     * @param pTransaction the transaction
     * @return the status
     */
    protected static MetisFieldRequired isThirdPartyAmountRequired(final Transaction pTransaction) {
        /* ThirdParty Amount is possible only if ThirdParty exists */
        return pTransaction.getThirdParty() != null
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
    protected static MetisFieldRequired isPartnerAmountClassRequired(final TransactionCategoryClass pCategory,
                                                                     final TransactionAsset pAccount,
                                                                     final TransactionAsset pPartner) {
        /* If the transaction requires null amount, then partner amount must also be null */
        if (pCategory.needsNullAmount()) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* If Partner currency is null or the same as Account then Partner amount is not allowed */
        AssetCurrency myCurrency = pAccount.getAssetCurrency();
        AssetCurrency myPartnerCurrency = pPartner.getAssetCurrency();
        if ((myCurrency == null) || (myPartnerCurrency == null)) {
            return MetisFieldRequired.NOTALLOWED;
        }
        return MetisDifference.isEqual(myCurrency, myPartnerCurrency)
                                                                      ? MetisFieldRequired.NOTALLOWED
                                                                      : MetisFieldRequired.MUSTEXIST;
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Access details about the Transaction */
        Transaction myTransaction = getOwner();
        TransactionAsset myAccount = myTransaction.getAccount();
        TransactionAsset myPartner = myTransaction.getPartner();
        Currency myCurrency = myAccount.getCurrency();

        /* Loop through the classes */
        for (TransactionInfoClass myClass : TransactionInfoClass.values()) {
            /* Access info for class */
            boolean isExisting = isExisting(myClass);
            TransactionInfo myInfo = myClass.isLinkSet()
                                                         ? null
                                                         : getInfo(myClass);

            /* Determine requirements for class */
            MetisFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == MetisFieldRequired.MUSTEXIST) {
                    myTransaction.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == MetisFieldRequired.NOTALLOWED) {
                myTransaction.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* Switch on class */
            switch (myClass) {
                case CREDITDATE:
                    /* Check value */
                    TethysDate myDate = myInfo.getValue(TethysDate.class);
                    if (myDate.compareTo(myTransaction.getDate()) <= 0) {
                        myTransaction.addError(ERROR_BADDATE, getFieldForClass(myClass));
                    }
                    break;
                case QUALIFYYEARS:
                    /* Check value */
                    Integer myYears = myInfo.getValue(Integer.class);
                    if (myYears == 0) {
                        myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (myYears < 0) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case TAXCREDIT:
                    /* Check value */
                    TethysMoney myAmount = myInfo.getValue(TethysMoney.class);
                    if (!myAmount.isPositive()) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    } else if (!myAmount.getCurrency().equals(myCurrency)) {
                        myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(myClass));
                    }
                    break;
                case NATINSURANCE:
                case DEEMEDBENEFIT:
                case CHARITYDONATION:
                    /* Check value */
                    myAmount = myInfo.getValue(TethysMoney.class);
                    if (myAmount.isZero()) {
                        myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (!myAmount.isPositive()) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    } else if (!myAmount.getCurrency().equals(myCurrency)) {
                        myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(myClass));
                    }
                    break;
                case PARTNERAMOUNT:
                    /* Check value */
                    myAmount = myInfo.getValue(TethysMoney.class);
                    if (!myAmount.isPositive()) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    } else if (!myAmount.getCurrency().equals(myPartner.getCurrency())) {
                        myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(myClass));
                    }
                    break;
                case THIRDPARTY:
                    Deposit myThirdParty = myInfo.getDeposit();
                    if (!myCurrency.equals(myThirdParty.getCurrency())) {
                        myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(myClass));
                    }
                    break;
                case THIRDPARTYAMOUNT:
                    /* Check value */
                    myThirdParty = myTransaction.getThirdParty();
                    myAmount = myInfo.getValue(TethysMoney.class);
                    if (myAmount.isZero()) {
                        myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (!myAmount.isPositive()) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    } else if (!myAmount.getCurrency().equals(myThirdParty.getCurrency())) {
                        myTransaction.addError(TransactionBase.ERROR_CURRENCY, getFieldForClass(myClass));
                    }
                    break;
                case CREDITUNITS:
                case DEBITUNITS:
                    /* Check value */
                    TethysUnits myUnits = myInfo.getValue(TethysUnits.class);
                    if (myUnits.isZero()) {
                        myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (!myUnits.isPositive()) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case DILUTION:
                    /* Check range */
                    TethysDilution myDilution = myInfo.getValue(TethysDilution.class);
                    if (myDilution.outOfRange()) {
                        myTransaction.addError(DataItem.ERROR_RANGE, getFieldForClass(myClass));
                    }
                    break;
                case REFERENCE:
                case COMMENTS:
                    /* Check length */
                    String myValue = myInfo.getValue(String.class);
                    if (myValue.length() > myClass.getMaximumLength()) {
                        myTransaction.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                case PENSION:
                case TRANSTAG:
                case XCHANGERATE:
                default:
                    break;
            }
        }
    }

    @Override
    protected void setDefaultValue(final DataListSet<MoneyWiseDataType> pUpdateSet,
                                   final TransactionInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch (pClass) {
            case CREDITUNITS:
            case DEBITUNITS:
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
            case THIRDPARTY:
                setValue(pClass, getOwner().getDefaultThirdParty());
                break;
            default:
                break;
        }
    }
}
