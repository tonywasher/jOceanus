package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.EventData;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.EncryptedItem;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;
import uk.co.tolcroft.models.database.TableEncrypted;

public class TableEventData extends TableEncrypted<EventData> {
	/**
	 * The name of the EventData table
	 */
	protected final static String TableName 	= EventData.listName;
				
	/**
	 * The table definition
	 */
	private TableDefinition theTableDef;	/* Set during load */

	/**
	 * The event data list
	 */
	private EventData.List	theList 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableEventData(Database<?> 	pDatabase) {
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
		
		/* Define sort column variable */
		ColumnDefinition myEvtCol;
		
		/* Declare the columns */
		theTableDef.addReferenceColumn(EventData.FIELD_INFOTYPE, EventData.fieldName(EventData.FIELD_INFOTYPE), TableEventInfoType.TableName);
		myEvtCol = theTableDef.addReferenceColumn(EventData.FIELD_EVENT, EventData.fieldName(EventData.FIELD_EVENT), TableEvent.TableName);
		theTableDef.addEncryptedColumn(EventData.FIELD_VALUE, EventData.fieldName(EventData.FIELD_VALUE), EncryptedItem.MONEYLEN);
		
		/* Declare the sort order */
		myEvtCol.setSortOrder(SortOrder.ASCENDING);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getEventData();
		setList(theList);
	}

	/* Load the event */
	protected void loadItem(int pId, int pControlId) throws Exception {
		int  			myInfoTypId;
		int  			myEventId;
		byte[] 			myValue;
		
		/* Get the various fields */
		myInfoTypId = theTableDef.getIntegerValue(EventData.FIELD_INFOTYPE);
		myEventId 	= theTableDef.getIntegerValue(EventData.FIELD_EVENT);
		myValue 	= theTableDef.getBinaryValue(EventData.FIELD_VALUE);
	
		/* Add into the list */
		theList.addItem(pId,
						pControlId,
			       	   	myInfoTypId,
				        myEventId,
				        myValue);
	}
	
	/* Set a field value */
	protected void setFieldValue(EventData	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case EventData.FIELD_INFOTYPE:	theTableDef.setIntegerValue(EventData.FIELD_INFOTYPE, pItem.getInfoType().getId());	break;
			case EventData.FIELD_EVENT:		theTableDef.setIntegerValue(EventData.FIELD_EVENT, pItem.getEvent().getId());		break;
			case EventData.FIELD_VALUE:		theTableDef.setBinaryValue(EventData.FIELD_VALUE, pItem.getData());					break;
			default:						super.setFieldValue(pItem, iField);													break;
		}
	}
}