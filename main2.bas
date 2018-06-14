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

	Private EditLoadNo As EditText
	Private EditQty As EditText
	Private EditPHNO As EditText
	Private LabTMIN As Label
	Private LabTMOUT As Label
	Private EditSBT As EditText
	Private EditDTAHR As EditText
	Private EditDTAMIN As EditText
	Private EditDTASEC As EditText
	Private EditQTAHR As EditText
	Private EditQTAMIN As EditText
	Private EditQTASEC As EditText
	Private EditQTS As EditText
	Private LabQTF As Label
	Private EditQTF As EditText
	
	
	''
	Dim Acceptparts() As String
	Private EditTMHRIN As EditText
	Private EditTMHROUT As EditText
	Private EditTMMININ As EditText
	Private EditTMMINOUT As EditText
	Private TGBUTIN As ToggleButton
	Private TGBUTOUT As ToggleButton
End Sub

Sub Activity_Create(FirstTime As Boolean)
	'Do not forget to load the layout file created with the visual designer. For example:
	Activity.LoadLayout("main2")
	
	If Main.Parts.Length > 0 Then
	'''''''''''''''''''''''''''''
	LabTMIN.Text = Main.parts(4) ''PREHEAT_TIME_IN
	LabTMOUT.Text = Main.parts(5)''PREHEAT_TIME_OUT
	'''''''''''''''''''''''''''''''''''
	EditPHNO.Text = Main.parts(6) '' PREHEAT_OVEN_NO test
	EditQty.Text = Main.parts(7) ''qty
	EditLoadNo.Text = Main.parts(8) '' Load No
	EditSBT.Text = Main.parts(9) ''SALT_BATH_TEMP_ACTUAL
	
	
	EditDTAHR.Text = Main.parts(10) ''DELAY_TIME_ACTUAL_HR
	EditDTAMIN.Text = Main.parts(11) ''DELAY_TIME_ACTUAL_MIN
	EditDTASEC.Text = Main.parts(12) ''DELAY_TIME_ACTUAL_SEC
	
	EditQTAHR.Text = Main.parts(13) ''QUENCH_TIME_ACTUAL_HR
	EditQTAMIN.Text = Main.parts(14) ''QUENCH_TIME_ACTUAL_MIN
	EditQTASEC.Text = Main.parts(15) ''QUENCH_TIME_ACTUAL_SEC
	
	EditQTS.Text = Main.parts(16) ''QUENCH_TEMP_START
	EditQTF.Text = Main.parts(17) ''QUENCH_TEMP_FINISH
	
	
		EditTMHRIN.Text =  Main.parts(18)
		EditTMMININ.Text =  Main.parts(19)
		
		If  Main.parts(20) = "AM" Then
		TGBUTIN.Checked = True
		End If
		
		If  Main.parts(19) = "PM" Then
			TGBUTIN.Checked = False
		End If
		
		EditTMHROUT.Text =  Main.parts(21)
		EditTMMINOUT.Text =  Main.parts(22)
		
		If  Main.parts(23) = "AM" Then
			TGBUTOUT.Checked = True
		End If
		
		If  Main.parts(23) = "PM" Then
			TGBUTOUT.Checked = False
		End If
		
		
		
	
	
	End If
	'' pk part(1),parts(2),parts(21)
	
	
End Sub

Sub Activity_Resume

End Sub

Sub Activity_Pause (UserClosed As Boolean)

End Sub


Sub BuAccept_Click
'	If EditLoadNo.Text = "" Then
'		
'	  Msgbox("Load No can not be blank","Error")
'	  
'		
'	Else
		
		callaccept
		
'End If
	
	
End Sub
Sub JobDone (Job As HttpJob)
	Dim s As String
	If Job.Success = True Then
		Log("Yeaahh")
		Select Job.JobName
			Case "acceptfccard"
				s = Job.GetString
				Dim returnstring() As String
			
				returnstring = Regex.Split(CRLF,s)
			
			If returnstring(0)="SUCCESS" Then
					Msgbox(returnstring(1),"Success")
					Activity.finish
					StartActivity(Main)
			Else
				
					Msgbox(returnstring(0) & " " & returnstring(1),"Error")
					
			End If
			
			
				'ParsJSON(s,"2")
		End Select
	Else
		Msgbox("Update Failed","")
		Log("Buuuh")
	End If
