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
package org.polarsys.capella.core.business.queries.queries.pa;

import java.util.ArrayList;
import java.util.List;

import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.ctx.System;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;
import org.polarsys.capella.core.data.pa.PhysicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalComponentPkg;
import org.polarsys.capella.core.model.helpers.PhysicalArchitectureExt;
import org.polarsys.capella.core.model.helpers.query.CapellaQueries;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_PhysicalNode_UsedInterface extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 * <p>
	 * Gets All the Interfaces contained in the Interface Package (and all of
	 * its sub-packages) of the Physical Architecture layer.
	 * </p>
	 * <p>
	 * Except The interfaces that are already used by the current Physical
	 * Component.
	 * </p>
	 * <p>
	 * Refer MQRY_ PhysicalComponent_UsedInterfaces_1
	 * </p>
	 * @see org.polarsys.capella.core.business.queries.core.business.queries.IBusinessQuery#getAvailableElements(org.polarsys.capella.core.common.model.CapellaElement)
	 */
	public List<CapellaElement> getAvailableElements(CapellaElement element_p) {
		SystemEngineering systemEngineering = CapellaQueries.getInstance().getRootQueries().getSystemEngineering(element_p);
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>();
		if (null == systemEngineering) {
			return availableElements;
		}
		if (element_p instanceof PhysicalComponent) {
			PhysicalComponent currentPC = (PhysicalComponent) element_p;
			availableElements.addAll(getRule_MQRY_PC_UsedInterfaces_11(systemEngineering, currentPC));
		} else if (element_p instanceof PhysicalArchitecture || element_p instanceof PhysicalComponentPkg) {
			availableElements.addAll(getRule_MQRY_PC_UsedInterfaces_11(systemEngineering, null));
		}
		availableElements = ListExt.removeDuplicates(availableElements);
		return availableElements;
	}

	/** 
	 * <p>
	 * Gets All the Interfaces contained in the Interface Package (and all of
	 * its sub-packages) of the Physical Architecture layer.
	 * </p>
	 * <p>
	 * Except The interfaces that are already used by the current Physical
	 * Component.
	 * </p>
	 * <p>
	 * Refer MQRY_ PhysicalComponent_UsedInterfaces_1
	 * </p>
	 * @param systemEngineering_pthe {@link System}
	 * @param currentPC_pthe current {@link PhysicalComponent}
	 * @return list of interfaces
	 */
	private List<CapellaElement> getRule_MQRY_PC_UsedInterfaces_11(SystemEngineering systemEngineering_p, PhysicalComponent currentPC_p) {
		List<CapellaElement> availableElements = new ArrayList<CapellaElement>(1);
		availableElements.addAll(PhysicalArchitectureExt.getOwnedInterfacesFromPhysicalLayerFiltered(systemEngineering_p, currentPC_p, true));
		return availableElements;
	}

}