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
package net.sourceforge.joceanus.jmoneywise.atlas.database;

import net.sourceforge.joceanus.jmetis.data.MetisDataItem.MetisDataFieldId;
import net.sourceforge.joceanus.jmoneywise.atlas.data.basic.MoneyWiseDataSet;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseCurrency;
import net.sourceforge.joceanus.jmoneywise.atlas.data.statics.MoneyWiseStaticResource;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataSet;
import net.sourceforge.joceanus.jprometheus.atlas.data.PrometheusDataValues;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.atlas.database.PrometheusTableStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableStaticData extension for AccountCategoryType.
 * @author Tony Washer
 */
public class MoneyWiseTableCurrency
        extends PrometheusTableStaticData<MoneyWiseCurrency> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = MoneyWiseCurrency.LIST_NAME;

    /**
     * Constructors.
     * @param pDatabase the database control
     */
    protected MoneyWiseTableCurrency(final PrometheusDataStore pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();
        myTableDef.addBooleanColumn(MoneyWiseStaticResource.CURRENCY_DEFAULT);
    }

    @Override
    protected void declareData(final PrometheusDataSet pData) {
        final MoneyWiseDataSet myData = (MoneyWiseDataSet) pData;
        setList(myData.getAccountCurrencies());
    }

    @Override
    protected PrometheusDataValues loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final PrometheusDataValues myValues = getRowValues(MoneyWiseCurrency.OBJECT_NAME);
        myValues.addValue(MoneyWiseStaticResource.CURRENCY_DEFAULT, myTableDef.getBooleanValue(MoneyWiseStaticResource.CURRENCY_DEFAULT));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final MoneyWiseCurrency pItem,
                                 final MetisDataFieldId iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (MoneyWiseStaticResource.CURRENCY_DEFAULT.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isDefault());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}