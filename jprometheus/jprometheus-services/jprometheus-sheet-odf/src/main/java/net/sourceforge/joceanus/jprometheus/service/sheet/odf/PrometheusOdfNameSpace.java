/*******************************************************************************
 * Prometheus: Application Framework
 * Copyright 2012,2022 Tony Washer
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
package net.sourceforge.joceanus.jprometheus.service.sheet.odf;

/**
 * NameSpace definitions.
 */
public enum PrometheusOdfNameSpace {
    /**
     * Office.
     */
    OFFICE("office", "urn:oasis:names:tc:opendocument:xmlns:office:1.0"),

    /**
     * Table.
     */
    TABLE("table", "urn:oasis:names:tc:opendocument:xmlns:table:1.0"),

    /**
     * Text.
     */
    TEXT("text", "urn:oasis:names:tc:opendocument:xmlns:text:1.0"),

    /**
     * Number.
     */
    NUMBER("number", "urn:oasis:names:tc:opendocument:xmlns:datastyle:1.0"),

    /**
     * Format.
     */
    FORMAT("fo", "urn:oasis:names:tc:opendocument:xmlns:xsl-fo-compatible:1.0"),

    /**
     * Style.
     */
    STYLE("style", "urn:oasis:names:tc:opendocument:xmlns:style:1.0");

    /**
     * The prefix.
     */
    private final String thePrefix;


    /**
     * The nameSpace.
     */
    private final String theNameSpace;
    /**
     * Constructor.
     * @param pPrefix the prefix
     * @param pNameSpace the nameSpace
     */
    PrometheusOdfNameSpace(final String pPrefix,
                           final String pNameSpace) {
        thePrefix = pPrefix;
        theNameSpace = pNameSpace;
    }

    /**
     * Obtain the prefix.
     * @return the prefix.
     */
    public String getPrefix() {
        return thePrefix;
    }

    /**
     * Obtain the nameSpace.
     * @return the nameSpace.
     */
    public String getNameSpace() {
        return theNameSpace;
    }

    /**
     * Get qualified name.
     * @param pItem the item
     * @return the qualified name.
     */
    static String buildQualifiedName(final PrometheusOdfItem pItem) {
        final StringBuilder myBuilder = new StringBuilder();
        myBuilder.append(pItem.getNameSpace().getPrefix());
        myBuilder.append(':');
        myBuilder.append(pItem.getName());
        return myBuilder.toString();
    }

    /**
     * Element definition.
     */
    public interface PrometheusOdfItem {
        /**
         * Obtain the name.
         * @return the name
         */
        String getName();

        /**
         * Obtain the qualified name.
         * @return the name
         */
        String getQualifiedName();

        /**
         * Obtain the nameSpace.
         * @return the nameSpace
         */
        PrometheusOdfNameSpace getNameSpace();
    }
}
