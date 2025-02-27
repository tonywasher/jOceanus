# Portfolios
**Portfoliois** are accounts where **holdings** of various **securities** are held. 
The portfolio also holds a number of **deposit** accounts, representing each currency necessary to support the holdings in the portfolio.  

The value of a **portfolio** at any particular time is the sum of the various **holdings** and cash accounts held within the portfolio.

Each **portfolio** has a **parent** institution, so for example a Barclays share portfolio would have Barclays as a parent.
Each cash account is viewed as a **deposit** with the same parent.

Each **portfolio** account belongs to a **portfolioType** that controls reporting of the account and can also restrict which transactions
can be performed against an account.

The various categories are as follows
<table class="defTable">
<tr><th class="defHdr">Category</th><th class="defHdr">Description</th></tr>
<tr><td>Standard</td><td>A standard portfolio</td></tr>
<tr><td>TaxFree</td><td>A taxFree portfolio</td></tr>
<tr><td>SIPP</td><td>A SIPP portfolio</td></tr>
<tr><td>Pensions</td><td>A singular placeholder portfolio intended to hold StatePension, DefinedBenefit and DefinedContribution pensions</td></tr>
</table>
