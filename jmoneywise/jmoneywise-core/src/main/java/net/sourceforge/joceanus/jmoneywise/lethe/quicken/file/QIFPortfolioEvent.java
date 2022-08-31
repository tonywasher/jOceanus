/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2022 Tony Washer
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.lethe.quicken.file;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jmoneywise.lethe.data.Transaction;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QActionType;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.definitions.QPortfolioLineType;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFCategoryAccountLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFCategoryLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFClearedLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFDateLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFPayeeLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFPriceLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFRatioLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFSecurityLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFUnitsLine;
import net.sourceforge.joceanus.jmoneywise.lethe.quicken.file.QIFLine.QIFXferAccountLine;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.TethysDataFormatter;

/**
 * Class representing a QIF Portfolio Event record.
 */
public class QIFPortfolioEvent
        extends QIFEventRecord<QPortfolioLineType> {
    /**
     * The Date.
     */
    private final TethysDate theDate;

    /**
     * The Cleared Flag.
     */
    private final Boolean isCleared;

    /**
     * The Action.
     */
    private final QActionType theAction;

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
        final String myComment = pTrans.getComments();
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
                                final TethysDataFormatter pFormatter,
                                final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, QPortfolioLineType.class);

        /* Determine details */
        TethysDate myDate = null;
        QActionType myAction = null;
        Boolean myCleared = null;

        /* Obtain parsers */
        final TethysDateFormatter myDateParser = pFormatter.getDateFormatter();
        final TethysDecimalParser myDecParser = pFormatter.getDecimalParser();

        /* Loop through the lines */
        final Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            final String myLine = myIterator.next();

            /* Determine the category */
            final QPortfolioLineType myType = QPortfolioLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case DATE:
                        final TethysDate myDateDay = myDateParser.parseDateBase(myData, QIFWriter.QIF_BASEYEAR);
                        addLine(new QIFPortfolioDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case CLEARED:
                        final Boolean myFlag = myData.equals(QIFLine.QIF_RECONCILED);
                        addLine(new QIFPortfolioClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case AMOUNT:
                        TethysMoney myMoney = myDecParser.parseMoneyValue(myData);
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
                        final TethysPrice myPrice = myDecParser.parsePriceValue(myData);
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
                        final TethysUnits myUnits = myDecParser.parseUnitsValue(myData);
                        addLine(new QIFPortfolioQuantityLine(myUnits));
                        break;
                    case SECURITY:
                        addLine(new QIFPortfolioSecurityLine(pFile.getSecurity(myData)));
                        break;
                    case XFERACCOUNT:
                        /* Look for account, category and classes */
                        final QIFAccount myAccount = QIFXferAccountLine.parseAccount(pFile, myData);
                        final QIFEventCategory myCategory = QIFCategoryLine.parseCategory(pFile, myData);
                        List<QIFClass> myClasses = QIFXferAccountLine.parseAccountClasses(pFile, myData);
                        if (myAccount == null) {
                            myClasses = QIFCategoryLine.parseCategoryClasses(pFile, myData);
                            addLine(new QIFPortfolioCategoryLine(myCategory, myClasses));
                            convertPayee();
                        } else if (myCategory == null) {
                            addLine(new QIFPortfolioAccountLine(myAccount, myClasses));
                        } else {
                            addLine(new QIFPortfolioCategoryAccountLine(myCategory, myAccount, myClasses));
                            convertPayee();
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

        /* Convert any split */
        if (QActionType.STKSPLIT.equals(myAction)) {
            convertSplit();
        }

        /* Build details */
        theDate = myDate;
        theAction = myAction;
        isCleared = myCleared;
    }

    @Override
    public TethysDate getDate() {
        return theDate;
    }

    @Override
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
        /* Add category line */
        addLine(new QIFPortfolioCategoryLine(pCategory));
    }

    /**
     * record category.
     * @param pCategory the category
     * @param pClasses the classes
     */
    protected void recordCategory(final QIFEventCategory pCategory,
                                  final List<QIFClass> pClasses) {
        /* Add category line */
        addLine(new QIFPortfolioCategoryLine(pCategory, pClasses));
    }

    /**
     * record amount.
     * @param pAmount the amount
     */
    protected void recordAmount(final TethysMoney pAmount) {
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
                              final TethysMoney pAmount) {
        /* Add transfer lines */
        addLine(new QIFPortfolioAccountLine(pAccount));
        addLine(new QIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record transfer.
     * @param pAccount the transfer account
     * @param pClasses the classes
     * @param pAmount the transfer amount
     */
    protected void recordXfer(final QIFAccount pAccount,
                              final List<QIFClass> pClasses,
                              final TethysMoney pAmount) {
        /* Add transfer lines */
        addLine(new QIFPortfolioAccountLine(pAccount, pClasses));
        addLine(new QIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record transfer.
     * @param pCategory the transfer category
     * @param pAmount the transfer amount
     */
    protected void recordXfer(final QIFEventCategory pCategory,
                              final TethysMoney pAmount) {
        /* Add transfer lines */
        addLine(new QIFPortfolioCategoryLine(pCategory));
        addLine(new QIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record quantity.
     * @param pQuantity the units quantity
     */
    protected void recordQuantity(final TethysUnits pQuantity) {
        /* Add quantity line */
        addLine(new QIFPortfolioQuantityLine(pQuantity));
    }

    /**
     * record quantity.
     * @param pRatio the split ratio
     */
    protected void recordQuantity(final TethysRatio pRatio) {
        /* Add quantity line */
        addLine(new QIFPortfolioSplitRatioLine(pRatio));
    }

    /**
     * record price.
     * @param pPrice the price
     */
    protected void recordPrice(final TethysPrice pPrice) {
        /* Add price line */
        addLine(new QIFPortfolioPriceLine(pPrice));
    }

    /**
     * record commission.
     * @param pCommission the commission
     */
    protected void recordCommission(final TethysMoney pCommission) {
        /* Add commission line */
        addLine(new QIFPortfolioCommissionLine(pCommission));
    }

    /**
     * Convert Payee.
     */
    private void convertPayee() {
        /* Look for a payee line */
        final QIFLine<QPortfolioLineType> myLine = getLine(QPortfolioLineType.PAYEE);
        if (myLine instanceof QIFPortfolioPayeeDescLine) {
            /* Access payee */
            final QIFPortfolioPayeeDescLine myDesc = (QIFPortfolioPayeeDescLine) myLine;
            final String myName = myDesc.getValue();

            /* Register the payee */
            final QIFPayee myPayee = getFile().registerPayee(myName);
            addLine(new QIFPortfolioPayeeLine(myPayee));
        }
    }

    /**
     * Convert Split.
     */
    private void convertSplit() {
        /* Look for an action line */
        final QIFLine<QPortfolioLineType> myLine = getLine(QPortfolioLineType.QUANTITY);
        if (myLine instanceof QIFPortfolioQuantityLine) {
            /* Extract action */
            final QIFPortfolioQuantityLine myQuantity = (QIFPortfolioQuantityLine) myLine;
            final TethysUnits myUnits = myQuantity.getUnits();

            /* Convert to ratio line */
            final TethysRatio myRatio = new TethysRatio(myUnits.toString());
            addLine(new QIFPortfolioSplitRatioLine(myRatio));
        }
    }

    /**
     * The Portfolio Date line.
     */
    public class QIFPortfolioDateLine
            extends QIFDateLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pDate the Date
         */
        protected QIFPortfolioDateLine(final TethysDate pDate) {
            /* Call super-constructor */
            super(pDate);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.DATE;
        }
    }

    /**
     * The Portfolio Comment line.
     */
    public class QIFPortfolioCommentLine
            extends QIFStringLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pComment the comment
         */
        protected QIFPortfolioCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }

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
    }

    /**
     * The Portfolio Cleared line.
     */
    public class QIFPortfolioClearedLine
            extends QIFClearedLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pCleared is the event cleared?
         */
        protected QIFPortfolioClearedLine(final Boolean pCleared) {
            /* Call super-constructor */
            super(pCleared);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.CLEARED;
        }
    }

    /**
     * The Portfolio Amount line.
     */
    public class QIFPortfolioAmountLine
            extends QIFMoneyLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected QIFPortfolioAmountLine(final TethysMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.AMOUNT;
        }

        /**
         * Obtain Amount.
         * @return the amount
         */
        public TethysMoney getAmount() {
            return getMoney();
        }
    }

    /**
     * The Portfolio Commission line.
     */
    public class QIFPortfolioCommissionLine
            extends QIFMoneyLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pCommission the commission
         */
        protected QIFPortfolioCommissionLine(final TethysMoney pCommission) {
            /* Call super-constructor */
            super(pCommission);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.COMMISSION;
        }

        /**
         * Obtain Commission.
         * @return the commission
         */
        public TethysMoney getCommission() {
            return getMoney();
        }
    }

    /**
     * The Portfolio Price line.
     */
    public class QIFPortfolioPriceLine
            extends QIFPriceLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pPrice the price
         */
        protected QIFPortfolioPriceLine(final TethysPrice pPrice) {
            /* Call super-constructor */
            super(pPrice);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.PRICE;
        }
    }

    /**
     * The Portfolio Quantity line.
     */
    public class QIFPortfolioQuantityLine
            extends QIFUnitsLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pUnits the units
         */
        protected QIFPortfolioQuantityLine(final TethysUnits pUnits) {
            /* Call super-constructor */
            super(pUnits);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.QUANTITY;
        }
    }

    /**
     * The Portfolio Split Ratio line.
     */
    public class QIFPortfolioSplitRatioLine
            extends QIFRatioLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pRatio the ratio
         */
        protected QIFPortfolioSplitRatioLine(final TethysRatio pRatio) {
            /* Call super-constructor */
            super(pRatio);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.QUANTITY;
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

        /**
         * Constructor.
         * @param pAction the action type
         */
        protected QIFPortfolioActionLine(final QActionType pAction) {
            /* Store the data */
            theAction = pAction;
        }

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

        @Override
        protected void formatData(final TethysDataFormatter pFormatter,
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
        /**
         * Constructor.
         * @param pSecurity the security
         */
        protected QIFPortfolioSecurityLine(final QIFSecurity pSecurity) {
            /* Call super-constructor */
            super(pSecurity);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.SECURITY;
        }
    }

    /**
     * The Portfolio Payee Account line.
     */
    public class QIFPortfolioPayeeLine
            extends QIFPayeeLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pPayee the payee
         */
        protected QIFPortfolioPayeeLine(final QIFPayee pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.PAYEE;
        }
    }

    /**
     * The Portfolio Payee Description line.
     */
    public class QIFPortfolioPayeeDescLine
            extends QIFStringLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pPayee the payee description
         */
        protected QIFPortfolioPayeeDescLine(final String pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.PAYEE;
        }
    }

    /**
     * The Portfolio Account line.
     */
    public class QIFPortfolioAccountLine
            extends QIFXferAccountLine<QPortfolioLineType> {
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

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.XFERACCOUNT;
        }
    }

    /**
     * The Portfolio Category line.
     */
    public class QIFPortfolioCategoryLine
            extends QIFCategoryLine<QPortfolioLineType> {
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

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.XFERACCOUNT;
        }
    }

    /**
     * The Portfolio Category line.
     */
    public class QIFPortfolioCategoryAccountLine
            extends QIFCategoryAccountLine<QPortfolioLineType> {
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

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.XFERACCOUNT;
        }
    }

    /**
     * The Portfolio Transfer Amount line.
     */
    public class QIFPortfolioXferAmountLine
            extends QIFMoneyLine<QPortfolioLineType> {
        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected QIFPortfolioXferAmountLine(final TethysMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        @Override
        public QPortfolioLineType getLineType() {
            return QPortfolioLineType.XFERAMOUNT;
        }

        /**
         * Obtain Amount.
         * @return the amount
         */
        public TethysMoney getAmount() {
            return getMoney();
        }
    }
}
