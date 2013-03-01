'*******************************************************************************
'* jMoneyWise: Finance Application
'* Copyright 2013 Tony Washer
'*
'* Licensed under the Apache License, Version 2.0 (the "License");
'* you may not use this file except in compliance with the License.
'* You may obtain a copy of the License at
'*
'*   http://www.apache.org/licenses/LICENSE-2.0
'*
'* Unless required by applicable law or agreed to in writing, software
'* distributed under the License is distributed on an "AS IS" BASIS,
'* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
'* See the License for the specific language governing permissions and
'* limitations under the License.
'* ------------------------------------------------------------
'* SubVersion Revision Information:
'* $URL$
'* $Revision$
'* $Author$
'* $Date$
'******************************************************************************/
'Range Names
Const rangeAccounts = "Accounts"
Const rangeAccountCategorys = "AccountCategory"
Const rangeAccountParents = "Parents"
Const rangeAccountAliases = "Aliases"
Const rangeAssetsAccounts = "AssetsNames"
Const rangeAssetsData = "AssetsData"
Const rangeAssetsYears = "AssetsDates"
Const rangeUnitsAccounts = "UnitsNames"
Const rangeUnitsData = "UnitsData"
Const rangeUnitsYears = "UnitsDates"
Const rangePricesAccounts = "PricesNames"
Const rangePricesData = "PricesData"
Const rangePricesYears = "PricesDates"
Const rangeSpotPricesAccounts = "SpotPricesNames"
Const rangeSpotPricesData = "SpotPricesData"
Const rangeSpotPricesYears = "SpotPricesSheets"
Const rangeSpotPricesDates = "SpotPricesDates"
Const rangeDilutionDetails = "DilutionDetails"
Const rangeIncomeAccounts = "IncomeNames"
Const rangeIncomeData = "IncomeData"
Const rangeIncomeYears = "IncomeDates"
Const rangeExpenseAccounts = "ExpenseNames"
Const rangeExpenseData = "ExpenseData"
Const rangeExpenseYears = "ExpenseDates"
Const rangeTransAccounts = "TransNames"
Const rangeTransData = "TransData"
Const rangeTransYears = "TransDates"
Const rangeTransTypes = "TransType"
Const rangeMarketProfit = "MarketProfit"

'Explicit account names 
Const acctTaxMan = "InlandRevenue"
Const acctMarket = "Market" 

'Hidden Reporting TransType Names
Const tranUnitDividend    = "UnitTrustDividend"
Const tranTaxFreeDividend = "TaxFreeDividend"
Const tranTaxFreeInterest = "TaxFreeInterest"
Const tranTaxCredit       = "TaxCredit"
Const tranMktGrowth       = "MarketGrowth"
Const tranMktShrink       = "MarketShrink"
Const tranCapitalGain	  = "CapitalGain"
Const tranCapitalLoss	  = "CapitalLoss"
Const tranTaxableGain	  = "TaxableGain"

'Column locations
Const colDate = 1
Const colCredit = 5
Const colDebit = 4
Const colAmount = 3
Const colReduct = 6
Const colUnits = 7
Const colTrans = 8
Const colTaxCred = 9

'Colors
Const colorRed = 16711680
Const colorGreen = 65280
Const colorWhite = -1

'Information for account
Type AccountInfo
	strAccount As String
	strAccountType As String
	strParent As String
	strAlias As String
	hasUnits As Boolean
	hasValue As Boolean
	isActive As Boolean
	isCapital As Boolean
	isLifeBond As Boolean
	idxAssets As Integer
	idxIncome As Integer
	idxExpense As Integer
	idxUnits As Integer
	idxPrice As Integer
	acctValue As Double
	acctUnits As Double
	acctPrice As Double
	acctLastValue As Double
	acctLastUnits As Double
	acctInvestment As Double
	acctDividends As Double
	acctMarket As Double
	acctIncome As Double
	acctExpense As Double
	acctCost As Double
	acctProfit As Double
	acctRealisedGains As Double
	acctGains As Double
	acctCashTakeover As Double
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'Information for TransType
Type TransInfo
	strTransType As String
	idxTrans As Integer
	tranValue As Double
	tranTaxCredit As Double
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

Dim theFuncs As Object

'Access the OpenOffice functions
Function getFuncs() As Object
	
	'If the value has not yet been set 
	IF (theFuncs IS NOTHING) THEN
		'Create the new function access object
		theFuncs = createunoservice("com.sun.star.sheet.FunctionAccess")
	END IF
	
	'Return the variable value
	getFuncs = theFuncs
	
End Function

