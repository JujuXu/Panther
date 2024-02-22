package panther.pantherii;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Websocket extends Thread {
    @Override
    public void run() {
        try {
            websocket();
        } catch (IOException | NoSuchAlgorithmException ignored) {

        }
    }

    public void websocket() throws IOException, NoSuchAlgorithmException {
        int port = 80;
        String ipv4 = "192.168.1.157";

        System.out.println("Starting WebSocket server...");

        ServerSocket serverSocket = new ServerSocket(80);

        if (!serverSocket.isClosed()) {
            System.out.println("WebSocket server started on " + ipv4 + ":" + port);

            System.out.println("Awaiting client...");

            Socket client = serverSocket.accept();
            System.out.println(client.getInetAddress());

            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();
            Scanner scan = new Scanner(in, "UTF-8");

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
                out.write(response, 0, response.length);
                out.flush();

                System.out.println("Connection established !");
            }

            link(serverSocket,in,out);
        }
    }

    private void link(ServerSocket ser, InputStream in, OutputStream out) throws IOException, NoSuchAlgorithmException {
        boolean run = true;

        BufferedReader br = new BufferedReader(new InputStreamReader(in));

        ArrayList<Integer> stream = new ArrayList<>();

        while (run) {
            int brr = br.read();
            System.out.println(brr);
            if(brr == -1) {
                ser.close();
                websocket();
                return;
            }

            if(br.ready()) {

                stream.add(brr);

                    /*byte[] a = {(byte) 129, (byte) 134, (byte) 167, (byte) 225, (byte) 225, (byte) 210, (byte) 198, (byte) 131, (byte) 130, (byte) 182, (byte) 194, (byte) 135};
                    out.write(a);
                    out.flush();*/
            }

            if(!stream.isEmpty()) {
                //System.out.println(stream);

                if(stream.get(0) == 65533 || stream.get(0) == 0) {
                    stream.remove(0);
                } else if (stream.get(stream.size()-1) == 65533) {
                    //System.out.println(stream);
                    //System.out.println(stream.size());
                    StringBuilder msg = new StringBuilder();

                    int size = stream.size()-1;

                    for(int i=0;i<size;i++) {
                        msg.append((char) ((int) stream.get(0)));
                        stream.remove(0);
                    }

                    System.out.println(msg);
                    out.write(encodeString("C'est nickel ça !"));
                    out.flush();
                }
            }
        }
    }

    public byte[] encodeString(String message) {
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
