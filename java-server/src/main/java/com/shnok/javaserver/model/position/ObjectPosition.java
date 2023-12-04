package com.shnok.javaserver.model.position;

import com.shnok.javaserver.model.GameObject;
import com.shnok.javaserver.model.Point3D;
import com.shnok.javaserver.model.WorldRegion;
import com.shnok.javaserver.service.WorldManagerService;
import lombok.Data;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Data
public class ObjectPosition
{
    private final GameObject activeObject;
    private int heading = 0;
    private Point3D worldPosition;
    private WorldRegion worldRegion; // Object localization : Used for items/chars that are seen in the world

    public ObjectPosition(GameObject activeObject) {
        this.activeObject = activeObject;

        System.out.println(WorldManagerService.getInstance());
        setWorldRegion(WorldManagerService.getInstance().getRegion(getWorldPosition()));
    }

    public final void setXYZ(float x, float y, float z) {
        setWorldPosition(x, y, z);

        if (WorldManagerService.getInstance().getRegion(getWorldPosition()) != getWorldRegion()) {
            updateWorldRegion();
        }
    }

    /**
     * checks if current object changed its region, if so, update referencies
     */
    public void updateWorldRegion() {
        if (!getActiveObject().isVisible()) {
            return;
        }

        WorldRegion newRegion = WorldManagerService.getInstance().getRegion(getWorldPosition());
        if (newRegion != getWorldRegion()) {
            getWorldRegion().removeVisibleObject(getActiveObject());

            setWorldRegion(newRegion);

            // Add the L2Oject spawn to _visibleObjects and if necessary to _allplayers of its L2WorldRegion
            getWorldRegion().addVisibleObject(getActiveObject());
        }
    }

    public float getX()
    {
        return getWorldPosition().getX();
    }

    public void setX(int value)
    {
        getWorldPosition().setX(value);
    }

    public float getY()
    {
        return getWorldPosition().getY();
    }

    public void setY(int value)
    {
        getWorldPosition().setY(value);
    }

    public float getZ()
    {
        return getWorldPosition().getZ();
    }

    public void setZ(int value)
    {
        getWorldPosition().setZ(value);
    }

    public Point3D getWorldPosition() {
        if (worldPosition == null) {
            worldPosition = new Point3D(0, 0, 0);
        }
        return worldPosition;
    }

    public void setWorldPosition(float x, float y, float z) {
        getWorldPosition().setXYZ(x, y, z);
    }

    public final void setWorldPosition(Point3D newPosition) {
        setWorldPosition(newPosition.getX(), newPosition.getY(), newPosition.getZ());
    }

    public final WorldRegion getWorldRegion()
    {
        return worldRegion;
    }

    public final void setWorldRegion(WorldRegion value) {
        worldRegion = value;
    }
}