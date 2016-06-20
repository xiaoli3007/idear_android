package com.example.pipe;

public class SinhaPipeException extends Exception {
	private static final long serialVersionUID = 1L;

	public SinhaPipeException(Exception e) {
		super(e);
	}

	public SinhaPipeException(String string) {
		super(string);
	}

	public SinhaPipeException(String paramString, Throwable paramThrowable) {
		super(paramString, paramThrowable);
	}

	public SinhaPipeException(Throwable paramThrowable) {
		super(paramThrowable);
	}
}