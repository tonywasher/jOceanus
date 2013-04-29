'*******************************************************************************
'* jMoneyWise: Finance Application
'* Copyright 2012,2013 Tony Washer
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
Private Const rangeTaxParams As String = "TaxParams"

'Tax Column locations
Private Const colTaxYear As Integer = 0
Private Const colTaxRegime As Integer = 1
Private Const colTaxAllow As Integer = 2
Private Const colTaxLoBand As Integer = 3
Private Const colTaxBasicBand As Integer = 4
Private Const colTaxRentAllow As Integer = 5
Private Const colTaxLoRate As Integer = 6
Private Const colTaxBasicRate As Integer = 7
Private Const colTaxIntRate As Integer = 8
Private Const colTaxDivRate As Integer = 9
Private Const colTaxHiRate As Integer = 10
Private Const colTaxHiDivRate As Integer = 11
Private Const colTaxAddRate As Integer = 12
Private Const colTaxAddDivRate As Integer = 13
Private Const colTaxLoAgeAllow As Integer = 14
Private Const colTaxHiAgeAllow As Integer = 15
Private Const colTaxAgeAllowLmt As Integer = 16
Private Const colTaxAddAllowLmt As Integer = 17
Private Const colTaxAddAllowTHold As Integer = 18
Private Const colTaxCapAllow As Integer = 19
Private Const colTaxCapRate As Integer = 20
Private Const colTaxHiCapRate As Integer = 21

'Parameters for Tax
Public Type TaxParms
	'Tax details
	strYear As String
	strRegime As String
	
	'Values
	taxAllowance As Double
	taxLoBand As Double
	taxBasicBand As Double
	taxRentAllow As Double
	taxCapAllow As Double
	taxLoAgeAllow As Double
	taxHiAgeAllow As Double
	taxAgeAllowLimit As Double
	taxAddAllowLimit As Double
	taxAddAllowTHold As Double
	
	'Rates
	taxLoRate As Double
	taxBasicRate As Double
	taxHiRate As Double
	taxAddRate As Double
	taxIntRate As Double
	taxDivRate As Double
	taxHiDivRate As Double
	taxAddDivRate As Double
	taxCapRate As Double
	taxHiCapRate As Double
	
	'Additional three fields declared to fix bug in debugger which loses last three fields
	idx1 As Integer
	idx2 As Integer
	idx3 As Integer
End Type

'Allocate an instance of taxParms
Private Function allocateTaxParms() As TaxParms 
	Dim myResult As New TaxParms
	Set allocateTaxParms = myResult
End Function

'Load Tax
Private Sub loadTax(ByRef Context As FinanceState)
	Dim myDoc As Object
	Dim myRange As Object
	Dim myRow As Object
	
	'Access the current workbook and the map
	Set myDoc = Context.docBook
	Set myMap = Context.mapTax
	     
    'Access the Tax info
    Set myRange = myDoc.NamedRanges.getByName(rangeTaxParams).getReferredCells()
    
	'Loop through the rows in the range    
    For Each myRow in myRange.getRows()
		'Allocate the new tax year
    	Set myTax = allocateTaxParms()
    
    	'Access name	
	    myYear = myRow.getCellByPosition(colTaxYear, 0).getString()
	    
    	'Build values 
    	myTax.strYear = myYear
	    myTax.strRegime = myRow.getCellByPosition(colTaxRegime, 0).getString()
	    myTax.taxAllowance = myRow.getCellByPosition(colTaxAllow, 0).getValue()
	    myTax.taxLoBand = myRow.getCellByPosition(colTaxLoBand, 0).getValue()
	    myTax.taxBasicBand = myRow.getCellByPosition(colTaxBasicBand, 0).getValue()
	    myTax.taxRentAllow = myRow.getCellByPosition(colTaxRentAllow, 0).getValue()
	    myTax.taxCapAllow = myRow.getCellByPosition(colTaxCapAllow, 0).getValue()
	    myTax.taxLoAgeAllow = myRow.getCellByPosition(colTaxLoAgeAllow, 0).getValue()
	    myTax.taxHiAgeAllow = myRow.getCellByPosition(colTaxHiAgeAllow, 0).getValue()
	    myTax.taxAgeAllowLimit = myRow.getCellByPosition(colTaxAgeAllowLmt, 0).getValue()
	    myTax.taxAddAllowLimit = myRow.getCellByPosition(colTaxAddAllowLmt, 0).getValue()
	    myTax.taxAddAllowTHold = myRow.getCellByPosition(colTaxAddAllowTHold, 0).getValue()
	    myTax.taxLoRate = myRow.getCellByPosition(colTaxLoRate, 0).getValue()
	    myTax.taxBasicRate = myRow.getCellByPosition(colTaxBasicRate, 0).getValue()
	    myTax.taxHiRate = myRow.getCellByPosition(colTaxHiRate, 0).getValue()
	    myTax.taxAddRate = myRow.getCellByPosition(colTaxAddRate, 0).getValue()
	    myTax.taxIntRate = myRow.getCellByPosition(colTaxIntRate, 0).getValue()
	    myTax.taxDivRate = myRow.getCellByPosition(colTaxDivRate, 0).getValue()
	    myTax.taxHiDivRate = myRow.getCellByPosition(colTaxHiDivRate, 0).getValue()
	    myTax.taxAddDivRate = myRow.getCellByPosition(colTaxAddDivRate, 0).getValue()
	    myTax.taxCapRate = myRow.getCellByPosition(colTaxCapRate, 0).getValue()
	    myTax.taxHiCapRate = myRow.getCellByPosition(colTaxHiCapRate, 0).getValue()

    	'Store the tax details
    	putHashKey(myMap, myName, myTax) 
	Next
End Sub

