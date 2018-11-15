package com.inyanga.blecontrollerutility.util;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

public class CtlConfigurator implements Runnable {

    private ByteSerializer streamToController;
    private Socket socket;
    private CfgState state = CfgState.NOT_INITED;

    private enum CfgState {
        NOT_INITED,
        SENT_GET_CFG_RQ,
        RECEIVED_CFG,
        SENT_SET_CFG_RQ
    }

    public void setServerIp(final String ip, final int port) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);
                    streamToController = new ByteSerializer(socket.getInputStream(), socket.getOutputStream());

                    NetworkMessage.MsgCtlGetConfigRq cfgRq = new NetworkMessage.MsgCtlGetConfigRq();
                    cfgRq.paramId = 13;
//                    NetworkMessage.MsgCtlSetConfigRq cfgRq = new NetworkMessage.MsgCtlSetConfigRq();
//                    cfgRq.paramId = 13;
//                    cfgRq.valueString = "192.124.187.243:5720";
                    NetworkMessage.writeTo(streamToController, cfgRq);
                    state = CfgState.SENT_GET_CFG_RQ;
                    Log.i("NETWORK_CONNECTION", "Packet sent");
                    Thread runnerThread = new Thread(CtlConfigurator.this);
                    runnerThread.setDaemon(true);
                    runnerThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    @Override
    public void run() {
        Log.i("NETWORK_CONNECTION", "Network thread started: ");
        Log.i("NETWORK_CONNECTION", "Socket is closed: " + socket.isClosed());
        try {
            while (!socket.isClosed()) {
                NetworkMessage.Msg m = NetworkMessage.readFrom(streamToController);
                onMessage(m);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void onMessage(NetworkMessage.Msg msg) throws Exception {
        if (msg instanceof NetworkMessage.MsgCtlGetConfigRp) {
            NetworkMessage.MsgCtlGetConfigRp rp = (NetworkMessage.MsgCtlGetConfigRp) msg;
            if (state == CfgState.SENT_GET_CFG_RQ) {
                Log.i("NETWORK_CONNECTION", "Current ip: " + rp.valueString);
                NetworkMessage.MsgCtlSetConfigRq rq = new NetworkMessage.MsgCtlSetConfigRq();
                rq.paramId = 13;
                rq.valueString = "192.124.187.243:5720";
                NetworkMessage.writeTo(streamToController, rq);
                state = CfgState.SENT_SET_CFG_RQ;
            } else {
                Log.i("NETWORK_CONNECTION", "Current ip: " + rp.valueString);
            }
        }
    }
}
