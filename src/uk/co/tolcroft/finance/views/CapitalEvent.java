package uk.co.tolcroft.finance.views;

import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.finance.data.StaticClass.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Number.*;
import uk.co.tolcroft.models.data.DataItem;
import uk.co.tolcroft.models.data.DataList;
import uk.co.tolcroft.models.data.DataSet;
import uk.co.tolcroft.models.data.DataState;
import uk.co.tolcroft.models.data.HistoryValues;
import uk.co.tolcroft.models.help.DebugDetail;

public class CapitalEvent extends DataItem<CapitalEvent> {
	/**
	 * The name of the object
	 */
	private static final String objName = "CapitalEvent";

	/**
	 * The attributes
	 */
	public static final String capitalInitialCost	= "CostInitial";
	public static final String capitalDeltaCost		= "CostDelta";
	public static final String capitalFinalCost		= "CostFinal";
	public static final String capitalInitialUnits	= "UnitsInitial";
	public static final String capitalDeltaUnits	= "UnitsDelta";
	public static final String capitalFinalUnits	= "UnitsFinal";
	public static final String capitalInitialGains	= "GainsInitial";
	public static final String capitalDeltaGains	= "GainsDelta";
	public static final String capitalFinalGains	= "GainsFinal";
	public static final String capitalInitialGained	= "GainedInitial";
	public static final String capitalDeltaGained	= "GainedDelta";
	public static final String capitalFinalGained	= "GainedFinal";
	public static final String capitalInitialDiv	= "DividendInitial";
	public static final String capitalDeltaDiv		= "DividendDelta";
	public static final String capitalFinalDiv		= "DividendFinal";
	public static final String capitalInitialInvest	= "InvestedInitial";
	public static final String capitalDeltaInvest	= "InvestedDelta";
	public static final String capitalFinalInvest	= "InvestedFinal";
	public static final String capitalInitialValue	= "ValueInitial";
	public static final String capitalFinalValue	= "ValueFinal";
	public static final String capitalInitialPrice	= "PriceInitial";
	public static final String capitalFinalPrice	= "PriceFinal";
	public static final String capitalMarket		= "MarketMovement";
	public static final String capitalTakeoverCost	= "TakeoverCost";
	public static final String capitalTakeoverCash	= "TakeoverCash";
	public static final String capitalTakeoverStock	= "TakeoverStock";
	public static final String capitalTakeoverTotal	= "TakeoverTotal";
	public static final String capitalTakeoverPrice	= "TakeoverPrice";
	public static final String capitalTakeoverValue	= "TakeoverValue";

	/* Members */
	private AttributeList	theAttributes	= null;
	private Date			theDate			= null;
	
	/* Access methods */
	public	Date			getDate()		{ return theDate; }
	
	/* Map the getBase function */
	public Event 			getBase()		{ return (Event)super.getBase(); }
	
	/**
	 * Obtain the type of the item
	 * @return the type of the item
	 */
	public String itemType() { return objName; }
	
	/**
	 * Obtain the number of fields for an item
	 * @return the number of fields
	 */
	public int	numFields() { return 1 + theAttributes.size(); }
	
	/**
	 * Determine the field name for a particular field
	 * @return the field name
	 */
	public static String	fieldName(int iField) {
		/* Handle id of zero */
		if (iField == 0) return "Date";
		
		/* Handle out of range */
		return DataItem.fieldName(iField);
	}
	
	/**
	 * Determine the field name in a non-static fashion 
	 */
	public String getFieldName(int iField) { 
		Attribute myAttr;
		
		/* If we have a valid element */
		if ((iField > 0) && (iField < numFields())) {			
			/* Access the attribute */
			myAttr = theAttributes.get(iField-1);
			return myAttr.getName();
		}
		
		return fieldName(iField);
	}
	
	/**
	 * Format the value of a particular field as a table row
	 * @param pDetail the debug detail
	 * @param iField the field number
	 * @param pValues the values to use
	 * @return the formatted field
	 */
	public String formatField(DebugDetail pDetail, int iField, HistoryValues<CapitalEvent> pValues) {
		Attribute myAttr;

		/* If we have a valid element */
		if (iField < numFields()) {
			/* Handle id of zero */
			if (iField == 0) return Date.format(theDate);

			/* Access the attribute */
			myAttr = theAttributes.get(iField-1);

			/* format the attribute */
			return myAttr.format();
		}
		
		/* Handle out of range */
		return "";
	}

