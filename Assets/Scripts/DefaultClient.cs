﻿using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using System;
using System.Net;
using System.Net.Sockets;
using System.Threading;
using System.Text;
using System.Threading.Tasks;

public class DefaultClient : MonoBehaviour
{
    // Start is called before the first frame update
    void Start()
    {
      AsynchronousClient.setIp("127.0.0.1");
      AsynchronousClient.setPort(11000);
      Task.Run(AsynchronousClient.StartReceiving);
    }

    public async void Connect() {
      bool result = await Task.Run(AsynchronousClient.Connect);
      Debug.Log("Connected");

    }

    public void SendData() {
      //AsynchronousClient.SendString("123");
      AsynchronousClient.SendPing();
      //Task.Run(AsynchronousClient.Receive);
    //  Receive();
    }

    public void Disconnect() {
      AsynchronousClient.Disconnect();
    }

    /*public async void Receive() {
      Debug.Log("Begin receive");
      await Task.Run(AsynchronousClient.Receive);
    }*/
}
