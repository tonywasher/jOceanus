# Deposit Income transactions

Income from Deposits transactions can be made as follows

Suppose we start with the following deposit/payee accounts and transaction categories

<details open="true" name="accounts">
<summary>Deposit Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Parent</th><th class="defHdr">Category</th><th class="defHdr">Currency</th><th class="defHdr">Starting Balance</th></tr>
<tr><td>BarclaysCurrent</td><td>Barclays</td><td>Checking</td><td>GBP</td><td>£10,000.00</td></tr>
<tr><td>NatWideISA</td><td>NationWide</td><td>TaxFree</td><td>GBP</td><td>£10,000.00</td></tr>
</table>
</details>
<details name="accounts">
<summary>Payee Accounts</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Barclays</td><td>Institution</td></tr>
<tr><td>NationWide</td><td>Institution</td></tr>
<tr><td>HMRC</td><td>TaxMan</td></tr>
</table>
</details>
<details name="accounts">
<summary>Transaction Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Name</th><th class="defHdr">Type</th></tr>
<tr><td>Income:Interest</td><td>Interest</td></tr>
<tr><td>Income:TaxedInterest</td><td>TaxedInterest</td></tr>
<tr><td>Income:GrossInterest</td><td>GrossInterest</td></tr>
<tr><td>Income:TaxFreeInterest</td><td>TaxFreeInterest</td></tr>
<tr><td>Income:LoyaltyBonus</td><td>LoyaltyBonus</td></tr>
<tr><td>Income:TaxedLoyaltyBonus</td><td>TaxedLoyaltyBonus</td></tr>
<tr><td>Income:GrossLoyaltyBonus</td><td>GrossLoyaltyBonus</td></tr>
<tr><td>Income:TaxFreeLoyaltyBonus</td><td>TaxFreeLoyaltyBonus</td></tr>
<tr><td>Income:CashBack</td><td>CashBack</td></tr>
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
<th class="defHdr">TaxCredit</th><th class="defHdr">Withheld</th></tr>
<tr><td>01-Sep-86</td><td>BarclaysCurrent</td><td>Income:Interest</td><td>£13.00</td><td>To</td><td>BarclaysCurrent</td><td>£2.81</td><td>£0.77</td></tr>
<tr><td>02-Sep-86</td><td>BarclaysCurrent</td><td>Income:Interest</td><td>£3.22</td><td>From</td><td>BarclaysCurrent</td><td>£0.76</td><td>£0.03</td></tr>
<tr><td>03-Sep-86</td><td>BarclaysCurrent</td><td>Income:LoyaltyBonus</td><td>£9.80</td><td>To</td><td>BarclaysCurrent</td><td>£2.30</td><td/></tr>
<tr><td>04-Sep-86</td><td>BarclaysCurrent</td><td>Income:LoyaltyBonus</td><td>£0.76</td><td>From</td><td>BarclaysCurrent</td><td>£0.34</td><td/></tr>
<tr><td>05-Sep-86</td><td>BarclaysCurrent</td><td>Income:CashBack</td><td>£1.00</td><td>To</td><td>BarclaysCurrent</td><td/><td/></tr>
<tr><td>06-Sep-86</td><td>BarclaysCurrent</td><td>Income:CashBack</td><td>£0.10</td><td>From</td><td>BarclaysCurrent</td><td/><td/></tr>
<tr><td>07-Sep-86</td><td>NatWideISA</td><td>Income:Interest</td><td>£56.00</td><td>To</td><td>NatWideISA</td><td/><td/></tr>
<tr><td>08-Sep-86</td><td>NatWideISA</td><td>Income:Interest</td><td>£8.70</td><td>From</td><td>NatWideISA</td><td/><td/></tr>
<tr><td>09-Sep-86</td><td>NatWideISA</td><td>Income:LoyaltyBonus</td><td>£14.56</td><td>To</td><td>NatWideISA</td><td/><td/></tr>
<tr><td>10-Sep-86</td><td>NatWideISA</td><td>Income:LoyaltyBonus</td><td>£7.98</td><td>From</td><td>NatWideISA</td><td/><td/></tr>
<tr><td>01-Sep-20</td><td>BarclaysCurrent</td><td>Income:Interest</td><td>£7.34</td><td>To</td><td>BarclaysCurrent</td><td/><td/></tr>
<tr><td>02-Sep-20</td><td>BarclaysCurrent</td><td>Income:Interest</td><td>£2.98</td><td>From</td><td>BarclaysCurrent</td><td/><td/></tr>
<tr><td>03-Sep-20</td><td>BarclaysCurrent</td><td>Income:LoyaltyBonus</td><td>£8.45</td><td>To</td><td>BarclaysCurrent</td><td/><td/></tr>
<tr><td>04-Sep-20</td><td>BarclaysCurrent</td><td>Income:LoyaltyBonus</td><td>£1.45</td><td>From</td><td>BarclaysCurrent</td><td/><td/></tr>
</table>
</details>
<br>

