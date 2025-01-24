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
package net.sourceforge.joceanus.moneywise.data.basic;

import java.util.Map;

import net.sourceforge.joceanus.metis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.field.MetisFieldRequired;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolioInfo.MoneyWisePortfolioInfoList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

/**
 * PortfolioInfoSet class.
 * @author Tony Washer
 */
public class MoneyWisePortfolioInfoSet
        extends PrometheusDataInfoSet<MoneyWisePortfolioInfo> {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<MoneyWisePortfolioInfoSet> FIELD_DEFS = MetisFieldSet.newFieldSet(MoneyWisePortfolioInfoSet.class);

    /**
     * FieldSet map.
     */
    private static final Map<MetisDataFieldId, MoneyWiseAccountInfoClass> FIELDSET_MAP = FIELD_DEFS.buildFieldMap(MoneyWiseAccountInfoClass.class, MoneyWisePortfolioInfoSet::getFieldValue);

    /**
     * Reverse FieldSet map.
     */
    private static final Map<MoneyWiseAccountInfoClass, MetisDataFieldId> REVERSE_FIELDMAP = MetisFieldSet.reverseFieldMap(FIELDSET_MAP, MoneyWiseAccountInfoClass.class);

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
     * @param pTypeList the infoTypeList for the set
     * @param pInfoList the InfoList for the set
     */
    protected MoneyWisePortfolioInfoSet(final MoneyWisePortfolio pOwner,
                                        final MoneyWiseAccountInfoTypeList pTypeList,
                                        final MoneyWisePortfolioInfoList pInfoList) {
        /* Store the Owner and Info List */
        super(pOwner, pTypeList, pInfoList);
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public MoneyWisePortfolio getOwner() {
        return (MoneyWisePortfolio) super.getOwner();
    }

    /**
     * Obtain fieldValue for infoSet.
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
     * @param pInfoClass the class of info to get
     * @return the value to set
     */
    private Object getInfoSetValue(final MoneyWiseAccountInfoClass pInfoClass) {
        /* Return the value */
        final Object myValue = getField(pInfoClass);
        return myValue != null
                ? myValue
                : MetisDataFieldValue.SKIP;
    }

    /**
     * Obtain the class of the field if it is an infoSet field.
     * @param pField the field
     * @return the class
     */
    public static MoneyWiseAccountInfoClass getClassForField(final MetisDataFieldId pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
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
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final MoneyWisePortfolioInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
    }

    /**
     * Resolve editSetLinks.
     * @param pEditSet the editSet
     * @throws OceanusException on error
     */
    void resolveEditSetLinks(final PrometheusEditSet pEditSet) throws OceanusException {
        /* Loop through the items */
        for (MoneyWisePortfolioInfo myInfo : this) {
            myInfo.resolveEditSetLinks(pEditSet);
        }
    }

    /**
     * Determine if a field is required.
     * @param pField the infoSet field
     * @return the status
     */
    public MetisFieldRequired isFieldRequired(final MetisDataFieldId pField) {
        final MoneyWiseAccountInfoClass myClass = getClassForField(pField);
        return myClass == null
                ? MetisFieldRequired.NOTALLOWED
                : isClassRequired(myClass);
    }

    @Override
    public MetisFieldRequired isClassRequired(final PrometheusDataInfoClass pClass) {
        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES:
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
                return MetisFieldRequired.CANEXIST;

            /* Not Allowed */
            case MATURITY:
            case OPENINGBALANCE:
            case AUTOEXPENSE:
            case AUTOPAYEE:
            case SYMBOL:
            case REGION:
            case UNDERLYINGSTOCK:
            case OPTIONPRICE:
            default:
                return MetisFieldRequired.NOTALLOWED;
        }
    }

    /**
     * Validate the infoSet.
     */
    protected void validate() {
        /* Loop through the classes */
        for (final MoneyWiseAccountInfoClass myClass : MoneyWiseAccountInfoClass.values()) {
            /* Access info for class */
            final MoneyWisePortfolioInfo myInfo = getInfo(myClass);

            /* If basic checks are passed */
            if (checkClass(myInfo, myClass)) {
                /* validate the class */
                validateClass(myInfo, myClass);
            }
        }
    }

    /**
     * Validate the class.
     * @param pInfo the info
     * @param pClass the infoClass
     */
    private void validateClass(final MoneyWisePortfolioInfo pInfo,
                               final MoneyWiseAccountInfoClass pClass) {
        /* Switch on class */
        switch (pClass) {
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case SORTCODE:
            case ACCOUNT:
            case NOTES:
            case REFERENCE:
                validateInfoLength(pInfo);
                break;
            default:
                break;
        }
    }

    /**
     * Validate the info length.
     * @param pInfo the info
     */
    private void validateInfoLength(final MoneyWisePortfolioInfo pInfo) {
        final char[] myArray = pInfo.getValue(char[].class);
        final MoneyWiseAccountInfoClass myClass = pInfo.getInfoClass();
        if (myArray.length > myClass.getMaximumLength()) {
            getOwner().addError(PrometheusDataItem.ERROR_LENGTH, getFieldForClass(myClass));
        }
    }
}
