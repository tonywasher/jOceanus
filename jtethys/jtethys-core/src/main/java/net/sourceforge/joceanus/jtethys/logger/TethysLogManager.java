/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2023 Tony Washer
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
package net.sourceforge.joceanus.jtethys.logger;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import net.sourceforge.joceanus.jtethys.TethysDataConverter;

/**
 * Log Manager.
 */
public final class TethysLogManager {
    /**
     * The data section length.
     */
    private static final int DATA_SECTION = 32;

    /**
     * The data advance.
     */
    private static final int DATA_ADVANCE = 3;

    /**
     * The output stream.
     */
    private static TethysLogSink theSink = new TethysLogStdOut();

    /**
     * The initial time.
     */
    private final long theTimeZero;

    /**
     * The constructor.
     */
    private TethysLogManager() {
        theTimeZero = System.nanoTime();
    }

    /**
     * Obtain the singleton instance.
     * @return the instance.
     */
    private static TethysLogManager getInstance() {
        return TethysLogManagerHelper.INSTANCE;
    }

    /**
     * Obtain a logger.
     * @param pOwner the owning class
     * @return the logger
     */
    public static TethysLogger getLogger(final Class<?> pOwner) {
        return new TethysLogger(getInstance(), pOwner);
    }

    /**
     * Set Sink.
     * @param pSink the sink
     */
    public static void setSink(final TethysLogSink pSink) {
        theSink = pSink;
    }

    /**
     * Format message.
     * @param pOwner the owner
     * @param pLevel the log level
     * @param pMessage the message to format
     * @return the formatted string
     */
    String formatMessage(final Class<?> pOwner,
                         final TethysLogLevel pLevel,
                         final String pMessage) {
        return (System.nanoTime() - theTimeZero)
                + " "
                + pLevel.name()
                + ": "
                + pOwner.getSimpleName()
                + "- "
                + pMessage;
    }

    /**
     * Format data.
     * @param pData the data to format
     * @return the formatted data
     */
    public static String formatData(final byte[] pData) {
        return pData == null
                ? "\nnull"
                : formatData(pData, 0, pData.length);
    }

    /**
     * Format data.
     * @param pData the data to format
     * @param pOffset the offset
     * @param pLength the length of data
     * @return the formatted data
     */
    public static String formatData(final byte[] pData,
                                    final int pOffset,
                                    final int pLength) {
        /* Handle null data */
        if (pData == null) {
            return "\nnull";
        }

        /* Handle partial buffer */
        byte[] myData = pData;
        if (pOffset != 0 || pLength != pData.length) {
            myData = new byte[pLength];
            System.arraycopy(pData, pOffset, myData, 0, pLength);
        }

        /* Format the data */
        final String myFormatted = TethysDataConverter.bytesToHexString(myData);

        /* Place it into StringBuilder buffer */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myFormatted);

        /* Loop through the data */
        int myOffSet = 0;
        for (int i = 0; i < pLength; i++) {
            /* Insert blank/newLine between each HexPair */
            final char myChar = (i % DATA_SECTION) == 0 ? '\n' : ' ';
            myBuilder.insert(myOffSet, myChar);
            myOffSet += DATA_ADVANCE;
        }

        /* Return the data */
        return myBuilder.toString();
    }

    /**
     * Write log message.
     * @param pMessage the message to format
     */
    void writeLogMessage(final String pMessage) {
        synchronized (this) {
            theSink.writeLogMessage(pMessage);
        }
    }

    /**
     * Write log message and exception.
     * @param pMessage the message to format
     * @param pException the exception
     */
    void writeLogMessage(final String pMessage,
                         final Throwable pException) {
        synchronized (this) {
            theSink.writeLogMessage(pMessage);
            final ByteArrayOutputStream myBaos = new ByteArrayOutputStream();
            try (PrintStream myPs = new PrintStream(myBaos)) {
                if (pException != null) {
                    pException.printStackTrace(myPs);
                } else {
                    myPs.println("Null Exception");
                }
                theSink.writeLogMessage(myBaos.toString());
            }
        }
    }

    /**
     * Log Manager Helper.
     */
    private static final class TethysLogManagerHelper  {
        /**
         * The Log Manager instance.
         */
        private static final TethysLogManager INSTANCE = new TethysLogManager();
    }

    /**
     * Default Log Sink.
     */
    static class TethysLogStdOut
            implements TethysLogSink {
        /**
         * The output stream.
         */
        private final PrintStream theOutput = System.out;

        @Override
        public void writeLogMessage(final String pMessage) {
            theOutput.println(pMessage);
        }
    }
}
