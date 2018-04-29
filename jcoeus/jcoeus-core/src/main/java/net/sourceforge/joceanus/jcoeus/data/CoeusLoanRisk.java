/*******************************************************************************
 * Coeus: Peer2Peer Analysis
 * Copyright 2012,2018 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jcoeus.data;

/**
 * Loan Risk.
 */
public enum CoeusLoanRisk {
    /**
     * Risk Band A+.
     */
    APLUS,

    /**
     * Risk Band A.
     */
    A,

    /**
     * Risk Band B.
     */
    B,

    /**
     * Risk Band C.
     */
    C,

    /**
     * Risk Band D.
     */
    D,

    /**
     * Risk Band E.
     */
    E,

    /**
     * Risk Band S.
     */
    S,

    /**
     * Risk Band Unclassified.
     */
    UNCLASSIFIED;

    /**
     * The String name.
     */
    private String theName;

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = CoeusResource.getKeyForRisk(this).getValue();
        }

        /* return the name */
        return theName;
    }
}
