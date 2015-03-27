'*******************************************************************************
'* jMoneyWise: Finance Application
'* Copyright 2012,2014 Tony Washer
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
'Allocate index in a vertical array
Private Function allocateVerticalIndex(ByRef Context As FinanceState, _
									   ByRef RangeName As String, _
									   ByRef Key As String) As Integer
	Dim myRange As Object
	Dim myDoc As Object 
	Dim myRow As Integer
	Dim myValue As String
	
	'Access the current workbook
	Set myDoc = Context.docBook
	
   	'Resolve the range name
   	Set myRange = myDoc.NamedRanges.getByName(RangeName).getReferredCells()
   	
	'Loop through the rows in the range    
    For myRow = 1 TO myRange.getRows().Count		
    	'Access the value
    	myValue =  myRange.getCellByPosition(0, myRow-1).getString()
    	
		'If we have matched the key
		If (myValue = Key) Then 
			' Record index and break loop
			allocateVerticalIndex = myRow - 1
			Exit Function
		End If
    Next
    
   	'Allocate a new Row 1 from the end.
	myAddress = myRange.RangeAddress
	mySheet = myDoc.Sheets(myAddress.Sheet)
	mySheet.Rows.insertByIndex(myAddress.EndRow,1)
		
	'Access the row and set new key value
	myCell = mySheet.getCellByPosition(0, myAddress.EndRow)
	myCell.setString(Key)
	allocateVerticalIndex = myAddress.EndRow-1

	'Report the addition   		
	msgBox "Added " & Key & " to " & RangeName
End Function
    
'Obtain index in a horizontal array
Private Function getHorizontalIndex(ByRef Context As FinanceState, _
									ByRef RangeName As String, _
									ByRef Key As String) As Integer
	Dim myRange As Object
	Dim myDoc As Object 
	Dim myCol As Integer
	Dim myValue As String
	
	'Access the current workbook
	Set myDoc = Context.docBook
	
   	'Resolve the range name
   	Set myRange = myDoc.NamedRanges.getByName(RangeName).getReferredCells()
   	
	'Loop through the columns in the range    
    For myCol = 1 TO myRange.getColumns().Count		
    	'Access the value
    	myValue =  myRange.getCellByPosition(myCol-1, 0).getString()
    	
		'If we have matched the key
		If (myValue = Key) Then 
			' Record index and break loop
			getHorizontalIndex = myCol - 1 
			isFound = True()
			Exit Function
		End If
    Next
    
	'Report the error
	msgBox Key & " was not found in " & RangeName
   	End   
End Function

'Obtain index in a vertical array
Public Function getVerticalIndex(ByRef Context As FinanceState, _
								 ByRef RangeName As String, _
								 ByRef Key As String) As Integer
	Dim myRange As Object
	Dim myDoc As Object 
	Dim myRow As Integer
	Dim myValue As String
	
	'Access the current workbook
	myDoc = Context.docBook
	
   	'Resolve the range name
   	myRange = myDoc.NamedRanges.getByName(RangeName).getReferredCells()
   	
	'Loop through the rows in the range    
	isFound = False()
    For myRow = 1 TO myRange.getRows().Count		
    	'Access the value
    	myValue =  myRange.getCellByPosition(0, myRow-1).getString()
    	
		'If we have matched the key
		If (myValue = Key) Then 
			' Record index and break loop
			getVerticalIndex = myRow
			isFound = True()
			Exit For
		End If
    Next
    
    'If we did not find the value
    If NOT isFound Then
		'Report the error
   		msgBox Key & " was not found in " & RangeName
   		getVerticalIndex = 0
   		End
    End If
End Function
    
'Get the date for a Year
Public Function getDateForYear(ByRef Year As String) As Double
	Dim myYear As Integer
	
    'Isolate the year part
    myYear = Right(Year, 2)
    
    'Calculate the actual year
    If (myYear < 50) Then
    	myYear = myYear + 2000
    Else 
    	myYear = myYear + 1900
    End if
    
    'Build the new date
    getDateForYear = DateSerial(myYear, 4, 5)     
End Function

'Clear results
Private Sub clearResults(ByRef data As Object, _
						 ByVal col As Integer)
	'Determine clear value 							  
	myClear = com.sun.star.sheet.CellFlags.VALUE _
			+com.sun.star.sheet.CellFlags.STRING _
			+com.sun.star.sheet.CellFlags.FORMULA
			
	'Create result table for accounts
	myCount = data.getRows().Count - 1
	
	'Loop through the Results table 
	For myIndex = 0 to myCount
		'Access the cell
		Set myCell = data.getCellByPosition(col, myIndex)
			
		'Clear the cell if required
		myCell.clearContents(myClear)
	Next
End Sub