The analysis of these transactions is as follows

<details open="true" name="analysis">
<summary>Assets</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">BarclaysCurrent</th><th class="defHdr">NatWideISA</th></tr>
<tr><td>06-Apr-80</td><td>£10,000.00</td><td>£10,000.00</td></tr>
<tr><td>01-Sep-86</td><td>£10,013.00</td><td>£10,000.00</td></tr>
<tr><td>02-Sep-86</td><td>£10,009.78</td><td>£10,000.00</td></tr>
<tr><td>03-Sep-86</td><td>£10,019.58</td><td>£10,000.00</td></tr>
<tr><td>04-Sep-86</td><td>£10,018.82</td><td>£10,000.00</td></tr>
<tr><td>05-Sep-86</td><td>£10,019.82</td><td>£10,000.00</td></tr>
<tr><td>06-Sep-86</td><td>£10,019.72</td><td>£10,000.00</td></tr>
<tr><td>07-Sep-86</td><td>£10,019.72</td><td>£10,056.00</td></tr>
<tr><td>08-Sep-86</td><td>£10,019.72</td><td>£10,047.30</td></tr>
<tr><td>09-Sep-86</td><td>£10,019.72</td><td>£10,061.86</td></tr>
<tr><td>10-Sep-86</td><td>£10,019.72</td><td>£10,053.88</td></tr>
<tr><td>01-Sep-20</td><td>£10,027.06</td><td>£10,053.88</td></tr>
<tr><td>02-Sep-20</td><td>£10,024.08</td><td>£10,053.88</td></tr>
<tr><td>03-Sep-20</td><td>£10,032.53</td><td>£10,053.88</td></tr>
<tr><td>04-Sep-20</td><td>£10,031.08</td><td>£10,053.88</td></tr>
<tr><td>Profit</td><th colspan="2">£84.96</th></tr>
</table>
</details>

<details name="analysis">
<summary>Payees</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Barclays</th><th class="defHdr">NationWide</th><th class="defHdr">HMRC</th></tr>
<tr><td>01-Sep-86</td><td>£15.81</td><td/><td>-£2.81</td></tr>
<tr><td>02-Sep-86</td><td>£11.83</td><td/><td>-£2.05</td></tr>
<tr><td>03-Sep-86</td><td>£23.93</td><td/><td>-£4.35</td></tr>
<tr><td>04-Sep-86</td><td>£23.83</td><td/><td>-£4.01</td></tr>
<tr><td>05-Sep-86</td><td>£24.83</td><td/><td>-£4.01</td></tr>
<tr><td>06-Sep-86</td><td>£24.73</td><td/><td>-£4.01</td></tr>
<tr><td>07-Sep-86</td><td>£24.73</td><td>£56.00</td></td><td>-£4.01</td></tr>
<tr><td>08-Sep-86</td><td>£24.73</td><td>£47.30</td><td>-£4.01</td></tr>
<tr><td>09-Sep-86</td><td>£24.73</td><td>£61.86</td></td><td>-£4.01</td></tr>
<tr><td>10-Sep-86</td><td>£24.73</td><td>£53.88</td><td>-£4.01</td></tr>
<tr><td>01-Sep-20</td><td>£31.07</td><td>£53.88</td><td>-£4.01</td></tr>
<tr><td>02-Sep-20</td><td>£28.09</td><td>£53.88</td><td>-£4.01</td></tr>
<tr><td>03-Sep-20</td><td>£36.54</td><td>£53.88</td><td>-£4.01</td></tr>
<tr><td>04-Sep-20</td><td>£35.09</td><td>£53.88</td><td>-£4.01</td></tr>
<tr><td>Profit</td><th colspan="3">£84.96</th></tr>
</table>
</details>

