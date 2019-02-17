/*******************************************************************************
 * Themis: Java Project Framework
 * Copyright 2012,2019 Tony Washer
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
package net.sourceforge.joceanus.jthemis.scm.data;

/**
 * Owner representation.
 */
public interface ThemisScmOwner {
    /**
     * Obtain name.
     * @return name
     */
    String getName();

    /**
     * Is this a branch?
     * @return true/false
     */
    default boolean isBranch() {
        return false;
    }

    /**
     * Is this a tag?
     * @return true/false
     */
    default boolean isTag() {
        return false;
    }

    /**
     * Obtain the name of the branch.
     * @return the name of the branch
     */
    String getBranchName();
}
