# Loans
**Loans** are accounts where monies are lent to or by you, such as CreditCards and Mortgages,
and are viewed as monetary assets. Each **loan** has a **parent** institution or individual,
so for example a Barclays loan would have Barclays as a parent. 

The value of a **Loan** at any particular time is the sum of credits to the account minus the sum of debits from the account, up to that time.
Money in the **loan** is held in a specified currency. If this is different from the reporting currency, then the value of the **loan** is reported
in both the local and reporting currency.

A **loan** may generate (or charge) interest, which can be credited to/debited from itself or transferred to/from another account. 

For reporting purposes, the interest is deemed to be income from (or expense to) the **parent** of the loan.

Each **loan** account belongs to a **loan category** that controls reporting of the account and can also restrict which transactions
can be performed against an account.

The various categories are as follows
<table class="defTable">
<tr><th class="defHdr">Category</th><th class="defHdr">Description</th></tr>
<tr><td>CreditCard</td><td>This is a standard creditCard current</td></tr>
<tr><td>PersonalLoan</td><td>This is a loan to/from an individual</td></tr>
<tr><td>Loan</td><td>This is a standard loan, such as a mortgage or commercial loan</td></tr>
</table>