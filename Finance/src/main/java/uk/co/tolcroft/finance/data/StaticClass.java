/*******************************************************************************
 * JFinanceApp: Finance Application
 * Copyright 2012 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package uk.co.tolcroft.finance.data;

import net.sourceforge.JDataManager.JDataException;
import net.sourceforge.JDataManager.JDataException.ExceptionClass;
import uk.co.tolcroft.models.data.StaticData.StaticInterface;

/**
 * Static classes.
 * @author Tony Washer
 */
public class StaticClass {
    /**
     * Enumeration of Account Type Classes.
     */
    public enum AccountClass implements StaticInterface {
        /**
         * Current Banking Account.
         */
        CURRENT(1, 0),

        /**
         * Instant Access Savings Account.
         */
        INSTANT(2, 1),

        /**
         * Savings Account Requiring Notice for Withdrawals.
         */
        NOTICE(3, 2),

        /**
         * Fixed Rate Savings Bond.
         */
        BOND(4, 3),

        /**
         * Instant Access Cash ISA Account.
         */
        CASHISA(5, 4),

        /**
         * Fixed Rate Cash ISA Bond.
         */
        ISABOND(6, 5),

        /**
         * Index Linked Bond.
         */
        TAXFREEBOND(7, 6),

        /**
         * Equity Bond.
         */
        EQUITYBOND(8, 7),

        /**
         * Shares.
         */
        SHARES(9, 8),

        /**
         * Unit Trust or OEIC.
         */
        UNITTRUST(10, 9),

        /**
         * Life Bond.
         */
        LIFEBOND(11, 10),

        /**
         * Unit Trust or OEIC in ISA wrapper.
         */
        UNITISA(12, 11),

        /**
         * Car.
         */
        CAR(13, 12),

        /**
         * House.
         */
        HOUSE(14, 13),

        /**
         * Debts.
         */
        DEBTS(15, 16),

        /**
         * CreditCard.
         */
        CREDITCARD(16, 15),

        /**
         * WriteOff.
         */
        WRITEOFF(17, 22),

        /**
         * External Account.
         */
        EXTERNAL(18, 24),

        /**
         * Employer Account.
         */
        EMPLOYER(19, 18),

        /**
         * Asset Owner Account.
         */
        OWNER(20, 25),

        /**
         * Market.
         */
        MARKET(21, 26),

        /**
         * Inland Revenue.
         */
        TAXMAN(22, 20),

        /**
         * Cash.
         */
        CASH(23, 19),

        /**
         * Inheritance.
         */
        INHERITANCE(24, 21),

        /**
         * Endowment.
         */
        ENDOWMENT(25, 14),

        /**
         * Benefit.
         */
        BENEFIT(26, 23),

        /**
         * Deferred between tax years.
         */
        DEFERRED(27, 17);

        /**
         * Class Id.
         */
        private final int theId;

        /**
         * Class Order.
         */
        private final int theOrder;

        /**
         * Obtain Class Id.
         * @return the class id
         */
        @Override
        public int getClassId() {
            return theId;
        }

        /**
         * Obtain Class Order.
         * @return the class order
         */
        @Override
        public int getOrder() {
            return theOrder;
        }

        /**
         * Constructor.
         * @param uId the Id
         * @param uOrder the default order.
         */
        private AccountClass(final int uId,
                             final int uOrder) {
            theId = uId;
            theOrder = uOrder;
        }

        /**
         * get value from id.
         * @param id the id value
         * @return the corresponding enum object
         * @throws JDataException on error
         */
        public static AccountClass fromId(final int id) throws JDataException {
            for (AccountClass myClass : values()) {
                if (myClass.getClassId() == id) {
                    return myClass;
                }
            }
            throw new JDataException(ExceptionClass.DATA, "Invalid Frequency Class Id: " + id);
        }
    }

    /**
     * Enumeration of Transaction Type Classes.
     */
    public enum TransClass implements StaticInterface {
        /**
         * Taxed Salary Income.
         */
        TAXEDINCOME(1, 0),

