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
package uk.co.tolcroft.models;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import uk.co.tolcroft.models.ModelException.ExceptionClass;

public abstract class PropertySet {
	/**
	 * The Preference node for this set
	 */
	private Preferences 			theHandle		= null;
	
	/**
	 * The map of properties 
	 */
	private Map<String, Property> 	theMap			= null;
	
	/**
	 * The list of properties that have a value on initialisation
	 */
	private String[]				theActive		= null;

	/**
	 * Obtain the collection of properties
	 * @return the properties
	 */
	public Collection<Property> 	getProperties()	{ return theMap.values(); }
	
	/**
	 * Constructor 
	 */
	public PropertySet() throws ModelException {
		/* Access the handle */
		theHandle = Preferences.userNodeForPackage(this.getClass());
		
		/* Allocate the property map */
		theMap = new HashMap<String, Property>();
		
		/* Access the active key names */
		try { theActive = theHandle.keys(); } catch (Throwable e) {}
		
		/* Define the properties */
		defineProperties();
		
		/* Store property changes */
		storeChanges();
	}

	/**
	 * Callback from Constructor to define the properties within the propertySet
	 */
	protected abstract void defineProperties();
	
	/**
	 * Callback from Constructor to obtain default value
	 * @param pName the name of the property
	 * @return the default value
	 */
	protected abstract Object getDefaultValue(String pName);
	
	/**
	 * Callback from Constructor to obtain display name
	 * @param pName the name of the property
	 * @return the display name
	 */
	protected abstract String getDisplayName(String pName);
	
	/**
	 * Define property, callback from {@link #defineProperties}
	 * @param pName the name of the property
	 * @param pType the type of the property
	 * @return the newly created property
	 */
	protected Property defineProperty(String 		pName,
									  PropertyType	pType) {
		Property myProp = null;
		
		/* Switch on property type */
		switch (pType) {
			/* Create property */
			case String: 	myProp = new StringProperty(pName); break;
			case File: 		myProp = new StringProperty(pName, PropertyType.File); break;
			case Directory: myProp = new StringProperty(pName, PropertyType.Directory); break;
			case Integer: 	myProp = new IntegerProperty(pName); break;
			case Boolean: 	myProp = new BooleanProperty(pName); break;
			case Date: 		myProp = new DateProperty(pName); break;
		}
		
		/* Add it to the list of properties */
		defineProperty(myProp);
		
		/* Return the property */
		return myProp;
	}
	
	/**
	 * Define Enum property, callback from {@link #defineProperties}
	 * @param pName the name of the property
	 * @param pClass the Enum class
	 * @return the newly created property
	 */
	protected <E extends Enum<E>> Property defineProperty(String 	pName,
									  	  				  Class<E>	pType) {
		/* Create the property */
		Property myProp = new EnumProperty<E>(pName, pType);
		
		/* Add it to the list of properties */
		defineProperty(myProp);
		
		/* Return the property */
		return myProp;
	}
	
	/**
	 * Obtain property
	 * @param pName the name of the property
	 * @return the property
	 */
	public Property getProperty(String pName) 	{ return theMap.get(pName); }
	
	/**
	 * Obtain Integer property
	 * @param pName the name of the property
	 * @return the Integer property or null if no such integer property exists
	 */
	private IntegerProperty getIntegerProperty(String	pName) {
		/* Access property */
		Property myProp = getProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		if (!(myProp instanceof IntegerProperty)) return null;
		
		/* Return the property */
		return (IntegerProperty)myProp;
	}
	
	/**
	 * Obtain Integer value
	 * @param pName the name of the property
	 * @return the Integer value or null if no such integer property exists
	 */
	public Integer getIntegerValue(String	pName) {
		/* Access property */
		IntegerProperty myProp = getIntegerProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		
		/* Return the property */
		return myProp.getValue();
	}
	
	/**
	 * Obtain Boolean property
	 * @param pName the name of the property
	 * @return the Boolean property or null if no such boolean property exists
	 */
	private BooleanProperty getBooleanProperty(String	pName) {
		/* Access property */
		Property myProp = getProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		if (!(myProp instanceof BooleanProperty)) return null;
		
		/* Return the property */
		return (BooleanProperty)myProp;
	}
	
	/**
	 * Obtain Boolean value
	 * @param pName the name of the property
	 * @return the Boolean value or null if no such boolean property exists
	 */
	public Boolean getBooleanValue(String	pName) {
		/* Access property */
		BooleanProperty myProp = getBooleanProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		
		/* Return the value */
		return myProp.getValue();
	}
	
