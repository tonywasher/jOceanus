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

package io.github.tonywasher.joceanus.themis.parser.xmaven;

import io.github.tonywasher.joceanus.metis.data.MetisDataItem.MetisDataMap;
import io.github.tonywasher.joceanus.metis.field.MetisFieldItem;
import io.github.tonywasher.joceanus.metis.field.MetisFieldSet;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.oceanus.format.OceanusDataFormatter;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisChar;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisDataResource;

import java.util.HashMap;
import java.util.Map;

/**
 * Maven dependency cache.
 */
public class ThemisXMavenVersionCache
        implements MetisDataMap<String, String>, MetisFieldItem {
    /**
     * Report fields.
     */
    private static final MetisFieldSet<ThemisXMavenVersionCache> FIELD_DEFS = MetisFieldSet.newFieldSet(ThemisXMavenVersionCache.class);

    /*
     * Declare Fields.
     */
    static {
        FIELD_DEFS.declareLocalField(ThemisDataResource.DATA_PARENT, ThemisXMavenVersionCache::getParent);
    }

    /**
     * The map of prefix to version.
     */
    private final Map<String, String> theMap;

    /**
     * The parent cache.
     */
    private ThemisXMavenVersionCache theParent;

    /**
     * Constructor.
     */
    ThemisXMavenVersionCache() {
        theMap = new HashMap<>();
    }

    @Override
    public MetisFieldSet<ThemisXMavenVersionCache> getDataFieldSet() {
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
    private ThemisXMavenVersionCache getParent() {
        return theParent;
    }

    /**
     * Set the parent cache.
     *
     * @param pParent the parent cache
     */
    void setParent(final ThemisXMavenVersionCache pParent) {
        theParent = pParent;
    }

    /**
     * Add dependency to cache.
     *
     * @param pId the id to add to the cache
     * @throws OceanusException on error
     */
    public void addToCache(final ThemisXMavenId pId) throws OceanusException {
        /* Determine the prefix and the proposed version */
        final String myPrefix = buildPrefix(pId);
        final String myProposed = pId.getVersion();

        /* Reject null version */
        if (myProposed == null) {
            throw new ThemisDataException("Proposed version for " + myPrefix + " is null");
        }

        /* Update the dependency */
        theMap.put(myPrefix, myProposed);
    }

    /**
     * Validate version against cache.
     *
     * @param pId the version to check against cache
     * @return the autoCorrected id.
     * @throws OceanusException on error
     */
    public ThemisXMavenId lookUpVersion(final ThemisXMavenId pId) throws OceanusException {
        /* Determine the prefix and the proposed version */
        final String myPrefix = buildPrefix(pId);
        final String myProposed = pId.getVersion();

        /* Look for existing entry */
        final String myVersion = lookUpVersionInCache(myPrefix);

        /* If the prefix is in the cache */
        if (myVersion != null) {
            /* Handle no proposed version */
            if (myProposed == null) {
                return new ThemisXMavenId(pId, myVersion);

                /* Check proposed version is valid */
            } else if (myProposed.equals(myVersion)) {
                return pId;

                /* Reject conflict */
            } else {
                throw new ThemisDataException("Conflicting explicit dependency version for " + myPrefix
                        + " Required=" + myVersion
                        + " Proposed=" + myProposed);
            }

            /* else prefix is not in cache */
        } else {
            /* Reject if version is unknown */
            if (myProposed == null) {
                throw new ThemisDataException("Proposed version for " + myPrefix + " is null");
            }

            /* Add new dependency and return id */
            theMap.put(myPrefix, pId.getVersion());
            return pId;
        }
    }

    /**
     * Import dependencies.
     *
     * @param pChild the child dependency cache
     * @throws OceanusException on error
     */
    void importDependencies(final ThemisXMavenVersionCache pChild) throws OceanusException {
        /* Loop through the child entries */
        for (Map.Entry<String, String> myEntry : pChild.getUnderlyingMap().entrySet()) {
            /* Access details */
            final String myPrefix = myEntry.getKey();
            final String myProposed = myEntry.getValue();

            /* Look for existing entry */
            final String myVersion = lookUpVersionInCache(myPrefix);

            /* Check for mismatch */
            if (myVersion != null && !myVersion.equals(myProposed)) {
                throw new ThemisDataException("Conflicting imported version for " + myPrefix
                        + " Required=" + myVersion
                        + " Proposed=" + myProposed);
            }

            /* Store the entry */
            theMap.put(myPrefix, myProposed);
        }
    }

    /**
     * Look up prefix in chain of caches.
     *
     * @param pPrefix the prefix
     * @return the version (or null)
     */
    private String lookUpVersionInCache(final String pPrefix) {
        /* Look for existing entry */
        final String myVersion = theMap.get(pPrefix);
        return (theParent == null || myVersion != null) ? myVersion : theParent.lookUpVersionInCache(pPrefix);
    }

    /**
     * Build prefix.
     *
     * @param pId the id
     * @return the prefix
     */
    private String buildPrefix(final ThemisXMavenId pId) {
        return pId.getGroupId() + ThemisChar.COLON + pId.getArtifactId();
    }
}
