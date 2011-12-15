package uk.co.tolcroft.models.apps;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import uk.co.tolcroft.models.ui.DateButton;

public class DateExample {
	private static void createAndShowGUI() {
		try { 
			/* Create the frame */
			JFrame myFrame = new JFrame("DateButton Demo");

			/* Create the Example program */
			DateExample myProgram = new DateExample();
			
			/* Create the panel */
			JPanel myPanel = myProgram.makePanel();
			
			/* Attach the panel to the frame */
			myPanel.setOpaque(true);
			myFrame.setContentPane(myPanel);
			myFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
			
			/* Show the frame */
			myFrame.pack();
			myFrame.setLocationRelativeTo(null);
			myFrame.setVisible(true);
		}
		catch (Exception e) {}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}

	/* Members */
	private JTable 					theTable 		= null;
	private DateButton.CellRenderer theRenderer 	= null;
	private DateButton.CellEditor	theEditor		= null;
	private JComboBox				theLocaleList	= null;
	private JComboBox				theFormatList	= null;
	private DateButton				theStartDate	= null;
	private DateButton				theEndDate		= null;
	private Locale					theLocale		= Locale.UK;
	private String					theFormat		= "dd-MMM-yyyy";
	private DateListener			theListener		= new DateListener();
	
	/**
	 * Create the panel 
	 */
	private JPanel makePanel() {
		/* Create the table */
		makeTable();
		
		/* Create the range buttons */
		makeRangeButtons();
		
		/* Create the locale list */
		makeLocaleList();
		
		/* Create the format list */
		makeFormatList();
		
		/* Create the additional labels */
		JLabel myFormat = new JLabel("Format:"); 
		JLabel myLocale = new JLabel("Locale:"); 
		JLabel myStart 	= new JLabel("Start:"); 
		JLabel myEnd 	= new JLabel("End:");
		
		/* Create a Range sub-panel */
		JPanel myRange = new JPanel(new GridBagLayout());
		GridBagConstraints myConstraints = new GridBagConstraints();
		myRange.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Range Selection"));
		myConstraints = new GridBagConstraints();
		myConstraints.gridx 	= 0;
		myConstraints.gridy 	= 0;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 0.0;
		myConstraints.anchor    = GridBagConstraints.LINE_END;
		myConstraints.insets	= new Insets(5,5,5,5);
		myRange.add(myStart, myConstraints);
		myConstraints.gridx 	= 1;
		myConstraints.gridy 	= 0;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 1.0;
		myRange.add(theStartDate, myConstraints);
		myConstraints.gridx 	= 0;
		myConstraints.gridy 	= 1;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 0.0;
		myConstraints.anchor    = GridBagConstraints.LINE_END;
		myConstraints.insets	= new Insets(5,5,5,5);
		myRange.add(myEnd, myConstraints);
		myConstraints.gridx 	= 1;
		myConstraints.gridy 	= 1;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 1.0;
		myConstraints.fill 		= GridBagConstraints.HORIZONTAL;
		myRange.add(theEndDate, myConstraints);
		
		/* Create a Style sub-panel */
		JPanel myStyle = new JPanel(new GridBagLayout());
		myConstraints = new GridBagConstraints();
		myStyle.setBorder(javax.swing.BorderFactory
				.createTitledBorder("Format Selection"));
		myConstraints = new GridBagConstraints();
		myConstraints.gridx 	= 0;
		myConstraints.gridy 	= 0;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 0.0;
		myConstraints.anchor    = GridBagConstraints.LINE_END;
		myConstraints.insets	= new Insets(5,5,5,5);
		myStyle.add(myLocale, myConstraints);
		myConstraints.gridx 	= 1;
		myConstraints.gridy 	= 0;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 1.0;
		myStyle.add(theLocaleList, myConstraints);
		myConstraints.gridx 	= 0;
		myConstraints.gridy 	= 1;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 0.0;
		myConstraints.anchor    = GridBagConstraints.LINE_END;
		myConstraints.insets	= new Insets(5,5,5,5);
		myStyle.add(myFormat, myConstraints);
		myConstraints.gridx 	= 1;
		myConstraints.gridy 	= 1;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 1.0;
		myConstraints.fill 		= GridBagConstraints.HORIZONTAL;
		myStyle.add(theFormatList, myConstraints);
		
		/* Create the panel */
		JPanel myPanel = new JPanel(new GridBagLayout());
		myConstraints = new GridBagConstraints();
		myConstraints.gridx 	= 0;
		myConstraints.gridy 	= 0;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 0.5;
		myPanel.add(myRange, myConstraints);
		myConstraints.gridx 	= 1;
		myConstraints.gridy 	= 0;
		myConstraints.gridwidth = 1;
		myConstraints.weightx   = 0.5;
		myPanel.add(myStyle, myConstraints);
		
		myConstraints.gridx 	= 0;
		myConstraints.gridy 	= 1;
		myConstraints.gridwidth = 2;
		myConstraints.gridheight= GridBagConstraints.REMAINDER;
		myConstraints.fill 		= GridBagConstraints.HORIZONTAL;
		myPanel.add(new JScrollPane(theTable), myConstraints);
		theTable.setPreferredScrollableViewportSize(new Dimension(400, 90));

		/* Return the panel */
		return myPanel;
	}
	
