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
import io.github.tonywasher.joceanus.themis.xanalysis.parser.base.ThemisXAnalysisInstance.ThemisXAnalysisTypeInstance;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclClassInterface;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclConstructor;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclMethod;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.decl.ThemisXAnalysisDeclRecord;
import io.github.tonywasher.joceanus.themis.xanalysis.parser.type.ThemisXAnalysisTypeParameter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Type state.
 */
public class ThemisXAnalysisMapperTypeState {
    /**
     * The typeState stack.
     */
    private final Deque<ThemisXAnalysisMapperTypeMap> theStack;

    /**
     * The active typeState.
     */
    private ThemisXAnalysisMapperTypeMap theType;

    /**
     * Constructor.
     */
    ThemisXAnalysisMapperTypeState() {
        /* create stack */
        theStack = new ArrayDeque<>();

        /* Create initial Type Map */
        theType = new ThemisXAnalysisMapperTypeMap();
    }

    /**
     * Reset.
     */
    void reset() {
        theStack.clear();
        theType = new ThemisXAnalysisMapperTypeMap();
    }

    /**
     * Process the start of an instance.
     *
     * @param pInstance the instance
     * @return should cleanUp be called after processing this instance?
     */
    boolean processElement(final ThemisXAnalysisInstance pInstance) {
        /* Process interesting instances */
        if (pInstance instanceof ThemisXAnalysisDeclClassInterface myClass) {
            return processClass(myClass);
        }
        if (pInstance instanceof ThemisXAnalysisDeclRecord myRecord) {
            return processRecord(myRecord);
        }
        if (pInstance instanceof ThemisXAnalysisDeclMethod myMethod) {
            return processMethod(myMethod);
        }
        if (pInstance instanceof ThemisXAnalysisDeclConstructor myConstruct) {
            return processConstructor(myConstruct);
        }

        /* Note that there was no bump */
        return false;
    }

    /**
     * cleanUp after stacked instance.
     */
    void cleanUpAfterInstance() {
        theType = theStack.pop();
    }

    /**
     * Look up type.
     *
     * @param pName the name of the type
     * @return the type
     */
    ThemisXAnalysisTypeInstance lookUpType(final String pName) {
        return theType.lookUpType(pName);
    }

    /**
     * Process classInterface instance.
     *
     * @param pInstance the instance
     * @return true
     */
    private boolean processClass(final ThemisXAnalysisDeclClassInterface pInstance) {
        final boolean isStatic = pInstance.isInterface() || pInstance.isTopLevel() || pInstance.getModifiers().isStatic();
        theStack.push(theType);
        theType = new ThemisXAnalysisMapperTypeMap(isStatic ? null : theType);
        theType.declareTypeParams(pInstance.getTypeParameters());
        return true;
    }

    /**
     * Process record instance.
     *
     * @param pInstance the instance
     * @return true
     */
    private boolean processRecord(final ThemisXAnalysisDeclRecord pInstance) {
        theStack.push(theType);
        theType = new ThemisXAnalysisMapperTypeMap();
        theType.declareTypeParams(pInstance.getTypeParameters());
        return true;
    }

    /**
     * Process method instance.
     *
     * @param pInstance the instance
     * @return true
     */
    private boolean processMethod(final ThemisXAnalysisDeclMethod pInstance) {
        final boolean isStatic = pInstance.getModifiers().isStatic();
        theStack.push(theType);
        theType = new ThemisXAnalysisMapperTypeMap(isStatic ? null : theType);
        theType.declareTypeParams(pInstance.getTypeParameters());
        return true;
    }

    /**
     * Process constructor instance.
     *
     * @param pInstance the instance
     * @return true
     */
    private boolean processConstructor(final ThemisXAnalysisDeclConstructor pInstance) {
        theStack.push(theType);
        theType = new ThemisXAnalysisMapperTypeMap(theType);
        theType.declareTypeParams(pInstance.getTypeParameters());
        return true;
    }

    /**
     * The cascading map of types.
     */
    private static class ThemisXAnalysisMapperTypeMap {
        /**
         * The parent map.
         */
        private final ThemisXAnalysisMapperTypeMap theParent;

        /**
         * The type variables map.
         */
        private final Map<String, ThemisXAnalysisTypeParameter> theTypes;

        /**
         * Constructor.
         */
        ThemisXAnalysisMapperTypeMap() {
            this(null);
        }

        /**
         * Constructor.
         *
         * @param pParent the parent
         */
        ThemisXAnalysisMapperTypeMap(final ThemisXAnalysisMapperTypeMap pParent) {
            theParent = pParent;
            theTypes = new HashMap<>();
        }

        /**
         * Declare type parameters.
         *
         * @param pParams the parameters
         */
        void declareTypeParams(final List<ThemisXAnalysisTypeInstance> pParams) {
            for (ThemisXAnalysisTypeInstance myNode : pParams) {
                final ThemisXAnalysisTypeParameter myParam = (ThemisXAnalysisTypeParameter) myNode;
                theTypes.put(myParam.getName(), myParam);
            }
        }

        /**
         * Look up type.
         *
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
}
