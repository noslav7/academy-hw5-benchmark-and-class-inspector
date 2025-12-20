package academy.accessor;

class DirectAccessorTest extends AbstractAccessorTest {

    @Override
    protected AccessorStrategy createAccessor() throws Throwable {
        defaultStudentName = "John Smith";
        defaultStudentAge = 25;
        differentStudentName = "Jane Doe";
        differentStudentAge = 22;
        return new DirectAccessor();
    }
}

