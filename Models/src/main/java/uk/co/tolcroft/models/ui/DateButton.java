/**
 * DateButton Date Selection widgets
 * Copyright (C) 2011 Tony Washer
 * Tony.Washer@yahoo.co.uk
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package uk.co.tolcroft.models.ui;
	
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;

import javax.swing.AbstractAction;
import javax.swing.AbstractCellEditor;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;

/**
 * <p>Provides a button which displays the currently selected {@link Date}
 * <p>When clicked the button displays a mode-less {@link Dialog} enabling date selection
 * This dialog will be closed if focus is lost or escape is pressed
 * <p>An underlying {@link DateModel} is used to control locale, format and select-able range, 
 * and to directly set the selected date. Changes to the selected date are reported via a 
 * {@link java.beans.PropertyChangeEvent} for the {@link #valueDATE} property, and may be monitored by attaching a 
 * {@link java.beans.PropertyChangeListener} 
 * 
 * @author Tony Washer
 */
public class DateButton extends JButton {
	/* Serial Id */
	private static final long serialVersionUID = 7110911129423423705L;
	
	/**
	 * Standard fonts
	 */
	private static Font StandardFont 	= new Font("Courier", Font.PLAIN, 10);
	private static Font InactiveFont 	= new Font("Courier", Font.ITALIC, 10);
	private static Font SelectedFont 	= new Font("Courier", Font.BOLD, 10);

	/** 
	 * ToolTip texts
	 */
	private static String 	theCurrentDay		= "Today";
	private static String 	theSelectedDay		= "Selected Date";
	private static String 	theNextMonth		= "Next Month";
	private static String 	thePreviousMonth	= "Previous Month";
	private static String 	theNextYear			= "Next Year";
	private static String 	thePreviousYear		= "Previous Year";

	/**
	 * Name of the Date property
	 */
	public static final String	valueDATE		= "SelectedDate";
	
	/**
	 * Self reference
	 */
	private final DateButton 		theSelf		= this;
	
	/**
	 * The Underlying Dialog 
	 */
	private final Dialog 			theDialog;
	
	/**
	 * The Underlying Date Model
	 */
	private final DateModel			theModel;
	
	/**
	 * Constructor
	 */
	public DateButton() {
		/* Create the Dialog */
		theDialog = new Dialog(this);
		theModel  = theDialog.getDateModel();
		
		/* Register this button as the model owner */
		theModel.setOwner(this);
		
		/* Configure the button */
		addActionListener(new ButtonListener());
		setMargin(new Insets(1,1,1,1));
		setPreferredSize(new Dimension(100, 25));
	}
	
	/**
	 * Obtain Dialog 
	 * @return the dialog
	 */
	private Dialog 		getDialog() 	{ return theDialog; }
	
	/**
	 * Obtain DateModel 
	 * @return the date model
	 */
	public DateModel 	getDateModel() 	{ return theModel; }
	
	/**
	 * Obtain SelectedDate 
	 * @return the selected date
	 */
	public Date getSelectedDate() { return theModel.getSelectedDate(); }
	
	/**
	 * Set SelectedDate 
	 * @param pDate the selected date
	 */
	public void setSelectedDate(Date pDate) { theModel.setSelectedDate(pDate); }
	
	/**
	 * Set Locale
	 * @param pLocale the Locale
	 */
	public void setLocale(Locale	pLocale) { 
		theModel.setLocale(pLocale);
		super.setLocale(pLocale);
	}
	
	/**
	 * Set the date format
	 * @param pFormat the format string
	 */
	public void setFormat(String pFormat) { theModel.setFormat(pFormat); }

	/**
	 * Set the range of allowable dates
	 * @param pEarliest the Earliest select-able date
	 * @param pLatest the Latest select-able date
	 * @throws IllegalArgumentException if pEarliest is later than pLatest
	 */
	public void	setSelectableRange(Date pEarliest,
								   Date pLatest) {	theModel.setSelectableRange(pEarliest, pLatest); }

	/**
	 * Refresh the text 
	 */
	private void refreshText() { setText(theModel.format(theModel.getSelectedDate())); }

	@Override 
	protected void firePropertyChange(String pProperty,
									  Object pOldValue,
									  Object pNewValue) { 
		super.firePropertyChange(pProperty, pOldValue, pNewValue); }
	
	/**
	 * Listener class 
	 */
	private class ButtonListener implements ActionListener {
		@Override
		public void actionPerformed(final ActionEvent e) {
			/* If this is the button */
			if (e.getSource() == theSelf) {
				/* Position the dialog just below the button */
				Point myLoc = getLocationOnScreen();
				theDialog.setLocation(myLoc.x, myLoc.y + getHeight());

				/* Show the dialog */
				theDialog.setVisible(true);
			}
		}
	}
	
	/**
	 * Are the dates in the same month
	 * @param pFirst the first date
	 * @param pSecond the second date 
	 * @return true/false
	 */
	private static boolean isSameMonth(Calendar pFirst, Calendar pSecond) {
		if (!isSameYear(pFirst, pSecond)) return false;
		else return (pFirst.get(Calendar.MONTH) == pSecond.get(Calendar.MONTH));
	}
	
