/*******************************************************************************
 * Copyright (c) 2006, 2014 THALES GLOBAL SERVICES.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *  
 * Contributors:
 *    Thales - initial API and implementation
 *******************************************************************************/
package org.polarsys.capella.core.business.queries.queries.oa;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacommon.State;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.interaction.AbstractCapability;

public class GetCurrent_OperationalCapability_AvailableInStates extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> currentElements = getCurrentElements(capellaElement, false);
		return (List) currentElements;
	}

	/** 
	 * @see org.polarsys.capella.core.business.queries.capellacore.IBusinessQuery#getCurrentElements(org.polarsys.capella.core.data.capellacore.CapellaElement,boolean)
	 */
	public List<CapellaElement> getCurrentElements(CapellaElement element_p, boolean onlyGenerated_p) {
		List<CapellaElement> currentElements = new ArrayList<CapellaElement>();
		if (element_p instanceof AbstractCapability) {
			AbstractCapability ele = (AbstractCapability) element_p;
			EList<State> availableInStates = ele.getAvailableInStates();
			for (State abstractStateMode : availableInStates) {
				currentElements.add(abstractStateMode);
			}
		}
		return currentElements;
	}

}