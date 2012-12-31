package com.cnki.client.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLHandshakeException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.webkit.DownloadListener;

public class HttpTools {
	public static final int METHOD_GET = 1;
	public static final int METHOD_POST = 2;
	
	/** ִ��downfile�󣬵õ������ļ��Ĵ�С */
	private long contentLength;
	/** ��������ʧ����Ϣ **/
	private String strResult = "�������޷����ӣ���������";

	/** http ����ͷ���� **/
	private HttpParams httpParams;
	/** httpClient ���� **/
	private DefaultHttpClient httpClient;

	/** �õ������� **/
	private Context context;
	private Activity activity = null;
	
	/**���½���UI**/
	Handler mHandler;

	/** HTTP������Ĺ��췽�� */
	public HttpTools(Context context) {
		this.context = context;
		
		getHttpClient();
	}
	
	public HttpTools() {}
	

	/**
	 * �õ� apache http HttpClient���� һ������£����ǻ�ȡhttpclient�����һ�仰��httpClient = new
	 * DefaultHttpClient();
	 * �������DefaultHttpClient()������û���������������������ƣ����Ծ��������������ͨ������ HTTP������Լ������
	 * **/
	public DefaultHttpClient getHttpClient() {
		/** ���� HttpParams ���������� HTTP ���� **/
		httpParams = new BasicHttpParams();

		/** �������ӳ�ʱ�� Socket ��ʱ���Լ� Socket �����С **/

		HttpConnectionParams.setConnectionTimeout(httpParams, 20 * 1000);

		HttpConnectionParams.setSoTimeout(httpParams, 20 * 1000);

		HttpConnectionParams.setSocketBufferSize(httpParams, 8192);
		// ���µķ�ʽ���·��ʣ��������ͨ����������������
		/** �������HTTP�����ض���������ˣ�true����˼�����÷����ض���get��post�ض���������ͬ */
		HttpClientParams.setRedirecting(httpParams, true);

		/**
		 * ����һ�� HttpClient ʵ�� //�����Զ�ѡ�����磬����Ӧcmwap��CMNET��wifi��3G
		 */
		MyHttpCookies li = new MyHttpCookies(context);
		// �������Ϊ���������
		String proxyStr = li.getHttpProxyStr();
		if (proxyStr != null && proxyStr.trim().length() > 0) {
			HttpHost proxy = new HttpHost(proxyStr, 80);
			httpClient.getParams().setParameter(ConnRouteParams.DEFAULT_PROXY,
					proxy);
		}
		/** ע�� HttpClient httpClient = new HttpClient(); ��Commons HttpClient **/
		httpClient = new DefaultHttpClient(httpParams);
		// ���������쳣ʱ��Ҫ�����Ĵ��� .Ϊ�˿����Զ����쳣�ָ����ƣ�Ӧ���ṩһ��HttpRequestRetryHandler�ӿڵ�ʵ�֡�
		httpClient.setHttpRequestRetryHandler(requestRetryHandler);

		return httpClient;

	}

	/**
	 * �쳣�Զ��ָ�����, ʹ��HttpRequestRetryHandler�ӿ�ʵ��������쳣�ָ�
	 */
	private static HttpRequestRetryHandler requestRetryHandler = new HttpRequestRetryHandler() {
		// �Զ���Ļָ�����
		public boolean retryRequest(IOException exception, int executionCount,
				HttpContext context) {
			// ���ûָ����ԣ��ڷ����쳣ʱ���Զ�����N��
			if (executionCount >= 3) {
				// �������������Դ�������ô�Ͳ�Ҫ������
				return false;
			}
			if (exception instanceof NoHttpResponseException) {
				// ������������������ӣ���ô������
				return true;
			}
			if (exception instanceof SSLHandshakeException) {
				// ��Ҫ����SSL�����쳣
				return false;
			}
			HttpRequest request = (HttpRequest) context
					.getAttribute(ExecutionContext.HTTP_REQUEST);
			boolean idempotent = (request instanceof HttpEntityEnclosingRequest);
			if (!idempotent) {
				// ���������Ϊ���ݵȵģ���ô������
				return true;
			}
			return false;
		}
	};

