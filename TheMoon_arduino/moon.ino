#include <Mouse.h>

#include <SoftwareSerial.h>
SoftwareSerial BTSerial(2, 3);

#define Power_SW    4


#define L1_5    5
#define L1_6    6
#define L1_7    7
#define L1_8    8
#define L1_9    9


//------- 모터관련 변수 초기화 ---------
int motorSpeed = 5400 ;    // 최저속도 9600 최고속도 1200
int revCount = 1; //회전 수
int angle = 90; // 움직일 각도
int stepangle = map(angle, 0, 360, 0, 512); //스텝모터의 최대각으로 매핑??
int lookup[8] = {B01000, B01100, B00100, B00110, B00010, B00011, B00001, B01001};
int motorPin1 = 11; // IN1
int motorPin2 = 10; // IN2
int motorPin3 = 9; // IN3
int motorPin4 = 8; // IN4
float timer;
boolean motor_flag = true;



// 데이터 관련 변수 초기화
String send_data;
byte bt_buffer[80][8];
int bufferPosition;
int i = 0;


void setup() {

  Serial.begin(9600);

  // set the data rate for the BT port
  BTSerial.begin(9600);


  pinMode(Power_SW, INPUT_PULLUP);
  pinMode(L1_5, OUTPUT);
  pinMode(L1_6, OUTPUT);
  pinMode(L1_7, OUTPUT);
  pinMode(L1_8, OUTPUT);
  pinMode(L1_9, OUTPUT);

  pinMode(motorPin1, OUTPUT);
  pinMode(motorPin2, OUTPUT);
  pinMode(motorPin3, OUTPUT);
  pinMode(motorPin4, OUTPUT);

  digitalWrite(L1_5, LOW);
  digitalWrite(L1_6, LOW);
  digitalWrite(L1_7, LOW);
  digitalWrite(L1_8, LOW);
  digitalWrite(L1_9, LOW);

}

void loop() {
  //  int y, z; // 모터 제어, 각도 제어 회전수 제어용 변수 선언
  timer = millis();
  /*
    if(BTSerial.available()) { // if BT sends something
      char data = BTSerial.read();
      Serial.print(data); // write it to serial(serial monitor)
      bt_buffer[bufferPosition][i] = data;
      if(i == 7){
        bufferPosition++;
        i=-1;
        Serial.print("\n");
      }
      if(data == 'Q')
      {
        Serial.print("\n");
        bufferPosition =0;
        i=-1;
      }
      i++;
      delay(1);
    }
  */
  //unsigned float motor_timer;
  //timer = millis();

  motor_angle(stepangle);
  //Serial.println(bt_buffer[0][8]+bt_buffer[0][9]);
  /*  for(z=0; z< revCount; z++) // 회전수
    {
      for(y=0; y < stepangle; y++)  // 회전각
      {
        for(i =0; i< 8; i++)
        {
         digitalWrite(motorPin1,bitRead(lookup[i],0));
         digitalWrite(motorPin2,bitRead(lookup[i],1));
         digitalWrite(motorPin3,bitRead(lookup[i],2));
         digitalWrite(motorPin4,bitRead(lookup[i],3));
         delayMicroseconds(motorSpeed);
        }
      }
      delay(500);
    }
    }*/

}
