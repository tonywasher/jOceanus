# Cash
**Cash** accounts represent Cash in your wallet/purse.

The value of a **cash** account at any particular time is the sum of credits to the account minus the sum of debits from the account, up to that time.
Money in the **cash** account is held in a specified currency. If this is different from the reporting currency, then the value of the **cash** account 
is reported both the local and reporting currency.

For those who do not wish to micromanage their cash usage, a second type of **cash** account is provided called **AutoCash**

When money is transferred to an **AutoCash** account it is immediately viewed as being an expense to a named expense category to a named payee.
The payee and category are specified in the definition of the **AutoCash** account

It is also possible to pay expenses from the **cash** account, representing times when a cash payment was made that is worth recording.
In this case the money is simply transferred from the configured cash expense category to the new category.

**cash** accounts do not have a **parent** institution.

Each **cash** account belongs to a **cash category** that controls reporting of the account and can also restrict which transactions
can be performed against an account. The category also determines whether the account is **AutoCash** or not.
