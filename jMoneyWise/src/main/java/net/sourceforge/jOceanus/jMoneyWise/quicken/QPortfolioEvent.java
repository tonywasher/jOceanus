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
package net.sourceforge.jOceanus.jMoneyWise.quicken;

import net.sourceforge.jOceanus.jDataManager.JDataFormatter;
import net.sourceforge.jOceanus.jMoneyWise.data.Event;

/**
 * Quicken Portfolio Event Representation.
 */
public final class QPortfolioEvent
        extends QEvent {

    /**
     * Constructor.
     * @param pFormatter the data formatter
     * @param pEvent the event
     * @param pCredit is this the credit item?
     */
    private QPortfolioEvent(final JDataFormatter pFormatter,
                            final Event pEvent,
                            final boolean pCredit) {
        /* Call super-constructor */
        super(pFormatter, pEvent, pCredit);
    }

    @Override
    protected String buildQIF() {

        /* Return the detail */
        return completeItem();
    }

    /**
     * Event List class.
     */
    protected static class QPortfolioEventList
            extends QEventBaseList<QPortfolioEvent> {
        /**
         * Constructor.
         * @param pAccount the list owner
         * @param pFormatter the data formatter
         */
        protected QPortfolioEventList(final QAccount pAccount,
                                      final JDataFormatter pFormatter) {
            /* Call super constructor */
            super(pAccount, pFormatter);
        }

        /**
         * Register event.
         * @param pEvent the event
         * @param isCredit is this the credit item?
         */
        protected void registerEvent(final Event pEvent,
                                     final boolean isCredit) {
            QPortfolioEvent myEvent = new QPortfolioEvent(getFormatter(), pEvent, isCredit);
            addEvent(myEvent);
        }
    }

    /**
     * Quicken Portfolio Event Line Types.
     */
    public enum QPortLineType {
        /**
         * Date.
         */
        Date("D"),

        /**
         * Action.
         */
        Action("N"),

        /**
         * Security.
         */
        Security("Y"),

        /**
         * Price.
         */
        Price("I"),

        /**
         * Quantity.
         */
        Quantity("Q"),

        /**
         * Amount.
         */
        Amount("T"),

        /**
         * Cleared Status.
         */
        Cleared("C"),

        /**
         * Comment.
         */
        Comment("M"),

        /**
         * Payee.
         */
        Payee("P"),

        /**
         * Commission.
         */
        Commission("O"),

        /**
         * TransferAccount.
         */
        TransferAccount("L"),

        /**
         * TransferAmount.
         */
        TransferAmount("$");

        /**
         * The symbol.
         */
        private final String theSymbol;

        /**
         * Obtain the symbol.
         * @return the symbol
         */
        public String getSymbol() {
            return theSymbol;
        }

        /**
         * Constructor.
         * @param pSymbol the symbol
         */
        private QPortLineType(final String pSymbol) {
            /* Store symbol */
            theSymbol = pSymbol;
        }
    }
}
