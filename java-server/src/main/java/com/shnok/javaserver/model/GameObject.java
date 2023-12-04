package com.shnok.javaserver.model;

import com.shnok.javaserver.model.position.ObjectPosition;
import lombok.Data;

/**
 * This class represents all spawnable objects in the world.<BR>
 * <BR>
 * Such as : static object, player, npc, item... <BR>
 * <BR>
 */
@Data
public abstract class GameObject {
    protected int id;
    protected int model;
    protected boolean visible = true;
    protected ObjectPosition position;

    public GameObject() {
    }

    public GameObject(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public final ObjectPosition getPosition() {
        if (position == null) {
            position = new ObjectPosition(this);
        }
        return position;
    }

    public void setPosition(Point3D position) {
        getPosition().setXYZ(position.getX(), position.getY(), position.getZ());
    }

    public Point3D getPos() {
        return getPosition().getWorldPosition();
    }

    public final float getPosX() {
        return getPosition().getX();
    }

    public final float getPosY() {
        return getPosition().getY();
    }

    public final float getPosZ() {
        return getPosition().getZ();
    }

    /**
     * returns reference to region this object is in
     * @return
     */
    public WorldRegion getWorldRegion()
    {
        return getPosition().getWorldRegion();
    }

}
