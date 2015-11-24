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

import java.util.List;

import net.sourceforge.joceanus.jmoneywise.quicken.definitions.QEventLineType;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFCategoryLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFMoneyLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFRateLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFStringLine;
import net.sourceforge.joceanus.jmoneywise.quicken.file.QIFLine.QIFXferAccountLine;
import net.sourceforge.joceanus.jtethys.decimal.TethysMoney;
import net.sourceforge.joceanus.jtethys.decimal.TethysRate;

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
    private TethysMoney theAmount;

    /**
     * The Percentage.
     */
    private TethysRate thePercentage;

    /**
     * The Comment.
     */
    private String theComment;

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
    public TethysMoney getAmount() {
        return theAmount;
    }

    /**
     * Obtain the percentage.
     * @return the percentage.
     */
    public TethysRate getPercentage() {
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
     * Set the split amount.
     * @param pAmount the amount
     */
    protected void setSplitAmount(final TethysMoney pAmount) {
        /* Add the line */
        addLine(new QIFEventSplitAmountLine(pAmount));
        theAmount = pAmount;
    }

    /**
     * Set the split percentage.
     * @param pPercent the percentage
     */
    protected void setSplitPercentage(final TethysRate pPercent) {
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

        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SPLITCATEGORY;
        }
    }

    /**
     * The Event Split Category line.
     */
    public class QIFEventSplitCategoryLine
            extends QIFCategoryLine<QEventLineType> {
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

        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SPLITCATEGORY;
        }
    }

    /**
     * The Event Split Amount line.
     */
    public class QIFEventSplitAmountLine
            extends QIFMoneyLine<QEventLineType> {
        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected QIFEventSplitAmountLine(final TethysMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        /**
         * Obtain Amount.
         * @return the amount
         */
        public TethysMoney getAmount() {
            return getMoney();
        }

        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SPLITAMOUNT;
        }
    }

    /**
     * The Event Split Percent line.
     */
    public class QIFEventSplitPercentLine
            extends QIFRateLine<QEventLineType> {
        /**
         * Constructor.
         * @param pPercent the percentage
         */
        protected QIFEventSplitPercentLine(final TethysRate pPercent) {
            /* Call super-constructor */
            super(pPercent);
        }

        /**
         * Obtain Percentage.
         * @return the percentage
         */
        public TethysRate getPercentage() {
            return getRate();
        }

        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SPLITPERCENT;
        }
    }

    /**
     * The Event Split Comment line.
     */
    public class QIFEventSplitCommentLine
            extends QIFStringLine<QEventLineType> {
        /**
         * Constructor.
         * @param pComment the comment
         */
        protected QIFEventSplitCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }

        @Override
        public QEventLineType getLineType() {
            return QEventLineType.SPLITCOMMENT;
        }

        /**
         * Obtain Comment.
         * @return the comment
         */
        public String getComment() {
            return getValue();
        }
    }
}
