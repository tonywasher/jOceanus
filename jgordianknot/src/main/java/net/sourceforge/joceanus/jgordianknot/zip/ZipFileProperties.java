/*******************************************************************************
 * jGordianKnot: Security Suite
 * Copyright 2012,2014 Tony Washer
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
package net.sourceforge.joceanus.jgordianknot.zip;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.joceanus.jgordianknot.JGordianDataException;
import net.sourceforge.joceanus.jtethys.DataConverter;
import net.sourceforge.joceanus.jtethys.JOceanusException;

/**
 * Class represents the properties of an encrypted file in the Zip file.
 */
public class ZipFileProperties {
    /**
     * The property separator.
     */
    private static final char SEP_PROPERTY = '/';

    /**
     * The value separator.
     */
    private static final char SEP_VALUE = '=';

    /**
     * The long separator.
     */
    private static final char SEP_LONG = '!';

    /**
     * The Buffer length.
     */
    private static final int BUFFER_LEN = 1000;

    /**
     * The Value buffer length.
     */
    private static final char BUFFER_VALLEN = 200;

    /**
     * List of properties.
     */
    private final List<Property> theList;

    /**
     * Constructor.
     */
    protected ZipFileProperties() {
        /* Allocate the array */
        theList = new ArrayList<Property>();
    }

    /**
     * Constructor from encoded string.
     * @param pCodedString the encoded string
     * @throws JOceanusException on error
     */
    protected ZipFileProperties(final String pCodedString) throws JOceanusException {
        /* Allocate the array */
        theList = new ArrayList<Property>();

        /* Wrap string in a string builder */
        StringBuilder myString = new StringBuilder(pCodedString);
        String myPropSep = Character.toString(SEP_PROPERTY);

        /* while we have separators in the string */
        for (;;) {
            /* Locate next separator and break if not found */
            int myLoc = myString.indexOf(myPropSep);
            if (myLoc == -1) {
                break;
            }

            /* Parse the encoded property and remove it from the buffer */
            parseEncodedProperty(myString.substring(0, myLoc));
            myString.delete(0, myLoc + 1);
        }

        /* Parse the remaining property */
        parseEncodedProperty(myString.toString());
    }

    /**
     * Set the named property.
     * @param pName the name of the property
     * @param pValue the Value of the property
     * @throws JOceanusException on error
     */
    protected void setProperty(final String pName,
                               final String pValue) throws JOceanusException {
        /* Set the new value */
        setProperty(pName, DataConverter.stringToByteArray(pValue));
    }

    /**
     * Set the named property.
     * @param pName the name of the property
     * @param pValue the Value of the property
     */
    protected void setProperty(final String pName,
                               final byte[] pValue) {
        /* Determine whether we are setting a null value */
        boolean isNull = pValue == null;

        /* Access any existing property */
        Property myProperty = getProperty(pName);

        /* If the property does not exist */
        if (myProperty == null) {
            /* If we have a value */
            if (!isNull) {
                /* Create the new property */
                myProperty = new Property(theList, pName);

                /* Set the new value */
                myProperty.setByteValue(pValue);
            }

            /* else if the property now has no value */
        } else if ((isNull)
                   && (myProperty.getLongValue() == null)) {
            /* Remove the value from the list */
            theList.remove(myProperty);

            /* else just set the value */
        } else {
            /* Set the new value */
            myProperty.setByteValue(pValue);
        }
    }

    /**
     * Set the named property.
     * @param pName the name of the property
     * @param pValue the Value of the property
     */
    protected void setProperty(final String pName,
                               final Long pValue) {
        /* Determine whether we are setting a null value */
        boolean isNull = pValue == null;

        /* Access any existing property */
        Property myProperty = getProperty(pName);

        /* If the property does not exist */
        if (myProperty == null) {
            /* If we have a value */
            if (!isNull) {
                /* Create the new property */
                myProperty = new Property(theList, pName);

                /* Set the new value */
                myProperty.setLongValue(pValue);
            }

            /* else if the property now has no value */
        } else if ((isNull)
                   && (myProperty.getByteValue() == null)) {
            /* Remove the value from the list */
            theList.remove(myProperty);

            /* else just set the value */
        } else {
            /* Set the new value */
            myProperty.setLongValue(pValue);
        }
    }

    /**
     * Obtain the string value of the named property.
     * @param pName the name of the property
     * @return the value of the property or <code>null</code> if the property does not exist
     * @throws JOceanusException on error
     */
    protected String getStringProperty(final String pName) throws JOceanusException {
        /* Access the property */
        byte[] myValue = getByteProperty(pName);

        /* Return the value */
        return (myValue == null)
                ? null
                : DataConverter.byteArrayToString(myValue);
    }

    /**
     * Obtain the bytes value of the named property.
     * @param pName the name of the property
     * @return the value of the property or <code>null</code> if the property does not exist
     */
    protected byte[] getByteProperty(final String pName) {
        /* Access the property */
        Property myProperty = getProperty(pName);

        /* Return the value */
        return (myProperty == null)
                ? null
                : myProperty.getByteValue();
    }

    /**
     * Obtain the long value of the named property.
     * @param pName the name of the property
     * @return the value of the property or <code>-1</code> if the property does not exist
     */
    protected Long getLongProperty(final String pName) {
        /* Access the property */
        Property myProperty = getProperty(pName);

        /* Return the value */
        return (myProperty == null)
                ? null
                : myProperty.getLongValue();
    }

