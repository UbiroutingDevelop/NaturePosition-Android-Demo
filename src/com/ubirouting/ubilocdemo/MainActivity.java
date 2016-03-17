package com.ubirouting.ubilocdemo;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.shitu.core.nature.newlib.Position;
import com.shitu.core.nature.newlib.ShituAllFloors;
import com.shitu.core.nature.newlib.ShituLocationListener;
import com.shitu.core.nature.newlib.ShituLocationLoader;
import com.shitu.core.nature.newlib.ShituLocationManager;
import com.shitu.core.nature.newlib.ShituLocationParameters;
import com.ubirouting.datalib.entity.Floor;
import com.ubirouting.datalib.entity.Place;
import com.ubirouting.datalib.manager.ShituPlaceDataManager;
import com.ubirouting.datalib.manager.ShituPlaceDataManager.onReturnDataListener;


public class MainActivity extends Activity implements ShituLocationListener{

	private ShituPlaceDataManager dataM;
	private Place currentPlace;
	
	private ShituLocationManager natureLocation;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //在使用定位前调用 一次运行需调用1次
        ShituLocationLoader.getInstance(this);
        
        dataM=new ShituPlaceDataManager(this);
        dataM.getData(new onReturnDataListener() {
			
			@Override
			public void onDataReturn(List<Place> arg0) {
				for (Place place : arg0) {
					//可以获取到您之前添加的信息
					Log.d(LOGD_TEXT, "id:"+place.getPlaceId()+",name:"+place.getPlaceName());
				}
				
				//定位需要确定一个准确建筑
				if(arg0!=null&&arg0.size()>0){
					currentPlace=arg0.get(11);
				}
				
				//初始化定位
				handler.sendEmptyMessage(0);
			}
		});
    }
    
    Handler handler=new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		initWifiLocation();
    	};
    };

    private void initWifiLocation(){
		Log.d(LOGD_TEXT, "initWifiLocation==========================");
		ShituAllFloors allFloors = new ShituAllFloors();
		for (Floor floor : currentPlace.getFloorList()) {
			
//			BitmapFactory.Options options = new BitmapFactory.Options();  
//			options.inJustDecodeBounds = true;  /* 这里使返回的bmp是null 不生成真的bitmap,防止内存溢出*/  
//			BitmapFactory.decodeFile(floor.getFloorPicPath(), options);
			
//			int height=options.outHeight;
//			int width=options.outWidth;
			
			//定位需要每一层的像素高宽 可使用上面代码通过 图片来获取
			//此demo 并未集成 下载图片
			int height=500;
			int width=500;
			
			allFloors.appendFloor(floor.getFloorNum(), width,
					height, floor.getScale(),
					(int) floor.getAngle());
		}
		
		ShituLocationParameters parameters = new ShituLocationParameters(
				//所定位的建筑ID
				currentPlace.getPlaceId(),
				//默认楼层(demo中直接取 0 作为默认楼层)
				currentPlace.getFloorList().get(0).getFloorNum(), 
				//allFloor对象 需要在其中 构造 :层num,图片像素宽,图片像素高,scale,angle
				allFloors,
				//定位模式
				ShituLocationManager.LOC_MAG_WIFI);
		parameters.enableLight();

		natureLocation = ShituLocationManager.newManager(this, parameters);
		natureLocation.setOnLocationListener(this);
		//到了这一步 可能并不会定出位置
		//这受限于您的 所用来申请 Key的账号下 是否存在建筑
		//如果对demo 不做 更改 默认取您建筑列表中的第一个建筑的第一层用来定位
		//如果您这层中 未上传数据同样可能无法定出位置(回调getLocation)
		if(natureLocation!=null){
			natureLocation.onResume();
			natureLocation.start();
		}
	}
    
    
    @Override
	protected void onResume() {
		super.onResume();
		if(natureLocation!=null){
			Log.d(LOGD_TEXT, "natureLocation:"+natureLocation);
			natureLocation.onResume();
			natureLocation.start();
		}
	}
    
	@Override
	public void onGetAngle(int angle) {
		Log.d(LOGD_TEXT, "angle:"+angle);
	}

	@Override
	public void onGetLocation(Position position) {
		Log.d(LOGD_TEXT, "position:"+position.toString());
	}

	@Override
	public void onGetStatus(int status) {
		switch (status) {
		case ShituLocationListener.STATUS_FIRST_RELIABLE_LOCATE:
			Toast.makeText(this, "开始定位", Toast.LENGTH_LONG).show();
			break;
		case ShituLocationListener.STATUS_RECONNECT_SUCCESS:
			Toast.makeText(this, "重新连接服务器成功", Toast.LENGTH_LONG).show();
			break;
		}		
	}
	
	@Override
	public void onException(String description, int errorType) {
		switch (errorType) {
		case ShituLocationListener.ERROR_FAILED_CONNECTING_TO_SERVER:
			// 第一次都没连上
			Toast.makeText(this, "初始化连接失败", Toast.LENGTH_LONG).show();
			break;
		case ShituLocationListener.ERROR_FAILED_TO_GET_POSITION:
			// 定过过程中断开
			Toast.makeText(this, "连接服务器断开", Toast.LENGTH_LONG).show();
			break;
		case ShituLocationListener.ERROR_LACKOF_KEY_SENSORS:
			// 缺少关键传感器
			Toast.makeText(this, "缺少关键传感器", Toast.LENGTH_LONG).show();
			break;
		default:
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		natureLocation.onDestroy();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		natureLocation.onPause();
	}
	
	private final String LOGD_TEXT="ubiloclib";
    
}
