/*******************************************************************************
 * jTethys: Java Utilities
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
package net.sourceforge.joceanus.jtethys;

import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;

/**
 * Log4j Configuration.
 */
public final class TethysLogConfig {
    /**
     * Private constructor.
     */
    private TethysLogConfig() {
    }

    /**
     * Configure log4j.
     */
    public static void configureLog4j() {
        /* Configure log4j */
        Properties myLogProp = new Properties();
        myLogProp.setProperty("log4j.rootLogger", "ERROR, A1");
        myLogProp.setProperty("log4j.appender.A1", "org.apache.log4j.ConsoleAppender");
        myLogProp.setProperty("log4j.appender.A1.layout", "org.apache.log4j.PatternLayout");
        myLogProp.setProperty("log4j.appender.A1.layout.ConversionPattern", "%-4r [%t] %-5p %c %x - %m%n");
        PropertyConfigurator.configure(myLogProp);
    }
}
