/*******************************************************************************
 * Tethys: Java Utilities
 * Copyright 2012,2019 Tony Washer
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

/**
 * Log Manager.
 */
public final class TethysLogManager {
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
     * @param pMessage the message to format
     * @return the formatted string
     */
    String formatMessage(final Class<?> pOwner,
                         final String pMessage) {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(System.nanoTime() - theTimeZero);
        myBuilder.append(": ");
        myBuilder.append(pOwner.getCanonicalName());
        myBuilder.append("- ");
        myBuilder.append(pMessage);
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
    public static class TethysLogManagerHelper  {
        /**
         * The Log Manager instance.
         */
        private static final TethysLogManager INSTANCE = new TethysLogManager();
    }
}
