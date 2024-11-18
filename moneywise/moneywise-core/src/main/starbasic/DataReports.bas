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
Public Const rangeAssetsYears As String = "AssetsYears"
Public Const rangeAssetsNames As String = "AssetsNames"
Public Const rangeAssetsData As String = "AssetsData"
Public Const rangeIncomeYears As String = "IncomeYears"
Public Const rangeIncomeNames As String = "IncomeNames"
Public Const rangeIncomeData As String = "IncomeData"
Public Const rangeExpenseYears As String = "ExpenseYears"
Public Const rangeExpenseNames As String = "ExpenseNames"
Public Const rangeExpenseData As String = "ExpenseData"
Public Const rangeCategoryYears As String = "CategoryYears"
Public Const rangeCategoryNames As String = "CategoryNames"
Public Const rangeCategoryData As String = "CategoryData"
Public Const rangeUnitsYears As String = "UnitsYears"
Public Const rangeUnitsNames As String = "UnitsNames"
Public Const rangeUnitsData As String = "UnitsData"
Public Const rangeOpenBalance As String = "OpeningBalances"

Private Const colSort = 1
Private Const colSortLast = 1000000

'PreProcess reports
Public Sub preProcessReports(ByRef Context As FinanceState)
	'Access the current workbook
	Set myDoc = Context.docBook
	
    'Reset sort indexes for assets
    Set myData = myDoc.NamedRanges.getByName(rangeAssetsData).getReferredCells()
	resetSortIndexes(myData)

    'Reset sort indexes for income
    Set myData = myDoc.NamedRanges.getByName(rangeIncomeData).getReferredCells()
	resetSortIndexes(myData)

    'Reset sort indexes for expenses
    Set myData = myDoc.NamedRanges.getByName(rangeExpenseData).getReferredCells()
	resetSortIndexes(myData)

    'Reset sort indexes for categories
    Set myData = myDoc.NamedRanges.getByName(rangeCategoryData).getReferredCells()
	resetSortIndexes(myData)

    'Reset sort indexes for units
    Set myData = myDoc.NamedRanges.getByName(rangeUnitsData).getReferredCells()
	resetSortIndexes(myData)

	'Report Opening Balances
	reportOpeningBalances(Context)
End Sub
							
'Reset sortIndexes
Private Sub resetSortIndexes(ByRef Range As Object)
    'Reset sort indexes for range
	clearResults(Range, colSort)
	
	'Determine the last row of the range
	myLast = Range.getRows().Count - 1
	
	'Set the sort index to the maximum to ensure it stays last
	myCell = Range.getCellByPosition(colSort, myLast)
    myCell.setValue(colSortLast)
End Sub

'PostProcess reports
Public Sub postProcessReports(ByRef Context As FinanceState)
	'Access the current workbook
	Set myDoc = Context.docBook
	
    'Sort the assets report
    Set myData = myDoc.NamedRanges.getByName(rangeAssetsData).getReferredCells()
	sortReport(myData)

    'Sort the income report
    Set myData = myDoc.NamedRanges.getByName(rangeIncomeData).getReferredCells()
	sortReport(myData)

    'Sort the expenses report
    Set myData = myDoc.NamedRanges.getByName(rangeExpenseData).getReferredCells()
	sortReport(myData)

    'Sort the categories report
    Set myData = myDoc.NamedRanges.getByName(rangeCategoryData).getReferredCells()
	sortReport(myData)

    'Sort the units report
    Set myData = myDoc.NamedRanges.getByName(rangeUnitsData).getReferredCells()
	sortReport(myData)
End Sub

