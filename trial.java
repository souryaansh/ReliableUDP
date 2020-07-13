
public class trial {
    public static void main(String args[]) {
        UDPClient cli = new UDPClient();
        cli.establishConnection();
        cli.getAvailableFileNames();
        try{cli.startTransfer();}
        catch(Exception e){
            System.out.println("There was an : "+e+" during transfer");
        }
    }
}