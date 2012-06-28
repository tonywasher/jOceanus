/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
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
package uk.co.tolcroft.finance.data;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.JDataManager.Difference;
import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import net.sourceforge.JDataManager.JDataFields;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import net.sourceforge.JDataManager.JDataObject.JDataContents;
import net.sourceforge.JDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.JDecimal.Dilution;
import net.sourceforge.JDecimal.Money;
import net.sourceforge.JDecimal.Units;
import uk.co.tolcroft.finance.data.EventData.EventDataList;
import uk.co.tolcroft.finance.data.EventInfoType.EventInfoTypeList;
import uk.co.tolcroft.finance.data.EventValue.EventValueList;
import uk.co.tolcroft.finance.data.StaticClass.EventInfoClass;
import uk.co.tolcroft.models.data.DataList.ListStyle;

/**
 * EventInfoSet data type.
 * @author Tony Washer
 */
public class EventInfoSet implements JDataContents {
    /**
     * Report fields.
     */
    protected static final JDataFields FIELD_DEFS = new JDataFields(EventInfoSet.class.getSimpleName());

    /**
     * Event Field Id.
     */
    public static final JDataField FIELD_EVENT = FIELD_DEFS.declareEqualityField("Event");

    /**
     * Tax Credit Field Id.
     */
    public static final JDataField FIELD_TAXCREDIT = FIELD_DEFS.declareEqualityField("TaxCredit");

    /**
     * NatInsurance Field Id.
     */
    public static final JDataField FIELD_NATINSURE = FIELD_DEFS.declareEqualityField("NatInsurance");

    /**
     * Benefit Field Id.
     */
    public static final JDataField FIELD_BENEFIT = FIELD_DEFS.declareEqualityField("Benefit");

    /**
     * Credit Units Field Id.
     */
    public static final JDataField FIELD_CREDITUNITS = FIELD_DEFS.declareEqualityField("CreditUnits");

    /**
     * Debit Units Field Id.
     */
    public static final JDataField FIELD_DEBITUNITS = FIELD_DEFS.declareEqualityField("DebitUnits");

    /**
     * Dilution Field Id.
     */
    public static final JDataField FIELD_DILUTION = FIELD_DEFS.declareEqualityField("Dilution");

    /**
     * Xfer Delay Field Id.
     */
    public static final JDataField FIELD_XFERDELAY = FIELD_DEFS.declareEqualityField("XferDelay");

    /**
     * QualifyYears Field Id.
     */
    public static final JDataField FIELD_QUALIFYYEARS = FIELD_DEFS.declareEqualityField("QualifyYears");

    /**
     * Third Party Field Id.
     */
    public static final JDataField FIELD_THIRDPARTY = FIELD_DEFS.declareEqualityField("ThirdParty");

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    @Override
    public Object getFieldValue(final JDataField pField) {
        if (FIELD_EVENT.equals(pField)) {
            return theEvent;
        }
        if (FIELD_TAXCREDIT.equals(pField)) {
            return theDataMap.get(EventInfoClass.TaxCredit);
        }
        if (FIELD_NATINSURE.equals(pField)) {
            return theDataMap.get(EventInfoClass.NatInsurance);
        }
        if (FIELD_BENEFIT.equals(pField)) {
            return theDataMap.get(EventInfoClass.Benefit);
        }
        if (FIELD_CREDITUNITS.equals(pField)) {
            return theDataMap.get(EventInfoClass.CreditUnits);
        }
        if (FIELD_DEBITUNITS.equals(pField)) {
            return theDataMap.get(EventInfoClass.DebitUnits);
        }
        if (FIELD_DILUTION.equals(pField)) {
            return theDataMap.get(EventInfoClass.Dilution);
        }
        if (FIELD_XFERDELAY.equals(pField)) {
            return theValueMap.get(EventInfoClass.XferDelay);
        }
        if (FIELD_QUALIFYYEARS.equals(pField)) {
            return theValueMap.get(EventInfoClass.QualifyYears);
        }
        if (FIELD_THIRDPARTY.equals(pField)) {
            return theValueMap.get(EventInfoClass.CashAccount);
            // return theValueMap.get(EventInfoClass.ThirdParty);
        }
        return JDataFieldValue.UnknownField;
    }

