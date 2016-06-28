package com.shangxiang.android;

import com.shangxiang.android.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;

public class BaseFragment extends Fragment implements OnClickListener {
	private FragmentManager managerFragment;

	@Override
	public void onActivityCreated(Bundle sinha) {
		super.onActivityCreated(sinha);
		ShangXiang.classCurr = getClass();
		this.managerFragment = getActivity().getSupportFragmentManager();
	}

	public void goActivity(Class<?> target, Bundle bundle) {
		goActivity(ShangXiang.APP, target, bundle);
	}

	public void goActivity(Class<?> target) {
		goActivity(ShangXiang.APP, target, null);
	}

	public void goActivity(Context content, Class<?> target, Bundle bundle) {
		Intent intent = new Intent(ShangXiang.APP, target);
		if (null != bundle) {
			intent.putExtras(bundle);
		}
		this.startActivity(intent);
	}

	public void goFragment(Fragment fragment) {
		goFragment(fragment, null, true);
	}

	public void goFragment(Fragment fragment, Bundle bundle) {
		goFragment(fragment, bundle, true);
	}

	public void goFragment(Fragment fragment, Bundle bundle, boolean anim) {
		if (null != bundle) {
			fragment.setArguments(bundle);
		}
		ShangXiang.fragmentCurr = fragment;
		FragmentTransaction transFrogment = this.managerFragment.beginTransaction();
		transFrogment.setCustomAnimations(R.anim.push_right_in, R.anim.push_left_out, R.anim.push_left_in, R.anim.push_right_out);
		transFrogment.addToBackStack(Consts.TAG);
//		transFrogment.hide(this);
//		if (!fragment.isAdded()) {
//			transFrogment.add(ShangXiang.tabContent, fragment);
//		} else {
//			transFrogment.show(fragment);
//		}
//		transFrogment.commitAllowingStateLoss();
		transFrogment.replace(ShangXiang.tabContent, fragment);
		transFrogment.commit();
	}

	public void goHomeFragment() {
		this.managerFragment.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onClick(View v) {
	}
}