	/**
	 * Are the dates in the same year
	 * @param pFirst the first date
	 * @param pSecond the second date 
	 * @return true/false
	 */
	private static boolean isSameYear(Calendar pFirst, Calendar pSecond) {
		if (pFirst == null) return false;
		return (pFirst.get(Calendar.YEAR) == pSecond.get(Calendar.YEAR));
	}
	
	/**
	 * Provides Date Management support for {@link DateButton}
	 */
	public static class DateModel {
		/**
		 * The Locale
		 */
		private Locale				theLocale		= null;
		private DateFormatSymbols	theSymbols		= null;
		private String				theFormat		= null;
		private SimpleDateFormat 	theDateFormat 	= null;
	
		/**
		 * Start and End Date for range of dates allowed
		 */
		private Calendar			theEarliest		= null;
		private Calendar			theLatest		= null;
	
		/**
		 * The selected Date
		 */
		private Calendar			theSelected		= null;

		/**
		 * The active display month
		 */
		private Calendar			theMonth		= null;

		/**
		 * The button to notify of Date changes
		 */
		private DateButton			theOwner		= null;
		
		/**
		 * Constructor
		 */
		private DateModel() {
			/* Set defaults */
			setLocale(Locale.getDefault());
			setFormat("dd-MMM-yyyy");
		}

		/**
		 * Get the selected date
		 * @return the Selected date
		 */
		public Date getSelectedDate() {
			/* Store the date */
			return (theSelected == null) ? null : theSelected.getTime();
		}
		
		/**
		 * Get the earliest select-able date
		 * @return the Earliest date
		 */
		public Date getEarliestDate() {
			/* Store the date */
			return (theEarliest == null) ? null : theEarliest.getTime();
		}
		
		/**
		 * Get the latest select-able date
		 * @return the Latest date
		 */
		public Date getLatestDate() {
			/* Store the date */
			return (theLatest == null) ? null : theLatest.getTime();
		}
		
		/**
		 * Access functions
		 */
		private Calendar 			getCurrentMonth() 	{ return theMonth; }
		private Calendar 			getEarliest() 		{ return theEarliest; }
		private Calendar 			getLatest() 		{ return theLatest; }
		private DateFormatSymbols 	getSymbols() 		{ return theSymbols; }
		
		/**
		 * Set Locale
		 * @param pLocale the Locale
		 */
		public void setLocale(Locale	pLocale) {
			/* Store locale */
			theLocale 	= pLocale;
			theSymbols 	= DateFormatSymbols.getInstance(theLocale);

			/* Update the selected date/range to the new locale */
			setSelectedDate(getSelectedDate());
			setSelectableRange(getEarliestDate(), getLatestDate());
			
			/* Update the date format if available */
			if (theFormat != null) setFormat(theFormat);
		}

		/**
		 * Set the date format
		 * @param pFormat the format string
		 */
		public void setFormat(String pFormat) {
			/* Create the format */
			theDateFormat = new SimpleDateFormat(pFormat, theLocale);
			theFormat	  = new String(pFormat);

			/* Refresh the display */
			if (theOwner != null) theOwner.refreshText();
		}

		/**
		 * Set the Button owner 
		 * @return pOwner the owning button
		 */
		private void setOwner(DateButton pOwner) { theOwner = pOwner; }
		
		/**
		 * Set the selected date
		 * @param pDate the Selected date
		 */
		public void	setSelectedDate(Date pDate) {
			Calendar myOld	= theSelected;
			Calendar myNew	= null;
			
			/* If the date is non-null */
			if (pDate != null) {
				/* Build the new date */
				myNew = Calendar.getInstance(theLocale);
				myNew.setTime(pDate);
			} 
			
			/* Ignore if there is no change */
			if (!isDateChanged(myOld, myNew)) return;

			/* Store the date */
			theSelected = duplicateDate(myNew);
			if (theOwner != null) theOwner.refreshText();
			
			/* Fire a Property change */
			if (theOwner != null) 
				theOwner.firePropertyChange(valueDATE, 
											(myOld == null) ? null : myOld.getTime(),
											(myNew == null) ? null : myNew.getTime());
		}

		/**
		 * Set the range of allowable dates
		 * @param pEarliest the Earliest select-able date
		 * @param pLatest the Latest select-able date
		 * @throws IllegalArgumentException if pEarliest is later than pLatest
		 */
		public void	setSelectableRange(Date pEarliest,
									   Date pLatest) {
			/* Check that we have a valid range */
			if ((pEarliest != null) && (pLatest != null)) {
				if (pLatest.compareTo(pEarliest) < 0)
					throw new IllegalArgumentException("Latest date is before Earliest Date");
			}
			
			/* Null the fields */
			theEarliest = null;
			theLatest	= null;
			
			/* If we have an earliest */
			if (pEarliest != null) {
				/* Store the date */
				theEarliest = Calendar.getInstance(theLocale); 
				theEarliest.setTime(pEarliest);
			}

			/* If we have a latest */
			if (pLatest != null) {
				/* Store the date */
				theLatest = Calendar.getInstance(theLocale);
				theLatest.setTime(pLatest);
			}
		}
		
