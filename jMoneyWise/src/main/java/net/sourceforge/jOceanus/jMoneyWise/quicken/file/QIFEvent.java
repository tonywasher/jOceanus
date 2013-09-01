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

import net.sourceforge.jOceanus.jDateDay.JDateDay;
import net.sourceforge.jOceanus.jDecimal.JMoney;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFCategoryLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFClearedLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFDateLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFPayeeLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.jOceanus.jMoneyWise.quicken.file.QIFLine.QIFXferAccountLine;

/**
 * Class representing a QIF Event record.
 */
public class QIFEvent {
    /**
     * The Event Date line.
     */
    public class QIFEventDateLine
            extends QIFDateLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Date;
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
    public static class QIFEventReferenceLine
            extends QIFStringLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Reference;
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
    public static class QIFEventCommentLine
            extends QIFStringLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Comment;
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
    public static class QIFEventClearedLine
            extends QIFClearedLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Cleared;
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
    public static class QIFEventPayeeAccountLine
            extends QIFPayeeLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Payee;
        }

        /**
         * Constructor.
         * @param pPayee the payee
         */
        protected QIFEventPayeeAccountLine(final QIFAccount pPayee) {
            /* Call super-constructor */
            super(pPayee);
        }
    }

    /**
     * The Event Payee Description line.
     */
    public static class QIFEventPayeeDescLine
            extends QIFStringLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Payee;
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
    public static class QIFEventAmountLine
            extends QIFMoneyLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Amount;
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
    public static class QIFEventAccountLine
            extends QIFXferAccountLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Category;
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        protected QIFEventAccountLine(final QIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }
    }

    /**
     * The Event Category line.
     */
    public static class QIFEventCategoryLine
            extends QIFCategoryLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.Category;
        }

        /**
         * Constructor.
         * @param pCategory the category
         */
        protected QIFEventCategoryLine(final QIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }
    }

    /**
     * The Event Split Account line.
     */
    public static class QIFEventSplitAccountLine
            extends QIFXferAccountLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SplitCategory;
        }

        /**
         * Constructor.
         * @param pAccount the account
         */
        protected QIFEventSplitAccountLine(final QIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }
    }

    /**
     * The Event Split Category line.
     */
    public static class QIFEventSplitCategoryLine
            extends QIFCategoryLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SplitCategory;
        }

        /**
         * Constructor.
         * @param pCategory the category
         */
        protected QIFEventSplitCategoryLine(final QIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }
    }

    /**
     * The Event Split Amount line.
     */
    public static class QIFEventSplitAmountLine
            extends QIFMoneyLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SplitAmount;
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
        protected QIFEventSplitAmountLine(final JMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }
    }

    /**
     * The Event Split Comment line.
     */
    public static class QIFEventSplitCommentLine
            extends QIFStringLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SplitComment;
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
        protected QIFEventSplitCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }
    }
}
