package com.chat.ruoyichat.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReBack2 {
/**
{
  "type": "recv-sms",
  "sim_src": 1,
  "sms_num": 1,z
  "sms": [
    [
      0,
      "12.sp256.101",
      1755697537,
      "12052703122",
      "16463915790",
      "U2VuZGVyOiAxMjA1MjcwMzEyMg0KUmVjZWl2ZXI6ICIxMi4wMSIgMTY0NjM5MTU3OTANClNNU0M6IDE0MDU0NzI0MDcwDQpTQ1RTOiAyNTA4MjAwNjQ1MzZBOA0KDQpKampqampqampqampqampqampqampqampqampqamo="
    ]
  ]
}
**/
    private String type;
    private Long sim_src;
    private Long sms_num;
    private Object[] sms;
}
