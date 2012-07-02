/*******************************************************************************
 * JFinanceApp: Finance Application
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
package uk.co.tolcroft.finance.database;

import java.util.Date;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataFields.JDataField;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.finance.data.TaxYear;
import uk.co.tolcroft.finance.data.TaxYear.TaxYearList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.ColumnDefinition;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.DatabaseTable;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;

/**
 * DatabaseTable extension for TaxYear.
 * @author Tony Washer
 */
public class TableTaxYear extends DatabaseTable<TaxYear> {
    /**
     * The name of the TaxYears table.
     */
    protected static final String TABLE_NAME = TaxYear.LIST_NAME;

    /**
     * The TaxYear list.
     */
    private TaxYearList theList = null;

    /**
     * The DataSet.
     */
    private FinanceData theData = null;

    /**
     * Constructor.
     * @param pDatabase the database control
     */
    protected TableTaxYear(final Database<?> pDatabase) {
        super(pDatabase, TABLE_NAME);
        TableDefinition myTableDef = getTableDef();

        /* define the columns */
        ColumnDefinition myDateCol = myTableDef.addDateColumn(TaxYear.FIELD_TAXYEAR);
        myTableDef.addReferenceColumn(TaxYear.FIELD_REGIME, TableTaxRegime.TABLE_NAME);
        myTableDef.addMoneyColumn(TaxYear.FIELD_ALLOW);
        myTableDef.addMoneyColumn(TaxYear.FIELD_RENTAL);
        myTableDef.addMoneyColumn(TaxYear.FIELD_LOBAND);
        myTableDef.addMoneyColumn(TaxYear.FIELD_BSBAND);
        myTableDef.addMoneyColumn(TaxYear.FIELD_LOAGAL);
        myTableDef.addMoneyColumn(TaxYear.FIELD_HIAGAL);
        myTableDef.addMoneyColumn(TaxYear.FIELD_AGELMT);
        myTableDef.addNullMoneyColumn(TaxYear.FIELD_ADDLMT);
        myTableDef.addRateColumn(TaxYear.FIELD_LOTAX);
        myTableDef.addRateColumn(TaxYear.FIELD_BASTAX);
        myTableDef.addRateColumn(TaxYear.FIELD_HITAX);
        myTableDef.addRateColumn(TaxYear.FIELD_INTTAX);
        myTableDef.addRateColumn(TaxYear.FIELD_DIVTAX);
        myTableDef.addRateColumn(TaxYear.FIELD_HDVTAX);
        myTableDef.addNullRateColumn(TaxYear.FIELD_ADDTAX);
        myTableDef.addNullRateColumn(TaxYear.FIELD_ADVTAX);
        myTableDef.addNullMoneyColumn(TaxYear.FIELD_ADDBDY);
        myTableDef.addNullMoneyColumn(TaxYear.FIELD_CAPALW);
        myTableDef.addNullRateColumn(TaxYear.FIELD_CAPTAX);
        myTableDef.addNullRateColumn(TaxYear.FIELD_HCPTAX);

        /* Declare the sort order */
        myDateCol.setSortOrder(SortOrder.ASCENDING);
    }

    @Override
    protected void declareData(final DataSet<?> pData) {
        FinanceData myData = (FinanceData) pData;
        theData = myData;
        theList = myData.getTaxYears();
        setList(theList);
    }

    @Override
    protected void postProcessOnLoad() throws JDataException {
        theData.calculateDateRange();
    }

