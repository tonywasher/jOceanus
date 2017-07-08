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
Private Const rangePricesNames As String = "PricesNames"
Private Const rangePricesData As String = "PricesData"
Private Const rangePricesDates As String = "PricesDates"
Private Const rangeCurrencyNames As String = "CurrencyNames"
Private Const rangeCurrencyData As String = "CurrencyData"
Private Const rangeCurrencyDates As String = "CurrencyDates"

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
	Dim myValue As Double

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
	myValue = myPrice * Account.acctUnits
	
	'If the asset is in a foreign currency
	if Not (Account.strCurrency = "") Then
	    myValue = getAdjustedValueForDateAndCurrency(Context, Account, myValue, PriceDate)
	End If
	
	'Return the value
	getAssetValueForDate = myValue
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

'Get the xchgRate Index for an Account
Public Function getXchgRateIndexForAcct(ByRef Context As FinanceState, _
						  			    ByRef Account As AccountStats) As Integer
	Dim myName As String
	
	'If the xchgRate index is unknown
	If (Account.idxCurrency = -1) Then	
		'Determine the name to search on
		myName = Account.strCurrency
	     
    	'Determine the index into the CurrencyNames
    	Account.idxCurrency = getHorizontalIndex(Context, rangeCurrencyNames, myName)
    End If

	'Return the index    
    getXchgRateIndexForAcct = Account.idxCurrency    
End Function

'Get the Adjusted Value on a Date for an asset in a foreign currency
Public Function getAdjustedValueForDateAndCurrency(ByRef Context As FinanceState, _
  									               ByRef Account As AccountStats, _
									               ByVal Value As Double, _
									               ByVal RateDate As Date) As Double
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myRate As Double

	'Access the XchgRate index for the date
	myRow = getXchgRateIndexForDate(Context, RateDate)
	
    'Determine the column index to use
	myCol = getXchgRateIndexForAcct(Context, Account)
    		 
	'Access the xchgRate 
	myRate = getBaseXchgRate(Context, myCol, myRow)
				
	'Calculate value
	getAdjustedValueForDateAndCurrency = Value * myRate
End Function

'Get the XchgRate Index for a Date
Public Function getXchgRateIndexForDate(ByRef Context As FinanceState, _
						   			    ByVal RateDate As Date) As Integer
	Dim myDoc As Object
	Dim myRow as Integer
	Dim myDate as Date
	
	'Access the current workbook
	myDoc = Context.docBook
	     
    'Access the range
    myRange = myDoc.NamedRanges.getByName(rangeCurrencyDates).getReferredCells()
    
	'Loop through the rows in the range    
    For myRow = 1 TO myRange.getRows().Count		
    	'Access the date
    	myDate =  myRange.getCellByPosition(0, myRow-1).getValue()
    	
		'If we are too late
		If (myDate > RateDate) Then 
			Exit For
		End If
    Next
    
    'Return the index
    getXchgRateIndexForDate = myRow - 1
End Function

'Get the XchgRate for a set of indices
Public Function getBaseXchgRate(ByRef Context As FinanceState, _
								ByVal AcctIndex As Integer, _
								ByVal RateIndex As Integer) As Double
	Dim myIndex As Integer
	Dim myDoc As Object
	Dim myRange As Object
	Dim myRate As Double
	
	'Access the current workbook
	myDoc = Context.docBook
	     
    'Access the XchgRateData for the range that we are interested in
    myRange = myDoc.NamedRanges.getByName(rangeCurrencyData).getReferredCells()
    
    'Loop backwards through the Cells in the range
    myPrice = 0
	For myIndex = RateIndex To 1 Step -1
		'Access the rate
		myRate = myRange.getCellByPosition(AcctIndex, myIndex-1).getValue()
		
		'Break loop if we found a rate
		If Not (myRate = 0) Then 
			Exit For
		End If
	Next
	
	'Return the Rate
	getBaseXchgRate = myRate
End Function