	/**
	 * Obtain String property
	 * @param pName the name of the property
	 * @return the String property or null if no such string property exists
	 */
	private StringProperty getStringProperty(String	pName) {
		/* Access property */
		Property myProp = getProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		if (!(myProp instanceof StringProperty)) return null;
		
		/* Return the property */
		return (StringProperty)myProp;
	}
	
	/**
	 * Obtain String value
	 * @param pName the name of the property
	 * @return the String value or null if no such string property exists
	 */
	public String getStringValue(String	pName) {
		/* Access property */
		StringProperty myProp = getStringProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		
		/* Return the value */
		return myProp.getValue();
	}
	
	/**
	 * Obtain Date property
	 * @param pName the name of the property
	 * @return the Date property or null if no such date property exists
	 */
	private DateProperty getDateProperty(String	pName) {
		/* Access property */
		Property myProp = getProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		if (!(myProp instanceof DateProperty)) return null;
		
		/* Return the property */
		return (DateProperty)myProp;
	}
	
	/**
	 * Obtain Date value
	 * @param pName the name of the property
	 * @return the Date or null if no such date property exists
	 */
	public DateDay getDateValue(String	pName) {
		/* Access property */
		DateProperty myProp = getDateProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		
		/* Return the value */
		return myProp.getValue();
	}
	
	/**
	 * Obtain Enum property
	 * @param pName the name of the property
	 * @param pClass the Enum class
	 * @return the Enum property or null if no such Enum property exists
	 */
	private <E extends Enum<E>> EnumProperty<E> getEnumProperty(String		pName,
																Class<E> 	pClass) {
		/* Access property */
		Property myProp = getProperty(pName);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		if (!(myProp instanceof EnumProperty)) return null;
		
		/* Access as Enum property */
		EnumProperty<?> myEnumProp = (EnumProperty<?>)myProp;
		if (myEnumProp.theClass != pClass) return null;
		
		/* Return the property */
		@SuppressWarnings("unchecked")
		EnumProperty<E> myResult = (EnumProperty<E>)myProp;
		return myResult;
	}
	
	/**
	 * Obtain Enum value
	 * @param pName the name of the property
	 * @param pClass the Enum class
	 * @return the Enum or null if no such Enum property exists
	 */
	public <E extends Enum<E>> E getEnumValue(String	pName,
											  Class<E> 	pClass) {
		/* Access Enum property */
		EnumProperty<E> myProp = getEnumProperty(pName, pClass);
		
		/* If not found or wrong type return null */
		if (myProp == null) return null;
		
		/* Return the value */
		return myProp.getValue();
	}
	
	/**
	 * Define a Property for the node
	 * @param pProperty the property to define
	 */
	private void defineProperty(Property pProperty) {
		/* Access the name of the property */
		String myName = pProperty.getName();
		
		/* Reject if the name is already present */
		if (theMap.get(myName) != null) 
			throw new IllegalArgumentException("Property " + myName + " is already defined");
		
		/* Add the property to the map */
		theMap.put(pProperty.getName(), pProperty);
	}
	
	/**
	 * Reset all changes in this property set 
	 */
	public void resetChanges() {
		/* Loop through all the properties */
		for (Property myProp : theMap.values()) {
			/* Reset the changes */
			myProp.resetChanges();
		}
	}

	/**
	 * Store Property changes 
	 */
	public void storeChanges() throws ModelException {
		/* Loop through all the properties */
		for (Property myProp : theMap.values()) {
			/* Store any changes */
			myProp.storeProperty();
		}
		
		/* Protect against exceptions */
		try {
			/* Flush the output */
			theHandle.flush();
		}
		
		catch (Throwable e) {
			throw new ModelException(ExceptionClass.PREFERENCE,
								"Failed to flush preferences to store",
								e);
		}
	}
	
	/**
	 * Does the property set have changes 
	 */
	public boolean hasChanges()  {
		/* Loop through all the properties */
		for (Property myProp : theMap.values()) {
			/* Store any changes */
			if (myProp.isChanged()) return true;
		}
		
		/* Return no changes */
		return false;
	}
	
	/**
	 * Check whether a property exists 
	 * @param pName the name of the property
	 * @return whether the property already exists
	 */
	private boolean checkExists(String pName) {
		/* If we failed to get the active keys, assume it exists */
		if (theActive == null) return false;
		
		/* Loop through all the keys */
		for (String myName : theActive) {
			/* If the name matches return true */
			if (myName.equals(pName)) return true;
		}
		
		/* return no match */
		return false;
	}
	