        /**
         * Interest Income.
         */
        INTEREST(2, 1),

        /**
         * Dividend Income.
         */
        DIVIDEND(3, 2),

        /**
         * Unit Trust Dividend Income.
         */
        UNITTRUSTDIVIDEND(4, 33),

        /**
         * Taxable Gain.
         */
        TAXABLEGAIN(5, 18),

        /**
         * Capital Gain.
         */
        CAPITALGAIN(6, 36),

        /**
         * Capital Loss.
         */
        CAPITALLOSS(7, 37),

        /**
         * Tax Free Interest.
         */
        TAXFREEINTEREST(8, 34),

        /**
         * Tax Free Dividend.
         */
        TAXFREEDIVIDEND(9, 35),

        /**
         * Tax Free Income.
         */
        TAXFREEINCOME(10, 3),

        /**
         * Benefit.
         */
        BENEFIT(11, 7),

        /**
         * Inheritance.
         */
        INHERITED(12, 4),

        /**
         * Market Growth.
         */
        MARKETGROWTH(13, 31),

        /**
         * Market Shrinkage.
         */
        MARKETSHRINK(14, 32),

        /**
         * Expense.
         */
        EXPENSE(15, 22),

        /**
         * Recovered Expense.
         */
        RECOVERED(16, 9),

        /**
         * Transfer.
         */
        TRANSFER(17, 19),

        /**
         * Admin charge.
         */
        ADMINCHARGE(18, 12),

        /**
         * Stock Split.
         */
        STOCKSPLIT(19, 13),

        /**
         * Stock Demerger.
         */
        STOCKDEMERGER(20, 11),

        /**
         * Stock Rights Taken.
         */
        STOCKRIGHTTAKEN(21, 14),

        /**
         * Stock Rights Waived.
         */
        STOCKRIGHTWAIVED(22, 15),

        /**
         * CashTakeover (For the cash part of a stock and cash takeover).
         */
        CASHTAKEOVER(23, 16),

        /**
         * Stock Takeover (for the stock part of a stock and cash takeover).
         */
        STOCKTAKEOVER(24, 17),

        /**
         * Expense Recovered directly to Cash.
         */
        CASHRECOVERY(25, 20),

        /**
         * Expense paid directly from Cash.
         */
        CASHPAYMENT(26, 21),

        /**
         * Endowment payment.
         */
        ENDOWMENT(27, 23),

        /**
         * Mortgage charge.
         */
        MORTGAGE(28, 24),

        /**
         * Insurance payment.
         */
        INSURANCE(29, 25),

        /**
         * National Insurance.
         */
        NATINSURANCE(30, 28),

        /**
         * Tax Relief.
         */
        TAXRELIEF(31, 10),

        /**
         * Tax Owed.
         */
        TAXOWED(32, 29),

        /**
         * Tax Refund.
         */
        TAXREFUND(33, 8),

        /**
         * Additional taxation.
         */
        EXTRATAX(34, 26),

        /**
         * Interest on Debts.
         */
        DEBTINTEREST(35, 5),

        /**
         * Write Off.
         */
        WRITEOFF(36, 27),

        /**
         * Tax Credit.
         */
        TAXCREDIT(37, 30),

        /**
         * Rental Income.
         */
        RENTALINCOME(38, 6);

        /**
         * Class Id.
         */
        private final int theId;

        /**
         * Class Order.
         */
        private final int theOrder;

        /**
         * Obtain Class Id.
         * @return the class id
         */
        @Override
        public int getClassId() {
            return theId;
        }

        /**
         * Obtain Class Order.
         * @return the class order
         */
        @Override
        public int getOrder() {
            return theOrder;
        }

        /**
         * Constructor.
         * @param uId the id
         * @param uOrder the default order.
         */
        private TransClass(final int uId,
                           final int uOrder) {
            theId = uId;
            theOrder = uOrder;
        }

