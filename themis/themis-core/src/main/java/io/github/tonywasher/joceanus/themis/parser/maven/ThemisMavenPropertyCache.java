/*
 * Themis: Java Project Framework
 * Copyright 2026. Tony Washer
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

package io.github.tonywasher.joceanus.themis.parser.maven;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataMap;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.base.OceanusSystem;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;
import io.github.tonywasher.joceanus.themis.parser.maven.ThemisMavenId.ThemisXElementParser;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Maven property cache.
 */
public class ThemisMavenPropertyCache
        implements ThemisXElementParser, MetisDataMap<String, String>, MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisMavenPropertyCache> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisMavenPropertyCache.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_PARENT, ThemisMavenPropertyCache::getParent);
    }

    /**
     * The property prefix.
     */
    private static final String PROP_START = "${";

    /**
     * The property suffix.
     */
    private static final String PROP_END = "}";

    /**
     * The properties.
     */
    private final Map<String, String> theMap;

    /**
     * The parent cache.
     */
    private ThemisMavenPropertyCache theParent;

    /**
     * Constructor.
     */
    ThemisMavenPropertyCache() {
        theMap = new HashMap<>();
        setProperty("javafx.platform", OceanusSystem.determineSystem().getClassifier());
    }

    @Override
    public MetisFieldSet<ThemisMavenPropertyCache> getDataFieldSet() {
        return FIELD_DEFS;
    }

    @Override
    public String formatObject(final OceanusDataFormatter pFormatter) {
        return getClass().getSimpleName();
    }

    @Override
    public Map<String, String> getUnderlyingMap() {
        return theMap;
    }

    /**
     * Obtain the Parent.
     *
     * @return the parent
     */
    private ThemisMavenPropertyCache getParent() {
        return theParent;
    }

    /**
     * Set the parent cache.
     *
     * @param pParent the parent cache
     */
    void setParent(final ThemisMavenPropertyCache pParent) {
        theParent = pParent;
    }

    /**
     * Set the property.
     *
     * @param pName  the name of the property
     * @param pValue the value of the property
     */
    void setProperty(final String pName,
                     final String pValue) {
        final String myProperty = getPropertyKey(pName);
        theMap.put(myProperty, pValue);
    }

    /**
     * Get the property.
     *
     * @param pName the name of the property
     * @return the value of the property (or null)
     */
    String getProperty(final String pName) {
        final String myProperty = getPropertyKey(pName);
        return theMap.get(myProperty);
    }

    /**
     * Obtain the property key.
     *
     * @param pName the name of the property
     * @return the property key
     */
    private String getPropertyKey(final String pName) {
        return PROP_START + pName + PROP_END;
    }

    @Override
    public String getElementValue(final Element pElement,
                                  final String pValue) throws OceanusException {
        /* Return null if no element */
        if (pElement == null) {
            return null;
        }

        /* Loop through the children */
        for (Node myChild = pElement.getFirstChild();
             myChild != null;
             myChild = myChild.getNextSibling()) {
            /* Return result if we have a match */
            if (myChild instanceof Element
                    && pValue.equals(myChild.getNodeName())) {
                return replaceProperty(myChild.getTextContent());
            }
        }

        /* Not found */
        return null;
    }

    /**
     * Replace properties.
     *
     * @param pValue the value
     * @return the value or the replaced property
     */
    private String replaceProperty(final String pValue) throws ThemisDataException {
        /* Loop while there is a property to replace */
        String myCurrent = pValue;
        while (containsProperty(myCurrent)) {
            /* Replace the next property */
            final String myResult = replaceNextProperty(myCurrent);

            /* If we failed to change the property */
            if (myResult.equals(myCurrent)) {
                throw new ThemisDataException("Unknown embedded property - " + myCurrent);
            }

            /* Switch result */
            myCurrent = myResult;
        }
        return myCurrent;
    }

    /**
     * Does the value contain a property?
     *
     * @param pValue the value
     * @return true/false
     */
    private boolean containsProperty(final String pValue) {
        final int myIndex = pValue.indexOf(PROP_START);
        return myIndex != -1;
    }

    /**
     * Replace next property.
     *
     * @param pValue the value
     * @return the value or the replaced property
     */
    private String replaceNextProperty(final String pValue) {
        for (Map.Entry<String, String> myEntry : theMap.entrySet()) {
            if (pValue.contains(myEntry.getKey())) {
                return pValue.replace(myEntry.getKey(), myEntry.getValue());
            }
        }
        return theParent != null ? theParent.replaceNextProperty(pValue) : pValue;
    }
}
