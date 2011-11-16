package uk.co.tolcroft.finance.database;

import uk.co.tolcroft.finance.data.EventValue;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.database.Database;
import uk.co.tolcroft.models.database.DatabaseTable;
import uk.co.tolcroft.models.database.TableDefinition;
import uk.co.tolcroft.models.database.TableDefinition.ColumnDefinition;
import uk.co.tolcroft.models.database.TableDefinition.SortOrder;

public class TableEventValues extends DatabaseTable<EventValue> {
	/**
	 * The name of the EventValues table
	 */
	protected final static String TableName 	= EventValue.listName;

	/**
	 * The table definition
	 */
	private TableDefinition theTableDef;	/* Set during load */

	/**
	 * The EventValues list
	 */
	private EventValue.List	theList 			= null;

	/**
	 * Constructor
	 * @param pDatabase the database control
	 */
	protected TableEventValues(Database<?>	pDatabase) {
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
		theTableDef.addReferenceColumn(EventValue.FIELD_INFOTYPE, EventValue.fieldName(EventValue.FIELD_INFOTYPE), TableEventInfoType.TableName);
		myEvtCol = theTableDef.addReferenceColumn(EventValue.FIELD_EVENT, EventValue.fieldName(EventValue.FIELD_EVENT), TableEvent.TableName);
		theTableDef.addIntegerColumn(EventValue.FIELD_VALUE, EventValue.fieldName(EventValue.FIELD_VALUE));
		
		/* Declare the sort order */
		myEvtCol.setSortOrder(SortOrder.ASCENDING);
	}
	
	/* Declare DataSet */
	protected void declareData(DataSet<?> pData) {
		FinanceData myData = (FinanceData)pData;
		theList = myData.getEventValues();
		setList(theList);
	}

	/* Load the tax year */
	public void loadItem(int pId) throws Exception {
		int		  		myInfoType;
		int		  		myEvent;
		int				myValue;
		
		/* Get the various fields */
		myInfoType  = theTableDef.getIntegerValue(EventValue.FIELD_INFOTYPE);
		myEvent     = theTableDef.getIntegerValue(EventValue.FIELD_EVENT);
		myValue		= theTableDef.getIntegerValue(EventValue.FIELD_VALUE);
	
		/* Add into the list */
		theList.addItem(pId, myInfoType, myEvent, myValue); 
	}
	
	/* Set a field value */
	protected void setFieldValue(EventValue	pItem, int iField) throws Exception  {
		/* Switch on field id */
		switch (iField) {
			case EventValue.FIELD_INFOTYPE:	theTableDef.setIntegerValue(iField, pItem.getInfoType().getId());	break;
			case EventValue.FIELD_EVENT:	theTableDef.setIntegerValue(iField, pItem.getEvent().getId());		break;
			case EventValue.FIELD_VALUE:	theTableDef.setIntegerValue(iField, pItem.getValue());				break;
			default:						super.setFieldValue(pItem, iField);									break;
		}
	}

}
