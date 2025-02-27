# Simple Transfer transactions

Simple transfers can be made between any valued account. ie between any of **Cash**, **Deposits**, **Loans** or **Portfolios**  

Suppose we start with the following deposit accounts 

<details open="true" name="accounts">
<summary>Deposit Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Parent</th><th class="defHdr">Category</th><th class="defHdr">Currency</th><th class="defHdr">Starting Balance</th></tr>
<tr><td>BarclaysCurrent</td><td>Barclays</td><td>Checking</td><td>GBP</td><td>£10,000.00</td></tr>
<tr><td>NatWideFlexDirect</td><td>NationWide</td><td>Checking</td><td>GBP</td><td>£10,000.00</td></tr>
<tr><td>StarlingEuros</td><td>Starling</td><td>Checking</td><td>EUR</td><td>€5,000.00</td></tr>
<tr><td>StarlingDollars</td><td>Starling</td><td>Checking</td><td>USD</td><td>$5,000.00</td></tr>
</table>
</details>
<details name="accounts">
<summary>Payee Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Barclays</td><td>Institution</td></tr>
<tr><td>NationWide</td><td>Institution</td></tr>
<tr><td>Starling</td><td>Institution</td></tr>
<tr><td>Market</td><td>Market</td></tr>
</table>
</details>
<details name="accounts">
<summary>Transaction Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Market:CurrencyFluctuation</td><td>CurrencyFluctuation</td></tr>
</table>
</details>
<br>

Then we have the following transactions

<details open="true">
<summary>Transactions</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Account</th><th class="defHdr">Category</th><th class="defHdr">Amount</th>
<th class="defHdr">Direction</th><th class="defHdr">Partner</th><th class="defHdr">PartnerAmount</th></tr>
<tr><td>01-Jun-85</td><td>BarclaysCurrent</td><td>Transfer</td><td>£2,000.00</td><td>To</td><td>NatWideFlexDirect</td><td/></tr>
<tr><td>02-Jun-85</td><td>BarclaysCurrent</td><td>Transfer</td><td>£2,000.00</td><td>To</td><td>StarlingEuro</td><td>€2,100.00</td></tr>
<tr><td>03-Jun-85</td><td>StarlingEuros</td><td>Transfer</td><td>€1,000.00</td><td>To</td><td>BarclaysCurrent</td><td>£950.00</td></tr>
<tr><td>04-Jun-85</td><td>StarlingEuros</td><td>Transfer</td><td>€500.00</td><td>To</td><td>StarlingDollars</td><td>$550.00</td></tr>
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
<th class="defHdr" rowspan="2">NatWideFlexDirect</th><th class="defHdr" colspan="2">StarlingEuros</th>
<th class="defHdr" colspan="2">StarlingDollars</th><th class="defHdr" rowspan="2">Total</th></tr>
<tr><th class="defHdr">EUR</th><th class="defHdr">GBP</th><th class="defHdr">USD</th><th class="defHdr">GBP</th></tr>
<tr><td>06-Apr-80</td><td>£10,000.00</td><td>£10,000.00</td><td>€5,000.00</td><td>£4,500.00</td><td>$5,000.00</td><td>£4,000.00</td><td>£28,500.00</td></tr>
<tr><td>01-Jun-85</td><td>£8,000.00</td><td>£12,000.00</td><td>€5,000.00</td><td>£4,500.00</td><td>$5,000.00</td><td>£4,000.00</td><td>£28,500.00</td></tr>
<tr><td>02-Jun-85</td><td>£6,000.00</td><td>£12,000.00</td><td>€7,100.00</td><td>£6,390.00</td><td>$5,000.00</td><td>£4,000.00</td><td>£28,390.00</td></tr>
<tr><td>03-Jun-85</td><td>£6,950.00</td><td>£12,000.00</td><td>€6,100.00</td><td>£5,490.00</td><td>$5,000.00</td><td>£4,000.00</td><td>£28,440.00</td></tr>
<tr><td>04-Jun-85</td><td>£6,950.00</td><td>£12,000.00</td><td>€5,600.00</td><td>£5,040.00</td><td>$5,550.00</td><td>£4,440.00</td><td>£28,430.00</td></tr>
<tr><td>01-Jun-10</td><td>£6,950.00</td><td>£12,000.00</td><td>€5,600.00</td><td>£5,320.00</td><td>$5,550.00</td><td>£4,717.50</td><td>£28,987.50</td></tr>
<tr><td colspan="7">Profit</td><th>£487.50</th></tr>
</table>
</details>

<details name="analysis">
<summary>Payees</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Market</th></tr>
<tr><td>06-Apr-80</td><td/></tr>
<tr><td>01-Jun-85</td><td/></tr>
<tr><td>02-Jun-85</td><td>-£110.00</td></tr>
<tr><td>03-Jun-85</td><td>-60.00</td></tr>
<tr><td>04-Jun-85</td><td>-70.00</td></tr>
<tr><td>01-Jun-10</td><td>£487.50</td></tr>
<tr><td>Profit</td><th>£487.50</th></tr>
</table>
</details>

<details name="analysis">
<summary>Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Market:CurrencyFluctuation</th></tr>
<tr><td>06-Apr-80</td><td/></tr>
<tr><td>01-Jun-85</td><td/></tr>
<tr><td>02-Jun-85</td><td>-£110.00</td></tr>
<tr><td>03-Jun-85</td><td>-60.00</td></tr>
<tr><td>04-Jun-85</td><td>-70.00</td></tr>
<tr><td>01-Jun-10</td><td>£487.50</td></tr>
<tr><td>Profit</td><th>£487.50</th></tr>
</table>
</details>

<details name="analysis">
<summary>TaxationBasis</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Market</th></tr>
<tr><td>06-Apr-80</td><td/></tr>
<tr><td>01-Jun-85</td><td/></tr>
<tr><td>02-Jun-85</td><td>-£110.00</td></tr>
<tr><td>03-Jun-85</td><td>-£60.00</td></tr>
<tr><td>04-Jun-85</td><td>-£70.00</td></tr>
<tr><td>01-Jun-10</td><td>£487.50</td></tr>
<tr><td>Profit</td><th>£487.50</th></tr>
</table>
</details>
