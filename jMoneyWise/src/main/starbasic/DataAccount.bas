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
Private Const rangeAccountInfo As String = "AccountInfo"
Private Const rangeAccountCategories As String = "AccountCategoryInfo"

'Account Column locations
Private Const colAcctName As Integer = 1
Private Const colAcctType As Integer = 2
Private Const colAcctTaxFree As Integer = 3
Private Const colAcctClosed As Integer = 4
Private Const colAcctParent As Integer = 5
Private Const colAcctAlias As Integer = 6
Private Const colAcctPortfolio As Integer = 7
Private Const colAcctOpenBal As Integer = 8
Private Const colAcctSymbol As Integer = 9
Private Const colAcctAutoExp As Integer = 10

'Account Type Column locations
Private Const colAcTpName As Integer = 0
Private Const colAcTpClass As Integer = 1
Private Const colAcTpValue As Integer = 2
Private Const colAcTpUnits As Integer = 3
Private Const colAcTpUnitTrust As Integer = 4
Private Const colAcTpEndowment As Integer = 5
Private Const colAcTpCapital As Integer = 6
Private Const colAcTpLifeBond As Integer = 7

'Account Type
Public Type AccountType
	' Account Type details
	strAccountType As String
	strAccountClass As String
	
	'Account flags
	hasUnits As Boolean
	hasValue As Boolean
	isNonAsset As Boolean
	isUnitTrust As Boolean
	isEndowment As Boolean
	isCapital As Boolean
	isLifeBond As Boolean	
	
	'Additional three fields declared to fix bug in debugger which loses last three fields
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'Statistics for account
Public Type AccountStats
	'Account details
	strAccount As String
	strParent As String
	strAlias As String
	strPortfolio As String
	strSymbol As String
	
	'Account type
	acctType As AccountType
	 
	'Parent statistics
	acctParent As AccountStats
	
	'AutoExpense category
	catAutoExpense As CategoryStats
	
	'Account flags
	hasUnits As Boolean
	hasValue As Boolean
	isNonAsset As Boolean
	isAutoExpense As Boolean
	isActive As Boolean
	isClosed As Boolean
	isTaxFree As Boolean
	isCapital As Boolean
	isLifeBond As Boolean
	isEndowment As Boolean
	isUnitTrust As Boolean
	
	'Reporting indices
	idxAssets As Integer
	idxIncome As Integer
	idxExpense As Integer
	idxUnits As Integer
	idxPrice As Integer
	
	'Counters
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
	
	'Additional three fields declared to fix bug in debugger which loses last three fields
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'Allocate an instance of accountType
Private Function allocateAccountType() As AccountType
	Dim myResult As New AccountType
	Set allocateAccountType = myResult
End Function

'Allocate an instance of accountStats
Private Function allocateAccount() As AccountStats
	Dim myResult As New AccountStats
	Set allocateAccount = myResult
End Function

'Load Account types
Private Sub loadAccountTypes(ByRef Context As FinanceState)
	Dim myDoc As Object
	Dim myRange As Object
	Dim myRow As Object
	
	'Access the current workbook and the map
	Set myDoc = Context.docBook
	Set myMap = Context.mapAccountTypes
	     
    'Access the Account info
    Set myRange = myDoc.NamedRanges.getByName(rangeAccountCategories).getReferredCells()
    
	'Loop through the rows in the range    
    For Each myRow in myRange.getRows()
		'Allocate the new account Type    
    	Set myType = allocateAccountType()
    
    	'Access name	
	    myName = myRow.getCellByPosition(colAcTpName, 0).getString()
	    
    	'Build values 
    	myType.strAccountType = myName
	    myType.strAccountClass = myRow.getCellByPosition(colAcTpClass, 0).getString()
	    myType.hasValue = myRow.getCellByPosition(colAcTpValue, 0).getValue()
	    myType.hasUnits = myRow.getCellByPosition(colAcTpUnits, 0).getValue()
	    myType.isNonAsset = Not(myType.hasValue Or myType.hasUnits)
	    myType.isUnitTrust = myRow.getCellByPosition(colAcTpUnitTrust, 0).getValue()
	    myType.isCapital = myRow.getCellByPosition(colAcTpCapital, 0).getValue()
	    myType.isLifeBond = myRow.getCellByPosition(colAcTpLifeBond, 0).getValue()
	    myType.isEndowment = myRow.getCellByPosition(colAcTpEndowment, 0).getValue()
    		
    	'Store the account type
    	putHashKey(myMap, myName, myType) 
	Next
End Sub

