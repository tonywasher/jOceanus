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
'Colors
Public Const colorRed As Long = 16711680
Public Const colorGreen As Long = 65280
Public Const colorWhite As Long = -1

'Hidden account names 
Public Const acctMarket As String = "Market" 

'Well known accounts
Public Const acctTaxMan As String = "InlandRevenue"
Public Const acctStatePension As String = "StatePension"

'Hidden Reporting Category Names
Public Const catTaxCredit As String = "Taxes:IncomeTax"
Public Const catTaxRelief As String = "Taxes:Relief"
Public Const catBenefit As String = "Income:Benefit"
Public Const catEmployerNatIns As String = "Income:EmployerNatIns"
Public Const catEmployeeNatIns As String = "Income:EmployeeNatIns"
Public Const catWithheld As String = "Expenses:Virtual"
Public Const catMktGrowth As String = "Market:Growth"
Public Const catCapitalGain	As String = "Market:CapitalGain"
Public Const catTaxableGain	As String = "Market:ChargeableGain"

'Analyse data for the year
Sub analyseYear(ByRef Context As FinanceState, _
				ByRef Year As String)
	Dim myDoc As Object
	Dim myRange As Object
    Dim myDebUnits As Double
    Dim myCredUnits As Double
    Dim myReduction As Double
    Dim myValue As Double
    Dim myTaxCred As Double
    Dim myEeNatIns As Double
    Dim myErNatIns As Double
    Dim myBenefit As Double
    Dim myWithheld As Double
    Dim myDate As Date
	Dim myEvent As Object
	Dim myLastEvent As Object
	Dim myDebAcct As Object
	Dim myCredAcct As Object
	Dim myParAcct As Object
	Dim myCatInfo As Object
	Dim myTaxInfo As Object
	Dim myTaxReliefInfo As Object
	Dim myErNatInsInfo As Object
	Dim myEeNatInsInfo As Object
	Dim myBenInfo As Object
	Dim myWithInfo As Object
	Dim myTaxGainInfo As Object
	Dim myCharInfo As Object
	Dim myTaxAcct As Object
	Dim myPensionAcct As Object
	Dim myNIAcct As Object
	             
	'Access the current workbook
	myDoc = Context.docBook

	'Access the requested range
	myRange = myDoc.NamedRanges.getByName(Year).getReferredCells()
	
	'Access the TaxCredit and Taxable Gain information
    myTaxAcct = getCachedAccount(Context, acctTaxMan) 
    myPensionAcct = getCachedAccount(Context, acctStatePension) 
    myTaxInfo = getCategoryStats(Context, catTaxCredit) 
    myTaxReliefInfo = getCategoryStats(Context, catTaxRelief) 
    myErNatInsInfo = getCategoryStats(Context, catEmployerNatIns) 
    myEeNatInsInfo = getCategoryStats(Context, catEmployeeNatIns) 
    myBenInfo = getCategoryStats(Context, catBenefit) 
    myTaxGainInfo = getCategoryStats(Context, catTaxableGain) 
    myWithInfo = getCategoryStats(Context, catWithheld) 
	
    'Build the new date
    myFinalDate = DateSerial(2018, 04, 05)     
    
	'Loop through the rows in the range    
    For Each myRow in myRange.getRows()
		'Parse the row 
		myEvent = parseEventRow(Context, myRow, myLastEvent)
							
		'Access the information from the event
		myDate = myEvent.dtDate
		myCredAcct = myEvent.acctCredit
		myDebAcct = myEvent.acctDebit
		myCatInfo = myEvent.catCategory
		myValue = myEvent.evtValue
		myTaxCred = myEvent.evtTaxCredit
		myEeNatIns = myEvent.evtEeNatIns
		myErNatIns = myEvent.evtErNatIns
		myBenefit = myEvent.evtBenefit
		myWithheld = myEvent.evtWithheld
		myDebUnits = myEvent.evtDebUnits
		myCredUnits = myEvent.evtCredUnits
		
		'Break Off after date
		If (myDate > myFinalDate) Then
			Exit For
		End If	
		
    	'If we have a value in the Units cell
		If ((myDebUnits <> 0) Or (myCredUnits <> 0)) Then
			'If the debit account has units 
			If (myDebAcct.hasUnits) Then
				'Subtract from units
				myDebAcct.acctUnits = myDebAcct.acctUnits - myDebUnits
				myDebAcct.isActive = True()
			End If

			'If the credit account has units 
			If (myCredAcct.hasUnits) Then
				'Add to units
				myCredAcct.acctUnits = myCredAcct.acctUnits + myCredUnits
				myCredAcct.isActive = True()
				
				'If this is a Stock Takeover
				If (myCatInfo.isStockTakeover) Then
					'Set the debit units to zero
					myDebAcct.acctUnits = 0
				End If
			End If			
        End If
        
        'If this is a security Closure, reset units
        If (myCatInfo.isStockClosure) Then 
 		    myDebAcct.acctUnits = 0
        End If 
        
        'If this is the first credit of an autoUnits security, then set units
        If (myCredAcct.hasUnits And myCredAcct.acctType.isAutoUnits And myCredAcct.acctUnits = 0) Then 
 		    myCredAcct.acctUnits = myCredAcct.acctType.numAutoUnits
        End If 
        
    	'If we have a value in the Amount cell
		If (myValue <> 0) Then
			'Access the statistics for the Credit account
			myCredAcct.isActive = True()
			
			'If the credit account has value 
			If (myCredAcct.hasValue) Then
				'If we have autoExpense
				If (myCredAcct.isAutoExpense) Then
					'Add to expense
					myAutoExpense = myCredAcct.catAutoExpense
					myAutoExpense.catValue = myAutoExpense.catValue + myValue				
					myCredAcct.acctExpense = myCredAcct.acctExpense + myValue		
				'If this is Loan payment
				ElseIf (myCatInfo.isLoanPay) Then
					'Access the parent account
					myParAcct = myCredAcct.acctParent					
					
					'Adjust parent expense
					myParAcct.acctExpense = myParAcct.acctExpense + myTaxCred + myValue
				Else
					'Add to value
					myCredAcct.acctValue = myCredAcct.acctValue + myValue
				EndIf
				
			'Else If the credit account has units 
			ElseIf myCredAcct.hasUnits Then
				'Add to investment
				myCredAcct.acctInvestment = myCredAcct.acctInvestment + myValue

				'Add to cost
				myCredAcct.acctCost = myCredAcct.acctCost + myValue
									
			'Else this is an expense
			Else
				'Add to expense
				myCredAcct.acctExpense = myCredAcct.acctExpense + myValue		
			End If
				
			'Access the statistics for the Debit account
			myDebAcct.isActive = True()
			
			'If the debit account has value 
			If (myDebAcct.hasValue) Then
				'If this is interest
				If (myCatInfo.isInterest) Then
					'Access the parent account
					myParAcct = myDebAcct.acctParent					
					
					'Adjust parent income
					myParAcct.acctIncome = myParAcct.acctIncome + myValue + myTaxCred + myWithheld
					myParAcct.acctExpense = myParAcct.acctExpense + myWithheld
				'If this is asset earnings
				ElseIf (myCatInfo.isAssetEarn) Then
					'Access the parent account
					myParAcct = myDebAcct.acctParent					
					
					'Adjust parent income
					myParAcct.acctIncome = myParAcct.acctIncome + myValue + myTaxCred
				ElseIf (myCatInfo.isRental) Then
					'Access the parent account (of credit!!)
					myParAcct = myCredAcct.acctParent					
					
					'Adjust parent income
					myParAcct.acctIncome = myParAcct.acctIncome + myValue
				'If we have autoExpense
				ElseIf (myDebAcct.isAutoExpense) Then
					'Subtract from expense
					myAutoExpense = myDebAcct.catAutoExpense
					myAutoExpense.catValue = myAutoExpense.catValue - myValue
					myDebAcct.acctExpense = myDebAcct.acctExpense - myValue		
				Else
					'Subtract from Value
					myDebAcct.acctValue = myDebAcct.acctValue - myValue
					myDebAcct.acctExpense = myDebAcct.acctIncome - myValue
				EndIf
				
			'If the debit account has units 
			ElseIf (myDebAcct.hasUnits) Then
				'If this is a dividend
				If (myCatInfo.isDividend) Then
					'Add to Dividend value
					myDebAcct.acctDividends = myDebAcct.acctDividends + myValue + myTaxCred

					'Access the parent account
					myParAcct = myDebAcct.acctParent
					
					'Adjust parent income
					myParAcct.acctIncome = myParAcct.acctIncome + myValue + myTaxCred
					
				'Else it is not a dividend or cash takeover
				Else
					'Subtract from investment value
					myDebAcct.acctInvestment = myDebAcct.acctInvestment - myValue - myTaxCred

					'Assume the reduction is the full value taken
					myReduction = myValue
					myCost = myDebAcct.acctCost
					
					'If we are reducing units in the account
					If (myDebUnits > 0) Then
						'Calculate the reduction
						myReduction = myCost * myDebUnits
						myReduction = myReduction / (myDebUnits + myDebAcct.acctUnits)
						
					'Else if this is a stocks rights that is waived
					ElseIf (myCatInfo.isStockRights) Then 
						'Calculate the existing value of the stock
						myShares = getAssetValueForDate(Context, myDebAcct, myDate)
						
						'If this is a large stocks rights waiver(>£3000 and > 5% stock value)
						If (myValue > 3000) And ((myValue * 20) > myShares) Then
							'Apportion the cost between the stock and shares part
							myProportion = myShares / (myShares + myValue)
							myStockCost = myProportion * myCost
							myCashCost = myCost - myStockCost

							'Cost is reduced by the cash part 
							myReduction = myCashCost
						End If
					End If
					
					'If the reduction is greater than the costs
					If (myReduction > myCost) Then
						'Reduce the cost to zero
						myReduction = myCost
					End If
						
					'If this is a Taxable Gain
					If (myDebAcct.isLifeBond) Then
						'Add the amount and tax credit
						myTaxGainInfo.catValue = myTaxGainInfo.catValue + myValue
						myTaxGainInfo.catValue = myTaxGainInfo.catValue - myReduction
						myTaxGainInfo.catTaxCredit = myTaxGainInfo.catTaxCredit + myTaxCred
				
						'Add the tax credit
						myTaxInfo.catValue = myTaxInfo.catValue + myTaxCred
						
						'Add the tax credit to the Gains
						myDebAcct.acctGains = myDebAcct.acctGains + myTaxCred
					End If
					
					'Add to gains (allowing for cost reduction)
					myDebAcct.acctGains = myDebAcct.acctGains + myValue
					myDebAcct.acctGains = myDebAcct.acctGains - myReduction

					'Remove from cost
					myDebAcct.acctCost = myDebAcct.acctCost - myReduction
				End If

			'Else this is an income
			Else
				'If this is income from a portfolio
				If (myDebAcct.isPortfolio) Then
					'Switch to the parent account
					myDebAcct = myDebAcct.acctParent
				End If
				
				'If this is a recovered transaction
				If Not (myCatInfo.isIncome) Then
					'Subtract from expense and reverse sign
					myDebAcct.acctExpense = myDebAcct.acctExpense - myValue
					myValue = -myValue
					
				'Else standard transaction
				Else
					'Add to income		
					myDebAcct.acctIncome = myDebAcct.acctIncome + myValue + myTaxCred + myEeNatIns + myErNatIns + myWithheld
				End If		
			End If
			
			'If there is a TaxCredit
			If (myTaxCred > 0) Then
                If (myCatInfo.isLoanPay) Then
                    'Adjust taxman expense
                    myTaxAcct.acctExpense = myTaxAcct.acctExpense - myTaxCred
                Else
				    'Adjust taxman expense
                    myTaxAcct.acctExpense = myTaxAcct.acctExpense + myTaxCred
                End If 
			End If
			
			'If this is not a transfer or taxable gain
			If Not (myCatInfo.isTransfer) Then				
				'Add the amount
				myCatInfo.catValue = myCatInfo.catValue + myValue
				
				'Handle TaxCredit
				If (myTaxCred <> 0) Then
				    myCatInfo.catTaxCredit = myCatInfo.catTaxCredit + myTaxCred
                    If (myCatInfo.isLoanPay) Then
    				    myTaxReliefInfo.catValue = myTaxReliefInfo.catValue - myTaxCred
   				    Else 
    				    myTaxInfo.catValue = myTaxInfo.catValue + myTaxCred
   				    End If
   				End If

				'Handle Benefit/Withheld
				If (myBenefit <> 0) Or (myWithheld <> 0) Then
  				    myCatInfo.catWithheld = myCatInfo.catWithheld + myWithheld
				    myBenInfo.catValue = myBenInfo.catValue + myBenefit
				    myWithInfo.catValue = myWithInfo.catValue + myBenefit + myWithheld
				End If
				
				'Handle National insurance
				If (myEeNatIns <> 0) Or (myErNatIns <> 0) Then
  				    myNIAcct = myCredAcct
   				    If Not(myNIAcct.isPension) Then
				        myNIAcct = myPensionAcct
    				    myErNatInsInfo.catValue = myErNatInsInfo.catValue + myErNatIns				
    				    myEeNatInsInfo.catValue = myEeNatInsInfo.catValue + myEeNatIns				
    				Else
    				    myCatInfo.catValue = myCatInfo.catValue + myErNatIns + myEeNatIns				    				
				    End If
  				    myNIAcct.isActive = True()
				    myNIAcct.acctInvestment = myNIAcct.acctInvestment + myEeNatIns + myErNatIns
				    myNIAcct.acctCost = myNIAcct.acctCost + myEeNatIns + myErNatIns
                    If (myNIAcct.acctUnits = 0) Then 
       		            myNIAcct.acctUnits = myNIAcct.acctType.numAutoUnits
                    End If 								
                End If 
			End If

        'If we have a Stock Demerger
        ElseIf (myCatInfo.isStockDemerger) Then
			'Access the Existing Cost
			myCost = myDebAcct.acctCost
			
			'Calculate the transfer cost information
			myCost = myDebAcct.acctCost * myDilution
			myTransfer = myDebAcct.acctCost - myCost
			
			'Adjust the costs
			myCredAcct.acctCost = myCredAcct.acctCost + myTransfer
			myDebAcct.acctCost = myDebAcct.acctCost - myTransfer
			
			'Adjust the investments
			myCredAcct.acctInvestment = myCredAcct.acctInvestment + myTransfer
			myDebAcct.acctInvestment = myDebAcct.acctInvestment - myTransfer
		End If

        'If we have a Stock Takeover
        If (myCatInfo.isStockTakeover) Then
			'Access the Existing Cost and any cash element of takeover
			myCost = myDebAcct.acctCost
			myCash = myEvent.evtThirdPartyValue

			'Access the value of shares used in the takeover on this date
			myShares = getAssetValueForDate(Context, myCredAcct, myDate)						
			
			'Apportion the cost between the stock and shares part
			myProportion = myShares / (myShares + myCash)
			myStockCost = myProportion * myCost
			myCashCost = myCost - myStockCost

			'Calculate the gains
			myGains = myCash - myCashCost
						
			'Adjust the costs and gains
			myCredAcct.acctCost = myCredAcct.acctCost + myStockCost
			myDebAcct.acctGains = myDebAcct.acctGains + myGains
			myDebAcct.acctInvestment = myDebAcct.acctInvestment - myCash
			myDebAcct.acctCost = 0
			If (myCash <> 0) Then
			    myCredAcct = myEvent.acctThirdParty
			    myCredAcct.acctValue = myCredAcct.acctValue + myCash
			End If
        End If
        
        'Store details
        myLastEvent = myEvent
    Next    