		/**
		 * Format a date
		 * @param pDate the date to format
		 */
		protected String format(Date pDate) {
			/* Handle null */
			if (pDate == null) return null;
			
			/* Format the date */
			return theDateFormat.format(pDate);
		}

		/**
		 * Obtain current date
		 * @param pSource the source date
		 * @return the current date
		 */
		private Calendar currentDate() {
			Calendar myDate = Calendar.getInstance(theLocale);
			return myDate;
		}
		
		/**
		 * Duplicate a Date
		 * @param pSource the source date
		 * @return the duplicated date
		 */
		private Calendar duplicateDate(Calendar pSource) {
			Calendar myDate = Calendar.getInstance(theLocale);
			myDate.setTime(pSource.getTime());
			return myDate;
		}
		
		/**
		 * Obtain current day of month or zero if not current month
		 * @return the current month day
		 */
		private int getCurrentDay() {
			Calendar myDate = currentDate();
			return (isSameMonth(myDate, theMonth)) ? myDate.get(Calendar.DAY_OF_MONTH) : 0;
		}

		/**
		 * Obtain Selected day of month or zero if not current month
		 * @return the selected month day
		 */
		private int getSelectedDay() {
			return (isSameMonth(theSelected, theMonth)) ? theSelected.get(Calendar.DAY_OF_MONTH) : 0;
		}
		
		/**
		 * Obtain Earliest day of month or zero if not current month
		 * @return the earliest month day
		 */
		private int getEarliestDay() {
			return (isSameMonth(theEarliest, theMonth)) ? theEarliest.get(Calendar.DAY_OF_MONTH) : 0;
		}

		/**
		 * Obtain Latest day of month or zero if not current month
		 * @return the latest month day
		 */
		private int getLatestDay() {
			return (isSameMonth(theLatest, theMonth)) ? theLatest.get(Calendar.DAY_OF_MONTH) : 0;
		}
		
		/**
		 * Adjust current month to previous month
		 */
		private void previousMonth() {
    		theMonth.add(Calendar.MONTH, -1);			
		}
		
		/**
		 * Adjust current month to next month
		 */
		private void nextMonth() {
    		theMonth.add(Calendar.MONTH, 1);			
		}
		
		/**
		 * Adjust current month to previous year
		 */
		private void previousYear() {
    		theMonth.add(Calendar.YEAR, -1);
    		if ((theEarliest != null) && (theMonth.compareTo(theEarliest) < 0)) {
    			theMonth = duplicateDate(theEarliest);
    			theMonth.set(Calendar.DAY_OF_MONTH, 1);
    		}
		}
		
		/**
		 * Adjust current month to next year
		 */
		private void nextYear() {
    		theMonth.add(Calendar.YEAR, 1);
    		if ((theLatest != null) && (theMonth.compareTo(theLatest) > 0)) {
    			theMonth = duplicateDate(theLatest);
    			theMonth.set(Calendar.DAY_OF_MONTH, 1);
    		}
		}
		
		/**
		 * Set selected day in current month
		 * @param pDay the selected day
		 */
		private void setSelectedDay(int pDay) {
			/* Build the new selected date */
			Calendar myOld	= theSelected;
			Calendar myNew	= Calendar.getInstance(theLocale);
			myNew.setTime(theMonth.getTime());
			myNew.set(Calendar.DAY_OF_MONTH, pDay);
			
			/* Ignore if there is no change */
			if (!isDateChanged(myOld, myNew)) return;
			
			/* Store the selected date */
			theSelected = duplicateDate(myNew);
			if (theOwner != null) theOwner.refreshText();

			/* Fire property change */
			if (theOwner != null) 
				theOwner.firePropertyChange(valueDATE, 
											(myOld == null) ? null : myOld.getTime(),
											myNew.getTime());			
		}
		
		/**
		 * Initialise the current month
		 */
		private void initialiseCurrent() {
			/* Access Selected Date */
			Calendar myDate;
			if (theSelected == null) myDate = Calendar.getInstance(theLocale);
			else myDate = duplicateDate(theSelected);
		
			/* Move to start date if we are earlier */
			if ((theEarliest != null) && (myDate.compareTo(theEarliest) < 0))
				myDate = duplicateDate(theEarliest);
			
			/* Move to end date if we are later */
			if ((theLatest != null) && (myDate.compareTo(theLatest) > 0))
				myDate = duplicateDate(theLatest);

			/* Set to 1st of month and record it */
			myDate.set(Calendar.DAY_OF_MONTH, 1);
			
			/* Record as current month */
			theMonth = myDate;
		}
		