'Load Accounts
Private Sub loadAccounts(ByRef Context As FinanceState)
	Dim myDoc As Object
	Dim myRange As Object
	Dim myRow As Object
	
	'Access the current workbook and the map
	Set myDoc = Context.docBook
	Set myMap = Context.mapAccounts
	     
    'Access the Account info
    Set myRange = myDoc.NamedRanges.getByName(rangeAccountInfo)
    
	'Loop through the rows in the range    
    For Each myRow in myRange.getReferredCells().getRows()
		'Allocate the new account    
    	Set myAcct = allocateAccount()
    
    	'Access name	
	    myName = myRow.getCellByPosition(colAcctName, 0).getString()
	    
    	'Access AccountType
    	myActType = myRow.getCellByPosition(colAcctType, 0).getString()
    	Set myType = getAccountType(Context, myActType)
    	
    	'Build core values 
    	myAcct.strAccount = myName
	    myAcct.strParent = myRow.getCellByPosition(colAcctParent, 0).getString()
	    myAcct.isTaxFree = myRow.getCellByPosition(colAcctTaxFree, 0).getValue()
	    myAcct.isClosed = myRow.getCellByPosition(colAcctClosed, 0).getValue()

		'Promote values from account type	    
    	myAcct.acctType = myType
		myAcct.hasUnits = myType.hasUnits 
        myAcct.hasValue = myType.hasValue
        myAcct.isNonAsset = myType.isNonAsset
		myAcct.isUnitTrust = myType.isUnitTrust
		myAcct.isCapital = myType.isCapital
		myAcct.isLifeBond = myType.isLifeBond
		myAcct.isEndowment = myType.isEndowment
		myAcct.isActive = True()
    		
		'Handle value accounts
		myAcct.isAutoExpense = False()
		If (myAcct.hasValue) Then		
		    myAcct.acctValue = myRow.getCellByPosition(colAcctOpenBal, 0).getValue()
			myAutoExpense = myRow.getCellByPosition(colAcctAutoExp, 0).getString()
			If (myAutoExpense <> "") Then
				Set myAcct.catAutoExpense = getCategoryStats(Context, myAutoExpense)
				myAcct.isAutoExpense = True()
			End If
		End If
		
		'Handle asset accounts
		If (myAcct.hasUnits) Then		
	    	myAcct.strAlias = myRow.getCellByPosition(colAcctAlias, 0).getString()
	    	myAcct.strPortfolio = myRow.getCellByPosition(colAcctPortfolio, 0).getString()
		    myAcct.strSymbol = myRow.getCellByPosition(colAcctSymbol, 0).getString()
		End If 
		
		'Initialise indices
		myAcct.idxAssets = -1
		myAcct.idxIncome = -1
		myAcct.idxExpense = -1
		myAcct.idxPrice = -1
		myAcct.idxUnits = -1
		
		'Reset the account
		resetAccount(myAcct)
		    		
    	'Store the account
    	putHashKey(myMap, myName, myAcct) 
	Next
	
    'Access Opening Balances
    Set myOpeningAcct = getCachedAccount(Context, acctOpenBal)
    Set myOpeningCat = getCategoryStats(Context, acctOpenBal)
    
	'Loop through the Accounts 
	myIterator = hashIterator(Context.mapAccounts)
	While (hashHasNext(myIterator))
		'Access the element
		Set myAcct = hashNext(myIterator)
		
		'If we have a parent
		myParent = myAcct.strParent
		If (myParent <> "") Then		
			Set myAcct.acctParent = getAccountStats(Context, myParent)		
		End If

		'If we have a value
		myValue = myAcct.acctValue
		If (myValue <> 0) Then		
			'Access account in cache
			Set x = getCachedAccount(Context, myAcct.strAccount)
			
			'Adjust income for opening balance
			myOpeningAcct.acctIncome = myOpeningAcct.acctIncome + myValue
			myOpeningCat.catValue = myOpeningCat.catValue + myValue
		End If
	Wend
End Sub

'Reset Account totals
Private Sub resetAccount(ByRef account As AccountStats) 
	'Reset counters
	account.acctInvestment = 0
	account.acctDividends = 0
	account.acctMarket = 0
	account.acctPrice = 0
	account.acctIncome = 0
	account.acctExpense = 0
	account.acctGains = 0
		
	'If the account has Units/Value
	If Not (account.isNonAsset) Then
		'Record last years values
	    account.acctLastValue = account.acctValue
		account.acctLastUnits = account.acctUnits
			
		'Reset Value for assets
		If (account.hasUnits) Then
			account.acctValue = 0
		End If
			
		'If we finished with no value
		If (account.acctLastValue = 0) Then
			'Reset active flag
			account.isActive = False()
		End If
	End If
End Sub

'Access the AccountType
Public Function getAccountType(ByRef Context As FinanceState, _
			       ByRef acctType As String) As AccountType
	Dim myMap As Object
	Dim myType As Object 
	
	'Access the map
	Set myMap = Context.mapAccountTypes
	
	'Access any existing value
	Set myType = findHashKey(myMap, acctType)
		
	'If this is an unknown Account
	If (IsNull(myType)) Then
		'Report error
		msgBox "AccountType " + acctType + " was not found"
		End
	End If
	
	'Return the information
	Set getAccountType = myType
End Function

'Access the Account Statistics 
Public Function getAccountStats(ByRef Context As FinanceState, _
				ByRef Account As String) As AccountStats
	Dim myMap As Object
	Dim myAccount As Object 
	
	'Access the map
	Set myMap = Context.mapAccounts
	
	'Access any existing value
	Set myAccount = findHashKey(myMap, Account)
		
	'If this is an unknown Account
	If (IsNull(myAccount)) Then
		'Report error
		msgBox "Account " + Account + " was not found"
		End
	End If
	
	'Return the information
	Set getAccountStats = myAccount
End Function
