/*******************************************************************************
 * Metis: Java Data Framework
 * Copyright 2012,2025 Tony Washer
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
package net.sourceforge.joceanus.metis.test;

import java.util.Iterator;

import net.sourceforge.joceanus.metis.field.MetisFieldItem;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldDef;
import net.sourceforge.joceanus.metis.field.MetisFieldItem.MetisFieldSetDef;
import net.sourceforge.joceanus.metis.field.MetisFieldSet;
import net.sourceforge.joceanus.metis.field.MetisFieldSimpleId;

/**
 * Data Tester.
 */
public final class MetisDataTester {
    /**
     * Private constructor.
     */
    private MetisDataTester() {
    }

    /**
     * Main.
     * @param args the program arguments
     */
    public static void main(final String[] args) {
        final MainClass myClass = new MainClass();
        final MetisFieldSetDef mySet = myClass.getDataFieldSet();
        final Iterator<MetisFieldDef> myIterator = mySet.fieldIterator();
        while (myIterator.hasNext()) {
            final MetisFieldDef myField = myIterator.next();

            System.out.println(myField.getFieldId().getId() + '=' + myField.getFieldValue(myClass));
        }
    }

    /**
     * Base class.
     */
    public abstract static class BaseClass
            implements MetisFieldItem {
        /**
         * FieldSet.
         */
        private static final MetisFieldSet<BaseClass> FIELD_DEFS = MetisFieldSet.newFieldSet(BaseClass.class);

        /*
         * Declare fields.
         */
        static {
            FIELD_DEFS.declareLocalField(new MetisFieldSimpleId("One"), p -> 1);
            FIELD_DEFS.declareLocalField(new MetisFieldSimpleId("Two"), BaseClass::getCounter);
        }

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
     * Main class.
     */
    public static class MainClass extends BaseClass {
        /**
         * FieldSet.
         */
        private static final MetisFieldSet<MainClass> FIELD_DEFS = MetisFieldSet.newFieldSet(MainClass.class);

        /**
         * FieldSet.
         */
        private static final int THREE = 3;

        /*
         * DataFieldThree.
         */
        static {
            FIELD_DEFS.declareLocalField(new MetisFieldSimpleId("Three"), p -> THREE);
        }

        /**
         * LocalFields.
         */
        private final MetisFieldSet<MainClass> theLocalFields;

        /**
         * Counter.
         */
        private final Integer theValue = 20;

        /**
         * Constructor.
         */
        public MainClass() {
            theLocalFields = MetisFieldSet.newFieldSet(this);
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
        public MetisFieldSet<MainClass> getDataFieldSet() {
            return theLocalFields;
        }
    }
}