    @Override
    public String formatObject() {
        return FIELD_DEFS.getName();
    }

    /**
     * The Event to which this set belongs.
     */
    private Event theEvent = null;

    /**
     * The list of EventInfoTypes.
     */
    private EventInfoTypeList theTypes = null;

    /**
     * The EventData list for new data.
     */
    private EventDataList theDataList = null;

    /**
     * The EventValue list for new data.
     */
    private EventValueList theValueList = null;

    /**
     * The Map of the Event Values.
     */
    private Map<EventInfoClass, EventValue> theValueMap = null;

    /**
     * The Map of the Event Data.
     */
    private Map<EventInfoClass, EventData> theDataMap = null;

    /**
     * Constructor.
     * @param pEvent the Event to which this Set belongs
     */
    protected EventInfoSet(final Event pEvent) {
        /* Store the Event */
        theEvent = pEvent;

        /* Create the Maps */
        theValueMap = new EnumMap<EventInfoClass, EventValue>(EventInfoClass.class);
        theDataMap = new EnumMap<EventInfoClass, EventData>(EventInfoClass.class);

        /* Access the dataSet */
        FinanceData myData = pEvent.getDataSet();

        /* If the underlying event is EDIT */
        if (pEvent.getStyle() == ListStyle.EDIT) {
            /* Create the lists for the Info */
            theValueList = new EventValueList(myData, ListStyle.EDIT);
            theDataList = new EventDataList(myData, ListStyle.EDIT);

            /* else use the ones for the DataSet */
        } else {
            /* Register the Value and Data lists */
            theValueList = myData.getEventValues();
            theDataList = myData.getEventData();
        }

        /* Access the EventInfo Types */
        theTypes = myData.getInfoTypes();
    }

    /**
     * Constructor.
     * @param pEvent the event to which this is linked
     * @param pSet the InfoSet to clone
     */
    protected EventInfoSet(final Event pEvent,
                           final EventInfoSet pSet) {
        /* Call standard constructor */
        this(pEvent);

        /* Return if there is no infoSet */
        if (pSet == null) {
            return;
        }

        /* Clone the InfoSetor each EventInfo in the underlying ValueMap */
        for (EventValue myValue : pSet.theValueMap.values()) {
            /* Create the new value */
            EventValue myNew = new EventValue(theValueList, myValue);
            theValueList.add(myNew);

            /* Add to the value map */
            theValueMap.put(myValue.getInfoType().getInfoClass(), myNew);
        }

        /* For each EventData in the underlying DataMap */
        for (EventData myData : pSet.theDataMap.values()) {
            /* Create the new data */
            EventData myNew = new EventData(theDataList, myData);
            theDataList.add(myNew);

            /* Add to the value map */
            theDataMap.put(myData.getInfoType().getInfoClass(), myNew);
        }
    }

    /**
     * Create a new EventValue.
     * @param pClass the class of the item
     * @return the new value
     * @throws JDataException on error
     */
    protected EventValue getNewValue(final EventInfoClass pClass) throws JDataException {
        /* Access the EventInfoType */
        EventInfoType myType = theTypes.findItemByClass(pClass);

        /* Create the new value and add to the list */
        EventValue myValue = theValueList.addNewItem(myType, theEvent);

        /* Register the value and return it */
        registerValue(myValue);
        return myValue;
    }

    /**
     * Create a new EventData.
     * @param pClass the class of the item
     * @return the new data
     * @throws JDataException on error
     */
    protected EventData getNewData(final EventInfoClass pClass) throws JDataException {
        /* Access the EventInfoType */
        EventInfoType myType = theTypes.findItemByClass(pClass);

        /* Create the new data and add to the list */
        EventData myData = theDataList.addNewItem(myType, theEvent);

        /* Register the data and return it */
        registerData(myData);
        return myData;
    }

