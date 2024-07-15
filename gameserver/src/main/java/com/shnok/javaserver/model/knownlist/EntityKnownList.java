package com.shnok.javaserver.model.knownlist;

import com.shnok.javaserver.enums.Event;
import com.shnok.javaserver.model.object.GameObject;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.object.entity.NpcInstance;
import com.shnok.javaserver.model.object.entity.PlayerInstance;
import com.shnok.javaserver.util.VectorUtils;
import javolution.util.FastList;
import javolution.util.FastMap;
import lombok.extern.log4j.Log4j2;

import java.util.Collection;
import java.util.Map;

import static com.shnok.javaserver.config.Configuration.server;

@Log4j2
public class EntityKnownList extends ObjectKnownList  {
    private Map<Integer, PlayerInstance> knownPlayers;
    private Map<Integer, Integer> knownRelations;

    public EntityKnownList(Entity activeChar) {
        super(activeChar);
    }

    @Override
    public boolean addKnownObject(GameObject object) {
        return addKnownObject(object, false);
    }

    @Override
    public boolean addKnownObject(GameObject object, boolean silent) {
        if (!super.addKnownObject(object, silent)) {
            return false;
        }
        if (object.isPlayer()) {
            getKnownPlayers().put(object.getId(), (PlayerInstance) object);
            if (server.printKnownList()) {
                log.debug("[{}] Adding known player: {}", getActiveObject().getId(), object.getId());
            }
        } else {
            if (server.printKnownList()) {
                log.debug("[{}] Adding known npc: {}", getActiveObject().getId(), object.getId());
            }
        }

        return true;
    }

    public final boolean knowsThePlayer(PlayerInstance player) {
        return (getActiveChar() == player) || getKnownPlayers().containsKey(player.getId());
    }

    /** Remove all GameObject from knownObjects and knownPlayer of the Entity then cancel Attak or Cast and notify AI. */
    @Override
    public final void removeAllKnownObjects()
    {
        super.removeAllKnownObjects();
        getKnownPlayers().clear();
    }

    @Override
    public boolean removeKnownObject(GameObject object) {
        if (!super.removeKnownObject(object)) {
            return false;
        }

        if (object.isPlayer()) {
            getKnownPlayers().remove(object.getId());
            if(server.printKnownList()) {
                log.debug("[{}] Removing known player: {}", getActiveObject().getId(), object.getId());
            }
        }

        if(getActiveChar().getAi() != null) {
            if(getActiveChar().getAi().getTarget() == object) {
                if(server.printKnownList()) {
                    log.debug("[{}] Removed entity was target", getActiveChar().getId());
                }

                getActiveChar().getAi().notifyEvent(Event.FORGET_OBJECT, object);
                log.debug("[{}] Removed entity was target", getActiveChar().getId());
            }
        }

        object.getKnownList().removeKnownObject(getActiveObject());

        return true;
    }

    public Entity getActiveChar() {
        return (Entity) super.getActiveObject();
    }

    @Override
    public int getDistanceToForgetObject(GameObject object) {
        return 0;
    }

    @Override
    public int getDistanceToWatchObject(GameObject object) {
        return 0;
    }

    public Collection<Entity> getKnownCharacters() {
        FastList<Entity> result = new FastList<>();

        for (GameObject obj : getKnownObjects().values()) {
            if ((obj != null) && (obj.isEntity())) {
                result.add((Entity) obj);
            }
        }

        return result;
    }

    public Collection<Entity> getKnownCharactersInRadius(long radius) {
        FastList<Entity> result = new FastList<>();

        for (GameObject obj : getKnownObjects().values()) {
            if (obj.isPlayer()) {
                if (VectorUtils.checkIfInRange((int) radius, getActiveChar(), obj)) {
                    result.add((PlayerInstance) obj);
                }
            } else if (obj.isNpc()) {
                if (VectorUtils.checkIfInRange((int) radius, getActiveChar(), obj)) {
                    result.add((NpcInstance) obj);
                }
            }
        }

        return result;
    }

    public final Map<Integer, PlayerInstance> getKnownPlayers() {
        if (knownPlayers == null) {
            knownPlayers = new FastMap<Integer, PlayerInstance>().shared();
        }
        return knownPlayers;
    }

    public final Collection<PlayerInstance> getKnownPlayersInRadius(long radius) {
        FastList<PlayerInstance> result = new FastList<>();
        for (PlayerInstance player : getKnownPlayers().values()) {
            if (VectorUtils.checkIfInRange((int) radius, getActiveChar(), player)) {
                result.add(player);
            }
        }

        return result;
    }
}