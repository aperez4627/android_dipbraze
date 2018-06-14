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

public class main2 extends Activity implements B4AActivity{
	public static main2 mostCurrent;
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
			processBA = new BA(this.getApplicationContext(), null, null, "com.bis.dip", "com.bis.dip.main2");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main2).");
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
		activityBA = new BA(this, layout, processBA, "com.bis.dip", "com.bis.dip.main2");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.bis.dip.main2", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main2) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main2) Resume **");
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
		return main2.class;
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
        BA.LogInfo("** Activity (main2) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
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
            BA.LogInfo("** Activity (main2) Resume **");
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
public anywheresoftware.b4a.objects.EditTextWrapper _editloadno = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editqty = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editphno = null;
public anywheresoftware.b4a.objects.LabelWrapper _labtmin = null;
public anywheresoftware.b4a.objects.LabelWrapper _labtmout = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editsbt = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editdtahr = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editdtamin = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editdtasec = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editqtahr = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editqtamin = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editqtasec = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editqts = null;
public anywheresoftware.b4a.objects.LabelWrapper _labqtf = null;
public anywheresoftware.b4a.objects.EditTextWrapper _editqtf = null;
public static String[] _acceptparts = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittmhrin = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittmhrout = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittmminin = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittmminout = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _tgbutin = null;
public anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper _tgbutout = null;
public anywheresoftware.b4a.samples.httputils2.httputils2service _httputils2service = null;
public com.bis.dip.main _main = null;
public com.bis.dip.starter _starter = null;
public com.bis.dip.statemanager _statemanager = null;
public com.bis.dip.settingsapp _settingsapp = null;

public static void initializeProcessGlobals() {
             try {
                Class.forName(BA.applicationContext.getPackageName() + ".main").getMethod("initializeProcessGlobals").invoke(null, null);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
}
public static String  _activity_create(boolean _firsttime) throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 45;BA.debugLine="Activity.LoadLayout(\"main2\")";
mostCurrent._activity.LoadLayout("main2",mostCurrent.activityBA);
 //BA.debugLineNum = 47;BA.debugLine="If Main.Parts.Length > 0 Then";
if (mostCurrent._main._parts.length>0) { 
 //BA.debugLineNum = 49;BA.debugLine="LabTMIN.Text = Main.parts(4) ''PREHEAT_TIME_IN";
mostCurrent._labtmin.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (4)]));
 //BA.debugLineNum = 50;BA.debugLine="LabTMOUT.Text = Main.parts(5)''PREHEAT_TIME_OUT";
mostCurrent._labtmout.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (5)]));
 //BA.debugLineNum = 52;BA.debugLine="EditPHNO.Text = Main.parts(6) '' PREHEAT_OVEN_NO";
mostCurrent._editphno.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (6)]));
 //BA.debugLineNum = 53;BA.debugLine="EditQty.Text = Main.parts(7) ''qty";
mostCurrent._editqty.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (7)]));
 //BA.debugLineNum = 54;BA.debugLine="EditLoadNo.Text = Main.parts(8) '' Load No";
mostCurrent._editloadno.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (8)]));
 //BA.debugLineNum = 55;BA.debugLine="EditSBT.Text = Main.parts(9) ''SALT_BATH_TEMP_ACT";
mostCurrent._editsbt.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (9)]));
 //BA.debugLineNum = 58;BA.debugLine="EditDTAHR.Text = Main.parts(10) ''DELAY_TIME_ACTU";
mostCurrent._editdtahr.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (10)]));
 //BA.debugLineNum = 59;BA.debugLine="EditDTAMIN.Text = Main.parts(11) ''DELAY_TIME_ACT";
mostCurrent._editdtamin.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (11)]));
 //BA.debugLineNum = 60;BA.debugLine="EditDTASEC.Text = Main.parts(12) ''DELAY_TIME_ACT";
mostCurrent._editdtasec.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (12)]));
 //BA.debugLineNum = 62;BA.debugLine="EditQTAHR.Text = Main.parts(13) ''QUENCH_TIME_ACT";
mostCurrent._editqtahr.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (13)]));
 //BA.debugLineNum = 63;BA.debugLine="EditQTAMIN.Text = Main.parts(14) ''QUENCH_TIME_AC";
mostCurrent._editqtamin.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (14)]));
 //BA.debugLineNum = 64;BA.debugLine="EditQTASEC.Text = Main.parts(15) ''QUENCH_TIME_AC";
