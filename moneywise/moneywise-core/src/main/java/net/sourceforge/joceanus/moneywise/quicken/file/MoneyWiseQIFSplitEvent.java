/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.moneywise.quicken.file;

import java.util.List;

import net.sourceforge.joceanus.moneywise.quicken.definitions.MoneyWiseQEventLineType;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFCategoryLine;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFMoneyLine;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFRateLine;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFStringLine;
import net.sourceforge.joceanus.moneywise.quicken.file.MoneyWiseQIFLine.MoneyWiseQIFXferAccountLine;
import net.sourceforge.joceanus.oceanus.decimal.OceanusMoney;
import net.sourceforge.joceanus.oceanus.decimal.OceanusRate;

/**
 * Split Event Record.
 */
public class MoneyWiseQIFSplitEvent
        extends MoneyWiseQIFRecord<MoneyWiseQEventLineType> {
    /**
     * The Event Category.
     */
    private final MoneyWiseQIFEventCategory theCategory;

    /**
     * The Transfer Account.
     */
    private final MoneyWiseQIFAccount theAccount;

    /**
     * The Amount.
     */
    private OceanusMoney theAmount;

    /**
     * The Percentage.
     */
    private OceanusRate thePercentage;

    /**
     * The Comment.
     */
    private String theComment;

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pCategory the category
     */
    protected MoneyWiseQIFSplitEvent(final MoneyWiseQIFFile pFile,
                                     final MoneyWiseQIFEventCategory pCategory) {
        this(pFile, pCategory, null);
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pCategory the category
     * @param pClasses the classes
     */
    protected MoneyWiseQIFSplitEvent(final MoneyWiseQIFFile pFile,
                                     final MoneyWiseQIFEventCategory pCategory,
                                     final List<MoneyWiseQIFClass> pClasses) {
        /* Call Super-constructor */
        super(pFile, MoneyWiseQEventLineType.class);

        /* Set values */
        theCategory = pCategory;
        theAccount = null;
        theAmount = null;
        thePercentage = null;
        theComment = null;

        /* Add the line */
        addLine(new MoneyWiseQIFEventSplitCategoryLine(theCategory, pClasses));
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pAccount the transfer account
     */
    protected MoneyWiseQIFSplitEvent(final MoneyWiseQIFFile pFile,
                                     final MoneyWiseQIFAccount pAccount) {
        /* Call Super-constructor */
        this(pFile, pAccount, null);
    }

    /**
     * Constructor.
     * @param pFile the QIF File
     * @param pAccount the transfer account
     * @param pClasses the classes
     */
    protected MoneyWiseQIFSplitEvent(final MoneyWiseQIFFile pFile,
                                     final MoneyWiseQIFAccount pAccount,
                                     final List<MoneyWiseQIFClass> pClasses) {
        /* Call Super-constructor */
        super(pFile, MoneyWiseQEventLineType.class);

        /* Set values */
        theCategory = null;
        theAccount = pAccount;
        theAmount = null;
        thePercentage = null;
        theComment = null;

        /* Add the line */
        addLine(new MoneyWiseQIFEventSplitAccountLine(pAccount, pClasses));
    }

    /**
     * Obtain the event category.
     * @return the event category.
     */
    public MoneyWiseQIFEventCategory getCategory() {
        return theCategory;
    }

    /**
     * Obtain the account.
     * @return the account.
     */
    public MoneyWiseQIFAccount getAccount() {
        return theAccount;
    }

    /**
     * Obtain the amount.
     * @return the amount.
     */
    public OceanusMoney getAmount() {
        return theAmount;
    }

    /**
     * Obtain the percentage.
     * @return the percentage.
     */
    public OceanusRate getPercentage() {
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
    protected void setSplitAmount(final OceanusMoney pAmount) {
        /* Add the line */
        addLine(new MoneyWiseQIFEventSplitAmountLine(pAmount));
        theAmount = pAmount;
    }

    /**
     * Set the split percentage.
     * @param pPercent the percentage
     */
    protected void setSplitPercentage(final OceanusRate pPercent) {
        /* Add the line */
        addLine(new MoneyWiseQIFEventSplitPercentLine(pPercent));
        thePercentage = pPercent;
    }

    /**
     * Set the split comment.
     * @param pComment the comment
     */
    protected void setSplitComment(final String pComment) {
        /* Add the line */
        addLine(new MoneyWiseQIFEventSplitCommentLine(pComment));
        theComment = pComment;
    }

    /**
     * The Event Split Account line.
     */
    public class MoneyWiseQIFEventSplitAccountLine
            extends MoneyWiseQIFXferAccountLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pAccount the account
         */
        protected MoneyWiseQIFEventSplitAccountLine(final MoneyWiseQIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }

        /**
         * Constructor.
         * @param pAccount the account
         * @param pClasses the classes
         */
        protected MoneyWiseQIFEventSplitAccountLine(final MoneyWiseQIFAccount pAccount,
                                                    final List<MoneyWiseQIFClass> pClasses) {
            /* Call super-constructor */
            super(pAccount, pClasses);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.SPLITCATEGORY;
        }
    }

    /**
     * The Event Split Category line.
     */
    public class MoneyWiseQIFEventSplitCategoryLine
            extends MoneyWiseQIFCategoryLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pCategory the category
         */
        protected MoneyWiseQIFEventSplitCategoryLine(final MoneyWiseQIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         * @param pCategory the category
         * @param pClasses the classes
         */
        protected MoneyWiseQIFEventSplitCategoryLine(final MoneyWiseQIFEventCategory pCategory,
                                                     final List<MoneyWiseQIFClass> pClasses) {
            /* Call super-constructor */
            super(pCategory, pClasses);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.SPLITCATEGORY;
        }
    }

    /**
     * The Event Split Amount line.
     */
    public class MoneyWiseQIFEventSplitAmountLine
            extends MoneyWiseQIFMoneyLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pAmount the amount
         */
        protected MoneyWiseQIFEventSplitAmountLine(final OceanusMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        /**
         * Obtain Amount.
         * @return the amount
         */
        public OceanusMoney getAmount() {
            return getMoney();
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.SPLITAMOUNT;
        }
    }

    /**
     * The Event Split Percent line.
     */
    public class MoneyWiseQIFEventSplitPercentLine
            extends MoneyWiseQIFRateLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pPercent the percentage
         */
        protected MoneyWiseQIFEventSplitPercentLine(final OceanusRate pPercent) {
            /* Call super-constructor */
            super(pPercent);
        }

        /**
         * Obtain Percentage.
         * @return the percentage
         */
        public OceanusRate getPercentage() {
            return getRate();
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.SPLITPERCENT;
        }
    }

    /**
     * The Event Split Comment line.
     */
    public class MoneyWiseQIFEventSplitCommentLine
            extends MoneyWiseQIFStringLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         * @param pComment the comment
         */
        protected MoneyWiseQIFEventSplitCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.SPLITCOMMENT;
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