'Sort report
Private Sub sortReport(ByRef Range As Object)
    ' First remove any unused Rows
    removeUnusedRows(Range)
    
    ' allocate the variables
    Dim sortFields(1) As New com.sun.star.util.SortField
    Dim sortProperties(0) As New com.sun.star.beans.PropertyValue

    ' set the sort fields for the sort columns	
    sortFields(0).Field = colSort
    sortFields(0).SortAscending = TRUE
    sortFields(0).FieldType = com.sun.star.util.SortFieldType.NUMERIC
    sortFields(1).Field = 0
    sortFields(1).SortAscending = TRUE
    sortFields(1).FieldType = com.sun.star.util.SortFieldType.ALPHANUMERIC
    
    ' set the sort descriptor 
    sortProperties(0).Name = "SortFields"
    sortProperties(0).Value = sortFields()

    ' sort on columns
    Range.Sort(sortProperties())    
End Sub

'remove unused rows
Private Sub removeUnusedRows(ByRef Range As Object)
    'obtain the sheet
    Set myDoc = ThisComponent
    Set myCell = Range.getCellByPosition(0, 0)
    Set myAddress = myCell.getCellAddress()
    Set mySheet = myDoc.Sheets(myAddress.Sheet)
    
    'obtain the first row 
    myBaseRow = myAddress.Row
    
	'Create result table for accounts
	myRow = Range.getRows().Count - 1
	
	'Loop through the Results table 
	For myIndex = myRow - 1 to 0 Step -1
		'Access the cell
		Set myCell = Range.getCellByPosition(colSort, myIndex)
			
		'If the cell is empty
		If (myCell.Type = com.sun.star.table.CellContentType.EMPTY) Then
		    mySheet.Rows.removeByIndex(myBaseRow + myIndex, 1)
		End If
	Next
End Sub

'Report on Units Data for the Year
Private Sub reportUnitsYear(ByRef Context As FinanceState, _
							ByRef Year As String)
	Dim myDoc As Object
	Dim myData As Object
	Dim myAccount As Object
	Dim myRow As Integer
	Dim myCol As Integer
	
	'Access the current workbook
	Set myDoc = Context.docBook
	
    'Access the ranges
    Set myData = myDoc.NamedRanges.getByName(rangeUnitsData).getReferredCells()
    
    'Determine the column to use
    myCol = getHorizontalIndex(Context, rangeUnitsYears, Year)
    
    'Adjust to pass over name and sort index
    myCol = myCol + colSort + 1

	'Clear results
	clearResults(myData, myCol)
	
	'Loop through the Accounts 
	Set myIterator = hashIterator(Context.mapCache)
	While (hashHasNext(myIterator))
		'Access the element
		Set myAccount = hashNext(myIterator)

		'If the element has Units
		If ((myAccount.hasUnits) And (myAccount.isActive)) Then
		    'Determine the row to use
    		myRow = myAccount.idxUnits
    		if (myRow = -1) Then
    			'Look up the index
    			myRow = allocateVerticalIndex(Context, rangeUnitsNames, myAccount.strAccount)
  				myAccount.idxUnits = myRow
  				
  				'Record the sort index
  				myCell = myData.getCellByPosition(colSort, myRow)
  				myCell.setValue(myAccount.acctType.idxSort)
    		End If
    		 
			'Access the cell
			myCell = myData.getCellByPosition(myCol, myRow)
			myCell.setValue(myAccount.acctUnits)
		End If 
	Wend
End Sub

'Report on Opening Balances
Private Sub reportOpeningBalances(ByRef Context As FinanceState)
	Dim myDoc As Object
	Dim myData As Object
	Dim myAccount As Object
	Dim myRow As Integer
	
	'Access the current workbook
	Set myDoc = Context.docBook
	
    'Access the ranges
    Set myData = myDoc.NamedRanges.getByName(rangeOpenBalance).getReferredCells()
    Set myAssets = myDoc.NamedRanges.getByName(rangeAssetsData).getReferredCells()
    
	'Clear results
	clearResults(myData, 0)
	
	'Loop through the Accounts 
	Set myIterator = hashIterator(Context.mapAccounts)
	While (hashHasNext(myIterator))
		'Access the element
		Set myAccount = hashNext(myIterator)
		
		'If the element has Units or Value
		If Not(myAccount.isNonAsset) And (myAccount.acctOpenBalance <> 0) Then
		    'Determine the row to use
    		myRow = myAccount.idxAssets
    		if (myRow = -1) Then
    			'Look up the index
    			myRow = allocateVerticalIndex(Context, rangeAssetsNames, myAccount.strAccount)
   				myAccount.idxAssets = myRow
  				
  				'Record the sort index
  				myCell = myAssets.getCellByPosition(colSort, myRow)
  				myCell.setValue(myAccount.acctType.idxSort)
    		End If
    		 
			'Access the cell
			myCell = myData.getCellByPosition(0, myRow)
			myCell.setValue(myAccount.acctOpenBalance)

			'Access account in cache
			Set x = getCachedAccount(Context, myAccount.strAccount)
		End If 
	Wend
