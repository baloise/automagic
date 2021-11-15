package com.baloise.automagic.common;

/**
 * Alternative constructor to allow CPS transformed calls 
 * see https://www.jenkins.io/doc/book/pipeline/cps-method-mismatches/#constructors
 */
interface Constructed<T,P> {
	T construct(P params)
}
