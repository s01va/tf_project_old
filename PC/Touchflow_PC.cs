using System;
using System.IO;
using System.Threading;
using System.Windows.Forms;
using System.Diagnostics;
using System.Linq;
using System.Runtime.InteropServices;

using Microsoft.Win32;

using Touchflow_PC.Properties;

using InTheHand.Net.Bluetooth;
using InTheHand.Net.Sockets;

using WindowsInput;
using WindowsInput.Native;

namespace Touchflow_PC
{
    static class Touchflow_PC
    {
        const int MAXKEYNUM = 4;
        const string Premiere_Process_Name = "Adobe Premiere Pro";
        const string titlename = "Touchflow PC";
        
        [STAThread]
        static void Main()
        {
            var noti = new NotifyIcon();
            Thread notiThread = new Thread(new ThreadStart(() => NotiForm(noti)));
            Thread runThread = new Thread(new ThreadStart(() => Run(noti)));
            notiThread.Start();
            runThread.Start();
            notiThread.Join();
            runThread.Join();
        }
        private static void NotiForm(NotifyIcon noti)
        {
            // Tray icon / Notification
            Application.EnableVisualStyles();
            Application.SetCompatibleTextRenderingDefault(false);

            noti.Text = titlename;
            noti.Icon = Resources.icon;
            noti.ContextMenu = new ContextMenu();

            noti.ContextMenu.MenuItems.Add("설정", option);
            //noti.ContextMenu.MenuItems.Add("종료", new EventHandler((s, e) => Application.Exit()));
            noti.ContextMenu.MenuItems.Add("종료", new EventHandler((s, e) => Environment.Exit(0)));
            // 추후에 이 Environment.Exit(0)을 더 좋은 것으로 바꾸길 바람
            noti.Visible = true;
            Application.Run();
        }

        private static void option(object sender, EventArgs e)
        {
            RegistryKey rkey = Registry.CurrentUser.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);

            Form optionform = new Form();
            optionform.Width = 250;
            optionform.Height = 150;
            optionform.Text = "설정";
            optionform.Icon = Resources.icon;
            optionform.StartPosition = FormStartPosition.CenterScreen;

            CheckBox check_startingp = new CheckBox();
            check_startingp.Text = "시작프로그램으로 등록";
            check_startingp.AutoSize = true;
            check_startingp.Location = new System.Drawing.Point(20, 20);

            Button okbutton = new Button();
            okbutton.Text = "적용";
            okbutton.Location = new System.Drawing.Point((optionform.Width - okbutton.Width)/2, 60);

            if ((string)rkey.GetValue(titlename) == Application.ExecutablePath.ToString())
                check_startingp.Checked = true;
            
            optionform.Controls.Add(check_startingp);
            optionform.Controls.Add(okbutton);
            optionform.Show();

            okbutton.Click += new System.EventHandler((s, e1) => okbutton_clicked(s, e, check_startingp, optionform));
        }
        
        private static void okbutton_clicked(object sender, EventArgs e, CheckBox check_startingp, Form optionform)
        {
            RegistryKey rkey = Registry.CurrentUser.OpenSubKey("SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run", true);
            if (check_startingp.Checked)
            {
                rkey.SetValue(titlename, Application.ExecutablePath.ToString());
                rkey.Close();
                MessageBox.Show("시작프로그램으로 설정하였습니다.");
            }
            else
            {
                rkey.DeleteValue(titlename, false);
                rkey.Close();
                MessageBox.Show("시작프로그램 설정을 해제하였습니다.");
            }
            
            optionform.Refresh();
            optionform.Close();
        }
        
