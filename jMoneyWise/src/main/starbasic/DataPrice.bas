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
Private Const rangePricesNames As String = "PricesNames"
Private Const rangePricesData As String = "PricesData"
Private Const rangePricesDates As String = "PricesDates"

'Get the Spot Price Index for an Account
Public Function getSpotPriceIndexForAcct(ByRef Context As FinanceState, _
					 ByRef Account As AccountStats) As Integer
	Dim myName As String
	
	'If the price index is unknown
	If (Account.idxPrice = -1) Then	
		'Determine the name to search on
		myName = Account.strAccount
		If Not (Account.strAlias = "") Then
			myName = Account.strAlias
		End If
	     
    	'Determine the index into the PriceNames
    	Account.idxPrice = getHorizontalIndex(Context, rangePricesNames, myName)
    End If

	'Return the index    
    getSpotPriceIndexForAcct = Account.idxPrice    
End Function

'Get the Asset Value for a Date
Public Function getAssetValueForDate(ByRef Context As FinanceState, _
				 ByRef Account As AccountStats, _
				 ByVal PriceDate As Date) As Double
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myPrice As Double
	Dim myDilution As Double

	'Access the SpotPrice index for the date
	myRow = getSpotPriceIndexForDate(Context, PriceDate)
	
    'Determine the column index to use
	myCol = getSpotPriceIndexForAcct(Context, Account)
    		 
	'Access the price and dilution 
	myPrice = getBaseSpotPrice(Context, myCol, myRow)
				
	'If we did not find a price
	If (myPrice = 0) And (Account.acctUnits > 0) Then
		msgBox Account.strAccount & " price was not found in Prices Sheet"
	End If

	'Calculate value
	getAssetValueForDate = myPrice * Account.acctUnits
End Function

'Get the Spot Price Index for a Date
Public Function getSpotPriceIndexForDate(ByRef Context As FinanceState, _
					 ByVal PriceDate As Date) As Integer
	Dim myDoc As Object
	Dim myRow as Integer
	Dim myDate as Date
	
	'Access the current workbook
	myDoc = Context.docBook
	     
    'Access the range
    myRange = myDoc.NamedRanges.getByName(rangePricesDates).getReferredCells()
    
	'Loop through the rows in the range    
    For myRow = 1 TO myRange.getRows().Count		
    	'Access the date
    	myDate =  myRange.getCellByPosition(0, myRow-1).getValue()
    	
		'If we are too late
		If (myDate > PriceDate) Then 
			Exit For
		End If
    Next
    
    'Return the index
    getSpotPriceIndexForDate = myRow - 1
End Function

'Get the Spot Price for a set of indices
Public Function getBaseSpotPrice(ByRef Context As FinanceState, _
				 ByVal AcctIndex As Integer, _
				 ByVal PriceIndex As Integer) As Double
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myRange As Object
	Dim myPrice As Double
	
	'Access the current workbook
	myDoc = Context.docBook
	     
    'Access the SpotPricesData for the range that we are interested in
    myRange = myDoc.NamedRanges.getByName(rangePricesData).getReferredCells()
    
    'Loop backwards through the Cells in the range
    myPrice = 0
	For myIndex = PriceIndex To 1 Step -1
		'Access the price
		myPrice = myRange.getCellByPosition(AcctIndex, myIndex-1).getValue()
		
		'Break loop if we found a price
		If Not (myPrice = 0) Then 
			Exit For
		End If
	Next
	
	'Return the Price
	getBaseSpotPrice = myPrice
End Function
