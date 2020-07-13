import java.io.*;
import java.net.*;
import java.net.InetAddress;
import java.util.*;

public class UDPServer {
    public static DatagramSocket mainSocket;
    public static void main(String[] args) throws Exception {
        int port = 8080;
        DatagramPacket receiving,sending;
        InetAddress addr = InetAddress.getLocalHost();
        byte[] getData ;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter Desired packet loss in %");
        int lossper = Integer.parseInt(sc.nextLine());
        if(lossper>0)lossper = 100/lossper;
        System.out.println("Enter Desired packet delay in ms");
        int delay = Integer.parseInt(sc.nextLine());
        sc.close();
        while(true){
            mainSocket = new DatagramSocket(port);
            System.out.println("\n\nSerever is up!\n");
            getData = new byte[1024];
            receiving = new DatagramPacket(getData, 1024);
            mainSocket.receive(receiving);
            InetAddress srcaddress = receiving.getAddress();
            int srcport = receiving.getPort();
            System.out.println(new String(receiving.getData()));
            // System.out.println("check1");
            String dir = "/home/souryaansh/Pictures/";
            // System.out.println("check1");
            File arrFiles[] = (new File(dir)).listFiles();
            // System.out.println("check1");
            StringBuilder fileList = new StringBuilder("\n");
            for(int i=0;i<arrFiles.length;i++){
                fileList.append(arrFiles[i].getName() + " ,size: " + arrFiles[i].length() + " Bytes\n");
            }
            sending = new DatagramPacket(fileList.toString().getBytes(),0, fileList.toString().length(), srcaddress ,srcport);
            mainSocket.send(sending);
            receiving = new DatagramPacket(new byte[1024], 1024);
            mainSocket.receive(receiving);
            String desFile = actual(new String(receiving.getData()));
            int fileIndex =-1;
            for(int i=0;i<arrFiles.length;i++){
                if(desFile.equalsIgnoreCase(arrFiles[i].getName())){
                    fileIndex = i;
                    break;
                }
            }
            if(fileIndex==-1){
                mainSocket.send(new DatagramPacket("404".getBytes(), 3,addr,3));
            }
            else{
                FileInputStream fr = new FileInputStream(arrFiles[fileIndex].getAbsolutePath());
                int bytesRead;
                byte[] buffer = new byte[1024];
                byte[] toSend = new byte[512];
                int i=0;
                i=0;
                String s = new String(Integer.toString((int)arrFiles[fileIndex].length()));
                mainSocket.send(new DatagramPacket(s.getBytes(),s.length(),srcaddress,srcport));
                mainSocket.receive(new DatagramPacket(toSend,512));
                int times = (int)((arrFiles[fileIndex].length())/1024);
                int count = 0;
                while(true){
                    i=Integer.parseInt(actual(new String(toSend)));
                    if(i>times)break;
                    System.out.println("transferring packet number :"+i);
                    byte[] toSendByte = new byte[1536];
                    int j = 0;
                    buffer = new byte[1024];
                    fr = new FileInputStream(arrFiles[fileIndex].getAbsolutePath());
                    while(j<=i){
                        bytesRead=fr.read(buffer);
                        ++j;
                    }
                    System.arraycopy(buffer, 0, toSendByte, 0, buffer.length);
                    System.arraycopy((Integer.toString(i)).getBytes(), 0, toSendByte, 1024,(Integer.toString(i).length()) );
                    System.out.println(Integer.toString(i));
                    if(lossper<=0 || (count%lossper)!=0){
                        if(i>=times){break;}
                        Thread.sleep(delay);
                        mainSocket.send(new DatagramPacket(toSendByte, toSendByte.length,srcaddress,srcport));
                    }
                    mainSocket.setSoTimeout(110);
                    try{
                    mainSocket.receive(new DatagramPacket(toSend, toSend.length));}
                    catch(SocketTimeoutException e){
                        ++count;
                        if(i>=times){break;}
                        System.out.println("Loss or Delay from Client Side!");
                        continue;
                    }
                    i=Integer.parseInt(actual(new String(toSend)));
                    if(i>times)break;
                    ++count;
                }
                toSend = new byte[1];
                toSend[0]=-1;
                mainSocket.send(new DatagramPacket(toSend, 1,srcaddress,srcport));
            }
            mainSocket.close();
        }

    }
    static String actual(String fromClient){
        StringBuilder forDes = new StringBuilder();
            for(int i=0;i<fromClient.length();i++){
                int num = fromClient.charAt(i);
                if(num!=0){
                    forDes.append(fromClient.charAt(i));
                }
            }
            return forDes.toString();
    }
}
