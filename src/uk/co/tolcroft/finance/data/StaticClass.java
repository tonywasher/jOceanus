package uk.co.tolcroft.finance.data;

import uk.co.tolcroft.models.Exception;
import uk.co.tolcroft.models.Exception.*;
import uk.co.tolcroft.models.StaticData.*;

public class StaticClass {
	/**
	 * StaticData Name length
	 */
	public final static int NAMELEN = 50;

	/**
	 * StaticData Description length
	 */
	public final static int DESCLEN = 100;

	/**
	 * Enumeration of Account Type Classes. 
	 */
	public enum AccountClass implements StaticInterface {
		/**
		 * Current Banking Account
		 */
		CURRENT(1),

		/**
		 * Instant Access Savings Account
		 */
		INSTANT(2),
		
		/**
		 * Savings Account Requiring Notice for Withdrawals
		 */
		NOTICE(3),

		/**
		 * Fixed Rate Savings Bond
		 */
		BOND(4),
		
		/**
		 * Instant Access Cash ISA Account
		 */
		CASHISA(5),

		/**
		 * Fixed Rate Cash ISA Bond
		 */
		ISABOND(6),

		/**
		 * Index Linked Bond
		 */
		TAXFREEBOND(7),

		/**
		 * Equity Bond
		 */
		EQUITYBOND(8),

		/**
		 * Shares
		 */
		SHARES(9),

		/**
		 * Unit Trust or OEIC
		 */
		UNITTRUST(10),

		/**
		 * Life Bond
		 */
		LIFEBOND(11),

		/**
		 * Unit Trust or OEIC in ISA wrapper
		 */
		UNITISA(12),

		/**
		 * Car
		 */
		CAR(13),

		/**
		 * House
		 */
		HOUSE(14),

		/**
		 * Debts
		 */
		DEBTS(15),

		/**
		 * CreditCard
		 */
		CREDITCARD(16),

		/**
		 * WriteOff
		 */
		WRITEOFF(17),

		/**
		 * External Account
		 */
		EXTERNAL(18),

		/**
		 * Employer Account
		 */
		EMPLOYER(19),

		/**
		 * Asset Owner Account
		 */
		OWNER(20),

		/**
		 * Market
		 */
		MARKET(21),

		/**
		 * Inland Revenue
		 */
		TAXMAN(22),

		/**
		 * Cash
		 */
		CASH(23),

		/**
		 * Inheritance
		 */
		INHERITANCE(24),

		/**
		 * Endowment
		 */
		ENDOWMENT(25),

		/**
		 * Benefit
		 */
		BENEFIT(26),

		/**
		 * Deferred between tax years
		 */
		DEFERRED(27);
		
		/**
		 * Class Id
		 */
		private int theId		= -1;
		
		/**
		 * Class Order
		 */
		private int theOrder	= -1;
		
		/**
		 * Obtain Class Id
		 * @return the class id
		 */
		public int getClassId() { return theId; }
		
		/**
		 * Obtain Class Order
		 * @return the class order
		 */
		public int getOrder() 	{
			if (theOrder == -1) theOrder = calculateOrder(this);
			return theOrder; 
		}
		
		/**
		 * Obtain order
		 * @param pClass the account class
		 * @return the order
		 */
		private static int calculateOrder(AccountClass pClass) {
			/* Switch on id */
			switch(pClass) {
				case CURRENT: 		return 0;
				case INSTANT:		return 1;
				case NOTICE: 		return 2;
				case BOND:			return 3;
				case CASHISA: 		return 4;
				case ISABOND: 		return 5;
				case TAXFREEBOND:	return 6;
				case EQUITYBOND: 	return 7;
				case SHARES:		return 8;
				case UNITTRUST: 	return 9;
				case LIFEBOND: 		return 10;
				case UNITISA:		return 11;
				case CAR: 			return 12;
				case HOUSE:			return 13;
				case ENDOWMENT: 	return 14;
				case CREDITCARD: 	return 15;
				case DEBTS:			return 16;
				case DEFERRED: 		return 17;
				case EMPLOYER:		return 18;
				case CASH: 			return 19;
				case TAXMAN: 		return 20;
				case INHERITANCE:	return 21;
				case WRITEOFF: 		return 22;
				case BENEFIT:		return 23;
				case EXTERNAL: 		return 24;
				case OWNER: 		return 25;
				case MARKET: 	
				default:			return 26;
			}
		}