    /**
     * Obtain the named property from the list.
     * @param pName the name of the property
     * @return the value of the property or <code>null</code> if the property does not exist
     */
    private Property getProperty(final String pName) {
        /* Loop through the properties */
        Iterator<Property> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access next property */
            Property myProperty = myIterator.next();

            /* Check the property name */
            int iDiff = pName.compareTo(myProperty.getName());

            /* If this is the required property, return it */
            if (iDiff == 0) {
                return myProperty;
            }

            /* If this property is later than the required name, no such property */
            if (iDiff < 0) {
                break;
            }
        }

        /* Return not found */
        return null;
    }

    /**
     * Encode the properties.
     * @return the encoded string
     */
    protected String encodeProperties() {
        StringBuilder myString = new StringBuilder(BUFFER_LEN);
        StringBuilder myValue = new StringBuilder(BUFFER_VALLEN);

        /* Loop through the properties */
        Iterator<Property> myIterator = theList.iterator();
        while (myIterator.hasNext()) {
            /* Access next property */
            Property myProperty = myIterator.next();

            /* Build the value string */
            myValue.setLength(0);
            myValue.append(myProperty.getName());
            myValue.append(SEP_VALUE);

            /* If we have a byte value */
            if (myProperty.getByteValue() != null) {
                /* Add the byte value as a Hex String */
                myValue.append(DataConverter.bytesToHexString(myProperty.getByteValue()));
            }

            /* Add the value separator */
            myValue.append(SEP_LONG);

            /* If we have a long value */
            if (myProperty.getLongValue() != null) {
                /* Add the long value as a Hex String */
                myValue.append(DataConverter.longToHexString(myProperty.getLongValue()));
            }

            /* Add the value to the string */
            if (myString.length() > 0) {
                myString.append(SEP_PROPERTY);
            }
            myString.append(myValue);
        }

        /* Return the encoded string */
        return myString.toString();
    }

    /**
     * Parse the encoded string representation to obtain the property.
     * @param pValue the encoded property
     * @throws JOceanusException on error
     */
    private void parseEncodedProperty(final String pValue) throws JOceanusException {
        /* Locate the Value separator in the string */
        int myLoc = pValue.indexOf(SEP_VALUE);

        /* Check that we found the value separator */
        if (myLoc == -1) {
            throw new JGordianDataException("Missing value separator: "
                                            + pValue);
        }

        /* Split the values and name */
        String myName = pValue.substring(0, myLoc);
        String myBytes = pValue.substring(myLoc + 1);
        int myLen = myBytes.length();

        /* If the name is already present reject it */
        if (getProperty(myName) != null) {
            throw new JGordianDataException("Duplicate name: "
                                            + pValue);
        }

        /* Locate the Long separator in the string */
        myLoc = myBytes.indexOf(SEP_LONG);

        /* Check that we found the long separator */
        if (myLoc == -1) {
            throw new JGordianDataException("Missing long separator: "
                                            + pValue);
        }

        /* Access the separate byte and long values */
        String myLong = (myLoc < myLen - 1)
                ? myBytes.substring(myLoc + 1)
                : null;
        myBytes = (myLoc > 0)
                ? myBytes.substring(0, myLoc)
                : null;

        /* Must have at least one of Bytes/Long */
        if ((myBytes == null)
            && (myLong == null)) {
            throw new JGordianDataException("Invalid property: "
                                            + myName);
        }

        /* Create a new property */
        Property myProperty = new Property(theList, myName);

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
     * Individual Property.
     */
    private static final class Property {
        /**
         * Name of property.
         */
        private final String theName;

        /**
         * Value of property.
         */
        private byte[] theByteValue = null;

        /**
         * Value of property.
         */
        private Long theLongValue = null;

        /**
         * Standard Constructor.
         * @param pList property list
         * @param pName the name of the property
         */
        private Property(final List<Property> pList,
                         final String pName) {
            /* Check for invalid name */
            if (pName.indexOf(SEP_VALUE) != -1) {
                throw new IllegalArgumentException("Invalid property name - "
                                                   + pName);
            }

            /* Store name */
            theName = pName;

            /* Loop through the properties in the list */
            int iIndex = 0;
            Iterator<Property> myIterator = pList.iterator();
            while (myIterator.hasNext()) {
                /* Access next property */
                Property myProperty = myIterator.next();

                /* Check the property name */
                int iDiff = pName.compareTo(myProperty.getName());

                /* If this property is later than us */
                if (iDiff < 0) {
                    break;
                }

                /* Reject attempt to add duplicate name */
                if (iDiff == 0) {
                    throw new IllegalArgumentException("Duplicate property - "
                                                       + pName);
                }

                /* Increment index */
                iIndex++;
            }

            /* Add into the list at the correct point */
            pList.add(iIndex, this);
        }

        /**
         * Obtain the name of the property.
         * @return the name of the property
         */
        private String getName() {
            return theName;
        }

        /**
         * Obtain the byte value of the property.
         * @return the value of the property
         */
        private byte[] getByteValue() {
            return theByteValue;
        }

        /**
         * Obtain the long value of the property.
         * @return the value of the property
         */
        private Long getLongValue() {
            return theLongValue;
        }

        /**
         * Set the byte value.
         * @param pValue the new value
         */
        private void setByteValue(final byte[] pValue) {
            theByteValue = (pValue == null)
                    ? null
                    : Arrays.copyOf(pValue, pValue.length);
        }

        /**
         * Set the long value.
         * @param pValue the new value
         */
        private void setLongValue(final Long pValue) {
            theLongValue = pValue;
        }
    }
}