package com.raoulvdberge.refinedpipes.network.pipe.attachment.extractor;

public enum RoutingMode {
    NEAREST,
    FURTHEST,
    RANDOM,
    ROUND_ROBIN;

    public static RoutingMode get(byte b) {
        RoutingMode[] m = values();

        if (b < 0 || b >= m.length) {
            return NEAREST;
        }

        return m[b];
    }

    public RoutingMode next() {
        switch (this) {
            case NEAREST:
                return FURTHEST;
            case FURTHEST:
                return RANDOM;
            case RANDOM:
                return ROUND_ROBIN;
            case ROUND_ROBIN:
            default:
                return NEAREST;
        }
    }
}
