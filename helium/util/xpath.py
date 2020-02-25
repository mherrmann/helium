# -*- coding: utf-8 -*-
def lower(text):
	alphabet = 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝ'
	return "translate(%s, '%s', '%s')" % (text, alphabet, alphabet.lower())

def replace_nbsp(text, by=' '):
	return "translate(%s, '\u00a0', %r)" % (text, by)

def predicate(condition):
	return '[%s]' % condition if condition else ''

def predicate_or(*conditions):
	return predicate(' or '.join([c for c in conditions if c]))