		/**
		 * Constructor
		 */
		private AccountClass(int uId) {
			theId 		= uId;
		}

		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static AccountClass fromId(int id) throws Exception {
			for (AccountClass myClass: values()) {	if (myClass.getClassId() == id) return myClass; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid Frequency Class Id: " + id);
		}
	}
	
	/**
	 * Enumeration of Transaction Type Classes. 
	 */
	public enum TransClass implements StaticInterface {
		/**
		 * Taxed Salary Income
		 */
		TAXEDINCOME(1),

		/**
		 * Interest Income
		 */
		INTEREST(2),

		/**
		 * Dividend Income
		 */
		DIVIDEND(3),

		/**
		 * Unit Trust Dividend Income
		 */
		UNITTRUSTDIVIDEND(4),

		/**
		 * Taxable Gain
		 */
		TAXABLEGAIN(5),

		/**
		 * Capital Gain
		 */
		CAPITALGAIN(6),

		/**
		 * Capital Loss
		 */
		CAPITALLOSS(7),

		/**
		 * Tax Free Interest
		 */
		TAXFREEINTEREST(8),

		/**
		 * Tax Free Dividend
		 */
		TAXFREEDIVIDEND(9),

		/**
		 * Tax Free Income
		 */
		TAXFREEINCOME(10),

		/**
		 * Benefit
		 */
		BENEFIT(11),     

		/**
		 * Inheritance
		 */
		INHERITED(12),

		/**
		 * Market Growth
		 */
		MARKETGROWTH(13),

		/**
		 * Market Shrinkage
		 */
		MARKETSHRINK(14),

		/**
		 * Expense
		 */
		EXPENSE(15),

		/**
		 * Recovered Expense
		 */
		RECOVERED(16),

		/**
		 * Transfer
		 */
		TRANSFER(17),

		/**
		 * Admin charge
		 */
		ADMINCHARGE(18),

		/**
		 * Stock Split
		 */
		STOCKSPLIT(19),

		/**
		 * Stock Demerger
		 */
		STOCKDEMERGER(20),

		/**
		 * Stock Rights Taken
		 */
		STOCKRIGHTTAKEN(21),

		/**
		 * Stock Rights Waived
		 */
		STOCKRIGHTWAIVED(22),

		/**
		 * CashTakeover (For the cash part of a stock and cash takeover)
		 */
		CASHTAKEOVER(23),

		/**
		 * Stock Takeover (for the stock part of a stock and cash takeover)
		 */
		STOCKTAKEOVER(24),

		/**
		 * Expense Recovered directly to Cash
		 */
		CASHRECOVERY(25),

		/**
		 * Expense paid directly from Cash
		 */
		CASHPAYMENT(26),

		/**
		 * Endowment payment
		 */
		ENDOWMENT(27),

		/**
		 * Mortgage charge
		 */
		MORTGAGE(28),

		/**
		 * Insurance payment
		 */
		INSURANCE(29),

		/**
		 * National Insurance
		 */
		NATINSURANCE(30),

		/**
		 * Tax Relief
		 */
		TAXRELIEF(31),    

		/**
		 * Tax Owed
		 */
		TAXOWED(32),      

		/**
		 * Tax Refund
		 */
		TAXREFUND(33),    

		/**
		 * Additional taxation
		 */
		EXTRATAX(34),

		/**
		 * Interest on Debts
		 */
		DEBTINTEREST(35), 

