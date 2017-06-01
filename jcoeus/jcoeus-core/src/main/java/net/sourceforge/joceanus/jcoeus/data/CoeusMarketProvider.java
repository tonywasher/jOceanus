/*******************************************************************************
 * jCoeus: Peer2Peer Analysis
 * Copyright 2012,2017 Tony Washer
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

/**
 * Loan Market Providers.
 */
public enum CoeusMarketProvider {
    /**
     * Funding Circle.
     */
    FUNDINGCIRCLE,

    /**
     * LendingWorks.
     */
    LENDINGWORKS,

    /**
     * RateSetter.
     */
    RATESETTER,

    /**
     * Zopa.
     */
    ZOPA;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CoeusResource.getKeyForMarket(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Does the market support badDebt?
     * @return true/false
     */
    public boolean supportsBadDebt() {
        switch (this) {
            case RATESETTER:
            case LENDINGWORKS:
                return false;
            case FUNDINGCIRCLE:
            case ZOPA:
            default:
                return true;
        }
    }

    /**
     * Does the market support the totalSet?
     * @param pTotalSet the totalSet
     * @return true/false
     */
    public boolean supportsTotalSet(final CoeusTotalSet pTotalSet) {
        /* If we have no badDebt */
        if (!supportsBadDebt()) {
            switch (pTotalSet) {
                case LOSSES:
                case BADDEBT:
                case RECOVERED:
                case NETTINTEREST:
                case BADDEBTINTEREST:
                case BADDEBTCAPITAL:
                    return false;
                default:
                    break;
            }
        }

        /* Support everything else */
        return true;
    }
}