mostCurrent._editqtasec.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (15)]));
 //BA.debugLineNum = 66;BA.debugLine="EditQTS.Text = Main.parts(16) ''QUENCH_TEMP_START";
mostCurrent._editqts.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (16)]));
 //BA.debugLineNum = 67;BA.debugLine="EditQTF.Text = Main.parts(17) ''QUENCH_TEMP_FINIS";
mostCurrent._editqtf.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (17)]));
 //BA.debugLineNum = 70;BA.debugLine="EditTMHRIN.Text =  Main.parts(18)";
mostCurrent._edittmhrin.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (18)]));
 //BA.debugLineNum = 71;BA.debugLine="EditTMMININ.Text =  Main.parts(19)";
mostCurrent._edittmminin.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (19)]));
 //BA.debugLineNum = 73;BA.debugLine="If  Main.parts(20) = \"AM\" Then";
if ((mostCurrent._main._parts[(int) (20)]).equals("AM")) { 
 //BA.debugLineNum = 74;BA.debugLine="TGBUTIN.Checked = True";
mostCurrent._tgbutin.setChecked(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 77;BA.debugLine="If  Main.parts(19) = \"PM\" Then";
if ((mostCurrent._main._parts[(int) (19)]).equals("PM")) { 
 //BA.debugLineNum = 78;BA.debugLine="TGBUTIN.Checked = False";
mostCurrent._tgbutin.setChecked(anywheresoftware.b4a.keywords.Common.False);
 };
 //BA.debugLineNum = 81;BA.debugLine="EditTMHROUT.Text =  Main.parts(21)";
mostCurrent._edittmhrout.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (21)]));
 //BA.debugLineNum = 82;BA.debugLine="EditTMMINOUT.Text =  Main.parts(22)";
mostCurrent._edittmminout.setText(BA.ObjectToCharSequence(mostCurrent._main._parts[(int) (22)]));
 //BA.debugLineNum = 84;BA.debugLine="If  Main.parts(23) = \"AM\" Then";
