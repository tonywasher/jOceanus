package uk.co.tolcroft.finance.sheets;

import jxl.*;
import jxl.write.*;
import uk.co.tolcroft.finance.core.Threads.*;
import uk.co.tolcroft.finance.data.*;
import uk.co.tolcroft.models.*;
import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;

public class SheetAccount {
	/**
	 * NamedArea for Accounts
	 */
	private static final String Accounts 	   = "Accounts";
	
	/**
	 *  Load the Accounts from an archive
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to load <code>true/false</code> 
	 */
	protected static boolean loadArchive(statusCtl 	pThread,
										 Workbook	pWorkbook,
							   	  		 DataSet	pData) throws Exception {
		/* Local variables */
		Account.List 	myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myCol;
		String    		myAccount;
		String    		myAcType; 
		String    		myParent;
		java.util.Date  myMaturity;
		java.util.Date  myClosed;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try { 
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Accounts);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Accounts)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
		
				/* Count the number of accounts */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of accounts */
				myList = pData.getAccounts();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table in reverse order */
				for (int i = myBottom.getRow();
			     	 i >= myTop.getRow();
			     	 i--) {
				
					/* Access account and account type */
					myAccount = mySheet.getCell(myCol, i).getContents();
					myAcType  = mySheet.getCell(myCol+1, i).getContents();
				
					/* Handle maturity which may be missing */
					myCell     = mySheet.getCell(myCol+2, i);
					myMaturity = null;
					if (myCell.getType() != CellType.EMPTY) {
						myDateCell = (DateCell)myCell;
						myMaturity = myDateCell.getDate();
					}
			
					/* Handle parent which may be missing */
					myCell     = mySheet.getCell(myCol+3, i);
					myParent = null;
					if (myCell.getType() != CellType.EMPTY) {
						myParent = myCell.getContents();
					}
			
					/* Handle closed which may be missing */
					myCell     = mySheet.getCell(myCol+4, i);
					myClosed = null;
					if (myCell.getType() != CellType.EMPTY) {
						myDateCell = (DateCell)myCell;
						myClosed = myDateCell.getDate();
					}
				
					/* Add the value into the finance tables */
					myList.addItem(0,
						           myAccount,
						           myAcType,
						           null,
						           myMaturity,
						           myClosed,
						           myParent,
						           null,
						           null,
						           null,
						           null,
						           null,
						           null,
						           null);
				
					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		/* Handle exceptions */
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to Load Accounts",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Load the Accounts from a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to load from
	 *  @param pData the data set to load into
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean loadBackup(statusCtl 	pThread,
			 							Workbook	pWorkbook,
		   	  				   	        DataSet		pData) throws Exception {
		/* Local variables */
		Account.List 	myList;
		Range[]   		myRange;
		Sheet     		mySheet;
		Cell      		myTop;
		Cell      		myBottom;
		int       		myCol;
		int       		myCols;
		long	  		myID;
		String    		myName;
		String	  		myDesc;
		long      		myAcTypeId;
		long      		myParentId;
		String     		myHexString;
		byte[]     		myInitVector;
		byte[]     		myWebSite;
		byte[]     		myCustNo;
		byte[]     		myUserId;
		byte[]     		myPassword;
		byte[]     		myAccount;
		byte[]     		myNotes;
		java.util.Date  myMaturity;
		java.util.Date  myClosed;
		DateCell  		myDateCell;
		Cell      		myCell;
		int       		myTotal;
		int				mySteps;
		int       		myCount = 0;
		
		/* Protect against exceptions */
		try {
			/* Find the range of cells */
			myRange = pWorkbook.findByName(Accounts);
		
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();
			
			/* Declare the new stage */
			if (!pThread.setNewStage(Accounts)) return false;
		
			/* If we found the range OK */
			if ((myRange != null) && (myRange.length == 1)) {
			
				/* Access the relevant sheet and Cell references */
				mySheet  = pWorkbook.getSheet(myRange[0].getFirstSheetIndex());
				myTop    = myRange[0].getTopLeft();
				myBottom = myRange[0].getBottomRight();
				myCol    = myTop.getColumn();
				myCols   = mySheet.getColumns();
		
				/* Count the number of accounts */
				myTotal  = myBottom.getRow() - myTop.getRow() + 1;
			
				/* Access the list of accounts */
				myList = pData.getAccounts();
			
				/* Declare the number of steps */
				if (!pThread.setNumSteps(myTotal)) return false;
			
				/* Loop through the rows of the table */
				for (int i = myTop.getRow();
					 i <= myBottom.getRow();
					 i++) {
				
					/* Access account and account type */
					myCell    	= mySheet.getCell(myCol, i);
					myID      	= Long.parseLong(myCell.getContents());
					myName 		= mySheet.getCell(myCol+1, i).getContents();
					myCell    	= mySheet.getCell(myCol+2, i);
					myAcTypeId	= Long.parseLong(myCell.getContents());

					/* Handle description which may be missing */
					myDesc = null;
					if (myCols > myCol+3) {
						myCell     = mySheet.getCell(myCol+3, i);
						if (myCell.getType() != CellType.EMPTY) {
							myDesc = myCell.getContents();
						}
					}
			
					/* Handle parentId which may be missing */
					myParentId = -1;
					if (myCols > myCol+4) {
						myCell     = mySheet.getCell(myCol+4, i);
						if (myCell.getType() != CellType.EMPTY) {
							myParentId	= Long.parseLong(myCell.getContents());
						}
					}
				
					/* Handle maturity which may be missing */
					myMaturity = null;
					if (myCols > myCol+5) {
						myCell     = mySheet.getCell(myCol+5, i);
						if (myCell.getType() != CellType.EMPTY) {
							myDateCell = (DateCell)myCell;
							myMaturity = myDateCell.getDate();
						}
					}
			
					/* Handle closed which may be missing */
					myClosed = null;
					if (myCols > myCol+6) {
						myCell     = mySheet.getCell(myCol+6, i);
						if (myCell.getType() != CellType.EMPTY) {
							myDateCell = (DateCell)myCell;
							myClosed = myDateCell.getDate();
						}
					}
				
					/* Handle initVector which may be missing */
					myInitVector = null;
					if (myCols > myCol+7) {
						myCell     = mySheet.getCell(myCol+7, i);
						if (myCell.getType() != CellType.EMPTY) {
							myHexString  = myCell.getContents();
							myInitVector = Utils.BytesFromHexString(myHexString);
						}
					}
				
					/* Handle WebSite which may be missing */
					myWebSite = null;
					if (myCols > myCol+8) {
						myCell     = mySheet.getCell(myCol+8, i);
						if (myCell.getType() != CellType.EMPTY) {
							myHexString  = myCell.getContents();
							myWebSite    = Utils.BytesFromHexString(myHexString);
						}
					}
				
					/* Handle CustNo which may be missing */
					myCustNo = null;
					if (myCols > myCol+9) {
						myCell     = mySheet.getCell(myCol+9, i);
						if (myCell.getType() != CellType.EMPTY) {
							myHexString  = myCell.getContents();
							myCustNo     = Utils.BytesFromHexString(myHexString);
						}
					}
				
					/* Handle userId which may be missing */
					myUserId = null;
					if (myCols > myCol+10) {
						myCell     = mySheet.getCell(myCol+10, i);
						if (myCell.getType() != CellType.EMPTY) {
							myHexString  = myCell.getContents();
							myUserId     = Utils.BytesFromHexString(myHexString);
						}
					}
				
					/* Handle password which may be missing */
					myPassword = null;
					if (myCols > myCol+11) {
						myCell     = mySheet.getCell(myCol+11, i);
						if (myCell.getType() != CellType.EMPTY) {
							myHexString  = myCell.getContents();
							myPassword   = Utils.BytesFromHexString(myHexString);
						}
					}
				
					/* Handle account which may be missing */
					myAccount = null;
					if (myCols > myCol+12) {
						myCell     = mySheet.getCell(myCol+12, i);
						if (myCell.getType() != CellType.EMPTY) {
							myHexString  = myCell.getContents();
							myAccount    = Utils.BytesFromHexString(myHexString);
						}
					}
				
					/* Handle notes which may be missing */
					myNotes = null;
					if (myCols > myCol+13) {
						myCell     = mySheet.getCell(myCol+13, i);
						if (myCell.getType() != CellType.EMPTY) {
							myHexString  = myCell.getContents();
							myNotes      = Utils.BytesFromHexString(myHexString);
						}
					}
				
					/* Add the value into the finance tables */
					myList.addItem(myID,
						           myName,
						           myAcTypeId,
						           myDesc,
						           myMaturity,
						           myClosed,
						           myParentId,
							       myInitVector,
							       myWebSite,
							       myCustNo,
							       myUserId,
							       myPassword,
							       myAccount,
							       myNotes);
				
					/* Report the progress */
					myCount++;
					if ((myCount % mySteps) == 0) 
						if (!pThread.setStepsDone(myCount)) return false;
				}
			}
		}
		
		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to load Accounts",
								e);
		}
		