		/**
		 * Has the date changed?
		 * @param pFirst the first date
		 * @param pSecond the second date 
		 * @return <code>true/false</code>
		 */
		private static boolean isDateChanged(Calendar pFirst, Calendar pSecond) {
			if (pFirst == null) return (pSecond != null);
			else return !pFirst.equals(pSecond);
		}
	}
	
	/**
	 * Panel class representing a single day in the panel
	 */
	private static class PanelDay extends JLabel {
		/* Serial Id */
		private static final long serialVersionUID = -5636278095729007866L;

		/**
		 * Self Reference
		 */
		private PanelDay		theSelf		= this;
		
		/**
		 * Owning dialog
		 */
		private Dialog			theDialog	= null;
		
		/**
		 * Various borders
		 */
		private Border	theStdBorder	= BorderFactory.createEmptyBorder();
		private Border	theSelBorder	= BorderFactory.createLineBorder(Color.green.darker());
		private Border	theHltBorder	= BorderFactory.createLineBorder(Color.orange);
		
		/**
		 * The Day that this Label represents 
		 */
		private int 	theDay			= -1;
		
		/**
		 * Is the day select-able
		 */
		private boolean isSelectable	= false;
		
		/**
		 * Component attributes
		 */
		private Font	theFont			= null;
		private Color	theForeGround	= null;
		private Color	theBackGround	= null;
		private Border	theBorder		= null;
		private String	theToolTip		= null;
		
		/**
		 * Constructor
		 * @param pDialog the owning dialog
		 */
		private PanelDay(Dialog pDialog) {
			/* Store the parameter */
			theDialog = pDialog;
			
			/* Initialise values */
            setHorizontalAlignment(SwingConstants.CENTER);
            setOpaque(true);
            addMouseListener(new CalendarMouse());
		}
		
		/**
		 * Set day for label
		 * @param pDay the Day number
		 * @param isSelectable is the day select-able
		 */
		private void setDay(int iDay, boolean isSelectable) {
			/* Record the day */
			theDay 				= iDay;
			this.isSelectable 	= isSelectable;
			
			/* Set the text for the item */
			if (iDay > 0) 	setText(Integer.toString(theDay));
			else			setText("");
			
			/* Set Characteristics */
			setFont(theFont);
			setForeground(theForeGround);
			setBackground(theBackGround);
			setBorder(theBorder);
			setToolTipText(theToolTip);
			
			/* Enable/Disable the label */
			setEnabled(isSelectable);
		}
		
		/**
		 * Reset a Day Label
		 * @param isActive
		 */
		private void resetDay(boolean isActive) {
			/* Record detail */
			theFont 		= ((isActive) ? StandardFont : InactiveFont);
			theForeGround 	= Color.black;
			theBackGround	= Color.white;
			theBorder		= theStdBorder;
			theToolTip		= null;
			isSelectable	= isActive;
		}
		
		/**
		 * Set a day as a Weekend
		 */
		private void setWeekend() {
			/* Record detail */
			theForeGround 	= Color.red;
		}
		
		/**
		 * Set a day as Current Day
		 */
		private void setCurrent() {
			/* Record detail */
			theForeGround 	= Color.blue;
			theBackGround 	= Color.gray;
			theToolTip		= theCurrentDay;
		}
		
		/**
		 * Set a day as Selected Day
		 */
		private void setSelected() {
			/* Record detail */
			theFont			= SelectedFont;
			theForeGround 	= Color.green.darker();
			theBackGround 	= Color.green.brighter();
			theBorder		= theSelBorder;
			theToolTip		= theSelectedDay;
		}		
		
		/**
		 * CalendarMouse 
		 */
		private class CalendarMouse extends MouseAdapter {
			/**
			 * Handle Mouse Clicked
			 */
		    public void mouseClicked(final MouseEvent e) {
		    	/* If item is select-able */
		    	if (isSelectable) { theDialog.setSelected(theDay); }
		    }
		    
		    /**
			 * Handle entry into label
			 */
	        public void mouseEntered(final MouseEvent e) {
	        	/* Highlight the border of a select-able item */
	        	if (isSelectable) theSelf.setBorder(theHltBorder);
	        }

	        /**
	         * Handle exit from the label
	         */
	        public void mouseExited(final MouseEvent e) {
	        	/* Reset border to standard for label that has changed */
	            if (isSelectable) theSelf.setBorder(theBorder);
	        }
		}
	}
	
	/**
	 * PanelMonth class representing the set of PanelDay labels in a month
	 */
	private static class PanelMonth extends JPanel {
		/* Serial Id */
		private static final long serialVersionUID = 4003892701953050327L;

		/**
		 * The array of days of week (in column order)
		 */
		private int[]			theDaysOfWk	= new int[7];
		
		/**
		 * The Array of Day Names
		 */
		private JLabel[]		theHdrs		= new JLabel[7];
		
		/**
		 * The Array of Day Labels
		 */
		private PanelDay[][]	theDays		= new PanelDay[6][7];
		
		/**
		 * The Dialog
		 */
		private Dialog			theDialog	= null;
		
