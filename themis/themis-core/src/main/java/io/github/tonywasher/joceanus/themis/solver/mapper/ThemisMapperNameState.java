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

import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.exc.ThemisDataException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisClassInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisDeclarationInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisId;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisTypeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisModifierList;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclEnum;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclEnumValue;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclField;
import io.github.tonywasher.joceanus.themis.parser.decl.ThemisDeclaration;
import io.github.tonywasher.joceanus.themis.parser.expr.ThemisExprTypePattern;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeParameter;
import io.github.tonywasher.joceanus.themis.parser.node.ThemisNodeVariable;
import io.github.tonywasher.joceanus.themis.parser.stmt.ThemisStatement;
import io.github.tonywasher.joceanus.themis.parser.type.ThemisTypeClassInterface;
import io.github.tonywasher.joceanus.themis.solver.reflect.ThemisReflectExternal;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

/**
 * Name state.
 */
public class ThemisMapperNameState {
    /**
     * The nameState stack.
     */
    private final Deque<ThemisMapperNameMap> theStack;

    /**
     * The active nameState.
     */
    private ThemisMapperNameMap theName;

    /**
     * Constructor.
     */
    ThemisMapperNameState() {
        /* create stack */
        theStack = new ArrayDeque<>();

        /* Create initial Name Map */
        theName = new ThemisMapperNameMap();
    }

    /**
     * Reset.
     */
    void reset() {
        theStack.clear();
        theName = new ThemisMapperNameMap();
    }

    /**
     * Process the start of an instance.
     *
     * @param pInstance the instance
     * @return should cleanUp be called after processing this instance?
     * @throws OceanusException on error
     */
    boolean processInstance(final ThemisInstance pInstance) throws OceanusException {
        /* Determine whether we should bump the stack */
        final boolean doBump = bumpStack(pInstance);
        if (doBump) {
            bumpStack();
        }

        /* Process interesting elements */
        if (pInstance instanceof ThemisClassInstance myClass) {
            processClass(myClass);
        }
        if (pInstance instanceof ThemisDeclEnum myEnum) {
            return processEnum(myEnum);
        }
        if (pInstance instanceof ThemisNodeVariable myVar) {
            return processVariable(myVar);
        }
        if (pInstance instanceof ThemisNodeParameter myParam) {
            return processParameter(myParam);
        }
        if (pInstance instanceof ThemisExprTypePattern myPattern) {
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
        theName = new ThemisMapperNameMap(theName);
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
    ThemisTypeInstance lookUpName(final String pName) {
        return theName.lookUpName(pName);
    }

    /**
     * Process enumValue.
     *
     * @param pElement the element
     * @return false
     */
    private boolean processEnum(final ThemisDeclEnum pElement) {
        final ThemisMapperTypeRef myRef = new ThemisMapperTypeRef(pElement);
        for (ThemisDeclarationInstance myInstance : pElement.getValues()) {
            final ThemisDeclEnumValue myValue = (ThemisDeclEnumValue) myInstance;
            theName.declareEnum(myRef, myValue);
        }
        return false;
    }

    /**
     * Process variableDecl element.
     *
     * @param pElement the element
     * @return false
     */
    private boolean processVariable(final ThemisNodeVariable pElement) {
        theName.declareVariable(pElement);
        return false;
    }

    /**
     * Process parameter.
     *
     * @param pElement the parameter
     * @return false
     */
    private boolean processParameter(final ThemisNodeParameter pElement) {
        theName.declareParameter(pElement);
        return false;
    }

    /**
     * Process pattern element.
     *
     * @param pElement the element
     * @return false
     */
    private boolean processPattern(final ThemisExprTypePattern pElement) {
        theName.declarePattern(pElement);
        return false;
    }

    /**
     * Process class element.
     *
     * @param pClass the element
     * @throws OceanusException on error
     */
    private void processClass(final ThemisClassInstance pClass) throws OceanusException {
        for (ThemisTypeInstance myClass : pClass.getExtends()) {
            final ThemisTypeClassInterface myExtend = (ThemisTypeClassInterface) myClass;
            ThemisClassInstance myClassInstance = myExtend.getClassInstance();
            if (myClassInstance instanceof ThemisReflectExternal myExternal) {
                myClassInstance = myExternal.getClassInstance();
            }
            if (myClassInstance == null) {
                throw new ThemisDataException("Class Instance not found: " + pClass.getFullName());
            }

            /* Loop through all the field declarations */
            for (ThemisInstance myChild : myClassInstance.getBody()) {
                if (myChild instanceof ThemisDeclField myField) {
                    processField(myField);
                }
            }

            /* Follow the chain */
            processClass(myClassInstance);
        }
    }

    /**
     * Process field element.
     *
     * @param pField the element
     */
    private void processField(final ThemisDeclField pField) {
        /* If the field is public or protected */
        final ThemisModifierList myModifiers = pField.getModifiers();
        if (myModifiers.isProtected() || myModifiers.isPublic()) {
            for (ThemisNodeInstance myVarNode : pField.getVariables()) {
                final ThemisNodeVariable myVar = (ThemisNodeVariable) myVarNode;
                processVariable(myVar);
            }
        }
    }

    /**
     * Should an instance bump the name stack?
     *
     * @param pInstance the instance
     * @return true/false
     */
    private boolean bumpStack(final ThemisInstance pInstance) {
        /* Process interesting ids */
        final ThemisId myId = pInstance.getId();
        if (myId instanceof ThemisDeclaration myDeclType) {
            return bumpStack(myDeclType);
        }
        if (myId instanceof ThemisStatement myStmtType) {
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
    private boolean bumpStack(final ThemisDeclaration pDeclType) {
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
    private boolean bumpStack(final ThemisStatement pStmtType) {
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
    private static class ThemisMapperNameMap {
        /**
         * The parent map.
         */
        private final ThemisMapperNameMap theParent;

        /**
         * The type variables map.
         */
        private final Map<String, ThemisTypeInstance> theNames;

        /**
         * Constructor.
         */
        ThemisMapperNameMap() {
            this(null);
        }

        /**
         * Constructor.
         *
         * @param pParent the parent
         */
        ThemisMapperNameMap(final ThemisMapperNameMap pParent) {
            theParent = pParent;
            theNames = new HashMap<>();
        }

        /**
         * Declare enumValue.
         *
         * @param pRef  the reference
         * @param pEnum the enumValue
         */
        void declareEnum(final ThemisMapperTypeRef pRef,
                         final ThemisDeclEnumValue pEnum) {
            theNames.put(pEnum.getName().toString(), pRef);
        }

        /**
         * Declare variable.
         *
         * @param pVar the variable declarator
         */
        void declareVariable(final ThemisNodeVariable pVar) {
            theNames.put(pVar.getName().toString(), pVar.getType());
        }

        /**
         * Declare parameter.
         *
         * @param pParam the parameter
         */
        void declareParameter(final ThemisNodeParameter pParam) {
            theNames.put(pParam.getName().toString(), pParam.getType());
        }

        /**
         * Declare pattern.
         *
         * @param pPattern the pattern
         */
        void declarePattern(final ThemisExprTypePattern pPattern) {
            theNames.put(pPattern.getName().toString(), pPattern.getType());
        }

        /**
         * Look up name.
         *
         * @param pName the name of the variable
         * @return the type
         */
        ThemisTypeInstance lookUpName(final String pName) {
            /* Look up name in map */
            final ThemisTypeInstance myType = theNames.get(pName);
            if (myType != null) {
                return myType;
            }

            /* If we did not find the name, try the parent map */
            return theParent == null ? null : theParent.lookUpName(pName);
        }
    }
}
