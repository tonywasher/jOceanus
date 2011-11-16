package uk.co.tolcroft.models.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.text.DateFormatSymbols;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

public class DateSelect extends JDialog {
	/* Serial Id */
	private static final long serialVersionUID = -1836229351108898981L;

	/**
	 * Standard fonts
	 */
	private static Font StandardFont 	= new Font("Courier", Font.PLAIN, 10);
	private static Font InactiveFont 	= new Font("Courier", Font.ITALIC, 10);
	private static Font SelectedFont 	= new Font("Courier", Font.BOLD, 10);

	/** 
	 * ToolTip texts
	 */
	private String 	theCurrentDay		= "Today";
	private String 	theSelectedDay		= "Selected Date";
	private String 	theNextMonth		= "Next Month";
	private String 	thePreviousMonth	= "Previous Month";
	private String 	theNextYear			= "Next Year";
	private String 	thePreviousYear		= "Previous Year";

	/**
	 * The Locale
	 */
	private Locale				theLocale		= Locale.UK;
	private DateFormatSymbols	theSymbols		= null;
	
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
	 * The month to display
	 */
	private Calendar			theMonth		= null;
	
	/**
	 * The month array 
	 */
	private CalendarMonth		theMonthPanel	= null;

	/**
	 * The navigation 
	 */
	private CalendarNavigation	theNavigation	= null;

	/**
	 * CalendarDay
	 */
	private class CalendarDay extends JLabel {
		/* Serial Id */
		private static final long serialVersionUID = -5636278095729007866L;

		/**
		 * Self Reference
		 */
		private CalendarDay		theSelf		= this;
		
