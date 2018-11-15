package com.inyanga.blecontrollerutility.util;

import javax.crypto.Cipher;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class ByteSerializer {
    DataOutputStream out;
    DataInputStream in;

    public ByteSerializer(OutputStream outputStream) {
        out = new DataOutputStream(outputStream);
    }

    public ByteSerializer(InputStream inputStream) {
        in = new DataInputStream(inputStream);
    }

    public ByteSerializer(InputStream inputStream, OutputStream outputStream) {
        in = new DataInputStream(inputStream);
        out = new DataOutputStream(outputStream);
    }

    protected void setOut(DataOutputStream out) {
        this.out = out;
    }

    public void writeString(String s) throws IOException {
        writeBytes(s == null ? null : s.getBytes("UTF-8"));
    }

    public String readString() throws IOException {
        byte[] b = readBytes();
        if (b == null) return null;
        return new String(b, "UTF-8");
    }

    public void writeDate(Date date) throws IOException {
        writeString(new SimpleDateFormat("yyyyMMddHHmmss").format(date));
    }

    public Date readDate() throws IOException {
        try {
            return new SimpleDateFormat("yyyyMMddHHmmss").parse(readString());
        } catch (ParseException e) {
            throw new IOException(e);
        }
    }

    public void writeStringList(LinkedList<String> linkedList) throws IOException {
        out.writeShort(linkedList.size());
        for (String s : linkedList) writeString(s);
    }

    public LinkedList<String> readStringList() throws IOException {
        int size = in.readShort();
        LinkedList<String> l = new LinkedList<>();
        for (int n = 0; n < size; ++n) {
            l.addLast(readString());
        }
        return l;
    }

    public void writeBytesList(LinkedList<byte[]> linkedList) throws IOException {
        out.writeShort(linkedList.size());
        for (byte[] s : linkedList) writeBytes(s);
    }

    public LinkedList<byte[]> readBytesList() throws IOException {
        int size = in.readShort();
        LinkedList<byte[]> l = new LinkedList<>();
        for (int n = 0; n < size; ++n) {
            l.addLast(readBytes());
        }
        return l;
    }

    public void writeBytes(byte[] bytes) throws IOException {
        if (bytes == null) out.writeShort(-1);
        else {
            out.writeShort(bytes.length);
            out.write(bytes);
        }
    }

    public void writeBytesRaw(byte[] data) throws IOException {
        out.write(data);
    }

    public byte[] readBytes() throws IOException {
        int l = in.readShort();
        if (l == -1) return null;
        byte[] b = new byte[l];
        if (in.read(b) != l) throw new IOException("Unexpected EOF");
        return b;
    }

    public void flush() throws IOException {
        out.flush();
    }

    public void writeShort(short value) throws IOException {
        out.writeShort(value);
    }

    public short readShort() throws IOException {
        return in.readShort();
    }

    public void writeByte(int b) throws IOException {
        out.writeByte(b);
    }

    public byte readByte() throws IOException {
        return in.readByte();
    }

    public void writeInt(int i) throws IOException {
        out.writeInt(i);
    }

    public int readInt() throws IOException {
        return in.readInt();
    }

    public void disconnect() {
        //To change body of created methods use File | Settings | File Templates.
    }
}