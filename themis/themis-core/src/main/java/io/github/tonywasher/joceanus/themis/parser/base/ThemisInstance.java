/*
 * Themis: Java Project Framework
 * Copyright 2012-2026. Tony Washer
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
package io.github.tonywasher.joceanus.themis.parser.base;

import com.github.javaparser.ast.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

/**
 * Instance definitions.
 */
public interface ThemisInstance {
    /**
     * Obtain the node.
     *
     * @return the node
     */
    Node getNode();

    /**
     * Obtain the id of the node.
     *
     * @return the id
     */
    ThemisId getId();

    /**
     * Register child node.
     *
     * @param pChild the child node
     */
    void registerChild(ThemisInstance pChild);

    /**
     * Obtain the parent.
     *
     * @return the parent
     */
    ThemisInstance getParent();

    /**
     * Obtain the list of children.
     *
     * @return the list of children
     */
    List<ThemisInstance> getChildren();

    /**
     * Select children from tree.
     *
     * @param pId the type of node
     * @return the list of selected children
     */
    default List<ThemisInstance> discoverChildren(final ThemisId pId) {
        return discoverChildren(n -> n.getId().equals(pId));
    }

    /**
     * Select children.
     *
     * @param pTest the predicate to select children
     * @return the list of selected children
     */
    List<ThemisInstance> discoverChildren(Predicate<ThemisInstance> pTest);

    /**
     * Select nodes from tree.
     *
     * @param pList the list to populate
     * @param pTest the predicate to select nodes
     */
    void discoverNodes(List<ThemisInstance> pList,
                       Predicate<ThemisInstance> pTest);

    /**
     * Select nodes from tree.
     *
     * @param pId the type of node
     * @return the list of selected nodes
     */
    default List<ThemisInstance> discoverNodes(final ThemisId pId) {
        return discoverNodes(n -> n.getId().equals(pId));
    }

    /**
     * Select nodes from tree.
     *
     * @param pTest the predicate to select nodes
     * @return the list of selected nodes
     */
    default List<ThemisInstance> discoverNodes(final Predicate<ThemisInstance> pTest) {
        final List<ThemisInstance> myList = new ArrayList<>();
        discoverNodes(myList, pTest);
        return myList;
    }

    /**
     * The id.
     */
    interface ThemisId {
    }

    /**
     * The base declaration interface.
     */
    interface ThemisDeclarationInstance
            extends ThemisInstance {
    }

    /**
     * The base type interface.
     */
    interface ThemisTypeInstance
            extends ThemisInstance {
    }

    /**
     * The base node interface.
     */
    interface ThemisNodeInstance
            extends ThemisInstance {
    }

    /**
     * The base statement interface.
     */
    interface ThemisStatementInstance
            extends ThemisInstance {
    }

    /**
     * The base expression interface.
     */
    interface ThemisExpressionInstance
            extends ThemisInstance {
    }

    /**
     * The base module interface.
     */
    interface ThemisModuleInstance
            extends ThemisInstance {
    }

    /**
     * The base class interface.
     */
    interface ThemisClassInstance {
        /**
         * Obtain the name.
         *
         * @return the name
         */
        String getName();

        /**
         * Obtain the fullName.
         *
         * @return the fullName
         */
        String getFullName();

        /**
         * Obtain the modifiers.
         *
         * @return the modifiers
         */
        ThemisModifierList getModifiers();

        /**
         * Obtain the body.
         *
         * @return the body
         */
        default List<ThemisDeclarationInstance> getBody() {
            return Collections.emptyList();
        }

        /**
         * Obtain the extends types.
         *
         * @return the extends
         */
        default List<ThemisTypeInstance> getExtends() {
            return Collections.emptyList();
        }

        /**
         * Obtain the implements types.
         *
         * @return the implements
         */
        default List<ThemisTypeInstance> getImplements() {
            return Collections.emptyList();
        }

        /**
         * Obtain the type parameters.
         *
         * @return the parameters
         */
        default List<ThemisTypeInstance> getTypeParameters() {
            return Collections.emptyList();
        }

        /**
         * Obtain the annotations.
         *
         * @return the annotations
         */
        default List<ThemisExpressionInstance> getAnnotations() {
            return Collections.emptyList();
        }

        /**
         * is the class a top-level class?
         *
         * @return true/false
         */
        default boolean isTopLevel() {
            return false;
        }

        /**
         * is the class an interface?
         *
         * @return true/false
         */
        default boolean isInterface() {
            return false;
        }

        /**
         * is the class an inner class?
         *
         * @return true/false
         */
        default boolean isInner() {
            return false;
        }

        /**
         * is the class a local declaration?
         *
         * @return true/false
         */
        default boolean isLocalDeclaration() {
            return false;
        }

        /**
         * is the class an anonymous class?
         *
         * @return true/false
         */
        default boolean isAnonClass() {
            return false;
        }
    }

    /**
     * The base method interface.
     */
    interface ThemisMethodInstance {
        /**
         * Obtain the name.
         *
         * @return the name
         */
        String getName();

        /**
         * Obtain the modifiers.
         *
         * @return the modifiers
         */
        ThemisModifierList getModifiers();

        /**
         * Obtain the parameters.
         *
         * @return the parameters
         */
        default List<ThemisNodeInstance> getParameters() {
            return Collections.emptyList();
        }

        /**
         * Obtain the thrown exceptions.
         *
         * @return the thrown exceptions
         */
        default List<ThemisTypeInstance> getThrown() {
            return Collections.emptyList();
        }

        /**
         * Obtain the type parameters.
         *
         * @return the parameters
         */
        default List<ThemisTypeInstance> getTypeParameters() {
            return Collections.emptyList();
        }

        /**
         * Obtain the body.
         *
         * @return the body
         */
        ThemisStatementInstance getBody();

        /**
         * Obtain the annotations.
         *
         * @return the annotations
         */
        default List<ThemisExpressionInstance> getAnnotations() {
            return Collections.emptyList();
        }
    }
}
