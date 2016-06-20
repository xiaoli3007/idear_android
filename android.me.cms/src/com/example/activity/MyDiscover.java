package com.example.activity;

import org.json.JSONArray;
import org.json.JSONException;

import com.example.adapter.DiscoverAdapter;
import com.example.utils.Utils;
import com.example.cms.BaseFragment;
import com.example.cms.R;
import com.example.cms.Cms;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

public class MyDiscover extends BaseFragment {
	private RelativeLayout layoutLoading;
	private Button buttonBack;
	private ListView viewList;
	private DiscoverAdapter adapterDiscoverList;
	private boolean showLoading = false;

	@SuppressLint("InflateParams")
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle sinha) {
		View view = inflater.inflate(R.layout.my_discover, null);

		this.layoutLoading = (RelativeLayout) view.findViewById(R.id.loading);
		this.buttonBack = (Button) view.findViewById(R.id.my_discover_title_back_button);
		this.buttonBack.setOnClickListener(this);
		this.viewList = (ListView) view.findViewById(R.id.my_discover_container);


		return view;
	}

	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		this.adapterDiscoverList = new DiscoverAdapter(getActivity());
		this.viewList.setAdapter(this.adapterDiscoverList);
		loadDiscover();
	}

	private void loadDiscover() {
		if (Utils.CheckNetwork()) {
			showLoading();
			try {
				this.adapterDiscoverList.data = new JSONArray("[{\"name\":\"寂静的风\", \"date\":\"刚刚在五台山求愿财富\", \"content\":\"身体健康、万事如意、心想事成、财源广进、天天发财、长寿多福身体健康、万事如意、心想事成、财源广进、天天发财、长寿多福身体健康、万事如意、心想事成、财源广进、天天发财、长寿多福身体健康、万事如意、心想事成、财源广进、天天发财、长寿多福身体健康、万事如意、心想事成、财源广进、天天发财、长寿多福身体健康、万事如意、心想事成、财源广进、天天发财、长寿多福身体健康、万事如意、心想事成、财源广进、天天发财、长寿多福身体健康、万事如意、心想事成、财源广进、天天发财、长寿多福\", \"blessed\":\"太乙真人、居然散人等123人加持\"}, {\"name\":\"机器猫\", \"date\":\"1天前在五台山还愿\", \"content\":\"感谢菩萨完成我的愿望，谢谢！\", \"blessed\":\"太乙真人、居然散人等123人加持\"}, {\"name\":\"机器猫\", \"date\":\"1天前在五台山还愿\", \"content\":\"感谢菩萨完成我的愿望，谢谢！\", \"blessed\":\"太乙真人、居然散人等123人加持\"}, {\"name\":\"机器猫\", \"date\":\"1天前在五台山还愿\", \"content\":\"感谢菩萨完成我的愿望，谢谢！\", \"blessed\":\"太乙真人、居然散人等123人加持\"}, {\"name\":\"机器猫\", \"date\":\"1天前在五台山还愿\", \"content\":\"感谢菩萨完成我的愿望，谢谢！\", \"blessed\":\"太乙真人、居然散人等123人加持\"}, {\"name\":\"机器猫\", \"date\":\"1天前在五台山还愿\", \"content\":\"感谢菩萨完成我的愿望，谢谢！\", \"blessed\":\"太乙真人、居然散人等123人加持\"}, {\"name\":\"机器猫\", \"date\":\"1天前在五台山还愿\", \"content\":\"感谢菩萨完成我的愿望，谢谢！\", \"blessed\":\"太乙真人、居然散人等123人加持\"}, {\"name\":\"机器猫\", \"date\":\"1天前在五台山还愿\", \"content\":\"感谢菩萨完成我的愿望，谢谢！\", \"blessed\":\"太乙真人、居然散人等123人加持\"}]");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			this.adapterDiscoverList.notifyDataSetChanged();
			showLoading();
		} else {
			Utils.ShowToast(getActivity(), R.string.dialog_network_check_content);
		}
	}

	private void showLoading() {
		Utils.animView(this.layoutLoading, !this.showLoading);
		this.showLoading = !this.showLoading;
	}

	@Override
	public void onClick(View v) {
		if (v == this.buttonBack) {
			getActivity().onBackPressed();
		}
	}
}