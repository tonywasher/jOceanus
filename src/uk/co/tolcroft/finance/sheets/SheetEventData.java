package uk.co.tolcroft.finance.sheets;

import uk.co.tolcroft.finance.data.EventData;
import uk.co.tolcroft.finance.data.FinanceData;
import uk.co.tolcroft.models.sheets.SheetDataItem;
import uk.co.tolcroft.models.sheets.SpreadSheet.SheetType;

public class SheetEventData extends SheetDataItem<EventData> {
	/**
	 * NamedArea for Events
	 */
	private static final String EventDataName	= EventData.listName;
	
	/**
	 * Is the spreadsheet a backup spreadsheet or an edit-able one
	 */
	private boolean 		isBackup	= false;
	
	/**
	 * Events data list
	 */
	private EventData.List 	theList		= null;

	/**
	 * Constructor for loading a spreadsheet
	 * @param pReader the spreadsheet reader
	 */
	protected SheetEventData(FinanceReader	pReader) {
		/* Call super constructor */
		super(pReader, EventDataName);
		
		/* Note whether this is a backup */
		isBackup = (pReader.getType() == SheetType.BACKUP);
		
		/* Access the Lists */
		FinanceData myData = pReader.getData();
		theList 	= myData.getEventData();
	}

	/**
	 *  Constructor for creating a spreadsheet
	 *  @param pWriter the spreadsheet writer
	 */
	protected SheetEventData(FinanceWriter	pWriter) {
		/* Call super constructor */
		super(pWriter, EventDataName);
		
		/* Note whether this is a backup */
		isBackup = (pWriter.getType() == SheetType.BACKUP);
				
		/* Access the Events list */
		theList = pWriter.getData().getEventData();
		setDataList(theList);		
	}
	
	/**
	 * Load an item from the spreadsheet
	 */
	protected void loadItem() throws Throwable {

		/* If this is a backup load */
		if (isBackup) {
			/* Access the IDs */
			int	myID 		= loadInteger(0);
			int myControlId	= loadInteger(1);
			int	myInfoId	= loadInteger(2);
			int	myEventId	= loadInteger(3);
		
			/* Access the binary values  */
			byte[] 	myValue		= loadBytes(4);
		
			/* Load the item */
			theList.addItem(myID, myControlId, myInfoId, myEventId, myValue);
		}
		
		/* else this is a load from an edit-able spreadsheet */
		else {
			/* Access the Account */
			//int	   myID 		= loadInteger(0);
				
			/* Load the item */
			//theList.addItem(myID, myDate, myDesc, myAmount, myDebit, myCredit, myUnits, myTransType, myTaxCredit, myDilution, myYears);
		}
	}

	/**
	 *  Insert a item into the spreadsheet
	 *  @param pItem the Item to insert
	 *  @param isBackup is the spreadsheet a backup, or else clear text
	 */
	protected void insertItem(EventData	pItem) throws Throwable  {
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the fields */
			writeInteger(0, pItem.getId());
			writeInteger(1, pItem.getControlKey().getId());				
			writeInteger(2, pItem.getInfoType().getId());				
			writeInteger(3, pItem.getEvent().getId());				
			writeBytes(4, pItem.getData());
		}

		/* else we are creating an edit-able spreadsheet */
		else {
			/* Set the fields */
			//writeInteger(0, pItem.getId());
		}
	}

	/**
	 * PreProcess on write
	 */
	protected boolean preProcessOnWrite() throws Throwable {		
		/* Ignore if we are creating a backup */
		if (isBackup) return false;

		/* Write titles */
		//writeString(0, Event.fieldName(Event.FIELD_ID));
		return true;
	}	

	/**
	 * PostProcess on write
	 */
	protected void postProcessOnWrite() throws Throwable {		
		/* If we are creating a backup */
		if (isBackup) {
			/* Set the five columns as the range */
			nameRange(5);
		}

		/* else this is an edit-able spreadsheet */
		else {
			/* Set the four columns as the range */
			//nameRange(4);

			/* Hide the ID column */
			//setHiddenColumn(0);
		}
	}
}