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
'Event Column locations
Public Const colEvtDate As Integer = 0
Public Const colEvtDebit As Integer = 1
Public Const colEvtCredit As Integer = 2
Public Const colEvtAmount As Integer = 3
Public Const colEvtCategory As Integer = 4
Private Const colEvtReconciled As Integer = 5
Private Const colEvtComment As Integer = 6
Private Const colEvtTaxCred As Integer = 7
Private Const colEvtEeNatIns As Integer = 8
Private Const colEvtErNatIns As Integer = 9
Private Const colEvtBenefit As Integer = 10
Private Const colEvtDebUnits As Integer = 11
Private Const colEvtCredUnits As Integer = 12
Private Const colEvtDilution As Integer = 13
Private Const colEvtRef As Integer = 14
Private Const colEvtYears As Integer = 15
Private Const colEvtWithheld As Integer = 16
Private Const colEvtThirdParty As Integer = 17
Private Const colEvtThirdPartyAmount As Integer = 18
Private Const colEvtPartnerAmount As Integer = 19
Private Const colEvtXchangeRate As Integer = 20
Private Const colEvtTags As Integer = 21

'Hidden categories
Public Const catTransfer As String = "Transfer"
Public Const catInterest As String = "Income:Interest"
Public Const catDividend As String = "Income:Dividend"
Public Const catShareDividend As String = "Income:ShareDividend"
Public Const catUnitDividend As String = "Income:UnitTrustDividend"
Public Const catTaxFreeDividend As String = "Income:TaxFreeDividend"
Public Const catTaxedInterest As String = "Income:TaxedInterest"
Public Const catGrossInterest As String = "Income:GrossInterest"
Public Const catTaxFreeInterest As String = "Income:TaxFreeInterest"

'Event information
Public Type EventInfo
	'Date of event
	dtDate As Date
	
	'Credit/debit accounts and category
	acctDebit As AccountStats
	acctCredit As AccountStats
	acctThirdParty As AccountStats
	catCategory As CategoryStats

	'String versions
	strDebit As String
	strCredit As String
	strThirdParty As String
	strCategory As String
	strTags As String
		
	'Values
	evtValue As Double
	evtThirdPartyValue As Double
	evtPartnerValue As Double
	evtTaxCredit As Double
	evtEeNatIns As Double
	evtErNatIns As Double
	evtBenefit As Double
	evtWithheld As Double
	evtDebUnits As Double
	evtCredUnits As Double
	evtDilution As Double
	evtXchangeRate As Double
	evtYears As Integer
		
	'Additional three fields declared to fix bug in debugger which loses last three fields
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'Allocate an instance of EventInfo
Private Function allocateEvent() As EventInfo 
	Dim myResult As New EventInfo
	Set allocateEvent = myResult
End Function

'Adjust the Category
Private Sub adjustCategory(ByRef Context As FinanceState, _
						   ByRef Event As EventInfo) 
	'Access category
	myCategory = Event.strCategory
	Set myDebitInfo = Event.acctDebit
	Set myCreditInfo = Event.acctCredit
	
	'Switch on category
	Select Case myCategory
		Case catInterest
			If (myDebitInfo.isTaxFree) Then
				myCategory = catTaxFreeInterest
			ElseIf (myDebitInfo.isGross) Then
				myCategory = catGrossInterest
			Else
				myCategory = catTaxedInterest
			End If
		Case catDividend
			If (myDebitInfo.isTaxFree) Then
				myCategory = catTaxFreeDividend
			ElseIf (myDebitInfo.isUnitTrust) Then
				myCategory = catUnitDividend
			Else
				myCategory = catShareDividend
			End If
	End Select
	
	'Resolve the category
	Event.catCategory = getCategoryStats(Context, myCategory)
	Event.strCategory = myCategory
End Sub

 'Parse the event 
Public Function parseEventRow(ByRef Context As FinanceState, _
							  ByRef eventRow As ScTableRowObj, _
							  ByRef lastEvent As EventInfo) As EventInfo
	Dim myEvent As Variant
	Dim myDate As Date
	Dim myDebit As String
	Dim myCredit As String
	Dim myThirdParty As String
	Dim myCategory As String
	
	'Allocate the event
	Set myEvent = allocateEvent()
	
	'Access key information	
	myDate       = eventRow.getCellByPosition(colEvtDate, 0).getValue()
	myDebit      = eventRow.getCellByPosition(colEvtDebit, 0).getString()
	myCredit     = eventRow.getCellByPosition(colEvtCredit, 0).getString()
	myThirdParty = eventRow.getCellByPosition(colEvtThirdParty, 0).getString()
	myCategory   = eventRow.getCellByPosition(colEvtCategory, 0).getString()
	myTags       = eventRow.getCellByPosition(colEvtTags, 0).getString()
	
	'If this is a split record
	If (myDate = 0) And Not IsNull(lastEvent) Then 
		' Pick up old Date 
		myDate = lastEvent.dtDate
			
		'Pick up old Debit if required
		If (myDebit = "") Then
			myDebit = lastEvent.strDebit
		End If
			
		'Pick up old Credit if required
		If (myCredit = "") Then
			myCredit = lastEvent.strCredit
		End If		
	End If
					
	'Access the Values in the row
	myEvent.dtDate  	  = myDate
	myEvent.strDebit      = myDebit
	myEvent.strCredit     = myCredit
	myEvent.strThirdParty = myThirdParty
	myEvent.strCategory   = myCategory
	myEvent.strTags       = myTags
	
	'Look up the values
	Set myEvent.acctDebit  = getCachedAccount(Context, myDebit)
	Set myEvent.acctCredit = getCachedAccount(Context, myCredit)
	If (myThirdParty <> "") Then
		Set myEvent.acctThirdParty = getCachedAccount(Context, myThirdParty)
	End If

	'Adjust the category if required
	adjustCategory(Context, myEvent)

	'Access the Values in the row
	myEvent.evtValue           = eventRow.getCellByPosition(colEvtAmount, 0).getValue()
	myEvent.evtDebUnits        = eventRow.getCellByPosition(colEvtDebUnits, 0).getValue()
	myEvent.evtCredUnits       = eventRow.getCellByPosition(colEvtCredUnits, 0).getValue()
	myEvent.evtTaxCredit       = eventRow.getCellByPosition(colEvtTaxCred, 0).getValue()
	myEvent.evtEeNatIns        = eventRow.getCellByPosition(colEvtEeNatIns, 0).getValue()
	myEvent.evtErNatIns        = eventRow.getCellByPosition(colEvtErNatIns, 0).getValue()
	myEvent.evtBenefit         = eventRow.getCellByPosition(colEvtBenefit, 0).getValue()
	myEvent.evtDilution        = eventRow.getCellByPosition(colEvtDilution, 0).getValue()
	myEvent.evtWithheld        = eventRow.getCellByPosition(colEvtWithheld, 0).getValue()
	myEvent.evtThirdPartyValue = eventRow.getCellByPosition(colEvtThirdPartyAmount, 0).getValue()
	myEvent.evtXchangeRate     = eventRow.getCellByPosition(colEvtXchangeRate, 0).getValue()
	myEvent.evtPartnerValue    = eventRow.getCellByPosition(colEvtPartnerAmount, 0).getValue()
	
	'Return the event
	Set parseEventRow = myEvent	
End Function