	/**
	 * 
	 * �ܵ���˵DoGet�ǲ���ȫ�ģ�����û�����Ϣ��¶��URL�У��������ֻ��п��ƿ�������������˽�������ץ���Ļ�����ץ����
	 * �ṩGET��ʽ�ķ����������� doGet ����ʾ����
	 * 
	 * @param url
	 *            �����ַ
	 * @return ���� String jsonResult;
	 * 
	 */
	public String doGet(String url) {
		/** ����HttpGet���� **/
		HttpGet httpRequest = new HttpGet(url);
		httpRequest.setHeaders(this.getHeader());
		try {
			/** ���ֻỰSession **/
			/** ����Cookie **/
			MyHttpCookies li = new MyHttpCookies(context);
			CookieStore cs = li.getuCookie();
			/** ��һ������App�����CookieΪ�գ�����ʲôҲ������ֻ�е�APP��Cookie��Ϊ�յ�ʱ�����������Cooke�Ž�ȥ **/
			if (cs != null) {
				httpClient.setCookieStore(li.getuCookie());
			}

			/** ���ֻỰSession end **/

			/* �������󲢵ȴ���Ӧ */
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			/* ��״̬��Ϊ200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* ���������� */
				strResult = EntityUtils.toString(httpResponse.getEntity());

				/** ִ�гɹ�֮��õ� **/
				/** �ɹ�֮��ѷ��سɹ���Cookis����APP�� **/
				// ����ɹ�֮��ÿ�ζ�����Cookis����֤ÿ�����������µ�Cookis
				li.setuCookie(httpClient.getCookieStore());

			} else {
				strResult = "Error Response: "
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			strResult = nullToString(e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			strResult = nullToString(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			strResult = nullToString(e.getMessage());
			e.printStackTrace();
		} finally {
			httpRequest.abort();
			this.shutDownClient();
		}
		return strResult;
	}

	/**
	 * �ṩGET��ʽ�ķ����������� doGet ����ʾ���� Map params=new HashMap();
	 * params.put("usename","helijun"); params.put("password","123456");
	 * httpClient.doGet(url,params)��
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @return ���� String jsonResult;
	 * 
	 * **/
	public String doGet(String url, Map params) {
		/** ����HTTPGet���� **/
		String paramStr = "";
		if (params == null)
			params = new HashMap();
		/** ��������������� **/
		Iterator iter = params.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object key = entry.getKey();
			String val = nullToString(entry.getValue());
			paramStr += paramStr = "&" + key + "=" + URLEncoder.encode(val);
		}
		if (!paramStr.equals("")) {
			paramStr = paramStr.replaceFirst("&", "?");
			url += paramStr;
		}
		return doGet(url);
	}
	
	/**
	 * �ṩGET��ʽ�ķ����������� doGet ����ʾ���� Map params=new HashMap();
	 * params.put("usename","gongshuanglin"); params.put("password","123456");
	 * httpClient.doGet(url,params)��
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @return ���� String jsonResult;
	 * 
	 */
	public String doGet(String url, List<NameValuePair> params) {
		/** ����HTTPGet���� **/
		String paramStr = "";
		if (params == null)
			params = new ArrayList<NameValuePair>();
		/** ��������������� **/

		for (NameValuePair obj : params) {
			paramStr += paramStr = "&" + obj.getName() + "="
					+ URLEncoder.encode(obj.getValue());
		}
		if (!paramStr.equals("")) {
			paramStr = paramStr.replaceFirst("&", "?");
			url += paramStr;
		}
		return doGet(url);
	}

	/**
	 * �ṩPost��ʽ�ķ����������� Post ����ʾ���� doPost ����ʾ�� List<NameValuePair> paramlist =
	 * new ArrayList<NameValuePair>(); paramlist(new BasicNameValuePair("email",
	 * "xxx@123.com")); paramlist(new BasicNameValuePair("address", "123abc"));
	 * httpClient.doPost(url,paramlist);
	 * 
	 * @param url
	 *            �����ַ
	 * @param params
	 *            �������
	 * @return ���� String jsonResult;
	 * **/

	public String doPost(String url, List<NameValuePair> params) {
		/* ����HTTPPost���� */

		HttpPost httpRequest = new HttpPost(url);
		// ��������Header��Ϣ��
		httpRequest.setHeaders(this.getHeader());
		try {

			/** ������������������� */
			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));

			/** ���ֻỰSession **/
			/** ����Cookie **/
			MyHttpCookies li = new MyHttpCookies(context);
			CookieStore cs = li.getuCookie();
			/** ��һ������App�����CookieΪ�գ�����ʲôҲ������ֻ�е�APP��Cookie��Ϊ�յ�ʱ�����������Cooke�Ž�ȥ **/
			if (cs != null) {
				httpClient.setCookieStore(li.getuCookie());
			}

			/** ���ֻỰSession end **/

			/** �������󲢵ȴ���Ӧ */

			HttpResponse httpResponse = httpClient.execute(httpRequest);

			/** ��״̬��Ϊ200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/* ���������� */
				strResult = EntityUtils.toString(httpResponse.getEntity());

				/** ִ�гɹ�֮��õ� **/
				/** �ɹ�֮��ѷ��سɹ���Cookis����APP�� **/
				// ����ɹ�֮��ÿ�ζ�����Cookis����֤ÿ�����������µ�Cookis
				li.setuCookie(httpClient.getCookieStore());

				/** ����Cookie end **/
			} else {
				strResult = "Error Response: "
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			strResult = "";
			// strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			strResult = "";
			// strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			strResult = "";
			// strResult = e.getMessage().toString();
			e.printStackTrace();
		} finally {
			httpRequest.abort();
			this.shutDownClient();
		}
		return strResult;
	}

	/**
	 * ͨ��ָ���Ľӿڻ�ȡ����ʵ���ļ������õ�ʵ���ļ��Ĵ�С
	 * һ������µ���ĳ���ӿ�����ʵ������ʱ����Ҫ�ͻ����ṩһЩ��Ϣ�������û��������ļ�������֮��ģ����һ���Ҫ�ṩ���󷽷�����.
	 * @param url
	 * @return
	 */
	public HttpEntity DownLoadFile(String url,ArrayList<NameValuePair> params, int method) {
		/** ����HttpGet���� **/
		HttpUriRequest httpRequest = null;
		switch (method) {
		case METHOD_GET:
			String paramStr = "";
			if (params == null)
				params = new ArrayList<NameValuePair>();
			/** ��������������� **/

			for (NameValuePair obj : params) {
				paramStr += paramStr = "&" + obj.getName() + "="
						+ URLEncoder.encode(obj.getValue());
			}
			if (!paramStr.equals("")) {
				paramStr = paramStr.replaceFirst("&", "?");
				url += paramStr;
			}
			httpRequest = new HttpGet(url);
			httpRequest.setHeaders(this.getHeader());
			break;

		case METHOD_POST:
			httpRequest = new HttpPost(url);
			httpRequest.setHeaders(this.getHeader());
			if (params != null && !params.isEmpty()) {
				//��������ʵ��
				try {
					UrlEncodedFormEntity requestentity = new UrlEncodedFormEntity(
							params);
					((HttpPost)httpRequest).setEntity(requestentity);
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			break;
		}
		
		try {
			/** ���ֻỰSession **/
			/** ����Cookie **/
			MyHttpCookies li = new MyHttpCookies(context);
			CookieStore cs = li.getuCookie();
			/** ��һ������App�����CookieΪ�գ�����ʲôҲ������ֻ�е�APP��Cookie��Ϊ�յ�ʱ�����������Cooke�Ž�ȥ **/
			if (cs != null) {
				httpClient.setCookieStore(li.getuCookie());
			}
			/** ���ֻỰSession end **/
			/* �������󲢵ȴ���Ӧ */
			HttpResponse httpResponse = httpClient.execute(httpRequest);
			/* ��״̬��Ϊ200 ok */
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				/** ִ�гɹ�֮��õ� **/
				/** �ɹ�֮��ѷ��سɹ���Cookis����APP�� **/
				// ����ɹ�֮��ÿ�ζ�����Cookis����֤ÿ�����������µ�Cookis
				li.setuCookie(httpClient.getCookieStore());
				this.contentLength = httpResponse.getEntity()
						.getContentLength();
				/* ���������� */
				return httpResponse.getEntity();
			} else {
				strResult = "Error Response: "
						+ httpResponse.getStatusLine().toString();
			}
		} catch (ClientProtocolException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (IOException e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} catch (Exception e) {
			strResult = e.getMessage().toString();
			e.printStackTrace();
		} finally {
			// httpRequest.abort();
			// this.shutDownClient();
		}
		this.contentLength = 0;
		return null;
	}
	/**
	 * �����صõ���entityת��Ϊ������
	 * @param url
	 * @param params
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public  InputStream getStream(String url,ArrayList<NameValuePair> params,int method) throws Exception{
		InputStream in = null;
		HttpEntity _entity  = DownLoadFile(url, params, method);
		if(_entity != null){
			in = _entity.getContent();
		}
		return in;
	}
	
	/**
	 * ͨ��ʵ������ȡ������
	 * @param entity
	 * @return
	 * @throws IllegalStateException
	 * @throws IOException
	 */
	public static InputStream getStream(HttpEntity entity) throws IllegalStateException, IOException{
		if(entity != null){
			return entity.getContent();
		}
		return null;
	}
	
	/**
	 * �����صõ���entityת��Ϊ�ֽ����飬�ʺ���ͼƬ�Ļ�ȡ
	 * @param url
	 * @param params
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public  byte[] getByte(String url,ArrayList<NameValuePair> params,int method) throws Exception{
		byte[] _bytes = null;
		HttpEntity _entity = DownLoadFile(url, params, method);
		if(_entity != null){
			return _bytes = EntityUtils.toByteArray(_entity);
		}
		return _bytes;			
	}
	/**
	 * �����صõ���entityת��ΪString���ͣ��ʺ��ڻ�ȡ��������JSON�ַ������߷���ֵ
	 * @param url
	 * @param params
	 * @param method
	 * @return
	 * @throws Exception
	 */
	public  String toString(String url,ArrayList<NameValuePair> params,int method) throws Exception{
		HttpEntity _entity = DownLoadFile(url, params, method);
		if(_entity != null){
			return EntityUtils.toString(_entity, "utf-8");
		}
		return null;
	}
	
	public long getContentLength() {
		return contentLength;
	}
	
	/**
	 * �����ʽ������HttpURLConnection������������ģ��������ַ�ʽ�Ƚ�ԭʼ������չ��û��HttPClient����
	 * ��һ���������������Դ�����ַ���Ҳ����
	 * @param urlPath
	 * @return
	 */
	public String getStringByURLConnection(String urlPath) {
		String json = null;
		try {
			
			URL url = new URL(urlPath);
			// ����HttpURLConnection����,���ǿ��Դ������л�ȡ��ҳ����.
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			 // ��λ�Ǻ��룬���ó�ʱʱ��Ϊ5��
			conn.setConnectTimeout(5*1000);
			// HttpURLConnection��ͨ��HTTPЭ������path·���ģ�������Ҫ��������ʽ,���Բ����ã���ΪĬ��ΪGET
			conn.setRequestMethod("GET");
			if(conn.getResponseCode() == 200) {
				InputStream is = conn.getInputStream();
				byte[] data = readStream(is); //��������ת��Ϊ�ַ�����
				json = new String(data);//���ַ�����ת��Ϊ�ַ���
			}				
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return json;
	}
	
	/**
	 * ��������ת��Ϊ�ַ�����
	 * @param is
	 * @return
	 * @throws Exception
	 */
	public  byte[] readStream(InputStream is)  {
		ByteArrayOutputStream bout = null;
		try {
			bout = new ByteArrayOutputStream();
			byte[] buffer = new byte[1024];
			int len = 0;
			while((len = is.read(buffer)) != -1){
				bout.write(buffer, 0, len);
			}
			
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bout.toByteArray();
	}
	

	/** �õ��豸��Ϣ��ϵͳ�汾���������� **/
	private Header[] getHeader() {
		/** ����ͷ��Ϣ end **/
		MyHttpCookies li = new MyHttpCookies(context);
		return li.getHttpHeader();
	}

	/**
	 * ����obj���� ��null����""
	 * 
	 * @param obj
	 * @return
	 */
	public static String nullToString(Object obj) {
		if (obj == null) {
			return "";
		}
		return obj.toString();
	}

	/** ����HTTPCLient **/
	public void shutDownClient() {
		httpClient.getConnectionManager().shutdown();
	}
}