	/**
	 * Integer Property
	 */
	public abstract class Property {
		/**
		 * Property Name
		 */
		private final String 		theName;
		
		/**
		 * Display Name
		 */
		private final String 		theDisplay;
		
		/**
		 * Property Type
		 */
		private final PropertyType 	theType;
		
		/**
		 * Property Value
		 */
		private Object				theValue	= null;
		
		/**
		 * New Property Value
		 */
		private Object				theNewValue	= null;
		
		/**
		 * Is there a change to the property
		 */
		private boolean				isChanged	= false;
		
		/**
		 * Obtain the name of the property
		 * @return the name of the property
		 */
		public String getName() { return theName; }
		
		/**
		 * Obtain the display name of the property
		 * @return the display name of the property
		 */
		public String getDisplay() { return theDisplay; }
		
		/**
		 * Obtain the type of the property
		 * @return the type of the property
		 */
		public PropertyType getType() { return theType; }
		
		/**
		 * Obtain the value of the property
		 * @return the value of the property
		 */
		private Object getValue() { 
			/* Return the active value */
			return (isChanged) ? theNewValue : theValue; 
		}
		
		/**
		 * Is the property changed
		 * @return is the property changed
		 */
		public boolean isChanged() { return isChanged; }
		
		/**
		 * Constructor 
		 * @param pName the name of the property
		 * @param pType the type of the property
		 */
		private Property(String pName, PropertyType pType) {
			/* Store name and type */
			theName 	= pName;		
			theType 	= pType;
			theDisplay 	= getDisplayName(theName);
		}
		
		/**
		 * Set value
		 * @param pValue the value
		 */
		private void setValue(Object pValue) { 
			theValue = pValue;
		}
		
		/**
		 * Set new value
		 * @param pNewValue the new value
		 */
		private void setNewValue(Object pNewValue) { 
			theNewValue = (pNewValue == null) ? getDefaultValue(theName)  
											  : pNewValue;
			isChanged	= Utils.differs(theNewValue, theValue).isDifferent();
		}
		
		/** 
		 * Reset changes 
		 */
		private void resetChanges() {
			/* Reset change indicators */
			theNewValue	= null;
			isChanged 	= false;
		}
		
		/**
		 * Store property
		 */
		private void storeProperty() {
			/* If the property has changed */
			if (isChanged) {
				/* else Store the value */
				storeTheProperty(theNewValue);
				
				/* Note the new value and reset changes */
				setValue(theNewValue);
				resetChanges();
			}
		}
		
		/**
		 * Store the value of the property
		 * @param pNewValue the new value to store
		 */
		protected abstract void storeTheProperty(Object pNewValue);
	}
	
	/**
	 * Integer Property
	 */
	public class IntegerProperty extends Property {
		/**
		 * Obtain the value of the property
		 * @return the value of the property
		 */
		public Integer getValue() { return (Integer)super.getValue(); }
		
		/**
		 * Constructor	 
		 * @param pName the name of the property
		 */
		public IntegerProperty(String 	pName) {
			/* Store name */
			super(pName, PropertyType.Integer);
	
			/* Check whether we have an existing value */
			boolean bExists = checkExists(pName);
			
			/* If it exists */
			if (bExists) {
				/* Access the value */
				int myValue = theHandle.getInt(pName, -1);
				
				/* Set as initial value */
				super.setValue(new Integer(myValue));
			}
			
			/* else value does not exist */
			else {
				/* Use default as a changed value */
				super.setNewValue(getDefaultValue(pName));
			}
		}
		
		/**
		 * Set value
		 * @param pNewValue the new value
		 */
		public void setValue(Integer pNewValue) {
			/* Take a copy if not null */
			if (pNewValue != null) pNewValue = new Integer(pNewValue);
			
			/* Set the new value */
			super.setNewValue(pNewValue);
		}
		
		@Override
		protected void storeTheProperty(Object pNewValue) {
			/* Store the value */
			theHandle.putInt(getName(), (Integer)pNewValue);			
		}
	}
	
	/**
	 * Boolean Property
	 */
	public class BooleanProperty extends Property {
		/**
		 * Obtain the value of the property
		 * @return the value of the property
		 */
		public Boolean getValue() { return (Boolean)super.getValue(); }
		
