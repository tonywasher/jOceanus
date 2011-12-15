package uk.co.tolcroft.models.ui;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import uk.co.tolcroft.finance.ui.MaintProperties;
import uk.co.tolcroft.models.DateDay;
import uk.co.tolcroft.models.PropertySet;
import uk.co.tolcroft.models.PropertySet.BooleanProperty;
import uk.co.tolcroft.models.PropertySet.DateProperty;
import uk.co.tolcroft.models.PropertySet.EnumProperty;
import uk.co.tolcroft.models.PropertySet.IntegerProperty;
import uk.co.tolcroft.models.PropertySet.Property;
import uk.co.tolcroft.models.PropertySet.PropertyType;
import uk.co.tolcroft.models.PropertySet.StringProperty;
import uk.co.tolcroft.models.ui.ValueField.ValueClass;

public class PropertySetPanel extends JPanel {
	/* Serial Id */
	private static final long serialVersionUID = -713132970269487546L;

	/**
	 * The Parent for this panel
	 */
	private final MaintProperties 	theParent;
	
	/**
	 * The PropertySet for this panel
	 */
	private final PropertySet 		theProperties;
	
	/**
	 * The individual property items
	 */
	private List<PropertyItem> 		theList 	= null;

	/**
	 * The Set name 
	 */
	private String					theName		= null;
	
	/**
	 * Do we have changes 
	 */
	private boolean					hasChanges	= false;
	
	@Override
	public String toString() { return theName; }
	
	/**
	 * Constructor
	 */
	public PropertySetPanel(MaintProperties pParent,
							PropertySet 	pSet) {
		/* Options SubPanel */
		JPanel 	myOptions	= null;
		int		myRow		= 0;
		
		/* Record the set */
		theProperties 	= pSet;
		theParent		= pParent;

		/* Record the name of the set */
		theName = pSet.getClass().getSimpleName();
	
		/* Create the list of items */
		theList = new ArrayList<PropertyItem>();
		
		/* Set a border */
		setBorder(javax.swing.BorderFactory
				.createTitledBorder("Properties"));

		/* Set the layout for this panel */
		setLayout(new GridBagLayout());
		GridBagConstraints myConstraints = new GridBagConstraints();
		
		/* Loop through the properties */
		for (Property myProp : pSet.getProperties()) {
			/* Create the item */
			PropertyItem myItem = new PropertyItem(myProp);
			
			/* Add it to the list */
			theList.add(myItem);
			
			/* Switch on the property type */
			switch (myProp.getType()) {
				case Boolean:
					/* If we do not yet have an options panel */
					if (myOptions == null) {
						/* Create options */
						myOptions = new JPanel();
						myOptions.setLayout(new FlowLayout(FlowLayout.LEADING));						
						myOptions.setBorder(javax.swing.BorderFactory
								.createTitledBorder("Options"));
					}
					
					/* Add the item to the options panel */
					myOptions.add(myItem.getComponent());
					break;
				case String:
					/* Add the Label into the first slot */
					myConstraints.gridx 	= 0;
					myConstraints.gridy 	= myRow;
					myConstraints.gridwidth = 1;
					myConstraints.fill 		= GridBagConstraints.NONE;
					myConstraints.weightx   = 0.0;
					myConstraints.anchor    = GridBagConstraints.LINE_END;
					myConstraints.insets	= new Insets(5,5,5,5);
					add(myItem.getLabel(), myConstraints);

					/* Add the Component into the second slot */
					myConstraints.gridx 	= 1;
					myConstraints.gridy 	= myRow++;
					myConstraints.gridwidth = GridBagConstraints.REMAINDER;
					myConstraints.fill 		= GridBagConstraints.HORIZONTAL;
					myConstraints.weightx   = 1.0;
					myConstraints.anchor    = GridBagConstraints.LINE_START;
					myConstraints.insets	= new Insets(5,5,5,5);
					add(myItem.getComponent(), myConstraints);
					break;
				default:
					/* Add the Label into the first slot */
					myConstraints.gridx 	= 0;
					myConstraints.gridy 	= myRow;
					myConstraints.gridwidth = 1;
					myConstraints.fill 		= GridBagConstraints.NONE;
					myConstraints.weightx   = 0.0;
					myConstraints.anchor    = GridBagConstraints.LINE_END;
					myConstraints.insets	= new Insets(5,5,5,5);
					add(myItem.getLabel(), myConstraints);

					/* Add the Component into the second slot */
					myConstraints.gridx 	= 1;
					myConstraints.gridy 	= myRow;
					myConstraints.gridwidth = 1;
					myConstraints.fill 		= GridBagConstraints.NONE;
					myConstraints.weightx   = 0.0;
					myConstraints.anchor    = GridBagConstraints.LINE_START;
					myConstraints.insets	= new Insets(5,5,5,5);
					add(myItem.getComponent(), myConstraints);

					/* Add the Component into the second slot */
					myConstraints.gridx 	= 2;
					myConstraints.gridy 	= myRow++;
					myConstraints.gridwidth = GridBagConstraints.REMAINDER;
					myConstraints.fill 		= GridBagConstraints.HORIZONTAL;
					myConstraints.weightx   = 1.0;
					myConstraints.anchor    = GridBagConstraints.LINE_START;
					myConstraints.insets	= new Insets(5,5,5,5);
					add(new JLabel(), myConstraints);
					break;
			}
			
			/* If we have an options panel */
			if (myOptions != null) {
				/* Add the Label into the first slot */
				myConstraints.gridx 	= 0;
				myConstraints.gridy 	= myRow++;
				myConstraints.gridwidth = GridBagConstraints.REMAINDER;
				myConstraints.fill 		= GridBagConstraints.HORIZONTAL;				
				myConstraints.weightx   = 1.0;
				myConstraints.anchor    = GridBagConstraints.LINE_START;
				myConstraints.insets	= new Insets(5,5,5,5);
				add(myOptions, myConstraints);
			}
		}
	}
	