		/**
		 * The Date Model
		 */
		private DateModel		theModel	= null;
		
		/**
		 * The number of currently visible rows
		 */
		private int				theNumRows	= 6;
		
		/**
		 * Constructor
		 * @param pDialog the owning dialog
		 */
		private PanelMonth(Dialog pDialog) {
			/* Store the dialog */
			theDialog = pDialog;
			
			/* Store the DateModel */
			theModel = pDialog.theModel;
			
			/* Set this as a 7x7 GridLayout */
	        GridLayout myLayout = new GridLayout();
	        myLayout.setColumns(7);
	        myLayout.setRows(0);
	        setLayout(myLayout);
	        
			/* Loop through the labels */
			for (int iCol=0; iCol<7; iCol++) {
				/* Access the label */
				JLabel myLabel  = new JLabel();
				theHdrs[iCol]	= myLabel;
				
				/* Set colour */
				myLabel.setHorizontalAlignment(SwingConstants.CENTER);
				myLabel.setBackground(Color.lightGray);
				myLabel.setOpaque(true);
				
				/* Add to the grid */
				add(myLabel);
			}

	        /* Add the Days to the layout */
	        for (int iRow=0; iRow<6; iRow++) {
	        	for (int iCol=0; iCol<7; iCol++) {
	        		PanelDay myDay = new PanelDay(pDialog);
	        		theDays[iRow][iCol] = myDay;
	        		add(myDay);
	        	}
	        }
		}
		
		/**
		 * ReSize the number of visible rows
		 * @param iNumRow number of visible rows 
		 */
		private void reSizeRows(int iNumRows) {
			/* Hide any visible rows that should now be hidden */
			while (iNumRows < theNumRows) {
				/* Decrement number of rows */
				theNumRows--;
				
				/* Loop through remaining rows */
				for (PanelDay day : theDays[theNumRows] ) {
					/* Remove from panel */
					remove(day);
				}
			}

			/* Show any hidden rows that should now be visible */
			while (iNumRows > theNumRows) {
				/* Loop through remaining rows */
				for (PanelDay day : theDays[theNumRows] ) {
					/* Add to panel */
					add(day);
				}

				/* Increment number of rows */
				theNumRows++;
			}

			/* RePack the Dialog */
			theDialog.reSizeDialog();
		}

		/* Is the DayOfWeek a Weekend day */
		private boolean isWeekend(int pDoW) { return ((pDoW == Calendar.SATURDAY) || (pDoW == Calendar.SUNDAY)); }
		
		/* obtain column number for DayOfWeek */
		private int getDayColumn(int pDoW) { for(int i=0; i < 7; i++) { if (theDaysOfWk[i] == pDoW) return i; } return -1; }
		
		/**
		 * build the month display for the requested month
		 */
		private void buildMonth() {
			int iRow 		= 0;
			int iCol 		= 0;
			
			/* Access the current month */
			Calendar myMonth = theModel.getCurrentMonth();
			int 	 iMonth	 = myMonth.get(Calendar.MONTH);
			
			/* Access the Weekday of the 1st of the month */
			int iFirst 		= myMonth.get(Calendar.DAY_OF_WEEK);
			int iFirstCol 	= getDayColumn(iFirst);
			
			/* Access the interesting days of the month */
			int iCurrent 	= theModel.getCurrentDay();
			int iSelected	= theModel.getSelectedDay();
			int iEarliest	= theModel.getEarliestDay();
			int iLatest		= theModel.getLatestDay();

			/* Create a copy of the month to display */
			Calendar myDate = theModel.duplicateDate(myMonth);
			
			/* Adjust the day to beginning of week if required */
			if (iFirstCol > 0) myDate.add(Calendar.DAY_OF_MONTH, -iFirstCol);
			
			/* Loop through initial columns */
			for(int iDay = myDate.get(Calendar.DAY_OF_MONTH);
				iCol < iFirstCol; 
				iCol++, iDay++, myDate.add(Calendar.DAY_OF_MONTH, 1)) {
				/* Access the label */
				PanelDay myLabel = theDays[0][iCol];
				
				/* Reset the day and set no day */
				myLabel.resetDay(false);
				myLabel.setDay(iDay, false);
			}
			
			/* Loop through the days of the month */
			for (int iDay = 1;
				 iMonth == myDate.get(Calendar.MONTH);
				 iCol++, iDay++, myDate.add(Calendar.DAY_OF_MONTH, 1)) {
				/* Reset column if necessary */
				if (iCol > 6) { iRow++; iCol=0; }
				
				/* Access the label */
				PanelDay myLabel = theDays[iRow][iCol];
				
				/* Set initial parts of the day */
				myLabel.resetDay(true);
				if (isWeekend(myDate.get(Calendar.DAY_OF_WEEK)))	myLabel.setWeekend();
				if (iCurrent == iDay) myLabel.setCurrent();
				if (iSelected == iDay) myLabel.setSelected();
				
				/* Determine whether the day is select-able */
				boolean isSelectable = true;
				if ((iEarliest > 0) && (iDay < iEarliest)) isSelectable = false;
				if ((iLatest > 0) && (iDay > iLatest)) isSelectable = false;
				
				/* Set the day */
				myLabel.setDay(iDay, isSelectable);
			}
			
			/* Loop through remaining columns */
			for(int iDay = 1; iCol < 7; iCol++, iDay++) {
				/* Access the label */
				PanelDay myLabel = theDays[iRow][iCol];
				
				/* Reset the day and set no day */
				myLabel.resetDay(false);
				myLabel.setDay(iDay, false);
			}
			
			/* Ensure correct number of rows are visible */
			reSizeRows(iRow+1);
		}
		