	/**
	 * Build History (no history)
	 */
	protected void buildHistory() {}

	/**
	 * Constructor
	 * @param pList the list to belong to
	 * @param pEvent the underlying event
	 */
	private CapitalEvent(List 			pList,
						 Event 			pEvent) {
		/* Call super-constructor */
		super(pList, pEvent.getId());
		
		/* Create the attributes list */
		theAttributes = new AttributeList();
		theDate		  = pEvent.getDate();
		
		/* Link to the event */
		setBase(pEvent);
		
		/* Set status */
		setState(DataState.CLEAN);
	}
	
	/**
	 * Constructor
	 * @param pList the list to belong to
	 * @param pEvent the underlying event
	 */
	private CapitalEvent(List 			pList,
						 Date			pDate) {
		/* Call super-constructor */
		super(pList, 0);
		
		/* Create the attributes list */
		theAttributes = new AttributeList();
		theDate		  = pDate;
		
		/* Set status */
		setState(DataState.CLEAN);
	}
	
	/**
	 * Compare this CapitalEvent to another to establish equality.
	 * 
	 * @param pThat The CapitalEvent to compare to
	 * @return <code>true</code> if the event is identical, <code>false</code> otherwise
	 */
	public boolean equals(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return true;
		if (pThat == null) return false;
		
		/* Make sure that the object is a Capital Event */
		if (pThat.getClass() != this.getClass()) return false;
		
		/* Access the object as a CapitalEvent */
		CapitalEvent myEvent = (CapitalEvent)pThat;
		
		/* Compare the dates */
		if (!theDate.equals(myEvent.theDate)) return false;
		
		/* If we have a null base then equal if and only if that has null base */
		if (getBase() == null) return (myEvent.getBase() != null);
		
		/* If we don't have null base then non-equal if that has null base */
		if (myEvent.getBase() == null) return false;

		/* Check for equality */
		return getBase().equals(myEvent.getBase());
	}

	/**
	 * Compare this capital event to another to establish sort order. 
	 * @param pThat The Capital Event to compare to
	 * @return (-1,0,1) depending of whether this object is before, equal, 
	 * 					or after the passed object in the sort order
	 */
	public int compareTo(Object pThat) {
		/* Handle the trivial cases */
		if (this == pThat) return 0;
		if (pThat == null) return -1;
		
		/* Make sure that the object is a CapitalEvent */
		if (pThat.getClass() != this.getClass()) return -1;
		
		/* Access the object as a CapitalEvent */
		CapitalEvent myThat = (CapitalEvent)pThat;
		
		/* Compare the dates */
		int iResult = theDate.compareTo(myThat.theDate);
		if (iResult != 0) return iResult;
		
		/* If we have a null base then we are after non-null and equal to null */
		if (getBase() == null) return (myThat.getBase() == null) ? 0 : 1;
		
		/* If we don't have null base then before any null base */
		if (myThat.getBase() == null) return -1;

		/* Compare the underlying events */
		return getBase().compareTo(myThat.getBase());
	}
	
	/**
	 * Add Money Attribute
	 * @param pName the name of the attribute 
	 * @param pValue the value of the attribute
	 */
	protected void addAttribute(String 	pName,
								Money	pValue) {
		/* Create the attribute and add to the list */
		MoneyAttribute myAttr = new MoneyAttribute(pName, 
												   new Money(pValue));
		theAttributes.add(myAttr);
	}
	

	/**
	 * Add Units Attribute
	 * @param pName the name of the attribute 
	 * @param pValue the value of the attribute
	 */
	protected void addAttribute(String 	pName,
								Units	pValue) {
		/* Create the attribute and add to the list */
		UnitsAttribute myAttr = new UnitsAttribute(pName,
												   new Units(pValue));
		theAttributes.add(myAttr);
	}
	
	/**
	 * Add Price Attribute
	 * @param pName the name of the attribute 
	 * @param pValue the value of the attribute
	 */
	protected void addAttribute(String 	pName,
								Price	pValue) {
		/* Create the attribute and add to the list */
		PriceAttribute myAttr = new PriceAttribute(pName, 
												   new Price(pValue));
		theAttributes.add(myAttr);
	}
	
