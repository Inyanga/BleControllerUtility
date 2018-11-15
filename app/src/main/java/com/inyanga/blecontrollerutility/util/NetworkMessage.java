package com.inyanga.blecontrollerutility.util;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;

public class NetworkMessage {
    public final static int PROTOCOL_VERSION_1 = 1;

    public enum ConfigParam {
        SERIAL(0),
        BLE_MAC(1),
        WIFI_MAC(2),
        ETH_IP(3),
        ETH_MASK(4),
        ETH_GW(5),
        BLE_NAME(6),
        BLE_ADV_PACKET(7),
        WIFI_IP(8),
        WIFI_MASK(9),
        WIFI_GW(10),
        WIFI_SSID(11),
        TIME(12),
        SVR_PRI(13),
        SVR_SEC(14),
        CRYPTO_SUPPORTED_KEYS(20),
        CRYPTO_KEY_TYPE(21),
        CRYPTO_PUBLIC_KEY(22),
        CRYPTO_CERT_CHAIN(23),
        ROOT_0_DN(30),
        ROOT_0_PUBLIC_KEY(31),
        ROOT_1_DN(32),
        ROOT_1_PUBLIC_KEY(33),
        OPENED_INTERVAL(40);

        short paramId;

        private ConfigParam(int paramId) {
            this.paramId = (short) paramId;
        }

        public short getParamId() {
            return paramId;
        }
    }

    public enum ResultCode {
        OK(0),
        FAILED(1),
        INVALID_DATE(2),
        INVALID_SIGNATURE(3),
        PERMIT_NOT_VALID_FOR_CLIENT(4),
        INVALID_STATE(5),
        NOT_ENOUGH_RIGHTS(6),
        PERMIT_NOT_VALID_FOR_CONTROLLER(7),
        PERMIT_ISSUER_NOT_VALID(8),
        ///// server errors

        CONTROLLER_NOT_CONNECTED(1000),
        INVALID_ARGUMENT(1001),
        IO_ERROR(1003),
        INTERNAL_ERROR(1500);


        short val;
        ResultCode(int val) {
            this.val = (short)val;
        }

        public short getVal() {
            return val;
        }
    }


    public final static MsgBasicRp RP_OK = new MsgBasicRp(ResultCode.OK, "OK");


    public static class MsgDef {
        public MsgDef(int code, Class msgCls) {
            this.code = code;
            this.msgCls = msgCls;
        }

        public final int code;
        public final Class msgCls;
    }

    public final static MsgDef[] msgDefs = new MsgDef[]{
            new MsgDef(0, MsgBasicRp.class),
            new MsgDef(1, MsgSessionInitRq.class),
            new MsgDef(2, MsgSessionInitRp.class),
            new MsgDef(3, MsgAuthenticateRq.class), // MsgBasicRp response
            new MsgDef(4, MsgPresentPermitRq.class), // MsgBasicRp response
            new MsgDef(5, MsgSvrRequestPermit.class),
            new MsgDef(6, MsgSvrRequestPermitRp.class),
            new MsgDef(7, MsgPingRq.class),
            new MsgDef(8, MsgPingRp.class),
            new MsgDef(9, MsgSvrRequestPermit.class),
            new MsgDef(10, MsgSvrRequestPermitRp.class),
            new MsgDef(11, MsgCtlGetConfigRq.class),
            new MsgDef(12, MsgCtlGetConfigRp.class),
            new MsgDef(13, MsgCtlSetConfigRq.class),
            new MsgDef(14, MsgCtlSystemRq.class),
            new MsgDef(15, MsgCtlEventNotify.class),
            new MsgDef(16, MsgCtlEventNotifyRp.class),
            new MsgDef(100, MsgBye.class),
    };

    public static abstract class Msg {
        public int protoVersion;

        public abstract void writeTo(ByteSerializer out) throws IOException;

        public abstract void readFrom(ByteSerializer in) throws IOException;
    }

