/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.themis.xanalysis.base;

import com.github.javaparser.ast.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Instance definitions.
 */
public interface ThemisXAnalysisInstance {
    /**
     * Obtain the node.
     * @return the node
     */
    Node getNode();

    /**
     * Obtain the id of the node.
     * @return the id
     */
    ThemisXAnalysisId getId();

    /**
     * Register child node.
     * @param pChild the child node
     */
    void registerChild(ThemisXAnalysisInstance pChild);

    /**
     * Obtain the parent.
     * @return the parent
     */
    ThemisXAnalysisInstance getParent();

    /**
     * Obtain the list of children.
     * @return the list of children
     */
    List<ThemisXAnalysisInstance> getChildren();

    /**
     * Select children.
     * @param pTest the predicate to select children
     * @return the list of selected children
     */
    List<ThemisXAnalysisInstance> discoverChildren(Predicate<ThemisXAnalysisInstance> pTest);

    /**
     * Select nodes from tree.
     * @param pList the list to populate
     * @param pTest the predicate to select nodes
     */
    void discoverNodes(List<ThemisXAnalysisInstance> pList,
                       Predicate<ThemisXAnalysisInstance> pTest);

    /**
     * Select nodes from tree.
     * @param pId the type of node
     * @return the list of selected nodes
     */
    default List<ThemisXAnalysisInstance> discoverNodes(final ThemisXAnalysisId pId) {
        return discoverNodes(n -> n.getId().equals(pId));
    }

    /**
     * Select nodes from tree.
     * @param pTest the predicate to select nodes
     * @return the list of selected nodes
     */
    default List<ThemisXAnalysisInstance> discoverNodes(final Predicate<ThemisXAnalysisInstance> pTest) {
        final List<ThemisXAnalysisInstance> myList = new ArrayList<>();
        discoverNodes(myList, pTest);
        return myList;
    }

    /**
     * The id.
     */
    interface ThemisXAnalysisId {
    }

    /**
     * The base declaration interface.
     */
    interface ThemisXAnalysisDeclarationInstance
            extends ThemisXAnalysisInstance {
    }

    /**
     * The base type interface.
     */
    interface ThemisXAnalysisTypeInstance
            extends ThemisXAnalysisInstance {
    }

    /**
     * The base node interface.
     */
    interface ThemisXAnalysisNodeInstance
            extends ThemisXAnalysisInstance {
    }

    /**
     * The base statement interface.
     */
    interface ThemisXAnalysisStatementInstance
            extends ThemisXAnalysisInstance {
    }

    /**
     * The base expression interface.
     */
    interface ThemisXAnalysisExpressionInstance
            extends ThemisXAnalysisInstance {
    }

    /**
     * The base module interface.
     */
    interface ThemisXAnalysisModuleInstance
            extends ThemisXAnalysisInstance {
    }

    /**
     * The base class interface.
     */
    interface ThemisXAnalysisClassInstance {
        /**
         * Obtain the name.
         * @return the name
         */
        String getName();

        /**
         * Obtain the fullName.
         * @return the fullName
         */
        String getFullName();

        /**
         * Obtain the modifiers.
         * @return the modifiers
         */
        default List<ThemisXAnalysisNodeInstance> getModifiers() {
            return Collections.emptyList();
        }

        /**
         * Obtain the body.
         * @return the body
         */
        default List<ThemisXAnalysisDeclarationInstance> getBody() {
            return Collections.emptyList();
        }

        /**
         * Obtain the extends types.
         * @return the extends
         */
        default List<ThemisXAnalysisTypeInstance> getExtends() {
            return Collections.emptyList();
        }

        /**
         * Obtain the implements types.
         * @return the implements
         */
        default List<ThemisXAnalysisTypeInstance> getImplements() {
            return Collections.emptyList();
        }

        /**
         * Obtain the type parameters.
         * @return the parameters
         */
        default List<ThemisXAnalysisTypeInstance> getTypeParameters() {
            return Collections.emptyList();
        }

        /**
         * Obtain the annotations.
         * @return the annotations
         */
        default List<ThemisXAnalysisExpressionInstance> getAnnotations() {
            return Collections.emptyList();
        }

        /**
         * is the class a local declaration?
         * @return true/false
         */
        default boolean isLocalDeclaration() {
            return false;
        }
    }

    /**
     * The base method interface.
     */
    interface ThemisXAnalysisMethodInstance {
        /**
         * Obtain the name.
         * @return the name
         */
        String getName();

        /**
         * Obtain the modifiers.
         * @return the modifiers
         */
        default List<ThemisXAnalysisNodeInstance> getModifiers() {
            return Collections.emptyList();
        }

        /**
         * Obtain the parameters.
         * @return the parameters
         */
        default List<ThemisXAnalysisNodeInstance> getParameters() {
            return Collections.emptyList();
        }

        /**
         * Obtain the thrown exceptions.
         * @return the thrown exceptions
         */
        default List<ThemisXAnalysisTypeInstance> getThrown() {
            return Collections.emptyList();
        }

        /**
         * Obtain the type parameters.
         * @return the parameters
         */
        default List<ThemisXAnalysisTypeInstance> getTypeParameters() {
            return Collections.emptyList();
        }

        /**
         * Obtain the body.
         * @return the body
         */
        ThemisXAnalysisStatementInstance getBody();

        /**
         * Obtain the annotations.
         * @return the annotations
         */
        default List<ThemisXAnalysisExpressionInstance> getAnnotations() {
            return Collections.emptyList();
        }
    }
}