	/**
	 * Does the Property Set have changes
	 * @return does the set have changes  
	 */
	public boolean hasChanges() { return hasChanges; }
	
	/**
	 * Reset changes 
	 */
	public void resetChanges() {
		/* Reset changes and clear flag */
		theProperties.resetChanges();
		hasChanges = false;
		
		/* Update the fields */
		updateFields();
	}
	
	/**
	 * Store changes 
	 */
	public void storeChanges() throws Exception {
		/* Reset changes and clear flag */
		theProperties.storeChanges();
		hasChanges = false;
		
		/* Update the fields */
		updateFields();
	}
	
	/**
	 * Update fields 
	 */
	private void updateFields() {
		/* Loop through the items */
		for (PropertyItem myItem : theList) {
			/* Update the field */
			myItem.updateField();
		}
	}
	
	/**
	 * Notify changes 
	 */
	private void notifyChanges()  {
		/* Set flag */
		hasChanges = true;
		
		/* Update the fields */
		updateFields();
		
		/* Notify the parent */
		theParent.notifyChanges();
	}
	
	/**
	 * A Property item
	 */
	private class PropertyItem {
		/**
		 * The property
		 */
		private final Property 		theProperty;
		
		/**
		 * The property type
		 */
		private final PropertyType 	theType;
		
		/**
		 * Underlying field
		 */
		private final PropertyField theField;
		
		/**
		 * The label for the property
		 */
		private final JLabel		theLabel;
		
		/**
		 * Constructor
		 */
		private PropertyItem(Property pProperty) {
			/* Store the reference to the property */
			theProperty = pProperty;
			theType		= pProperty.getType();
			
			/* Switch on field type */
			switch (theType) {
				/* Create the Underlying field */
				case String: 	theField = new StringField(theProperty); break;
				case Integer: 	theField = new IntegerField(theProperty); break;
				case Boolean: 	theField = new BooleanField(theProperty); break;
				case Date: 		theField = new DateField(theProperty); break;
				case Enum: 		theField = new EnumField(theProperty); break;
				default:		theField = null;
			}
			
			/* If the type if not boolean */
			if (theType != PropertyType.Boolean) {
				/* Create the label */
				theLabel = new JLabel(theProperty.getDisplay() + ":");
				theLabel.setHorizontalAlignment(JLabel.RIGHT);
			}
			
			/* else no JLabel */
			else theLabel = null;
			
			/* Initialise the field */
			theField.updateField();
		}
		
