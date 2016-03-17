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
        
        //��ʹ�ö�λǰ���� һ�����������1��
        ShituLocationLoader.getInstance(this);
        
        dataM=new ShituPlaceDataManager(this);
        dataM.getData(new onReturnDataListener() {
			
			@Override
			public void onDataReturn(List<Place> arg0) {
				for (Place place : arg0) {
					//���Ի�ȡ����֮ǰ��ӵ���Ϣ
					Log.d(LOGD_TEXT, "id:"+place.getPlaceId()+",name:"+place.getPlaceName());
				}
				
				//��λ��Ҫȷ��һ��׼ȷ����
				if(arg0!=null&&arg0.size()>0){
					currentPlace=arg0.get(11);
				}
				
				//��ʼ����λ
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
//			options.inJustDecodeBounds = true;  /* ����ʹ���ص�bmp��null ���������bitmap,��ֹ�ڴ����*/  
//			BitmapFactory.decodeFile(floor.getFloorPicPath(), options);
			
//			int height=options.outHeight;
//			int width=options.outWidth;
			
			//��λ��Ҫÿһ������ظ߿� ��ʹ���������ͨ�� ͼƬ����ȡ
			//��demo ��δ���� ����ͼƬ
			int height=500;
			int width=500;
			
			allFloors.appendFloor(floor.getFloorNum(), width,
					height, floor.getScale(),
					(int) floor.getAngle());
		}
		
		ShituLocationParameters parameters = new ShituLocationParameters(
				//����λ�Ľ���ID
				currentPlace.getPlaceId(),
				//Ĭ��¥��(demo��ֱ��ȡ 0 ��ΪĬ��¥��)
				currentPlace.getFloorList().get(0).getFloorNum(), 
				//allFloor���� ��Ҫ������ ���� :��num,ͼƬ���ؿ�,ͼƬ���ظ�,scale,angle
				allFloors,
				//��λģʽ
				ShituLocationManager.LOC_MAG_WIFI);
		parameters.enableLight();

		natureLocation = ShituLocationManager.newManager(this, parameters);
		natureLocation.setOnLocationListener(this);
		//������һ�� ���ܲ����ᶨ��λ��
		//������������ ���������� Key���˺��� �Ƿ���ڽ���
		//�����demo ���� ���� Ĭ��ȡ�������б��еĵ�һ�������ĵ�һ��������λ
		//���������� δ�ϴ�����ͬ�������޷�����λ��(�ص�getLocation)
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
			Toast.makeText(this, "��ʼ��λ", Toast.LENGTH_LONG).show();
			break;
		case ShituLocationListener.STATUS_RECONNECT_SUCCESS:
			Toast.makeText(this, "�������ӷ������ɹ�", Toast.LENGTH_LONG).show();
			break;
		}		
	}
	
	@Override
	public void onException(String description, int errorType) {
		switch (errorType) {
		case ShituLocationListener.ERROR_FAILED_CONNECTING_TO_SERVER:
			// ��һ�ζ�û����
			Toast.makeText(this, "��ʼ������ʧ��", Toast.LENGTH_LONG).show();
			break;
		case ShituLocationListener.ERROR_FAILED_TO_GET_POSITION:
			// ���������жϿ�
			Toast.makeText(this, "���ӷ������Ͽ�", Toast.LENGTH_LONG).show();
			break;
		case ShituLocationListener.ERROR_LACKOF_KEY_SENSORS:
			// ȱ�ٹؼ�������
			Toast.makeText(this, "ȱ�ٹؼ�������", Toast.LENGTH_LONG).show();
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
