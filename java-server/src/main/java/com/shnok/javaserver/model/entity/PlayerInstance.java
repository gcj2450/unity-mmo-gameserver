package com.shnok.javaserver.model.entity;

import com.shnok.javaserver.dto.ServerPacket;
import com.shnok.javaserver.dto.serverpackets.UserInfoPacket;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.knownlist.ObjectKnownList;
import com.shnok.javaserver.model.knownlist.PlayerKnownList;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.PlayerStatus;
import com.shnok.javaserver.model.status.Status;
import com.shnok.javaserver.model.template.PlayerTemplate;
import com.shnok.javaserver.service.ThreadPoolManagerService;
import com.shnok.javaserver.thread.GameClientThread;
import com.shnok.javaserver.util.VectorUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
@Setter
public class PlayerInstance extends Entity {
    public final String name;
    private GameClientThread gameClient;

    public PlayerInstance(int id, String name) {
        super(id);
        this.name = name;
    }

    public PlayerInstance(String name, PlayerTemplate playerTemplate) {
        this.name = name;
        this.template = playerTemplate;
        this.status = new PlayerStatus(playerTemplate);
    }

    @Override
    public PlayerKnownList getKnownList() {
        if ((super.getKnownList() == null) || !(super.getKnownList() instanceof PlayerKnownList)) {
            setKnownList(new PlayerKnownList(this));
        }
        return (PlayerKnownList) super.getKnownList();
    }

    // Send packet to player
    public boolean sendPacket(ServerPacket packet) {
        if(gameClient.isClientReady() && gameClient.isAuthenticated()) {
            if(gameClient.sendPacket(packet)) {
                if(packet instanceof UserInfoPacket) {
                    log.debug("[{}] Sending user packet", getGameClient().getCurrentPlayer().getId());
                }
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    @Override
    public void inflictDamage(Entity attacker, int value) {
        super.inflictDamage(attacker, value);
    }

    @Override
    public final PlayerStatus getStatus() {
        return (PlayerStatus) super.getStatus();
    }

    @Override
    public void setStatus(Status status) {
        this.status = (PlayerStatus) status;
    }

    @Override
    public boolean canMove() {
        return canMove;
    }

    @Override
    public boolean moveTo(Point3D destination) {
        return false;
    }

    @Override
    public void onDeath() {

    }
}
