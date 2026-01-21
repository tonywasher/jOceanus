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
import io.github.tonywasher.joceanus.metis.field.MetisFieldRequired;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransInfo.MoneyWiseTransInfoList;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransCategoryClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseTransInfoType.MoneyWiseTransInfoTypeList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoSet;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * TransactionInfoSet class.
 *
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
     *
     * @param pOwner    the Owner to which this Set belongs
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
     *
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
     *
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
     *
     * @param pField the field
     * @return the class
     */
    public static MoneyWiseTransInfoClass getClassForField(final MetisDataFieldId pField) {
        /* Look up field in map */
        return FIELDSET_MAP.get(pField);
    }

    /**
     * Obtain the field for the infoSet class.
     *
     * @param pClass the class
     * @return the field
     */
    public static MetisDataFieldId getFieldForClass(final MoneyWiseTransInfoClass pClass) {
        /* Look up field in map */
        return REVERSE_FIELDMAP.get(pClass);
    }

    @Override
    public MetisDataFieldId getFieldForClass(final PrometheusDataInfoClass pClass) {
        return getFieldForClass((MoneyWiseTransInfoClass) pClass);
    }

    @Override
    public Iterator<PrometheusDataInfoClass> classIterator() {
        final PrometheusDataInfoClass[] myValues = MoneyWiseTransInfoClass.values();
        return Arrays.stream(myValues).iterator();
    }

    /**
     * Clone the dataInfoSet.
     *
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
        for (MoneyWiseTransInfo myInfo : this) {
            myInfo.resolveEditSetLinks(pEditSet);
        }
    }

    /**
     * Obtain the deposit for the infoClass.
     *
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
     * Determine if an infoSet class is metaData.
     *
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
     * Determine if AccountDeltaUnits can/mustBe/mustNotBe positive.
     *
     * @param pDir   the direction
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
     *
     * @param pDir   the direction
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
}
