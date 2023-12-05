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

import java.util.Iterator;
import java.util.Map;

import net.sourceforge.joceanus.jmetis.data.MetisDataFieldValue;
import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmetis.field.MetisFieldRequired;
import net.sourceforge.joceanus.jmetis.field.MetisFieldSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseCashInfo.MoneyWiseCashInfoList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWisePayee.MoneyWisePayeeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseTransCategory.MoneyWiseTransCategoryList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseAccountInfoType.MoneyWiseAccountInfoTypeList;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataInfoSet;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataItem;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataList.PrometheusDataListSet;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;

/**
 * CashInfoSet class.
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
    private static final String ERROR_AUTOEXP = MoneyWiseBasicResource.CASH_ERROR_AUTOEXPENSE.getValue();

    /**
     * Constructor.
     * @param pOwner the Owner to which this Set belongs
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

    /**
     * Obtain the payee for the infoClass.
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

    /**
     * Clone the dataInfoSet.
     * @param pSource the InfoSet to clone
     */
    protected void cloneDataInfoSet(final MoneyWiseCashInfoSet pSource) {
        /* Clone the dataInfoSet */
        cloneTheDataInfoSet(pSource);
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
        /* Access details about the Cash */
        final MoneyWiseCash myCash = getOwner();
        final MoneyWiseCashCategory myCategory = myCash.getCategory();

        /* If we have no Category, no class is allowed */
        if (myCategory == null) {
            return MetisFieldRequired.NOTALLOWED;
        }

        /* Switch on class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            /* Allowed set */
            case NOTES:
                return MetisFieldRequired.CANEXIST;

            case OPENINGBALANCE:
                return myCash.isAutoExpense()
                        ? MetisFieldRequired.NOTALLOWED
                        : MetisFieldRequired.CANEXIST;
            case AUTOPAYEE:
            case AUTOEXPENSE:
                return myCash.isAutoExpense()
                        ? MetisFieldRequired.MUSTEXIST
                        : MetisFieldRequired.NOTALLOWED;

            /* Disallowed Set */
            case SORTCODE:
            case ACCOUNT:
            case REFERENCE:
            case WEBSITE:
            case CUSTOMERNO:
            case USERID:
            case PASSWORD:
            case MATURITY:
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
            /* validate the class */
            validateClass(myClass);
        }
    }

    /**
     * Validate the class.
     * @param pClass the infoClass
     */
    private void validateClass(final MoneyWiseAccountInfoClass pClass) {
        /* Access details about the Cash */
        final MoneyWiseCash myCash = getOwner();

        /* Access info for class */
        final MoneyWiseCashInfo myInfo = getInfo(pClass);
        final boolean isExisting = myInfo != null
                && !myInfo.isDeleted();

        /* Determine requirements for class */
        final MetisFieldRequired myState = isClassRequired(pClass);

        /* If the field is missing */
        if (!isExisting) {
            /* Handle required field missing */
            if (myState == MetisFieldRequired.MUSTEXIST) {
                myCash.addError(PrometheusDataItem.ERROR_MISSING, getFieldForClass(pClass));
            }
            return;
        }

        /* If field is not allowed */
        if (myState == MetisFieldRequired.NOTALLOWED) {
            myCash.addError(PrometheusDataItem.ERROR_EXIST, getFieldForClass(pClass));
            return;
        }

        /* Switch on class */
        switch (pClass) {
            case OPENINGBALANCE:
                /* Access data */
                final TethysMoney myBalance = myInfo.getValue(TethysMoney.class);
                if (!myBalance.getCurrency().equals(myCash.getCurrency())) {
                    myCash.addError(MoneyWiseDepositInfoSet.ERROR_CURRENCY, getFieldForClass(pClass));
                }
                break;
            case AUTOEXPENSE:
                /* Access data */
                final MoneyWiseTransCategory myExpense = myInfo.getEventCategory();
                final MoneyWiseTransCategoryClass myCatClass = myExpense.getCategoryTypeClass();
                if (!myCatClass.isExpense() || myCatClass.canParentCategory()) {
                    myCash.addError(ERROR_AUTOEXP, getFieldForClass(pClass));
                }
                break;
            case NOTES:
                /* Access data */
                final char[] myArray = myInfo.getValue(char[].class);
                if (myArray.length > pClass.getMaximumLength()) {
                    myCash.addError(PrometheusDataItem.ERROR_LENGTH, getFieldForClass(pClass));
                }
                break;
            default:
                break;
        }

    }

    @Override
    protected void setDefaultValue(final PrometheusDataListSet pUpdateSet,
                                   final PrometheusDataInfoClass pClass) throws OceanusException {
        /* Switch on the class */
        switch ((MoneyWiseAccountInfoClass) pClass) {
            case AUTOEXPENSE:
                setValue(pClass, getDefaultAutoExpense(pUpdateSet));
                break;
            case AUTOPAYEE:
                setValue(pClass, getDefaultAutoPayee(pUpdateSet));
                break;
            default:
                break;
        }
    }

    /**
     * Obtain default expense for autoExpense cash.
     * @param pUpdateSet the updateSet
     * @return the default expense
     */
    private static MoneyWiseTransCategory getDefaultAutoExpense(final PrometheusDataListSet pUpdateSet) {
        /* Access the category list */
        final MoneyWiseTransCategoryList myCategories = pUpdateSet.getDataList(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseTransCategoryList.class);

        /* loop through the categories */
        final Iterator<MoneyWiseTransCategory> myIterator = myCategories.iterator();
        while (myIterator.hasNext()) {
            final MoneyWiseTransCategory myCategory = myIterator.next();

            /* Ignore deleted categories */
            if (myCategory.isDeleted()) {
                continue;
            }

            /* Ignore categories that are the wrong class */
            final MoneyWiseTransCategoryClass myCatClass = myCategory.getCategoryTypeClass();
            if (myCatClass.isExpense() && !myCatClass.canParentCategory()) {
                return myCategory;
            }
        }

        /* Return no category */
        return null;
    }

    /**
     * Obtain default payee for autoExpense cash.
     * @param pUpdateSet the updateSet
     * @return the default payee
     */
    private static MoneyWisePayee getDefaultAutoPayee(final PrometheusDataListSet pUpdateSet) {
        /* Access the payee list */
        final MoneyWisePayeeList myPayees = pUpdateSet.getDataList(MoneyWiseBasicDataType.PAYEE, MoneyWisePayeeList.class);

        /* loop through the payees */
        final Iterator<MoneyWisePayee> myIterator = myPayees.iterator();
        while (myIterator.hasNext()) {
            final MoneyWisePayee myPayee = myIterator.next();

            /* Ignore deleted and closed payees */
            if (!myPayee.isDeleted() && !myPayee.isClosed()) {
                return myPayee;
            }
        }

        /* Return no payee */
        return null;
    }
}
