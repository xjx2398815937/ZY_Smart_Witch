package com.example.zy_smart_switch;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintStream;

public class MainActivity extends AppCompatActivity {

    private TextView lianjie;
    private Button kai;
    private Button guan;

    private Context context;
    private Socket socket;
    private PrintStream output;

    private Boolean mIsExit;

    //进行先关按钮的初始化，通过findViewById找到对应的控件
    public  void  Init(){
        lianjie = findViewById(R.id.id2);
        kai = findViewById(R.id.id3);
        guan = findViewById(R.id.id4);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        //调用函数进行初始化
        Init();
        Toast.makeText(MainActivity.this, "Waiting for Smart Switch connected...", Toast.LENGTH_LONG).show();
        //开启一个连接线程等待智能开关的连接
        new Thread(new ConnectThread()).start();

        //开按键监听
        kai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new SendThread(1)).start();
            }
        });
        //关按键监听
        guan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new SendThread(0)).start();
            }
        });



    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mIsExit) {
                this.finish();

            } else {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mIsExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsExit = false;
                    }
                }, 2000);
            }
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    //连接线程
    public class ConnectThread implements Runnable{
        @Override
        public void run() {
            Message msg = Message.obtain();
            try {
                //开始监听1314端口
                ServerSocket serverSocket = new ServerSocket(1314);
                //如果有客户端连接
                socket = serverSocket.accept();
                //获取输入输出流
                output = new PrintStream(socket.getOutputStream(), true, "utf-8");
                //消息提示
                msg.what = 0x123;
                handler.sendMessage(msg);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }
    }

    //发送数据线程
    public class SendThread implements Runnable{
        int msg;
        public SendThread(int msg)
        {
            super();
            this.msg = msg;
        }
        @Override
        public void run() {
            try {
                //发送数据
                output.print(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //消息处理方法
    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (0x123 == msg.what) {
               lianjie.setText("已连接");
               kai.setEnabled(true);
               guan.setEnabled(true);
            }
        }
    };

}
