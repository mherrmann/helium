package com.heliumhq.site;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.min;
import static java.util.Arrays.asList;

public class ParamCombination implements Iterable<List<Param>> {
	private final List<List<Param>> params;
	public ParamCombination() {
		this.params = new ArrayList<List<Param>>();
	}

	public void add(Param param) {
		params.add(new ArrayList<Param>(asList(param)));
	}

	public boolean join(ParamCombination other) {
		int commonSuffixLength = getCommonSuffixLength(other);
		int thisRemaining = params.size() - commonSuffixLength;
		int otherRemaining = other.params.size() - commonSuffixLength;
		if (thisRemaining < otherRemaining)
			return false;
		for (int i = otherRemaining - 1; i >= 0; i--) {
			List<Param> thisParams = params.get(i);
			List<Param> otherParams = other.params.get(i);
			if (allAreEqual(thisParams, otherParams))
				return true;
			if (! allAreOptional(thisParams) || ! allAreOptional(otherParams))
				return false;
		}
		for (int i = otherRemaining - 1; i >= 0; i--) {
			List<Param> thisParams = params.get(i);
			List<Param> otherParams = other.params.get(i);
			thisParams.addAll(otherParams);
		}
		return true;
	}

	private int getCommonSuffixLength(ParamCombination other) {
		int result = 0;
		final int thisSize = params.size();
		final int otherSize = other.params.size();
		while (result < min(thisSize, otherSize)) {
			List<Param> thisParams = params.get(thisSize - result - 1);
			List<Param> otherParams = other.params.get(otherSize - result - 1);
			if (thisParams.size() != 1 || otherParams.size() != 1)
				break;
			Param thisParam = thisParams.get(0);
			Param otherParam = otherParams.get(0);
			if (! thisParam.getName().equals(otherParam.getName()))
				break;
			result ++;
		}
		return result;
	}

	private boolean allAreOptional(List<Param> params) {
		for (Param param : params)
			if (! param.isOptional())
				return false;
		return true;
	}

	private boolean allAreEqual(List<Param> first, List<Param> second) {
		if (first.size() != second.size())
			return false;
		for (int i = 0; i < first.size(); i++)
			if (! first.get(i).equals(second.get(i)))
				return false;
		return true;
	}

	@Override
	public Iterator<List<Param>> iterator() {
		return params.iterator();
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("ParamCombination{");
		EnumerationHelper paramHelper = new EnumerationHelper();
		for (List<Param> paramAlternatives : params) {
			result.append(paramHelper.next());
			EnumerationHelper paramAltHelper = new EnumerationHelper();
			result.append("[");
			for (Param param : paramAlternatives) {
				result.append(paramAltHelper.next());
				result.append(param.getName());
			}
			result.append("]");
		}
		result.append('}');
		return result.toString();
	}
}