End Sub
Sub callaccept'
	Dim s As String
	Dim job1 As HttpJob
	's = Main.accepturl & Main.parts(1) & "~" & Main.parts(2) & "~" & Main.parts(21) & "~" & Main.parts(14) & "~" & Main.parts(15)
	job1.Initialize("acceptfccard", Me)
	'job1.Download2(Main.accepturl,Main.parts )
	'job1.Download2(Main.accepturl,)
	'job1.(Main.updateurl,("dsdds"))


	Dim JsonMap As Map
	JsonMap.Initialize
	
	
	Main.parts(6) = EditPHNO.Text
	Main.parts(7) = EditQty.Text
	Main.parts(8) = EditLoadNo.Text
	
	
	Main.parts(9) = EditSBT.Text
	
	Main.parts(10) = EditDTAHR.Text
	Main.parts(11) = EditDTAMIN.Text
	Main.parts(12) = EditDTASEC.Text
	
	Main.parts(13) = EditQTAHR.Text
	Main.parts(14) = EditQTAMIN.Text
	Main.parts(15) = EditQTASEC.Text
	
	Main.parts(16) = EditQTS.Text
	Main.parts(17) = EditQTF.Text
	
	Main.parts(18) = EditTMHRIN.Text
	Main.parts(19) = EditTMMININ.Text
	
	If TGBUTIN.Checked = True Then
		Main.parts(20) = "AM"
		
	Else
		Main.parts(20) = "PM"
	End If
	
	
	Main.parts(21) = EditTMHROUT.Text
	Main.parts(22) = EditTMMINOUT.Text
	
	If TGBUTOUT.Checked = True Then
		Main.parts(23) = "AM"
		
	Else
		Main.parts(23) = "PM"
	End If
	
	
	JsonMap.Put("ORDER_NUMBER",Main.parts(1))
	JsonMap.Put("FC_NO",Main.parts(2))
	JsonMap.Put("SCH_REC_ID",Main.parts(3))
	JsonMap.Put("PREHEAT_TIME_IN",Main.parts(4))
	JsonMap.Put("PREHEAT_TIME_OUT",Main.parts(5))
	JsonMap.Put("PREHEAT_OVEN_NO",Main.parts(6))
	JsonMap.Put("QTY",Main.parts(7))
	JsonMap.Put("LOAD_NO",Main.parts(8))
	JsonMap.Put("SALT_BATH_TEMP_ACTUAL",Main.parts(9))
	JsonMap.Put("DELAY_TIME_ACTUAL_HR",Main.parts(10))
	JsonMap.Put("DELAY_TIME_ACTUAL_MIN",Main.parts(11))
	JsonMap.Put("DELAY_TIME_ACTUAL_SEC",Main.parts(12))
	JsonMap.Put("QUENCH_TIME_ACTUAL_HR",Main.parts(13))
	JsonMap.Put("QUENCH_TIME_ACTUAL_MIN",Main.parts(14))
	JsonMap.Put("QUENCH_TIME_ACTUAL_SEC",Main.parts(15))
	
	JsonMap.Put("QUENCH_TEMP_START",Main.parts(16))
	JsonMap.Put("QUENCH_TEMP_FINISH",Main.parts(17))

	JsonMap.Put("PREHEAT_TIME_IN_HR",Main.parts(18))
	JsonMap.Put("PREHEAT_TIME_IN_MIN",Main.parts(19))
	JsonMap.Put("PREHEAT_TIME_IN_PM",Main.parts(20))
	
	JsonMap.Put("PREHEAT_TIME_OUT_HR",Main.parts(21))
	JsonMap.Put("PREHEAT_TIME_OUT_MIN",Main.parts(22))
	JsonMap.Put("PREHEAT_TIME_OUT_PM",Main.parts(23))
	
	
	Dim JSONGenerator As JSONGenerator
	JSONGenerator.Initialize(JsonMap)
	job1.PostString(Main.updateurl,JSONGenerator.ToPrettyString(2))
	job1.GetRequest.SetContentType("application/json")
	
	

'	job1.Download(s)
End Sub

	








Sub ButExit_Click
	
	Activity.Finish
	StartActivity(Main)
End Sub

Sub EditTMHROUT_TextChanged (Old As String, New As String)
	If New <> "" Then
	If New >12 Then
		Msgbox("Invalid Number","")
		EditTMHROUT.Text = "" 
		End If
		If New < 0 Then
			Msgbox("Invalid Number","")
			EditTMHROUT.Text = ""
		End If
	End If
End Sub

Sub EditTMMINOUT_TextChanged (Old As String, New As String)
	If New <> "" Then
		If New >60 Then
			Msgbox("Invalid Number","")
			EditTMMINOUT.Text = ""
		End If
		If New < 0 Then
			Msgbox("Invalid Number","")
			EditTMMINOUT.Text = ""
		End If
	End If
End Sub

Sub EditTMHRIN_TextChanged (Old As String, New As String)
	If New <> "" Then
		If New >12 Then
			Msgbox("Invalid Number","")
			EditTMHRIN.Text = ""
		End If
		If New < 0 Then
			Msgbox("Invalid Number","")
			EditTMHRIN.Text = ""
		End If
	End If
End Sub

Sub EditTMMININ_TextChanged (Old As String, New As String)
	If New <> "" Then
		If New >60 Then
			Msgbox("Invalid Number","")
			EditTMMININ.Text = ""
		End If
		If New < 0 Then
			Msgbox("Invalid Number","")
			EditTMMININ.Text = ""
		End If
	End If
End Sub