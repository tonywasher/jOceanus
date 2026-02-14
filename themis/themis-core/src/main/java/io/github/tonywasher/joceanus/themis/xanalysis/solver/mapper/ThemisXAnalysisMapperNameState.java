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

package io.github.tonywasher.joceanus.themis.xanalysis.solver.mapper;

import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisId;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclaration;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.expr.ThemisXAnalysisExprTypePattern;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeParameter;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.node.ThemisXAnalysisNodeVariable;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.stmt.ThemisXAnalysisStatement;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Name state.
 */
public class ThemisXAnalysisMapperNameState {
    /**
     * The nameState stack.
     */
    private final Deque<ThemisXAnalysisMapperNameMap> theStack;

    /**
     * The active nameState.
     */
    private ThemisXAnalysisMapperNameMap theName;

    /**
     * Constructor.
     */
    ThemisXAnalysisMapperNameState() {
        /* create stack */
        theStack = new ArrayDeque<>();

        /* Create initial Name Map */
        theName = new ThemisXAnalysisMapperNameMap();
    }

    /**
     * Reset.
     */
    void reset() {
        theStack.clear();
        theName = new ThemisXAnalysisMapperNameMap();
    }

    /**
     * Process the start of an instance.
     *
     * @param pInstance the instance
     * @return should cleanUp be called after processing this instance?
     */
    boolean processInstance(final ThemisXAnalysisInstance pInstance) {
        /* Determine whether we should bump the stack */
        final boolean doBump = bumpStack(pInstance);
        if (doBump) {
            bumpStack();
        }

        /* Process interesting elements */
        if (pInstance instanceof ThemisXAnalysisNodeVariable myVar) {
            return processVariable(myVar);
        }
        if (pInstance instanceof ThemisXAnalysisNodeParameter myParam) {
            return processParameter(myParam);
        }
        if (pInstance instanceof ThemisXAnalysisExprTypePattern myPattern) {
            return processPattern(myPattern);
        }

        /* Return bump indication */
        return doBump;
    }

    /**
     * Bump the stack.
     */
    private void bumpStack() {
        theStack.push(theName);
        theName = new ThemisXAnalysisMapperNameMap(theName);
    }

    /**
     * cleanUp after stacked instance.
     */
    void cleanUpAfterInstance() {
        theName = theStack.pop();
    }

    /**
     * Look up type.
     *
     * @param pName the name of the type
     * @return the type
     */
    ThemisXAnalysisTypeInstance lookUpName(final String pName) {
        return theName.lookUpName(pName);
    }

    /**
     * Process variableDecl element.
     *
     * @param pElement the element
     * @return false
     */
    private boolean processVariable(final ThemisXAnalysisNodeVariable pElement) {
        theName.declareVariable(pElement);
        return false;
    }

    /**
     * Process parameter.
     *
     * @param pElement the parameter
     * @return false
     */
    private boolean processParameter(final ThemisXAnalysisNodeParameter pElement) {
        theName.declareParameter(pElement);
        return false;
    }

    /**
     * Process pattern element.
     *
     * @param pElement the element
     * @return false
     */
    private boolean processPattern(final ThemisXAnalysisExprTypePattern pElement) {
        theName.declarePattern(pElement);
        return false;
    }

    /**
     * Should an instance bump the name stack?
     *
     * @param pInstance the instance
     * @return true/false
     */
    private boolean bumpStack(final ThemisXAnalysisInstance pInstance) {
        /* Process interesting ids */
        final ThemisXAnalysisId myId = pInstance.getId();
        if (myId instanceof ThemisXAnalysisDeclaration myDeclType) {
            return bumpStack(myDeclType);
        }
        if (myId instanceof ThemisXAnalysisStatement myStmtType) {
            return bumpStack(myStmtType);
        }
        return false;
    }

    /**
     * Should a declaration bump the name stack?
     *
     * @param pDeclType the declaration type
     * @return true/false
     */
    private boolean bumpStack(final ThemisXAnalysisDeclaration pDeclType) {
        switch (pDeclType) {
            case CLASSINTERFACE:
            case ENUM:
            case RECORD:
            case METHOD:
            case CONSTRUCTOR:
                return true;
            default:
                return false;
        }
    }

    /**
     * Should a statement bump the name stack?
     *
     * @param pStmtType the declaration type
     * @return true/false
     */
    private boolean bumpStack(final ThemisXAnalysisStatement pStmtType) {
        switch (pStmtType) {
            case BLOCK:
            case FOR:
            case FOREACH:
            case TRY:
                return true;
            default:
                return false;
        }
    }

    /**
     * The cascading map of names.
     */
    private static class ThemisXAnalysisMapperNameMap {
        /**
         * The parent map.
         */
        private final ThemisXAnalysisMapperNameMap theParent;

        /**
         * The type variables map.
         */
        private final Map<String, ThemisXAnalysisTypeInstance> theNames;

        /**
         * Constructor.
         */
        ThemisXAnalysisMapperNameMap() {
            this(null);
        }

        /**
         * Constructor.
         *
         * @param pParent the parent
         */
        ThemisXAnalysisMapperNameMap(final ThemisXAnalysisMapperNameMap pParent) {
            theParent = pParent;
            theNames = new HashMap<>();
        }

        /**
         * Declare variable.
         *
         * @param pVar the variable declarator
         */
        void declareVariable(final ThemisXAnalysisNodeVariable pVar) {
            theNames.put(pVar.getName().toString(), pVar.getType());
        }

        /**
         * Declare parameter.
         *
         * @param pParam the parameter
         */
        void declareParameter(final ThemisXAnalysisNodeParameter pParam) {
            theNames.put(pParam.getName().toString(), pParam.getType());
        }

        /**
         * Declare pattern.
         *
         * @param pPattern the pattern
         */
        void declarePattern(final ThemisXAnalysisExprTypePattern pPattern) {
            theNames.put(pPattern.getName().toString(), pPattern.getType());
        }

        /**
         * Look up name.
         *
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
    }
}
