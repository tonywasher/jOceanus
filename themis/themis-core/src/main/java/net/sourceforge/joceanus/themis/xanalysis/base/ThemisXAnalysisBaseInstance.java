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
import net.sourceforge.joceanus.oceanus.base.OceanusException;

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
     * Constructor.
     * @param pParser the parser
     * @param pNode the node
     * @throws OceanusException on error
     */
    protected ThemisXAnalysisBaseInstance(final ThemisXAnalysisParser pParser,
                                          final T pNode) throws OceanusException {
        theNode = pNode;
    }

    /**
     * Obtain the node.
     * @return the node
     */
    public T getNode() {
        return theNode;
    }

    @Override
    public String toString() {
        return theNode.toString();
    }
}
