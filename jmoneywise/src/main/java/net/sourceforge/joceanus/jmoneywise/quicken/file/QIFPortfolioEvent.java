/*******************************************************************************
 * jMoneyWise: Finance Application
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmetis.viewer.JDataFormatter;
import net.sourceforge.joceanus.jmoneywise.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QActionType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QPortfolioLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFCategoryAccountLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFCategoryLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFClearedLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFDateLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFPayeeLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFPriceLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFRatioLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFSecurityLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFUnitsLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFXferAccountLine;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JPrice;
import net.sourceforge.joceanus.jtethys.decimal.JRatio;
import net.sourceforge.joceanus.jtethys.decimal.JUnits;

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
     * Constructor.
     * @param pFile the QIF File
     * @param pTrans the transaction
     * @param pAction the action
     */
    protected QIFPortfolioEvent(final QIFFile pFile,
                                final Transaction pTrans,
                                final QActionType pAction) {
        /* Call super-constructor */
        super(pFile, QPortfolioLineType.class);

        /* Store values */
        theDate = pTrans.getDate();
        isCleared = pTrans.isReconciled();
        theAction = pAction;

        /* Add the lines */
        addLine(new QIFPortfolioDateLine(theDate));
        addLine(new QIFPortfolioActionLine(theAction));
        addLine(new QIFPortfolioClearedLine(isCleared));

        /* Add the comment line if it exists */
        String myComment = pTrans.getComments();
        if (myComment != null) {
            addLine(new QIFPortfolioCommentLine(myComment));
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
                    case DATE:
                        JDateDay myDateDay = myDateParser.parseDateDay(myData);
                        addLine(new QIFPortfolioDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case CLEARED:
                        Boolean myFlag = Boolean.parseBoolean(myData);
                        addLine(new QIFPortfolioClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case AMOUNT:
                        JMoney myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new QIFPortfolioAmountLine(myMoney));
                        break;
                    case COMMENT:
                        addLine(new QIFPortfolioCommentLine(myData));
                        break;
                    case ACTION:
                        myAction = QActionType.parseLine(myData);
                        addLine(new QIFPortfolioActionLine(myAction));
                        break;
                    case PRICE:
                        JPrice myPrice = myDecParser.parsePriceValue(myData);
                        addLine(new QIFPortfolioPriceLine(myPrice));
                        break;
                    case COMMISSION:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new QIFPortfolioCommissionLine(myMoney));
                        break;
                    case PAYEE:
                        addLine(new QIFPortfolioPayeeDescLine(myData));
                        break;
                    case QUANTITY:
                        JUnits myUnits = myDecParser.parseUnitsValue(myData);
                        addLine(new QIFPortfolioQuantityLine(myUnits));
                        break;
                    case SECURITY:
                        addLine(new QIFPortfolioSecurityLine(pFile.getSecurity(myData)));
                        break;
                    case XFERACCOUNT:
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
                    case XFERAMOUNT:
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
    }

    /**
     * record security.
     * @param pSecurity the security
     */
    protected void recordSecurity(final QIFSecurity pSecurity) {
        /* Add security line */
        addLine(new QIFPortfolioSecurityLine(pSecurity));
    }

    /**
     * record category.
     * @param pCategory the category
     */
    protected void recordCategory(final QIFEventCategory pCategory) {
        /* Add security line */
        addLine(new QIFPortfolioCategoryLine(pCategory));
    }

    /**
     * record amount.
     * @param pAmount the amount
     */
    protected void recordAmount(final JMoney pAmount) {
        /* Add amount line */
        addLine(new QIFPortfolioAmountLine(pAmount));
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
     * record payee.
     * @param pPayee the payee
     */
    protected void recordPayee(final QIFPayee pPayee) {
        /* Add payee line */
        addLine(new QIFPortfolioPayeeLine(pPayee));
    }

    /**
     * record transfer.
     * @param pAccount the transfer account
     * @param pAmount the transfer amount
     */
    protected void recordXfer(final QIFAccount pAccount,
                              final JMoney pAmount) {
        /* Add transfer lines */
        addLine(new QIFPortfolioAccountLine(pAccount));
        addLine(new QIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record transfer.
     * @param pCategory the transfer category
     * @param pAmount the transfer amount
     */
    protected void recordXfer(final QIFEventCategory pCategory,
                              final JMoney pAmount) {
        /* Add transfer lines */
        addLine(new QIFPortfolioCategoryLine(pCategory));
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
     */
    protected void recordPrice(final JPrice pPrice) {
        /* Add price line */
        addLine(new QIFPortfolioPriceLine(pPrice));
    }

    /**
     * record commission.
     * @param pCommission the commission
     */
    protected void recordCommission(final JMoney pCommission) {
        /* Add commission line */
        addLine(new QIFPortfolioCommissionLine(pCommission));
    }

    /**
     * The Portfolio Date line.
     */
    public class QIFPortfolioDateLine
            extends QIFDateLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.DATE;
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
            return QPortfolioLineType.COMMENT;
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
            return QPortfolioLineType.CLEARED;
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
            return QPortfolioLineType.AMOUNT;
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
            return QPortfolioLineType.COMMISSION;
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
            return QPortfolioLineType.PRICE;
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
            return QPortfolioLineType.QUANTITY;
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
            return QPortfolioLineType.QUANTITY;
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
            return QPortfolioLineType.ACTION;
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
            pBuilder.append(theAction.getSymbol());
        }
    }

    /**
     * The Portfolio Security line.
     */
    public class QIFPortfolioSecurityLine
            extends QIFSecurityLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.SECURITY;
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
    public class QIFPortfolioPayeeLine
            extends QIFPayeeLine<QPortfolioLineType> {
        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.PAYEE;
        }

        /**
         * Constructor.
         * @param pPayee the payee
         */
        protected QIFPortfolioPayeeLine(final QIFPayee pPayee) {
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
            return QPortfolioLineType.PAYEE;
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
            return QPortfolioLineType.XFERACCOUNT;
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
            return QPortfolioLineType.XFERACCOUNT;
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
            return QPortfolioLineType.XFERACCOUNT;
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
            return QPortfolioLineType.XFERAMOUNT;
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
