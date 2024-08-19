package com.shnok.javaserver.dto.external.serverpackets.authentication;

import com.shnok.javaserver.dto.SendablePacket;
import com.shnok.javaserver.enums.network.packettypes.external.ServerPacketType;
import com.shnok.javaserver.service.GameTimeControllerService;

import static com.shnok.javaserver.config.Configuration.server;

public class GameTimePacket extends SendablePacket {
    public GameTimePacket() {
        super(ServerPacketType.GameTimePacket.getValue());

        writeL(GameTimeControllerService.getInstance().gameTicks);
        writeI(GameTimeControllerService.getInstance().getTickDurationMs());
        writeI(server.dayDurationMin());

        buildPacket();
    }
}
