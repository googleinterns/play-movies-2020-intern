from os.path import isfile, join
import unittest
import sys
import inspect
import tests
from tests.device import device

testDirectory = 'tests'

def runTests():
    """
    Runs all methods in the tests folder that start with `test`.
    """
    suite = unittest.TestSuite()
    for testClass in tests.testClasses:
        for method in dir(testClass):
            if method.startswith("test"):
                suite.addTest(testClass(method))

    if device == None:
        print('Failed to connect to device or install apk. No tests will be run.')
    else:
        unittest.TextTestRunner().run(suite)


if __name__ == '__main__':
    runTests()
