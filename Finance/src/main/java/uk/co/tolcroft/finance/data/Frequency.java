package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.ModelException;
import uk.co.tolcroft.models.ModelException.ExceptionClass;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataSet;
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
			      	  String	sName) throws ModelException {
		super(pList, sName);
	}

	/**
	 * Construct a standard frequency on load
	 * @param pList	The list to associate the Frequency with
	 * @param uId   ID of Frequency
	 * @param isEnabled is the frequency enabled
	 * @param uOrder the sort order
	 * @param pName Name of Frequency
	 * @param pDesc Description of Frequency
	 */
	private Frequency(List 		pList,
					  int		uId,
			          boolean	isEnabled,
			          int		uOrder,
			          String	pName,
			          String	pDesc) throws ModelException {
		super(pList, uId, isEnabled, uOrder, pName, pDesc);
	}
	
	/**
	 * Construct a standard Frequency on load
	 * @param pList	The list to associate the Frequency with
	 * @param uId   ID of Frequency
	 * @param uControlId the control id of the new item
	 * @param isEnabled is the frequency enabled
	 * @param uOrder the sort order
	 * @param pName Encrypted Name of Frequency
	 * @param pDesc Encrypted Description of TaxRegime
	 */
	private Frequency(List 		pList,
			      	  int		uId,
			      	  int		uControlId,
			      	  boolean	isEnabled,
			      	  int		uOrder,
			      	  byte[]	pName,
			      	  byte[]	pDesc) throws ModelException {
		super(pList, uId, uControlId, isEnabled, uOrder, pName, pDesc);
	}

	/**
	 * Represents a list of {@link Frequency} objects. 
	 */
	public static class List  extends StaticList<List, Frequency, FreqClass> {
		protected Class<FreqClass> getEnumClass() { return FreqClass.class; }

		/** 
	 	 * Construct an empty CORE frequency list
	 	 * @param pData the DataSet for the list
	 	 */
		protected List(FinanceData pData) { 
			super(List.class, Frequency.class, pData, ListStyle.CORE); }
		
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
		 * @throws ModelException on error
		 */ 
		public void addItem(String 	pFrequency) throws ModelException {
			Frequency myFrequency;
			
			/* Create a new Frequency */
			myFrequency = new Frequency(this, pFrequency);
				
			/* Check that this FrequencyId has not been previously added */
			if (!isIdUnique(myFrequency.getId())) 
				throw new ModelException(ExceptionClass.DATA,
	  					  			myFrequency,
			  			            "Duplicate FrequencyId");
				 
			/* Check that this Frequency has not been previously added */
			if (searchFor(pFrequency) != null) 
				throw new ModelException(ExceptionClass.DATA,
	  					  			myFrequency,
			                        "Duplicate Frequency");
				
			/* Add the Frequency to the list */
			add(myFrequency);		
		}
		
		/**
		 * Add a Frequency to the list
		 * @param uId   ID of Frequency
		 * @param isEnabled is the frequency enabled
		 * @param uOrder the sort order
		 * @param pFrequency the Name of the frequency
		 * @param pDesc the Description of the frequency
		 */ 
		public void addItem(int	   	uId,
							boolean	isEnabled,
							int		uOrder,
				            String 	pFrequency,
				            String 	pDesc) throws ModelException {
			Frequency myFreq;
				
			/* Create a new Frequency */
			myFreq = new Frequency(this, uId, isEnabled, uOrder, pFrequency, pDesc);
				
			/* Check that this FrequencyId has not been previously added */
			if (!isIdUnique(myFreq.getId())) 
				throw new ModelException(ExceptionClass.DATA,
	  					  			myFreq,
			  			            "Duplicate FrequencyId");
				 
			/* Add the Frequency to the list */
			add(myFreq);
			
			/* Validate the Frequency */
			myFreq.validate();

			/* Handle validation failure */
			if (myFreq.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myFreq,
									"Failed validation");
		}	

		/**
		 * Add a Frequency
		 * @param uId the Id of the frequency
		 * @param uControlId the control id of the new item
		 * @param isEnabled is the frequency enabled
		 * @param uOrder the sort order
		 * @param pFrequency the Encrypted Name of the frequency
		 * @param pDesc the Encrypted Description of the frequency
		 * @throws ModelException on error
		 */ 
		public void addItem(int		uId,
							int		uControlId,
							boolean	isEnabled,
							int		uOrder,
				            byte[] 	pFrequency,
				            byte[]	pDesc) throws ModelException {
			Frequency myFreq;
			
			/* Create a new Frequency */
			myFreq = new Frequency(this, uId, uControlId, isEnabled, uOrder, pFrequency, pDesc);
				
			/* Check that this FrequencyId has not been previously added */
			if (!isIdUnique(uId)) 
				throw new ModelException(ExceptionClass.DATA,
	  					  			myFreq,
			  			            "Duplicate FrequencyId");
				 
			/* Add the Frequency to the list */
			add(myFreq);		
				
			/* Validate the Frequency */
			myFreq.validate();

			/* Handle validation failure */
			if (myFreq.hasErrors()) 
				throw new ModelException(ExceptionClass.VALIDATE,
									myFreq,
									"Failed validation");
		}		
	}
}
