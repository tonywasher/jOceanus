# Payee Income transactions

Income from Payees can be made as follows

Suppose we start with the following deposit/payee accounts and transaction categories

<details open="true" name="accounts">
<summary>Deposit Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Parent</th><th class="defHdr">Category</th><th class="defHdr">Currency</th><th class="defHdr">Starting Balance</th></tr>
<tr><td>BarclaysCurrent</td><td>Barclays</td><td>Checking</td></td><td>GBP</td><td>£10,000.00</td></tr>
</table>
</details>
<details name="accounts">
<summary>Payee Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Barclays</td><td>Institution</td></tr>
<tr><td>IBM</td><td>Institution</td></tr>
<tr><td>Parents</td><td>Individual</td></tr>
<tr><td>Government</td><td>Government</td></tr>
<tr><td>HMRC</td><td>TaxMan</td></tr>
</table>
</details>
<details name="accounts">
<summary>Transaction Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Income:Inheritance</td><td>Inheritance</td></tr>
<tr><td>Income:Salary</td><td>TaxedIncome</td></tr>
<tr><td>Income:SocialSecurity</td><td>GrossIncome</td></tr>
<tr><td>Income:Gifts</td><td>GiftedIncome</td></tr>
<tr><td>Income:Benefit</td><td>VirtualIncome</td></tr>
<tr><td>Taxes:IncomeTax</td><td>IncomeTax</td></tr>
<tr><td>Expenses:Virtual</td><td>Withheld</td></tr>
</table>
</details>
<br>

Then we have the following transactions

<details open="true">
<summary>Transactions</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Account</th><th class="defHdr">Category</th><th class="defHdr">Amount</th>
<th class="defHdr">Direction</th><th class="defHdr">Partner</th>
<th class="defHdr">TaxCredit</th><th class="defHdr">Benefit</th></tr>
<tr><td>01-Aug-86</td><td>BarclaysCurrent</td><td>Income:Inheritance</td><td>£500.00</td><td>From</td><td>Parents</td><td/><td/></tr>
<tr><td>02-Aug-86</td><td>BarclaysCurrent</td><td>Income:Inheritance</td><td>£60.00</td><td>To</td><td>Parents</td><td/><td/></tr>
<tr><td>03-Aug-86</td><td>BarclaysCurrent</td><td>Income:Salary</td><td>£1,000.00</td><td>From</td><td>IBM</td><td>£20.00</td><td>£10.00</td></tr>
<tr><td>04-Aug-86</td><td>BarclaysCurrent</td><td>Income:Salary</td><td>£100.00</td><td>To</td><td>IBM</td><td>£0.90</td><td>£0.50</td></tr>
<tr><td>05-Aug-86</td><td>BarclaysCurrent</td><td>Income:SocialSecurity</td><td>£100.00</td><td>From</td><td>Government</td><td/><td/></tr>
<tr><td>06-Aug-86</td><td>BarclaysCurrent</td><td>Income:SocialSecurity</td><td>£10.00</td><td>To</td><td>Government</td><td/><td/></tr>
<tr><td>07-Aug-86</td><td>BarclaysCurrent</td><td>Income:Gifts</td><td>£50.00</td><td>From</td><td>Parents</td><td/><td/></tr>
<tr><td>08-Aug-86</td><td>BarclaysCurrent</td><td>Income:Gifts</td><td>£5.00</td><td>To</td><td>Parents</td><td/><td/></tr>
</table>
</details>
<br>

The analysis of these transactions is as follows

<details open="true" name="analysis">
<summary>Assets</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">BarclaysCurrent</th></tr>
<tr><td>06-Apr-80</td><td>£10,000.00</td></tr>
<tr><td>01-Aug-86</td><td>£10,500.00</td></tr>
<tr><td>02-Aug-86</td><td>£10,440.00</td></tr>
<tr><td>03-Aug-86</td><td>£11,440.00</td></tr>
<tr><td>04-Aug-86</td><td>£11,340.00</td></tr>
<tr><td>05-Aug-86</td><td>£11,440.00</td></tr>
<tr><td>06-Aug-86</td><td>£11,430.00</td></tr>
<tr><td>07-Aug-86</td><td>£11,480.00</td></tr>
<tr><td>08-Aug-86</td><td>£11,475.00</td></tr>
<tr><td>Profit</td><th>£1,475.00</th></tr>
</table>
</details>

