boolean flag = false;

void sw_press(void)
{
  if (digitalRead(Power_SW) == LOW ) { //  파워 스위치
    if (flag == false) {
      flag = true;
      power_on = !power_on;
      move_flag = true;
      delay(500);
    }
  }
  else flag = false;
}
