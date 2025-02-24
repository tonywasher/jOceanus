# Cash and AutoCash transactions

Cash accounts can be used as normal accounts or as autoCash

Suppose we start with the following deposit/payee accounts and transactions

<details open="true" name="accounts">
<summary>Deposit Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Currency</th><th class="defHdr">Starting Balance</th></tr>
<tr><td>BarclaysCurrent</td><td>GBP</td><td>£10,000.00</td></tr>
<tr><td>StarlingEuros</td><td>EUR</td><td>€5,000.00</td></tr>
</table>
</details>
<details name="Accounts">
<summary>Cash Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Currency</th><th class="defHdr">Starting Balance</th>
<th class="defHdr">AutoPayee</th><th class="defHdr">autoExpense</th></tr>
<tr><td>Cash</td><td>GBP</td><td/><td>CashExpense</td><td>Expenses:Cash</td></tr>
<tr><td>Euros</td><td>EUR</td><td>CashExpense</td><td>Expenses:Cash</td></tr>
<tr><td>CashWallet</td><td>GBP</td><td>£10.00</td></td><td/><td/></tr>
<tr><td>EuroWallet</td><td>EUR</td><td/><td/></tr>
</table>
</details>
<details name="accounts">
<summary>Payee Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>ASDA</td><td>Payee</td></tr>
<tr><td>CashExpense</td><td>Payee</td></tr>
<tr><td>Market</td><td>Market</td></tr>
</table>
</details>
<details name="accounts">
<summary>Transaction Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Market:CurrencyFluctuation</td><td>CurrencyFluctuation</td></tr>
<tr><td>Shopping:Food</td><td>Expense</td></tr>
<tr><td>Expenses:Cash</td><td>Expense</td></tr>
</table>
</details>
<br>

Then we have the following transactions

