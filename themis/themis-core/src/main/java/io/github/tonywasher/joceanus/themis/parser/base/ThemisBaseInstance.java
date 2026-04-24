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
import com.github.javaparser.ast.comments.Comment;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Instance Base Class.
 *
 * @param <T> the Node
 */
public abstract class ThemisBaseInstance<T extends Node>
        implements ThemisInstance {
    /**
     * The node.
     */
    private final T theNode;

    /**
     * The parent.
     */
    private final ThemisInstance theParent;

    /**
     * The list of children.
     */
    private final List<ThemisInstance> theChildren;

    /**
     * The list of comments.
     */
    private final List<ThemisInstance> theComments;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pNode   the node
     * @throws OceanusException on error
     */
    protected ThemisBaseInstance(final ThemisParserDef pParser,
                                 final T pNode) throws OceanusException {
        /* Record the node and lists */
        theNode = pNode;
        theChildren = new ArrayList<>();
        theComments = new ArrayList<>();

        /* Register the node */
        theParent = pParser.registerInstance(this);

        /* Parse comments */
        final Comment myComment = pNode.getComment().orElse(null);
        if (myComment != null) {
            theComments.add(pParser.parseNode(myComment));
        }
        for (Comment myOrphan : pNode.getOrphanComments()) {
            theComments.add(pParser.parseNode(myOrphan));
        }
    }

    @Override
    public void registerChild(final ThemisInstance pChild) {
        theChildren.add(pChild);
    }

    @Override
    public T getNode() {
        return theNode;
    }

    @Override
    public ThemisInstance getParent() {
        return theParent;
    }

    @Override
    public List<ThemisInstance> getChildren() {
        return theChildren;
    }

    /**
     * Obtain the list of comments.
     *
     * @return the list of comments
     */
    public List<ThemisInstance> getComments() {
        return theComments;
    }

    @Override
    public List<ThemisInstance> discoverChildren(final Predicate<ThemisInstance> pTest) {
        /* Loop adding children to list */
        final List<ThemisInstance> myList = new ArrayList<>();
        for (ThemisInstance myChild : theChildren) {
            if (pTest.test(myChild)) {
                myList.add(myChild);
            }
        }
        return myList;
    }

    @Override
    public void discoverNodes(final List<ThemisInstance> pList,
                              final Predicate<ThemisInstance> pTest) {
        /* Add self to list if required */
        if (pTest.test(this)) {
            pList.add(this);
        }

        /* Loop adding children to list */
        for (ThemisInstance myChild : theChildren) {
            myChild.discoverNodes(pList, pTest);
        }
    }

    @Override
    public String toString() {
        return theNode.toString();
    }
}
