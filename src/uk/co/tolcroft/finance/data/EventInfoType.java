package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.finance.data.StaticClass.EventInfoClass;
import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.StaticData;

public class EventInfoType extends StaticData<EventInfoType, EventInfoClass> {
	/**
	 * The name of the object
	 */
	public static final String objName = "EventInfoType";

	/**
	 * The name of the object
	 */
	public static final String listName = objName + "s";

	/**
	 * Return the EventInfo class of the EventInfoType
	 * @return the class
	 */
	public EventInfoClass getInfoClass()         { return super.getStaticClass(); }
	
	/* Linking methods */
	public EventInfoType getBase() { return (EventInfoType)super.getBase(); }

	/* Override the isActive method */
	public boolean isActive() { return true; }

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Construct a copy of a InfoType.
	 * @param pList	The list to associate the EventInfoType with
	 * @param pType The InfoType to copy 
	 */
	protected EventInfoType(List  			pList,
			            	EventInfoType	pType) { 
		super(pList, pType);
	}
	
	/**
	 * Construct a standard InfoType on load
	 * @param pList	The list to associate the EventInfoType with
	 * @param sName Name of InfoType
	 */
	private EventInfoType(List 	 pList,
			          	  String sName) throws ModelException {
		super(pList, sName);
	}
	
	/**
	 * Construct an InfoType on load
	 * @param pList	The list to associate the InfoType with
	 * @param uId the id of the new item
	 * @param isEnabled is the type enabled
	 * @param uOrder the sort order
	 * @param pName Name of InfoType
	 * @param pDesc Description of InfoType
	 */
	private EventInfoType(List 		pList,
					  	  int		uId,
					  	  boolean	isEnabled,
					  	  int		uOrder, 
					  	  String	pName,
					  	  String	pDesc) throws ModelException {
		super(pList, uId, isEnabled, uOrder , pName, pDesc);
	}
	
	/**
	 * Construct a standard InfoType on load
	 * @param pList	The list to associate the InfoType with
	 * @param uId   ID of InfoType
	 * @param uControlId the control id of the new item
	 * @param isEnabled is the InfoType enabled
	 * @param uOrder the sort order
	 * @param pName Encrypted Name of InfoType
	 * @param pDesc Encrypted Description of InfoType
	 */
	private EventInfoType(List 		pList,
			      	  	  int		uId,
			      	  	  int		uControlId,
			      	  	  boolean	isEnabled,
			      	  	  int		uOrder, 
			      	  	  byte[]	pName,
			      	  	  byte[]	pDesc) throws ModelException {
		super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
	}

	/**
	 * Represents a list of {@link TaxRegime} objects. 
	 */
	public static class List  extends StaticList<List, EventInfoType, EventInfoClass> {
		protected Class<EventInfoClass> getEnumClass() { return EventInfoClass.class; }
		
	 	/** 
	 	 * Construct an empty CORE eventInfo list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, EventInfoType.class, pData, ListStyle.CORE); }

		/**
		 * Constructor for a cloned List
		 * @param pSource the source List
		 */
		private List(List pSource) { 
			super(pSource);
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
		public List getEditList() 	{ return getExtractList(ListStyle.EDIT); }
		public List getShallowCopy() 	{ return getExtractList(ListStyle.COPY); }
		public List getDeepCopy(DataSet<?> pDataSet)	{ 
			/* Build an empty Extract List */
			List myList = new List(this);
			myList.setData(pDataSet);
			
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
		 * Add a new item to the list
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public EventInfoType addNewItem(DataItem<?> pItem) {
			EventInfoType myType = new EventInfoType(this, (EventInfoType)pItem);
			add(myType);
			return myType;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @return the newly added item
		 */
		public EventInfoType addNewItem() { return null; }
			
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }
				
		/**
		 * Add an InfoType
		 * @param pType the Name of the InfoType
		 */ 
		public void addItem(String pType) throws ModelException {
			EventInfoType	myType;
			
			/* Create a new InfoType */
			myType = new EventInfoType(this, pType);
				
			/* Check that this InfoId has not been previously added */
			if (!isIdUnique(myType.getId())) 
				throw new ModelException(ExceptionClass.DATA,
	  					  			myType,
			  			            "Duplicate EventInfoTypeId");
				 
			/* Check that this InfoType has not been previously added */
			if (searchFor(pType) != null) 
				throw new ModelException(ExceptionClass.DATA,
	  					  			myType,
			                        "Duplicate EventInfoType");
								
			/* Add the Type to the list */
			add(myType);		
		}			

		/**
		 * Add an InfoType to the list
		 * @param uId the id of the new item
		 * @param isEnabled is the type enabled
		 * @param uOrder the sort order
		 * @param pInfoType the Name of the InfoType
		 * @param pDesc the Description of the InfoType
		 */ 
		public void addItem(int	   	uId,
							boolean	isEnabled,
							int		uOrder,
				            String 	pInfoType,
				            String 	pDesc) throws ModelException {
			EventInfoType myType;
				
			/* Create a new InfoType */
			myType = new EventInfoType(this, uId, isEnabled, uOrder, pInfoType, pDesc);
				
			/* Check that this InfoId has not been previously added */
			if (!isIdUnique(myType.getId())) 
				throw new ModelException(ExceptionClass.DATA,
	  					  			myType,
			  			            "Duplicate EventInfoTypeId");
				 
			/* Add the InfoType to the list */
			add(myType);
				
			/* Validate the TaxRegime */
			myType.validate();

			/* Handle validation failure */
			if (myType.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myType,
									"Failed validation");
		}	

		/**
		 * Add an InfoType
		 * @param uId the Id of the InfoType
		 * @param uControlId the control id of the new item
		 * @param isEnabled is the regime enabled
		 * @param uOrder the sort order
		 * @param pInfoType the Encrypted Name of the InfoType
		 * @param pDesc the Encrypted Description of the InfoType
		 */ 
		public void addItem(int    	uId,
							int	   	uControlId,
							boolean	isEnabled,
							int		uOrder,
				            byte[] 	pInfoType,
				            byte[] 	pDesc) throws ModelException {
			EventInfoType	myType;
			
			/* Create a new InfoType */
			myType = new EventInfoType(this, uId, uControlId, isEnabled, uOrder, pInfoType, pDesc);
				
			/* Check that this TaxRegimeId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new ModelException(ExceptionClass.DATA,
	  					  			myType,
			  			            "Duplicate EventInfoTypeId");
				 
			/* Add the InfoType to the list */
			add(myType);		
				
			/* Validate the InfoType */
			myType.validate();

			/* Handle validation failure */
			if (myType.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myType,
									"Failed validation");
		}			
	}
}