        /**
         * get value from id.
         * @param id the id value
         * @return the corresponding enum object
         * @throws JDataException on error
         */
        public static TransClass fromId(final int id) throws JDataException {
            for (TransClass myClass : values()) {
                if (myClass.getClassId() == id) {
                    return myClass;
                }
            }
            throw new JDataException(ExceptionClass.DATA, "Invalid Frequency Class Id: " + id);
        }
    }

    /**
     * Enumeration of Tax Type Buckets.
     */
    public enum TaxBucket {
        /**
         * Transaction Summary.
         */
        TRANSSUMM(0),

        /**
         * Transaction Total.
         */
        TRANSTOTAL(100),

        /**
         * Tax Detail.
         */
        TAXDETAIL(200),

        /**
         * Tax Summary.
         */
        TAXSUMM(300),

        /**
         * Tax Total.
         */
        TAXTOTAL(400);

        /**
         * Order base.
         */
        private final int theBase;

        /**
         * Get the order base.
         * @return the order base
         */
        private int getBase() {
            return theBase;
        }

        /**
         * Constructor.
         * @param pBase the base
         */
        private TaxBucket(final int pBase) {
            theBase = pBase;
        }
    }

    /**
     * Enumeration of Tax Type Classes.
     */
    public enum TaxClass implements StaticInterface {
        /**
         * Gross Salary Income.
         */
        GROSSSALARY(1, 0, TaxBucket.TRANSSUMM),

        /**
         * Gross Interest Income.
         */
        GROSSINTEREST(2, 1, TaxBucket.TRANSSUMM),

        /**
         * Gross Dividend Income.
         */
        GROSSDIVIDEND(3, 2, TaxBucket.TRANSSUMM),

        /**
         * Gross Unit Trust Dividend Income.
         */
        GROSSUTDIVS(4, 3, TaxBucket.TRANSSUMM),

        /**
         * Gross Rental Income.
         */
        GROSSRENTAL(5, 4, TaxBucket.TRANSSUMM),

        /**
         * Gross Taxable gains.
         */
        GROSSTAXGAINS(6, 5, TaxBucket.TRANSSUMM),

        /**
         * Gross Capital gains.
         */
        GROSSCAPGAINS(7, 6, TaxBucket.TRANSSUMM),

        /**
         * Total Tax Paid.
         */
        TAXPAID(8, 7, TaxBucket.TRANSSUMM),

        /**
         * Market Growth/Shrinkage.
         */
        MARKET(9, 8, TaxBucket.TRANSSUMM),

        /**
         * Tax Free Income.
         */
        TAXFREE(10, 9, TaxBucket.TRANSSUMM),

        /**
         * Gross Expense.
         */
        EXPENSE(11, 10, TaxBucket.TRANSSUMM),

        /**
         * Virtual Income.
         */
        VIRTUAL(12, 11, TaxBucket.TRANSSUMM),

        /**
         * Non-Core Income.
         */
        NONCORE(13, 12, TaxBucket.TRANSSUMM),

        /**
         * Profit on Year.
         */
        PROFITLOSS(14, 0, TaxBucket.TRANSTOTAL),

        /**
         * Core Income after tax ignoring market movements and inheritance.
         */
        COREINCOME(15, 1, TaxBucket.TRANSTOTAL),

        /**
         * Profit on year after ignoring market movements and inheritance.
         */
        COREPROFITLOSS(16, 2, TaxBucket.TRANSTOTAL),

        /**
         * Gross Income.
         */
        GROSSINCOME(17, 0, TaxBucket.TAXDETAIL),

        /**
         * Original Allowance.
         */
        ORIGALLOW(18, 1, TaxBucket.TAXDETAIL),

        /**
         * Adjusted Allowance.
         */
        ADJALLOW(19, 2, TaxBucket.TAXDETAIL),

        /**
         * High Tax Band.
         */
        HITAXBAND(20, 3, TaxBucket.TAXDETAIL),

        /**
         * Salary at nil-rate.
         */
        SALARYFREE(21, 4, TaxBucket.TAXDETAIL),

