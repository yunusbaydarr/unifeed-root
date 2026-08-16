package app.unifeed.common;

/** Keeps externally supplied collection limits within an endpoint's contract. */
public final class RequestLimits {
    private RequestLimits() { }
    public static int between(int requested, int maximum) { return Math.min(Math.max(requested, 1), maximum); }
}
