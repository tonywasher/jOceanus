package net.sourceforge.joceanus.jmetis;

import java.util.Iterator;

import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosField;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldItem.MetisDataEosFieldSetDef;
import net.sourceforge.joceanus.jmetis.eos.data.MetisDataEosFieldSet;

public class MetisDataTester {

    public static void main(final String[] args) {
        MainClass myClass = new MainClass();
        MetisDataEosFieldSetDef mySet = myClass.getDataFieldSet();
        Iterator<MetisDataEosFieldDef> myIterator = mySet.fieldIterator();
        while (myIterator.hasNext()) {
            MetisDataEosFieldDef myField = myIterator.next();

            System.out.println(myField.getName() + '=' + myField.getFieldValue(myClass));
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
         * DataFieldOne.
         */
        private static MetisDataEosField<BaseClass> FIELD_ONE = FIELD_DEFS.declareLocalField("One", p -> 1);

        /**
         * DataFieldTwo.
         */
        private static MetisDataEosField<BaseClass> FIELD_TWO = FIELD_DEFS.declareLocalField("Two", BaseClass::getCounter);

        /**
         * Counter.
         */
        private final Integer theValue = Integer.valueOf(2);

        /**
         * Obtain counter.
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
        private static MetisDataEosField<MainClass> FIELD_THREE = FIELD_DEFS.declareLocalField("Three", p -> 3);

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