<details name="analysis">
<summary>Categories</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">Income:<br/>TaxedInterest</th><th class="defHdr">Income:<br/>GrossInterest</th>
<th class="defHdr">Income:<br/>TaxFreeInterest</th><th class="defHdr">Income:<br/>TaxedLoyaltyBonus</th>
<th class="defHdr">Income:<br/>GrossLoyaltyBonus</th><th class="defHdr">Income:<br/>TaxFreeLoyaltyBonus</th><th class="defHdr">Income:<br/>CashBack</th>
<th class="defHdr">Taxes:<br/>IncomeTax</th><th class="defHdr">Expenses:<br/>Virtual</th></tr>
<tr><td>01-Sep-86</td><td>£16.58</td><td/><td/><td/><td/><td/><td/><td>-£2.81</td><td>-£0.77</td></tr>
<tr><td>02-Sep-86</td><td>£12.57</td><td/><td/><td/><td/><td/><td/><td>-£2.05</td><td>-£0.74</td></tr>
<tr><td>03-Sep-86</td><td>£12.57</td><td/><td/><td>£12.10</td><td/><td/><td/><td>-£4.35</td><td>-£0.74</td></tr>
<tr><td>04-Sep-86</td><td>£12.57</td><td/><td/><td>£11.00</td><td/><td/><td/><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>05-Sep-86</td><td>£12.57</td><td/><td/><td>£11.00</td><td/><td/><td>£1.00</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>06-Sep-86</td><td>£12.57</td><td/><td/><td>£11.00</td><td/><td/><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>07-Sep-86</td><td>£12.57</td><td/><td>£56.00</td><td>£11.00</td><td/><td/><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>08-Sep-86</td><td>£12.57</td><td/><td>£47.30</td><td>£11.00</td><td/><td/><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>09-Sep-86</td><td>£12.57</td><td/><td>£47.30</td><td>£11.00</td><td/><td>£14.56</td><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>10-Sep-86</td><td>£12.57</td><td/><td>£47.30</td><td>£11.00</td><td/><td>£6.58</td><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>01-Sep-20</td><td>£12.57</td><td/><td>£47.30</td><td>£11.00</td><td/><td>£6.58</td><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>02-Sep-20</td><td>£12.57</td><td/><td>£47.30</td><td>£11.00</td><td/><td>£6.58</td><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>03-Sep-20</td><td>£12.57</td><td>£7.34</td><td>£47.30</td><td>£11.00</td><td>£8.45</td><td>£6.58</td><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>04-Sep-20</td><td>£12.57</td><td>£4.36</td><td>£47.30</td><td>£11.00</td><td>£7.00</td><td>£6.58</td><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>Profit</td><th colspan="9">£84.96</th></tr>
</table>
</details>

<details name="analysis">
<summary>TaxationBasis</summary>
<table class="defTable">
<tr><th class="defHdr">Date</th><th class="defHdr">TaxedInterest</th><th class="defHdr">TaxedInterest</th><th class="defHdr">TaxFree</th>
<th class="defHdr">TaxPaid</th><th class="defHdr">Virtual</th></tr>
<tr><td>01-Sep-86</td><td>£16.58</td><td/><td/><td>-£2.81</td><td>-£0.77</td></tr>
<tr><td>02-Sep-86</td><td>£12.57</td><td/><td/><td>-£2.05</td><td>-£0.74</td></tr>
<tr><td>03-Sep-86</td><td>£24.67</td><td/><td/><td>-£4.35</td><td>-£0.74</td></tr>
<tr><td>04-Sep-86</td><td>£23.57</td><td/><td/><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>05-Sep-86</td><td>£23.57</td><td/><td>£1.00</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>06-Sep-86</td><td>£23.57</td><td/><td>£0.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>07-Sep-86</td><td>£23.57</td><td/><td>£56.90</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>08-Sep-86</td><td>£23.57</td><td/><td>£48.20</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>09-Sep-86</td><td>£23.57</td><td/><td>£62.76</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>10-Sep-86</td><td>£23.57</td><td/><td>£54.78</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>01-Sep-20</td><td>£23.57</td><td>£7.34</td><td>£54.78</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>02-Sep-20</td><td>£23.57</td><td>£4.36</td><td>£54.78</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>03-Sep-20</td><td>£23.57</td><td>£12.81</td><td>£54.78</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>04-Sep-20</td><td>£23.57</td><td>£11.36</td><td>£54.78</td><td>-£4.01</td><td>-£0.74</td></tr>
<tr><td>Profit</td><th colspan="5">£84.96</th></tr>
</table>
</details>
