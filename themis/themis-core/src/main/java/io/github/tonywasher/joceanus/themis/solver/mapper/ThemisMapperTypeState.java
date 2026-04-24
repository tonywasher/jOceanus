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

package io.github.tonywasher.joceanus.themis.solver.mapper;

import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclClassInterface;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclConstructor;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclMethod;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclRecord;
import io.github.tonywasher.joceanus.themis.parser.type.ThemisTypeParameter;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Type state.
 */
public class ThemisMapperTypeState {
    /**
     * The typeState stack.
     */
    private final Deque<ThemisMapperTypeMap> theStack;

    /**
     * The active typeState.
     */
    private ThemisMapperTypeMap theType;

    /**
     * Constructor.
     */
    ThemisMapperTypeState() {
        /* create stack */
        theStack = new ArrayDeque<>();

        /* Create initial Type Map */
        theType = new ThemisMapperTypeMap();
    }

    /**
     * Reset.
     */
    void reset() {
        theStack.clear();
        theType = new ThemisMapperTypeMap();
    }

    /**
     * Process the start of an instance.
     *
     * @param pInstance the instance
     * @return should cleanUp be called after processing this instance?
     */
    boolean processInstance(final ThemisInstance pInstance) {
        /* Process interesting instances */
        if (pInstance instanceof ThemisDeclClassInterface myClass) {
            return processClass(myClass);
        }
        if (pInstance instanceof ThemisDeclRecord myRecord) {
            return processRecord(myRecord);
        }
        if (pInstance instanceof ThemisDeclMethod myMethod) {
            return processMethod(myMethod);
        }
        if (pInstance instanceof ThemisDeclConstructor myConstruct) {
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
    ThemisTypeInstance lookUpType(final String pName) {
        return theType.lookUpType(pName);
    }

    /**
     * Process classInterface instance.
     *
     * @param pInstance the instance
     * @return true
     */
    private boolean processClass(final ThemisDeclClassInterface pInstance) {
        final boolean isStatic = pInstance.isInterface() || pInstance.isTopLevel() || pInstance.getModifiers().isStatic();
        theStack.push(theType);
        theType = new ThemisMapperTypeMap(isStatic ? null : theType);
        theType.declareTypeParams(pInstance.getTypeParameters());
        return true;
    }

    /**
     * Process record instance.
     *
     * @param pInstance the instance
     * @return true
     */
    private boolean processRecord(final ThemisDeclRecord pInstance) {
        theStack.push(theType);
        theType = new ThemisMapperTypeMap();
        theType.declareTypeParams(pInstance.getTypeParameters());
        return true;
    }

    /**
     * Process method instance.
     *
     * @param pInstance the instance
     * @return true
     */
    private boolean processMethod(final ThemisDeclMethod pInstance) {
        final boolean isStatic = pInstance.getModifiers().isStatic();
        theStack.push(theType);
        theType = new ThemisMapperTypeMap(isStatic ? null : theType);
        theType.declareTypeParams(pInstance.getTypeParameters());
        return true;
    }

    /**
     * Process constructor instance.
     *
     * @param pInstance the instance
     * @return true
     */
    private boolean processConstructor(final ThemisDeclConstructor pInstance) {
        theStack.push(theType);
        theType = new ThemisMapperTypeMap(theType);
        theType.declareTypeParams(pInstance.getTypeParameters());
        return true;
    }

    /**
     * The cascading map of types.
     */
    private static class ThemisMapperTypeMap {
        /**
         * The parent map.
         */
        private final ThemisMapperTypeMap theParent;

        /**
         * The type variables map.
         */
        private final Map<String, ThemisTypeParameter> theTypes;

        /**
         * Constructor.
         */
        ThemisMapperTypeMap() {
            this(null);
        }

        /**
         * Constructor.
         *
         * @param pParent the parent
         */
        ThemisMapperTypeMap(final ThemisMapperTypeMap pParent) {
            theParent = pParent;
            theTypes = new HashMap<>();
        }

        /**
         * Declare type parameters.
         *
         * @param pParams the parameters
         */
        void declareTypeParams(final List<ThemisTypeInstance> pParams) {
            for (ThemisTypeInstance myNode : pParams) {
                final ThemisTypeParameter myParam = (ThemisTypeParameter) myNode;
                theTypes.put(myParam.getName(), myParam);
            }
        }

        /**
         * Look up type.
         *
         * @param pName the name of the type
         * @return the type
         */
        ThemisTypeInstance lookUpType(final String pName) {
            /* Look up name in map */
            final ThemisTypeInstance myType = theTypes.get(pName);
            if (myType != null) {
                return myType;
            }

            /* If we did not find the type, try the parent map */
            return theParent == null ? null : theParent.lookUpType(pName);
        }
    }
}