	/**
	 * Create the Table
	 */
	private void makeTable() {
		/* Create the data for a table */
		String[] 	myColumns 	= { "Date", "Description" };
		Object[][] 	myData		= { { makeDate(2011, Calendar.JULY, 1),   "First Entry"  }
								  ,	{ makeDate(2012, Calendar.MARCH, 14), "Second Entry" }
								  ,	{ makeDate(2012, Calendar.NOVEMBER, 19), "Third Entry" }
								  ,	{ makeDate(2013, Calendar.MAY, 31), "Fourth Entry" }
								  ,	{ makeDate(2014, Calendar.FEBRUARY, 28), "Fifth Entry" }
								  };
		
		/* Create the table */
		theTable = new JTable(myData, myColumns);
		
		/* Create the renderer and editor */
		theRenderer	= new DateButton.CellRenderer();
		theEditor 	= new DateButton.CellEditor();
		
		/* Format the first column */
		TableColumnModel myColModel = theTable.getColumnModel();
		
		/* Set the width of the column */
		TableColumn myFirstCol = myColModel.getColumn(0);
		myFirstCol.setPreferredWidth(100);
		
		/* Set the renderer */
		myFirstCol.setCellRenderer(theRenderer);
		myFirstCol.setCellEditor(theEditor);

		/* Set the width of the column */
		TableColumn mySecondCol = myColModel.getColumn(1);
		mySecondCol.setPreferredWidth(200);
	}
	
	/**
	 * Create the list of available locales
	 */
	private void makeLocaleList() {
		/* Create the Combo box and populate it */
		theLocaleList = new JComboBox();
		for(shortLocale myLocale : shortLocale.values()) {
			/* Add the Locale to the list */
			theLocaleList.addItem(myLocale);
		}
		
		/* Set the default item */
		theLocaleList.setSelectedItem(shortLocale.UnitedKingdom);
		
		/* Apply the locale */
		applyLocale();

		/* Add Listener to List */
		theLocaleList.addItemListener(theListener);
	}
	
	/**
	 * Create the list of available formats
	 */
	private void makeFormatList() {
		/* Create the Combo box and populate it */
		theFormatList = new JComboBox();
		theFormatList.addItem("dd-MMM-yyyy");
		theFormatList.addItem("dd/MMM/yy");
		theFormatList.addItem("yyyy/MMM/dd");
		
		/* Set the default item */
		theFormatList.setSelectedItem(theFormat);
		
		/* Apply the format */
		applyFormat();

		/* Add Listener to List */
		theFormatList.addItemListener(theListener);
	}
	
