package com.wyj.Activity;

import org.json.JSONArray;
import org.json.JSONException;

import com.wyj.Activity.R;
import com.wyj.adapter.ListTempleAdapter;
import com.wyj.adapter.OrderSuccItemAdapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.os.Bundle;

import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class OrderPaySucc extends Activity implements OnClickListener {

	private static String TAG = "OrderPaySucc";

	private ImageView order_form_paysucc_back;
	private ProgressDialog pDialog = null;
	private Button order_form_paysucc_orderdetail;
	private Button order_form_paysucc_sharebutton;
	private ListView viewList;
	private LinearLayout share_order_layout;
	
	private OrderSuccItemAdapter adapterordersucc;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.order_pay_succ);

		findViewById();
		setListener();
		
		load_succ_orders();
	}

	

	private void findViewById() {

		order_form_paysucc_back = (ImageView) findViewById(R.id.order_form_paysucc_back);
		order_form_paysucc_orderdetail =(Button) findViewById(R.id.order_form_paysucc_orderdetail);
		order_form_paysucc_sharebutton =(Button) findViewById(R.id.order_form_paysucc_sharebutton);
		viewList = (ListView) findViewById(R.id.ordersucc_list_item);
		
		share_order_layout= (LinearLayout) findViewById(R.id.share_order_layout);
	}

	private void setListener() {

		order_form_paysucc_back.setOnClickListener(this);
		order_form_paysucc_orderdetail.setOnClickListener(this);
		order_form_paysucc_sharebutton.setOnClickListener(this);
		share_order_layout.setOnClickListener(this);
	}
	
	
	private void load_succ_orders() {
		// TODO Auto-generated method stub
		try {
			adapterordersucc = new OrderSuccItemAdapter(
					OrderPaySucc.this, new JSONArray("[{ \"info\":\"广州刘小压请什么什么居士在五台山上香祈福到今年大苏打大苏打一年得偿所愿\"}, { \"info\":\"广州刘小压请什么什么居士在五台山上香祈福到今年大苏打大苏打一年得偿所愿\"}, { \"info\":\"广州刘小压请什么什么居士在五台山上香祈福到今年大苏打大苏打一年得偿所愿\"}]"));
			viewList.setAdapter(adapterordersucc);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		case R.id.order_form_paysucc_back:
			Intent bak_My_intent = new Intent(OrderPaySucc.this, Wish.class);
			WishGroupTab.getInstance().switchActivity("Wish", bak_My_intent,
					-1, -1);
			break;
		case R.id.order_form_paysucc_sharebutton:
			
			share_order_layout.setVisibility(View.VISIBLE);
			
			break;
		case R.id.share_order_layout:
			share_order_layout.setVisibility(View.GONE);
			break;

		}
	}
	
	

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			WishGroupTab.getInstance().onKeyDown(keyCode, event);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}
