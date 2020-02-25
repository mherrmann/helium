def inverse(dictionary):
	"""
	{a: {b}} -> {b: {a}}
	"""
	result = {}
	for key, values in dictionary.items():
		for value in values:
			if value not in result:
				result[value] = set()
			result[value].add(key)
	return result