		/**
		 * Write Off
		 */
		WRITEOFF(36),

		/**
		 * Tax Credit
		 */
		TAXCREDIT(37),

		/**
		 * Rental Income
		 */
		RENTALINCOME(38);
		
		/**
		 * Class Id
		 */
		private int theId		= -1;
		
		/**
		 * Class Order
		 */
		private int theOrder	= -1;
		
		/**
		 * Obtain Class Id
		 * @return the class id
		 */
		public int getClassId() { return theId; }
		
		/**
		 * Obtain Class Order
		 * @return the class order
		 */
		public int getOrder() 	{
			if (theOrder == -1) theOrder = calculateOrder(this);
			return theOrder; 
		}
		
		/**
		 * Obtain order
		 * @param pClass the account class
		 * @return the order
		 */
		private static int calculateOrder(TransClass pClass) {
			/* Switch on id */
			switch(pClass) {
				case TAXEDINCOME: 		return 0;
				case INTEREST:			return 1;
				case DIVIDEND: 			return 2;
				case TAXFREEINCOME:		return 3;
				case INHERITED: 		return 4;
				case DEBTINTEREST:		return 5;
				case RENTALINCOME:		return 6;
				case BENEFIT: 			return 7;
				case TAXREFUND:			return 8;
				case RECOVERED: 		return 9;
				case TAXRELIEF: 		return 10;
				case STOCKDEMERGER:		return 11;
				case ADMINCHARGE:		return 12;
				case STOCKSPLIT:		return 13;
				case STOCKRIGHTTAKEN: 	return 14;
				case STOCKRIGHTWAIVED: 	return 15;
				case CASHTAKEOVER:		return 16;
				case STOCKTAKEOVER:		return 17;
				case TAXABLEGAIN:		return 18;
				case TRANSFER: 			return 19;
				case CASHRECOVERY: 		return 20;
				case CASHPAYMENT:		return 21;
				case EXPENSE: 			return 22;
				case ENDOWMENT:			return 23;
				case MORTGAGE: 			return 24;
				case INSURANCE: 		return 25;
				case EXTRATAX:			return 26;
				case WRITEOFF:			return 27;
				case NATINSURANCE:		return 28;
				case TAXOWED: 			return 29;
				case TAXCREDIT: 		return 30;
				case MARKETGROWTH:		return 31;
				case MARKETSHRINK:		return 32;
				case UNITTRUSTDIVIDEND:	return 33;
				case TAXFREEINTEREST:	return 34;
				case TAXFREEDIVIDEND:	return 35;
				case CAPITALGAIN:		return 35;
				case CAPITALLOSS: 	
				default:				return 37;
			}
		}

		/**
		 * Constructor
		 */
		private TransClass(int uId) {
			theId 		= uId;
		}

		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static TransClass fromId(int id) throws Exception {
			for (TransClass myClass: values()) {	if (myClass.getClassId() == id) return myClass; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid Frequency Class Id: " + id);
		}
	}
	
	/**
	 * Enumeration of Tax Type Buckets
	 */
	public enum TaxBucket {
		TRANSSUMM, TRANSTOTAL, TAXDETAIL, TAXSUMM, TAXTOTAL;
	}
	
	/**
	 * Enumeration of Tax Type Classes. 
	 */
	public enum TaxClass implements StaticInterface {
		/**
		 * Gross Salary Income
		 */
		GROSSSALARY(1, TaxBucket.TRANSSUMM),

		/**
		 * Gross Interest Income
		 */
		GROSSINTEREST(2, TaxBucket.TRANSSUMM),

		/**
		 * Gross Dividend Income
		 */
		GROSSDIVIDEND(3, TaxBucket.TRANSSUMM),

		/**
		 * Gross Unit Trust Dividend Income
		 */
		GROSSUTDIVS(4, TaxBucket.TRANSSUMM),

		/**
		 * Gross Rental Income
		 */
		GROSSRENTAL(5, TaxBucket.TRANSSUMM),

