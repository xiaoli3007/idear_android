package com.guo.memorandum;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public class AlarmNote extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//��ñ���
		String messageTitle=intent.getStringExtra("messageTitle");
		//�������
		String messageContent=intent.getStringExtra("messageContent");
		Intent in=new Intent();
		in.setClass(context, Alarm.class);
		in.putExtra("messageTitle", messageTitle);
		in.putExtra("messageContent", messageContent);
		in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//����Alarm
		context.startActivity(in);
	}
}
