/*******************************************************************************
 * GordianKnot: Security Suite
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.gordianknot.api.digest;

import net.sourceforge.joceanus.gordianknot.api.base.GordianConsumer;

/**
 * GordianKnot interface for extendable Output Functions.
 */
public interface GordianXof
        extends GordianConsumer {
    /**
     * Output the results of the final calculation for this digest to pOutLen number of bytes.
     *
     * @param pOutBuf output array to write the output bytes to.
     * @param pOutOff offset to start writing the bytes at.
     * @param pOutLen the number of output bytes requested.
     * @return the number of bytes written
     */
    int finish(byte[] pOutBuf,
               int pOutOff,
               int pOutLen);

    /**
     * Start outputting the results of the final calculation for this digest. Unlike finish, this method
     * will continue producing output until the Xof is explicitly reset, or signals otherwise.
     *
     * @param pOutBuf output array to write the output bytes to.
     * @param pOutOff offset to start writing the bytes at.
     * @param pOutLen the number of output bytes requested.
     * @return the number of bytes written
     */
    int output(byte[] pOutBuf,
               int pOutOff,
               int pOutLen);
}
