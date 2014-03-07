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
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.data;

import java.util.Map;
import java.util.ResourceBundle;

import net.sourceforge.joceanus.jmetis.viewer.JDataFieldValue;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataField;
import net.sourceforge.joceanus.jmetis.viewer.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.TransactionInfo.TransactionInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType.EventInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.data.statics.SecurityTypeClass;
import net.sourceforge.joceanus.jprometheus.data.DataInfoSet;
import net.sourceforge.joceanus.jprometheus.data.DataItem;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.decimal.JDilution;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

/**
 * EventInfoSet class.
 * @author Tony Washer
 */
public class TransactionInfoSet
        extends DataInfoSet<TransactionInfo, Transaction, EventInfoType, EventInfoClass, MoneyWiseDataType> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(TransactionInfoSet.class.getName());

    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(NLS_BUNDLE.getString("DataName"), DataInfoSet.FIELD_DEFS);

    /**
     * FieldSet map.
     */
    private static final Map<JDataField, EventInfoClass> FIELDSET_MAP = JDataFields.buildFieldMap(FIELD_DEFS, EventInfoClass.class);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<EventInfoClass, JDataField> REVERSE_FIELDMAP = JDataFields.reverseFieldMap(FIELDSET_MAP, EventInfoClass.class);

    /**
     * Bad Credit Date Error Text.
     */
    private static final String ERROR_BADDATE = NLS_BUNDLE.getString("ErrorBadDate");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle InfoSet fields */
        EventInfoClass myClass = getClassForField(pField);
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
    private Object getInfoSetValue(final EventInfoClass pInfoClass) {
        Object myValue;

        switch (pInfoClass) {
            case THIRDPARTY:
                /* Access deposit of object */
                myValue = getDeposit(pInfoClass);
                break;
            case PORTFOLIO:
                /* Access portfolio of object */
                myValue = getPortfolio(pInfoClass);
                break;
            case EVENTTAG:
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
                                : JDataFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    public static EventInfoClass getClassForField(final JDataField pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     * @param pClass the class
     * @return the field
     */
    public static JDataField getFieldForClass(final EventInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
    }

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList source InfoSet
     */
    protected TransactionInfoSet(final Transaction pOwner,
                                 final EventInfoTypeList pTypeList,
                                 final TransactionInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
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
    public Deposit getDeposit(final EventInfoClass pInfoClass) {
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
     * Obtain the portfolio for the infoClass.
     * @param pInfoClass the Info Class
     * @return the portfolio
     */
    public Portfolio getPortfolio(final EventInfoClass pInfoClass) {
        /* Access existing entry */
        TransactionInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the portfolio */
        return myValue.getPortfolio();
    }

    /**
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public JDataFieldRequired isFieldRequired(final JDataField pField) {
        EventInfoClass myClass = getClassForField(pField);
        return myClass == null
                              ? JDataFieldRequired.NOTALLOWED
                              : isClassRequired(myClass);
    }

    /**
     * Determine if an infoSet class is required.
     * @param pClass the infoSet class
     * @return the status
     */
    protected JDataFieldRequired isClassRequired(final EventInfoClass pClass) {
        /* Access details about the Transaction */
        Transaction myTransaction = getOwner();
        AssetBase<?> myDebit = myTransaction.getDebit();
        AssetBase<?> myCredit = myTransaction.getCredit();
        EventCategory myCategory = myTransaction.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return JDataFieldRequired.NOTALLOWED;
        }
        EventCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Switch on class */
        switch (pClass) {
        /* Reference and comments are always available */
            case REFERENCE:
            case COMMENTS:
            case EVENTTAG:
                return JDataFieldRequired.CANEXIST;

                /* NatInsurance and benefit can only occur on salary */
            case NATINSURANCE:
            case DEEMEDBENEFIT:
                return (myClass == EventCategoryClass.TAXEDINCOME)
                                                                  ? JDataFieldRequired.CANEXIST
                                                                  : JDataFieldRequired.NOTALLOWED;

                /* Credit amount and date are only available for transfer */
            case CREDITDATE:
                return (myClass == EventCategoryClass.TRANSFER)
                                                               ? JDataFieldRequired.CANEXIST
                                                               : JDataFieldRequired.NOTALLOWED;

                /* Charity donation is only available for interest */
            case CHARITYDONATION:
                return (myClass == EventCategoryClass.INTEREST)
                                                               ? JDataFieldRequired.CANEXIST
                                                               : JDataFieldRequired.NOTALLOWED;

                /* Handle Tax Credit */
            case TAXCREDIT:
                return isTaxCreditClassRequired(myDebit, myClass);

                /* Handle debit units separately */
            case DEBITUNITS:
                return isDebitUnitsClassRequired(myDebit, myClass);

                /* Handle CreditUnits separately */
            case CREDITUNITS:
                return isCreditUnitsClassRequired(myCredit, myClass);

                /* Handle Dilution separately */
            case DILUTION:
                return isDilutionClassRequired(myClass);

                /* Qualify Years is needed only for Taxable Gain */
            case QUALIFYYEARS:
                return ((myClass == EventCategoryClass.TRANSFER)
                        && (myDebit instanceof Security)
                        && (((Security) myDebit).isSecurityClass(SecurityTypeClass.LIFEBOND)))
                                                                                              ? JDataFieldRequired.MUSTEXIST
                                                                                              : JDataFieldRequired.NOTALLOWED;

                /* Handle ThirdParty separately */
            case THIRDPARTY:
                return isThirdPartyClassRequired(myTransaction, myClass);

            case PORTFOLIO:
                return ((myDebit instanceof Security) || (myCredit instanceof Security))
                                                                                        ? JDataFieldRequired.MUSTEXIST
                                                                                        : JDataFieldRequired.NOTALLOWED;

            case PENSION:
            case CREDITAMOUNT:
            default:
                return JDataFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a TaxCredit infoSet class is required.
     * @param pDebit the debit account
     * @param pClass the category class
     * @return the status
     */
    private JDataFieldRequired isTaxCreditClassRequired(final AssetBase<?> pDebit,
                                                        final EventCategoryClass pClass) {
        /* Switch on class */
        switch (pClass) {
            case TAXEDINCOME:
            case BENEFITINCOME:
                return JDataFieldRequired.MUSTEXIST;
            case GRANTINCOME:
                return JDataFieldRequired.CANEXIST;
            case INTEREST:
                if (!(pDebit instanceof Deposit)) {
                    return JDataFieldRequired.NOTALLOWED;
                }
                Deposit myDeposit = (Deposit) pDebit;
                return ((myDeposit.isTaxFree()) || (myDeposit.isGross()))
                                                                         ? JDataFieldRequired.NOTALLOWED
                                                                         : JDataFieldRequired.MUSTEXIST;
            case DIVIDEND:
                if (!(pDebit instanceof Security)) {
                    return JDataFieldRequired.MUSTEXIST;
                }

                /* Check portfolio tax status */
                Portfolio myPortfolio = getPortfolio(EventInfoClass.PORTFOLIO);
                return ((myPortfolio != null) && !myPortfolio.isTaxFree())
                                                                          ? JDataFieldRequired.MUSTEXIST
                                                                          : JDataFieldRequired.NOTALLOWED;
            case TRANSFER:
                return (pDebit instanceof Security)
                       && (((Security) pDebit).isSecurityClass(SecurityTypeClass.LIFEBOND))
                                                                                           ? JDataFieldRequired.MUSTEXIST
                                                                                           : JDataFieldRequired.NOTALLOWED;
            default:
                return JDataFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a DebitUnits infoSet class is required.
     * @param pDebit the debit account
     * @param pClass the category class
     * @return the status
     */
    private JDataFieldRequired isDebitUnitsClassRequired(final AssetBase<?> pDebit,
                                                         final EventCategoryClass pClass) {
        /* Debit Asset must be security */
        if (!(pDebit instanceof Security)) {
            return JDataFieldRequired.NOTALLOWED;
        }
        switch (pClass) {
            case TRANSFER:
            case STOCKADJUST:
            case STOCKDEMERGER:
                return JDataFieldRequired.CANEXIST;
            default:
                return JDataFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a CreditUnits infoSet class is required.
     * @param pCredit the credit account
     * @param pClass the category class
     * @return the status
     */
    private JDataFieldRequired isCreditUnitsClassRequired(final AssetBase<?> pCredit,
                                                          final EventCategoryClass pClass) {
        /* Credit Asset must be security */
        if (!(pCredit instanceof Security)) {
            return JDataFieldRequired.NOTALLOWED;
        }
        switch (pClass) {
            case STOCKRIGHTSTAKEN:
            case STOCKDEMERGER:
            case STOCKTAKEOVER:
            case STOCKSPLIT:
                return JDataFieldRequired.MUSTEXIST;
            case TRANSFER:
            case INHERITED:
            case STOCKADJUST:
            case DIVIDEND:
                return JDataFieldRequired.CANEXIST;
            default:
                return JDataFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a Dilution infoSet class is required.
     * @param pClass the category class
     * @return the status
     */
    private JDataFieldRequired isDilutionClassRequired(final EventCategoryClass pClass) {
        /* Dilution is only required for stock split/rights/deMerger */
        switch (pClass) {
            case STOCKSPLIT:
            case STOCKRIGHTSWAIVED:
            case STOCKRIGHTSTAKEN:
            case STOCKDEMERGER:
                return JDataFieldRequired.MUSTEXIST;
            default:
                return JDataFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Determine if a ThirdParty infoSet class is required.
     * @param pTransaction the transaction
     * @param pClass the category class
     * @return the status
     */
    private JDataFieldRequired isThirdPartyClassRequired(final Transaction pTransaction,
                                                         final EventCategoryClass pClass) {
        /* ThirdParty is possible only for StockTakeOver */
        switch (pClass) {
            case STOCKTAKEOVER:
                return pTransaction.getAmount().isNonZero()
                                                           ? JDataFieldRequired.MUSTEXIST
                                                           : JDataFieldRequired.NOTALLOWED;
            default:
                return JDataFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Access details about the Transaction */
        Transaction myTransaction = getOwner();

        /* Loop through the classes */
        for (EventInfoClass myClass : EventInfoClass.values()) {
            /* Access info for class */
            boolean isExisting = isExisting(myClass);
            TransactionInfo myInfo = myClass.isLinkSet()
                                                        ? null
                                                        : getInfo(myClass);

            /* Determine requirements for class */
            JDataFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == JDataFieldRequired.MUSTEXIST) {
                    myTransaction.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NOTALLOWED) {
                myTransaction.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* Switch on class */
            switch (myClass) {
                case CREDITDATE:
                    /* Check value */
                    JDateDay myDate = myInfo.getValue(JDateDay.class);
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
                    JMoney myAmount = myInfo.getValue(JMoney.class);
                    if (!myAmount.isPositive()) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case NATINSURANCE:
                case DEEMEDBENEFIT:
                case CHARITYDONATION:
                    /* Check value */
                    myAmount = myInfo.getValue(JMoney.class);
                    if (myAmount.isZero()) {
                        myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (!myAmount.isPositive()) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case CREDITUNITS:
                case DEBITUNITS:
                    /* Check value */
                    JUnits myUnits = myInfo.getValue(JUnits.class);
                    if (myUnits.isZero()) {
                        myTransaction.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (!myUnits.isPositive()) {
                        myTransaction.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case DILUTION:
                    /* Check range */
                    JDilution myDilution = myInfo.getValue(JDilution.class);
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
                case THIRDPARTY:
                case PORTFOLIO:
                case CREDITAMOUNT:
                case PENSION:
                default:
                    break;
            }
        }
    }
}
