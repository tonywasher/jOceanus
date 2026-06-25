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

import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFAccount.MoneyWiseXQIFXferAccountLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFEventCategory.MoneyWiseXQIFCategoryLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFMoneyLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFRateLine;
import io.github.tonywasher.joceanus.moneywise.atlas.quicken.xfile.MoneyWiseXQIFLine.MoneyWiseXQIFStringLine;
import io.github.tonywasher.joceanus.moneywise.quicken.definitions.MoneyWiseQEventLineType;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusMoney;
import io.github.tonywasher.joceanus.oceanus.decimal.OceanusRate;

import java.util.List;

/**
 * Split Event Record.
 */
public class MoneyWiseXQIFSplitEvent
        extends MoneyWiseXQIFRecord<MoneyWiseQEventLineType> {
    /**
     * The Event Category.
     */
    private final MoneyWiseXQIFEventCategory theCategory;

    /**
     * The Transfer Account.
     */
    private final MoneyWiseXQIFAccount theAccount;

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
     *
     * @param pCategory the category
     */
    protected MoneyWiseXQIFSplitEvent(final MoneyWiseXQIFEventCategory pCategory) {
        this(pCategory, null);
    }

    /**
     * Constructor.
     *
     * @param pCategory the category
     * @param pClasses  the classes
     */
    protected MoneyWiseXQIFSplitEvent(final MoneyWiseXQIFEventCategory pCategory,
                                      final List<MoneyWiseXQIFClass> pClasses) {
        /* Call Super-constructor */
        super(MoneyWiseQEventLineType.class);

        /* Set values */
        theCategory = pCategory;
        theAccount = null;
        theAmount = null;
        thePercentage = null;
        theComment = null;

        /* Add the line */
        addLine(new MoneyWiseXQIFEventSplitCategoryLine(theCategory, pClasses));
    }

    /**
     * Constructor.
     *
     * @param pAccount the transfer account
     */
    protected MoneyWiseXQIFSplitEvent(final MoneyWiseXQIFAccount pAccount) {
        /* Call Super-constructor */
        this(pAccount, null);
    }

    /**
     * Constructor.
     *
     * @param pAccount the transfer account
     * @param pClasses the classes
     */
    protected MoneyWiseXQIFSplitEvent(final MoneyWiseXQIFAccount pAccount,
                                      final List<MoneyWiseXQIFClass> pClasses) {
        /* Call Super-constructor */
        super(MoneyWiseQEventLineType.class);

        /* Set values */
        theCategory = null;
        theAccount = pAccount;
        theAmount = null;
        thePercentage = null;
        theComment = null;

        /* Add the line */
        addLine(new MoneyWiseXQIFEventSplitAccountLine(pAccount, pClasses));
    }

    /**
     * Obtain the event category.
     *
     * @return the event category.
     */
    public MoneyWiseXQIFEventCategory getCategory() {
        return theCategory;
    }

    /**
     * Obtain the account.
     *
     * @return the account.
     */
    public MoneyWiseXQIFAccount getAccount() {
        return theAccount;
    }

    /**
     * Obtain the amount.
     *
     * @return the amount.
     */
    public OceanusMoney getAmount() {
        return theAmount;
    }

    /**
     * Obtain the percentage.
     *
     * @return the percentage.
     */
    public OceanusRate getPercentage() {
        return thePercentage;
    }

    /**
     * Obtain the comment.
     *
     * @return the comment.
     */
    public String getComment() {
        return theComment;
    }

    /**
     * Set the split amount.
     *
     * @param pAmount the amount
     */
    protected void setSplitAmount(final OceanusMoney pAmount) {
        /* Add the line */
        addLine(new MoneyWiseXQIFEventSplitAmountLine(pAmount));
        theAmount = pAmount;
    }

    /**
     * Set the split percentage.
     *
     * @param pPercent the percentage
     */
    protected void setSplitPercentage(final OceanusRate pPercent) {
        /* Add the line */
        addLine(new MoneyWiseXQIFEventSplitPercentLine(pPercent));
        thePercentage = pPercent;
    }

    /**
     * Set the split comment.
     *
     * @param pComment the comment
     */
    protected void setSplitComment(final String pComment) {
        /* Add the line */
        addLine(new MoneyWiseXQIFEventSplitCommentLine(pComment));
        theComment = pComment;
    }

    /**
     * The Event Split Account line.
     */
    public static class MoneyWiseXQIFEventSplitAccountLine
            extends MoneyWiseXQIFXferAccountLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pAccount the account
         */
        protected MoneyWiseXQIFEventSplitAccountLine(final MoneyWiseXQIFAccount pAccount) {
            /* Call super-constructor */
            super(pAccount);
        }

        /**
         * Constructor.
         *
         * @param pAccount the account
         * @param pClasses the classes
         */
        protected MoneyWiseXQIFEventSplitAccountLine(final MoneyWiseXQIFAccount pAccount,
                                                     final List<MoneyWiseXQIFClass> pClasses) {
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
    public static class MoneyWiseXQIFEventSplitCategoryLine
            extends MoneyWiseXQIFCategoryLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pCategory the category
         */
        protected MoneyWiseXQIFEventSplitCategoryLine(final MoneyWiseXQIFEventCategory pCategory) {
            /* Call super-constructor */
            super(pCategory);
        }

        /**
         * Constructor.
         *
         * @param pCategory the category
         * @param pClasses  the classes
         */
        protected MoneyWiseXQIFEventSplitCategoryLine(final MoneyWiseXQIFEventCategory pCategory,
                                                      final List<MoneyWiseXQIFClass> pClasses) {
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
    public static class MoneyWiseXQIFEventSplitAmountLine
            extends MoneyWiseXQIFMoneyLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pAmount the amount
         */
        protected MoneyWiseXQIFEventSplitAmountLine(final OceanusMoney pAmount) {
            /* Call super-constructor */
            super(pAmount);
        }

        /**
         * Obtain Amount.
         *
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
    public static class MoneyWiseXQIFEventSplitPercentLine
            extends MoneyWiseXQIFRateLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pPercent the percentage
         */
        protected MoneyWiseXQIFEventSplitPercentLine(final OceanusRate pPercent) {
            /* Call super-constructor */
            super(pPercent);
        }

        /**
         * Obtain Percentage.
         *
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
    public static class MoneyWiseXQIFEventSplitCommentLine
            extends MoneyWiseXQIFStringLine<MoneyWiseQEventLineType> {
        /**
         * Constructor.
         *
         * @param pComment the comment
         */
        protected MoneyWiseXQIFEventSplitCommentLine(final String pComment) {
            /* Call super-constructor */
            super(pComment);
        }

        @Override
        public MoneyWiseQEventLineType getLineType() {
            return MoneyWiseQEventLineType.SPLITCOMMENT;
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
}
