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
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFCategoryLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFClearedLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFDateLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFPayeeLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFXferAccountLine;
import net.sourceforge.joceanus.jtethys.dateday.JDateDay;
import net.sourceforge.joceanus.jtethys.dateday.JDateDayFormatter;
import net.sourceforge.joceanus.jtethys.decimal.JDecimalParser;
import net.sourceforge.joceanus.jtethys.decimal.JMoney;
import net.sourceforge.joceanus.jtethys.decimal.JRate;

/**
 * Class representing a QIF Event record.
 */
public class QIFEvent
        extends QIFEventRecord<QEventLineType> {
    /**
     * The Date.
     */
    private final JDateDay theDate;

    /**
     * The Cleared Flag.
     */
    private final Boolean isCleared;

    @Override
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
     * Constructor.
     * @param pFile the QIF File
     * @param pTrans the transaction
     */
    protected QIFEvent(final QIFFile pFile,
                       final Transaction pTrans) {
        /* Call super-constructor */
        super(pFile, QEventLineType.class);

        /* Store values */
        theDate = pTrans.getDate();
        isCleared = pTrans.isReconciled();

        /* Add the lines */
        addLine(new QIFEventDateLine(theDate));
        addLine(new QIFEventClearedLine(isCleared));

        /* Add the reference line if it exists */
        String myRef = pTrans.getReference();
        if (myRef != null) {
            recordReference(myRef);
        }

        /* Add the comment line if it exists */
        String myComment = pTrans.getComments();
        if (myComment != null) {
            recordComment(myComment);
        }
    }

    /**
     * Constructor for opening balance.
     * @param pFile the QIF File
     * @param pStartDate the start date
     */
    protected QIFEvent(final QIFFile pFile,
                       final JDateDay pStartDate) {
        /* Call super-constructor */
        super(pFile, QEventLineType.class);

        /* Store values */
        theDate = pStartDate;
        isCleared = true;

        /* Add the lines */
        addLine(new QIFEventDateLine(theDate));
        addLine(new QIFEventClearedLine(isCleared));
        addLine(new QIFEventPayeeDescLine("OpeningBalance"));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pFormatter the Data Formatter
     * @param pLines the data lines
     */
    protected QIFEvent(final QIFFile pFile,
                       final JDataFormatter pFormatter,
                       final List<String> pLines) {
        /* Call super-constructor */
        super(pFile, QEventLineType.class);

        /* Determine details */
        JDateDay myDate = null;
        Boolean myCleared = null;

        /* Current split record */
        QIFSplitEvent mySplit = null;

        /* Obtain parsers */
        JDateDayFormatter myDateParser = pFormatter.getDateFormatter();
        JDecimalParser myDecParser = pFormatter.getDecimalParser();

        /* Loop through the lines */
        Iterator<String> myIterator = pLines.iterator();
        while (myIterator.hasNext()) {
            String myLine = myIterator.next();

            /* Determine the category */
            QEventLineType myType = QEventLineType.parseLine(myLine);
            if (myType != null) {
                /* Access data */
                String myData = myLine.substring(myType.getSymbol().length());

                /* Switch on line type */
                switch (myType) {
                    case DATE:
                        JDateDay myDateDay = myDateParser.parseDateDay(myData);
                        addLine(new QIFEventDateLine(myDateDay));
                        myDate = myDateDay;
                        break;
                    case CLEARED:
                        Boolean myFlag = myData.equals(QIFLine.QIF_RECONCILED);
                        addLine(new QIFEventClearedLine(myFlag));
                        myCleared = myFlag;
                        break;
                    case AMOUNT:
                        JMoney myMoney = myDecParser.parseMoneyValue(myData);
                        addLine(new QIFEventAmountLine(myMoney));
                        break;
                    case COMMENT:
                        addLine(new QIFEventCommentLine(myData));
                        break;
                    case REFERENCE:
                        addLine(new QIFEventReferenceLine(myData));
                        break;
                    case PAYEE:
                        addLine(new QIFEventPayeeDescLine(myData));
                        break;
                    case CATEGORY:
                        /* Check for account and category */
                        QIFAccount myAccount = QIFXferAccountLine.parseAccount(pFile, myData);
                        QIFEventCategory myCategory = QIFEventCategoryLine.parseCategory(pFile, myData);
                        if (myAccount != null) {
                            /* Look for account classes */
                            List<QIFClass> myClasses = QIFXferAccountLine.parseAccountClasses(pFile, myData);
                            addLine(new QIFEventAccountLine(myAccount, myClasses));
                        } else {
                            /* Look for category classes */
                            List<QIFClass> myClasses = QIFEventCategoryLine.parseCategoryClasses(pFile, myData);
                            addLine(new QIFEventCategoryLine(myCategory, myClasses));
                            convertPayee();
                        }
                        break;
                    case SPLITCATEGORY:
                        /* Check for account */
                        myAccount = QIFXferAccountLine.parseAccount(pFile, myData);
                        myCategory = QIFEventCategoryLine.parseCategory(pFile, myData);
                        if (myAccount != null) {
                            /* Look for account classes */
                            List<QIFClass> myClasses = QIFXferAccountLine.parseAccountClasses(pFile, myData);
                            mySplit = new QIFSplitEvent(pFile, myAccount, myClasses);
                        } else {
                            /* Look for category classes */
                            List<QIFClass> myClasses = QIFEventCategoryLine.parseCategoryClasses(pFile, myData);
                            mySplit = new QIFSplitEvent(pFile, myCategory, myClasses);
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
                        JRate myRate = myDecParser.parseRateValue(myData);
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

    /**
     * record reference.
     * @param pReference the reference
     */
    private void recordReference(final String pReference) {
        /* Add reference line */
        addLine(new QIFEventReferenceLine(pReference));
    }

    /**
     * record comment.
     * @param pComment the comment
     */
    private void recordComment(final String pComment) {
        /* Add comment line */
        addLine(new QIFEventCommentLine(pComment));
    }

    /**
     * record payee.
     * @param pPayee the payee
     */
    protected void recordPayee(final QIFPayee pPayee) {
        /* Add payee line */
        addLine(new QIFEventPayeeLine(pPayee));
    }

    /**
     * record payee description.
     * @param pPayeeDesc the payee description
     */
    protected void recordPayee(final String pPayeeDesc) {
        /* Add payee line */
        addLine(new QIFEventPayeeDescLine(pPayeeDesc));
    }

    /**
     * record amount.
     * @param pAmount the amount
     */
    protected void recordAmount(final JMoney pAmount) {
        /* Add amount line */
        addLine(new QIFEventAmountLine(pAmount));
    }

    /**
     * record transfer account.
     * @param pAccount the account
     */
    protected void recordAccount(final QIFAccount pAccount) {
        /* Add account line */
        addLine(new QIFEventAccountLine(pAccount));
    }

    /**
     * record transfer account.
     * @param pAccount the account
     * @param pClasses the classes
     */
    protected void recordAccount(final QIFAccount pAccount,
                                 final List<QIFClass> pClasses) {
        /* Add account line */
        addLine(new QIFEventAccountLine(pAccount, pClasses));
    }

    /**
     * record category.
     * @param pCategory the category
     */
    protected void recordCategory(final QIFEventCategory pCategory) {
        /* Add category line */
        addLine(new QIFEventCategoryLine(pCategory));
    }

    /**
     * record category.
     * @param pCategory the category
     * @param pClasses the classes
     */
    protected void recordCategory(final QIFEventCategory pCategory,
                                  final List<QIFClass> pClasses) {
        /* Add category line */
        addLine(new QIFEventCategoryLine(pCategory, pClasses));
    }

    /**
     * record new Split record for transfer.
     * @param pAccount the account
     * @param pAmount the amount
     * @param pComment the comment
     */
    protected void recordSplitRecord(final QIFAccount pAccount,
                                     final JMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        QIFSplitEvent mySplit = new QIFSplitEvent(getFile(), pAccount);
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
    protected void recordSplitRecord(final QIFAccount pAccount,
                                     final List<QIFClass> pClasses,
                                     final JMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        QIFSplitEvent mySplit = new QIFSplitEvent(getFile(), pAccount, pClasses);
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
    protected void recordSplitRecord(final QIFEventCategory pCategory,
                                     final JMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        QIFSplitEvent mySplit = new QIFSplitEvent(getFile(), pCategory);
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
    protected void recordSplitRecord(final QIFEventCategory pCategory,
                                     final List<QIFClass> pClasses,
                                     final JMoney pAmount,
                                     final String pComment) {
        /* Create new split and add it */
        QIFSplitEvent mySplit = new QIFSplitEvent(getFile(), pCategory, pClasses);
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
        QIFLine<QEventLineType> myLine = getLine(QEventLineType.PAYEE);
        if (myLine instanceof QIFEventPayeeDescLine) {
            /* Access payee */
            QIFEventPayeeDescLine myDesc = (QIFEventPayeeDescLine) myLine;
            String myName = myDesc.getValue();

            /* Register the payee */
            QIFPayee myPayee = getFile().registerPayee(myName);
            addLine(new QIFEventPayeeLine(myPayee));
        }
    }

    /**
     * The Event Date line.
     */
    public class QIFEventDateLine
            extends QIFDateLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.DATE;
        }

        /**
         * Constructor.
         * @param pDate the Date
         */
        protected QIFEventDateLine(final JDateDay pDate) {
            /* Call super-constructor */
            super(pDate);
        }
    }

    /**
     * The Event Reference line.
     */
    public class QIFEventReferenceLine
            extends QIFStringLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.REFERENCE;
        }

        /**
         * Obtain Reference.
         * @return the reference
         */
        public String getReference() {
            return getValue();
        }

        /**
         * Constructor.
         * @param pRef the Reference
         */
        protected QIFEventReferenceLine(final String pRef) {
            /* Call super-constructor */
            super(pRef);
        }
    }

    /**
     * The Event Comment line.
     */
    public class QIFEventCommentLine
            extends QIFStringLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.COMMENT;
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
        protected QIFEventCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }
    }

    /**
     * The Event Cleared line.
     */
    public class QIFEventClearedLine
            extends QIFClearedLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.CLEARED;
        }

        /**
         * Constructor.
         * @param pCleared is the event cleared?
         */
        protected QIFEventClearedLine(final Boolean pCleared) {
            /* Call super-constructor */
            super(pCleared);
        }
    }

    /**
     * The Event Payee Account line.
     */
    public class QIFEventPayeeLine
            extends QIFPayeeLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.PAYEE;
        }

        /**
         * Constructor.
         * @param pPayee the payee
         */
        protected QIFEventPayeeLine(final QIFPayee pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }
    }

    /**
     * The Event Payee Description line.
     */
    public class QIFEventPayeeDescLine
            extends QIFStringLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.PAYEE;
        }

        /**
         * Constructor.
         * @param pPayee the payee description
         */
        protected QIFEventPayeeDescLine(final String pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }
    }

    /**
     * The Event Amount line.
     */
    public class QIFEventAmountLine
            extends QIFMoneyLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.AMOUNT;
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
        protected QIFEventAmountLine(final JMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }
    }

    /**
     * The Event Account line.
     */
    public class QIFEventAccountLine
            extends QIFXferAccountLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.CATEGORY;
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        protected QIFEventAccountLine(final QIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }

        /**
         * Constructor.
         * @param pAccount the account
         * @param pClasses the classes
         */
        protected QIFEventAccountLine(final QIFAccount pAccount,
                                      final List<QIFClass> pClasses) {
            /* Call super-constructor */
            super(pAccount, pClasses);
        }
    }

    /**
     * The Event Category line.
     */
    public class QIFEventCategoryLine
            extends QIFCategoryLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.CATEGORY;
        }

        /**
         * Constructor.
         * @param pCategory the category
         */
        protected QIFEventCategoryLine(final QIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         * @param pCategory the category
         * @param pClasses the classes
         */
        protected QIFEventCategoryLine(final QIFEventCategory pCategory,
                                       final List<QIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pClasses);
        }
    }
}
