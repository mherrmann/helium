from helium._impl.util.lang import isbound
import inspect

def repr_args(f, args=None, kwargs=None, repr_fn=repr):
	if args is None:
		args = []
	if kwargs is None:
		kwargs = {}
	arg_names, _, _, defaults = inspect.getfullargspec(f)[:4]
	if isbound(f):
		# Skip 'self' parameter:
		arg_names = arg_names[1:]
	num_defaults = 0 if defaults is None else len(defaults)
	num_requireds = len(arg_names) - num_defaults
	result = []
	for i, arg_name in enumerate(arg_names):
		has_default = i >= len(arg_names) - num_defaults
		if has_default:
			default_value = defaults[i - num_requireds]
		if i < len(args): # Normal arg
			value = args[i]
			prefix = ''
			value_is_default = has_default and value == default_value
		elif arg_name in kwargs: # Keyword arg
			value = kwargs[arg_name]
			prefix = arg_name + '='
			value_is_default = has_default and value == default_value
		else: # Optional arg without given value
			value_is_default = True
		if not value_is_default:
			result.append(prefix + repr_fn(value))
	for vararg in args[len(arg_names):]:
		result.append(repr_fn(vararg))
	for kwarg in kwargs:
		if kwarg not in arg_names:
			result.append(kwarg + '=' + repr_fn(kwargs[kwarg]))
	return ', '.join(result)