package sky4s.sduino.serial;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

//import android.util.Log;

public class XModem {
    // Debugging
    private static final String TAG = "BluetoothTestActivity";

    private final int SECSIZE = 128; // packet is per 128-byte?
    private final int MAXERRORS = 10; // retry for 10 times
    private final byte CPMEOF = 26;

    // Protocol bytes
    private final char SOH = 0x01;
    private final char STX = 0x02;
    private final char EOT = 0x04;
    private final char ACK = 0x06;
    private final char NAK = 0x15; // Base-10 value: 21
    private final char CAN = 0x18; // Base-10 value: 24
    private final char CRC = 0x43; // Base-10 value: 67

    // TLV bytes
    private final byte TAG_BYTE = (byte) 0xA5;
    private final byte INV_TAG_BYTE = (byte) 0x5A;

    protected DataInputStream inStream;
    protected DataOutputStream outStream;
    protected PrintWriter errStream;

    // File
    private String fileName;

    public XModem(DataInputStream is, DataOutputStream os, PrintWriter errs) {
        inStream = is;
        outStream = os;
        errStream = errs;
    }

    public XModem() {
        inStream = (DataInputStream) System.in;
        outStream = new DataOutputStream(System.out);
        errStream = new PrintWriter(System.err);
    }

    protected boolean gotChar;

    /*
     * Send a file to the remote
     */
    public boolean send(String tfile) throws IOException {
        fileName = tfile; // full path.
        char errorcount;
        byte blocknumber, character;
        int checksum;
        byte[] sector = new byte[SECSIZE];
        int nbytes;

        DataInputStream dis;
        dis = new DataInputStream(new FileInputStream(tfile));
        errStream.println("file open, ready to send");
//               Log.i(TAG, "file open, ready to send");
        errorcount = 0;
        blocknumber = 0x01;

        gotChar = false;

        do {
            character = getChar();
//                       Log.i(TAG, "Initial character, obtained: " + (int)character);
            gotChar = true;
            if (character != NAK && errorcount < MAXERRORS) {
                ++errorcount;
            }
        } while (character != NAK && errorcount < MAXERRORS);

        errStream.println("transmission beginning");
        if (errorcount == MAXERRORS) {
            xError();
        }

        while ((nbytes = dis.read(sector)) != 0) {
            if (nbytes <= -1) {
                break;
            }
            if (nbytes < SECSIZE) {
                for (int i = nbytes; i < SECSIZE; i++) {
                    sector[i] = (byte) 0xff;
                }
            }
            errorcount = 0;
            while (errorcount < MAXERRORS) {
                errStream.println("{" + blocknumber + "}");
                StringBuffer buf = new StringBuffer().append(blocknumber);
                putChar(SOH, "SOH"); // header
                putChar(blocknumber, "BlockNumber");
                putChar(~blocknumber, "Inverse BlockNumber");
                checksum = 0;
                for (byte b : sector) {
                    checksum += b;
                }
                putChar(sector, checksum % 256, "Sector + Checksum");
//                              putChar(sector, 0x00, "Sector+ Checksum Zero");
//                              putChar(sector, "Sector without Checksum");
                outStream.flush();
                if (getChar() != ACK) {
                    ++errorcount;
                } else {
                    break;
                }
            }
            if (errorcount == MAXERRORS) {
                xError();
            }
            blocknumber = (byte) ((++blocknumber) % 256);
        }
        boolean isAck = false;
        while (!isAck) {
            putChar(EOT, "EOT");
            isAck = getChar() == ACK;
        }
        errStream.println("Transmission complete.");
        return true;
    }

    /**
     * Receive a file from remote
     * @throws IOException
     */
    public boolean recv(String tfile) throws IOException {
        char errorcount;
        byte blocknumber, character;
        int checksum;
        boolean gotChar;
        byte[] sector = new byte[SECSIZE];

        // First off, upon client connecting, sent a NAK byte
        DataOutputStream dos;
        dos = new DataOutputStream(new FileOutputStream(tfile));
        gotChar = false;
        putChar(NAK, "NAK");
        errorcount = 0;
        blocknumber = 0x01;

        // Keep reading bytes as they come.
        do {
            character = getChar();
            gotChar = true;
            if (character != EOT) {
                try {
                    byte not_ch;
                    // first off, SOH byte
                    if (character != SOH) {
                        errStream.println("Not SOH");
                        if (errorcount++ < MAXERRORS) {
                            continue;
                        } else {
                            xError();
                        }
                    }
                    character = getChar(); // blocknumber
                    not_ch = (byte)~getChar(); // ~blocknumber
                    if (character != not_ch) { // blocknumber and ~blocknumber not equal
                        System.out.println("Blocknumber and ~Blocknumber is not complement");
                        errorcount++;
                        continue;
                    }
                    if (character != blocknumber) { // are we reading the same blocknumber?
                        System.out.println("We're not reading the proper blocknumber");
                        errorcount++;
                        continue;
                    }
                    checksum = 0;
                    for (int i = 0; i < SECSIZE; i++) {
                        sector[i] = getChar();
                        checksum += sector[i];
                    }
                    if (checksum != getChar()) {
                        System.out.println("Bad checksum");
                        errorcount++;
                        continue;
                    }
                    putChar(ACK, "ACK");
                    blocknumber++;
                    try {
                        dos.write(sector);
                    } catch (IOException ioe) {
                        errStream.println("write failed. Blocknumber " + blocknumber);
                    }
                } finally {
                    if (errorcount != 0) {
                        putChar(NAK, "NAK");
                    }
                }
            }
        } while (character != EOT);

        dos.close();

        putChar(ACK, "ACK");
        putChar(ACK, "ACK");

        return true;
    }

    // We want to package the bytes with TLV informations.
    public void tlvPacked() {
        // First TLV is file information
        // the bytes goes as follows: TAG_BYTE-1or2-INV_TAG_BYTE-Information length (int 32-bit)-Value (char[])
        String[] fileStrings = fileName.split("/");
        String fileName = fileStrings[fileStrings.length - 1];
        int length = fileName.length();
        byte[] tag = new byte[3];
        byte[] valueFileName = new byte[length];
    }

    protected byte getChar() throws IOException {
//               Log.i(TAG, "getChar");
        byte something = (byte) inStream.read();
//               Log.i(TAG, "getChar from inStream " + (int)something);
        return something;
    }

    protected void putChar(int c, String debug) throws IOException {
        outStream.writeByte(c);
    }

    protected void putChar(byte[] b, String debug) throws IOException {
        outStream.write(b);
    }

    protected void putChar(byte[] b, int c, String debug) throws IOException {
        byte[] b2 = new byte[b.length + 1];
        int i = 0;
        for (byte b1 : b) {
            b2[i++] = b1;
        }
        b2[b2.length - 1] = (byte) c;
        outStream.write(b2);
    }

    protected void xError() {
        errStream.println("too many errors...aborting");
    }
}
