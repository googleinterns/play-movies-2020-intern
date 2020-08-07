import os
import sys
import pkgutil
import unittest
from tests import device


device.connectToDevice()

pkg_dir = os.path.dirname(__file__)
for (module_loader, name, ispkg) in pkgutil.iter_modules([pkg_dir]):
    exec('import ' + name)

testClasses = [cls for cls in unittest.TestCase.__subclasses__() if 'tests' in str(cls)]
