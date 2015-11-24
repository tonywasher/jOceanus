/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.database;

import net.sourceforge.joceanus.jmetis.data.JDataFields.JDataField;
import net.sourceforge.joceanus.jmoneywise.MoneyWiseDataType;
import net.sourceforge.joceanus.jmoneywise.data.MoneyWiseData;
import net.sourceforge.joceanus.jmoneywise.data.statics.AssetCurrency;
import net.sourceforge.joceanus.jprometheus.data.DataSet;
import net.sourceforge.joceanus.jprometheus.data.DataValues;
import net.sourceforge.joceanus.jprometheus.database.Database;
import net.sourceforge.joceanus.jprometheus.database.TableDefinition;
import net.sourceforge.joceanus.jprometheus.database.TableStaticData;
import net.sourceforge.joceanus.jtethys.OceanusException;

/**
 * TableStaticData extension for AccountCategoryType.
 * @author Tony Washer
 */
public class TableAssetCurrency
        extends TableStaticData<AssetCurrency, MoneyWiseDataType> {
    /**
     * The table name.
     */
    protected static final String TABLE_NAME = AssetCurrency.LIST_NAME;

    /**
     * Constructors.
     * @param pDatabase the database control
     */
    protected TableAssetCurrency(final Database<MoneyWiseData> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();
        myTableDef.addBooleanColumn(AssetCurrency.FIELD_DEFAULT);
    }

    @Override
    protected void declareData(final DataSet<?, ?> pData) {
        MoneyWiseData myData = (MoneyWiseData) pData;
        setList(myData.getAccountCurrencies());
    }

    @Override
    protected DataValues<MoneyWiseDataType> loadValues() throws OceanusException {
        /* Access the table definition */
        TableDefinition myTableDef = getTableDef();

        /* Build data values */
        DataValues<MoneyWiseDataType> myValues = getRowValues(AssetCurrency.OBJECT_NAME);
        myValues.addValue(AssetCurrency.FIELD_DEFAULT, myTableDef.getBooleanValue(AssetCurrency.FIELD_DEFAULT));

        /* Return the values */
        return myValues;
    }

    @Override
    protected void setFieldValue(final AssetCurrency pItem,
                                 final JDataField iField) throws OceanusException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (AssetCurrency.FIELD_DEFAULT.equals(iField)) {
            myTableDef.setBooleanValue(iField, pItem.isDefault());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
