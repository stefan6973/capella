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
package org.polarsys.capella.core.business.queries.cs;

import java.util.Collections;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.polarsys.capella.common.queries.interpretor.QueryInterpretor;
import org.polarsys.capella.common.queries.queryContext.QueryContext;
import org.polarsys.capella.core.business.queries.IBusinessQuery;
import org.polarsys.capella.core.business.queries.QueryConstants;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.CsPackage;

/**
 * Query that returns all exchange items in the model from System Engineering.
 */
public class Interface_ExchangeItems implements IBusinessQuery {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EClass getEClass() {
		return CsPackage.Literals.INTERFACE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<EReference> getEStructuralFeatures() {
		return Collections.singletonList(CsPackage.Literals.INTERFACE__OWNED_EXCHANGE_ITEM_ALLOCATIONS);
	}
	
	@Override
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		QueryContext context = new QueryContext();
		context.putValue(QueryConstants.ECLASS_PARAMETER, getEClass());
		return QueryInterpretor.executeQuery(QueryConstants.GET_AVAILABLE__INTERFACE__EXCHANGE_ITEMS___LIB, element_p, context);
	}

	@Override
	public List<CapellaElement> getCurrentElements(CapellaElement element_p, boolean onlyGenerated_p) {
		QueryContext context = new QueryContext();
		context.putValue(QueryConstants.ECLASS_PARAMETER, getEClass());
		return QueryInterpretor.executeQuery(QueryConstants.GET_CURRENT__INTERFACE__EXCHANGE_ITEMS, element_p, context);
	}
}