End Sub

'Report on Assets Data for the Year
Private Sub reportAssetsYear(ByRef Context As FinanceState, _
							 ByRef Year As String)
	Dim myDoc As Object
	Dim myData As Object
	Dim myAccount As Object
	Dim myRow As Integer
	Dim myCol As Integer
	
	'Access the current workbook
	Set myDoc = Context.docBook
	
    'Access the ranges
    Set myData = myDoc.NamedRanges.getByName(rangeAssetsData).getReferredCells()
    
    'Determine the column to use
    myCol = getHorizontalIndex(Context, rangeAssetsYears, Year)

    'Adjust to pass over name and sort index
    myCol = myCol + colSort + 1

	'Clear results
	clearResults(myData, myCol)
	
	'Loop through the Accounts 
	Set myIterator = hashIterator(Context.mapCache)
	While (hashHasNext(myIterator))
		'Access the element
		Set myAccount = hashNext(myIterator)
		
		'If the element has Units or Value
		If Not(myAccount.isNonAsset) And Not(myAccount.isAutoExpense) And (myAccount.isActive) Then
		    'Determine the row to use
    		myRow = myAccount.idxAssets
    		if (myRow = -1) Then
    			'Look up the index
    			myRow = allocateVerticalIndex(Context, rangeAssetsNames, myAccount.strAccount)
   				myAccount.idxAssets = myRow
  				
  				'Record the sort index
  				myCell = myData.getCellByPosition(colSort, myRow)
  				myCell.setValue(myAccount.acctType.idxSort)
    		End If
    		 
			'Access the cell
			myCell = myData.getCellByPosition(myCol, myRow)
			myCell.setValue(myAccount.acctValue)
		End If 
	Wend
End Sub

'Report on Income Data for the Year
Sub reportIncomeYear(ByRef Context As FinanceState, _
					 ByRef Year As String)
	Dim myDoc As Object
	Dim myData As Object
	Dim myAccount As Object
	Dim myRow As Integer
	Dim myCol As Integer
	
	'Access the current workbook
	Set myDoc = Context.docBook
	
    'Access the ranges
    Set myData = myDoc.NamedRanges.getByName(rangeIncomeData).getReferredCells()
    
    'Determine the column to use
    myCol = getHorizontalIndex(Context, rangeIncomeYears, Year)

    'Adjust to pass over name and sort index
    myCol = myCol + colSort + 1
    
	'Clear results
	clearResults(myData, myCol)
	
	'Loop through the Accounts 
	Set myIterator = hashIterator(Context.mapCache)
	While (hashHasNext(myIterator))
		'Access the element
		Set myAccount = hashNext(myIterator)

		'If the element has neither units nor value and has income
		If ((myAccount.isNonAsset) And (myAccount.acctIncome <> 0)) Then
		    'Determine the row to use
    		myRow = myAccount.idxIncome
    		if (myRow = -1) Then
    			'Look up the index
    			myRow = allocateVerticalIndex(Context, rangeIncomeNames, myAccount.strAccount)
  				myAccount.idxIncome = myRow
  				
  				'Record the sort index
  				myCell = myData.getCellByPosition(colSort, myRow)
  				myCell.setValue(myAccount.acctType.idxSort)
    		End If
    		 
			'Access the cell
			myCell = myData.getCellByPosition(myCol, myRow)
			myCell.setValue(myAccount.acctIncome)
		End If 
	Wend
