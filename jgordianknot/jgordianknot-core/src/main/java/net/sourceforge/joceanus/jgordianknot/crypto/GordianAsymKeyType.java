/*******************************************************************************
 * jGordianKnot: Security Suite
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
package net.sourceforge.joceanus.jgordianknot.crypto;

/**
 * Asymmetric KeyTypes.
 */
public enum GordianAsymKeyType {
    /**
     * RSA.
     */
    RSA,

    /**
     * EC1.
     */
    EC1,

    /**
     * EC2.
     */
    EC2,

    /**
     * EC3.
     */
    EC3,

    /**
     * EC4.
     */
    EC4;

    /**
     * Is the key elliptic?
     * @return true/false
     */
    public boolean isElliptic() {
        return this != RSA;
    }

    /**
     * Obtain the named elliptic curve.
     * @return the curve name
     */
    public String getCurve() {
        switch (this) {
            case EC1:
                return "sect571r1";
            case EC2:
                return "sect571k1";
            case EC3:
                return "brainpoolP512r1";
            case EC4:
                return "brainpoolP512t1";
            default:
                return null;
        }
    }
}
