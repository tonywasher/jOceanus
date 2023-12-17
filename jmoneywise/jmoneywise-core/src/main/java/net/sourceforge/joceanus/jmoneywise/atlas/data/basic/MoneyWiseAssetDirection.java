/*******************************************************************************
 * MoneyWise: Finance Application
 * Copyright 2012,2023 Tony Washer
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
 ******************************************************************************/
package net.sourceforge.joceanus.jmoneywise.atlas.data.basic;

/**
 * Asset Direction.
 */
public enum MoneyWiseAssetDirection {
    /**
     * To.
     */
    TO(1),

    /**
     * From.
     */
    FROM(2);

    /**
     * The String name.
     */
    private String theName;

    /**
     * Class Id.
     */
    private final int theId;

    /**
     * Constructor.
     * @param uId the Id
     */
    MoneyWiseAssetDirection(final int uId) {
        theId = uId;
    }

    /**
     * Obtain class Id.
     * @return the Id
     */
    public int getId() {
        return theId;
    }

    @Override
    public String toString() {
        /* If we have not yet loaded the name */
        if (theName == null) {
            /* Load the name */
            theName = MoneyWiseBasicResource.getKeyForAssetDirection(this).getValue();
        }

        /* return the name */
        return theName;
    }

    /**
     * Reverse.
     * @return the reversed direction
     */
    public MoneyWiseAssetDirection reverse() {
        return this == TO
                ? FROM
                : TO;
    }

    /**
     * Is this the from direction?
     * @return true/false
     */
    public boolean isFrom() {
        return this == FROM;
    }

    /**
     * Is this the to direction?
     * @return true/false
     */
    public boolean isTo() {
        return this == TO;
    }

    /**
     * get value from name.
     * @param pName the name value
     * @return the corresponding enum object
     */
    public static MoneyWiseAssetDirection fromName(final String pName) {
        for (MoneyWiseAssetDirection myDir : values()) {
            if (pName.equals(myDir.toString())) {
                return myDir;
            }
        }
        return null;
    }
}
