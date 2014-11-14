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

import org.polarsys.capella.common.helpers.EcoreUtil2;
import org.polarsys.capella.common.queries.AbstractQuery;
import org.polarsys.capella.common.queries.queryContext.IQueryContext;
import org.polarsys.capella.core.data.capellacore.CapellaElement;
import org.polarsys.capella.core.data.capellamodeller.SystemEngineering;
import org.polarsys.capella.core.data.cs.BlockArchitecture;
import org.polarsys.capella.core.data.ctx.System;
import org.polarsys.capella.core.data.la.LogicalArchitecture;
import org.polarsys.capella.core.data.la.LogicalArchitecturePkg;
import org.polarsys.capella.core.data.pa.LogicalComponentRealization;
import org.polarsys.capella.core.data.pa.PaPackage;
import org.polarsys.capella.core.data.pa.PhysicalArchitecture;
import org.polarsys.capella.core.data.pa.PhysicalComponent;
import org.polarsys.capella.core.data.pa.PhysicalComponentPkg;
import org.polarsys.capella.core.model.helpers.LogicalArchitectureExt;
import org.polarsys.capella.core.model.helpers.PhysicalComponentExt;
import org.polarsys.capella.core.model.helpers.SystemEngineeringExt;
import org.polarsys.capella.core.model.helpers.query.CapellaQueries;
import org.polarsys.capella.core.model.utils.ListExt;

public class GetAvailable_PhysicalNode_ImplementedLCS extends AbstractQuery {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List<Object> execute(Object input, IQueryContext context) {
		CapellaElement capellaElement = (CapellaElement) input;
		List<CapellaElement> availableElements = getAvailableElements(capellaElement);
		return (List) availableElements;
	}

	/** 
	 * <p>
	 * Gets All the Logical Components contained in the Logical Architecture
	 * layer.
	 * </p>
	 * <p>
	 * Except The Logical Components that are already implemented by a Physical
	 * Component
	 * </p>
	 * <p>
	 * Refer MQRY_ PhysicalComponent_ImplLogicalComp_1
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
			availableElements.addAll(getRule_MQRY_PN_ImplLC_12(currentPC));
		} else if (element_p instanceof PhysicalArchitecture || element_p instanceof PhysicalComponentPkg) {
			availableElements.addAll(getRule_MQRY_PN_ImplLC_11(systemEngineering, null, false));
		}
		availableElements = ListExt.removeDuplicates(availableElements);
		availableElements.removeAll(getCurrentElements(element_p, false));
		return availableElements;
	}

	/** 
	 * get all LC from the LA layer implemented by current PC's PA layer
	 * @param currentPC_p
	 * @return
	 */
	private List<CapellaElement> getRule_MQRY_PN_ImplLC_12(PhysicalComponent currentPC_p) {
		List<CapellaElement> list = new ArrayList<CapellaElement>(1);
		PhysicalArchitecture pa = (PhysicalArchitecture) EcoreUtil2.getFirstContainer(currentPC_p, PaPackage.Literals.PHYSICAL_ARCHITECTURE);
		List<BlockArchitecture> las = pa.getAllocatedArchitectures();
		for (BlockArchitecture logicalArchitecture : las) {
			list.addAll(LogicalArchitectureExt.getAllLCsFromLogicalArchitectureLayer((LogicalArchitecture) logicalArchitecture));
		}
		return list;
	}

	/** 
	 * Gets filtered list of All LCs from LogicalArchitecture Layer
	 * @param systemEngineering_pthe {@link System}
	 * @param currentPC_pthe current {@link PhysicalComponent}
	 * @param isFilterRequired_pflag for checking filters
	 * @return list of LCs
	 */
	private List<CapellaElement> getRule_MQRY_PN_ImplLC_11(SystemEngineering systemEngineering_p, PhysicalComponent currentPC_p, boolean isFilterRequired_p) {
		List<CapellaElement> list = new ArrayList<CapellaElement>(1);
		LogicalArchitecturePkg logArchPkg = SystemEngineeringExt.getOwnedLogicalArchitecturePkg(systemEngineering_p);
		if (null != logArchPkg) {
			for (LogicalArchitecture logArch : logArchPkg.getOwnedLogicalArchitectures()) {
				list.addAll(PhysicalComponentExt.getLCsFromLogicalArchitecture(logArch, currentPC_p, isFilterRequired_p));
			}
		}
		list.addAll(PhysicalComponentExt.getLCsFromLogicalArchitecture(SystemEngineeringExt.getOwnedLogicalArchitecture(systemEngineering_p), currentPC_p,
				isFilterRequired_p));
		return list;
	}

	/** 
	 * <p>
	 * Gets all the Logical Components that are implemented by a Physical
	 * Component
	 * </p>
	 * <p>
	 * Refer MQRY_ PhysicalComponent_ImplLogicalComp_1
	 * </p>
	 * @see org.polarsys.capella.core.business.queries.core.business.queries.IBusinessQuery#getCurrentElements(org.polarsys.capella.core.common.model.CapellaElement,boolean)
	 */
	public List<CapellaElement> getCurrentElements(CapellaElement element_p, boolean onlyGenerated_p) {
		SystemEngineering systemEngineering = CapellaQueries.getInstance().getRootQueries().getSystemEngineering(element_p);
		List<CapellaElement> currentElements = new ArrayList<CapellaElement>();
		if (null == systemEngineering) {
			return currentElements;
		}
		if (element_p instanceof PhysicalComponent) {
			PhysicalComponent currentPC = (PhysicalComponent) element_p;
			for (LogicalComponentRealization lcImplementation : currentPC.getLogicalComponentRealizations()) {
				currentElements.add(lcImplementation.getAllocatedComponent());
			}
			for (LogicalComponentRealization lcImplementation : currentPC.getOwnedLogicalComponentRealizations()) {
				currentElements.add(lcImplementation.getAllocatedComponent());
			}
			currentElements = ListExt.removeDuplicates(currentElements);
		}
		return currentElements;
	}

}