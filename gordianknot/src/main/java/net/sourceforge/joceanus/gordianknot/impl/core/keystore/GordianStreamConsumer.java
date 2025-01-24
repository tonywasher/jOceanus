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
package net.sourceforge.joceanus.gordianknot.impl.core.keystore;

import java.io.OutputStream;

import net.sourceforge.joceanus.gordianknot.api.base.GordianConsumer;

/**
 * Class to process an outputStream to a consumer.
 */
public class GordianStreamConsumer extends OutputStream {
    /**
     * The consumer.
     */
    private final GordianConsumer theConsumer;

    /**
     * Constructor.
     * @param pConsumer the consumer
     */
    public GordianStreamConsumer(final GordianConsumer pConsumer) {
        theConsumer = pConsumer;
    }

    @Override
    public void write(final byte[] pBytes) {
        write(pBytes, 0, pBytes == null ? 0 : pBytes.length);
    }

    @Override
    public void write(final int pByte) {
        theConsumer.update((byte) pByte);
    }

    @Override
    public void write(final byte[] pBytes,
                      final int pOffset,
                      final int pLength) {
        theConsumer.update(pBytes, pOffset, pLength);
    }
}
