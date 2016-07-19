'*******************************************************************************
'* jMoneyWise: Finance Application
'* Copyright 2012,2016 Tony Washer
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
'Information for FinanceState
Public Type FinanceState
	'SpreadSheet
	docBook As Object
	
	'Category List
	mapCategories As Object
	
	'Account List
	mapAccountTypes As Object
	mapAccounts As Object
	
	'Tax List
	mapTax As Object
	
	'Cached accounts
	mapCache As Object
End Type

'Allocate macro state
Private Function allocateState() As FinanceState 
	'Allocate state
	Dim myState As New FinanceState
	
	'Initialise values
	Set myState.docBook = ThisComponent
	Set myState.mapCategories = allocateHashMap()
	Set myState.mapAccountTypes = allocateHashMap()
	Set myState.mapAccounts = allocateHashMap()
	Set myState.mapTax = allocateHashMap()
	Set myState.mapCache = allocateHashMap()
	
	'Load data
	loadCategories(myState)
	loadAccountTypes(myState)
	loadAccounts(myState)
	loadTax(myState)
	
	'Return the state
	Set allocateState = myState
End Function

'Access the Cached Account Statistics 
Public Function getCachedAccount(ByRef Context As FinanceState, _
								 ByRef Account As String) As AccountStats
	Dim myMap As Object
	Dim myAccount As Object 
	
	'Access the map
	Set myCache = Context.mapCache
	Set myMap = Context.mapCache
	
	'Access any existing value
	Set myAccount = findHashKey(myCache, Account)
		
	'If this is an unknown Account
	If (IsNull(myAccount)) Then
		'Access from main list
		Set myAccount = getAccountStats(Context, Account)
		
		'Add to cache
		putHashKey(myCache, Account, myAccount)
		
		'If the account has a parent
		Set myParent = myAccount.acctParent
		If Not isNull(myParent) Then		
			'Make sure that the parent is in the cache
			putHashKey(myCache, myParent.strAccount, myParent)
		End If
	End If
	
	'Return the information
	Set getCachedAccount = myAccount
End Function