        private static void Run(NotifyIcon noti)
        {
            // Check bluetooth availability
            if (!BluetoothRadio.IsSupported)
            {
                MessageBox.Show("블루투스 기기가 지원되지 않는 기기입니다.\n프로그램을 종료합니다.", titlename);
                return;
            }

            // Bluetooth client setting
            BluetoothClient btClient;
            BluetoothDeviceInfo btInfo;
            BluetoothListener btListener;
            StreamReader btstreamReceiver;
            Boolean btConnected = false;
            int msg;

            try
            {
                btListener = new BluetoothListener(BluetoothService.SerialPort);
                //noti.ShowBalloonTip(30000, titlename, "Bluetooth listener created.", ToolTipIcon.None);
                btListener.Start();
                //noti.ShowBalloonTip(30000, titlename, "Bluetooth listener started.", ToolTipIcon.None);
                noti.ShowBalloonTip(30000, titlename, "블루투스가 준비되었습니다.", ToolTipIcon.None);

                while (true)
                {
                    try
                    {
                        btClient = btListener.AcceptBluetoothClient();
                        btInfo = new BluetoothDeviceInfo(btClient.RemoteEndPoint.Address);
                        btConnected = btClient.Connected;

                        if (btConnected == false)
                        {
                            noti.ShowBalloonTip(30000, titlename, btInfo.DeviceName + "와 연결이 끊어졌습니다.", ToolTipIcon.None);
                            continue;
                        }
                        else
                        {
                            noti.ShowBalloonTip(30000, titlename, btInfo.DeviceName + " 와 성공적으로 연결되었습니다.", ToolTipIcon.None);
                        }

                        try
                        {
                            // Bluetooth: Trying Connection
                            btstreamReceiver = new StreamReader(btClient.GetStream());

                            while (true)
                            {
                                // Receiving message from the client device
                                msg = btstreamReceiver.Read();

                                if (Convert.ToInt32(msg) == -1)
                                {
                                    noti.ShowBalloonTip(30000, titlename, "Disconnected with " + btInfo.DeviceName, ToolTipIcon.None);
                                    break;
                                }
                                else
                                {
                                    // Checking Adobe Premiere Process is runing
                                    Process premiere_process = Process.GetProcessesByName(Premiere_Process_Name).FirstOrDefault();
                                    if (premiere_process == null)
                                    {
                                        noti.ShowBalloonTip(30000, titlename, Premiere_Process_Name + " is not running.", ToolTipIcon.None);
                                    }
                                    else
                                    {
                                        IntPtr hwnd = premiere_process.MainWindowHandle;
                                        IntPtr context = ImmGetDefaultIMEWnd(hwnd);
                                        if (isAlpha(premiere_process, hwnd, context) == true)
                                        {
                                            // Received Message to Key Event
                                            handleKeyEvent(btstreamReceiver, msg);
                                        }
                                        else
                                        {
                                            //context = ImmSetConversionStatus(context, true, 0);
                                            changeHangulAlpha();
                                            // Received Message to Key Event
                                            handleKeyEvent(btstreamReceiver, msg);
                                            //context = ImmSetConversionStatus(context, false, 0);
                                            changeHangulAlpha();
                                        }
                                    }
                                }
                            }
                        }
                        catch
                        {
                            MessageBox.Show("Bluetooth stream Error.", titlename);
                        }
                    }
                    catch
                    {
                        MessageBox.Show("Bluetooth connecting Error.", titlename);
                    }
                }
            }
            catch
            {
                MessageBox.Show("Bluetooth Listener Error.", titlename);
            }
        }

