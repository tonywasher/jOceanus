/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2020 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.lethe.database;

import net.sourceforge.joceanus.jmetis.lethe.data.MetisFields.MetisLetheField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.lethe.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.lethe.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataSet;
import net.sourceforge.joceanus.jprometheus.lethe.data.DataValues;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusDataStore;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableDefinition;
import net.sourceforge.joceanus.jprometheus.lethe.database.PrometheusTableStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableStaticData extension for AccountCategoryType.
 * @author Tony Washer
 */
public class TableAssetCurrency
        extends PrometheusTableStaticData<AssetCurrency, MoneyWiseDataType> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = AssetCurrency.LIST_NAME;

    /**
     * Constructors.
     * @param pDatabase the database control
     */
    protected TableAssetCurrency(final PrometheusDataStore<MoneyWiseData> pDatabase) {
        super(pDatabase, TABLE_NAME);
        final PrometheusTableDefinition myTableDef = getTableDef();
        myTableDef.addBooleanColumn(AssetCurrency.FIELD_DEFAULT);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        final MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getAccountCurrencies());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        final PrometheusTableDefinition myTableDef = getTableDef();

        /* Build data values */
        final DataValues<MoneyWiseDataType> myValues = getRowValues(AssetCurrency.OBJECT_NAME);
        myValues.addValue(AssetCurrency.FIELD_DEFAULT, myTableDef.getBooleanValue(AssetCurrency.FIELD_DEFAULT));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final AssetCurrency pItem,
                                 final MetisLetheField iField) throws OceanusException {
        /* Switch on field id */
        final PrometheusTableDefinition myTableDef = getTableDef();
        if (AssetCurrency.FIELD_DEFAULT.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isDefault());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
