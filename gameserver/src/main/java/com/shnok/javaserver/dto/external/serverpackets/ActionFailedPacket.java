package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class ActionFailedPacket extends SendablePacket {
    public ActionFailedPacket() {
        super(ServerPacketType.ActionFailed.getValue());
        writeB((byte) -1);
        buildPacket();
    }

    public ActionFailedPacket(byte action) {
        super(ServerPacketType.ActionFailed.getValue());
        writeB(action);
        buildPacket();
    }
}