<details name="analysis">
<summary>Payees</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Parents</th><th class="defHdr">IBM</th>
<th class="defHdr">Government</th><th class="defHdr">HMRC</th></tr>
<tr><td>01-Aug-86</td><td>£500.00</td><td/><td/><td/></tr>
<tr><td>02-Aug-86</td><td>£440.00</td><td/><td/><td/></tr>
<tr><td>03-Aug-86</td><td>£440.00</td><td>£1,020.00</td><td/><td>-£20.00</td></tr>
<tr><td>04-Aug-86</td><td>£440.00</td><td>£919.10</td><td/><td>-£19.10</td></tr>
<tr><td>05-Aug-86</td><td>£440.00</td><td>£919.10</td><td>£100.00</td><td>-£19.10</td></tr>
<tr><td>06-Aug-86</td><td>£440.00</td><td>£919.10</td><td>£90.00</td><td>-£19.10</td></tr>
<tr><td>07-Aug-86</td><td>£490.00</td><td>£919.10</td><td>£90.00</td><td>-£19.10</td></tr>
<tr><td>08-Aug-86</td><td>£485.00</td><td>£919.10</td><td>£90.00</td><td>-£19.10</td></tr>
<tr><td>Profit</td><th colspan="4">£1.475.00</th></tr>
</table>
</details>

<details name="analysis">
<summary>Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Income:<br/>Inheritance</th><th class="defHdr">Income:<br/>Gifts</th>
<th class="defHdr">Income:<br/>Salary</th><th class="defHdr">Income:<br/>Benefit</th>
<th class="defHdr">Income:<br/>SocialSecurity</th><th class="defHdr">Taxes:<br/>IncomeTax</th><th class="defHdr">Expenses:<br/>Virtual</th></tr>
<tr><td>01-Aug-86</td><td>£500.00</td><td/><td/><td/><td/><td/><td/></tr>
<tr><td>02-Aug-86</td><td>£440.00</td><td/><td/><td/><td/><td/><td/></tr>
<tr><td>03-Aug-86</td><td>£440.00</td><td/><td>£1020.00</td><td>£10.00</td><td/><td>-£20.00</td><td>-£10.00</td></tr>
<tr><td>04-Aug-86</td><td>£440.00</td><td/><td>£919.10</td><td>£9.50</td><td/><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>05-Aug-86</td><td>£440.00</td><td/><td>£919.10</td><td>£9.50</td><td>£100.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>06-Aug-86</td><td>£440.00</td><td/><td>£919.10</td><td>£9.50</td><td>£90.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>07-Aug-86</td><td>£440.00</td><td>£50.00</td><td>£919.10</td><td>£9.50</td><td>£90.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>08-Aug-86</td><td>£440.00</td><td>£45.00</td><td>£919.10</td><td>£9.50</td><td>£90.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>Profit</td><th colspan="7">£1,475.00</th></tr>
</table>
</details>

<details name="analysis">
<summary>TaxationBasis</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Salary</th><th class="defHdr">TaxFree</th>
<th class="defHdr">TaxPaid</th><th class="defHdr">Virtual</th></tr>
<tr><td>01-Aug-86</td><td/><td>£500.00</td><td/><td/></tr>
<tr><td>02-Aug-86</td><td/><td>£440.00</td><td/><td/></tr>
<tr><td>03-Aug-86</td><td>£1,030.00</td><td>£440.00</td><td>-£20.00</td><td>-£10.00</td></tr>
<tr><td>04-Aug-86</td><td>£928.60</td><td>£440.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>05-Aug-86</td><td>£1,028.60</td><td>£440.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>06-Aug-86</td><td>£1,018.60</td><td>£440.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>07-Aug-86</td><td>£1,018.60</td><td>£490.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>08-Aug-86</td><td>£1,018.60</td><td>£485.00</td><td>-£19.10</td><td>-£9.50</td></tr>
<tr><td>Profit</td><th colspan="4">£1,475.00</th></tr>
</table>
</details>
