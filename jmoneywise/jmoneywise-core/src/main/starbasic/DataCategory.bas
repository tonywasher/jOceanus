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
Private Const rangeCategoryInfo As String = "EventCategoryInfo"

'Category Column locations
Private Const colCatName As Integer = 0
Private Const colCatClass As Integer = 1
Private Const colCatParent As Integer = 2
Private Const colCatHidden As Integer = 3
Private Const colCatIncome As Integer = 4
Private Const colCatTransfer As Integer = 5
Private Const colCatInterest As Integer = 6
Private Const colCatDividend As Integer = 7
Private Const colCatRental As Integer = 8
Private Const colCatLoanPay As Integer = 9
Private Const colCatAssetEarn As Integer = 10
Private Const colCatRights As Integer = 11
Private Const colCatDemerger As Integer = 12
Private Const colCatTakeover As Integer = 13

'Statistics for Category
Public Type CategoryStats
	'Category details
	strCategory As String
	strCatClass As String
	strCatParent As String
	
	'Flags
	isInterest As Boolean
	isDividend As Boolean
	isIncome As Boolean
	isTransfer As Boolean
	isRental As Boolean
	isLoanPay As Boolean
	isAssetEarn As Boolean
	isStockDemerger As Boolean
	isStockTakeover As Boolean
	isStockRights As Boolean
	
	'Reporting index
	idxCategory As Integer
	
	'Counters
	catValue As Double
	catTaxCredit As Double
	catNatInsurance As Double
	catWithheld As Double
	
	'Additional three fields declared to fix bug in debugger which loses last three fields
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'Allocate an instance of categoryStats
Private Function allocateCategory() As CategoryStats 
	Dim myResult As New CategoryStats
	Set allocateCategory = myResult
End Function

'Load Categories
Private Sub loadCategories(ByRef Context As FinanceState)
	Dim myDoc As Object
	Dim myRange As Object
	Dim myRow As Object
	
	'Access the current workbook and the map
	Set myDoc = Context.docBook
	Set myMap = Context.mapCategories
	     
    'Access the Category info
    Set myRange = myDoc.NamedRanges.getByName(rangeCategoryInfo).getReferredCells()
    
	'Loop through the rows in the range    
    For Each myRow in myRange.getRows()
		'Allocate the new category    
    	Set myCat = allocateCategory()
    
    	'Access name	
	    myName = myRow.getCellByPosition(colCatName, 0).getString()
	    
    	'Build values 
    	myCat.strCategory = myName
	    myCat.strCatClass = myRow.getCellByPosition(colCatClass, 0).getString()
	    myCat.strCatParent = myRow.getCellByPosition(colCatParent, 0).getString()
	    myCat.isIncome = myRow.getCellByPosition(colCatIncome, 0).getValue()
	    myCat.isTransfer = myRow.getCellByPosition(colCatTransfer, 0).getValue()
	    myCat.isInterest = myRow.getCellByPosition(colCatInterest, 0).getValue()
	    myCat.isDividend = myRow.getCellByPosition(colCatDividend, 0).getValue()
	    myCat.isRental = myRow.getCellByPosition(colCatRental, 0).getValue()
	    myCat.isLoanPay = myRow.getCellByPosition(colCatLoanPay, 0).getValue()
	    myCat.isAssetEarn = myRow.getCellByPosition(colCatAssetEarn, 0).getValue()
	    myCat.isStockRights = myRow.getCellByPosition(colCatRights, 0).getValue()
	    myCat.isStockDemerger = myRow.getCellByPosition(colCatDemerger, 0).getValue()
	    myCat.isStockTakeover = myRow.getCellByPosition(colCatTakeover, 0).getValue()

		'Initialise index
		myCat.idxCategory = -1
		
		'Reset the category
		resetCategory(myCat)
		    		
    	'Store the category
    	putHashKey(myMap, myName, myCat) 
	Next
End Sub

'Reset Category totals
Private Sub resetCategory(ByRef category As CategoryStats) 
	'Reset counters
	category.catValue = 0
	category.catTaxCredit = 0
	category.catNatInsurance = 0
	category.catWithheld = 0
End Sub

'Access the Category Statistics 
Public Function getCategoryStats(ByRef Context As FinanceState, _
								 ByRef Category As String) As CategoryStats
	Dim myMap As Object
	Dim myCategory As Object 
	
	'Access the map
	Set myMap = Context.mapCategories
	
	'Access any existing value
	Set myCategory = findHashKey(myMap, Category)
		
	'If this is a new TransType
	If (IsNull(myCategory)) Then
		'Report error
		msgBox "Category " + Category + " was not found"
		End
	End If
	
	'Return the information
	Set getCategoryStats = myCategory	
End Function
