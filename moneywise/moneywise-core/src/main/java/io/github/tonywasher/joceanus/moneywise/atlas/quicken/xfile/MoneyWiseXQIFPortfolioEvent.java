/*
 * MoneyWise: Finance Application
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile;

import io.github.tonywasher.joceanus.moneywise.atlas.data.analysis.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFAccount.MoneyWiseXQIFXferAccountLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFEventCategory.MoneyWiseXQIFCategoryAccountLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFEventCategory.MoneyWiseXQIFCategoryLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFClearedLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFDateLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFMoneyLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFPriceLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFRatioLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFStringLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFUnitsLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFPayee.MoneyWiseXQIFPayeeLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFSecurity.MoneyWiseXQIFSecurityLine;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQActionType;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQPortfolioLineType;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateFormatter;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimalParser;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusPrice;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRatio;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusUnits;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.List;

/**
 * Class representing a XQIF Portfolio Event record.
 */
public class MoneyWiseXQIFPortfolioEvent
        extends MoneyWiseXQIFEventRecord<MoneyWiseQPortfolioLineType> {
    /**
     * The register.
     */
    private final MoneyWiseXQIFRegister theRegister;

    /**
     * The Date.
     */
    private final OceanusDate theDate;

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
     *
     * @param pRegister the XQIF Register
     * @param pTrans    the transaction
     * @param pAction   the action
     */
    protected MoneyWiseXQIFPortfolioEvent(final MoneyWiseXQIFRegister pRegister,
                                          final MoneyWiseXAnalysisEvent pTrans,
                                          final MoneyWiseQActionType pAction) {
        /* Call super-constructor */
        super(MoneyWiseQPortfolioLineType.class);

        /* Store values */
        theRegister = pRegister;
        theDate = pTrans.getDate();
        isCleared = pTrans.isReconciled();
        theAction = pAction;

        /* Add the lines */
        addLine(new MoneyWiseXQIFPortfolioDateLine(theDate));
        addLine(new MoneyWiseXQIFPortfolioActionLine(theAction));
        addLine(new MoneyWiseXQIFPortfolioClearedLine(isCleared));

        /* Add the comment line if it exists */
        final String myComment = pTrans.getComments();
        if (myComment != null) {
            addLine(new MoneyWiseXQIFPortfolioCommentLine(myComment));
        }
    }

    /**
     * Constructor.
     *
     * @param pRegister  the XQIF register
     * @param pFormatter the Data Formatter
     * @param pLines     the data lines
     */
    protected MoneyWiseXQIFPortfolioEvent(final MoneyWiseXQIFRegister pRegister,
                                          final OceanusDataFormatter pFormatter,
                                          final List<String> pLines) {
        /* Call super-constructor */
        super(MoneyWiseQPortfolioLineType.class);

        /* Determine details */
        theRegister = pRegister;
        OceanusDate myDate = null;
        MoneyWiseQActionType myAction = null;
        Boolean myCleared = null;

        /* Obtain parsers */
        final OceanusDateFormatter myDateParser = pFormatter.getDateFormatter();
        final OceanusDecimalParser myDecParser = pFormatter.getDecimalParser();

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
                        final OceanusDate myDateDay = myDateParser.parseDateBase(myData, MoneyWiseXQIFConstants.XQIF_BASEYEAR);
                        addLine(new MoneyWiseXQIFPortfolioDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case CLEARED:
                        final Boolean myFlag = myData.equals(MoneyWiseXQIFLine.XQIF_RECONCILED);
                        addLine(new MoneyWiseXQIFPortfolioClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case AMOUNT:
                        OceanusMoney myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new MoneyWiseXQIFPortfolioAmountLine(myMoney));
                        break;
                    case COMMENT:
                        addLine(new MoneyWiseXQIFPortfolioCommentLine(myData));
                        break;
                    case ACTION:
                        myAction = MoneyWiseQActionType.parseLine(myData);
                        addLine(new MoneyWiseXQIFPortfolioActionLine(myAction));
                        break;
                    case PRICE:
                        final OceanusPrice myPrice = myDecParser.parsePriceValue(myData);
                        addLine(new MoneyWiseXQIFPortfolioPriceLine(myPrice));
                        break;
                    case COMMISSION:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new MoneyWiseXQIFPortfolioCommissionLine(myMoney));
                        break;
                    case PAYEE:
                        addLine(new MoneyWiseXQIFPortfolioPayeeDescLine(myData));
                        break;
                    case QUANTITY:
                        final OceanusUnits myUnits = myDecParser.parseUnitsValue(myData);
                        addLine(new MoneyWiseXQIFPortfolioQuantityLine(myUnits));
                        break;
                    case SECURITY:
                        addLine(new MoneyWiseXQIFPortfolioSecurityLine(pRegister.getSecurity(myData)));
                        break;
                    case XFERACCOUNT:
                        /* Look for account, category and classes */
                        final MoneyWiseXQIFAccount myAccount = MoneyWiseXQIFEvent.parseAccount(pRegister, myData);
                        final MoneyWiseXQIFEventCategory myCategory = MoneyWiseXQIFEvent.parseCategory(pRegister, myData);
                        List<MoneyWiseXQIFClass> myClasses = MoneyWiseXQIFEvent.parseAccountClasses(pRegister, myData);
                        if (myAccount == null) {
                            myClasses = MoneyWiseXQIFEvent.parseCategoryClasses(pRegister, myData);
                            addLine(new MoneyWiseXQIFPortfolioCategoryLine(myCategory, myClasses));
                            convertPayee();
                        } else if (myCategory == null) {
                            addLine(new MoneyWiseXQIFPortfolioAccountLine(myAccount, myClasses));
                        } else {
                            addLine(new MoneyWiseXQIFPortfolioCategoryAccountLine(myCategory, myAccount, myClasses));
                            convertPayee();
                        }
                        break;
                    case XFERAMOUNT:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new MoneyWiseXQIFPortfolioXferAmountLine(myMoney));
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
    public OceanusDate getDate() {
        return theDate;
    }

    @Override
    public Boolean isCleared() {
        return isCleared;
    }

    /**
     * Obtain the action.
     *
     * @return the action.
     */
    public MoneyWiseQActionType getAction() {
        return theAction;
    }

    /**
     * record security.
     *
     * @param pSecurity the security
     */
    protected void recordSecurity(final MoneyWiseXQIFSecurity pSecurity) {
        /* Add security line */
        addLine(new MoneyWiseXQIFPortfolioSecurityLine(pSecurity));
    }

    /**
     * record category.
     *
     * @param pCategory the category
     */
    protected void recordCategory(final MoneyWiseXQIFEventCategory pCategory) {
        /* Add category line */
        addLine(new MoneyWiseXQIFPortfolioCategoryLine(pCategory));
    }

    /**
     * record category.
     *
     * @param pCategory the category
     * @param pClasses  the classes
     */
    protected void recordCategory(final MoneyWiseXQIFEventCategory pCategory,
                                  final List<MoneyWiseXQIFClass> pClasses) {
        /* Add category line */
        addLine(new MoneyWiseXQIFPortfolioCategoryLine(pCategory, pClasses));
    }

    /**
     * record amount.
     *
     * @param pAmount the amount
     */
    protected void recordAmount(final OceanusMoney pAmount) {
        /* Add amount line */
        addLine(new MoneyWiseXQIFPortfolioAmountLine(pAmount));
    }

    /**
     * record payee.
     *
     * @param pPayee the payee
     */
    protected void recordPayee(final String pPayee) {
        /* Add payee line */
        addLine(new MoneyWiseXQIFPortfolioPayeeDescLine(pPayee));
    }

    /**
     * record payee.
     *
     * @param pPayee the payee
     */
    protected void recordPayee(final MoneyWiseXQIFPayee pPayee) {
        /* Add payee line */
        addLine(new MoneyWiseXQIFPortfolioPayeeLine(pPayee));
    }

    /**
     * record transfer.
     *
     * @param pAccount the transfer account
     * @param pAmount  the transfer amount
     */
    protected void recordXfer(final MoneyWiseXQIFAccount pAccount,
                              final OceanusMoney pAmount) {
        /* Add transfer lines */
        addLine(new MoneyWiseXQIFPortfolioAccountLine(pAccount));
        addLine(new MoneyWiseXQIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record transfer.
     *
     * @param pAccount the transfer account
     * @param pClasses the classes
     * @param pAmount  the transfer amount
     */
    protected void recordXfer(final MoneyWiseXQIFAccount pAccount,
                              final List<MoneyWiseXQIFClass> pClasses,
                              final OceanusMoney pAmount) {
        /* Add transfer lines */
        addLine(new MoneyWiseXQIFPortfolioAccountLine(pAccount, pClasses));
        addLine(new MoneyWiseXQIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record transfer.
     *
     * @param pCategory the transfer category
     * @param pAmount   the transfer amount
     */
    protected void recordXfer(final MoneyWiseXQIFEventCategory pCategory,
                              final OceanusMoney pAmount) {
        /* Add transfer lines */
        addLine(new MoneyWiseXQIFPortfolioCategoryLine(pCategory));
        addLine(new MoneyWiseXQIFPortfolioXferAmountLine(pAmount));
    }

    /**
     * record quantity.
     *
     * @param pQuantity the units quantity
     */
    protected void recordQuantity(final OceanusUnits pQuantity) {
        /* Add quantity line */
        addLine(new MoneyWiseXQIFPortfolioQuantityLine(pQuantity));
    }

    /**
     * record quantity.
     *
     * @param pRatio the split ratio
     */
    protected void recordQuantity(final OceanusRatio pRatio) {
        /* Add quantity line */
        addLine(new MoneyWiseXQIFPortfolioSplitRatioLine(pRatio));
    }

    /**
     * record price.
     *
     * @param pPrice the price
     */
    protected void recordPrice(final OceanusPrice pPrice) {
        /* Add price line */
        addLine(new MoneyWiseXQIFPortfolioPriceLine(pPrice));
    }

    /**
     * record commission.
     *
     * @param pCommission the commission
     */
    protected void recordCommission(final OceanusMoney pCommission) {
        /* Add commission line */
        addLine(new MoneyWiseXQIFPortfolioCommissionLine(pCommission));
    }

    /**
     * Convert Payee.
     */
    private void convertPayee() {
        /* Look for a payee line */
        final MoneyWiseXQIFLine<MoneyWiseQPortfolioLineType> myLine = getLine(MoneyWiseQPortfolioLineType.PAYEE);
        if (myLine instanceof MoneyWiseXQIFPortfolioPayeeDescLine myDesc) {
            /* Access payee */
            final String myName = myDesc.getValue();

            /* Register the payee */
            final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(myName);
            addLine(new MoneyWiseXQIFPortfolioPayeeLine(myPayee));
        }
    }

    /**
     * Convert Split.
     */
    private void convertSplit() {
        /* Look for an action line */
        final MoneyWiseXQIFLine<MoneyWiseQPortfolioLineType> myLine = getLine(MoneyWiseQPortfolioLineType.QUANTITY);
        if (myLine instanceof MoneyWiseXQIFPortfolioQuantityLine myQuantity) {
            /* Extract action */
            final OceanusUnits myUnits = myQuantity.getUnits();

            /* Convert to ratio line */
            final OceanusRatio myRatio = new OceanusRatio(myUnits);
            addLine(new MoneyWiseXQIFPortfolioSplitRatioLine(myRatio));
        }
    }

    /**
     * The Portfolio Date line.
     */
    public static class MoneyWiseXQIFPortfolioDateLine
            extends MoneyWiseXQIFDateLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pDate the Date
         */
        protected MoneyWiseXQIFPortfolioDateLine(final OceanusDate pDate) {
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
    public static class MoneyWiseXQIFPortfolioCommentLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pComment the comment
         */
        protected MoneyWiseXQIFPortfolioCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.COMMENT;
        }

        /**
         * Obtain Comment.
         *
         * @return the comment
         */
        public String getComment() {
            return getValue();
        }
    }

    /**
     * The Portfolio Cleared line.
     */
    public static class MoneyWiseXQIFPortfolioClearedLine
            extends MoneyWiseXQIFClearedLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pCleared is the event cleared?
         */
        protected MoneyWiseXQIFPortfolioClearedLine(final Boolean pCleared) {
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
    public static class MoneyWiseXQIFPortfolioAmountLine
            extends MoneyWiseXQIFMoneyLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pAmount the amount
         */
        protected MoneyWiseXQIFPortfolioAmountLine(final OceanusMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.AMOUNT;
        }

        /**
         * Obtain Amount.
         *
         * @return the amount
         */
        public OceanusMoney getAmount() {
            return getMoney();
        }
    }

    /**
     * The Portfolio Commission line.
     */
    public static class MoneyWiseXQIFPortfolioCommissionLine
            extends MoneyWiseXQIFMoneyLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pCommission the commission
         */
        protected MoneyWiseXQIFPortfolioCommissionLine(final OceanusMoney pCommission) {
            /* Call super-constructor */
            super(pCommission);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.COMMISSION;
        }

        /**
         * Obtain Commission.
         *
         * @return the commission
         */
        public OceanusMoney getCommission() {
            return getMoney();
        }
    }

    /**
     * The Portfolio Price line.
     */
    public static class MoneyWiseXQIFPortfolioPriceLine
            extends MoneyWiseXQIFPriceLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pPrice the price
         */
        protected MoneyWiseXQIFPortfolioPriceLine(final OceanusPrice pPrice) {
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
    public static class MoneyWiseXQIFPortfolioQuantityLine
            extends MoneyWiseXQIFUnitsLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pUnits the units
         */
        protected MoneyWiseXQIFPortfolioQuantityLine(final OceanusUnits pUnits) {
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
    public static class MoneyWiseXQIFPortfolioSplitRatioLine
            extends MoneyWiseXQIFRatioLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pRatio the ratio
         */
        protected MoneyWiseXQIFPortfolioSplitRatioLine(final OceanusRatio pRatio) {
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
    public static class MoneyWiseXQIFPortfolioActionLine
            extends MoneyWiseXQIFLine<MoneyWiseQPortfolioLineType> {
        /**
         * The action type.
         */
        private final MoneyWiseQActionType theAction;

        /**
         * Constructor.
         *
         * @param pAction the action type
         */
        protected MoneyWiseXQIFPortfolioActionLine(final MoneyWiseQActionType pAction) {
            /* Store the data */
            theAction = pAction;
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.ACTION;
        }

        /**
         * Obtain the security.
         *
         * @return the security
         */
        public MoneyWiseQActionType getAction() {
            return theAction;
        }

        @Override
        protected void formatData(final OceanusDataFormatter pFormatter,
                                  final StringBuilder pBuilder) {
            /* Add the action */
            pBuilder.append(theAction.getSymbol());
        }
    }

    /**
     * The Portfolio Security line.
     */
    public static class MoneyWiseXQIFPortfolioSecurityLine
            extends MoneyWiseXQIFSecurityLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pSecurity the security
         */
        protected MoneyWiseXQIFPortfolioSecurityLine(final MoneyWiseXQIFSecurity pSecurity) {
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
    public static class MoneyWiseXQIFPortfolioPayeeLine
            extends MoneyWiseXQIFPayeeLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pPayee the payee
         */
        protected MoneyWiseXQIFPortfolioPayeeLine(final MoneyWiseXQIFPayee pPayee) {
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
    public static class MoneyWiseXQIFPortfolioPayeeDescLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pPayee the payee description
         */
        protected MoneyWiseXQIFPortfolioPayeeDescLine(final String pPayee) {
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
    public static class MoneyWiseXQIFPortfolioAccountLine
            extends MoneyWiseXQIFXferAccountLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pAccount the account
         */
        protected MoneyWiseXQIFPortfolioAccountLine(final MoneyWiseXQIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }

        /**
         * Constructor.
         *
         * @param pAccount the account
         * @param pClasses the account classes
         */
        protected MoneyWiseXQIFPortfolioAccountLine(final MoneyWiseXQIFAccount pAccount,
                                                    final List<MoneyWiseXQIFClass> pClasses) {
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
    public static class MoneyWiseXQIFPortfolioCategoryLine
            extends MoneyWiseXQIFCategoryLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pCategory the category
         */
        protected MoneyWiseXQIFPortfolioCategoryLine(final MoneyWiseXQIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         *
         * @param pCategory the category
         * @param pClasses  the account classes
         */
        protected MoneyWiseXQIFPortfolioCategoryLine(final MoneyWiseXQIFEventCategory pCategory,
                                                     final List<MoneyWiseXQIFClass> pClasses) {
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
    public static class MoneyWiseXQIFPortfolioCategoryAccountLine
            extends MoneyWiseXQIFCategoryAccountLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pCategory the category
         * @param pAccount  the account
         */
        protected MoneyWiseXQIFPortfolioCategoryAccountLine(final MoneyWiseXQIFEventCategory pCategory,
                                                            final MoneyWiseXQIFAccount pAccount) {
            /* Call super-constructor */
            super(pCategory, pAccount);
        }

        /**
         * Constructor.
         *
         * @param pCategory the category
         * @param pAccount  the account
         * @param pClasses  the account classes
         */
        protected MoneyWiseXQIFPortfolioCategoryAccountLine(final MoneyWiseXQIFEventCategory pCategory,
                                                            final MoneyWiseXQIFAccount pAccount,
                                                            final List<MoneyWiseXQIFClass> pClasses) {
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
    public static class MoneyWiseXQIFPortfolioXferAmountLine
            extends MoneyWiseXQIFMoneyLine<MoneyWiseQPortfolioLineType> {
        /**
         * Constructor.
         *
         * @param pAmount the amount
         */
        protected MoneyWiseXQIFPortfolioXferAmountLine(final OceanusMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        @Override
        public MoneyWiseQPortfolioLineType getLineType() {
            return MoneyWiseQPortfolioLineType.XFERAMOUNT;
        }

        /**
         * Obtain Amount.
         *
         * @return the amount
         */
        public OceanusMoney getAmount() {
            return getMoney();
        }
    }
}
