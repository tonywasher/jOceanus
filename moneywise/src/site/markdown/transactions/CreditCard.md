# CreditCard transactions

CreditCard transactions can be made as follows

Suppose we start with the following deposit/loan/payee accounts and transaction categories

<details open="true" name="accounts">
<summary>Deposit Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Parent</th><th class="defHdr">Category</th><th class="defHdr">Currency</th><th class="defHdr">Starting Balance</th></tr>
<tr><td>BarclaysCurrent</td><td>Barclays</td><td>Checking</td><td>GBP</td><td>£10,000.00</td></tr>
</table>
</details>
<details name="accounts">
<summary>Loan Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Parent</th><th class="defHdr">Category</th><th class="defHdr">Currency</th><th class="defHdr">Starting Balance</th></tr>
<tr><td>Barclaycard</td><td>Barclays</td><td>Creditcard</td><td>GBP</td><td>-£100.00</td></tr>
</table>
</details>
<details name="accounts">
<summary>Payee Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Barclays</td><td>Institution</td></tr>
<tr><td>ASDA</td><td>Payee</td></tr>
</table>
</details>
<details name="accounts">
<summary>Transaction Categories</summary>
<table class="defTable">
<tr><td>Loan:InterestCharged</td><td>LoanInterestCharged</td></tr>
<tr><td>Income:CashBack</td><td>CashBack</td></tr>
<tr><td>Charges:Fines</td><td>Expense</td></tr>
<tr><td>Shopping:Food</td><td>Expense</td></tr>
</table>
</details>
<br>

Then we have the following transactions

<details open="true">
<summary>Transactions</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Account</th><th class="defHdr">Category</th><th class="defHdr">Amount</th>
<th class="defHdr">Direction</th><th class="defHdr">Partner</th></tr>
<tr><td>01-Jan-86</td><td>Barclaycard</td><td>Shopping:Food</td><td>£76.56</td><td>To</td><td>ASDA</td></tr>
<tr><td>02-Jan-86</td><td>Barclaycard</td><td>Shopping:Food</td><td>£5.23</td><td>From</td><td>ASDA</td></tr>
<tr><td>03-Jan-86</td><td>Barclaycard</td><td>Charges:Fines</td><td>£6.89</td><td>To</td><td>Barclays</td></tr>
<tr><td>04-Jan-86</td><td>Barclaycard</td><td>Charges:Fines</td><td>£0.13</td><td>From</td><td>Barclays</td></tr>
<tr><td>05-Jan-86</td><td>BarclaysCurrent</td><td>Transfer</td><td>£50.00</td><td>To</td><td>Barclaycard</td></tr>
<tr><td>06-Jan-86</td><td>Barclaycard</td><td>Loan:InterestCharged</td><td>£25.67</td><td>From</td><td>Barclaycard</td></tr>
<tr><td>07-Jan-86</td><td>Barclaycard</td><td>Loan:InterestCharged</td><td>£1.56</td><td>To</td><td>Barclaycard</td></tr>
<tr><td>08-Jan-86</td><td>NatWideISA</td><td>Income:CashBack</td><td>£10.00</td><td>To</td><td>Barclaycard</td></tr>
</table>
</details>
<br>

The analysis of these transactions is as follows

<details open="true" name="analysis">
<summary>Assets</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">BarclaysCurrent</th><th class="defHdr">Barclaycard</th></tr>
<tr><td>06-Apr-80</td><td>£10,000.00</td><td>-£100.00</td></tr>
<tr><td>01-Jan-86</td><td>£10,000.00</td><td>-£176.56</td></tr>
<tr><td>02-Jan-86</td><td>£10,000.00</td><td>-£171.33</td></tr>
<tr><td>03-Jan-86</td><td>£10,000.00</td><td>-£178.22</td></tr>
<tr><td>04-Jan-86</td><td>£10,000.00</td><td>-£178.09</td></tr>
<tr><td>05-Jan-86</td><td>£9,950.00</td><td>-£128.09</td></tr>
<tr><td>06-Jan-86</td><td>£9,950.00</td><td>-£153.76</td></tr>
<tr><td>07-Jan-86</td><td>£9,950.00</td><td>-£152.20</td></tr>
<tr><td>08-Jan-86</td><td>£9,950.00</td><td>-£142.20</td></tr>
<tr><td>Profit</td><th colspan="2">-£92.20</th></tr>
</table>
</details>

<details name="analysis">
<summary>Payees</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Barclays</th><th class="defHdr">ASDA</th></tr>
<tr><td>01-Jan-86</td><td/><td>-£76.56</td></tr>
<tr><td>02-Jan-86</td><td/><td>-£71.33</td></tr>
<tr><td>03-Jan-86</td><td>-£6.89</td><td>-£71.33</td></tr>
<tr><td>04-Jan-86</td><td>-£6.76</td><td>-£71.33</td></tr>
<tr><td>05-Jan-86</td><td>-£6.76</td><td>-£71.33</td></tr>
<tr><td>06-Jan-86</td><td>-£32.43</td><td>-£71.33</td></tr>
<tr><td>07-Jan-86</td><td>-£30.87</td><td>-£71.33</td></tr>
<tr><td>08-Jan-86</td><td>-£20.87</td><td>-£71.33</td></tr>
<tr><td>Profit</td><th colspan="2">-£92.20</th></tr>
</table>
</details>

<details name="analysis">
<summary>Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Loan:<br/>InterestCharged</th><th class="defHdr">Income:<br/>CashBack</th>
<th class="defHdr">Charges:<br/>Fines</th><th class="defHdr">Shopping:<br/>Food</th></tr>
<tr><td>01-Jan-86</td><td/><td/><td/><td>-£76.56</td></tr>
<tr><td>02-Jan-86</td><td/><td/><td/><td>-£71.33</td></tr>
<tr><td>03-Jan-86</td><td/><td/><td>-£6.89</td><td>-£71.33</td></tr>
<tr><td>04-Jan-86</td><td/><td/><td>-£6.76</td><td>-£71.33</td></tr>
<tr><td>05-Jan-86</td><td/><td/><td>-£6.76</td><td>-£71.33</td></tr>
<tr><td>06-Jan-86</td><td>-£25.67</td><td/><td>-£6.76</td><td>-£71.33</td></tr>
<tr><td>07-Jan-86</td><td>-£24.11</td><td/><td>-£6.76</td><td>-£71.33</td></tr>
<tr><td>08-Jan-86</td><td>-£24.11</td><td>£10.00</td><td>-£6.76</td><td>-£71.33</td></tr>
<tr><td>Profit</td><th colspan="4">-£92.20</th></tr>
</table>
</details>

<details name="analysis">
<summary>TaxationBasis</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Expense</th><th class="defHdr">TaxFree</th></tr>
<tr><td>01-Jan-86</td><td>-£76.56</td><td/></tr>
<tr><td>02-Jan-86</td><td>-£71.33</td><td/></tr>
<tr><td>03-Jan-86</td><td>-£78.22</td><td/></tr>
<tr><td>04-Jan-86</td><td>-£78.09</td><td/></tr>
<tr><td>05-Jan-86</td><td>-£78.09</td><td/></tr>
<tr><td>06-Jan-86</td><td>-£103.76</td><td/></tr>
<tr><td>07-Jan-86</td><td>-£102.20</td><td/></tr>
<tr><td>08-Jan-86</td><td>-£102.20</td><td>£10.00</td></tr>
<tr><td>Profit</td><th colspan="2">-£92.20</th></tr>
</table>
</details>