End Sub

'Value the priced assets
Sub valuePricedAssets(ByRef Context As FinanceState, _
					  ByRef Year As String)
	Dim myDoc As Object
	Dim myAcct As Object
	Dim myAccount As Object
	Dim myMarket As Object
	Dim myGrowth As Object
	Dim myRow As Integer
	Dim myCol As Integer
	Dim myValue As Double
	Dim myDate As Date
	Dim myPrice As Double
	Dim myDilution As Double
	
    'Determine the row index to use
	myDate = getDateForYear(Year)
	myRow  = getSpotPriceIndexForDate(Context, myDate)

	'Access the market transactions
	myMarket = getCachedAccount(Context, acctMarket)
	
	'Access the market categories
	myGrowth   = getCategoryStats(Context, catMktGrowth)
	myCapGains = getCategoryStats(Context, catCapitalGain)
	
	'Loop through the Accounts 
	myIterator = hashIterator(Context.mapAccounts)
	While (hashHasNext(myIterator))
		'Access the element
		myAccount = hashNext(myIterator)
		
		'If the element has Units 
	    If (myAccount.hasUnits) And	(myAccount.isActive) Then
		    'Determine the column index to use
    		myCol = getSpotPriceIndexForAcct(Context, myAccount)
    		 
    		'Access the price
			myPrice = getBaseSpotPrice(Context, myCol, myRow)
				
    		'If we did not find a price
    		If (myPrice = 0) And (myAccount.acctUnits > 0) Then
    			msgBox myAccount.strAccount & " price was not found in Prices Sheet"
    		End If

 			'If the units is nearly zero
			If ((myAccount.acctUnits < 0.005) And (myAccount.acctUnits > -0.005)) Then
				'Set it to truly zero
				myAccount.acctUnits = 0
			End If
			
			'Store price and calculate value
			myAccount.acctPrice = myPrice
			myAccount.acctValue = myAccount.acctPrice * myAccount.acctUnits
			
 	        'If the asset is in a foreign currency
	        If (myAccount.isForeign) Then
  	            myAccount.acctValue = getAdjustedValueForDateAndCurrency(Context, myAccount, myAccount.acctValue, myDate)
        	End If
	
			'If the value is nearly zero
			If ((myAccount.acctValue < 0.005) And (myAccount.acctValue > -0.005)) Then
				'Set it to truly zero
				myAccount.acctValue = 0
			End If
			
 			'If the cost is nearly zero
			If ((myAccount.acctCost < 0.005) And (myAccount.acctCost > -0.005)) Then
				'Set it to truly zero
				myAccount.acctCost = 0
			End If
			
 			'If the gains is nearly zero
			If ((myAccount.acctGains < 0.005) And (myAccount.acctGains > -0.005)) Then
				'Set it to truly zero
				myAccount.acctGains = 0
			End If
			
			'Calculate the profit
			myAccount.acctProfit = myAccount.acctValue - myAccount.acctCost
			     		 
 			'If the profit is nearly zero
			If ((myAccount.acctProfit < 0.005) And (myAccount.acctProfit > -0.005)) Then
				'Set it to truly zero
				myAccount.acctProfit = 0
			End If
			
	   		'Calculate market income/expense and add it to totals
    		myValue = myAccount.acctValue
    		myValue = myValue - myAccount.acctLastValue
    		myValue = myValue - myAccount.acctInvestment
    		
    		'If we have gains on the account
    		If Not (myAccount.acctGains = 0) Then
    			'If the account is subject to capital gains
    			If myAccount.isCapital Then
	    			'Subtract gains from market movement
    				myValue = myValue - myAccount.acctGains
    		
    				'If the gains are positive
    				If (myAccount.acctGains > 0) Then
    					'Add to capital Gains
    					myCapGains.catValue = myCapGains.catValue + myAccount.acctGains
						myMarket.acctIncome = myMarket.acctIncome + myAccount.acctGains
    				Else
    					'Add to capital Gains
    					myCapGains.catValue = myCapGains.catValue + myAccount.acctGains	
						myMarket.acctExpense = myMarket.acctExpense - myAccount.acctGains
					End If

    			'Else If the account is subject to taxable gains
    			ElseIf myAccount.isLifeBond Then
	    			'Subtract gains from market movement
    				myValue = myValue - myAccount.acctGains
    		
    				'If the gains are positive
    				If (myAccount.acctGains > 0) Then
    					'Add to income
						myMarket.acctIncome = myMarket.acctIncome + myAccount.acctGains
					End If
    			End If	
    		End If
    		
   			'Adjust realised gains
   			myAccount.acctRealisedGains = myAccount.acctRealisedGains + myAccount.acctGains

			'Record market movement    			
    		myAccount.acctMarket = myValue
    		    		
    		'myValue = myValue - myAccount.acctDividends
			If (myValue > 0) Then
				myMarket.acctIncome = myMarket.acctIncome + myValue
				myGrowth.catValue = myGrowth.catValue + myValue
			Else 
				myMarket.acctExpense = myMarket.acctExpense - myValue
				myGrowth.catValue = myGrowth.catValue + myValue
			End If
		
		'Else check for rounding errors on zero values
		ElseIf (myAccount.hasValue) Then
			'If the account is nearly zero
			If ((myAccount.acctValue < 0.005) And (myAccount.acctValue > -0.005)) Then
				'Set it to truly zero
				myAccount.acctValue = 0
			End If

		'Else check for negative expenses
		ElseIf Not ((myAccount.hasUnits) Or (myAccount.hasValue)) Then
			'If the expense is negative
			If (myAccount.acctExpense < 0) Then
				'Switch it to be an income
				myAccount.acctIncome = myAccount.acctIncome - myAccount.acctExpense
				myAccount.acctExpense = 0
			End If
		End If 
	Wend
