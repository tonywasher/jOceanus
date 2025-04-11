/*******************************************************************************
 * Prometheus: Application Framework
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
package net.sourceforge.joceanus.prometheus.maps;

import net.sourceforge.joceanus.metis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.metis.data.MetisDataType;
import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.list.MetisListKey;
import net.sourceforge.joceanus.oceanus.format.OceanusDataFormatter;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoClass;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataInfoItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataList;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * Maps control.
 */
public class PrometheusMapsCtl
        implements MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<PrometheusMapsCtl> FIELD_DEFS = MetisFieldSet.newFieldSet(PrometheusMapsCtl.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_TOUCHMAP, PrometheusMapsCtl::getTouchMap);
        FIELD_DEFS.declareLocalField(PrometheusMapsResource.MAPS_INSTANCEMAP, PrometheusMapsCtl::getInstanceMap);
    }

    /**
     * The touchMap.
     */
    private PrometheusMapsTouchCtl theTouch;

    /**
     * The instanceMap.
     */
    private PrometheusMapsInstanceCtl theInstance;

    /**
     * The deconstruct lambda.
     */
    private Function<Object, List<PrometheusDataItem>> theDeconstruct;

    /**
     * Constructor.
     * @param pDeconstruct the deconstruct processor
     */
    public PrometheusMapsCtl(final Function<Object, List<PrometheusDataItem>> pDeconstruct) {
        /* Create maps */
        theTouch = new PrometheusMapsTouchCtl();
        theInstance = new PrometheusMapsInstanceCtl();
        theDeconstruct = pDeconstruct;
    }

    @Override
    public MetisFieldSetDef getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return PrometheusMapsInstanceCtl.class.getSimpleName();
    }

    /**
     * Obtain the touch map.
     * @return the touch map
     */
    private PrometheusMapsTouchCtl getTouchMap() {
        return theTouch;
    }

    /**
     * Obtain the instance map.
     * @return the instance map
     */
    private PrometheusMapsInstanceCtl getInstanceMap() {
        return theInstance;
    }

    /**
     * Declare fieldId map.
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     */
    public void declareFieldIdMap(final MetisListKey pListKey,
                                  final MetisDataFieldId pFieldId) {
        theInstance.declareFieldIdMap(pListKey, pFieldId);
    }

    /**
     * Declare fieldId map.
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     * @param pFilter the filter
     */
    public void declareFieldIdMap(final MetisListKey pListKey,
                                  final MetisDataFieldId pFieldId,
                                  final Function<PrometheusDataItem, Boolean> pFilter) {
        theInstance.declareFieldIdMap(pListKey, pFieldId, pFilter);
    }

    /**
     * Declare shared fieldId map.
     * @param pListKey the listKey
     * @param pFieldId the fieldId
     * @param pSharedKey the shared listKey
     */
    public void declareFieldIdMap(final MetisListKey pListKey,
                                  final MetisDataFieldId pFieldId,
                                  final MetisListKey pSharedKey) {
        theInstance.declareFieldIdMap(pListKey, pFieldId, pSharedKey);
    }

    /**
     * Declare dateId map.
     * @param pListKey the listKey
     * @param pOwnerId the ownerId
     * @param pDateId the dateId
     */
    public void declareDateIdMap(final MetisListKey pListKey,
                                 final MetisDataFieldId pOwnerId,
                                 final MetisDataFieldId pDateId) {
        theInstance.declareDateIdMap(pListKey, pOwnerId, pDateId, false);
    }

    /**
     * Declare dateId map.
     * @param pListKey the listKey
     * @param pOwnerId the ownerId
     * @param pDateId the dateId
     * @param pAllowNull do we allow null value?
     */
    public void declareDateIdMap(final MetisListKey pListKey,
                                 final MetisDataFieldId pOwnerId,
                                 final MetisDataFieldId pDateId,
                                 final boolean pAllowNull) {
        theInstance.declareDateIdMap(pListKey, pOwnerId, pDateId, pAllowNull);
    }

    /**
     * Reset the maps.
     */
    public void resetMaps() {
        theTouch.resetMap();
        theInstance.resetMaps();
    }

    /**
     * Adjust for DataSet.
     * @param pDataSet the dataSet
     */
    public void adjustForDataSet(final PrometheusDataSet pDataSet) {
        final Iterator<MetisListKey> myIterator = pDataSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();
            final PrometheusDataList<?> myList = pDataSet.getDataList(myKey, PrometheusDataList.class);

            adjustForDataList(myList);
        }
    }

    /**
     * Adjust for EditSet.
     * @param pEditSet the editSet
     */
    public void adjustForEditSet(final PrometheusEditSet pEditSet) {
        final PrometheusDataSet myDataSet = pEditSet.getDataSet();
        final Iterator<MetisListKey> myIterator = myDataSet.keyIterator();
        while (myIterator.hasNext()) {
            final MetisListKey myKey = myIterator.next();
            final PrometheusDataList<?> myList = pEditSet.getDataList(myKey, PrometheusDataList.class);

            adjustForDataList(myList);
        }
    }

    /**
     * Adjust for DataList.
     * @param pDataList the dataList
     */
    void adjustForDataList(final PrometheusDataList<?> pDataList) {
        final Iterator<?> myIterator = pDataList.iterator();
        while (myIterator.hasNext()) {
            final PrometheusDataItem myItem = (PrometheusDataItem) myIterator.next();
            if (!myItem.isDeleted()) {
                adjustForDataItem(myItem);
            }
        }
    }

    /**
     * Adjust for DataItem.
     * @param pItem the dataItem
     */
    void adjustForDataItem(final PrometheusDataItem pItem) {
        /* Add item to instance maps */
        theInstance.addItemToMaps(pItem);

        /* If the item is an InfoItem */
        if (pItem instanceof PrometheusDataInfoItem myItem) {
            adjustForDataInfoItemTouches(myItem);
        } else {
            adjustForStandardItemTouches(pItem);
        }
    }

    /**
     * Adjust for DataItem.
     * @param pItem the dataItem
     */
    void adjustForStandardItemTouches(final PrometheusDataItem pItem) {
        /* Loop through the item fields */
        final MetisFieldSetDef myFieldSet = pItem.getDataFieldSet();
        final Iterator<MetisFieldDef> myIterator = myFieldSet.fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();

            /* If this is a link */
            if (MetisDataType.LINK.equals(myField.getDataType())) {
                final Object myTouched = myField.getFieldValue(pItem);
                if (myTouched != null) {
                    if (myTouched instanceof PrometheusDataItem myItem) {
                        theTouch.recordTouch(myItem, pItem);
                    } else {
                        final List<PrometheusDataItem> myList = theDeconstruct.apply(myTouched);
                        for (PrometheusDataItem myItem : myList) {
                            theTouch.recordTouch(myItem, pItem);
                        }
                    }
                }
            }
        }
    }

    /**
     * Adjust for DataInfoItem.
     * @param pInfo the dataInfoItem
     */
    void adjustForDataInfoItemTouches(final PrometheusDataInfoItem pInfo) {
        final PrometheusDataInfoClass myClass = pInfo.getInfoClass();
        if (myClass.isLink()) {
            theTouch.recordTouch(pInfo.getLink(PrometheusDataItem.class), pInfo.getOwner());
        }
    }
}
