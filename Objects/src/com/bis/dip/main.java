package com.bis.dip;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (processBA == null) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.bis.dip", "com.bis.dip.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.bis.dip", "com.bis.dip.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.bis.dip.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEventFromUI(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        if (mostCurrent != null)
            processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        for (int i = 0;i < permissions.length;i++) {
            Object[] o = new Object[] {permissions[i], grantResults[i] == 0};
            processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
        }
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static String _empscanurl = "";
public static String _ordscanurl = "";
public static String _updateurl = "";
public static String _empid = "";
public static String _orderno = "";
public static String _furnacecardno = "";
public static String[] _parts = null;
public anywheresoftware.b4a.objects.ButtonWrapper _buopen = null;
public anywheresoftware.b4a.objects.LabelWrapper _labempname = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editemployeeid = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editorderctl = null;
public anywheresoftware.b4a.objects.LabelWrapper _laborderctl = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public com.bis.dip.starter _starter = null;
public com.bis.dip.main2 _main2 = null;
public com.bis.dip.statemanager _statemanager = null;
public com.bis.dip.settingsapp _settingsapp = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
vis = vis | (main2.mostCurrent != null);
vis = vis | (settingsapp.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 44;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 46;BA.debugLine="Activity.LoadLayout(\"main1\")";
mostCurrent._activity.LoadLayout("main1",mostCurrent.activityBA);
 //BA.debugLineNum = 48;BA.debugLine="If StateManager.GetSetting(\"EMP_URL\") = \"\" Then";
if ((mostCurrent._statemanager._getsetting(mostCurrent.activityBA,"EMP_URL")).equals("")) { 
 //BA.debugLineNum = 49;BA.debugLine="StateManager.SetSetting(\"EMP_URL\",\"http://bisapp";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"EMP_URL","http://bisapps.biscomputer.com/apex/bis/api/dipfurnace/furnace/");
 };
 //BA.debugLineNum = 52;BA.debugLine="If StateManager.GetSetting(\"ORD_URL\") = \"\" Then";
if ((mostCurrent._statemanager._getsetting(mostCurrent.activityBA,"ORD_URL")).equals("")) { 
 //BA.debugLineNum = 53;BA.debugLine="StateManager.SetSetting(\"ORD_URL\",\"http://bisapp";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"ORD_URL","http://bisapps.biscomputer.com/apex/bis/api/dipfurnace/order/");
 };
 //BA.debugLineNum = 56;BA.debugLine="If StateManager.GetSetting(\"UPDATE_URL\") = \"\" The";
if ((mostCurrent._statemanager._getsetting(mostCurrent.activityBA,"UPDATE_URL")).equals("")) { 
 //BA.debugLineNum = 57;BA.debugLine="StateManager.SetSetting(\"UPDATE_URL\",\"http://bis";
mostCurrent._statemanager._setsetting(mostCurrent.activityBA,"UPDATE_URL","http://bisapps.biscomputer.com/apex/bis/api/dipfurnace/update/");
 };
 //BA.debugLineNum = 60;BA.debugLine="empscanurl =  StateManager.GetSetting(\"EMP_URL\")";
_empscanurl = mostCurrent._statemanager._getsetting(mostCurrent.activityBA,"EMP_URL");
 //BA.debugLineNum = 61;BA.debugLine="ordscanurl  =  StateManager.GetSetting(\"ORD_URL\")";
_ordscanurl = mostCurrent._statemanager._getsetting(mostCurrent.activityBA,"ORD_URL");
 //BA.debugLineNum = 63;BA.debugLine="updateurl  =  StateManager.GetSetting(\"UPDATE_URL";
_updateurl = mostCurrent._statemanager._getsetting(mostCurrent.activityBA,"UPDATE_URL");
 //BA.debugLineNum = 65;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 85;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 87;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume124() throws Exception{
 //BA.debugLineNum = 67;BA.debugLine="Sub Activity_Resume124";
 //BA.debugLineNum = 69;BA.debugLine="OrderNo = \"\"";
_orderno = "";
 //BA.debugLineNum = 70;BA.debugLine="FurnaceCardNo = \"\"";
_furnacecardno = "";
 //BA.debugLineNum = 71;BA.debugLine="EditOrderCtl.Text = \"\"";
mostCurrent._editorderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 72;BA.debugLine="LabOrderCtl.Text =\"\"";
mostCurrent._laborderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 74;BA.debugLine="EditEmployeeID.Text = \"\"";
mostCurrent._editemployeeid.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 75;BA.debugLine="EmpId = \"\"";
_empid = "";
 //BA.debugLineNum = 76;BA.debugLine="LabEmpName.Text = \"\"";
mostCurrent._labempname.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 77;BA.debugLine="If EditEmployeeID.Text <> \"\" Then";
if ((mostCurrent._editemployeeid.getText()).equals("") == false) { 
 //BA.debugLineNum = 78;BA.debugLine="EditOrderCtl.RequestFocus";
mostCurrent._editorderctl.RequestFocus();
 }else {
 //BA.debugLineNum = 80;BA.debugLine="EditEmployeeID.RequestFocus";
mostCurrent._editemployeeid.RequestFocus();
 };
 //BA.debugLineNum = 83;BA.debugLine="End Sub";
return "";
}
public static String  _butopen_click() throws Exception{
 //BA.debugLineNum = 112;BA.debugLine="Sub ButOpen_Click";
 //BA.debugLineNum = 114;BA.debugLine="If EmpId <> \"\" Then";
if ((_empid).equals("") == false) { 
 //BA.debugLineNum = 117;BA.debugLine="If   OrderNo <> \"\" Then";
if ((_orderno).equals("") == false) { 
 //BA.debugLineNum = 120;BA.debugLine="StartActivity(\"main2\")";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)("main2"));
 }else {
 //BA.debugLineNum = 124;BA.debugLine="Msgbox(\"Invalid Order Scanned\",\"Alert\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Order Scanned"),BA.ObjectToCharSequence("Alert"),mostCurrent.activityBA);
 };
 }else {
 //BA.debugLineNum = 127;BA.debugLine="Msgbox(\"Invalid Employee Scanned\",\"Alert\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Employee Scanned"),BA.ObjectToCharSequence("Alert"),mostCurrent.activityBA);
 };
 //BA.debugLineNum = 129;BA.debugLine="End Sub";
return "";
}
public static String  _butsettings_click() throws Exception{
 //BA.debugLineNum = 337;BA.debugLine="Sub ButSettings_Click";
 //BA.debugLineNum = 338;BA.debugLine="StartActivity(settingsApp)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._settingsapp.getObject()));
 //BA.debugLineNum = 339;BA.debugLine="End Sub";
return "";
}
public static String  _button1_click() throws Exception{
String _s = "";
anywheresoftware.b4a.samples.httputils2.httpjob _job1 = null;
anywheresoftware.b4a.objects.collections.Map _jsonmap = null;
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jsongenerator = null;
 //BA.debugLineNum = 298;BA.debugLine="Sub Button1_Click '' test button";
 //BA.debugLineNum = 301;BA.debugLine="Dim s As String";
_s = "";
 //BA.debugLineNum = 302;BA.debugLine="Dim job1 As HttpJob";
_job1 = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 303;BA.debugLine="Dim jsonmap As Map";
_jsonmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 304;BA.debugLine="jsonmap.Initialize";
_jsonmap.Initialize();
 //BA.debugLineNum = 306;BA.debugLine="job1.Initialize(\"acceptfccard\", Me)";
_job1._initialize(processBA,"acceptfccard",main.getObject());
 //BA.debugLineNum = 310;BA.debugLine="jsonmap.Put(\"ORDER_NUMBER\",\"121212\")";
_jsonmap.Put((Object)("ORDER_NUMBER"),(Object)("121212"));
 //BA.debugLineNum = 311;BA.debugLine="jsonmap.Put(\"FC_NO\",\"1D\")";
_jsonmap.Put((Object)("FC_NO"),(Object)("1D"));
 //BA.debugLineNum = 312;BA.debugLine="Dim JSONGenerator As JSONGenerator";
_jsongenerator = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 313;BA.debugLine="JSONGenerator.Initialize(jsonmap)";
_jsongenerator.Initialize(_jsonmap);
 //BA.debugLineNum = 317;BA.debugLine="job1.PostString(updateurl,JSONGenerator.ToPrettyS";
_job1._poststring(_updateurl,_jsongenerator.ToPrettyString((int) (2)));
 //BA.debugLineNum = 318;BA.debugLine="job1.GetRequest.SetContentType(\"application/json\"";
_job1._getrequest().SetContentType("application/json");
 //BA.debugLineNum = 335;BA.debugLine="End Sub";
return "";
}
public static String  _button2_click() throws Exception{
 //BA.debugLineNum = 344;BA.debugLine="Sub Button2_Click";
 //BA.debugLineNum = 345;BA.debugLine="StartActivity(main2)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._main2.getObject()));
 //BA.debugLineNum = 346;BA.debugLine="End Sub";
return "";
}
public static String  _callempname() throws Exception{
String _s = "";
anywheresoftware.b4a.samples.httputils2.httpjob _job1 = null;
 //BA.debugLineNum = 271;BA.debugLine="Sub callempname";
 //BA.debugLineNum = 272;BA.debugLine="Dim s As String";
_s = "";
 //BA.debugLineNum = 273;BA.debugLine="Dim job1 As HttpJob";
_job1 = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 274;BA.debugLine="s = empscanurl&EditEmployeeID.Text";
_s = _empscanurl+mostCurrent._editemployeeid.getText();
 //BA.debugLineNum = 275;BA.debugLine="job1.Initialize(\"JobEmpScan\", Me)";
_job1._initialize(processBA,"JobEmpScan",main.getObject());
 //BA.debugLineNum = 276;BA.debugLine="job1.Download(s )";
_job1._download(_s);
 //BA.debugLineNum = 278;BA.debugLine="End Sub";
return "";
}
public static String  _callorderscan() throws Exception{
String _s = "";
anywheresoftware.b4a.samples.httputils2.httpjob _job2 = null;
 //BA.debugLineNum = 280;BA.debugLine="Sub callorderscan";
 //BA.debugLineNum = 281;BA.debugLine="Dim s As String";
_s = "";
 //BA.debugLineNum = 282;BA.debugLine="Dim job2 As HttpJob";
_job2 = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 283;BA.debugLine="s = ordscanurl&EditOrderCtl.Text&\"~\"&EditEmployee";
_s = _ordscanurl+mostCurrent._editorderctl.getText()+"~"+mostCurrent._editemployeeid.getText();
 //BA.debugLineNum = 285;BA.debugLine="job2.Initialize(\"JobOrderScan\", Me)";
_job2._initialize(processBA,"JobOrderScan",main.getObject());
 //BA.debugLineNum = 286;BA.debugLine="job2.Download(s)";
_job2._download(_s);
 //BA.debugLineNum = 287;BA.debugLine="End Sub";
return "";
}
public static String  _editemployeeid_enterpressed() throws Exception{
 //BA.debugLineNum = 104;BA.debugLine="Sub EditEmployeeID_EnterPressed";
 //BA.debugLineNum = 105;BA.debugLine="If EditEmployeeID.Text <> \"\" Then";
if ((mostCurrent._editemployeeid.getText()).equals("") == false) { 
 //BA.debugLineNum = 106;BA.debugLine="callempname";
_callempname();
 //BA.debugLineNum = 107;BA.debugLine="ProgressDialogShow (\"Connecting to Server ...\")";
anywheresoftware.b4a.keywords.Common.ProgressDialogShow(mostCurrent.activityBA,BA.ObjectToCharSequence("Connecting to Server ..."));
 };
 //BA.debugLineNum = 110;BA.debugLine="End Sub";
return "";
}
public static String  _editemployeeid_focuschanged(boolean _hasfocus) throws Exception{
 //BA.debugLineNum = 291;BA.debugLine="Sub EditEmployeeID_FocusChanged (HasFocus As Boole";
 //BA.debugLineNum = 294;BA.debugLine="End Sub";
return "";
}
public static String  _editorderctl_enterpressed() throws Exception{
 //BA.debugLineNum = 90;BA.debugLine="Sub EditOrderCtl_EnterPressed";
 //BA.debugLineNum = 91;BA.debugLine="If EmpId <> \"\" Then";
if ((_empid).equals("") == false) { 
 //BA.debugLineNum = 92;BA.debugLine="If EditOrderCtl.Text <> \"\" Then";
if ((mostCurrent._editorderctl.getText()).equals("") == false) { 
 //BA.debugLineNum = 93;BA.debugLine="callorderscan";
_callorderscan();
 };
 }else {
 //BA.debugLineNum = 96;BA.debugLine="Msgbox(\"Enter Employee First\",\"Alert\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Enter Employee First"),BA.ObjectToCharSequence("Alert"),mostCurrent.activityBA);
 //BA.debugLineNum = 97;BA.debugLine="EditOrderCtl.text = \"\"";
mostCurrent._editorderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 98;BA.debugLine="LabOrderCtl.Text = \"\"";
mostCurrent._laborderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 99;BA.debugLine="EditEmployeeID.RequestFocus";
mostCurrent._editemployeeid.RequestFocus();
 };
 //BA.debugLineNum = 102;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 28;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 32;BA.debugLine="Private BuOpen As Button";
mostCurrent._buopen = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 33;BA.debugLine="Private LabEmpName As Label";
mostCurrent._labempname = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Private EditEmployeeID As EditText";
mostCurrent._editemployeeid = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private EditOrderCtl As EditText";
mostCurrent._editorderctl = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private LabOrderCtl As Label";
mostCurrent._laborderctl = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 42;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
String _s = "";
String[] _returnstring = null;
 //BA.debugLineNum = 130;BA.debugLine="Sub JobDone (Job As HttpJob)";
 //BA.debugLineNum = 131;BA.debugLine="Dim s As String";
_s = "";
 //BA.debugLineNum = 132;BA.debugLine="ProgressDialogHide";
anywheresoftware.b4a.keywords.Common.ProgressDialogHide();
 //BA.debugLineNum = 133;BA.debugLine="If Job.Success = True Then";
if (_job._success==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 134;BA.debugLine="Log(\"Yeaahh\")";
anywheresoftware.b4a.keywords.Common.Log("Yeaahh");
 //BA.debugLineNum = 135;BA.debugLine="Select Job.JobName";
switch (BA.switchObjectToInt(_job._jobname,"JobEmpScan","JobOrderScan","acceptfccard")) {
case 0: {
 //BA.debugLineNum = 137;BA.debugLine="s = Job.GetString";
_s = _job._getstring();
 //BA.debugLineNum = 138;BA.debugLine="ParsJSON(s,\"1\")";
_parsjson(_s,"1");
 break; }
case 1: {
 //BA.debugLineNum = 140;BA.debugLine="s = Job.GetString";
_s = _job._getstring();
 //BA.debugLineNum = 141;BA.debugLine="ParsJSON(s,\"2\")";
_parsjson(_s,"2");
 break; }
case 2: {
 //BA.debugLineNum = 143;BA.debugLine="s = Job.GetString";
_s = _job._getstring();
 //BA.debugLineNum = 144;BA.debugLine="Dim returnstring() As String";
_returnstring = new String[(int) (0)];
java.util.Arrays.fill(_returnstring,"");
 //BA.debugLineNum = 146;BA.debugLine="returnstring = Regex.Split(CRLF,s)";
_returnstring = anywheresoftware.b4a.keywords.Common.Regex.Split(anywheresoftware.b4a.keywords.Common.CRLF,_s);
 //BA.debugLineNum = 149;BA.debugLine="Msgbox(returnstring(0),\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_returnstring[(int) (0)]),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 150;BA.debugLine="Msgbox(returnstring(1),\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_returnstring[(int) (1)]),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 break; }
}
;
 }else {
 //BA.debugLineNum = 160;BA.debugLine="Msgbox(Job.ErrorMessage & \" rescan\" ,\"Alert:\"&Jo";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_job._errormessage+" rescan"),BA.ObjectToCharSequence("Alert:"+_job._jobname),mostCurrent.activityBA);
 //BA.debugLineNum = 162;BA.debugLine="If Job.JobName = \"JobEmpScan\" Then";
if ((_job._jobname).equals("JobEmpScan")) { 
 //BA.debugLineNum = 163;BA.debugLine="EditEmployeeID.Text = \"\"";
mostCurrent._editemployeeid.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 164;BA.debugLine="EditEmployeeID.RequestFocus";
mostCurrent._editemployeeid.RequestFocus();
 };
 //BA.debugLineNum = 167;BA.debugLine="If Job.JobName = \"JobOrderScan\" Then";
if ((_job._jobname).equals("JobOrderScan")) { 
 //BA.debugLineNum = 168;BA.debugLine="EditOrderCtl.Text = \"\"";
mostCurrent._editorderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 169;BA.debugLine="EditOrderCtl.RequestFocus";
mostCurrent._editorderctl.RequestFocus();
 };
 //BA.debugLineNum = 173;BA.debugLine="Log(\"Buuuh\")";
anywheresoftware.b4a.keywords.Common.Log("Buuuh");
 };
 //BA.debugLineNum = 175;BA.debugLine="Job.Release";
_job._release();
 //BA.debugLineNum = 176;BA.debugLine="End Sub";
return "";
}
public static String  _parsjson(String _jsonstring,String _jobtype) throws Exception{
anywheresoftware.b4a.objects.collections.JSONParser _parser = null;
String[] _localparts = null;
anywheresoftware.b4a.objects.collections.Map _map1 = null;
anywheresoftware.b4a.objects.collections.List _m = null;
 //BA.debugLineNum = 177;BA.debugLine="Sub ParsJSON (jsonstring As String,jobtype As Stri";
 //BA.debugLineNum = 178;BA.debugLine="Log(jsonstring)";
anywheresoftware.b4a.keywords.Common.Log(_jsonstring);
 //BA.debugLineNum = 184;BA.debugLine="Dim parser As JSONParser";
_parser = new anywheresoftware.b4a.objects.collections.JSONParser();
 //BA.debugLineNum = 185;BA.debugLine="Dim Localparts() As String";
_localparts = new String[(int) (0)];
java.util.Arrays.fill(_localparts,"");
 //BA.debugLineNum = 186;BA.debugLine="parser.Initialize(jsonstring)";
_parser.Initialize(_jsonstring);
 //BA.debugLineNum = 187;BA.debugLine="Dim Map1 As Map";
_map1 = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 188;BA.debugLine="Map1 = parser.NextObject";
_map1 = _parser.NextObject();
 //BA.debugLineNum = 189;BA.debugLine="Dim m As List";
_m = new anywheresoftware.b4a.objects.collections.List();
 //BA.debugLineNum = 190;BA.debugLine="m = Map1.Get(\"items\")";
_m.setObject((java.util.List)(_map1.Get((Object)("items"))));
 //BA.debugLineNum = 193;BA.debugLine="If jobtype = \"1\" Then";
if ((_jobtype).equals("1")) { 
 //BA.debugLineNum = 195;BA.debugLine="If m.Size > 0 Then";
if (_m.getSize()>0) { 
 //BA.debugLineNum = 196;BA.debugLine="Map1 = m.get(0)";
_map1.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_m.Get((int) (0))));
 //BA.debugLineNum = 197;BA.debugLine="Log(Map1.get(\"employee_id\"))";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_map1.Get((Object)("employee_id"))));
 //BA.debugLineNum = 198;BA.debugLine="EmpId = Map1.get(\"employee_id\")";
_empid = BA.ObjectToString(_map1.Get((Object)("employee_id")));
 //BA.debugLineNum = 199;BA.debugLine="LabEmpName.Text = Map1.get(\"employee_name\")";
mostCurrent._labempname.setText(BA.ObjectToCharSequence(_map1.Get((Object)("employee_name"))));
 //BA.debugLineNum = 200;BA.debugLine="EditOrderCtl.RequestFocus";
mostCurrent._editorderctl.RequestFocus();
 }else {
 //BA.debugLineNum = 202;BA.debugLine="LabEmpName.Text = \"Invalid Employee ID Scanned\"";
mostCurrent._labempname.setText(BA.ObjectToCharSequence("Invalid Employee ID Scanned"));
 //BA.debugLineNum = 203;BA.debugLine="EditEmployeeID.Text = \"\"";
mostCurrent._editemployeeid.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 204;BA.debugLine="EmpId = \"\"";
_empid = "";
 //BA.debugLineNum = 205;BA.debugLine="EditEmployeeID.RequestFocus";
mostCurrent._editemployeeid.RequestFocus();
 };
 };
 //BA.debugLineNum = 208;BA.debugLine="If jobtype = \"2\" Then";
if ((_jobtype).equals("2")) { 
 //BA.debugLineNum = 209;BA.debugLine="If m.Size > 0 Then";
if (_m.getSize()>0) { 
 //BA.debugLineNum = 210;BA.debugLine="Map1 = m.get(0)";
_map1.setObject((anywheresoftware.b4a.objects.collections.Map.MyMap)(_m.Get((int) (0))));
 //BA.debugLineNum = 211;BA.debugLine="Log(Map1.get(\"result\"))";
anywheresoftware.b4a.keywords.Common.Log(BA.ObjectToString(_map1.Get((Object)("result"))));
 //BA.debugLineNum = 216;BA.debugLine="Localparts = Regex.Split(\"~\", Map1.get(\"result\"";
_localparts = anywheresoftware.b4a.keywords.Common.Regex.Split("~",BA.ObjectToString(_map1.Get((Object)("result"))));
 //BA.debugLineNum = 219;BA.debugLine="If Localparts(0)= \"SUCCESS\" Then";
if ((_localparts[(int) (0)]).equals("SUCCESS")) { 
 //BA.debugLineNum = 220;BA.debugLine="OrderNo = Localparts(1)";
_orderno = _localparts[(int) (1)];
 //BA.debugLineNum = 221;BA.debugLine="FurnaceCardNo = Localparts(2)";
_furnacecardno = _localparts[(int) (2)];
 //BA.debugLineNum = 222;BA.debugLine="EditOrderCtl.Text = \"\"";
mostCurrent._editorderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 223;BA.debugLine="LabOrderCtl.Text = OrderNo & \" Valid Order\"";
mostCurrent._laborderctl.setText(BA.ObjectToCharSequence(_orderno+" Valid Order"));
 //BA.debugLineNum = 224;BA.debugLine="Parts =  Localparts";
_parts = _localparts;
 //BA.debugLineNum = 226;BA.debugLine="If Localparts(24) = \"1\" Then";
if ((_localparts[(int) (24)]).equals("1")) { 
 //BA.debugLineNum = 227;BA.debugLine="Msgbox(\"INSCAN DONE\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("INSCAN DONE"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 228;BA.debugLine="OrderNo = \"\"";
_orderno = "";
 //BA.debugLineNum = 229;BA.debugLine="FurnaceCardNo = \"\"";
_furnacecardno = "";
 //BA.debugLineNum = 230;BA.debugLine="EditOrderCtl.Text = \"\"";
mostCurrent._editorderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 231;BA.debugLine="LabOrderCtl.Text =\"\"";
mostCurrent._laborderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 233;BA.debugLine="EditEmployeeID.Text = \"\"";
mostCurrent._editemployeeid.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 234;BA.debugLine="EmpId = \"\"";
_empid = "";
 //BA.debugLineNum = 235;BA.debugLine="LabEmpName.Text = \"\"";
mostCurrent._labempname.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 237;BA.debugLine="EditEmployeeID.RequestFocus";
mostCurrent._editemployeeid.RequestFocus();
 };
 //BA.debugLineNum = 242;BA.debugLine="If Localparts(24) = \"2\" Then";
if ((_localparts[(int) (24)]).equals("2")) { 
 //BA.debugLineNum = 243;BA.debugLine="StartActivity(main2)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._main2.getObject()));
 };
 //BA.debugLineNum = 247;BA.debugLine="If Localparts(24) = \"3\" Then";
if ((_localparts[(int) (24)]).equals("3")) { 
 //BA.debugLineNum = 248;BA.debugLine="Msgbox(\"ALL SCANS ARE DONE\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("ALL SCANS ARE DONE"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 249;BA.debugLine="StartActivity(main2)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._main2.getObject()));
 };
 };
 //BA.debugLineNum = 256;BA.debugLine="If Localparts(0)= \"FAILURE\" Then";
if ((_localparts[(int) (0)]).equals("FAILURE")) { 
 //BA.debugLineNum = 257;BA.debugLine="OrderNo = \"\"";
_orderno = "";
 //BA.debugLineNum = 258;BA.debugLine="FurnaceCardNo = \"\"";
_furnacecardno = "";
 //BA.debugLineNum = 259;BA.debugLine="EditOrderCtl.Text = \"\"";
mostCurrent._editorderctl.setText(BA.ObjectToCharSequence(""));
 //BA.debugLineNum = 260;BA.debugLine="LabOrderCtl.Text = \"Invalid Scan\"";
mostCurrent._laborderctl.setText(BA.ObjectToCharSequence("Invalid Scan"));
 };
 };
 };
 //BA.debugLineNum = 270;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        anywheresoftware.b4a.samples.httputils2.httputils2service._process_globals();
main._process_globals();
starter._process_globals();
main2._process_globals();
statemanager._process_globals();
settingsapp._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 15;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 18;BA.debugLine="Dim empscanurl As String";
_empscanurl = "";
 //BA.debugLineNum = 19;BA.debugLine="Dim ordscanurl As String";
_ordscanurl = "";
 //BA.debugLineNum = 21;BA.debugLine="Dim updateurl As String";
_updateurl = "";
 //BA.debugLineNum = 22;BA.debugLine="Dim EmpId As String = \"\"";
_empid = "";
 //BA.debugLineNum = 23;BA.debugLine="Dim OrderNo As String = \"\"";
_orderno = "";
 //BA.debugLineNum = 24;BA.debugLine="Dim FurnaceCardNo As String = \"\"";
_furnacecardno = "";
 //BA.debugLineNum = 25;BA.debugLine="Dim Parts() As String";
_parts = new String[(int) (0)];
java.util.Arrays.fill(_parts,"");
 //BA.debugLineNum = 26;BA.debugLine="End Sub";
return "";
}
}