<details open="true">
<summary>Transactions</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Account</th><th class="defHdr">Category</th><th class="defHdr">Amount</th>
<th class="defHdr">Direction</th><th class="defHdr">Partner</th></tr>
<tr><td>01-Jul-86</td><td>BarclaysCurrent</td><td>Transfer</td><td>£50.00</td><td>To</td><td>Cash</td></tr>
<tr><td>02-Jul-86</td><td>BarclaysCurrent</td><td>Transfer</td><td>£20.00</td><td>From</td><td>Cash</td></tr>
<tr><td>03-Jul-86</td><td>Cash</td><td>Shopping:Food</td><td>£12.00</td><td>To</td><td>ASDA</td></tr>
<tr><td>04-Jul-86</td><td>Cash</td><td>Shopping:Food</td><td>£6.00</td><td>From</td><td>ASDA</td></tr>
<tr><td>05-Jul-86</td><td>BarclaysCurrent</td><td>Transfer</td><td>£22.00</td><td>To</td><td>CashWallet</td></tr>
<tr><td>06-Jul-86</td><td>BarclaysCurrent</td><td>Transfer</td><td>£7.00</td><td>From</td><td>CashWallet</td></tr>
<tr><td>07-Jul-86</td><td>StarlingEuros</td><td>Transfer</td><td>€65.00</td><td>To</td><td>Euros</td></tr>
<tr><td>08-Jul-86</td><td>StarlingEuros</td><td>Transfer</td><td>€14.00</td><td>From</td><td>Euros</td></tr>
<tr><td>09-Jul-86</td><td>Euros</td><td>Shopping:Food</td><td>£17.00</td><td>To</td><td>ASDA</td></tr>
<tr><td>10-Jul-86</td><td>Euros</td><td>Shopping:Food</td><td>£8.00</td><td>From</td><td>ASDA</td></tr>
<tr><td>11-Jul-86</td><td>StarlingEuros</td><td>Transfer</td><td>€13.00</td><td>To</td><td>EuroWallet</td></tr>
<tr><td>12-Jul-86</td><td>StarlingEuros</td><td>Transfer</td><td>€8.50</td><td>From</td><td>EuroWallet</td></tr>
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
<tr><th class="defHdr" rowspan="2">Date</th><th class="defHdr" rowspan="2">BarclaysCurrent</th><th class="defHdr" rowspan="2">CashWallet</th>
<th class="defHdr" colspan="2">StarlingEuros</th><th class="defHdr" colspan="2">EuroWallet</th><th class="defHdr" rowspan="2">Total</th></tr>
<tr><th class="defHdr">EUR</th><th class="defHdr">GBP</th><th class="defHdr">EUR</th><th class="defHdr">GBP</th></tr>
<tr><td>06-Apr-80</td><td>£10,000.00</td><td>£10.00</td><td>€5,000.00</td><td>£4,500.00</td><td/><td/><td>£14,510.00</td></tr>
<tr><td>01-Jul-86</td><td>£9,950.00</td><td>£10.00</td><td>€5,000.00</td><td>£4,500.00</td><td/><td/><td>£14,460.00</td></tr>
<tr><td>02-Jul-86</td><td>£9,970.00</td><td>£10.00</td><td>€5,000.00</td><td>£4,500.00</td><td/><td/><td>£14,480.00</td></tr>
<tr><td>03-Jul-86</td><td>£9,970.00</td><td>£10.00</td><td>€5,000.00</td><td>£4,500.00</td><td/><td/><td>£14,480.00</td></tr>
<tr><td>04-Jul-86</td><td>£9,970.00</td><td>£10.00</td><td>€5,000.00</td><td>£4,500.00</td><td/><td/><td>£14,480.00</td></tr>
<tr><td>05-Jul-86</td><td>£9,955.00</td><td>£32.00</td><td>€5,000.00</td><td>£4,500.00</td><td/><td/><td>£14,480.00</td></tr>
<tr><td>06-Jul-86</td><td>£9,955.00</td><td>£25.00</td><td>€5,000.00</td><td>£4,500.00</td><td/><td/><td>£14,480.00</td></tr>
<tr><td>07-Jul-86</td><td>£9,955.00</td><td>£25.00</td><td>€4,935.00</td><td>£4,441.50</td><td/><td/><td>£14,421.50</td></tr>
<tr><td>08-Jul-86</td><td>£9,955.00</td><td>£25.00</td><td>€4,949.00</td><td>£4,454.10</td><td/><td/><td>£14,434.10</td></tr>
<tr><td>09-Jul-86</td><td>£9,955.00</td><td>£25.00</td><td>€4,949.00</td><td>£4,454.10</td><td/><td/><td>£14,434.10</td></tr>
<tr><td>10-Jul-86</td><td>£9,955.00</td><td>£25.00</td><td>€4,949.00</td><td>£4,454.10</td><td/><td/><td>£14,434.10</td></tr>
<tr><td>11-Jul-86</td><td>£9,955.00</td><td>£32.00</td><td>€4,936.00</td><td>£4,442.40</td><td>€13.00</td><td>£11.70</td><td>£14,434.10</td></tr>
<tr><td>12-Jul-86</td><td>£9,955.00</td><td>£25.00</td><td>€4,944.50</td><td>£4,450.05</td><td>€4.50</td><td>£4.05</td><td>£14,434.10</td></tr>
<tr><td>01-Jun-10</td><td>£9,955.00</td><td>£25.00</td><td>€4,944.50</td><td>£4,697.28</td><td>€4.50</td><td>£4.28</td><td>£14,681.56</td></tr>
<tr><td colspan="7">Profit</td><th>£171.56</th></tr>
</table>
</details>

