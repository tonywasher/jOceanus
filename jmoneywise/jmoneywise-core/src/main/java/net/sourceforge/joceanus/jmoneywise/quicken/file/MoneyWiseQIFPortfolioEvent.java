/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.List;

import net.sourceforge.joceanus.jmoneywise.data.basic.MoneyWiseTransaction;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.MoneyWiseQActionType;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.MoneyWiseQPortfolioLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFCategoryAccountLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFCategoryLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFClearedLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFDateLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFPayeeLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFPriceLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFRatioLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFSecurityLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFUnitsLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFXferAccountLine;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysPrice;
import net.sourceforge.joceanus.jtethys.decimal.TethysRatio;
import net.sourceforge.joceanus.jtethys.decimal.TethysUnits;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class representing a QIF Portfolio Event record.
 */
public class MoneyWiseQIFPortfolioEvent
        extends MoneyWiseQIFEventRecord<MoneyWiseQPortfolioLineType> {
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
    private final MoneyWiseQActionType theAction;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pTrans the transaction
     * @param pAction the action
     */
    protected MoneyWiseQIFPortfolioEvent(final MoneyWiseQIFFile pFile,
                                         final MoneyWiseTransaction pTrans,
                                         final MoneyWiseQActionType pAction) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQPortfolioLineType.class);

        /* Store values */
        theDate = pTrans.getDate();
        isCleared = pTrans.isReconciled();
        theAction = pAction;

        /* Add the lines */
        addLine(new MoneyWiseQIFPortfolioDateLine(theDate));
        addLine(new MoneyWiseQIFPortfolioActionLine(theAction));
        addLine(new MoneyWiseQIFPortfolioClearedLine(isCleared));

        /* Add the comment line if it exists */
        final String myComment = pTrans.getComments();
        if (myComment != null) {
            addLine(new MoneyWiseQIFPortfolioCommentLine(myComment));
        }
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the Data Formatter
     * @param pLines the data lines
     */
    protected MoneyWiseQIFPortfolioEvent(final MoneyWiseQIFFile pFile,
                                         final TethysUIDataFormatter pFormatter,
                                         final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQPortfolioLineType.class);

        /* Determine details */
        TethysDate myDate = null;
        MoneyWiseQActionType myAction = null;
        Boolean myCleared = null;

        /* Obtain parsers */
        final TethysDateFormatter myDateParser = pFormatter.getDateFormatter();
        final TethysDecimalParser myDecParser = pFormatter.getDecimalParser();

        /* Loop through the lines */
        for (String myLine : pLines) {
            /* Determine the category */
            final MoneyWiseQPortfolioLineType myType = MoneyWiseQPortfolioLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case DATE:
                        final TethysDate myDateDay = myDateParser.parseDateBase(myData, MoneyWiseQIFWriter.QIF_BASEYEAR);
                        addLine(new MoneyWiseQIFPortfolioDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case CLEARED:
                        final Boolean myFlag = myData.equals(MoneyWiseQIFLine.QIF_RECONCILED);
                        addLine(new MoneyWiseQIFPortfolioClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case AMOUNT:
                        TethysMoney myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new MoneyWiseQIFPortfolioAmountLine(myMoney));
                        break;
                    case COMMENT:
                        addLine(new MoneyWiseQIFPortfolioCommentLine(myData));
                        break;
                    case ACTION:
                        myAction = MoneyWiseQActionType.parseLine(myData);
                        addLine(new MoneyWiseQIFPortfolioActionLine(myAction));
                        break;
                    case PRICE:
                        final TethysPrice myPrice = myDecParser.parsePriceValue(myData);
                        addLine(new MoneyWiseQIFPortfolioPriceLine(myPrice));
                        break;
                    case COMMISSION:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new MoneyWiseQIFPortfolioCommissionLine(myMoney));
                        break;
                    case PAYEE:
                        addLine(new MoneyWiseQIFPortfolioPayeeDescLine(myData));
                        break;
                    case QUANTITY:
                        final TethysUnits myUnits = myDecParser.parseUnitsValue(myData);
                        addLine(new MoneyWiseQIFPortfolioQuantityLine(myUnits));
                        break;
                    case SECURITY:
                        addLine(new MoneyWiseQIFPortfolioSecurityLine(pFile.getSecurity(myData)));
                        break;
                    case XFERACCOUNT:
                        /* Look for account, category and classes */
                        final MoneyWiseQIFAccount myAccount = MoneyWiseQIFXferAccountLine.parseAccount(pFile, myData);
                        final MoneyWiseQIFEventCategory myCategory = MoneyWiseQIFCategoryLine.parseCategory(pFile, myData);
                        List<MoneyWiseQIFClass> myClasses = MoneyWiseQIFXferAccountLine.parseAccountClasses(pFile, myData);
                        if (myAccount == null) {
                            myClasses = MoneyWiseQIFCategoryLine.parseCategoryClasses(pFile, myData);
                            addLine(new MoneyWiseQIFPortfolioCategoryLine(myCategory, myClasses));
                            convertPayee();
                        } else if (myCategory == null) {
                            addLine(new MoneyWiseQIFPortfolioAccountLine(myAccount, myClasses));
                        } else {
                            addLine(new MoneyWiseQIFPortfolioCategoryAccountLine(myCategory, myAccount, myClasses));
                            convertPayee();
                        }
                        break;
                    case XFERAMOUNT:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new MoneyWiseQIFPortfolioXferAmountLine(myMoney));
                        break;
                    default:
                        break;
                }
            }
        }

        /* Convert any split */
        if (MoneyWiseQActionType.STKSPLIT.equals(myAction)) {
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
    public MoneyWiseQActionType getAction() {
        return theAction;
    }

    /**
     * record security.
     * @param pSecurity the security
     */
    protected void recordSecurity(final MoneyWiseQIFSecurity pSecurity) {
        /* Add security line */
        addLine(new MoneyWiseQIFPortfolioSecurityLine(pSecurity));
    }

    /**
     * record category.
     * @param pCategory the category
     */
    protected void recordCategory(final MoneyWiseQIFEventCategory pCategory) {
        /* Add category line */
        addLine(new MoneyWiseQIFPortfolioCategoryLine(pCategory));
    }

    /**
     * record category.
     * @param pCategory the category
     * @param pClasses the classes
     */
    protected void recordCategory(final MoneyWiseQIFEventCategory pCategory,
                                  final List<MoneyWiseQIFClass> pClasses) {
        /* Add category line */
        addLine(new MoneyWiseQIFPortfolioCategoryLine(pCategory, pClasses));
    }

    /**
     * record amount.
     * @param pAmount the amount
     */
    protected void recordAmount(final TethysMoney pAmount) {
        /* Add amount line */
        addLine(new MoneyWiseQIFPortfolioAmountLine(pAmount));
    }

    /**
     * record payee.
     * @param pPayee the payee
     */
    protected void recordPayee(final String pPayee) {
        /* Add payee line */
        addLine(new MoneyWiseQIFPortfolioPayeeDescLine(pPayee));
    }

    /**
     * record payee.
     * @param pPayee the payee
     */
    protected void recordPayee(final MoneyWiseQIFPayee pPayee) {
        /* Add payee line */
        addLine(new MoneyWiseQIFPortfolioPayeeLine(pPayee));
    }

    /**
     * record transfer.
     * @param pAccount the transfer account
     * @param pAmount the transfer amount
     */
    protected void recordXfer(final MoneyWiseQIFAccount pAccount,
                              final TethysMoney pAmount) {
        /* Add transfer lines */
        addLine(new MoneyWiseQIFPortfolioAccountLine(pAccount));
        addLine(new MoneyWiseQIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record transfer.
     * @param pAccount the transfer account
     * @param pClasses the classes
     * @param pAmount the transfer amount
     */
    protected void recordXfer(final MoneyWiseQIFAccount pAccount,
                              final List<MoneyWiseQIFClass> pClasses,
                              final TethysMoney pAmount) {
        /* Add transfer lines */
        addLine(new MoneyWiseQIFPortfolioAccountLine(pAccount, pClasses));
        addLine(new MoneyWiseQIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record transfer.
     * @param pCategory the transfer category
     * @param pAmount the transfer amount
     */
    protected void recordXfer(final MoneyWiseQIFEventCategory pCategory,
                              final TethysMoney pAmount) {
        /* Add transfer lines */
        addLine(new MoneyWiseQIFPortfolioCategoryLine(pCategory));
        addLine(new MoneyWiseQIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record quantity.
     * @param pQuantity the units quantity
     */
    protected void recordQuantity(final TethysUnits pQuantity) {
        /* Add quantity line */
        addLine(new MoneyWiseQIFPortfolioQuantityLine(pQuantity));
    }

    /**
     * record quantity.
     * @param pRatio the split ratio
     */
    protected void recordQuantity(final TethysRatio pRatio) {
        /* Add quantity line */
        addLine(new MoneyWiseQIFPortfolioSplitRatioLine(pRatio));
    }

    /**
     * record price.
     * @param pPrice the price
     */
    protected void recordPrice(final TethysPrice pPrice) {
        /* Add price line */
        addLine(new MoneyWiseQIFPortfolioPriceLine(pPrice));
    }

    /**
     * record commission.
     * @param pCommission the commission
     */
    protected void recordCommission(final TethysMoney pCommission) {
        /* Add commission line */
        addLine(new MoneyWiseQIFPortfolioCommissionLine(pCommission));
    }

    /**
     * Convert Payee.
     */
    private void convertPayee() {
        /* Look for a payee line */
        final MoneyWiseQIFLine<MoneyWiseQPortfolioLineType> myLine = getLine(MoneyWiseQPortfolioLineType.PAYEE);
        if (myLine instanceof MoneyWiseQIFPortfolioPayeeDescLine) {
            /* Access payee */
            final MoneyWiseQIFPortfolioPayeeDescLine myDesc = (MoneyWiseQIFPortfolioPayeeDescLine) myLine;
            final String myName = myDesc.getValue();

            /* Register the payee */
            final MoneyWiseQIFPayee myPayee = getFile().registerPayee(myName);
            addLine(new MoneyWiseQIFPortfolioPayeeLine(myPayee));
        }
    }

    /**
     * Convert Split.
     */
    private void convertSplit() {
        /* Look for an action line */
        final MoneyWiseQIFLine<MoneyWiseQPortfolioLineType> myLine = getLine(MoneyWiseQPortfolioLineType.QUANTITY);
        if (myLine instanceof MoneyWiseQIFPortfolioQuantityLine) {
            /* Extract action */
            final MoneyWiseQIFPortfolioQuantityLine myQuantity = (MoneyWiseQIFPortfolioQuantityLine) myLine;
            final TethysUnits myUnits = myQuantity.getUnits();

            /* Convert to ratio line */
            final TethysRatio myRatio = new TethysRatio(myUnits.toString());
            addLine(new MoneyWiseQIFPortfolioSplitRatioLine(myRatio));
        }
    }

    /**
     * The Portfolio Date line.
     */
    public static class MoneyWiseQIFPortfolioDateLine
            extends MoneyWiseQIFDateLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pDate the Date
         */
        protected MoneyWiseQIFPortfolioDateLine(final TethysDate pDate) {
            /* Call super-constructor */
            super(pDate);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.DATE;
        }
    }

    /**
     * The Portfolio Comment line.
     */
    public static class MoneyWiseQIFPortfolioCommentLine
            extends MoneyWiseQIFStringLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pComment the comment
         */
        protected MoneyWiseQIFPortfolioCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.COMMENT;
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
    public static class MoneyWiseQIFPortfolioClearedLine
            extends MoneyWiseQIFClearedLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pCleared is the event cleared?
         */
        protected MoneyWiseQIFPortfolioClearedLine(final Boolean pCleared) {
            /* Call super-constructor */
            super(pCleared);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.CLEARED;
        }
    }

    /**
     * The Portfolio Amount line.
     */
    public static class MoneyWiseQIFPortfolioAmountLine
            extends MoneyWiseQIFMoneyLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected MoneyWiseQIFPortfolioAmountLine(final TethysMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.AMOUNT;
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
    public static class MoneyWiseQIFPortfolioCommissionLine
            extends MoneyWiseQIFMoneyLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pCommission the commission
         */
        protected MoneyWiseQIFPortfolioCommissionLine(final TethysMoney pCommission) {
            /* Call super-constructor */
            super(pCommission);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.COMMISSION;
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
    public static class MoneyWiseQIFPortfolioPriceLine
            extends MoneyWiseQIFPriceLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pPrice the price
         */
        protected MoneyWiseQIFPortfolioPriceLine(final TethysPrice pPrice) {
            /* Call super-constructor */
            super(pPrice);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.PRICE;
        }
    }

    /**
     * The Portfolio Quantity line.
     */
    public static class MoneyWiseQIFPortfolioQuantityLine
            extends MoneyWiseQIFUnitsLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pUnits the units
         */
        protected MoneyWiseQIFPortfolioQuantityLine(final TethysUnits pUnits) {
            /* Call super-constructor */
            super(pUnits);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.QUANTITY;
        }
    }

    /**
     * The Portfolio Split Ratio line.
     */
    public static class MoneyWiseQIFPortfolioSplitRatioLine
            extends MoneyWiseQIFRatioLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pRatio the ratio
         */
        protected MoneyWiseQIFPortfolioSplitRatioLine(final TethysRatio pRatio) {
            /* Call super-constructor */
            super(pRatio);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.QUANTITY;
        }
    }

    /**
     * The Portfolio Action line.
     */
    public static class MoneyWiseQIFPortfolioActionLine
            extends MoneyWiseQIFLine<MoneyWiseQPortfolioLineType> {
        /**
         * The action type.
         */
        private final MoneyWiseQActionType theAction;

        /**
         * Constructor.
         * @param pAction the action type
         */
        protected MoneyWiseQIFPortfolioActionLine(final MoneyWiseQActionType pAction) {
            /* Store the data */
            theAction = pAction;
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.ACTION;
        }

        /**
         * Obtain the security.
         * @return the security
         */
        public MoneyWiseQActionType getAction() {
            return theAction;
        }

        @Override
        protected void formatData(final TethysUIDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Add the action */
            pBuilder.append(theAction.getSymbol());
        }
    }

    /**
     * The Portfolio Security line.
     */
    public static class MoneyWiseQIFPortfolioSecurityLine
            extends MoneyWiseQIFSecurityLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pSecurity the security
         */
        protected MoneyWiseQIFPortfolioSecurityLine(final MoneyWiseQIFSecurity pSecurity) {
            /* Call super-constructor */
            super(pSecurity);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.SECURITY;
        }
    }

    /**
     * The Portfolio Payee Account line.
     */
    public static class MoneyWiseQIFPortfolioPayeeLine
            extends MoneyWiseQIFPayeeLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pPayee the payee
         */
        protected MoneyWiseQIFPortfolioPayeeLine(final MoneyWiseQIFPayee pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.PAYEE;
        }
    }

    /**
     * The Portfolio Payee Description line.
     */
    public static class MoneyWiseQIFPortfolioPayeeDescLine
            extends MoneyWiseQIFStringLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pPayee the payee description
         */
        protected MoneyWiseQIFPortfolioPayeeDescLine(final String pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.PAYEE;
        }
    }

    /**
     * The Portfolio Account line.
     */
    public static class MoneyWiseQIFPortfolioAccountLine
            extends MoneyWiseQIFXferAccountLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pAccount the account
         */
        protected MoneyWiseQIFPortfolioAccountLine(final MoneyWiseQIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }

        /**
         * Constructor.
         * @param pAccount the account
         * @param pClasses the account classes
         */
        protected MoneyWiseQIFPortfolioAccountLine(final MoneyWiseQIFAccount pAccount,
                                                   final List<MoneyWiseQIFClass> pClasses) {
            /* Call super-constructor */
            super(pAccount, pClasses);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.XFERACCOUNT;
        }
    }

    /**
     * The Portfolio Category line.
     */
    public static class MoneyWiseQIFPortfolioCategoryLine
            extends MoneyWiseQIFCategoryLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pCategory the category
         */
        protected MoneyWiseQIFPortfolioCategoryLine(final MoneyWiseQIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         * @param pCategory the category
         * @param pClasses the account classes
         */
        protected MoneyWiseQIFPortfolioCategoryLine(final MoneyWiseQIFEventCategory pCategory,
                                                    final List<MoneyWiseQIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pClasses);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.XFERACCOUNT;
        }
    }

    /**
     * The Portfolio Category line.
     */
    public static class MoneyWiseQIFPortfolioCategoryAccountLine
            extends MoneyWiseQIFCategoryAccountLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pCategory the category
         * @param pAccount the account
         */
        protected MoneyWiseQIFPortfolioCategoryAccountLine(final MoneyWiseQIFEventCategory pCategory,
                                                           final MoneyWiseQIFAccount pAccount) {
            /* Call super-constructor */
            super(pCategory, pAccount);
        }

        /**
         * Constructor.
         * @param pCategory the category
         * @param pAccount the account
         * @param pClasses the account classes
         */
        protected MoneyWiseQIFPortfolioCategoryAccountLine(final MoneyWiseQIFEventCategory pCategory,
                                                           final MoneyWiseQIFAccount pAccount,
                                                           final List<MoneyWiseQIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pAccount, pClasses);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.XFERACCOUNT;
        }
    }

    /**
     * The Portfolio Transfer Amount line.
     */
    public static class MoneyWiseQIFPortfolioXferAmountLine
            extends MoneyWiseQIFMoneyLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected MoneyWiseQIFPortfolioXferAmountLine(final TethysMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.XFERAMOUNT;
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
