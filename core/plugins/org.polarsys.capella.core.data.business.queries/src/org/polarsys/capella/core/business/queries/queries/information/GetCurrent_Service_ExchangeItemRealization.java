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
package org.polarsys.capella.core.business.queries.queries.information;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.polarsys.capella.common.data.modellingcore.AbstractExchangeItem;
import org.polarsys.capella.common.data.modellingcore.TraceableElement;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.information.ExchangeItemRealization;
import org.polarsys.capella.core.data.information.Service;

public class GetCurrent_Service_ExchangeItemRealization extends AbstractQuery {

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
		List<CapellaElement> returnedList = new ArrayList<CapellaElement>();
		if (element_p instanceof Service) {
			Service ele = (Service) element_p;
			EList<ExchangeItemRealization> ownedExchangeItemRealisation = ele.getOwnedExchangeItemRealizations();
			for (ExchangeItemRealization exchangeItemRealisation : ownedExchangeItemRealisation) {
				TraceableElement targetElement = exchangeItemRealisation.getTargetElement();
				if (null != targetElement && targetElement instanceof AbstractExchangeItem) {
					returnedList.add((CapellaElement) targetElement);
				}
			}
		}
		return returnedList;
	}

}