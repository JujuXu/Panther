package panther.pantherii;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Websocket extends Thread {
    private InputStream wsIN;
    private OutputStream wsOUT;
    private boolean run;

    ArrayList<Integer> stream = new ArrayList<>();
    ArrayList<String> data = new ArrayList<>();
    private ServerSocket serverSocket;
    private Socket client;

    @Override
    public void run() {
        try {
            websocket();
        } catch (IOException | NoSuchAlgorithmException ignored) {

        }
    }

    public void websocket() throws IOException, NoSuchAlgorithmException {
        int port = 80;
        String lh = Inet4Address.getLocalHost().toString();
        String ipv4 = lh.substring(lh.indexOf('/')+1);

        Main.sendLog("Starting WebSocket server on port "+port);

        serverSocket = new ServerSocket(80);

        if (!serverSocket.isClosed()) {
            Main.sendLog("WebSocket server started on " + ipv4 + ":" + port);

            getClient();
        }
    }

    private void getClient() throws IOException, NoSuchAlgorithmException {
        Main.sendLog("Awaiting Panther32 to connect...");

        client = serverSocket.accept();
        //System.out.println(client.getInetAddress());

        wsIN = client.getInputStream();
        wsOUT = client.getOutputStream();
        Scanner scan = new Scanner(wsIN, "UTF-8");

        String data = scan.useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("^GET").matcher(data);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))
                    + "\r\n\r\n").getBytes("UTF-8");
            wsOUT.write(response, 0, response.length);
            wsOUT.flush();

            run = true;

            Main.sendLog("Connection to Panther32 established !");
            new Timer().schedule(new Ping(Main.getWS(),10),10*1000);

            readWS();
        }
    }

    private void readWS() throws IOException, NoSuchAlgorithmException {

        BufferedReader br = new BufferedReader(new InputStreamReader(wsIN));

        while (run) {
            if(br.ready()) {
                int brr = br.read();
                stream.add(brr);
            }

            if(!stream.isEmpty()) {

                if(stream.get(0) == 65533 || stream.get(0) == 0) {
                    stream.remove(0);
                } else if (stream.get(stream.size()-1) == 65533) {
                    StringBuilder msg = new StringBuilder();

                    int size = stream.size()-1;

                    for(int i=0;i<size;i++) {
                        msg.append((char) ((int) stream.get(0)));
                        stream.remove(0);
                    }

                    data.add(msg.toString());
                    System.out.println(msg);
                }
            }
        }

        closeClient();
        getClient();
    }

    private void closeClient() throws IOException {
        while(!client.isClosed()) {
            client.close();
        }
    }

    public void sendData(String str) throws IOException {
        if(wsOUT!=null) {
            wsOUT.write(encodeString(str));
            wsOUT.flush();
        }
    }

    public ArrayList<String> getData() {
        ArrayList<String> ddata = data;
        data = new ArrayList<>();

        return ddata;
    }

    public void reset() {
        run = false;
    }

    private byte[] encodeString(String message) {
        byte[] rawData = message.getBytes(StandardCharsets.UTF_8);

        int headerLength;
        byte[] frame;

        if (rawData.length <= 125) {
            headerLength = 2;
            frame = new byte[headerLength + rawData.length];
            frame[1] = (byte) rawData.length;
        } else if (rawData.length <= 65535) {
            headerLength = 4;
            frame = new byte[headerLength + rawData.length];
            frame[1] = (byte) 126;
            frame[2] = (byte) ((rawData.length >> 8) & 0xFF);
            frame[3] = (byte) (rawData.length & 0xFF);
        } else {
            headerLength = 10;
            frame = new byte[headerLength + rawData.length];
            frame[1] = (byte) 127;
            for (int i = 0; i < 8; i++) {
                frame[9 - i] = (byte) ((rawData.length >> (i * 8)) & 0xFF);
            }
        }

        // Set FIN and opcode
        frame[0] = (byte) 0x81; // FIN + Text Frame

        // Copy payload data into frame
        System.arraycopy(rawData, 0, frame, headerLength, rawData.length);

        return frame;
    }
}
