package finance;

import java.util.Date;

import finance.finData.Account;
import finance.finData.Rate;
import finance.finData.Price;
import finance.finData.Pattern;
import finance.finData.Event;
import finance.finData.TaxParms;
import finance.finObject.ExceptionClass;
import finance.finObject.ObjectClass;
import finance.finObject.State;

/**
 * Provides Builder functions for constructing data on load from external sources.
 * On initialisation it will create an empty data set with either a new ID manager or one 
 * that has previously been used.   
 *   
 * @author 	Tony Washer
 * @version 1.0
 * 
 * @see finBuilder.IdManager
 */
public class finBuilder {

	/**
	 * The Data that is being built
	 */
	private finData           theData      = null;
	
	/**
	 * The static data that is being built
	 */
	private finStatic         theStatic    = null;
	
	/**
	 * The IdManager for controlling IDs in this DataSet
	 */
	private IdManager 		  theIdManager = null;

	/**
	 * The input data version (allows migration of data)
	 */
	/*private int				  theInputVersion = finProperties.DATAVERSION_0;*/
	
	/**
	 * Retrieve the DataSet that has been built
	 * @return the  DataSet
	 */
	public finData getData()    { return theData; }
		
	/**
	 * Standard constructor
	 */
	public finBuilder() {
		theData      = new finData();
		theStatic    = theData.getStatic();
		theIdManager = theData.getIdManager();
	}
	
	/**
	 * Constructor for reusing an existing IdManager
	 * @param pManager the IdManager to reuse
	 */
	public finBuilder(IdManager pManager) {
		theData      = new finData(pManager);
		theStatic    = theData.getStatic();
		theIdManager = pManager;
	}

	/**
	 * Set Data version 
	 * @param pVersion the version
	 */
	public void setDataVersion(long pVersion) {
		theData.setDataVersion(pVersion);
	}
	
