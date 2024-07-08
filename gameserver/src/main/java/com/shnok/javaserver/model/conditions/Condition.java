package com.shnok.javaserver.model.conditions;

import com.shnok.javaserver.db.entity.DBItem;
import com.shnok.javaserver.model.object.entity.Entity;
import com.shnok.javaserver.model.skills.Skill;

/**
 * The Class Condition.
 * @author mkizub
 */
public abstract class Condition implements ConditionListener {
    private ConditionListener _listener;
    private String _msg;
    private int _msgId;
    private boolean _addName = false;
    private boolean _result;

    /**
     * Sets the message.
     * @param msg the new message
     */
    public final void setMessage(String msg) {
        _msg = msg;
    }

    /**
     * Gets the message.
     * @return the message
     */
    public final String getMessage() {
        return _msg;
    }

    /**
     * Sets the message id.
     * @param msgId the new message id
     */
    public final void setMessageId(int msgId) {
        _msgId = msgId;
    }

    /**
     * Gets the message id.
     * @return the message id
     */
    public final int getMessageId() {
        return _msgId;
    }

    /**
     * Adds the name.
     */
    public final void addName() {
        _addName = true;
    }

    /**
     * Checks if is adds the name.
     * @return true, if is adds the name
     */
    public final boolean isAddName() {
        return _addName;
    }

    /**
     * Sets the listener.
     * @param listener the new listener
     */
    void setListener(ConditionListener listener) {
        _listener = listener;
        notifyChanged();
    }

    /**
     * Gets the listener.
     * @return the listener
     */
    final ConditionListener getListener() {
        return _listener;
    }

    public final boolean test(Entity caster, Entity target, Skill skill) {
        return test(caster, target, skill, null);
    }

    public final boolean test(Entity caster, Entity target, DBItem item) {
        return test(caster, target, null, null);
    }

    public final boolean test(Entity caster, Entity target, Skill skill, DBItem item) {
        boolean res = testImpl(caster, target, skill, item);
        if ((_listener != null) && (res != _result)) {
            _result = res;
            notifyChanged();
        }
        return res;
    }

    /**
     * Test the condition.
     * @param effector the effector
     * @param effected the effected
     * @param skill the skill
     * @param item the item
     * @return {@code true} if successful, {@code false} otherwise
     */
    public abstract boolean testImpl(Entity effector, Entity effected, Skill skill, DBItem item);

    @Override
    public void notifyChanged() {
        if (_listener != null) {
            _listener.notifyChanged();
        }
    }
}