		/**
		 * Update the field
		 */
		protected void updateField() 		{ theField.updateField(); }
		
		/**
		 * Obtain label
		 * @return the label
		 */
		protected JLabel getLabel() 	{ return theLabel; }
		
		/**
		 * Obtain component
		 * @return the component
		 */
		protected JComponent getComponent() { return theField.getComponent(); }
				
		/**
		 * Abstract property field
		 */
		private abstract class PropertyField {
			/**
			 * Obtain component
			 * @return the component
			 */
			protected abstract JComponent getComponent();
			
			/**
			 * Update the field value and adjust rendering
			 */
			protected abstract void updateField();
		}
		
		/**
		 * StringField class
		 */
		private class StringField extends PropertyField {
			/**
			 * The underlying value field 
			 */
			private final ValueField		theField;
			
			/**
			 * The property as a stringProperty 
			 */
			private final StringProperty	theString;
			
			/**
			 * Constructor
			 * @param pProperty
			 */
			private StringField(Property pProperty) {
				/* Access the property and create the underlying field */
				theString 	= (StringProperty)pProperty;
				theField	= new ValueField();
				theField.setColumns(60);
				
				/* Add property change listener */
				theField.addPropertyChangeListener(ValueField.valueName, new PropertyListener());
			}
			
			@Override
			protected void updateField() {
				/* Update the field */
				theField.setValue(theString.getValue());
				
				/* Set font and foreground */
				theField.setForeground(RenderData.getForeground(theString));
				theField.setFont(RenderData.getFont(theString));				
			}
			
			@Override
			protected JComponent getComponent() { return theField; }
			
			/**
			 * PropertyListener class
			 */
			private class PropertyListener implements PropertyChangeListener {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Object 	o 		= evt.getSource();
					/* If this is our property */
					if (o == theField) {
						/* Set the new value of the property */
						String myValue = (String)theField.getValue();
						theString.setValue(myValue);
					
						/* Note if we have any changes */
						if (theString.isChanged())
							notifyChanges();
					}
				}
			}			
		}
		
		/**
		 * IntegerField class
		 */
		private class IntegerField extends PropertyField {
			/**
			 * The underlying value field 
			 */
			private final ValueField		theField;
			
			/**
			 * The property as an integerProperty 
			 */
			private final IntegerProperty	theInteger;
			
			/**
			 * Constructor
			 * @param pProperty
			 */
			private IntegerField(Property pProperty) {
				/* Access the property and create the underlying field */
				theInteger 	= (IntegerProperty)pProperty;
				theField	= new ValueField(ValueClass.Integer);
				theField.setColumns(10);
				
				/* Add property change listener */
				theField.addPropertyChangeListener(ValueField.valueName, new PropertyListener());
			}
			
			@Override
			protected void updateField() {
				/* Update the field */
				theField.setValue(theInteger.getValue());
				
				/* Set font and foreground */
				theField.setForeground(RenderData.getForeground(theInteger));
				theField.setFont(RenderData.getFont(theInteger));				
			}
			
			@Override
			protected JComponent getComponent() { return theField; }
			