        /**
         * Salary at low-rate.
         */
        SALARYLO(22, 5, TaxBucket.TAXDETAIL),

        /**
         * Salary at basic-rate.
         */
        SALARYBASIC(23, 6, TaxBucket.TAXDETAIL),

        /**
         * Salary at high-rate.
         */
        SALARYHI(24, 7, TaxBucket.TAXDETAIL),

        /**
         * Salary at additional-rate.
         */
        SALARYADD(25, 8, TaxBucket.TAXDETAIL),

        /**
         * Rental at nil-rate.
         */
        RENTALFREE(26, 9, TaxBucket.TAXDETAIL),

        /**
         * Rental at low-rate.
         */
        RENTALLO(27, 10, TaxBucket.TAXDETAIL),

        /**
         * Rental at basic-rate.
         */
        RENTALBASIC(28, 11, TaxBucket.TAXDETAIL),

        /**
         * Rental at high-rate.
         */
        RENTALHI(29, 12, TaxBucket.TAXDETAIL),

        /**
         * Rental at additional-rate.
         */
        RENTALADD(30, 13, TaxBucket.TAXDETAIL),

        /**
         * Interest at nil-rate.
         */
        INTERESTFREE(31, 14, TaxBucket.TAXDETAIL),

        /**
         * Interest at low-rate.
         */
        INTERESTLO(32, 15, TaxBucket.TAXDETAIL),

        /**
         * Interest at basic-rate.
         */
        INTERESTBASIC(33, 16, TaxBucket.TAXDETAIL),

        /**
         * Interest at high-rate.
         */
        INTERESTHI(34, 17, TaxBucket.TAXDETAIL),

        /**
         * Interest at additional-rate.
         */
        INTERESTADD(35, 18, TaxBucket.TAXDETAIL),

        /**
         * Dividends at basic-rate.
         */
        DIVIDENDBASIC(36, 19, TaxBucket.TAXDETAIL),

        /**
         * Dividends at high-rate.
         */
        DIVIDENDHI(37, 20, TaxBucket.TAXDETAIL),

        /**
         * Dividends at additional-rate.
         */
        DIVIDENDADD(38, 21, TaxBucket.TAXDETAIL),

        /**
         * Slice at basic-rate.
         */
        SLICEBASIC(39, 22, TaxBucket.TAXDETAIL),

        /**
         * Slice at high-rate.
         */
        SLICEHI(40, 23, TaxBucket.TAXDETAIL),

        /**
         * Slice at additional-rate.
         */
        SLICEADD(41, 24, TaxBucket.TAXDETAIL),

        /**
         * Gains at basic-rate.
         */
        GAINSBASIC(42, 25, TaxBucket.TAXDETAIL),

        /**
         * Gains at high-rate.
         */
        GAINSHI(43, 26, TaxBucket.TAXDETAIL),

        /**
         * Gains at additional-rate.
         */
        GAINSADD(44, 27, TaxBucket.TAXDETAIL),

        /**
         * Capital at nil-rate.
         */
        CAPITALFREE(45, 28, TaxBucket.TAXDETAIL),

        /**
         * Capital at basic-rate.
         */
        CAPITALBASIC(46, 29, TaxBucket.TAXDETAIL),

        /**
         * Capital at high-rate.
         */
        CAPITALHI(47, 30, TaxBucket.TAXDETAIL),

        /**
         * Total Taxation Due on Salary.
         */
        TAXDUESALARY(48, 0, TaxBucket.TAXSUMM),

        /**
         * Total Taxation Due on Rental.
         */
        TAXDUERENTAL(49, 1, TaxBucket.TAXSUMM),

        /**
         * Total Taxation Due on Interest.
         */
        TAXDUEINTEREST(50, 2, TaxBucket.TAXSUMM),

        /**
         * Total Taxation Due on Dividends.
         */
        TAXDUEDIVIDEND(51, 3, TaxBucket.TAXSUMM),