	/**
	 * Add an AccountType
	 * @param uId the Id of the account type
	 * @param pActType the Name of the account type
	 * @throws finObject.Exception on error
	 */ 
	public void addAccountType(long   uId,
			                   String pActType) throws finObject.Exception {
		finStatic.AccountType myActType;
		finStatic.ActTypeList myActTypes;
			
		/* Access the transaction types */
		myActTypes = theData.getActTypes();
			
		/* Create a new Account Type */
		myActType = theStatic.new AccountType(myActTypes, uId, pActType);
			
		/* Check that this AccountTypeId has not been previously added */
		if ((theIdManager.checkActTypeID(uId)) &&
			(myActTypes.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
                      					  ObjectClass.ACCOUNTTYPE,
                      					  myActType,
		  			                      "Duplicate AccountTypeId");
			 
		/* Check that this AccountType has not been previously added */
		if (myActTypes.searchFor(pActType) != null) 
			throw new finObject.Exception(ExceptionClass.DATA,
   					  					  ObjectClass.ACCOUNTTYPE,
   					  					  myActType,
		  			                      "Duplicate Account Type");
			 
		/* Add the Account Type to the list */
		myActType.addToList();
	}
		
	/**
	 * Add a TransactionType
	 * @param uId the Id of the transaction type
	 * @param pTransType the Name of the transaction type
	 * @throws finObject.Exception on error
	 */ 
	public void addTransType(long   uId,
			                 String pTransType) throws finObject.Exception {
		finStatic.TransType     myTransType;
		finStatic.TransTypeList myTransTypes;
		
		/* Access the transaction types */
		myTransTypes = theData.getTransTypes();
		
		/* Create a new Transaction Type */
		myTransType = theStatic.new TransType(myTransTypes, uId, pTransType);
		
		/* Check that this TransTypeId has not been previously added */
		if ((theIdManager.checkTransTypeID(uId)) &&
		    (myTransTypes.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
   					  					  ObjectClass.TRANSTYPE,
   					  					  myTransType,
		  			                      "Duplicate TransTypeId");
			 
		/* Check that this TransactionType has not been previously added */
		if (myTransTypes.searchFor(pTransType) != null) 
			throw new finObject.Exception(ExceptionClass.DATA,
  					  					  ObjectClass.TRANSTYPE,
  					  					  myTransType,
		                                  "Duplicate Transaction Type");
			
		/* Add the Transaction Type to the list */
		myTransType.addToList();		
	}
		
	/**
	 * Add a TaxType
	 * @param uId the Id of the tax type
	 * @param pTaxType the Name of the tax type
	 * @throws finObject.Exception on error
	 */ 
	public void addTaxType(long   uId,
			               String pTaxType) throws finObject.Exception {
		finStatic.TaxTypeList  myTaxTypes;
		finStatic.TaxType      myTaxType;
		
		/* Access the tax types */
		myTaxTypes = theData.getTaxTypes();
		
		/* Create a new Tax Type */
		myTaxType = theStatic.new TaxType(myTaxTypes, uId, pTaxType);
		
		/* Check that this TaxTypeId has not been previously added */
		if ((theIdManager.checkTaxTypeID(uId)) &&
		    (myTaxTypes.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
  					  					  ObjectClass.TAXTYPE,
  					  					  myTaxType,
		  			                      "Duplicate TaxTypeId");
			 
		/* Check that this TaxType has not been previously added */
		if (myTaxTypes.searchFor(pTaxType) != null) 
			throw new finObject.Exception(ExceptionClass.DATA,
  					  					  ObjectClass.TAXTYPE,
  					  					  myTaxType,
		                                  "Duplicate Tax Type");
			
		/* Add the Tax Type to the list */
		myTaxType.addToList();
	}
		
	/**
	 * Add a Frequency
	 * @param uId the Id of the frequency
	 * @param pFrequency the Name of the frequency
	 * @throws finObject.Exception on error
	 */ 
	public void addFrequency(long   uId,
			                 String pFrequency) throws finObject.Exception {
		finStatic.Frequency myFrequency;
		finStatic.FreqList  myFrequencys;
		
		/* Access the frequencies */
		myFrequencys = theData.getFrequencys();
		
		/* Create a new Frequency */
		myFrequency = theStatic.new Frequency(myFrequencys, uId, pFrequency);
			
		/* Check that this FrequencyId has not been previously added */
		if ((theIdManager.checkFreqID(uId)) &&
		    (myFrequencys.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
  					  					  ObjectClass.FREQUENCY,
  					  					  myFrequency,
		  			                      "Duplicate FrequencyId");
			 
		/* Check that this Frequency has not been previously added */
		if (myFrequencys.searchFor(pFrequency) != null) 
			throw new finObject.Exception(ExceptionClass.DATA,
  					  					  ObjectClass.FREQUENCY,
  					  					  myFrequency,
		                                  "Duplicate Frequency");
			
		/* Add the Frequency to the list */
		myFrequency.addToList();		
	}
		
	/**
	 * Add a TaxRegime
	 * @param uId the Id of the tax regime
	 * @param pTaxRegime the Name of the tax regime
	 * @throws finObject.Exception on error
	 */ 
	public void addTaxRegime(long   uId,
			                 String pTaxRegime) throws finObject.Exception {
		finStatic.TaxRegime     myTaxRegime;
		finStatic.TaxRegimeList myTaxRegimes;
		
		/* Access the tax regimes */
		myTaxRegimes = theData.getTaxRegimes();
		
		/* Create a new tax regime */
		myTaxRegime = theStatic.new TaxRegime(myTaxRegimes, uId, pTaxRegime);
			
		/* Check that this TaxRegimeId has not been previously added */
		if ((theIdManager.checkTaxRegimeID(uId)) &&
		    (myTaxRegimes.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
  					  					  ObjectClass.TAXREGIME,
  					  					  myTaxRegime,
		  			                      "Duplicate TaxRegimeId");
			 
		/* Check that this TaxRegime has not been previously added */
		if (myTaxRegimes.searchFor(pTaxRegime) != null) 
			throw new finObject.Exception(ExceptionClass.DATA,
  					  					  ObjectClass.TAXREGIME,
  					  					  myTaxRegime,
		                                  "Duplicate TaxRegime");
			
		/* Add the TaxRegime to the list */
		myTaxRegime.addToList();		
	}
		
	/**
	 * Add an Account
	 * @param uId the Id of the account
	 * @param pAccount the Name of the account 
	 * @param pAcType the Name of the account type
	 * @param pDesc the Description of the account (or null)
	 * @param pMaturity the Maturity date for a bond (or null)
	 * @param pClosed the Close Date for the account (or null)
	 * @param pParent the Name of the parent account (or null)
	 * @throws finObject.Exception on error
	 */ 
	public void addAccount(long     uId,
			               String   pAccount,
			               String   pAcType,
			               String   pDesc,
			               Date     pMaturity,
			               Date     pClosed,
			               String   pParent) throws finObject.Exception {
		finStatic.ActTypeList 	myActTypes;
		finStatic.AccountType 	myActType;
		finData.AccountList 	myAccounts;
		finData.Account		 	myParent;
		long				  	myParentId = -1;
			
		/* Access the account types and accounts */
		myActTypes = theData.getActTypes();
		myAccounts = theData.getAccounts();
			
		/* Look up the Account Type */
		myActType = myActTypes.searchFor(pAcType);
		if (myActType == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Account <" + pAccount + 
					                      "> has invalid Account Type <" + 
					                      pAcType + ">");
		
		/* If we have a parent */
		if (pParent != null) {
			/* Look up the Parent */
			myParent = myAccounts.searchFor(pParent);
			if (myParent == null) 
				throw new finObject.Exception(ExceptionClass.DATA,
		                                  	  "Account <" + pAccount + 
					                          "> has invalid Parent <" + 
					                          pParent + ">");
			myParentId = myParent.getId();
		}
		
		/* Add the account */
		addAccount(uId,
				   pAccount,
				   myActType.getId(),
				   pDesc,
				   pMaturity,
				   pClosed,
				   myParentId);
	}
		
	/**
	 * Add an Account
	 * @param uId the Id of the account
	 * @param pAccount the Name of the account 
	 * @param uAcTypeId the Id of the account type
	 * @param pDesc the Description of the account (or null)
	 * @param pMaturity the Maturity date for a bond (or null)
	 * @param pClosed the Close Date for the account (or null)
	 * @param uParentId the Id of the parent account (or -1)
	 * @throws finObject.Exception on error
	 */ 
	public void addAccount(long     uId,
			               String   pAccount,
			               long     uAcTypeId,
			               String   pDesc,
			               Date     pMaturity,
			               Date     pClosed,
			               long     uParentId) throws finObject.Exception {
		finData.Account       myAccount;
		finData.AccountList   myAccounts;
			
		/* Access accounts list */
		myAccounts = theData.getAccounts();
			
		/* Create the new account */
		myAccount = theData.new Account(myAccounts,
				                        uId, 
				                        pAccount, 
				                        uAcTypeId,
				                        pDesc,
				                        pMaturity,
				                        pClosed,
				                        uParentId);
			
		/* Check that this AccountId has not been previously added */
		if ((theIdManager.checkAccountID(uId)) &&
		    (myAccounts.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
										  ObjectClass.ACCOUNT,
										  myAccount,
		  			                      "Duplicate AccountId");
			 
		/* Check that this Account has not been previously added */
		if (myAccounts.searchFor(pAccount) != null) 
			throw new finObject.Exception(ExceptionClass.DATA,
					  					  ObjectClass.ACCOUNT,
					  					  myAccount,
		                                  "Duplicate Account");
		
		/* Add the Account to the list */
		myAccount.addToList();				
	}
		
	/* Validate accounts */
	public void validateAccounts() throws finObject.Exception {
		finData.AccountList myList;
		finData.Account     myCurr;
	
		/* Mark active rates */
		theData.getRates().markActiveRates();
		
		/* Mark active prices */
		theData.getPrices().markActivePrices();
		
		/* Mark active patterns */
		theData.getPatterns().markActivePatterns();
		
		/* Access the account list */
		myList = theData.getAccounts();
			
		/* Loop through the accounts */
		for (myCurr = myList.getFirst();
		     myCurr != null;
		     myCurr = myCurr.getNext()) {
			/* If the account has a parent Id */
			if (myCurr.getParentId() != -1) {
				/* Set the parent */
				myCurr.setParent(myList.searchFor(myCurr.getParentId()));
			}
				
			/* Validate the account */
			myCurr.validate();
				
			/* Handle validation failure */
			if (myCurr.hasErrors()) 
				throw new finObject.Exception(ExceptionClass.VALIDATE,
											  ObjectClass.ACCOUNT,
											  myCurr,
											  "Failed validation");				
		}
	}
		
	/* Allow a price to be added */
	public void addPrice(long     uId,
						 Date     pDate,
            			 String   pAccount,
			             String   pPrice) throws finObject.Exception {
		finData.Account     myAccount;
		finData.AccountList myAccounts;
		
		/* Access the Accounts */
		myAccounts = theData.getAccounts();
		
		/* Look up the Account */
		myAccount = myAccounts.searchFor(pAccount);
		if (myAccount == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Price on <" + 
		                                  finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid Account <" +
					                      pAccount + ">");
								
		/* Add the price */
		addPrice(uId,
				 pDate,
				 myAccount.getId(),
				 pPrice);
	}
		
	/* Allow a price to be added */
	public void addPrice(long     uId,
			             Date     pDate,
			             long     uAccountId,
			             String   pPrice) throws finObject.Exception {
		finData.PriceList 	myPrices;
		finData.Price     	myPrice;
		
		/* Access the Prices */
		myPrices = theData.getPrices();
		
		/* Create the price and PricePoint */
		myPrice = theData.new Price(myPrices, uId, uAccountId, pDate, pPrice);
		
		/* Check that this PriceId has not been previously added */
		if ((theIdManager.checkPriceID(uId)) &&
		    (myPrices.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
										  ObjectClass.PRICE,
										  myPrice,
		  			                      "Duplicate PriceId <" + uId + ">");
		 
		/* Add to the list */
		myPrice.addToList();
	}
		
	/* Allow a rate to be added */
	public void addRate(long     uId,
						String   pAccount,
            			String   pRate,
            			Date     pDate,
			            String   pBonus) throws finObject.Exception {
		finData.Account     myAccount;
		finData.AccountList myAccounts;
		
		/* Access the Accounts */
		myAccounts = theData.getAccounts();
		
		/* Look up the Account */
		myAccount = myAccounts.searchFor(pAccount);
		if (myAccount == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Price on <" + 
		                                  finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid Account <" +
					                      pAccount + ">");
			
		/* Add the rate */
		addRate(uId,
				myAccount.getId(),
				pRate,
				pDate,
				pBonus);
	}
	
	/* Allow a rate to be added */
	public void addRate(long     uId,
						long  	 uAccountId,
            			String   pRate,
            			Date     pDate,
			            String   pBonus) throws finObject.Exception {
		finData.RateList 	myPeriods;
		finData.Rate     	myLine;
		
		/* Access the Accounts */
		myPeriods = theData.getRates();
		
		/* Create the period */
		myLine    = theData.new Rate(myPeriods, uId, uAccountId,
				                     pDate, pRate, pBonus);
			
		/* Check that this RateId has not been previously added */
		if ((theIdManager.checkRateID(uId)) &&
			(myPeriods.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
					                      ObjectClass.RATE,
					                      myLine,
		  			                      "Duplicate RateId");
		 
		/* Add to the list */
		myLine.addToList();
	}
		
	/* Allow a pattern to be added */
	public void addPattern(long     uId,
			               Date     pDate,
			               String   pDesc,
			               String   pAmount,
			               String   pAccount,
			               String 	pPartner,
			               String	pTransType,
			               String  	pFrequency,
			               boolean  isCredit) throws finObject.Exception {
		finStatic.TransTypeList	myTranTypes;
		finStatic.FreqList		myFrequencies;
		finData.AccountList 	myAccounts;
		finData.Account     	myAccount;
		finData.Account     	myPartner;
		finStatic.TransType    	myTransType;
		finStatic.Frequency    	myFrequency;
		
		/* Access the Lists */
		myAccounts 		= theData.getAccounts();
		myTranTypes		= theData.getTransTypes();
		myFrequencies	= theData.getFrequencys();
		
		/* Look up the Account */
		myAccount = myAccounts.searchFor(pAccount);
		if (myAccount == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Pattern on <" + 
		                                  finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid Account <" +
					                      pAccount + ">");
			
		/* Look up the Partner */
		myPartner = myAccounts.searchFor(pPartner);
		if (myPartner == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Pattern on <" + 
		                                  finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid Partner <" +
					                      pPartner + ">");
			
		/* Look up the TransType */
		myTransType = myTranTypes.searchFor(pTransType);
		if (myTransType == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Pattern on <" + 
		                                  finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid TransType <" +
					                      pTransType + ">");
			
		/* Look up the Frequency */
		myFrequency = myFrequencies.searchFor(pFrequency);
		if (myFrequency == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Pattern on <" + 
		                                  finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid Frequency <" +
					                      pFrequency + ">");
			
		/* Add the pattern */
		addPattern(uId,
				   pDate,
				   pDesc,
				   pAmount,
				   myAccount.getId(),
				   myPartner.getId(),
				   myTransType.getId(),
				   myFrequency.getId(),
				   isCredit);
	}
		
	/* Allow a pattern to be added */
	public void addPattern(long     uId,
			               Date     pDate,
			               String   pDesc,
			               String   pAmount,
			               long     uAccountId,
			               long   	uPartnerId,
			               long		uTransId,
			               long   	uFreqId,
			               boolean  isCredit) throws finObject.Exception {
		finData.PatternList myPatterns;
		finData.Pattern     myPattern;
		
		/* Access the Patterns */
		myPatterns = theData.getPatterns();
		
		/* Create the new pattern */
		myPattern = theData.new Pattern(myPatterns, uId, uAccountId, pDate, 
									    pDesc, pAmount, uPartnerId, 
							 		    uTransId, uFreqId, isCredit);
		
		/* Check that this PatternId has not been previously added */
		if ((theIdManager.checkPatternID(uId)) &&
		    (myPatterns.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
		  			                      "Duplicate PatternId <" + uId + ">");
		 
		/* Add to the list */
		myPattern.addToList();
	}
		
	/* Allow a tax parameter to be added */
	public void addTaxYear(long     uId,
						   String   pRegime,
			               Date     pDate,
			               String   pAllowance,
			               String   pRentalAllow,
			               String   pLoAgeAllow,
			               String   pHiAgeAllow,
			               String	pCapAllow,
			               String   pAgeAllowLimit,
			               String   pAddAllowLimit,
			               String   pLoTaxBand,
			               String   pBasicTaxBand,
			               String   pAddIncBound,
			               String   pLoTaxRate,
			               String   pBasicTaxRate,
			               String   pHiTaxRate,
			               String   pIntTaxRate,
			               String   pDivTaxRate,
			               String   pHiDivTaxRate,
			               String   pAddTaxRate,
			               String   pAddDivTaxRate,
			               String	pCapTaxRate,
			               String	pHiCapTaxRate) throws finObject.Exception {
		finStatic.TaxRegime    	myTaxRegime;		
		finStatic.TaxRegimeList myTaxRegimes;
		
		/* Access the TaxRegimes */
		myTaxRegimes = theData.getTaxRegimes();
		
		/* Look up the Tax Regime */
		myTaxRegime = myTaxRegimes.searchFor(pRegime);
		if (myTaxRegime == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "TaxYear on <" + 
					                      finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid TaxRegime <" + pRegime + ">");
		
		/* Create the tax year */
		addTaxYear(uId,
				   myTaxRegime.getId(),
				   pDate,
				   pAllowance,
				   pRentalAllow,
				   pLoAgeAllow,
				   pHiAgeAllow,
				   pCapAllow,
				   pAgeAllowLimit,
				   pAddAllowLimit,
				   pLoTaxBand,
				   pBasicTaxBand,
				   pAddIncBound,
				   pLoTaxRate,
				   pBasicTaxRate,
				   pHiTaxRate,
				   pIntTaxRate,
				   pDivTaxRate,
				   pHiDivTaxRate,
				   pAddTaxRate,
				   pAddDivTaxRate,
				   pCapTaxRate,
				   pHiCapTaxRate);		
    }
	
	/* Allow a tax parameter to be added */
	public void addTaxYear(long     uId,
						   long     uRegimeId,
			               Date     pDate,
			               String   pAllowance,
			               String   pRentalAllow,
			               String   pLoAgeAllow,
			               String   pHiAgeAllow,
			               String	pCapAllow,
			               String   pAgeAllowLimit,
			               String   pAddAllowLimit,
			               String   pLoTaxBand,
			               String   pBasicTaxBand,
			               String   pAddIncBound,
			               String   pLoTaxRate,
			               String   pBasicTaxRate,
			               String   pHiTaxRate,
			               String   pIntTaxRate,
			               String   pDivTaxRate,
			               String   pHiDivTaxRate,
			               String   pAddTaxRate,
			               String   pAddDivTaxRate,
			               String	pCapTaxRate,
			               String	pHiCapTaxRate) throws finObject.Exception {
		finData.TaxParms       myTaxYear;		
		finData.TaxParmList    myTaxYears;
		
		/* Access the TaxYears */
		myTaxYears = theData.getTaxYears();
		
		/* Create the tax year */
		myTaxYear = theData.new TaxParms(myTaxYears,
				                         uId,
				                         uRegimeId,
				                         pDate,
				                         pAllowance,
				                         pRentalAllow,
				      				     pLoAgeAllow,
				    				     pHiAgeAllow,
				    				     pCapAllow,
				    				     pAgeAllowLimit,
				    				     pAddAllowLimit,
				                         pLoTaxBand,
				                         pBasicTaxBand,
				    				     pAddIncBound,
				                         pLoTaxRate,
				                         pBasicTaxRate,
				                         pHiTaxRate,
				                         pIntTaxRate,
				                         pDivTaxRate,
				                         pHiDivTaxRate,
				                         pAddTaxRate,
				                         pAddDivTaxRate,
				                         pCapTaxRate,
				                         pHiCapTaxRate);
		
		/* Check that this TaxYearId has not been previously added */
		if ((theIdManager.checkTaxParmID(uId)) &&
		    (myTaxYears.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
					  					  ObjectClass.TAXPARAMS,
					  					  myTaxYear,
		  			                      "Duplicate TaxYearId");
		 
		/* Check that this TaxYear has not been previously added */
		if (myTaxYears.searchFor(new finObject.Date(pDate)) != null) 
			throw new finObject.Exception(ExceptionClass.DATA,
					  					  ObjectClass.TAXPARAMS,
					  					  myTaxYear,
		                                  "Duplicate TaxYear");
		
		/* Validate the tax year */
		myTaxYear.validate();
		
		/* Handle validation failure */
		if (myTaxYear.hasErrors()) 
			throw new finObject.Exception(ExceptionClass.VALIDATE,
										  ObjectClass.TAXPARAMS,
										  myTaxYear,
										  "Failed validation");
		
		/* Add the TaxYear to the list */
		myTaxYear.addToList();				
    }
	
	/* Allow an account to be added */
	public void addEvent(long     uId,
			             Date     pDate,
			             String   pDesc,
			             String   pAmount,
			             String   pDebit,
			             String   pCredit,
			             String   pUnits,
			             String   pTransType,
			             String   pTaxCredit,
			             int      pYears) throws finObject.Exception {
		finData.AccountList      myAccounts;
		finData.Account          myDebit;
		finData.Account          myCredit;
		finStatic.TransTypeList  myTransTypes;
		finStatic.TransType      myTransType;
			
		/* Access the accounts */
		myAccounts   = theData.getAccounts();
		myTransTypes = theData.getTransTypes();
		
		/* Look up the Transaction Type */
		myTransType = myTransTypes.searchFor(pTransType);
		if (myTransType == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Event on <" + 
					                      finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid Transact Type <" + pTransType + ">");
		
		/* Look up the Credit Account */
		myCredit = myAccounts.searchFor(pCredit);
		if (myCredit == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Event on <" + 
					                      finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid Credit account <" + pCredit + ">");
		
		/* Look up the Debit Account */
		myDebit = myAccounts.searchFor(pDebit);
		if (myDebit == null) 
			throw new finObject.Exception(ExceptionClass.DATA,
		                                  "Event on <" + 
					                      finObject.formatDate(new finObject.Date(pDate)) +
					                      "> has invalid Debit account <" + pDebit + ">");
		
		/* Add the event */
		addEvent(uId,
			     pDate,
			     pDesc,
			     pAmount,
				 myDebit.getId(),
				 myCredit.getId(),
				 pUnits,
				 myTransType.getId(), 
				 pTaxCredit, 
				 pYears);
	}
		
	/* Allow an event to be added */
	public void addEvent(long     uId,
			             Date     pDate,
			             String   pDesc,
			             String   pAmount,
			             long     uDebitId,
			             long     uCreditId,
			             String   pUnits,
			             long  	  uTransId,
			             String   pTaxCredit,
			             int      pYears) throws finObject.Exception {
		finData.Event            myEvent;
		finData.EventList        myEvents;
		
		/* Access the various lists */
		myEvents     = theData.getEvents();
		
		/* Create the new Event */
		myEvent = theData.new Event(myEvents, uId, pDate, pDesc,
				                    uDebitId, uCreditId, uTransId, 
				                    pAmount, pUnits, pTaxCredit, pYears);
		
		/* Check that this EventId has not been previously added */
		if ((theIdManager.checkEventID(uId)) &&			
		    (myEvents.searchFor(uId) != null)) 
			throw new finObject.Exception(ExceptionClass.DATA,
										  ObjectClass.EVENT,
										  myEvent,
		  			                      "Duplicate EventId");
		 
		/* If this is not a market adjustment to a priced item */
		if (!myEvent.isMarketAdjustment()) {
			/* Validate the event */
			myEvent.validate();

			/* Handle validation failure */
			if (myEvent.hasErrors()) 
				throw new finObject.Exception(ExceptionClass.VALIDATE,
											  ObjectClass.EVENT,
											  myEvent,
											  "Failed validation");
				
			/* Add the Event to the list */
			myEvent.addToList();
		}
	}
	
	/* Id Manager */
	public static class IdManager {
		private long theMaxActType   = 0;
		private long theMaxTransType = 0;
		private long theMaxTaxType   = 0;
		private long theMaxTaxRegime = 0;
		private long theMaxTaxParm   = 0;
		private long theMaxAccount   = 0;
		private long theMaxPrice     = 0;
		private long theMaxRate      = 0;	
		private long theMaxEvent     = 0;
		private long theMaxFreq      = 0;
		private long theMaxPattern   = 0;
		
		/**
		 *  Check whether we need to test an AccountType ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkActTypeID(long uId) {
			return ((uId != 0) && (uId <= theMaxActType));
		}
		
		/**
		 *  Check whether we need to test a TransType ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkTransTypeID(long uId) {
			return ((uId != 0) && (uId <= theMaxTransType));
		}
		
		/**
		 *  Check whether we need to test a TaxType ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkTaxTypeID(long uId) {
			return ((uId != 0) && (uId <= theMaxTaxType));
		}
		
		/**
		 *  Check whether we need to test a TaxRegime ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkTaxRegimeID(long uId) {
			return ((uId != 0) && (uId <= theMaxTaxRegime));
		}
		
		/**
		 *  Check whether we need to test a TaxParm ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkTaxParmID(long uId) {
			return ((uId != 0) && (uId <= theMaxTaxParm));
		}
		
		/**
		 *  Check whether we need to test an Account ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkAccountID(long uId) {
			return ((uId != 0) && (uId <= theMaxAccount));
		}
		
		/**
		 *  Check whether we need to test a Price ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkPriceID(long uId) {
			return ((uId != 0) && (uId <= theMaxPrice));
		}
		
		/**
		 *  Check whether we need to test a Rate ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkRateID(long uId) {
			return ((uId != 0) && (uId <= theMaxRate));
		}
		
		/**
		 *  Check whether we need to test an Event ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkEventID(long uId) {
			return ((uId != 0) && (uId <= theMaxEvent));
		}
		
		/**
		 *  Check whether we need to test a Frequency ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkFreqID(long uId) {
			return ((uId != 0) && (uId <= theMaxFreq));
		}
		
		/**
		 *  Check whether we need to test a Pattern ID
		 *  
		 * @param uId the Id to check
		 * @return Do we need top check for uniqueness true/false
		 */
		protected boolean checkPatternID(long uId) {
			return ((uId != 0) && (uId <= theMaxPattern));
		}
		
		/**
		 *  Generate/Record new AccountType
		 *  
		 * @param pActType the new act type
		 */
		protected void setNewActType(finStatic.AccountType pActType) {
			long id = pActType.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max actType */
				theMaxActType++;
				pActType.setId(theMaxActType);
				pActType.setState(State.NEW);
			}
			
			/* else we need to update max ActType to reflect this id */
			else if (theMaxActType < id) {
				/* Update the max actType */
				theMaxActType = id;
				pActType.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new TransType
		 *  
		 * @param pTranType the new trans type
		 */
		protected void setNewTransType(finStatic.TransType pTranType) {
			long id = pTranType.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max transType */
				theMaxTransType++;
				pTranType.setId(theMaxTransType);
				pTranType.setState(State.NEW);
			}
			
			/* else we need to update max transType to reflect this id */
			else if (theMaxTransType < id) {
				/* Update the max transType */
				theMaxTransType = id;
				pTranType.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new Frequency
		 *  
		 * @param pFrequency the new Frequency
		 */
		protected void setNewFrequency(finStatic.Frequency pFrequency) {
			long id = pFrequency.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max frequency */
				theMaxFreq++;
				pFrequency.setId(theMaxFreq);
				pFrequency.setState(State.NEW);
			}
			
			/* else we need to update max frequency to reflect this id */
			else if (theMaxFreq < id) {
				/* Update the max frequency */
				theMaxFreq = id;
				pFrequency.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new Pattern
		 *  
		 * @param pPattern the new Pattern
		 */
		protected void setNewPattern(Pattern pPattern) {
			long id = pPattern.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max pattern */
				theMaxPattern++;
				pPattern.setId(theMaxPattern);
				pPattern.setState(State.NEW);
			}
			
			/* else we need to update max pattern to reflect this id */
			else if (theMaxPattern < id) {
				/* Update the max pattern */
				theMaxPattern = id;
				pPattern.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new TaxType
		 *  
		 * @param pTaxType the new tax type
		 */
		protected void setNewTaxType(finStatic.TaxType pTaxType) {
			long id = pTaxType.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max taxType */
				theMaxTaxType++;
				pTaxType.setId(theMaxTaxType);
				pTaxType.setState(State.NEW);
			}
			
			/* else we need to update max TaxType to reflect this id */
			else if (theMaxTaxType < id) {
				/* Update the max taxType */
				theMaxTaxType = id;
				pTaxType.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new TaxRegime
		 *  
		 * @param pTaxRegime the new tax regime
		 */
		protected void setNewTaxRegime(finStatic.TaxRegime pTaxRegime) {
			long id = pTaxRegime.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max taxType */
				theMaxTaxRegime++;
				pTaxRegime.setId(theMaxTaxRegime);
				pTaxRegime.setState(State.NEW);
			}
			
			/* else we need to update max TaxRegime to reflect this id */
			else if (theMaxTaxRegime < id) {
				/* Update the max taxRegime */
				theMaxTaxRegime = id;
				pTaxRegime.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new TaxParm
		 *  
		 * @param pTaxParam the new tax param
		 */
		protected void setNewTaxParm(TaxParms pTaxParam) {
			long id = pTaxParam.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max actType */
				theMaxTaxParm++;
				pTaxParam.setId(theMaxTaxParm);
				pTaxParam.setState(State.NEW);
			}
			
			/* else we need to update max taxParam to reflect this id */
			else if (theMaxTaxParm < id) {
				/* Update the max taxParam */
				theMaxTaxParm = id;
				pTaxParam.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new Account
		 *    
		 * @param pAccount the new act type
		 */
		protected void setNewAccount(Account pAccount) {
			long id = pAccount.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max account */
				theMaxAccount++;
				pAccount.setId(theMaxAccount);
				pAccount.setState(State.NEW);
			}
			
			/* else we need to update max Account to reflect this id */
			else if (theMaxAccount < id) {
				/* Update the max account */
				theMaxAccount = id;
				pAccount.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new Price
		 *  
		 * @param pPrice the new price
		 */
		protected void setNewPrice(Price pPrice) {
			long id = pPrice.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max price */
				theMaxPrice++;
				pPrice.setId(theMaxPrice);
				pPrice.setState(State.NEW);
			}
			
			/* else we need to update max price to reflect this id */
			else if (theMaxPrice < id) {
				/* Update the max price */
				theMaxPrice = id;
				pPrice.setState(State.CLEAN);			
			}
		}

		/**
		 *  Generate/Record new Rate
		 *  
		 * @param pRate the new rate
		 */
		protected void setNewRate(Rate pRate) {
			long id = pRate.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max Rate */
				theMaxRate++;
				pRate.setId(theMaxRate);
				pRate.setState(State.NEW);
			}
			
			/* else we need to update max Rate to reflect this id */
			else if (theMaxRate < id) {
				/* Update the max rate */
				theMaxRate = id;
				pRate.setState(State.CLEAN);
			}
		}

		/**
		 *  Generate/Record new Event
		 *  
		 * @param pEvent the new event
		 */
		protected void setNewEvent(Event pEvent) {
			long id = pEvent.getId();
			
			/* If we need to generate a new id */
			if (id == 0) {
				/* Increment and use the max event */
				theMaxEvent++;
				pEvent.setId(theMaxEvent);
				pEvent.setState(State.NEW);
			}
			
			/* else we need to update max Event to reflect this id */
			else if (theMaxEvent < id) {
				/* Update the max event */
				theMaxEvent = id;
				pEvent.setState(State.CLEAN);
			}
		}
	}
}
