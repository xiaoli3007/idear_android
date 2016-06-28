package com.shangxiang.android.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.shangxiang.android.Consts;
import com.shangxiang.android.ShangXiang;
import com.shangxiang.android.utils.Utils;

public class SinhaPipeClient {
	public String ERR_TIME_OUT = "ERR_TIME_OUT";
	public String ERR_GET_ERR = "ERR_GET_ERR";
	public String ERR_UNKNOW = "ERR_GET_ERR";
	private int methodType = 0;
	private HttpClient httpClient;
	private HttpGet httpMethodGet;
	private HttpPost httpMethodPost;
	private int timeout = 5000;
	private int soTimeout = 10000;
	private int connTimeout = 30000;
	private String headerCharset = HTTP.UTF_8;
	private String headerUserAgent = "Sinha-Android";
	private boolean resetCookies = false;

	public SinhaPipeClient() {
		this.httpClient = GetHttpClient();
	}

	private synchronized HttpClient GetHttpClient() {
		if (null == this.httpClient) {
			HttpParams params = new BasicHttpParams();
			HttpClientParams.setCookiePolicy(params, CookiePolicy.BROWSER_COMPATIBILITY);
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setUseExpectContinue(params, true);
			HttpProtocolParams.setUserAgent(params, this.headerUserAgent);
			HttpProtocolParams.setContentCharset(params, this.headerCharset);
			ConnManagerParams.setTimeout(params, this.timeout);
			HttpConnectionParams.setConnectionTimeout(params, this.soTimeout);
			HttpConnectionParams.setSoTimeout(params, this.connTimeout);

			SchemeRegistry schemeReg = new SchemeRegistry();
			schemeReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
			schemeReg.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

			ClientConnectionManager connManager = new ThreadSafeClientConnManager(params, schemeReg);
			this.httpClient = new DefaultHttpClient(connManager, params);
		}
		return this.httpClient;
	}

	public void Config(String method, URI uri, List<NameValuePair> params, SinhaMultiPartEntity multipartEntity) {
		if (method == "post") {
			this.methodType = 1;
			if (null != this.httpMethodPost) {
				this.httpMethodPost = null;
			}
			this.httpMethodPost = MethodPost(uri, params);
		} else if (method == "upload") {
			this.methodType = 1;
			if (null != this.httpMethodPost) {
				this.httpMethodPost = null;
			}
			this.httpMethodPost = MethodUpload(MakeURI(uri, params), multipartEntity);
		} else {
			this.methodType = 0;
			if (null != this.httpMethodGet) {
				this.httpMethodGet = null;
			}
			this.httpMethodGet = MethodGet(MakeURI(uri, params));
		}
	}

	public void Config(String method, String url, List<NameValuePair> params) {
		this.Config(method, URI.create(url), params, null);
	}

	public void Config(String method, String url, List<NameValuePair> params, boolean resetCookies) {
		this.Config(method, URI.create(url), params, null);
		this.resetCookies = resetCookies;
	}

	public void Config(String method, String url, List<NameValuePair> params, boolean resetCookies, SinhaMultiPartEntity multipartEntity) {
		this.Config(method, URI.create(url), params, multipartEntity);
		this.resetCookies = resetCookies;
	}

	public HttpPost MethodPost(URI uri, List<NameValuePair> params) {
		HttpPost httpMethod = new HttpPost(uri);
		if (Consts.DEBUG_NET) {
			Utils.Log("url: " + uri.toString());
		}
		if (null != params) {
			try {
				httpMethod.setEntity(new UrlEncodedFormEntity(params, this.headerCharset));
				if (Consts.DEBUG_NET) {
					try {
						Utils.Log("post: " + EntityUtils.toString(httpMethod.getEntity()));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		httpMethod.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httpMethod.addHeader("charset", this.headerCharset);
		httpMethod.addHeader("MyInfo-Agent", this.headerUserAgent);
		httpMethod.addHeader("Cookie", ShangXiang.cookies);
		httpMethod.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, this.connTimeout);
		httpMethod.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, this.connTimeout);
		return httpMethod;
	}

	public HttpGet MethodGet(URI uri) {
		HttpGet httpMethod = new HttpGet(uri);
		if (Consts.DEBUG_NET) {
			Utils.Log("url: " + uri.toString());
		}
		httpMethod.addHeader("Content-Type", "application/x-www-form-urlencoded");
		httpMethod.addHeader("charset", this.headerCharset);
		httpMethod.addHeader("MyInfo-Agent", this.headerUserAgent);
		httpMethod.addHeader("Cookie", ShangXiang.cookies);
		httpMethod.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, this.connTimeout);
		httpMethod.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, this.connTimeout);
		return httpMethod;
	}

