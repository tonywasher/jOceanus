'*******************************************************************************
'* jMoneyWise: Finance Application
'* Copyright 2012,2017 Tony Washer
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
Private Const colAcctClass As Integer = 3
Private Const colAcctClosed As Integer = 4
Private Const colAcctParent As Integer = 5
Private Const colAcctAlias As Integer = 6
Private Const colAcctPortfolio As Integer = 7
Private Const colAcctMaturity As Integer = 8
Private Const colAcctOpenBal As Integer = 9
Private Const colAcctSymbol As Integer = 10
Private Const colAcctRegion As Integer = 11
Private Const colAcctCurrency As Integer = 12
Private Const colAcctAutoExp As Integer = 13

'Account Type Column locations
Private Const colAcTpName As Integer = 0
Private Const colAcTpClass As Integer = 1
Private Const colAcTpParent As Integer = 2
Private Const colAcTpNewClass As Integer = 3
Private Const colAcTpNetCat As Integer = 4
Private Const colAcTpValue As Integer = 5
Private Const colAcTpUnits As Integer = 6
Private Const colAcTpUnitTrust As Integer = 7
Private Const colAcTpEndowment As Integer = 8
Private Const colAcTpPortfolio As Integer = 9
Private Const colAcTpCapital As Integer = 10
Private Const colAcTpLifeBond As Integer = 11
Private Const colAcTpPension As Integer = 12
Private Const colAcTpAutoUnits As Integer = 13
Private Const colAcTpTaxFree As Integer = 14
Private Const colAcTpGross As Integer = 15

'Account Type
Public Type AccountType
	' Account Type details
	strAccountType As String
	strAccountClass As String
	strAccountParent As String
	
	'Sort index
	idxSort As Integer
	
	'Account flags
	hasUnits As Boolean
	hasValue As Boolean
	isGross As Boolean
	isTaxFree As Boolean
	isNonAsset As Boolean
	isUnitTrust As Boolean
	isEndowment As Boolean
	isPortfolio As Boolean
	isCapital As Boolean
	isLifeBond As Boolean	
	isPension As Boolean	
	isAutoUnits As Boolean	
	numAutoUnits As Integer	
	
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
	strPortfolio As String
	strAlias As String
	strSymbol As String
	strCurrency As String
	strRegion As String
	
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
	isGross As Boolean
	isTaxFree As Boolean
	isCapital As Boolean
	isLifeBond As Boolean
	isEndowment As Boolean
	isUnitTrust As Boolean
	isPension As Boolean
	isPortfolio As Boolean
	isForeign As Boolean
	
	'Reporting indices
	idxAssets As Integer
	idxIncome As Integer
	idxExpense As Integer
	idxUnits As Integer
	idxPrice As Integer
	idxCurrency As Integer
	
	'Counters
	acctValue As Double
	acctOpenBalance As Double
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
    
    'Set sort index 
    mySortIdx = 1
    
	'Loop through the rows in the range    
    For Each myRow in myRange.getRows()
		'Allocate the new account Type    
    	Set myType = allocateAccountType()
    
    	'Access name	
	    myName = myRow.getCellByPosition(colAcTpName, 0).getString()
	    
    	'Build values 
    	myType.strAccountType = myName
	    myType.strAccountClass = myRow.getCellByPosition(colAcTpClass, 0).getString()
	    myType.strAccountParent = myRow.getCellByPosition(colAcTpParent, 0).getString()
	    myType.hasValue = myRow.getCellByPosition(colAcTpValue, 0).getValue()
	    myType.hasUnits = myRow.getCellByPosition(colAcTpUnits, 0).getValue()
	    myType.isNonAsset = Not(myType.hasValue Or myType.hasUnits)
	    myType.isUnitTrust = myRow.getCellByPosition(colAcTpUnitTrust, 0).getValue()
	    myType.isCapital = myRow.getCellByPosition(colAcTpCapital, 0).getValue()
	    myType.isLifeBond = myRow.getCellByPosition(colAcTpLifeBond, 0).getValue()
	    myType.isEndowment = myRow.getCellByPosition(colAcTpEndowment, 0).getValue()
	    myType.isPortfolio = myRow.getCellByPosition(colAcTpPortfolio, 0).getValue()
	    myType.isPension = myRow.getCellByPosition(colAcTpPension, 0).getValue()
	    myType.isTaxFree = myRow.getCellByPosition(colAcTpTaxFree, 0).getValue()
	    myType.isGross = myRow.getCellByPosition(colAcTpGross, 0).getValue()
	    
	    'Record the sort index
	    myType.idxSort = mySortIdx 

        'Handle autoUnits    		
	    myType.numAutoUnits = myRow.getCellByPosition(colAcTpAutoUnits, 0).getValue()
	    myType.isAutoUnits = myType.numAutoUnits <> 0
	    
    	'Store the account type
    	putHashKey(myMap, myName, myType) 
    	
    	'Increment sort index
    	mySortIdx = mySortIdx + 1
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
	    myAcct.strPortfolio = myRow.getCellByPosition(colAcctPortfolio, 0).getString()
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
		myAcct.isPortfolio = myType.isPortfolio
		myAcct.isPension = myType.isPension
		myAcct.isTaxFree = myType.isTaxFree
		myAcct.isGross = myType.isGross
		myAcct.isActive = True()    		
    		
		'Handle value accounts
		myAcct.isAutoExpense = False()
		If (myAcct.hasValue) Then
		    myOpenBal = myRow.getCellByPosition(colAcctOpenBal, 0).getValue()
		    myAcct.acctValue = myOpenBal
		    myAcct.acctOpenBalance = myOpenBal
		    myAcct.strCurrency = myRow.getCellByPosition(colAcctCurrency, 0).getString()
			myAutoExpense = myRow.getCellByPosition(colAcctAutoExp, 0).getString()
			If (myAutoExpense <> "") Then
				Set myAcct.catAutoExpense = getCategoryStats(Context, myAutoExpense)
				myAcct.isAutoExpense = True()
			End If
		End If
		
		'Handle asset accounts
		If (myAcct.hasUnits) Then		
		    myAcct.strCurrency = myRow.getCellByPosition(colAcctCurrency, 0).getString()
	    	myAcct.strAlias = myRow.getCellByPosition(colAcctAlias, 0).getString()
		    myAcct.strSymbol = myRow.getCellByPosition(colAcctSymbol, 0).getString()
		    myAcct.strRegion = myRow.getCellByPosition(colAcctRegion, 0).getString()
		End If 
		
		' Determine whether the account is foreign
	    myAcct.isForeign = Not(myAcct.strCurrency = "")
   		    
		'Initialise indices
		myAcct.idxAssets = -1
		myAcct.idxIncome = -1
		myAcct.idxExpense = -1
		myAcct.idxPrice = -1
		myAcct.idxUnits = -1
		myAcct.idxCurrency = -1
		
		'Reset the account
		resetAccount(myAcct)
		    		
    	'Store the account
    	putHashKey(myMap, myName, myAcct) 
	Next
	
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
