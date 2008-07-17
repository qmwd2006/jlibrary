/*
* jLibrary, Open Source Document Management System
* 
* Copyright (c) 2003-2006, Martín Pérez Mariñán, Blandware (represented by
* Andrey Grebnev), and individual contributors as indicated by the
* @authors tag. See copyright.txt in the distribution for a full listing of
* individual contributors. All rights reserved.
* 
* This is free software; you can redistribute it and/or modify it
* under the terms of the Modified BSD License as published by the Free 
* Software Foundation.
* 
* This software is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the Modified
* BSD License for more details.
* 
* You should have received a copy of the Modified BSD License along with 
* this software; if not, write to the Free Software Foundation, Inc., 
* 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the
* FSF site: http://www.fsf.org.
*/
package org.jlibrary.core.search.algorithms;

import java.util.Iterator;
import java.util.Set;

import org.jlibrary.core.entities.Node;
import org.jlibrary.core.search.SearchException;
import org.jlibrary.core.search.SearchHit;

/**
 * Default search algorithm. This algorithm will take the search results and 
 * will ponderate them through a importance-based factor. The algorithm will 
 * work as follows:
 * <p/>
 * <ul>
 * <li>Take each result and calculate the nearest distance to a border. If 
 * the result is near to the high limit then the difference from the high 
 * limit to the result will be taken; on the other hand, if the result is 
 * near to the low limit, then the difference to the low limit will be taken.</li>
 * <li>Divide that differente by 2</li>
 * <li>Multiply that difference by a factor based on the importance. If the 
 * node has the higher importance then it will be multiplied by 1. If the 
 * node has the lower importance then his score will be multiplied by -1.</li>
 * <li>Add the result of the above operation to the node score.</li>
 * </ul>
 * This algorithm will move results near to the medium on a higher degree than 
 * results near to the limits. So it promotes the average.
 * <p/>
 * @author mpermar
 *
 */
public class DefaultSearchAlgorithm implements SearchAlgorithm {

	/**
	 * @see SearchAlgorithm#filterSearchResults(Set)
	 */
	public Set filterSearchResults(Set set) throws SearchException {

		Iterator it = set.iterator();
		while (it.hasNext()) {
			SearchHit hit = (SearchHit) it.next();
			
			double importanceFactor = mapImportance(hit.getImportance());
			double score = hit.getScore();
			double differenceFactor;
			if (1 - score > 0.5) {
				differenceFactor = score / 2;
			} else {
				differenceFactor = (1 - score) / 2;
			}
			differenceFactor*=importanceFactor;
			hit.setScore(score+differenceFactor);
		}
		
		return set;
	}

	private double mapImportance(Integer importance) {

		if (importance.equals(Node.IMPORTANCE_HIGHEST)) {
			return 1;
		} else if (importance.equals(Node.IMPORTANCE_HIGH)) {
			return 0.5;
		} else if (importance.equals(Node.IMPORTANCE_MEDIUM)) {
			return 0;
		} else if (importance.equals(Node.IMPORTANCE_LOW)) {
			return -0.5;
		} else if (importance.equals(Node.IMPORTANCE_LOWEST)) {
			return -1;
		}
		return 0;
	}

}
