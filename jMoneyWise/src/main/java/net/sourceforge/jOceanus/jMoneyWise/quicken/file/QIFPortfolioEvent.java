/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2013 Tony Washer
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
package net.sourceforge.jOceanus.jMoneyWise.quicken.file;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRatio;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFClearedLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFDateLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFPayeeLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFPriceLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFRatioLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFSecurityLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFUnitsLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFXferAccountLine;

/**
 * Class representing a QIF Portfolio Event record.
 */
public class QIFPortfolioEvent {
    /**
     * The Portfolio Date line.
     */
    public class QIFPortfolioDateLine
            extends QIFDateLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Date;
        }

        /**
         * Constructor.
         * @param pDate the Date
         */
        protected QIFPortfolioDateLine(final JDateDay pDate) {
            /* Call super-constructor */
            super(pDate);
        }
    }

    /**
     * The Portfolio Comment line.
     */
    public static class QIFPortfolioCommentLine
            extends QIFStringLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Comment;
        }

        /**
         * Obtain Comment.
         * @return the comment
         */
        public String getComment() {
            return getValue();
        }

        /**
         * Constructor.
         * @param pComment the comment
         */
        protected QIFPortfolioCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }
    }

    /**
     * The Portfolio Cleared line.
     */
    public static class QIFPortfolioClearedLine
            extends QIFClearedLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Cleared;
        }

        /**
         * Constructor.
         * @param pCleared is the event cleared?
         */
        protected QIFPortfolioClearedLine(final Boolean pCleared) {
            /* Call super-constructor */
            super(pCleared);
        }
    }

    /**
     * The Portfolio Amount line.
     */
    public static class QIFPortfolioAmountLine
            extends QIFMoneyLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Amount;
        }

        /**
         * Obtain Amount.
         * @return the amount
         */
        public JMoney getAmount() {
            return getMoney();
        }

        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected QIFPortfolioAmountLine(final JMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }
    }

    /**
     * The Portfolio Commission line.
     */
    public static class QIFPortfolioCommissionLine
            extends QIFMoneyLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Commission;
        }

        /**
         * Obtain Commission.
         * @return the commission
         */
        public JMoney getCommission() {
            return getMoney();
        }

        /**
         * Constructor.
         * @param pCommission the commission
         */
        protected QIFPortfolioCommissionLine(final JMoney pCommission) {
            /* Call super-constructor */
            super(pCommission);
        }
    }

    /**
     * The Portfolio Price line.
     */
    public static class QIFPortfolioPriceLine
            extends QIFPriceLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Price;
        }

        @Override
        public JPrice getPrice() {
            return super.getPrice();
        }

        /**
         * Constructor.
         * @param pPrice the price
         */
        protected QIFPortfolioPriceLine(final JPrice pPrice) {
            /* Call super-constructor */
            super(pPrice);
        }
    }

    /**
     * The Portfolio Quantity line.
     */
    public static class QIFPortfolioQuantityLine
            extends QIFUnitsLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Quantity;
        }

        @Override
        public JUnits getUnits() {
            return super.getUnits();
        }

        /**
         * Constructor.
         * @param pUnits the units
         */
        protected QIFPortfolioQuantityLine(final JUnits pUnits) {
            /* Call super-constructor */
            super(pUnits);
        }
    }

    /**
     * The Portfolio Split Ratio line.
     */
    public static class QIFPortfolioSplitRatioLine
            extends QIFRatioLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Quantity;
        }

        @Override
        public JRatio getRatio() {
            return super.getRatio();
        }

        /**
         * Constructor.
         * @param pRatio the ratio
         */
        protected QIFPortfolioSplitRatioLine(final JRatio pRatio) {
            /* Call super-constructor */
            super(pRatio);
        }
    }

    /**
     * The Portfolio Action line.
     */
    public static class QIFPortfolioActionLine
            extends QIFLine<QPortfolioLineType> {
        /**
         * The action type.
         */
        private final QActionType theAction;

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Security;
        }

        /**
         * Obtain the security.
         * @return the security
         */
        public QActionType getAction() {
            return theAction;
        }

        /**
         * Constructor.
         * @param pAction the action type
         */
        protected QIFPortfolioActionLine(final QActionType pAction) {
            /* Store the data */
            theAction = pAction;
        }

        @Override
        protected void formatData(final JDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Add the action */
            pBuilder.append(theAction.toString());
        }
    }

    /**
     * The Portfolio Security line.
     */
    public static class QIFPortfolioSecurityLine
            extends QIFSecurityLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Security;
        }

        /**
         * Constructor.
         * @param pSecurity the security
         */
        protected QIFPortfolioSecurityLine(final QIFSecurity pSecurity) {
            /* Call super-constructor */
            super(pSecurity);
        }
    }

    /**
     * The Portfolio Payee Account line.
     */
    public static class QIFPortfolioPayeeAccountLine
            extends QIFPayeeLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Payee;
        }

        /**
         * Constructor.
         * @param pPayee the payee
         */
        protected QIFPortfolioPayeeAccountLine(final QIFAccount pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }
    }

    /**
     * The Portfolio Payee Description line.
     */
    public static class QIFPortfolioPayeeDescLine
            extends QIFStringLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.Payee;
        }

        /**
         * Constructor.
         * @param pPayee the payee description
         */
        protected QIFPortfolioPayeeDescLine(final String pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }
    }

    /**
     * The Portfolio Account line.
     */
    public static class QIFPortfolioAccountLine
            extends QIFXferAccountLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.TransferAccount;
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        protected QIFPortfolioAccountLine(final QIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }
    }

    /**
     * The Portfolio Transfer Amount line.
     */
    public static class QIFPortfolioXferAmountLine
            extends QIFMoneyLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.TransferAmount;
        }

        /**
         * Obtain Amount.
         * @return the amount
         */
        public JMoney getAmount() {
            return getMoney();
        }

        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected QIFPortfolioXferAmountLine(final JMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }
    }
}