		/**
		 * Owning month array 
		 */
		private CalendarMonth	theOwner	= null;
		
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
		 * Is the day selectable
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
		 * @param pMonth the owning array
		 */
		private CalendarDay(CalendarMonth pMonth) {
			/* Store the parameter */
			theOwner = pMonth;
			
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
		    	if (isSelectable) { theOwner.setSelected(theDay); }
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
	
	/* Are the dates in the same month */
	private boolean isSameMonth(Calendar pFirst, Calendar pSecond) {
		if (!isSameYear(pFirst, pSecond)) return false;
		else return (pFirst.get(Calendar.MONTH) == pSecond.get(Calendar.MONTH));
	}
	
	/* Are the dates in the same year */
	private boolean isSameYear(Calendar pFirst, Calendar pSecond) {
		if (pFirst == null) return false;
		return (pFirst.get(Calendar.YEAR) == pSecond.get(Calendar.YEAR));
	}
	
	/* Duplicate a Date */
	private Calendar duplicateDate(Calendar pSource) {
		Calendar myDate = Calendar.getInstance(theLocale);
		myDate.setTime(pSource.getTime());
		return myDate;
	}
	
	/**
	 * CalendarMonth
	 */
	private class CalendarMonth extends JPanel {
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
		private CalendarDay[][]	theDays		= new CalendarDay[6][7];

		/**
		 * The Dialog 
		 */
		private DateSelect		theDialog	= null;
		
		/**
		 * The number of currently visible rows
		 */
		private int				theNumRows	= 6;
		
		/**
		 * Constructor
		 * @param pDialog the Dialog
		 */
		private CalendarMonth(DateSelect pDialog) {
			/* Store the dialog */
			theDialog = pDialog;
			
			/* Set this as a 7x7 GridLayout */
	        GridLayout myLayout = new GridLayout();
	        myLayout.setColumns(7);
	        myLayout.setRows(0);
	        setLayout(myLayout);
	        
	        /* Build the Day Names */
	        buildDayNames();
	        
	        /* Add the Days to the layout */
	        for (int iRow=0; iRow<6; iRow++) {
	        	for (int iCol=0; iCol<7; iCol++) {
	        		CalendarDay myDay = new CalendarDay(this);
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
				for (CalendarDay day : theDays[theNumRows] ) {
					/* Set the label as hidden */
					day.setVisible(false);
				}
			}

			/* Show any hidden rows that should now be visible */
			while (iNumRows > theNumRows) {
				/* Loop through remaining rows */
				for (CalendarDay day : theDays[theNumRows] ) {
					/* Set the label as visible */
					day.setVisible(true);
				}

				/* Increment number of rows */
				theNumRows++;
			}

			/* RePack the Dialog */
			theDialog.pack();
		}

		/* Is the DayOfWeek a Weekend day */
		private boolean isWeekend(int pDoW) { return ((pDoW == Calendar.SATURDAY) || (pDoW == Calendar.SUNDAY)); }
		
		/* obtain column number for DayOfWeek */
		private int getDayColumn(int pDoW) { for(int i=0; i < 7; i++) { if (theDaysOfWk[i] == pDoW) return i; } return -1; }
		
		/**
		 * Set Selected Date
		 * @param pDay the Selected day 
		 */
		private void setSelected(int pDay) {
			/* Create new Calendar instance */
			theSelected = Calendar.getInstance();
			theSelected.setTime(theMonth.getTime());
			theSelected.set(Calendar.DAY_OF_MONTH, pDay);

			/* Close the dialog */
			theDialog.setVisible(false);
		}
		
		/**
		 * build the month display for the requested month
		 * @param pDate the 1st day of the month
		 */
		private void buildMonth() {
			int iRow 		= 0;
			int iCol 		= 0;
			int iMonth		= theMonth.get(Calendar.MONTH);
			
			/* Access the Weekday of the 1st of the month */
			int iFirst 		= theMonth.get(Calendar.DAY_OF_WEEK);
			int iFirstCol 	= getDayColumn(iFirst);
			
			/* Access todays date */
			Calendar myDate = Calendar.getInstance(theLocale);
			
			/* Determine whether this is the current month */
			int iCurrent = (isSameMonth(myDate, theMonth)) ? myDate.get(Calendar.DAY_OF_MONTH) : 0;

			/* Determine whether this is month of the currently selected date */
			int iSelected = (isSameMonth(theSelected, theMonth)) ? theSelected.get(Calendar.DAY_OF_MONTH) : 0;
			
			/* Determine whether this is month of the earliest select-able date */
			int iEarliest = (isSameMonth(theEarliest, theMonth)) ? theEarliest.get(Calendar.DAY_OF_MONTH) : 0;
								
			/* Determine whether this is month of the latest select-able date */
			int iLatest   = (isSameMonth(theLatest, theMonth)) ? theLatest.get(Calendar.DAY_OF_MONTH) : 0;
								
			/* Create a copy of the month to display */
			myDate.setTime(theMonth.getTime());
			
			/* Adjust the day to beginning of week if required */
			if (iFirstCol > 0) myDate.add(Calendar.DAY_OF_MONTH, -iFirstCol);
			
			/* Loop through initial columns */
			for(int iDay = myDate.get(Calendar.DAY_OF_MONTH);
				iCol < iFirstCol; 
				iCol++, iDay++, myDate.add(Calendar.DAY_OF_MONTH, 1)) {
				/* Access the label */
				CalendarDay myLabel = theDays[0][iCol];
				
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
				CalendarDay myLabel = theDays[iRow][iCol];
				
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
				CalendarDay myLabel = theDays[iRow][iCol];
				
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
			Calendar myDate = Calendar.getInstance(theLocale);
			
			/* Get the Display Names */ 
			String[] myDays = theSymbols.getShortWeekdays();
			
			/* Build the array of the days of the week */
			int myDoW = myDate.getFirstDayOfWeek();
			for (int i=0; i<7; i++, myDoW++) {
				/* If we are beyond Saturday reset to Sunday */
				if (myDoW > Calendar.SATURDAY) myDoW = Calendar.SUNDAY;
				
				/* Store the day into the array */
				theDaysOfWk[i] = myDoW;
			} 
			
			/* Loop through the labels */
			for (int i=0; i<7; i++) {
				/* Access the label */
				JLabel myLabel  = new JLabel();
				theHdrs[i]		= myLabel;
				
				/* Set the Name */
				myDoW = theDaysOfWk[i];
				myLabel.setText(myDays[myDoW]);
				
				/* Set colour */
				myLabel.setHorizontalAlignment(SwingConstants.CENTER);
				myLabel.setForeground((isWeekend(myDoW)) ? Color.red : Color.black);
				myLabel.setBackground(Color.lightGray);
				myLabel.setOpaque(true);
				
				/* Add to the grid */
				add(myLabel);
			}
		}
	}
	
	/**
	 * CalendarNavigation
	 */
	private class CalendarNavigation extends JPanel implements ActionListener {
		/* Serial Id */
		private static final long serialVersionUID = 4399207012690467687L;

		/**
		 * The Date Label 
		 */
		private JLabel	theDateLabel		= null;
		
		/**
		 * The Buttons
		 */
		private JButton	thePrevMonthButton	= null;
		private JButton	theNextMonthButton	= null;
		private JButton	thePrevYearButton	= null;
		private JButton	theNextYearButton	= null;

		/**
		 * Constructor
		 */
		private CalendarNavigation() {
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
			thePrevMonthButton.addActionListener(this);
			theNextMonthButton.addActionListener(this);
			thePrevYearButton.addActionListener(this);
			theNextYearButton.addActionListener(this);
			
			/* Add these elements into a box */
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			add(thePrevYearButton);
			add(Box.createHorizontalStrut(1));
			add(thePrevMonthButton);
			add(Box.createHorizontalStrut(1));
			add(theDateLabel);
			add(Box.createHorizontalStrut(1));
			add(theNextMonthButton);
			add(Box.createHorizontalStrut(1));
			add(theNextYearButton);
		}
		
		/**
		 * Build month details
		 */
		private void buildMonth() {
			/* Determine the display for the label */
			String myMonth = theMonth.getDisplayName(Calendar.MONTH, Calendar.LONG, theLocale);
			String myYear  = Integer.toString(theMonth.get(Calendar.YEAR));
			
			/* Set the label */
			theDateLabel.setText(myMonth + ", " + myYear);
			
			/* Enable/Disable buttons as required */
			thePrevMonthButton.setEnabled(!isSameMonth(theEarliest, theMonth));
			thePrevYearButton.setEnabled(!isSameYear(theEarliest, theMonth));
			theNextMonthButton.setEnabled(!isSameMonth(theLatest, theMonth));
			theNextYearButton.setEnabled(!isSameYear(theLatest, theMonth));
								
			/* Build the month array */
			theMonthPanel.buildMonth();
		}
		
		/**
		 * Action Listener for buttons
		 */
		public void actionPerformed(ActionEvent e) {
			/* Access the event source */
	    	Object src = e.getSource();
	    	
	    	/* If the button is previous month */
	    	if (src == thePrevMonthButton) {
	    		/* Adjust the month */
	    		theMonth.add(Calendar.MONTH, -1);
	    		buildMonth();
	    	}
	    	else if (src == theNextMonthButton) {
	    		/* Adjust the month */
	    		theMonth.add(Calendar.MONTH, 1);
	    		buildMonth();
	    	}
	    	else if (src == thePrevYearButton) {
	    		/* Adjust the month */
	    		theMonth.add(Calendar.YEAR, -1);
	    		if ((theEarliest != null) && (theMonth.compareTo(theEarliest) < 0)) {
	    			theMonth = duplicateDate(theEarliest);
	    			theMonth.set(Calendar.DAY_OF_MONTH, 1);
	    		}
	    		buildMonth();
	    	}
	    	else if (src == theNextYearButton) {
	    		/* Adjust the month */
	    		theMonth.add(Calendar.YEAR, 1);
	    		if ((theLatest != null) && (theMonth.compareTo(theLatest) > 0)) {
	    			theMonth = duplicateDate(theLatest);
	    			theMonth.set(Calendar.DAY_OF_MONTH, 1);
	    		}
	    		buildMonth();
	    	}
	    }
	}
	
	/**
	 * Constructor
	 * @param pParent
	 */
	public DateSelect(JFrame pParent) { this(pParent, Locale.UK); }

	/**
	 * Constructor
	 * @param pParent
	 * @param pLocale
	 */
	public DateSelect(JFrame 	pParent,
					  Locale	pLocale) {
		/* Initialise the dialog */
		super(pParent, true);

		/* Store locale */
		theLocale 	= pLocale;
		theSymbols 	= DateFormatSymbols.getInstance(theLocale);
		
		/* Create the Month Panel and the navigation */
		theMonthPanel = new CalendarMonth(this);
		theNavigation = new CalendarNavigation();
		
		/* Set this to be the main panel */
		Container myContainer = getContentPane();
		myContainer.add(theNavigation, BorderLayout.NORTH);
		myContainer.add(theMonthPanel, BorderLayout.SOUTH);
		pack();
		
		/* Set the relative location */
		setLocationRelativeTo(pParent);
	}
	
	/**
	 * show the dialog
	 */
	public void showDialog() {
		/* Access Selected Date */
		Calendar myDate = theSelected;
		if (myDate == null) myDate = Calendar.getInstance();
		else myDate = duplicateDate(theSelected);
		
		/* Move to start date if we are earlier */
		if ((theEarliest != null) && (myDate.compareTo(theEarliest) < 0))
			myDate = duplicateDate(theEarliest);
			
		/* Move to end date if we are later */
		if ((theLatest != null) && (myDate.compareTo(theLatest) > 0))
			myDate = duplicateDate(theLatest);

		/* Set to 1st of month and record it */
		myDate.set(Calendar.DAY_OF_MONTH, 1);
		theMonth = myDate;
		
		/* Build detail */
		theNavigation.buildMonth();
		
		/* Show the dialog */
		setVisible(true);
	}
	
	/**
	 * Get the selected date
	 * @return the Selected date
	 */
	public Date getSelected() {
		/* Store the date */
		return (theSelected == null) ? null : theSelected.getTime();
	}
	
	/**
	 * Set the selected date
	 * @param pDate the Selected date
	 */
	public void	setSelected(Date pDate) {
		/* Store the date */
		theSelected = Calendar.getInstance(theLocale);
		theSelected.setTime(pDate); 
	}

	/**
	 * Set the range of allowable dates
	 * @param pEarliest the Earliest select-able date
	 * @param pLatest the Latest select-able date
	 */
	public void	setSelectableRange(Date pEarliest,
								   Date pLatest) {
		/* Null the fields */
		theEarliest = null;
		theLatest	= null;
		
		/* Check that we have a valid range */
		if ((pEarliest != null) && (pLatest != null)) {
			if (pLatest.compareTo(pEarliest) < 0)
				throw new IllegalArgumentException("End date is before Start Date");
		}
		
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
}