        /**
         * Total Taxation Due on Taxable Gains.
         */
        TAXDUETAXGAINS(52, 4, TaxBucket.TAXSUMM),

        /**
         * Total Taxation Due on Slice.
         */
        TAXDUESLICE(53, 5, TaxBucket.TAXSUMM),

        /**
         * Total Taxation Due on Capital Gains.
         */
        TAXDUECAPGAINS(54, 6, TaxBucket.TAXSUMM),

        /**
         * Total Taxation Due.
         */
        TOTALTAXATION(55, 0, TaxBucket.TAXTOTAL),

        /**
         * Taxation Profit (TaxDue-TaxPaid).
         */
        TAXPROFITLOSS(56, 1, TaxBucket.TAXTOTAL);

        /**
         * Class Id.
         */
        private final int theId;

        /**
         * Class Order.
         */
        private final int theOrder;

        /**
         * Class Bucket.
         */
        private TaxBucket theBucket = null;

        /**
         * Obtain Class Id.
         * @return the class id
         */
        @Override
        public int getClassId() {
            return theId;
        }

        /**
         * Obtain Class Bucket.
         * @return the class bucket
         */
        public TaxBucket getClassBucket() {
            return theBucket;
        }

        /**
         * Obtain Class Order.
         * @return the class order
         */
        @Override
        public int getOrder() {
            return theOrder;
        }

        /**
         * Constructor.
         * @param uId the id
         * @param uOrder the order
         * @param pBucket the bucket
         */
        private TaxClass(final int uId,
                         final int uOrder,
                         final TaxBucket pBucket) {
            /* Set values */
            theId = uId;
            theOrder = pBucket.getBase() + uOrder;
            theBucket = pBucket;
        }

        /**
         * get value from id.
         * @param id the id value
         * @return the corresponding enum object
         * @throws JDataException on error
         */
        public static TaxClass fromId(final int id) throws JDataException {
            for (TaxClass myClass : values()) {
                if (myClass.getClassId() == id) {
                    return myClass;
                }
            }
            throw new JDataException(ExceptionClass.DATA, "Invalid Tax Class Id: " + id);
        }
    }

    /**
     * Enumeration of Frequency Classes.
     */
    public enum FreqClass implements StaticInterface {
        /**
         * Weekly Frequency.
         */
        WEEKLY(1, 0, 7),

        /**
         * Monthly Frequency.
         */
        FORTNIGHTLY(2, 1, 14),

        /**
         * Monthly Frequency.
         */
        MONTHLY(3, 2, 1),

        /**
         * Monthly Frequency (at end of month).
         */
        ENDOFMONTH(4, 3, 1),

        /**
         * Quarterly Frequency.
         */
        QUARTERLY(5, 4, 3),

        /**
         * Half Yearly Frequency.
         */
        HALFYEARLY(6, 5, 6),

        /**
         * Annual Frequency.
         */
        ANNUALLY(7, 6, 0),

        /**
         * Only on Maturity.
         */
        MATURITY(8, 7, 0),

        /**
         * Monthly for up to ten-months.
         */
        TENMONTHS(9, 8, 1);

        /**
         * Class Id.
         */
        private final int theId;

        /**
         * Class Order.
         */
        private final int theOrder;

        /**
         * Adjustment factor.
         */
        private final int theAdjust;

        /**
         * Obtain Class Id.
         * @return the class id
         */
        @Override
        public int getClassId() {
            return theId;
        }

        /**
         * Obtain Class Order.
         * @return the class order
         */
        @Override
        public int getOrder() {
            return theOrder;
        }

        /**
         * Obtain Adjustment.
         * @return the adjustment
         */
        public int getAdjustment() {
            return theAdjust;
        }

        /**
         * Constructor.
         * @param uId the id
         * @param uOrder the default order
         * @param uAdjust the adjustment
         */
        private FreqClass(final int uId,
                          final int uOrder,
                          final int uAdjust) {
            theId = uId;
            theOrder = uOrder;
            theAdjust = uAdjust;
        }

