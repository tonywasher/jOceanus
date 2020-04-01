/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2020 Tony Washer
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
    private final PrintStream theOutput;

    /**
     * The initial time.
     */
    private final long theTimeZero;

    /**
     * The constructor.
     */
    private TethysLogManager() {
        theOutput = System.out;
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
        /* Handle null data */
        if (pData == null) {
            return "\rnull";
        }

        /* Format the data */
        final String myData = TethysDataConverter.bytesToHexString(pData);

        /* Place it into StringBuilder buffer */
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(myData);

        /* Loop through the data */
        int myOffSet = 0;
        for (int i = 0; i < pData.length; i++) {
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
            theOutput.println(pMessage);
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
            theOutput.println(pMessage);
            if (pException != null) {
                pException.printStackTrace(theOutput);
            } else {
                theOutput.println("Null Exception");
            }
        }
    }

    /**
     * Log Manager Helper.
     */
    private static class TethysLogManagerHelper  {
        /**
         * The Log Manager instance.
         */
        private static final TethysLogManager INSTANCE = new TethysLogManager();
    }
}
