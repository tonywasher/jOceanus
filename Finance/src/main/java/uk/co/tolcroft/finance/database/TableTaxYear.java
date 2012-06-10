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
     * The table definition.
     */
    private TableDefinition theTableDef; /* Set during load */

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
    }

    /**
     * Define the table columns (called from within super-constructor).
     * @param pTableDef the table definition
     */
    @Override
    protected void defineTable(final TableDefinition pTableDef) {
        /* Define Standard table */
        super.defineTable(pTableDef);
        theTableDef = pTableDef;

        /* Define sort column variable */
        ColumnDefinition myDateCol;

        /* define the columns */
        myDateCol = theTableDef.addDateColumn(TaxYear.FIELD_TAXYEAR);
        theTableDef.addReferenceColumn(TaxYear.FIELD_REGIME, TableTaxRegime.TABLE_NAME);
        theTableDef.addMoneyColumn(TaxYear.FIELD_ALLOW);
        theTableDef.addMoneyColumn(TaxYear.FIELD_RENTAL);
        theTableDef.addMoneyColumn(TaxYear.FIELD_LOBAND);
        theTableDef.addMoneyColumn(TaxYear.FIELD_BSBAND);
        theTableDef.addMoneyColumn(TaxYear.FIELD_LOAGAL);
        theTableDef.addMoneyColumn(TaxYear.FIELD_HIAGAL);
        theTableDef.addMoneyColumn(TaxYear.FIELD_AGELMT);
        theTableDef.addNullMoneyColumn(TaxYear.FIELD_ADDLMT);
        theTableDef.addRateColumn(TaxYear.FIELD_LOTAX);
        theTableDef.addRateColumn(TaxYear.FIELD_BASTAX);
        theTableDef.addRateColumn(TaxYear.FIELD_HITAX);
        theTableDef.addRateColumn(TaxYear.FIELD_INTTAX);
        theTableDef.addRateColumn(TaxYear.FIELD_DIVTAX);
        theTableDef.addRateColumn(TaxYear.FIELD_HDVTAX);
        theTableDef.addNullRateColumn(TaxYear.FIELD_ADDTAX);
        theTableDef.addNullRateColumn(TaxYear.FIELD_ADVTAX);
        theTableDef.addNullMoneyColumn(TaxYear.FIELD_ADDBDY);
        theTableDef.addNullMoneyColumn(TaxYear.FIELD_CAPALW);
        theTableDef.addNullRateColumn(TaxYear.FIELD_CAPTAX);
        theTableDef.addNullRateColumn(TaxYear.FIELD_HCPTAX);

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
        Date myYear = theTableDef.getDateValue(TaxYear.FIELD_TAXYEAR);
        int myRegime = theTableDef.getIntegerValue(TaxYear.FIELD_REGIME);
        String myAllowance = theTableDef.getStringValue(TaxYear.FIELD_ALLOW);
        String myRentalAllow = theTableDef.getStringValue(TaxYear.FIELD_RENTAL);
        String myLoBand = theTableDef.getStringValue(TaxYear.FIELD_LOBAND);
        String myBasicBand = theTableDef.getStringValue(TaxYear.FIELD_BSBAND);
        String myLoAgeAllow = theTableDef.getStringValue(TaxYear.FIELD_LOAGAL);
        String myHiAgeAllow = theTableDef.getStringValue(TaxYear.FIELD_HIAGAL);
        String myAgeAllowLimit = theTableDef.getStringValue(TaxYear.FIELD_AGELMT);
        String myAddAllowLimit = theTableDef.getStringValue(TaxYear.FIELD_ADDLMT);
        String myLoTaxRate = theTableDef.getStringValue(TaxYear.FIELD_LOTAX);
        String myBasicTaxRate = theTableDef.getStringValue(TaxYear.FIELD_BASTAX);
        String myHiTaxRate = theTableDef.getStringValue(TaxYear.FIELD_HITAX);
        String myIntTaxRate = theTableDef.getStringValue(TaxYear.FIELD_INTTAX);
        String myDivTaxRate = theTableDef.getStringValue(TaxYear.FIELD_DIVTAX);
        String myHiDivTaxRate = theTableDef.getStringValue(TaxYear.FIELD_HDVTAX);
        String myAddTaxRate = theTableDef.getStringValue(TaxYear.FIELD_ADDTAX);
        String myAddDivTaxRate = theTableDef.getStringValue(TaxYear.FIELD_ADVTAX);
        String myAddIncBound = theTableDef.getStringValue(TaxYear.FIELD_ADDBDY);
        String myCapitalAllow = theTableDef.getStringValue(TaxYear.FIELD_CAPALW);
        String myCapTaxRate = theTableDef.getStringValue(TaxYear.FIELD_CAPTAX);
        String myHiCapTaxRate = theTableDef.getStringValue(TaxYear.FIELD_HCPTAX);

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
        if (iField == TaxYear.FIELD_TAXYEAR) {
            theTableDef.setDateValue(iField, pItem.getTaxYear());
        } else if (iField == TaxYear.FIELD_REGIME) {
            theTableDef.setIntegerValue(iField, pItem.getTaxRegime().getId());
        } else if (iField == TaxYear.FIELD_ALLOW) {
            theTableDef.setMoneyValue(iField, pItem.getAllowance());
        } else if (iField == TaxYear.FIELD_RENTAL) {
            theTableDef.setMoneyValue(iField, pItem.getRentalAllowance());
        } else if (iField == TaxYear.FIELD_LOBAND) {
            theTableDef.setMoneyValue(iField, pItem.getLoBand());
        } else if (iField == TaxYear.FIELD_BSBAND) {
            theTableDef.setMoneyValue(iField, pItem.getBasicBand());
        } else if (iField == TaxYear.FIELD_LOAGAL) {
            theTableDef.setMoneyValue(iField, pItem.getLoAgeAllow());
        } else if (iField == TaxYear.FIELD_HIAGAL) {
            theTableDef.setMoneyValue(iField, pItem.getHiAgeAllow());
        } else if (iField == TaxYear.FIELD_AGELMT) {
            theTableDef.setMoneyValue(iField, pItem.getAgeAllowLimit());
        } else if (iField == TaxYear.FIELD_ADDLMT) {
            theTableDef.setMoneyValue(iField, pItem.getAddAllowLimit());
        } else if (iField == TaxYear.FIELD_LOTAX) {
            theTableDef.setRateValue(iField, pItem.getLoTaxRate());
        } else if (iField == TaxYear.FIELD_BASTAX) {
            theTableDef.setRateValue(iField, pItem.getBasicTaxRate());
        } else if (iField == TaxYear.FIELD_HITAX) {
            theTableDef.setRateValue(iField, pItem.getHiTaxRate());
        } else if (iField == TaxYear.FIELD_INTTAX) {
            theTableDef.setRateValue(iField, pItem.getIntTaxRate());
        } else if (iField == TaxYear.FIELD_DIVTAX) {
            theTableDef.setRateValue(iField, pItem.getDivTaxRate());
        } else if (iField == TaxYear.FIELD_HDVTAX) {
            theTableDef.setRateValue(iField, pItem.getHiDivTaxRate());
        } else if (iField == TaxYear.FIELD_ADDTAX) {
            theTableDef.setRateValue(iField, pItem.getAddTaxRate());
        } else if (iField == TaxYear.FIELD_ADVTAX) {
            theTableDef.setRateValue(iField, pItem.getAddDivTaxRate());
        } else if (iField == TaxYear.FIELD_ADDBDY) {
            theTableDef.setMoneyValue(iField, pItem.getAddIncBound());
        } else if (iField == TaxYear.FIELD_CAPALW) {
            theTableDef.setMoneyValue(iField, pItem.getCapitalAllow());
        } else if (iField == TaxYear.FIELD_CAPTAX) {
            theTableDef.setRateValue(iField, pItem.getCapTaxRate());
        } else if (iField == TaxYear.FIELD_HCPTAX) {
            theTableDef.setRateValue(iField, pItem.getHiCapTaxRate());
        } else {
            super.setFieldValue(pItem, iField);
        }
    }
}