    /**
     * Validate an InfoSet.
     */
    protected void validate() {
        /* Access Event values */
        Account myDebit = theEvent.getDebit();
        Account myCredit = theEvent.getCredit();
        TransactionType myTrans = theEvent.getTransType();

        /* Access Units */
        Units myDebUnits = getUnits(EventInfoClass.DebitUnits);
        Units myCredUnits = getUnits(EventInfoClass.CreditUnits);

        /* If we have Credit/Debit Units */
        if ((myDebUnits != null) || (myCredUnits != null)) {
            /* If we have debit units */
            if ((myDebit != null) && (myDebUnits != null)) {
                /* Debit Units are only allowed if debit is priced */
                if (!myDebit.isPriced()) {
                    theEvent.addError("Units are only allowed involving assets", FIELD_DEBITUNITS);
                }

                /* TranType of dividend cannot debit units */
                if ((myTrans != null) && (myTrans.isDividend())) {
                    theEvent.addError("Units cannot be debited for a dividend", FIELD_DEBITUNITS);
                }

                /* Units must be non-zero and positive */
                if ((!myDebUnits.isNonZero()) || (!myDebUnits.isPositive())) {
                    theEvent.addError("Units must be non-Zero and positive", FIELD_DEBITUNITS);
                }
            }

            /* If we have Credit units */
            if ((myCredit != null) && (myCredUnits != null)) {
                /* Credit Units are only allowed if credit is priced */
                if (!myCredit.isPriced()) {
                    theEvent.addError("Units are only allowed involving assets", FIELD_CREDITUNITS);
                }

                /* TranType of admin charge cannot credit units */
                if ((myTrans != null) && (myTrans.isAdminCharge())) {
                    theEvent.addError("Units cannot be credited for an AdminCharge", FIELD_CREDITUNITS);
                }

                /* Units must be non-zero and positive */
                if ((!myCredUnits.isNonZero()) || (!myCredUnits.isPositive())) {
                    theEvent.addError("Units must be non-Zero and positive", FIELD_CREDITUNITS);
                }
            }

            /* If both credit/debit are both priced */
            if ((myCredit != null) && (myDebit != null) && (myCredit.isPriced()) && (myDebit.isPriced())) {
                /* TranType must be stock split or dividend between same account */
                if ((myTrans == null)
                        || ((!myTrans.isDividend()) && (!myTrans.isStockSplit())
                                && (!myTrans.isAdminCharge()) && (!myTrans.isStockDemerger()) && (!myTrans
                                    .isStockTakeover()))) {
                    theEvent.addError("Units can only refer to a single priced asset unless "
                                              + "transaction is StockSplit/AdminCharge/Demerger/Takeover or Dividend",
                                      FIELD_CREDITUNITS);
                    theEvent.addError("Units can only refer to a single priced asset unless "
                                              + "transaction is StockSplit/AdminCharge/Demerger/Takeover or Dividend",
                                      FIELD_DEBITUNITS);
                }

                /* Dividend between priced requires identical credit/debit */
                if ((myTrans != null) && (myTrans.isDividend()) && (Difference.isEqual(myCredit, myDebit))) {
                    theEvent.addError("Unit Dividends between assets must be between same asset",
                                      FIELD_CREDITUNITS);
                }

                /* Cannot have Credit and Debit if accounts are identical */
                if ((myCredUnits != null) && (myDebUnits != null) && (Difference.isEqual(myCredit, myDebit))) {
                    theEvent.addError("Cannot credit and debit same account", FIELD_CREDITUNITS);
                }
            }

            /* Else check for required units */
        } else {
            if (theEvent.isStockSplit()) {
                theEvent.addError("Stock Split requires non-zero Units", FIELD_CREDITUNITS);
            } else if (theEvent.isAdminCharge()) {
                theEvent.addError("Admin Charge requires non-zero Units", FIELD_DEBITUNITS);
            }
        }

        /* Access Dilution */
        Dilution myDilution = getDilution(EventInfoClass.Dilution);

        /* If we have a dilution */
        if (myDilution != null) {
            /* If the dilution is not allowed */
            if ((!Event.needsDilution(myTrans)) && (!myTrans.isStockSplit())) {
                theEvent.addError("Dilution factor given where not allowed", FIELD_DILUTION);
            }

            /* If the dilution is out of range */
            if (myDilution.outOfRange()) {
                theEvent.addError("Dilution factor value is outside allowed range (0-1)", FIELD_DILUTION);
            }

            /* else if we are missing a required dilution factor */
        } else if (Event.needsDilution(myTrans)) {
            theEvent.addError("Dilution factor missing where required", FIELD_DILUTION);
        }

        /* Access Years and Tax Credit */
        Integer myYears = getValue(EventInfoClass.QualifyYears);
        Money myTax = getMoney(EventInfoClass.TaxCredit);

        /* If we are a taxable gain */
        if ((myTrans != null) && (myTrans.isTaxableGain())) {
            /* Years must be positive */
            if ((myYears == null) || (myYears <= 0)) {
                theEvent.addError("Years must be non-zero and positive", FIELD_QUALIFYYEARS);
            }

            /* Tax Credit must be non-null and positive */
            if ((myTax == null) || (!myTax.isPositive())) {
                theEvent.addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
            }

            /* If we need a tax credit */
        } else if ((myTrans != null) && (Event.needsTaxCredit(myTrans, myDebit))) {
            /* Tax Credit must be non-null and positive */
            if ((myTax == null) || (!myTax.isPositive())) {
                theEvent.addError("TaxCredit must be non-null", FIELD_TAXCREDIT);
            }

            /* Years must be null */
            if (myYears != null) {
                theEvent.addError("Years must be null", FIELD_QUALIFYYEARS);
            }

            /* else we should not have a tax credit */
        } else if (myTrans != null) {
            /* Tax Credit must be null */
            if (myTax != null) {
                theEvent.addError("TaxCredit must be null", FIELD_TAXCREDIT);
            }

            /* Years must be null */
            if (myYears != null) {
                theEvent.addError("Years must be null", FIELD_QUALIFYYEARS);
            }
        }
    }

