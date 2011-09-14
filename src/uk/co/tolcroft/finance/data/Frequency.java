package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.ExceptionClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.StaticData;
import uk.co.tolcroft.finance.data.StaticClass.FreqClass;

public class Frequency extends StaticData<Frequency, FreqClass> {
	/**
	 * The name of the object
	 */
	public static final String objName = "Frequency";

	/**
	 * The name of the object
	 */
	public static final String listName = "Frequencies";

	/**
	 * Return the Frequency class of the Frequency
	 * @return the class
	 */
	public FreqClass getFrequency()         { return super.getStaticClass(); }
	
	/* Linking methods */
	public Frequency getBase() { return (Frequency)super.getBase(); }

	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
			
	/**
	 * Construct a copy of a Frequency.
	 * @param pList	The list to associate the Frequency with
	 * @param pFrequency The frequency to copy 
	 */
	protected Frequency(List		pList,
			            Frequency	pFrequency) { 
		super(pList, pFrequency);
	}
	
	/**
	 * Construct a standard Frequency on load
	 * @param pList	The list to associate the Frequency with
	 * @param sName Name of Frequency
	 */
	private Frequency(List 		pList,
			      	  String	sName) throws Exception {
		super(pList, sName);
	}

	/**
	 * Construct a standard frequency on load
	 * @param pList	The list to associate the Frequency with
	 * @param uId   ID of Frequency
	 * @param uClassId the class id of the new item
	 * @param pName Name of Frequency
	 * @param pDesc Description of Frequency
	 */
	private Frequency(List 		pList,
					  int		uId,
			          int		uClassId,
			          String	pName,
			          String	pDesc) throws Exception {
		super(pList, uId, uClassId, pName, pDesc);
	}
	
	/**
	 * Construct a standard Frequency on load
	 * @param pList	The list to associate the Frequency with
	 * @param uId   ID of Frequency
	 * @param uControlId the control id of the new item
	 * @param uClassId the class id of the new item
	 * @param pName Encrypted Name of Frequency
	 * @param pDesc Encrypted Description of TaxRegime
	 */
	private Frequency(List 		pList,
			      	  int		uId,
			      	  int		uControlId,
			      	  int		uClassId,
			      	  byte[]	pName,
			      	  byte[]	pDesc) throws Exception {
		super(pList, uId, uControlId, uClassId, pName, pDesc);
	}

	/**
	 * Represents a list of {@link Frequency} objects. 
	 */
	public static class List  extends StaticList<Frequency, FreqClass> {
		protected Class<FreqClass> getEnumClass() { return FreqClass.class; }

		/** 
	 	 * Construct an empty CORE frequency list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(Frequency.class, pData, ListStyle.CORE); }
		
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
		public List getClonedList() { return getExtractList(ListStyle.CORE); }

		/** 
		 * Construct a difference ControlData list
		 * @param pNew the new ControlData list 
		 * @param pOld the old ControlData list 
		 */
		protected List getDifferences(DataList<Frequency> pOld) { 
			/* Build an empty Difference List */
			List myList = new List(this);
			
			/* Calculate the differences */
			myList.getDifferenceList(this, pOld);
			
			/* Return the list */
			return myList;
		}

		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return listName; }

		/**
		 * Add a new item to the list
		 * @param pItem item to be added
		 * @return the newly added item
		 */
		public Frequency addNewItem(DataItem<?> pItem) {
			Frequency myFreq = new Frequency(this, (Frequency)pItem);
			add(myFreq);
			return myFreq;
		}
	
		/**
		 * Create a new empty element in the edit list (null-operation)
		 * @return the newly added item
		 */
		public Frequency addNewItem() { return null; }			
		
		/**
		 * Add a Frequency
		 * @param pFrequency the Name of the frequency
		 * @throws Exception on error
		 */ 
		public void addItem(String 	pFrequency) throws Exception {
			Frequency myFrequency;
			
			/* Create a new Frequency */
			myFrequency = new Frequency(this, pFrequency);
				
			/* Check that this Frequency has not been previously added */
			if (searchFor(pFrequency) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate Frequency");
				
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(myFrequency.getStaticClassId()) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate FrequencyClass");
				
			/* Add the Frequency to the list */
			add(myFrequency);		
		}
		
		/**
		 * Add a Frequency to the list
		 * @param uId   ID of Frequency
		 * @param uClassId the ClassId of the frequency
		 * @param pFrequency the Name of the frequency
		 * @param pDesc the Description of the frequency
		 */ 
		public void addItem(int	   uId,
							int	   uClassId,
				            String pFrequency,
				            String pDesc) throws Exception {
			Frequency myFreq;
				
			/* Create a new Frequency */
			myFreq = new Frequency(this, uId, uClassId, pFrequency, pDesc);
				
			/* Check that this FrequencyId has not been previously added */
			if (!isIdUnique(myFreq.getId())) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFreq,
			  			            "Duplicate FrequencyId");
				 
			/* Check that this Frequency has not been previously added */
			if (searchFor(myFreq.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	   					  			myFreq,
			  			            "Duplicate Frequency");
				 
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(uClassId) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFreq,
			                        "Duplicate FrequencyClass");
				
			/* Add the Frequency to the list */
			add(myFreq);
		}	

		/**
		 * Add a Frequency
		 * @param uId the Id of the frequency
		 * @param uControlId the control id of the new item
		 * @param uClassId the ClassId of the frequency
		 * @param pFrequency the Encrypted Name of the frequency
		 * @param pDesc the Encrypted Description of the frequency
		 * @throws Exception on error
		 */ 
		public void addItem(int		uId,
							int		uControlId,
							int		uClassId,
				            byte[] 	pFrequency,
				            byte[]	pDesc) throws Exception {
			Frequency myFrequency;
			
			/* Create a new Frequency */
			myFrequency = new Frequency(this, uId, uControlId, uClassId, pFrequency, pDesc);
				
			/* Check that this FrequencyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			  			            "Duplicate FrequencyId");
				 
			/* Check that this Frequency has not been previously added */
			if (searchFor(myFrequency.getName()) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate Frequency");
				
			/* Check that this ClassId has not been previously added */
			if (searchForEnum(uClassId) != null) 
				throw new Exception(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate FrequencyClass");
				
			/* Add the Frequency to the list */
			add(myFrequency);		
		}		
	}
}