		/**
		 * Gross Taxable gains
		 */
		GROSSTAXGAINS(6, TaxBucket.TRANSSUMM),

		/**
		 * Gross Capital gains
		 */
		GROSSCAPGAINS(7, TaxBucket.TRANSSUMM),

		/**
		 * Total Tax Paid
		 */
		TAXPAID(8, TaxBucket.TRANSSUMM),

		/**
		 * Market Growth/Shrinkage
		 */
		MARKET(9, TaxBucket.TRANSSUMM),     

		/**
		 * Tax Free Income
		 */
		TAXFREE(10, TaxBucket.TRANSSUMM),

		/**
		 * Gross Expense
		 */
		EXPENSE(11, TaxBucket.TRANSSUMM),

		/**
		 * Virtual Income
		 */
		VIRTUAL(12, TaxBucket.TRANSSUMM),

		/**
		 * Non-Core Income
		 */
		NONCORE(13, TaxBucket.TRANSSUMM),

		/**
		 * Profit on Year
		 */
		PROFITLOSS(14, TaxBucket.TRANSTOTAL),
		
		/**
		 * Core Income after tax ignoring market movements and inheritance
		 */
		COREINCOME(15, TaxBucket.TRANSTOTAL),
		
		/**
		 * Profit on year after ignoring market movements and inheritance
		 */
		COREPROFITLOSS(16, TaxBucket.TRANSTOTAL),
		
		/**
		 * Gross Income
		 */
		GROSSINCOME(17, TaxBucket.TAXDETAIL),

		/**
		 * Original Allowance
		 */
		ORIGALLOW(18, TaxBucket.TAXDETAIL),

		/**
		 * Adjusted Allowance
		 */
		ADJALLOW(19, TaxBucket.TAXDETAIL),

		/**
		 * High Tax Band
		 */
		HITAXBAND(20, TaxBucket.TAXDETAIL),

		/**
		 * Salary at nil-rate
		 */
		SALARYFREE(21, TaxBucket.TAXDETAIL),

		/**
		 * Salary at low-rate
		 */
		SALARYLO(22, TaxBucket.TAXDETAIL),

		/**
		 * Salary at basic-rate
		 */
		SALARYBASIC(23, TaxBucket.TAXDETAIL),

		/**
		 * Salary at high-rate
		 */
		SALARYHI(24, TaxBucket.TAXDETAIL),

		/**
		 * Salary at additional-rate
		 */
		SALARYADD(25, TaxBucket.TAXDETAIL),

		/**
		 * Rental at nil-rate
		 */
		RENTALFREE(26, TaxBucket.TAXDETAIL),

		/**
		 * Rental at low-rate
		 */
		RENTALLO(27, TaxBucket.TAXDETAIL),

		/**
		 * Rental at basic-rate
		 */
		RENTALBASIC(28, TaxBucket.TAXDETAIL),

		/**
		 * Rental at high-rate
		 */
		RENTALHI(29, TaxBucket.TAXDETAIL),

		/**
		 * Rental at additional-rate
		 */
		RENTALADD(30, TaxBucket.TAXDETAIL),

		/**
		 * Interest at nil-rate
		 */
		INTERESTFREE(31, TaxBucket.TAXDETAIL),

		/**
		 * Interest at low-rate
		 */
		INTERESTLO(32, TaxBucket.TAXDETAIL),

		/**
		 * Interest at basic-rate
		 */
		INTERESTBASIC(33, TaxBucket.TAXDETAIL),

		/**
		 * Interest at high-rate
		 */
		INTERESTHI(34, TaxBucket.TAXDETAIL),

		/**
		 * Interest at additional-rate
		 */
		INTERESTADD(35, TaxBucket.TAXDETAIL),

		/**
		 * Dividends at basic-rate
		 */
		DIVIDENDBASIC(36, TaxBucket.TAXDETAIL),

