/*******************************************************************************
 * Themis: Java Project Framework
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
package net.sourceforge.joceanus.jthemis.analysis;

import java.util.EnumMap;
import java.util.Map;

import net.sourceforge.joceanus.jtethys.OceanusException;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericBase;
import net.sourceforge.joceanus.jthemis.analysis.ThemisAnalysisGeneric.ThemisAnalysisGenericVarList;

/**
 * Properties for an element.
 */
public final class ThemisAnalysisProperties {
    /**
     * Null properties.
     */
    static final ThemisAnalysisProperties NULL = new ThemisAnalysisProperties(true);

    /**
     * Is this the null entry?
     */
    private final boolean isNull;

    /**
     * The map of Modifiers.
     */
    private final Map<ThemisAnalysisModifier, Boolean> theModifiers;

    /**
     * The generic variable list.
     */
    private ThemisAnalysisGeneric theGenericVars;

    /**
     * Constructor.
     */
    private ThemisAnalysisProperties() {
        this(false);
    }

    /**
     * Constructor.
     * @param pNull is this the null element?
     */
    private ThemisAnalysisProperties(final boolean pNull) {
        isNull = pNull;
        theModifiers = isNull
                       ? null
                       : new EnumMap<>(ThemisAnalysisModifier.class);
    }

    /**
     * Is the modifier present?
     * @param pModifier the modifier
     * @return true/false
     */
    boolean hasModifier(final ThemisAnalysisModifier pModifier) {
        return !isNull && theModifiers.containsKey(pModifier);
    }

    /**
     * Are generic variables present?
     * @return true/false
     */
    boolean hasGeneric() {
        return theGenericVars != null;
    }

    /**
     * Set modifier.
     * @param pModifier the modifier
     * @return the updated properties
     */
    ThemisAnalysisProperties setModifier(final ThemisAnalysisModifier pModifier) {
        final ThemisAnalysisProperties myProps = isNull
                                                 ? new ThemisAnalysisProperties()
                                                 : this;
        myProps.theModifiers.put(pModifier, Boolean.TRUE);
        return myProps;
    }

    /**
     * Set the generic variable list.
     * @param pGeneric the generic variables
     * @return the updated properties
     */
    ThemisAnalysisProperties setGenericVariables(final ThemisAnalysisGeneric pGeneric) {
        final ThemisAnalysisProperties myProps = isNull
                                                 ? new ThemisAnalysisProperties()
                                                 : this;
        myProps.theGenericVars = pGeneric;
        return myProps;
    }

    /**
     * Resolve the generic variables.
     * @param pParser the parser
     * @throws OceanusException on error
     */
    void resolveGeneric(final ThemisAnalysisParser pParser) throws OceanusException {
        /* Resolve any generic base instance */
        if (theGenericVars instanceof ThemisAnalysisGenericBase) {
            /* Resolve the variables */
            final ThemisAnalysisGeneric myVars = new ThemisAnalysisGenericVarList(pParser, (ThemisAnalysisGenericBase) theGenericVars);

            /* Only record the parsed variables if the parser is nonTemporary */
            if (!pParser.isTemporary()) {
                theGenericVars = myVars;
            }
        }
    }
}
