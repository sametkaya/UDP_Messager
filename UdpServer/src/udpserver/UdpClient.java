/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package udpserver;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import java.util.EventListener;
import java.util.EventObject;
import javax.swing.event.EventListenerList;

/**
 *
 * @author INSECT
 */
public class UdpClient {

    private static DatagramSocket Socket;
    private static InetAddress ServerIp;
    private static int ServerPort;
    private static Thread Listen;

    private static EventListenerList MessageReceivedListenerList = new EventListenerList();

    public static void Create(int Port, int ServerPort, InetAddress ServerIp) throws SocketException {
        UdpClient.Socket = new DatagramSocket(Port);
        UdpClient.ServerIp = ServerIp;
        UdpClient.ServerPort = ServerPort;
        //MessageReceivedListenerList = new EventListenerList();
        UdpClient.Listen = new Thread() {
            public void run() {

               // byte[] SData = new byte[1024];
                // BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println("Server Ready");
                while (true) {
                    try {

                        byte[] RData = new byte[1024];

                        DatagramPacket RPack = new DatagramPacket(RData, RData.length);

                        //blocking var
                        UdpClient.Socket.receive(RPack);
                        String Text = new String(RPack.getData());
                      //  FileOutputStream fos = new FileOutputStream(new File("C:\\Users\\samet\\Desktop\\dosya1.txt"));
                      //  byte[] b1 = new byte[RPack.getLength()];
                      //  fos.write(RData, 0, RData.length);
                       // System.out.println("\nFrom Client "+RPack.getAddress()+" <<< " + Text);
                          UdpClient.FireMessageReceivedEvent(Text);

                        RData = null;
                        RPack = null;

                    } catch (Exception e) {

                        System.out.println("hata ");
                    }
                }

            }

        };

        UdpClient.Listen.start();

    }

//    public static void Start() {
//        UdpServerClass.Listen.start();
//
//    }
//    public static void Stop() {
//        UdpServerClass.Listen.interrupt();
//        UdpServerClass.ConnectedClients.clear();
//        UdpServerClass.Socket.close();
//
//    }
    public static void SentMessage(String Message) throws IOException {
        byte[] SData = new byte[1024];
        SData = Message.getBytes();

        DatagramPacket SPack = new DatagramPacket(SData, SData.length, UdpClient.ServerIp,
                UdpClient.ServerPort);
        UdpClient.Socket.send(SPack);
        SPack = null;
        SData = null;

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
        UdpClient.MessageReceivedListenerList.add(ReceivedMessageListener.class, listener);
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
