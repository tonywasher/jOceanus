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
package net.sourceforge.joceanus.jmoneywise.quicken.file;

import java.util.List;

import net.sourceforge.joceanus.jdecimal.JMoney;
import net.sourceforge.joceanus.jdecimal.JRate;
import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFCategoryLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFRateLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFXferAccountLine;

/**
 * Split Event Record.
 */
public class QIFSplitEvent
        extends QIFRecord<QEventLineType> {
    /**
     * The Event Category.
     */
    private final QIFEventCategory theCategory;

    /**
     * The Transfer Account.
     */
    private final QIFAccount theAccount;

    /**
     * The Amount.
     */
    private JMoney theAmount;

    /**
     * The Percentage.
     */
    private JRate thePercentage;

    /**
     * The Comment.
     */
    private String theComment;

    /**
     * Obtain the event category.
     * @return the event category.
     */
    public QIFEventCategory getCategory() {
        return theCategory;
    }

    /**
     * Obtain the account.
     * @return the account.
     */
    public QIFAccount getAccount() {
        return theAccount;
    }

    /**
     * Obtain the amount.
     * @return the amount.
     */
    public JMoney getAmount() {
        return theAmount;
    }

    /**
     * Obtain the percentage.
     * @return the percentage.
     */
    public JRate getPercentage() {
        return thePercentage;
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
     * @param pCategory the category
     */
    protected QIFSplitEvent(final QIFFile pFile,
                            final QIFEventCategory pCategory) {
        this(pFile, pCategory, null);
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pCategory the category
     * @param pClasses the classes
     */
    protected QIFSplitEvent(final QIFFile pFile,
                            final QIFEventCategory pCategory,
                            final List<QIFClass> pClasses) {
        /* Call Super-constructor */
        super(pFile, QEventLineType.class);

        /* Set values */
        theCategory = pCategory;
        theAccount = null;
        theAmount = null;
        thePercentage = null;
        theComment = null;

        /* Add the line */
        addLine(new QIFEventSplitCategoryLine(theCategory, pClasses));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pAccount the transfer account
     */
    protected QIFSplitEvent(final QIFFile pFile,
                            final QIFAccount pAccount) {
        /* Call Super-constructor */
        this(pFile, pAccount, null);
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pAccount the transfer account
     * @param pClasses the classes
     */
    protected QIFSplitEvent(final QIFFile pFile,
                            final QIFAccount pAccount,
                            final List<QIFClass> pClasses) {
        /* Call Super-constructor */
        super(pFile, QEventLineType.class);

        /* Set values */
        theCategory = null;
        theAccount = pAccount;
        theAmount = null;
        thePercentage = null;
        theComment = null;

        /* Add the line */
        addLine(new QIFEventSplitAccountLine(pAccount, pClasses));
    }

    /**
     * Set the split amount.
     * @param pAmount the amount
     */
    protected void setSplitAmount(final JMoney pAmount) {
        /* Add the line */
        addLine(new QIFEventSplitAmountLine(pAmount));
        theAmount = pAmount;
    }

    /**
     * Set the split percentage.
     * @param pPercent the percentage
     */
    protected void setSplitPercentage(final JRate pPercent) {
        /* Add the line */
        addLine(new QIFEventSplitPercentLine(pPercent));
        thePercentage = pPercent;
    }

    /**
     * Set the split comment.
     * @param pComment the comment
     */
    protected void setSplitComment(final String pComment) {
        /* Add the line */
        addLine(new QIFEventSplitCommentLine(pComment));
        theComment = pComment;
    }

    /**
     * The Event Split Account line.
     */
    public class QIFEventSplitAccountLine
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

        /**
         * Constructor.
         * @param pAccount the account
         * @param pClasses the classes
         */
        protected QIFEventSplitAccountLine(final QIFAccount pAccount,
                                           final List<QIFClass> pClasses) {
            /* Call super-constructor */
            super(pAccount, pClasses);
        }
    }

    /**
     * The Event Split Category line.
     */
    public class QIFEventSplitCategoryLine
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

        /**
         * Constructor.
         * @param pCategory the category
         * @param pClasses the classes
         */
        protected QIFEventSplitCategoryLine(final QIFEventCategory pCategory,
                                            final List<QIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pClasses);
        }
    }

    /**
     * The Event Split Amount line.
     */
    public class QIFEventSplitAmountLine
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
     * The Event Split Percent line.
     */
    public class QIFEventSplitPercentLine
            extends QIFRateLine<QEventLineType> {
        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SplitPercent;
        }

        /**
         * Obtain Percentage.
         * @return the percentage
         */
        public JRate getPercentage() {
            return getRate();
        }

        /**
         * Constructor.
         * @param pPercent the percentage
         */
        protected QIFEventSplitPercentLine(final JRate pPercent) {
            /* Call super-constructor */
            super(pPercent);
        }
    }

    /**
     * The Event Split Comment line.
     */
    public class QIFEventSplitCommentLine
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
