package panther.pantherii;

import java.io.*;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/**
 * This class handles the WebSocket connection and data transmission. Website used : https://developer.mozilla.org/en-US/docs/Web/API/WebSockets_API/Writing_a_WebSocket_server_in_Java
 */
public class Websocket extends Thread {
    /**
     * The WebSocket input stream.
     */
    private InputStream wsIN;
    /**
     * The WebSocket output stream.
     */
    private OutputStream wsOUT;
    /**
     * The status of the WebSocket connection. True if the connection is active, false otherwise.
     * If the connection is not active, the WebSocket server waits for a new client connection.
     * If the connection is active, the WebSocket server reads data from the client.
     * If set to false, the loop in the readWS() method is stopped.
     */
    private boolean run;
    /**
     * The list of data received over the WebSocket connection.
     * It contains the messages received from the ESP32 in the form of strings, formatted as opcodes.
     * It's a raw data list.
     */
    ArrayList<Integer> stream = new ArrayList<>();
    /**
     * The list of decoded data, formatted as strings for each complete message received from the ESP32.
     */
    ArrayList<String> data = new ArrayList<>();
    /**
     * The WebSocket server socket.
     */
    private ServerSocket serverSocket;
    /**
     * The WebSocket client socket.
     */
    private Socket client;

    /**
     * Starts the methods chain to setup the websocket server and handle the data transmission / reception.
     */

    @Override
    public void run() {
        try {
            websocket();
        } catch (IOException | NoSuchAlgorithmException ignored) {

        }
    }

    /**
     * This method initializes the WebSocket server on port 80 and waits for a client to connect.
     * It logs the server start information and then calls the getClient() method to handle the client connection.
     *
     * @throws IOException If an I/O error occurs when opening the server socket.
     * @throws NoSuchAlgorithmException If the SHA-1 algorithm is not available in the environment.
     */
    public void websocket() throws IOException, NoSuchAlgorithmException {
        int port = 80;
        String lh = Inet4Address.getLocalHost().toString();
        String ipv4 = lh.substring(lh.indexOf('/')+1);

        PantherInterface.sendLog("Starting WebSocket server on port "+port);

        serverSocket = new ServerSocket(80);

        if (!serverSocket.isClosed()) {
            PantherInterface.sendLog("WebSocket server started on " + ipv4 + ":" + port);

            getClient();
        }
    }

    /**
     * This method handles the client connection to the WebSocket server.
     * It waits for a client to connect, then reads the client's handshake request and sends a handshake response.
     * If the handshake is successful, it starts a new Ping task and calls the readWS() method to handle WebSocket data.
     *
     * @throws IOException If an I/O error occurs when accepting the client connection or reading from the input stream.
     * @throws NoSuchAlgorithmException If the SHA-1 algorithm is not available in the environment.
     */
    private void getClient() throws IOException, NoSuchAlgorithmException {
        PantherInterface.sendLog("Awaiting Panther32 to connect...");

        client = serverSocket.accept();
        //System.out.println(client.getInetAddress());

        wsIN = client.getInputStream();
        wsOUT = client.getOutputStream();
        Scanner scan = new Scanner(wsIN, StandardCharsets.UTF_8);

        String data = scan.useDelimiter("\\r\\n\\r\\n").next();
        Matcher get = Pattern.compile("^GET").matcher(data);

        if (get.find()) {
            Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
            match.find();
            byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                    + "Connection: Upgrade\r\n"
                    + "Upgrade: websocket\r\n"
                    + "Sec-WebSocket-Accept: "
                    + Base64.getEncoder().encodeToString(MessageDigest.getInstance("SHA-1").digest((match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes(StandardCharsets.UTF_8)))
                    + "\r\n\r\n").getBytes(StandardCharsets.UTF_8);
            wsOUT.write(response, 0, response.length);
            wsOUT.flush();

            run = true;

            PantherInterface.sendLog("Connection to Panther32 established !");
            new Timer().schedule(new Ping(3*1000),3*1000);

            readWS();
        }
    }

    /**
     * This method handles the WebSocket data from the client.
     * It reads data from the WebSocket input stream and adds it to the data ArrayList.
     * If the WebSocket connection is closed, it calls the closeClient() method and then the getClient() method to wait for a new client connection.
     *
     * @throws IOException If an I/O error occurs when reading from the input stream.
     * @throws NoSuchAlgorithmException If the SHA-1 algorithm is not available in the environment.
     */
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
                    //PantherInterface.sendLog(msg.toString());
                }
            }
        }

        closeClient();
        getClient();
    }

    /**
     * This method closes the WebSocket client connection.
     * It checks if the client connection is closed and then closes the client socket.
     * If the client connection is not closed, it keeps trying to close the client connection until it is closed.
     *
     * @throws IOException If an I/O error occurs when reading from the input stream.
     */
    private void closeClient() throws IOException {
        while(!client.isClosed()) {
            client.close();
        }
    }

    /**
     * Sends a string message over the WebSocket connection.
     *
     */
    public void sendData(String str) throws IOException {
        if(wsOUT!=null) {
            if(!serverSocket.isClosed() && !client.isClosed()) {
                wsOUT.write(encodeString(str));
                wsOUT.flush();
            }
        }
    }

    /**
     * Retrieves the data received over the WebSocket connection.
     * @return ArrayList of String data.
     */
    public ArrayList<String> getData() {
        ArrayList<String> dataR = new ArrayList<>(data);
        checkSize();
        return dataR;
    }

    /**
     * Checks and maintains the size of the data ArrayList.
     */
    private void checkSize() {
        while(data.size() > 20) {
            data.remove(0);
        }
    }

    /**
     * Clears the "ping" messages from the data ArrayList.
     */
    public void clearPings() {
        for(int i=0;i<data.size();i++) {
            if(data.get(i).contains("ping")) {
                data.remove(i);
                i--;
            }
        }
    }

    /**
     * Resets the WebSocket connection.
     */
    public void reset() {
        run = false;
    }

    /**
     * Checks if the WebSocket connection is active.
     * @return boolean indicating the connection status.
     */
    public boolean isConnected() {
        return run;
    }

    /**
     * Encodes a string message into a byte array for transmission over WebSocket.
     * @param message The string message to be encoded.
     * @return The encoded message as a byte array.
     */
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
