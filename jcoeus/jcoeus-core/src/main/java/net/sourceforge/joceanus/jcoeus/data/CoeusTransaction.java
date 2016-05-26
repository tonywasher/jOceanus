/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
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
package net.sourceforge.joceanus.jcoeus.data;

import net.sourceforge.joceanus.jtethys.date.TethysDate;
import net.sourceforge.joceanus.jtethys.decimal.TethysDecimal;

/**
 * Coeus Transaction.
 */
public interface CoeusTransaction {
    /**
     * Obtain the date.
     * @return the date
     */
    TethysDate getDate();

    /**
     * Obtain the Description.
     * @return the description
     */
    String getDescription();

    /**
     * Obtain the transactionType.
     * @return the transactionType
     */
    CoeusTransactionType getTransType();

    /**
     * Obtain the loanId.
     * @return the loanId
     */
    String getLoanId();

    /**
     * Obtain the holdingDelta.
     * @return the holdingDelta
     */
    TethysDecimal getHoldingDelta();

    /**
     * Obtain the capitalDelta.
     * @return the capitalDelta
     */
    TethysDecimal getCapitalDelta();

    /**
     * Obtain the interestDelta.
     * @return the interestDelta
     */
    TethysDecimal getInterestDelta();

    /**
     * Obtain the feesDelta.
     * @return the feesDelta
     */
    TethysDecimal getFeesDelta();

    /**
     * Obtain the cashBackDelta.
     * @return the cashBackDelta
     */
    TethysDecimal getCashBackDelta();

    /**
     * Obtain the badDebtDelta.
     * @return the badDebtDelta
     */
    TethysDecimal getBadDebtDelta();
}
