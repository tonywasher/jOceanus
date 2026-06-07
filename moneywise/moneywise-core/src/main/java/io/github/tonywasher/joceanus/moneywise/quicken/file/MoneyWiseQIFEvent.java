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
package io.github.tonywasher.joceanus.moneywise.quicken.file;

import io.github.tonywasher.joceanus.moneywise.data.basic.MoneyWiseTransaction;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQEventLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFAccount.MoneyWiseQIFXferAccountLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFEventCategory.MoneyWiseQIFCategoryLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFClearedLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFDateLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFMoneyLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.MoneyWiseQIFPayee.MoneyWiseQIFPayeeLine;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDate;
import io.github.tonywasher.joceanus.oceanus.date.OceanusDateFormatter;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusDecimalParser;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class representing a QIF Event record.
 */
public class MoneyWiseQIFEvent
        extends MoneyWiseQIFEventRecord<MoneyWiseQEventLineType> {
    /**
     * The register.
     */
    private final MoneyWiseQIFRegister theRegister;

    /**
     * The Date.
     */
    private final OceanusDate theDate;

    /**
     * The Cleared Flag.
     */
    private final Boolean isCleared;

    /**
     * Constructor.
     *
     * @param pRegister the QIF register
     * @param pTrans    the transaction
     */
    protected MoneyWiseQIFEvent(final MoneyWiseQIFRegister pRegister,
                                final MoneyWiseTransaction pTrans) {
        /* Call super-constructor */
        super(MoneyWiseQEventLineType.class);

        /* Store values */
        theRegister = pRegister;
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
     *
     * @param pRegister  the QIF Register
     * @param pStartDate the start date
     */
    protected MoneyWiseQIFEvent(final MoneyWiseQIFRegister pRegister,
                                final OceanusDate pStartDate) {
        /* Call super-constructor */
        super(MoneyWiseQEventLineType.class);

        /* Store values */
        theRegister = pRegister;
        theDate = pStartDate;
        isCleared = true;

        /* Add the lines */
        addLine(new MoneyWiseQIFEventDateLine(theDate));
        addLine(new MoneyWiseQIFEventClearedLine(isCleared));
        addLine(new MoneyWiseQIFEventPayeeDescLine("Opening Balance"));
    }

    /**
     * Constructor.
     *
     * @param pRegister  the QIF Register
     * @param pFormatter the Data Formatter
     * @param pLines     the data lines
     */
    protected MoneyWiseQIFEvent(final MoneyWiseQIFRegister pRegister,
                                final OceanusDataFormatter pFormatter,
                                final List<String> pLines) {
        /* Call super-constructor */
        super(MoneyWiseQEventLineType.class);

        /* Determine details */
        theRegister = pRegister;
        OceanusDate myDate = null;
        Boolean myCleared = null;

        /* Current split record */
        MoneyWiseQIFSplitEvent mySplit = null;

        /* Obtain parsers */
        final OceanusDateFormatter myDateParser = pFormatter.getDateFormatter();
        final OceanusDecimalParser myDecParser = pFormatter.getDecimalParser();

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
                        final OceanusDate myDateDay = myDateParser.parseDateBase(myData, MoneyWiseQIFConstants.QIF_BASEYEAR);
                        addLine(new MoneyWiseQIFEventDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case CLEARED:
                        final Boolean myFlag = myData.equals(MoneyWiseQIFLine.QIF_RECONCILED);
                        addLine(new MoneyWiseQIFEventClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case AMOUNT:
                        OceanusMoney myMoney = myDecParser.parseMoneyValue(myData);
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
                        MoneyWiseQIFAccount myAccount = parseAccount(pRegister, myData);
                        MoneyWiseQIFEventCategory myCategory = parseCategory(pRegister, myData);
                        if (myAccount != null) {
                            /* Look for account classes */
                            final List<MoneyWiseQIFClass> myClasses = parseAccountClasses(pRegister, myData);
                            addLine(new MoneyWiseQIFEventAccountLine(myAccount, myClasses));
                        } else {
                            /* Look for category classes */
                            final List<MoneyWiseQIFClass> myClasses = parseCategoryClasses(pRegister, myData);
                            addLine(new MoneyWiseQIFEventCategoryLine(myCategory, myClasses));
                            convertPayee();
                        }
                        break;
                    case SPLITCATEGORY:
                        /* Check for account */
                        myAccount = parseAccount(pRegister, myData);
                        myCategory = parseCategory(pRegister, myData);
                        if (myAccount != null) {
                            /* Look for account classes */
                            final List<MoneyWiseQIFClass> myClasses = parseAccountClasses(pRegister, myData);
                            mySplit = new MoneyWiseQIFSplitEvent(myAccount, myClasses);
                        } else {
                            /* Look for category classes */
                            final List<MoneyWiseQIFClass> myClasses = parseCategoryClasses(pRegister, myData);
                            mySplit = new MoneyWiseQIFSplitEvent(myCategory, myClasses);
                            convertPayee();
                        }

                        /* Record new split record */
                        addRecord(mySplit);
                        break;
                    case SPLITAMOUNT:
                        myMoney = myDecParser.parseMoneyValue(myData);
                        Objects.requireNonNull(mySplit).setSplitAmount(myMoney);
                        break;
                    case SPLITPERCENT:
                        final OceanusRate myRate = myDecParser.parseRateValue(myData);
                        Objects.requireNonNull(mySplit).setSplitPercentage(myRate);
                        break;
                    case SPLITCOMMENT:
                        Objects.requireNonNull(mySplit).setSplitComment(myData);
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
    public OceanusDate getDate() {
        return theDate;
    }

    @Override
    public Boolean isCleared() {
        return isCleared;
    }

    /**
     * Parse account line.
     *
     * @param pRegister the QIF Register
     * @param pLine     the line.
     * @return the account name (or null)
     */
    static MoneyWiseQIFAccount parseAccount(final MoneyWiseQIFRegister pRegister,
                                            final String pLine) {
        /* Determine line to use */
        String myLine = pLine;

        /* If the line contains a category separator */
        if (pLine.contains(MoneyWiseQIFConstants.QIF_CATSEP)) {
            /* Move to data following separator */
            final int i = pLine.indexOf(MoneyWiseQIFConstants.QIF_CATSEP);
            myLine = pLine.substring(i + 1);
        }

        /* If the line contains classes */
        if (myLine.contains(MoneyWiseQIFConstants.QIF_CLASS)) {
            /* drop class data */
            final int i = myLine.indexOf(MoneyWiseQIFConstants.QIF_CLASS);
            myLine = myLine.substring(0, i);
        }

        /* If we have the account delimiters */
        if (myLine.startsWith(MoneyWiseQIFConstants.QIF_XFERSTART)
                && myLine.endsWith(MoneyWiseQIFConstants.QIF_XFEREND)) {
            /* Remove account delimiters */
            final int i = MoneyWiseQIFConstants.QIF_XFERSTART.length();
            final int j = MoneyWiseQIFConstants.QIF_XFEREND.length();
            final String myAccount = myLine.substring(i, myLine.length()
                    - j);
            return pRegister.getAccount(myAccount);
        }

        /* Return no account */
        return null;
    }

    /**
     * Parse account classes.
     *
     * @param pRegister the QIF Register
     * @param pLine     the line.
     * @return the account name (or null)
     */
    static List<MoneyWiseQIFClass> parseAccountClasses(final MoneyWiseQIFRegister pRegister,
                                                       final String pLine) {
        /* Determine line to use */
        String myLine = pLine;

        /* If the line contains a category separator */
        if (pLine.contains(MoneyWiseQIFConstants.QIF_CATSEP)) {
            /* Move to data following separator */
            final int i = pLine.indexOf(MoneyWiseQIFConstants.QIF_CATSEP);
            myLine = pLine.substring(i + 1);
        }

        /* If the line contains classes */
        if (myLine.contains(MoneyWiseQIFConstants.QIF_CLASS)) {
            /* drop preceding data */
            final int i = myLine.indexOf(MoneyWiseQIFConstants.QIF_CLASS);
            myLine = myLine.substring(i + 1);

            /* Build list of classes */
            final String[] myClasses = myLine.split(MoneyWiseQIFConstants.QIF_CLASSSEP);
            final List<MoneyWiseQIFClass> myList = new ArrayList<>();
            for (String myClass : myClasses) {
                myList.add(pRegister.getClass(myClass));
            }

            /* Return the classes */
            return myList;
        }

        /* Return no classes */
        return null;
    }


    /**
     * Parse category line.
     *
     * @param pRegister the QIF Register
     * @param pLine     the line.
     * @return the account name (or null)
     */
    static MoneyWiseQIFEventCategory parseCategory(final MoneyWiseQIFRegister pRegister,
                                                   final String pLine) {
        /* Determine line to use */
        String myLine = pLine;

        /* If the line contains a category separator */
        if (pLine.contains(MoneyWiseQIFConstants.QIF_CATSEP)) {
            /* Drop data after separator */
            final int i = pLine.indexOf(MoneyWiseQIFConstants.QIF_CATSEP);
            myLine = pLine.substring(0, i);
        }

        /* If the line contains classes */
        if (myLine.contains(MoneyWiseQIFConstants.QIF_CLASS)) {
            /* drop class data */
            final int i = myLine.indexOf(MoneyWiseQIFConstants.QIF_CLASS);
            myLine = myLine.substring(0, i);
        }

        /* If we have the account delimiters */
        if ((myLine.startsWith(MoneyWiseQIFConstants.QIF_XFERSTART))
                && (myLine.endsWith(MoneyWiseQIFConstants.QIF_XFEREND))) {
            /* This is an account */
            return null;
        }

        /* Return category */
        return pRegister.getCategory(myLine);
    }

    /**
     * Parse category classes.
     *
     * @param pRegister the QIF Register
     * @param pLine     the line.
     * @return the account name (or null)
     */
    static List<MoneyWiseQIFClass> parseCategoryClasses(final MoneyWiseQIFRegister pRegister,
                                                        final String pLine) {
        /* Determine line to use */
        String myLine = pLine;

        /* If the line contains a category separator */
        if (pLine.contains(MoneyWiseQIFConstants.QIF_CATSEP)) {
            /* Drop data after separator */
            final int i = pLine.indexOf(MoneyWiseQIFConstants.QIF_CATSEP);
            myLine = pLine.substring(0, i);
        }

        /* If the line contains classes */
        if (myLine.contains(MoneyWiseQIFConstants.QIF_CLASS)) {
            /* drop preceding data */
            final int i = myLine.indexOf(MoneyWiseQIFConstants.QIF_CLASS);
            myLine = myLine.substring(i + 1);

            /* Build list of classes */
            final String[] myClasses = myLine.split(MoneyWiseQIFConstants.QIF_CLASSSEP);
            final List<MoneyWiseQIFClass> myList = new ArrayList<>();
            for (String myClass : myClasses) {
                myList.add(pRegister.getClass(myClass));
            }

            /* Return the classes */
            return myList;
        }

        /* Return no classes */
        return null;
    }

    /**
     * record reference.
     *
     * @param pReference the reference
     */
    private void recordReference(final String pReference) {
        /* Add reference line */
        addLine(new MoneyWiseQIFEventReferenceLine(pReference));
    }

    /**
     * record comment.
     *
     * @param pComment the comment
     */
    private void recordComment(final String pComment) {
        /* Add comment line */
        addLine(new MoneyWiseQIFEventCommentLine(pComment));
    }

    /**
     * record payee.
     *
     * @param pPayee the payee
     */
    protected void recordPayee(final MoneyWiseQIFPayee pPayee) {
        /* Add payee line */
        addLine(new MoneyWiseQIFEventPayeeLine(pPayee));
    }

    /**
     * record payee description.
     *
     * @param pPayeeDesc the payee description
     */
    protected void recordPayee(final String pPayeeDesc) {
        /* Add payee line */
        addLine(new MoneyWiseQIFEventPayeeDescLine(pPayeeDesc));
    }

    /**
     * record amount.
     *
     * @param pAmount the amount
     */
    protected void recordAmount(final OceanusMoney pAmount) {
        /* Add amount line */
        addLine(new MoneyWiseQIFEventAmountLine(pAmount));
    }

    /**
     * record transfer account.
     *
     * @param pAccount the account
     */
    protected void recordAccount(final MoneyWiseQIFAccount pAccount) {
        /* Add account line */
        addLine(new MoneyWiseQIFEventAccountLine(pAccount));
    }

    /**
     * record transfer account.
     *
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
     *
     * @param pCategory the category
     */
    protected void recordCategory(final MoneyWiseQIFEventCategory pCategory) {
        /* Add category line */
        addLine(new MoneyWiseQIFEventCategoryLine(pCategory));
    }

    /**
     * record category.
     *
     * @param pCategory the category
     * @param pClasses  the classes
     */
    protected void recordCategory(final MoneyWiseQIFEventCategory pCategory,
                                  final List<MoneyWiseQIFClass> pClasses) {
        /* Add category line */
        addLine(new MoneyWiseQIFEventCategoryLine(pCategory, pClasses));
    }

    /**
     * record new Split record for transfer.
     *
     * @param pAccount the account
     * @param pAmount  the amount
     * @param pComment the comment
     */
    protected void recordSplitRecord(final MoneyWiseQIFAccount pAccount,
                                     final OceanusMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseQIFSplitEvent mySplit = new MoneyWiseQIFSplitEvent(pAccount);
        mySplit.setSplitAmount(pAmount);
        if (pComment != null) {
            mySplit.setSplitComment(pComment);
        }
        addRecord(mySplit);
    }

    /**
     * record new Split record for transfer.
     *
     * @param pAccount the account
     * @param pClasses the classes
     * @param pAmount  the amount
     * @param pComment the comment
     */
    protected void recordSplitRecord(final MoneyWiseQIFAccount pAccount,
                                     final List<MoneyWiseQIFClass> pClasses,
                                     final OceanusMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseQIFSplitEvent mySplit = new MoneyWiseQIFSplitEvent(pAccount, pClasses);
        mySplit.setSplitAmount(pAmount);
        if (pComment != null) {
            mySplit.setSplitComment(pComment);
        }
        addRecord(mySplit);
    }

    /**
     * record new Split record for category.
     *
     * @param pCategory the category
     * @param pAmount   the amount
     * @param pComment  the comment
     */
    protected void recordSplitRecord(final MoneyWiseQIFEventCategory pCategory,
                                     final OceanusMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseQIFSplitEvent mySplit = new MoneyWiseQIFSplitEvent(pCategory);
        mySplit.setSplitAmount(pAmount);
        if (pComment != null) {
            mySplit.setSplitComment(pComment);
        }
        addRecord(mySplit);
    }

    /**
     * record new Split record for category.
     *
     * @param pCategory the category
     * @param pClasses  the classes
     * @param pAmount   the amount
     * @param pComment  the comment
     */
    protected void recordSplitRecord(final MoneyWiseQIFEventCategory pCategory,
                                     final List<MoneyWiseQIFClass> pClasses,
                                     final OceanusMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseQIFSplitEvent mySplit = new MoneyWiseQIFSplitEvent(pCategory, pClasses);
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
        if (myLine instanceof MoneyWiseQIFEventPayeeDescLine myDesc) {
            /* Access payee */
            final String myName = myDesc.getValue();

            /* Register the payee */
            final MoneyWiseQIFPayee myPayee = theRegister.registerPayee(myName);
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
         *
         * @param pDate the Date
         */
        protected MoneyWiseQIFEventDateLine(final OceanusDate pDate) {
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
         *
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
         *
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
         *
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
         *
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
         *
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
         *
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
         *
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
         *
         * @param pAmount the amount
         */
        protected MoneyWiseQIFEventAmountLine(final OceanusMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.AMOUNT;
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
     * The Event Account line.
     */
    public static class MoneyWiseQIFEventAccountLine
            extends MoneyWiseQIFXferAccountLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pAccount the account
         */
        protected MoneyWiseQIFEventAccountLine(final MoneyWiseQIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }

        /**
         * Constructor.
         *
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
         *
         * @param pCategory the category
         */
        protected MoneyWiseQIFEventCategoryLine(final MoneyWiseQIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         *
         * @param pCategory the category
         * @param pClasses  the classes
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
