using UnityEngine;
using System;
public class UpdateAnimationPacket : ServerPacket {
    private int _id;
    private int _animId;
    private float _value;

    public UpdateAnimationPacket(){}
    public UpdateAnimationPacket(byte[] d) : base(d) {
        Parse();
    }
    
    public override void Parse() {    
        try {
            _id = ReadI();
            _animId = ReadI();
            _value = ReadF();
        } catch(Exception e) {
            Debug.Log(e);
        }
    }

    public int getId() {
        return _id;
    }

    public int getAnimId() {
        return _animId;
    }

    public float getValue() {
        return _value;
    }
}