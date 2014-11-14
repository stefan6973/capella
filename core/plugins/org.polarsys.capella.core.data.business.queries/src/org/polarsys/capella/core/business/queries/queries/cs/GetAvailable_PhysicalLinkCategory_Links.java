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
package org.polarsys.capella.core.business.queries.queries.cs;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.polarsys.capella.common.helpers.EObjectExt;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.business.queries.IBusinessQuery;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.cs.CsPackage;
import org.polarsys.capella.core.data.cs.PhysicalLinkCategory;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_PhysicalLinkCategory_Links extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 * @see IBusinessQuery#getAvailableElements(org.polarsys.capella.core.common.model.CapellaElement)
	 */
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		if (element_p instanceof PhysicalLinkCategory) {
			BlockArchitecture arch = SystemEngineeringExt.getRootBlockArchitecture(element_p);
			if (null != arch) {
				availableElements.addAll(getElementsFromBlockArchitecture(arch, (PhysicalLinkCategory) element_p));
			}
		}
		availableElements = ListExt.removeDuplicates(availableElements);
		return availableElements;
	}

	/** 
	 * get all the physical links from 'exchangeCategory_p' parent Block Architecture
	 * @param arch_p 
	 * @param exchangeCategory_p
	 * @return list of PhysicalLink
	 */
	private List<CapellaElement> getElementsFromBlockArchitecture(BlockArchitecture arch_p, PhysicalLinkCategory exchangeCategory_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
		if (arch_p != null) {
			for (EObject obj : EObjectExt.getAll(arch_p, CsPackage.Literals.PHYSICAL_LINK)) {
				availableElements.add((CapellaElement) obj);
			}
		}
		return availableElements;
	}

}