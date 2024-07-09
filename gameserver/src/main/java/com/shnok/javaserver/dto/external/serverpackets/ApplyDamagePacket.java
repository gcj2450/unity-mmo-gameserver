package com.shnok.javaserver.dto.external.serverpackets;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;

public class ApplyDamagePacket extends SendablePacket {
    public ApplyDamagePacket(int sender, int target, int damage, float newHp, boolean criticalHit) {
        super(ServerPacketType.ApplyDamage.getValue());
        writeI(sender);
        writeI(target);
        writeI(damage);
        writeI((int) newHp);
        writeB(criticalHit ? (byte) 1 : (byte) 0);
        buildPacket();
    }
}