		/**
		 * Dividends at high-rate
		 */
		DIVIDENDHI(37, TaxBucket.TAXDETAIL),

		/**
		 * Dividends at additional-rate
		 */
		DIVIDENDADD(38, TaxBucket.TAXDETAIL),

		/**
		 * Slice at basic-rate
		 */
		SLICEBASIC(39, TaxBucket.TAXDETAIL),

		/**
		 * Slice at high-rate
		 */
		SLICEHI(40, TaxBucket.TAXDETAIL),

		/**
		 * Slice at additional-rate
		 */
		SLICEADD(41, TaxBucket.TAXDETAIL),

		/**
		 * Gains at basic-rate
		 */
		GAINSBASIC(42, TaxBucket.TAXDETAIL),

		/**
		 * Gains at high-rate
		 */
		GAINSHI(43, TaxBucket.TAXDETAIL),

		/**
		 * Gains at additional-rate
		 */
		GAINSADD(44, TaxBucket.TAXDETAIL),

		/**
		 * Capital at nil-rate
		 */
		CAPITALFREE(45, TaxBucket.TAXDETAIL),

		/**
		 * Capital at basic-rate
		 */
		CAPITALBASIC(46, TaxBucket.TAXDETAIL),

		/**
		 * Capital at high-rate
		 */
		CAPITALHI(47, TaxBucket.TAXDETAIL),

		/**
		 * Total Taxation Due on Salary
		 */
		TAXDUESALARY(48, TaxBucket.TAXSUMM),

		/**
		 * Total Taxation Due on Rental
		 */
		TAXDUERENTAL(49, TaxBucket.TAXSUMM),

		/**
		 * Total Taxation Due on Interest
		 */
		TAXDUEINTEREST(50, TaxBucket.TAXSUMM),

		/**
		 * Total Taxation Due on Dividends
		 */
		TAXDUEDIVIDEND(51, TaxBucket.TAXSUMM),

		/**
		 * Total Taxation Due on Taxable Gains
		 */
		TAXDUETAXGAINS(52, TaxBucket.TAXSUMM),

		/**
		 * Total Taxation Due on Slice
		 */
		TAXDUESLICE(53, TaxBucket.TAXSUMM),

		/**
		 * Total Taxation Due on Capital Gains
		 */
		TAXDUECAPGAINS(54, TaxBucket.TAXSUMM),

		/**
		 * Total Taxation Due
		 */
		TOTALTAXATION(55, TaxBucket.TAXTOTAL),

		/**
		 * Taxation Profit (TaxDue-TaxPaid)
		 */
		TAXPROFITLOSS(56, TaxBucket.TAXTOTAL);

		/**
		 * Class Id
		 */
		private int 		theId		= -1;
		
		/**
		 * Class Order
		 */
		private int 		theOrder	= -1;
		
		/**
		 * Class Bucket
		 */
		private TaxBucket 	theBucket	= null;
		
		/**
		 * Obtain Class Id
		 * @return the class id
		 */
		public int getClassId() { return theId; }
		
		/**
		 * Obtain Class Bucket
		 * @return the class bucket
		 */
		public TaxBucket getClassBucket() { return theBucket; }
		
		/**
		 * Obtain Class Order
		 * @return the class order
		 */
		public int getOrder() 	{
			if (theOrder == -1) theOrder = calculateOrder(this);
			return theOrder; 
		}
		
