package com.ford.cvs.caq.client;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.ford.cvs.caq.client.data.VehicleDataListener;
import com.ford.cvs.caq.client.layout.SdlAQILayout;
import com.ford.cvs.caq.client.layout.SdlAQILayoutCrystal;
import com.ford.cvs.caq.client.layout.SdlAQILayoutDemo1;
import com.ford.cvs.caq.client.layout.SdlAQILayoutMeter;
import com.smartdevicelink.exception.SdlException;
import com.smartdevicelink.proxy.callbacks.OnServiceEnded;
import com.smartdevicelink.proxy.callbacks.OnServiceNACKed;
import com.smartdevicelink.proxy.ex.IProxyListenerALMEx;
import com.smartdevicelink.proxy.ex.Operator.ShowOp;
import com.smartdevicelink.proxy.ex.SdlProxyEx;
import com.smartdevicelink.proxy.rpc.AddCommandResponse;
import com.smartdevicelink.proxy.rpc.AddSubMenuResponse;
import com.smartdevicelink.proxy.rpc.AlertManeuverResponse;
import com.smartdevicelink.proxy.rpc.AlertResponse;
import com.smartdevicelink.proxy.rpc.ChangeRegistrationResponse;
import com.smartdevicelink.proxy.rpc.CreateInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteCommandResponse;
import com.smartdevicelink.proxy.rpc.DeleteFileResponse;
import com.smartdevicelink.proxy.rpc.DeleteInteractionChoiceSetResponse;
import com.smartdevicelink.proxy.rpc.DeleteSubMenuResponse;
import com.smartdevicelink.proxy.rpc.DiagnosticMessageResponse;
import com.smartdevicelink.proxy.rpc.DialNumberResponse;
import com.smartdevicelink.proxy.rpc.EndAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.GenericResponse;
import com.smartdevicelink.proxy.rpc.GetDTCsResponse;
import com.smartdevicelink.proxy.rpc.GetVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.GetWayPointsResponse;
import com.smartdevicelink.proxy.rpc.ListFilesResponse;
import com.smartdevicelink.proxy.rpc.OnAudioPassThru;
import com.smartdevicelink.proxy.rpc.OnButtonEvent;
import com.smartdevicelink.proxy.rpc.OnButtonPress;
import com.smartdevicelink.proxy.rpc.OnCommand;
import com.smartdevicelink.proxy.rpc.OnDriverDistraction;
import com.smartdevicelink.proxy.rpc.OnHMIStatus;
import com.smartdevicelink.proxy.rpc.OnHashChange;
import com.smartdevicelink.proxy.rpc.OnKeyboardInput;
import com.smartdevicelink.proxy.rpc.OnLanguageChange;
import com.smartdevicelink.proxy.rpc.OnLockScreenStatus;
import com.smartdevicelink.proxy.rpc.OnPermissionsChange;
import com.smartdevicelink.proxy.rpc.OnStreamRPC;
import com.smartdevicelink.proxy.rpc.OnSystemRequest;
import com.smartdevicelink.proxy.rpc.OnTBTClientState;
import com.smartdevicelink.proxy.rpc.OnTouchEvent;
import com.smartdevicelink.proxy.rpc.OnVehicleData;
import com.smartdevicelink.proxy.rpc.OnWayPointChange;
import com.smartdevicelink.proxy.rpc.PerformAudioPassThruResponse;
import com.smartdevicelink.proxy.rpc.PerformInteractionResponse;
import com.smartdevicelink.proxy.rpc.PutFileResponse;
import com.smartdevicelink.proxy.rpc.ReadDIDResponse;
import com.smartdevicelink.proxy.rpc.ResetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.ScrollableMessageResponse;
import com.smartdevicelink.proxy.rpc.SendLocationResponse;
import com.smartdevicelink.proxy.rpc.SetAppIconResponse;
import com.smartdevicelink.proxy.rpc.SetDisplayLayoutResponse;
import com.smartdevicelink.proxy.rpc.SetGlobalPropertiesResponse;
import com.smartdevicelink.proxy.rpc.SetMediaClockTimerResponse;
import com.smartdevicelink.proxy.rpc.ShowConstantTbtResponse;
import com.smartdevicelink.proxy.rpc.ShowResponse;
import com.smartdevicelink.proxy.rpc.SliderResponse;
import com.smartdevicelink.proxy.rpc.SoftButton;
import com.smartdevicelink.proxy.rpc.SpeakResponse;
import com.smartdevicelink.proxy.rpc.StreamRPCResponse;
import com.smartdevicelink.proxy.rpc.SubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.SubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.SubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.SystemRequestResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeButtonResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeVehicleDataResponse;
import com.smartdevicelink.proxy.rpc.UnsubscribeWayPointsResponse;
import com.smartdevicelink.proxy.rpc.UpdateTurnListResponse;
import com.smartdevicelink.proxy.rpc.enums.FileType;
import com.smartdevicelink.proxy.rpc.enums.HMILevel;
import com.smartdevicelink.proxy.rpc.enums.SdlDisconnectedReason;
import com.smartdevicelink.proxy.rpc.enums.SoftButtonType;
import com.smartdevicelink.proxy.rpc.enums.SystemAction;
import com.smartdevicelink.transport.BTTransportConfig;

