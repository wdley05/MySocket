package com.example.socket;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    EditText input1;
    TextView output1;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        input1 = findViewById(R.id.input1);
        output1 = findViewById(R.id.output1);

        Button sendButton = findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                final String data = input1.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        send(data);
                    }
                }).start();
            }
        });

        Button startServerButton = findViewById(R.id.startServerButton);
        startServerButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        startServer();
                    }
                }).start();
            }
        });
    }

    public void startServer(){
        int port = 5001;
        try {
            ServerSocket server = new ServerSocket(port);
            while (true){
                Socket sock = server.accept();
                InetAddress clientHost = sock.getLocalAddress();
                int clientPort = sock.getPort();
                printIn("클라이언트 연결됨 : "+ clientHost+", "+clientPort);

                ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
                String input = (String) instream.readObject();
                printIn("데이터 받음 : "+ input);

                ObjectOutputStream outstream = new ObjectOutputStream(sock.getOutputStream());
                outstream.writeObject(input + " from server. ");
                outstream.flush();
                printIn("데이터 보냄");

                sock.close();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void printIn(final String data){
        handler.post(new Runnable() {
            @Override
            public void run() {
                output1.append(data+"\n");
            }
        });
    }

    public void send(String data){
        //6만개가 넘는 포트 중에 골라서 지정.
        int port = 5001;
        try {
            Socket sock = new Socket("localhost", port);
            ObjectOutputStream outStream = new ObjectOutputStream(sock.getOutputStream());
            outStream.writeObject(data);
            outStream.flush();

            ObjectInputStream instream = new ObjectInputStream(sock.getInputStream());
            String input = (String) instream.readObject();
            sock.close();

        }catch (Exception e){
            e.printStackTrace();
        }

    }
}