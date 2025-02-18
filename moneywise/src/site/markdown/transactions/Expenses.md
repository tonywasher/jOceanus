# Simple Expense transactions

Simple expenses can be made from any valued account to/from any payee

Suppose we start with the following deposit/payee accounts and transactions

<details open="true" name="accounts">
<summary>Deposit Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Currency</th><th class="defHdr">Starting Balance</th></tr>
<tr><td>BarclaysCurrent</td><td>GBP</td><td>£10,000.00</td></tr>
<tr><td>StarlingEuros</td><td>EUR</td><td>€5,000.00</td></tr>
</table>
</details>
<details name="accounts">
<summary>Payee Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>ASDA</td><td>Payee</td></tr>
<tr><td>Market</td><td>Market</td></tr>
</table>
</details>
<details name="accounts">
<summary>Transaction Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Market:CurrencyFluctuation</td><td>CurrencyFluctuation</td></tr>
<tr><td>Shopping:Food</td><td>Expense</td></tr>
</table>
</details><br>

Then we have the following transactions

<details open="true">
<summary>Transactions</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Account</th><th class="defHdr">Category</th><th class="defHdr">Amount</th>
<th class="defHdr">Direction</th><th class="defHdr">Partner</th></tr>
<tr><td>01-Jun-86</td><td>BarclaysCurrent</td><td>Shopping:Food</td><td>£21.95</td><td>To</td><td>ASDA</td></tr>
<tr><td>02-Jun-86</td><td>BarclaysCurrent</td><td>Shopping:Food</td><td>£9.99</td><td>From</td><td>ASDA</td></tr>
<tr><td>03-Jun-86</td><td>StarlingEuros</td><td>Shopping:Food</td><td>€31.20</td><td>To</td><td>ASDA</td></tr>
<tr><td>04-Jun-86</td><td>StarlingEuros</td><td>Shopping:Food</td><td>€5.12</td><td>From</td><td>ASDA</td></tr>
</table>
</details>
<br>

and the following exchange Rates

<details open="true">
<summary>Exchange Rates</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">EUR</th><th class="defHdr">USD</th></tr>
<tr><td>06-Apr-80</td><td>0.90</td><td>0.80</td></tr>
<tr><td>01-Jun-10</td><td>0.95</td><td>0.85</td></tr>
</table>
</details>
<br>

The analysis of these transactions is as follows

<details open="true" name="analysis">
<summary>Assets</summary>
<table class="defTable">
<tr><th class="defHdr" rowspan="2">Date</th><th class="defHdr" rowspan="2">BarclaysCurrent</th>
<th class="defHdr" colspan="2">StarlingEuros</th><th class="defHdr" rowspan="2">Total</th></tr>
<tr><th class="defHdr">EUR</th><th class="defHdr">GBP</th></tr>
<tr><td>06-Apr-80</td><td>£10,000.00</td><td>€5,000.00</td><td>£4,500.00</td><td>£14,500.00</td></tr>
<tr><td>01-Jun-86</td><td>£9,978.05</td><td>€5,000.00</td><td>£4,500.00</td><td>£14,478.05</td></tr>
<tr><td>02-Jun-86</td><td>£9,988.04</td><td>€5,000.00</td><td>£4,500.00</td><td>£14,488.04</td></tr>
<tr><td>03-Jun-86</td><td>£9,988.04</td><td>€4,968.80</td><td>£4,471.92</td><td>£14,459.96</td></tr>
<tr><td>04-Jun-86</td><td>£9,988.04</td><td>€4,973.92</td><td>£4,476.53</td><td>£14,464.57</td></tr>
<tr><td>01-Jun-10</td><td>£9,988.04</td><td>€4,973.92</td><td>£4,725.22</td><td>£14,713.26</td></tr>
<tr><td colspan="4">Profit</td><th>£213.26</th></tr>
</table>
</details>

<details name="analysis">
<summary>Payees</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">ASDA</th><th class="defHdr">Market</th></tr>
<tr><td>06-Apr-80</td><td/><td/></tr>
<tr><td>01-Jun-86</td><td>-£21.95</td><td/></tr>
<tr><td>02-Jun-86</td><td>-£11.96</td><td/></tr>
<tr><td>03-Jun-86</td><td>-£40.04</td><td/></tr>
<tr><td>04-Jun-86</td><td>-£35.43</td><td/></tr>
<tr><td>01-Jun-10</td><td>-£35.43</td><td>£248.69</td></tr>
<tr><td>Profit</td><th colspan="2">£213.26</th></tr>
</table>
</details>

<details name="analysis">
<summary>Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Shopping:Food</th><th class="defHdr">Market:CurrencyFluctuation</th></tr>
<tr><td>06-Apr-80</td><td/></tr>
<tr><td>01-Jun-86</td><td>-£21.95</td><td/></tr>
<tr><td>02-Jun-86</td><td>-£11.96</td><td/></tr>
<tr><td>03-Jun-86</td><td>-£40.04</td><td/></tr>
<tr><td>04-Jun-86</td><td>-£35.43</td><td/></tr>
<tr><td>01-Jun-10</td><td>-£35.43</td><td>£248.69</td></tr>
<tr><td>Profit</td><th colspan="2">£213.26</th></tr>
</table>
</details>

<details name="analysis">
<summary>TaxationBasis</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Expense</th><th class="defHdr">Market</th></tr>
<tr><td>06-Apr-80</td><td/><td/></tr>
<tr><td>01-Jun-86</td><td>-£21.95</td><td/></tr>
<tr><td>02-Jun-86</td><td>-£11.96</td><td/></tr>
<tr><td>03-Jun-86</td><td>-£40.04</td><td/></tr>
<tr><td>04-Jun-86</td><td>-£35.43</td><td/></tr>
<tr><td>01-Jun-10</td><td>-£35.43</td><td>£248.69</td></tr>
<tr><td>Profit</td><th colspan="2">£213.26</th></tr>
</table>
</details>
