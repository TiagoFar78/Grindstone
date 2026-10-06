@echo off
cd /d "%~dp0"
powershell -NoProfile -ExecutionPolicy Bypass -Command "iex ((Get-Content -LiteralPath '%~f0' -Raw) -split '(?m)^#PS#')[1]"
exit /b
#PS#
Add-Type @"
using System;
using System.Text;
using System.Runtime.InteropServices;
public class Win {
    [StructLayout(LayoutKind.Sequential)] public struct RECT { public int L, T, R, B; }
    [StructLayout(LayoutKind.Sequential, CharSet = CharSet.Unicode)]
    public struct STARTUPINFO {
        public int cb; public string lpReserved; public string lpDesktop; public string lpTitle;
        public int dwX, dwY, dwXSize, dwYSize, dwXCountChars, dwYCountChars, dwFillAttribute, dwFlags;
        public short wShowWindow, cbReserved2; public IntPtr lpReserved2, hStdInput, hStdOutput, hStdError;
    }
    [StructLayout(LayoutKind.Sequential)]
    public struct PROCESS_INFORMATION { public IntPtr hProcess, hThread; public int dwProcessId, dwThreadId; }

    [DllImport("user32.dll")] public static extern bool SetProcessDPIAware();
    [DllImport("user32.dll", CharSet = CharSet.Unicode)] public static extern IntPtr FindWindow(string cls, string title);
    [DllImport("user32.dll")] public static extern bool MoveWindow(IntPtr h, int x, int y, int w, int ht, bool repaint);
    [DllImport("user32.dll")] public static extern bool GetWindowRect(IntPtr h, out RECT r);
    [DllImport("user32.dll")] public static extern uint GetWindowThreadProcessId(IntPtr h, out uint pid);
    [DllImport("dwmapi.dll")] public static extern int DwmGetWindowAttribute(IntPtr h, int attr, out RECT r, int size);
    [DllImport("kernel32.dll")] static extern bool CloseHandle(IntPtr h);
    [DllImport("user32.dll")] static extern IntPtr GetAncestor(IntPtr h, uint flags);
    [DllImport("kernel32.dll")] static extern bool FreeConsole();
    [DllImport("kernel32.dll")] static extern bool AttachConsole(uint pid);
    [DllImport("kernel32.dll")] static extern IntPtr GetConsoleWindow();
    [DllImport("kernel32.dll", CharSet = CharSet.Unicode, SetLastError = true)]
    static extern bool CreateProcess(string app, StringBuilder cmd, IntPtr pa, IntPtr ta, bool inherit, uint flags,
        IntPtr env, string dir, ref STARTUPINFO si, out PROCESS_INFORMATION pi);

    // Starts a command in a NEW console window that opens already at x,y with size w,h (pixels)
    public static uint Launch(string cmdLine, string dir, string title, int x, int y, int w, int h) {
        STARTUPINFO si = new STARTUPINFO();
        si.cb = Marshal.SizeOf(typeof(STARTUPINFO));
        si.lpTitle = title;
        si.dwX = x; si.dwY = y; si.dwXSize = w; si.dwYSize = h;
        si.dwFlags = 0x2 | 0x4;                 // STARTF_USESIZE | STARTF_USEPOSITION
        PROCESS_INFORMATION pi;
        if (!CreateProcess(null, new StringBuilder(cmdLine), IntPtr.Zero, IntPtr.Zero, false, 0x10, IntPtr.Zero, dir, ref si, out pi)) return 0;
        CloseHandle(pi.hThread); CloseHandle(pi.hProcess);
        return (uint)pi.dwProcessId;
    }

    // Window handle of the console used by process pid (independent of the window title)
    public static IntPtr ConsoleHwnd(uint pid) {
        FreeConsole();
        IntPtr h = IntPtr.Zero;
        if (AttachConsole(pid)) { h = GetConsoleWindow(); FreeConsole(); }
        if (h != IntPtr.Zero) {
            // Windows Terminal: the console window is hidden and owned by the real, visible window
            IntPtr root = GetAncestor(h, 3);   // GA_ROOTOWNER
            if (root != IntPtr.Zero) h = root;
        }
        return h;
    }

    public static uint OwnerPid(IntPtr h) { uint p; GetWindowThreadProcessId(h, out p); return p; }

    // Fine adjustment: makes the VISIBLE area match x,y,w,h (compensates invisible borders)
    public static void Place(IntPtr h, int x, int y, int w, int ht) {
        RECT a, b;
        MoveWindow(h, x, y, w, ht, true);
        GetWindowRect(h, out a);
        if (DwmGetWindowAttribute(h, 9, out b, 16) != 0) return;
        int l = b.L - a.L, t = b.T - a.T, r = a.R - b.R, bo = a.B - b.B;
        MoveWindow(h, x - l, y - t, w + l + r, ht + t + bo, true);
    }
}
"@
[Win]::SetProcessDPIAware() | Out-Null   # must run before reading the screen size
Add-Type -AssemblyName System.Windows.Forms

# ---- Settings (seconds) ----
$serverWait = 0.1

# ---- Layout: server = left half, Player1 = top right, Player2 = bottom right ----
$wa = [System.Windows.Forms.Screen]::PrimaryScreen.WorkingArea
$halfW = [int]($wa.Width / 2)
$halfH = [int]($wa.Height / 2)
$shell = New-Object -ComObject WScript.Shell
$dir = (Get-Location).Path

function Start-Placed($title, $bat, $x, $y, $w, $h) {
    $cpid = [Win]::Launch("cmd.exe /c $bat", $dir, $title, $x, $y, $w, $h)
    $hwnd = [IntPtr]::Zero
    if ($cpid -ne 0) {
        for ($i = 0; $i -lt 100 -and $hwnd -eq [IntPtr]::Zero; $i++) {
            $hwnd = [Win]::ConsoleHwnd($cpid)
            if ($hwnd -eq [IntPtr]::Zero) { Start-Sleep -Milliseconds 10 }
        }
    }
    if ($hwnd -ne [IntPtr]::Zero) {
        [Win]::Place($hwnd, $x, $y, $w, $h)
        Start-Sleep -Milliseconds 30
        [Win]::Place($hwnd, $x, $y, $w, $h)
    }
    return @{ Hwnd = $hwnd; Title = $title }
}

function Send-Name($win, $name) {
    $owner = 0
    if ($win.Hwnd -ne [IntPtr]::Zero) { $owner = [Win]::OwnerPid($win.Hwnd) }
    if ($owner -eq 0 -or -not $shell.AppActivate([int]$owner)) { $shell.AppActivate($win.Title) | Out-Null }
    Start-Sleep -Milliseconds 30
    [System.Windows.Forms.SendKeys]::SendWait("$name{ENTER}")
}

$server = Start-Placed 'Game-Server' 'run-server.bat' $wa.X $wa.Y $halfW $wa.Height
Start-Sleep -Seconds $serverWait

$c1 = Start-Placed 'Game-Player1' 'run-client.bat' ($wa.X + $halfW) $wa.Y $halfW $halfH
Send-Name $c1 'Player1'

$c2 = Start-Placed 'Game-Player2' 'run-client.bat' ($wa.X + $halfW) ($wa.Y + $halfH) $halfW $halfH
Send-Name $c2 'Player2'
