# MoneyWise Project

MoneyWise is a subComponent of the jOceanus project.

MoneyWise is a Personal Finance project that provides a single view of personal finances. Data is held on all assets
and transactions relating to those assets. Reports are provided including point in time valuations, income/expense over a period and taxation calculations.
All taxation is UK tax-based.

Where multiple currencies are in use, one of the currencies (normally the local currency) is viewed as the reporting currency. 
Each account balance is maintained in the account currency, and automatically calculated in the reporting currency according to the exchange rate 
for that day. When transfers are made between accounts of different currencies, it is unlikely that the exchange rate used for the transaction matches
the daily exchange rate, and any discrepancy is viewed as a currencyFluctuation. This is viewed as an income/expense from/to the special payee 
**Market**

Mechanisms are provided to export to **MoneyDance**, and it is intended to provide the functionality to also import from **MoneyDance**.
Other personal finance applications such as **Quicken** and **GnuCash** may work, but are seriously lacking in support for multiple currencies. 
Import and export to these applications should only be attempted for single currency portfolios. 

The following account types are provided

1. [Payees](accounts/Payees.html)
2. [Securities](accounts/Securities.html)
3. [Deposits](accounts/Deposits.html)
4. [Cash](accounts/Cash.html)
5. [Loans](accounts/Loans.html)
6. [Portfolios](accounts/Portfolios.html)

Basic Transactions

1. [Transfers](transactions/Transfers.html)
2. [Expenses](transactions/Expenses.html)
3. [Cash](transactions/Cash.html)
4. [PayeeIncome](transactions/PayeeIncome.html)
5. [DepositIncome](transactions/DepositIncome.html)
6. [CreditCard](transactions/CreditCard.html)
7. [Mortgage](transactions/Mortgage.html)
8. [PrivateLoan](transactions/PrivateLoan.html)

Investment Transactions

1.  [ShareBuySell](transactions/ShareBuySell.html)
