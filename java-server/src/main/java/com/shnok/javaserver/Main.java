package com.shnok.javaserver;

import com.shnok.javaserver.db.repository.NpcRepository;
import com.shnok.javaserver.db.service.DatabaseMockupService;
import com.shnok.javaserver.pathfinding.Geodata;
import com.shnok.javaserver.pathfinding.PathFinding;
import com.shnok.javaserver.service.*;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Main {
    public static void main(String[] args) {
       runServer(args);
    }

    public static void runServer(String... args)  {
        log.info("Starting application.");
        try {
            Config.LoadSettings();
        } catch (Exception e) {
            log.error("Error while loading config file.", e);
            return;
        }

        //SpawnListRepository spawnListRepository = new SpawnListRepository();
        //SpawnList spawnList = new SpawnList(25000,"partisan_agit_2121_01",1,35372,44368,107440,-2032,0,0,0,60,0,0);
        //spawnListRepository.addSpawnList(spawnList);
        NpcRepository npcRepository = new NpcRepository();
        npcRepository.getNpcById(12077);

        ThreadPoolManagerService.getInstance().initialize();
        Runtime.getRuntime().addShutdownHook(ServerShutdownService.getInstance());

        //TODO: Update for gradle and new geodata structure
        Geodata.getInstance();
        PathFinding.getInstance();

        WorldManagerService.getInstance().initialize();
        GameTimeControllerService.getInstance().initialize();
        DatabaseMockupService.getInstance().initialize();
        SpawnManagerService.getInstance().initialize();

        GameServerListenerService.getInstance().Initialize();
        GameServerListenerService.getInstance().start();
        try {
            GameServerListenerService.getInstance().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("Application closed");
    }
}
