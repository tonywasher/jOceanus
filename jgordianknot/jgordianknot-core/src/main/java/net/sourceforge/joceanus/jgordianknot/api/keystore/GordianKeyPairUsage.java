/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.api.keystore;

import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bouncycastle.asn1.x509.KeyUsage;

/**
 * KeyPair Usage.
 */
public class GordianKeyPairUsage {
    /**
     * The Usage set.
     */
    private final EnumSet<GordianKeyPairUse> theUsageSet;

    /**
     * Constructor.
     *
     * @param pUse the usage.
     */
    public GordianKeyPairUsage(final GordianKeyPairUse... pUse) {
        theUsageSet = EnumSet.noneOf(GordianKeyPairUse.class);
        theUsageSet.addAll(List.of(pUse));
    }

    /**
     * Add a usage.
     * @param pUse the use to add
     */
    public void addUse(final GordianKeyPairUse pUse) {
        theUsageSet.add(pUse);
    }

    /**
     * Does the keyPair have the specified use?
     * @param pUse the use to test for
     * @return true/false
     */
    public boolean hasUse(final GordianKeyPairUse pUse) {
        return theUsageSet.contains(pUse);
    }

    /**
     * Obtain the usageSet.
     *
     * @return the UseSet
     */
    public Set<GordianKeyPairUse> getUsageSet()  {
        return EnumSet.copyOf(theUsageSet);
    }

    /**
     * Obtain the keyUsage.
     * @return the keyUsage
     */
    public KeyUsage getKeyUsage() {
        int myUsage = 0;
        for (GordianKeyPairUse myUse : theUsageSet) {
            myUsage |= myUse.getUsage();
        }
        return new KeyUsage(myUsage);
    }
}
