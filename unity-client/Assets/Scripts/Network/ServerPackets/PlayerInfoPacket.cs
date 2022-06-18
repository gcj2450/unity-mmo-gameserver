using UnityEngine;
using System;

public class PlayerInfoPacket : ServerPacket {

    private NetworkIdentity _identity = new NetworkIdentity();
    private PlayerStatus _status = new PlayerStatus();

    public PlayerInfoPacket(){}
    public PlayerInfoPacket(byte[] d) : base(d) {
        Parse();
    }
    
    public override void Parse() {    
        try {
            _identity.Id = ReadI();
            _identity.Name = ReadS();
            _identity.SetPosX(ReadF());
            _identity.SetPosY(ReadF());
            _identity.SetPosZ(ReadF());
            _identity.Owned = _identity.Name == DefaultClient.GetInstance().username;
            _status.Level = ReadI();
            _status.Hp = ReadI();
            _status.MaxHp = ReadI();
            _status.Stamina = ReadI();
            _status.MaxStamina = ReadI();
        } catch(Exception e) {
            Debug.Log(e);
        }
    }

    public NetworkIdentity GetIdentity() {
        return _identity;
    }

    public PlayerStatus GetStatus() {
        return _status;
    }
}