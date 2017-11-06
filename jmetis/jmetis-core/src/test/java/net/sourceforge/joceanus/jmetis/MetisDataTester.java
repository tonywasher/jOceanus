/*******************************************************************************
 * jMetis: Java Data Framework
 * Copyright 2012,2017 Tony Washer
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
package net.sourceforge.joceanus.jmetis;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.atlas.data.MetisDataField.MetisSimpleFieldId;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldSetDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;

/**
 * Data Tester.
 */
public class MetisDataTester {
    /**
     * Main.
     * @param args the program arguments
     */
    public static void main(final String[] args) {
        MainClass myClass = new MainClass();
        MetisDataEosFieldSetDef mySet = myClass.getDataFieldSet();
        Iterator<MetisDataEosFieldDef> myIterator = mySet.fieldIterator();
        while (myIterator.hasNext()) {
            MetisDataEosFieldDef myField = myIterator.next();

            System.out.println(myField.getFieldId().getId() + '=' + myField.getFieldValue(myClass));
        }
    }

    /**
     * Base class
     */
    public static abstract class BaseClass
            implements MetisDataEosFieldItem {
        /**
         * FieldSet.
         */
        private static MetisDataEosFieldSet<BaseClass> FIELD_DEFS = MetisDataEosFieldSet.newFieldSet(BaseClass.class);

        /**
         * Declare fields.
         */
        static {
            FIELD_DEFS.declareLocalField(new MetisSimpleFieldId("One"), p -> 1);
            FIELD_DEFS.declareLocalField(new MetisSimpleFieldId("Two"), BaseClass::getCounter);
        }

        /**
         * DataFieldTwo.
         */

        /**
         * Counter.
         */
        private final Integer theValue = Integer.valueOf(2);

        /**
         * Obtain counter.
         * @return the counter
         */
        public Integer getCounter() {
            return theValue;
        }
    }

    /**
     * Main class
     */
    public static class MainClass extends BaseClass {
        /**
         * FieldSet.
         */
        private static MetisDataEosFieldSet<MainClass> FIELD_DEFS = MetisDataEosFieldSet.newFieldSet(MainClass.class);

        /**
         * DataFieldThree.
         */
        static {
            FIELD_DEFS.declareLocalField(new MetisSimpleFieldId("Three"), p -> 3);
        }

        /**
         * LocalFields.
         */
        private final MetisDataEosFieldSet<MainClass> theLocalFields;

        /**
         * Counter.
         */
        private final Integer theValue = Integer.valueOf(20);

        /**
         * Constructor.
         */
        public MainClass() {
            theLocalFields = MetisDataEosFieldSet.newFieldSet(this);
            theLocalFields.declareLocalField("Four", MainClass::getMainCounter);
        }

        /**
         * Obtain counter.
         * @return the counter
         */
        public Integer getMainCounter() {
            return theValue;
        }

        @Override
        public MetisDataEosFieldSet<MainClass> getDataFieldSet() {
            return theLocalFields;
        }
    }
}
