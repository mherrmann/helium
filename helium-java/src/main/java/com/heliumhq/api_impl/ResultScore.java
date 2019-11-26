package com.heliumhq.api_impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

class ResultScore<T> implements Comparable<ResultScore<T>> {
	public static <R> List<R> getSortedResults(
			Collection<ResultScore<R>> resultScores
	) {
		List<ResultScore<R>> sortedResultScores =
				new ArrayList<ResultScore<R>>();
		sortedResultScores.addAll(resultScores);
		Collections.sort(sortedResultScores);
		List<R> result = new ArrayList<R>();
		for (ResultScore<R> resultScore : sortedResultScores)
			result.add(resultScore.result);
		return result;
	}
	final T result;
	double score;
	ResultScore(T result) {
		this(result, 0);
	}
	ResultScore(T result, double score) {
		this.result = result;
		this.score = score;
	}
	@Override
	public int compareTo(ResultScore<T> other) {
		return (int) Math.round(score - other.score);
	}
}