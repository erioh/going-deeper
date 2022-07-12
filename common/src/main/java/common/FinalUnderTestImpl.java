package common;


import static common.Constants.PING;
import static common.Constants.TO_STRING;

public final class FinalUnderTestImpl implements InterfaceUnderTest {
    @Override
    public String ping() {
        return PING;
    }

    public String echo(String echo) {
        return echo;
    }

    @Override
    public String toString() {
        return TO_STRING;
    }
}