		/* Return to caller */
		return true;
	}
	
	/**
	 *  Write the Accounts to a backup
	 *  @param pThread   the thread status control
	 *  @param pWorkbook the workbook to write to
	 *  @param pData the data set to write from
	 *  @return continue to write <code>true/false</code> 
	 */
	protected static boolean writeBackup(statusCtl 			pThread,
										 WritableWorkbook	pWorkbook,
		   	        					 DataSet			pData) throws Exception {
		WritableSheet 					mySheet;
		DataList<Account>.ListIterator	myIterator;
		Account.List					myAccounts;
		Account							myCurr;
		int								myRow;
		int								myCount;
		int								myTotal;
		int								mySteps;
		jxl.write.Label					myCell;
		jxl.write.DateTime				myDate;
		WritableCellFormat				myFormat;

		/* Protect against exceptions */
		try { 
			/* Declare the new stage */
			if (!pThread.setNewStage(Accounts)) return false;
		
			/* Create the formats */
			myFormat		= new WritableCellFormat(DateFormats.FORMAT2);
	
			/* Create the sheet */
			mySheet    = pWorkbook.createSheet(Accounts, 0);
	
			/* Access the number of reporting steps */
			mySteps = pThread.getReportingSteps();

			/* Access the accounts */
			myAccounts = pData.getAccounts();
	
			/* Count the number of accounts */
			myTotal   = myAccounts.size();
		
			/* Declare the number of steps */
			if (!pThread.setNumSteps(myTotal)) return false;
		
			/* Initialise counts */
			myRow   = 0;
			myCount = 0;
		
			/* Access the iterator */
			myIterator = myAccounts.listIterator();
			
			/* Loop through the account */
			while ((myCurr  = myIterator.next()) != null) {
				/* Create the Identifier cell */
				myCell   = new jxl.write.Label(0, myRow, 
											   Long.toString(myCurr.getId()));
				mySheet.addCell(myCell);
				myCell   = new jxl.write.Label(2, myRow,
											   Long.toString(myCurr.getActType().getId()));
				mySheet.addCell(myCell);
				if (myCurr.getParent() != null) {
					myCell   = new jxl.write.Label(4, myRow, 
										           Long.toString(myCurr.getParent().getId()));
					mySheet.addCell(myCell);
				}
			
				/* Create the String cells */
				myCell = new jxl.write.Label(1, myRow, myCurr.getName());
				mySheet.addCell(myCell);
				if (myCurr.getDesc() != null) {
					myCell = new jxl.write.Label(3, myRow, myCurr.getDesc());
					mySheet.addCell(myCell);
				}
				
				/* Create the Date cells */
				if (myCurr.getMaturity() != null) {
					myDate = new jxl.write.DateTime(5, myRow, 
					   						    	myCurr.getMaturity().getDate(),
					   						    	myFormat);
					mySheet.addCell(myDate);
				}
				if (myCurr.getClose() != null) {
					myDate = new jxl.write.DateTime(6, myRow, 
					   						    	myCurr.getClose().getDate(),
					   						    	myFormat);
					mySheet.addCell(myDate);
				}
			
				/* Create the Security cells */
				if (myCurr.getInitVector() != null) {
					myCell = new jxl.write.Label(7, myRow, 
											     Utils.HexStringFromBytes(myCurr.getInitVector()));
					mySheet.addCell(myCell);
				}
				if (myCurr.getWebSite() != null) {
					myCell = new jxl.write.Label(7, myRow, 
											     Utils.HexStringFromBytes(myCurr.getWebSite()));
					mySheet.addCell(myCell);
				}
				if (myCurr.getCustNo() != null) {
					myCell = new jxl.write.Label(7, myRow, 
											     Utils.HexStringFromBytes(myCurr.getCustNo()));
					mySheet.addCell(myCell);
				}
				if (myCurr.getUserId() != null) {
					myCell = new jxl.write.Label(10, myRow, 
											     Utils.HexStringFromBytes(myCurr.getUserId()));
					mySheet.addCell(myCell);
				}
				if (myCurr.getPassword() != null) {
					myCell = new jxl.write.Label(11, myRow, 
											     Utils.HexStringFromBytes(myCurr.getPassword()));
					mySheet.addCell(myCell);
				}
				if (myCurr.getAccount() != null) {
					myCell = new jxl.write.Label(12, myRow, 
											     Utils.HexStringFromBytes(myCurr.getAccount()));
					mySheet.addCell(myCell);
				}
				if (myCurr.getNotes() != null) {
					myCell = new jxl.write.Label(13, myRow, 
											     Utils.HexStringFromBytes(myCurr.getNotes()));
					mySheet.addCell(myCell);
				}

				/* Report the progress */
				myCount++;
				myRow++;
				if ((myCount % mySteps) == 0) 
					if (!pThread.setStepsDone(myCount)) return false;
			}
	
			/* Add the Range name */
			if (myRow > 0)
				pWorkbook.addNameArea(Accounts, mySheet, 0, 0, 13, myRow-1);
		}

		catch (Throwable e) {
			throw new Exception(ExceptionClass.EXCEL, 
								"Failed to create Accounts",
								e);
		}
	
		/* Return to caller */
		return true;
	}
	
}