End Sub

'Report on Expense Data for the Year
Sub reportExpenseYear(ByRef Context As FinanceState, _
					  ByRef Year As String)
	Dim myDoc As Object
	Dim myData As Object
	Dim myAccount As Object
	Dim myRow As Integer
	Dim myCol As Integer
	
	'Access the current workbook
	Set myDoc = Context.docBook
	
    'Access the ranges
    Set myData = myDoc.NamedRanges.getByName(rangeExpenseData).getReferredCells()
    
    'Determine the column to use
    myCol = getHorizontalIndex(Context, rangeExpenseYears, Year)

    'Adjust to pass over name and sort index
    myCol = myCol + colSort + 1

	'Clear results
	clearResults(myData, myCol)
	
	'Loop through the Accounts 
	Set myIterator = hashIterator(Context.mapCache)
	While (hashHasNext(myIterator))
		'Access the element
		Set myAccount = hashNext(myIterator)
		
		'If the element has neither units nor value
		If ((myAccount.isNonAsset) Or (myAccount.isAutoExpense)) And (myAccount.acctExpense <> 0) Then
		    'Determine the row to use
    		myRow = myAccount.idxExpense
    		if (myRow = -1) Then
    			'Look up the index
    			myRow = allocateVerticalIndex(Context, rangeExpenseNames, myAccount.strAccount)
 				myAccount.idxExpense = myRow
  				
  				'Record the sort index
  				myCell = myData.getCellByPosition(colSort, myRow)
  				myCell.setValue(myAccount.acctType.idxSort)
			End If
			    		
			'Access the cell
			myCell = myData.getCellByPosition(myCol, myRow)
			myCell.setValue(myAccount.acctExpense)
		End If 
	Wend
End Sub

'Report on Category Data for the Year
Sub reportCategoryYear(ByRef Context As FinanceData, _
					   ByRef Year As String)
	Dim myDoc As Object
	Dim myData As Object
	Dim myCategory As Object
	Dim myRow As Integer
	Dim myCol As Integer
	Dim hasInfo As Boolean
		
	'Access the current workbook
	Set myDoc = Context.docBook
	
    'Access the ranges
    Set myData = myDoc.NamedRanges.getByName(rangeCategoryData).getReferredCells()
    
    'Determine the column to use
    myCol = getHorizontalIndex(Context, rangeCategoryYears, Year)

    'Adjust to pass over name and sort index
    myCol = myCol + colSort + 1

	'Clear results
	clearResults(myData, myCol)
	
	'Loop through the Accounts 
	Set myIterator = hashIterator(Context.mapCategories)
	While (hashHasNext(myIterator))
		'Access the element
		Set myCategory = hashNext(myIterator)
		
		'Determine whether we have any information on this category
		hasInfo = Not(myCategory.isTransfer)  _
			      And ((myCategory.catValue <> 0) Or (myCategory.catTaxCredit <> 0) _
			      Or   (myCategory.catWithheld <> 0))
		    		 
		'If the element has information
		If hasInfo Then
		    'Determine the row to use
   			myRow = myCategory.idxCategory
   			if (myRow = -1) Then
   				'Look up the index
	   			myRow = allocateVerticalIndex(Context, rangeCategoryNames, myCategory.strCategory)
   				myCategory.idxCategory = myRow
  				
  				'Record the sort index
  				myCell = myData.getCellByPosition(colSort, myRow)
  				myCell.setValue(myCategory.idxSort)
    		End If

			'Access the cell
			myCell = myData.getCellByPosition(myCol, myRow)
			myValue = myCategory.catValue + myCategory.catTaxCredit _
						+ myCategory.catWithheld
            If Not(myCategory.isIncome) And Not(myCategory.isMarket) Then
                myValue = -myValue
            End If
			myCell.setValue(myValue)
		End If
	Wend
End Sub
