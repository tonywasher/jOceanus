package uk.co.tolcroft.models.data;

import uk.co.tolcroft.models.Difference;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.help.DebugDetail;

public class ControlData extends DataItem<ControlData> {
	/**
	 * The name of the object
	 */
	public static final String objName = "ControlData";

	/**
	 * The name of the object
	 */
	public static final String listName = objName;

	/* Access methods */
	public  int 			getDataVersion()  	{ return getValues().getDataVersion(); }
	public  ControlKey		getControlKey()  	{ return getValues().getControlKey(); }

	/* Linking methods */
	public ControlData	getBase() 		{ return (ControlData)super.getBase(); }
	public Values  		getValues()  	{ return (Values)super.getCurrentValues(); }	
	
	/* Field IDs */
	public static final int FIELD_VERS	   = DataItem.NUMFIELDS;
	public static final int FIELD_CONTROL  = DataItem.NUMFIELDS+1;
	public static final int NUMFIELDS	   = DataItem.NUMFIELDS+2; 

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() {return NUMFIELDS; }
	
	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public static String	fieldName(int iField) {
		switch (iField) {
			case FIELD_VERS:		return "Version";
			case FIELD_CONTROL:		return "Control";
			default:		  		return DataItem.fieldName(iField);
		}
	}
				
	/**
	 * Determine the field name in a non-static fashion 
	 */
	public String getFieldName(int iField) { return fieldName(iField); }
	
	/**
	 * Format the value of a particular field as a table row
	 * @param pDetail the debug detail
	 * @param iField the field number
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<ControlData> pValues) {
		Values myValues = (Values)pValues;
		String 	myString = "";
		switch (iField) {
			case FIELD_VERS:
				myString += myValues.getDataVersion(); 
				break;
			case FIELD_CONTROL:
				myString = "Id=" + myValues.getControlId();					
				if (myValues.getControlKey() != null)
					myString = pDetail.addDebugLink(myValues.getControlKey(), myString);
				break;
			default: 		
				myString += super.formatField(pDetail, iField, pValues); 
				break;
		}
		return myString;
	}
							
	/**
	 * Get an initial set of values 
	 * @return an initial set of values 
	 */
	protected HistoryValues<ControlData> getNewValues() { return new Values(); }
	
	/**
 	 * Construct a copy of a ControlData 
 	 * @param pSource The source 
 	 */
	protected ControlData(List pList, ControlData pSource) {
		/* Set standard values */
		super(pList, pSource.getId());
		Values myValues = getValues();
		myValues.copyFrom(pSource.getValues());

		/* Switch on the LinkStyle */
		switch (pList.getStyle()) {
			case CLONE:
				isolateCopy(pList.getData());
			case CORE:
			case COPY:
				pList.setNewId(this);				
				break;
			case EDIT:
				setBase(pSource);
				setState(DataState.CLEAN);
				break;
			case UPDATE:
				setBase(pSource);
				setState(pSource.getState());
				break;
		}
	}

	/* Standard constructor */
	private ControlData(List pList,
				  	    int	 uId,
				  	    int  uVersion, 
				  	    int	 uControlId) throws Exception {
		/* Initialise the item */
		super(pList, uId);
		Values myValues = getValues();

		/* Record the values */
		myValues.setDataVersion(uVersion);
		myValues.setControlId(uControlId);
		
		/* Look up the ControlKey */
		ControlKey myControl = pList.theData.getControlKeys().searchFor(uControlId);
		if (myControl == null) 
			throw new Exception(ExceptionClass.DATA,
		                        this,
					            "Invalid ControlKey Id");
		myValues.setControlKey(myControl);

		/* Allocate the id */
		pList.setNewId(this);				
	}

	/* Limited (no security) constructor */
	private ControlData(List pList,
						int	 uId,
				  	    int	 uVersion) {
		/* Initialise the item */
		super(pList, uId);
		Values myValues = getValues();

		/* Record the values */
		myValues.setDataVersion(uVersion);
				
		/* Allocate the id */
		pList.setNewId(this);				
	}

