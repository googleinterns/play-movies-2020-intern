from com.android.monkeyrunner import MonkeyRunner, MonkeyDevice, MonkeyImage
import os
import unittest
from tests import constants
from tests.device import device

class TestSigninActivity(unittest.TestCase):
    def setUp(self):
        """
        Launches the Activity under test. Note that device.startActivity does not actually wait for the Activity to finish launching, so
        the test must handle synchronization itself. This is another reason to avoid MonkeyRunner.
        """
        device.startActivity(component=constants.PACKAGE_NAME + '/' + constants.PACKAGE_NAME + '.usecase.signin.SigninActivity')
        MonkeyRunner.sleep(2)

    def tearDown(self):
        """
        Kills the Application under test and returns to the home screen. 
        """
        device.shell('am force-stop ' + constants.PACKAGE_NAME)

    def testSigninActivity_displaysCorrectlyOnLaunch(self):
        screenshotName = 'testSigninActivity_displaysCorrectlyOnLaunch.png'
        expected = MonkeyRunner.loadImageFromFile(constants.EXPECTED_SCREENSHOT_DIR + screenshotName)

        actual = device.takeSnapshot()

        actual.writeToFile(constants.ACTUAL_SCREENSHOT_DIR + screenshotName, 'png')
        self.assertTrue(actual.sameAs(expected, 0.95))

    def testSigninActivity_tapAddAccount_displaysAddAccountActivity(self):
        screenshotName = 'testSigninActivity_tapAddAccount_displaysAddAccountActivity.png'
        expected = MonkeyRunner.loadImageFromFile(constants.EXPECTED_SCREENSHOT_DIR + screenshotName)
        addAccountButtonX = 154
        addAccountButtonY = 445

        device.touch(addAccountButtonX, addAccountButtonY, MonkeyDevice.DOWN_AND_UP)
        MonkeyRunner.sleep(1)
        actual = device.takeSnapshot()
        
        actual.writeToFile(constants.ACTUAL_SCREENSHOT_DIR + screenshotName, 'png')
        self.assertTrue(actual.sameAs(expected, 0.95))