		/**
		 * build Day names
		 */
		private void buildDayNames() {
			/* Get todays date */
			Calendar myDate = theModel.currentDate();
			
			/* Get the Display Names */ 
			String[] myDays = theModel.getSymbols().getShortWeekdays();
			
			/* Build the array of the days of the week */
			int myDoW = myDate.getFirstDayOfWeek();
			for (int iDay=0; iDay<7; iDay++, myDoW++) {
				/* If we are beyond Saturday reset to Sunday */
				if (myDoW > Calendar.SATURDAY) myDoW = Calendar.SUNDAY;
				
				/* Store the day into the array */
				theDaysOfWk[iDay] = myDoW;
			} 
			
			/* Loop through the labels */
			for (int iCol=0; iCol<7; iCol++) {
				/* Access the label */
				JLabel myLabel  = theHdrs[iCol];
				
				/* Set the Name */
				myDoW = theDaysOfWk[iCol];
				myLabel.setText(myDays[myDoW]);
				
				/* Set colour */
				myLabel.setForeground((isWeekend(myDoW)) ? Color.red : Color.black);
			}
		}
	}
	
	/**
	 * PanelNavigation class allowing navigation between months
	 */
	private static class PanelNavigation extends JPanel {
		/* Serial Id */
		private static final long serialVersionUID = 4399207012690467687L;

		/**
		 * The owning dialog
		 */
		private Dialog				theDialog			= null;
		
		/**
		 * The Date Model
		 */
		private DateModel			theModel			= null;
		
		/**
		 * The Date Label 
		 */
		private JLabel				theDateLabel		= null;
		
		/**
		 * The Buttons
		 */
		private JButton				thePrevMonthButton	= null;
		private JButton				theNextMonthButton	= null;
		private JButton				thePrevYearButton	= null;
		private JButton				theNextYearButton	= null;

		/**
		 * Constructor
		 * @param pDialog the owning dialog
		 */
		private PanelNavigation(Dialog pDialog) {
			/* Record the dialog */
			theDialog = pDialog;
			
			/* Store the DateModel */
			theModel = pDialog.theModel;
			
			/* Create the label */
			theDateLabel 		= new JLabel();
			
			/* Create the buttons */
			thePrevMonthButton	= new JButton("<");
			theNextMonthButton	= new JButton(">");
			thePrevYearButton	= new JButton("<<");
			theNextYearButton	= new JButton(">>");
 
			/* Add ToopTips */
			theNextMonthButton.setToolTipText(theNextMonth);
			thePrevMonthButton.setToolTipText(thePreviousMonth);
			theNextYearButton.setToolTipText(theNextYear);
			thePrevYearButton.setToolTipText(thePreviousYear);
			
			/* Listen for button events */
			NavigateListener myListener = new NavigateListener();
			thePrevMonthButton.addActionListener(myListener);
			theNextMonthButton.addActionListener(myListener);
			thePrevYearButton.addActionListener(myListener);
			theNextYearButton.addActionListener(myListener);
			
			/* Restrict the margins */
			thePrevMonthButton.setMargin(new Insets(1,1,1,1));
			theNextMonthButton.setMargin(new Insets(1,1,1,1));
			thePrevYearButton.setMargin(new Insets(1,1,1,1));
			theNextYearButton.setMargin(new Insets(1,1,1,1));

			/* Add these elements into a box */
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(thePrevYearButton);
			add(Box.createHorizontalStrut(1));
			add(thePrevMonthButton);
			add(Box.createHorizontalGlue());
			add(theDateLabel);
			add(Box.createHorizontalGlue());
			add(theNextMonthButton);
			add(Box.createHorizontalStrut(1));
			add(theNextYearButton);
		}
		
		/**
		 * Build month details
		 */
		private void buildMonth() {
			/* Store the active month */
			Calendar myCurrent = theModel.getCurrentMonth();
			
			/* Determine the display for the label */
			String myMonth = myCurrent.getDisplayName(Calendar.MONTH, Calendar.LONG, theModel.theLocale);
			String myYear  = Integer.toString(myCurrent.get(Calendar.YEAR));
			
			/* Set the label */
			theDateLabel.setText(myMonth + ", " + myYear);
			
			/* Access boundary dates */
			Calendar myEarliest = theModel.getEarliest();
			Calendar myLatest	= theModel.getLatest();
			
			/* Enable/Disable buttons as required */
			thePrevMonthButton.setEnabled(!isSameMonth(myEarliest, myCurrent));
			thePrevYearButton.setEnabled(!isSameYear(myEarliest, myCurrent));
			theNextMonthButton.setEnabled(!isSameMonth(myLatest, myCurrent));
			theNextYearButton.setEnabled(!isSameYear(myLatest, myCurrent));
		}
		