		/**
		 * Obtain order
		 * @param pClass the frequency class
		 * @return the order
		 */
		private static int calculateOrder(TaxClass pClass) {
			int myClassBase = 0;
			
			/* switch on bucket */
			switch (pClass.getClassBucket()) {
				case TRANSSUMM: 	myClassBase = 0; 	break;
				case TRANSTOTAL: 	myClassBase = 100;	break;
				case TAXDETAIL: 	myClassBase = 200; 	break;
				case TAXSUMM: 		myClassBase = 300; 	break;
				case TAXTOTAL: 		myClassBase = 400; 	break;
			}
			
			/* Switch on id */
			switch(pClass) {
				case GROSSSALARY: 		return myClassBase + 0;
				case GROSSINTEREST:		return myClassBase + 1;
				case GROSSDIVIDEND: 	return myClassBase + 2;
				case GROSSUTDIVS:		return myClassBase + 3; 
				case GROSSRENTAL: 		return myClassBase + 4;
				case GROSSTAXGAINS:		return myClassBase + 5; 
				case GROSSCAPGAINS: 	return myClassBase + 6; 
				case TAXPAID:			return myClassBase + 7;
				case TAXFREE:	 		return myClassBase + 8;
				case MARKET:			return myClassBase + 9;
				case EXPENSE: 			return myClassBase + 10;
				case VIRTUAL:			return myClassBase + 11; 
				case NONCORE:			return myClassBase + 12; 
				
				case PROFITLOSS:		return myClassBase + 0; 
				case COREPROFITLOSS:	return myClassBase + 1; 
				case COREINCOME:		return myClassBase + 2; 
				
				case GROSSINCOME:		return myClassBase + 0; 
				case ORIGALLOW:			return myClassBase + 1; 
				case ADJALLOW:			return myClassBase + 2; 
				case HITAXBAND:			return myClassBase + 3; 
				case SALARYFREE:		return myClassBase + 4; 
				case RENTALFREE:		return myClassBase + 5; 
				case INTERESTFREE:		return myClassBase + 6; 
				case CAPITALFREE:		return myClassBase + 7; 
				case SALARYLO:			return myClassBase + 8; 
				case RENTALLO:			return myClassBase + 9; 
				case INTERESTLO:		return myClassBase + 10; 
				case SALARYBASIC:		return myClassBase + 11; 
				case RENTALBASIC:		return myClassBase + 12; 
				case INTERESTBASIC:		return myClassBase + 13; 
				case DIVIDENDBASIC:		return myClassBase + 14; 
				case SLICEBASIC:		return myClassBase + 15; 
				case GAINSBASIC:		return myClassBase + 16; 
				case CAPITALBASIC:		return myClassBase + 17; 
				case SALARYHI:			return myClassBase + 18; 
				case RENTALHI:			return myClassBase + 19; 
				case INTERESTHI:		return myClassBase + 20; 
				case DIVIDENDHI:		return myClassBase + 21; 
				case SLICEHI:			return myClassBase + 22; 
				case GAINSHI:			return myClassBase + 23; 
				case CAPITALHI:			return myClassBase + 24; 
				case SALARYADD:			return myClassBase + 25; 
				case RENTALADD:			return myClassBase + 26; 
				case INTERESTADD:		return myClassBase + 27; 
				case DIVIDENDADD:		return myClassBase + 28; 
				case SLICEADD:			return myClassBase + 29; 
				case GAINSADD:			return myClassBase + 30; 
				case TAXDUESLICE:		return myClassBase + 31; 
				
				case TAXDUESALARY:		return myClassBase + 0; 
				case TAXDUERENTAL:		return myClassBase + 1; 
				case TAXDUEINTEREST:	return myClassBase + 2; 
				case TAXDUEDIVIDEND:	return myClassBase + 3; 
				case TAXDUETAXGAINS:	return myClassBase + 4; 
				case TAXDUECAPGAINS:	return myClassBase + 5; 
				
				case TOTALTAXATION:		return myClassBase + 0;  
				case TAXPROFITLOSS:
				default:				return myClassBase + 1;  
			}
		}

		/**
		 * Constructor
		 */
		private TaxClass(int uId, TaxBucket pBucket) {
			theId 		= uId;
			theBucket	= pBucket;
		}

		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static TaxClass fromId(int id) throws Exception {
			for (TaxClass myClass: values()) {	if (myClass.getClassId() == id) return myClass; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid Tax Class Id: " + id);
		}
	}
	
