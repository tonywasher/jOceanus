/*******************************************************************************
 * GordianKnot: Security Suite
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
package net.sourceforge.joceanus.gordianknot.api.keypair;

/**
 * Asymmetric StateAware KeyPair.
 */
public interface GordianStateAwareKeyPair {
    /**
     * Obtain number of signatures remaining.
     * @return the number of signatures remaining
     */
    long getUsagesRemaining();

    /**
     * Obtain a subKeyPair shard with the required number of usages, and update this keyPair's usage count.
     * @param pNumUsages the number of usage for the shard
     * @return the subKeyPair
     */
    GordianKeyPair getKeyPairShard(int pNumUsages);
}