    /**
     * Register the event value.
     * @param pValue the Value
     * @throws JDataException on error
     */
    protected void registerValue(final EventValue pValue) throws JDataException {
        /* Obtain the Map value */
        EventInfoType myType = pValue.getInfoType();
        EventValue myValue = theValueMap.get(myType.getInfoClass());

        /* If we already have a value */
        if (myValue != null) {
            throw new JDataException(ExceptionClass.LOGIC, theEvent, "InfoClass " + myType.getName()
                    + " already registered");
        }

        /* Store the value to the map */
        theValueMap.put(myType.getInfoClass(), pValue);
    }

    /**
     * DeRegister the event value.
     * @param pValue the Value
     */
    protected void deRegisterValue(final EventValue pValue) {
        /* Obtain the Type */
        EventInfoType myType = pValue.getInfoType();

        /* Remove the reference from the map */
        theValueMap.remove(myType.getInfoClass());
    }

    /**
     * Register the event data.
     * @param pData the Data
     * @throws JDataException on error
     */
    protected void registerData(final EventData pData) throws JDataException {
        /* Obtain the Map value */
        EventInfoType myType = pData.getInfoType();
        EventData myData = theDataMap.get(myType.getInfoClass());

        /* If we already have a value */
        if (myData != null) {
            throw new JDataException(ExceptionClass.LOGIC, theEvent, "InfoClass " + myType.getName()
                    + " already registered");
        }

        /* Store the value to the map */
        theDataMap.put(myType.getInfoClass(), pData);
    }

    /**
     * DeRegister the event data.
     * @param pData the Data
     */
    protected void deRegisterData(final EventData pData) {
        /* Obtain the Type */
        EventInfoType myType = pData.getInfoType();

        /* Remove the reference from the map */
        theDataMap.remove(myType.getInfoClass());
    }

    /**
     * Obtain the required Event Value.
     * @param pType the Value Type
     * @return the value
     */
    protected Integer getValue(final EventInfoClass pType) {
        /* Obtain the Map value */
        EventValue myValue = theValueMap.get(pType);

        /* Return the value */
        return (myValue == null) ? null : myValue.getValue();
    }

    /**
     * Obtain the required Event Account.
     * @param pType the Value Type
     * @return the account
     */
    protected Account getAccount(final EventInfoClass pType) {
        /* Obtain the Map value */
        EventValue myValue = theValueMap.get(pType);

        /* Return the value */
        return (myValue == null) ? null : myValue.getAccount();
    }

