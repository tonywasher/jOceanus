# Securities
**Securities** are items such as stocks and shares, houses and cars. These items do not comprise accounts in their own right,
but need to be held as a **Holding** of a number of **units** inside a portfolio to have a value.


The value of a **holding** is the **SecurityPrice** multiplied by the number of units in the holding. Various transactions may affect the number
of **units** in the holding, and obviously price changes and where appropriate exchange rate changes will affect the value of the holding. 

The price of the **security** is defined in a specified currency. If this is different from the reporting currency,
then the value of any **holding** is reported in both the local and reporting currency.

Some types of **holding** may generate deposit payments, which can be re-invested in the holding or else paid out to the portfolio cash account
or other deposit account.

Each **security** has a **parent** institution, so for example a Barclays share would have Barclays as a parent.
For reporting purposes, the dividend is deemed to be income from the **parent** of the security.

Each **security** account belongs to a **securityType** that controls reporting of holdings of the security and can also restrict which transactions
can be performed against a security.

The various tytpes of security are as follows
<table class="defTable">
<tr><th class="defHdr">SecurityType</th><th class="defHdr">Description</th></tr>
<tr><td>Shares</td><td>A standard stock or share</td></tr>
<tr><td>GrowthUnitTrust</td><td>A unit trust where underlying income is re-invested in the trust, and is reflected either
in additional units or as an enhanced price</td></tr>
<tr><td>LifeBond</td><td>An asset which experiences specific tax treatment</td></tr>
<tr><td>Endowment</td><td>An endowment policy</td></tr>
<tr><td>Property</td><td>A property such as a house or flat</td></tr>
<tr><td>Vehicle</td><td>A car or van etc.</td></tr>
<tr><td>DefinedContribution</td><td>A defined contribution pension</td></tr>
<tr><td>DefinedBenefit</td><td>A defined benefit pension</td></tr>
<tr><td>StatePension</td><td>The state pension</td></tr>
<tr><td>StockOption</td><td>A stock option</td></tr>
<tr><td>Asset</td><td>A generic asset</td></tr>
</table>