'Get the Account Type for an account
Function getAccountType(Account As String) As String
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	     
    'Determine the index into the account table of this account
    myRange = myDoc.NamedRanges.getByName(rangeAccounts)
    myIndex = myFuncs.callFunction("MATCH", array(Account, myRange.getReferredCells(), 0))
    
	'If we did not find a match
	If (myIndex = 0) Then
		'Report the error
		msgBox Account & " was not found in Accounts List"
	End If
    		 
    'Access the Account type
    myRange = myDoc.NamedRanges.getByName(rangeAccountCategorys)
    getAccountType = myFuncs.callFunction("INDEX", array(myRange.getReferredCells(), myIndex, 1)

End Function

'Get the parent for an account
Function getParent(Account As String) As String
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	     
    'Determine the index into the account table of this account
    myRange = myDoc.NamedRanges.getByName(rangeAccounts)
    myIndex = myFuncs.callFunction("MATCH", array(Account, myRange.getReferredCells(), 0))
    
    'Access the Parent
    myRange = myDoc.NamedRanges.getByName(rangeAccountParents)
    getParent = myFuncs.callFunction("INDEX", array(myRange.getReferredCells(), myIndex, 1)

	'If we did not find the parent
	If (getParent = "0") Then
		'Set the name to empty string
		getParent = ""
	End If
End Function

'Get the alias for an account
Function getAlias(Account As String) As String
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	     
    'Determine the index into the account table of this account
    myRange = myDoc.NamedRanges.getByName(rangeAccounts)
    myIndex = myFuncs.callFunction("MATCH", array(Account, myRange.getReferredCells(), 0))
    
    'Access the Alias
    myRange = myDoc.NamedRanges.getByName(rangeAccountAliases)
    getAlias = myFuncs.callFunction("INDEX", array(myRange.getReferredCells(), myIndex, 1)

	'If we did not find the Alias
	If (getAlias = "0") Then
		'Set the name to empty string
		getAlias = ""
	End If
End Function

'Get the date for a Year
Function getDateForYear(Year As String) As Double
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	     
    'Determine the index into the SpotPriceYears
    myRange = myDoc.NamedRanges.getByName(rangeSpotPricesYears)
    myIndex = myFuncs.callFunction("MATCH", array(Year, myRange.getReferredCells(), 0))

    'Handle date not found (too early)
    If (myIndex = 0) Then
    	myIndex = 1
    End If
    
    'Access the Date
    myRange = myDoc.NamedRanges.getByName(rangeSpotPricesDates).getReferredCells()
    getDateForYear = myRange.getCellByPosition(0, myIndex-1).getValue()
End Function

'Get the Spot Price Index for an Account
Function getSpotPriceIndexForAcct(Account As AccountInfo) As Integer
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	Dim myName As String
	
	'Access cached index 
	myIndex = Account.idxPrice

	'If the price index is unknown
	If (myIndex = 0) Then	
		'Access the function table
		myFuncs = getFuncs()

		'Access the current workbook
		myDoc = ThisComponent
	
		'Determine the name to search on
		myName = Account.strAccount
		If Not (Account.strAlias = "") Then
			myName = Account.strAlias
		End If
	     
    	'Determine the index into the SpotPriceNames
    	myRange = myDoc.NamedRanges.getByName(rangeSpotPricesAccounts)
    	myIndex = myFuncs.callFunction("MATCH", array(myName, myRange.getReferredCells(), 0))
    
		'If we did not find a match
		If (myIndex = 0) Then
			'Report the error
    		msgBox myName & " was not found in SpotPrices Sheet"
    	Else
    		'Store the index (Add two to allow for the header columns)
    		Account.idxPrice = myIndex + 2
    	End If
    End If

	'Return the index    
    getSpotPriceIndexForAcct = Account.idxPrice
    
End Function

'Get the Spot Price for a Date
Function getSpotPriceForDate(Account As AccountInfo, PriceDate As Double) As Integer
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myPrice As Double
	'Dim myDilution As Double

	'Access the SpotPrice index for the date
	myRow = getSpotPriceIndexForDate(PriceDate)
	
    'Determine the column index to use
	myCol = getSpotPriceIndexForAcct(Account)
    		 
	'Access the price and dilution 
	myPrice = getBaseSpotPrice(myCol, myRow)
'	myDilution = getDilutionFactor(Account.strAccount, PriceDate)
				
	'If we did not find a price
	If (myPrice = 0) And (Account.acctUnits > 0) Then
		msgBox Account.strAccount & " price was not found in Spot Prices Sheet"
	End If

	'Adjust for dilution and calculate value
	'myPrice = myPrice / myDilution
	getSpotPriceForDate = myPrice * Account.acctUnits
End Function

'Get the Spot Price Index for a Date
Function getSpotPriceIndexForDate(PriceDate As Double) As Integer
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	     
    'Determine the index into the SpotPriceDates
    myRange = myDoc.NamedRanges.getByName(rangeSpotPricesDates)
    myIndex = myFuncs.callFunction("MATCH", array(PriceDate, myRange.getReferredCells(), 1))
    
    'Adjust for header
    getSpotPriceIndexForDate = myIndex + 1
End Function

'Get the Spot Price for a set of indices
Function getBaseSpotPrice(AcctIndex As Integer, PriceIndex As Integer) As Double
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	Dim myPrice As Double
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	     
    'Access the SpotPricesData for the range that we are interested in
    myRange = myDoc.NamedRanges.getByName(rangeSpotPricesData).getReferredCells()
    
    'Loop backwards through the Cells in the range
    myPrice = 0
	For myIndex = PriceIndex-1 To 1 Step -1
		'Access the price
		myPrice = myRange.getCellByPosition(AcctIndex-1, myIndex).getValue()
		
		'Break loop oif we found a price
		If Not (myPrice = 0) Then 
			Exit For
		End If
	Next
	
	'Return the Price
	getBaseSpotPrice = myPrice
End Function

'Get the number of defined accounts
Function getNumAccounts() As Integer
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	     
    'Determine the count of accounts
    myRange = myDoc.NamedRanges.getByName(rangeAccounts)
    getNumAccounts = myRange.getReferredCells().getRows().Count

End Function

'Get the number of defined transaction types
Function getNumTransTypes() As Integer
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	     
    'Determine the count of TransTypes
    myRange = myDoc.NamedRanges.getByName(rangeTransTypes)
    getNumTransTypes = myRange.getReferredCells().getRows().Count

End Function

'Adjust the TransType
Function adjustTransType(AccountType As String, TransType As String) As String
	Select Case TransType
		Case "Interest"
			Select Case AccountType 
				Case "CashISA", "ISABond", "TaxFreeBond"
					adjustTransType = tranTaxFreeInterest
				Case Else
					adjustTransType = TransType
			End Select
		Case "Dividend"
			Select Case AccountType 
				Case "UnitISA"
					adjustTransType = tranTaxFreeDividend
				Case "UnitTrust"
					adjustTransType = tranUnitDividend
				Case Else
					adjustTransType = TransType
			End Select
		Case Else
			adjustTransType = TransType
	End Select
End Function
 
'Determine whether the TransType is recovered
Function isRecovered(TransType As String) As Boolean
	Select Case TransType
		Case "Recovered", "CashPayment", "CashRecovery"
			isRecovered = True()
		Case Else
			isRecovered = False()
	End Select
End Function
 
'Determine whether the TransType is transfer
Function isTransfer(TransType As String) As Boolean
	Select Case TransType
		Case "Transfer", "CashPayment", "CashRecovery"
			isTransfer = True()
		Case Else
			isTransfer = False()
	End Select
End Function
 
'Determine whether the TransType is market adjustment
Function isMktAdjust(TransType As String) As Boolean
	Select Case TransType
		Case acctMktGrowth, acctMktShrink
			isMktAdjust = True()
		Case Else
			isMktAdjust = False()
	End Select
End Function
 
'Determine whether the TransType is dividend
Function isDividend(TransType As String) As Boolean
	Select Case TransType
		Case "Dividend"
			isDividend = True()
		Case Else
			isDividend = False()
	End Select
End Function
 
'Determine whether the TransType is interest
Function isInterest(TransType As String) As Boolean
	Select Case TransType
		Case "Interest"
			isInterest = True()
		Case Else
			isInterest = False()
	End Select
End Function
 
'Determine whether the TransType is stock takeover
Function isStockTakeover(TransType As String) As Boolean
	Select Case TransType
		Case "StockTakeover"
			isStockTakeover = True()
		Case Else
			isStockTakeover = False()
	End Select
End Function
 
'Determine whether the TransType is stock demerger
Function isStockDemerger(TransType As String) As Boolean
	Select Case TransType
		Case "StockDemerger"
			isStockDemerger = True()
		Case Else
			isStockDemerger = False()
	End Select
End Function
 
'Determine whether the TransType is cash takeover
Function isCashTakeover(TransType As String) As Boolean
	Select Case TransType
		Case "CashTakeover"
			isCashTakeover = True()
		Case Else
			isCashTakeover = False()
	End Select
End Function
 
'Determine whether the TransType is stock rights waived
Function isStockRightWaived(TransType As String) As Boolean
	Select Case TransType
		Case "StockRightWaived"
			isStockRightWaived = True()
		Case Else
			isStockRightWaived = False()
	End Select
End Function
 
'Determine whether the TransType is taxable gain
Function isTaxableGain(TransType As String) As Boolean
	Select Case TransType
		Case "TaxableGain"
			isTaxableGain = True()
		Case Else
			isTaxableGain = False()
	End Select
End Function
 
'Determine whether the Account Type is subject to Capital Gains
Function isCapital(AccountType As String) As Boolean
	Select Case AccountType
		Case "Shares", "UnitTrust"
			isCapital = True()
		Case Else
			isCapital = False()
	End Select
End Function
 
'Determine whether the Account Type is subject to Taxable Gains
Function isLifeBond(AccountType As String) As Boolean
	Select Case AccountType
		Case "LifeBond"
			isLifeBond = True()
		Case Else
			isLifeBond = False()
	End Select
End Function
 
'Determine whether the Account Type has Units
Function hasUnits(AccountType As String) As Boolean
	Select Case AccountType
		Case "Shares", "UnitTrust", "UnitISA", "LifeBond", "Car", "House", "Endowment"
			hasUnits = True()
		Case Else
			hasUnits = False()
	End Select
End Function
 
'Determine whether the Account Type has a value (non-Units)
Function hasValue(AccountType As String) As Boolean
	Select Case AccountType
		Case "Current", "Instant", "Restricted", "Bond", "TaxFreeBond", "CashISA", "ISABond", "EquityBond", "CreditCard", "Debts", "Deferred"
			hasValue = True()
		Case Else
			hasValue = False()
	End Select
End Function

'Access the Account Statistics 
Function getAccountInfo(AccountList() As AccountInfo, Account As String) As AccountInfo
	Dim myAccount As AccountInfo
	Dim isNew As Boolean
	
	' Assume that this is a new account
	isNew = True()
	
	'Loop through the existing array
	For myIndex = 0 to UBound(AccountList)
		' Access the element
		myAccount = AccountList(myIndex)

		'If the element is null, break the loop
		If (myAccount.strAccount = "") Then
			Exit For
		End If
		
		'If we have found the account in the array
		If (myAccount.strAccount = Account) Then
			'Note that this is not a new account
			isNew = False()
			Exit For
		End If
	Next myIndex
		
	'If this is a new account
	If isNew Then
		' Initialise the account Info
		myAccount.strAccount = Account
		myAccount.strAccountType = getAccountType(Account)
		myAccount.strParent = getParent(Account)
		myAccount.hasUnits = hasUnits(myAccount.strAccountType) 
		myAccount.hasValue = hasValue(myAccount.strAccountType)
		myAccount.isCapital = isCapital(myAccount.strAccountType)
		myAccount.isLifeBond = isLifeBond(myAccount.strAccountType)
		myAccount.isActive = True()

		'If we have units determine any alias
		If (myAccount.hasUnits) Then		
			myAccount.strAlias = getAlias(Account)
		Else
			myAccount.strAlias = ""
		End If
		
		'Allocate the indices
		myAccount.idxAssets = 0
		myAccount.idxIncome = 0
		myAccount.idxExpense = 0
		myAccount.idxUnits = 0
		myAccount.idxPrice = 0
		
		'Allocate the Counters
		myAccount.acctValue = 0
		myAccount.acctUnits = 0
		myAccount.acctInvestment = 0
		myAccount.acctDividends = 0
		myAccount.acctMarket = 0
		myAccount.acctIncome = 0
		myAccount.acctExpense = 0
		myAccount.acctLastValue = 0
		myAccount.acctLastUnits = 0
		myAccount.acctCost = 0
		myAccount.acctProfit = 0
		myAccount.acctRealisedGains = 0
		myAccount.acctGains = 0 
		myAccount.acctCashTakeover = 0
	EndIf
	
	'Return the information
	getAccountInfo = myAccount
	
End Function

'Access the TransType Statistics 
Function getTransInfo(TransList() As TransInfo, TransType As String) As TransInfo
	Dim myTransType As TransInfo
	Dim isNew As Boolean
	
	' Assume that this is a new transtype
	isNew = True()
	
	'Loop through the existing array
	For myIndex = 0 to UBound(TransList)
		' Access the element
		myTransType = TransList(myIndex)

		'If the element is null, break the loop
		If (myTransType.strTransType = "") Then
			Exit For
		End If
		
		'If we have found the transtype in the array
		If (myTransType.strTransType = TransType) Then
			'Note that this is not a new transtype
			isNew = False()
			Exit For
		End If
	Next myIndex
		
	'If this is a new TransType
	If isNew Then
		' Initialise the TransType Info
		myTransType.strTransType = TransType
		
		'Allocate the Counters
		myTransType.tranValue = 0
		myTransType.tranTaxCredit = 0
	EndIf
	
	'Return the information
	getTransInfo = myTransType
	
End Function

'Calculate and report on totals for data
Sub TotalIt()
	'Determine the number of accounts
	myCount = getNumAccounts()
	
	'Declare the account details list
	Dim AccountList(myCount) As AccountInfo
	
	'Determine the number of transaction types
	myCount = getNumTransTypes()
	
	'Declare the TransType details list
	Dim TransList(myCount) As TransInfo
	
	'Access the current workbook
	myDoc = ThisComponent
	
	'Access the unit years range
	myRange = myDoc.NamedRanges.getByName(rangeAssetsYears).getReferredCells()
	
	'Switch off autocalc for the duration
	Dim isAutoCalc As Boolean
	isAutoCalc = True() 'myDoc.isAutomaticCalculationEnabled()
	If isAutoCalc Then
		myDoc.enableAutomaticCalculation(False())
	End If
	
	'Loop through the years
	For myIndex = myRange.getColumns().Count To 1 Step -1
		'Access the year
		myCell = myRange.getCellByPosition(myIndex-1, 0)
		myYear = myCell.getString()
	
		'Set the Cell background color to red
		myCell.CellBackColor = colorRed

		'Analyse the Year
		analyseYear(AccountList, TransList, myYear)
		valuePricedAssets(AccountList, TransList, myYear)
		
		'Set the Cell background color to green
		myCell.CellBackColor = colorGreen
			
		'Report on the Year
		reportUnitsYear(AccountList, myYear)
		reportAssetsYear(AccountList, myYear)
		reportIncomeYear(AccountList, myYear)
		reportExpenseYear(AccountList, myYear)
		reportTransYear(TransList, myYear)
		resetYear(AccountList, TransList)
		
		'Set the Cell background color to white
		myCell.CellBackColor = colorWhite
	Next
	
	'Reswitch on AutoCalc
	If isAutoCalc Then
		myDoc.calculateAll()
		myDoc.enableAutomaticCalculation(True())
	End If
End Sub 
 
'Calculate and report on totals for market
Sub TotalMarket()
	'Determine the number of accounts
	myCount = getNumAccounts()
	
	'Declare the account details list
	Dim AccountList(myCount) As AccountInfo
	
	'Determine the number of transaction types
	myCount = getNumTransTypes()
	
	'Declare the TransType details list
	Dim TransList(myCount) As TransInfo

	'Access the current workbook
	myDoc = ThisComponent
	
	'Access the unit years range
	myRange = myDoc.NamedRanges.getByName(rangeAssetsYears).getReferredCells()
	
	'Switch off autocalc for the duration
	Dim isAutoCalc As Boolean
	isAutoCalc = True() 'myDoc.isAutomaticCalculationEnabled()
	If isAutoCalc Then
		myDoc.enableAutomaticCalculation(False())
	End If
	
	'Loop through the years
	For myIndex = myRange.getColumns().Count To 1 Step -1
		'Access the year
		myCell = myRange.getCellByPosition(myIndex-1, 0)
		myYear = myCell.getString()
	
		'Set the Cell background color to red
		myCell.CellBackColor = colorRed
			
		'Analyse the Year
		analyseYear(AccountList, TransList, myYear)
		valuePricedAssets(AccountList, TransList, myYear)
		
		'Set the Cell background color to green
		myCell.CellBackColor = colorGreen
			
		'Report on the Year
		reportMarketYear(AccountList, myYear)

		'Set the Cell background color to white
		myCell.CellBackColor = colorWhite
	Next
	
	'Reswitch on AutoCalc
	If isAutoCalc Then
		myDoc.calculateAll()
		myDoc.enableAutomaticCalculation(True())
	End If
End Sub 
 
'Analyse data for the year
Sub analyseYear(AccountList() As AccountInfo, TransList() As TransInfo, Year As String)
	Dim myDoc As Object
	Dim myFuncs As Object
	Dim myRange As Object
    Dim myCredit As String
    Dim myDebit As String
    Dim myDebitType As String
    Dim myTrans As String
    Dim myParent As String
    Dim myUnits As Double
    Dim myReduction As Double
    Dim myValue As Double
    Dim myTaxCred As Double
    Dim myReduct As Double
    Dim myDate As Double
	Dim myDebInfo As AccountInfo
	Dim myCredInfo As AccountInfo
	Dim myTInfo As TransInfo
	Dim myTaxInfo As TransInfo
	Dim myTGInfo As TransInfo
	             
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	
	'Access the requested range
	myRange = myDoc.NamedRanges.getByName(Year).getReferredCells()
	
	'Access the TaxCredit and Taxable Gain information
	myTaxInfo = getTransInfo(TransList, tranTaxCredit) 
	myTGInfo = getTransInfo(TransList, tranTaxableGain) 
	
	'Loop through the rows in the range    
    For Each myRow in myRange.getRows()
		'Access the Values in the row
		myCredit  = myRow.getCellByPosition(colCredit-1, 0).getString()
		myDebit   = myRow.getCellByPosition(colDebit-1, 0).getString()
		myTrans   = myRow.getCellByPosition(colTrans-1, 0).getString()
        myValue   = myRow.getCellByPosition(colAmount-1, 0).getValue()
        myUnits   = myRow.getCellByPosition(colUnits-1, 0).getValue()
        myTaxCred = myRow.getCellByPosition(colTaxCred-1, 0).getValue()
        myReduct  = myRow.getCellByPosition(colReduct-1, 0).getValue()
        myDate    = myRow.getCellByPosition(colDate-1, 0).getValue()

		'Skip this row if we have no dates
		If (myDate = 0) Then Next
					        
    	'If we have a value in the Units cell
		If Not (myUnits = 0) Then
			'Access the information for the accounts
			myCredInfo = getAccountInfo(AccountList, myCredit)
			myDebInfo = getAccountInfo(AccountList, myDebit)
			
			'If the credit account has units 
			If myCredInfo.hasUnits Then
				'Add to units
				myCredInfo.acctUnits = myCredInfo.acctUnits + myUnits
				myCredInfo.isActive = True()
				
				'If this is a Stock Takeover
				If isStockTakeover(myTrans) Then
					'Set the debit units to zero
					myDebInfo.acctUnits = 0
				End If

			'Else If the debit account has units 
			ElseIf (myDebInfo.hasUnits) Then
				'Subtract from units
				myDebInfo.acctUnits = myDebInfo.acctUnits - myUnits
				myDebInfo.isActive = True()
			End If
        End If
        
    	'If we have a value in the Amount cell
		If Not (myValue = 0) Then
			'Access the information for the Credit account
			myCredInfo = getAccountInfo(AccountList, myCredit)
			myCredInfo.isActive = True()
			
			'If the credit account has value 
			If myCredInfo.hasValue Then
				'Add to value
				myCredInfo.acctValue = myCredInfo.acctValue + myValue
				
			'Else If the credit account has units 
			ElseIf myCredInfo.hasUnits Then
				'Add to investment
				myCredInfo.acctInvestment = myCredInfo.acctInvestment + myValue

				'Add to cost
				myCredInfo.acctCost = myCredInfo.acctCost + myValue
									
			'Else this is an expense
			Else
				'Add to expense
				myCredInfo.acctExpense = myCredInfo.acctExpense + myValue		
			End If
				
			'Access the information for the Debit account
			myDebInfo = getAccountInfo(AccountList, myDebit)
			myDebitType = myDebInfo.strAccountType
			myDebInfo.isActive = True()
			
			'If the debit account has value 
			If myDebInfo.hasValue Then
				'If this is interest
				If isInterest(myTrans) Then
					'Access the parent account
					myParent = getParent(myDebit)
					myDebInfo = getAccountInfo(AccountList, myParent)
					
					'Adjust parent income
					myDebInfo.acctIncome = myDebInfo.acctIncome + myValue + myTaxCred
				Else
					'Subtract from Value
					myDebInfo.acctValue = myDebInfo.acctValue - myValue
				EndIf
				
			'If the debit account has units 
			ElseIf (myDebInfo.hasUnits) Then
				'If this is a dividend
				If isDividend(myTrans) Then
					'Add to Dividend value
					myDebInfo.acctDividends = myDebInfo.acctDividends + myValue + myTaxCred

					'Access the parent account
					myParent = getParent(myDebit)
					myDebInfo = getAccountInfo(AccountList, myParent)
					
					'Adjust parent income
					myDebInfo.acctIncome = myDebInfo.acctIncome + myValue + myTaxCred
					
				'Else if it is a cash takeover
				ElseIf isCashTakeover(myTrans) Then
					'Record Cash Takeover amount for later calculation
					myDebInfo.acctCashTakeover = myDebInfo.acctCashTakeover + myValue + myTaxCred

				'Else it is not a dividend or cash takeover
				Else
					'Subtract from investment value
					myDebInfo.acctInvestment = myDebInfo.acctInvestment - myValue - myTaxCred

					'Assume the reduction is the full value taken
					myReduction = myValue
					
					'If we are reducing units in the account
					If (myUnits > 0) Then
						'Calculate the reduction
						myReduction = myDebInfo.acctCost * myUnits
						myReduction = myReduction / (myUnits + myDebInfo.acctUnits)
						
					'Else if this is a stocks rights that is waived
					ElseIf isStockRightWaived(myTrans) Then 
						'Calculate the existing value of the stock
						myShares = getSpotPriceForDate(myDebInfo, myDate)						
						
						'If this is a large stocks rights waiver(>£3000 and > 5% stock value)
						If (myValue > 3000) And ((myValue * 20) > myShares) Then
							'Apportion the cost between the stock and shares part
							myProportion = myShares / (myShares + myCash)
							myStockCost = myProportion * myCost
							myCashCost = myCost - myStockCost

							'Cost is reduced by the cash part 
							myReduction = myCashCost
						End If
					End If
					
					'If the reduction is greater than the costs
					If (myReduction > myDebInfo.acctCost) Then
						'Reduce the cost to zero
						myReduction = myDebInfo.acctCost
					End If
						
					'If this is a Taxable Gain
					If isTaxableGain(myTrans) Then
						'Add the amount and tax credit
						myTGInfo.tranValue = myTGInfo.tranValue + myValue
						myTGInfo.tranValue = myTGInfo.tranValue - myReduction
						myTGInfo.tranTaxCredit = myTGInfo.tranTaxCredit + myTaxCred
				
						'Add the tax credit
						myTaxInfo.tranValue = myTaxInfo.tranValue + myTaxCred
						
						'Add the tax credit to the Gains
						myDebInfo.acctGains = myDebInfo.acctGains + myTaxCred
					End If
					
					'Add to gains (allowing for cost reduction)
					myDebInfo.acctGains = myDebInfo.acctGains + myValue
					myDebInfo.acctGains = myDebInfo.acctGains - myReduction

					'Remove from cost
					myDebInfo.acctCost = myDebInfo.acctCost - myReduction
				End If

			'Else this is an income
			Else
				'If this is a recovered transaction
				If isRecovered(myTrans) Then
					'Subtract from expense
					myDebInfo.acctExpense = myDebInfo.acctExpense - myValue
					
				'Else standard transaction
				Else
					'Add to income		
					myDebInfo.acctIncome = myDebInfo.acctIncome + myValue + myTaxCred
				End If		
			End If
			
			'If there is a TaxCredit
			If myTaxCred > 0 Then
				'Access the taxman account
				myCredInfo = getAccountInfo(AccountList, acctTaxMan)
					
				'Adjust taxman expense
				myCredInfo.acctExpense = myCredInfo.acctExpense + myTaxCred
			End If
			
			'If this is not a transfer or market adjustment or taxable gain
			If Not (isTransfer(myTrans) Or isMktAdjust(myTrans) Or isTaxableGain(myTrans)) Then
				'Adjust the trans type if required
				myTrans = adjustTransType(myDebitType, myTrans)
			
				'Access the transaction info
				myTInfo = getTransInfo(TransList, myTrans)
				
				'Add the amount and tax credit
				myTInfo.tranValue = myTInfo.tranValue + myValue
				myTInfo.tranTaxCredit = myTInfo.tranTaxCredit + myTaxCred
				
				'Add the tax credit
				myTaxInfo.tranValue = myTaxInfo.tranValue + myTaxCred
			End If

        'If we have a Stock Demerger
        ElseIf isStockDemerger(myTrans) Then
			'Access the information for the accounts
			myCredInfo = getAccountInfo(AccountList, myCredit)
			myDebInfo = getAccountInfo(AccountList, myDebit)

			'Access the Existing Cost
			myCost = myDebInfo.acctCost
			
			'Calculate the transfer cost information
			myCost = myDebInfo.acctCost * myReduct
			myTransfer = myDebInfo.acctCost - myCost
			
			'Adjust the costs
			myCredInfo.acctCost = myCredInfo.acctCost + myTransfer
			myDebInfo.acctCost = myDebInfo.acctCost - myTransfer
			
			'Adjust the investments
			myCredInfo.acctInvestment = myCredInfo.acctInvestment + myTransfer
			myDebInfo.acctInvestment = myDebInfo.acctInvestment - myTransfer

        'If we have a Stock Takeover
        ElseIf isStockTakeover(myTrans) Then
			'Access the information for the accounts
			myCredInfo = getAccountInfo(AccountList, myCredit)
			myDebInfo = getAccountInfo(AccountList, myDebit)

			'Access the Existing Cost and any cash element of takeover
			myCost = myDebInfo.acctCost
			myCash = myDebInfo.acctCashTakeover

			'Access the value of shares used in the takeover on this date
			myShares = getSpotPriceForDate(myCredInfo, myDate)						
			
			'Apportion the cost between the stock and shares part
			myProportion = myShares / (myShares + myCash)
			myStockCost = myProportion * myCost
			myCashCost = myCost - myStockCost

			'Calculate the gains
			myGains = myCash - myCashCost
						
			'Adjust the costs and gains
			myCredInfo.acctCost = myCredInfo.acctCost + myStockCost
			myDebInfo.acctGains = myDebInfo.acctGains + myGains
			myDebInfo.acctInvestment = myDebInfo.acctInvestment - myCash
			myDebInfo.acctCost = 0
        End If
    Next    
End Sub

'Value the priced assets
Sub valuePricedAssets(AccountList() As AccountInfo, TransList() As TransInfo, Year As String)
	Dim myFuncs As Object
	Dim myDoc As Object
	Dim myAccounts As Object
	Dim myYears As Object
	Dim myData As Object
	Dim myAcct As Object
	Dim myAccount As AccountInfo
	Dim myMarket As AccountInfo
	Dim myGrowth As TransInfo
	Dim myShrink As TransInfo
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myValue As Double
	Dim myDate As Double
	Dim myPrice As Double
	'Dim myDilution As Double
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	
    'Access the ranges
    myAccounts = myDoc.NamedRanges.getByName(rangePricesAccounts).getReferredCells()
    myYears = myDoc.NamedRanges.getByName(rangePricesYears).getReferredCells()
    myData = myDoc.NamedRanges.getByName(rangePricesData).getReferredCells()
    
    'Determine the row index to use
	myDate = getDateForYear(Year)
	myRow  = getSpotPriceIndexForDate(myDate)

	'Access the market transactions
	myMarket = getAccountInfo(AccountList, acctMarket)
	
	'Access the market transactions
	myGrowth   = getTransInfo(TransList, tranMktGrowth)
	myShrink   = getTransInfo(TransList, tranMktShrink)
	myCapGains = getTransInfo(TransList, tranCapitalGain)
	myCapLoss  = getTransInfo(TransList, tranCapitalLoss)
	
	'Loop through the Accounts 
	For myIndex = 0 to UBound(AccountList)
		'Access the element
		myAccount = AccountList(myIndex)

		'If the element is null, break the loop
		If (myAccount.strAccount = "") Then
			Exit For
		End If
		
		'If the element has Units 
		If (myAccount.hasUnits) And	(myAccount.isActive) Then
		    'Determine the column index to use
    		myCol = getSpotPriceIndexForAcct(myAccount)
    		 
    		'Access the price and dilution 
			myPrice = getBaseSpotPrice(myCol, myRow)
			'myDilution = getDilutionFactor(myAccount.strAccount, myDate)
				
    		'If we did not find a price
    		If (myPrice = 0) And (myAccount.acctUnits > 0) Then
    			msgBox myAccount.strAccount & " price was not found in Spot Prices Sheet"
    		End If

 			'If the units is nearly zero
			If ((myAccount.acctUnits < 0.005) And (myAccount.acctUnits > -0.005)) Then
				'Set it to truly zero
				myAccount.acctUnits = 0
			End If
			
			'Adjust for dilution and calculate value
			myAccount.acctPrice = myPrice
			myAccount.acctValue = myAccount.acctPrice * myAccount.acctUnits
			
 			'If the value is nearly zero
			If ((myAccount.acctValue < 0.005) And (myAccount.acctValue > -0.005)) Then
				'Set it to truly zero
				myAccount.acctValue = 0
			End If
			
 			'If the cost is nearly zero
			If ((myAccount.acctCost < 0.005) And (myAccount.acctCost > -0.005)) Then
				'Set it to truly zero
				myAccount.acctCost = 0
			End If
			
 			'If the gains is nearly zero
			If ((myAccount.acctGains < 0.005) And (myAccount.acctGains > -0.005)) Then
				'Set it to truly zero
				myAccount.acctGains = 0
			End If
			
			'Calculate the profit
			myAccount.acctProfit = myAccount.acctValue - myAccount.acctCost
			     		 
 			'If the profit is nearly zero
			If ((myAccount.acctProfit < 0.005) And (myAccount.acctProfit > -0.005)) Then
				'Set it to truly zero
				myAccount.acctProfit = 0
			End If
			
	   		'Calculate market income/expense and add it to totals
    		myValue = myAccount.acctValue
    		myValue = myValue - myAccount.acctLastValue
    		myValue = myValue - myAccount.acctInvestment
    		
    		'If we have gains on the account
    		If Not (myAccount.acctGains = 0) Then
    			'If the account is subject to capital gains
    			If myAccount.isCapital Then
	    			'Subtract gains from market movement
    				myValue = myValue - myAccount.acctGains
    		
    				'If the gains are positive
    				If (myAccount.acctGains > 0) Then
    					'Add to capital Gains
    					myCapGains.tranValue = myCapGains.tranValue + myAccount.acctGains
						myMarket.acctIncome = myMarket.acctIncome + myAccount.acctGains
    				Else
    					'Add to capital Loss
    					myCapLoss.tranValue = myCapLoss.tranValue - myAccount.acctGains	
						myMarket.acctExpense = myMarket.acctExpense - myAccount.acctGains
					End If

    			'Else If the account is subject to taxable gains
    			ElseIf myAccount.isLifeBond Then
	    			'Subtract gains from market movement
    				myValue = myValue - myAccount.acctGains
    		
    				'If the gains are positive
    				If (myAccount.acctGains > 0) Then
    					'Add to income
						myMarket.acctIncome = myMarket.acctIncome + myAccount.acctGains
					End If
    			End If	
    		End If
    		
   			'Adjust realised gains
   			myAccount.acctRealisedGains = myAccount.acctRealisedGains + myAccount.acctGains

			'Record market movement    			
    		myAccount.acctMarket = myValue
    		    		
    		'myValue = myValue - myAccount.acctDividends
			If (myValue > 0) Then
				myMarket.acctIncome = myMarket.acctIncome + myValue
				myGrowth.tranValue = myGrowth.tranValue + myValue
			Else 
				myMarket.acctExpense = myMarket.acctExpense - myValue
				myShrink.tranValue = myShrink.tranValue - myValue
			End If
		
		'Else check for rounding errors on zero values
		ElseIf (myAccount.hasValue) Then
			'If the account is nearly zero
			If ((myAccount.acctValue < 0.005) And (myAccount.acctValue > -0.005)) Then
				'Set it to truly zero
				myAccount.acctValue = 0
			End If

		'Else check for negative expenses
		ElseIf Not ((myAccount.hasUnits) Or (myAccount.hasValue)) Then
			'If the expense is negative
			If (myAccount.acctExpense < 0) Then
				'Switch it to be an income
				myAccount.acctIncome = myAccount.acctIncome - myAccount.acctExpense
				myAccount.acctExpense = 0
			End If
		End If 
	Next
End Sub

'Report on Assets Data for the Year
Sub reportAssetsYear(AccountList() As AccountInfo, Year As String)
	Dim myFuncs As Object
	Dim myDoc As Object
	Dim myAccounts As Object
	Dim myYears As Object
	Dim myData As Object
	Dim myAccount As AccountInfo
	Dim myName As String
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myCount As Integer
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	
    'Access the ranges
    myAccounts = myDoc.NamedRanges.getByName(rangeAssetsAccounts).getReferredCells()
    myYears = myDoc.NamedRanges.getByName(rangeAssetsYears).getReferredCells()
    myData = myDoc.NamedRanges.getByName(rangeAssetsData).getReferredCells()
    
    'Determine the column to use
    myCol = myFuncs.callFunction("MATCH", array(Year, myYears, 0))

	'Create result table for accounts
	myCount = myAccounts.getRows().Count
	Dim myTable(myCount-1) As Boolean
	
	'Loop through the Accounts 
	For myIndex = 0 to UBound(AccountList)
		'Access the element
		myAccount = AccountList(myIndex)

		'If the element is null, break the loop
		If (myAccount.strAccount = "") Then
			Exit For
		End If
		
		'If the element has Units or Value
		If (myAccount.hasUnits) Or (myAccount.hasValue) Then
		    'Determine the row to use
    		myRow = myAccount.idxAssets
    		if (myRow = 0) Then
    			'Look up the index
	    		myRow = myFuncs.callFunction("MATCH", array(myAccount.strAccount, myAccounts, 0))
    
    			'If we did not find a match
    			If (myRow = 0) Then
    				msgBox myAccount.strAccount & " was not found in Assets Sheet"
    			Else
    				myAccount.idxAssets = myRow
    			End If
    		End If
    		 
			'Access the cell
			myCell = myData.getCellByPosition(myCol-1, myRow-1)
			If (myAccount.isActive) Then
				myCell.setValue(myAccount.acctValue)
			ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
			
			'Note that this account has been marked
			myTable(myRow-1) = True()
		End If 
	Next

	'Loop through the Results table 
	For myIndex = 0 to UBound(myTable)
		'If we have not touched this account
		If Not myTable(myIndex) Then
			'Access the cell
			myCell = myData.getCellByPosition(myCol-1, myIndex)
			
			'Clear the cell if required
			If Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
		End If 
	Next
End Sub

'Report on Income Data for the Year
Sub reportIncomeYear(AccountList() As AccountInfo, Year As String)
	Dim myFuncs As Object
	Dim myDoc As Object
	Dim myAccounts As Object
	Dim myYears As Object
	Dim myData As Object
	Dim myAccount As AccountInfo
	Dim myName As String
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myCount As Integer
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	
    'Access the ranges
    myAccounts = myDoc.NamedRanges.getByName(rangeIncomeAccounts).getReferredCells()
    myYears = myDoc.NamedRanges.getByName(rangeIncomeYears).getReferredCells()
    myData = myDoc.NamedRanges.getByName(rangeIncomeData).getReferredCells()
    
    'Determine the column to use
    myCol = myFuncs.callFunction("MATCH", array(Year, myYears, 0))

	'Create result table for accounts
	myCount = myAccounts.getRows().Count
	Dim myTable(myCount-1) As Boolean
	
	'Loop through the Accounts 
	For myIndex = 0 to UBound(AccountList)
		'Access the element
		myAccount = AccountList(myIndex)

		'If the element is null, break the loop
		If (myAccount.strAccount = "") Then
			Exit For
		End If
		
		'If the element has neither units nor value
		If Not ((myAccount.hasUnits) Or (myAccount.hasValue)) Then
		    'Determine the row to use
    		myRow = myAccount.idxIncome
    		if (myRow = 0) Then
    			'Look up the index
    			myRow = myFuncs.callFunction("MATCH", array(myAccount.strAccount, myAccounts, 0))
    
    			'If we did not find a match
    			If (myRow = 0) Then
    				If Not (myAccount.acctIncome = 0) Then
    					msgBox myAccount.strAccount & " was not found in Income Sheet"
    				End If
    			Else
    				myAccount.idxIncome = myRow
    			End If
    		End If
    		 
    		'If we have income to Add
    		If Not (myRow = 0) Then 
				'Access the cell
				myCell = myData.getCellByPosition(myCol-1, myRow-1)
				If Not (myAccount.acctIncome = 0)  Then
					myCell.setValue(myAccount.acctIncome)
				ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
					myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
										+com.sun.star.sheet.CellFlags.STRING _
										+com.sun.star.sheet.CellFlags.FORMULA)
				End If
			
				'Note that this account has been marked
				myTable(myRow-1) = True()
			End If
		End If 
	Next

	'Loop through the Results table 
	For myIndex = 0 to UBound(myTable)
		'If we have not touched this account
		If Not myTable(myIndex) Then
			'Access the cell
			myCell = myData.getCellByPosition(myCol-1, myIndex)
			
			'Clear the cell if required
			If Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
		End If 
	Next
End Sub

'Report on Expense Data for the Year
Sub reportExpenseYear(AccountList() As AccountInfo, Year As String)
	Dim myFuncs As Object
	Dim myDoc As Object
	Dim myAccounts As Object
	Dim myYears As Object
	Dim myData As Object
	Dim myAccount As AccountInfo
	Dim myName As String
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myCount As Integer
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	
    'Access the ranges
    myAccounts = myDoc.NamedRanges.getByName(rangeExpenseAccounts).getReferredCells()
    myYears = myDoc.NamedRanges.getByName(rangeExpenseYears).getReferredCells()
    myData = myDoc.NamedRanges.getByName(rangeExpenseData).getReferredCells()
    
    'Determine the column to use
    myCol = myFuncs.callFunction("MATCH", array(Year, myYears, 0))

	'Create result table for accounts
	myCount = myAccounts.getRows().Count
	Dim myTable(myCount-1) As Boolean
	
	'Loop through the Accounts 
	For myIndex = 0 to UBound(AccountList)
		'Access the element
		myAccount = AccountList(myIndex)

		'If the element is null, break the loop
		If (myAccount.strAccount = "") Then
			Exit For
		End If
		
		'If the element has neither units nor value
		If Not ((myAccount.hasUnits) Or (myAccount.hasValue)) Then
		    'Determine the row to use
    		myRow = myAccount.idxExpense
    		if (myRow = 0) Then
    			'Look up the index
    			myRow = myFuncs.callFunction("MATCH", array(myAccount.strAccount, myAccounts, 0))
    
    			'If we did not find a match
    			If (myRow = 0) Then
    				If Not (myAccount.acctExpense = 0) Then
    					msgBox myAccount.strAccount & " was not found in Expense Sheet: Val=" & myAccount.acctExpense
    				End If
				Else
					myAccount.idxExpense = myRow
				End If
			End If
			    		
    		'If we have expense
    		If Not (myRow = 0) Then
				'Access the cell
				myCell = myData.getCellByPosition(myCol-1, myRow-1)
				If Not (myAccount.acctExpense = 0)  Then
					myCell.setValue(myAccount.acctExpense)
				ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
					myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
										+com.sun.star.sheet.CellFlags.STRING _
										+com.sun.star.sheet.CellFlags.FORMULA)
				End If
			
				'Note that this account has been marked
				myTable(myRow-1) = True()
			End If
		End If 
	Next

	'Loop through the Results table 
	For myIndex = 0 to UBound(myTable)
		'If we have not touched this account
		If Not myTable(myIndex) Then
			'Access the cell
			myCell = myData.getCellByPosition(myCol-1, myIndex)
			
			'Clear the cell if required
			If Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
		End If 
	Next
End Sub

'Report on Trans Data for the Year
Sub reportTransYear(TransList() As TransInfo, Year As String)
	Dim myFuncs As Object
	Dim myDoc As Object
	Dim myTransActs As Object
	Dim myYears As Object
	Dim myData As Object
	Dim myTrans As TransInfo
	Dim myName As String
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myCount As Integer
		
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	
    'Access the ranges
    myTransActs = myDoc.NamedRanges.getByName(rangeTransAccounts).getReferredCells()
    myYears = myDoc.NamedRanges.getByName(rangeTransYears).getReferredCells()
    myData = myDoc.NamedRanges.getByName(rangeTransData).getReferredCells()
    
    'Determine the column to use
    myCol = myFuncs.callFunction("MATCH", array(Year, myYears, 0))

	'Create result table for TransTypes
	myCount = myTransActs.getRows().Count
	Dim myTable(myCount-1) As Boolean
	
	'Loop through the TransTypes 
	For myIndex = 0 to UBound(TransList)
		'Access the element
		myTrans = TransList(myIndex)

		'If the element is null, break the loop
		If (myTrans.strTransType = "") Then
			Exit For
		End If
		
	    'Determine the row to use
   		myRow = myTrans.idxTrans
   		if (myRow = 0) Then
   			'Look up the index
   			myRow = myFuncs.callFunction("MATCH", array(myTrans.strTransType, myTransActs, 0))
    
    		'If we did not find a match
    		If (myRow = 0) Then
    			msgBox myTrans.strTransType & " was not found in Movements Sheet"
    		Else
    			myTrans.idxTrans = myRow
    		End If
    	End If
    		 
		'Access the cell
		myCell = myData.getCellByPosition(myCol-1, myRow-1)
		If ((myTrans.tranValue > 0) Or (myTrans.tranTaxCredit > 0)) Then
			myValue = myTrans.tranValue + myTrans.tranTaxCredit
			myCell.setValue(myValue)
		ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
			myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
								+com.sun.star.sheet.CellFlags.STRING _
								+com.sun.star.sheet.CellFlags.FORMULA)
		End If 
			
		'Note that this transtype has been marked
		myTable(myRow-1) = True()
	Next

	'Loop through the Results table 
	For myIndex = 0 to UBound(myTable)
		'If we have not touched this TransType
		If Not myTable(myIndex) Then
			'Access the cell
			myCell = myData.getCellByPosition(myCol-1, myIndex)
			
			'Clear the cell if required
			If Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
		End If 
	Next
