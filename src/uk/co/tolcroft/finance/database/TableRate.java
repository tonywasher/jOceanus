package uk.co.tolcroft.finance.database;

import java.util.Date;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;
import uk.co.tolcroft.models.database.TableEncrypted;

public class TableRate extends TableEncrypted<AcctRate> {
	/**
	 * The name of the Rates table
	 */
	protected final static String TableName 	= AcctRate.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition theTableDef ;	/* Set during load */

	/**
	 * The rate list
	 */
	private AcctRate.List	theList 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableRate(Database<?>	pDatabase) {
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
		myActCol = theTableDef.addReferenceColumn(AcctRate.FIELD_ACCOUNT, AcctRate.fieldName(AcctRate.FIELD_ACCOUNT), TableAccount.TableName);
		theTableDef.addEncryptedColumn(AcctRate.FIELD_RATE, AcctRate.fieldName(AcctRate.FIELD_RATE), EncryptedItem.RATELEN);
		theTableDef.addNullEncryptedColumn(AcctRate.FIELD_BONUS, AcctRate.fieldName(AcctRate.FIELD_BONUS), EncryptedItem.RATELEN);
		myDateCol = theTableDef.addNullDateColumn(AcctRate.FIELD_ENDDATE, AcctRate.fieldName(AcctRate.FIELD_ENDDATE));
		
		/* Declare Sort Columns */
		myDateCol.setSortOrder(SortOrder.ASCENDING);
		myActCol.setSortOrder(SortOrder.ASCENDING);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getRates();
		setList(theList);
	}

	/* Load the rate */
	protected void loadItem(int pId, int pControlId) throws Exception {
		int	  	myAccountId;
		byte[]	myRate;
		byte[] 	myBonus;
		Date  	myEndDate;
		
		/* Get the various fields */
		myAccountId = theTableDef.getIntegerValue(AcctRate.FIELD_ACCOUNT);
		myRate 		= theTableDef.getBinaryValue(AcctRate.FIELD_RATE);
		myBonus     = theTableDef.getBinaryValue(AcctRate.FIELD_BONUS);
		myEndDate  	= theTableDef.getDateValue(AcctRate.FIELD_ENDDATE);
	
		/* Add into the list */
		theList.addItem(pId,
						pControlId,
			            myAccountId, 
			            myRate,
			            myEndDate, 
			            myBonus);
		
		/* Return to caller */
		return;
	}
	
	/* Set a field value */
	protected void setFieldValue(AcctRate	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case AcctRate.FIELD_ACCOUNT:	theTableDef.setIntegerValue(iField, pItem.getAccount().getId());	break;
			case AcctRate.FIELD_RATE:		theTableDef.setBinaryValue(iField, pItem.getRateBytes());			break;
			case AcctRate.FIELD_BONUS:		theTableDef.setBinaryValue(iField, pItem.getBonusBytes());			break;
			case AcctRate.FIELD_ENDDATE:	theTableDef.setDateValue(iField, pItem.getEndDate());				break;
			default:						super.setFieldValue(pItem, iField);									break;
		}
	}	
}