	public HttpPost MethodUpload(URI uri, SinhaMultiPartEntity multipartEntity) {
		HttpPost httpMethod = new HttpPost(uri);
		if (Consts.DEBUG_NET) {
			Utils.Log("url: " + uri.toString());
		}
		httpMethod.setEntity(multipartEntity);
		httpMethod.addHeader("charset", this.headerCharset);
		httpMethod.addHeader("MyInfo-Agent", this.headerUserAgent);
		httpMethod.addHeader("Cookie", ShangXiang.cookies);
		httpMethod.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, this.connTimeout);
		httpMethod.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, this.connTimeout);
		return httpMethod;
	}

	public HttpResponse Call() throws SinhaPipeException {
		HttpResponse response = null;
		try {
			if (1 == this.methodType) {
				response = this.httpClient.execute(this.httpMethodPost);
			} else {
				response = this.httpClient.execute(this.httpMethodGet);
			}
			if (this.resetCookies) {
				List<Cookie> cookies = ((DefaultHttpClient) this.httpClient).getCookieStore().getCookies();
				StringBuffer stringBuffer = new StringBuffer();
				for (Cookie cookie : cookies) {
					stringBuffer.append(cookie.getName()).append("=").append(cookie.getValue()).append(";");
				}
				ShangXiang.cookies = stringBuffer.toString();
			}
			int statusCode = response.getStatusLine().getStatusCode();
			if (Consts.DEBUG_NET) {
				Utils.Log("status: " + statusCode);
			}
			if (statusCode != HttpStatus.SC_OK) {
				throw new SinhaPipeException(this.ERR_GET_ERR);
			}
		} catch (SinhaPipeException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new SinhaPipeException(this.ERR_UNKNOW);
		} finally {
		}
		return response;
	}

	private URI MakeURI(URI uri, List<NameValuePair> params) {
		URI uriResult = null;
		try {
			if (null == uri) {
				return null;
			}
			String url = uri.toString().replace("?" + uri.getQuery(), "");
			url += "?" + (null != uri.getQuery() ? uri.getQuery() : "") + MakeParams(params);
			uriResult = URI.create(url);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return uriResult;
	}

	private String MakeParams(List<NameValuePair> params) {
		String paramsResult = null;
		StringBuffer stringBuffer = new StringBuffer();
		if (null != params && 0 != params.size()) {
			for (int i = 0; i < params.size(); i++) {
				NameValuePair param = params.get(i);
				stringBuffer.append("&").append(param.getName()).append("=").append(param.getValue());
			}
		}
		paramsResult = stringBuffer.toString();
		return paramsResult;
	}

	public String ToString(HttpResponse response) throws SinhaPipeException {
		String result = null;
		try {
			StringBuilder builder = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			for (String s = br.readLine(); s != null; s = br.readLine()) {
				builder.append(s);
			}
			result = builder.toString();
		} catch (IllegalStateException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return result;
	}

	public Bitmap ToBitmap(HttpResponse response) throws SinhaPipeException {
		Bitmap paramsResult = null;
		try {
			paramsResult = BitmapFactory.decodeStream(response.getEntity().getContent());
		} catch (IllegalStateException e) {
			throw new SinhaPipeException(this.ERR_UNKNOW);
		} catch (IOException e) {
			throw new SinhaPipeException(this.ERR_UNKNOW);
		}
		return paramsResult;
	}

	public void ShutDown() {
		this.httpClient.getConnectionManager().shutdown();
	}
}