End Sub

'Report on Units Data for the Year
Sub reportUnitsYear(AccountList() As AccountInfo, Year As String)
	Dim myFuncs As Object
	Dim myDoc As Object
	Dim myAccounts As Object
	Dim myYears As Object
	Dim myData As Object
	Dim myAccount As AccountInfo
	Dim myName As String
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myCount As Integer
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	
    'Access the ranges
    myAccounts = myDoc.NamedRanges.getByName(rangeUnitsAccounts).getReferredCells()
    myYears = myDoc.NamedRanges.getByName(rangeUnitsYears).getReferredCells()
    myData = myDoc.NamedRanges.getByName(rangeUnitsData).getReferredCells()
    
    'Determine the column to use
    myCol = myFuncs.callFunction("MATCH", array(Year, myYears, 0))

	'Create result table for accounts
	myCount = myAccounts.getRows().Count
	Dim myTable(myCount-1) As Boolean
	
	'Loop through the Accounts 
	For myIndex = 0 to UBound(AccountList)
		'Access the element
		myAccount = AccountList(myIndex)

		'If the element is null, break the loop
		If (myAccount.strAccount = "") Then
			Exit For
		End If
		
		'If the element has Units
		If (myAccount.hasUnits) Then
		    'Determine the row to use
    		myRow = myAccount.idxUnits
    		if (myRow = 0) Then
    			'Look up the index
    			myRow = myFuncs.callFunction("MATCH", array(myAccount.strAccount, myAccounts, 0))
    
    			'If we did not find a match
    			If (myRow = 0) Then
    				msgBox myAccount.strAccount & " was not found in Units Sheet"
    			Else
    				myAccount.idxUnits = myRow
    			End If
    		End If
    		 
			'Access the cell
			myCell = myData.getCellByPosition(myCol-1, myRow-1)
			If (myAccount.isActive) Then
				myCell.setValue(myAccount.acctUnits)
			ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
			
			'Note that this account has been marked
			myTable(myRow-1) = True()
		End If 
	Next

	'Loop through the Results table 
	For myIndex = 0 to UBound(myTable)
		'If we have not touched this account
		If Not myTable(myIndex) Then
			'Access the cell
			myCell = myData.getCellByPosition(myCol-1, myIndex)
			
			'Clear the cell if required
			If Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
		End If 
	Next
