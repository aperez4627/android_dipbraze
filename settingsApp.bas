B4A=true
Group=Default Group
ModulesStructureVersion=1
Type=Activity
Version=7.8
@EndOfDesignText@
#Region  Activity Attributes 
	#FullScreen: False
	#IncludeTitle: True
	
#End Region

Sub Process_Globals
	'These global variables will be declared once when the application starts.
	'These variables can be accessed from all modules.

End Sub

Sub Globals
	'These global variables will be redeclared each time the activity is created.
	'These variables can only be accessed from this module.
	Dim p As Phone
	Private EditText1 As EditText
	Private EditText2 As EditText
	Private EditText3 As EditText
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("settings")
	

	p.SetScreenOrientation(0)
	
	EditText1.Text = StateManager.GetSetting("EMP_URL") 
	EditText2.Text = StateManager.GetSetting("ORD_URL")
	EditText3.Text = StateManager.GetSetting("UPDATE_URL")




End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub ButSave_Click
	
	StateManager.setSetting("EMP_URL",EditText1.Text)
	StateManager.setSetting("ORD_URL",EditText2.Text)
	StateManager.setSetting("UPDATE_URL",EditText3.Text)
	
	
	StateManager.SaveSettings
	Main.empscanurl =  StateManager.GetSetting("EMP_URL")
	Main.ordscanurl  =  StateManager.GetSetting("ORD_URL")
	'Dim accepturl As String =  "http://bisapps.biscomputer.com/apex/bis/api/dipfurnace/accept/"
	Main.updateurl  =  StateManager.GetSetting("UPDATE_URL")
	Activity.Finish
	StartActivity(Main)
	p.SetScreenOrientation(-1)
End Sub