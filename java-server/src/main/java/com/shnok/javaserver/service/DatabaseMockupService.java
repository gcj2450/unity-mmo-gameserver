package com.shnok.javaserver.service;

import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.SpawnInfo;
import com.shnok.javaserver.model.entities.Entity;
import com.shnok.javaserver.model.entities.NpcInstance;
import com.shnok.javaserver.model.entities.PlayerInstance;
import com.shnok.javaserver.model.status.NpcStatus;
import com.shnok.javaserver.model.status.PlayerStatus;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class DatabaseMockupService {
    private final FastMap<Integer, Entity> npcList = new FastMap<>();
    private final List<SpawnInfo> spawnList = new ArrayList<>();
    private Point3D playerSpawnPoint;

    @Autowired
    public DatabaseMockupService(@Value("${server.spawn.location.x}") float spawnX,
                                 @Value("${server.spawn.location.y}") float spawnY,
                                 @Value("${server.spawn.location.z}") float spawnZ) {
        this.playerSpawnPoint = new Point3D(spawnX, spawnY, spawnZ);
    }

    public void initialize() {
        log.info("Initializing mockup database.");
        generateNpcList();
        generateSpawnList();
        log.info("Generated {} npc(s)", npcList.size());
        log.info("Generated {} spawn points(s)", spawnList.size());
    }

    private void generateNpcList() {
        NpcStatus status = new NpcStatus(1, 3);
        npcList.put(0, new NpcInstance(0, status, true, false, false, null));
        npcList.put(1, new NpcInstance(1, status, false, true, false, null));
        npcList.put(2, new NpcInstance(2, status, false, false, true, new Point3D[]{
                new Point3D(-5, 0, 5),
                new Point3D(-5, 0, -5),
                new Point3D(5, 0, -5),
                new Point3D(5, 0, 5)
        }));
        npcList.put(3, new NpcInstance(3, status, false, false, true, new Point3D[]{
                new Point3D(-13, 4, 12),
                new Point3D(9, 1, -14)
        }));
    }

    private void generateSpawnList() {
       /* for (int i = 0; i < 3; i++) {
            spawnList.add(new SpawnInfo(spawnList.size(), 0, 1000));
        }
        for (int i = 0; i < 2; i++) {
            spawnList.add(new SpawnInfo(spawnList.size(), 1, 1000));
        }
        spawnList.add(new SpawnInfo(spawnList.size(), 1, 5000, new Point3D(-5, 2, 6)));
        spawnList.add(new SpawnInfo(spawnList.size(), 2, 5000, new Point3D(-5, 0, 5)));
        spawnList.add(new SpawnInfo(spawnList.size(), 3, 5000, new Point3D(-13, 4, 12)));*/
    }

    public NpcInstance getNpc(int id) {
        if (npcList.containsKey(id)) {
            NpcInstance n = (NpcInstance) npcList.get(id);
            NpcStatus s = n.getStatus();
            return new NpcInstance(
                    n.getNpcId(),
                    new NpcStatus(s.getLevel(),
                            s.getMaxHp()),
                    n.isStatic(),
                    n.doRandomWalk(),
                    n.doPatrol(),
                    n.getPatrolWaypoints());
        }
        return null;
    }

    public PlayerInstance getPlayerData(String username) {
        PlayerInstance player = new PlayerInstance(0, username);
        player.setStatus(new PlayerStatus());
        player.setPosition(playerSpawnPoint);
        return player;
    }

    public List<SpawnInfo> getSpawnList() {
        return spawnList;
    }

}