End Sub

'Calculate and report on totals for data
Sub TotalIt()
	Dim myDoc As Object
	
	'Determine the number of accounts (add one for OpeningBalance)
	myContext = allocateState()
	myDoc = myContext.docBook
		
	'Access the unit years range
	myRange = myDoc.NamedRanges.getByName(rangeAssetsYears).getReferredCells()
	
	'Switch off autocalc for the duration
	Dim isAutoCalc As Boolean
	isAutoCalc = True() 'myDoc.isAutomaticCalculationEnabled()
	If isAutoCalc Then
		myDoc.enableAutomaticCalculation(False())
	End If

    'PreProcess reports
    preProcessReports(myContext)
    	
	'Loop through the years
	For myIndex = myRange.getColumns().Count To 1 Step -1
		'Access the year
		myCell = myRange.getCellByPosition(myIndex-1, 0)
		myYear = myCell.getString()
	
		'Set the Cell background color to red
		myCell.CellBackColor = colorRed

		'Analyse the Year
		analyseYear(myContext, myYear)
		valuePricedAssets(myContext, myYear)
		
		'Set the Cell background color to green
		myCell.CellBackColor = colorGreen
			
		'Report on the Year
		reportUnitsYear(myContext, myYear)
		reportAssetsYear(myContext, myYear)
		reportIncomeYear(myContext, myYear)
		reportExpenseYear(myContext, myYear)
		reportCategoryYear(myContext, myYear)
		resetYear(myContext)
		
		'Set the Cell background color to white
		myCell.CellBackColor = colorWhite
	Next
	
    'PostProcess reports
    postProcessReports(myContext)
    	
	'Reswitch on AutoCalc
	If isAutoCalc Then
		myDoc.calculateAll()
		myDoc.enableAutomaticCalculation(True())
	End If
End Sub 
 
'Reset counters after processing a years data
Sub resetYear(ByRef Context As FinanceState)
	Dim myAccount As Object
	Dim myCategory As Object
	Dim myIterator As Object
		
	'Reset the cache
	resetHashMap(Context.mapCache)
	
	'Loop through the Accounts 
	myIterator = hashIterator(Context.mapAccounts)
	While (hashHasNext(myIterator))
		'Access the element
		myAccount = hashNext(myIterator)

		'Reset counters
		resetAccount(myAccount)
		
		'If the account has value
		If (myAccount.acctValue <> 0) Or (myAccount.acctUnits <> 0) Then
			'Access account in cache
			Set x = getCachedAccount(Context, myAccount.strAccount)
		End If
	Wend

	'Loop through the Categories 
	myIterator = hashIterator(Context.mapCategories)
	While (hashHasNext(myIterator))
		'Access the element
		myCategory = hashNext(myIterator)
		
		'Reset counters
		resetCategory(myCategory)
	Wend
End Sub
