/*******************************************************************************
 * Copyright 2012 Tony Washer
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
 * ------------------------------------------------------------
 * SubVersion Revision Information:
 * $URL$
 * $Revision$
 * $Author$
 * $Date$
 ******************************************************************************/
package net.sourceforge.JGordianKnot.ZipFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.JDataManager.DataConverter;
import net.sourceforge.JDataManager.ModelException;
import net.sourceforge.JDataManager.ModelException.ExceptionClass;

public class ZipFileProperties {
    /**
     * The property separator
     */
    private final static char thePropSeparator = '/';

    /**
     * The value separator
     */
    private final static char theValuSeparator = '=';

    /**
     * The value separator
     */
    private final static char theLongSeparator = '!';

    /**
     * List of properties
     */
    private final List<Property> theList;

    /**
     * Constructor
     */
    protected ZipFileProperties() {
        /* Allocate the array */
        theList = new ArrayList<Property>();
    }

    /**
     * Constructor from encoded string
     * @param pCodedString the encoded string
     * @throws ModelException
     */
    protected ZipFileProperties(String pCodedString) throws ModelException {
        /* Allocate the array */
        theList = new ArrayList<Property>();

        /* Wrap string in a string builder */
        StringBuilder myString = new StringBuilder(pCodedString);
        String myPropSep = Character.toString(thePropSeparator);
        int myLoc;

        /* while we have separators in the string */
        while ((myLoc = myString.indexOf(myPropSep)) != -1) {
            /* Parse the encoded property and remove it from the buffer */
            parseEncodedProperty(myString.substring(0, myLoc));
            myString.delete(0, myLoc + 1);
        }

        /* Parse the remaining property */
        parseEncodedProperty(myString.toString());
    }

    /**
     * Set the named property
     * @param pName the name of the property
     * @param pValue the Value of the property
     */
    protected void setProperty(String pName,
                               String pValue) {
        /* Set the new value */
        setProperty(pName, DataConverter.stringToByteArray(pValue));
    }

    /**
     * Set the named property
     * @param pName the name of the property
     * @param pValue the Value of the property
     */
    protected void setProperty(String pName,
                               byte[] pValue) {
        Property myProperty;

        /* Access any existing property */
        myProperty = getProperty(pName);

        /* If the property does not exist */
        if (myProperty != null) {
            /* Create the new property */
            myProperty = new Property(pName);
        }

        /* Set the new value */
        myProperty.setByteValue(pValue);
    }

    /**
     * Set the named property
     * @param pName the name of the property
     * @param pValue the Value of the property
     */
    protected void setProperty(String pName,
                               long pValue) {
        Property myProperty;

        /* Access any existing property */
        myProperty = getProperty(pName);

        /* If the property does not exist */
        if (myProperty != null) {
            /* Create the new property */
            myProperty = new Property(pName);
        }

        /* Set the new value */
        myProperty.setLongValue(pValue);
    }

    /**
     * Obtain the string value of the named property
     * @param pName the name of the property
     * @return the value of the property or <code>null</code> if the property does not exist
     * @throws ModelException
     */
    protected String getStringProperty(String pName) throws ModelException {
        /* Access the property */
        byte[] myValue = getByteProperty(pName);

        /* Return the value */
        return (myValue == null) ? null : DataConverter.byteArrayToString(myValue);
    }

    /**
     * Obtain the bytes value of the named property
     * @param pName the name of the property
     * @return the value of the property or <code>null</code> if the property does not exist
     */
    protected byte[] getByteProperty(String pName) {
        Property myProperty;

        /* Access the property */
        myProperty = getProperty(pName);

        /* Return the value */
        return (myProperty == null) ? null : myProperty.getByteValue();
    }

    /**
     * Obtain the long value of the named property
     * @param pName the name of the property
     * @return the value of the property or <code>-1</code> if the property does not exist
     */
    protected long getLongProperty(String pName) {
        Property myProperty;

        /* Access the property */
        myProperty = getProperty(pName);

        /* Return the value */
        return (myProperty == null) ? -1 : myProperty.getLongValue();
    }

    /**
     * Obtain the named property from the list
     * @param pName the name of the property
     * @return the value of the property or <code>null</code> if the property does not exist
     */
    private Property getProperty(String pName) {
        /* Loop through the properties */
        Iterator<Property> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access next property */
            Property myProperty = myIterator.next();

            /* Check the property name */
            int iDiff = pName.compareTo(myProperty.getName());

            /* If this is the required property, return it */
            if (iDiff == 0)
                return myProperty;

            /* If this property is later than the required name, no such property */
            if (iDiff < 0)
                break;
        }