        static void handleKeyEvent(StreamReader btstreamReceiver, int msg_)
        {
            int[] msg = new int[MAXKEYNUM]; // Original message array
            int[] cvtmsg = new int[MAXKEYNUM]; // Converted message array
            int keynum = MAXKEYNUM;

            string AndroidKeycodemsg = "";
            string PCKeycodemsg = "";
            string Keycharmsg = "";

            KeysConverter kc = new KeysConverter();

            msg[0] = msg_;

            try
            {
                for (int c = 1; c < MAXKEYNUM; c++)
                {
                    msg[c] = btstreamReceiver.Read();

                    cvtmsg[c - 1] = convertKeycode(msg[c - 1]);
                    AndroidKeycodemsg = AndroidKeycodemsg + " " + msg[c - 1];
                    PCKeycodemsg = PCKeycodemsg + " " + cvtmsg[c - 1];
                    Keycharmsg = Keycharmsg + " " + kc.ConvertToString(cvtmsg[c - 1]);

                    if (msg[c] == 0)
                    {
                        keynum = c;
                        break;
                    }
                    else
                    {
                        if (c == MAXKEYNUM - 1)
                        {
                            cvtmsg[c] = convertKeycode(msg[c]);
                            AndroidKeycodemsg = AndroidKeycodemsg + " " + msg[c];
                            PCKeycodemsg = PCKeycodemsg + " " + cvtmsg[c];
                            Keycharmsg = Keycharmsg + " " + kc.ConvertToString(cvtmsg[c]);
                            btstreamReceiver.Read();
                        }
                    }
                }

                //log("Pressed Keys:" + Keycharmsg);

                InputSimulator kbsim = new InputSimulator();
                keyupanddown(kbsim, keynum, keynum, cvtmsg);
            }
            catch
            {
                //log("Read Error(" + e.ToString() + ")");
            }
        }

        static void keyupanddown(InputSimulator kbsim, int keynum, int count_, int[] cvtmsg)
        {
            int count = count_ - 1;
            kbsim.Keyboard.KeyDown((VirtualKeyCode)cvtmsg[keynum - count_]);
            if (count != 0)
                keyupanddown(kbsim, keynum, count, cvtmsg);
            kbsim.Keyboard.KeyUp((VirtualKeyCode)cvtmsg[keynum - count_]);
        }

        static int convertKeycode(int msg)
        {
            int result = msg;

            if (msg >= 7 && msg <= 16) // Number key
                result = msg + 41;
            else if (msg >= 29 && msg <= 54) // Alphabet key
                result = msg + 36;
            else // Special key
            {
                switch (msg)
                {
                    case 4: // Backspace
                        result = 8;
                        break;
                    case 113: // Left Control
                        result = 17;
                        break;
                    case 57: // Left Alt
                        result = 164;
                        break;
                    case 59: // Left Shift
                        result = 16;
                        break;
                    case 67: // Delete key
                        result = 46;
                        break;
                    case 19: // DPAD up
                        result = 38;
                        break;
                    case 20: // DPAD down
                        result = 40;
                        break;
                    // Speicial key
                    case 55: // Comma (,)
                        result = 188;
                        break;
                    case 56: // Period (.)
                        result = 190;
                        break;
                    case 62: // Space
                        result = 32;
                        break;
                    case 73: // Backslash (\)
                        result = 226;
                        break;
                }
            }

            return result;
        }

        // Change Hangeul <-> Alphabet
        [DllImport("imm32.dll")]
        static extern IntPtr ImmGetDefaultIMEWnd(IntPtr hWnd);

        [DllImport("imm32.dll")]
        static extern IntPtr ImmGetContext(IntPtr hWnd);

        [DllImport("user32.dll", CharSet = CharSet.Auto)]
        static extern IntPtr SendMessage(IntPtr hWnd, UInt32 Msg, IntPtr wParam, IntPtr IParam);

        [DllImport("imm32.dll")]
        static extern IntPtr ImmSetConversionStatus(IntPtr hWnd, bool fdwConversation, Int32 fdwSentence);

        const int WM_IME_CONTROL = 643;

        static bool isAlpha(Process p, IntPtr hwnd, IntPtr context)
        {
            IntPtr status = SendMessage(context, WM_IME_CONTROL, new IntPtr(0x5), new IntPtr(0));

            if (status.ToInt32() != 0)
                return false;
            else
                return true;
        }
        static void changeHangulAlpha()
        {
            //log("Pressed 한/영");
            InputSimulator kbsim = new InputSimulator();
            kbsim.Keyboard.KeyPress(VirtualKeyCode.HANGEUL);
        }   
    }
}
