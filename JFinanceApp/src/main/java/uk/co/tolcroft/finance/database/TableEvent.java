/*******************************************************************************
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

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.database.TableEncrypted;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;

public class TableEvent extends TableEncrypted<Event> {
	/**
	 * The name of the Events table
	 */
	protected final static String TableName 	= Event.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition theTableDef;	/* Set during load */

	/**
	 * The event list
	 */
	private Event.List		theList 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableEvent(Database<?> 	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		/* Define Standard table */
		super.defineTable(pTableDef);
		theTableDef = pTableDef;
		
		/* Define sort column variable */
		ColumnDefinition myDateCol;
		
		/* Define the columns */
		myDateCol = theTableDef.addDateColumn(Event.FIELD_DATE, Event.fieldName(Event.FIELD_DATE));
		theTableDef.addEncryptedColumn(Event.FIELD_DESC, Event.fieldName(Event.FIELD_DESC), Event.DESCLEN);
		theTableDef.addEncryptedColumn(Event.FIELD_AMOUNT, Event.fieldName(Event.FIELD_AMOUNT), EncryptedItem.MONEYLEN);
		theTableDef.addReferenceColumn(Event.FIELD_DEBIT, Event.fieldName(Event.FIELD_DEBIT), TableAccount.TableName);
		theTableDef.addReferenceColumn(Event.FIELD_CREDIT, Event.fieldName(Event.FIELD_CREDIT), TableAccount.TableName);
		theTableDef.addNullEncryptedColumn(Event.FIELD_UNITS, Event.fieldName(Event.FIELD_UNITS), EncryptedItem.UNITSLEN);
		theTableDef.addReferenceColumn(Event.FIELD_TRNTYP, Event.fieldName(Event.FIELD_TRNTYP), TableTransactionType.TableName);
		theTableDef.addNullEncryptedColumn(Event.FIELD_TAXCREDIT, Event.fieldName(Event.FIELD_TAXCREDIT), EncryptedItem.MONEYLEN);
		theTableDef.addNullEncryptedColumn(Event.FIELD_DILUTION, Event.fieldName(Event.FIELD_DILUTION), EncryptedItem.DILUTELEN);
		theTableDef.addNullIntegerColumn(Event.FIELD_YEARS, Event.fieldName(Event.FIELD_YEARS));
		
		/* Declare the sort order */
		myDateCol.setSortOrder(SortOrder.ASCENDING);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getEvents();
		setList(theList);
	}

	/* Load the event */
	protected void loadItem(int pId, int pControlId) throws ModelException {
		int  	myDebitId;
		int  	myCreditId;
		int  	myTranType;
		byte[] 	myDesc;
		byte[] 	myAmount;
		byte[] 	myUnits;
		byte[] 	myTaxCred;
		byte[]	myDilution;
		Integer	myYears;
		Date  	myDate;
		
		/* Get the various fields */
		myDate 		= theTableDef.getDateValue(Event.FIELD_DATE);
		myDesc    	= theTableDef.getBinaryValue(Event.FIELD_DESC);
		myAmount    = theTableDef.getBinaryValue(Event.FIELD_AMOUNT);
		myDebitId 	= theTableDef.getIntegerValue(Event.FIELD_DEBIT);
		myCreditId 	= theTableDef.getIntegerValue(Event.FIELD_CREDIT);
		myUnits 	= theTableDef.getBinaryValue(Event.FIELD_UNITS);
		myTranType  = theTableDef.getIntegerValue(Event.FIELD_TRNTYP);
		myTaxCred   = theTableDef.getBinaryValue(Event.FIELD_TAXCREDIT);
		myDilution  = theTableDef.getBinaryValue(Event.FIELD_DILUTION);
		myYears  	= theTableDef.getIntegerValue(Event.FIELD_YEARS);
	
		/* Add into the list */
		theList.addItem(pId,
						pControlId,
			       	   	myDate,
				        myDesc,
				        myAmount,
				        myDebitId, 
				        myCreditId,
				        myUnits,
				        myTranType,
				        myTaxCred,
				        myDilution,
				        myYears);
	}
	
	/* Set a field value */
	protected void setFieldValue(Event	pItem, int iField) throws ModelException  {
		/* Switch on field id */
		switch (iField) {
			case Event.FIELD_DATE: 		theTableDef.setDateValue(Event.FIELD_DATE, pItem.getDate());					break;
			case Event.FIELD_DESC:		theTableDef.setBinaryValue(Event.FIELD_DESC, pItem.getDescBytes());				break;
			case Event.FIELD_AMOUNT:	theTableDef.setBinaryValue(Event.FIELD_AMOUNT, pItem.getAmountBytes());			break;
			case Event.FIELD_DEBIT:		theTableDef.setIntegerValue(Event.FIELD_DEBIT, pItem.getDebit().getId());		break;
			case Event.FIELD_CREDIT:	theTableDef.setIntegerValue(Event.FIELD_CREDIT, pItem.getCredit().getId());		break;
			case Event.FIELD_UNITS:		theTableDef.setBinaryValue(Event.FIELD_UNITS, pItem.getUnitsBytes());			break;
			case Event.FIELD_TRNTYP:	theTableDef.setIntegerValue(Event.FIELD_TRNTYP, pItem.getTransType().getId());	break;
			case Event.FIELD_TAXCREDIT:	theTableDef.setBinaryValue(Event.FIELD_TAXCREDIT, pItem.getTaxCredBytes());		break;
			case Event.FIELD_DILUTION:	theTableDef.setBinaryValue(Event.FIELD_DILUTION, pItem.getDilutionBytes());		break;
			case Event.FIELD_YEARS:		theTableDef.setIntegerValue(Event.FIELD_YEARS, pItem.getYears());				break;
			default:					super.setFieldValue(pItem, iField);												break;
		}
	}
}
