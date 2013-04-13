/*******************************************************************************
 * jMoneyWise: Finance Application
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
package net.sourceforge.jOceanus.jMoneyWise.data;

import net.sourceforge.jOceanus.jDataManager.JDataFields;
import net.sourceforge.jOceanus.jDataManager.JDataFields.JDataField;
import net.sourceforge.jOceanus.jDataManager.JDataObject.JDataFieldValue;
import net.sourceforge.jOceanus.jDataModels.data.DataInfoSet;
import net.sourceforge.jOceanus.jMoneyWise.data.EventInfo.EventInfoList;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoClass;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType;
import net.sourceforge.jOceanus.jMoneyWise.data.statics.EventInfoType.EventInfoTypeList;

/**
 * EventInfoSet class.
 * @author Tony Washer
 */
public class EventInfoSet
        extends DataInfoSet<EventInfo, Event, EventInfoType, EventInfoClass> {
    /**
     * Report fields.
     */
    private static final JDataFields FIELD_DEFS = new JDataFields(EventInfoSet.class.getSimpleName(), DataInfoSet.FIELD_DEFS);

    @Override
    public JDataFields getDataFields() {
        return FIELD_DEFS;
    }

    /**
     * DebitUnits Field Id.
     */
    public static final JDataField FIELD_DEBITUNITS = FIELD_DEFS.declareLocalField(EventInfoClass.DebitUnits.toString());

    /**
     * CreditUnits Field Id.
     */
    public static final JDataField FIELD_CREDITUNITS = FIELD_DEFS.declareLocalField(EventInfoClass.CreditUnits.toString());

    /**
     * TaxCredit Field Id.
     */
    public static final JDataField FIELD_TAXCREDIT = FIELD_DEFS.declareLocalField(EventInfoClass.TaxCredit.toString());

    /**
     * Dilution Field Id.
     */
    public static final JDataField FIELD_DILUTION = FIELD_DEFS.declareLocalField(EventInfoClass.Dilution.toString());

    /**
     * Years Field Id.
     */
    public static final JDataField FIELD_YEARS = FIELD_DEFS.declareLocalField(EventInfoClass.QualifyYears.toString());

    /**
     * NatInsurance Field Id.
     */
    public static final JDataField FIELD_NATINS = FIELD_DEFS.declareLocalField(EventInfoClass.NatInsurance.toString());

    /**
     * Benefit Field Id.
     */
    public static final JDataField FIELD_BENEFIT = FIELD_DEFS.declareLocalField(EventInfoClass.Benefit.toString());

    /**
     * Pension Field Id.
     */
    public static final JDataField FIELD_PENSION = FIELD_DEFS.declareLocalField(EventInfoClass.Pension.toString());

    /**
     * XferDelay Field Id.
     */
    public static final JDataField FIELD_XFERDELAY = FIELD_DEFS.declareLocalField(EventInfoClass.XferDelay.toString());

    /**
     * Reference Field Id.
     */
    public static final JDataField FIELD_REFERENCE = FIELD_DEFS.declareLocalField(EventInfoClass.Reference.toString());

    /**
     * Donation Field Id.
     */
    public static final JDataField FIELD_DONATION = FIELD_DEFS.declareLocalField(EventInfoClass.CharityDonation.toString());

    /**
     * ThirdParty Field Id.
     */
    public static final JDataField FIELD_THIRDPARTY = FIELD_DEFS.declareLocalField(EventInfoClass.ThirdParty.toString());

    /**
     * CreditAmount Field Id.
     */
    public static final JDataField FIELD_CREDITAMT = FIELD_DEFS.declareLocalField(EventInfoClass.CreditAmount.toString());

    @Override
    public Object getFieldValue(final JDataField pField) {
        /* Handle InfoSet fields */
        EventInfoClass myClass = getFieldClass(pField);
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
        /* Access value of object */
        Object myValue = getField(pInfoClass);

        /* Return the value */
        return (myValue != null)
                ? myValue
                : JDataFieldValue.SkipField;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    protected static EventInfoClass getFieldClass(final JDataField pField) {
        if (FIELD_DEBITUNITS.equals(pField)) {
            return EventInfoClass.DebitUnits;
        }
        if (FIELD_CREDITUNITS.equals(pField)) {
            return EventInfoClass.CreditUnits;
        }
        if (FIELD_TAXCREDIT.equals(pField)) {
            return EventInfoClass.TaxCredit;
        }
        if (FIELD_DILUTION.equals(pField)) {
            return EventInfoClass.Dilution;
        }
        if (FIELD_YEARS.equals(pField)) {
            return EventInfoClass.QualifyYears;
        }
        if (FIELD_NATINS.equals(pField)) {
            return EventInfoClass.NatInsurance;
        }
        if (FIELD_BENEFIT.equals(pField)) {
            return EventInfoClass.Benefit;
        }
        if (FIELD_PENSION.equals(pField)) {
            return EventInfoClass.Pension;
        }
        if (FIELD_XFERDELAY.equals(pField)) {
            return EventInfoClass.XferDelay;
        }
        if (FIELD_REFERENCE.equals(pField)) {
            return EventInfoClass.Reference;
        }
        if (FIELD_DONATION.equals(pField)) {
            return EventInfoClass.CharityDonation;
        }
        if (FIELD_THIRDPARTY.equals(pField)) {
            return EventInfoClass.ThirdParty;
        }
        if (FIELD_CREDITAMT.equals(pField)) {
            return EventInfoClass.CreditAmount;
        }
        return null;
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
        super.cloneDataInfoSet(pSource);
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
}
