package com.refinedmods.refinedpipes.network.pipe.attachment.extractor;

public enum BlacklistWhitelist {
    BLACKLIST,
    WHITELIST;

    public BlacklistWhitelist next() {
        switch (this) {
            case BLACKLIST:
                return WHITELIST;
            case WHITELIST:
                return BLACKLIST;
            default:
                return BLACKLIST;
        }
    }

    public static BlacklistWhitelist get(byte b) {
        BlacklistWhitelist[] v = values();

        if (b < 0 || b >= v.length) {
            return BLACKLIST;
        }

        return v[b];
    }
}