        /* Return not found */
        return null;
    }

    /**
     * Encode the properties
     * @return the encoded string
     */
    protected String encodeProperties() {
        StringBuilder myString = new StringBuilder(1000);
        StringBuilder myValue = new StringBuilder(200);

        /* Loop through the properties */
        Iterator<Property> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access next property */
            Property myProperty = myIterator.next();

            /* Build the value string */
            myValue.setLength(0);
            myValue.append(myProperty.getName());
            myValue.append(theValuSeparator);

            /* If we have a byte value */
            if (myProperty.getByteValue() != null) {
                /* Add the byte value as a Hex String */
                myValue.append(DataConverter.bytesToHexString(myProperty.getByteValue()));
            }

            /* Add the value separator */
            myValue.append(theLongSeparator);

            /* If we have a long value */
            if (myProperty.getLongValue() != -1) {
                /* Add the long value as a Hex String */
                myValue.append(DataConverter.longToHexString(myProperty.getLongValue()));
            }

            /* Add the value to the string */
            if (myString.length() > 0)
                myString.append(thePropSeparator);
            myString.append(myValue);
        }

        /* Return the encoded string */
        return myString.toString();
    }

    /**
     * Parse the encoded string representation to obtain the property
     * @param pValue the encoded property
     * @throws ModelException
     */
    private void parseEncodedProperty(String pValue) throws ModelException {
        Property myProperty;
        String myName;
        String myBytes;
        String myLong;
        int myLen;
        int myLoc;

        /* Locate the Value separator in the string */
        myLoc = pValue.indexOf(theValuSeparator);

        /* Check that we found the value separator */
        if (myLoc == -1)
            throw new ModelException(ExceptionClass.DATA, "Missing value separator: " + pValue);

        /* Split the values and name */
        myName = pValue.substring(0, myLoc);
        myBytes = pValue.substring(myLoc + 1);
        myLen = myBytes.length();

        /* If the name is already present reject it */
        if (getProperty(myName) != null)
            throw new ModelException(ExceptionClass.DATA, "Duplicate name: " + pValue);

        /* Locate the Long separator in the string */
        myLoc = myBytes.indexOf(theLongSeparator);

        /* Check that we found the long separator */
        if (myLoc == -1)
            throw new ModelException(ExceptionClass.DATA, "Missing long separator: " + pValue);

        /* Access the separate byte and long values */
        myLong = (myLoc < myLen - 1) ? myBytes.substring(myLoc + 1) : null;
        myBytes = (myLoc > 0) ? myBytes.substring(0, myLoc) : null;

        /* Must have at least one of Bytes/Long */
        if ((myBytes == null) && (myLong == null))
            throw new ModelException(ExceptionClass.DATA, "Missing long separator: " + pValue);

        /* Create a new property */
        myProperty = new Property(myName);

        /* If we have a bytes array */
        if (myBytes != null) {
            /* Set the bytes value */
            myProperty.setByteValue(DataConverter.hexStringToBytes(myBytes));
        }

        /* If we have a long value */
        if (myLong != null) {
            /* Access the bytes value */
            myProperty.setLongValue(DataConverter.hexStringToLong(myLong));
        }
    }

    /**
     * Individual Property
     */
    private class Property {
        /**
         * Name of property
         */
        private String theName = null;

        /**
         * Value of property
         */
        private byte[] theByteValue = null;

        /**
         * Value of property
         */
        private long theLongValue = -1;

        /**
         * Standard Constructor
         * @param pName the name of the property
         */
        private Property(String pName) {
            /* Check for invalid name */
            if (pName.indexOf(theValuSeparator) != -1)
                throw new IllegalArgumentException("Invalid property name - " + pName);

            /* Store name */
            theName = pName;

            /* Loop through the properties in the list */
            int iIndex = 0;
            Iterator<Property> myIterator = theList.iterator();
            while (myIterator.hasNext()) {
                /* Access next property */
                Property myProperty = myIterator.next();

                /* Check the property name */
                int iDiff = pName.compareTo(myProperty.getName());

                /* If this property is later than us */
                if (iDiff < 0)
                    break;

                /* Reject attempt to add duplicate name */
                if (iDiff == 0)
                    throw new IllegalArgumentException("Duplicate property - " + pName);

                /* Increment index */
                iIndex++;
            }

            /* Add into the list at the correct point */
            theList.add(iIndex, this);
        }

        /**
         * Obtain the name of the property
         * @return the name of the property
         */
        private String getName() {
            return theName;
        }

        /**
         * Obtain the byte value of the property
         * @return the value of the property
         */
        private byte[] getByteValue() {
            return theByteValue;
        }

        /**
         * Obtain the long value of the property
         * @return the value of the property
         */
        private long getLongValue() {
            return theLongValue;
        }

        /**
         * Set the byte value
         * @param pValue the new value
         */
        private void setByteValue(byte[] pValue) {
            theByteValue = Arrays.copyOf(pValue, pValue.length);
        }

        /**
         * Set the long value
         * @param pValue the new value
         */
        private void setLongValue(long pValue) {
            theLongValue = pValue;
        }
    }
}
