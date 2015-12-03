package com.flash.commons.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flash.commons.json.JsonHelper;
/**
 * HTTP方法类库
 * @author leon
 *
 */
public class HttpClientUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtils.class);

	public static final String UTF8 = "utf-8";
	
	public static final String GBK = "gbk";
	/**
	 * Https get请求
	 * 
	 * @author lonaking
	 * @return
	 */
	public static String httpsGet(String url) {
		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		try {
			sslContextBuilder.loadTrustMaterial(null,
					new TrustSelfSignedStrategy());
			SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(
					sslContextBuilder.build());
			// 构造https请求的client
			CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(socketFactory).build();
			HttpGet get = new HttpGet(url);
			CloseableHttpResponse response = client.execute(get);
			String result = EntityUtils.toString(response.getEntity(), "utf-8");
			// TODO 这里没用做response.close() 后期更换为httpclient 连接池子
			LOGGER.debug("httpsget请求发送成功，得到数据{}", result);
			return result;
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	public static RequestConfig config;

	static {
		config = RequestConfig.custom().setConnectTimeout(2000)
				.setSocketTimeout(2000).setConnectionRequestTimeout(1000)
				.setMaxRedirects(1).setStaleConnectionCheckEnabled(true)
				.build();
	}

	public static HttpResponse doGet(String url) {
		return doGet(url, null);
	}

	/**
	 * get请求
	 * @param url
	 * @param args
	 * @return
	 */
	private static HttpResponse doGet(String url, Map<String, String> args) {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(config);
		CloseableHttpResponse response;
		try {
			response = client.execute(httpGet);
			LOGGER.info("请求成功,返回数据{},状态码{}",EntityUtils.toString(response.getEntity()),response.getStatusLine().getStatusCode());
			
			return response;
		} catch (ClientProtocolException e) {
			LOGGER.error("请求失败,{}",e.getMessage());
		} catch (IOException e) {
			LOGGER.error("请求失败,{}",e.getMessage());
		}
		return null;
	}

	
	public static HttpResponse doPost(String url) throws HttpException {
		return doPost(url, null);
	}
	
	/**
	 * post请求 
	 * @param url
	 * @param jsonString 
	 * @return
	 * @throws HttpException 
	 */
	public static HttpResponse doPost(String url, String jsonString) throws HttpException {
		CloseableHttpClient client = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(url);
		httpPost.setConfig(config);
		try {
			if (jsonString != "" && null != jsonString) {
				StringEntity entity = new StringEntity(jsonString);
				httpPost.setEntity(entity);
			}
			CloseableHttpResponse response = client.execute(httpPost);
			LOGGER.info("请求成功,返回数据{},状态码{}",EntityUtils.toString(response.getEntity()),response.getStatusLine().getStatusCode());
			return response;
		} catch (ClientProtocolException e) {
			LOGGER.error("请求失败,{}",e.getMessage());
			throw new HttpException("请求失败,"+e.getMessage(),e.getCause());
		} catch (IOException e) {
			LOGGER.error("请求失败,{}",e.getMessage());
			throw new HttpException("请求失败"+e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * get请求 返回指定类型对象
	 * @param url
	 * @param cla
	 * @return
	 * @throws HttpException 出现任何异常都会抛出此异常
	 */
	public static <T> T get(String url, Class<T> cla) throws HttpException{
		LOGGER.debug("开始进行http-get请求,url={}",url);
		long start = System.currentTimeMillis();
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(config);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpGet);
			String result = EntityUtils.toString(response.getEntity(), UTF8);
			LOGGER.debug("请求{}成功,返回数据{},耗时{}ms",url,result, System.currentTimeMillis() - start);
			return JsonHelper.transJsonStringToObj(result, cla);
		} catch (ClientProtocolException e) {
			LOGGER.error("请求失败,{}",e.getMessage());
			throw new HttpException("请求失败,"+e.getMessage(),e.getCause());
		} catch (IOException e) {
			LOGGER.error("请求失败,{}",e.getMessage());
			throw new HttpException("请求失败"+e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * get请求，返回请求结果
	 * @param url
	 * @return
	 * @throws HttpException
	 */
	public static String get(String url) throws HttpException{
		LOGGER.debug("开始进行http-get请求,url={}",url);
		long start = System.currentTimeMillis();
		CloseableHttpClient client = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(url);
		httpGet.setConfig(config);
		CloseableHttpResponse response = null;
		try {
			response = client.execute(httpGet);
			String result = EntityUtils.toString(response.getEntity(), UTF8);
			LOGGER.debug("请求{}成功,返回数据{},耗时{}ms",url,result, System.currentTimeMillis() - start);
			return result;
		} catch (ClientProtocolException e) {
			LOGGER.error("请求失败,{}",e.getMessage());
			throw new HttpException("请求失败,"+e.getMessage(),e.getCause());
		} catch (IOException e) {
			LOGGER.error("请求失败,{}",e.getMessage());
			throw new HttpException("请求失败"+e.getMessage(), e.getCause());
		}
	}
	
	public static String post(String url,Map<String, String> paramsMap) throws HttpException{
		return post(url, paramsMap,null);
	}
	
	public static String post(String url, Map<String, String> paramsMap, String charset) throws HttpException {
		if (url == null || url.isEmpty()) {
			LOGGER.error("请求失败，url为空");
			throw new HttpException("请求失败,url不能为空");
		}
		CloseableHttpClient client = HttpClients.createDefault();
		List<NameValuePair> params = getParamsList(paramsMap);
		UrlEncodedFormEntity formEntity = null;
		HttpPost post = null;
		CloseableHttpResponse response = null;
		String res = null;
		try {
			charset = (charset == null ? UTF8 : charset);
			formEntity = new UrlEncodedFormEntity(params, charset);
			post = new HttpPost(url);
			post.setEntity(formEntity);
			response = client.execute(post);
			res = EntityUtils.toString(response.getEntity());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (response != null) {
				try {
					response.close();
				} catch (IOException e) {
					LOGGER.error(e.getMessage());
				}
			}
		}
		return res;
	}

	/**
	 * 将参数Map转换成为NameValuePair集合
	 * @param paramsMap
	 * @return
	 */
	private static List<NameValuePair> getParamsList(Map<String, String> paramsMap) {
		if (paramsMap == null || paramsMap.size() == 0) {
			return new  ArrayList<NameValuePair>();
		}
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		for (Map.Entry<String, String> map : paramsMap.entrySet()) {
			params.add(new BasicNameValuePair(map.getKey(), map.getValue()));
		}
		if(params==null){
			return new  ArrayList<NameValuePair>();
		}
		return params;
	}
	private static Exception HttpException(String string) {
		// TODO Auto-generated method stub
		return null;
	}
	
}