	/**
	 * Create the range buttons
	 */
	private void makeRangeButtons() {
		/* Create the buttons */
		theStartDate 	= new DateButton();
		theEndDate		= new DateButton();
		
		/* Initialise the values */
		Date myStart 	= makeDate(2007, Calendar.JANUARY, 25);
		Date myEnd		= makeDate(2014, Calendar.AUGUST, 9);
		
		/* Set the values */
		theStartDate.setSelectedDate(myStart);
		theEndDate.setSelectedDate(myEnd);
		
		/* Apply the range */
		applyRange();
		
		/* Add Listener to buttons */
		theStartDate.addPropertyChangeListener(DateButton.valueDATE, theListener);
		theEndDate.addPropertyChangeListener(DateButton.valueDATE, theListener);
	}
	
	/**
	 * Convenience method to create a date from Year, Month, Day
	 * @param pYear the year
	 * @param pMonth the month,
	 * @param pDay
	 * @return the requested date
	 */
	private Date makeDate(int pYear, int pMonth, int pDay) {
		Calendar myDate = Calendar.getInstance(theLocale);
		myDate.set(Calendar.YEAR, pYear); 
		myDate.set(Calendar.MONTH, pMonth); 
		myDate.set(Calendar.DAY_OF_MONTH, pDay); 
		return myDate.getTime();
	}
	
	/**
	 * Apply Locale to underlying objects
	 */
	private void applyLocale() {
		/* Set the Renderer and Editor Locale */
		theRenderer.setLocale(theLocale);
		theEditor.setLocale(theLocale);
		
		/* Set the Start/End Date button locale */
		theStartDate.setLocale(theLocale);
		theEndDate.setLocale(theLocale);
		
		/* Note that we should redraw the table */
		theTable.repaint();
	}
	
	/**
	 * Apply format to underlying objects
	 */
	private void applyFormat() {
		/* Set the Renderer and Editor Format */
		theRenderer.setFormat(theFormat);
		theEditor.setFormat(theFormat);
		
		/* Set the Start/End Date button format */
		theStartDate.setFormat(theFormat);
		theEndDate.setFormat(theFormat);
		
		/* Note that we should redraw the table */
		theTable.repaint();
	}
	
	/**
	 * Apply Range to underlying objects
	 */
	private void applyRange() {
		/* Access the Start/End Dates */
		Date myStart 	= theStartDate.getSelectedDate();
		Date myEnd		= theEndDate.getSelectedDate();

		/* Set the select-able range for the start/end buttons */
		theStartDate.setSelectableRange(null, myEnd);
		theEndDate.setSelectableRange(myStart, null);
		
		/* Set the Editor range */
		theEditor.setSelectableRange(myStart, myEnd);
	}
	
	/**
	 * Listener class
	 */
	private class DateListener implements PropertyChangeListener,
										  ItemListener {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			/* Access source object */
			Object o = evt.getSource();
			
			/* If this is the start/end date */
			if ((o == theStartDate) ||
				(o == theEndDate)) {
				/* Apply the new range */
				applyRange();
			}
		}

		@Override
		public void itemStateChanged(ItemEvent evt) {
			/* Access source object */
			Object 	o = evt.getSource();
			
			/* Ignore if we are not selecting */
			if (evt.getStateChange() != ItemEvent.SELECTED) return;
			
			/* If this is the Locale list */
			if (o == theLocaleList) {
				/* Store the new locale */
				theLocale = ((shortLocale)evt.getItem()).getLocale();
				
				/* Apply the new locale */
				applyLocale();
			}
			
			/* If this is the Format list */
			else if (o == theFormatList) {
				/* Store the new format */
				theFormat = (String)evt.getItem();
				
				/* Apply the new format */
				applyFormat();
			}
		}
	}
	
	/**
	 * Some useful locales 
	 */
	private enum shortLocale {
		Germany(Locale.GERMANY),
		France(Locale.FRANCE),
		Italy(Locale.ITALY),
		Japan(Locale.JAPAN),
		UnitedStates(Locale.US),
		UnitedKingdom(Locale.UK);
		
		/**
		 * Locale property
		 */
		private final Locale theLocale;
		
		/**
		 * Obtain locale value
		 */
		public Locale getLocale() { return theLocale; }
		
		/**
		 * Constructor
		 */
		private shortLocale(Locale pLocale) {
			/* Store the Locale */
			theLocale = pLocale;
		}
	}
}