	/**
	 * Enumeration of Frequency Classes. 
	 */
	public enum FreqClass implements StaticInterface {
		/**
		 * Monthly Frequency
		 */
		MONTHLY(1),

		/**
		 * Monthly Frequency (at end of month)
		 */
		ENDOFMONTH(2),

		/**
		 * Quarterly Frequency
		 */
		QUARTERLY(3),
		
		/**
		 * Half Yearly Frequency
		 */
		HALFYEARLY(4),

		/**
		 * Annual Frequency
		 */
		ANNUALLY(5),

		/**
		 * Only on Maturity
		 */
		MATURITY(6),

		/**
		 * Monthly for up to ten-months
		 */
		TENMONTHS(7);
		
		/**
		 * Class Id
		 */
		private int theId		= -1;
		
		/**
		 * Class Order
		 */
		private int theOrder	= -1;
		
		/**
		 * Obtain Class Id
		 * @return the class id
		 */
		public int getClassId() { return theId; }
		
		/**
		 * Obtain Class Order
		 * @return the class order
		 */
		public int getOrder() 	{
			if (theOrder == -1) theOrder = calculateOrder(this);
			return theOrder; 
		}
		
		/**
		 * Obtain order
		 * @param pClass the frequency class
		 * @return the order
		 */
		private static int calculateOrder(FreqClass pClass) {
			/* Switch on id */
			switch(pClass) {
				case MONTHLY: 		return 0;
				case ENDOFMONTH:	return 1;
				case QUARTERLY: 	return 2;
				case HALFYEARLY:	return 3;
				case ANNUALLY: 		return 4;
				case MATURITY: 		return 5;
				case TENMONTHS: 	
				default:			return 6;
			}
		}

		/**
		 * Constructor
		 */
		private FreqClass(int uId) {
			theId 		= uId;
		}

		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static FreqClass fromId(int id) throws Exception {
			for (FreqClass myClass: values()) {	if (myClass.getClassId() == id) return myClass; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid Frequency Class Id: " + id);
		}
	}
	
	/**
	 * Enumeration of TaxRegime Classes. 
	 */
	public enum TaxRegClass implements StaticInterface {
		/**
		 * Archive tax regime
		 */
		ARCHIVE(1),

		/**
		 * Standard tax regime
		 */
		STANDARD(2),

		/**
		 * Low Interest Tax Band
		 */
		LOINTEREST(3),

		/**
		 * Additional tax band
		 */
		ADDITIONALBAND(4);
		
		/**
		 * Class Id
		 */
		private int theId		= -1;
		
		/**
		 * Class Order
		 */
		private int theOrder	= -1;
		
		/**
		 * Obtain Class Id
		 * @return the class id
		 */
		public int getClassId() { return theId; }
		
		/**
		 * Obtain Class Order
		 * @return the class order
		 */
		public int getOrder() 	{
			if (theOrder == -1) theOrder = calculateOrder(this);
			return theOrder; 
		}
		
		/**
		 * Obtain order
		 * @param pClass the Tax Regime class
		 * @return the order
		 */
		private static int calculateOrder(TaxRegClass pClass) {
			/* Switch on id */
			switch(pClass) {
				case ARCHIVE: 		return 0;
				case STANDARD:		return 1;
				case LOINTEREST: 	return 2;
				case ADDITIONALBAND: 	
				default:			return 3;
			}
		}

		/**
		 * Constructor
		 */
		private TaxRegClass(int uId) {
			theId 		= uId;
		}

		/**
		 * get value from id
		 * @param id the id value
		 * @return the corresponding enum object
		 */
		public static TaxRegClass fromId(int id) throws Exception {
			for (TaxRegClass myClass: values()) {	if (myClass.getClassId() == id) return myClass; }
			throw new Exception(ExceptionClass.DATA,
								"Invalid Tax Regime Class Id: " + id);
		}
	}	
}
