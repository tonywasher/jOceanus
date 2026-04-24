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
package io.github.tonywasher.joceanus.themis.parser.node;

import com.github.javaparser.ast.Node;
import io.github.tonywasher.joceanus.oceanus.base.OceanusException;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisBaseInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisInstance.ThemisNodeInstance;
import io.github.tonywasher.joceanus.themis.parser.base.ThemisParserDef;

/**
 * Node Base Class.
 *
 * @param <T> the Node
 */
public abstract class ThemisBaseNode<T extends Node>
        extends ThemisBaseInstance<T>
        implements ThemisNodeInstance {
    /**
     * The nodeId.
     */
    private final ThemisNode theId;

    /**
     * Constructor.
     *
     * @param pParser the parser
     * @param pNode   the node
     */
    ThemisBaseNode(final ThemisParserDef pParser,
                   final T pNode) throws OceanusException {
        super(pParser, pNode);
        theId = ThemisNode.determineNode(pParser, pNode);
    }

    @Override
    public ThemisNode getId() {
        return theId;
    }
}
