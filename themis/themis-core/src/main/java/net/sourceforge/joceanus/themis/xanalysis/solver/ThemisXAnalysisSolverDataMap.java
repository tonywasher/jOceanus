/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012-2026 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.solver;

import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisNodeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import net.sourceforge.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclField;
import net.sourceforge.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprVarDecl;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeParameter;
import net.sourceforge.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeVariable;
import net.sourceforge.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisTypeParameter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data Map.
 */
public class ThemisXAnalysisSolverDataMap {
    /**
     * Parent map.
     */
    private final ThemisXAnalysisSolverDataMap theParent;

    /**
     * The named variables map.
     */
    private final Map<String, ThemisXAnalysisTypeInstance> theNames;

    /**
     * The type variables map.
     */
    private final Map<String, ThemisXAnalysisTypeParameter> theTypes;

    /**
     * Constructor.
     */
    ThemisXAnalysisSolverDataMap() {
        this(null);
    }

    /**
     * Constructor.
     * @param pParent the parent
     */
    ThemisXAnalysisSolverDataMap(final ThemisXAnalysisSolverDataMap pParent) {
        theParent = pParent;
        theNames = new HashMap<>();
        theTypes = new HashMap<>();
    }

    /**
     * Declare field.
     * @param pField the field
     */
    void declareField(final ThemisXAnalysisDeclField pField) {
        for (ThemisXAnalysisNodeInstance myNode : pField.getVariables()) {
            final ThemisXAnalysisNodeVariable myVar = (ThemisXAnalysisNodeVariable) myNode;
            theNames.put(myVar.getName().toString(), myVar.getType());
        }
    }

    /**
     * Declare variables.
     * @param pVar the variable declarator
     */
    void declareVars(final ThemisXAnalysisExprVarDecl pVar) {
        for (ThemisXAnalysisNodeInstance myNode : pVar.getVariables()) {
            final ThemisXAnalysisNodeVariable myVar = (ThemisXAnalysisNodeVariable) myNode;
            theNames.put(myVar.getName().toString(), myVar.getType());
        }
    }

    /**
     * Declare parameters.
     * @param pParams the parameters
     */
    void declareParams(final List<ThemisXAnalysisNodeInstance> pParams) {
        for (ThemisXAnalysisNodeInstance myNode : pParams) {
            final ThemisXAnalysisNodeParameter myParam = (ThemisXAnalysisNodeParameter) myNode;
            theNames.put(myParam.getName().toString(), myParam.getType());
        }
    }

    /**
     * Declare type parameters.
     * @param pParams the parameters
     */
    void declareTypeParams(final List<ThemisXAnalysisTypeInstance> pParams) {
        for (ThemisXAnalysisTypeInstance myNode : pParams) {
            final ThemisXAnalysisTypeParameter myParam = (ThemisXAnalysisTypeParameter) myNode;
            theTypes.put(myParam.getName(), myParam);
        }
    }

    /**
     * Look up name.
     * @param pName the name of the variable
     * @return the type
     */
    ThemisXAnalysisTypeInstance lookUpName(final String pName) {
        /* Look up name in map */
        final ThemisXAnalysisTypeInstance myType = theNames.get(pName);
        if (myType != null) {
            return myType;
        }

        /* If we did not find the name, try the parent map */
        return theParent == null ? null : theParent.lookUpName(pName);
    }

    /**
     * Look up type.
     * @param pName the name of the type
     * @return the type
      */
    ThemisXAnalysisTypeInstance lookUpType(final String pName) {
        /* Look up name in map */
        final ThemisXAnalysisTypeInstance myType = theTypes.get(pName);
        if (myType != null) {
            return myType;
        }

        /* If we did not find the type, try the parent map */
        return theParent == null ? null : theParent.lookUpType(pName);
    }
}