    /**
     * Obtain the required Event Data Money.
     * @param pType the Value Type
     * @return the money
     */
    protected Money getMoney(final EventInfoClass pType) {
        /* Obtain the Map value */
        EventData myData = theDataMap.get(pType);

        /* Return the data */
        return (myData == null) ? null : myData.getMoney();
    }

    /**
     * Obtain the required Event Data Units.
     * @param pType the Value Type
     * @return the units
     */
    protected Units getUnits(final EventInfoClass pType) {
        /* Obtain the Map value */
        EventData myData = theDataMap.get(pType);

        /* Return the data */
        return (myData == null) ? null : myData.getUnits();
    }

    /**
     * Obtain the required Event Data Dilution.
     * @param pType the Value Type
     * @return the dilution
     */
    protected Dilution getDilution(final EventInfoClass pType) {
        /* Obtain the Map value */
        EventData myData = theDataMap.get(pType);

        /* Return the data */
        return (myData == null) ? null : myData.getDilution();
    }

    /**
     * Set the required Event Value.
     * @param pType the Value Type
     * @param pValue the Value (may be null)
     * @throws JDataException on error
     */
    protected void setValue(final EventInfoType pType,
                            final Integer pValue) throws JDataException {
        /* Obtain the Map value */
        EventValue myValue = theValueMap.get(pType.getInfoClass());

        /* If we do not have a map value */
        if (myValue == null) {
            /* Add a new value */
            myValue = theValueList.addNewItem(pType, theEvent);

            /* Store it to the map */
            theValueMap.put(pType.getInfoClass(), myValue);
        }

        /* Store the value */
        myValue.setValue(pValue);
    }

    /**
     * Set the required Event Account.
     * @param pType the Value Type
     * @param pValue the Value (may be null)
     * @throws JDataException on error
     */
    protected void setAccount(final EventInfoType pType,
                              final Account pValue) throws JDataException {
        /* Obtain the Map value */
        EventValue myValue = theValueMap.get(pType.getInfoClass());

        /* If we do not have a map value */
        if (myValue == null) {
            /* Add a new value */
            myValue = theValueList.addNewItem(pType, theEvent);

            /* Store it to the map */
            theValueMap.put(pType.getInfoClass(), myValue);
        }

        /* Store the value */
        myValue.setAccount(pValue);
    }

    /**
     * Set the required Event Money.
     * @param pType the Value Type
     * @param pValue the Value (may be null)
     * @throws JDataException on error
     */
    protected void setMoney(final EventInfoType pType,
                            final Money pValue) throws JDataException {
        /* Obtain the Map data */
        EventData myData = theDataMap.get(pType.getInfoClass());

        /* If we do not have a map value */
        if (myData == null) {
            /* Add a new value */
            myData = theDataList.addNewItem(pType, theEvent);

            /* Store it to the map */
            theDataMap.put(pType.getInfoClass(), myData);
        }

        /* Store the value */
        myData.setMoney(pValue);
    }

    /**
     * Set the required Event Units.
     * @param pType the Value Type
     * @param pValue the Value (may be null)
     * @throws JDataException on error
     */
    protected void setUnits(final EventInfoType pType,
                            final Units pValue) throws JDataException {
        /* Obtain the Map data */
        EventData myData = theDataMap.get(pType.getInfoClass());

        /* If we do not have a map value */
        if (myData == null) {
            /* Add a new value */
            myData = theDataList.addNewItem(pType, theEvent);

            /* Store it to the map */
            theDataMap.put(pType.getInfoClass(), myData);
        }

        /* Store the value */
        myData.setUnits(pValue);
    }

    /**
     * Set the required Event Dilution.
     * @param pType the Value Type
     * @param pValue the Value (may be null)
     * @throws JDataException on error
     */
    protected void setDilution(final EventInfoType pType,
                               final Dilution pValue) throws JDataException {
        /* Obtain the Map data */
        EventData myData = theDataMap.get(pType.getInfoClass());

        /* If we do not have a map value */
        if (myData == null) {
            /* Add a new value */
            myData = theDataList.addNewItem(pType, theEvent);

            /* Store it to the map */
            theDataMap.put(pType.getInfoClass(), myData);
        }

        /* Store the value */
        myData.setDilution(pValue);
    }
}
