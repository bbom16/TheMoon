int init_angle = 0;
int init_count = 0;
int move_angle = 0;

boolean move_flag = true;
//스위치 관련 변수 초기화
boolean power_on = false;

void motor_assign(int index) { //motor에 값 대입
  if (index == 0) {
    for (int i = 0; i < 8; i++) {
      digitalWrite(motorPin1, bitRead(lookup[i], 0));
      digitalWrite(motorPin2, bitRead(lookup[i], 1));
      digitalWrite(motorPin3, bitRead(lookup[i], 2));
      digitalWrite(motorPin4, bitRead(lookup[i], 3));
      delayMicroseconds(motorSpeed);
    }
    init_angle++;
  }
  else
  {
    Serial.println(move_angle);
    for(int i = 7; i>= 0; i--)
    {
      digitalWrite(motorPin1, bitRead(lookup[i], 0));
      digitalWrite(motorPin2, bitRead(lookup[i], 1));
      digitalWrite(motorPin3, bitRead(lookup[i], 2));
      digitalWrite(motorPin4, bitRead(lookup[i], 3));
      delayMicroseconds(motorSpeed);
    }
    move_angle++;
  }
}

void motor_set() { //초기화 모터
  if (init_angle < stepangle && init_count < revCount) {
    motor_assign(0);
  }
  if (init_angle == stepangle) {
    init_angle = 0;
    init_count++;
  }
  if (init_count == revCount) init_count = 0;
}

void motor_angle(int s_angle) { //초기화 이후 원하는 각도만큼 이동
  sw_press();
  if (power_on) {
    motor_flag = false;
  }
  if (motor_flag) {
    motor_set();
  }
  else {
    if (move_angle < s_angle && move_flag) {
      motor_assign(1);
    }
    if (move_angle >= s_angle) {
      move_flag = false;
    }
  }
}