if ((mostCurrent._main._parts[(int) (23)]).equals("AM")) { 
 //BA.debugLineNum = 85;BA.debugLine="TGBUTOUT.Checked = True";
mostCurrent._tgbutout.setChecked(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 88;BA.debugLine="If  Main.parts(23) = \"PM\" Then";
if ((mostCurrent._main._parts[(int) (23)]).equals("PM")) { 
 //BA.debugLineNum = 89;BA.debugLine="TGBUTOUT.Checked = False";
mostCurrent._tgbutout.setChecked(anywheresoftware.b4a.keywords.Common.False);
 };
 };
 //BA.debugLineNum = 100;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 106;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 108;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 102;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 104;BA.debugLine="End Sub";
return "";
}
public static String  _buaccept_click() throws Exception{
 //BA.debugLineNum = 111;BA.debugLine="Sub BuAccept_Click";
 //BA.debugLineNum = 119;BA.debugLine="callaccept";
_callaccept();
 //BA.debugLineNum = 124;BA.debugLine="End Sub";
return "";
}
public static String  _butexit_click() throws Exception{
 //BA.debugLineNum = 255;BA.debugLine="Sub ButExit_Click";
 //BA.debugLineNum = 257;BA.debugLine="Activity.Finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 258;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._main.getObject()));
 //BA.debugLineNum = 259;BA.debugLine="End Sub";
return "";
}
public static String  _callaccept() throws Exception{
String _s = "";
anywheresoftware.b4a.samples.httputils2.httpjob _job1 = null;
anywheresoftware.b4a.objects.collections.Map _jsonmap = null;
anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator _jsongenerator = null;
 //BA.debugLineNum = 154;BA.debugLine="Sub callaccept'";
 //BA.debugLineNum = 155;BA.debugLine="Dim s As String";
_s = "";
 //BA.debugLineNum = 156;BA.debugLine="Dim job1 As HttpJob";
_job1 = new anywheresoftware.b4a.samples.httputils2.httpjob();
 //BA.debugLineNum = 158;BA.debugLine="job1.Initialize(\"acceptfccard\", Me)";
_job1._initialize(processBA,"acceptfccard",main2.getObject());
 //BA.debugLineNum = 164;BA.debugLine="Dim JsonMap As Map";
_jsonmap = new anywheresoftware.b4a.objects.collections.Map();
 //BA.debugLineNum = 165;BA.debugLine="JsonMap.Initialize";
_jsonmap.Initialize();
 //BA.debugLineNum = 168;BA.debugLine="Main.parts(6) = EditPHNO.Text";
mostCurrent._main._parts[(int) (6)] = mostCurrent._editphno.getText();
 //BA.debugLineNum = 169;BA.debugLine="Main.parts(7) = EditQty.Text";
mostCurrent._main._parts[(int) (7)] = mostCurrent._editqty.getText();
 //BA.debugLineNum = 170;BA.debugLine="Main.parts(8) = EditLoadNo.Text";
mostCurrent._main._parts[(int) (8)] = mostCurrent._editloadno.getText();
 //BA.debugLineNum = 173;BA.debugLine="Main.parts(9) = EditSBT.Text";
mostCurrent._main._parts[(int) (9)] = mostCurrent._editsbt.getText();
 //BA.debugLineNum = 175;BA.debugLine="Main.parts(10) = EditDTAHR.Text";
mostCurrent._main._parts[(int) (10)] = mostCurrent._editdtahr.getText();
 //BA.debugLineNum = 176;BA.debugLine="Main.parts(11) = EditDTAMIN.Text";
mostCurrent._main._parts[(int) (11)] = mostCurrent._editdtamin.getText();
 //BA.debugLineNum = 177;BA.debugLine="Main.parts(12) = EditDTASEC.Text";
mostCurrent._main._parts[(int) (12)] = mostCurrent._editdtasec.getText();
 //BA.debugLineNum = 179;BA.debugLine="Main.parts(13) = EditQTAHR.Text";
mostCurrent._main._parts[(int) (13)] = mostCurrent._editqtahr.getText();
 //BA.debugLineNum = 180;BA.debugLine="Main.parts(14) = EditQTAMIN.Text";
mostCurrent._main._parts[(int) (14)] = mostCurrent._editqtamin.getText();
 //BA.debugLineNum = 181;BA.debugLine="Main.parts(15) = EditQTASEC.Text";
mostCurrent._main._parts[(int) (15)] = mostCurrent._editqtasec.getText();
 //BA.debugLineNum = 183;BA.debugLine="Main.parts(16) = EditQTS.Text";
mostCurrent._main._parts[(int) (16)] = mostCurrent._editqts.getText();
 //BA.debugLineNum = 184;BA.debugLine="Main.parts(17) = EditQTF.Text";
mostCurrent._main._parts[(int) (17)] = mostCurrent._editqtf.getText();
 //BA.debugLineNum = 186;BA.debugLine="Main.parts(18) = EditTMHRIN.Text";
mostCurrent._main._parts[(int) (18)] = mostCurrent._edittmhrin.getText();
 //BA.debugLineNum = 187;BA.debugLine="Main.parts(19) = EditTMMININ.Text";
mostCurrent._main._parts[(int) (19)] = mostCurrent._edittmminin.getText();
 //BA.debugLineNum = 189;BA.debugLine="If TGBUTIN.Checked = True Then";
if (mostCurrent._tgbutin.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 190;BA.debugLine="Main.parts(20) = \"AM\"";
mostCurrent._main._parts[(int) (20)] = "AM";
 }else {
 //BA.debugLineNum = 193;BA.debugLine="Main.parts(20) = \"PM\"";
mostCurrent._main._parts[(int) (20)] = "PM";
 };
 //BA.debugLineNum = 197;BA.debugLine="Main.parts(21) = EditTMHROUT.Text";
mostCurrent._main._parts[(int) (21)] = mostCurrent._edittmhrout.getText();
 //BA.debugLineNum = 198;BA.debugLine="Main.parts(22) = EditTMMINOUT.Text";
mostCurrent._main._parts[(int) (22)] = mostCurrent._edittmminout.getText();
 //BA.debugLineNum = 200;BA.debugLine="If TGBUTOUT.Checked = True Then";
if (mostCurrent._tgbutout.getChecked()==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 201;BA.debugLine="Main.parts(23) = \"AM\"";
mostCurrent._main._parts[(int) (23)] = "AM";
 }else {
 //BA.debugLineNum = 204;BA.debugLine="Main.parts(23) = \"PM\"";
mostCurrent._main._parts[(int) (23)] = "PM";
 };
 //BA.debugLineNum = 208;BA.debugLine="JsonMap.Put(\"ORDER_NUMBER\",Main.parts(1))";
_jsonmap.Put((Object)("ORDER_NUMBER"),(Object)(mostCurrent._main._parts[(int) (1)]));
 //BA.debugLineNum = 209;BA.debugLine="JsonMap.Put(\"FC_NO\",Main.parts(2))";
_jsonmap.Put((Object)("FC_NO"),(Object)(mostCurrent._main._parts[(int) (2)]));
 //BA.debugLineNum = 210;BA.debugLine="JsonMap.Put(\"SCH_REC_ID\",Main.parts(3))";
_jsonmap.Put((Object)("SCH_REC_ID"),(Object)(mostCurrent._main._parts[(int) (3)]));
 //BA.debugLineNum = 211;BA.debugLine="JsonMap.Put(\"PREHEAT_TIME_IN\",Main.parts(4))";
_jsonmap.Put((Object)("PREHEAT_TIME_IN"),(Object)(mostCurrent._main._parts[(int) (4)]));
 //BA.debugLineNum = 212;BA.debugLine="JsonMap.Put(\"PREHEAT_TIME_OUT\",Main.parts(5))";
_jsonmap.Put((Object)("PREHEAT_TIME_OUT"),(Object)(mostCurrent._main._parts[(int) (5)]));
 //BA.debugLineNum = 213;BA.debugLine="JsonMap.Put(\"PREHEAT_OVEN_NO\",Main.parts(6))";
_jsonmap.Put((Object)("PREHEAT_OVEN_NO"),(Object)(mostCurrent._main._parts[(int) (6)]));
 //BA.debugLineNum = 214;BA.debugLine="JsonMap.Put(\"QTY\",Main.parts(7))";
_jsonmap.Put((Object)("QTY"),(Object)(mostCurrent._main._parts[(int) (7)]));
 //BA.debugLineNum = 215;BA.debugLine="JsonMap.Put(\"LOAD_NO\",Main.parts(8))";
_jsonmap.Put((Object)("LOAD_NO"),(Object)(mostCurrent._main._parts[(int) (8)]));
 //BA.debugLineNum = 216;BA.debugLine="JsonMap.Put(\"SALT_BATH_TEMP_ACTUAL\",Main.parts(9)";
_jsonmap.Put((Object)("SALT_BATH_TEMP_ACTUAL"),(Object)(mostCurrent._main._parts[(int) (9)]));
 //BA.debugLineNum = 217;BA.debugLine="JsonMap.Put(\"DELAY_TIME_ACTUAL_HR\",Main.parts(10)";
_jsonmap.Put((Object)("DELAY_TIME_ACTUAL_HR"),(Object)(mostCurrent._main._parts[(int) (10)]));
 //BA.debugLineNum = 218;BA.debugLine="JsonMap.Put(\"DELAY_TIME_ACTUAL_MIN\",Main.parts(11";
_jsonmap.Put((Object)("DELAY_TIME_ACTUAL_MIN"),(Object)(mostCurrent._main._parts[(int) (11)]));
 //BA.debugLineNum = 219;BA.debugLine="JsonMap.Put(\"DELAY_TIME_ACTUAL_SEC\",Main.parts(12";
_jsonmap.Put((Object)("DELAY_TIME_ACTUAL_SEC"),(Object)(mostCurrent._main._parts[(int) (12)]));
 //BA.debugLineNum = 220;BA.debugLine="JsonMap.Put(\"QUENCH_TIME_ACTUAL_HR\",Main.parts(13";
_jsonmap.Put((Object)("QUENCH_TIME_ACTUAL_HR"),(Object)(mostCurrent._main._parts[(int) (13)]));
 //BA.debugLineNum = 221;BA.debugLine="JsonMap.Put(\"QUENCH_TIME_ACTUAL_MIN\",Main.parts(1";
_jsonmap.Put((Object)("QUENCH_TIME_ACTUAL_MIN"),(Object)(mostCurrent._main._parts[(int) (14)]));
 //BA.debugLineNum = 222;BA.debugLine="JsonMap.Put(\"QUENCH_TIME_ACTUAL_SEC\",Main.parts(1";
_jsonmap.Put((Object)("QUENCH_TIME_ACTUAL_SEC"),(Object)(mostCurrent._main._parts[(int) (15)]));
 //BA.debugLineNum = 224;BA.debugLine="JsonMap.Put(\"QUENCH_TEMP_START\",Main.parts(16))";
_jsonmap.Put((Object)("QUENCH_TEMP_START"),(Object)(mostCurrent._main._parts[(int) (16)]));
 //BA.debugLineNum = 225;BA.debugLine="JsonMap.Put(\"QUENCH_TEMP_FINISH\",Main.parts(17))";
_jsonmap.Put((Object)("QUENCH_TEMP_FINISH"),(Object)(mostCurrent._main._parts[(int) (17)]));
 //BA.debugLineNum = 227;BA.debugLine="JsonMap.Put(\"PREHEAT_TIME_IN_HR\",Main.parts(18))";
_jsonmap.Put((Object)("PREHEAT_TIME_IN_HR"),(Object)(mostCurrent._main._parts[(int) (18)]));
 //BA.debugLineNum = 228;BA.debugLine="JsonMap.Put(\"PREHEAT_TIME_IN_MIN\",Main.parts(19))";
_jsonmap.Put((Object)("PREHEAT_TIME_IN_MIN"),(Object)(mostCurrent._main._parts[(int) (19)]));
 //BA.debugLineNum = 229;BA.debugLine="JsonMap.Put(\"PREHEAT_TIME_IN_PM\",Main.parts(20))";
_jsonmap.Put((Object)("PREHEAT_TIME_IN_PM"),(Object)(mostCurrent._main._parts[(int) (20)]));
 //BA.debugLineNum = 231;BA.debugLine="JsonMap.Put(\"PREHEAT_TIME_OUT_HR\",Main.parts(21))";
_jsonmap.Put((Object)("PREHEAT_TIME_OUT_HR"),(Object)(mostCurrent._main._parts[(int) (21)]));
 //BA.debugLineNum = 232;BA.debugLine="JsonMap.Put(\"PREHEAT_TIME_OUT_MIN\",Main.parts(22)";
_jsonmap.Put((Object)("PREHEAT_TIME_OUT_MIN"),(Object)(mostCurrent._main._parts[(int) (22)]));
 //BA.debugLineNum = 233;BA.debugLine="JsonMap.Put(\"PREHEAT_TIME_OUT_PM\",Main.parts(23))";
_jsonmap.Put((Object)("PREHEAT_TIME_OUT_PM"),(Object)(mostCurrent._main._parts[(int) (23)]));
 //BA.debugLineNum = 236;BA.debugLine="Dim JSONGenerator As JSONGenerator";
_jsongenerator = new anywheresoftware.b4a.objects.collections.JSONParser.JSONGenerator();
 //BA.debugLineNum = 237;BA.debugLine="JSONGenerator.Initialize(JsonMap)";
_jsongenerator.Initialize(_jsonmap);
 //BA.debugLineNum = 238;BA.debugLine="job1.PostString(Main.updateurl,JSONGenerator.ToPr";
_job1._poststring(mostCurrent._main._updateurl,_jsongenerator.ToPrettyString((int) (2)));
 //BA.debugLineNum = 239;BA.debugLine="job1.GetRequest.SetContentType(\"application/json\"";
_job1._getrequest().SetContentType("application/json");
 //BA.debugLineNum = 244;BA.debugLine="End Sub";
return "";
}
public static String  _edittmhrin_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 287;BA.debugLine="Sub EditTMHRIN_TextChanged (Old As String, New As";
 //BA.debugLineNum = 288;BA.debugLine="If New <> \"\" Then";