    @Override
    public void loadItem(final int pId) throws JDataException {
        /* Get the various fields */
        TableDefinition myTableDef = getTableDef();
        Date myYear = myTableDef.getDateValue(TaxYear.FIELD_TAXYEAR);
        int myRegime = myTableDef.getIntegerValue(TaxYear.FIELD_REGIME);
        String myAllowance = myTableDef.getStringValue(TaxYear.FIELD_ALLOW);
        String myRentalAllow = myTableDef.getStringValue(TaxYear.FIELD_RENTAL);
        String myLoBand = myTableDef.getStringValue(TaxYear.FIELD_LOBAND);
        String myBasicBand = myTableDef.getStringValue(TaxYear.FIELD_BSBAND);
        String myLoAgeAllow = myTableDef.getStringValue(TaxYear.FIELD_LOAGAL);
        String myHiAgeAllow = myTableDef.getStringValue(TaxYear.FIELD_HIAGAL);
        String myAgeAllowLimit = myTableDef.getStringValue(TaxYear.FIELD_AGELMT);
        String myAddAllowLimit = myTableDef.getStringValue(TaxYear.FIELD_ADDLMT);
        String myLoTaxRate = myTableDef.getStringValue(TaxYear.FIELD_LOTAX);
        String myBasicTaxRate = myTableDef.getStringValue(TaxYear.FIELD_BASTAX);
        String myHiTaxRate = myTableDef.getStringValue(TaxYear.FIELD_HITAX);
        String myIntTaxRate = myTableDef.getStringValue(TaxYear.FIELD_INTTAX);
        String myDivTaxRate = myTableDef.getStringValue(TaxYear.FIELD_DIVTAX);
        String myHiDivTaxRate = myTableDef.getStringValue(TaxYear.FIELD_HDVTAX);
        String myAddTaxRate = myTableDef.getStringValue(TaxYear.FIELD_ADDTAX);
        String myAddDivTaxRate = myTableDef.getStringValue(TaxYear.FIELD_ADVTAX);
        String myAddIncBound = myTableDef.getStringValue(TaxYear.FIELD_ADDBDY);
        String myCapitalAllow = myTableDef.getStringValue(TaxYear.FIELD_CAPALW);
        String myCapTaxRate = myTableDef.getStringValue(TaxYear.FIELD_CAPTAX);
        String myHiCapTaxRate = myTableDef.getStringValue(TaxYear.FIELD_HCPTAX);

        /* Add into the list */
        theList.addItem(pId, myRegime, myYear, myAllowance, myRentalAllow, myLoAgeAllow, myHiAgeAllow,
                        myCapitalAllow, myAgeAllowLimit, myAddAllowLimit, myLoBand, myBasicBand,
                        myAddIncBound, myLoTaxRate, myBasicTaxRate, myHiTaxRate, myIntTaxRate, myDivTaxRate,
                        myHiDivTaxRate, myAddTaxRate, myAddDivTaxRate, myCapTaxRate, myHiCapTaxRate);
    }

    @Override
    protected void setFieldValue(final TaxYear pItem,
                                 final JDataField iField) throws JDataException {
        /* Switch on field id */
        TableDefinition myTableDef = getTableDef();
        if (iField == TaxYear.FIELD_TAXYEAR) {
            myTableDef.setDateValue(iField, pItem.getTaxYear());
        } else if (iField == TaxYear.FIELD_REGIME) {
            myTableDef.setIntegerValue(iField, pItem.getTaxRegime().getId());
        } else if (iField == TaxYear.FIELD_ALLOW) {
            myTableDef.setMoneyValue(iField, pItem.getAllowance());
        } else if (iField == TaxYear.FIELD_RENTAL) {
            myTableDef.setMoneyValue(iField, pItem.getRentalAllowance());
        } else if (iField == TaxYear.FIELD_LOBAND) {
            myTableDef.setMoneyValue(iField, pItem.getLoBand());
        } else if (iField == TaxYear.FIELD_BSBAND) {
            myTableDef.setMoneyValue(iField, pItem.getBasicBand());
        } else if (iField == TaxYear.FIELD_LOAGAL) {
            myTableDef.setMoneyValue(iField, pItem.getLoAgeAllow());
        } else if (iField == TaxYear.FIELD_HIAGAL) {
            myTableDef.setMoneyValue(iField, pItem.getHiAgeAllow());
        } else if (iField == TaxYear.FIELD_AGELMT) {
            myTableDef.setMoneyValue(iField, pItem.getAgeAllowLimit());
        } else if (iField == TaxYear.FIELD_ADDLMT) {
            myTableDef.setMoneyValue(iField, pItem.getAddAllowLimit());
        } else if (iField == TaxYear.FIELD_LOTAX) {
            myTableDef.setRateValue(iField, pItem.getLoTaxRate());
        } else if (iField == TaxYear.FIELD_BASTAX) {
            myTableDef.setRateValue(iField, pItem.getBasicTaxRate());
        } else if (iField == TaxYear.FIELD_HITAX) {
            myTableDef.setRateValue(iField, pItem.getHiTaxRate());
        } else if (iField == TaxYear.FIELD_INTTAX) {
            myTableDef.setRateValue(iField, pItem.getIntTaxRate());
        } else if (iField == TaxYear.FIELD_DIVTAX) {
            myTableDef.setRateValue(iField, pItem.getDivTaxRate());
        } else if (iField == TaxYear.FIELD_HDVTAX) {
            myTableDef.setRateValue(iField, pItem.getHiDivTaxRate());
        } else if (iField == TaxYear.FIELD_ADDTAX) {
            myTableDef.setRateValue(iField, pItem.getAddTaxRate());
        } else if (iField == TaxYear.FIELD_ADVTAX) {
            myTableDef.setRateValue(iField, pItem.getAddDivTaxRate());
        } else if (iField == TaxYear.FIELD_ADDBDY) {
            myTableDef.setMoneyValue(iField, pItem.getAddIncBound());
        } else if (iField == TaxYear.FIELD_CAPALW) {
            myTableDef.setMoneyValue(iField, pItem.getCapitalAllow());
        } else if (iField == TaxYear.FIELD_CAPTAX) {
            myTableDef.setRateValue(iField, pItem.getCapTaxRate());
        } else if (iField == TaxYear.FIELD_HCPTAX) {
            myTableDef.setRateValue(iField, pItem.getHiCapTaxRate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
