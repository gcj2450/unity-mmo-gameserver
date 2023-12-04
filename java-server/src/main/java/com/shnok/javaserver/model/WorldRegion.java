package com.shnok.javaserver.model;

import com.shnok.javaserver.model.entities.PlayerInstance;
import javolution.util.FastList;
import lombok.extern.log4j.Log4j2;

import java.util.Iterator;

@Log4j2
public class WorldRegion {
    private final FastList<GameObject> visibleObjects;
    private final FastList<WorldRegion> surroundingRegions;
    private final FastList<PlayerInstance> allPlayers;
    private final int tileX, tileY;

    public WorldRegion(int tileX, int tileY)
    {
        allPlayers = new FastList<>();
        surroundingRegions = new FastList<>();
        visibleObjects = new FastList<>();
        this.tileX = tileX;
        this.tileY = tileY;
    }

    public void addVisibleObject(GameObject object) {
        if (object == null) {
            return;
        }
        visibleObjects.add(object);

        if (object instanceof PlayerInstance)
        {
            allPlayers.add((PlayerInstance) object);
        }
    }

    public void removeVisibleObject(GameObject object) {
        if (object == null) {
            return;
        }
        visibleObjects.remove(object);

        if (object instanceof PlayerInstance) {
            allPlayers.remove((PlayerInstance) object);
        }
    }

    public void addSurroundingRegion(WorldRegion region) {
        surroundingRegions.add(region);
    }

    public FastList<WorldRegion> getSurroundingRegions() {
        return surroundingRegions;
    }

    public Iterator<PlayerInstance> iterateAllPlayers()
    {
        return allPlayers.iterator();
    }

    public FastList<GameObject> getVisibleObjects() {
        return visibleObjects;
    }

    public String getName() {
        return "(" + tileX + ", " + tileY + ")";
    }
}
