package com.wyj.pipe;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.wyj.Activity.R;



import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class Utils {
	public static final String DATE_MODE1 = "yyyy/MM/dd";
	public static final String DATE_MODE2 = "yyyy-MM-dd";

	public interface Callback {
		void callFinished();
	}

	public static boolean CheckNetwork() {
		ConnectivityManager cm = (ConnectivityManager) Cms.APP.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (null == netInfo || !netInfo.isConnected()) {
			return false;
		}
		if (netInfo.isRoaming()) {
			return true;
		}
		return true;
	}

	public static void setAR(int status) {
		if (Cms.AR_ORIGINAL != status) {
			Settings.System.putInt(Cms.APP.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, status);
			Cms.AR_CUR = status;
		}
	}

	public static void resetAR() {
		if (Cms.AR_ORIGINAL != Cms.AR_CUR) {
			Settings.System.putInt(Cms.APP.getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, Cms.AR_ORIGINAL);
			Cms.AR_CUR = Cms.AR_ORIGINAL;
		}
	}

	public static String formatDate(Calendar calendar) {
		return formatDate(DATE_MODE1, calendar);
	}

	public static String formatDate(int year, int month, int dayOfMonth) {
		return formatDate(DATE_MODE1, year, month, dayOfMonth);
	}

	public static String formatDate(String template, Calendar calendar) {
		return formatDate(template, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
	}

	@SuppressLint("SimpleDateFormat")
	public static String formatDate(String template, int year, int month, int dayOfMonth) {
		SimpleDateFormat formatter = new SimpleDateFormat(template);
		GregorianCalendar date = new GregorianCalendar(year, month, dayOfMonth);
		return formatter.format(date.getTime());
	}

	public static void ShowToast(Context context, int content) {
		ShowToast(context, context.getString(content));
	}

	public static void ShowToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public static void Dialog(Context context, int tip, int content) {
		Dialog(context, context.getString(tip), context.getString(content), null, null, null);
	}

	public static void Dialog(Context context, int tip, int content, final Callback callbackOK) {
		Dialog(context, context.getString(tip), context.getString(content), callbackOK, null, null);
	}

	public static void Dialog(Context context, int tip, int content, final Callback callbackOK, final Callback callbackCancel, final Callback callbackDismiss) {
		Dialog(context, context.getString(tip), context.getString(content), callbackOK, callbackCancel, callbackDismiss);
	}

	public static void Dialog(Context context, String tip, String content) {
		Dialog(context, tip, content, null, null, null);
	}

	public static void Dialog(Context context, String tip, String content, final Callback callbackOK, final Callback callbackCancel, final Callback callbackDismiss) {
		AlertDialog.Builder dialog = new AlertDialog.Builder(context).setTitle(tip).setMessage(content);
		dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				if (null != callbackOK) {
					callbackOK.callFinished();
				}
			}
		});
		if (null != callbackCancel) {
			dialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					callbackCancel.callFinished();
				}
			});
		}
		AlertDialog showDialog = dialog.create();
		if (null != callbackDismiss) {
			showDialog.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(DialogInterface dialog) {
					callbackDismiss.callFinished();
				}
			});
		}
		showDialog.show();
	}

	public static void animView(View view, boolean display) {
		float alphaFrom, alphaTo, translateFrom, translateTo;
		int visible;
		if (display) {
			alphaFrom = 0.0f;
			alphaTo = 1.0f;
			translateFrom = -1.0f;
			translateTo = 0.0f;
			visible = View.VISIBLE;
		} else {
			alphaFrom = 1.0f;
			alphaTo = 0.0f;
			translateFrom = 0.0f;
			translateTo = -1.0f;
			visible = View.GONE;
		}
		AnimationSet animationSet = new AnimationSet(true);
		Animation animation = new AlphaAnimation(alphaFrom, alphaTo);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, translateFrom, Animation.RELATIVE_TO_SELF, translateTo);
		animation.setDuration(500);
		animationSet.addAnimation(animation);
		view.startAnimation(animationSet);
		view.setVisibility(visible);
	}

	public static void hideKeyboard(Context context) {
		View viewCur = ((Activity) context).getCurrentFocus();
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Application.INPUT_METHOD_SERVICE);
		if (null != viewCur && null != imm) {
			imm.hideSoftInputFromWindow(viewCur.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float sizeRound) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(1, 1, bitmap.getWidth() - 1, bitmap.getHeight() - 1);
		final RectF rectF = new RectF(rect);

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, sizeRound, sizeRound, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;
	}

	public static void Log(String s) {
		Log.d(Consts.TAG, s);
	}

	public static String MD5(String string) {
		byte[] bytData = null;
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			bytData = digest.digest(string.getBytes());
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		StringBuffer out = new StringBuffer();
		for (byte data : bytData) {
			String hex = Integer.toHexString(data);
			if (hex.length() == 1) {
				out.append("0");
			} else if (hex.length() > 2) {
				hex = hex.substring(hex.length() - 2);
			}
			out.append(hex);
		}
		return out.toString().toLowerCase();
	}

	public static String InputStreamToString(InputStream is, String encode) {
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer sb = new StringBuffer();
		String line = "";
		try {
			while ((line = br.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}

	public static String encodeURIComponent(String component) {
		String result = null;
		try {
			result = URLEncoder.encode(component, "UTF-8").replaceAll("\\%28", "(").replaceAll("\\%29", ")").replaceAll("\\+", "%20").replaceAll("\\%27", "'").replaceAll("\\%21", "!").replaceAll("\\%7E", "~");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static String getRegions() {
		return "[{\"name\":\"请选择\",\"sub\":[{\"name\":\"请选择\"}],\"type\":1},{\"name\":\"北京\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"东城区\"},{\"name\":\"西城区\"},{\"name\":\"崇文区\"},{\"name\":\"宣武区\"},{\"name\":\"朝阳区\"},{\"name\":\"海淀区\"},{\"name\":\"丰台区\"},{\"name\":\"石景山区\"},{\"name\":\"房山区\"},{\"name\":\"通州区\"},{\"name\":\"顺义区\"},{\"name\":\"昌平区\"},{\"name\":\"大兴区\"},{\"name\":\"怀柔区\"},{\"name\":\"平谷区\"},{\"name\":\"门头沟区\"},{\"name\":\"密云县\"},{\"name\":\"延庆县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"广东\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"广州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"越秀区\"},{\"name\":\"荔湾区\"},{\"name\":\"海珠区\"},{\"name\":\"天河区\"},{\"name\":\"白云区\"},{\"name\":\"黄埔区\"},{\"name\":\"番禺区\"},{\"name\":\"花都区\"},{\"name\":\"南沙区\"},{\"name\":\"萝岗区\"},{\"name\":\"增城市\"},{\"name\":\"从化市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"深圳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"福田区\"},{\"name\":\"罗湖区\"},{\"name\":\"南山区\"},{\"name\":\"宝安区\"},{\"name\":\"龙岗区\"},{\"name\":\"盐田区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"珠海\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"香洲区\"},{\"name\":\"斗门区\"},{\"name\":\"金湾区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"汕头\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"金平区\"},{\"name\":\"濠江区\"},{\"name\":\"龙湖区\"},{\"name\":\"潮阳区\"},{\"name\":\"潮南区\"},{\"name\":\"澄海区\"},{\"name\":\"南澳县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"韶关\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"浈江区\"},{\"name\":\"武江区\"},{\"name\":\"曲江区\"},{\"name\":\"乐昌市\"},{\"name\":\"南雄市\"},{\"name\":\"始兴县\"},{\"name\":\"仁化县\"},{\"name\":\"翁源县\"},{\"name\":\"新丰县\"},{\"name\":\"乳源瑶族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"佛山\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"禅城区\"},{\"name\":\"南海区\"},{\"name\":\"顺德区\"},{\"name\":\"三水区\"},{\"name\":\"高明区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"江门\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"蓬江区\"},{\"name\":\"江海区\"},{\"name\":\"新会区\"},{\"name\":\"恩平市\"},{\"name\":\"台山市\"},{\"name\":\"开平市\"},{\"name\":\"鹤山市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"湛江\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"赤坎区\"},{\"name\":\"霞山区\"},{\"name\":\"坡头区\"},{\"name\":\"麻章区\"},{\"name\":\"吴川市\"},{\"name\":\"廉江市\"},{\"name\":\"雷州市\"},{\"name\":\"遂溪县\"},{\"name\":\"徐闻县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"茂名\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"茂南区\"},{\"name\":\"茂港区\"},{\"name\":\"化州市\"},{\"name\":\"信宜市\"},{\"name\":\"高州市\"},{\"name\":\"电白县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"肇庆\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"端州区\"},{\"name\":\"鼎湖区\"},{\"name\":\"高要市\"},{\"name\":\"四会市\"},{\"name\":\"广宁县\"},{\"name\":\"怀集县\"},{\"name\":\"封开县\"},{\"name\":\"德庆县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"惠州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"惠城区\"},{\"name\":\"惠阳区\"},{\"name\":\"博罗县\"},{\"name\":\"惠东县\"},{\"name\":\"龙门县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"梅州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"梅江区\"},{\"name\":\"兴宁市\"},{\"name\":\"梅县\"},{\"name\":\"大埔县\"},{\"name\":\"丰顺县\"},{\"name\":\"五华县\"},{\"name\":\"平远县\"},{\"name\":\"蕉岭县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"汕尾\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"城区\"},{\"name\":\"陆丰市\"},{\"name\":\"海丰县\"},{\"name\":\"陆河县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"河源\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"源城区\"},{\"name\":\"紫金县\"},{\"name\":\"龙川县\"},{\"name\":\"连平县\"},{\"name\":\"和平县\"},{\"name\":\"东源县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"阳江\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"江城区\"},{\"name\":\"阳春市\"},{\"name\":\"阳西县\"},{\"name\":\"阳东县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"清远\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"清城区\"},{\"name\":\"英德市\"},{\"name\":\"连州市\"},{\"name\":\"佛冈县\"},{\"name\":\"阳山县\"},{\"name\":\"清新县\"},{\"name\":\"连山壮族瑶族自治县\"},{\"name\":\"连南瑶族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"东莞\",\"sub\":[],\"type\":0},{\"name\":\"中山\",\"sub\":[],\"type\":0},{\"name\":\"潮州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"湘桥区\"},{\"name\":\"潮安县\"},{\"name\":\"饶平县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"揭阳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"榕城区\"},{\"name\":\"普宁市\"},{\"name\":\"揭东县\"},{\"name\":\"揭西县\"},{\"name\":\"惠来县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"云浮\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"云城区\"},{\"name\":\"罗定市\"},{\"name\":\"云安县\"},{\"name\":\"新兴县\"},{\"name\":\"郁南县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"上海\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"黄浦区\"},{\"name\":\"卢湾区\"},{\"name\":\"徐汇区\"},{\"name\":\"长宁区\"},{\"name\":\"静安区\"},{\"name\":\"普陀区\"},{\"name\":\"闸北区\"},{\"name\":\"虹口区\"},{\"name\":\"杨浦区\"},{\"name\":\"宝山区\"},{\"name\":\"闵行区\"},{\"name\":\"嘉定区\"},{\"name\":\"松江区\"},{\"name\":\"金山区\"},{\"name\":\"青浦区\"},{\"name\":\"南汇区\"},{\"name\":\"奉贤区\"},{\"name\":\"浦东新区\"},{\"name\":\"崇明县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"天津\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"和平区\"},{\"name\":\"河东区\"},{\"name\":\"河西区\"},{\"name\":\"南开区\"},{\"name\":\"河北区\"},{\"name\":\"红桥区\"},{\"name\":\"塘沽区\"},{\"name\":\"汉沽区\"},{\"name\":\"大港区\"},{\"name\":\"东丽区\"},{\"name\":\"西青区\"},{\"name\":\"北辰区\"},{\"name\":\"津南区\"},{\"name\":\"武清区\"},{\"name\":\"宝坻区\"},{\"name\":\"静海县\"},{\"name\":\"宁河县\"},{\"name\":\"蓟县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"重庆\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"渝中区\"},{\"name\":\"大渡口区\"},{\"name\":\"江北区\"},{\"name\":\"南岸区\"},{\"name\":\"北碚区\"},{\"name\":\"渝北区\"},{\"name\":\"巴南区\"},{\"name\":\"长寿区\"},{\"name\":\"双桥区\"},{\"name\":\"沙坪坝区\"},{\"name\":\"万盛区\"},{\"name\":\"万州区\"},{\"name\":\"涪陵区\"},{\"name\":\"黔江区\"},{\"name\":\"永川区\"},{\"name\":\"合川区\"},{\"name\":\"江津区\"},{\"name\":\"九龙坡区\"},{\"name\":\"南川区\"},{\"name\":\"綦江县\"},{\"name\":\"潼南县\"},{\"name\":\"荣昌县\"},{\"name\":\"璧山县\"},{\"name\":\"大足县\"},{\"name\":\"铜梁县\"},{\"name\":\"梁平县\"},{\"name\":\"开县\"},{\"name\":\"忠县\"},{\"name\":\"城口县\"},{\"name\":\"垫江县\"},{\"name\":\"武隆县\"},{\"name\":\"丰都县\"},{\"name\":\"奉节县\"},{\"name\":\"云阳县\"},{\"name\":\"巫溪县\"},{\"name\":\"巫山县\"},{\"name\":\"石柱土家族自治县\"},{\"name\":\"秀山土家族苗族自治县\"},{\"name\":\"酉阳土家族苗族自治县\"},{\"name\":\"彭水苗族土家族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"辽宁\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"沈阳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"沈河区\"},{\"name\":\"皇姑区\"},{\"name\":\"和平区\"},{\"name\":\"大东区\"},{\"name\":\"铁西区\"},{\"name\":\"苏家屯区\"},{\"name\":\"东陵区\"},{\"name\":\"于洪区\"},{\"name\":\"新民市\"},{\"name\":\"法库县\"},{\"name\":\"辽中县\"},{\"name\":\"康平县\"},{\"name\":\"新城子区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"大连\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"西岗区\"},{\"name\":\"中山区\"},{\"name\":\"沙河口区\"},{\"name\":\"甘井子区\"},{\"name\":\"旅顺口区\"},{\"name\":\"金州区\"},{\"name\":\"瓦房店市\"},{\"name\":\"普兰店市\"},{\"name\":\"庄河市\"},{\"name\":\"长海县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"鞍山\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"铁东区\"},{\"name\":\"铁西区\"},{\"name\":\"立山区\"},{\"name\":\"千山区\"},{\"name\":\"海城市\"},{\"name\":\"台安县\"},{\"name\":\"岫岩满族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"抚顺\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"顺城区\"},{\"name\":\"新抚区\"},{\"name\":\"东洲区\"},{\"name\":\"望花区\"},{\"name\":\"抚顺县\"},{\"name\":\"清原满族自治县\"},{\"name\":\"新宾满族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"本溪\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"平山区\"},{\"name\":\"明山区\"},{\"name\":\"溪湖区\"},{\"name\":\"南芬区\"},{\"name\":\"本溪满族自治县\"},{\"name\":\"桓仁满族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"丹东\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"振兴区\"},{\"name\":\"元宝区\"},{\"name\":\"振安区\"},{\"name\":\"东港市\"},{\"name\":\"凤城市\"},{\"name\":\"宽甸满族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"锦州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"太和区\"},{\"name\":\"古塔区\"},{\"name\":\"凌河区\"},{\"name\":\"凌海市\"},{\"name\":\"黑山县\"},{\"name\":\"义县\"},{\"name\":\"北宁市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"营口\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"站前区\"},{\"name\":\"西市区\"},{\"name\":\"鲅鱼圈区\"},{\"name\":\"老边区\"},{\"name\":\"大石桥市\"},{\"name\":\"盖州市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"阜新\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"海州区\"},{\"name\":\"新邱区\"},{\"name\":\"太平区\"},{\"name\":\"清河门区\"},{\"name\":\"细河区\"},{\"name\":\"彰武县\"},{\"name\":\"阜新蒙古族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"辽阳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"白塔区\"},{\"name\":\"文圣区\"},{\"name\":\"宏伟区\"},{\"name\":\"太子河区\"},{\"name\":\"弓长岭区\"},{\"name\":\"灯塔市\"},{\"name\":\"辽阳县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"盘锦\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"双台子区\"},{\"name\":\"兴隆台区\"},{\"name\":\"盘山县\"},{\"name\":\"大洼县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"铁岭\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"银州区\"},{\"name\":\"清河区\"},{\"name\":\"调兵山市\"},{\"name\":\"开原市\"},{\"name\":\"铁岭县\"},{\"name\":\"昌图县\"},{\"name\":\"西丰县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"朝阳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"双塔区\"},{\"name\":\"龙城区\"},{\"name\":\"凌源市\"},{\"name\":\"北票市\"},{\"name\":\"朝阳县\"},{\"name\":\"建平县\"},{\"name\":\"喀喇沁左翼蒙古族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"葫芦岛\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"龙港区\"},{\"name\":\"南票区\"},{\"name\":\"连山区\"},{\"name\":\"兴城市\"},{\"name\":\"绥中县\"},{\"name\":\"建昌县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"江苏\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"南京\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"玄武区\"},{\"name\":\"白下区\"},{\"name\":\"秦淮区\"},{\"name\":\"建邺区\"},{\"name\":\"鼓楼区\"},{\"name\":\"下关区\"},{\"name\":\"栖霞区\"},{\"name\":\"雨花台区\"},{\"name\":\"浦口区\"},{\"name\":\"江宁区\"},{\"name\":\"六合区\"},{\"name\":\"溧水县\"},{\"name\":\"高淳县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"苏州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"金阊区\"},{\"name\":\"平江区\"},{\"name\":\"沧浪区\"},{\"name\":\"虎丘区\"},{\"name\":\"吴中区\"},{\"name\":\"相城区\"},{\"name\":\"常熟市\"},{\"name\":\"张家港市\"},{\"name\":\"昆山市\"},{\"name\":\"吴江市\"},{\"name\":\"太仓市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"无锡\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"崇安区\"},{\"name\":\"南长区\"},{\"name\":\"北塘区\"},{\"name\":\"滨湖区\"},{\"name\":\"锡山区\"},{\"name\":\"惠山区\"},{\"name\":\"江阴市\"},{\"name\":\"宜兴市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"常州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"钟楼区\"},{\"name\":\"天宁区\"},{\"name\":\"戚墅堰区\"},{\"name\":\"新北区\"},{\"name\":\"武进区\"},{\"name\":\"金坛市\"},{\"name\":\"溧阳市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"镇江\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"京口区\"},{\"name\":\"润州区\"},{\"name\":\"丹徒区\"},{\"name\":\"丹阳市\"},{\"name\":\"扬中市\"},{\"name\":\"句容市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"南通\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"崇川区\"},{\"name\":\"港闸区\"},{\"name\":\"通州市\"},{\"name\":\"如皋市\"},{\"name\":\"海门市\"},{\"name\":\"启东市\"},{\"name\":\"海安县\"},{\"name\":\"如东县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"泰州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"海陵区\"},{\"name\":\"高港区\"},{\"name\":\"姜堰市\"},{\"name\":\"泰兴市\"},{\"name\":\"靖江市\"},{\"name\":\"兴化市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"扬州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"广陵区\"},{\"name\":\"维扬区\"},{\"name\":\"邗江区\"},{\"name\":\"江都市\"},{\"name\":\"仪征市\"},{\"name\":\"高邮市\"},{\"name\":\"宝应县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"盐城\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"亭湖区\"},{\"name\":\"盐都区\"},{\"name\":\"大丰市\"},{\"name\":\"东台市\"},{\"name\":\"建湖县\"},{\"name\":\"射阳县\"},{\"name\":\"阜宁县\"},{\"name\":\"滨海县\"},{\"name\":\"响水县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"连云港\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"新浦区\"},{\"name\":\"海州区\"},{\"name\":\"连云区\"},{\"name\":\"东海县\"},{\"name\":\"灌云县\"},{\"name\":\"赣榆县\"},{\"name\":\"灌南县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"徐州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"云龙区\"},{\"name\":\"鼓楼区\"},{\"name\":\"九里区\"},{\"name\":\"泉山区\"},{\"name\":\"贾汪区\"},{\"name\":\"邳州市\"},{\"name\":\"新沂市\"},{\"name\":\"铜山县\"},{\"name\":\"睢宁县\"},{\"name\":\"沛县\"},{\"name\":\"丰县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"淮安\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"清河区\"},{\"name\":\"清浦区\"},{\"name\":\"楚州区\"},{\"name\":\"淮阴区\"},{\"name\":\"涟水县\"},{\"name\":\"洪泽县\"},{\"name\":\"金湖县\"},{\"name\":\"盱眙县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"宿迁\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"宿城区\"},{\"name\":\"宿豫区\"},{\"name\":\"沭阳县\"},{\"name\":\"泗阳县\"},{\"name\":\"泗洪县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"湖北\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"武汉\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"江岸区\"},{\"name\":\"武昌区\"},{\"name\":\"江汉区\"},{\"name\":\"硚口区\"},{\"name\":\"汉阳区\"},{\"name\":\"青山区\"},{\"name\":\"洪山区\"},{\"name\":\"东西湖区\"},{\"name\":\"汉南区\"},{\"name\":\"蔡甸区\"},{\"name\":\"江夏区\"},{\"name\":\"黄陂区\"},{\"name\":\"新洲区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"黄石\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"黄石港区\"},{\"name\":\"西塞山区\"},{\"name\":\"下陆区\"},{\"name\":\"铁山区\"},{\"name\":\"大冶市\"},{\"name\":\"阳新县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"十堰\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"张湾区\"},{\"name\":\"茅箭区\"},{\"name\":\"丹江口市\"},{\"name\":\"郧县\"},{\"name\":\"竹山县\"},{\"name\":\"房县\"},{\"name\":\"郧西县\"},{\"name\":\"竹溪县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"荆州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"沙市区\"},{\"name\":\"荆州区\"},{\"name\":\"洪湖市\"},{\"name\":\"石首市\"},{\"name\":\"松滋市\"},{\"name\":\"监利县\"},{\"name\":\"公安县\"},{\"name\":\"江陵县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"宜昌\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"西陵区\"},{\"name\":\"伍家岗区\"},{\"name\":\"点军区\"},{\"name\":\"猇亭区\"},{\"name\":\"夷陵区\"},{\"name\":\"宜都市\"},{\"name\":\"当阳市\"},{\"name\":\"枝江市\"},{\"name\":\"秭归县\"},{\"name\":\"远安县\"},{\"name\":\"兴山县\"},{\"name\":\"五峰土家族自治县\"},{\"name\":\"长阳土家族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"襄樊\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"襄城区\"},{\"name\":\"樊城区\"},{\"name\":\"襄阳区\"},{\"name\":\"老河口市\"},{\"name\":\"枣阳市\"},{\"name\":\"宜城市\"},{\"name\":\"南漳县\"},{\"name\":\"谷城县\"},{\"name\":\"保康县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"鄂州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"鄂城区\"},{\"name\":\"华容区\"},{\"name\":\"梁子湖区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"荆门\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"东宝区\"},{\"name\":\"掇刀区\"},{\"name\":\"钟祥市\"},{\"name\":\"京山县\"},{\"name\":\"沙洋县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"孝感\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"孝南区\"},{\"name\":\"应城市\"},{\"name\":\"安陆市\"},{\"name\":\"汉川市\"},{\"name\":\"云梦县\"},{\"name\":\"大悟县\"},{\"name\":\"孝昌县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"黄冈\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"黄州区\"},{\"name\":\"麻城市\"},{\"name\":\"武穴市\"},{\"name\":\"红安县\"},{\"name\":\"罗田县\"},{\"name\":\"浠水县\"},{\"name\":\"蕲春县\"},{\"name\":\"黄梅县\"},{\"name\":\"英山县\"},{\"name\":\"团风县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"咸宁\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"咸安区\"},{\"name\":\"赤壁市\"},{\"name\":\"嘉鱼县\"},{\"name\":\"通山县\"},{\"name\":\"崇阳县\"},{\"name\":\"通城县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"随州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"曾都区\"},{\"name\":\"广水市\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"恩施土家族苗族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"恩施市\"},{\"name\":\"利川市\"},{\"name\":\"建始县\"},{\"name\":\"来凤县\"},{\"name\":\"巴东县\"},{\"name\":\"鹤峰县\"},{\"name\":\"宣恩县\"},{\"name\":\"咸丰县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"仙桃\",\"sub\":[],\"type\":0},{\"name\":\"天门\",\"sub\":[],\"type\":0},{\"name\":\"潜江\",\"sub\":[],\"type\":0},{\"name\":\"神农架林区\",\"sub\":[],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"四川\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"成都\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"青羊区\"},{\"name\":\"锦江区\"},{\"name\":\"金牛区\"},{\"name\":\"武侯区\"},{\"name\":\"成华区\"},{\"name\":\"龙泉驿区\"},{\"name\":\"青白江区\"},{\"name\":\"新都区\"},{\"name\":\"温江区\"},{\"name\":\"都江堰市\"},{\"name\":\"彭州市\"},{\"name\":\"邛崃市\"},{\"name\":\"崇州市\"},{\"name\":\"金堂县\"},{\"name\":\"郫县\"},{\"name\":\"新津县\"},{\"name\":\"双流县\"},{\"name\":\"蒲江县\"},{\"name\":\"大邑县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"自贡\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"大安区\"},{\"name\":\"自流井区\"},{\"name\":\"贡井区\"},{\"name\":\"沿滩区\"},{\"name\":\"荣县\"},{\"name\":\"富顺县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"攀枝花\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"仁和区\"},{\"name\":\"米易县\"},{\"name\":\"盐边县\"},{\"name\":\"东区\"},{\"name\":\"西区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"泸州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"江阳区\"},{\"name\":\"纳溪区\"},{\"name\":\"龙马潭区\"},{\"name\":\"泸县\"},{\"name\":\"合江县\"},{\"name\":\"叙永县\"},{\"name\":\"古蔺县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"德阳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"旌阳区\"},{\"name\":\"广汉市\"},{\"name\":\"什邡市\"},{\"name\":\"绵竹市\"},{\"name\":\"罗江县\"},{\"name\":\"中江县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"绵阳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"涪城区\"},{\"name\":\"游仙区\"},{\"name\":\"江油市\"},{\"name\":\"盐亭县\"},{\"name\":\"三台县\"},{\"name\":\"平武县\"},{\"name\":\"安县\"},{\"name\":\"梓潼县\"},{\"name\":\"北川羌族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"广元\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"元坝区\"},{\"name\":\"朝天区\"},{\"name\":\"青川县\"},{\"name\":\"旺苍县\"},{\"name\":\"剑阁县\"},{\"name\":\"苍溪县\"},{\"name\":\"市中区\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"遂宁\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"船山区\"},{\"name\":\"安居区\"},{\"name\":\"射洪县\"},{\"name\":\"蓬溪县\"},{\"name\":\"大英县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"内江\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"市中区\"},{\"name\":\"东兴区\"},{\"name\":\"资中县\"},{\"name\":\"隆昌县\"},{\"name\":\"威远县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"乐山\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"市中区\"},{\"name\":\"五通桥区\"},{\"name\":\"沙湾区\"},{\"name\":\"金口河区\"},{\"name\":\"峨眉山市\"},{\"name\":\"夹江县\"},{\"name\":\"井研县\"},{\"name\":\"犍为县\"},{\"name\":\"沐川县\"},{\"name\":\"马边彝族自治县\"},{\"name\":\"峨边彝族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"南充\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"顺庆区\"},{\"name\":\"高坪区\"},{\"name\":\"嘉陵区\"},{\"name\":\"阆中市\"},{\"name\":\"营山县\"},{\"name\":\"蓬安县\"},{\"name\":\"仪陇县\"},{\"name\":\"南部县\"},{\"name\":\"西充县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"眉山\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"东坡区\"},{\"name\":\"仁寿县\"},{\"name\":\"彭山县\"},{\"name\":\"洪雅县\"},{\"name\":\"丹棱县\"},{\"name\":\"青神县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"宜宾\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"翠屏区\"},{\"name\":\"宜宾县\"},{\"name\":\"兴文县\"},{\"name\":\"南溪县\"},{\"name\":\"珙县\"},{\"name\":\"长宁县\"},{\"name\":\"高县\"},{\"name\":\"江安县\"},{\"name\":\"筠连县\"},{\"name\":\"屏山县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"广安\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"广安区\"},{\"name\":\"华蓥市\"},{\"name\":\"岳池县\"},{\"name\":\"邻水县\"},{\"name\":\"武胜县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"达州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"通川区\"},{\"name\":\"万源市\"},{\"name\":\"达县\"},{\"name\":\"渠县\"},{\"name\":\"宣汉县\"},{\"name\":\"开江县\"},{\"name\":\"大竹县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"雅安\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"雨城区\"},{\"name\":\"芦山县\"},{\"name\":\"石棉县\"},{\"name\":\"名山县\"},{\"name\":\"天全县\"},{\"name\":\"荥经县\"},{\"name\":\"宝兴县\"},{\"name\":\"汉源县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"巴中\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"巴州区\"},{\"name\":\"南江县\"},{\"name\":\"平昌县\"},{\"name\":\"通江县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"资阳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"雁江区\"},{\"name\":\"简阳市\"},{\"name\":\"安岳县\"},{\"name\":\"乐至县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"阿坝藏族羌族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"马尔康县\"},{\"name\":\"九寨沟县\"},{\"name\":\"红原县\"},{\"name\":\"汶川县\"},{\"name\":\"阿坝县\"},{\"name\":\"理县\"},{\"name\":\"若尔盖县\"},{\"name\":\"小金县\"},{\"name\":\"黑水县\"},{\"name\":\"金川县\"},{\"name\":\"松潘县\"},{\"name\":\"壤塘县\"},{\"name\":\"茂县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"甘孜藏族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"康定县\"},{\"name\":\"丹巴县\"},{\"name\":\"炉霍县\"},{\"name\":\"九龙县\"},{\"name\":\"甘孜县\"},{\"name\":\"雅江县\"},{\"name\":\"新龙县\"},{\"name\":\"道孚县\"},{\"name\":\"白玉县\"},{\"name\":\"理塘县\"},{\"name\":\"德格县\"},{\"name\":\"乡城县\"},{\"name\":\"石渠县\"},{\"name\":\"稻城县\"},{\"name\":\"色达县\"},{\"name\":\"巴塘县\"},{\"name\":\"泸定县\"},{\"name\":\"得荣县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"凉山彝族自治州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"西昌市\"},{\"name\":\"美姑县\"},{\"name\":\"昭觉县\"},{\"name\":\"金阳县\"},{\"name\":\"甘洛县\"},{\"name\":\"布拖县\"},{\"name\":\"雷波县\"},{\"name\":\"普格县\"},{\"name\":\"宁南县\"},{\"name\":\"喜德县\"},{\"name\":\"会东县\"},{\"name\":\"越西县\"},{\"name\":\"会理县\"},{\"name\":\"盐源县\"},{\"name\":\"德昌县\"},{\"name\":\"冕宁县\"},{\"name\":\"木里藏族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"陕西\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"西安\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"莲湖区\"},{\"name\":\"新城区\"},{\"name\":\"碑林区\"},{\"name\":\"雁塔区\"},{\"name\":\"灞桥区\"},{\"name\":\"未央区\"},{\"name\":\"阎良区\"},{\"name\":\"临潼区\"},{\"name\":\"长安区\"},{\"name\":\"高陵县\"},{\"name\":\"蓝田县\"},{\"name\":\"户县\"},{\"name\":\"周至县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"铜川\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"耀州区\"},{\"name\":\"王益区\"},{\"name\":\"印台区\"},{\"name\":\"宜君县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"宝鸡\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"渭滨区\"},{\"name\":\"金台区\"},{\"name\":\"陈仓区\"},{\"name\":\"岐山县\"},{\"name\":\"凤翔县\"},{\"name\":\"陇县\"},{\"name\":\"太白县\"},{\"name\":\"麟游县\"},{\"name\":\"扶风县\"},{\"name\":\"千阳县\"},{\"name\":\"眉县\"},{\"name\":\"凤县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"咸阳\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"秦都区\"},{\"name\":\"渭城区\"},{\"name\":\"杨陵区\"},{\"name\":\"兴平市\"},{\"name\":\"礼泉县\"},{\"name\":\"泾阳县\"},{\"name\":\"永寿县\"},{\"name\":\"三原县\"},{\"name\":\"彬县\"},{\"name\":\"旬邑县\"},{\"name\":\"长武县\"},{\"name\":\"乾县\"},{\"name\":\"武功县\"},{\"name\":\"淳化县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"渭南\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"临渭区\"},{\"name\":\"韩城市\"},{\"name\":\"华阴市\"},{\"name\":\"蒲城县\"},{\"name\":\"潼关县\"},{\"name\":\"白水县\"},{\"name\":\"澄城县\"},{\"name\":\"华县\"},{\"name\":\"合阳县\"},{\"name\":\"富平县\"},{\"name\":\"大荔县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"延安\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"宝塔区\"},{\"name\":\"安塞县\"},{\"name\":\"洛川县\"},{\"name\":\"子长县\"},{\"name\":\"黄陵县\"},{\"name\":\"延川县\"},{\"name\":\"富县\"},{\"name\":\"延长县\"},{\"name\":\"甘泉县\"},{\"name\":\"宜川县\"},{\"name\":\"志丹县\"},{\"name\":\"黄龙县\"},{\"name\":\"吴起县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"汉中\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"汉台区\"},{\"name\":\"留坝县\"},{\"name\":\"镇巴县\"},{\"name\":\"城固县\"},{\"name\":\"南郑县\"},{\"name\":\"洋县\"},{\"name\":\"宁强县\"},{\"name\":\"佛坪县\"},{\"name\":\"勉县\"},{\"name\":\"西乡县\"},{\"name\":\"略阳县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"榆林\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"榆阳区\"},{\"name\":\"清涧县\"},{\"name\":\"绥德县\"},{\"name\":\"神木县\"},{\"name\":\"佳县\"},{\"name\":\"府谷县\"},{\"name\":\"子洲县\"},{\"name\":\"靖边县\"},{\"name\":\"横山县\"},{\"name\":\"米脂县\"},{\"name\":\"吴堡县\"},{\"name\":\"定边县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"安康\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"汉滨区\"},{\"name\":\"紫阳县\"},{\"name\":\"岚皋县\"},{\"name\":\"旬阳县\"},{\"name\":\"镇坪县\"},{\"name\":\"平利县\"},{\"name\":\"石泉县\"},{\"name\":\"宁陕县\"},{\"name\":\"白河县\"},{\"name\":\"汉阴县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"商洛\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"商州区\"},{\"name\":\"镇安县\"},{\"name\":\"山阳县\"},{\"name\":\"洛南县\"},{\"name\":\"商南县\"},{\"name\":\"丹凤县\"},{\"name\":\"柞水县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"河北\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"石家庄\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"长安区\"},{\"name\":\"桥东区\"},{\"name\":\"桥西区\"},{\"name\":\"新华区\"},{\"name\":\"裕华区\"},{\"name\":\"井陉矿区\"},{\"name\":\"鹿泉市\"},{\"name\":\"辛集市\"},{\"name\":\"藁城市\"},{\"name\":\"晋州市\"},{\"name\":\"新乐市\"},{\"name\":\"深泽县\"},{\"name\":\"无极县\"},{\"name\":\"赵县\"},{\"name\":\"灵寿县\"},{\"name\":\"高邑县\"},{\"name\":\"元氏县\"},{\"name\":\"赞皇县\"},{\"name\":\"平山县\"},{\"name\":\"井陉县\"},{\"name\":\"栾城县\"},{\"name\":\"正定县\"},{\"name\":\"行唐县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"唐山\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"路北区\"},{\"name\":\"路南区\"},{\"name\":\"古冶区\"},{\"name\":\"开平区\"},{\"name\":\"丰南区\"},{\"name\":\"丰润区\"},{\"name\":\"遵化市\"},{\"name\":\"迁安市\"},{\"name\":\"迁西县\"},{\"name\":\"滦南县\"},{\"name\":\"玉田县\"},{\"name\":\"唐海县\"},{\"name\":\"乐亭县\"},{\"name\":\"滦县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"秦皇岛\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"海港区\"},{\"name\":\"山海关区\"},{\"name\":\"北戴河区\"},{\"name\":\"昌黎县\"},{\"name\":\"抚宁县\"},{\"name\":\"卢龙县\"},{\"name\":\"青龙满族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"邯郸\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"邯山区\"},{\"name\":\"丛台区\"},{\"name\":\"复兴区\"},{\"name\":\"峰峰矿区\"},{\"name\":\"武安市\"},{\"name\":\"邱县\"},{\"name\":\"大名县\"},{\"name\":\"魏县\"},{\"name\":\"曲周县\"},{\"name\":\"鸡泽县\"},{\"name\":\"肥乡县\"},{\"name\":\"广平县\"},{\"name\":\"成安县\"},{\"name\":\"临漳县\"},{\"name\":\"磁县\"},{\"name\":\"涉县\"},{\"name\":\"永年县\"},{\"name\":\"馆陶县\"},{\"name\":\"邯郸县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"邢台\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"桥东区\"},{\"name\":\"桥西区\"},{\"name\":\"南宫市\"},{\"name\":\"沙河市\"},{\"name\":\"临城县\"},{\"name\":\"内丘县\"},{\"name\":\"柏乡县\"},{\"name\":\"隆尧县\"},{\"name\":\"任县\"},{\"name\":\"南和县\"},{\"name\":\"宁晋县\"},{\"name\":\"巨鹿县\"},{\"name\":\"新河县\"},{\"name\":\"广宗县\"},{\"name\":\"平乡县\"},{\"name\":\"威县\"},{\"name\":\"清河县\"},{\"name\":\"临西县\"},{\"name\":\"邢台县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"保定\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"新市区\"},{\"name\":\"北市区\"},{\"name\":\"南市区\"},{\"name\":\"定州市\"},{\"name\":\"涿州市\"},{\"name\":\"安国市\"},{\"name\":\"高碑店市\"},{\"name\":\"易县\"},{\"name\":\"徐水县\"},{\"name\":\"涞源县\"},{\"name\":\"顺平县\"},{\"name\":\"唐县\"},{\"name\":\"望都县\"},{\"name\":\"涞水县\"},{\"name\":\"高阳县\"},{\"name\":\"安新县\"},{\"name\":\"雄县\"},{\"name\":\"容城县\"},{\"name\":\"蠡县\"},{\"name\":\"曲阳县\"},{\"name\":\"阜平县\"},{\"name\":\"博野县\"},{\"name\":\"满城县\"},{\"name\":\"清苑县\"},{\"name\":\"定兴县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"张家口\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"桥东区\"},{\"name\":\"桥西区\"},{\"name\":\"宣化区\"},{\"name\":\"下花园区\"},{\"name\":\"张北县\"},{\"name\":\"康保县\"},{\"name\":\"沽源县\"},{\"name\":\"尚义县\"},{\"name\":\"蔚县\"},{\"name\":\"阳原县\"},{\"name\":\"怀安县\"},{\"name\":\"万全县\"},{\"name\":\"怀来县\"},{\"name\":\"赤城县\"},{\"name\":\"崇礼县\"},{\"name\":\"宣化县\"},{\"name\":\"涿鹿县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"承德\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"双桥区\"},{\"name\":\"双滦区\"},{\"name\":\"鹰手营子矿区\"},{\"name\":\"兴隆县\"},{\"name\":\"平泉县\"},{\"name\":\"滦平县\"},{\"name\":\"隆化县\"},{\"name\":\"承德县\"},{\"name\":\"丰宁满族自治县\"},{\"name\":\"宽城满族自治县\"},{\"name\":\"围场满族蒙古族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"沧州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"新华区\"},{\"name\":\"运河区\"},{\"name\":\"泊头市\"},{\"name\":\"任丘市\"},{\"name\":\"黄骅市\"},{\"name\":\"河间市\"},{\"name\":\"献县\"},{\"name\":\"吴桥县\"},{\"name\":\"沧县\"},{\"name\":\"东光县\"},{\"name\":\"肃宁县\"},{\"name\":\"南皮县\"},{\"name\":\"盐山县\"},{\"name\":\"青县\"},{\"name\":\"海兴县\"},{\"name\":\"孟村回族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"廊坊\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"安次区\"},{\"name\":\"广阳区\"},{\"name\":\"霸州市\"},{\"name\":\"三河市\"},{\"name\":\"香河县\"},{\"name\":\"永清县\"},{\"name\":\"固安县\"},{\"name\":\"文安县\"},{\"name\":\"大城县\"},{\"name\":\"大厂回族自治县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"衡水\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"桃城区\"},{\"name\":\"冀州市\"},{\"name\":\"深州市\"},{\"name\":\"枣强县\"},{\"name\":\"武邑县\"},{\"name\":\"武强县\"},{\"name\":\"饶阳县\"},{\"name\":\"安平县\"},{\"name\":\"故城县\"},{\"name\":\"景县\"},{\"name\":\"阜城县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1},{\"name\":\"山西\",\"sub\":[{\"name\":\"请选择\",\"sub\":[]},{\"name\":\"太原\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"杏花岭区\"},{\"name\":\"小店区\"},{\"name\":\"迎泽区\"},{\"name\":\"尖草坪区\"},{\"name\":\"万柏林区\"},{\"name\":\"晋源区\"},{\"name\":\"古交市\"},{\"name\":\"阳曲县\"},{\"name\":\"清徐县\"},{\"name\":\"娄烦县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"大同\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"城区\"},{\"name\":\"矿区\"},{\"name\":\"南郊区\"},{\"name\":\"新荣区\"},{\"name\":\"大同县\"},{\"name\":\"天镇县\"},{\"name\":\"灵丘县\"},{\"name\":\"阳高县\"},{\"name\":\"左云县\"},{\"name\":\"广灵县\"},{\"name\":\"浑源县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"阳泉\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"城区\"},{\"name\":\"矿区\"},{\"name\":\"郊区\"},{\"name\":\"平定县\"},{\"name\":\"盂县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"长治\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"城区\"},{\"name\":\"郊区\"},{\"name\":\"潞城市\"},{\"name\":\"长治县\"},{\"name\":\"长子县\"},{\"name\":\"平顺县\"},{\"name\":\"襄垣县\"},{\"name\":\"沁源县\"},{\"name\":\"屯留县\"},{\"name\":\"黎城县\"},{\"name\":\"武乡县\"},{\"name\":\"沁县\"},{\"name\":\"壶关县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"晋城\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"城区\"},{\"name\":\"高平市\"},{\"name\":\"泽州县\"},{\"name\":\"陵川县\"},{\"name\":\"阳城县\"},{\"name\":\"沁水县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"朔州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"朔城区\"},{\"name\":\"平鲁区\"},{\"name\":\"山阴县\"},{\"name\":\"右玉县\"},{\"name\":\"应县\"},{\"name\":\"怀仁县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"晋中\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"榆次区\"},{\"name\":\"介休市\"},{\"name\":\"昔阳县\"},{\"name\":\"灵石县\"},{\"name\":\"祁县\"},{\"name\":\"左权县\"},{\"name\":\"寿阳县\"},{\"name\":\"太谷县\"},{\"name\":\"和顺县\"},{\"name\":\"平遥县\"},{\"name\":\"榆社县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"运城\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"盐湖区\"},{\"name\":\"河津市\"},{\"name\":\"永济市\"},{\"name\":\"闻喜县\"},{\"name\":\"新绛县\"},{\"name\":\"平陆县\"},{\"name\":\"垣曲县\"},{\"name\":\"绛县\"},{\"name\":\"稷山县\"},{\"name\":\"芮城县\"},{\"name\":\"夏县\"},{\"name\":\"万荣县\"},{\"name\":\"临猗县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"忻州\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"忻府区\"},{\"name\":\"原平市\"},{\"name\":\"代县\"},{\"name\":\"神池县\"},{\"name\":\"五寨县\"},{\"name\":\"五台县\"},{\"name\":\"偏关县\"},{\"name\":\"宁武县\"},{\"name\":\"静乐县\"},{\"name\":\"繁峙县\"},{\"name\":\"河曲县\"},{\"name\":\"保德县\"},{\"name\":\"定襄县\"},{\"name\":\"岢岚县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"临汾\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"尧都区\"},{\"name\":\"侯马市\"},{\"name\":\"霍州市\"},{\"name\":\"汾西县\"},{\"name\":\"吉县\"},{\"name\":\"安泽县\"},{\"name\":\"大宁县\"},{\"name\":\"浮山县\"},{\"name\":\"古县\"},{\"name\":\"隰县\"},{\"name\":\"襄汾县\"},{\"name\":\"翼城县\"},{\"name\":\"永和县\"},{\"name\":\"乡宁县\"},{\"name\":\"曲沃县\"},{\"name\":\"洪洞县\"},{\"name\":\"蒲县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"吕梁\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"离石区\"},{\"name\":\"孝义市\"},{\"name\":\"汾阳市\"},{\"name\":\"文水县\"},{\"name\":\"中阳县\"},{\"name\":\"兴县\"},{\"name\":\"临县\"},{\"name\":\"方山县\"},{\"name\":\"柳林县\"},{\"name\":\"岚县\"},{\"name\":\"交口县\"},{\"name\":\"交城县\"},{\"name\":\"石楼县\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}],\"type\":1}{\"name\":\"海外\",\"sub\":[{\"name\":\"请选择\"},{\"name\":\"其他\"}],\"type\":0},{\"name\":\"其他\"}]";
	}
}
