package com.flowithus.touchflow;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.view.KeyEvent;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 10; // 블루투스 활성화 상태
    private BluetoothAdapter bluetoothAdapter; // 블루투스 어댑터
    private Set<BluetoothDevice> devices; // 블루투스 디바이스 데이터 셋
    private BluetoothDevice bluetoothDevice; // 블루투스 디바이스
    private BluetoothClass bluetoothClass; // 블루투스 기기 종류 파악
    private BluetoothSocket bluetoothSocket = null; // 블루투스 소켓
    private OutputStream outputStream = null; // 블루투스에 데이터를 출력하기 위한 출력 스트림
    private InputStream inputStream = null; // 블루투스에 데이터를 입력하기 위한 입력 스트림
    private byte[] readBuffer; // 수신된 문자열을 저장하기 위한 버퍼
    private int readBufferPosition; // 버퍼 내 문자 저장 위치

    // ImageButton

    // 1st button. xml group1
    private ImageButton bt1st_01;
    private ImageButton bt1st_02;
    private ImageButton bt1st_03;
    private ImageButton bt1st_04;
    private ImageButton bt1st_05;
    private ImageButton bt1st_06;
    private ImageButton bt1st_07;
    private ImageButton bt1st_08;

    // 1st button. xml group0 -> group2
    private ImageButton bt2nd_01;
    private ImageButton bt2nd_02;
    private ImageButton bt2nd_03;
    private ImageButton bt2nd_04;
    private ImageButton bt2nd_05;
    private ImageButton bt2nd_06;
    private ImageButton bt2nd_07;
    private ImageButton bt2nd_08;
    private ImageButton bt2nd_09;
    private ImageButton bt2nd_10;
    private ImageButton bt2nd_11;
    private ImageButton bt2nd_12;

    // 1st button. xml group0 -> group3
    private ImageButton bt3rd_01;
    private ImageButton bt3rd_02;
    private ImageButton bt3rd_03;
    private ImageButton bt3rd_04;
    private ImageButton bt3rd_05;
    private ImageButton bt3rd_06;
    private ImageButton bt3rd_07;
    private ImageButton bt3rd_08;
    private ImageButton bt3rd_09;
    private ImageButton bt3rd_10;
    private ImageButton bt3rd_11;
    private ImageButton bt3rd_12;

    // 뒤로가기 두번 종료 전용
    private long backKeyPressedTime = 0;
    private Toast backKeytoast;

    // 디버깅용
    private String CustomMessage = "Custom";
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        // 각 컨테이너들의 id를 메인 xml과 맞춘다.

        bt1st_01 = findViewById(R.id.bt1st_01);
        bt1st_02 = findViewById(R.id.bt1st_02);
        bt1st_03 = findViewById(R.id.bt1st_03);
        bt1st_04 = findViewById(R.id.bt1st_04);
        bt1st_05 = findViewById(R.id.bt1st_05);
        bt1st_06 = findViewById(R.id.bt1st_06);
        bt1st_07 = findViewById(R.id.bt1st_07);
        bt1st_08 = findViewById(R.id.bt1st_08);

        bt2nd_01 = findViewById(R.id.bt2nd_01);
        bt2nd_02 = findViewById(R.id.bt2nd_02);
        bt2nd_03 = findViewById(R.id.bt2nd_03);
        bt2nd_04 = findViewById(R.id.bt2nd_04);
        bt2nd_05 = findViewById(R.id.bt2nd_05);
        bt2nd_06 = findViewById(R.id.bt2nd_06);
        bt2nd_07 = findViewById(R.id.bt2nd_07);
        bt2nd_08 = findViewById(R.id.bt2nd_08);
        bt2nd_09 = findViewById(R.id.bt2nd_09);
        bt2nd_10 = findViewById(R.id.bt2nd_10);
        bt2nd_11 = findViewById(R.id.bt2nd_11);
        bt2nd_12 = findViewById(R.id.bt2nd_12);

        bt3rd_01 = findViewById(R.id.bt3rd_01);
        bt3rd_02 = findViewById(R.id.bt3rd_02);
        bt3rd_03 = findViewById(R.id.bt3rd_03);
        bt3rd_04 = findViewById(R.id.bt3rd_04);
        bt3rd_05 = findViewById(R.id.bt3rd_05);
        bt3rd_06 = findViewById(R.id.bt3rd_06);
        bt3rd_07 = findViewById(R.id.bt3rd_07);
        bt3rd_08 = findViewById(R.id.bt3rd_08);
        bt3rd_09 = findViewById(R.id.bt3rd_09);
        bt3rd_10 = findViewById(R.id.bt3rd_10);
        bt3rd_11 = findViewById(R.id.bt3rd_11);
        bt3rd_12 = findViewById(R.id.bt3rd_12);

        int flag = 0;
        // 이메일 주소 입력받기
        if (flag == 0) {
            AlertDialog.Builder inputemail = new AlertDialog.Builder(this);
            inputemail.setTitle("Input Email");

            final EditText textboxemail = new EditText(MainActivity.this);
            inputemail.setView(textboxemail);
            inputemail.setPositiveButton("제출", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String emailaddr = textboxemail.getText().toString();
                    emailSocket(emailaddr);
                }
            });
            inputemail.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            AlertDialog alertDialogemail = inputemail.create();
            alertDialogemail.show();
            flag++;
        }

        if (flag == 1) {
            // 블루투스 활성화하기
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); // 블루투스 어댑터를 디폴트 어댑터로 설정

            if (bluetoothAdapter == null) { // 디바이스가 블루투스를 지원하지 않을 때
                //처리 코드
            } else { // 디바이스가 블루투스를 지원할 시
                if (bluetoothAdapter.isEnabled()) { // 기기에 블루투스가 켜져 있을시
                    selectBluetoothDevice();
                } else { // 기기에 블루투스가 꺼져 있을시
                    // 블루투스를 활성화시키기 위한 다이얼로그 출력
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    // 선택한 값이 onActivityResult 함수에서 콜백됨
                    startActivityForResult(intent, REQUEST_ENABLE_BT);
                }
            }
        }

        /*
        // WiFi or LTE
        Context context = getApplicationContext();
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) {
            // 네트워크 연결 코드
            connectivityManager.getActiveNetwork();
            Log.d("","Hello");
        }
        */

        // for bt1sts select effect
        boolean isoneselected = false;

        bt1st_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1st_02.setSelected(false);
                bt1st_03.setSelected(false);
                bt1st_04.setSelected(false);
                bt1st_05.setSelected(false);
                bt1st_06.setSelected(false);
                bt1st_07.setSelected(false);
                bt1st_08.setSelected(false);

                if (!bt1st_01.isSelected()) {
                    bt1st_01.setSelected(true);
                    sendKeyEvent1(KeyEvent.KEYCODE_V);
                }
                else
                    bt1st_01.setSelected(false);
            }
        });

        bt1st_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1st_01.setSelected(false);
                bt1st_03.setSelected(false);
                bt1st_04.setSelected(false);
                bt1st_05.setSelected(false);
                bt1st_06.setSelected(false);
                bt1st_07.setSelected(false);
                bt1st_08.setSelected(false);

                if (!bt1st_02.isSelected()) {
                    bt1st_02.setSelected(true);
                    sendKeyEvent1(KeyEvent.KEYCODE_A);
                }
                else
                    bt1st_02.setSelected(false);
            }
        });

        bt1st_03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1st_01.setSelected(false);
                bt1st_02.setSelected(false);
                bt1st_04.setSelected(false);
                bt1st_05.setSelected(false);
                bt1st_06.setSelected(false);
                bt1st_07.setSelected(false);
                bt1st_08.setSelected(false);

                if (!bt1st_03.isSelected()) {
                    bt1st_03.setSelected(true);
                    sendKeyEvent1(KeyEvent.KEYCODE_B);
                }
                else
                    bt1st_03.setSelected(false);
            }
        });

        bt1st_04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1st_01.setSelected(false);
                bt1st_02.setSelected(false);
                bt1st_03.setSelected(false);
                bt1st_05.setSelected(false);
                bt1st_06.setSelected(false);
                bt1st_07.setSelected(false);
                bt1st_08.setSelected(false);

                if (!bt1st_04.isSelected()) {
                    bt1st_04.setSelected(true);
                    sendKeyEvent1(KeyEvent.KEYCODE_C);
                }
                else
                    bt1st_04.setSelected(false);
            }
        });

        bt1st_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1st_01.setSelected(false);
                bt1st_02.setSelected(false);
                bt1st_03.setSelected(false);
                bt1st_04.setSelected(false);
                bt1st_06.setSelected(false);
                bt1st_07.setSelected(false);
                bt1st_08.setSelected(false);

                if (!bt1st_05.isSelected()) {
                    bt1st_05.setSelected(true);
                    sendKeyEvent1(KeyEvent.KEYCODE_Y);
                }
                else
                    bt1st_05.setSelected(false);
            }
        });

        bt1st_06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1st_01.setSelected(false);
                bt1st_02.setSelected(false);
                bt1st_03.setSelected(false);
                bt1st_04.setSelected(false);
                bt1st_05.setSelected(false);
                bt1st_07.setSelected(false);
                bt1st_08.setSelected(false);

                if (!bt1st_06.isSelected()) {
                    bt1st_06.setSelected(true);
                    sendKeyEvent1(KeyEvent.KEYCODE_P);
                }
                else
                    bt1st_06.setSelected(false);
            }
        });

        bt1st_07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1st_01.setSelected(false);
                bt1st_02.setSelected(false);
                bt1st_03.setSelected(false);
                bt1st_04.setSelected(false);
                bt1st_05.setSelected(false);
                bt1st_06.setSelected(false);
                bt1st_08.setSelected(false);

                if (!bt1st_07.isSelected()) {
                    bt1st_07.setSelected(true);
                    sendKeyEvent1(KeyEvent.KEYCODE_H);
                }
                else
                    bt1st_07.setSelected(false);
            }
        });

        bt1st_08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bt1st_01.setSelected(false);
                bt1st_02.setSelected(false);
                bt1st_03.setSelected(false);
                bt1st_04.setSelected(false);
                bt1st_05.setSelected(false);
                bt1st_06.setSelected(false);
                bt1st_07.setSelected(false);

                if (!bt1st_08.isSelected()) {
                    bt1st_08.setSelected(true);
                    sendKeyEvent1(KeyEvent.KEYCODE_T);
                }
                else
                    bt1st_08.setSelected(false);
            }
        });

        bt2nd_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_S);
            }
        });

        bt2nd_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent4(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_M);
            }
        });

        bt2nd_03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_BACKSLASH);
            }
        });

        bt2nd_04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_G);
            }
        });

        bt2nd_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent3(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_G);
            }
        });

        bt2nd_06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_BACKSLASH);
            }
        });

        bt2nd_07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_DPAD_UP);
            }
        });

        bt2nd_08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_DPAD_DOWN);
            }
        });

        bt2nd_09.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_R);
            }
        });

        bt2nd_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_COMMA);
            }
        });

        bt2nd_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_PERIOD);
            }
        });

        bt2nd_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_T);
            }
        });

        bt3rd_01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent3(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_M);
            }
        });

        bt3rd_02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_M);
            }
        });

        bt3rd_03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_M);
            }
        });

        bt3rd_04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_J);
            }
        });

        bt3rd_05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_SPACE);
            }
        });

        bt3rd_06.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_L);
            }
        });

        bt3rd_07.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_Z);
            }
        });

        bt3rd_08.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_ALT_LEFT, KeyEvent.KEYCODE_BACK);
            }
        });

        bt3rd_09.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent3(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_SHIFT_LEFT, KeyEvent.KEYCODE_Z);
            }
        });

        bt3rd_10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_Q);
            }
        });

        bt3rd_11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent2(KeyEvent.KEYCODE_CTRL_LEFT, KeyEvent.KEYCODE_K);
            }
        });

        bt3rd_12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendKeyEvent1(KeyEvent.KEYCODE_W);
            }
        });
    }

    void sendKeyEvent1(int evt) {
        try {
            // 데이터 송신
            String strevt = Integer.toString(evt) + "\n";
            outputStream.write(evt);
            outputStream.write(0);
            Log.d(CustomMessage, strevt);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(CustomMessage, e.toString());
        }
    }

    void sendKeyEvent2(int evt1, int evt2) {
        try {
            // 데이터 송신
            String strevt = Integer.toString(evt1) + " " + Integer.toString(evt2) + "\n";
            outputStream.write(evt1);
            outputStream.write(evt2);
            outputStream.write(0);
            Log.d(CustomMessage, strevt);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(CustomMessage, e.toString());
        }
    }

    void sendKeyEvent3(int evt1, int evt2, int evt3) {
        try {
            // 데이터 송신
            String strevt = Integer.toString(evt1) + " " + Integer.toString(evt2) + " " + Integer.toString(evt3) + "\n";
            outputStream.write(evt1);
            outputStream.write(evt2);
            outputStream.write(evt3);
            outputStream.write(0);
            Log.d(CustomMessage, strevt);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(CustomMessage, e.toString());
        }
    }

    void sendKeyEvent4(int evt1, int evt2, int evt3, int evt4) {
        try {
            // 데이터 송신
            String strevt = Integer.toString(evt1) + " " + Integer.toString(evt2) + " " + Integer.toString(evt3) + "\n";
            outputStream.write(evt1);
            outputStream.write(evt2);
            outputStream.write(evt3);
            outputStream.write(evt4);
            outputStream.write(0);
            Log.d(CustomMessage, strevt);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(CustomMessage, e.toString());
        }
    }

    // 권한 요청 다이얼로그에서 '사용'을 누르면 onActivityResult() 함수가 호출됨
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_ENABLE_BT) {
            selectBluetoothDevice();
        }
        else {
            finish();
        }
        /*
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                Log.d(CustomMessage, String.valueOf(requestCode)); // requestCode == 10(REQUEST_ENABLE_BT)
                if (requestCode == RESULT_OK) { // 블루투스 권한 요청 창에서 '사용'을 눌렀을 시
                    selectBluetoothDevice();
                }
                else {
                    Log.d(CustomMessage, String.valueOf(requestCode));
                    // '취소'를 눌렀을 시
                }
                break;
        }
        */
    }

    public void selectBluetoothDevice() {
        // 이미 페어링 되어있는 블루투스 기기를 찾는다.
        devices = bluetoothAdapter.getBondedDevices();

        // 페어링 된 디바이스의 크기를 저장
        int pairedDeviceCount = devices.size();
        // 페어링 되어있는 장치가 없을 경우
        if (pairedDeviceCount == 0) {
            // 페어링을 하기위한 함수 호출
        }
        // 페어링 되어있는 장치가 있을 경우
        else {
            // 디바이스를 선택하기 위한 다이얼로그 생성
            AlertDialog.Builder pairedPCbuilder = new AlertDialog.Builder(this);
            pairedPCbuilder.setTitle("페어링 되어있는 PC");
            // 페어링 된 각각의 디바이스의 이름과 주소를 저장
            List<String> list = new ArrayList<>();
            // 모든 디바이스의 이름을 리스트에 추가
            for (BluetoothDevice bluetoothDevice:devices) {
                Log.d(CustomMessage, "Paired all devices: " + String.valueOf(bluetoothDevice.getBluetoothClass()));
                bluetoothClass = bluetoothDevice.getBluetoothClass();
                if (bluetoothClass.getMajorDeviceClass() == BluetoothClass.Device.Major.COMPUTER) {
                    list.add(bluetoothDevice.getName());
                    Log.d(CustomMessage, "Paired PC devices: " + String.valueOf(bluetoothDevice.getBluetoothClass()));
                }
            }

            // List를 CharSequence 배열로 변경
            final CharSequence[] charSequences = list.toArray(new CharSequence[list.size()]);
            list.toArray(new CharSequence[list.size()]);

            // 해당 아이템을 눌렀을 때 호출되는 이벤트 리스너
            pairedPCbuilder.setItems(charSequences, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    // 해당 디바이스와 연결하는 함수 호출
                    connectDevice(charSequences[which].toString());
                }
            });

            pairedPCbuilder.setNeutralButton("기기 추가", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                    startActivityForResult(intent, 0);
                }
            });

            pairedPCbuilder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });

            // 뒤로가기 버튼 누를 때 창이 닫히지 않도록 설정
            pairedPCbuilder.setCancelable(false);
            // 다이얼로그 생성
            AlertDialog alertDialog = pairedPCbuilder.create();
            alertDialog.show();
        }
    }

    public void connectDevice(String deviceName) {
        // 페어링 된 디바이스들을 모두 탐색
        for (BluetoothDevice tempDevice:devices) {
            // 사용자가 선택한 이름과 같은 디바이스로 설정하고 반복문 종료
            if (deviceName.equals(tempDevice.getName())) {
                bluetoothDevice = tempDevice;
                break;
            }
        }
        // UUID 생성
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // rfcomm 통신
        // Rfcomm 채널을 통해 블루투스 디바이스와 통신하는 소켓 생성
        try {
            bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
            bluetoothSocket.connect();
            // 데이터 송,수신 스트림을 얻어옵니다.
            outputStream = bluetoothSocket.getOutputStream();
            //inputStream = bluetoothSocket.getInputStream();
            // 데이터 수신 함수 호출
            //receiveData();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(CustomMessage, "Error in connectDevice: " + e.toString());
        }
    }

    public void receiveData() {
        final Handler handler = new Handler();
        // 데이터를 수신하기 위한 버퍼를 생성

        // 데이터를 수신하기 위한 쓰레드 생성
       Thread workerThread = new Thread(new Runnable() { // 문자열 수신에 사용되는 스레드
            @Override
            public void run() {
                while (Thread.currentThread().isInterrupted()) {
                    try {
                        // 데이터를 수신했는지 확인
                        int byteAvailable = inputStream.available();
                        // 데이터가 수신이 된 경우
                        if (byteAvailable > 0) {
                            // 입력 스트림에서 바이트 단위로 읽어옴
                            byte[] bytes = new byte[byteAvailable];
                            inputStream.read(bytes);
                            // 입력 스트림 바이트를 한 바이트씩 읽어온다.
                            for (int i = 0; i < byteAvailable; i++) {
                                byte tempByte = bytes[i];
                                // 개행문자를 기준으로 받음 (한줄씩)
                                if (tempByte == '\n') {
                                    // readBuffer 배열을 encodedBytes로 복사
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    // 인코딩 된 바이트 배열을 문자열로 변환
                                    final String text = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            // 텍스트 뷰에 출력
                                            //textViewReceive.append(text + '\n');
                                        }
                                    });
                                }
                                // 개행 문자가 아닐 경우
                                else {
                                    readBuffer[readBufferPosition++] = tempByte;
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        // 1초마다 받아옴
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
}
                }
                        }
                        });
                        workerThread.start();
                        }

    @Override
    public void onBackPressed(){
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            showGuide();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            MainActivity.this.finish();
            backKeytoast.cancel();
        }
    }

    public void showGuide() {
        backKeytoast = Toast.makeText(MainActivity.this, "\'뒤로\' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT);
        backKeytoast.show();
    }

    // 소켓 통신으로 서버에게 이메일 주소를 넘겨주는 함수
    public void emailSocket(final String emailaddr) {
        try {
            new Thread() {
                public void run() {
                    try {
                        Socket socket = new Socket();
                        SocketAddress socketAddress = new InetSocketAddress("ec2-15-164-217-88.ap-northeast-2.compute.amazonaws.com", 53535);
                        socket.connect(socketAddress);
                        OutputStream os = socket.getOutputStream();
                        //String testemail = "jinn0525@gmail.com";
                        os.write(emailaddr.getBytes(), 0, emailaddr.length());
                        os.close();
                        socket.close();
                    } catch (Exception e) {
                        Log.d("DEBUG", e.toString());
                    }
                }
            }.start();

        } catch (Exception e) {
            Log.d("DEBUG", e.toString());
        }
    }
}
