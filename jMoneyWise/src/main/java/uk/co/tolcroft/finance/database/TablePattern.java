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
import uk.co.tolcroft.models.database.TableEncrypted;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;

public class TablePattern extends TableEncrypted<Event> {
	/**
	 * The name of the Patterns table
	 */
	protected final static String TableName		= Pattern.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition theTableDef;	/* Set during load */

	/**
	 * The pattern list
	 */
	private Pattern.List	theList 			= null;

	/**
	 * The accounts list
	 */
	private Account.List	theAccounts			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TablePattern(Database<?>	pDatabase) {
		super(pDatabase, TableName);
	}
	
	/**
	 * Define the table columns (called from within super-constructor)
	 * @param pTableDef the table definition
	 */
	protected void defineTable(TableDefinition	pTableDef) {
		/* Define sort column variable */
		super.defineTable(pTableDef);
		theTableDef = pTableDef;

		/* Define Sort Column variables */
		ColumnDefinition myDateCol;
		ColumnDefinition myActCol;
		
		/* Declare the columns */
		myActCol = theTableDef.addReferenceColumn(Pattern.VFIELD_ACCOUNT, Pattern.fieldName(Pattern.VFIELD_ACCOUNT), TableAccount.TableName);
		myDateCol = theTableDef.addDateColumn(Pattern.FIELD_DATE, Pattern.fieldName(Pattern.FIELD_DATE));
		theTableDef.addEncryptedColumn(Pattern.FIELD_DESC, Pattern.fieldName(Pattern.FIELD_DESC), Pattern.DESCLEN);
		theTableDef.addEncryptedColumn(Pattern.FIELD_AMOUNT, Pattern.fieldName(Pattern.FIELD_AMOUNT), EncryptedItem.MONEYLEN);
		theTableDef.addReferenceColumn(Pattern.VFIELD_PARTNER, Pattern.fieldName(Pattern.VFIELD_PARTNER), TableAccount.TableName);
		theTableDef.addReferenceColumn(Pattern.FIELD_TRNTYP, Pattern.fieldName(Pattern.FIELD_TRNTYP), TableTransactionType.TableName);
		theTableDef.addBooleanColumn(Pattern.FIELD_ISCREDIT, Pattern.fieldName(Pattern.FIELD_ISCREDIT));
		theTableDef.addReferenceColumn(Pattern.FIELD_FREQ, Pattern.fieldName(Pattern.FIELD_FREQ), TableFrequency.TableName);
		
		/* Declare Sort Columns */
		myDateCol.setSortOrder(SortOrder.ASCENDING);
		myActCol.setSortOrder(SortOrder.ASCENDING);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList 	= myData.getPatterns();
		theAccounts = myData.getAccounts();
		setList(theList);
	}

	/**
	 * postProcess on Load
	 */
	protected void postProcessOnLoad() throws ModelException {
		theAccounts.validateLoadedAccounts();
	}
	
	/* Load the pattern */
	protected void loadItem(int pId, int pControlId) throws ModelException {
		int		myAccountId;
		int  	myPartnerId;
		int  	myTranType;
		int  	myFreq;
		boolean	isCredit;
		byte[] 	myDesc;
		byte[] 	myAmount;
		Date  	myDate;
		
		/* Get the various fields */
		myAccountId = theTableDef.getIntegerValue(Pattern.VFIELD_ACCOUNT);
		myDate 		= theTableDef.getDateValue(Pattern.FIELD_DATE);
		myDesc    	= theTableDef.getBinaryValue(Pattern.FIELD_DESC);
		myAmount    = theTableDef.getBinaryValue(Pattern.FIELD_AMOUNT);
		myPartnerId = theTableDef.getIntegerValue(Pattern.VFIELD_PARTNER);
		myTranType  = theTableDef.getIntegerValue(Pattern.FIELD_TRNTYP);
		isCredit    = theTableDef.getBooleanValue(Pattern.FIELD_ISCREDIT);
		myFreq  	= theTableDef.getIntegerValue(Pattern.FIELD_FREQ);
	
		/* Add into the list */
		theList.addItem(pId,
						pControlId,
		           	    myDate,
			            myDesc,
			            myAmount,
			            myAccountId, 
			            myPartnerId,
			            myTranType,
			            myFreq,
			            isCredit);
		
		/* Return to caller */
		return;
	}
	
	/* Set a field value */
	protected void setFieldValue(Event	pItem, int iField) throws ModelException  {
		Pattern myItem = (Pattern)pItem;
		
		/* Switch on field id */
		switch (iField) {
			case Pattern.VFIELD_ACCOUNT:	theTableDef.setIntegerValue(Pattern.VFIELD_ACCOUNT, myItem.getAccount().getId());	break;
			case Pattern.FIELD_DATE:		theTableDef.setDateValue(Pattern.FIELD_DATE, pItem.getDate());						break;
			case Pattern.FIELD_DESC:		theTableDef.setBinaryValue(Pattern.FIELD_DESC, pItem.getDescBytes());				break;
			case Pattern.FIELD_AMOUNT:		theTableDef.setBinaryValue(Pattern.FIELD_AMOUNT, pItem.getAmountBytes());			break;
			case Pattern.VFIELD_PARTNER:	theTableDef.setIntegerValue(Pattern.VFIELD_PARTNER, myItem.getPartner().getId());	break;
			case Pattern.FIELD_TRNTYP:		theTableDef.setIntegerValue(Pattern.FIELD_TRNTYP, pItem.getTransType().getId());	break;
			case Pattern.FIELD_ISCREDIT:	theTableDef.setBooleanValue(Pattern.FIELD_ISCREDIT, myItem.isCredit());				break;
			case Pattern.FIELD_FREQ:		theTableDef.setIntegerValue(Pattern.FIELD_FREQ, myItem.getFrequency().getId());		break;
			default:						super.setFieldValue(pItem, iField);													break;
		}
	}
}