		/**
		 * Constructor	 
		 * @param pName the name of the property
		 */
		public BooleanProperty(String 	pName) {
			/* Store name */
			super(pName, PropertyType.Boolean);
	
			/* Check whether we have an existing value */
			boolean bExists = checkExists(pName);
			
			/* If it exists */
			if (bExists) {
				/* Access the value */
				boolean myValue = theHandle.getBoolean(pName, false);
				
				/* Set as initial value */
				super.setValue(new Boolean(myValue));
			}
			
			/* else value does not exist */
			else {
				/* Use default as a changed value */
				super.setNewValue(getDefaultValue(pName));
			}
		}
		
		/**
		 * Set value
		 * @param pNewValue the new value
		 */
		public void setValue(Boolean pNewValue) {
			/* Take a copy if not null */
			if (pNewValue != null) pNewValue = new Boolean(pNewValue);
			
			/* Set the new value */
			super.setNewValue(pNewValue);
		}
		
		@Override
		protected void storeTheProperty(Object pNewValue) {
			/* Store the value */
			theHandle.putBoolean(getName(), (Boolean)pNewValue);			
		}
	}
		
	/**
	 * String Property
	 */
	public class StringProperty extends Property {
		/**
		 * Obtain the value of the property
		 * @return the value of the property
		 */
		public String getValue() { return (String)super.getValue(); }
		
		/**
		 * Constructor	 
		 * @param pName the name of the property
		 */
		public StringProperty(String 	pName) { this(pName, PropertyType.String); }
		
		/**
		 * Constructor	 
		 * @param pName the name of the property
		 * @param pType the type of the property
		 */
		public StringProperty(String 		pName,
							  PropertyType	pType) {
			/* Store name */
			super(pName, pType);
	
			/* Check whether we have an existing value */
			boolean bExists = checkExists(pName);
			
			/* If it exists */
			if (bExists) {
				/* Access the value */
				String myValue = theHandle.get(pName, null);
				
				/* Set as initial value */
				super.setValue(new String(myValue));
			}
			
			/* else value does not exist */
			else {
				/* Use default as a changed value */
				super.setNewValue(getDefaultValue(pName));
			}
		}
		
		/**
		 * Set value
		 * @param pNewValue the new value
		 */
		public void setValue(String pNewValue) {
			/* Take a copy if not null */
			if (pNewValue != null) pNewValue = new String(pNewValue);
			
			/* Set the new value */
			super.setNewValue(pNewValue);
		}
		
		@Override
		protected void storeTheProperty(Object pNewValue) {
			/* Store the value */
			theHandle.put(getName(), (String)pNewValue);			
		}
	}	

	/**
	 * Date Property
	 */
	public class DateProperty extends Property {
		/**
		 * Obtain the value of the property
		 * @return the value of the property
		 */
		public DateDay getValue() { return (DateDay)super.getValue(); }
		
		/**
		 * Constructor	 
		 * @param pName the name of the property
		 */
		public DateProperty(String 	pName) {
			/* Store name */
			super(pName, PropertyType.Date);
	
			/* Check whether we have an existing value */
			boolean bExists = checkExists(pName);
			
			/* If it exists */
			if (bExists) {
				/* Access the value */
				String myValue = theHandle.get(pName, null);
				if (myValue == null) bExists = false;
				else {
					/* Parse the Date */
					DateDay   myDate  = new DateDay(myValue);
					if (myDate.isNull()) bExists = false;
					else {
						/* Set as initial value */
						super.setValue(myDate);
					}
				}
			}
			
			/* if value does not exist or is invalid */
			if (!bExists) {
				/* Use default as a changed value */
				super.setNewValue(getDefaultValue(pName));
			}
		}
		
		/**
		 * Set value
		 * @param pNewValue the new value
		 */
		public void setValue(DateDay pNewValue) {
			/* Take a copy if not null */
			if (pNewValue != null) pNewValue = new DateDay(pNewValue);
			
			/* Set the new value */
			super.setNewValue(pNewValue);
		}
		
		@Override
		protected void storeTheProperty(Object pNewValue) {
			/* Store the value */
			theHandle.put(getName(), ((DateDay)pNewValue).formatDate());			
		}
	}	
	
	/**
	 * Enum Property
	 */
	public class EnumProperty<E extends Enum<E>> extends Property {
		/**
		 * The enum class 
		 */
		private Class<E> 	theClass	= null;
		
		/**
		 * The enum class 
		 */
		private E[] 		theValues	= null;
		
		/**
		 * Obtain the value of the property
		 * @return the value of the property
		 */
		public E getValue() { return theClass.cast(super.getValue()); }
		
