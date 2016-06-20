package com.example.listphoto;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public abstract class CommonAdapter<T> extends BaseAdapter {
	protected LayoutInflater inflater;
	protected Context context;
	protected List<T> data;
	protected final int mItemLayoutId;

	public CommonAdapter(Context context, List<T> data, int itemLayoutId) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.data = data;
		this.mItemLayoutId = itemLayoutId;
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public T getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder = getViewHolder(position, convertView, parent);
		convert(viewHolder, getItem(position));
		return viewHolder.getConvertView();

	}

	public abstract void convert(ViewHolder helper, T item);

	private ViewHolder getViewHolder(int position, View convertView, ViewGroup parent) {
		return ViewHolder.get(context, convertView, parent, mItemLayoutId, position);
	}

}