	/**
	 * Find an attribute
	 * @param pName the name of the attribute
	 * @return the value of the attribute or null
	 */
	public Object findAttribute(String pName) {
		/* Search for the attribute */
		return theAttributes.findAttribute(pName);
	}
	
	/* The List of capital events */
	public static class List extends DataList<List, CapitalEvent> {
		/* Members */
		private FinanceData		theData			= null;
		private Account			theAccount		= null;
	
		/* Access methods */
		public FinanceData		getData()		{ return theData; }

		/** 
	 	 * Construct an empty Capital event list
	 	 * @param pData the DataSet
	 	 * @param pAccount the Account for the list
	 	 */
		protected List(FinanceData	pData,
					   Account		pAccount) { 
			super(List.class, CapitalEvent.class, ListStyle.VIEW, false);
			
			/* Store the data */
			theData			= pData;
			theAccount		= pAccount;
		}

		/* Obtain extract lists. */
		public List getUpdateList() { return null; }
		public List getEditList() 	{ return null; }
		public List getShallowCopy() { return null; }
		public List getDeepCopy(DataSet<?> pData) { return null; }
		public List getDifferences(List pOld) { return null; }

		/**
		 * Add a new item to the list
		 * @param pItem the item to add
		 * @return the newly added item
		 */
		public CapitalEvent addNewItem(DataItem<?> pItem) { return null; }

	
		/**
		 * Add a new item to the edit list
		 */
		public CapitalEvent addNewItem() { return null; }
	
		/**
		 * Obtain the type of the item
		 * @return the type of the item
		 */
		public String itemType() { return objName; }		

		/**
		 * Add an event to the list
		 * @param pEvent the Event to add
		 */
		protected CapitalEvent addEvent(Event 	pEvent) {
			CapitalEvent myEvent;
			
			/* Create the Capital Event and add to list */
			myEvent = new CapitalEvent(this, pEvent);
			add(myEvent);
			
			/* return the new event */
			return myEvent;
		}

		/**
		 * Add a date event to the list
		 * @param pDate the Date for the event
		 */
		protected CapitalEvent addEvent(Date 	pDate) {
			CapitalEvent myEvent;
			
			/* Create the Capital Event and add to list */
			myEvent = new CapitalEvent(this, pDate);
			add(myEvent);
			
			/* return the new event */
			return myEvent;
		}
		
		/**
		 * Find the cash takeover event (if present)
		 */
		protected CapitalEvent getCashTakeOver() {
			ListIterator myIterator;
			CapitalEvent myEvent;
			
			/* Create the iterator */
			myIterator = listIterator();
			
			/* Access the last element */
			myEvent = myIterator.peekLast();
			
			/* If the element is a cash takeover */
			if ((myEvent != null) &&
				(myEvent.getBase() != null) &&
				(myEvent.getBase().getTransType().getTranClass() == TransClass.CASHTAKEOVER))
				return myEvent;
			
			/* Return no such event */
			return null;
		}

		/**
		 * Purge events after date
		 * @param pDate date from which to purge events
		 */
		protected void purgeAfterDate(Date pDate) {
			ListIterator myIterator;
			CapitalEvent myEvent;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the events */
			while ((myEvent = myIterator.next()) != null) {
				/* If this is past (or on) the date remove it */
				if (pDate.compareTo(myEvent.getDate()) <= 0)
					myIterator.remove();
			}
			
			/* Return */
			return;
		}

		/**
		 * Add additional fields to HTML String
		 * @param pBuffer the string buffer 
		 */
		public void addHTMLFields(StringBuilder pBuffer) {
			/* Start the Fields section */
			pBuffer.append("<tr><th rowspan=\"2\">Fields</th></tr>");
				
			/* Format the account */
			pBuffer.append("<tr><td>Account</td><td>"); 
			pBuffer.append(Account.format(theAccount)); 
			pBuffer.append("</td></tr>"); 
		}
	}
	
	/* Attribute class */
	private abstract class Attribute implements LinkObject<Attribute> {
	    /**
		 * Storage for the List Node
		 */
	    private 	LinkNode<Attribute>		theLink		= null;

