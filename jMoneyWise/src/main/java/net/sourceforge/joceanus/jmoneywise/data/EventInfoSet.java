/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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

import net.sourceforge.joceanus.jdatamanager.JDataFieldValue;
import net.sourceforge.joceanus.jdatamanager.JDataFields;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataField;
import net.sourceforge.joceanus.jdatamanager.JDataFields.JDataFieldRequired;
import net.sourceforge.joceanus.jdatamodels.data.DataInfoSet;
import net.sourceforge.joceanus.jdatamodels.data.DataItem;
import net.sourceforge.joceanus.jdateday.JDateDay;
import net.sourceforge.joceanus.jdecimal.JDilution;
import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JUnits;
import net.sourceforge.joceanus.jmoneywise.data.EventInfo.EventInfoList;
import net.sourceforge.joceanus.jmoneywise.data.statics.AccountCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventCategoryClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoClass;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType;
import net.sourceforge.joceanus.jmoneywise.data.statics.EventInfoType.EventInfoTypeList;

/**
 * EventInfoSet class.
 * @author Tony Washer
 */
public class EventInfoSet
        extends DataInfoSet<EventInfo, Event, EventInfoType, EventInfoClass> {
    /**
     * Resource Bundle.
     */
    private static final ResourceBundle NLS_BUNDLE = ResourceBundle.getBundle(EventInfoSet.class.getName());

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

    /**
     * Invalid Account Error Text.
     */
    private static final String ERROR_BADACCOUNT = NLS_BUNDLE.getString("ErrorBadAccount");

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
            case ThirdParty:
                /* Access account of object */
                myValue = getAccount(pInfoClass);
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
    protected EventInfoSet(final Event pOwner,
                           final EventInfoTypeList pTypeList,
                           final EventInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final EventInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
    }

    /**
     * Obtain the account for the infoClass.
     * @param pInfoClass the Info Class
     * @return the account
     */
    public Account getAccount(final EventInfoClass pInfoClass) {
        /* Access existing entry */
        EventInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the account */
        return myValue.getAccount();
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
        /* Access details about the Account */
        Event myEvent = getOwner();
        Account myDebit = myEvent.getDebit();
        Account myCredit = myEvent.getCredit();
        EventCategory myCategory = myEvent.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return JDataFieldRequired.NOTALLOWED;
        }
        EventCategoryClass myClass = myCategory.getCategoryTypeClass();

        /* Switch on class */
        switch (pClass) {
        /* Reference and comments are always available */
            case Reference:
            case Comments:
                return JDataFieldRequired.CANEXIST;

                /* NatInsurance and benefit can only occur on salary */
            case NatInsurance:
            case DeemedBenefit:
                return (myClass == EventCategoryClass.TaxedIncome)
                        ? JDataFieldRequired.CANEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Credit amount and date are only available for transfer */
            case CreditDate:
                return (myClass == EventCategoryClass.Transfer)
                        ? JDataFieldRequired.CANEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Charity donation is only available for interest */
            case CharityDonation:
                return (myClass == EventCategoryClass.Interest)
                        ? JDataFieldRequired.CANEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Handle Tax Credit */
            case TaxCredit:
                switch (myClass) {
                    case TaxedIncome:
                    case BenefitIncome:
                        return JDataFieldRequired.MUSTEXIST;
                    case GrantIncome:
                        return JDataFieldRequired.CANEXIST;
                    case Interest:
                        return ((myDebit.isTaxFree()) || (myDebit.isGrossInterest()))
                                ? JDataFieldRequired.NOTALLOWED
                                : JDataFieldRequired.MUSTEXIST;
                    case Dividend:
                        return (myDebit.isTaxFree())
                                ? JDataFieldRequired.NOTALLOWED
                                : JDataFieldRequired.MUSTEXIST;
                    case Transfer:
                        return myDebit.isCategoryClass(AccountCategoryClass.LifeBond)
                                ? JDataFieldRequired.MUSTEXIST
                                : JDataFieldRequired.NOTALLOWED;
                    default:
                        return JDataFieldRequired.NOTALLOWED;
                }

                /* Handle debit units */
            case DebitUnits:
                if (!myDebit.hasUnits()) {
                    return JDataFieldRequired.NOTALLOWED;
                }
                switch (myClass) {
                    case Transfer:
                    case StockAdjust:
                    case StockDeMerger:
                        return JDataFieldRequired.CANEXIST;
                    default:
                        return JDataFieldRequired.NOTALLOWED;
                }

            case CreditUnits:
                if (!myCredit.hasUnits()) {
                    return JDataFieldRequired.NOTALLOWED;
                }
                switch (myClass) {
                    case StockRightsTaken:
                    case StockDeMerger:
                    case StockTakeOver:
                    case StockSplit:
                        return JDataFieldRequired.MUSTEXIST;
                    case Transfer:
                    case Inherited:
                    case StockAdjust:
                    case Dividend:
                        return JDataFieldRequired.CANEXIST;
                    default:
                        return JDataFieldRequired.NOTALLOWED;
                }

                /* Dilution is only required for stock split/rights/deMerger */
            case Dilution:
                switch (myClass) {
                    case StockSplit:
                    case StockRightsWaived:
                    case StockRightsTaken:
                    case StockDeMerger:
                        return JDataFieldRequired.MUSTEXIST;
                    default:
                        return JDataFieldRequired.NOTALLOWED;
                }

                /* Qualify Years is needed only for Taxable Gain */
            case QualifyYears:
                return ((myClass == EventCategoryClass.Transfer) && (myDebit.isCategoryClass(AccountCategoryClass.LifeBond)))
                        ? JDataFieldRequired.MUSTEXIST
                        : JDataFieldRequired.NOTALLOWED;

                /* Qualify Years is possible only for StockTakeOver */
            case ThirdParty:
                switch (myClass) {
                    case StockTakeOver:
                        return myEvent.getAmount().isNonZero()
                                ? JDataFieldRequired.MUSTEXIST
                                : JDataFieldRequired.NOTALLOWED;
                    default:
                        return JDataFieldRequired.NOTALLOWED;
                }

            default:
            case Pension:
            case CreditAmount:
                return JDataFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Access details about the Event */
        Event myEvent = getOwner();

        /* Loop through the classes */
        for (EventInfoClass myClass : EventInfoClass.values()) {
            /* Access info for class */
            EventInfo myInfo = getInfo(myClass);
            boolean isExisting = (myInfo != null)
                                 && !myInfo.isDeleted();

            /* Determine requirements for class */
            JDataFieldRequired myState = isClassRequired(myClass);

            /* If the field is missing */
            if (!isExisting) {
                /* Handle required field missing */
                if (myState == JDataFieldRequired.MUSTEXIST) {
                    myEvent.addError(DataItem.ERROR_MISSING, getFieldForClass(myClass));
                }
                continue;
            }

            /* If field is not allowed */
            if (myState == JDataFieldRequired.NOTALLOWED) {
                myEvent.addError(DataItem.ERROR_EXIST, getFieldForClass(myClass));
                continue;
            }

            /* Switch on class */
            switch (myClass) {
                case CreditDate:
                    /* Check value */
                    JDateDay myDate = myInfo.getValue(JDateDay.class);
                    if (myDate.compareTo(myEvent.getDate()) <= 0) {
                        myEvent.addError(ERROR_BADDATE, getFieldForClass(myClass));
                    }
                    break;
                case QualifyYears:
                    /* Check value */
                    Integer myYears = myInfo.getValue(Integer.class);
                    if (myYears == 0) {
                        myEvent.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (myYears < 0) {
                        myEvent.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case TaxCredit:
                    /* Check value */
                    JMoney myAmount = myInfo.getValue(JMoney.class);
                    if (!myAmount.isPositive()) {
                        myEvent.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case NatInsurance:
                case DeemedBenefit:
                case CharityDonation:
                    /* Check value */
                    myAmount = myInfo.getValue(JMoney.class);
                    if (myAmount.isZero()) {
                        myEvent.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (!myAmount.isPositive()) {
                        myEvent.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case CreditUnits:
                case DebitUnits:
                    /* Check value */
                    JUnits myUnits = myInfo.getValue(JUnits.class);
                    if (myUnits.isZero()) {
                        myEvent.addError(DataItem.ERROR_ZERO, getFieldForClass(myClass));
                    } else if (!myUnits.isPositive()) {
                        myEvent.addError(DataItem.ERROR_NEGATIVE, getFieldForClass(myClass));
                    }
                    break;
                case Dilution:
                    /* Check range */
                    JDilution myDilution = myInfo.getValue(JDilution.class);
                    if (myDilution.outOfRange()) {
                        myEvent.addError(DataItem.ERROR_RANGE, getFieldForClass(myClass));
                    }
                    break;
                case ThirdParty:
                    /* Check account type */
                    Account myThirdParty = myInfo.getAccount();
                    if (!myThirdParty.isSavings()) {
                        myEvent.addError(ERROR_BADACCOUNT, getFieldForClass(myClass));
                    }
                    break;
                case Reference:
                case Comments:
                    /* Check length */
                    String myValue = myInfo.getValue(String.class);
                    if (myValue.length() > myClass.getMaximumLength()) {
                        myEvent.addError(DataItem.ERROR_LENGTH, getFieldForClass(myClass));
                    }
                    break;
                default:
                case CreditAmount:
                case Pension:
                    break;
            }
        }
    }
}
