import java.io.*;
import java.net.*;
import java.net.InetAddress;
import java.util.*;

public class UDPClient {
    public static int port = 8080;
    public static InetAddress addr;
    public static DatagramPacket establish;
    public static DatagramSocket mainSocket;
    public static Scanner sc  = new Scanner(System.in);
    public UDPClient() {
        try{
            addr = InetAddress.getLocalHost();
            mainSocket = new DatagramSocket();
        }
        catch(Exception e){
            System.out.println("Error in making getting localhost");
        }
    }
    public void establishConnection(){
        String myAddr = "Address: "+ addr + " Port: " + port;
        establish = new DatagramPacket(myAddr.getBytes(),myAddr.length(),addr,port);
        try{mainSocket.send(establish);}
        catch(Exception e){
            System.out.println(e);
        }
    }
    public void getAvailableFileNames() {
        byte[] receiveFileNames = new byte[100000];
        DatagramPacket getFileNames = new DatagramPacket(receiveFileNames, receiveFileNames.length);
        try{mainSocket.receive(getFileNames);}
        catch(Exception e){
            System.out.println(e);
        }
        String fileNames = new String(getFileNames.getData());
        System.out.println(fileNames);
    }
    public void startTransfer() throws IOException {
        System.out.println("Enter the filename you want:");
        String desFile = sc.nextLine();
        mainSocket.send(new DatagramPacket(desFile.getBytes(),desFile.length(),addr,port));

        byte[] buffer = new byte[1536];
        DatagramPacket fileActual = new DatagramPacket(buffer, 1536);
        FileOutputStream fo = new FileOutputStream("received/"+desFile);
        int i = 0;
        String s = Integer.toString(i);
        byte[] bytefileSize = new byte[1024];
        mainSocket.receive(new DatagramPacket(bytefileSize, bytefileSize.length));
        int fileSize = Integer.parseInt(actual(bytefileSize,0));
        System.out.print("**"+fileSize+"**");
        int times = fileSize/1024;
        mainSocket.send(new DatagramPacket(s.getBytes(),s.length(),addr,port));
        mainSocket.receive(fileActual);
        long time = System.currentTimeMillis();
        while(i<times){
            int rem = 1024;
            if(i==times){
                rem = fileSize%1024;
            }
            if(Integer.parseInt(actual(buffer,1024))==i){
                System.out.println("Getting Packet number: "+ i);
                fo.write(buffer,0,rem);
                ++i;
            }
            else if(i>=times)break;
            else{
                System.out.println("****************Packet Lost, asking server to send again****************");
            }
            s = Integer.toString(i);
            mainSocket.send(new DatagramPacket(s.getBytes(),s.length(),addr,port));
            mainSocket.setSoTimeout(1000);
            try{mainSocket.receive(fileActual);}
            catch(Exception e){System.out.println("Loss or delay from Server Side!");}
        }
        sc.close();
        mainSocket.close();
        fo.close();
        // fr.close();
        time = System.currentTimeMillis()-time;
        System.out.println(((long)fileSize)/time);
    }

    static String actual(byte[] fromServer, int start){
        StringBuilder forDes = new StringBuilder();
            for(int i=start;i<fromServer.length;i++){
                int num = fromServer[i];
                if(num!=0){
                    forDes.append((char)fromServer[i]);
                }
            }
            return forDes.toString();
    }
    public static void main(String args[]) {
        System.out.println("Client!");
    }
}
