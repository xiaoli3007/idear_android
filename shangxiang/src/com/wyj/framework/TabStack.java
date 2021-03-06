package com.wyj.framework;

import java.util.LinkedList;

public class TabStack {

	private LinkedList<String> views;

	public TabStack() {
		views = new LinkedList<String>();
	}

	public String top() {
		if (!views.isEmpty())
			return views.getFirst();
		else
			return null;
	}

	public void pop() {
		if (!views.isEmpty())
			views.removeFirst();
	}

	public boolean isEmpty() {
		return views.isEmpty();
	}

	public void push(String view) {
		views.addFirst(view);
	}

	public void clear() {
		views.clear();
	}

	public void traverse() {
		while (!isEmpty()) {
			System.out.println(top());
			pop();
		}
	}

	public void popSome(int sum) {
		for (int i = 0; i < sum; i++) {
			pop();
		}
	}

	public int getTheSumToPop(String id) {
		int index = views.indexOf(id);
		return index + 1;
	}

	public void popSome(String id) {
		popSome(getTheSumToPop(id));
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (String id : views) {
			builder.append(id);
			builder.append(",\t");

		}
		return builder.toString();
	}

	public int size() {
		return this.views.size();
	}
}
