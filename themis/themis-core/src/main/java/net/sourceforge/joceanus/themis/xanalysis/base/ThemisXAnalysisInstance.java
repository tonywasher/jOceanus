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

import java.util.List;

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
     * Register child node.
     * @param pChild the child node
     */
    void registerChild(ThemisXAnalysisInstance pChild);

    /**
     * Obtain the list of children.
     * @return the list of children
     */
    List<ThemisXAnalysisInstance> getChildren();

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
}