		/**
		 * Action Listener for buttons
		 */
		private class NavigateListener implements ActionListener {
			@Override
			public void actionPerformed(final ActionEvent e) {
				/* Access the event source */
				Object src = e.getSource();
	    	
				/* If the button is previous month */
				if (src == thePrevMonthButton) {
					/* Adjust the month */
					theModel.previousMonth();
					theDialog.buildMonth();
				}
				else if (src == theNextMonthButton) {
					/* Adjust the month */
					theModel.nextMonth();
					theDialog.buildMonth();
				}
				else if (src == thePrevYearButton) {
					/* Adjust the month */
					theModel.previousYear();
					theDialog.buildMonth();
				}
				else if (src == theNextYearButton) {
					/* Adjust the month */
					theModel.nextYear();
					theDialog.buildMonth();
				}
			}
		}
	}
	
	/**
	 * Dialog class providing a dialog allowing selection of a date. 
	 * <p>It will rebuild the dialog according to the currently set locale and date format 
	 * whenever it is made visible 
	 */
	private static class Dialog extends JDialog {		
		/* Serial Id */
		private static final long serialVersionUID = 2518033527621472786L;

		/**
		 * The month array 
		 */
		private PanelMonth		theDaysPanel	= null;

		/**
		 * The navigation 
		 */
		private PanelNavigation	theNavigation	= null;

		/**
		 * The Date Model
		 */
		private DateModel			theModel		= null;
		
		/**
		 * The CellEditor if present
		 */
		private CellEditor			theEditor		= null;
		
		/**
		 *  Constructor 
		 *  @param pParent the parent component to which this dialog is attached
		 */
		public Dialog(Component pParent) {
			/* Initialise the dialog */
			super(JOptionPane.getFrameForComponent(pParent), false);
			
			/* Set as undecorated */
			setUndecorated(true);
			
			/* Create the DateModel */
			theModel = new DateModel();
			
			/* Build the panels */
			theDaysPanel  = new PanelMonth(this);
			theNavigation = new PanelNavigation(this);			

			/* Set this to be the main panel */
			JPanel myContainer = new JPanel(new BorderLayout());
			myContainer.setBorder(BorderFactory.createLineBorder(Color.black));
			myContainer.add(theNavigation, BorderLayout.NORTH);
			myContainer.add(theDaysPanel, BorderLayout.SOUTH);
			setContentPane(myContainer);
			pack();
			
			/* Handle Escape Key */
			handleEscapeKey(myContainer);
			
			/* Create focus listener */
			addWindowFocusListener(new CalendarFocus());
			
			/* Set the relative location */
			setLocationRelativeTo(pParent);
		}
		
		/**
		 * Obtain DateModel 
		 * @return the date model
		 */
		public DateModel getDateModel() { return theModel; }
		
		/**
		 * Set the Cell Editor owner 
		 * @return pOwner the owning CellEditor
		 */
		private void setEditor(CellEditor pEditor) { theEditor = pEditor; }
		
		/**
		 * Build the month 
		 */
		private void buildMonth() {
			/* Build the month */
			theNavigation.buildMonth();
			theDaysPanel.buildMonth();
		}

		/**
		 * Set Selected Date
		 * @param pDay the Selected day 
		 */
		private void setSelected(int pDay) {
			/* Set the selected day */
			theModel.setSelectedDay(pDay);

			/* Fire editing stopped */
			if (theEditor != null) theEditor.fireEditingStopped();

			/* Close the dialog */
			setVisible(false);
		}

		/**
		 * Resize the dialog 
		 */
		private void reSizeDialog() {
			pack();
		}
		
		/**
		 * Set dialog visibility 
		 */
		public void setVisible(boolean bVisible) {
			/* If we are becoming visible */
			if (bVisible) {
				/* Initialise the current month */
				theModel.initialiseCurrent();

				/* Build the day names */
				theDaysPanel.buildDayNames();
				
				/* Build detail */
				buildMonth();
			}
			
			/* Pass the call on */
			super.setVisible(bVisible);
		}
		
		/**
		 * Handle Escape to close dialog 
		 */
		private void handleEscapeKey(JPanel pPane) {
	        /* Access Maps */
			ActionMap myAction = pPane.getActionMap();
			InputMap  myInput  = pPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
			
			/* Build the maps */
			myInput.put(KeyStroke.getKeyStroke("ESCAPE"), "Escape");
			myAction.put("Escape", new CalendarAction());
		}	
		
		/**
		 * Close non-Modal
		 */
		private void closeNonModal() {
			/* Close any edit session and set non-visible */
			if (theEditor != null) theEditor.fireEditingCanceled();
			setVisible(false);			
		}
		