        /**
         * get value from id.
         * @param id the id value
         * @return the corresponding enum object
         * @throws JDataException on error
         */
        public static FreqClass fromId(final int id) throws JDataException {
            for (FreqClass myClass : values()) {
                if (myClass.getClassId() == id) {
                    return myClass;
                }
            }
            throw new JDataException(ExceptionClass.DATA, "Invalid Frequency Class Id: " + id);
        }
    }

    /**
     * Enumeration of TaxRegime Classes.
     */
    public enum TaxRegClass implements StaticInterface {
        /**
         * Archive tax regime.
         */
        ARCHIVE(1, 0),

        /**
         * Standard tax regime.
         */
        STANDARD(2, 1),

        /**
         * Low Interest Tax Band.
         */
        LOINTEREST(3, 2),

        /**
         * Additional tax band.
         */
        ADDITIONALBAND(4, 3);

        /**
         * Class Id.
         */
        private final int theId;

        /**
         * Class Order.
         */
        private final int theOrder;

        /**
         * Obtain Class Id.
         * @return the class id
         */
        @Override
        public int getClassId() {
            return theId;
        }

        /**
         * Obtain Class Order.
         * @return the class order
         */
        @Override
        public int getOrder() {
            return theOrder;
        }

        /**
         * Constructor.
         * @param uId the id
         * @param uOrder the default order
         */
        private TaxRegClass(final int uId,
                            final int uOrder) {
            theId = uId;
            theOrder = uOrder;
        }

        /**
         * get value from id.
         * @param id the id value
         * @return the corresponding enum object
         * @throws JDataException on error
         */
        public static TaxRegClass fromId(final int id) throws JDataException {
            for (TaxRegClass myClass : values()) {
                if (myClass.getClassId() == id) {
                    return myClass;
                }
            }
            throw new JDataException(ExceptionClass.DATA, "Invalid Tax Regime Class Id: " + id);
        }
    }

    /**
     * Enumeration of EventInfo Classes..
     */
    public enum EventInfoClass implements StaticInterface {
        /**
         * Tax Credit.
         */
        TaxCredit(1, 0),

        /**
         * National Insurance.
         */
        NatInsurance(2, 1),

        /**
         * Benefit.
         */
        Benefit(3, 2),

        /**
         * Pension.
         */
        Pension(4, 3),

        /**
         * QualifyingYears.
         */
        QualifyYears(5, 4),

        /**
         * TransferDelay.
         */
        XferDelay(6, 5),

        /**
         * Credit Units.
         */
        CreditUnits(7, 6),

        /**
         * Debit Units.
         */
        DebitUnits(8, 7),

        /**
         * Dilution.
         */
        Dilution(9, 8),

        /**
         * CashConsideration.
         */
        CashConsider(10, 9),

        /**
         * ThirdParty Account.
         */
        // ThirdParty(11, 10);
        CashAccount(11, 10);

        /**
         * Class Id.
         */
        private final int theId;

        /**
         * Class Order.
         */
        private final int theOrder;

        /**
         * Obtain Class Id.
         * @return the class id
         */
        @Override
        public int getClassId() {
            return theId;
        }

        /**
         * Obtain Class Order.
         * @return the class order
         */
        @Override
        public int getOrder() {
            return theOrder;
        }

        /**
         * Constructor.
         * @param uId the id
         * @param uOrder the default order
         */
        private EventInfoClass(final int uId,
                               final int uOrder) {
            theId = uId;
            theOrder = uOrder;
        }

        /**
         * get value from id.
         * @param id the id value
         * @return the corresponding enum object
         * @throws JDataException on error
         */
        public static EventInfoClass fromId(final int id) throws JDataException {
            for (EventInfoClass myClass : values()) {
                if (myClass.getClassId() == id) {
                    return myClass;
                }
            }
            throw new JDataException(ExceptionClass.DATA, "Invalid EventInfo Class Id: " + id);
        }
    }
}
