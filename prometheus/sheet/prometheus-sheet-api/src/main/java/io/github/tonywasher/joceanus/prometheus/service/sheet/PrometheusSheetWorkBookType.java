/*
 * Prometheus: Application Framework
 * Copyright 2012-2026. Tony Washer
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
 */
package io.github.tonywasher.joceanus.prometheus.service.sheet;

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.resource.OceanusBundleId;

/**
 * WorkBook types.
 */
public enum PrometheusSheetWorkBookType {
    /**
     * Excel xls.
     */
    EXCELXLS("xls"),

    /**
     * Oasis.
     */
    OASIS("ods");

    /**
     * The String name.
     */
    private String theName;

    /**
     * File extension.
     */
    private final String theExtension;

    /**
     * Constructor.
     *
     * @param pExtension the extension code
     */
    PrometheusSheetWorkBookType(final String pExtension) {
        theExtension = pExtension;
    }

    /**
     * Obtain the extension.
     *
     * @return the extension
     */
    public String getExtension() {
        return "."
                + theExtension;
    }

    /**
     * Determine workBookType of file.
     *
     * @param pFileName the filename
     * @return the workBookType
     * @throws OceanusException on error
     */
    public static PrometheusSheetWorkBookType determineType(final String pFileName) throws OceanusException {
        /* Loop through all values */
        for (PrometheusSheetWorkBookType myType : values()) {
            /* If we have matched the type */
            if (pFileName.endsWith(myType.getExtension())) {
                /* Return it */
                return myType;
            }
        }

        /* Unrecognised type */
        throw new PrometheusSheetException("Unrecognised file type "
                + pFileName);
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = bundleIdForWorkBookType(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Obtain the resource bundleId for the workBookType.
     *
     * @param pType the type
     * @return the resource bundleId
     */
    private static OceanusBundleId bundleIdForWorkBookType(final PrometheusSheetWorkBookType pType) {
        return switch (pType) {
            case EXCELXLS -> PrometheusSheetResource.WORKBOOK_EXCELXLS;
            case OASIS -> PrometheusSheetResource.WORKBOOK_OASIS;
            default -> throw new IllegalArgumentException();
        };
    }
}
