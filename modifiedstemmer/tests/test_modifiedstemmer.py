#!/usr/bin/env python
# -*- coding: utf-8 -*-


"""
Tests for `modifiedstemmer` module.
"""
import unittest
import os
import sys
sys.path.append(os.path.abspath(os.path.join(os.path.dirname(__file__), '..', 'src/modifiedstemmer')))
from PorterStemmer_Modified import PorterStemmer_Modified


class Test_Modified_stemmer(unittest.TestCase):

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