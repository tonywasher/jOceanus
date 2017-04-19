/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2016 Tony Washer
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
package net.sourceforge.joceanus.jmetis.lethe.profile;

import net.sourceforge.joceanus.jmetis.MetisDataException;
import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jtethys.TethysLogConfig;
import net.sourceforge.joceanus.jtethys.ui.TethysProgram;

/**
 * Program definition.
 */
public class MetisProgram {
    /**
     * Interface for application.
     */
    @FunctionalInterface
    public interface MetisApplication {
        /**
         * Set Program information.
         * @param pInfo the program information
         */
        void setProgramInfo(MetisProgram pInfo);
    }

    /**
     * Profile.
     */
    private final MetisProfile theProfile;

    /**
     * The program definition.
     */
    private final TethysProgram theApp;

    /**
     * Constructor.
     * @param pClass the program definition class
     * @throws OceanusException on error
     */
    public MetisProgram(final Class<? extends TethysProgram> pClass) throws OceanusException {
        try {
            /* Create a timer */
            theProfile = new MetisProfile("StartUp");

            /* Configure log4j */
            TethysLogConfig.configureLog4j();

            /* Obtain program details */
            theApp = pClass.newInstance();

        } catch (InstantiationException
                | IllegalAccessException e) {
            throw new MetisDataException("failed to access program definitions", e);
        }
    }

    /**
     * Obtain the profile.
     * @return the profile
     */
    public MetisProfile getProfile() {
        return theProfile;
    }

    /**
     * Obtain the program definition.
     * @return the definition
     */
    public TethysProgram getProgramDefinitions() {
        return theApp;
    }
}