		/**
		 * CalendarAction Handle escape action 
		 */
		private class CalendarAction extends AbstractAction {
			/* SerialId */
			private static final long serialVersionUID = 5464442251457102478L;

			@Override
			public void actionPerformed(final ActionEvent e) { closeNonModal();	}
 		}
		
		/**
		 * CalendarFocus Handle loss of focus 
		 */
		private class CalendarFocus extends WindowAdapter {
            @Override
            public void windowLostFocus(final WindowEvent e) { closeNonModal();	}
		}
	}
	
	/**
	 * Provides a TableCellRenderer that displays {@link Date} data into a Table Cell. 
	 * <p>An underlying {@link DateModel} is used to control locale and format,  
	 */
	public static class CellRenderer extends DefaultTableCellRenderer {
		/* Serial Id */
		private static final long serialVersionUID = -7402034704535633915L;

		/**
		 * The Date Model
		 */
		private DateModel			theModel		= null;
		
		/**
		 * Constructor
		 */
		public CellRenderer() {
			/* Record the model */
			theModel = new DateModel();
		}
		
		/**
		 * Obtain DateModel 
		 * @return the date model
		 */
		public DateModel getDateModel() { return theModel; }
		
		/**
		 * Set Locale
		 * @param pLocale the Locale
		 */
		public void setLocale(Locale	pLocale) { 
			theModel.setLocale(pLocale);
			super.setLocale(pLocale);
		}
		
		/**
		 * Set the date format
		 * @param pFormat the format string
		 */
		public void setFormat(String pFormat) { theModel.setFormat(pFormat); }

		/**
		 * Set value for the renderer. This will convert a Date into the required string format before passing it on.
		 * If the object is already a string or is null it is passed directly on.
		 * @param value the value to display (String, Date or null)
		 */
		public void setValue(Object value) {
			String s = "";
			
			/* Handle String/null values */
			if (value instanceof String) s = (String) value;
			else if (value == null) s = "";
			
			/* Handle date values */
			else if (value instanceof Date) {
				Date d = (Date)value;
				s = theModel.format(d);
			}

			/* Pass the value on */
			super.setValue(s);
		}
	}
	
	/**
	 * Provides a TableCellEditor that uses a {@link CalendarButton} to edit a Table cell that 
	 * contains {@link Date} data. 
	 * <p>An underlying {@link DateModel} is used to control locale, format and select-able range,  
	 */
	public static class CellEditor extends AbstractCellEditor 
								   implements TableCellEditor {
		/* Serial Id */
		private static final long serialVersionUID = 429603680827168376L;

		/**
		 * the button
		 */
		private DateButton 		theButton	= null;
		
		/**
		 * The Dialog 
		 */
		private Dialog 			theDialog	= null;
		
		/**
		 * Constructor
		 */
		public CellEditor() {
			/* Create the button and access the Dialog */
			theButton = new DateButton();
			theDialog = theButton.getDialog();
			theDialog.setEditor(this);
			theButton.setBorderPainted(false);
		}
		
		/**
		 * Obtain DateModel 
		 * @return the date model
		 */
		public DateModel getDateModel() { return theButton.getDateModel(); }
		
		/**
		 * Obtain SelectedDate 
		 * @return the selected date
		 */
		public Date getSelectedDate() { return theButton.getSelectedDate(); }
		
		/**
		 * Set SelectedDate 
		 * @param pDate the selected date
		 */
		public void setSelectedDate(Date pDate) { theButton.setSelectedDate(pDate); }
		
		/**
		 * Set Locale
		 * @param pLocale the Locale
		 */
		public void setLocale(Locale	pLocale) { theButton.setLocale(pLocale); }
		
		/**
		 * Set the date format
		 * @param pFormat the format string
		 */
		public void setFormat(String pFormat) { theButton.setFormat(pFormat); }

		/**
		 * Set the range of allowable dates
		 * @param pEarliest the Earliest select-able date
		 * @param pLatest the Latest select-able date
		 * @throws IllegalArgumentException if pEarliest is later than pLatest
		 */
		public void	setSelectableRange(Date pEarliest,
									   Date pLatest) {	theButton.setSelectableRange(pEarliest, pLatest); }

		@Override
		protected void fireEditingCanceled() { super.fireEditingCanceled(); }
		
		@Override
		protected void fireEditingStopped() { super.fireEditingStopped(); }
		
		@Override
		public Object getCellEditorValue() {
			Date myDate = theButton.getSelectedDate();
			return myDate;
		}

		@Override
		public JComponent getTableCellEditorComponent(JTable table,
				Object value, boolean isSelected, int row, int column) {
			/* If the value is the date */
			if (value instanceof Date) {
				Date myDate = (Date)value;
				/* Set the selected date */
				theButton.setSelectedDate(myDate);
			}
			
			/* else set the selected date to null */
			else theButton.setSelectedDate(null);
			
			/* Return the button as the component */
			return theButton;
		}
	}
}
