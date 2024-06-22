# -*- coding: utf-8 -*-
#!/usr/bin/env python
"""
Tests for `modified_porterstemmer` module.
"""
import unittest
import os
import sys
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src')))
from PorterStemmer_Modified import PorterStemmer_Modified


class Test_Modified_Porterstemmer(unittest.TestCase):

    def setUp(self):
        pass

    def test_stem(self):
        stemmer = PorterStemmer_Modified()

        with open('tests/tests.csv') as test_cases:
            for line in test_cases:
                orig, stemmed = line.strip().split(',')
                self.assertEqual(stemmer.stem(orig), stemmed)

        test_cases.close()

    def tearDown(self):
        pass

if __name__ == '__main__':
    unittest.main()