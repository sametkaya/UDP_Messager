/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.EventListener;
import java.util.EventObject;
import javax.swing.event.EventListenerList;

/**
 *
 * @author INSECT
 */
public class UdpServer {

    private static DatagramSocket SocketThis; // dinlenecek olan soket
    private static Thread Listen; //soketi dinlemek için thread
    private static ArrayList<Client> ConnectedClients; // gelen clientları turacak olan liste
    private static EventListenerList MessageReceivedListenerList = new EventListenerList();

    public static void Create(int Port) throws SocketException {
        UdpServer.SocketThis = new DatagramSocket(Port);
        UdpServer.ConnectedClients = new ArrayList<Client>();
        //MessageReceivedListenerList = new EventListenerList();
        UdpServer.Listen = new Thread() {
            public void run() {
                // byte[] SData = new byte[1024];
                // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Server Ready");
                while (true) {
                    try {

                        byte[] RData = new byte[1024]; //buffer hafızası
                        DatagramPacket RPack = new DatagramPacket(RData, RData.length); // datagramm paket oluşturuluyor
                        //blocking var
                        UdpServer.SocketThis.receive(RPack);  // paket dinleme işlemi
                        String Text = new String(RPack.getData());

                        //  System.out.println("\nFrom Client " + RPack.getAddress() + " <<< " + Text);
                        UdpServer.FireMessageReceivedEvent(Text);
                        Client newclient = new Client(RPack.getAddress(), RPack.getPort());
                        UdpServer.ConnectedClients.add(newclient);
                        //  AddClient(newclient);
                        RData = null;
                        RPack = null;
                    } catch (Exception e) {

                        System.out.println("hata ");
                    }
                }

            }

        };

    }
    public static void Start() {
        UdpServer.Listen.start();

    }
    public static void Stop() {
        UdpServer.Listen.interrupt();
        UdpServer.ConnectedClients.clear();
        UdpServer.SocketThis.close();

    }

    public static void AddClient(Client newclient) {
        boolean isexist = false;
        for (Client ConnectedClient : ConnectedClients) {
            if (ConnectedClient.Ip.toString().equals(newclient.Ip.toString()) && ConnectedClient.Port == newclient.Port) {
                isexist = true;
            }
        }

        if (!isexist) {
            UdpServer.ConnectedClients.add(newclient);
        }

    }

    public static void SentMessage(String Message) throws IOException {
        byte[] SData = new byte[1024];
        SData = Message.getBytes();
        for (Client T : ConnectedClients) {
            System.out.print("Msg to" + T.Ip + " Cleint : ");
            DatagramPacket SPack = new DatagramPacket(SData, SData.length, T.Ip, T.Port);
            UdpServer.SocketThis.send(SPack);
            SPack = null;
            SData = null;
        }
    }

    public static void SendFile(String FilePath) throws IOException {

        File file = new File(FilePath);
        FileInputStream fis = new FileInputStream(file);
        byte[] SData = new byte[(int) file.length()];
        fis.read(SData, 0, SData.length);
        for (Client T : ConnectedClients) {
            System.out.print("Msg to" + T.Ip + " Cleint : ");
            DatagramPacket SPack = new DatagramPacket(SData, SData.length, T.Ip, T.Port);
            UdpServer.SocketThis.send(SPack);
            SPack = null;
            SData = null;
        }

        fis.close();
    }

    public class ReceivedMessage extends EventObject {

        public ReceivedMessage(Object source) {
            super(source);
        }
    }

    public interface ReceivedMessageListener extends EventListener {

        public void MessageReceived(ReceivedMessage evt);

        public void MessageReceived(String evt);
    }

    public static void addMyEventListener(ReceivedMessageListener listener) {
        UdpServer.MessageReceivedListenerList.add(ReceivedMessageListener.class, listener);
    }

    private static void FireMessageReceivedEvent(ReceivedMessage evt) {
        Object[] listeners = MessageReceivedListenerList.getListenerList();
        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == ReceivedMessageListener.class) {
                ((ReceivedMessageListener) listeners[i + 1]).MessageReceived(evt);
            }
        }
    }

    private static void FireMessageReceivedEvent(String message) {
        Object[] listeners = MessageReceivedListenerList.getListenerList();

        for (int i = 0; i < listeners.length; i = i + 2) {
            if (listeners[i] == ReceivedMessageListener.class) {
                ((ReceivedMessageListener) listeners[i + 1]).MessageReceived(message);
            }
        }
    }

}