    public static class MsgSessionInitRq extends Msg {
        public int sessionId;
        public LinkedList<byte[]> certChain;
        public byte[] authChallenge;
        public byte cipher; // не используется, зарезервировано на будущее
        public byte[] ephemeralPublicKey; // не используется, зарезервировано на будущее

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeInt(sessionId);
            out.writeBytesList(certChain);
            out.writeBytes(authChallenge);
            out.writeByte(cipher);
            out.writeBytes(ephemeralPublicKey);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            sessionId = in.readInt();
            certChain = in.readBytesList();
            authChallenge = in.readBytes();
            cipher = in.readByte();
            ephemeralPublicKey = in.readBytes();
        }
    }

    public static class MsgSessionInitRp extends MsgBasicRp {
        public String fullDN;
        public LinkedList<byte[]> certChain;
        public byte[] authChallenge;
        public byte[] challengeResponse;
        public byte[] ephemeralPublicKey; // не используется, зарезервировано на будущее

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            super.writeTo(out);
            out.writeString(fullDN);
            out.writeBytesList(certChain);
            out.writeBytes(authChallenge);
            out.writeBytes(challengeResponse);
            out.writeBytes(ephemeralPublicKey);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            super.readFrom(in);
            fullDN = in.readString();
            certChain = in.readBytesList();
            authChallenge = in.readBytes();
            challengeResponse = in.readBytes();
            ephemeralPublicKey = in.readBytes();
        }
    }

    public static class MsgAuthenticateRq extends Msg {
        public byte[] challengeResponse;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeBytes(challengeResponse);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            challengeResponse = in.readBytes();
        }
    }

    public static class MsgPingRq extends Msg {
        public short ping;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeShort(ping);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            ping = in.readShort();
        }
    }

    public static class MsgPingRp extends Msg {
        public short pong;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeShort(pong);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            pong = in.readShort();
        }
    }

    public static class MsgBye extends Msg {
        @Override
        public void writeTo(ByteSerializer out) throws IOException {
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
        }
    }

    public static class MsgBasicRp extends Msg {
        public short resultCode;
        public String info;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeShort(resultCode);
            out.writeString(info);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            resultCode = in.readShort();
            info = in.readString();
        }

        public MsgBasicRp() {
        }

        public MsgBasicRp(ResultCode rc, String info) {
            this.resultCode = rc.getVal();
            this.info = info;
        }

        public MsgBasicRp(short resultCode, String info) {
            this.resultCode = resultCode;
            this.info = info;
        }
    }

    //////////// Сообщения клиент <-> контроллер

    public static class MsgPresentPermitRq extends Msg {
        public byte[] permitData;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeBytes(permitData);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            permitData = in.readBytes();
        }
    }

    //////////// Сообщения клиент <-> сервер

    public static class MsgSvrRequestPermit extends Msg {
        public String subjectDNMask;
        public Date validFrom;
        public Date validTo;
        public LinkedList<String> validForCtls;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeString(subjectDNMask);
            out.writeDate(validFrom);
            out.writeDate(validTo);
            out.writeStringList(validForCtls);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            subjectDNMask = in.readString();
            validFrom = in.readDate();
            validTo = in.readDate();
            validForCtls = in.readStringList();
        }
    }

    public static class MsgSvrRequestPermitRp extends MsgBasicRp {
        public byte[] permit;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            super.writeTo(out);
            out.writeBytes(permit);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            super.readFrom(in);
            permit = in.readBytes();
        }
    }

    //////////// Сообщения контроллер <-> сервер

    public static class MsgCtlGetConfigRq extends Msg {
        public short paramId;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeShort(paramId);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            paramId = in.readShort();
        }
    }

    public static class MsgCtlGetConfigRp extends MsgBasicRp {
        public short paramId;
        public String valueString;
        public short valueShort;
        public byte[] valueBytes;

        public MsgCtlGetConfigRp() {
        }

        public MsgCtlGetConfigRp(ResultCode rc, String info) {
            super(rc, info);
        }

        public MsgCtlGetConfigRp(short paramId, short val) {
            super(RP_OK.resultCode, RP_OK.info);
            this.paramId = paramId;
            this.valueShort = val;
        }

        public MsgCtlGetConfigRp(short paramId, String val) {
            super(RP_OK.resultCode, RP_OK.info);
            this.paramId = paramId;
            this.valueString = val;
        }

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            super.writeTo(out);
            out.writeShort(paramId);
            out.writeString(valueString);
            out.writeShort(valueShort);
            out.writeBytes(valueBytes);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            super.readFrom(in);
            paramId = in.readShort();
            valueString = in.readString();
            valueShort = in.readShort();
            valueBytes = in.readBytes();
        }
    }

    public static class MsgCtlSetConfigRq extends Msg {
        public short paramId;
        public String valueString;
        public short valueShort;
        public byte[] valueBytes;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeShort(paramId);
            out.writeString(valueString);
            out.writeShort(valueShort);
            out.writeBytes(valueBytes);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            paramId = in.readShort();
            valueString = in.readString();
            valueShort = in.readShort();
            valueBytes = in.readBytes();
        }
    }

    public static class MsgCtlSystemRq extends Msg {
        public final static short CMD_RESET = 99, CMD_REBOOT = 0, CMD_UPDATE = 2, CMD_OPEN = 10;
        public short cmd;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeShort(cmd);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            cmd = in.readShort();
        }
    }

    public static class EventData {
        public static final byte TYPE_PERMIT_PASSED = 1, TYPE_PERMIT_FAILED = 2;
        public int timestamp;
        public byte type;
        public String params;
    }

    public static class MsgCtlEventNotify extends Msg {
        public LinkedList<EventData> events;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeShort((short) events.size());
            for (EventData event : events) {
                out.writeInt(event.timestamp);
                out.writeByte(event.type);
                out.writeString(event.params);
            }
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            short cnt = in.readShort();
            events = new LinkedList<>();
            for (int n = 0; n < cnt; ++n) {
                EventData evt = new EventData();
                evt.timestamp = in.readInt();
                evt.type = in.readByte();
                evt.params = in.readString();
                events.addLast(evt);
            }
        }
    }

    public static class MsgCtlEventNotifyRp extends Msg {
        public int maxConfirmedTimestamp;

        @Override
        public void writeTo(ByteSerializer out) throws IOException {
            out.writeInt(maxConfirmedTimestamp);
        }

        @Override
        public void readFrom(ByteSerializer in) throws IOException {
            maxConfirmedTimestamp = in.readInt();
        }
    }
    ////////////

    public static void writeTo(ByteSerializer out, Msg msg) throws IOException {
        Integer code = null;
        for (MsgDef msgDef : msgDefs) {
            if (msgDef.msgCls == msg.getClass()) {
                code = msgDef.code;
                break;
            }
        }
        if (code == null)
            throw new IOException("Message code missing for message class: " + msg.getClass());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        msg.writeTo(new ByteSerializer(bos));
        byte[] data = bos.toByteArray();
        ///
        out.writeByte(PROTOCOL_VERSION_1);
        out.writeShort((short) (data.length + 1));
        out.writeByte(code);
        out.writeBytesRaw(data);
        out.flush();
    }

    public static Msg readFrom(ByteSerializer in) throws Exception {
        int protoVersion = in.readByte();
        if (protoVersion != PROTOCOL_VERSION_1)
            throw new Exception("Protocol version for message not supported: " + protoVersion);
        /////
        short length = in.readShort();
        int cmd = in.readByte();
        Msg message = null;
        for (MsgDef msgDef : msgDefs) {
            if (msgDef.code == cmd) {
                message = (Msg) (msgDef.msgCls.newInstance());
                break;
            }
        }
        if (message == null) throw new Exception("Invalid command code: " + cmd);
        message.readFrom(in);
        message.protoVersion = (byte) protoVersion;
        return message;
    }

    public static Msg readFrom(DataInputStream is) throws Exception {
        return readFrom(new ByteSerializer(is));
    }
}