		/**
		 * Obtain the values of the property
		 * @return the values of the property
		 */
		public E[] getValues() { return theValues; }
		
		/**
		 * Constructor	 
		 * @param pName the name of the property
		 */
		public EnumProperty(String 		pName,
				            Class<E> 	pClass) {
			/* Store name */
			super(pName, PropertyType.Enum);
			
			/* Store the class */
			theClass 	= pClass;
			theValues	= theClass.getEnumConstants();
	
			/* Check whether we have an existing value */
			boolean bExists = checkExists(pName);
			
			/* If it exists */
			if (bExists) {
				/* Access the value */
				String myValue = theHandle.get(pName, null);
				if (myValue == null) bExists = false;
				else {
					/* Set the value */
					bExists = setValue(myValue);
				}
			}
			
			/* if value does not exist or is invalid */
			if (!bExists) {
				/* Use default as a changed value */
				super.setNewValue(getDefaultValue(pName));
			}
		}
		
		/**
		 * Set value
		 * @param pNewValue the new value
		 * @return whether the value was valid or not
		 */
		public boolean setValue(String pNewValue) {
			/* Loop through the Enum constants */
			for (E myEnum : theValues) {
				/* If we match */
				if (pNewValue.equals(myEnum.name())) {
					/* Set as initial value */
					super.setValue(myEnum);
					return true;
				}
			}
			
			/* Return invalid value */
			return false;
		}
		
		/**
		 * Set value
		 * @param pNewValue the new value
		 */
		public void setValue(Enum<E> pNewValue) {
			/* Set the new value */
			super.setNewValue(pNewValue);
		}
		
		@Override
		protected void storeTheProperty(Object pNewValue) {
			/* Store the value */
			theHandle.put(getName(), theClass.cast(pNewValue).name());			
		}
	}	
	
	/**
	 * Enum class for property types
	 */
	public enum PropertyType {
		String,
		Integer,
		Boolean,
		Date,
		File,
		Directory,
		Enum;
	}
	
	/**
	 * Interface to determine relevant property set
	 */
	public static interface PropertySetChooser {
		/**
		 * Determine class of relevant property set
		 * @return the propertySet class
		 */
		public Class<? extends PropertySet> getPropertySetClass();
	}
	
	/**
	 * PropertySetManager class
	 */
	public static class PropertyManager {
		/**
		 * Map of propertySets 
		 */
		private static Map<Class<?>, PropertySet>	theMap	= new HashMap<Class<?>, PropertySet>();
		
		/**
		 * Obtain the collection of property sets
		 * @return the property sets
		 */
		public static Collection<PropertySet> 		getPropertySets()	{ return theMap.values(); }
		
		/**
		 * Listener manager
		 */
		private static EventManager					theListeners	= new EventManager(PropertyManager.class);
		
		/**
		 * Add action Listener to list 
		 * @param pListener the listener to add
		 */
		public static void addActionListener(ActionListener pListener) {
			/* Add the Action Listener to the list */
			theListeners.addActionListener(pListener);
		}
		
		/**
		 * Remove action Listener to list 
		 * @param pListener the listener to remove
		 */
		public static void removeActionListener(ActionListener pListener) {
			/* Remove the Action Listener from the list */
			theListeners.removeActionListener(pListener);
		}
		
		/**
		 * Obtain the property set for the calling class
		 * @param pOwner the owning class
		 * @return the relevant propertySet
		 */
		public static PropertySet getPropertySet(PropertySetChooser pOwner) {
			/* Determine the required propertySet class */
			Class<? extends PropertySet> myClass = pOwner.getPropertySetClass();
			
			/* Return the PropertySet */
			return getPropertySet(myClass);
		}
	
		/**
		 * Obtain the property set for the calling class
		 * @param pClass the class of the property set
		 * @return the relevant propertySet
		 */
		public static synchronized PropertySet getPropertySet(Class<? extends PropertySet> pClass) {
			/* Locate a cached PropertySet */
			PropertySet mySet = theMap.get(pClass);
			
			/* If we have not seen this set before */
			if (mySet == null) {
				/* Protect against exceptions */
				try { 
					/* Access the new set */
					mySet = pClass.newInstance();
					
					/* Cache the set */
					theMap.put(pClass, mySet);
					
					/* Fire the action performed */
					theListeners.fireActionPerformed(mySet,
													 ActionEvent.ACTION_PERFORMED, 
													 null);
				} catch (Throwable e) {	}
			}
			
			/* Return the PropertySet */
			return mySet;
		}
	}
}
