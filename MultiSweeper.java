import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.Serializable;
import java.util.Vector;
import java.util.concurrent.SynchronousQueue;

import javax.swing.*;



public class MultiSweeper {
    GameClientSocket clientSocket;
    GameServerSocket serverSocket;
    boolean creatingServer;
    int serverPort;
    String clientName;
    String serverName;


    public MultiSweeper() {
        
        clientName = "Hitesh";
        creatingServer = true;   
        serverPort = 10000;   
        if(creatingServer) {
            int row = 9;
            int columns = 9;
            int mines = 10;
            serverName = "Blah";
            try {
                serverSocket = new GameServerSocket(serverPort, serverName, row, columns, mines);
                do {
                    serverSocket.accept(50000, true);
                } while (true);
            } catch (IOException E){
                E.printStackTrace();
            }
        } else {
            clientSocket = new GameClientSocket(clientName);
            clientSocket.connect(serverName, serverPort);
        }
    }


    public class GameServerSocket extends EasyMultiThreadedServer {

        String[][] sourceBoardData;
        SynchronousQueue<GameCommandPacket> packetBuffer;
        int row, columns, mines;

        public GameServerSocket(int port, String name, int row, int columns, int mines) throws IOException {
            super(port, name);
            this.row = row;
            this.columns = columns;
            this.mines = mines;
            genData();
        }

        public void genData(){
            sourceBoardData = new String[row][columns];

            for(int i = 0; i < sourceBoardData.length; i++){
                for(int k = 0; k < sourceBoardData[i].length; k++){
                    sourceBoardData[i][k] = "close,";
                }
            }

            int count = mines;

            while(count > 0)
            {
                int row = (int)(Math.random()*this.row);
                int col = (int)(Math.random()*this.columns);


                if (sourceBoardData[row][col] == null)
                {
                    sourceBoardData[row][col] = "close,B";
                    count--;
                }

            }
        }
        

        @Override
        public void workerThreadImplementation(ServerHandler handler) {
            Incoming in = new Incoming(this, handler);
            Outgoing out = new Outgoing(this, handler);
            in.start();
            out.start();            
        }

        class GameThread extends Thread {

            public void run() {
                while(true){
                    if(!packetBuffer.isEmpty()){
                        try {
                            GameCommandPacket packet = packetBuffer.take();
                            if(sourceBoardData[packet.x][packet.y].contains("close")){
                                if(packet.action == GameCommandPacket.Action.OPEN){
                                    sourceBoardData[packet.x][packet.y] = "open" + "," + sourceBoardData[packet.x][packet.y].split(",")[1];
                                } else if (packet.action == GameCommandPacket.Action.FLAG) {
                                    sourceBoardData[packet.x][packet.y] = sourceBoardData[packet.x][packet.y].split(",")[0] + "F";
                                }
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        //flag, open, close
        //state, item
        class Outgoing extends Thread {
            String[][] sourceBoardData;
            GameServerSocket thisSocket;
            ServerHandler handler;

            public Outgoing(GameServerSocket thisSocket, ServerHandler handler){
                this.thisSocket = thisSocket;
                this.handler = handler;
                sourceBoardData = new String[thisSocket.sourceBoardData.length][thisSocket.sourceBoardData[0].length];
            }

            public void run() {
                while(true){
                    if(!isEqual()){
                        sendData();
                    }
                }
            }

            public void sendData(){
                sourceBoardData = thisSocket.sourceBoardData;
                handler.send(sourceBoardData);
            }

            private boolean isEqual(){
                for(int i = 0; i < sourceBoardData.length; i++)
                    for(int k = 0; k < sourceBoardData[i].length; k++)
                        if(!sourceBoardData[i][k].equals(thisSocket.sourceBoardData[i][k]))
                            return false;
                return true;
            }
        }
    
        class Incoming extends Thread {
            GameServerSocket thisSocket;
            ServerHandler handler;
            
            public Incoming(GameServerSocket socket, ServerHandler handler){
                this.thisSocket = socket;
                this.handler = handler;
            }

            public void run() {
                while(true){
                    try {
                        thisSocket.packetBuffer.put((GameCommandPacket) handler.receive());
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        }
        
    }

    public class GameClientSocket extends EasyClientSocket implements ActionListener, MouseListener {
        JToggleButton[][] board;
        JPanel mainBoard;
        JFrame game;

        public GameClientSocket(String clientName){
            super(clientName);
            game = new JFrame();
        }

        class Incoming extends Thread {
            String[][] sourceBoardData;
            EasyClientSocket clientSocket;
            
            @Override
            public void run() {
                while(true){
                    sourceBoardData = (String[][]) clientSocket.receive();
                    refreshBoard(sourceBoardData);
                }
            }
    
        }

        public void refreshBoard(String[][] boardData){
            if(board == null) {
                board = new JToggleButton[boardData.length][boardData[0].length];
                mainBoard = new JPanel();
                mainBoard.setLayout(new GridLayout(boardData.length, boardData[0].length));
                game.setVisible(true);
            }
      
        }

        public void setIcons(String[][] boardData){
            for(int i = 0; i < boardData.length; i++){
                for(int k = 0; k < boardData[i].length; k++){
                    switch (boardData[i][k].split(",")[1]) {
                        case "B":
                        if(boardData[i][k].split(",")[0].equals("close"))
                            board[i][k].setIcon(new ImageIcon("Minesweeper Images\\mine.png"));
                        else {
                            
                        }
                        break;
                        case "F":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\flag.png"));
                        break;
                        case "1":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\1.png"));
                        break;
                        case "2":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\2.png"));
                        break;
                        case "3":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\3.png"));
                        break;
                        case "4":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\4.png"));
                        break;
                        case "5":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\5.png"));
                        break;
                        case "6":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\6.png"));
                        break;
                        case "7":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\7.png"));
                        break;
                        case "8":
                        board[i][k].setIcon(new ImageIcon("Minesweeper Images\\8.png"));
                        break;

                    }
                }
            }
        }

        public void refeshBoardStates(String[][] boardData){
            setIcons(boardData);
            for(int i = 0; i < boardData.length; i++){
                for(int k = 0; k < boardData[i].length; k++){
                    if(boardData[i][k].split(",")[0].equalsIgnoreCase("close")){
                        if(board[i][k].isEnabled()){
                            board[i][k].getModel().setPressed(false);
                        }
                    } else {
                        board[i][k].getModel().setPressed(true);
                    }
                }
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            
        }
    }

    public static class GameCommandPacket implements Serializable {

        public enum Action {
            FLAG,
            OPEN
        }

        Action action;
        int x;
        int y;

        public GameCommandPacket(int x, int y, Action actionTaken){
            this.action = actionTaken;
            this.x = x;
            this.y = y;
        }
    }




    

}