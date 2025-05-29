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
import com.github.javaparser.ast.comments.Comment;
import net.sourceforge.joceanus.oceanus.base.OceanusException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Instance Base Class.
 * @param <T> the Node
 */
public abstract class ThemisXAnalysisBaseInstance<T extends Node>
        implements ThemisXAnalysisInstance {
    /**
     * The node.
     */
    private final T theNode;

    /**
     * The list of children.
     */
    private final List<ThemisXAnalysisInstance> theChildren;

    /**
     * The list of comments.
     */
    private final List<ThemisXAnalysisInstance> theComments;

    /**
     * Constructor.
     * @param pParser the parser
     * @param pNode the node
     * @throws OceanusException on error
     */
    protected ThemisXAnalysisBaseInstance(final ThemisXAnalysisParser pParser,
                                          final T pNode) throws OceanusException {
        /* Record the node and lists */
        theNode = pNode;
        theChildren = new ArrayList<>();
        theComments = new ArrayList<>();

        /* Register the node */
        pParser.registerInstance(this);

        /* Parse comments */
        final Comment myComment = pNode.getComment().orElse(null);
        if (myComment != null) {
            theComments.add(pParser.parseNode(myComment));
        }
        for(Comment myOrphan : pNode.getOrphanComments()) {
            theComments.add(pParser.parseNode(myOrphan));
        }
    }

    @Override
    public void registerChild(final ThemisXAnalysisInstance pChild) {
        theChildren.add(pChild);
    }

    @Override
    public T getNode() {
        return theNode;
    }

    @Override
    public List<ThemisXAnalysisInstance> getChildren() {
        return theChildren;
    }

    /**
     * Obtain the list of comments.
     * @return the list of comments
     */
    public List<ThemisXAnalysisInstance> getComments() {
        return theComments;
    }

    @Override
    public List<ThemisXAnalysisInstance> discoverChildren(final Predicate<ThemisXAnalysisInstance> pTest) {
        /* Loop adding children to list */
        final List<ThemisXAnalysisInstance> myList = new ArrayList<>();
        for(ThemisXAnalysisInstance myChild : theChildren) {
            if (pTest.test(myChild)) {
                myList.add(myChild);
            }
        }
        return myList;
    }

    @Override
    public void discoverNodes(final List<ThemisXAnalysisInstance> pList,
                              final Predicate<ThemisXAnalysisInstance> pTest) {
        /* Add self to list if required */
        if (pTest.test(this)) {
            pList.add(this);
        }

        /* Loop adding children to list */
        for(ThemisXAnalysisInstance myChild : theChildren) {
            myChild.discoverNodes(pList, pTest);
        }
    }

    @Override
    public String toString() {
        return theNode.toString();
    }
}