		/* Members */
		private String 	theName 	= null;
		private Object 	theValue	= null;
		
		/* Access methods */
		public	String	getName()	{ return theName; }
		public	Object	getValue()	{ return theValue; }

		/**
		 * Get the link node for this item
		 * @return the Link node or <code>null</code>
		 */
		public LinkNode<Attribute>	getLinkNode(SortedList<Attribute> pList)	{ return theLink; }

		/**
		 * Get the link node for this item
		 * @return the Link node or <code>null</code>
		 */
		public void			setLinkNode(SortedList<Attribute> l, LinkNode<Attribute> o)	{ theLink = o; }

		/**
		 * Determine whether the item is visible to standard searches
		 * @return <code>true/false</code>
		 */
		public boolean		isHidden()    	{ return false; }

		/**
		 * Constructor
		 * @param pName the name
		 * @param pValue the value
		 */
		private Attribute(String pName,
						  Object pValue) {
			/* Store the values */
			theName 	= pName;
			theValue 	= pValue;
		}

		/**
		 * Compare this Attribute to another to establish sort order.
		 * 
		 * @param pThat The Attribute to compare to
		 * @return (-1,0,1) depending of whether this object is before, equal, 
		 * 					or after the passed object in the sort order
		 */
		public int compareTo(Object pThat) {
			/* Handle the trivial cases */
			if (this == pThat) return 0;
			if (pThat == null) return -1;
			
			/* Make sure that the object is an Attributer */
			if (pThat.getClass() != this.getClass()) return -1;
			
			/* Access the object as an Attribute */
			Attribute myThat = (Attribute)pThat;
			
			/* Compare the year */
			return theName.compareTo(myThat.theName);
		}
		
		/**
		 * Format the element
		 * @return the formatted element 
		 */
		public abstract String format();
	}

	/* MoneyAttribute class */
	public class MoneyAttribute extends Attribute {
		/* Access methods */
		public	Money	getValue()	{ return (Money)super.getValue(); }
		
		/**
		 * Constructor
		 * @param pName the name
		 * @param pValue the value
		 */
		private MoneyAttribute(String pName,
						  	   Money  pValue) {
			/* Store the values */
			super(pName, pValue);
		}

		/**
		 * Format the element
		 * @return the formatted element 
		 */
		public String format() { return Money.format(getValue()); }
	}

	/* UnitsAttribute class */
	public class UnitsAttribute extends Attribute {
		/* Access methods */
		public	Units	getValue()	{ return (Units)super.getValue(); }
		
		/**
		 * Constructor
		 * @param pName the name
		 * @param pValue the value
		 */
		private UnitsAttribute(String pName,
						  	   Units  pValue) {
			/* Store the values */
			super(pName, pValue);
		}

		/**
		 * Format the element
		 * @return the formatted element 
		 */
		public String format() { return Units.format(getValue()); }
	}
	
	/* PricesAttribute class */
	public class PriceAttribute extends Attribute {
		/* Access methods */
		public	Price	getValue()	{ return (Price)super.getValue(); }
		
		/**
		 * Constructor
		 * @param pName the name
		 * @param pValue the value
		 */
		private PriceAttribute(String pName,
						  	   Price  pValue) {
			/* Store the values */
			super(pName, pValue);
		}

		/**
		 * Format the element
		 * @return the formatted element 
		 */
		public String format() { return Price.format(getValue()); }
	}
	
	/**
	 * List of Attributes
	 */
	public class AttributeList extends SortedList<Attribute> {
		/**
		 *  Construct a list. Inserts search backwards from the end for the insert point
		 */
		private AttributeList() { super(Attribute.class); }
			
		/**
		 * Find an attribute
		 * @param pName the name of the attribute
		 * @return the value of the attribute or null
		 */
		protected Object findAttribute(String pName) {
			ListIterator 	myIterator;
			Attribute		myCurr;
			
			/* Access the iterator */
			myIterator = listIterator();
			
			/* Loop through the attributes */
			while ((myCurr = myIterator.next()) != null) {
				/* If we found the name return its value */
				if (myCurr.getName().equals(pName))
					return myCurr.getValue();
			}

			/* return attribute not found */
			return null;
		}
	}
}
