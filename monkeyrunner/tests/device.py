from com.android.monkeyrunner import MonkeyRunner

device = None

def connectToDevice():
    """
    Waits to connect to an emulator or physical device, with a timeout of 3 seconds.
    """
    global device

    apkPath = '../MoviesTVSentiments/app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk'
    device = MonkeyRunner.waitForConnection(timeout=3)
    installed = device.installPackage(apkPath)
    if not installed:
        device = None