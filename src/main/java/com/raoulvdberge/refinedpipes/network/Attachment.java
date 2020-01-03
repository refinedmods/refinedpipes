package com.raoulvdberge.refinedpipes.network;

public class Attachment {
    private final AttachmentType type;

    public Attachment(AttachmentType type) {
        this.type = type;
    }

    public AttachmentType getType() {
        return type;
    }
}
