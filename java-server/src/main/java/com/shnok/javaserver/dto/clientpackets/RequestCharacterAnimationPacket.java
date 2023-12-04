package com.shnok.javaserver.dto.clientpackets;

import com.shnok.javaserver.dto.ClientPacket;

public class RequestCharacterAnimationPacket extends ClientPacket {
    private final byte animId;
    private final float value;

    public RequestCharacterAnimationPacket(byte[] data) {
        super(data);
        animId = readB();
        value = readF();
    }

    public byte getAnimId() {
        return animId;
    }

    public float getValue() {
        return value;
    }
}