if ((_new).equals("") == false) { 
 //BA.debugLineNum = 289;BA.debugLine="If New >12 Then";
if ((double)(Double.parseDouble(_new))>12) { 
 //BA.debugLineNum = 290;BA.debugLine="Msgbox(\"Invalid Number\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Number"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 291;BA.debugLine="EditTMHRIN.Text = \"\"";
mostCurrent._edittmhrin.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 293;BA.debugLine="If New < 0 Then";
if ((double)(Double.parseDouble(_new))<0) { 
 //BA.debugLineNum = 294;BA.debugLine="Msgbox(\"Invalid Number\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Number"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 295;BA.debugLine="EditTMHRIN.Text = \"\"";
mostCurrent._edittmhrin.setText(BA.ObjectToCharSequence(""));
 };
 };
 //BA.debugLineNum = 298;BA.debugLine="End Sub";
return "";
}
public static String  _edittmhrout_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 261;BA.debugLine="Sub EditTMHROUT_TextChanged (Old As String, New As";
 //BA.debugLineNum = 262;BA.debugLine="If New <> \"\" Then";
if ((_new).equals("") == false) { 
 //BA.debugLineNum = 263;BA.debugLine="If New >12 Then";
if ((double)(Double.parseDouble(_new))>12) { 
 //BA.debugLineNum = 264;BA.debugLine="Msgbox(\"Invalid Number\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Number"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 265;BA.debugLine="EditTMHROUT.Text = \"\"";
mostCurrent._edittmhrout.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 267;BA.debugLine="If New < 0 Then";
if ((double)(Double.parseDouble(_new))<0) { 
 //BA.debugLineNum = 268;BA.debugLine="Msgbox(\"Invalid Number\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Number"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 269;BA.debugLine="EditTMHROUT.Text = \"\"";
mostCurrent._edittmhrout.setText(BA.ObjectToCharSequence(""));
 };
 };
 //BA.debugLineNum = 272;BA.debugLine="End Sub";
return "";
}
public static String  _edittmminin_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 300;BA.debugLine="Sub EditTMMININ_TextChanged (Old As String, New As";
 //BA.debugLineNum = 301;BA.debugLine="If New <> \"\" Then";