	/**
	 * Compare this controlData to another to establish equality.
	 * 
	 * @param pThat The controlData to compare to
	 * @return <code>true</code> if the static is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a ControlData */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a ControlData */
		ControlData myThat = (ControlData)pThat;
		
		/* Check for equality on id */
		if (getId() != myThat.getId())	return false;
		
		/* Compare the changeable values */
		return getValues().histEquals(myThat.getValues()).isIdentical();
	}

	/**
	 * Compare this controlData to another to establish sort order. 
	 * @param pThat The controlData to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		int iDiff;
		
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a ControlData */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a ControlData */
		ControlData myThat = (ControlData)pThat;

		/* Compare the Versions */
		iDiff =(int)(getDataVersion() - myThat.getDataVersion());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;

		/* Compare the IDs */
		iDiff =(int)(getId() - myThat.getId());
		if (iDiff < 0) return -1;
		if (iDiff > 0) return 1;
		return 0;
	}

	/**
	 * Isolate Data Copy
	 * @param pData the DataSet
	 */
	private void isolateCopy(DataSet<?> pData) {
		ControlKey.List myKeys = pData.getControlKeys();
		
		/* Update to use the local copy of the ControlKeys */
		Values 		myValues   	= getValues();
		ControlKey 	myKey 		= myValues.getControlKey();
		ControlKey 	myNewKey 	= myKeys.searchFor(myKey.getId());
		myValues.setControlKey(myNewKey);
	}

	/**
	 * Set a new ControlKey 
	 * @param pControl the new control key 
	 */
	protected void setControlKey(ControlKey pControl) throws Exception {
		/* If we do not have a control Key */
		Values myValues = getValues();

		if (myValues.getControlId() == -1) {
			/* Store the control details and return */
			myValues.setControlKey(pControl);
			return;
		}
		
		/* Store the current detail into history */
		pushHistory();

		/* Store the control details */
		myValues.setControlKey(pControl);

		/* Check for changes */
		if (checkForHistory()) setState(DataState.CHANGED);
	}
	
	/**
	 * Static List
	 */
	public static class List  extends DataList<List, ControlData> {
		/* Members */
		private DataSet<?>		theData			= null;
		public 	DataSet<?> 		getData()		{ return theData; }
		private ControlData		theControl		= null;
		public 	ControlData		getControl()	{ return theControl; }
		
		/** 
		 * Construct an empty CORE static list
	 	 * @param pData the DataSet for the list
		 */
		protected List(DataSet<?> pData) { 
			super(List.class, ControlData.class, ListStyle.CORE, false);
			theData = pData;
		}

		/** 
		 * Construct an empty generic ControlData list
	 	 * @param pData the DataSet for the list
		 * @param pStyle the style of the list 
		 */
		protected List(DataSet<?> pData, ListStyle pStyle) {
			super(List.class, ControlData.class, pStyle, false);
			theData = pData;
		}

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
			theData = pSource.theData;
		}
		
		/**
		 * Construct an update extract for the List.
		 * @return the update Extract
		 */
		private List getExtractList(ListStyle pStyle) {
			/* Build an empty Extract List */
			List myList = new List(this);
			
			/* Obtain underlying updates */
			myList.populateList(pStyle);
			
			/* Return the list */
			return myList;
		}

		/* Obtain extract lists. */
		public List getUpdateList() { return getExtractList(ListStyle.UPDATE); }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.theData = pDataSet;
			
			/* Obtain underlying clones */
			myList.populateList(ListStyle.CLONE);
			myList.setStyle(ListStyle.CORE);
			
			/* Return the list */
			return myList;
		}

		/** 
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
		 */
		protected List getDifferences(List pOld) { 
			/* Build an empty Difference List */
			List myList = new List(this);
			
			/* Calculate the differences */
			myList.getDifferenceList(this, pOld);
			
			/* Return the list */
			return myList;
		}

		/**
		 * Add a new item to the core list
		 * @param pItem item
		 * @return the newly added item
		 */
		public ControlData addNewItem(DataItem<?> pItem) { 
			ControlData myControl = new ControlData(this, (ControlData)pItem);
			add(myControl);
			theControl = myControl;
			return myControl; 
		}

		/**
		 * Add a new item to the edit list
		 * @return the newly added item
		 */
		public ControlData addNewItem() { return null; }

		/**
		 * 	Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 *  Add a ControlData item
		 */
		public void addItem(int  	uId,
							int  	uVersion,
							int		uControlId) throws Exception {
			ControlData     	myControl;
			
			/* Create the ControlData */
			myControl = new ControlData(this, 
								  		uId, 
								  		uVersion,
								  		uControlId);
			
			/* Check that this ControlId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
									myControl,
									"Duplicate ControlId (" + uId + ")");
			 
			/* Only one static is allowed */
			if (theControl != null) 
				throw new Exception(ExceptionClass.DATA,
									myControl,
									"Control record already exists");
			 
			/* Add to the list */
			theControl = myControl;
			add(myControl);
		}			

		/**
		 *  Add a ControlData item (with no security as yet)
		 */
		public void addItem(int		uId,
							int  	uVersion) throws Exception {
			ControlData     	myControl;
			
			/* Create the ControlData */
			myControl = new ControlData(this,  uId, uVersion);
			
			/* Only one static is allowed */
			if (theControl != null) 
				throw new Exception(ExceptionClass.DATA,
									myControl,
									"Control record already exists");
			 
			/* Add to the list */
			theControl = myControl;
			add(myControl);
		}			
	}

	/**
	 * Values for a controlData 
	 */
	public class Values extends HistoryValues<ControlData> {
		private int 			theDataVersion	= -1;
		private ControlKey		theControlKey	= null;
		private int 			theControlId 	= -1;
		
		/* Access methods */
		public  int 			getDataVersion()  	{ return theDataVersion; }
		public  ControlKey		getControlKey()  	{ return theControlKey; }
		public  int				getControlId()  	{ return theControlId; }
		
		private void setDataVersion(int pValue) {
			theDataVersion = pValue; }
		private void setControlKey(ControlKey pValue) {
			theControlKey  = pValue;
			theControlId = (theControlKey == null) ? -1 : theControlKey.getId();
		}
		private void setControlId(int pValue) {
			theControlId		= pValue; }

		/* Constructor */
		public Values() {}
		public Values(Values pValues) { copyFrom(pValues); }
		
		/* Check whether this object is equal to that passed */
		public Difference histEquals(HistoryValues<ControlData> pCompare) {
			/* Make sure that the object is the same class */
			if (pCompare.getClass() != this.getClass()) return Difference.Different;
			
			/* Cast correctly */
			Values myValues = (Values)pCompare;

			/* Test differences */
			if (theDataVersion != myValues.theDataVersion)  return Difference.Different;
			return ControlKey.differs(theControlKey, myValues.theControlKey);
		}
		
		/* Copy values */
		public HistoryValues<ControlData> copySelf() {
			return new Values(this);
		}
		public void    copyFrom(HistoryValues<?> pSource) {
			Values myValues = (Values)pSource;
			theDataVersion	= myValues.getDataVersion();
			theControlKey	= myValues.getControlKey();
			theControlId 	= myValues.getControlId();
		}
		public Difference	fieldChanged(int fieldNo, HistoryValues<ControlData> pOriginal) {
			Values 	pValues = (Values)pOriginal;
			Difference	bResult = Difference.Identical;
			switch (fieldNo) {
				case FIELD_VERS:
					bResult = (theDataVersion != pValues.theDataVersion) ? Difference.Different
							 											 : Difference.Identical;
					break;
				case FIELD_CONTROL:
					bResult = (ControlKey.differs(theControlKey,		pValues.theControlKey));
					break;
			}
			return bResult;
		}
	}	
}