import org.json.JSONException;

import java.util.Arrays;
import java.util.Vector;




public class SdlProxyService extends Service implements IProxyListenerALMEx
{
	private static final int CMD_ID_SWITCH = 100;
	private static final String[] CMD_NAME_SWITCH = {"切换"};
	private SdlProxyEx mProxy = null;
	private int mCorrelationID = 1000;

	private static final int ID_BTN_YES = 101;
	private static final int ID_BTN_NO = 102;
	private static final int COID_ALERT_CLOSE_WINDOW = 501;
	private boolean mAlertIsShowing = false;
	private boolean mIsRefreshing = false;
	private VehicleDataListener vehicleDataListener;
	private SdlAQILayout mLayout;


	public SdlProxyService()
	{

	}


	private void startProxy()
	{
		if (mProxy == null)
		{
			try
			{
				BTTransportConfig config = new BTTransportConfig();
				mProxy = new SdlProxyEx(this, getString(R.string.app_name), false, getString(R.string.applink_id),
										this, R.drawable.appicon2, FileType.GRAPHIC_PNG, config);
			}
			catch (SdlException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private void disposeProxy()
	{
		if (mProxy != null)
		{
			try
			{
				mProxy.dispose();
				mProxy = null;
				if (this.vehicleDataListener != null) {
					this.vehicleDataListener.forceClose();
				}
				setVehicleDataListener(null);
			}
			catch (SdlException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}


		}
	}


	public void refreshAir()
	{
		if (mIsRefreshing)
			return;
		mIsRefreshing = true;
	}

	public void setVehicleDataListener(VehicleDataListener listener) {
		this.vehicleDataListener = listener;
	}

	public class ServiceBinder extends Binder
	{
		public void updateAQI(int innerAQI, int outerAQI)
		{
			SdlProxyService.this.updateAQI(innerAQI, outerAQI);
		}

		public void updateWeather(SdlAQILayoutMeter.WeatherInfo weatherInfo)
		{
			SdlProxyService.this.updateWeather(weatherInfo);
		}

		public void update(int innerAQI, int outerAQI, SdlAQILayoutMeter.WeatherInfo weatherInfo)
		{
			SdlProxyService.this.update(innerAQI, outerAQI, weatherInfo);
		}

		public void AQIInnerAlert(boolean openFilter) { SdlProxyService.this.AQIInnerAlert(openFilter); }
		public void AQIInnerPurifiedAlert() { SdlProxyService.this.AQIInnerPurifiedAlert(); }
		public boolean ready() { return SdlProxyService.this.ready(); }
	}



	// 显示主屏幕
	private void showMainScreen()
	{
		try
		{
//			mProxy.setdisplaylayout("TILES_ONLY", mCorrelationID++);
			mProxy.setdisplaylayout("LARGE_GRAPHIC_ONLY", mCorrelationID++);

		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}


	}







	private boolean ready()
	{
		return mProxy != null && mProxy.getIsConnected();
	}

	private void updateWeather(SdlAQILayoutMeter.WeatherInfo weatherInfo)
	{
		try
		{
			mProxy.showImg(mLayout.updateWeather(weatherInfo), FileType.GRAPHIC_PNG, ShowOp.ShowType.IMAGE_MAIN, ++mCorrelationID);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void updateAQI(int innerAQI, int outerAQI)
	{
		try
		{
			mProxy.showImg(mLayout.updateAQI(innerAQI, outerAQI), FileType.GRAPHIC_PNG, ShowOp.ShowType.IMAGE_MAIN, ++mCorrelationID);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void update(int innerAQI, int outerAQI, SdlAQILayoutMeter.WeatherInfo weatherInfo)
	{
		try
		{
			mProxy.showImg(mLayout.update(innerAQI, outerAQI, weatherInfo), FileType.GRAPHIC_PNG, ShowOp.ShowType.IMAGE_MAIN, ++mCorrelationID);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void AQIInnerAlert(boolean openFilter)
	{
		if (mAlertIsShowing)
			return;

		mAlertIsShowing = true;

		SoftButton sb1 = new SoftButton();
		sb1.setText("Yes");
		sb1.setSoftButtonID(ID_BTN_YES);
		sb1.setType(SoftButtonType.SBT_TEXT);
		sb1.setSystemAction(SystemAction.DEFAULT_ACTION);

		SoftButton sb2 = new SoftButton();
		sb2.setText("No");
		sb2.setSoftButtonID(ID_BTN_NO);
		sb2.setType(SoftButtonType.SBT_TEXT);
		sb2.setSystemAction(SystemAction.DEFAULT_ACTION);

		Vector<SoftButton> btns = new Vector<>();
		btns.add(sb1);
//		btns.add(sb2);

		String txt;
		if (openFilter)
			txt = "车内空气较差，是否开启空调空滤器？";
		else
			txt = "车内空气较差，请开启车窗或开启外循环。是否开启外循环？";

		try
		{
		 	mProxy.alert(txt, txt, null, null, true, 5000, btns, COID_ALERT_CLOSE_WINDOW);
		}
		catch (SdlException e)
		{
			e.printStackTrace();
		}
	}

	private void AQIInnerPurifiedAlert()
	{
		if (mAlertIsShowing)
			return;

		mAlertIsShowing = true;
		mIsRefreshing = false;

		String txt = "车内空气已净化！";

		SoftButton sb1 = new SoftButton();
		sb1.setText("OK");
		sb1.setSoftButtonID(ID_BTN_NO);
		sb1.setType(SoftButtonType.SBT_TEXT);
		sb1.setSystemAction(SystemAction.DEFAULT_ACTION);

		Vector<SoftButton> btns = new Vector<>();
		btns.add(sb1);

		try
		{
			mProxy.alert(txt, txt, null, null, true, 5000, btns, COID_ALERT_CLOSE_WINDOW);

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}


	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onCreate()
	{
		startProxy();

		mLayout = SdlAQILayoutCrystal.getInstance(this);
//		mLayout = SdlAQILayoutDemo1.getInstance(this);

		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		disposeProxy();
		super.onDestroy();
	}


	@Override
	public IBinder onBind(Intent intent) {
		return new ServiceBinder();
	}



	@Override
	public void onOnHMIStatus(OnHMIStatus status)
	{
		switch (status.getSystemContext())
		{
		case SYSCTXT_MAIN:
			break;
		case SYSCTXT_VRSESSION:
			break;
		case SYSCTXT_MENU:
			break;
		default:
			return;
		}

		switch (status.getAudioStreamingState())
		{
		case AUDIBLE:
			// play audio if applicable
			break;
		case NOT_AUDIBLE:
			// pause/stop/mute audio if applicable
			break;
		default:
			return;
		}

		if (status.getHmiLevel() != HMILevel.HMI_NONE) {
			try {
				this.mProxy.subscribevehicledata(true,true,true,true,true,true,true,
                        true,true,true,true,true,true,true,mCorrelationID++);
			} catch (SdlException e) {
				e.printStackTrace();
			}
		}

		switch (status.getHmiLevel())
		{
		case HMI_FULL:
			if (status.getFirstRun())
			{
				try
				{
					mProxy.addCommand(CMD_ID_SWITCH, new Vector<>(Arrays.asList(CMD_NAME_SWITCH)), 0);
				}
				catch (SdlException e)
				{
					e.printStackTrace();
				}
				showMainScreen();
			}
			else
			{

			}
			break;
		case HMI_LIMITED:
			break;
		case HMI_BACKGROUND:
			break;
		case HMI_NONE:
			break;
		default:
			return;
		}
	}



	@Override
	public void onProxyClosed(String s, Exception e, SdlDisconnectedReason sdlDisconnectedReason)
	{
		if (this.vehicleDataListener != null) {
			this.vehicleDataListener.forceClose();
		}
	}

	@Override
	public void onServiceEnded(OnServiceEnded onServiceEnded)
	{

	}

	@Override
	public void onServiceNACKed(OnServiceNACKed onServiceNACKed)
	{

	}

	@Override
	public void onOnStreamRPC(OnStreamRPC onStreamRPC)
	{

	}

	@Override
	public void onStreamRPCResponse(StreamRPCResponse streamRPCResponse)
	{

	}

	@Override
	public void onError(String s, Exception e)
	{

	}

	@Override
	public void onGenericResponse(GenericResponse genericResponse)
	{

	}

	@Override
	public void onOnCommand(OnCommand response)
	{
		switch(response.getCmdID())
		{
		case CMD_ID_SWITCH:
			if (mLayout instanceof SdlAQILayoutCrystal)
				mLayout = SdlAQILayoutMeter.getInstance(this);
			else
				mLayout = SdlAQILayoutCrystal.getInstance(this);
			break;
		default:
			break;
		}
	}


	@Override
	public void onAddCommandResponse(AddCommandResponse response)
	{
	}

	@Override
	public void onAddSubMenuResponse(AddSubMenuResponse addSubMenuResponse)
	{

	}

	@Override
	public void onCreateInteractionChoiceSetResponse(CreateInteractionChoiceSetResponse response)
	{

	}

	@Override
	public void onAlertResponse(AlertResponse response)
	{
//		switch(response.getCorrelationID())
//		{
//		case COID_ALERT_CLOSE_WINDOW:
//			mAlertIsShowing = false;
//			break;
//		default:
//			break;
//		}
	}

	@Override
	public void onDeleteCommandResponse(DeleteCommandResponse deleteCommandResponse)
	{

	}

	@Override
	public void onDeleteInteractionChoiceSetResponse(
			DeleteInteractionChoiceSetResponse deleteInteractionChoiceSetResponse)
	{

	}

	@Override
	public void onDeleteSubMenuResponse(DeleteSubMenuResponse deleteSubMenuResponse)
	{

	}

	@Override
	public void onPerformInteractionResponse(PerformInteractionResponse response)
	{

	}

	@Override
	public void onResetGlobalPropertiesResponse(ResetGlobalPropertiesResponse resetGlobalPropertiesResponse)
	{

	}

	@Override
	public void onSetGlobalPropertiesResponse(SetGlobalPropertiesResponse response)
	{
//		boolean b = response.getSuccess();
//		String info = response.getInfo();
//		String result = response.getResultCode().toString();
//		result = "";
	}

	@Override
	public void onSetMediaClockTimerResponse(SetMediaClockTimerResponse setMediaClockTimerResponse)
	{

	}

	@Override
	public void onShowResponse(ShowResponse showResponse)
	{
	}

	@Override
	public void onSpeakResponse(SpeakResponse speakResponse)
	{

	}

	@Override
	public void onOnButtonEvent(OnButtonEvent onButtonEvent)
	{

	}

	@Override
	public void onOnButtonPress(OnButtonPress notification)
	{
		int customBtnName = 0;
		switch (notification.getButtonName())
		{
		case CUSTOM_BUTTON:
			customBtnName = notification.getCustomButtonName();
			switch (customBtnName)
			{
			case ID_BTN_YES:
				mAlertIsShowing = false;
				refreshAir();
				break;
			case ID_BTN_NO:
				mAlertIsShowing = false;
				break;
			default:
				break;
			}
			break;
		case OK:
			break;
		case SEEKLEFT:
			break;
		case SEEKRIGHT:
			break;
		default:
			break;
		}

	}

	@Override
	public void onSubscribeButtonResponse(SubscribeButtonResponse notification)
	{

	}

	@Override
	public void onUnsubscribeButtonResponse(UnsubscribeButtonResponse unsubscribeButtonResponse)
	{

	}

	@Override
	public void onOnPermissionsChange(OnPermissionsChange onPermissionsChange)
	{

	}

	@Override
	public void onSubscribeVehicleDataResponse(SubscribeVehicleDataResponse subscribeVehicleDataResponse)
	{
		boolean success = subscribeVehicleDataResponse.getSuccess().booleanValue();
		try {
			Log.d("Louis", "subvehicle data success ? " + success + ", respose" + subscribeVehicleDataResponse.serializeJSON().toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onUnsubscribeVehicleDataResponse(UnsubscribeVehicleDataResponse unsubscribeVehicleDataResponse)
	{

	}

	@Override
	public void onGetVehicleDataResponse(GetVehicleDataResponse getVehicleDataResponse)
	{

	}

	@Override
	public void onOnVehicleData(OnVehicleData onVehicleData)
	{
		if (vehicleDataListener != null) {
			vehicleDataListener.onVehicleData(onVehicleData);
		}
	}

	@Override
	public void onPerformAudioPassThruResponse(PerformAudioPassThruResponse response)
	{
		response.getSuccess();

	}

	@Override
	public void onEndAudioPassThruResponse(EndAudioPassThruResponse endAudioPassThruResponse)
	{

	}

	@Override
	public void onOnAudioPassThru(OnAudioPassThru apt)
	{
		//byte[] data = apt.getAPTData();

	}

	@Override
	public void onPutFileResponse(PutFileResponse putFileResponse)
	{
		//putFileResponse.getSpaceAvailable();
	}

	@Override
	public void onDeleteFileResponse(DeleteFileResponse deleteFileResponse)
	{

	}

	@Override
	public void onListFilesResponse(ListFilesResponse listFilesResponse)
	{

	}

	@Override
	public void onSetAppIconResponse(SetAppIconResponse setAppIconResponse)
	{

	}

	@Override
	public void onScrollableMessageResponse(ScrollableMessageResponse scrollableMessageResponse)
	{

	}

	@Override
	public void onChangeRegistrationResponse(ChangeRegistrationResponse changeRegistrationResponse)
	{

	}

	@Override
	public void onSetDisplayLayoutResponse(SetDisplayLayoutResponse setDisplayLayoutResponse)
	{

	}

	@Override
	public void onOnLanguageChange(OnLanguageChange onLanguageChange)
	{

	}

	@Override
	public void onOnHashChange(OnHashChange onHashChange)
	{

	}

	@Override
	public void onSliderResponse(SliderResponse sliderResponse)
	{

	}

	@Override
	public void onOnDriverDistraction(OnDriverDistraction onDriverDistraction)
	{

	}

	@Override
	public void onOnTBTClientState(OnTBTClientState onTBTClientState)
	{

	}

	@Override
	public void onOnSystemRequest(OnSystemRequest onSystemRequest)
	{

	}

	@Override
	public void onSystemRequestResponse(SystemRequestResponse systemRequestResponse)
	{

	}

	@Override
	public void onOnKeyboardInput(OnKeyboardInput onKeyboardInput)
	{

	}

	@Override
	public void onOnTouchEvent(OnTouchEvent onTouchEvent)
	{

	}

	@Override
	public void onDiagnosticMessageResponse(DiagnosticMessageResponse diagnosticMessageResponse)
	{

	}

	@Override
	public void onReadDIDResponse(ReadDIDResponse readDIDResponse)
	{

	}

	@Override
	public void onGetDTCsResponse(GetDTCsResponse getDTCsResponse)
	{

	}

	@Override
	public void onOnLockScreenNotification(OnLockScreenStatus onLockScreenStatus)
	{

	}

	@Override
	public void onDialNumberResponse(DialNumberResponse dialNumberResponse)
	{

	}

	@Override
	public void onSendLocationResponse(SendLocationResponse sendLocationResponse)
	{

	}

	@Override
	public void onShowConstantTbtResponse(ShowConstantTbtResponse showConstantTbtResponse)
	{

	}

	@Override
	public void onAlertManeuverResponse(AlertManeuverResponse alertManeuverResponse)
	{
		
	}

	@Override
	public void onUpdateTurnListResponse(UpdateTurnListResponse updateTurnListResponse)
	{

	}

	@Override
	public void onServiceDataACK(int i)
	{

	}

	@Override
	public void onGetWayPointsResponse(GetWayPointsResponse getWayPointsResponse)
	{

	}

	@Override
	public void onSubscribeWayPointsResponse(SubscribeWayPointsResponse subscribeWayPointsResponse)
	{

	}

	@Override
	public void onUnsubscribeWayPointsResponse(UnsubscribeWayPointsResponse unsubscribeWayPointsResponse)
	{

	}

	@Override
	public void onOnWayPointChange(OnWayPointChange onWayPointChange)
	{

	}

}
