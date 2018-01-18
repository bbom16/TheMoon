/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package bbfactory.themoon;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;


import android.widget.SeekBar;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import bbfactory.themoon.BluetoothLeService;
import bbfactory.themoon.SampleGattAttributes;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();

   int lunage;
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    // private TextView isSerial;
    //  private TextView mConnectionState;
    //   private TextView mDataField;
    // Switch Tswitch;

    int[] array = new int[3]; //년 월 일 배열
    String moonData = "";
    //int[] moonArray = new int[365]; //월령값 저장 배열
    boolean inLuna = false;
    String Lunage = "default";

    // private SeekBar mRed,mGreen,mBlue;
    private String mDeviceName;
    private String mDeviceAddress;
    TextView Mhour, Mminute;
    Button mButtonSend;
    Button mButtonBook;
    TextView M_YEAR, M_MONTH, M_DAY;
    //  private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private boolean mConnected = false;
    private BluetoothGattCharacteristic characteristicTX;
    private BluetoothGattCharacteristic characteristicRX;
    //    EditText mEditReceive, mEditSend;
    boolean flag = false; // 예약 해제/연결


    public final static UUID HM_RX_TX =
            UUID.fromString(SampleGattAttributes.HM_RX_TX);

    private final String LIST_NAME = "NAME";
    private final String LIST_UUID = "UUID";

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            Log.d(TAG, "BLUEBLUE" + "eeee0");
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
                Log.d(TAG, "BLUEBLUE" + "eeee1");
                //    makeChange("@A");
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                updateConnectionState(R.string.disconnected);
                Log.d(TAG, "BLUEBLUE" + "eeee2");
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                Log.d(TAG, "BLUEBLUE" + "eeee3");
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                Log.d(TAG, "BLUEBLUE" + "eeee4");
            }
        }
    };

    private void clearUI() {
        //mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.gatt_services_characteristics);
        StrictMode.enableDefaults();
        final Calendar calendar = Calendar.getInstance();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);

        load_moon_phase();

    //    Intent intent1 = new Intent(DeviceControlActivity.this, MainActivity.class);
      //  intent1.putExtra("moonData", Lunage);
     //   startActivity(intent1);

        //로그해보기.
        //    getActionBar().setTitle(mDeviceName);
        //  getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }


    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //  mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {

        if (data != null) {
            //  mDataField.setText(data);
        }
    }


    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();


        // Loops through available GATT Services.
        for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));

            // If the service exists for HM 10 Serial, say so.

            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            // get characteristic when UUID matches RX/TX UUID
            characteristicTX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
            characteristicRX = gattService.getCharacteristic(BluetoothLeService.UUID_HM_RX_TX);
        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }


    // on change of bars write char
    private void makeChange(String s) {
       final String str = s;
        Log.d(TAG, "Sending result=" + str);
        final byte[] tx = str.getBytes();
        int size = tx.length;
        Log.d("size----",String.valueOf(size));
        String byteToString = new String(tx,0,tx.length);
        Log.d("txtxtx---",byteToString);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //while(!mConnected);
                if (mConnected) {
                    Log.d(TAG, "Sending result=====" + str);
                    characteristicTX.setValue(tx);
                    String byteToString = new String(characteristicTX.getValue(),0,characteristicTX.getValue().length);
                    Log.d("ddd22",byteToString);
                    mBluetoothLeService.writeCharacteristic(characteristicTX);
                    //Log.d("chk---", String.valueOf(chk));
                    mBluetoothLeService.setCharacteristicNotification(characteristicRX, true);
                }
            }
        }, 10000);// 1.5초 정도 딜레이를 준 후 시작
    }

    @Override
    protected void onStop() { //ssid와 password 유지하기 위해서
        super.onStop();
        // Activity 가 종료되기 전에 저장한다
        // SharedPreferences 에 설정값(특별히 기억해야할 사용자 값)을 저장하기

    }

    void load_moon_phase() {
        String gets;
        String Key;
        Key = "mBA8FFW3r8YbMwHT3zj6cXcUNIUS%2Ba525Z6VQZSBAeiuahv3gSF8DyqZYxjPP%2FhST9cwVOKU4Oioe3Xjpms%2Fiw%3D%3D";
        for (int i = 0; i < 80; i++) {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, i); //날짜 하루 더하기
                array[0] = calendar.get(Calendar.YEAR);
                array[1] = calendar.get(Calendar.MONTH) + 1;
                array[2] = calendar.get(Calendar.DATE);
                Log.d("년 월 일", String.valueOf(array[0]) + '-' + String.valueOf(array[1]) + '-' + String.valueOf(array[2]));

                gets = "solYear=" + String.valueOf(array[0]) + "&solMonth=" + String.format("%02d", array[1]) + "&solDay=" + String.format("%02d", array[2]);
                Log.d("check1", "확인1111" + i);
                URL url = new URL("http://apis.data.go.kr/B090041/openapi/service/LunPhInfoService/getLunPhInfo?" + gets + "&ServiceKey=" + Key);
                Log.d("check2", "확인22222");
                XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
                Log.d("check3", "확인3333");
                XmlPullParser parser = parserCreator.newPullParser();
                Log.d("check4", "확인44444");
                parser.setInput(url.openStream(), null);
                Log.d("check5", "확인55555");

                int parserEvent = parser.getEventType();
                while (parserEvent != XmlPullParser.END_DOCUMENT) {
                    switch (parserEvent) {
                        case XmlPullParser.START_TAG:
                            if (parser.getName().equals("lunAge")) inLuna = true;
                            break;
                        case XmlPullParser.TEXT:
                            if (inLuna) {
                                Lunage = parser.getText();
                                inLuna = false;
                                Log.d("Lunage값", Lunage);
                                lunage = (int)Float.parseFloat(Lunage);
                                if(lunage >=0 && lunage < 3 || lunage >= 28) lunage = 14; //달 작을 때는 보름
                                else if(lunage >=3 && lunage < 6) lunage = 4;
                                else if(lunage >=6 && lunage < 8) lunage = 7;
                                else if(lunage == 8) lunage = 8;
                                else if(lunage >=9 && lunage < 11) lunage = 10;
                                else if(lunage >=11 && lunage < 14) lunage = 12;
                                else if(lunage >= 14 && lunage <16) lunage = 14;
                                else if(lunage >=16 && lunage < 19) lunage = 17;
                                else if(lunage >=19 && lunage < 22) lunage = 20;
                                else if(lunage == 22) lunage = 22;
                                else if(lunage >=23 && lunage < 26) lunage = 24;
                                else if(lunage >=26 && lunage < 28) lunage = 27;
                            }
                            break;
                        case XmlPullParser.END_TAG:
                            if (parser.getName().equals("item")) {
                                moonData = String.valueOf(array[0]%100) + String.valueOf(String.format("%02d", array[1])) + String.valueOf(String.format("%02d", array[2])) + String.valueOf(lunage);
                                // data.setText(Lunage);

                                makeChange(moonData);
                            }

                            break;
                    }
                    parserEvent = parser.next();
                    Log.d("parserEvent값", String.valueOf(parserEvent));
                }
                Log.d("while문 끝", "while끝!!!!");
            } catch (Exception e) {
                //data.setText("에러가..났습니다...");
                Log.d("parsing121222121", e.getMessage());
            }
        }
       // makeChange(moonData);
    }
}