if ((_new).equals("") == false) { 
 //BA.debugLineNum = 302;BA.debugLine="If New >60 Then";
if ((double)(Double.parseDouble(_new))>60) { 
 //BA.debugLineNum = 303;BA.debugLine="Msgbox(\"Invalid Number\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Number"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 304;BA.debugLine="EditTMMININ.Text = \"\"";
mostCurrent._edittmminin.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 306;BA.debugLine="If New < 0 Then";
if ((double)(Double.parseDouble(_new))<0) { 
 //BA.debugLineNum = 307;BA.debugLine="Msgbox(\"Invalid Number\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Number"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 308;BA.debugLine="EditTMMININ.Text = \"\"";
mostCurrent._edittmminin.setText(BA.ObjectToCharSequence(""));
 };
 };
 //BA.debugLineNum = 311;BA.debugLine="End Sub";
return "";
}
public static String  _edittmminout_textchanged(String _old,String _new) throws Exception{
 //BA.debugLineNum = 274;BA.debugLine="Sub EditTMMINOUT_TextChanged (Old As String, New A";
 //BA.debugLineNum = 275;BA.debugLine="If New <> \"\" Then";
if ((_new).equals("") == false) { 
 //BA.debugLineNum = 276;BA.debugLine="If New >60 Then";
if ((double)(Double.parseDouble(_new))>60) { 
 //BA.debugLineNum = 277;BA.debugLine="Msgbox(\"Invalid Number\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Number"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 278;BA.debugLine="EditTMMINOUT.Text = \"\"";
mostCurrent._edittmminout.setText(BA.ObjectToCharSequence(""));
 };
 //BA.debugLineNum = 280;BA.debugLine="If New < 0 Then";
if ((double)(Double.parseDouble(_new))<0) { 
 //BA.debugLineNum = 281;BA.debugLine="Msgbox(\"Invalid Number\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Invalid Number"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 282;BA.debugLine="EditTMMINOUT.Text = \"\"";
mostCurrent._edittmminout.setText(BA.ObjectToCharSequence(""));
 };
 };
 //BA.debugLineNum = 285;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 12;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 16;BA.debugLine="Private EditLoadNo As EditText";
mostCurrent._editloadno = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 17;BA.debugLine="Private EditQty As EditText";
mostCurrent._editqty = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 18;BA.debugLine="Private EditPHNO As EditText";
mostCurrent._editphno = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 19;BA.debugLine="Private LabTMIN As Label";
mostCurrent._labtmin = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 20;BA.debugLine="Private LabTMOUT As Label";
mostCurrent._labtmout = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 21;BA.debugLine="Private EditSBT As EditText";
mostCurrent._editsbt = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Private EditDTAHR As EditText";
mostCurrent._editdtahr = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 23;BA.debugLine="Private EditDTAMIN As EditText";
mostCurrent._editdtamin = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Private EditDTASEC As EditText";
mostCurrent._editdtasec = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Private EditQTAHR As EditText";
mostCurrent._editqtahr = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Private EditQTAMIN As EditText";
mostCurrent._editqtamin = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 27;BA.debugLine="Private EditQTASEC As EditText";
mostCurrent._editqtasec = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Private EditQTS As EditText";
mostCurrent._editqts = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 29;BA.debugLine="Private LabQTF As Label";
mostCurrent._labqtf = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 30;BA.debugLine="Private EditQTF As EditText";
mostCurrent._editqtf = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 34;BA.debugLine="Dim Acceptparts() As String";
mostCurrent._acceptparts = new String[(int) (0)];
java.util.Arrays.fill(mostCurrent._acceptparts,"");
 //BA.debugLineNum = 35;BA.debugLine="Private EditTMHRIN As EditText";
mostCurrent._edittmhrin = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 36;BA.debugLine="Private EditTMHROUT As EditText";
mostCurrent._edittmhrout = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 37;BA.debugLine="Private EditTMMININ As EditText";
mostCurrent._edittmminin = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 38;BA.debugLine="Private EditTMMINOUT As EditText";
mostCurrent._edittmminout = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 39;BA.debugLine="Private TGBUTIN As ToggleButton";
mostCurrent._tgbutin = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 40;BA.debugLine="Private TGBUTOUT As ToggleButton";
mostCurrent._tgbutout = new anywheresoftware.b4a.objects.CompoundButtonWrapper.ToggleButtonWrapper();
 //BA.debugLineNum = 41;BA.debugLine="End Sub";
return "";
}
public static String  _jobdone(anywheresoftware.b4a.samples.httputils2.httpjob _job) throws Exception{
String _s = "";
String[] _returnstring = null;
 //BA.debugLineNum = 125;BA.debugLine="Sub JobDone (Job As HttpJob)";
 //BA.debugLineNum = 126;BA.debugLine="Dim s As String";
_s = "";
 //BA.debugLineNum = 127;BA.debugLine="If Job.Success = True Then";
if (_job._success==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 128;BA.debugLine="Log(\"Yeaahh\")";
anywheresoftware.b4a.keywords.Common.Log("Yeaahh");
 //BA.debugLineNum = 129;BA.debugLine="Select Job.JobName";
switch (BA.switchObjectToInt(_job._jobname,"acceptfccard")) {
case 0: {
 //BA.debugLineNum = 131;BA.debugLine="s = Job.GetString";
_s = _job._getstring();
 //BA.debugLineNum = 132;BA.debugLine="Dim returnstring() As String";
_returnstring = new String[(int) (0)];
java.util.Arrays.fill(_returnstring,"");
 //BA.debugLineNum = 134;BA.debugLine="returnstring = Regex.Split(CRLF,s)";
_returnstring = anywheresoftware.b4a.keywords.Common.Regex.Split(anywheresoftware.b4a.keywords.Common.CRLF,_s);
 //BA.debugLineNum = 136;BA.debugLine="If returnstring(0)=\"SUCCESS\" Then";
if ((_returnstring[(int) (0)]).equals("SUCCESS")) { 
 //BA.debugLineNum = 137;BA.debugLine="Msgbox(returnstring(1),\"Success\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_returnstring[(int) (1)]),BA.ObjectToCharSequence("Success"),mostCurrent.activityBA);
 //BA.debugLineNum = 138;BA.debugLine="Activity.finish";
mostCurrent._activity.Finish();
 //BA.debugLineNum = 139;BA.debugLine="StartActivity(Main)";
anywheresoftware.b4a.keywords.Common.StartActivity(processBA,(Object)(mostCurrent._main.getObject()));
 }else {
 //BA.debugLineNum = 142;BA.debugLine="Msgbox(returnstring(0) & \" \" & returnstring(1";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence(_returnstring[(int) (0)]+" "+_returnstring[(int) (1)]),BA.ObjectToCharSequence("Error"),mostCurrent.activityBA);
 };
 break; }
}
;
 }else {
 //BA.debugLineNum = 150;BA.debugLine="Msgbox(\"Update Failed\",\"\")";
anywheresoftware.b4a.keywords.Common.Msgbox(BA.ObjectToCharSequence("Update Failed"),BA.ObjectToCharSequence(""),mostCurrent.activityBA);
 //BA.debugLineNum = 151;BA.debugLine="Log(\"Buuuh\")";
anywheresoftware.b4a.keywords.Common.Log("Buuuh");
 };
 //BA.debugLineNum = 153;BA.debugLine="End Sub";
return "";
}
public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 6;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 10;BA.debugLine="End Sub";
return "";
}
}