<details name="analysis">
<summary>Payees</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">ASDA</th><th class="defHdr">CashExpense</th><th class="defHdr">Market</th></tr>
<tr><td>06-Apr-80</td><td/><td/><td/></tr>
<tr><td>01-Jul-86</td><td/><td>-£50.00</td><td/></tr>
<tr><td>02-Jul-86</td><td/><td>-£30.00</td><td/></tr>
<tr><td>03-Jul-86</td><td>-£12.00</td><td>-£18.00</td><td/></tr>
<tr><td>04-Jul-86</td><td>-£6.00</td><td>-£24.00</td><td/></tr>
<tr><td>05-Jul-86</td><td>-£6.00</td><td>-£24.00</td><td/></tr>
<tr><td>06-Jul-86</td><td>-£6.00</td><td>-£24.00</td><td/></tr>
<tr><td>07-Jul-86</td><td>-£6.00</td><td>-£82.50</td><td/></tr>
<tr><td>08-Jul-86</td><td>-£6.00</td><td>-£69.90</td><td/></tr>
<tr><td>09-Jul-86</td><td>-£21.30</td><td>-£54.60</td><td/></tr>
<tr><td>10-Jul-86</td><td>-£14.10</td><td>-£61.80</td><td/></tr>
<tr><td>11-Jul-86</td><td>-£14.10</td><td>-£61.80</td><td/></tr>
<tr><td>12-Jul-86</td><td>-£14.10</td><td>-£61.80</td><td/></tr>
<tr><td>01-Jun-10</td><td>-£14.10</td><td>-£61.80</td><td>£247.46</td></tr>
<tr><td>Profit</td><th colspan="3">£171.56</th></tr>
</table>
</details>

<details name="analysis">
<summary>Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Shopping:Food</th><th class="defHdr">Expenses:Cash</th>
<th class="defHdr">Market:CurrencyFluctuation</th></tr>
<tr><td>06-Apr-80</td><td/><td/><td/></tr>
<tr><td>01-Jul-86</td><td/><td>-£50.00</td><td/></tr>
<tr><td>02-Jul-86</td><td/><td>-£30.00</td><td/></tr>
<tr><td>03-Jul-86</td><td>-£12.00</td><td>-£18.00</td><td/></tr>
<tr><td>04-Jul-86</td><td>-£6.00</td><td>-£24.00</td><td/></tr>
<tr><td>05-Jul-86</td><td>-£6.00</td><td>-£24.00</td><td/></tr>
<tr><td>06-Jul-86</td><td>-£6.00</td><td>-£24.00</td><td/></tr>
<tr><td>07-Jul-86</td><td>-£6.00</td><td>-£82.50</td><td/></tr>
<tr><td>08-Jul-86</td><td>-£6.00</td><td>-£69.90</td><td/></tr>
<tr><td>09-Jul-86</td><td>-£21.30</td><td>-£54.60</td><td/></tr>
<tr><td>10-Jul-86</td><td>-£14.10</td><td>-£61.80</td><td/></tr>
<tr><td>11-Jul-86</td><td>-£14.10</td><td>-£61.80</td><td/></tr>
<tr><td>12-Jul-86</td><td>-£14.10</td><td>-£61.80</td><td/></tr>
<tr><td>01-Jun-10</td><td>-£14.10</td><td>-£61.80</td><td>£247.46</td></tr>
<tr><td>Profit</td><th colspan="3">£171.56</th></tr>
</table>
</details>

<details name="analysis">
<summary>TaxationBasis</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Expense</th><th class="defHdr">Market</th></tr>
<tr><td>06-Apr-80</td><td/><td/></tr>
<tr><td>01-Jul-86</td><td>-£50.00</td><td/></tr>
<tr><td>02-Jul-86</td><td>-£30.00</td><td/></tr>
<tr><td>03-Jul-86</td><td>-£30.00</td><td/></tr>
<tr><td>04-Jul-86</td><td>-£30.00</td><td/></tr>
<tr><td>05-Jul-86</td><td>-£30.00</td><td/></tr>
<tr><td>06-Jul-86</td><td>-£30.00</td><td/></tr>
<tr><td>07-Jul-86</td><td>-£88.50</td><td/></tr>
<tr><td>08-Jul-86</td><td>-£75.90</td><td/></tr>
<tr><td>09-Jul-86</td><td>-£75.90</td><td/></tr>
<tr><td>10-Jul-86</td><td>-£75.90</td><td/></tr>
<tr><td>11-Jul-86</td><td>-£75.90</td><td/></tr>
<tr><td>12-Jul-86</td><td>-£75.90</td><td/></tr>
<tr><td>01-Jun-10</td><td>-£75.90</td><td>£247.46</td></tr>
<tr><td>Profit</td><th colspan="2">£171.56</th></tr>
</table>
</details>
