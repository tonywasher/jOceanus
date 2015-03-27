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
'Validate the most recent year
Sub ValidateLastYear()
	'Determine the number of accounts (add one for OpeningBalance)
	myContext = allocateState()
	myDoc = myContext.docBook
			
	'Access the unit years range
	myRange = myDoc.NamedRanges.getByName(rangeAssetsYears).getReferredCells()
	
	'Access the year
	myCell = myRange.getCellByPosition(0, 0)
	myYear = myCell.getString()
	
	'Analyse the Year
	validateYear(myContext, myYear)
End Sub

'Analyse data for the year
Sub validateYear(ByRef Context As FinanceState, _
				 ByRef Year As String)
	Dim myDoc As Object
	Dim myRange As Object
    Dim myCredit As String
    Dim myDebit As String
    Dim myCategory As String
    Dim myDate As Date
    Dim myLastDate As Date
    Dim myLastCredit As String
    Dim myLastDebit As String
	Dim myDebInfo As Object
	Dim myCredInfo As Object
	             
	'Access the current workbook
	myDoc = Context.docBook
	
	'Access the requested range
	myRange = myDoc.NamedRanges.getByName(Year).getReferredCells()
	
	'Loop through the rows in the range    
    For Each myRow in myRange.getRows()
		'Access the Values in the row
		myCredit    = myRow.getCellByPosition(colEvtCredit, 0).getString()
		myDebit     = myRow.getCellByPosition(colEvtDebit, 0).getString()
        myDate      = myRow.getCellByPosition(colEvtDate, 0).getValue()
		myCategory  = myRow.getCellByPosition(colEvtCategory, 0).getString()

		'If this is a split record
		If (myDate = 0) Then 
			' Pick up old Date 
			myDate = myLastDate
			
			'Pick up old Debit if required
			If (myDebit = "") Then
				myDebit = myLastDebit
			End If
			
			'Pick up old Credit if required
			If (myCredit = "") Then
				myCredit = myLastCredit
			End If
		End If
					
		'Check that we are ascending dates
		If (myDate < myLastDate) Then
			msgBox Year & "contains out of sequence dates " & myLastDate & " followed by " & myDate
			End
		End If	
						      
		'Access the information for the accounts
		myCredInfo = getAccountStats(Context, myCredit)
		myDebInfo = getAccountStats(Context, myDebit)
		myCatInfo = getCategoryStats(Context, myCategory)

		'Check that we do not have two nonAssets
		If ((myCredInfo.isNonAsset) And (myDebInfo.isNonAsset)) Then
			msgBox Year & " contains transfer from " & myDebit & " to " & myCredit & " on " & myDate
			End
		End If
		
		'If we have two non Assets
		If Not ((myCredInfo.isNonAsset) Or (myDebInfo.isNonAsset)) Then
			If Not ((myCatInfo.isTransfer) Or (myCatInfo.isInterest) Or (myCatInfo.isDividend)) Then
				msgBox Year & " contains non-transfer from " & myDebit & " to " & myCredit & " on " & myDate
				End
			End If 
		End If
		
        'Store details
        myLastDate = myDate
        myLastDebit = myDebit
        myLastCredit = myCredit
    Next    
    
    msgBox Year & " is valid"
    End
End Sub

