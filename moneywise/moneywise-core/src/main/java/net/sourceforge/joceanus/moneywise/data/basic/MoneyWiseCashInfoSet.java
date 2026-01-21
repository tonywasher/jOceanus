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
package net.sourceforge.joceanus.moneywise.data.basic;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.metis.data.MetisDataFieldValue;
import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseCashInfo.MoneyWiseCashInfoList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInfoClass;
import io.github.tonywasher.joceanus.prometheus.data.PrometheusDataInfoSet;
import io.github.tonywasher.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * CashInfoSet class.
 *
 * @author Tony Washer
 */
public class MoneyWiseCashInfoSet
        extends PrometheusDataInfoSet<MoneyWiseCashInfo> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWiseCashInfoSet> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWiseCashInfoSet.class);

    /**
     * FieldSet map.
     */
    private static final Map<MetisDataFieldId, MoneyWiseAccountInfoClass> FIELDSET_MAP = FIELD_DEFS.buildFieldMap(MoneyWiseAccountInfoClass.class, MoneyWiseCashInfoSet::getFieldValue);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<MoneyWiseAccountInfoClass, MetisDataFieldId> REVERSE_FIELDMAP = MetisFieldSet.reverseFieldMap(FIELDSET_MAP, MoneyWiseAccountInfoClass.class);

    /**
     * AutoExpense Not Expense Error Text.
     */
    public static final String ERROR_AUTOEXP = MoneyWiseBasicResource.CASH_ERROR_AUTOEXPENSE.getValue();

    /**
     * Constructor.
     *
     * @param pOwner    the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the InfoList for the set
     */
    protected MoneyWiseCashInfoSet(final MoneyWiseCash pOwner,
                                   final MoneyWiseAccountInfoTypeList pTypeList,
                                   final MoneyWiseCashInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MoneyWiseCash getOwner() {
        return (MoneyWiseCash) super.getOwner();
    }

    /**
     * Obtain fieldValue for infoSet.
     *
     * @param pFieldId the fieldId
     * @return the value
     */
    public Object getFieldValue(final MetisDataFieldId pFieldId) {
        /* Handle InfoSet fields */
        final MoneyWiseAccountInfoClass myClass = getClassForField(pFieldId);
        if (myClass != null) {
            return getInfoSetValue(myClass);
        }

        /* Pass onwards */
        return null;
    }

    /**
     * Get an infoSet value.
     *
     * @param pInfoClass the class of info to get
     * @return the value to set
     */
    private Object getInfoSetValue(final MoneyWiseAccountInfoClass pInfoClass) {
        final Object myValue;

        switch (pInfoClass) {
            case AUTOPAYEE:
                /* Access payee of object */
                myValue = getPayee(pInfoClass);
                break;
            case AUTOEXPENSE:
                /* Access event category of object */
                myValue = getEventCategory(pInfoClass);
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
     *
     * @param pField the field
     * @return the class
     */
    public static MoneyWiseAccountInfoClass getClassForField(final MetisDataFieldId pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     *
     * @param pClass the class
     * @return the field
     */
    public static MetisDataFieldId getFieldForClass(final MoneyWiseAccountInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
    }

    @Override
    public MetisDataFieldId getFieldForClass(final PrometheusDataInfoClass pClass) {
        return getFieldForClass((MoneyWiseAccountInfoClass) pClass);
    }

    /**
     * Obtain the payee for the infoClass.
     *
     * @param pInfoClass the Info Class
     * @return the payee
     */
    public MoneyWisePayee getPayee(final MoneyWiseAccountInfoClass pInfoClass) {
        /* Access existing entry */
        final MoneyWiseCashInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the payee */
        return myValue.getPayee();
    }

    /**
     * Obtain the event category for the infoClass.
     *
     * @param pInfoClass the Info Class
     * @return the event category
     */
    public MoneyWiseTransCategory getEventCategory(final MoneyWiseAccountInfoClass pInfoClass) {
        /* Access existing entry */
        final MoneyWiseCashInfo myValue = getInfo(pInfoClass);

        /* If we have no entry, return null */
        if (myValue == null) {
            return null;
        }

        /* Return the event category */
        return myValue.getEventCategory();
    }

    @Override
    public Iterator<PrometheusDataInfoClass> classIterator() {
        final PrometheusDataInfoClass[] myValues = MoneyWiseAccountInfoClass.values();
        return Arrays.stream(myValues).iterator();
    }

    /**
     * Clone the dataInfoSet.
     *
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final MoneyWiseCashInfoSet pSource) {
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
        for (MoneyWiseCashInfo myInfo : this) {
            myInfo.resolveEditSetLinks(pEditSet);
        }
    }
}
