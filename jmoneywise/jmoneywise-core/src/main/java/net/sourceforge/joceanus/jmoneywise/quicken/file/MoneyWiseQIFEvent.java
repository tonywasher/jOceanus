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
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.MoneyWiseQEventLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFCategoryLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFClearedLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFDateLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFPayeeLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFXferAccountLine;
import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.date.TethysDateFormatter;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;
import net.sourceforge.joceanus.jtethys.ui.api.base.TethysUIDataFormatter;

/**
 * Class representing a QIF Event record.
 */
public class MoneyWiseQIFEvent
        extends MoneyWiseQIFEventRecord<MoneyWiseQEventLineType> {
    /**
     * The Date.
     */
    private final TethysDate theDate;

    /**
     * The Cleared Flag.
     */
    private final Boolean isCleared;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pTrans the transaction
     */
    protected MoneyWiseQIFEvent(final MoneyWiseQIFFile pFile,
                                final MoneyWiseTransaction pTrans) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQEventLineType.class);

        /* Store values */
        theDate = pTrans.getDate();
        isCleared = pTrans.isReconciled();

        /* Add the lines */
        addLine(new MoneyWiseQIFEventDateLine(theDate));
        addLine(new MoneyWiseQIFEventClearedLine(isCleared));

        /* Add the reference line if it exists */
        final String myRef = pTrans.getReference();
        if (myRef != null) {
            recordReference(myRef);
        }

        /* Add the comment line if it exists */
        final String myComment = pTrans.getComments();
        if (myComment != null) {
            recordComment(myComment);
        }
    }

    /**
     * Constructor for opening balance.
     * @param pFile the QIF File
     * @param pStartDate the start date
     */
    protected MoneyWiseQIFEvent(final MoneyWiseQIFFile pFile,
                                final TethysDate pStartDate) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQEventLineType.class);

        /* Store values */
        theDate = pStartDate;
        isCleared = true;

        /* Add the lines */
        addLine(new MoneyWiseQIFEventDateLine(theDate));
        addLine(new MoneyWiseQIFEventClearedLine(isCleared));
        addLine(new MoneyWiseQIFEventPayeeDescLine("Opening Balance"));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the Data Formatter
     * @param pLines the data lines
     */
    protected MoneyWiseQIFEvent(final MoneyWiseQIFFile pFile,
                                final TethysUIDataFormatter pFormatter,
                                final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, MoneyWiseQEventLineType.class);

        /* Determine details */
        TethysDate myDate = null;
        Boolean myCleared = null;

        /* Current split record */
        MoneyWiseQIFSplitEvent mySplit = null;

        /* Obtain parsers */
        final TethysDateFormatter myDateParser = pFormatter.getDateFormatter();
        final TethysDecimalParser myDecParser = pFormatter.getDecimalParser();

        /* Loop through the lines */
        for (String myLine : pLines) {
            /* Determine the category */
            final MoneyWiseQEventLineType myType = MoneyWiseQEventLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                final String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case DATE:
                        final TethysDate myDateDay = myDateParser.parseDateBase(myData, MoneyWiseQIFWriter.QIF_BASEYEAR);
                        addLine(new MoneyWiseQIFEventDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case CLEARED:
                        final Boolean myFlag = myData.equals(MoneyWiseQIFLine.QIF_RECONCILED);
                        addLine(new MoneyWiseQIFEventClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case AMOUNT:
                        TethysMoney myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new MoneyWiseQIFEventAmountLine(myMoney));
                        break;
                    case COMMENT:
                        addLine(new MoneyWiseQIFEventCommentLine(myData));
                        break;
                    case REFERENCE:
                        addLine(new MoneyWiseQIFEventReferenceLine(myData));
                        break;
                    case PAYEE:
                        addLine(new MoneyWiseQIFEventPayeeDescLine(myData));
                        break;
                    case CATEGORY:
                        /* Check for account and category */
                        MoneyWiseQIFAccount myAccount = MoneyWiseQIFXferAccountLine.parseAccount(pFile, myData);
                        MoneyWiseQIFEventCategory myCategory = MoneyWiseQIFEventCategoryLine.parseCategory(pFile, myData);
                        if (myAccount != null) {
                            /* Look for account classes */
                            final List<MoneyWiseQIFClass> myClasses = MoneyWiseQIFXferAccountLine.parseAccountClasses(pFile, myData);
                            addLine(new MoneyWiseQIFEventAccountLine(myAccount, myClasses));
                        } else {
                            /* Look for category classes */
                            final List<MoneyWiseQIFClass> myClasses = MoneyWiseQIFEventCategoryLine.parseCategoryClasses(pFile, myData);
                            addLine(new MoneyWiseQIFEventCategoryLine(myCategory, myClasses));
                            convertPayee();
                        }
                        break;
                    case SPLITCATEGORY:
                        /* Check for account */
                        myAccount = MoneyWiseQIFXferAccountLine.parseAccount(pFile, myData);
                        myCategory = MoneyWiseQIFEventCategoryLine.parseCategory(pFile, myData);
                        if (myAccount != null) {
                            /* Look for account classes */
                            final List<MoneyWiseQIFClass> myClasses = MoneyWiseQIFXferAccountLine.parseAccountClasses(pFile, myData);
                            mySplit = new MoneyWiseQIFSplitEvent(pFile, myAccount, myClasses);
                        } else {
                            /* Look for category classes */
                            final List<MoneyWiseQIFClass> myClasses = MoneyWiseQIFEventCategoryLine.parseCategoryClasses(pFile, myData);
                            mySplit = new MoneyWiseQIFSplitEvent(pFile, myCategory, myClasses);
                            convertPayee();
                        }

                        /* Record new split record */
                        addRecord(mySplit);
                        break;
                    case SPLITAMOUNT:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        mySplit.setSplitAmount(myMoney);
                        break;
                    case SPLITPERCENT:
                        final TethysRate myRate = myDecParser.parseRateValue(myData);
                        mySplit.setSplitPercentage(myRate);
                        break;
                    case SPLITCOMMENT:
                        mySplit.setSplitComment(myData);
                        break;
                    default:
                        break;
                }
            }
        }

        /* Build details */
        theDate = myDate;
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
     * record reference.
     * @param pReference the reference
     */
    private void recordReference(final String pReference) {
        /* Add reference line */
        addLine(new MoneyWiseQIFEventReferenceLine(pReference));
    }

    /**
     * record comment.
     * @param pComment the comment
     */
    private void recordComment(final String pComment) {
        /* Add comment line */
        addLine(new MoneyWiseQIFEventCommentLine(pComment));
    }

    /**
     * record payee.
     * @param pPayee the payee
     */
    protected void recordPayee(final MoneyWiseQIFPayee pPayee) {
        /* Add payee line */
        addLine(new MoneyWiseQIFEventPayeeLine(pPayee));
    }

    /**
     * record payee description.
     * @param pPayeeDesc the payee description
     */
    protected void recordPayee(final String pPayeeDesc) {
        /* Add payee line */
        addLine(new MoneyWiseQIFEventPayeeDescLine(pPayeeDesc));
    }

    /**
     * record amount.
     * @param pAmount the amount
     */
    protected void recordAmount(final TethysMoney pAmount) {
        /* Add amount line */
        addLine(new MoneyWiseQIFEventAmountLine(pAmount));
    }

    /**
     * record transfer account.
     * @param pAccount the account
     */
    protected void recordAccount(final MoneyWiseQIFAccount pAccount) {
        /* Add account line */
        addLine(new MoneyWiseQIFEventAccountLine(pAccount));
    }

    /**
     * record transfer account.
     * @param pAccount the account
     * @param pClasses the classes
     */
    protected void recordAccount(final MoneyWiseQIFAccount pAccount,
                                 final List<MoneyWiseQIFClass> pClasses) {
        /* Add account line */
        addLine(new MoneyWiseQIFEventAccountLine(pAccount, pClasses));
    }

    /**
     * record category.
     * @param pCategory the category
     */
    protected void recordCategory(final MoneyWiseQIFEventCategory pCategory) {
        /* Add category line */
        addLine(new MoneyWiseQIFEventCategoryLine(pCategory));
    }

    /**
     * record category.
     * @param pCategory the category
     * @param pClasses the classes
     */
    protected void recordCategory(final MoneyWiseQIFEventCategory pCategory,
                                  final List<MoneyWiseQIFClass> pClasses) {
        /* Add category line */
        addLine(new MoneyWiseQIFEventCategoryLine(pCategory, pClasses));
    }

    /**
     * record new Split record for transfer.
     * @param pAccount the account
     * @param pAmount the amount
     * @param pComment the comment
     */
    protected void recordSplitRecord(final MoneyWiseQIFAccount pAccount,
                                     final TethysMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseQIFSplitEvent mySplit = new MoneyWiseQIFSplitEvent(getFile(), pAccount);
        mySplit.setSplitAmount(pAmount);
        if (pComment != null) {
            mySplit.setSplitComment(pComment);
        }
        addRecord(mySplit);
    }

    /**
     * record new Split record for transfer.
     * @param pAccount the account
     * @param pClasses the classes
     * @param pAmount the amount
     * @param pComment the comment
     */
    protected void recordSplitRecord(final MoneyWiseQIFAccount pAccount,
                                     final List<MoneyWiseQIFClass> pClasses,
                                     final TethysMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseQIFSplitEvent mySplit = new MoneyWiseQIFSplitEvent(getFile(), pAccount, pClasses);
        mySplit.setSplitAmount(pAmount);
        if (pComment != null) {
            mySplit.setSplitComment(pComment);
        }
        addRecord(mySplit);
    }

    /**
     * record new Split record for category.
     * @param pCategory the category
     * @param pAmount the amount
     * @param pComment the comment
     */
    protected void recordSplitRecord(final MoneyWiseQIFEventCategory pCategory,
                                     final TethysMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseQIFSplitEvent mySplit = new MoneyWiseQIFSplitEvent(getFile(), pCategory);
        mySplit.setSplitAmount(pAmount);
        if (pComment != null) {
            mySplit.setSplitComment(pComment);
        }
        addRecord(mySplit);
    }

    /**
     * record new Split record for category.
     * @param pCategory the category
     * @param pClasses the classes
     * @param pAmount the amount
     * @param pComment the comment
     */
    protected void recordSplitRecord(final MoneyWiseQIFEventCategory pCategory,
                                     final List<MoneyWiseQIFClass> pClasses,
                                     final TethysMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseQIFSplitEvent mySplit = new MoneyWiseQIFSplitEvent(getFile(), pCategory, pClasses);
        mySplit.setSplitAmount(pAmount);
        if (pComment != null) {
            mySplit.setSplitComment(pComment);
        }
        addRecord(mySplit);
    }

    /**
     * Convert Payee.
     */
    private void convertPayee() {
        /* Look for a payee line */
        final MoneyWiseQIFLine<MoneyWiseQEventLineType> myLine = getLine(MoneyWiseQEventLineType.PAYEE);
        if (myLine instanceof MoneyWiseQIFEventPayeeDescLine) {
            /* Access payee */
            final MoneyWiseQIFEventPayeeDescLine myDesc = (MoneyWiseQIFEventPayeeDescLine) myLine;
            final String myName = myDesc.getValue();

            /* Register the payee */
            final MoneyWiseQIFPayee myPayee = getFile().registerPayee(myName);
            addLine(new MoneyWiseQIFEventPayeeLine(myPayee));
        }
    }

    /**
     * The Event Date line.
     */
    public static class MoneyWiseQIFEventDateLine
            extends MoneyWiseQIFDateLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pDate the Date
         */
        protected MoneyWiseQIFEventDateLine(final TethysDate pDate) {
            /* Call super-constructor */
            super(pDate);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.DATE;
        }
    }

    /**
     * The Event Reference line.
     */
    public static class MoneyWiseQIFEventReferenceLine
            extends MoneyWiseQIFStringLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pRef the Reference
         */
        protected MoneyWiseQIFEventReferenceLine(final String pRef) {
            /* Call super-constructor */
            super(pRef);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.REFERENCE;
        }

        /**
         * Obtain Reference.
         * @return the reference
         */
        public String getReference() {
            return getValue();
        }
    }

    /**
     * The Event Comment line.
     */
    public static class MoneyWiseQIFEventCommentLine
            extends MoneyWiseQIFStringLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pComment the comment
         */
        protected MoneyWiseQIFEventCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.COMMENT;
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
     * The Event Cleared line.
     */
    public static class MoneyWiseQIFEventClearedLine
            extends MoneyWiseQIFClearedLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pCleared is the event cleared?
         */
        protected MoneyWiseQIFEventClearedLine(final Boolean pCleared) {
            /* Call super-constructor */
            super(pCleared);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.CLEARED;
        }
    }

    /**
     * The Event Payee Account line.
     */
    public static class MoneyWiseQIFEventPayeeLine
            extends MoneyWiseQIFPayeeLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pPayee the payee
         */
        protected MoneyWiseQIFEventPayeeLine(final MoneyWiseQIFPayee pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.PAYEE;
        }
    }

    /**
     * The Event Payee Description line.
     */
    public static class MoneyWiseQIFEventPayeeDescLine
            extends MoneyWiseQIFStringLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pPayee the payee description
         */
        protected MoneyWiseQIFEventPayeeDescLine(final String pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.PAYEE;
        }
    }

    /**
     * The Event Amount line.
     */
    public static class MoneyWiseQIFEventAmountLine
            extends MoneyWiseQIFMoneyLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected MoneyWiseQIFEventAmountLine(final TethysMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.AMOUNT;
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
     * The Event Account line.
     */
    public static class MoneyWiseQIFEventAccountLine
            extends MoneyWiseQIFXferAccountLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pAccount the account
         */
        protected MoneyWiseQIFEventAccountLine(final MoneyWiseQIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }

        /**
         * Constructor.
         * @param pAccount the account
         * @param pClasses the classes
         */
        protected MoneyWiseQIFEventAccountLine(final MoneyWiseQIFAccount pAccount,
                                               final List<MoneyWiseQIFClass> pClasses) {
            /* Call super-constructor */
            super(pAccount, pClasses);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.CATEGORY;
        }
    }

    /**
     * The Event Category line.
     */
    public static class MoneyWiseQIFEventCategoryLine
            extends MoneyWiseQIFCategoryLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pCategory the category
         */
        protected MoneyWiseQIFEventCategoryLine(final MoneyWiseQIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         * @param pCategory the category
         * @param pClasses the classes
         */
        protected MoneyWiseQIFEventCategoryLine(final MoneyWiseQIFEventCategory pCategory,
                                                final List<MoneyWiseQIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pClasses);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.CATEGORY;
        }
    }
}
