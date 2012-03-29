package intro

/**
 */
class ExternalHelpers {
    public static void helperMethod(x) {
        assert true
        assert 1
        assert x
    }

    public static void fooReceivesG(o, int n) {
        n * o.g()
    }
}
