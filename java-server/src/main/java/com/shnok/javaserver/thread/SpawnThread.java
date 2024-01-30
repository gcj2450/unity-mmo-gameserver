package com.shnok.javaserver.thread;

import com.shnok.javaserver.Config;
import com.shnok.javaserver.db.entity.SpawnList;
import com.shnok.javaserver.enums.NpcType;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.entity.NpcInstance;
import com.shnok.javaserver.model.template.NpcTemplate;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.node.Node;
import com.shnok.javaserver.service.WorldManagerService;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class SpawnThread implements Runnable {
    private final SpawnList spawnInfo;
    private final NpcTemplate npcTemplate;

    public SpawnThread(SpawnList spawnInfo, NpcTemplate npcTemplate) {
        this.spawnInfo = spawnInfo;
        this.npcTemplate = npcTemplate;

        if(Config.GEODATA_ENABLED) {
            // adjust spawn info to node y position
            try {
                Node n = Geodata.getInstance().getNodeAt(new Point3D(
                                spawnInfo.getSpawnPosition().getX(),
                                0,
                                spawnInfo.getSpawnPosition().getZ()));

                spawnInfo.setSpawnPosition(n.getCenter());
            } catch (Exception e) {
                log.debug("Adjusted spawninfo to position: {}", spawnInfo.getSpawnPosition());
            }
        }
    }

    @Override
    public void run() {
        try {
            if (npcTemplate == null) {
                log.info("Npc template is null, skipping respawn.");
                return;
            }

            NpcInstance npc = new NpcInstance(WorldManagerService.getInstance().nextID(), npcTemplate);
            npc.setPosition(spawnInfo.getSpawnPosition());
            npc.setHeading(spawnInfo.getHeading());

            if(npc.getTemplate().getType() == NpcType.L2Monster) {
                npc.setStatic(false);
                npc.setRandomWalk(true);
            } else {
                npc.setStatic(true);
                npc.setRandomWalk(false);
            }

            npc.setSpawnInfo(spawnInfo);

            if(Config.KEEP_AI_ALIVE) {
                npc.refreshAI();
            }

            WorldManagerService.getInstance().addNPC(npc);
            //ServerService.getInstance().broadcast(new NpcInfoPacket(npc));

            log.debug("Spawned entity {} with id {} at {}.", npc.getTemplate().getNpcId(), npc.getId(),
                    spawnInfo.getSpawnPosition().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
