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
package net.sourceforge.joceanus.moneywise.views;

import net.sourceforge.joceanus.metis.viewer.MetisViewerEntry;
import net.sourceforge.joceanus.metis.viewer.MetisViewerManager;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicDataType;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseBasicResource;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePayee;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWisePortfolio;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurity;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseSecurityHolding;
import net.sourceforge.joceanus.moneywise.data.basic.MoneyWiseTransCategory;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseAccountInfoClass;
import net.sourceforge.joceanus.moneywise.data.statics.MoneyWiseStaticDataType;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataItem;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataResource;
import net.sourceforge.joceanus.prometheus.data.PrometheusDataSet;
import net.sourceforge.joceanus.prometheus.data.PrometheusListKey;
import net.sourceforge.joceanus.prometheus.maps.PrometheusMapsCtl;
import net.sourceforge.joceanus.prometheus.views.PrometheusEditSet;
import net.sourceforge.joceanus.prometheus.views.PrometheusViewerEntryId;

import java.util.ArrayList;
import java.util.List;

/**
 * MoneyWise Maps.
 */
public class MoneyWiseMaps {
    /**
     * The Map control.
     */
    private final PrometheusMapsCtl theMaps;

    /**
     * Constructor.
     * @param pView the view
     */
    MoneyWiseMaps(final MoneyWiseView pView) {
        /* Create the control */
        theMaps = new PrometheusMapsCtl(MoneyWiseMaps::deconstructPair);

        /* Build the maps */
        buildMaps();

        /* Create the top level viewer entry for this view */
        final MetisViewerManager myViewer = pView.getViewerManager();
        final MetisViewerEntry mySection = pView.getViewerEntry(PrometheusViewerEntryId.ANALYSIS);
        final MetisViewerEntry myEntry = myViewer.newEntry(mySection, "NewMaps");
        myEntry.setObject(theMaps);
    }

    /**
     * Adjust for DataSet.
     * @param pDataSet the dataSet
     */
    public void adjustForDataSet(final PrometheusDataSet pDataSet) {
        theMaps.resetMaps();
        theMaps.adjustForDataSet(pDataSet);
    }

    /**
     * Adjust for EditSet.
     * @param pEditSet the editSet
     */
    public void adjustForEditSet(final PrometheusEditSet pEditSet) {
        theMaps.resetMaps();
        theMaps.adjustForEditSet(pEditSet);
    }

    /**
     * Build the maps.
     */
    private void buildMaps() {
        /* Build static maps */
        buildStaticMaps(MoneyWiseStaticDataType.DEPOSITTYPE);
        buildStaticMaps(MoneyWiseStaticDataType.CASHTYPE);
        buildStaticMaps(MoneyWiseStaticDataType.LOANTYPE);
        buildStaticMaps(MoneyWiseStaticDataType.PAYEETYPE);
        buildStaticMaps(MoneyWiseStaticDataType.SECURITYTYPE);
        buildStaticMaps(MoneyWiseStaticDataType.PORTFOLIOTYPE);
        buildStaticMaps(MoneyWiseStaticDataType.TRANSTYPE);
        buildStaticMaps(MoneyWiseStaticDataType.CURRENCY);
        buildStaticMaps(MoneyWiseStaticDataType.TAXBASIS);
        buildStaticMaps(MoneyWiseStaticDataType.ACCOUNTINFOTYPE);
        buildStaticMaps(MoneyWiseStaticDataType.TRANSINFOTYPE);

        /* Build transTag and region maps */
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.TRANSTAG, PrometheusDataResource.DATAITEM_FIELD_NAME);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.REGION, PrometheusDataResource.DATAITEM_FIELD_NAME);

        /* Build category maps */
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.DEPOSITCATEGORY, PrometheusDataResource.DATAITEM_FIELD_NAME);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.CASHCATEGORY, PrometheusDataResource.DATAITEM_FIELD_NAME);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.LOANCATEGORY, PrometheusDataResource.DATAITEM_FIELD_NAME);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.TRANSCATEGORY, PrometheusDataResource.DATAITEM_FIELD_NAME);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.TRANSCATEGORY, MoneyWiseStaticDataType.TRANSTYPE,
                t -> ((MoneyWiseTransCategory) t).getCategoryType().getCategoryClass().isSingular());

        /* Build asset maps */
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.PAYEE, PrometheusDataResource.DATAITEM_FIELD_NAME);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.PAYEE, MoneyWiseBasicResource.CATEGORY_NAME,
                t -> ((MoneyWisePayee) t).getCategoryClass().isSingular());
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.DEPOSIT, PrometheusDataResource.DATAITEM_FIELD_NAME, MoneyWiseBasicDataType.PAYEE);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.CASH, PrometheusDataResource.DATAITEM_FIELD_NAME, MoneyWiseBasicDataType.PAYEE);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.LOAN, PrometheusDataResource.DATAITEM_FIELD_NAME,MoneyWiseBasicDataType.PAYEE);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.PORTFOLIO, PrometheusDataResource.DATAITEM_FIELD_NAME, MoneyWiseBasicDataType.PAYEE);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.PORTFOLIO, MoneyWiseBasicResource.CATEGORY_NAME,
                t -> ((MoneyWisePortfolio) t).getCategoryClass().isSingular());
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.SECURITY, PrometheusDataResource.DATAITEM_FIELD_NAME);
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.SECURITY, MoneyWiseBasicResource.CATEGORY_NAME,
                t -> ((MoneyWiseSecurity) t).getCategoryClass().isSingular());
        theMaps.declareFieldIdMap(MoneyWiseBasicDataType.SECURITY, MoneyWiseAccountInfoClass.SYMBOL,
                t -> ((MoneyWiseSecurity) t).getCategoryClass().needsSymbol());

        /* Build date maps */
        theMaps.declareDateIdMap(MoneyWiseBasicDataType.EXCHANGERATE, MoneyWiseBasicResource.XCHGRATE_TO,
                MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        theMaps.declareDateIdMap(MoneyWiseBasicDataType.SECURITYPRICE, MoneyWiseBasicDataType.SECURITY,
                MoneyWiseBasicResource.MONEYWISEDATA_FIELD_DATE);
        theMaps.declareDateIdMap(MoneyWiseBasicDataType.DEPOSITRATE, MoneyWiseBasicDataType.DEPOSIT,
                MoneyWiseBasicResource.DEPOSITRATE_ENDDATE, true);
    }

    /**
     * Build the static map.
     * @param pKey the key
     */
    private void buildStaticMaps(final PrometheusListKey pKey) {
        theMaps.declareFieldIdMap(pKey, PrometheusDataResource.DATAITEM_FIELD_NAME);
        theMaps.declareFieldIdMap(pKey, PrometheusDataResource.STATICDATA_SORT);
    }

    /**
     * Deconstruct linkedPair.
     * @param pPair the linked pair
     */
    private static List<PrometheusDataItem> deconstructPair(final Object pPair) {
        final List<PrometheusDataItem> myList = new ArrayList<>();
        if (pPair instanceof MoneyWiseSecurityHolding) {
            final MoneyWiseSecurityHolding myHolding = (MoneyWiseSecurityHolding) pPair;
            myList.add(myHolding.getPortfolio());
            myList.add(myHolding.getSecurity());
        }
        return myList;
    }
}
