package com.example.cms;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseFragment extends Fragment implements OnClickListener {
	@Override
	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		Cms.classCurr = getClass();
	}

	public void goActivity(Class<?> target, Bundle bundle) {
		goActivity(Cms.APP, target, bundle);
	}

	public void goActivity(Class<?> target) {
		goActivity(Cms.APP, target, null);
	}

	public void goActivity(Context content, Class<?> target, Bundle bundle) {
		Intent intent = new Intent(Cms.APP, target);
		if (null != bundle) {
			intent.putExtras(bundle);
		}
		getActivity().startActivityForResult(intent, 0);
	}

	public void goFragment(Fragment fragment, Bundle bundle) {
		if (null != bundle) {
			fragment.setArguments(bundle);
		}
		goFragment(fragment);
	}

	public void goFragment(Fragment fragment) {
		FragmentTransaction transFrogment = getFragmentManager().beginTransaction();
		transFrogment.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_left_in, R.anim.push_right_out);
		transFrogment.replace(Cms.tabContent, fragment);
		transFrogment.addToBackStack(Consts.TAG);
		transFrogment.commit();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
	}
}
