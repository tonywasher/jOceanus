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
package io.github.tonywasher.joceanus.moneywise.quicken.file.atlas;

import io.github.tonywasher.joceanus.moneywise.analysis.atlas.base.MoneyWiseXAnalysisEvent;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQEventLineType;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFAccount.MoneyWiseXQIFXferAccountLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFEventCategory.MoneyWiseXQIFCategoryLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFLine.MoneyWiseXQIFClearedLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFLine.MoneyWiseXQIFDateLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFLine.MoneyWiseXQIFMoneyLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFLine.MoneyWiseXQIFStringLine;
import io.github.tonywasher.joceanus.moneywise.quicken.file.atlas.MoneyWiseXQIFPayee.MoneyWiseXQIFPayeeLine;
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
 * Class representing a XQIF Event record.
 */
public class MoneyWiseXQIFEvent
        extends MoneyWiseXQIFEventRecord<MoneyWiseQEventLineType> {
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
     * Constructor.
     *
     * @param pRegister the XQIF register
     * @param pTrans    the transaction
     */
    protected MoneyWiseXQIFEvent(final MoneyWiseXQIFRegister pRegister,
                                 final MoneyWiseXAnalysisEvent pTrans) {
        /* Call super-constructor */
        super(MoneyWiseQEventLineType.class);

        /* Store values */
        theRegister = pRegister;
        theDate = pTrans.getDate();
        isCleared = pTrans.isReconciled();

        /* Add the lines */
        addLine(new MoneyWiseXQIFEventDateLine(theDate));
        addLine(new MoneyWiseXQIFEventClearedLine(isCleared));

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
     * @param pRegister  the XQIF Register
     * @param pStartDate the start date
     */
    protected MoneyWiseXQIFEvent(final MoneyWiseXQIFRegister pRegister,
                                 final OceanusDate pStartDate) {
        /* Call super-constructor */
        super(MoneyWiseQEventLineType.class);

        /* Store values */
        theRegister = pRegister;
        theDate = pStartDate;
        isCleared = true;

        /* Add the lines */
        addLine(new MoneyWiseXQIFEventDateLine(theDate));
        addLine(new MoneyWiseXQIFEventClearedLine(isCleared));
        addLine(new MoneyWiseXQIFEventPayeeDescLine("Opening Balance"));
    }

    /**
     * Constructor.
     *
     * @param pRegister  the XQIF Register
     * @param pFormatter the Data Formatter
     * @param pLines     the data lines
     */
    protected MoneyWiseXQIFEvent(final MoneyWiseXQIFRegister pRegister,
                                 final OceanusDataFormatter pFormatter,
                                 final List<String> pLines) {
        /* Call super-constructor */
        super(MoneyWiseQEventLineType.class);

        /* Determine details */
        theRegister = pRegister;
        OceanusDate myDate = null;
        Boolean myCleared = null;

        /* Current split record */
        MoneyWiseXQIFSplitEvent mySplit = null;

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
                        final OceanusDate myDateDay = myDateParser.parseDateBase(myData, MoneyWiseXQIFConstants.XQIF_BASEYEAR);
                        addLine(new MoneyWiseXQIFEventDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case CLEARED:
                        final Boolean myFlag = myData.equals(MoneyWiseXQIFLine.XQIF_RECONCILED);
                        addLine(new MoneyWiseXQIFEventClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case AMOUNT:
                        OceanusMoney myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new MoneyWiseXQIFEventAmountLine(myMoney));
                        break;
                    case COMMENT:
                        addLine(new MoneyWiseXQIFEventCommentLine(myData));
                        break;
                    case REFERENCE:
                        addLine(new MoneyWiseXQIFEventReferenceLine(myData));
                        break;
                    case PAYEE:
                        addLine(new MoneyWiseXQIFEventPayeeDescLine(myData));
                        break;
                    case CATEGORY:
                        /* Check for account and category */
                        MoneyWiseXQIFAccount myAccount = parseAccount(pRegister, myData);
                        MoneyWiseXQIFEventCategory myCategory = parseCategory(pRegister, myData);
                        if (myAccount != null) {
                            /* Look for account classes */
                            final List<MoneyWiseXQIFClass> myClasses = parseAccountClasses(pRegister, myData);
                            addLine(new MoneyWiseXQIFEventAccountLine(myAccount, myClasses));
                        } else {
                            /* Look for category classes */
                            final List<MoneyWiseXQIFClass> myClasses = parseCategoryClasses(pRegister, myData);
                            addLine(new MoneyWiseXQIFEventCategoryLine(myCategory, myClasses));
                            convertPayee();
                        }
                        break;
                    case SPLITCATEGORY:
                        /* Check for account */
                        myAccount = parseAccount(pRegister, myData);
                        myCategory = parseCategory(pRegister, myData);
                        if (myAccount != null) {
                            /* Look for account classes */
                            final List<MoneyWiseXQIFClass> myClasses = parseAccountClasses(pRegister, myData);
                            mySplit = new MoneyWiseXQIFSplitEvent(myAccount, myClasses);
                        } else {
                            /* Look for category classes */
                            final List<MoneyWiseXQIFClass> myClasses = parseCategoryClasses(pRegister, myData);
                            mySplit = new MoneyWiseXQIFSplitEvent(myCategory, myClasses);
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
     * @param pRegister the XQIF Register
     * @param pLine     the line.
     * @return the account name (or null)
     */
    static MoneyWiseXQIFAccount parseAccount(final MoneyWiseXQIFRegister pRegister,
                                             final String pLine) {
        /* Determine line to use */
        String myLine = pLine;

        /* If the line contains a category separator */
        if (pLine.contains(MoneyWiseXQIFConstants.XQIF_CATSEP)) {
            /* Move to data following separator */
            final int i = pLine.indexOf(MoneyWiseXQIFConstants.XQIF_CATSEP);
            myLine = pLine.substring(i + 1);
        }

        /* If the line contains classes */
        if (myLine.contains(MoneyWiseXQIFConstants.XQIF_CLASS)) {
            /* drop class data */
            final int i = myLine.indexOf(MoneyWiseXQIFConstants.XQIF_CLASS);
            myLine = myLine.substring(0, i);
        }

        /* If we have the account delimiters */
        if (myLine.startsWith(MoneyWiseXQIFConstants.XQIF_XFERSTART)
                && myLine.endsWith(MoneyWiseXQIFConstants.XQIF_XFEREND)) {
            /* Remove account delimiters */
            final int i = MoneyWiseXQIFConstants.XQIF_XFERSTART.length();
            final int j = MoneyWiseXQIFConstants.XQIF_XFEREND.length();
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
     * @param pRegister the XQIF Register
     * @param pLine     the line.
     * @return the account name (or null)
     */
    static List<MoneyWiseXQIFClass> parseAccountClasses(final MoneyWiseXQIFRegister pRegister,
                                                        final String pLine) {
        /* Determine line to use */
        String myLine = pLine;

        /* If the line contains a category separator */
        if (pLine.contains(MoneyWiseXQIFConstants.XQIF_CATSEP)) {
            /* Move to data following separator */
            final int i = pLine.indexOf(MoneyWiseXQIFConstants.XQIF_CATSEP);
            myLine = pLine.substring(i + 1);
        }

        /* If the line contains classes */
        if (myLine.contains(MoneyWiseXQIFConstants.XQIF_CLASS)) {
            /* drop preceding data */
            final int i = myLine.indexOf(MoneyWiseXQIFConstants.XQIF_CLASS);
            myLine = myLine.substring(i + 1);

            /* Build list of classes */
            final String[] myClasses = myLine.split(MoneyWiseXQIFConstants.XQIF_CLASSSEP);
            final List<MoneyWiseXQIFClass> myList = new ArrayList<>();
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
     * @param pRegister the XQIF Register
     * @param pLine     the line.
     * @return the account name (or null)
     */
    static MoneyWiseXQIFEventCategory parseCategory(final MoneyWiseXQIFRegister pRegister,
                                                    final String pLine) {
        /* Determine line to use */
        String myLine = pLine;

        /* If the line contains a category separator */
        if (pLine.contains(MoneyWiseXQIFConstants.XQIF_CATSEP)) {
            /* Drop data after separator */
            final int i = pLine.indexOf(MoneyWiseXQIFConstants.XQIF_CATSEP);
            myLine = pLine.substring(0, i);
        }

        /* If the line contains classes */
        if (myLine.contains(MoneyWiseXQIFConstants.XQIF_CLASS)) {
            /* drop class data */
            final int i = myLine.indexOf(MoneyWiseXQIFConstants.XQIF_CLASS);
            myLine = myLine.substring(0, i);
        }

        /* If we have the account delimiters */
        if ((myLine.startsWith(MoneyWiseXQIFConstants.XQIF_XFERSTART))
                && (myLine.endsWith(MoneyWiseXQIFConstants.XQIF_XFEREND))) {
            /* This is an account */
            return null;
        }

        /* Return category */
        return pRegister.getCategory(myLine);
    }

    /**
     * Parse category classes.
     *
     * @param pRegister the XQIF Register
     * @param pLine     the line.
     * @return the account name (or null)
     */
    static List<MoneyWiseXQIFClass> parseCategoryClasses(final MoneyWiseXQIFRegister pRegister,
                                                         final String pLine) {
        /* Determine line to use */
        String myLine = pLine;

        /* If the line contains a category separator */
        if (pLine.contains(MoneyWiseXQIFConstants.XQIF_CATSEP)) {
            /* Drop data after separator */
            final int i = pLine.indexOf(MoneyWiseXQIFConstants.XQIF_CATSEP);
            myLine = pLine.substring(0, i);
        }

        /* If the line contains classes */
        if (myLine.contains(MoneyWiseXQIFConstants.XQIF_CLASS)) {
            /* drop preceding data */
            final int i = myLine.indexOf(MoneyWiseXQIFConstants.XQIF_CLASS);
            myLine = myLine.substring(i + 1);

            /* Build list of classes */
            final String[] myClasses = myLine.split(MoneyWiseXQIFConstants.XQIF_CLASSSEP);
            final List<MoneyWiseXQIFClass> myList = new ArrayList<>();
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
        addLine(new MoneyWiseXQIFEventReferenceLine(pReference));
    }

    /**
     * record comment.
     *
     * @param pComment the comment
     */
    void recordComment(final String pComment) {
        /* Add comment line */
        addLine(new MoneyWiseXQIFEventCommentLine(pComment));
    }

    /**
     * record payee.
     *
     * @param pPayee the payee
     */
    protected void recordPayee(final MoneyWiseXQIFPayee pPayee) {
        /* Add payee line */
        addLine(new MoneyWiseXQIFEventPayeeLine(pPayee));
    }

    /**
     * record payee description.
     *
     * @param pPayeeDesc the payee description
     */
    protected void recordPayee(final String pPayeeDesc) {
        /* Add payee line */
        addLine(new MoneyWiseXQIFEventPayeeDescLine(pPayeeDesc));
    }

    /**
     * record amount.
     *
     * @param pAmount the amount
     */
    protected void recordAmount(final OceanusMoney pAmount) {
        /* Add amount line */
        addLine(new MoneyWiseXQIFEventAmountLine(pAmount));
    }

    /**
     * record transfer account.
     *
     * @param pAccount the account
     */
    protected void recordAccount(final MoneyWiseXQIFAccount pAccount) {
        /* Add account line */
        addLine(new MoneyWiseXQIFEventAccountLine(pAccount));
    }

    /**
     * record transfer account.
     *
     * @param pAccount the account
     * @param pClasses the classes
     */
    protected void recordAccount(final MoneyWiseXQIFAccount pAccount,
                                 final List<MoneyWiseXQIFClass> pClasses) {
        /* Add account line */
        addLine(new MoneyWiseXQIFEventAccountLine(pAccount, pClasses));
    }

    /**
     * record category.
     *
     * @param pCategory the category
     */
    protected void recordCategory(final MoneyWiseXQIFEventCategory pCategory) {
        /* Add category line */
        addLine(new MoneyWiseXQIFEventCategoryLine(pCategory));
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
        addLine(new MoneyWiseXQIFEventCategoryLine(pCategory, pClasses));
    }

    /**
     * record new Split record for transfer.
     *
     * @param pAccount the account
     * @param pAmount  the amount
     * @param pComment the comment
     */
    protected void recordSplitRecord(final MoneyWiseXQIFAccount pAccount,
                                     final OceanusMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseXQIFSplitEvent mySplit = new MoneyWiseXQIFSplitEvent(pAccount);
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
    protected void recordSplitRecord(final MoneyWiseXQIFAccount pAccount,
                                     final List<MoneyWiseXQIFClass> pClasses,
                                     final OceanusMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseXQIFSplitEvent mySplit = new MoneyWiseXQIFSplitEvent(pAccount, pClasses);
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
    protected void recordSplitRecord(final MoneyWiseXQIFEventCategory pCategory,
                                     final OceanusMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseXQIFSplitEvent mySplit = new MoneyWiseXQIFSplitEvent(pCategory);
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
    protected void recordSplitRecord(final MoneyWiseXQIFEventCategory pCategory,
                                     final List<MoneyWiseXQIFClass> pClasses,
                                     final OceanusMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        final MoneyWiseXQIFSplitEvent mySplit = new MoneyWiseXQIFSplitEvent(pCategory, pClasses);
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
        final MoneyWiseXQIFLine<MoneyWiseQEventLineType> myLine = getLine(MoneyWiseQEventLineType.PAYEE);
        if (myLine instanceof MoneyWiseXQIFEventPayeeDescLine myDesc) {
            /* Access payee */
            final String myName = myDesc.getValue();

            /* Register the payee */
            final MoneyWiseXQIFPayee myPayee = theRegister.registerPayee(myName);
            addLine(new MoneyWiseXQIFEventPayeeLine(myPayee));
        }
    }

    /**
     * The Event Date line.
     */
    public static class MoneyWiseXQIFEventDateLine
            extends MoneyWiseXQIFDateLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pDate the Date
         */
        protected MoneyWiseXQIFEventDateLine(final OceanusDate pDate) {
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
    public static class MoneyWiseXQIFEventReferenceLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pRef the Reference
         */
        protected MoneyWiseXQIFEventReferenceLine(final String pRef) {
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
    public static class MoneyWiseXQIFEventCommentLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pComment the comment
         */
        protected MoneyWiseXQIFEventCommentLine(final String pComment) {
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
    public static class MoneyWiseXQIFEventClearedLine
            extends MoneyWiseXQIFClearedLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pCleared is the event cleared?
         */
        protected MoneyWiseXQIFEventClearedLine(final Boolean pCleared) {
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
    public static class MoneyWiseXQIFEventPayeeLine
            extends MoneyWiseXQIFPayeeLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pPayee the payee
         */
        protected MoneyWiseXQIFEventPayeeLine(final MoneyWiseXQIFPayee pPayee) {
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
    public static class MoneyWiseXQIFEventPayeeDescLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pPayee the payee description
         */
        protected MoneyWiseXQIFEventPayeeDescLine(final String pPayee) {
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
    public static class MoneyWiseXQIFEventAmountLine
            extends MoneyWiseXQIFMoneyLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pAmount the amount
         */
        protected MoneyWiseXQIFEventAmountLine(final OceanusMoney pAmount) {
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
    public static class MoneyWiseXQIFEventAccountLine
            extends MoneyWiseXQIFXferAccountLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pAccount the account
         */
        protected MoneyWiseXQIFEventAccountLine(final MoneyWiseXQIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }

        /**
         * Constructor.
         *
         * @param pAccount the account
         * @param pClasses the classes
         */
        protected MoneyWiseXQIFEventAccountLine(final MoneyWiseXQIFAccount pAccount,
                                                final List<MoneyWiseXQIFClass> pClasses) {
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
    public static class MoneyWiseXQIFEventCategoryLine
            extends MoneyWiseXQIFCategoryLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pCategory the category
         */
        protected MoneyWiseXQIFEventCategoryLine(final MoneyWiseXQIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         *
         * @param pCategory the category
         * @param pClasses  the classes
         */
        protected MoneyWiseXQIFEventCategoryLine(final MoneyWiseXQIFEventCategory pCategory,
                                                 final List<MoneyWiseXQIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pClasses);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.CATEGORY;
        }
    }
}