End Sub

'Report on Market Data for the Year
Sub reportMarketYear(AccountList() As AccountInfo, Year As String)
	Dim myFuncs As Object
	Dim myDoc As Object
	Dim myAccounts As Object
	Dim myData As Object
	Dim myAccount As AccountInfo
	Dim myName As String
	Dim myRow As Integer
	Dim myCount As Integer
	
	'Access the function table
	myFuncs = getFuncs()

	'Access the current workbook
	myDoc = ThisComponent
	
    'Access the ranges
    myAccounts = myDoc.NamedRanges.getByName(rangeUnitsAccounts).getReferredCells()
    myData = myDoc.NamedRanges.getByName(rangeMarketProfit).getReferredCells()
    
	'Create result table for accounts
	myCount = myAccounts.getRows().Count
	Dim myTable(myCount-1) As Boolean
	
	'Loop through the Accounts 
	For myIndex = 0 to UBound(AccountList)
		'Access the element
		myAccount = AccountList(myIndex)

		'If the element is null, break the loop
		If (myAccount.strAccount = "") Then
			Exit For
		End If
		
		'If the element has Units
		If (myAccount.hasUnits) Then
		    'Determine the row to use
    		myRow = myAccount.idxUnits
    		if (myRow = 0) Then
    			'Look up the index
    			myRow = myFuncs.callFunction("MATCH", array(myAccount.strAccount, myAccounts, 0))
    
    			'If we did not find a match
    			If (myRow = 0) Then
    				msgBox myAccount.strAccount & " was not found in Units Sheet"
    			Else
    				myAccount.idxUnits = myRow
    			End If
    		End If
    		 
			'Access the cost cell
			myCell = myData.getCellByPosition(0, myRow-1)
			If (myAccount.acctCost > 0) Then
				myCell.setValue(myAccount.acctCost)
			ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
			
			'Access the value cell
			myCell = myData.getCellByPosition(1, myRow-1)
			If (myAccount.acctValue > 0) Then
				myCell.setValue(myAccount.acctValue)
			ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
			
			'Access the profit cell
			myCell = myData.getCellByPosition(2, myRow-1)
			If Not (myAccount.acctProfit = 0) Then
				myCell.setValue(myAccount.acctProfit)
			ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
			
			'Access the dividends cell
			myCell = myData.getCellByPosition(3, myRow-1)
			If (myAccount.acctDividends > 0) Then
				myCell.setValue(myAccount.acctDividends)
			ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
			
			'Access the gains cell
			myCell = myData.getCellByPosition(4, myRow-1)
			If Not (myAccount.acctGains = 0) Then
				myCell.setValue(myAccount.acctGains)
			ElseIf Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then 
				myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
									+com.sun.star.sheet.CellFlags.STRING _
									+com.sun.star.sheet.CellFlags.FORMULA)
			End If
			
			'Note that this account has been marked
			myTable(myRow-1) = True()
		End If 
	Next

	'Loop through the Results table 
	For myIndex = 0 to UBound(myTable)
		'If we have not touched this account
		If Not myTable(myIndex) Then
			'Loop through the columns
			For myCol = 0 To 4
				'Access the cell
				myCell = myData.getCellByPosition(myCol, myIndex)
			
				'Clear the cell if required
				If Not (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then
					myCell.clearContents(com.sun.star.sheet.CellFlags.VALUE _
										+com.sun.star.sheet.CellFlags.STRING _
										+com.sun.star.sheet.CellFlags.FORMULA)
				End If
			Next
		End If 
	Next
End Sub

'Reset counters after processing a years data
Sub resetYear(AccountList() As AccountInfo, TransList() As TransInfo)
	Dim myAccount As AccountInfo
	Dim myTransType As TransInfo
	
	'Loop through the Accounts 
	For myIndex = 0 to UBound(AccountList)
		'Access the element
		myAccount = AccountList(myIndex)

		'If the element is null, break the loop
		If (myAccount.strAccount = "") Then
			Exit For
		End If
		
		'Reset counters
		myAccount.acctInvestment = 0
		myAccount.acctDividends = 0
		myAccount.acctMarket = 0
		myAccount.acctPrice = 0
		myAccount.acctIncome = 0
		myAccount.acctExpense = 0
		myAccount.acctGains = 0
		
		'If the account has Units/Value
		If (myAccount.hasValue) Or (myAccount.hasUnits) Then
			'Record last years values
		    myAccount.acctLastValue = myAccount.acctValue
			myAccount.acctLastUnits = myAccount.acctUnits
			
			'Reset Value for assets
			If (myAccount.hasUnits) Then
				myAccount.acctValue = 0
			End If
			
			'If we finished with no value
			If (myAccount.acctLastValue = 0) Then
				'Reset active flag
				myAccount.isActive = False()
			End If
		End If
	Next

	'Loop through the TransTypes 
	For myIndex = 0 to UBound(TransList)
		'Access the element
		myTransType = TransList(myIndex)

		'If the element is null, break the loop
		If (myTransType.strTransType = "") Then
			Exit For
		End If
		
		'Reset counters
		myTransType.tranValue = 0
		myTransType.tranTaxCredit = 0
	Next
End Sub
