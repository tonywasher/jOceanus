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

import java.util.Iterator;
import java.util.List;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDateDay.JDateDayFormatter;
import net.sourceforge.jOceanus.jDecimal.JDecimalParser;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jDecimal.JPrice;
import net.sourceforge.jOceanus.jDecimal.JRatio;
import net.sourceforge.jOceanus.jDecimal.JUnits;
import net.sourceforge.jOceanus.jMoneyWise.data.Account;
import net.sourceforge.jOceanus.jMoneyWise.quicken.definitions.QActionType;
import net.sourceforge.jOceanus.jMoneyWise.quicken.definitions.QPortfolioLineType;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFCategoryAccountLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFCategoryLine;
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
public class QIFPortfolioEvent
        extends QIFRecord<QPortfolioLineType> {
    /**
     * The Date.
     */
    private final JDateDay theDate;

    /**
     * The Cleared Flag.
     */
    private final Boolean isCleared;

    /**
     * The Action.
     */
    private final QActionType theAction;

    /**
     * The Amount.
     */
    private final JMoney theAmount;

    /**
     * The Comment.
     */
    private final String theComment;

    /**
     * Obtain the date.
     * @return the date.
     */
    public JDateDay getDate() {
        return theDate;
    }

    /**
     * Is the record cleared.
     * @return true/false.
     */
    public Boolean isCleared() {
        return isCleared;
    }

    /**
     * Obtain the action.
     * @return the action.
     */
    public QActionType getAction() {
        return theAction;
    }

    /**
     * Obtain the amount.
     * @return the amount.
     */
    public JMoney getAmount() {
        return theAmount;
    }

    /**
     * Obtain the comment.
     * @return the comment.
     */
    public String getComment() {
        return theComment;
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pDate the Date of the event
     * @param pAction the action
     * @param pCleared is the event cleared?
     * @param pAmount the amount
     * @param pComment the comment
     */
    protected QIFPortfolioEvent(final QIFFile pFile,
                                final JDateDay pDate,
                                final QActionType pAction,
                                final Boolean pCleared,
                                final JMoney pAmount,
                                final String pComment) {
        /* Call super-constructor */
        super(pFile, QPortfolioLineType.class);

        /* Store values */
        theDate = pDate;
        isCleared = pCleared;
        theAmount = pAmount;
        theComment = pComment;
        theAction = pAction;

        /* Add the lines */
        addLine(new QIFPortfolioDateLine(pDate));
        addLine(new QIFPortfolioActionLine(pAction));
        addLine(new QIFPortfolioClearedLine(pCleared));
        addLine(new QIFPortfolioAmountLine(pAmount));
        if (pComment != null) {
            addLine(new QIFPortfolioCommentLine(pComment));
        }
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the Data Formatter
     * @param pLines the data lines
     */
    protected QIFPortfolioEvent(final QIFFile pFile,
                                final JDataFormatter pFormatter,
                                final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, QPortfolioLineType.class);

        /* Determine details */
        JDateDay myDate = null;
        QActionType myAction = null;
        Boolean myCleared = null;
        JMoney myAmount = null;
        String myComment = null;

        /* Obtain parsers */
        JDateDayFormatter myDateParser = pFormatter.getDateFormatter();
        JDecimalParser myDecParser = pFormatter.getDecimalParser();

        /* Loop through the lines */
        Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            String myLine = myIterator.next();

            /* Determine the category */
            QPortfolioLineType myType = QPortfolioLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case Date:
                        JDateDay myDateDay = myDateParser.parseDateDay(myData);
                        addLine(new QIFPortfolioDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case Cleared:
                        Boolean myFlag = Boolean.parseBoolean(myData);
                        addLine(new QIFPortfolioClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case Amount:
                        JMoney myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new QIFPortfolioAmountLine(myMoney));
                        myAmount = myMoney;
                        break;
                    case Comment:
                        addLine(new QIFPortfolioCommentLine(myData));
                        myComment = myData;
                        break;
                    case Action:
                        myAction = QActionType.parseLine(myData);
                        addLine(new QIFPortfolioActionLine(myAction));
                        break;
                    case Price:
                        JPrice myPrice = myDecParser.parsePriceValue(myData);
                        addLine(new QIFPortfolioPriceLine(myPrice));
                        break;
                    case Commission:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new QIFPortfolioCommissionLine(myMoney));
                        break;
                    case Payee:
                        addLine(new QIFPortfolioPayeeDescLine(myData));
                        break;
                    case Quantity:
                        JUnits myUnits = myDecParser.parseUnitsValue(myData);
                        addLine(new QIFPortfolioQuantityLine(myUnits));
                        break;
                    case Security:
                        addLine(new QIFPortfolioSecurityLine(pFile.getSecurity(myData)));
                        break;
                    case TransferAccount:
                        /* Look for account, category and classes */
                        QIFAccount myAccount = QIFXferAccountLine.parseAccount(pFile, myData);
                        QIFEventCategory myCategory = QIFCategoryLine.parseCategory(pFile, myData);
                        List<QIFClass> myClasses = QIFXferAccountLine.parseAccountClasses(pFile, myData);
                        if (myAccount == null) {
                            myClasses = QIFCategoryLine.parseCategoryClasses(pFile, myData);
                            addLine(new QIFPortfolioCategoryLine(myCategory, myClasses));
                        } else if (myCategory == null) {
                            addLine(new QIFPortfolioAccountLine(myAccount, myClasses));
                        } else {
                            addLine(new QIFPortfolioCategoryAccountLine(myCategory, myAccount, myClasses));
                        }
                        break;
                    case TransferAmount:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new QIFPortfolioXferAmountLine(myMoney));
                        break;
                    default:
                        break;
                }
            }
        }

        /* Build details */
        theDate = myDate;
        theAction = myAction;
        isCleared = myCleared;
        theAmount = myAmount;
        theComment = myComment;
    }

    /**
     * record security.
     * @param pSecurity the security
     */
    protected void recordSecurity(final Account pSecurity) {
        /* Add security line */
        addLine(new QIFPortfolioSecurityLine(getFile().getSecurity(pSecurity.getName())));
    }

    /**
     * record payee.
     * @param pPayee the payee
     */
    protected void recordPayee(final String pPayee) {
        /* Add payee line */
        addLine(new QIFPortfolioPayeeDescLine(pPayee));
    }

    /**
     * record transfer.
     * @param pAccount the transfer account
     * @param pAmount the transfer amount
     */
    protected void recordXfer(final Account pAccount,
                              final JMoney pAmount) {
        /* Add transfer lines */
        addLine(new QIFPortfolioAccountLine(getFile().getAccount(pAccount.getName())));
        addLine(new QIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record quantity.
     * @param pQuantity the units quantity
     */
    protected void recordQuantity(final JUnits pQuantity) {
        /* Add quantity line */
        addLine(new QIFPortfolioQuantityLine(pQuantity));
    }

    /**
     * record quantity.
     * @param pRatio the split ratio
     */
    protected void recordQuantity(final JRatio pRatio) {
        /* Add quantity line */
        addLine(new QIFPortfolioSplitRatioLine(pRatio));
    }

    /**
     * record price.
     * @param pPrice the price
     * @param pCommission the commission
     */
    protected void recordPrice(final JPrice pPrice,
                               final JMoney pCommission) {
        /* Add price and commission lines */
        addLine(new QIFPortfolioPriceLine(pPrice));
        addLine(new QIFPortfolioCommissionLine(pCommission));
    }

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
    public class QIFPortfolioCommentLine
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
    public class QIFPortfolioClearedLine
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
    public class QIFPortfolioAmountLine
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
    public class QIFPortfolioCommissionLine
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
    public class QIFPortfolioPriceLine
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
    public class QIFPortfolioQuantityLine
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
    public class QIFPortfolioSplitRatioLine
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
    public class QIFPortfolioActionLine
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
    public class QIFPortfolioSecurityLine
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
    public class QIFPortfolioPayeeAccountLine
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
    public class QIFPortfolioPayeeDescLine
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
    public class QIFPortfolioAccountLine
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

        /**
         * Constructor.
         * @param pAccount the account
         * @param pClasses the account classes
         */
        protected QIFPortfolioAccountLine(final QIFAccount pAccount,
                                          final List<QIFClass> pClasses) {
            /* Call super-constructor */
            super(pAccount, pClasses);
        }
    }

    /**
     * The Portfolio Category line.
     */
    public class QIFPortfolioCategoryLine
            extends QIFCategoryLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.TransferAccount;
        }

        /**
         * Constructor.
         * @param pCategory the category
         */
        protected QIFPortfolioCategoryLine(final QIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         * @param pCategory the category
         * @param pClasses the account classes
         */
        protected QIFPortfolioCategoryLine(final QIFEventCategory pCategory,
                                           final List<QIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pClasses);
        }
    }

    /**
     * The Portfolio Category line.
     */
    public class QIFPortfolioCategoryAccountLine
            extends QIFCategoryAccountLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.TransferAccount;
        }

        /**
         * Constructor.
         * @param pCategory the category
         * @param pAccount the account
         */
        protected QIFPortfolioCategoryAccountLine(final QIFEventCategory pCategory,
                                                  final QIFAccount pAccount) {
            /* Call super-constructor */
            super(pCategory, pAccount);
        }

        /**
         * Constructor.
         * @param pCategory the category
         * @param pAccount the account
         * @param pClasses the account classes
         */
        protected QIFPortfolioCategoryAccountLine(final QIFEventCategory pCategory,
                                                  final QIFAccount pAccount,
                                                  final List<QIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pAccount, pClasses);
        }
    }

    /**
     * The Portfolio Transfer Amount line.
     */
    public class QIFPortfolioXferAmountLine
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