			/**
			 * PropertyListener class
			 */
			private class PropertyListener implements PropertyChangeListener {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Object 	o 		= evt.getSource();
					/* If this is our property */
					if (o == theField) {
						/* Set the new value of the property */
						Integer myValue = (Integer)theField.getValue();
						theInteger.setValue(myValue);
					
						/* Note if we have any changes */
						if (theInteger.isChanged())
							notifyChanges();
					}
				}
			}			
		}
		
		/**
		 * BooleanField class
		 */
		private class BooleanField extends PropertyField {
			/**
			 * The underlying button field 
			 */
			private final JCheckBox			theField;
			
			/**
			 * The property as a booleanProperty 
			 */
			private final BooleanProperty	theBoolean;
			
			/**
			 * Constructor
			 * @param pProperty
			 */
			private BooleanField(Property pProperty) {
				/* Access the property and create the underlying field */
				theBoolean 	= (BooleanProperty)pProperty;
				theField	= new JCheckBox(pProperty.getName());
				
				/* Add item listener */
				theField.addItemListener(new PropertyListener());
			}
			
			@Override
			protected void updateField() {
				/* Update the field */
				theField.setSelected(theBoolean.getValue());
				
				/* Set font and foreground */
				theField.setForeground(RenderData.getForeground(theBoolean));
				theField.setFont(RenderData.getFont(theBoolean));				
			}
			
			@Override
			protected JComponent getComponent() { return theField; }
			
			/**
			 * PropertyListener class
			 */
			private class PropertyListener implements ItemListener {

				@Override
				public void itemStateChanged(ItemEvent evt) {
					Object 	o 		= evt.getSource();
					/* If this is our property */
					if (o == theField) {
						/* Set the new value of the property */
						Boolean myValue = theField.isSelected();
						theBoolean.setValue(myValue);
					
						/* Note if we have any changes */
						if (theBoolean.isChanged())
							notifyChanges();
					}
				}
			}			
		}
		
		/**
		 * DateField class
		 */
		private class DateField extends PropertyField {
			/**
			 * The underlying button field 
			 */
			private final DateButton		theField;
			
			/**
			 * The property as a dateProperty 
			 */
			private final DateProperty		theDate;
			
			/**
			 * Constructor
			 * @param pProperty
			 */
			private DateField(Property pProperty) {
				/* Access the property and create the underlying field */
				theDate 		= (DateProperty)pProperty;
				theField		= new DateButton();
				
				/* Add property change listener */
				theField.addPropertyChangeListener(ValueField.valueName, new PropertyListener());
			}
			
			@Override
			protected void updateField() {
				/* Update the field */
				theField.setSelectedDate(theDate.getValue().getDate());
				
				/* Set font and foreground */
				theField.setForeground(RenderData.getForeground(theDate));
				theField.setFont(RenderData.getFont(theDate));				
			}
						
			@Override
			protected JComponent getComponent() { return theField; }
			
			/**
			 * PropertyListener class
			 */
			private class PropertyListener implements PropertyChangeListener {

				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					Object 	o 		= evt.getSource();
					/* If this is our property */
					if (o == theField) {
						/* Set the new value of the property */
						DateDay myValue = new DateDay(theField.getSelectedDate());
						theDate.setValue(myValue);
					
						/* Note if we have any changes */
						if (theDate.isChanged())
							notifyChanges();
					}
				}
			}			
		}
		
		/**
		 * EnumField class
		 */
		private class EnumField extends PropertyField {
			/**
			 * The underlying combo box field 
			 */
			private final JComboBox			theField;
			
			/**
			 * The property as an EnumProperty 
			 */
			private final EnumProperty<?>	theEnum;
			
			/**
			 * Constructor
			 * @param pProperty
			 */
			private EnumField(Property pProperty) {
				/* Access the property and create the underlying field */
				theEnum 		= (EnumProperty<?>)pProperty;
				theField		= new JComboBox();
				
				/* For all values */
				for (Enum<?> myEnum : theEnum.getValues()) {
					/* Add to the combo box */
					theField.addItem(myEnum.name());
				}
				
				/* Add item listener */
				theField.addItemListener(new PropertyListener());
			}
			
			@Override
			protected void updateField() {
				/* Update the field */
				theField.setSelectedItem(theEnum.getValue());
				
				/* Set font and foreground */
				theField.setForeground(RenderData.getForeground(theEnum));
				theField.setFont(RenderData.getFont(theEnum));				
			}
						
			@Override
			protected JComponent getComponent() { return theField; }
			
			/**
			 * PropertyListener class
			 */
			private class PropertyListener implements ItemListener {

				@Override
				public void itemStateChanged(ItemEvent evt) {
					Object 	o 		= evt.getSource();
					/* If this is our property */
					if (o == theField) {
						if (evt.getStateChange() == ItemEvent.SELECTED) {
							/* Set the new value of the property */
							String myName = (String)evt.getItem();
							theEnum.setValue(myName);
					
							/* Note if we have any changes */
							if (theEnum.isChanged())
								notifyChanges();
						}
					}
				}
			}			
		}
	}
}
